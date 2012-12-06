package org.monitoring.szt;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import java.util.Locale;
import org.monitoring.caliperBenchmark.Documents;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public class SZT_Mongo_save_batch extends SimpleBenchmark {

    @Param(
            {
                //"1",
                "3000"
            })
    int count;
    
    //@Param({"10","100","250","500","750","1000"}) int batch;
    @Param({"1500","2000"}) int batch;
    
    static MongoDatabase mongo_manual = new MongoDatabase("rawevent");
    Documents documents = new Documents();

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
        mongo_manual.deleteByVersion(33);  
    }

//    public void timeMongo_saveOne(int reps) {
//        for (int i = 0; i < reps; i++) {
//            for(RawEvent event : documents.getDocuments(i*count,i*count+count-1)){
//                mongo_manual.save(event);
//            }
//        }
//    }

    public void timeMongo_saveBatch(int reps) {
        for (int i = 0; i < reps; i++) {
            mongo_manual.saveBatch(documents.getDocuments(i*count,i*count+count-1),batch);
        }
    }
    
    

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(SZT_Mongo_save_batch.class, args);
    }
}
