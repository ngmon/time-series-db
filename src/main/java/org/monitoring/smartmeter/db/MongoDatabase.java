package org.monitoring.smartmeter.db;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.smartmeter.model.MeterEvent;

/**
 *
 * @author Michal
 */
public class MongoDatabase implements Database {

    Mongo m;
    DB db;
    DBCollection coll,statistics;
    MongoDatabaseMapper mapper = new MongoDatabaseMapper();

    public MongoDatabase(String database, String collection) {
        try {
            m = new Mongo("192.168.219.129", 27017);
            db = m.getDB(database);
            db.setWriteConcern(WriteConcern.SAFE);
            coll = db.getCollection(collection);
            statistics = db.getCollection("statistics");
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
        saveBatch(list,100);
    }
    
    public void saveBatch(List<MeterEvent> list, int count) {
        int i = 0;
        List<DBObject> batch = new LinkedList<DBObject>();
        for (MeterEvent event : list) {
            i++;
            batch.add(mapper.mapObjectToDBObject(event));
            if (i>0 && i % count == 0) {
                coll.insert(batch);                
                batch.clear();
            }            
        }
        if(!batch.isEmpty()){
            coll.insert(batch);
        }
    }   

    public void save(MeterEvent event) {
        coll.insert(mapper.mapObjectToDBObject(event));
        saveStatistics(event);
    }

    public void saveAll(List<MeterEvent> list) {
        for (MeterEvent event : list) {
            save(event);
        }
    }

    public List<MeterEvent> getAggregate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private void saveStatistics(MeterEvent event){
        long trim = event.getDate().getTime()%86400000;
        DBObject query = BasicDBObjectBuilder.start()
                .append("date", new Date(event.getDate().getTime()-trim))
                .append("source", event.getSource())
                .get();
        DBObject inc = BasicDBObjectBuilder.start()
                .append("daily", 1)
                .append("hourly."+((Integer)event.getDate().getHours()).toString(),1)
                .append("minutely."+event.getDate().getHours()+"."+((Integer)event.getDate().getMinutes()).toString(),1)
                .get();
        BasicDBObject update = new BasicDBObject("$inc", inc);
        statistics.update(query, update, true, false);
    }
}
