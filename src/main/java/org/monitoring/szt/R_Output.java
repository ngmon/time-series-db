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
public class R_Output {

    static Database postgre = new PostgreSQLDatabase();
    static MongoDatabase mongo = new MongoDatabase("rawevent2");

    public static void main(String[] args) {
        Timestamp from, to;
        from = Timestamp.valueOf("2012-11-22 09:58:09.955");
        to = Timestamp.valueOf("2012-11-22 09:58:10.955");

        List<RawEvent> list = postgre.getEventsInTimeRange(new Long(1), from, to);
        RawEvent re = list.get(1);
        re.setVersion(100);
        re.setId(new Long(9999997));
        System.out.println(re);


    }
}
