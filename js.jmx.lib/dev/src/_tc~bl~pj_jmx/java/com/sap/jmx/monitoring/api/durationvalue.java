/*
 * Created on 15.09.2003
 */
package com.sap.jmx.monitoring.api;

/**
 * @author d024817
 */
public class DurationValue
{
  private long totalNumber;
  private long totalTime;
  
  /**
   * Constructs a new <code>DurationValue</code> object.
   * @param totalNumber the total number since startup.
   * @param totalTime the total time since startup.
   */
  public DurationValue(
    final long totalNumber, 
    final long totalTime) 
  {
    this.totalNumber = totalNumber;
    this.totalTime = totalTime;
  }
  
  /**
   * Returns the total number since startup.
   * @return the total number since startup.
   */
  public long getTotalNumber()
  {
    return totalNumber;
  }

  /**
   * Returns the total time since startup.
   * @return the total time since startup.
   */
  public long getTotalTime()
  {
    return totalTime;
  }
}
