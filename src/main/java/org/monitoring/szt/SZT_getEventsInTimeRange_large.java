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
public class SZT_getEventsInTimeRange_large extends SimpleBenchmark {
    
    @Param({"1"/*, "60", "180"*/}) int seconds;
    
    Database mongo = new MongoDatabase("rawevent_large");
    Database postgre = new PostgreSQLDatabase();
    Timestamp from, to;
    long hour = 0; //2 *60*60*1000;

    @Override
    protected void setUp() {
        from = Timestamp.valueOf("2012-12-11 18:00:03");
        //to = Timestamp.valueOf("2012-11-23 02:00:30");
    }

    @Override
    protected void tearDown() {
    }

    public void timeMongo_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println( mongo.getEventsInTimeRange(new Long(870351), new Timestamp(from.getTime()+i*hour), new Timestamp(from.getTime() + seconds*1080 + i*hour)).size());
        }
    }

    public void timePostgre_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println(postgre.getEventsInTimeRange(new Long(870351), new Timestamp(from.getTime()+i*hour), new Timestamp(from.getTime() + seconds*1080 + i*hour)).size());
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(SZT_getEventsInTimeRange_large.class, args);
    }
}
