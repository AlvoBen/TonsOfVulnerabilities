package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report a state represented by a textual 
 * description and an alert color. There is no further monitoring logic 
 * associated with this type of resource mbean.
 */
public interface StateResourceMBean extends ResourceMBean
{
  /**
   * Returns a state value.
   * If the state value is null the reporting step will be skipped.
   * @return a state value.
   */
  public StateValue getState();
}
