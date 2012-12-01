package org.monitoring.szt.db;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.monitoring.szt.model.MeasurementType;
import org.monitoring.szt.model.RawEvent;
import org.monitoring.szt.model.SourceType;

/**
 *
 * @author Michal
 */
public class PostgreSQLDatabaseMapper {

    public List<RawEvent> getResult(ResultSet rs) {
        List<RawEvent> result = new LinkedList<RawEvent>();
        try {
            while (rs.next()) {
                RawEvent event = new RawEvent();
                event.setId(rs.getLong("id"));
                event.setMeasurementType(MeasurementType.valueOf(rs.getString("measurementType")));
                event.setOccurrenceTimestamp(rs.getTimestamp("occurrenceTimestamp"));
                event.setSimulationId(rs.getLong("simulationId"));
                event.setSimulationTimestamp(rs.getLong("SimulationTimestamp"));
                event.setSource(rs.getString("source"));
                event.setSourceType(SourceType.valueOf(rs.getString("sourceType")));
                event.setVersion(rs.getInt("version"));
                List<String> list = Arrays.asList(rs.getString("list").split(","));
                event.setValues(list);
                result.add(event);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void set(PreparedStatement st, RawEvent event) {
        try {
            st.setLong(1, event.getId());
            st.setString(2, event.getMeasurementType().toString());
            st.setTimestamp(3, new Timestamp(event.getOccurrenceTimestamp().getTime()));
            st.setLong(4, event.getSimulationId());
            st.setLong(5, event.getSimulationTimestamp());
            st.setString(6,event.getSource());
            st.setString(7, event.getSourceType().toString());
            st.setInt(8, event.getVersion());
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setValue(PreparedStatement st, RawEvent event, int index) {
        try {
            st.setLong(1, event.getId());
            st.setString(2, event.getValues().get(index).toString());
            st.setInt(3, index);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
