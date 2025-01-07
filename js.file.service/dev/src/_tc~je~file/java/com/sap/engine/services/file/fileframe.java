/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.file;

import java.rmi.*;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;

import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.log.LogInterface;
import com.sap.engine.interfaces.log.LoggerAlreadyExistingException;

//import com.inqmy.frame.*;
//import com.inqmy.frame.container.client.ClientContext;
//import com.inqmy.frame.container.log.LogContext;
/**
 * FileFrame is the main class of File Service. Being an implementation of
 * ApplicationServiceFrame interface it is responsible for registering,
 * starting and stopping File Service on server. File Service provides
 * functionality for uploading and downloading files between remote servers.
 *
 * @author
 * @version 4.0.0
 */
public class FileFrame implements ApplicationServiceFrame, ContainerEventListener  {

  //TODO   private static LogContext log = null;
  private static long logId = -1;
  private ObjectRegistry clientContext;
  private ApplicationServiceContext serviceContext = null;
  private CrossInterface crossInterface = null;

  /**
   * Starts File Service on server.
   * This method is invoked by Service Manager after loading all services.
   *
   * @param   properties      properties providing some information of
   *                          start process flow of the service.
   * @param   serviceContext  the ServiceContext given to the service to operate with.
   *
   * @exception   ServiceException  thrown if some problem occurs while the service initializes and starts.
   */
  public void start(ApplicationServiceContext serviceContext) throws ServiceException {
    try {
      this.serviceContext = serviceContext;
      int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE | ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE;
      Set names = new HashSet(1);
      names.add("cross");
      serviceContext.getServiceState().registerContainerEventListener(mask, names, this);
      clientContext = serviceContext.getContainerContext().getObjectRegistry();
      FileTransfer transferer = new FileTransferImpl();
      clientContext.registerInterface(transferer);
      crossInterface = ((CrossInterface) serviceContext.getContainerContext().getObjectRegistry().getProvidedInterface("cross"));
      
      if (crossInterface != null) {        
        crossInterface.setInitialObject("file", transferer);        
      }
      //TODO      log = serviceContext.getContainerContext().getLogContext();
      //      logId = log.register("fileService", null);
    } catch (RemoteException rException) {
      throw new ServiceException(rException.getMessage());
    }
  }

  /**
   * This method is invoked on File Service when some of its initial properties is being changed.
   *
   * @param   properties  new properties to be set.
   *
   * @return  true - if the service should be restarted before proceeding.
   *
   * @exception   IllegalArgumentException  thrown if an illegal or inappropriate argument
   *                                        was passed to the method.
   */
  public boolean changeProperties(Properties properties) throws IllegalArgumentException {
    return true;
  }

  /**
   * Stops File Service on server.
   * This is the last method invoked on it before stopping.
   *
   * @param   forse  if the service will be stopped immediatly
   * @param   dropDown  if the all cluster is dropped down
   *
   * @param   forceFlag  true - if the service should be stopped immediately.
   * @param   allFlag    true - if all cluster elements are dropped down.
   */
  public void stop() {
    //TODO    log.unregister(logId);
    CrossInterface cross = (CrossInterface) serviceContext.getContainerContext().getObjectRegistry().getProvidedInterface("cross");
    if (cross != null) {
      cross.removeInitialObject("file");
    } 
    clientContext.unregisterInterface();
  }

  /**
   * Logs messages about all considerable events in File Service.
   *
   * @param   messageType  byte value representing the level of importance of this event.
   * @param   message      String representation of the message to log.
   */
  public static void log(byte messageType, String message) {
    //TODO    if (log != null) {
    //      log.log(logId, messageType, message, (String) null, (String) null);
    //    }
  }

  public void containerStarted() {

  }

  public void beginContainerStop() {

  }

  public void serviceStarted(String serviceName, Object serviceInterface) {

  }

  public void serviceNotStarted(String serviceName) {

  }

  public void beginServiceStop(String serviceName) {

  }

  public void serviceStopped(String serviceName) {

  }

  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    if (interfaceName.equals("cross")) {
      crossInterface = (CrossInterface) interfaceImpl; 
      FileTransfer transferer = null;
      try {
        transferer =  new FileTransferImpl();
      } catch (Exception e) {//$JL-EXC$      
      }
      if (crossInterface != null) {
        crossInterface.setInitialObject("file", transferer);
      }
    }
  }

  public void interfaceNotAvailable(String interfaceName) {
    if (interfaceName.equals("cross")) {
      crossInterface = null;      
    } 
  }

  public void markForShutdown(long time) {
  }

  public boolean setServiceProperty(String key, String value) {
    return false;
  }

  public boolean setServiceProperties(Properties serviceProperties) {
    return false;
  }
}

