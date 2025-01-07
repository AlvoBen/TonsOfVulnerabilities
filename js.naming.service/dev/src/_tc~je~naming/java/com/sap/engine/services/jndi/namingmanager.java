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
package com.sap.engine.services.jndi;

import javax.naming.NamingException;

import com.sap.tc.logging.Location;

/**
 * Implementation indipendant base class for implementing naming service.
 * Actual subclass JNDIManager is staticaly created and used furthermore.
 *
 * @author Panayot Dobrikov, Elitsa Pancheva
 * @version 710
 */
public abstract class NamingManager {

  private static Location location = Location.getLocation(NamingManager.class);

  /**
   * Static abstract naming Manager
   */
  protected static NamingManager manager = null;

  /**
   * method for accessing corresponding Naming Manager
   *
   * @throws NamingException
   */
  public static NamingManager getNamingManager() throws javax.naming.NamingException {
    if (manager == null) {
      if (location.beDebug()) {
        location.debugT("JNDIManager field is NULL => will throw exception.");
      }
      throw new NamingException("The JNDI Registry Service is not started.");
    }
    return manager;
  }

  /**
   * Start JNDI
   *
   * @throws javax.naming.NamingException
   */
  public abstract JNDIProxy start() throws javax.naming.NamingException;

  /**
   * return Proxy, from which ServerContextImpl with new connnection is available
   *
   * @throws javax.naming.NamingException
   */
  public abstract JNDIProxy getProxy() throws javax.naming.NamingException;

  /**
   * Stop JNDI
   *
   * @throws javax.naming.NamingException
   */
  public abstract void stop() throws javax.naming.NamingException;

}

