package org.monitoring.szt.db;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public class MongoDatabase implements Database {

    Mongo m;
    DB db;
    DBCollection coll;
    MongoDatabaseMapper mapper = new MongoDatabaseMapperManual();

    public MongoDatabase( String collection) {
        try {
            m = new Mongo("192.168.219.129", 27017);
            db = m.getDB("postgres");
            db.setWriteConcern(WriteConcern.SAFE);
            coll = db.getCollection(collection);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setMapper(MongoDatabaseMapper mapper){
        this.mapper = mapper;
    }

    public List<RawEvent> getAllEventsDefaultOrder() {
        return mapper.mapDBObjectsToObjects(coll.find());
    }

    public List<RawEvent> getAllEvents() {
        return mapper.mapDBObjectsToObjects(coll.find().sort(new BasicDBObject("occurrenceTimestamp", 1)));
    }

    public List<RawEvent> getEventsInTimeRange(Long simulationId, Timestamp from, Timestamp to) {
        BasicDBObject query = new BasicDBObject();
        query.put("simulationId", simulationId);
        BasicDBObject time = new BasicDBObject();
        time.put("$gte", from);
        time.put("$lte", to);
        query.put("occurrenceTimestamp", time);
        return mapper.mapDBObjectsToObjects(coll.find(query).sort(new BasicDBObject("occurrenceTimestamp", 1)));
    }

    public List<RawEvent> getAllEventsFromSimulation(Long simulationId) {
        BasicDBObject query = new BasicDBObject();
        query.put("simulationId", simulationId);
        return mapper.mapDBObjectsToObjects(coll.find(query).sort(new BasicDBObject("occurrenceTimestamp", 1)));
    }

    public List<RawEvent> getRRDFromSimulationAndSource(String source, Long simulationId) {
        BasicDBObject query = new BasicDBObject();
        query.put("simulationId", simulationId);
        query.put("source", source);
        BasicDBObject sort = new BasicDBObject();
        sort.put("measurementType", 1);
        sort.put("occurrenceTimestamp", 1);
        return mapper.mapDBObjectsToObjects(coll.find(query).sort(sort));

    }

    public void save(List<RawEvent> list) {
        for (RawEvent event : list) {
            save(event);
        }
        //saveBatch(list, 100);
    }

    //not efficient as save(list) from unknown reason
    public void saveBatch(List<RawEvent> list, int count) {
        int i = 0;
        List<DBObject> batch = new LinkedList<DBObject>();
        for (RawEvent event : list) {
            i++;
            batch.add(mapper.mapObjectToDBObject(event));
            if (i>0 && i % count == 0) {
                coll.insert(batch);                
                batch.clear();
            }            
        }
        coll.insert(batch);
    }

    public void deleteByVersion(int num) {
        DBObject object = new BasicDBObject("version", num);
        coll.remove(object);
    }

    public void save(RawEvent event) {
        coll.save(mapper.mapObjectToDBObject(event));
    }
}
