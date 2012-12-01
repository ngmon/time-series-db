package org.monitoring.szt.model;


import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Raw event intended for CEP processing.
 *
 * @author Ondrej Kotek <ondrej.kotek@mycroftmind.com>
 */
@Entity("rawevent")
public class RawEvent implements Serializable {

    
    @Id
    //@GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    public Long getSimulationTimestamp() {
        return simulationTimestamp;
    }

    public void setSimulationTimestamp(Long simulationTimestamp) {
        this.simulationTimestamp = simulationTimestamp;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
    
    public void addValue(String value){
        this.values.add(value);
    }



    public void setEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    //@Version
    private int version;

    /**
     * Identifier of the simulation test this event comes from.
     */
    
    private Long simulationId;

    /**
     * Type of the event source this event comes from.
     */
    //@Enumerated(EnumType.STRING)
    
    private SourceType sourceType;

    /**
     * The event source this event comes from.
     */
    
    private String source;

    //@Enumerated(EnumType.STRING)
    
    private MeasurementType measurementType;
    
    private Date occurrenceTimestamp;

    
    private Long simulationTimestamp;




    private List<String> values = new LinkedList<String>();

    /**
     * Event timestamp for CEP engine. It should be almost equals to occurrenceTimestamp.
     */
    private Long eventTimestamp;


    public void setOccurrenceTimestamp(Date occurrenceTimestamp) {
        if (occurrenceTimestamp == null) {
            throw new IllegalArgumentException("Occurence timestamp cannot be null!");
        }
        this.occurrenceTimestamp = (Date) occurrenceTimestamp.clone();
    }
    
    public Date getOccurrenceTimestamp() {
        return occurrenceTimestamp;
    }


    public long retrieveEventTimestamp() {
        this.fillEventTimestamp();
        return this.eventTimestamp;
    }


    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public long getEventTimestamp() {
        this.fillEventTimestamp();
        return this.eventTimestamp;
    }

//    public int getNumberOfValues() {
//        return this.measurementType.getNumberOfFields();
//    }
//
//    public Class<?> getValueType(final int index) {
//        return this.measurementType.getValueTypes().get(index);
//    }

    public String getValueAsString(final int index) {
        if (index < this.values.size()) {
            return this.values.get(index);
        } else {
            return "";
        }
    }

    public Integer getValueAsInteger(final int index) {
        final String value = this.values.get(index);
        if ("null".equals(value)) {
            return null;
        } else {
            try {
                return Integer.parseInt(this.values.get(index));
            } catch (NumberFormatException e) {
                throw new IllegalAccessError("Cannot convert value with index " + index
                        + " to integer type (value is '" + this.values.get(index) + "')");
            }
        }
    }

    public Long getValueAsLong(final int index) {
        final String value = this.values.get(index);
        if ("null".equals(value)) {
            return null;
        } else {
            try {
                return Long.parseLong(this.values.get(index));
            } catch (NumberFormatException e) {
                throw new IllegalAccessError("Cannot convert value with index " + index
                        + " to long type (value is '" + this.values.get(index) + "')");
            }
        }
    }

    public Double getValueAsDouble(final int index) {
        final String value = this.values.get(index);
        if ("null".equals(value)) {
            return null;
        } else {
            try {
                return Double.parseDouble(this.values.get(index));
            } catch (NumberFormatException e) {
                throw new IllegalAccessError("Cannot convert value with index " + index
                        + " to integer type (value is '" + this.values.get(index) + "')");
            }
        }
    }

    private void fillEventTimestamp() {
        if (null == this.eventTimestamp) {
            this.eventTimestamp = this.occurrenceTimestamp.getTime();
        }
    }

    /**
     * Returns a string representation.
     * @return String representation.
     */
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        StringBuilder sb = new StringBuilder();
        sb.append("RawEvent");
        sb.append("\n  [");
        sb.append("id = ");
        sb.append(this.id);
        sb.append(", concId = ");
        sb.append(this.getValueAsString(0));
        sb.append(", measurementType = ");
        sb.append(this.measurementType);
        sb.append(", messageType = ");
        sb.append(this.getValueAsString(1));
        sb.append(", eventTimestamp = ");
        sb.append(sdf.format(new Date(this.getEventTimestamp())));
        sb.append("\n  simulationId = ");
        sb.append(this.simulationId);
        sb.append(", sourceType = ");
        sb.append(this.sourceType);
        sb.append(", source = ");
        sb.append(this.source);
        sb.append(", occurrenceTimestamp = ");
        sb.append(sdf.format(this.occurrenceTimestamp));
        sb.append(", simulationTimestamp = ");
        sb.append(this.simulationTimestamp);
        sb.append(", values = ");
        sb.append(this.values);
        sb.append("]");
        return sb.toString();
    }
}
