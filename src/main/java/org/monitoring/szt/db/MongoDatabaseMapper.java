package org.monitoring.szt.db;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.List;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public interface MongoDatabaseMapper {

    RawEvent mapDBObjectToObject(DBObject eventm);

    List<RawEvent> mapDBObjectsToObjects(DBCursor cursor);

    DBObject mapObjectToDBObject(RawEvent event);
    
}
