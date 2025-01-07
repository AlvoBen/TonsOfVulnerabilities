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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.services.security.remote.authentication.RemoteAppConfigurationEntry;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public interface RemoteAuthentication extends ManagementInterface, Remote {

  public RemoteUserStore getAuthenticationUserStore() throws RemoteException;

  public RemoteAppConfigurationEntry[] getLoginModules() throws RemoteException;

  public RemoteAppConfigurationEntry[] getLoginModules(String userStore) throws RemoteException;

  public String getProperty(String key) throws RemoteException;

  public Map getProperties() throws RemoteException;

  public String getTemplate() throws RemoteException;

  public void setLoginModules(RemoteAppConfigurationEntry[]  modules) throws RemoteException;

  public void setLoginModules(String userStore, RemoteAppConfigurationEntry[]  modules) throws RemoteException;

  public void setProperty(String key, String value) throws RemoteException;

  public void setTemplate(String template) throws RemoteException;

}