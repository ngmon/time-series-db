package org.monitoring.db;

import com.mongodb.DBObject;
import java.util.List;

/**
 *
 * @author Michal
 */
public interface Database {
    
    void saveDocument(DBObject object);
    
    void tearDown();

    public void saveDocuments(List<DBObject> documents);
    
}
