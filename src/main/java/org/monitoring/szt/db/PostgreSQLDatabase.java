/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.RawEvent;

/**
 *
 * @author Michal
 */
public class PostgreSQLDatabase implements Database {

    Connection conn = null;
    List<RawEvent> events = new LinkedList<RawEvent>();
    PostgreSQLDatabaseMapper mapper;

    public PostgreSQLDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "root");
            mapper = new PostgreSQLDatabaseMapper(conn);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public List<RawEvent> getAllEventsDefaultOrder() {
        try {
            String query = "SELECT * "
                    + "FROM RawEvent even";
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet result = st.executeQuery();
            return mapper.getResult(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<RawEvent> getAllEvents() {
        try {
            
            String query = "SELECT * "
                    + "FROM RawEvent event "
                    + "ORDER BY event.occurrenceTimestamp";
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet result = st.executeQuery();
            return mapper.getResult(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<RawEvent> getEventsInTimeRange(Long simulationId, Timestamp from, Timestamp to) {
        try {
            String query = "SELECT id, sourcetype, simulationid, simulationtimestamp, source," 
            + "measurementtype, occurrencetimestamp, version, array_to_string(array_agg(valuelist), ',') as list "
            + "FROM rawevent LEFT JOIN rawevent_values " //ORDER BY missing
            + "ON rawevent.id = rawevent_values.rawevent_id "
            + "WHERE simulationid = ? "
            + "AND occurrencetimestamp >= ? "
            + "AND occurrencetimestamp <= ? "
            + "GROUP BY id, sourcetype, source, measurementtype, occurrencetimestamp "
            + "ORDER BY occurrencetimestamp ASC";
            
            PreparedStatement st = conn.prepareStatement(query);
            st.setLong(1, simulationId);
            st.setTimestamp(2, from);
            st.setTimestamp(3, to);
            ResultSet result = st.executeQuery();
            return mapper.getResult(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<RawEvent> getAllEventsFromSimulation(Long simulationId) {
        try {
            String query = "SELECT * "
                    + "FROM RawEvent event "
                    + "WHERE event.simulationId = ? "
                    + "ORDER BY event.occurrenceTimestamp";
            PreparedStatement st = conn.prepareStatement(query);
            st.setLong(1, simulationId);
            ResultSet result = st.executeQuery();
            return mapper.getResult(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<RawEvent> getRRDFromSimulationAndSource(String source, Long simulationId) {
        try {
            String query =
                    "SELECT * "
                    + "FROM RawEvent event "
                    + "WHERE event.simulationId = ? AND event.source = ? "
//                    + "AND (event.sourceType = 'RRD_CPU' "
//                    + "OR event.sourceType = 'RRD_MEMORY' "
//                    + "OR event.sourceType = 'RRD_NETWORK') "
                    + "ORDER BY event.measurementType, event.occurrenceTimestamp";
            PreparedStatement st = conn.prepareStatement(query);
            st.setLong(1, simulationId);
            st.setString(2, source);
            ResultSet result = st.executeQuery();
            return mapper.getResult(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void save(List<RawEvent> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
