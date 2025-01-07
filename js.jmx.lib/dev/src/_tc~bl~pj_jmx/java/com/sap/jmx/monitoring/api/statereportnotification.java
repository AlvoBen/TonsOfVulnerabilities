package com.sap.jmx.monitoring.api;

/**
 * The <code>StateReportNotification</code> class represents a notification 
 * emitted by a <code>StateResourceMBean</code>. 
 */
public class StateReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public StateReportNotification(Object source) 
  {
    super(source);
  }
}
