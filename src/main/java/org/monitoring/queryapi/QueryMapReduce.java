package org.monitoring.queryapi;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * MapReduce query
 *
 * unsupported arithmetics on fields (eg expression value1 + value2 from data
 * field -> d.value1+d.value2) !
 *
 * @author Michal Dubravcik
 */
public class QueryMapReduce implements Query {

    private DBCollection col;
    private BasicDBObjectBuilder query = new BasicDBObjectBuilder();
    private BasicDBObjectBuilder sort = new BasicDBObjectBuilder();
    private int limit = 0;
    private Date start = null, end = null;
    private int groupTime = 1000;

    public QueryMapReduce(DBCollection col) {
        this.col = col;
    }

    public void append(String field, Object value) {
        query.add(field, value);
    }

    public Field field(String field) {
        return new Field((Query) this, "d." + field);
    }

    public QueryMapReduce orderAsc(String val) {
        sort.append(val, 1);
        return this;
    }

    public QueryMapReduce orderDesc(String val) {
        sort.append(val, -1);
        return this;
    }

    /**
     * Restricts the number of documents
     *
     * @param num
     * @return query for chaining
     */
    public QueryMapReduce limit(int num) {
        limit = num;
        return this;
    }
    
    public DBObject find(){
        return wrap("result",col.find(getMatchQuery()).sort(sort.get()).limit(limit));
    }

    public DBObject getMatchQuery() {
        BasicDBObjectBuilder queryLocal = new BasicDBObjectBuilder();
        if (start != null) {
            queryLocal.push("t").append("$gte", start);
        }
        if (end != null) {
            if (queryLocal.isEmpty()) {
                queryLocal.push("t").append("$lte", end);
            } else {
                queryLocal.append("$lte", end);
            }
        }
        DBObject out = query.get();
        out.putAll(queryLocal.get());
        return out;
    }

    private DBObject getMatchQueryWithSubTime(Date qStart, Date qEnd) {
        BasicDBObjectBuilder queryLocal = new BasicDBObjectBuilder();
        if (qStart != null) {
            queryLocal.push("t").append("$gte", qStart);
        }
        if (qEnd != null) {
            if (queryLocal.isEmpty()) {
                queryLocal.push("t").append("$lt", qEnd);
            } else {
                queryLocal.append("$lt", qEnd);
            }
        }
        DBObject out = query.get();
        out.putAll(queryLocal.get());
        return out;
    }

    public QueryMapReduce fromDate(Date date) {
        start = date;
        return this;
    }

    public QueryMapReduce toDate(Date date) {
        end = date;
        return this;
    }

    public QueryMapReduce setGroupTime(int groupTime) {
        this.groupTime = groupTime;
        return this;
    }

    public DBObject difference(DBObject match, String keyForDifference) {
        String keyInner = keyForDifference.substring(keyForDifference.lastIndexOf(".") + 1);

        DBObject project = BasicDBObjectBuilder.start().push("$project")
                .append("_id", 0).append("t", 1).append(keyInner, "$" + keyForDifference).get();
        Iterable<DBObject> list = col.aggregate(new BasicDBObject("$match", match), project).results();

        int i = 0;
        Number numPre = new Double("0");

        BasicDBList result = new BasicDBList();
        for (DBObject object : list) {
            Number num = (Number) object.get(keyInner);
            if (i > 0) {
                result.add(BasicDBObjectBuilder.start()
                        .append("t", object.get("t"))
                        .append(keyInner, num.doubleValue() - numPre.doubleValue()).get());
            }
            numPre = num;
            i++;
        }
        return wrap("result", result);
    }

    public DBObject reasonFor(String effectKey, Object effectValue, int limit, String... groupBy) {
        return reasonFor(new BasicDBObject(effectKey, effectValue), limit, groupBy);
    }

    public DBObject reasonFor(String effectKey, Object effectValue, String... groupBy) {
        return reasonFor(new BasicDBObject(effectKey, effectValue), 100, groupBy);
    }

    public DBObject reasonFor(DBObject effect, String... groupBy) {
        return reasonFor(effect, 100, groupBy);
    }

    public DBObject reasonFor(DBObject effect, int limit, String... groupBy) {
        BasicDBList dates = new BasicDBList();
        BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
        Iterable<DBObject> reasons;
        int num = 0;

        Iterable<DBObject> results = col.find(effect);

        for (DBObject result : results) {
            dates.add(
                    BasicDBObjectBuilder.start().push("t").append("$lte", (Date) result.get("t"))
                    .append("$gte", new Date(((Date) result.get("t")).getTime() - 1000)).get());
            num++;
        }
        if (num > 0) {
            DBObject match = BasicDBObjectBuilder.start()
                    .append("$match", new BasicDBObject("$or", dates)).get();

            for (String groupKey : groupBy) {
                builder.append(groupKey, "$" + groupKey);
            }
            DBObject groupInner = builder.get();
            DBObject group = BasicDBObjectBuilder.start().push("$group").append("_id", groupInner)
                    .push("count").append("$sum", 1).get();
            DBObject order = BasicDBObjectBuilder.start().push("$sort").append("count", -1).get();
            DBObject project = BasicDBObjectBuilder.start().push("$project")
                    .append("_id", 0).append("count", 1).append("group", "$_id").get();
            DBObject limiter = new BasicDBObject("$limit", limit);

            reasons = col.aggregate(match, group, order, limiter, project).results();
        } else {
            reasons = new ArrayList<DBObject>();
        }
        return wrap("founded effects", num, "result", reasons);
    }

    public DBObject distinct(String field) {
        return wrap("result", col.distinct("d." + field, getMatchQuery()));
    }

    public int count() {
        return col.find(getMatchQuery()).count();
    }

    public DBObject count(int groupTime) {
        String map =
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%" + groupTime + ");"
                + "emit(time, 1);"
                + "};";

        String reduce =
                "function(id, values) {"
                + "return values.length;"
                + "};";

        return wrap("result", aggregate(map, reduce));
    }

    public DBObject avg(int groupTime, String field) {
        return avgsum(groupTime, field, "avg");
    }

    public DBObject sum(int groupTime, String field) {
        return avgsum(groupTime, field, "sum");
    }

    private DBObject avgsum(int groupTime, String field, String type) {
        String map =
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%" + groupTime + ");"
                + "emit(time, this.d." + field + ");"
                + "};";

        String reduce =
                "function(id, values) {"
                + "return Array." + type + "(values);"
                + "};";

        return wrap("result", aggregate(map, reduce));
    }

    private int getInclusiveBeforePoint(int point){
        DBCollection cache = col.getDB().getCollection("cache");
        DBObject beforeStartQuery = BasicDBObjectBuilder.start().push("_id")
                .append("$lte", point).get();
        DBObject order = new BasicDBObject("_id", -1);
        List<DBObject> beforeStartResponseList = cache.find(beforeStartQuery).sort(order).limit(1).toArray();
        if(beforeStartResponseList.isEmpty()){
            return 3;
        }else{
            return (Integer) beforeStartResponseList.get(0).get("f");
        }
    }
    
    private boolean isStartInclusiveBeforePoint(int point) {
        return getInclusiveBeforePoint(point)==0;
    }
    
    private int getInclusiveAfterPoint(int point){
        DBCollection cache = col.getDB().getCollection("cache");
        DBObject afterEndQuery = BasicDBObjectBuilder.start().push("_id")
                .append("$gte", point).get();
        DBObject order = new BasicDBObject("_id", 1);
        List<DBObject> afterEndResponseList = cache.find(afterEndQuery).sort(order).limit(1).toArray();
        if(afterEndResponseList.isEmpty()){
            return 3;
        }else{
            return (Integer) afterEndResponseList.get(0).get("f");
        }
    }
    
    private boolean isEndInclusiveAfterPoint(int point) {
        return getInclusiveAfterPoint(point)==1;
    }
    
    private int getAtPoint(int point){
        DBCollection cache = col.getDB().getCollection("cache");
        DBObject atQuery = BasicDBObjectBuilder.start().append("_id", point).get();
        List<DBObject> atResponseList = cache.find(atQuery).limit(1).toArray();
        if(atResponseList.isEmpty()){
            return 3;
        }else{
            return (Integer) atResponseList.get(0).get("f");
        }
    }

    public DBObject cache(int start, int end) {
        DBCollection cache = col.getDB().getCollection("cache");
        BasicDBList or = new BasicDBList();
        or.add(BasicDBObjectBuilder.start().push("_id").append("$ne", start).get());
        or.add(BasicDBObjectBuilder.start().push("f").append("$ne", 0).get());
        DBObject match = BasicDBObjectBuilder.start()
                .push("_id").append("$gte", start).append("$lt", end).pop()
                .append("$or", or)
                .get();
        System.out.println(match);
        DBCursor cursor = cache.find(match);
        if (cursor.hasNext()) { //not empty response -> partially cached
            while (cursor.hasNext()) {
                DBObject point1, point2;
                point1 = cursor.next();
                if ((Integer) point1.get("f") == 0 && (Integer) point1.get("_id") == start){
                    continue;
                }
                if ((Integer) point1.get("f") == 0) {
                    System.out.println("//mp(start,point.getTime)");
                    continue;
                }
                if (cursor.hasNext()) {
                    point2 = cursor.next();
                    System.out.println("//mp(point1.getTime,point2.getTime)");
                } else {
                    System.out.println("//mp(point1.getTime,end)"+point1.get("_id"));
                }
            }
            int beforeStart = getInclusiveBeforePoint(start);
            if (beforeStart == 0) {
                //do nothing with start
            } else if (beforeStart == 1) {
                //remove old end
                cache.remove(new BasicDBObject("_id", start));
            } else {
                //insert start
                cache.save(BasicDBObjectBuilder.start().append("_id", start).append("f", 0).get());
            }
            cache.remove(BasicDBObjectBuilder.start().push("_id").append("$gt", start).append("$lte", end).get());
            
            int afterEnd = getInclusiveAfterPoint(end);
            if(afterEnd == 1){
                System.out.println("//do not add end");
            }else{                
                System.out.println("//insert end");
                cache.save(BasicDBObjectBuilder.start().append("_id", end).append("f", 1).get());
            }
            
        } else { //empty response -> all cached or nothing cached
            if (isStartInclusiveBeforePoint(start)) {
                System.out.println("//all cached, ready for query from cache");
            } else if(getAtPoint(end) == 0){
                cache.remove(new BasicDBObject("_id", end));
                cache.insert(BasicDBObjectBuilder.start().append("_id", start).append("f", 0).get());
                System.out.println("//all cached, remove end");
            }else {
                System.out.println("//nothing cached, need to recompute");

                DBObject[] batch = new DBObject[2];
                batch[0] = BasicDBObjectBuilder.start().append("_id", start).append("f", 0).get();
                batch[1] = BasicDBObjectBuilder.start().append("_id", end).append("f", 1).get();

                cache.insert(batch);
            }
        }
        return wrap("result", new BasicDBObject());
    }

    public DBObject cacheAvg(String field) {

        long startTime = start.getTime() - start.getTime() % groupTime;
        long endTime = end.getTime() - end.getTime() % groupTime;
        long queryStartTime = 0;
        long queryEndTime = 0;
        fromDate(new Date(startTime));
        toDate(new Date(endTime));
        boolean hold = false;
        DBObject cacheObj = null;
        DBCursor cursor = col.getDB().getCollection("cacheAvg")
                .find().sort(new BasicDBObject("_id", 1));
        Iterator<DBObject> it = cursor.iterator();

        String map =
                "function() {"
                + "var id;"
                + "id = {t:this.t, op: \"avg\",field:\"" + field + "\",gt:" + groupTime + ",match:" + query.get() + "};"
                + "id.t.setTime(id.t.getTime()-id.t.getTime()%" + groupTime + ");"
                + "emit(id, this.d." + field + ");"
                + "};";

        String reduce =
                "function(id, values) {"
                + "return Array.avg(values);"
                + "};";

        String finalize = "";

        //no need to compute, all cached
        if (cursor.count() >= (endTime - startTime) / groupTime) {
            return wrap("result", cursor.toArray());
        }

        if (cursor.count() > 0) {
            for (long actualTime = startTime; actualTime <= endTime; actualTime += groupTime) {
                if (it.hasNext() || hold) {
                    if (!hold) {
                        cacheObj = it.next();
                    }
                    long cacheObjTime = ((Date) ((DBObject) cacheObj.get("_id")).get("t")).getTime();

                    //actual time = cached time && hold - last one not equal
                    if (cacheObjTime == actualTime && hold) {
                        hold = false;
                        aggregate(map, reduce, finalize, "cacheAvg", MapReduceCommand.OutputType.MERGE, new Date(queryStartTime), new Date(queryEndTime + groupTime));
                        //System.out.println(queryStartTime + ">>" + queryEndTime);
                        //actual time != cached time && hold - first time found
                    } else if (cacheObjTime != actualTime && !hold) {
                        queryStartTime = actualTime;
                        queryEndTime = actualTime;
                        hold = true;
                        //actual time != cached time && hold - more times found
                    } else if (cacheObjTime != actualTime && hold) {
                        queryEndTime = actualTime;
                        //actual time = cached time && !hold - last one and actual found cached
                    } else {
                        //System.out.print("*");
                    }
                }
            }
        } else {
            aggregate(map, reduce, finalize, "cacheAvg", MapReduceCommand.OutputType.MERGE);
        }
        return wrap("result", col.getDB().getCollection("cacheAvg")
                .find().sort(new BasicDBObject("_id", 1)).toArray());
    }

    public DBObject min(int groupTime, String field) {
        return minmax(groupTime, field, "min");
    }

    public DBObject max(int groupTime, String field) {
        return minmax(groupTime, field, "max");
    }

    private DBObject minmax(int groupTime, String field, String type) {
        String map =
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%" + groupTime + ");"
                + "emit(time, this.d." + field + ");"
                + "};";

        String reduce =
                "function(id, values) {"
                + "return Math." + type + ".apply(Math, values);"
                + "};";

        return wrap("result", aggregate(map, reduce));
    }

    public DBObject median(int groupTime, String field) {
        String map =
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%" + groupTime + ");"
                + "emit(time, this.d." + field + ");"
                + "};";

        String reduce =
                "function(id, values) {"
                + "return (values.length%2!=0)?values[(1+values.length)/2-1]:(values[values.length/2-1]+values[values.length/2])/2"
                + "};";

        return wrap("result", aggregate(map, reduce));
    }

    @Deprecated
    public Iterable<DBObject> execute() {

        String map =
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%" + groupTime + ");"
                + "emit(time, this.d.v);"
                + "};";

        String reduce =
                "function(id, values) {"
                + "return Array.sum(values);"
                + "};";

        String output = "";

        String finalize =
                "function(key, reducedValue)"
                + "result = {time: key, value:reducedValue};"
                + "return result;"
                + "};";

        return aggregate(map, reduce);
    }

    private Iterable<DBObject> aggregate(String map, String reduce, String finalize, String output, MapReduceCommand.OutputType type) {
        return aggregate(map, reduce, finalize, output, type, null, null);
    }

    private Iterable<DBObject> aggregate(String map, String reduce, String finalize, String output, MapReduceCommand.OutputType type, Date qStart, Date qEnd) {
        MapReduceCommand mapReduceCmd;

        if (qEnd == null && qStart == null) {
            mapReduceCmd =
                    new MapReduceCommand(col, map, reduce, output, type, getMatchQuery());
        } else {
            mapReduceCmd =
                    new MapReduceCommand(col, map, reduce, output, type, getMatchQueryWithSubTime(qStart, qEnd));
        }

        if (!finalize.isEmpty()) {
            mapReduceCmd.setFinalize(finalize);
        }
        if (!sort.isEmpty()) {
            mapReduceCmd.setSort(sort.get());
        } else {
            mapReduceCmd.setSort(new BasicDBObject("_id", 1));
        }

        if (limit != 0) {
            mapReduceCmd.setLimit(limit);
        }

        MapReduceOutput out = col.mapReduce(mapReduceCmd);

        System.out.println(out.getCommandResult());
        System.out.println(out.getCommand());

        return out.results();
    }

    private Iterable<DBObject> aggregate(String map, String reduce, String finalize) {
        return aggregate(map, reduce, finalize, "", MapReduceCommand.OutputType.INLINE);
    }

    private Iterable<DBObject> aggregate(String map, String reduce) {
        return aggregate(map, reduce, "", "", MapReduceCommand.OutputType.INLINE);
    }

    private DBObject wrap(String firstKey, Object firstValue, String secondKey, Object secondValue) {
        return BasicDBObjectBuilder.start().append(firstKey, firstValue).append(secondKey, secondValue).get();
    }

    private DBObject wrap(String firstKey, Object firstValue) {
        return new BasicDBObject(firstKey, firstValue);
    }
}
