/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.userstore.emergency;

import java.util.Map;
import java.util.Properties;

import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;

/**
 * Login module configuration for use of the emergency user store.
 * It defines only one login module for username-password authentication.
 *
 * @version 6.30
 * @author  Ekaterina Zheleva
 */
public class EmergencyLoginModuleConfiguration implements LoginModuleConfiguration {

  private final Properties OPTIONS = new Properties();
  private final String[] AUTHENTICATION_MECHANISMS = new String[0];

  /**
   * Constructor is public.
   */
  public EmergencyLoginModuleConfiguration() {
  }

  /**
   *  Returns the description of the login module.
   *
   * @return  printable text.
   */
  public String getDescription() {
    return "Login module that verifies emergency users' passwords.";
  }

  /**
   *  Returns the display name of the login module.
   *
   * @return  display name.
   */
  public String getName() {
    return "EmergencyLoginModule";
  }

  /**
   *  Hints for common authentication mechanisms this login module is not suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getNotSuitableAuthenticationMechanisms() {
    return AUTHENTICATION_MECHANISMS;
  }

  /**
   *  Returns the class name of the login module.
   *
   * @return  class name.
   */
  public String getLoginModuleClassName() {
    return "com.sap.engine.services.security.server.jaas.EmergencyLoginModule";
  }

  /**
   *  Returns the options of the login module.
   *
   * @return  options.
   */
  public Map getOptions() {
    return OPTIONS;
  }

  /**
   *  Hints for common authentication mechanisms this login module is suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getSuitableAuthenticationMechanisms() {
    return AUTHENTICATION_MECHANISMS;
  }

  /**
   * Returns options editor for login modules that have no options.
   *
   * @return the class name of an options editor.
   */
  public String getOptionsEditor() {
    return "com.sap.engine.services.security.gui.userstore.stores.EmptyOptionsEditor";
  }
}
