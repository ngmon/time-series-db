/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.caliperBenchmark;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Michal
 */
public class Documents {

    private static List<DBObject> documents = new LinkedList<DBObject>();

    public Documents() {
        setUpDocuments();
    }

    public void setUpDocuments() {
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
//        for (int i = 0; i < 10; i++) {
//            List<DBObject> pom = new LinkedList<DBObject>();
//            pom.addAll(file);
//            Collections.copy(pom, file);
//            documents.addAll(pom);
//        }
        documents.addAll(file);

    }

    public DBObject getDocument(int i) {
        return documents.get(i);
    }

    public List<DBObject> getDocuments(int from, int to) {
        return documents.subList(from, to);
    }

    public int count() {
        int size = 0;
        for (DBObject t : documents) {
            size++;
        }
        return size;
    }

}
