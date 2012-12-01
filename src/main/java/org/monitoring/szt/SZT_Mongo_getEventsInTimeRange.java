package org.monitoring.szt;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.sql.Timestamp;
import java.util.Locale;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.MongoDatabaseMapperMorphia;

/**
 *
 * @author Michal
 */
public class SZT_Mongo_getEventsInTimeRange extends SimpleBenchmark {
    
    @Param({"1"}) int seconds;
    
    Database manual;
    MongoDatabase morphia;
    Timestamp from, to;

    @Override
    protected void setUp() {
        from = Timestamp.valueOf("2012-11-22 09:58:29");
        
        manual = new MongoDatabase("rawevent");
        
        morphia = new MongoDatabase("rawevent2");
        morphia.setMapper(new MongoDatabaseMapperMorphia());
    }

    @Override
    protected void tearDown() {
    }

    public void timeMongo_Manual_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println( manual.getEventsInTimeRange(new Long(1), from, new Timestamp(from.getTime() + seconds*1000)).size());
        }
    }

    public void timeMongo_Morphia_getEventsInTimeRange(int reps) {
        for (int i = 0; i < reps; i++) {
            System.out.println(morphia.getEventsInTimeRange(new Long(1), from, new Timestamp(from.getTime() + seconds*1000)).size());
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(SZT_Mongo_getEventsInTimeRange.class, args);
    }
}
