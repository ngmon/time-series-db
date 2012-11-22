/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.smartmeter.db;

import java.util.List;
import org.monitoring.smartmeter.MeterEvent;

/**
 *
 * @author Michal
 */
public interface Database {
                   
    public List<MeterEvent> getAllEvents();
       
    public void save(MeterEvent event);
    
    public void saveAll(List<MeterEvent> list);
    
    public List<MeterEvent> getAggregate();
    
}