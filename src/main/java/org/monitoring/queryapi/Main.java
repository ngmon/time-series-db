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
                
//        Iterable<DBObject> ob =  q.max(1000,"v");        
//        for(Object o : ob){
//            System.out.println(o);
//        }
        
        DBObject ob = q.reasonFor("s", "Q");
        System.out.println(ob);
        
//        DBObject ob2 = q.distinct("v");
//        System.out.println(ob2);
        
        
        
    }

}
