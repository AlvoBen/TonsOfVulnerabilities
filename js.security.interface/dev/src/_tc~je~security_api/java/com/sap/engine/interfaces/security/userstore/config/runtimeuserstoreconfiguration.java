/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.config;

import java.util.Properties;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  Class for configuring a single UserStore at runtime (on the fly).
 * It includes a display-name, description, user and group context class-names,
 * properties, configuration editor, ... .
 *
 * @author Svetlana Stancheva
 * @version 7.0
 */
public class RuntimeUserStoreConfiguration implements UserStoreConfiguration {

  /**
   *  Serial Version UID is fixed to 2697243797387521990L
   */
  public final static long serialVersionUID = 2697243797387521990L;

  private String anonymous = null;

  private String name = null;

  private String description = null;

  private String userSpi = null;

  private String groupSpi = null;

  private String editor = null;

  private Properties properties = null;

  private UserStoreConfiguration configuration = null;

  private LoginModuleConfiguration[] loginModules = null;

  /**
   *  Constructs a user store configuration. It may wrap an existing user
   * store configuration to inherit configurtions from.
   *
   * @param  configuration  wrapped user store configuration
   */
  public RuntimeUserStoreConfiguration(UserStoreConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Returns the anonymous user for the user store.
   *
   * @return the user name of the anonymous user account or <code>null</code>.
   */
  public String getAnonymousUser() {
    return anonymous;
  }

  /**
   *  Returns the description of the user store.
   *
   * @return  printable text or  <code>null</code>.
   */
  public String getDescription() {
    if (description != null) {
      return description;
    } else if (configuration != null) {
      return configuration.getDescription();
    } else {
      return null;
    }
  }

  /**
   *  Returns the display name of the user store.
   *
   * @return  display name or <code>null</code>.
   */
  public String getName() {
    if (name != null) {
      return name;
    } else if (configuration != null) {
      return configuration.getName();
    } else {
      return null;
    }
  }

  /**
   *  Returns the configured login modules for this user store.
   *
   * @return  an array of login module configurations.
   */
  public LoginModuleConfiguration[] getLoginModules() {
    if (loginModules != null) {
      return loginModules;
    } else if (configuration != null) {
      return configuration.getLoginModules();
    } else {
      return new LoginModuleConfiguration[0];
    }
  }

  /**
   *  Returns the class name of the user context spi for the user store.
   *
   * @return  class name.
   */
  public String getUserSpiClassName() {
    if (userSpi != null) {
      return userSpi;
    } else if (configuration != null) {
      return configuration.getUserSpiClassName();
    } else {
      return null;
    }
  }

  /**
   *  Returns the class name of the group context spi for the user store.
   *
   * @return  class name or <code>null</code>.
   */
  public String getGroupSpiClassName() {
    if (groupSpi != null) {
      return groupSpi;
    } else if (configuration != null) {
      return configuration.getGroupSpiClassName();
    } else {
      return null;
    }
  }

  /**
   *  Returns the properties of the user store.
   *
   * @return  the properties of the userstore or <code>null</code>.
   */
  public Properties getUserStoreProperties() {
    if (properties != null) {
      return properties;
    } else if (configuration != null) {
      return configuration.getUserStoreProperties();
    } else {
      return null;
    }
  }

  /**
   *  Returns the class name of the configuration editor for the user store.
   *
   * @return  class name or <code>null</code>.
   */
  public String getConfigurationEditorClassName() {
    if (editor != null) {
      return editor;
    } else if (configuration != null) {
      return configuration.getConfigurationEditorClassName();
    } else {
      return null;
    }
  }

  /**
   *  Changes the display name of the user store.
   * 
   * @param name the new display name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *  Changes the description of the user store.
   *
   * @param description the new description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   *  Changes user store properties.
   *
   * @param properties new user store properties.
   */
  public void setUserStoreProperties(Properties properties) {
    this.properties = properties;
  }

  /**
   *  Changes the set of login modules configured for the user store
   *
   * @param loginModules  new set of login modules.
   */
  public void setLoginModules(LoginModuleConfiguration[] loginModules) {
    this.loginModules = loginModules;
  }

  /**
   *  Changes the class name of the user context spi implementation.
   *
   * @param className  class name
   */
  public void setUserSpiClassName(String className) {
    this.userSpi = className;
  }

  /**
   *  Changes the class name of the group context spi implementation.
   *
   * @param className  class name
   */
  public void setGroupSpiClassName(String className) {
    this.groupSpi = className;
  }

  /**
   *  Changes the class name of the configuration editor for the user store
   *
   * @param editor  editor's class name
   */
  public void setConfigurationEditorClassName(String editor) {
    this.editor = editor;
  }

  /**
   *  Changes the user account used as anonymous in the user store.
   *
   * @param anonymous  the user name of the account.
   */
  public void setAnonymousUser(String anonymous) {
    this.anonymous = anonymous;
  }

  /**
   *  Notifies the wrapped configuration to clear its startup configurations. 
   */
  public void clearStartupConfiguration() {
    if (configuration == null) {
      return;
    }

    try {
      Method method = configuration.getClass().getMethod(
          "clearStartupConfiguration", null);

      if (method != null) {
        method.invoke(configuration, null);
      }
    } catch (SecurityException e) {
      handleException(e);
    } catch (NoSuchMethodException e) {
      handleException(e);
    } catch (IllegalArgumentException e) {
      handleException(e);
    } catch (IllegalAccessException e) {
      handleException(e);
    } catch (InvocationTargetException e) {
      handleException(e);
    }
  }

  /**
   *  In case of "clearStartupConfiguration" invocation fail 
   */
  private void handleException(Exception e) {
    //just do nothing
  };
}