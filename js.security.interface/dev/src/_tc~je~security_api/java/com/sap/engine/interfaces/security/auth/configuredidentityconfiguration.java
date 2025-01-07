/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.auth;

import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;

/**
 *  Configured identity configuration for a user mapping login module.
 * Each user is acting as one preconfigured user of the other user store.
 * The mapping is the same for all users.
 *
 *  The mapping login modules are used for JCA resource adapters.
 *
 * @author  Ekaterina Zheleva
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.AuthenticationContext
 */
public class ConfiguredIdentityConfiguration extends AppConfigurationEntry {

  /**
   *  Configured identity name identifier in the options.
   *
   *  Value is "Configured_Identity".
   */
  public static final String CONFIG_IDENTITY = "Configured_Identity";


  /**
   *  Key for the user store to be used by the configuration entry in the options.
   *
   *  Value is "User_Store".
   */
  public static final String USER_STORE = "User_Store";


  /**
   *  Managed Connection Factory ( MCF ) associated with the configuration entry.
   *
   *  Value is "Managed_Connection_Factory".
   */
  public static final String MCF = "Managed_Connection_Factory";


  /**
   *   Field containing the configured identity name.
   */
  private String configIdentity = null;


  /**
   *   Field containing the options of the login module.
   */
  private Map options = null;


  /**
   *  Constructs the entry with a login module class name, flag and options.
   *
   * @param  module   class name of the login module
   * @param  flag     OPTIONAL, REQUIRED, REQUISITE or SUFFICIENT
   * @param  options  options of the login module
   */
  public ConfiguredIdentityConfiguration(String module, AppConfigurationEntry.LoginModuleControlFlag flag, Map options) {
    super(module, flag, options);
    this.options = options;
    this.configIdentity = (String) options.get(CONFIG_IDENTITY);
  }


  /**
   *  Constructs the entry with options.
   *  Login module class name is:
   *    com.sap.engine.services.security.server.jaas.mapping.ConfiguredIdentityMappingLoginModule
   *  Flag is REQUIRED.
   *
   * @param  options  options of the login module
   */
  public ConfiguredIdentityConfiguration(Map options) {
    super("com.sap.engine.services.security.server.jaas.mapping.ConfiguredIdentityMappingLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
    this.options = options;
  }


  /**
   *  Returns the configured identity name for use of the login module.
   *
   * @return  the configured identity name for use of the login module.
   */
  public String getConfiguredIdentity() {
    return configIdentity;
  }


  /**
   *  Returns the options of the login module.
   *
   * @return  options of the login module.
   */
  public Map getOptions() {
    return options;
  }


  /**
   *  Sets the configured identity used by the login module.
   *
   * @param identity  a valid name of a user in the user store.
   */
  public void setConfiguredIdentity(String identity) {
    this.configIdentity = identity;
    getOptions().put(CONFIG_IDENTITY, configIdentity);
  }


}