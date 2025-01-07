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
package com.sap.engine.services.security.server;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityModificationContextObject;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.config.PolicyDescriptor;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.userstore.UserStoreSwitchHelper;
import com.sap.engine.services.security.userstore.descriptor.ExtendedConnectorUserstoreConfiguration;
import com.sap.engine.services.security.userstore.descriptor.UserStoreConfigurationImpl;
import com.sap.engine.services.security.userstore.persistent.UserStoreChangedConfigurationListener;
import com.sap.engine.services.security.userstore.persistent.UserStorePersistent;
import com.sap.engine.services.security.userstore.policy.PolicyInitializer;
import com.sap.tc.logging.Severity;

/**
 *  User store configurations are registered with a user store instance.
 * There is a default static user store instance which should be used. Creating
 * a different user store instance is also allowed.
 *
 * @author  Stephan Zlatarev
 * @author  Ekaterina Zheleva
 * @version 6.30
 */
public class UserStoreFactoryImpl implements UserStoreFactory {

  Configuration root = null;
  ConfigurationHandler rootHandler = null;
  ModificationContextImpl modifications = null;
  UserStoreFactoryCache cache = null;
  UserStorePersistent   persistent = null;
  UserStoreChangedConfigurationListener listener = null;

  public UserStoreFactoryImpl(UserStoreFactoryCache cache) {
    this.cache = cache;
    cache.setOwner(this);
    listener = new UserStoreChangedConfigurationListener(cache);
    this.persistent = new UserStorePersistent(listener);
    if (persistent.isInitialized()) {
      UserStoreConfiguration[] registered = persistent.loadUserStoreConfigurations();
      String loaderName = null;
      for (int i = 0; i < registered.length; i++) {
        try {
          if (registered[i] != null) {
            loaderName = ((UserStoreConfigurationImpl) registered[i]).getClassLoaderName();
            cache.registerUserStore(registered[i], loaderName);
          }
        } catch (Exception e) {
          Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "UserStoreFactoryImpl", e);
        }
      }
      cache.setActiveUserStore(persistent.getActiveUserStore());
    }
  }

  /**
   *  Returns the active configuration.
   *
   * @return  the active configuration.
   */
  public UserStore getActiveUserStore() {
    try {
      return cache.getActiveUserStore();
    } catch (ArrayIndexOutOfBoundsException _) {
      throw new SecurityException("No active userstore is set.");
    }
  }

  public void setActiveUserStore(String name) {
    UserStore activeUserStore = null;

    try {
      activeUserStore = getActiveUserStore();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "setActiveUserStore", e);
    }

    if (activeUserStore == null || !activeUserStore.getConfiguration().getName().equals(name)) {
      cache.setActiveUserStore(name);
      persistent.modify(name);
      SecurityContext security = (SecurityContext) SecurityServerFrame.getServiceContext().getContainerContext().getObjectRegistry().getProvidedInterface("security");
      if (security != null) {
        new UserStoreSwitchHelper(security).onActiveUserStoreChanged();
      }
    }
  }

  public void setActiveUserStore(String name, PolicyDescriptor policy) {
    try {
      new PolicyInitializer().initializeUserStore(SecurityServerFrame.getSecurityContext(), policy);
      setActiveUserStore(name);
    } catch (Exception e) {
      throw new SecurityException("Cannot change active user store.", e);
    }
  }

  /**
   *  Returns the configuration with the name.
   *
   * @param  name  the name of a registered configuration
   *
   * @return  the configuration with the given name or null if such does not exist.
   */
  public UserStore getUserStore(String name) {
    root = null;
    try {
      SecurityModificationContextObject ctxObject = (SecurityModificationContextObject) SecurityServerFrame.threadContext.getThreadContext().getContextObject(SecurityModificationContextObject.NAME);
      root = (Configuration) ctxObject.getConfiguration();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "Cannot get ThreadContext's SecurityModificationContextObject, carring the configuration associated with the thread.", e);
    }
    return cache.getUserStore(name);
  }

  /**
   *  Returns all registered configurations.
   *
   * @return  all configurations.
   */
  public UserStore[] listUserStores() {
    return cache.listUserStores();
  }

  public synchronized void registerUserStore(UserStoreConfiguration config, ClassLoader classLoader) {
    config = new ExtendedConnectorUserstoreConfiguration(config);
    root = null;
    rootHandler = null;
    modifications = null;
    try {
      SecurityModificationContextObject ctxObject = (SecurityModificationContextObject) SecurityServerFrame.threadContext.getThreadContext().getContextObject(SecurityModificationContextObject.NAME);
      root = (Configuration) ctxObject.getConfiguration();
      rootHandler = (ConfigurationHandler) ctxObject.getAppConfigurationHandler();
      modifications = (ModificationContextImpl) ctxObject.getModificationContext();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "Cannot get ThreadContext's SecurityModificationContextObject, carring the configuration associated with the thread.", e);
    }
    cache.registerUserStore(config, classLoader);
    persistent.store(config, Util.getClassLoaderName(classLoader), modifications);
  }

  public synchronized void updateUserStore(UserStoreConfiguration config, ClassLoader classLoader) {
    cache.updateUserStore(config, classLoader);
    persistent.modify(config, Util.getClassLoaderName(classLoader), null);
  }

  public synchronized void renameUserStore(String name, String newName) {
    UserStore userStore = cache.getUserStore(name);
    if (userStore == null) {
      throw new SecurityException("No user store with this name registered.");
    }
    if (userStore.isActive()) {
      return;
    }

    root = null;
    rootHandler = null;
    modifications = null;
    try {
      SecurityModificationContextObject ctxObject = (SecurityModificationContextObject) SecurityServerFrame.threadContext.getThreadContext().getContextObject(SecurityModificationContextObject.NAME);
      root = (Configuration) ctxObject.getConfiguration();
      rootHandler = (ConfigurationHandler) ctxObject.getAppConfigurationHandler();
      modifications = (ModificationContextImpl) ctxObject.getModificationContext();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "Cannot get ThreadContext's SecurityModificationContextObject, carring the configuration associated with the thread.", e);
    }

    cache.unregisterUserStore(name);
    UserStoreConfiguration userstoreConfig = persistent.loadUserStoreConfiguration(name);
    persistent.delete(name, modifications);
    String loaderName = null;
    try {
      if (userstoreConfig != null) {
        loaderName = ((UserStoreConfigurationImpl) userstoreConfig).getClassLoaderName();
        ((UserStoreConfigurationImpl) userstoreConfig).setName(newName);
        ((UserStoreConfigurationImpl) userstoreConfig).setProperty("CONNECTOR", newName);
        cache.registerUserStore(userstoreConfig, loaderName);
        persistent.store(userstoreConfig, loaderName, modifications);
      }
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "UserStoreFactoryImpl", e);
    }

  }

  public synchronized void unregisterUserStore(String name) {
    UserStore userStore = cache.getUserStore(name);
    if (userStore == null) {
      throw new SecurityException("No user store with this name registered.");
    }
    if (userStore.isActive()) {
      throw new SecurityException("Cannot unregister active user store.");
    }

    root = null;
    rootHandler = null;
    modifications = null;
    try {
      SecurityModificationContextObject ctxObject = (SecurityModificationContextObject) SecurityServerFrame.threadContext.getThreadContext().getContextObject(SecurityModificationContextObject.NAME);
      root = (Configuration) ctxObject.getConfiguration();
      rootHandler = (ConfigurationHandler) ctxObject.getAppConfigurationHandler();
      modifications = (ModificationContextImpl) ctxObject.getModificationContext();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG , "Cannot get ThreadContext's SecurityModificationContextObject, carring the configuration associated with the thread.", e);
      modifications = null;
    }

    cache.unregisterUserStore(name);
    persistent.delete(name, modifications);
  }

  public void registerListener(UserStoreListener listener) {
    cache.registerListener(listener);
  }

  public void unregisterListener(UserStoreListener listener) {
    cache.unregisterListener(listener);
  }

  public boolean isInitialized() {
    return persistent.isInitialized();
  }

  protected synchronized java.util.HashSet listPersistentUserStores() {
    modifications = null;
    try {
      SecurityModificationContextObject ctxObject = (SecurityModificationContextObject) SecurityServerFrame.threadContext.getThreadContext().getContextObject(SecurityModificationContextObject.NAME);
      modifications = (ModificationContextImpl) ctxObject.getModificationContext();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG , "Cannot get ThreadContext's SecurityModificationContextObject, carring the configuration associated with the thread.", e);
    }
    return persistent.listUserStores(modifications);
  }

}