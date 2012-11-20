/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt;

import java.util.List;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class ExportFromPostgreToMongo {
    
    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("postgres", "rawevent");

    public static void main(String[] args) {

        List<RawEvent> list = postgre.getAllEventsDefaultOrder();
        mongo.save(list);
        
        System.out.println("From Postgre " + list.size() + " exported to Mongo");
        
    }
}
