/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.db;

import com.mongodb.DBObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Michal
 */
public class PostgreSQLDatabase implements Database {

    Connection conn = null;
    String query = null;
    PreparedStatement st = null;

    public PostgreSQLDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "postgres", "root");
            query = "INSERT INTO test1 (occurrenceTime,detectionTime,hostname,type,"
                    + "application,process,processId,severity,priority,value)VALUES(?,?,?,?,?,?,?,?,?,?)";

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }

    public void saveDocument(DBObject object) {
        try {
            st = conn.prepareStatement(query);
            st.setString(1, (String) object.get("occurrenceTime"));
            st.setString(2, (String) object.get("detectionTime"));
            st.setString(3, (String) object.get("hostname"));
            st.setString(4, (String) object.get("type"));
            st.setString(5, (String) object.get("application"));
            st.setString(6, (String) object.get("process"));
            st.setString(7, (String) object.get("processId"));
            st.setInt(8, Integer.valueOf(object.get("severity").toString()));
            st.setInt(9, Integer.valueOf(object.get("priority").toString()));
            st.setString(10,  object.get("http://collectd_org/5_1/events_jsch").toString());

            int out = st.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void tearDown() {
        PreparedStatement drop;
        try {
            drop = conn.prepareStatement("DELETE FROM test1");
            drop.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void saveDocuments(List<DBObject> documents) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
