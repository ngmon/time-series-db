package org.monitoring.smartmeter;

import org.monitoring.smartmeter.model.MeterEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import org.monitoring.smartmeter.db.Database;
import org.monitoring.smartmeter.db.MongoDatabase;
import org.monitoring.smartmeter.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class Generate {

    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("postgres", "meterevent");
    static Random generator = new Random();
    static String[] sources = {"A1","A3","B9","B10","B11","GEN","SIM","CENTER"};
    
    public static void main(String[] args) {
        Date start = new Date();
        Date stop;
        for (int i = 116186; i < 1000000; i++) {
            Date date = new Date();

            MeterEvent event = new MeterEvent();
            event.setDate(date);
            event.setId(i);
            event.setSource(sources[generator.nextInt(sources.length)]);
            event.setValue((generator.nextGaussian()+10)*100);
            
            postgre.save(event);
            mongo.save(event);
            
            if(i%1000 == 0 ){
                stop = new Date();                
                System.out.println("Done " + i + ", time: "+ (stop.getTime() - start.getTime()));
                start = new Date();
            }
        }
    }
}