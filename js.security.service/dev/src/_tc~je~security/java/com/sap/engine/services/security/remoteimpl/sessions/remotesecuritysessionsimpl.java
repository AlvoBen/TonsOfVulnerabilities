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
package com.sap.engine.services.security.remoteimpl.sessions;

import com.sap.engine.services.security.login.SecuritySession;
import com.sap.engine.services.security.login.SecuritySessionPool;
import com.sap.engine.services.security.remote.sessions.RemoteSecuritySessions;
import com.sap.engine.services.security.server.AuthenticationContextImpl;

import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public class RemoteSecuritySessionsImpl extends PortableRemoteObject implements RemoteSecuritySessions {

  SecuritySessionPool pool = null;

  public RemoteSecuritySessionsImpl() throws RemoteException {
    pool = AuthenticationContextImpl.getSessionPool();
  }

  public SecuritySession[] listSecuritySessions() throws RemoteException {
    return pool.listSessions();
  }

  public void removeSecuritySession(SecuritySession session) throws RemoteException {
    pool.removeSessions(session);
  }

}