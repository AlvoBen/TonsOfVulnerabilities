package com.sap.jmx.monitoring.api;

/**
 * The <code>TextReportNotification</code> class represents a notification 
 * emitted by a <code>TextResourceMBean</code>. 
 */
public class TextReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public TextReportNotification(Object source)
  {
    super(source);
  }
}
