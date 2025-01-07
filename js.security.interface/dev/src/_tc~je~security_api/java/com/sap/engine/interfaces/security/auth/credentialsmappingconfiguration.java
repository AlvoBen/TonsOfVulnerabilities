/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.auth;

import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;

/**
 *  Credentials mapping configuration for a user mapping login module.
 * Each user is acting as himself to the other user store possibly with
 * different credentials.
 *
 *  The mapping login modules are used for JCA resource adapters.
 *
 * @author  Ekaterina Zheleva
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.AuthenticationContext
 */
public class CredentialsMappingConfiguration extends AppConfigurationEntry {


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
  public CredentialsMappingConfiguration(String module, AppConfigurationEntry.LoginModuleControlFlag flag, Map options) {
    super(module, flag, options);
    this.options = options;
  }


  /**
   *  Constructs the entry with options.
   *  Login module class name is:
   *    com.sap.engine.services.security.server.jaas.mapping.CredentialsMappingLoginModule
   *  Flag is REQUIRED.
   *
   * @param  options  options of the login module
   */
  public CredentialsMappingConfiguration(Map options) {
    super("com.sap.engine.services.security.server.jaas.mapping.CredentialsMappingLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
    this.options = options;
  }

  /**
   *  Returns the options of the login module.
   *
   * @return  options of the login module.
   */
  public Map getOptions() {
    return options;
  }

}

