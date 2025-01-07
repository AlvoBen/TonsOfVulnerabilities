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
package com.sap.engine.services.security.userstore.descriptor;

import java.util.*;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.DerivedConfiguration;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.security.Util;

/**
 * Configuration of a single user store. It holds a UserContext instance, configuration properties
 * and a set of login modules.
 *
 * @version 6.30
 * @author  Ekaterina Zheleva
 */
public class UserStoreConfigurationImpl implements UserStoreConfiguration {

  private boolean isActive;
  private String name;
  private String anonymous = null;
  private String description;
  private String editorClassName;
  private String groupContextClassName;
  private String userContextClassName;
  private Properties configuration = new Properties();
  private LoginModuleConfiguration[] modules;
  private String classLoaderName = null;

  public UserStoreConfigurationImpl(String userstoreName, Configuration container) {
    name = userstoreName;
    try {
      Configuration userstoreContainer = container.getSubConfiguration(Util.encode(userstoreName));
      classLoaderName = (String) userstoreContainer.getConfigEntry("classloader");
      description = (String) userstoreContainer.getConfigEntry("description");
      userContextClassName = (String) userstoreContainer.getConfigEntry("user-class-name");
      groupContextClassName = (String) userstoreContainer.getConfigEntry("group-class-name");
      editorClassName = (String) userstoreContainer.getConfigEntry("configuration-editor");

      if (userstoreContainer.existsConfigEntry("anonymous-user")) {
        anonymous = (String) userstoreContainer.getConfigEntry("anonymous-user");
      }

      readConfiguration(userstoreContainer.getSubConfiguration("configuration"));
      readLoginModules(userstoreContainer.getSubConfiguration("login-module"));
      readIsActive(container);

      if (anonymous == null) {
        anonymous = configuration.getProperty("anonymous-user");
      } else {
        configuration.setProperty("anonymous-user", anonymous);
      }
    } catch (Exception e) {
      throw new SecurityException("Cannot instanciate UserStoreConfiguration.", e);
    }
  }

  public String getConfigurationEditorClassName() {
    return editorClassName;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAnonymousUser() {
    return anonymous;
  }

  public LoginModuleConfiguration[] getLoginModules() {
    return modules;
  }

  public String getUserSpiClassName() {
    return userContextClassName;
  }

  public String getGroupSpiClassName() {
    return groupContextClassName;
  }

  public String getProperty(String key) {
    return configuration.getProperty(key);
  }

  public void setProperty(String key, String value) {
    if (configuration.getProperty(key) != null) {
      configuration.setProperty(key, value);
    }
  }

  public Properties getUserStoreProperties() {
    return configuration;
  }

  public String getClassLoaderName() {
    return classLoaderName;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  private void readIsActive(Configuration userstoresContainer) throws Exception {
    String activeUserStore = (String) userstoresContainer.getConfigEntry(com.sap.engine.services.security.userstore.persistent.UserStorePersistent.ACTIVE);
    isActive = activeUserStore.equals(name);
  }

  private void readConfiguration(Configuration propsContainer) throws Exception {
    String[] attributes = propsContainer.getAllConfigEntryNames();
    for (int i = 0; i < attributes.length; i++) {
      configuration.setProperty(attributes[i], (String) propsContainer.getConfigEntry(attributes[i]));
    }
  }

  private void readLoginModules(Configuration container) throws Exception {
    String[] allModules = container.getAllSubConfigurationNames();
    List listModules = new ArrayList();
    for (int i = 0; i < allModules.length; i++) {      
      String loginModuleName = allModules[i];
      
      Configuration loginModuleConfiguration = container.getSubConfiguration(loginModuleName);
      if ( (loginModuleConfiguration.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) ==  Configuration.CONFIG_TYPE_DERIVED ) {
        Configuration linkedConfig = ((DerivedConfiguration)loginModuleConfiguration).getLinkedConfiguration();
        if (linkedConfig == null) {
          continue;
        }
      }      
      
      LoginModuleConfigurationImpl loginModule = new LoginModuleConfigurationImpl(container, loginModuleName);
      listModules.add(loginModule);
    }
    modules = (LoginModuleConfigurationImpl[]) listModules.toArray(new LoginModuleConfigurationImpl[listModules.size()]);
  }

  public String toString() {
    return "configuration \r\n" + "name : " + name + "\r\n" + "UserSpiClass : " + userContextClassName + "\r\n" + "GroupSpiClass : " + groupContextClassName + "\r\n";
  }

}
