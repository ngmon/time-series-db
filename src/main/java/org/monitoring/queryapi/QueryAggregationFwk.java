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
public class QueryAggregationFwk implements Query {
    
    DBCollection col;
    String container;
    BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
    BasicDBObjectBuilder dateLimit = new BasicDBObjectBuilder();
    BasicDBList pipe = new BasicDBList();
    
    
    public QueryAggregationFwk(DBCollection col){
        this.col = col;
    }
    
    public void append(String field, Object value) {
        builder.add(field, value);
    }
    
    
    public Field field(String field){
        if(builder.isEmpty()){
            builder.push("$match");
        }        
        return new Field(this,field);
    }
    
    public QueryAggregationFwk orderAsc(String val){
        addPipeOperation("$sort", new BasicDBObject(val, 1));
        return this;
    }
        
    public QueryAggregationFwk orderDesc(String val){
        addPipeOperation("$sort", new BasicDBObject(val, -1));
        return this;
    }
    
    /**
     * Restricts the number of documents
     * @param num
     * @return query for chaining
     */
    public QueryAggregationFwk limit(int num){
        addPipeOperation("$limit", num);
        return this;        
    }
    
    /**
     * Skips over the specified number of documents
     * @param num
     * @return query for chaining
     */
    public QueryAggregationFwk skip(int num){
        addPipeOperation("$skip", num);
        return this;        
    }
    
    public QueryAggregationFwk project(String name, boolean bool){
        addPipeOperation("$project", new BasicDBObject(name, (bool==true?1:0)));
        return this;
    }
    
    public QueryAggregationFwk count(){
        build();
        builder.push("$group").append("_id","count").push("value").append("$sum", 1);
        return this;
    }
    
    private void groupByTime(int groupStep){
        BasicDBList mod = new BasicDBList();
        mod.add("$t");
        mod.add(groupStep);
        addPipeOperation("$project", BasicDBObjectBuilder
                .start().append("s", 1).append("t", 1).append("d", 1).push("mod").append("$mod", mod).get());
        
        BasicDBList subtract = new BasicDBList();
        subtract.add("$t");
        subtract.add("$mod");
        
        builder.push("$group").push("_id").append("$subtract", subtract).pop();        
    }
    public QueryAggregationFwk count(int groupStep){
        groupByTime(groupStep);
        
        builder.push("value").append("$sum", 1);
        return this;
        
    }
    
//    //impossible
//    public Query median(int groupStep, String field){
//        groupByTime(groupStep);
//        
//        builder.push("value").append("$push", "$d."+field); 
//        return this;
//        
//    }
    
    public QueryAggregationFwk rename(){
        addPipeOperation("$project", BasicDBObjectBuilder.start().append("time", "$_id").append("value",1).append("_id", 0).get());
        return this;
    }
    
    private void reduce(String reducer,String field, int groupStep){
        build();
        groupByTime(groupStep);
        builder.push("value").append("$"+reducer, "$d."+field);        
    }
    
    public QueryAggregationFwk sum(int groupStep, String field){      
        reduce("sum", field, groupStep);
        return this;
    }
    
    public QueryAggregationFwk avg(int groupStep, String field){    
        reduce("avg", field, groupStep);
        return this;
    }
    
    public QueryAggregationFwk min(int groupStep, String field){    
        reduce("min", field, groupStep);
        return this;
    }
    
    public QueryAggregationFwk max(int groupStep, String field){    
        reduce("max", field, groupStep);
        return this;
    }
    
    public QueryAggregationFwk fromDate(Date date){
        long dateNum = date.getTime();
        if(!dateLimit.isEmpty()){
            dateLimit.append("$gte", dateNum);
        }else{
            dateLimit.push("$match").push("t").append("$gte", dateNum);
        }
        return this;
    }
    
    public QueryAggregationFwk toDate(Date date){
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
