/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.login;

import java.util.Hashtable;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.system.SystemLoginModule;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 *  Instance of javax.security.auth.login.Configuration in the envirnment of SAP J2EE Engine.
 * It is responsible to prvide initialized LoginModules to the LoginContext that is used.
 *
 * @see com.sap.engine.system.SystemLoginModule
 *
 * @author  Stephan Zlatarev
 * @author  Svetlana Stancheva
 * @version 6.30
 */
public class LoginContextConfiguration extends Configuration {

  /**
   *  Method getAppConfigurationEntry for this name returns an array of zero elements.
   */
  public static final String EMPTY_STACK = "empty";
  public static final AppConfigurationEntry[] EMPTY_CONFIGURATION_STACK = new AppConfigurationEntry[0];
  public static final String SYSTEM_LOGIN_MODULE = SystemLoginModule.class.getName();
  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);

  private boolean initialized = false;
  private SecurityContext root = null;
  private Hashtable options = null;

  /**
   *  Initializes the configuration.
   *
   * @param  root  the root security context provided by security service.
   */
  public LoginContextConfiguration(SecurityContext root) {
    this.root = root;
  }

  /**
   * Returns an array with one AppConfigurationEntry for configured instance of SystemLoginModule.
   *
   * @see javax.security.auth.login.Configuration#getAppConfigurationEntry(String)
   */
  public AppConfigurationEntry[] getAppConfigurationEntry(String context) {
    if (!initialized) {
      refresh();
    }

    if (EMPTY_STACK.equals(context)) {
      return EMPTY_CONFIGURATION_STACK;
    }

    SecurityContext configuration = null;

    try {
      configuration = root.getPolicyConfigurationContext(context);
    } catch (SecurityException se) {
      configuration = null;
    }

    if (configuration == null) {
      return null;
    }

    options = new Hashtable(11);
    options.put(SystemLoginModule.APP_CONFIG_ENTRIES, configuration.getAuthenticationContext());
    AppConfigurationEntry entry = new AppConfigurationEntry(SYSTEM_LOGIN_MODULE, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);

    return new AppConfigurationEntry[] {entry};
  }

  /**
   *  Does nothing
   *
   * @see javax.security.auth.login.Configuration#refresh()
   */
  public void refresh() {
    try {
      SystemLoginModule.setReference(root.getAuthenticationContext());
      initialized = true;
    } catch (Exception e) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, e.getLocalizedMessage(), e);
      }
    }
  }

}

