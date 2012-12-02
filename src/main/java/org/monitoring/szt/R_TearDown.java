package org.monitoring.szt;

import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.MongoDatabaseMapperMorphia;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class R_TearDown {

    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("rawevent");
    static MongoDatabase mongo_morphia = new MongoDatabase("rawevent2");

    public static void main(String[] args) throws InterruptedException {
        
        mongo_morphia.setMapper(new MongoDatabaseMapperMorphia());

        mongo.deleteByVersion(33);
        mongo_morphia.deleteByVersion(33);
        postgre.deleteByVersion(33);

    }
}
