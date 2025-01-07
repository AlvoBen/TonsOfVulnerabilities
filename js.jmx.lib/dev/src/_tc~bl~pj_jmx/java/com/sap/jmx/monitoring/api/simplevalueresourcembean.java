package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report a single metric value. The value is
 * compared against thresholds.
 */
public interface SimpleValueResourceMBean extends MetricResourceMBean
{
  /**
   * Returns a single metric value.
   * @return a single metric value.
   */
  public int getValue();
}
