
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

import java.lang.reflect.Method;
import java.util.Properties;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.config.ConfigurationEditor;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.context.GroupContext;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.spi.GroupContextSpi;
import com.sap.engine.interfaces.security.userstore.spi.UserContextSpi;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.userstore.persistent.ConnectorUserStoreConfigurationListener;
import com.sap.tc.logging.Severity;

/**
 *
 * @author  Ekaterina Zheleva
 * @version 6.30
 */
public class UserStoreImpl implements UserStore {

  private ClassLoader classLoader = null;
  private String classLoaderName = null;
  private UserStoreFactoryCache factory = null;
  private UserStoreConfiguration config = null;
  private boolean active = false;
  private UserContext userContext = null;
  private GroupContext groupContext = null;
  private UserContextSpi userSpi = null;
  private ConnectorUserStoreConfigurationListener connectorUSListener = null;

  public UserStoreImpl(UserStoreFactoryCache factory, UserStoreConfiguration config, ClassLoader loader) {
    this.factory = factory;
    this.config = config;
    this.classLoader = (loader != null) ? loader : this.getClass().getClassLoader();
    String userClass = config.getUserSpiClassName();
    String groupClass = config.getGroupSpiClassName();
    Properties props = config.getUserStoreProperties();

    if (userClass != null && !userClass.equals("")) {
      java.lang.reflect.Constructor userClassConstructor = null;
      Configuration modificationsRoot = null;
      try {
        userClassConstructor = classLoader.loadClass(userClass).getConstructor(new Class[] {Configuration.class});
        modificationsRoot = ((UserStoreFactoryImpl) factory.getOwner()).root;

        if (modificationsRoot != null && userClassConstructor != null) {
          userSpi = (UserContextSpi) userClassConstructor.newInstance(new Object[] {modificationsRoot});
          userContext = new com.sap.engine.services.security.userstore.context.UserContext(userSpi, props);
        }
      } catch (Throwable t) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", t);
      }

      if (userSpi == null) {
        try {
          userSpi = (UserContextSpi) classLoader.loadClass(userClass).newInstance();
          this.userContext = new com.sap.engine.services.security.userstore.context.UserContext(userSpi, props);
        } catch (Throwable t) {
          throw new SecurityException("Can not instantiate UserContext.", t);
        }
      }

      if (userSpi != null && userClassConstructor != null) {
        try {
          connectorUSListener = new ConnectorUserStoreConfigurationListener(factory, config.getName());
          userSpi.getClass().getMethod("registerConnectorConfigurationListener", new Class[] {Class.forName("com.sap.engine.frame.core.configuration.ConfigurationChangedListener"), ConfigurationHandler.class}).invoke(userSpi, new Object[] {connectorUSListener, ((UserStoreFactoryImpl) factory.getOwner()).rootHandler});
        } catch (Exception e) {
          Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", e);
        }
      }
    }

    if (groupClass != null && !groupClass.equals("")) {
      try {
        GroupContextSpi groupSpi = (GroupContextSpi) classLoader.loadClass(groupClass).newInstance();
        this.groupContext = new com.sap.engine.services.security.userstore.context.GroupContext(groupSpi, props);
      } catch (Throwable t) {
        throw new SecurityException("Can not instantiate GroupContext.", t);
      }
    }
    try {
      Method method = config.getClass().getMethod("clearStartupConfiguration", null);
      if (method != null) {
        method.invoke(config, null);
      }
    } catch (Throwable e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", e);
    }
  }

  public UserStoreImpl(UserStoreFactoryCache factory, UserStoreConfiguration config, String loaderName) {
    this.factory = factory;
    this.config = config;
    this.classLoaderName = loaderName;
    String userClassName = config.getUserSpiClassName();
    String groupClassName = config.getGroupSpiClassName();
    Properties props = config.getUserStoreProperties();

    if (userClassName != null && !userClassName.equals("")) {
      java.lang.reflect.Constructor userClassConstructor = null;
      Configuration modificationsRoot = null;
      Class userClass = null;

      try {
        userClass = Util.loadClass(userClassName, loaderName);

        try {
          userClassConstructor = userClass.getConstructor(new Class[] {Configuration.class});
          modificationsRoot = ((UserStoreFactoryImpl) factory.getOwner()).root;
          if (modificationsRoot != null && userClassConstructor != null) {
            userSpi = (UserContextSpi) userClassConstructor.newInstance(new Object[] {modificationsRoot});
            userContext = new com.sap.engine.services.security.userstore.context.UserContext(userSpi, props);
          }
        } catch (Throwable t) {
          Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", t);
        }

        if (userSpi == null) {
          userSpi = (UserContextSpi) userClass.newInstance();
          this.userContext = new com.sap.engine.services.security.userstore.context.UserContext(userSpi, props);
        }

        if (userSpi != null && userClassConstructor != null) {
          try {
            connectorUSListener = new ConnectorUserStoreConfigurationListener(factory, config.getName());
            userSpi.getClass().getMethod("registerConnectorConfigurationListener", new Class[] {Class.forName("com.sap.engine.frame.core.configuration.ConfigurationChangedListener"), ConfigurationHandler.class}).invoke(userSpi, new Object[] {connectorUSListener, ((UserStoreFactoryImpl) factory.getOwner()).rootHandler});
          } catch (Exception e) {
            Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", e);
          }
        }
      } catch (Throwable t) {
        throw new SecurityException("Can not instantiate UserContext.", t);
      }
    }

    if (groupClassName != null && !groupClassName.equals("")) {
      Class groupClass = null;
      try {
        groupClass = Util.loadClass(groupClassName, loaderName);
        GroupContextSpi groupSpi = (GroupContextSpi) groupClass.newInstance();
        this.groupContext = new com.sap.engine.services.security.userstore.context.GroupContext(groupSpi, props);
      } catch (Throwable t) {
        throw new SecurityException("Can not instantiate GroupContext.", t);
      }
    }
    try {
      java.lang.reflect.Method method = config.getClass().getMethod("clearStartupConfiguration", null);
      if (method != null) {
        method.invoke(config, null);
      }
    } catch (Throwable e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", e);
    }
  }

  public void update(UserStoreConfiguration config) {
    this.config = config;

    if (userContext != null) {
      userContext.propertiesChanged(config.getUserStoreProperties());
    }
    if (groupContext != null) {
      groupContext.propertiesChanged(config.getUserStoreProperties());
    }
  }

  public UserStoreConfiguration getConfiguration() throws SecurityException {
    return config;
  }

  public UserContext getUserContext() throws SecurityException {
    return userContext;
  }

  public GroupContext getGroupContext() throws SecurityException {
    return groupContext;
  }

  public boolean isActive() throws SecurityException {
    return active;
  }

  public UserStoreFactory getFactory() throws SecurityException {
    return factory.getOwner();
  }

  /**
   *  Returns an instance of the configuration editor for the user store.
   *
   * @return  instance of ConfigurationEditor or null if it cannot be instanciated.
   */
  public ConfigurationEditor getConfigurationEditor() throws SecurityException {
    try {
      String className = config.getConfigurationEditorClassName();
      if (classLoader != null) {
        return (ConfigurationEditor) classLoader.loadClass(className).newInstance();
      }
      return (ConfigurationEditor) Util.loadClass(className, classLoaderName).newInstance();
    } catch (Throwable t) {
      throw new SecurityException("Configuration editor can not be loaded or instantiated.", t);
    }
  }

  void setActiveInternal(boolean active) {
    this.active = active;
  }

  public void setActive(boolean active) {
    factory.getOwner().setActiveUserStore(config.getName());
  }

  public void unregisterUserStoreListener() {
    if (connectorUSListener != null) {
      Configuration modificationsRoot = ((UserStoreFactoryImpl) factory.getOwner()).root;
      try {
        if (modificationsRoot == null || !modificationsRoot.isValid()) {
          userSpi.getClass().getMethod("unregisterConnectorConfigurationListener", new Class[] {Class.forName("com.sap.engine.frame.core.configuration.ConfigurationChangedListener")}).invoke(userSpi, new Object[] {connectorUSListener});
        } else {
          userSpi.getClass().getMethod("unregisterConnectorConfigurationListener", new Class[] {Class.forName("com.sap.engine.frame.core.configuration.ConfigurationChangedListener"), Configuration.class}).invoke(userSpi, new Object[] {connectorUSListener, modificationsRoot});
        }
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", e);
      }
    }
  }

  public void setUserStoreTransactionAttribute() {
    Configuration modificationsRoot = ((UserStoreFactoryImpl) factory.getOwner()).root;
    try {
      if (modificationsRoot != null && modificationsRoot.isValid()) {
        userSpi.getClass().getMethod("setTransactionAttribute", new Class[] {Configuration.class}).invoke(userSpi, new Object[] {modificationsRoot});
      }
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.PATH, "", e);
    }
  }
 
}

