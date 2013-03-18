package org.monitoring.queryapi;

import com.google.code.morphia.annotations.Embedded;

/**
 *
 * @author Michal Dubravcik
 */
@Embedded
public class DocumentData {

    private int value;
    private int source;
    private int part;
    
    

    public int getValue() {
        return value;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public void setValue(int value) {
        this.value = value;
    }
}