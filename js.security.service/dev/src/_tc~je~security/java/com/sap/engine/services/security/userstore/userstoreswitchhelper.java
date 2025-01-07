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
package com.sap.engine.services.security.userstore;

import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.AuthorizationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.server.AuthenticationContextImpl;
import com.sap.engine.services.security.server.AuthorizationContextImpl;
import com.sap.engine.services.security.server.SecurityContextImpl;
/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public class UserStoreSwitchHelper {

  private SecurityContext security = null;

  public UserStoreSwitchHelper(SecurityContext security) {
    this.security = security;
  }

  public void onActiveUserStoreChanged() {
    throw new UnsupportedOperationException("Runtime switch of the user store is not supported any more");
  }

  private void updateAuthentication() {
    String[] policies = security.listPolicyConfigurations();
    AuthenticationContext authentication = null;

    for (int i = 0; i < policies.length; i++) {
      authentication = security.getPolicyConfigurationContext(policies[i]).getAuthenticationContext();

      if (authentication instanceof AuthenticationContextImpl) {
        ((AuthenticationContextImpl) authentication).update();
      }
    }
  }

  private void updateAuthorization() {
    String[] policies = security.listPolicyConfigurations();
    AuthorizationContext authorization = null;

    for (int i = 0; i < policies.length; i++) {
      authorization = security.getPolicyConfigurationContext(policies[i]).getAuthorizationContext();

      if (authorization instanceof AuthorizationContextImpl) {
        ((AuthorizationContextImpl) authorization).update();
      }
    }
  }

  private void updateLogger() {
    if (security instanceof SecurityContextImpl) {
      ((SecurityContextImpl) security).getLoginModuleHelper().update();
    }
  }

}