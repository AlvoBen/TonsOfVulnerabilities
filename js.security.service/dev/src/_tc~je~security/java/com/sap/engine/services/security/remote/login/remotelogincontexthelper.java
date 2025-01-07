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
package com.sap.engine.services.security.remote.login;

import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public interface RemoteLoginContextHelper extends Remote {

  public void login(RemoteCallbackHandler calbbackHandler) throws LoginException, RemoteException;

  public void logout() throws LoginException, RemoteException;

}