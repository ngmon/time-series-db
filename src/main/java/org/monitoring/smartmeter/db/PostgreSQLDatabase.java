package org.monitoring.smartmeter.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.smartmeter.model.MeterEvent;

/**
 *
 * @author Michal
 */
public class PostgreSQLDatabase implements Database {

    Connection conn = null;
    List<MeterEvent> events = new LinkedList<MeterEvent>();
    PostgreSQLDatabaseMapper mapper;

    public PostgreSQLDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "root");
            mapper = new PostgreSQLDatabaseMapper();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void create(){
        try {
            String query = "CREATE TABLE meterevent (  id integer NOT NULL,"
                    + " edate timestamp without time zone,   esource character varying(25),"
                    + "  evalue double precision,  CONSTRAINT primary_key PRIMARY KEY (id) );"
                    + " ALTER TABLE meterevent   OWNER TO postgres; CREATE INDEX meterevent_edate_idx"
                    + "  ON meterevent  USING btree  (edate);";
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet result = st.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<MeterEvent> getAllEvents() {
        try {
            String query = "SELECT * "
                    + "FROM MeterEvent event "
                    + "ORDER BY event.time";
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet result = st.executeQuery();
            return mapper.getResult(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void save(List<MeterEvent> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void save(MeterEvent event) {
        try {
            String query = "INSERT INTO MeterEvent VALUES (?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(query);
            mapper.set(st, event);
            int result = st.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void saveAll(List<MeterEvent> list) {
        for (MeterEvent event : list) {
            save(event);
        }
    }

    public List<MeterEvent> getAggregate() {
        try {
            String query = "SELECT esource , sum(evalue) as sum "
                    + "FROM MeterEvent event"
                    + "GROUP BY event.edate";
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet result = st.executeQuery();
            while(result.next()) {
                //TODO
                result.getDate("date");
                result.getDouble("sum");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
