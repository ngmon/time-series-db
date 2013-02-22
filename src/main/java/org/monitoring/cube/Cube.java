package org.monitoring.cube;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.List;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.MongoDatabaseMapper;
import org.monitoring.szt.db.MongoDatabaseMapperManual;
import org.monitoring.szt.model.RawEvent;

/**
 * Hello world!
 *
 */
public class Cube {

    private static CubeEvent cube;

    public static void main(String[] args) {
        cube = new CubeEvent();
        Database mongo = new MongoDatabase("rawevent_week");
        long miliseconds = 150021;
        Timestamp from = Timestamp.valueOf("2012-12-01 00:00:00");
        while (true) {
            Timestamp to = new Timestamp(from.getTime() + miliseconds);
            List<RawEvent> list = mongo.getEventsInTimeRange(new Long(56301), from, to);
            for (RawEvent event : list) {
                Cube_RawEvent ev = new Cube_RawEvent(event);
                ev.sendViaRest();
            }
            
            from = to;
            System.out.println("size:"+list.size()+" date:"+from);
            
        }

    }
}
