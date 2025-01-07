﻿package com.sap.bc.cts.tp.log;

/**
 * A factory for <code>Trace</code> objects.
 * 
 * @author Java Change Management May 14, 2004
 */
public interface TraceFactory {
  /**
   * Indicates whether tracing is turned on for the specified 
   * <code>Class</code>.
   * 
   * @param forClass the specified <code>Class</code>
   * @return <code>true</code> if tracing is turned on for <code>forClass</code>;
   *          <code>false</code> otherwise
   */
  public boolean isTracingTurnedOn(Class forClass);
  
  /**
   * Returns a <code>Trace</code> for the specified <code>Class</code>.
   * 
   * @param forClass the specified <code>Class</code>
   * @return a <code>Trace</code> for the specified <code>Class</code>
   */
  public Trace getTrace(Class forClass);
}
