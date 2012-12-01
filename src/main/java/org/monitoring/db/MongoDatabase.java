package org.monitoring.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import java.util.List;

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

    public void saveDocument(DBObject object) {
        coll.insert(object);
    }
    
    public void saveDocuments(List <DBObject> documents){
        coll.insert(documents);
    }
    
    public void tearDown(){
            coll.drop();
    }
}
