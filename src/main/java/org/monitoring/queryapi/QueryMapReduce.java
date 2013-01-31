package org.monitoring.queryapi;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
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
    
    public Iterable<DBObject> distinct(String field){
        return col.distinct("d."+field, query.get());
    }
    
    public int count(){
        return col.find(query.get()).count();
    }
    
    public Iterable<DBObject> count(int groupTime){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setSeconds(time.getSeconds()-time.getSeconds()%"+groupTime+");"
                + "time.setMilliseconds(0);"
                + "emit(time, 1);"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return values.length;"
                + "};";
        
        return aggregate(map, reduce);
    }
        
    public Iterable<DBObject> avg(int groupTime, String field){
        return avgsum(groupTime, field, "avg");
    }    
    
    public Iterable<DBObject> sum(int groupTime, String field){
        return avgsum(groupTime, field, "sum");
    }
    
    private Iterable<DBObject> avgsum(int groupTime, String field, String type){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setSeconds(time.getSeconds()-time.getSeconds()%"+groupTime+");"
                + "time.setMilliseconds(0);"
                + "emit(time, this.d."+field+");"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return Array.avg(values);"
                + "};";  
        
        return aggregate(map, reduce);
    }
    
    public Iterable<DBObject> min(int groupTime, String field){
        return minmax(groupTime, field, "min");
    }
    
    public Iterable<DBObject> max(int groupTime, String field){
        return minmax(groupTime, field, "max");
    }
            
    private Iterable<DBObject> minmax(int groupTime, String field, String type){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setSeconds(time.getSeconds()-time.getSeconds()%"+groupTime+");"
                + "time.setMilliseconds(0);"
                + "emit(time, this.d."+field+");"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return Math."+type+".apply(Math, values);"
                + "};";  
        
        return aggregate(map, reduce);
    }
    
    public Iterable<DBObject> median(int groupTime, String field){
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setSeconds(time.getSeconds()-time.getSeconds()%"+groupTime+");"
                + "time.setMilliseconds(0);"
                + "emit(time, this.d."+field+");"
                + "};";
        
        String reduce = 
                "function(id, values) {"
                + "return (values.length%2!=0)?values[(1+values.length)/2-1]:(values[values.length/2-1]+values[values.length/2])/2"
                + "};";  
        
        return aggregate(map, reduce);
    }
    
    @Deprecated
    public Iterable<DBObject> execute(){
        
        String map = 
                "function() {"
                + "time = this.t;"
                + "time.setSeconds(time.getSeconds()-time.getSeconds()%1);"
                + "time.setMilliseconds(0);"
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
        
        System.out.println(out.getCommand());
        
        return out.results();
    }
    
    private Iterable<DBObject> aggregate(String map, String reduce, String finalize){
        return aggregate(map, reduce, finalize, "", MapReduceCommand.OutputType.INLINE);
    }
    
    private Iterable<DBObject> aggregate(String map, String reduce){
        return aggregate(map, reduce, "", "", MapReduceCommand.OutputType.INLINE);
    }

}
