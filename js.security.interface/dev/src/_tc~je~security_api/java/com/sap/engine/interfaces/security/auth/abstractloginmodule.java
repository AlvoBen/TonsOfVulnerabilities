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

import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;

import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.lang.ref.WeakReference;

/**
 * Does login module common work. Here is an example how it can be used:
 * <pre>
 *
 * public class TemplateLoginModule extends com.sap.engine.interfaces.security.auth.AbstractLoginModule {
 *
 *   public void initialize(javax.security.auth.Subject subject,
 *                          javax.security.auth.callback.CallbackHandler callbackHandler,
 *                          java.util.Map sharedState,
 *                          java.util.Map options) {
 *
 *     super.initialize (subject, callbackHandler, sharedState, options);
 *     ...
 *   }
 *
 *   public boolean login() throws javax.security.auth.login.LoginException {
 *      ...
 *       < Retrieve the user credentials via the callback handler. >
 *      ...
 *
 *       // After the user name is known, an update of the user info from the persistance should be made.
 *       // The operation must be done before the user credentils checks.
 *       // This method also checks the user name so that if user with such name does not exist in
 *       // the active user store, a java.lang.SecurityException is thrown.
 *       refreshUserInfo(<userName>);
 *       ...
 *
 *       try {
 *         < check the user credentials >
 *       } catch (Exception e) {
 * 	       throwUserLoginException(e);
 *       }
 *
 *     // Only one and exactly one login module from the stack must put the user name in the shared
 *     // state. This user name is considered to represent the authenticated user. For example if the
 *     // login is successful, method getRemoteUser() of the HTTP request will retrieve exactly this name.
 *     if (sharedState.get(AbstractLoginModule.NAME) == null) {
 *       sharedState.put(AbstractLoginModule.NAME, <userName>);
 *       nameSet = true;
 *     }
 *
 *     successful = true;
 *     return true;
 *   }
 *
 *   public boolean commit() throws javax.security.auth.login.LoginException {
 *     ...
 *     if (successful) {
 *       // The principals that are added to the subject should implement java.security.Principal.
 *       // You can use the class com.sap.engine.lib.security.Principal for this purpose.
 *       < add principals and credentials to the subject >
 *       ...
 *
 *       // If the login is successful, then the principal corresponding to the <userName> ( the
 *       // same user name that has been added to shared state ) must be added in the shared state
 *       // too. This principal is considered to be the main principal representing the user. For
 *       // example, this principal will be retrieved from method getUserPrincipal() of HTTP request.
 *       if (nameSet) {
 *         sharedState.put(AbstractLoginModule.PRINCIPAL, <userPrincipal>);
 *       }
 *     }
 *     ...
 *   }
 *
 *   public boolean abort() throws javax.security.auth.login.LoginException {
 *     ...
 *   }
 *
 *   public boolean logout() throws javax.security.auth.login.LoginException {
 *     ...
 * 	   < remove principals and credentials from subject >
 * 	   ...
 *   }
 *
 * </pre>
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public abstract class AbstractLoginModule implements LoginModule {

  /* The key under which the user principal is written into login module shared state */
  public static final String PRINCIPAL = "javax.security.auth.login.principal";

  /* The key under which the user name is written into login module shared state */
  public static final String NAME = "javax.security.auth.login.name";

  private static WeakReference helper = null;

  private Map sharedState = null;

  /**
   * Inicialization method that is used only in security service.
   *
   * @param helper - helping class that provides the functionality of the other methods.
   */
  public static void setLoginModuleHelper(LoginModuleHelper helper) {
    if (helper != null) {
      if (AbstractLoginModule.helper != null) {
        LoginModuleHelper moduleHelper = (LoginModuleHelper) AbstractLoginModule.helper.get();

        if (moduleHelper != null) {
          moduleHelper.checkPermission();
        }
      }

      AbstractLoginModule.helper = new WeakReference(helper);
    } else {
      AbstractLoginModule.helper = null;
    }
  }

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param userName - the name of the user whose lock status is being chacked
   * @throws javax.security.auth.login.LoginException - if the user is locked. The exception is always with message "User is locked."
   * @deprecated  This functionality is moved to the login context.
   */
  protected void checkUserLockStatus(String userName) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
       moduleHelper.checkUserLockStatus(userName, sharedState);
      }
    }
  }

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param certificate - the certificate that identifies the user whose lock status is being chacked
   * @throws javax.security.auth.login.LoginException - if the user is locked. The exception is always with message "User is locked."
   * @deprecated  This functionality is moved to the login context.
   */
  protected void checkUserLockStatus(X509Certificate certificate) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.checkUserLockStatus(certificate, sharedState);
      }
    }
  }

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param userContext - the user context of the active user store
   * @param userInfo - the user info of the user whose lock status is being chacked
   * @throws javax.security.auth.login.LoginException - if the user is locked. The exception is always with message "User is locked."
   * @deprecated  This functionality is moved to the login context.
   */
  protected void checkUserLockStatus(UserContext userContext, UserInfo userInfo) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.checkUserLockStatus(userContext, userInfo, sharedState);
      }
    }
  }

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param userName - the name of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks
   * @return - true if the user password is not expired, false - otherwise
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable.
   * @deprecated  This functionality is moved to the login context.
   */
  public boolean changePasswordIfNeeded(String userName, CallbackHandler callbackHandler) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        return moduleHelper.changePasswordIfNeeded(userName, callbackHandler);
      }
    }

    return false;
  }

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param certificate - the certificate of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks
   * @return - true if the user password is not expired, false - otherwise
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable.
   * @deprecated  This functionality is moved to the login context.
   */
  public boolean changePasswordIfNeeded(X509Certificate certificate, CallbackHandler callbackHandler) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        return moduleHelper.changePasswordIfNeeded(certificate, callbackHandler);
      }
    }

    return false;
  }

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param userContext - the user context of the active user store
   * @param userInfo - the user info of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks
   * @return - true if the user password is not expired, false - otherwise
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable.
   * @deprecated  This functionality is moved to the login context.
   */
  public boolean changePasswordIfNeeded(UserContext userContext, UserInfo userInfo, CallbackHandler callbackHandler) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        return moduleHelper.changePasswordIfNeeded(userContext, userInfo, callbackHandler);
      }
    }

    return false;
  }

  /**
   *  Checks if the user account is valid according to the values of its VALID_FROM and VALID_TO parameters.
   *
   * @param userName - the name of the user.
   * @return  true if the account is not valid, false otherwise.
   * @throws LoginException - if some exception occurs in the process of verification.
   * @deprecated  This functionality is moved to the login context.
   */
  public boolean isUserAccountExpired(String userName) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        return moduleHelper.isUserAccountExpired(userName, sharedState);
      }
    }

    return false;
  }

  /**
   *  Checks if the user account is valid according to the values of its VALID_FROM and VALID_TO parameters.
   *
   * @param userInfo - the name of the user.
   * @param userContext - the user context of the active user store
   * @return  true if the account is not valid, false otherwise.
   * @throws LoginException - if some exception occurs in the process of verification.
   * @deprecated  This functionality is moved to the login context.
   */
  public boolean isUserAccountExpired(UserInfo userInfo, UserContext userContext) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        return moduleHelper.isUserAccountExpired(userInfo, userContext, sharedState);
      }
    }

    return false;
  }

  /**
   *  Refresh the specified user's entry in the user store cache.
   *
   * @param userName  the name of the user.
   *
   * @throws LoginException  if the refresh failed.
   */
  public void refreshUserInfo(String userName) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.refreshUserInfo(userName, sharedState);
      }
    }
  }

  /**
   * Writes user logon statistics, such as logon date and logon count, to the database.
   *
   * @param successful - specifies if the login is successful
   * @param userName - the name of the user who has attempred to login himself.
   * @param timeStamp - the time when the user has attempted to login written in milliseconds
   * @param sharedState - the shared state map of the login module which has procesed the login
   * @deprecated  This functionality is moved to the login context.
   */
  protected void writeLogonStatistics(boolean successful, String userName, long timeStamp, Map sharedState) {
    
  }

  /**
   * Writes user logon statistics, such as logon date and logon count, to the database.
   *
   * @param successful - specifies if the login is successful
   * @param userInfo - the user info of the user who has attempred to login himself.
   * @param timeStamp - the time when the user has attempted to login written in milliseconds
   * @param sharedState - the shared state map of the login module which has procesed the login
   * @deprecated  This functionality is moved to the login context.
   */
  public void writeLogonStatistics(boolean successful, UserInfo userInfo, long timeStamp, Map sharedState) {

  }

  /**
   * Writes a message to the log sistem, using the category and location, specified in security service.
   *
   * @param severity - the log level of the message
   * @param message - the message to be logged
   */
  protected void logMessage(byte severity, String message) {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.logMessage(severity, message);
      }
    }
  }

  /**
   * Logs an exception, using the category and location, specified in security service.
   *
   * @param severity - the log level
   * @param throwable - the exception to be logged
   */
  protected void logThrowable(byte severity, Throwable throwable) {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.logThrowable(severity, throwable);
      }
    }
  }

  /**
   * This method is for throwing exceptions if the user credentials are not
   * correct. The method logs a message and then throws a new
   * javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param message - the message to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  protected void throwNewLoginException(String message) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.throwNewLoginException(message);
      }
    }
  }

  /**
   * This method is for throwing exceptions if the user credentials are not
   * correct. The method logs a message and then throws a new
   * javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param message - the message to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  protected void throwNewLoginException(String message, byte cause) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.throwNewLoginException(message, cause);
      }
    }
  }

  /**
   * This method is for exceptions caused by the caller. The method logs the exception
   * and then throws a new javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param exception - the exception to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  protected void throwUserLoginException(Exception exception) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.throwUserLoginException(exception);
      }
    }
  }

  /**
   * This method is for exceptions caused by the caller. The method logs the exception
   * and then throws a new javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param exception - the exception to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  protected void throwUserLoginException(Exception exception, byte cause) throws LoginException {
    if (helper != null) {
      LoginModuleHelper moduleHelper = (LoginModuleHelper) helper.get();

      if (moduleHelper != null) {
        moduleHelper.throwUserLoginException(exception, cause);
      }
    }
  }

  /**
   *  Initialises its shared state.
   */
  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    this.sharedState = sharedState;
  }

  public abstract boolean login() throws LoginException;

  public abstract boolean commit() throws LoginException;

  public abstract boolean abort() throws LoginException;

  public abstract boolean logout() throws LoginException;

}
