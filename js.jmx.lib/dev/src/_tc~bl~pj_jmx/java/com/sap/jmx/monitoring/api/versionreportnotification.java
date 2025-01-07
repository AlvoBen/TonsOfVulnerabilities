package com.sap.jmx.monitoring.api;

/**
 * The <code>VersionReportNotification</code> class represents a notification 
 * emitted by a <code>VersionResourceMBean</code>. 
 */
public class VersionReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public VersionReportNotification(Object source)
  {
    super(source);
  }
}
