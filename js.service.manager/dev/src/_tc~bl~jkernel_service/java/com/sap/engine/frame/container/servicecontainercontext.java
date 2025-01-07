/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame.container;

import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.frame.container.registry.ObjectRegistry;

/**
 * This context provides a way for iteraction with service container
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface ServiceContainerContext {

  /**
   * Get SystemMonitor. It is used for monitoring the whole system.
   *
   * @return  SystemMonitor for monitoring the system
   */
  public SystemMonitor getSystemMonitor();


  /**
   * Used to register runtime objects provided by services
   *
   * @return  ObjectRegistry for register or get objects
   */
  public ObjectRegistry getObjectRegistry();

}

