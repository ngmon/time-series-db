package org.monitoring.queryapi;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Michal Dubravcik
 */
public class TestApp {
    
    
    public static void main(String[] args) {
    
        Manager m = new Manager("192.168.219.129",27017, "postgres");
        
        List<String> it = new LinkedList<String>();
        it.add("blaa");
        it.add("bleeeee");
        
        DBObject ob = m.createQueryOnCollection("rawevent")
                .orderDesc("source")
                .field("sourceType").equal("INFRASTRUCTURE_STATISTICS")
                .field("source").exists()
                .orderDesc("v")
                .limit(5)
                .get();
        
        //while(ob.explain())
        
        System.out.println(ob.toString());
        
    }

}
