package org.monitoring.queryapi;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import java.util.Date;

/**
 *
 * @author Michal Dubravcik
 */
public class CachePointMapper {

    public CachePoint fromDB(DBObject dbobject) {
        CachePoint cpoint = new CachePoint();
        cpoint.setDate((Date) ((DBObject) dbobject.get("_id")).get("t"));
        cpoint.setMatch((String) ((DBObject)dbobject.get("_id")).get("match"));
        cpoint.setGroupTime((Integer) ((DBObject)dbobject.get("_id")).get("groupTime"));
        cpoint.setFlag(((Integer) dbobject.get("f"))==1?CachePoint.Flag.END:CachePoint.Flag.START);
        return cpoint;
    }

    public DBObject toDB(CachePoint cachePoint) {
        return BasicDBObjectBuilder.start().push("_id")
                .append("t", cachePoint.getDate())
                .append("match", cachePoint.getMatch()).append("groupTime", cachePoint.getGroupTime())
                .pop().append("f", cachePoint.getFlag().get()).get();
    }
}
