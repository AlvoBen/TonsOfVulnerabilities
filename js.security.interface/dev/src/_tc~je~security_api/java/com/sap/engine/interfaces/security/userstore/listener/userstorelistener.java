/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.listener;

public interface UserStoreListener {

  public void userStoreRegistered(String userstore) throws SecurityException;


  public void userStoreUnregistered(String userstore) throws SecurityException;


  public void userStoreActivated(String userstore) throws SecurityException;


  //may be method for notifying userStoreConfiguration changes  
  //and may be not

}

