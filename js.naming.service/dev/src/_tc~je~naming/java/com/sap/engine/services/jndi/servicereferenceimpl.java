/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi;

import java.rmi.*;
import javax.rmi.*;

/**
 * References of the services binded in the JNDI
 *
 * @author Panayot Dobrikov, Petio Petev
 * @version 4.0
 */
public class ServiceReferenceImpl extends PortableRemoteObject implements RemoteServiceReference {

  /**
   * Name of the service
   */
  private String serviceName = "";

  private boolean service = true;

  /**
   * Constructor
   *
   * @param name Name of the service
   */
  public ServiceReferenceImpl(String name) throws java.rmi.RemoteException {
    this(name, true);
  }

  public ServiceReferenceImpl(String name, boolean service) throws java.rmi.RemoteException {
    this.serviceName = name;
    this.service = service;
  }

  /**
   * Gets the service interface. If the service is "automatic" and it is not
   * started, the method starts it.
   *
   * @return The service interface
   * @throws RemoteException Thrown if there is problem getting the interface
   */
  public Remote getServiceInterface() {
    Object obj = null;
    if (service) {
      obj = JNDIFrame.containerContext.getObjectRegistry().getServiceInterface(serviceName);
    } else {
      obj = JNDIFrame.containerContext.getObjectRegistry().getProvidedInterface(serviceName);
    }
    return (obj instanceof Remote) ? (Remote) obj : null;
  }

  public boolean isService() {
    return service;
  }


  /**
   * Disposes the service interface
   */
  public void releaseService() {

  }

  /**
   * This method is called from client to obtain service Name
   *
   * @throws RemoteException
   */
  public String getServiceName() throws RemoteException {
    return serviceName;
  }

  /**
   * Represents the object as a string
   */
  public String toString() {
    return "(SRI_" + Integer.toHexString(hashCode()).toUpperCase() + ") { Service Name: " + this.serviceName + " }";
  }

}

