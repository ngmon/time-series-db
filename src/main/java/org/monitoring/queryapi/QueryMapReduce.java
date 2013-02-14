package org.monitoring.queryapi;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import java.util.ArrayList;
import java.util.Date;

/**
 * MapReduce query
 * 
 * unsupported arithmetics on fields (eg expression value1 + value2 from data field -> d.value1+d.value2) !
 *
 * @author Michal Dubravcik
 */
public class QueryMapReduce implements Query{
    
    DBCollection col;
    
    BasicDBObjectBuilder query = new BasicDBObjectBuilder();
    BasicDBObjectBuilder sort = new BasicDBObjectBuilder(); 
    int limit = 0;  
    private int groupTime;
        
    public QueryMapReduce(DBCollection col){
        this.col = col;
    }
        
    public void append(String field, Object value){
        query.add(field, value);
    }        
    
    public Field field(String field){
        return new Field((Query)this,"d."+field);
    }
        
    public QueryMapReduce orderAsc(String val){
        sort.append(val, 1);
        return this;
    }
    
    public QueryMapReduce orderDesc(String val){
        sort.append(val, -1);
        return this;
    }
    
    /**
     * Restricts the number of documents
     * @param num
     * @return query for chaining
     */    
    public QueryMapReduce limit(int num){
        limit = num;
        return this;        
    }
            
    public QueryMapReduce fromDate(Date date){
        query.push("t").append("$gte", date);        
        return this;
    }
    
    public QueryMapReduce toDate(Date date){
        query.push("t").append("$lte", date);         
        return this;
    }    
    
    public QueryMapReduce setGroupTime(int groupTime){
        this.groupTime = groupTime;        
        return this;
    }
    
    public DBObject reasonFor(String effectKey, Object effectValue, int limit, String... groupBy){
        return reasonFor(new BasicDBObject(effectKey, effectValue), limit, groupBy);
    }
    
    public DBObject reasonFor(String effectKey, Object effectValue, String... groupBy){
        return reasonFor(new BasicDBObject(effectKey, effectValue), 100, groupBy);
    }
    
    public DBObject reasonFor(DBObject effect, String... groupBy){
        return reasonFor(effect, 100, groupBy);
    }
    
    public DBObject reasonFor(DBObject effect, int limit, String... groupBy){
        BasicDBList dates = new BasicDBList();
        BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
        Iterable<DBObject> reasons;
        int num = 0;
        
        Iterable<DBObject> results = col.find(effect);
        
        for(DBObject result : results){            
            dates.add(
                    BasicDBObjectBuilder.start().push("t").append("$lte", (Date)result.get("t"))
                    .append("$gte", new Date(((Date)result.get("t")).getTime()-1000)).get()
            ); 
            num++;
        } 
        if(num > 0){
            DBObject match = BasicDBObjectBuilder.start()
                    .append("$match", new BasicDBObject("$or", dates)).get();
            
            for(String groupKey : groupBy){
                builder.append(groupKey, "$"+groupKey);
            }
            DBObject groupInner = builder.get();
            DBObject group = BasicDBObjectBuilder.start().push("$group").append("_id",groupInner)
                    .push("count").append("$sum", 1).get();
            DBObject order = BasicDBObjectBuilder.start().push("$sort").append("count", -1).get();
            DBObject project = BasicDBObjectBuilder.start().push("$project")
                    .append("_id", 0).append("count", 1).append("group", "$_id").get();
            DBObject limiter = new BasicDBObject("$limit", limit);  
            
            reasons = col.aggregate(match, group, order, limiter, project).results();
        }else{
            reasons = new ArrayList<DBObject>();
        }
        return wrap("founded effects", num, "reasons",reasons);
    }
    
    public DBObject distinct(String field){
        return wrap("result",col.distinct("d."+field, query.get()));
    }
    
    public int count(){
        return col.find(query.get()).count();
    }
    
    public DBObject count(int groupTime){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%"+groupTime+");"
                + "emit(time, 1);"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return values.length;"
                + "};";
        
        return wrap("result",aggregate(map, reduce));
    }
        
    public DBObject avg(int groupTime, String field){
        return avgsum(groupTime, field, "avg");
    }    
    
    public DBObject sum(int groupTime, String field){
        return avgsum(groupTime, field, "sum");
    }
    
    private DBObject avgsum(int groupTime, String field, String type){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%"+groupTime+");"
                + "emit(time, this.d."+field+");"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return Array.avg(values);"
                + "};";  
        
        return wrap("result",aggregate(map, reduce));
    }
    
    public DBObject min(int groupTime, String field){
        return minmax(groupTime, field, "min");
    }
    
    public DBObject max(int groupTime, String field){
        return minmax(groupTime, field, "max");
    }
            
    private DBObject minmax(int groupTime, String field, String type){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%"+groupTime+");"
                + "emit(time, this.d."+field+");"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return Math."+type+".apply(Math, values);"
                + "};";  
        
        return wrap("result",aggregate(map, reduce));
    }
    
    public DBObject median(int groupTime, String field){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%"+groupTime+");"
                + "emit(time, this.d."+field+");"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return (values.length%2!=0)?values[(1+values.length)/2-1]:(values[values.length/2-1]+values[values.length/2])/2"
                + "};";  
        
        return wrap("result",aggregate(map, reduce));
    }
    
    @Deprecated
    public Iterable<DBObject> execute(){
        
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setTime(time.getTime()-time.getTime()%"+groupTime+");"
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
    
    private Iterable<DBObject> aggregate(String map, String reduce,String finalize, String output,MapReduceCommand.OutputType type){
        
        MapReduceCommand mapReduceCmd = 
                new MapReduceCommand(col, map, reduce, output, type, query.get());        
        
        if(!finalize.isEmpty()){
            mapReduceCmd.setFinalize(finalize);
        }        
        if(!sort.isEmpty()){
            mapReduceCmd.setSort(sort.get());
        }
        if(limit!=0){
            mapReduceCmd.setLimit(limit);
        }
        
        MapReduceOutput out = col.mapReduce(mapReduceCmd);
        
        //System.out.println(out.getCommand());
        
        return out.results();
    }
    
    private Iterable<DBObject> aggregate(String map, String reduce, String finalize){
        return aggregate(map, reduce, finalize, "", MapReduceCommand.OutputType.INLINE);
    }
    
    private Iterable<DBObject> aggregate(String map, String reduce){
        return aggregate(map, reduce, "", "", MapReduceCommand.OutputType.INLINE);
    }
    
    private DBObject wrap(String firstKey, Object firstValue, String secondKey, Object secondValue){
        return BasicDBObjectBuilder.start().append(firstKey, firstValue).append(secondKey, secondValue).get();
    }
    private DBObject wrap(String firstKey, Object firstValue){
        return new BasicDBObject(firstKey, firstValue);
    }

}
