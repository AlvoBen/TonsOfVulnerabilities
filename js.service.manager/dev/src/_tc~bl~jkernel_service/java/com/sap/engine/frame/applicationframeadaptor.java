/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

/**
 * This is adaptor for ApplicationServiceFrame interface
 *
 * @author Jasen Minov
 * @version 6.30
 */
public abstract class ApplicationFrameAdaptor implements ApplicationServiceFrame {

  private ApplicationServiceContext serviceContext;

  /**
   * This method is invoked by the system when a service is started. It stores
   * reference to the service context into a local field and calls an abstract
   * <source> start <source> method.
   *
   * @param   serviceContext  This parameter is connection from the service to
   * resources provided by the system.
   * @exception   ServiceException  Thrown if some problem occures while the
   * service initializes resources or establishes connections to other modules
   * from the system.
   */
  public final void start(ApplicationServiceContext serviceContext) throws ServiceException {
    this.serviceContext = serviceContext;
    start();
  }

  /**
   * Get ApplicationServiceContext for access to the system.
   *
   * @return service context
   */
  public final ApplicationServiceContext getServiceContext() {
    return serviceContext;
  }

  /**
   * This method is invoked by the system when a service is started. In it
   * service can allocate resources and make connection to other components
   * from the system.
   *
   * @exception   ServiceException  Thrown if some problem occures while the
   * service initializes resources or establishes connections to other modules
   * from the system.
   */
  public abstract void start() throws ServiceException;

}

