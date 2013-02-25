package org.monitoring.queryapi;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Michal Dubravcik
 */
public class Main {
    
    
    public static void main(String[] args) {
    
        Manager m = new Manager("192.168.219.129",27017, "postgres");
        
        QueryMapReduce q = (QueryMapReduce) m.createQueryOnCollection("test2");
        q.setGroupTime(10000); 
        
        Calendar cal = new GregorianCalendar(2013, 1, 13, 16, 0, 0);
        Date start = cal.getTime();
        
        cal = new GregorianCalendar(2013, 1, 13, 19, 0, 0);
        Date end = cal.getTime();
        
        q.fromDate(start).toDate(end);
        
//        DBObject ob =  q.difference(new BasicDBObject(),"d.v");  
//        System.out.println(ob);
        
//        DBObject ob = q.reasonFor("s", "C", "s");
//        System.out.println(ob);
        
//        DBObject ob2 = q.cacheAvg("v");
//        for(Object ob : (Iterable)ob2.get("result")){
//            System.out.println(ob);
//        }
       
        DBObject ob3 = q.avgC("v");
        System.out.println(ob3);      
        
        
        
    }

}
