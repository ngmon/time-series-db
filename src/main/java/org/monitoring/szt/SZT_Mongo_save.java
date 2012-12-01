package org.monitoring.szt;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.sql.Timestamp;
import java.util.Locale;
import org.monitoring.caliperBenchmark.Documents;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.MongoDatabaseMapperMorphia;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class SZT_Mongo_save extends SimpleBenchmark {

    @Param(
            {
                //"1", 
                "10"
            })
    int count;
    
    Database mongo_manual;
    MongoDatabase mongo_morphia;
    Documents documents = new Documents();

    @Override
    protected void setUp() {
        mongo_manual = new MongoDatabase("rawevent");
        
        mongo_morphia = new MongoDatabase("rawevent2");
        mongo_morphia.setMapper(new MongoDatabaseMapperMorphia());
    }

    @Override
    protected void tearDown() {
        mongo_manual.deleteByVersion(33);        
        mongo_morphia.deleteByVersion(33);
    }

    public void timeMongo_manual_save(int reps) {
        for (int i = 0; i < reps; i++) {
            mongo_manual.save(documents.getDocuments(i*count,i*count+count-1));
        }
    }

    public void timeMongo_morphia_save(int reps) {
        for (int i = 0; i < reps; i++) {
            mongo_morphia.save(documents.getDocuments(i*count,i*count+count-1));
        }
    }
    
    

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        String[] x = {"--debug"};
        Runner.main(SZT_Mongo_save.class, args);
    }
}
