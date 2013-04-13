package org.monitoring.queryapisql;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.monitoring.queryapi.Event;
import org.monitoring.queryapi.preaggregation.Preaggregate;
import org.monitoring.queryapisql.PreaggregateSQL;
import org.monitoring.queryapisql.preaggregation.PostgreSQLDatabase;

/**
 *
 * @author Michal Dubravcik
 */

public class PreaggregateSQLCaliper extends SimpleBenchmark {

    List<Event> list = new ArrayList<Event>();
    int from, to;
    TimeUnit unit = TimeUnit.MINUTES;
    @Param TimeArray time;
    enum TimeArray{
        Hourly(new int[][] {{60, 1440}}),
        HourlyDaily(new int[][] {{60, 1440},{1440, 43200}}),
        HourlyWeeklyP(new int[][] {{60, 1440},{1440, 43200,3,3}}),
        HourlyWeeklyPMonthly(new int[][] {{60, 1440},{1440, 43200,3,3},{43200,525600}});
        final int[][] a;
        TimeArray(int[][] a){
            this.a = a;
        }
    }
    
    
    Preaggregate preaggregateSQL = new PreaggregateSQL();

    @Override
    protected void setUp() {
        PostgreSQLDatabase postgre = new PostgreSQLDatabase();
        postgre.dropTable();
        String[] fields = {"avg", "count", "sum"};
        postgre.createTable("aggregate60", 60, 1440, fields);
        postgre.createTable("aggregate1440", 1440, 43200, fields);
        postgre.createTable("aggregate43200", 43200, 525600, fields);
        postgre.createEventTable();
        Calendar cal = new GregorianCalendar(2013, 1, 1, 1, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        from = 0;
        to = 2000;
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
    
    public void timeSQLAggregate(int reps) {
        for (int i = 0; i < reps; i++) {
            preaggregateSQL.saveEvent(unit, time.a, list.get(i));
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(PreaggregateSQLCaliper.class, args);
    }
}
