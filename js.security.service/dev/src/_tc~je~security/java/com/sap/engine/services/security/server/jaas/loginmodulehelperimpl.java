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
 
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.log.LogInterface;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.LoginModuleHelper;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.PasswordPrincipal;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.security.core.InternalUMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Does login module common work.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public class LoginModuleHelperImpl implements LoginModuleHelper {

  /* This key is put in the shared state when the refresh of the user info is updated in the user sote from the persistense. The value contains the user name.*/
  public static final String REFRESH_DONE = "sap.security.auth.refresh.done";

  /* This key is put in the shared state when the lock status is already checked. Its value contains the lock status. */
  protected static final String USER_LOCK_STATUS = "sap.security.auth.user.lock.status";

  /* This key is put in the shared state with value 'true' when logon statistics are already written. */
  protected static final String LOGON_STATISTICS_WRITTEN = "sap.security.auth.logon.statistics.written";

  /* This is put in the shared state when user account is checked for validity. */
  protected static final String ACCOUNT_EXPIRED = "sap.security.auth.user.account.expired";

  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);
  
  private final SecurityContextImpl securityContext;
  private UserContext userContext = null;

  private Subject subject;
  
  private final String FORCE_PASSWORD_CHANGE = "ume.logon.force_password_change_on_sso";
  private final String UME_SERVICE_NAME = "com.sap.security.core.ume.service";
 
  public LoginModuleHelperImpl(SecurityContext securityContext) {
    this.securityContext = (SecurityContextImpl) securityContext;
    update();
  }

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param userName - the name of the user whose lock status is being chacked
   * @param sharedState - the shared state of the login modules.
   *
   * @deprecated This is not supported starting from 7.10
   * @throws LoginException - if the user is locked or if no user with the given name is found in the active user store
   */
  public void checkUserLockStatus(String userName, Map sharedState) throws LoginException {
    checkUserLockStatus(userContext, getUserInfo(userName), sharedState);
  }

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param certificate - the certificate that identifies the user whose lock status is being chacked
   * @param sharedState - the shared state of the login modules.
   * @deprecated This is not supported starting from 7.10
   * @throws LoginException - if the user is locked or if no user with the given certificate is found in the active user store
   */
  public void checkUserLockStatus(X509Certificate certificate, Map sharedState) throws LoginException {
    checkUserLockStatus(userContext, getUserInfo(certificate), sharedState);
  }

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param userContext - the user context of the authentication user store.
   * @param userInfo - the user info of the user whose lock status is being chacked
   * @param sharedState - the shared state of the login modules.
   *
   * @throws LoginException - if the user is locked
   */
  public void checkUserLockStatus(UserContext userContext, UserInfo userInfo, Map sharedState) throws LoginException {
    try {
      AccessController.doPrivileged(new CheckAction().getAction(userContext, userInfo, sharedState));
    } catch (PrivilegedActionException e) {
      Exception exception = e.getException();
      if (exception instanceof LoginException) {
        throw (LoginException) exception;
      } else if (exception instanceof RuntimeException) {
        throw (RuntimeException) exception;
      } else {
        throw new SecurityException("Exception occurred when checking user lock status!", e);
      }
    }
  }

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param userName - the name of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks.
   * @return - true if the user password is not expired, false - otherwise
   * @deprecated This is not supported starting from 7.10
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable or if no user with the given name is found in the active user store.
   */
  public boolean changePasswordIfNeeded(String userName, CallbackHandler callbackHandler) throws LoginException {
    return changePasswordIfNeeded(userContext, getUserInfo(userName), callbackHandler);
  }

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param certificate - the certificate of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks.
   * @return - true if the user password is not expired, false - otherwise
   * @deprecated This is not supported starting from 7.10
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable or if no user with the given certificate is found in the active user store.
   */
  public boolean changePasswordIfNeeded(X509Certificate certificate, CallbackHandler callbackHandler) throws LoginException {
    return changePasswordIfNeeded(userContext, getUserInfo(certificate), callbackHandler);
  }

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param userContext - the user context of the authentication user store.
   * @param userInfo - the user info of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks.
   * @return - true if the user password is not expired, false - otherwise
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable.
   */
  public boolean changePasswordIfNeeded(UserContext userContext, UserInfo userInfo, CallbackHandler callbackHandler) throws LoginException {
    try {
      return ((Boolean) AccessController.doPrivileged(new CheckAction().getAction(userContext, userInfo, callbackHandler, this))).booleanValue();
    } catch (PrivilegedActionException e) {
      Exception exception = e.getException();
      if (exception instanceof LoginException) {
        throw (LoginException) exception;
      } else if (exception instanceof RuntimeException) {
        throw (RuntimeException) exception;
      } else {
        throw new BaseLoginException("New password is not accepted", e, LoginExceptionDetails.PASSWORD_EXPIRED);
      }
    }
  }

  /**
   *  Checks if the user account is valid according to the values of its VALID_FROM and VALID_TO parameters.
   *
   * @param userName - the name of the user.
   * @param sharedState - the shared state of the login modules.
   * @return  true if the account is not valid, false otherwise.
   * @deprecated This is not supported starting from 7.10
   * @throws LoginException - if some exception occurs in the process of verification.
   */
  public boolean isUserAccountExpired(String userName, Map sharedState) throws LoginException {
    return isUserAccountExpired(getUserInfo(userName), userContext, sharedState);
  }

  /**
   *  Checks if the user account is valid according to the values of its VALID_FROM and VALID_TO parameters.
   *
   * @param userInfo - the name of the user.
   * @param userContext - the user context of the authentication user store.
   * @param sharedState - the shared state of the login modules.
   * @return  true if the account is not valid, false otherwise.
   * @throws LoginException - if some exception occurs in the process of verification.
   */
  public boolean isUserAccountExpired(UserInfo userInfo, UserContext userContext, Map sharedState) throws LoginException {
    try {
      return ((Boolean) AccessController.doPrivileged(new CheckAction().getAction(userInfo, userContext, sharedState))).booleanValue();
    } catch (PrivilegedActionException e) {
      Exception exception = e.getException();
      if (exception instanceof LoginException) {
        throw (LoginException) exception;
      } else if (exception instanceof RuntimeException) {
        throw (RuntimeException) exception;
      } else {
        throw new SecurityException("Error occurred when checking if the account is expired.", e);
      }
    }
  }

  /**
   *  Refresh the specified user's entry in the user store cache.
   *
   * @param userName  the name of the user.
   *
   * @throws LoginException  if the refresh failed.
   */
  public void refreshUserInfo(String userName, Map sharedState) throws LoginException {
    try {
      AccessController.doPrivileged(new CheckAction().getAction(userContext, userName, sharedState));
    } catch (PrivilegedActionException e) {
      Exception exception = e.getException();
      if (exception instanceof LoginException) {
        throw (LoginException) exception;
      } else if (exception instanceof RuntimeException) {
        throw (RuntimeException) exception;
      } else {
        throw new SecurityException("Error in refresh of user info.", e);
      }
    }
  }

  /**
   *  Refresh the specified user's entry in the user store cache.
   *
   * @param userContext - the user context of the authentication user store.
   * @param userName  the name of the user.
   * @param sharedState - the shared state passed to the login modules.
   *
   * @throws LoginException  if the refresh failed.
   */
  public void refreshUserInfo(UserContext userContext, String userName, Map sharedState) throws LoginException {
    try {
      AccessController.doPrivileged(new CheckAction().getAction(userContext, userName, sharedState));
    } catch (PrivilegedActionException e) {
      Exception exception = e.getException();
      if (exception instanceof LoginException) {
        throw (LoginException) exception;
      } else if (exception instanceof RuntimeException) {
        throw (RuntimeException) exception;
      } else {
        throw new SecurityException("Error in refresh of user info.", e);
      }
    }
  }

  /**
   * Writes a message to the log sistem, using the category and location, specified in security service.
   *
   * @param severity - the log level of the message
   * @param message - the message to be logged
   */
  public void logMessage(byte severity, String message) {
    LOCATION.logT(severityConverter(severity), message);
  }

  /**
   * Logs an exception, using the category and location, specified in security service.
   *
   * @param severity - the log level
   * @param throwable - the exception to be logged
   */
  public void logThrowable(byte severity, Throwable throwable) {
    LOCATION.traceThrowableT(severityConverter(severity), "Exception on login: ", throwable);
  }

  /**
   * This method is for throwing exceptions if the user credentials are not
   * correct. The method logs a message and then throws a new
   * javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param message - the message to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwNewLoginException(String message) throws LoginException {
    LOCATION.logT(Severity.DEBUG, message);
    throwNewLoginException();
  }

  /**
   * This method is for throwing exceptions if the user credentials are not
   * correct. The method logs a message and then throws a new
   * javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param message - the message to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwNewLoginException(String message, byte cause) throws LoginException {
    LOCATION.logT(Severity.DEBUG, message);
    throwNewLoginException(cause);
  }

  /**
   * This method is for exceptions caused by the caller. The method logs the exception
   * and then throws a new javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param exception - the exception to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwUserLoginException(Exception exception) throws LoginException {
    if (LOCATION.beDebug()) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Exception on login: ", exception);
    }
    throwNewLoginException();
  }

  /**
   * This method is for exceptions caused by the caller. The method logs the exception
   * and then throws a new javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param exception - the exception to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwUserLoginException(Exception exception, byte cause) throws LoginException {
    if (LOCATION.beDebug()) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Exception on login: ", exception);
    }
    throwNewLoginException(cause);
  }

  /**
   * Called by com.sap.engine.interfaces.security.auth.AbstractLoginModule to
   * verify if the caller has permission for some actions.
   */
  public void checkPermission() {
    Restrictions.checkPermission(Restrictions.COMPONENT_AUTHENTICATION, Restrictions.RESTRICTION_SET_HELPER);
  }

  public void update() {
    this.userContext = SecurityContextImpl.getRoot().getUserStoreContext().getActiveUserStore().getUserContext();
  }

  public UserInfo getUserInfo(UserContext userContext, String userName) throws LoginException {
    UserInfo userInfo = null;

    if (userName == null) {
      throwNewLoginException("The received user name is null.", LoginExceptionDetails.USERNAME_IS_NOT_VALID);
    }

    try {
      userInfo = userContext.getUserInfo(userName);
    } catch (SecurityException e) {
      throwUserLoginException(e, LoginExceptionDetails.USERNAME_IS_NOT_VALID);
    }

    if (userInfo == null) {
      throwNewLoginException("No such user " + userName + " found in the userstore.", LoginExceptionDetails.USERNAME_IS_NOT_VALID);
    }

    return userInfo;

  }

  private UserInfo getUserInfo(String userName) throws LoginException {
    UserInfo userInfo = null;

    if (userName == null) {
      throwNewLoginException("The received user name is null.", LoginExceptionDetails.USERNAME_IS_NOT_VALID);
    }

    try {
      userInfo = userContext.getUserInfo(userName);
    } catch (SecurityException e) {
      throwUserLoginException(e, LoginExceptionDetails.USERNAME_IS_NOT_VALID);
    }

    if (userInfo == null) {
      throwNewLoginException("No such user " + userName + " found in the userstore.", LoginExceptionDetails.USERNAME_IS_NOT_VALID);
    }

    return userInfo;
  }

  private UserInfo getUserInfo(X509Certificate certificate) throws LoginException {
    UserInfo userInfo = null;

    try {
      userInfo = userContext.getUserInfo(certificate);
    } catch (SecurityException e) {
      throwUserLoginException(e, LoginExceptionDetails.NO_USER_MAPPED_TO_THIS_CERTIFICATE);
    }

    if (userInfo == null) {
      throwNewLoginException("No user with the given certificate found in the userstore.", LoginExceptionDetails.NO_USER_MAPPED_TO_THIS_CERTIFICATE);
    }

    return userInfo;

  }

  private int severityConverter(byte logLevel) {
    switch (logLevel) {
      case LogInterface.DEBUG: return Severity.DEBUG;
      case LogInterface.TRACE: return Severity.PATH;
      case LogInterface.INFO: return Severity.INFO;
      case LogInterface.NOTICE: return Severity.INFO;
      case LogInterface.WARNING: return Severity.WARNING;
      case LogInterface.ERROR: return Severity.ERROR;
      case LogInterface.CRITICAL: return Severity.FATAL;
      case LogInterface.ALERT: return Severity.FATAL;
      case LogInterface.EMERGENCY: return Severity.FATAL;
    }
    return Severity.NONE;
  }

  private void throwNewLoginException() throws LoginException {
    throw new BaseLoginException("Authentication did not succeed.");
  }

  private void throwNewLoginException(byte cause) throws LoginException {
    throw new BaseLoginException("Authentication did not succeed.", cause);
  }
  
  public void setSubject( Subject subject ) {
  	this.subject = subject;
  }
  
  public Subject getSubject( ) {
  	return subject;
  }
  
  /**
   * This method checks if password change should be forced in case of SSO
   * Password change is skipped only if isPasswordChangeForcedProperty is FALSE and none PasswordPrincipal is found
   *
   * @return true if change should be forced, false otherwise
   */
  public boolean isPasswordChangeForced( ) {
    
    boolean isPasswordChangeForcedProperty = true;

    try {
      isPasswordChangeForcedProperty = InternalUMFactory.getConfiguration().getBooleanDynamic(FORCE_PASSWORD_CHANGE, true);
     } catch( Exception propertyException ) {
       
       if (LOCATION.beDebug()) {
          LOCATION.debugT("Error on retrieve property " + FORCE_PASSWORD_CHANGE + ": default value TRUE will be used");
          LOCATION.traceThrowableT(Severity.DEBUG, propertyException.getLocalizedMessage(), propertyException);
        }
     }
     
    if( isPasswordChangeForcedProperty )  { 
      return true; 
    } else {	
    	// !!! password change should be really skipped only if NO password login is commited  
	   if( subject == null ) {
	   	 return false;
	   }
       Object[] principals = subject.getPrincipals().toArray();
	
	   for( int i = 0; i < principals.length; i++ ) {
	     if( principals[i] instanceof PasswordPrincipal ) { 
	       return true;  
	     }	
	   }
	   return false;
    }
  }

}
