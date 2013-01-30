package org.monitoring.queryapi;

import com.mongodb.DBObject;
import java.util.Date;

/**
 *
 * @author Michal Dubravcik
 */
public interface Query {
    
    
    void append(String field, Object value);

    Iterable<DBObject> execute();

    Field field(String field);

    Query fromDate(Date date);

    Query limit(int num);

    Query orderAsc(String val);

    Query orderDesc(String val);

    Query toDate(Date date);    
}
