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
        Long gf = new Long("1358625189915");
        
        QueryMapReduce q = (QueryMapReduce) m.createQueryOnCollection("test2");
                
        
        Iterable<DBObject> ob = q.count(4);        
        
        for(Object o : ob){
            System.out.println(o);
        }
    }

}
