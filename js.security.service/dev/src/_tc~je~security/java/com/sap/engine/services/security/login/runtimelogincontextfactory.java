/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Feb 13, 2008 by I032049
 *   
 */
 
package com.sap.engine.services.security.login;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class RuntimeLoginContextFactory {

  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);

  public final static LoginContext getRuntimeLoginContext (String name, Subject subject, CallbackHandler callbackHandler, Configuration config) {
    return RuntimeLoginContextFactory.getRuntimeLoginContext(name, subject, callbackHandler, config, true);
  }  
  
  public final static LoginContext getRuntimeLoginContext (String name, Subject subject, CallbackHandler callbackHandler, Configuration config, boolean createSession) {
    LoginContext loginContext = null;
    try {
      loginContext = new FastLoginContext(name, subject, callbackHandler, config, createSession);
      LOCATION.logT(Severity.DEBUG, "Successfully get login context for runtime configuration {0}.", new Object[] {name});
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000200", "Cannot get login context for runtime configuration {0}.", new Object[] {name});
    }
    return loginContext;
  }  
}
