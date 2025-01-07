/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.implserver;

import javax.naming.*;

/**
 * Remote interface for Naming Enumeration implementation
 *
 * @author Panayot Dobrikov
 * @author Petio Petev
 * @version 4.00
 */
public interface ServerNamingEnum extends java.rmi.Remote {

  /**
   * Returns next object in the enumeration
   *
   * @return The next object in the enumeration
   * @throws java.rmi.RemoteException When there were problems encountered while doing remote operations
   * @throws NamingException When there were problems encountered while doing operations with the naming
   */
  public Object next() throws java.rmi.RemoteException, NamingException;


  //public int numberOfElements() throws java.rmi.RemoteException;
  /**
   * Returns the flag representing wether there are more objects
   *
   * @return The flag representing wether there are more objects in the enumerations
   * @throws java.rmi.RemoteException When there were problems encountered while doing remote operations
   * @throws NamingException When there were problems encountered while doing operations with the naming
   */
  public boolean hasMore() throws java.rmi.RemoteException, NamingException;


  /**
   * Closes the repository used to enumerate
   *
   * @throws java.rmi.RemoteException When there were problems encountered while doing remote operations
   */
  public void close() throws java.rmi.RemoteException;

}

