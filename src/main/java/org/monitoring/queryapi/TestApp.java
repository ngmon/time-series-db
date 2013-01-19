package org.monitoring.queryapi;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Michal Dubravcik
 */
public class TestApp {
    
    
    public static void main(String[] args) {
    
        Manager m = new Manager("192.168.219.129",27017, "postgres");
        
        Query q = m.createQueryOnCollection("test")
                .count(2000);
        
        System.out.println(q.get());
        
        
        Iterable<DBObject> ob = q.execute();
        
        //while(ob.explain())
        for(DBObject o : ob){
            System.out.println(new Date(Math.round((Double) o.get("_id"))) + "    "+ o.get("count").toString());
        }
    }

}
