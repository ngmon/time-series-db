package org.monitoring.queryapi;

import com.mongodb.BasicDBObject;
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
        QueryMapReduce q = (QueryMapReduce) m.createQueryOnCollection("test2");
        
        
//        DBObject ob =  q.difference(new BasicDBObject(),"d.v");  
//        System.out.println(ob);
        
//        DBObject ob = q.reasonFor("s", "C", "s");
//        System.out.println(ob);
        
        DBObject ob2 = q.cacheAvg("v");
        //System.out.println(ob2);
        for(Object ob : (Iterable)ob2.get("result")){
            System.out.println(ob);
        }
        
        
        
        
    }

}
