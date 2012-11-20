/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt.db;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.monitoring.szt.MeasurementType;
import org.monitoring.szt.RawEvent;
import org.monitoring.szt.SourceType;

/**
 *
 * @author Michal
 */
public class PostgreSQLDatabaseMapper {
    
    Connection conn;
    
    public PostgreSQLDatabaseMapper(Connection conn) {
        this.conn = conn;
        
    }
    
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
                //List<String> list = Lists.newArrayList(Splitter.on(",").split(rs.getString("list")));
                event.setValues(list);

                //get values from second table
//                String value_query =
//                        "SELECT * "
//                        + "FROM RawEvent_Values  "
//                        + "WHERE rawevent_id = ?";
//                PreparedStatement value_st = conn.prepareStatement(value_query);
//                value_st.setLong(1, event.getId());
//                ResultSet value_rs = value_st.executeQuery();
//                while (value_rs.next()) {
//                    event.addValue(value_rs.getString("valuelist"));
//                }
                result.add(event);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
}
