package org.monitoring.szt;

import java.util.Date;
import org.monitoring.caliperBenchmark.Documents;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.MongoDatabaseMapperMorphia;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class SaveTest {

    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("rawevent");
    static MongoDatabase mongo_morphia = new MongoDatabase("rawevent2");

    public static void main(String[] args) throws InterruptedException {
        Date start, stop;
        mongo_morphia.setMapper(new MongoDatabaseMapperMorphia());

        Documents documents = new Documents();

        start = new Date();
        mongo.save(documents.getDocuments(0, 100));
        stop = new Date();

        System.out.println("mongo" + (stop.getTime() - start.getTime()) + "ms");

        start = new Date();
        mongo_morphia.save(documents.getDocuments(0, 100));
        stop = new Date();

        System.out.println("morphia" + (stop.getTime() - start.getTime()) + "ms");


        start = new Date();
        //postgre.save(documents.getDocuments(0, 100));
        stop = new Date();

        System.out.println("postgre" + (stop.getTime() - start.getTime()) + "ms");



        System.out.println("sleep");
        Thread.sleep(25000);
        System.out.println("wake up");
        mongo.deleteByVersion(33);
        mongo_morphia.deleteByVersion(33);
        postgre.deleteByVersion(33);

    }
}
