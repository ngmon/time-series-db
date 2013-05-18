package org.monitoring.smartmeter;

import java.util.Date;
import java.util.Random;
import org.monitoring.smartmeter.db.Database;
import org.monitoring.smartmeter.db.MongoDatabase;
import org.monitoring.smartmeter.db.PostgreSQLDatabase;
import org.monitoring.smartmeter.model.MeterEvent;

/**
 *
 * @author Michal
 */
public class Generate2 {

    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("postgres", "meterevent");
    static Random generator = new Random();
    static String[] sources = {"A1", "A3", "B9", "B10", "B11", "GEN", "SIM", "CENTER"};

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            MeterEvent event = new MeterEvent();
            Date date = new Date();

            event.setDate(date);
            event.setId(3001611+i);
            event.setSource(sources[generator.nextInt(sources.length)]);
            event.setValue((generator.nextGaussian() + 10) * 100);

            mongo.save(event);
        }
    }
}
