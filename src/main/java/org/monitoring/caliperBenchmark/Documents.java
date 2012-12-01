package org.monitoring.caliperBenchmark;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.db.Database;
import org.monitoring.szt.db.MongoDatabase;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public class Documents {

    public static List<RawEvent> documents = new LinkedList<RawEvent>();

    public Documents() {
        setUpDocumentsFromMongo();
    }

    public void setUpDocumentsFromFile() {
        BufferedReader br = null;
        List<DBObject> file = new LinkedList<DBObject>();
        documents.clear();
        try {
            br = new BufferedReader(new FileReader("C:/Users/Michal/Documents/NetBeansProjects/Monitoring/src/main/resources/events.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                DBObject object = ((DBObject) JSON.parse(line));
                object = (DBObject) object.get("Event");
                for (String s : object.keySet()) {
                    if (s.contains(".")) {
                        object.put(s.replace(".", "_"), object.get(s));
                        object.removeField(s);
                    }
                }
                file.add(object);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //documents.addAll(file);
        throw new UnsupportedOperationException("Not supported yet.");

    }
    
    public void setUpDocumentsFromMongo() {
        Database mongo = new MongoDatabase( "rawevent");
        Timestamp from = Timestamp.valueOf("2012-11-22 09:58:29");
        long interval = 1000 * 60 * 100; //100 minutes => 127 000 events
        documents = mongo.getEventsInTimeRange(new Long(1), from, new Timestamp(from.getTime() + interval));
        for(RawEvent event : documents){
            event.setVersion(33);
            event.setId(event.getId()+3000000);
        }
    }

    public RawEvent getDocument(int i) {
        return documents.get(i);
    }

    public List<RawEvent> getDocuments(int from, int to) {
        return documents.subList(from, to);
    }

    public int count() {
//        int size = 0;
//        for (RawEvent t : documents) {
//            size++;
//        }
        return documents.size();
    }

}
