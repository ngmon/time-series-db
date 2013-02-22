package org.monitoring.szt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public class PostgreSQLDatabase implements Database {

    private String raweventTable = "rawevent";
    private String raweventTableValues = "rawevent_values";
    Connection conn = null;
    List<RawEvent> events = new LinkedList<RawEvent>();
    PostgreSQLDatabaseMapper mapper;

    public PostgreSQLDatabase() {
        try {
            Class.forName("org.postgresql.Driver");                     //postgres originally
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sztmonitoring", "postgres", "root");
            mapper = new PostgreSQLDatabaseMapper();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public PostgreSQLDatabase(String rawEventTable, String rawEventTableValues) {
        this();
        this.raweventTable = rawEventTable;
        this.raweventTableValues = rawEventTableValues;
    }

    public List<RawEvent> getAllEventsDefaultOrder() {
        try {
            String query = "SELECT id, sourcetype, simulationid, simulationtimestamp, source,"
                    + "measurementtype, occurrencetimestamp, version, array_to_string(array_agg(valuelist ORDER BY values_order), ',') as list "
                    + "FROM " + raweventTable + " JOIN " + raweventTableValues
                    + " ON " + raweventTable + ".id = " + raweventTableValues + ".rawevent_id "
                    + "GROUP BY id, sourcetype, source, measurementtype, occurrencetimestamp ";

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

            String query = "SELECT id, sourcetype, simulationid, simulationtimestamp, source,"
                    + "measurementtype, occurrencetimestamp, version, array_to_string(array_agg(valuelist ORDER BY values_order), ',') as list "
                    + "FROM " + raweventTable + " JOIN " + raweventTableValues
                    + " ON " + raweventTable + ".id = " + raweventTableValues + ".rawevent_id "
                    + "GROUP BY id, sourcetype, source, measurementtype, occurrencetimestamp "
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
                    + "measurementtype, occurrencetimestamp, version, array_to_string(array_agg(valuelist ORDER BY values_order), ',') as list "
                    + "FROM " + raweventTable + " JOIN " + raweventTableValues
                    + " ON " + raweventTable + ".id = " + raweventTableValues + ".rawevent_id "
                    + "WHERE simulationid = ? "
                    + "AND occurrencetimestamp >= ? "
                    + "AND occurrencetimestamp < ? "
                    + "GROUP BY id, sourcetype, source, measurementtype, occurrencetimestamp "
                    + "ORDER BY occurrencetimestamp ASC";

            PreparedStatement st = conn.prepareStatement(query);
            st.setLong(1, simulationId);
            st.setTimestamp(2, from);
            st.setTimestamp(3, to);
            //System.out.println(st.toString());
            ResultSet result = st.executeQuery();
            return mapper.getResult(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<RawEvent> getAllEventsFromSimulation(Long simulationId) {
        try {
            String query = "SELECT id, sourcetype, simulationid, simulationtimestamp, source,"
                    + "measurementtype, occurrencetimestamp, version, array_to_string(array_agg(valuelist ORDER BY values_order), ',') as list "
                    + "FROM " + raweventTable + " JOIN " + raweventTableValues
                    + " ON " + raweventTable + ".id = " + raweventTableValues + ".rawevent_id "
                    + "WHERE event.simulationId = ? "
                    + "GROUP BY id, sourcetype, source, measurementtype, occurrencetimestamp "
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
                    "SELECT id, sourcetype, simulationid, simulationtimestamp, source,"
                    + "measurementtype, occurrencetimestamp, version, array_to_string(array_agg(valuelist ORDER BY values_order), ',') as list "
                    + "FROM " + raweventTable + " JOIN " + raweventTableValues
                    + " ON " + raweventTable + ".id = " + raweventTableValues + ".rawevent_id "
                    + "WHERE event.simulationId = ? AND event.source = ? "
                    //                    + "AND (event.sourceType = 'RRD_CPU' "
                    //                    + "OR event.sourceType = 'RRD_MEMORY' "
                    //                    + "OR event.sourceType = 'RRD_NETWORK') "
                    + "GROUP BY id, sourcetype, source, measurementtype, occurrencetimestamp "
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

    public void deleteByVersion(int num) {
        PreparedStatement drop;
        try {
            drop = conn.prepareStatement("DELETE FROM " + raweventTable + " WHERE version = ?");
            drop.setInt(1, num);
            drop.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void insert(RawEvent event) {
        try {
            String query = "INSERT INTO " + raweventTable + "(id, measurementtype, occurrencetimestamp, simulationid,"
                    + " simulationtimestamp, source, sourcetype, version) VALUES(?,?,?,?,?,?,?,?)";
            PreparedStatement st;
            st = conn.prepareStatement(query);
            mapper.set(st, event);
            for (int index = 0; index < event.getValues().size(); index++) {
                query = "INSERT INTO " + raweventTableValues + "(rawevent_id, valuelist, values_order) VALUES(?,?,?)";
                st = conn.prepareStatement(query);
                mapper.setValue(st, event, index);
            }
            st.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void save(RawEvent event) {
        try {
            conn.setAutoCommit(false);
            insert(event);
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void save(List<RawEvent> list) {
        try {
            conn.setAutoCommit(false);
            for (RawEvent event : list) {
                insert(event);
            }
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
