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

public interface UserListener {

  public static final int INFORM_ON_USER_ADDED = 0x01;
  public static final int INFORM_ON_USER_REMOVED = 0x02;
  public static final int INFORM_ON_USER_GROUPED = 0x04;


  public void userAdded(String userName) throws SecurityException;


  public void userRemoved(String userName) throws SecurityException;


  public void userGrouped(String user, String newGroup) throws SecurityException;


  public void userUngrouped(String user, String oldGroup) throws SecurityException;

}

