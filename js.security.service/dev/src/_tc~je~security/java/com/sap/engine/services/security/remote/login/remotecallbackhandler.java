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

import javax.security.auth.callback.UnsupportedCallbackException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public interface RemoteCallbackHandler extends Remote {

  public Object handle(Object acallback) throws IOException, UnsupportedCallbackException, RemoteException;

  public Object[] handle(Object[] callbacks) throws IOException, UnsupportedCallbackException, RemoteException;

}