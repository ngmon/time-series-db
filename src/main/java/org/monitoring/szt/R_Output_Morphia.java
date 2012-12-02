package org.monitoring.szt;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.MongoDatabaseMapperMorphia;
import org.monitoring.szt.db.PostgreSQLDatabase;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public class R_Output_Morphia {

    static Database postgre = new PostgreSQLDatabase();
    static MongoDatabase mongo = new MongoDatabase("rawevent2");

    public static void main(String[] args) {
        mongo.setMapper(new MongoDatabaseMapperMorphia());
        Timestamp from, to;
        from = Timestamp.valueOf("2012-11-22 09:58:29");
        to = Timestamp.valueOf("2012-11-22 09:58:30");

        List<RawEvent> list = mongo.getEventsInTimeRange(new Long(1), from, to);
        

        System.out.println(list.size());



    }
}
