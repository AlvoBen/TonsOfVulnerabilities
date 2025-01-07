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
package com.sap.engine.interfaces.security.auth;

import com.sap.engine.interfaces.security.SecuritySession;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

/**
 *  Extension for the standard security session. It is subject to change.
 *
 * @author Svetlana Stancheva
 * @version 6.40
 */
public interface SecuritySessionExtention extends SecuritySession {

  /**
   *  Invalidates the current security session.
   *
   * @param authStack  The authentication stack name of the application.
   * @param handler  The callback handler to be used in the logout process.
   *
   * @throws javax.security.auth.login.LoginException  If the invalidation is not successful.
   * @throws IllegalStateException  If the method is called from a remote client.
   */
  public void logout(String authStack, CallbackHandler handler) throws LoginException;

}
