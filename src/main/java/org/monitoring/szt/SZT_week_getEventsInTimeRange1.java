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
public class SZT_week_getEventsInTimeRange1 extends SimpleBenchmark {
    
    @Param({"150"}) int miliseconds;
    
    Database mongo = new MongoDatabase("rawevent_week");
    Database postgre = new PostgreSQLDatabase("public2.rawevent","public2.rawevent_values");
    Timestamp from, to;

    @Override
    protected void setUp() {
        from = Timestamp.valueOf("2012-12-02 01:00:00.000");
        //to = Timestamp.valueOf("2012-11-23 02:00:30");
    }

    @Override
    protected void tearDown() {
    }

    public void timeMongo_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println( mongo.getEventsInTimeRange(new Long(56301), from, new Timestamp(from.getTime() + miliseconds)).size());
        }
    }

    public void timePostgre_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println(postgre.getEventsInTimeRange(new Long(56301), from, new Timestamp(from.getTime() + miliseconds)).size());
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(SZT_week_getEventsInTimeRange1.class, args);
    }
}
