package org.monitoring.queryapi.caliper;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.google.code.morphia.Morphia;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.monitoring.queryapi.Event;
import org.monitoring.queryapi.Manager;
import org.monitoring.queryapi.preaggregation.Preaggregate;
import org.monitoring.queryapi.preaggregation.PreaggregateMongo;
import org.monitoring.queryapi.preaggregation.PreaggregateMongoMR;
import org.monitoring.queryapi.preaggregation.PreaggregateMongoMRI;
import org.monitoring.queryapi.preaggregation.compute.Compute;
import org.monitoring.queryapi.preaggregation.compute.ComputeAvg;
import org.monitoring.queryapi.PreaggregateSQL;
import org.monitoring.queryapi.sql.PostgreSQLDatabase;

/**
 *
 * @author Michal Dubravcik
 */
public class CaliperPreaggregateMongo extends SimpleBenchmark {

    static Manager m = new Manager();
    static List<Event> list = new ArrayList<Event>();
    static Morphia morphia = new Morphia();
    static DBCollection col =  m.getDb().getCollection("aggregate"); 
    
    int from, to;
    Compute computer = new ComputeAvg();
    TimeUnit unit = TimeUnit.MINUTES;
    
    @Param TimeArray time;
    enum TimeArray{
        Hourly(new int[][] {{60, 24}}),
        HourlyDaily(new int[][] {{60, 24},{1440, 30}}),
        HourlyDailyWeeklyP(new int[][] {{60, 24},{1440, 30},{1440, 30, 3,3}}),
        HourlyDailyWeeklyPMonthly(new int[][] {{60, 24},{1440, 30},{1440, 30,3,3},{43200,12}});
        final int[][] a;
        TimeArray(int[][] a){
            this.a = a;
        }
    }
    
    @Param({"1","2","3","4","5","6","7","8","9","10","50","100"}) int batch;
    
    Preaggregate preaggregateMR = new PreaggregateMongoMR(col);
    PreaggregateMongoMRI preaggregateMRN = new PreaggregateMongoMRI(col);

    @Override
    protected void setUp() {
        col = m.getDb().getCollection("aggregate");
        //m.getDb().dropDatabase();
        col.createIndex(new BasicDBObject("date", 1));
        Calendar cal = new GregorianCalendar(2013, 1, 1, 1, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        from = 0;
        to = 200;
        for (int i = from; i < to; i++) {
            Event event = new Event();
            event.setDate(cal.getTime());            
            cal.setTime(new Date(cal.getTime().getTime() + 1000 * 60 * 15));
            event.setValue(10);
            list.add(event);
        }
    }

    @Override
    protected void tearDown() {
        //m.getDb().dropDatabase();
    }

    public void imeMapReduceAggregate(int reps) {
        for (int i = 0; i < reps; i++) {
            preaggregateMR.saveEvent(unit, time.a, list.get(i));
        }
    }

    public void timeMapReduceNAggregate(int reps) {
        preaggregateMRN.setCountForMr(batch);
        for (int i = 0; i < reps; i++) {
            preaggregateMRN.saveEvent(unit, time.a, list.get(i));
        }
    }
    

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(CaliperPreaggregateMongo.class, args);
    }
}
