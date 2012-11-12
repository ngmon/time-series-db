/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.caliperBenchmark;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.util.Locale;
import org.monitoring.db.Database;
import org.monitoring.db.DerbyDatabase;
import org.monitoring.db.MongoDatabase;

/**
 *
 * @author Michal
 */
public class CaliperDBStrategy extends SimpleBenchmark {

    Database mongo = new MongoDatabase();
    Database derby = new DerbyDatabase();
    Documents documents = new Documents();

    @Override
    public void setUp() {
    }
    
    @Override
    public void tearDown(){
        mongo.tearDown();
        derby.tearDown();
    }

    public void timeMongoStrategy(int reps) {
        for (int i = 0; i < reps; i++) {
            mongo.saveDocument(documents.getDocument(i));
        }
    }

    public void timeDerbyStrategy(int reps) {
        for (int i = 0; i < reps; i++) {            
            derby.saveDocument(documents.getDocument(i));
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(CaliperDBStrategy.class, args);
    }
}
