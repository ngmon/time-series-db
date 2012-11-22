package org.monitoring.szt;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;


/**
 * @author Martin Svehla <martin.svehla@mycroftmind.com>
 */

public enum MeasurementType {

    TEST_MEASUREMENT(0, new Class<?>[]{}),

    SIMULATION_STARTED(1, new Class<?>[]{Long.class}),
    SIMULATION_FINISHED(1, new Class<?>[]{Long.class}),

    // nodeId
    SIMULATOR_NODE_STARTED(1, new Class<?>[]{String.class}),
    SIMULATOR_NODE_SHUTDOWN(0, new Class<?>[]{}),
    // numberOfConcentrators, D1count, D3count, D4count
    SIMULATOR_SEND_STEP_STARTED(4, new Class<?>[]{
        Integer.class, Integer.class, Integer.class, Integer.class}),
    // numberOfConcentrators
    SIMULATOR_OPEN_CONNECTION_STEP_STARTED(1, new Class<?>[]{Integer.class}),
    // stepIndex, totalNumberOfSteps
    SIMULATOR_STEP_FINISHED(2, new Class<?>[]{Integer.class, Integer.class}),
    // concentratorId, messageType, targetAddress, targetPort, size, D1count, D3count
    SIMULATOR_SEND_DATA(5, new Class<?>[]{
        String.class, String.class, String.class, Integer.class, Integer.class,
        Integer.class, Integer.class}),
    // concentratorId, messageType, targetAddress, targetPort, size
    SIMULATOR_RECEIVED_DATA(5, new Class<?>[]{
        String.class, String.class, String.class, Integer.class, Integer.class}),
    // concentratorId
    SIMULATOR_SENDING_FAILED(1, new Class<?>[]{String.class}),

    RAW_ELIN_LOG(1, new Class<?>[]{String.class}),

    ///// RRD data
    CPU_LOAD(1, new Class<?>[]{Double.class}),
    MEMORY_LOAD(1, new Class<?>[]{Double.class}),
    NETWORK_0_UPLOAD(1, new Class<?>[]{Double.class}),
    NETWORK_0_DOWNLOAD(1, new Class<?>[]{Double.class}),
    NETWORK_0_ERRORS(1, new Class<?>[]{Double.class}),

    ///// value tresholds
    CPU_LOAD_THRESHOLD(1, new Class<?>[]{Double.class}),
    MEMORY_LOAD_THRESHOLD(1, new Class<?>[]{Double.class}),
    NETWORK_0_UPLOAD_THRESHOLD(1, new Class<?>[]{Double.class}),
    NETWORK_0_DOWNLOAD_THRESHOLD(1, new Class<?>[]{Double.class}),
    NETWORK_0_ERRORS_THRESHOLD(1, new Class<?>[]{Double.class}),

    ///// Monitors
    HOST_UNREACHABLE(1, new Class<?>[]{String.class}),
    URL_UNREACHABLE(1, new Class<?>[]{String.class});

    private int numberOfFields;

    private List<Class<?>> valueTypes;

    private MeasurementType(final int numberOfFields, final Class<?>[] types) {
        this.numberOfFields = numberOfFields;
        this.valueTypes = Collections.unmodifiableList(Arrays.asList(types));
    }
    
  
}
