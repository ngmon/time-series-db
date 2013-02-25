package org.monitoring.queryapi;

import java.util.Date;

/**
 *
 * @author Michal Dubravcik
 */
public class CachePoint {

    public enum Flag {

        START(0),
        END(1),
        NONE(3);
        int flag;

        private Flag(int flag) {
            this.flag = flag;
        }

        public int get() {
            return flag;
        }
    }
    
    private Date date;
    private String operation;
    private String match;
    private CachePoint.Flag flag;
    private int groupTime;

    public CachePoint() {
    }

    public CachePoint(Date date, String operation, String match, CachePoint.Flag flag, int groupTime) {
        this.date = date;
        this.operation = operation;
        this.match = match;
        this.flag = flag;
        this.groupTime = groupTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public CachePoint.Flag getFlag() {
        return flag;
    }

    public void setFlag(CachePoint.Flag flag) {
        this.flag = flag;
    }

    public int getGroupTime() {
        return groupTime;
    }

    public void setGroupTime(int groupTime) {
        this.groupTime = groupTime;
    }
}
