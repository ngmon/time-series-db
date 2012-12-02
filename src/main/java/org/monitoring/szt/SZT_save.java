package org.monitoring.szt;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.util.Locale;
import org.monitoring.caliperBenchmark.Documents;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class SZT_save extends SimpleBenchmark {

    @Param(
            {
                //"1", 
                "50"
            })
    int count;
    
    static Database mongo = new MongoDatabase("rawevent");
    Database postgre = new PostgreSQLDatabase();
    
    static Documents documents = new Documents();

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
        mongo.deleteByVersion(33);        
        //postgre.deleteByVersion(33);
    }

    public void timeMongo_save(int reps) {
        for (int i = 0; i < reps; i++) {
            mongo.save(documents.getDocuments(i*count,i*count+count-1));
        }
    }

//    public void timePostgreSQL_save(int reps) {
//        for (int i = 0; i < reps; i++) {
//            postgre.save(documents.getDocuments(i*count,i*count+count-1));
//        }
//    }
    
    

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        mongo.deleteByVersion(33); 
        Runner.main(SZT_save.class, args);        
    }
}
