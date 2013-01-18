package org.monitoring.queryapi;

import com.mongodb.DBCursor;
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
    
        Manager m = new Manager("192.168.219.129",27017);
        
        List<String> it = new LinkedList<String>();
        it.add("blaa");
        it.add("bleeeee");
        
        DBCursor ob = m.createQueryOnCollection("rawevent").field("sourceType").equal("SIMULATOR_LOG").execute();
        
        //while(ob.explain())
        
        System.out.println(ob);
        
    }

}
