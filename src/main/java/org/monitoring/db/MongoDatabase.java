/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.db;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;

/**
 *
 * @author Michal
 */
public class MongoDatabase implements Database {

    Mongo m;
    DB db;
    DBCollection coll;

    public MongoDatabase() {
        try {
            m = new Mongo("192.168.219.129", 27017);
            db = m.getDB("test");
            db.setWriteConcern(WriteConcern.SAFE);
            coll = db.getCollection("test1");
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

    public void saveDocument(DBObject object) {
        coll.insert(object);
    }
    
    
    public void tearDown(){
            coll.drop();
    }
}
