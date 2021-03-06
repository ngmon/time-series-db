package org.monitoring.szt.model;


/**
 * Type of sources for the SZT measurement events.
 *
 * @author Martin Svehla <martin.svehla@mycroftmind.com>
 */
public enum SourceType {

    DATA_COLLECTOR,
    ELIN_DISPATCH_LOG,
    ELIN_IO_LOG,
    SIMULATOR_LOG,
    HES_LOG,
    MDMS,
    SERVICE_MONITOR,
    INFRASTRUCTURE_STATISTICS,
    RRD_CPU,
    RRD_MEMORY,
    RRD_NETWORK,
    RRD_DISK,
    ORACLE;

}
