package com.sap.engine.services.httpserver.chain;

import com.sap.engine.services.httpserver.interfaces.properties.HostProperties;

/**
 * Host scope gives access to all virtual host related settings
 */
public interface HostScope extends Scope {
  
  /**
   * Gets the virtual host name
   * 
   * @return
   * a <code>String</code> that holds this virtual host name
   */
  public String getHostName();
  
  /**
   * Gets the virtual host properties
   * 
   * @return
   * this virtual host properties
   */
  public HostProperties getHostProperties();
}
