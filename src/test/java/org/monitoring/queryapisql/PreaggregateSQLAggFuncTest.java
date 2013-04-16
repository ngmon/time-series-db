package org.monitoring.queryapisql;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monitoring.queryapi.Event;
import org.monitoring.queryapi.preaggregation.Preaggregate;
import org.monitoring.queryapisql.preaggregation.PostgreSQLDatabase;


/**
 *
 * @author Michal Dubravcik
 */

public class PreaggregateSQLAggFuncTest {

    static List<Event> list = new ArrayList<Event>();
    static PostgreSQLDatabase postgre = new PostgreSQLDatabase();

    @BeforeClass
    public static void setUp() throws InterruptedException {
        postgre.dropTable();        
        String[] fields = {"avg", "count", "sum"};
        postgre.createTable("aggregate60", 60, 1440, fields);
        postgre.createTable("aggregate1440", 1440, 43200, fields);
        postgre.createTable("aggregate43200", 43200, 525600, fields);
        postgre.createEventTable();
        Calendar cal = new GregorianCalendar(2013, 1, 2, 15, 0, 0);        
        cal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 10; i++) {
            Event event = new Event();
            cal.setTime(new Date(cal.getTime().getTime() + 1000*60*60*12)); 
            event.setDate(cal.getTime());
            event.setValue(10);
            list.add(event);
        }
    }

    @Test
    public void saveEvent() {
        Preaggregate preaggregate = new PreaggregateSQLAggFunc();
        for(Event event : list){
            int[][] times = {{60, 1440,0,0},{1440, 43200,0,0},{43200, 525600,0,0}};
            preaggregate.saveEvent(TimeUnit.MINUTES, times, event);
        }
        /*
        DBCollection c = m.getDb().getCollection("aggregate1440");
        Calendar cal = new GregorianCalendar(2013, 1, 2, 1, 0, 0);
        Date d = cal.getTime();
        DBObject doc = c.findOne(new BasicDBObject("date", cal.getTime()));
        assertNotNull("empty response from DB aggregate1440", doc);
        assertEquals(new Double(100), (Double) ((DBObject)(((DBObject)doc.get("agg")).get("0"))).get("count"));
        assertEquals(new Double(0), (Double) ((DBObject)(((DBObject)doc.get("agg")).get("1"))).get("count"));
        
        c = m.getDb().getCollection("aggregate60");
        cal = new GregorianCalendar(2013, 1, 2, 1, 0, 0);
        doc = c.findOne(new BasicDBObject("date", cal.getTime()));
        assertNotNull("empty response from DB aggregate60", doc);
        assertEquals(new Double(60), (Double) ((DBObject)(((DBObject)doc.get("agg")).get("14"))).get("count"));
        assertEquals(new Double(40), (Double) ((DBObject)(((DBObject)doc.get("agg")).get("15"))).get("count"));
        
        c = m.getDb().getCollection("aggregate2880");
        cal = new GregorianCalendar(2013, 1, 2, 1, 0, 0);
        doc = c.findOne(new BasicDBObject("date", cal.getTime()));
        assertNotNull("empty response from DB aggregate2880", doc);
        assertEquals(new Double(100), (Double) ((DBObject)(((DBObject)doc.get("agg")).get("0"))).get("count"));
        */
    }
}
