package com.sap.jmx.monitoring.api;

/**
 * The <code>QualityRateReportNotification</code> class represents a notification 
 * emitted by a <code>QualityRateResourceMBean</code>. 
 */
public class QualityRateReportNotification extends ReportNotification
{
  /**
   * @see java.util.EventObject#EventObject(Object)
   */
  public QualityRateReportNotification(Object source)
  {
    super(source);
  }
}
