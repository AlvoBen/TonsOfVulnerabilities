package com.sap.engine.services.httpserver.chain;

import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.server.HttpHosts;
import com.sap.engine.services.httpserver.server.HttpMonitoring;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;

/**
 * This class gives access to all HTTP Provider service scope properties,
 * helper objects, etc.
 * 
 * TODO: Add new useful methods with care
 */
public interface ServerScope extends Scope {
  /**
   * Returns a object with all HTTP Provider service properties
   * 
   * @return
   * an <code>HttpProperties</code> object with all 
   * HTTP Provider service properties
   */
  public abstract HttpProperties getHttpProperties();
  
  /**
   * Gives access to all HTTP hosts
   * 
   * @return
   * an <code>HttpHosts</code> object with all HTTP hosts
   */
  public abstract HttpHosts getHttpHosts();
  
  /**
   * Gives access to HTTP Provider service monitoring
   * 
   * @return
   * an <code>HttpMonitoring</code> object
   */
  public abstract HttpMonitoring getHttpMonitoring();
  
  /**
   * Provides access to the logon groups 
   * 
   * @return the <code>LogonGroupsManagerImpl</code> object 
   */
  public LogonGroupsManager getLogonGroupsManager();
}
