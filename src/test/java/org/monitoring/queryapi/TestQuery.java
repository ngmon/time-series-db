package org.monitoring.queryapi;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author Michal
 */
public class TestQuery {

    static Manager m = new Manager();
    static List<Document> list = new ArrayList<Document>();
    static Morphia morphia = new Morphia();
    static DBCollection col;
    
    final static int NUM = 1;

    @BeforeClass    
    public static void setUp() {
        m.setCollection("querytest");
        col = m.getCollection();
        morphia.map(Document.class);
        Datastore ds = morphia.createDatastore(m.getDb().getMongo(), m.getDb().toString());

        for (int i = 0; i < NUM; i++) {
            Document doc = new Document();
            Calendar cal = new GregorianCalendar(2013, 1, 13, 16, 0, i);
            DocumentData docData = new DocumentData();
            docData.setValue(i);
            doc.setTime( cal.getTime());
            doc.setData(docData);
            ds.save(doc);
            list.add(doc);
        }
    }

    @AfterClass
    public static void tearDown() {
        col.drop();
    }

    @Test
    public void find() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        for (DBObject ob : (Iterable<DBObject>) q.orderAsc("_id").find().get("result")) {
            assertEquals("find - returned objects are not same", list.get(i), morphia.fromDBObject(Document.class, ob));
            i++;
        }
    }

    @Test
    public void distinct() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        for (int ob : (Iterable<Integer>) q.distinct("v").get("result")) {
            assertEquals("distinct - different values", list.get(i).getData().getValue(), ob);
            i++;
        }
    }

    @Test
    public void countAll() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        assertEquals("count", list.size(), q.countAll());
    }

    @Test
    public void count() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        for(DBObject ob : (Iterable<DBObject>) q.setStep(100000).count().get("result")){
            assertEquals( new Double(NUM), (Double) ob.get("value"));
            i++;            
        }
    }
    
    @Test
    public void avg() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        for(DBObject ob : (Iterable<DBObject>) q.setStep(1).orderAsc("_id").avg("v").get("result")){
            assertEquals(list.get(i).getData().getValue(), ob.get("value"));
            i++;
        }
    }
    
    @Test
    public void min() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        for(DBObject ob : (Iterable<DBObject>) q.setStep(100000).avg("v").get("result")){
            assertEquals(new Integer(0), ob.get("value"));
            i++;
        }
    }
    
    @Test
    public void max() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        for(DBObject ob : (Iterable<DBObject>) q.setStep(100000).avg("v").get("result")){
            assertEquals(new Integer(NUM-1), ob.get("value"));
            i++;
        }
    }
    
    @Test
    public void median() {
        Query q = m.createQueryOnCollection("querytest");
        int i = 0;
        DBObject res = (DBObject) q.setStep(100000).avg("v").get("result");
        assertNotEquals("Empty result",0, res.toMap().size());
        for(DBObject ob : (Iterable<DBObject>) res){
            assertEquals(new Integer(NUM/2), ob.get("value"));
            i++;
        }
    }
}
