package org.monitoring.queryapi;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Aggregation query
 * supported:  match, skip, limit, sort (1 level), project (1level)
 * unsupported: sort (all levels), group, unwind, project
 *
 * @author Michal Dubravcik
 */
public class Query {
    
    DBCollection col;
    String container;
    BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
    BasicDBList pipe = new BasicDBList();
    
    public Query(){  
    }
    
    public Query(DBCollection col){
        this.col = col;
    }
    
    public void append(String field, Object value){
        builder.add(field, value);
    }    
    
    public Field field(String field){
        if(builder.isEmpty()){
            builder.push("$match");
        }
        return new Field(this,field);
    }
    
    public Query orderAsc(String val){
        addPipeOperation("$skip", new BasicDBObject(val, 1));
        return this;
    }
    
    public Query orderDesc(String val){
        addPipeOperation("$skip", new BasicDBObject(val, -1));
        return this;
    }
    
    /**
     * Restricts the number of documents
     * @param num
     * @return query for chaining
     */
    public Query limit(int num){
        addPipeOperation("$limit", num);
        return this;        
    }
    
    /**
     * Skips over the specified number of documents
     * @param num
     * @return query for chaining
     */
    public Query skip(int num){
        addPipeOperation("$skip", num);
        return this;        
    }
    
    public Query project(String name){
        addPipeOperation("$project", new BasicDBObject(name, 1));
        return this;
    }
    
//    public Order orderByMany(){
//        return new Order(this);
//    }
    
    private void addPipeOperation(String operation, Object object){
        build();
        pipe.add(BasicDBObjectBuilder.start().append(operation, object).get());
        build();
    }
    
    private void build(){
        if(!builder.isEmpty()){
            pipe.add(builder.get());
            builder = new BasicDBObjectBuilder();
        }        
    }
    
    public DBObject get(){
        build();
        return pipe;
    }
    
    public Iterable<?> execute(){
        return col.aggregate(this.get()).results();
    }    

}
