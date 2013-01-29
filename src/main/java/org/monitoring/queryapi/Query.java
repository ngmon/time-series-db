package org.monitoring.queryapi;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.Date;

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
    BasicDBObjectBuilder dateLimit = new BasicDBObjectBuilder();
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
        addPipeOperation("$sort", new BasicDBObject(val, 1));
        return this;
    }
    
    public Query orderDesc(String val){
        addPipeOperation("$sort", new BasicDBObject(val, -1));
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
    
    public Query count(){
        builder.push("$group").append("_id","count").push("value").append("$sum", 1);
        return this;
    }
    private void groupTime(int groupStep){
        BasicDBList mod = new BasicDBList();
        mod.add("$t");
        mod.add(groupStep);
        addPipeOperation("$project", new BasicDBObjectBuilder()
                .start().append("s", 1).append("t", 1).append("d", 1).push("mod").append("$mod", mod).get());
        
        BasicDBList subtract = new BasicDBList();
        subtract.add("$t");
        subtract.add("$mod");
        
        builder.push("$group").push("_id").append("$subtract", subtract).pop();        
    }
    public Query count(int groupStep){
        groupTime(groupStep);
        
        builder.push("value").append("$sum", 1);
        return this;
        
    }
    
    public Query rename(){
        addPipeOperation("$project", new BasicDBObjectBuilder().start().append("time", "$_id").append("value",1).append("_id", 0).get());
        return this;
    }
    
    private void reduce(String reducer,String field, int groupStep){
        groupTime(groupStep);
        builder.push("value").append("$"+reducer, "$d."+field);        
    }
    
    public Query sum(int groupStep, String field){      
        reduce("sum", field, groupStep);
        return this;
    }
    
    public Query avg(int groupStep, String field){    
        reduce("avg", field, groupStep);
        return this;
    }
    
    public Query min(int groupStep, String field){    
        reduce("min", field, groupStep);
        return this;
    }
    
    public Query max(int groupStep, String field){    
        reduce("max", field, groupStep);
        return this;
    }
    
    public Query fromDate(Date date){
        long dateNum = date.getTime();
        if(!dateLimit.isEmpty()){
            dateLimit.append("$gte", dateNum);
        }else{
            dateLimit.push("$match").push("t").append("$gte", dateNum);
        }
        return this;
    }
    
    public Query toDate(Date date){
        long dateNum = date.getTime();
        if(!dateLimit.isEmpty()){
            dateLimit.append("$lte", dateNum);
        }else{
            dateLimit.push("$match").push("t").append("$lte", dateNum);
        }
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
    
    public BasicDBList get(){
        build();
        if(!dateLimit.isEmpty()){
            pipe.add(0, dateLimit.get());
        }
        return pipe;
    }
    
    public Iterable<DBObject> execute(){
        build();
        if(pipe.size()>1){
            return col.aggregate((DBObject) this.get().remove(0),this.get().toArray(new DBObject[0])).results();
        }
        else {
            return col.aggregate((DBObject)this.get().get(0)).results();
        }
    }    

}
