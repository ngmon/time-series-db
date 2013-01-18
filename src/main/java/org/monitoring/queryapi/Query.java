package org.monitoring.queryapi;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 *
 * @author Michal Dubravcik
 */
public class Query {
    
    DBCollection col;
    BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
    
    public Query(){        
    }
    
    public Query(DBCollection col){
        this.col = col;
    }
    
    public void append(String field, Object value){
        builder.add(field, value);
    }    
    
    public Field field(String field){
        return new Field(this,field);
    }
    
    public DBObject get(){
        return builder.get();
    }
    
    public DBCursor execute(){
        return col.find(this.get());
    }    

}
