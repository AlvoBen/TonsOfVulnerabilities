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

import com.sap.engine.services.security.remote.RemoteAuthentication;
import com.sap.engine.services.security.remote.RemoteUserStore;
import com.sap.engine.services.security.remote.RemoteSecurity;
import com.sap.engine.services.security.remote.authentication.RemoteAppConfigurationEntry;
import com.sap.engine.services.security.server.AuthenticationContextImpl;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.frame.state.ManagementListener;

import javax.security.auth.login.AppConfigurationEntry;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import java.util.Map;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class RemoteAuthenticationImpl extends PortableRemoteObject implements RemoteAuthentication {

  private AuthenticationContext authentication;
  private RemoteSecurity remote;

  public RemoteAuthenticationImpl(RemoteSecurity remote, AuthenticationContext authentication) throws RemoteException {
    this.authentication = authentication;
    this.remote = remote;
  }

  public RemoteUserStore getAuthenticationUserStore() throws RemoteException {
    String name = authentication.getAuthenticationUserStore().getConfiguration().getName();
    return remote.getRemoteUserStoreFactory().getUserStore(name);
  }

  public RemoteAppConfigurationEntry[] getLoginModules() throws RemoteException {
    AppConfigurationEntry[] entries = authentication.getLoginModules();
    RemoteAppConfigurationEntry[] result = new RemoteAppConfigurationEntry[entries.length];

    for (int i = 0; i < result.length; i++) {
      String name = entries[i].getLoginModuleName();
      Map options = entries[i].getOptions();
      LoginModuleConfiguration[] modules = getAuthenticationUserStore().getConfiguration().getLoginModules();

      for (int j = 0; j < modules.length; j++) {
        if (modules[j].getLoginModuleClassName().equals(name) && modules[j].getOptions().equals(options)) {
          name = modules[j].getName();
          break;
        }
      }

      result[i] = new RemoteAppConfigurationEntry(entries[i], name);
    }

    return result;
  }

  public RemoteAppConfigurationEntry[] getLoginModules(String userStore) throws RemoteException {
    AppConfigurationEntry[] entries = ((AuthenticationContextImpl) authentication).getLoginModules(userStore);
    RemoteAppConfigurationEntry[] result = new RemoteAppConfigurationEntry[entries.length];

    for (int i = 0; i < result.length; i++) {
      String name = entries[i].getLoginModuleName();
      Map options = entries[i].getOptions();
      LoginModuleConfiguration[] modules = getAuthenticationUserStore().getConfiguration().getLoginModules();

      for (int j = 0; j < modules.length; j++) {
        if (modules[j].getLoginModuleClassName().equals(name) && modules[j].getOptions().equals(options)) {
          name = modules[j].getName();
          break;
        }
      }

      result[i] = new RemoteAppConfigurationEntry(entries[i], name);
    }

    return result;
  }
  public String getProperty(String key) throws RemoteException {
    return authentication.getProperty(key);
  }

  public Map getProperties() throws RemoteException {
    return authentication.getProperties(); 
  }
   
  public String getTemplate() throws RemoteException {
    return authentication.getTemplate();
  }

  public void setLoginModules(RemoteAppConfigurationEntry[] modules) throws RemoteException {
    AppConfigurationEntry[] entries = new AppConfigurationEntry[modules.length];

    for (int i = 0; i < entries.length; i++) {
      entries[i] = modules[i].getAppConfigurationEntry();
    }

    authentication.setLoginModules(entries);
  }

  public void setLoginModules(String userStore, RemoteAppConfigurationEntry[]  modules) throws RemoteException {
    AppConfigurationEntry[] entries = new AppConfigurationEntry[modules.length];

    for (int i = 0; i < entries.length; i++) {
      entries[i] = modules[i].getAppConfigurationEntry();
    }

    ((AuthenticationContextImpl) authentication).setLoginModules(userStore, entries);
  }

  public void setProperty(String key, String value) throws RemoteException {
    authentication.setProperty(key, value);
  }

  public void setTemplate(String template) throws RemoteException {
    authentication.setLoginModules(template);
  }

  public void registerManagementListener( ManagementListener managementListener ) {
    /** @todo  registerManagmentListener */
  }
}