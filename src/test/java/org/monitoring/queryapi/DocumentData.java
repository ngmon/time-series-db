package org.monitoring.queryapi;

import com.google.code.morphia.annotations.Embedded;

/**
 *
 * @author Michal Dubravcik
 */
@Embedded
public class DocumentData {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}