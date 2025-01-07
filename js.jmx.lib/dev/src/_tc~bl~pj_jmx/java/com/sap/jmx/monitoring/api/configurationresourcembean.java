package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report a list of configuration parameters described
 * by their name and value. There is no further monitoring logic associated
 * with this type of resource mbean.
 */
public interface ConfigurationResourceMBean extends ResourceMBean
{
  /**
   * Returns the configuration of a resource.
   * If the configuration list is null the reporting step will be skipped.
   * @return the configuration of a resource.
   */
  public ConfigurationList getConfigurationParameters();
}
