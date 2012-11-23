/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.caliperBenchmark;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import java.io.IOException;
import java.util.Locale;

/**
 *
 * @author Michal
 */
public class MongoDB extends SimpleBenchmark {

    Mongo m;
    DB db;
    DBCollection coll;
    Documents documents = new Documents();

    @Override
    protected void setUp() {

        try {
            m = new Mongo("192.168.219.129", 27017);
            db = m.getDB("test");
            db.setWriteConcern(WriteConcern.SAFE);
            coll = db.getCollection("test1");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void timeSaveEventToMongo(int reps) {
        for (int i = 0; i < reps; i++) {
            coll.insert(documents.getDocument(i));
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(MongoDB.class, args);
    }
}
