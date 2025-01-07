/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security.auth;

import javax.security.auth.login.LoginContext;

/**
 *  Class used instead of LoginContext for delayed authentication when the
 * login context may not be used and its creation is expensive.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public interface LoginContextReference {

  /**
   *  Retrieves the instance of LoginContext
   *
   * @return  an instance of LoginContext
   */
  public LoginContext getLoginContext();

}
