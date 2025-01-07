/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

/**
 * This is base interface for services. It is extended by dispatcher/server/
 * standalone specific service interfaces.
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface ServiceFrame {

  /**
   * This is method for stopping the service. Service has to free all allocated
   * resources and to achieve the state before starting.
   *
   * @exception   ServiceRuntimeException  Thrown if some problem occures while
   * stopping the service.
   *
   */
  public void stop() throws ServiceRuntimeException;

}

