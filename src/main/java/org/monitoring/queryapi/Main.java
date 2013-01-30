package org.monitoring.queryapi;

import com.mongodb.DBObject;

/**
 *
 * @author Michal Dubravcik
 */
public class Main {
    
    
    public static void main(String[] args) {
    
        Manager m = new Manager("192.168.219.129",27017, "postgres");
        Long gf = new Long("1358625189915");
        Query q = m.createQueryOnCollection("test");
                
        
        Iterable<DBObject> ob = q.execute();
        
        //while(ob.explain())
        for(DBObject o : ob){
            System.out.println(o);
            //System.out.println(new Date(Math.round((Double) o.get("time"))) + "    "+ o.get("value").toString());
        }
    }

}
