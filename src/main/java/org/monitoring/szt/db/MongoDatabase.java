/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import org.monitoring.szt.RawEvent;

/**
 *
 * @author Michal
 */
public class MongoDatabase implements Database {

    Mongo m;
    DB db;
    DBCollection coll;
    MongoDatabaseMapper mapper = new MongoDatabaseMapper();

    public MongoDatabase(String database, String collection) {
        try {
            m = new Mongo("192.168.219.129", 27017);
            db = m.getDB(database);
            db.setWriteConcern(WriteConcern.SAFE);
            coll = db.getCollection(collection);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

    public List<RawEvent> getAllEventsDefaultOrder(){
        return mapper.mapDBObjectsToObjects(coll.find());
    }
    
    public List<RawEvent> getAllEvents() {
        return mapper.mapDBObjectsToObjects(coll.find().sort(new BasicDBObject("occurrenceTimestamp", "1")));
    }

    public List<RawEvent> getEventsInTimeRange(Long simulationId, Timestamp  from, Timestamp  to) {
        BasicDBObject query = new BasicDBObject();
        query.put("simulationId", simulationId);
        BasicDBObject time = new BasicDBObject();
        time.put("$gte", from);
        time.put("$lte", to);
        query.put("occurrenceTimestamp", time);
        return mapper.mapDBObjectsToObjects(coll.find(query).sort(new BasicDBObject("occurrenceTimestamp", "1")));
    }

    public List<RawEvent> getAllEventsFromSimulation(Long simulationId) {
        BasicDBObject query = new BasicDBObject();
        query.put("simulationId", simulationId);
        return mapper.mapDBObjectsToObjects(coll.find(query).sort(new BasicDBObject("occurrenceTimestamp", "1")));
    }

    public List<RawEvent> getRRDFromSimulationAndSource(String source, Long simulationId) {
        BasicDBObject query = new BasicDBObject();
        query.put("simulationId", simulationId);
        query.put("source", source);
        BasicDBObject sort = new BasicDBObject();
        sort.put("measurementType",1);
        sort.put("occurrenceTimestamp", 1);
        return mapper.mapDBObjectsToObjects(coll.find(query).sort(sort));

    }
    
    public void save(List<RawEvent> list){
        for(RawEvent event : list){
            coll.insert(mapper.mapObjectToDBObject(event));
            }
    }
}
