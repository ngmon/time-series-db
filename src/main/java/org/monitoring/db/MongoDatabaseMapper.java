/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.MeasurementType;
import org.monitoring.szt.RawEvent;
import org.monitoring.szt.SourceType;

/**
 *
 * @author Michal
 */
public class MongoDatabaseMapper {

    public DBObject mapObjectToDBObject(RawEvent event) {
        DBObject eventm = new BasicDBObject();
        return eventm;
    }

    public RawEvent mapDBObjectToObject(DBObject eventm) {
        RawEvent event = new RawEvent();
        return event;
    }
    
    public List<RawEvent> mapDBObjectsToObjects(DBCursor cursor){
        List<RawEvent> result = new LinkedList<RawEvent>();
        while(cursor.hasNext()){
            result.add(mapDBObjectToObject(cursor.next()));
        }
        return result;
    }
}
