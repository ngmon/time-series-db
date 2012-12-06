package org.monitoring.szt;

import com.google.caliper.Param;
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
public class SZT_getEventsInTimeRange extends SimpleBenchmark {
    
    @Param({"1"/*, "60", "180"*/}) int seconds;
    
    Database mongo = new MongoDatabase("rawevent");
    Database postgre = new PostgreSQLDatabase();
    Timestamp from, to;

    @Override
    protected void setUp() {
        from = Timestamp.valueOf("2012-11-22 09:58:29");
        //to = Timestamp.valueOf("2012-11-23 02:00:30");
    }

    @Override
    protected void tearDown() {
    }

    public void timeMongo_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println( mongo.getEventsInTimeRange(new Long(1), from, new Timestamp(from.getTime() + seconds*1000)).size());
        }
    }

    public void timePostgre_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println(postgre.getEventsInTimeRange(new Long(1), from, new Timestamp(from.getTime() + seconds*1000)).size());
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(SZT_getEventsInTimeRange.class, args);
    }
}
