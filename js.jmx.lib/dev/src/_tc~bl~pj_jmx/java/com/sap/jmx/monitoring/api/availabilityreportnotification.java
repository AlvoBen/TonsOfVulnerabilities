package com.sap.jmx.monitoring.api;

/**
 * The <code>AvailabilityReportNotification</code> class represents a notification 
 * emitted by an <code>AvailabilityResourceMBean</code>. 
 */
public class AvailabilityReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public AvailabilityReportNotification(Object source)
  {
    super(source);
  }
}
