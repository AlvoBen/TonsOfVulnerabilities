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

public interface GroupListener {

  public static final int INFORM_ON_GROUP_ADDED = 0x01;
  public static final int INFORM_ON_GROUP_REMOVED = 0x02;
  public static final int INFORM_ON_GROUP_LINKED = 0x04;


  public void groupAdded(String group) throws SecurityException;


  public void groupRemoved(String group) throws SecurityException;


  public void groupLinked(String child, String parentGroup) throws SecurityException;


  public void groupUnlinked(String child, String parentGroup) throws SecurityException;

}

