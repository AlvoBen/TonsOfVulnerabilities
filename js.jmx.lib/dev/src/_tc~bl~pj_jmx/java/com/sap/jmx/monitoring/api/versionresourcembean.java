package com.sap.jmx.monitoring.api;

/**
 * This interface is used to report a version value. There is no further monitoring logic 
 * associated with this type of resource mbean.
 */
public interface VersionResourceMBean extends ResourceMBean
{
  /**
   * Returns the version info.
   * If the version info is null the reporting step will be skipped.
   * @return the version info.
   */
  public VersionInfo getVersion();
}
