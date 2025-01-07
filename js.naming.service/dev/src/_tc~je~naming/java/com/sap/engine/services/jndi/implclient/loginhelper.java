/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.jndi.implclient;

import com.sap.engine.services.jndi.NamingCallbackHandler;
import com.sap.engine.services.jndi.JNDIFrame;

import javax.naming.NoPermissionException;

import com.sap.engine.interfaces.cross.RemoteBroker;
import com.sap.engine.interfaces.cross.RemoteEnvironment;
import com.sap.engine.interfaces.security.auth.RemoteLoginContextInterface;
import com.sap.engine.interfaces.security.auth.RemoteLoginContextFactory;
import com.sap.engine.boot.SystemProperties;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.LoginContext;
import javax.naming.Context;
import java.util.Hashtable;
import java.util.Enumeration;

/*
 * A helper class for accessing com.sap.engine.services.security.remote.login.RemoteLoginContext
 *
 * @author Elitsa Pancheva
 * @version 6.30
 */

public class LoginHelper {

  private final static Location LOG_LOCATION = Location.getLocation(LoginHelper.class);

  private RemoteLoginContextInterface loginContext;
  private LoginContext serverLoginContext;

  private static final String SECURITY_POLICY_CONFIGURATION = "sap.security.policy.configuration";
  private static final String NAMING_SERVICE_POLICY_CONFIGURATION = "service.naming";


  public LoginHelper() {

  }

  public void serverSideLogin(Hashtable env) throws NoPermissionException {
    if (JNDIFrame.loginContext != null) {
      try {
        String user = (String) env.get(Context.SECURITY_PRINCIPAL);
        String pass = (String) env.get(Context.SECURITY_CREDENTIALS);

        if (user == null && pass == null) {
          user = SystemProperties.getProperty("_" + Context.SECURITY_PRINCIPAL);
          pass = SystemProperties.getProperty("_" + Context.SECURITY_CREDENTIALS);
        }

        if ((JNDIFrame.loginContext != null) && (!checkForNullCredentials(env))) {
          serverLoginContext = JNDIFrame.loginContext.getLoginContext(null, getCallbackHandler(env));
          serverLoginContext.login();
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("The user is successfully authenticated. Client information: security principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".");
          }
        }
      } catch (LoginException le) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getInitialContext operation. Wrong security principal/credentials.", le);
        }
        NoPermissionException npe = new NoPermissionException("Exception during getInitialContext operation. Wrong security principal/credentials.");
        npe.setRootCause(le);
        throw npe;
      }
    }
  }

  public void clientSideLogin(Hashtable env, RemoteBroker broker) throws LoginException {
    if (!checkForNullCredentials(env)) {

      loginContext = new com.sap.engine.services.security.remote.login.RemoteLoginContext(getCallbackHandler(env), getPolicyConfiguration(env), broker);
      loginContext.login();
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("The user is successfully authenticated. Client information: security principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".");
      }
    }
  }

  private CallbackHandler getCallbackHandler(Hashtable env) {
    Object callbackHandler = env.get(RemoteEnvironment.SECURITY_CALLBACK_HANDLER);
    if (callbackHandler != null && callbackHandler instanceof CallbackHandler) {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Custom security callback handler " + callbackHandler.toString() + " is specified in the jndi environment => naming service will use it in login operation.");
      }
      return (CallbackHandler) callbackHandler;
    } else {
      return new NamingCallbackHandler(env);
    }
  }

  private String getPolicyConfiguration(Hashtable env) {
    Object policyCfg = env.get(SECURITY_POLICY_CONFIGURATION);
    if (policyCfg != null && policyCfg instanceof String) {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Custom security policy configuration " + policyCfg + "  is specified in the jndi environment => naming service will use it in login operation.", new Object[]{policyCfg});
      }
      return (String) policyCfg;
    } else {
      return NAMING_SERVICE_POLICY_CONFIGURATION;
    }
  }

  public void serverSideLogin(Hashtable env, RemoteBroker broker) throws LoginException {

    if (!checkForNullCredentials(env)) {
      loginContext = RemoteLoginContextFactory.getFactory().getRemoteLoginContext(getCallbackHandler(env), getPolicyConfiguration(env), broker);
      loginContext.login();
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("The user is successfully authenticated. Client information: security principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".");
      }
    }
  }

  public void logout() throws NoPermissionException {
    try {
      if (loginContext != null) {
        loginContext.logout();
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("The user is successfully logged out.");
        }
      } else if (serverLoginContext != null) {
        serverLoginContext.logout();
      }
    } catch (LoginException le) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Cannot log out the user.", le);
      }
      NoPermissionException npe = new NoPermissionException("Cannot log out the user.");
      npe.setRootCause(le);
      throw npe;
    }
  }

  private boolean checkForNullCredentials(Hashtable env) { // returns true if there are no security credentials and false otherwise
    if (env.get(RemoteEnvironment.SECURITY_CALLBACK_HANDLER) != null) {
      return false;
    }

    if (env.get(Context.SECURITY_PRINCIPAL) == null && env.get(Context.SECURITY_CREDENTIALS) == null) {
      String key = null;
      for (Enumeration properties = env.keys(); properties.hasMoreElements();) {
        key = (String) properties.nextElement();
        if (key.startsWith(NamingCallbackHandler.SECURITY_CREDENTIAL) && env.get(key) != null) {
          return false;
        }
      }
      return true;
    }

    return false;
  }


}
