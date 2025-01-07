/**
 * Copyright (c) 2002 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.security.remote;

import com.sap.engine.services.security.remote.roles.RemoteSecurityRoles;
import com.sap.engine.services.security.remote.resource.RemoteSecurityResources;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public interface RemoteAuthorization extends Remote {

  /**
   *  Returns an interface to the security resources module.
   *
   * @return  an interface to the security resources module.
   */
  public RemoteSecurityResources getResources() throws RemoteException;

  /**
   *  Returns an interface to the security roles module.
   *
   * @return  an interface to the security roles module.
   */
  public RemoteSecurityRoles getSecurityRoles() throws RemoteException;

  /**
   *  Returns an interface to the security roles module.
   *
   * @param  userStore  the userstore name for the security roles.
   *
   * @return  an interface to the security roles module.
   */
  public RemoteSecurityRoles getSecurityRoles(String userStore) throws RemoteException;

}