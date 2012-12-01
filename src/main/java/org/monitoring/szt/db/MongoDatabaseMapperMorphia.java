package org.monitoring.szt.db;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public class MongoDatabaseMapperMorphia implements MongoDatabaseMapper{
    
    Morphia morphia;

    public MongoDatabaseMapperMorphia() {
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
        morphia = new Morphia();
        morphia.map(RawEvent.class);
    }
    

    public DBObject mapObjectToDBObject(RawEvent event) {
        return morphia.toDBObject(event);
    }

    

    public RawEvent mapDBObjectToObject(DBObject eventm) {
        return morphia.fromDBObject(RawEvent.class, eventm);

    }
    
    public List<RawEvent> mapDBObjectsToObjects(DBCursor cursor){
        List<RawEvent> result = new LinkedList<RawEvent>();
        while(cursor.hasNext()){
            result.add(mapDBObjectToObject(cursor.next()));
        }
        return result;
    }
}
