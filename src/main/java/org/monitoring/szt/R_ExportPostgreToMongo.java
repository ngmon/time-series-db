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
public class R_ExportPostgreToMongo {

    static Database postgre = 
            new PostgreSQLDatabase("rawevent","rawevent_values");
    static Database mongo = new MongoDatabase( "rawevent_large");

    public static void main(String[] args) {
        Timestamp from, to;
        Date start_read, stop_read, start_write, stop_write;
        from = Timestamp.valueOf("2012-12-11 14:00:00.000");
        int count=9236306;
        int total = 14604205;
        List<RawEvent> list = null;
        while(count < total){ //&& (count == 0 || ! list.isEmpty())){                        
            to = new Timestamp(from.getTime()+(10*1000));
            
            start_read = new Date();                                    
            list = postgre.getEventsInTimeRange(new Long(870351), from, to);
            stop_read = new Date();
                        
            start_write = new Date();
            mongo.save(list);
            stop_write = new Date();   
            
            count += list.size();
            
            System.out.println("Processed in batch : "+ list.size() + ", Percent " + (100*count/total)
                    + " , read time elapsed " + (stop_read.getTime()-start_read.getTime()) + "ms"
                    + ", write time elapsed " + (stop_write.getTime()-start_write.getTime()) + "ms, (from "+from.toString()+")");
            
            from=to;
        }
        //List<RawEvent> list = mongo.getEventsInTimeRange(new Long(1), from, to);
        //System.out.println(list.size());
        
        //List<RawEvent> list = postgre.getEventsInTimeRange(new Long(1), from, to);
        //System.out.println(list);
    }
}
