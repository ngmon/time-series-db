package org.monitoring.queryapi;

import com.mongodb.DBObject;
import java.util.Date;

/**
 *
 * @author Michal Dubravcik
 */
public class Main {
    
    
    public static void main(String[] args) {
    
        Manager m = new Manager("192.168.219.129",27017, "postgres");
        Long gf = new Date().getTime();
        QueryMapReduce q = (QueryMapReduce) m.createQueryOnCollection("test2");//.fromDate(new Date(gf-1000*60*90));
         
        System.out.println(q.count());
        
        Iterable<DBObject> ob = q.count(5);        
        
        for(Object o : ob){
            System.out.println(o);
        }
    }

}
