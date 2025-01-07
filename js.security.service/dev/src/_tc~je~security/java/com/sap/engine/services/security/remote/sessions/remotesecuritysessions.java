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
package com.sap.engine.services.security.remote.sessions;

import com.sap.engine.services.security.login.SecuritySession;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public interface RemoteSecuritySessions extends Remote {

  public SecuritySession[] listSecuritySessions() throws RemoteException;

  public void removeSecuritySession(SecuritySession session) throws RemoteException;

}