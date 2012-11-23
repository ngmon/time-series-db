/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.smartmeter.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.smartmeter.model.MeterEvent;

/**
 *
 * @author Michal
 */
public class MongoDatabaseMapper {

    public DBObject mapObjectToDBObject(MeterEvent event) {
        DBObject eventm = new BasicDBObject();
        eventm.put("_id", event.getId());
        eventm.put("source", event.getSource());
        eventm.put("date", event.getDate());
        eventm.put("value", event.getValue());
        return eventm;
    }

    public MeterEvent mapDBObjectToObject(DBObject eventm) {
        MeterEvent event = new MeterEvent();
        event.setId((Integer) eventm.get("id"));
        event.setSource((String) eventm.get("source"));
        event.setDate((Date) eventm.get("date"));
        event.setValue((Double) eventm.get("value"));
        return event;
    }
    
    public List<MeterEvent> mapDBObjectsToObjects(DBCursor cursor){
        List<MeterEvent> result = new LinkedList<MeterEvent>();
        while(cursor.hasNext()){
            result.add(mapDBObjectToObject(cursor.next()));
        }
        return result;
    }
}
