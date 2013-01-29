package org.monitoring.queryapi;

import com.mongodb.DBObject;
import java.util.Date;

/**
 *
 * @author Michal Dubravcik
 */
public class TestApp {
    
    
    public static void main(String[] args) {
    
        Manager m = new Manager("192.168.219.129",27017, "postgres");
        Long gf = new Long("1358625189915");
        Query q = m.createQueryOnCollection("test")
                .sum(2000,"v").rename();
        
        System.out.println(q.get());
        
        
        Iterable<DBObject> ob = q.execute();
        
        //while(ob.explain())
        for(DBObject o : ob){
            System.out.println(o);
            //System.out.println(new Date(Math.round((Double) o.get("time"))) + "    "+ o.get("value").toString());
        }
    }

}
