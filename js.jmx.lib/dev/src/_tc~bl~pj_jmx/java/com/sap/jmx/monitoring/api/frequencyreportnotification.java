package com.sap.jmx.monitoring.api;

/**
 * The <code>FrequencyReportNotification</code> class represents a notification 
 * emitted by a <code>FrequencyResourceMBean</code>. 
 */
public class FrequencyReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public FrequencyReportNotification(Object source)
  {
    super(source);
  }
}
