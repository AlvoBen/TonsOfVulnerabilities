/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.auth;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;

import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;

/**
 * @author Krasimira Velikova
 * @version 7.1
 */
public class LoginContextFactory {
  
  private static SecurityContext ROOT_SECURITY_CONTEXT;
  
  public static LoginContext getLoginContext(String name, CallbackHandler callbackHandler) {
    return getLoginContext(name, null, callbackHandler);
  }

  public static LoginContext getLoginContext(String name, Subject subject, CallbackHandler callbackHandler) {
    SecurityContext secContext = ROOT_SECURITY_CONTEXT.getPolicyConfigurationContext(name);
    
    if (secContext == null) {
      throw new IllegalArgumentException("Missing security context " + name);
      //???
    }
    
    AuthenticationContext authContext = secContext.getAuthenticationContext(); 
    
    return authContext.getLoginContext(subject, callbackHandler);
  }

  /**
   * Sets the root of the security context. It can be set only once, if it is
   * already set does nothing.
   *
   * @param ctx the root security context
   */
  public static void setRootSecurityContext(SecurityContext ctx) {
    if (ROOT_SECURITY_CONTEXT == null) {
      ROOT_SECURITY_CONTEXT = ctx;
    }
  }
}
