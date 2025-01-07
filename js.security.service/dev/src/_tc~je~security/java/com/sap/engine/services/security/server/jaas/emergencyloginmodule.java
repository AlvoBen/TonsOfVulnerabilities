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
package com.sap.engine.services.security.server.jaas;

import com.sap.engine.interfaces.security.userstore.context.*;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.LoginExceptionDetails;

import javax.security.auth.callback.*;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.resource.spi.security.PasswordCredential;
import java.util.Map;
import java.io.IOException;

/**
 * @deprecated - Not used any longer.
 */

public class EmergencyLoginModule extends AbstractLoginModule {

  private CallbackHandler callbackHandler = null;
  private Subject subject = null;
  private String name = null;
  private char[] password = null;
  private Map sharedState = null;
  private UserContext userContext = null;
  private UserInfo user = null;
  private boolean successful;
  private boolean shouldBeIgnored;
//  private boolean nameSet = false;
  private SecurityContext securityContext = null;

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    this.callbackHandler = callbackHandler;
    this.subject = subject;
    this.sharedState = sharedState;
    successful = false;
    shouldBeIgnored = false;

    securityContext = SecurityContextImpl.getRoot();
    userContext = securityContext.getUserStoreContext().getActiveUserStore().getUserContext();
    if (userContext == null) {
      throw new SecurityException("Unable to get user context.");
    }
  }

  public boolean login() throws LoginException {
    NameCallback nameCallback = new NameCallback("User name: ");
    PasswordCallback passwordCallback = new PasswordCallback("Password: ", true);

    try {
      callbackHandler.handle(new Callback[] {nameCallback, passwordCallback});
    } catch (UnsupportedCallbackException e) {
      shouldBeIgnored = true;
      return false;
    } catch (IOException e) {
      throwUserLoginException(e);
    }

    name = nameCallback.getName();
    password = passwordCallback.getPassword();

    passwordCallback.clearPassword();

    if (name == null) {
      return false;
    }

	  if (!userContext.isEmergencyUser(name)) {
		  throwNewLoginException("User " + name + " is not the emergency user.", LoginExceptionDetails.EMERGENCY_USER_IS_ACTIVE);
	  }

    try {
      user = userContext.getUserInfo(name);
    } catch (SecurityException e) {
      throwUserLoginException(e, LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
    }

    if (user == null) {
      throwNewLoginException("No such user " + name + " found in the userstore.", LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
    }

    int passwordStatus = user.checkPasswordExtended(password);

    switch (passwordStatus) {
      case UserInfo.CHECKPWD_WRONGPWD: {
        throwNewLoginException("Invalid password for user " + name + " .", LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
        break;
      }
      case UserInfo.CHECKPWD_NOPWD: {
        throwNewLoginException("User " + name + " has no password.", LoginExceptionDetails.NO_PASSWORD);
        break;
      }
      case UserInfo.CHECKPWD_PWDLOCKED: {
        throwNewLoginException("The password for user " + name + " is locked.", LoginExceptionDetails.PASSWORD_LOCKED);
        break;
      }
      case UserInfo.CHECKPWD_PWDEXPIRED: {
        throwNewLoginException("The password for user " + name + " is expired because it has not been used for quite long time.", LoginExceptionDetails.PASSWORD_NOT_USED_FOR_LONG_TIME);
        break;
      }
      default: {

      }
    }

//    checkUserLockStatus(userContext, user);

//    changePasswordIfNeeded(userContext, user, callbackHandler);

//    if (sharedState.get(AbstractLoginModule.NAME) == null) {
      sharedState.put(AbstractLoginModule.NAME, name);
//      nameSet = true;
//    }
    successful = true;
    return true;
  }

  public boolean commit() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        Principal principal = new Principal(name);

        subject.getPrincipals().add(principal);
        subject.getPrivateCredentials().add(new PasswordCredential(name, password));

//        if (nameSet) {
          sharedState.put(AbstractLoginModule.PRINCIPAL, principal);
//        }
      } else {
        name = null;
        password = null;
        user = null;
      }

      return true;
    } else {
      shouldBeIgnored = false;
      return false;
    }
  }

  public boolean abort() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        name = null;
        password = null;
        successful = false;
        user = null;
      }

      return true;
    } else {
      shouldBeIgnored = false;
      return false;
    }
  }

  public boolean logout() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        userContext.emptySubject(subject);
        successful = false;
      }

      return true;
    } else {
      return false;
    }
  }

}
