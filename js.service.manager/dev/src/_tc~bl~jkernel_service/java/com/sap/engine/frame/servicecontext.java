/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.state.ServiceState;

/**
 * This context is root for all contexts accessed by a service. It contains
 * access to sub contexts that are equals for a all parts of a service that
 * work on Dispatcher or Server node or in standalone mode.
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface ServiceContext {

  /**
   * Gets the CoreContext to access the core of the system
   *
   * @return  CoreContext for core interaction.
   */
  public CoreContext getCoreContext();


  /**
   * Gets the ServiceState context to access service specific information.
   *
   * @return  ServiceState for service specific information.
   */
  public ServiceState getServiceState();

}

