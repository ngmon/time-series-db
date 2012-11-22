/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.smartmeter;

import java.util.Date;

/**
 *
 * @author Michal
 */
public class MeterEvent {
    
    private int id;
    private String source;
    private Date date;
    private Double value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
    
}
