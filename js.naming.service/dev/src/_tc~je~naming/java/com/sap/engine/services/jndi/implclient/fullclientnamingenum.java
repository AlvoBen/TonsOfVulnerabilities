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
package com.sap.engine.services.jndi.implclient;

import java.rmi.RemoteException;
import javax.naming.NamingException;

import com.sap.engine.lib.util.ConcurrentArrayObject;
import com.sap.engine.services.jndi.implserver.ServerNamingEnum;

import java.util.NoSuchElementException;

/**
 * @author Nikolay Dimitrov
 * @version 6.30
 */
public class FullClientNamingEnum implements ServerNamingEnum {

  private ServerNamingEnum namingEnumeration = null;
  private ConcurrentArrayObject LROEnumeration = null;
  private int LROindex = 0;
  private final int LROSize;
  private boolean hasMoreElements = true;

  public FullClientNamingEnum(ServerNamingEnum namingEnumeration, ConcurrentArrayObject LROEnumeration) {
    this.namingEnumeration = namingEnumeration;
    this.LROEnumeration = LROEnumeration;
    this.LROSize = LROEnumeration.size();
  }

  public Object next() throws RemoteException, NamingException {
    if (hasMore()) {
      if (LROindex < LROSize) {
        return LROEnumeration.get(LROindex++);
      } else {
        return namingEnumeration.next();
      }
    } else {
      throw new NoSuchElementException("There are no more elements in the naming enumeration.");
    }
  }

  public boolean hasMore() throws RemoteException, NamingException {
    if (hasMoreElements) {
      if (LROindex < LROSize) {
        return true;
      }

      if (namingEnumeration != null) { //passes null when no objects meet the specified criteria in naming, but there are some from LROTable
        if (namingEnumeration.hasMore()) {
          return true;
        } else {
          hasMoreElements = false;
          return false;
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public void close() throws RemoteException {
    hasMoreElements = false;
  }

}

