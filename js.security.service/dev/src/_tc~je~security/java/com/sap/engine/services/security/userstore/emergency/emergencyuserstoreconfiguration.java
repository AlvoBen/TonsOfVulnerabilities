/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.userstore.emergency;

import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import java.util.Properties;

/**
 * The implementation of UserStoreConfiguration for the Emergency UserStore user.
 *
 * @author  Ekaterina Zheleva
 * @version 6.30
 */
public class EmergencyUserStoreConfiguration implements UserStoreConfiguration {

  /**
   * Returns the name of the user store. Value is "Emergency User Store"
   */
  public static final String NAME = "Emergency User Store";

  static final long serialVersionUID = -7499342849900211698L;

  private Properties properties = null;
  private LoginModuleConfiguration[] loginModules = null;

  public EmergencyUserStoreConfiguration() {
    loginModules = new LoginModuleConfiguration[1];
    loginModules[0] = new EmergencyLoginModuleConfiguration();

    properties = new Properties();
  }

  /**
   *  Returns the description of the user store.
   *
   * @return  printable text.
   */
  public String getDescription() {
    return "Userstore for emergency situations.";
  }

  public String getAnonymousUser() {
    return NONE_ANONYMOUS_USER;
  }

  /**
   *  Returns the display name of the user store.
   *
   * @return  display name.
   */
  public String getName() {
    return NAME;
  }

  /**
   *  Returns the configured login modules for this user store.
   *
   * @return  an array of login module configurations.
   */
  public LoginModuleConfiguration[] getLoginModules() {
    return loginModules;
  }

  /**
   *  Returns the class name of the user context spi for the user store.
   *
   * @return  class name.
   */
  public String getUserSpiClassName() {
    return "com.sap.engine.services.security.userstore.emergency.EmergencyUserContextImpl";
  }

  /**
   *  Returns the class name of the group context spi for the user store.
   *
   * @return  class name.
   */
  public String getGroupSpiClassName() {
    return null;
  }

  /**
   *  Returns the properties of the user store.
   *
   * @return  the properties set for this userstore.
   */
  public Properties getUserStoreProperties() {
    return properties;
  }

  /**
   *  Returns the class name of the configuration editor for the user store.
   *
   * @return  class name.
   */
  public String getConfigurationEditorClassName() {
    return null;
  }
}
