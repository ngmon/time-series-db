package org.monitoring.queryapi;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import java.util.Date;

/**
 * MapReduce query
 *
 * @author Michal Dubravcik
 */
public class QueryMapReduce  implements Query{
    
    DBCollection col;
    
    BasicDBObjectBuilder query = new BasicDBObjectBuilder();
    BasicDBObjectBuilder sort = new BasicDBObjectBuilder();
    BasicDBObjectBuilder dateLimit = new BasicDBObjectBuilder(); 
    int limit;  
        
    public QueryMapReduce(DBCollection col){
        this.col = col;
    }
    
    public void append(String field, Object value){
        query.add(field, value);
    }    
    
    public Field field(String field){
        return new Field(this,field);
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
    
    
    public QueryMapReduce project(String name, boolean bool){
        
        return this;
    }
    
    
    public QueryMapReduce fromDate(Date date){
        long dateNum = date.getTime();
        if(dateLimit.isEmpty()){
            dateLimit.push("t");
        }
        dateLimit.append("$gte", dateNum);        
        return this;
    }
    
    public QueryMapReduce toDate(Date date){
        long dateNum = date.getTime();
        if(dateLimit.isEmpty()){
            dateLimit.push("t");
        }
        dateLimit.append("$lte", dateNum);        
        return this;
    }
    
    
    public Iterable<DBObject> execute(){
        
        String map = "";
        String reduce = "";
        String output = "";
        
        BasicDBObject match = new BasicDBObject();
        match.putAll(dateLimit.get());
        match.putAll(query.get());
        
        System.out.println(match);
        
        MapReduceCommand mapReduceCmd = new MapReduceCommand(col, map, reduce, output, MapReduceCommand.OutputType.MERGE, match);        
        
        return col.mapReduce(mapReduceCmd).results();
    }    

}
