package org.monitoring.smartmeter.db;

import org.monitoring.szt.db.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.monitoring.smartmeter.model.MeterEvent;
import org.monitoring.szt.model.MeasurementType;
import org.monitoring.szt.model.RawEvent;
import org.monitoring.szt.model.SourceType;

/**
 *
 * @author Michal
 */
public class PostgreSQLDatabaseMapper {

    public List<MeterEvent> getResult(ResultSet rs) {
        List<MeterEvent> result = new LinkedList<MeterEvent>();
        try {
            while (rs.next()) {
                MeterEvent event = new MeterEvent();
                event.setId(rs.getInt("id"));
                event.setDate(rs.getDate("date"));
                event.setSource(rs.getString("source"));
                event.setValue(rs.getDouble("value"));
                result.add(event);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    void set(PreparedStatement st, MeterEvent event) {
        try {
            st.setInt(1, event.getId());
            st.setTimestamp(2, new java.sql.Timestamp(event.getDate().getTime()));
            st.setString(3, event.getSource());
            st.setDouble(4, event.getValue());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
