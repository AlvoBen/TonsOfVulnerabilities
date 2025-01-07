package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report a number of events. The associated
 * monitoring logic calculates a frequency from the number of events based on
 * one minute intervals. The frequency is compared against thresholds.
 */
public interface FrequencyResourceMBean extends MetricResourceMBean
{
  /**
   * Returns the number of events that occured since the last call of this method.
   * @return the number of events that occured since the last call of this method.
   */
  public int getNumberOfEvents();
}
