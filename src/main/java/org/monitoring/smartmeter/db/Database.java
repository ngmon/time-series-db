package org.monitoring.smartmeter.db;

import java.util.List;
import org.monitoring.smartmeter.model.MeterEvent;

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
