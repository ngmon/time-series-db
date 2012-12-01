package org.monitoring.smartmeter.db;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import java.util.List;
import org.monitoring.smartmeter.model.MeterEvent;

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

    public List<MeterEvent> getAllEvents() {
        return mapper.mapDBObjectsToObjects(coll.find());
    }

    public void save(List<MeterEvent> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void save(MeterEvent event) {
        coll.insert(mapper.mapObjectToDBObject(event));
    }

    public void saveAll(List<MeterEvent> list) {
        for (MeterEvent event : list) {
            save(event);
        }
    }

    public List<MeterEvent> getAggregate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
