package org.monitoring.queryapi;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 *
 * @author Michal Dubravcik
 */
public interface Query {
    
    
    public void append(String field, Object value);

    public Iterable<DBObject> execute();    
}
