/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

/**
 * This is the interface that has to be implemented from service part that is
 * working on Server node.
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface ApplicationServiceFrame extends ServiceFrame {

  /**
   * This method is invoked by the system when a service is started. In it
   * service can allocate resources and make connection to other components
   * from the system.
   *
   * @param   serviceContext  This parameter is connection from the service to
   * resources provided by the system.
   * @exception   ServiceException  Thrown if some problem occures while the
   * service initializes resources or establishes connections to other modules
   * from the system.
   */
  public void start(ApplicationServiceContext serviceContext) throws ServiceException;

}

