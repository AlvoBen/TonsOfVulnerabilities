/**
 * Copyright (c) 2002 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.interfaces.security.auth;

import javax.security.auth.login.LoginException;

/**
 *  Performs the authentication for remote clients.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public interface RemoteLoginContextInterface {

  /**
   *  Authenticates the user and, if successful, associate Principals and Credentials with the authenticated Subject.
   *
   * @throws LoginException - on authentication failure.
   */
  public void login() throws LoginException;

  /**
   *  Logouts the authenticated user.
   *
   * @throws LoginException - on logout failure.
   */
  public void logout() throws LoginException;

  /**
   *  Gets the server identifier for the server used in the remote connection.
   *
   * @return the server ID.
   */
  public int getServerId();
}
