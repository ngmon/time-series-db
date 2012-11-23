/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.util.Locale;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class SZT_source extends SimpleBenchmark {

    Database mongo = new MongoDatabase("postgres", "rawevent");
    
    Database postgre = new PostgreSQLDatabase();
    
    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public void timeMongo_getRRDFromSimulationAndSource(int reps) {
        for (int i = 0; i < reps; i++) {            
            mongo.getRRDFromSimulationAndSource("ElinComServer-network-up-c.rrd", new Long(1));            
        }
    }
    
    public void timePostgre_getRRDFromSimulationAndSource(int reps) {
        for (int i = 0; i < reps; i++) {            
            postgre.getRRDFromSimulationAndSource("ElinComServer-network-up-c.rrd", new Long(1));            
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(SZT_source.class, args);
    }
}
