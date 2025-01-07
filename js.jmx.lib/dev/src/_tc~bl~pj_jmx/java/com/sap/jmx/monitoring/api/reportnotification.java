package com.sap.jmx.monitoring.api;

import javax.management.Notification;

/**
 * The <code>ReportNotification</code> class represents a notification 
 * emitted by a resource mbean. 
 */
public abstract class ReportNotification extends Notification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public ReportNotification(Object source)
  {
    super("sap.ccms.monitor.report", source, 0);
  }
}
