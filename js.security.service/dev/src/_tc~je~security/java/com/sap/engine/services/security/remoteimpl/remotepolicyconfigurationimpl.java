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

import com.sap.engine.services.security.remote.RemotePolicyConfiguration;
import com.sap.engine.services.security.remote.RemoteAuthentication;
import com.sap.engine.services.security.remote.RemoteSecurity;
import com.sap.engine.services.security.remote.RemoteAuthorization;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.frame.state.ManagementListener;

import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class RemotePolicyConfigurationImpl extends PortableRemoteObject implements RemotePolicyConfiguration {

  private RemoteSecurity remote;
  private SecurityContext security;

  public RemotePolicyConfigurationImpl(RemoteSecurity remote, SecurityContext security, String name) throws RemoteException {
    this.remote = remote;
    this.security = security;
  }

  public RemoteAuthentication getAuthentication() throws RemoteException {
    return new RemoteAuthenticationImpl(remote, security.getAuthenticationContext());
  }

  public RemoteAuthorization getAuthorization() throws RemoteException {
    return new RemoteAuthorizationImpl(remote, security.getAuthorizationContext());
  }
  
  public byte getPolicyConfigurationType() throws RemoteException {
    return security.getPolicyConfigurationType();
  }


  public void registerManagementListener( ManagementListener managementListener ) {
    /** @todo  registerManagmentListener */
  }
}