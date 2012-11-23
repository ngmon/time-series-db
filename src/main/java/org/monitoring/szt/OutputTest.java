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
public class OutputTest {

    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("postgres", "rawevent");

    public static void main(String[] args) {
        Timestamp from, to;
        from = Timestamp.valueOf("2012-11-22 09:58:09.955");
        to = Timestamp.valueOf("2012-11-22 09:58:10.955");
                                       
            List<RawEvent> list = postgre.getEventsInTimeRange(new Long(1), from, to);
            System.out.println(list.get(1));
           
        

    }
}
