package com.sap.jmx.monitoring.api;

/**
 * The <code>ConfigurationReportNotification</code> class represents a notification 
 * emitted by a <code>ConfigurationResourceMBean</code>. 
 */
public class ConfigurationReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public ConfigurationReportNotification(Object source)
  {
    super(source);
  }
}
