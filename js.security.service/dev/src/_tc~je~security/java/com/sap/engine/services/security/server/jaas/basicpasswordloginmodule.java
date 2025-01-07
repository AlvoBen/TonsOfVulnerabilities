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
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.engine.services.security.login.FastLoginContext;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.PasswordPrincipal;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.security.auth.callback.*;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.resource.spi.security.PasswordCredential;
import java.util.Map;
import java.io.IOException;

public class BasicPasswordLoginModule extends AbstractLoginModule {

  private final static String ALIAS_LOGON_OPTION = "LogonWithAlias";
  private final static String TRUE = "true";
  private final static String STRICT_LOGIN = "StrictLogin";

  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_LOCATION + ".BasicPasswordLoginModule");

  private CallbackHandler callbackHandler = null;
  private Subject subject = null;
  private String name = null;
  private char[] password = null;
  private Map sharedState = null;
  private Map options = null;
  private UserContext userContext = null;
  private UserInfo user = null;
  private boolean successful;
  private boolean shouldBeIgnored;
  private boolean nameSet = false;
  private boolean isStrictLogin = false;
  private SecurityContext securityContext = null;

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    super.initialize(subject, callbackHandler, sharedState, options);

    this.callbackHandler = callbackHandler;
    this.subject = subject;
    this.sharedState = sharedState;
    this.options = options;
    this.successful = false;
    this.shouldBeIgnored = false;

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
      this.shouldBeIgnored = true;
      this.isStrictLogin = TRUE.equalsIgnoreCase((String) options.get(STRICT_LOGIN));
      
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Unsupported Callback. ", e);
      }
      
      if (isStrictLogin) {
        throw new BaseLoginException("Unable to authenticate user.", e);
      } else {
        return false;
      }
    } catch (IOException e) {
      throwUserLoginException(e, LoginExceptionDetails.IO_EXCEPTION);
    }

    name = nameCallback.getName();
    //name must be trimmed 
    password = passwordCallback.getPassword();
    passwordCallback.clearPassword();

    if( name==null || name.length()==0 ) {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("No user name provided.");
      }
      
      this.shouldBeIgnored = true;
      this.isStrictLogin = TRUE.equalsIgnoreCase((String) options.get(STRICT_LOGIN));

      if (isStrictLogin) {
        throw new BaseLoginException("Unable to authenticate user.");
      } else {
        return false;
      }
    } else {

      if (LOCATION.beDebug()) {
        LOCATION.debugT("Provided username is " + name + ".");
      }
      
      if ( password == null || password.length == 0 ) {
        
        if (LOCATION.beDebug()) {
          LOCATION.debugT("No password is provided.");
        }
        
        throwNewLoginException("No password is provided.", LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
      }
    }
     
    try {
	    if (TRUE.equalsIgnoreCase((String) options.get(ALIAS_LOGON_OPTION))) {
	      if (LOCATION.beDebug()) {
	        LOCATION.debugT("User tries to authenticate with alias " + name);
	      }
	
	      try {
	        user = userContext.getUserInfoByLogonAlias(name);
	      } catch (SecurityException e) {
	        throwUserLoginException(e, LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
	      }
	
	      //needed to write statistics about the user.
	      if (user != null) {
	        sharedState.put(LoginModuleHelperImpl.REFRESH_DONE, user.getName());
	      }
	    } else {
	      try {
	        refreshUserInfo(name);
	        user = userContext.getUserInfo(name);
	      } catch (SecurityException e) {
	        throwUserLoginException(e, LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
	      }
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
	      case UserInfo.CHECKPWD_OK: {
	    	if (LOCATION.beDebug()) {
	    	  LOCATION.debugT("The password for user " + name + " is correct.");
	    	}
	        break;
	      }
	      default: {
	    	if (LOCATION.beDebug()) {
		      LOCATION.debugT("Unknown password check result [" + passwordStatus + "].");
		    }
	      }
	    }
    } catch (BaseLoginException e) {
      
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Login failed!", e);
      }
      
      e.fillInStackTrace();
      throw e;
    }

    name = user.getName();

    if (sharedState.get(AbstractLoginModule.NAME) == null) {
      sharedState.put(AbstractLoginModule.NAME, name);
      nameSet = true;
      sharedState.put(FastLoginContext.USER_INFO, user);
    }
    
    if (LOCATION.beDebug()) {
      LOCATION.debugT("User " + name + " is authenticated with username and password.");
    }
    
    successful = true;
    return true;
  }

  public boolean commit() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        Principal principal = new PasswordPrincipal(name);
        principal.setAuthenticationMethod(Principal.AUTH_METHOD_PASSWORD);

        subject.getPrincipals().add(principal);
        subject.getPrivateCredentials().add(new PasswordCredential(name, password));
        
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Password principal" + principal + " is added to subject.");
          LOCATION.debugT("Password credentials are added to subject's private credentials.");
        }

        if (nameSet) {
          sharedState.put(AbstractLoginModule.PRINCIPAL, principal);
          
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Password principal is added to shared state.");
          }
        }
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
      //TODO Remove password principal and password credentials form the subject
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
        
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Empty subject for current user context.");
        }
        
        successful = false;
      }

      return true;
    } else {
      return false;
    }
  }

}
