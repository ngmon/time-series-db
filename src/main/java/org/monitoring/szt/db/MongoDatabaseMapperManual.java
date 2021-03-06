package org.monitoring.szt.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.model.MeasurementType;
import org.monitoring.szt.model.RawEvent;
import org.monitoring.szt.model.SourceType;

/**
 *
 * @author Michal
 */
public class MongoDatabaseMapperManual implements MongoDatabaseMapper {

    @Override
    public DBObject mapObjectToDBObject(RawEvent event) {
        DBObject eventm = new BasicDBObject();
        eventm.put("_id", event.getId()); //_id in rawevent numeric 
        eventm.put("measurementType", event.getMeasurementType().toString());
        eventm.put("occurrenceTimestamp", event.getOccurrenceTimestamp());
        eventm.put("simulationId", event.getSimulationId());
        eventm.put("simulationTimestamp", event.getSimulationTimestamp());
        eventm.put("source", event.getSource());
        eventm.put("sourceType", event.getSourceType().toString());
        eventm.put("version", event.getVersion());
        eventm.put("values", event.getValues());
        return eventm;
    }

    @Override
    public RawEvent mapDBObjectToObject(DBObject eventm) {
        RawEvent event = new RawEvent();
        event.setId((Long) eventm.get("_id"));
        event.setMeasurementType(MeasurementType.valueOf((String) eventm.get("measurementType")));
        event.setOccurrenceTimestamp((new Timestamp(((Date) eventm.get("occurrenceTimestamp")).getTime())));
        event.setSimulationId((Long) eventm.get("simulationId"));
        event.setSimulationTimestamp((Long) eventm.get("simulationTimestamp"));
        event.setSource((String) eventm.get("source"));
        event.setSourceType(SourceType.valueOf((String) eventm.get("sourceType")));
        event.setVersion((Integer) eventm.get("version"));
        event.setValues((List<String>)eventm.get("values"));
        return event;
    }
    
    @Override
    public List<RawEvent> mapDBObjectsToObjects(DBCursor cursor){
        List<RawEvent> result = new LinkedList<RawEvent>();
        while(cursor.hasNext()){
            result.add(mapDBObjectToObject(cursor.next()));
        }
        return result;
    }
}
