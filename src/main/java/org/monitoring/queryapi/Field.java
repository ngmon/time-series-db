package org.monitoring.queryapi;

import com.mongodb.BasicDBObject;

/**
 *
 * @author Michal Dubravcik
 */
public class Field {
    
    Query query;
    String field;
            
    public Field(Query query, String field){
        this.query = query;
        this.field = field;
    }
    
    public Query exists(){
        query.append(field, new BasicDBObject("$exists", true));
        return query;
    }
    
    public Query doesNotExist(){
        query.append(field, new BasicDBObject("$exists", false));
        return query;
    }
    
    public Query equal(Object value){
        query.append(field, value);
        return query;
    }
    
    public Query notEqual(Object value){
        query.append(field, new BasicDBObject("$ne", value));
        return query;
    }
    
    public Query lessThan(Object value){
        query.append(field, new BasicDBObject("$lt", value));
        return query;
    }
    
    public Query lessThanOrEq(Object value){
        query.append(field, new BasicDBObject("$lte", value));
        return query;
    }
    
    public Query greaterThan(Object value){
        query.append(field, new BasicDBObject("$gt", value));
        return query;
    }
    
    public Query greaterThanOrEq(Object value){
        query.append(field, new BasicDBObject("$gte", value));
        return query;
    }
    
    
    public Query hasOneOf(Iterable<?> value){
        query.append(field, new BasicDBObject("$in", value));
        return query;
    }
    
    public Query hasAllOf(Iterable<?> value){
        query.append(field, new BasicDBObject("$all", value));
        return query;
    }
    
    public Query hasNoneOf(Iterable<?> value){
        query.append(field, new BasicDBObject("$nin", value));
        return query;
    }
    
    public Query hasThisElement(Object value){
        query.append(field, new BasicDBObject("$elemMatch", value));
        return query;
    }

}
