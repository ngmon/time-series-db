/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.szt.db;

import java.sql.Timestamp;
import java.util.List;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal
 */
public interface Database {
    
    public List<RawEvent> getAllEventsDefaultOrder();
               
    public List<RawEvent> getAllEvents();
    
    public List<RawEvent> getEventsInTimeRange(Long simulationId, Timestamp from, Timestamp  to);
    
    public List<RawEvent> getAllEventsFromSimulation(Long simulationId);
    
    public List<RawEvent> getRRDFromSimulationAndSource(String source, Long simulationId);
    
    public void save(List<RawEvent> list);
    
}
