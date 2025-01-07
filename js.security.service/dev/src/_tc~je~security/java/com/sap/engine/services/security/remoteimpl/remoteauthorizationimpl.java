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
package com.sap.engine.services.security.remoteimpl;

import com.sap.engine.services.security.remote.RemoteAuthorization;
import com.sap.engine.services.security.remote.RemoteSecurity;
import com.sap.engine.services.security.remote.resource.RemoteSecurityResources;
import com.sap.engine.services.security.remote.roles.RemoteSecurityRoles;
import com.sap.engine.services.security.remoteimpl.roles.RemoteSecurityRolesImpl;
import com.sap.engine.services.security.remoteimpl.resource.RemoteSecurityResourcesImpl;
import com.sap.engine.services.security.server.AuthorizationContextImpl;
import com.sap.engine.interfaces.security.AuthorizationContext;

import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public class RemoteAuthorizationImpl extends PortableRemoteObject implements RemoteAuthorization {

  private AuthorizationContext authorization;
  private RemoteSecurity remote;

  public RemoteAuthorizationImpl(RemoteSecurity remote, AuthorizationContext authorization) throws RemoteException {
    this.remote = remote;
    this.authorization = authorization;
  }

  /**
   *  Returns an interface to the security resources module.
   *
   * @return  an interface to the security resources module.
   */
  public RemoteSecurityResources getResources() throws RemoteException {
    return new RemoteSecurityResourcesImpl(authorization.getSecurityResourceContext());
  }

  /**
   *  Returns an interface to the security roles module.
   *
   * @return  an interface to the security roles module.
   */
  public RemoteSecurityRoles getSecurityRoles() throws RemoteException {
    return new RemoteSecurityRolesImpl(authorization.getSecurityRoleContext());
  }

  /**
   *  Returns an interface to the security roles module.
   *
   * @param  userStore  the userstore name for the security roles.
   *
   * @return  an interface to the security roles module.
   */
  public RemoteSecurityRoles getSecurityRoles(String userStore) throws RemoteException {
    return new RemoteSecurityRolesImpl(((AuthorizationContextImpl) authorization).getSecurityRoleContext(userStore));
  }

}