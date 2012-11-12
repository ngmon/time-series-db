/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.caliperBenchmark;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michal
 */
public class CaliperDB extends SimpleBenchmark {
    //number of inserted documents
    @Param({"10", "600"})
    int size;
    //Derby
    Connection conn = null;
    String query = null;
    PreparedStatement st = null;
    //Mongo
    Mongo m;
    DB db;
    DBCollection coll;
    DBCollection coll2;
    // objects to be saved to db
    List<DBObject> documents = new LinkedList<DBObject>();
    List<PreparedStatement> statements = new LinkedList<PreparedStatement>();
    DBObject document = null;

    @Override
    protected void setUp() {
        setUpDocuments();
        setUpMongo();
        setUpDerby();
    }

    @Override
    protected void tearDown() {
        coll.drop();
        PreparedStatement st;
        try {
            st = conn.prepareStatement("DELETE FROM test1");
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CaliperDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void timeMongo(int reps) {
        for (int i = 0; i < reps; i++) {
            for (int index = 0; index <= size; index++) {
                coll.insert(documents.get(index));
            }
        }
    }
    public void timeMongoWithSubList(int reps) {
        for (int i = 0; i < reps; i++) {
            coll2.insert(documents.subList(0, size));
        }
    }

    public void timeDerby(int reps) {
        for (int i = 0; i < reps; i++) {
            try {
                for (int index = 0; index <= size; index++) {
                    int result = statements.get(index).executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CaliperDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setUpMongo() {
        try {
            m = new Mongo("192.168.219.129", 27017);
            db = m.getDB("test");
            coll = db.getCollection("test1");
            coll2 = db.getCollection("test2");
        } catch (UnknownHostException ex) {
            Logger.getLogger(CaliperDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(CaliperDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setUpDerby() {
        try {
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/testing", "test", "test");
            query = "INSERT INTO test1 (occurrenceTime,detectionTime,hostname,type,"
                    + "application,process,processId,severity,priority,value)VALUES(?,?,?,?,?,?,?,?,?,?)";
            for (int index = 0; index <= size; index++) {
                DBObject event = (DBObject) documents.get(index).get("Event");
                st = conn.prepareStatement(query);
                st.setString(1, (String) event.get("occurrenceTime"));
                st.setString(2, (String) event.get("detectionTime"));
                st.setString(3, (String) event.get("hostname"));
                st.setString(4, (String) event.get("type"));
                st.setString(5, (String) event.get("application"));
                st.setString(6, (String) event.get("process"));
                st.setString(7, (String) event.get("processId"));
                st.setInt(8, Integer.valueOf(event.get("severity").toString()));
                st.setInt(9, Integer.valueOf(event.get("priority").toString()));
                st.setString(10, (String) event.get("http://collectd.org/5.1/events.jsch").toString());
                
                statements.add(st);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CaliperDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setUpDocuments() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("src/main/resources/events.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                DBObject object = ((DBObject) JSON.parse(line));
                documents.add(object);
            }
            // PICK UP FIRST DOCUMENT
            document = documents.get(1);
        } catch (IOException ex) {
            Logger.getLogger(CaliperDB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(CaliperDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Runner.main(CaliperDB.class, args);
    }
}
