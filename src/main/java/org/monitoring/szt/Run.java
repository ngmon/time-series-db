/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class Run {

    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("postgres", "rawevent");

    public static void main(String[] args) {
        Timestamp from, to;
        from = Timestamp.valueOf("2012-11-06 19:20:00.0");
        to = Timestamp.valueOf("2012-11-08 10:20:00.0");

        //List<RawEvent> list = mongo.getEventsInTimeRange(new Long(1), from, to);
        //System.out.println(list.size());
        
        List<RawEvent> list = postgre.getEventsInTimeRange(new Long(1), from, to);
        System.out.println(list.size());
    }
}
