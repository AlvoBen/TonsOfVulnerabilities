package com.sap.jmx.monitoring.api;

/**
 * The <code>SimpleValueReportNotification</code> class represents a notification 
 * emitted by a <code>SimpleValueResourceMBean</code>. 
 */
public class SimpleValueReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public SimpleValueReportNotification(Object source)
  {
    super(source);
  }
}
