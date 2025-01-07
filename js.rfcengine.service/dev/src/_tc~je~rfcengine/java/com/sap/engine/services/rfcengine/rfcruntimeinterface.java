/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rfcengine;

import com.sap.engine.frame.state.ManagementInterface;

import java.rmi.*;
import javax.rmi.*;
import java.util.*;

//import com.inqmy.frame.RuntimeInterface;
/**
 * RFC Engine Runtime Interface
 *
 * @author Nikolay Dimitrov, Hristo Iliev
 * @version 4.2
 */
public interface RFCRuntimeInterface
  extends ManagementInterface, Remote {

  /**
   * Starts a bundle
   *
   * @param  prgId   Program Id of the bundle
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public boolean startBundle(String prgId) throws RemoteException;


  /**
   * Stops a bundle
   *
   * @param  prgId   Program Id of the bundle
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public boolean stopBundle(String prgId) throws RemoteException;


  /**
   * Adds a bundle
   *
   * @param  conf   The bindle's configuration settings
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public boolean addBundle(BundleConfiguration conf) throws RemoteException;


  /**
   * Removes a bundle
   *
   * @param  prgId   Program ID of the bundle to be removed
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public boolean removeBundle(String prgId) throws RemoteException;


  /**
   * Changes the configuration of a bundle
   *
   * @param  config   The new configuration to be set
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public boolean changeBundleConfiguration(BundleConfiguration config) throws RemoteException;


  /**
   * Returns a bundle configuration
   *
   * @return   The requested bundle configuration or null if it doesn't exists
   */
  public BundleConfiguration getConfiguration(String programId) throws RemoteException;


  /**
   * Returns all bundle configurations
   *
   * @return   The configurations of all bundles
   */
  public BundleConfiguration[] getConfigurations() throws RemoteException;


  /**
   * Return the maximum processes in a bundle
   *
   * @return   The maximum processes
   */
  public int getMaxProcesses();


  /**
   * Return the maximum connections for a process
   *
   * @return   The maximum connections
   */
  public int getMaxConnections();
  
  /**
   * Register a new function
   * 
   * @param  functionName the name of the function
   * @param  ejbName the name of the EJB corresponding to the functionName
   */
   public void registerFunction(String functionName, String ejbName);
  
   /**
	* Unregister a function
	* 
	* @param  functionName the name of the function 
	*/
   public void unregisterFunction(String functionName);

}

