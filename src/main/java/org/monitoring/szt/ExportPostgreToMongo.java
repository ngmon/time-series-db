/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.PostgreSQLDatabase;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public class ExportPostgreToMongo {

    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("postgres", "rawevent");

    public static void main(String[] args) {
        Timestamp from, to;
        Date start_read, stop_read, start_write, stop_write;
        from = Timestamp.valueOf("2012-11-22 09:58:09.955");
        int count=0;
        while(count < 2444692){                        
            to = new Timestamp(from.getTime()+(60*10*1000));
            
            start_write = new Date();                                    
            List<RawEvent> list = postgre.getEventsInTimeRange(new Long(1), from, to);
            stop_write = new Date();
                        
            start_read = new Date();
            mongo.save(list);
            stop_read = new Date();   
            
            count += list.size();
            
            System.out.println("Processed in batch: " + list.size() + ", Percent " + (100*count/2444672)
                    + " , read time elapsed " + (stop_read.getTime()-start_read.getTime()) + "ms"
                    + ", write time elapsed " + (stop_write.getTime()-start_write.getTime()) + "ms");
            
            from=to;
        }
        //List<RawEvent> list = mongo.getEventsInTimeRange(new Long(1), from, to);
        //System.out.println(list.size());
        
        //List<RawEvent> list = postgre.getEventsInTimeRange(new Long(1), from, to);
        //System.out.println(list);
    }
}
