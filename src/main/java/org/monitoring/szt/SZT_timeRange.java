/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.sql.Timestamp;
import java.util.Locale;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class SZT_timeRange extends SimpleBenchmark {

    Database mongo = new MongoDatabase("postgres", "rawevent");
    Database postgre = new PostgreSQLDatabase();
    Timestamp from, to;

    @Override
    protected void setUp() {
        from = Timestamp.valueOf("2012-11-23 02:00:00");
        to = Timestamp.valueOf("2012-11-23 02:01:00");
    }

    @Override
    protected void tearDown() {
    }

    public void timeMongo_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println( mongo.getEventsInTimeRange(new Long(1), from, to).size());
        }
    }

    public void timePostgre_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println(postgre.getEventsInTimeRange(new Long(1), from, to).size());
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(SZT_timeRange.class, args);
    }
}
