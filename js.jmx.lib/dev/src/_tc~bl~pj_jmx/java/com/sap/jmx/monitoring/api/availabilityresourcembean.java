package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report the availabilty of a resource. The associated
 * monitoring logic collects all reported availability values within a quarter of an hour
 * and calculates an availability in percent based on that time period. The percentage 
 * is compared against thresholds. Furthermore an alert is generated if the application
 * reports no value during a certain time slice. The size of this time slice is defined by 
 * the application and the application has to guarantee that it will always report an
 * availability value within this time as long as it is available. Do not use an
 * <code>AvailabilityResourceMBean</code> if the application can't gurantee the 
 * necessary periodical reporting.
 */
public interface AvailabilityResourceMBean extends MetricResourceMBean
{
  /**
   * Returns whether the resource is available or not.
   * @return whether the resource is available or not.
   */
	public boolean isAvailable();
}
