package org.monitoring.queryapisql;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.monitoring.queryapi.Event;
import org.monitoring.queryapi.preaggregation.Preaggregate;
import org.monitoring.queryapisql.preaggregation.PostgreSQLDatabase;
/**
 *
 * @author Michal Dubravcik
 */
public class PreaggregateSQLAggFunc implements Preaggregate{
    String colName = "aggregate";
    PostgreSQLDatabase postgre = new PostgreSQLDatabase();

    public PreaggregateSQLAggFunc() {
    }
    
    public PreaggregateSQLAggFunc(String colName) {
        this.colName = colName;
    }
    
    public void saveEvent(TimeUnit unit, int[][] times, Event event) {
        Long fieldTime;
        int i = 0;
        postgre.save(event);

        while (i < times.length) {
            int timeActual = times[i][0];
            int timeNext = times[i][1];
            int rangeLeft = 0;
            if (times[i].length > 2) {
                rangeLeft = times[i][2];
            }
            int rangeRight = 0;
            if (times[i].length > 3) {
                rangeRight = times[i][3];
            }
            i++;

            String table = colName + timeActual;
            for (int k = -rangeLeft; k <= rangeRight; k++) {
                Date start = new Date(event.getDate().getTime()
                        - event.getDate().getTime() % unit.toMillis(timeActual)
                        + unit.toMillis(timeActual * (k - rangeLeft)));
                Date middle = new Date(start.getTime()
                        + unit.toMillis(timeActual * (rangeLeft)));
                Date end = new Date(middle.getTime()
                        + unit.toMillis(timeActual * (rangeRight + 1)));
                

                long eventDate =  middle.getTime() - middle.getTime() % unit.toMillis(timeNext);

                fieldTime = middle.getTime() % unit.toMillis(timeNext) / unit.toMillis(timeActual);
                String fieldTimeString = fieldTime.toString();

                postgre.updateAggregateWithAggFunc(table, start, end, new Date(eventDate), fieldTimeString, event);
            }
        }
    }
}

