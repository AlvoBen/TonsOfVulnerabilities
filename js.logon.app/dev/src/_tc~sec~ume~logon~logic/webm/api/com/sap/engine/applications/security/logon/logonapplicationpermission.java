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
package com.sap.engine.applications.security.logon;

import com.sap.security.api.permissions.NamePermission;

/**
 *  This permission is to be used by the logon application where needed.
 * 
 * @author Svetlana Stancheva
 * @version 7.10
 */
public class LogonApplicationPermission extends NamePermission {

  public LogonApplicationPermission(String name) {
    super(name);
  }  
  
  public LogonApplicationPermission(String name, String actions) {
    super(name, actions);
  }

}
