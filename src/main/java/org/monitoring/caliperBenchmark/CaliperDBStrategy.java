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
import org.monitoring.db.MySQLDatabase;
import org.monitoring.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 * results online
 *   http://microbenchmarks.appspot.com/run/michal.dubravcik@gmail.com/org.monitoring.caliperBenchmark.CaliperDBStrategy
 */
public class CaliperDBStrategy extends SimpleBenchmark {

    Database mongo = new MongoDatabase();
    Database postgre = new PostgreSQLDatabase();
    Database mysql = new MySQLDatabase();
    Database derby = new DerbyDatabase();
    Documents documents = new Documents();

    @Override
    public void setUp() {
    }
    
    @Override
    public void tearDown(){
        mongo.tearDown();
        postgre.tearDown();
    }

    public void timeMongoStrategy(int reps) {
        for (int i = 0; i < reps; i++) {
            mongo.saveDocument(documents.getDocument(i));
        }
    }

    public void timePostgreSQLStrategy(int reps) {
        for (int i = 0; i < reps; i++) {            
            postgre.saveDocument(documents.getDocument(i));
        }
    }
    public void timeDerbySQLStrategy(int reps) {
        for (int i = 0; i < reps; i++) {            
            derby.saveDocument(documents.getDocument(i));
        }
    }
    public void timeMySQLStrategy(int reps) {
        for (int i = 0; i < reps; i++) {            
            mysql.saveDocument(documents.getDocument(i));
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(CaliperDBStrategy.class, args);
    }
}
