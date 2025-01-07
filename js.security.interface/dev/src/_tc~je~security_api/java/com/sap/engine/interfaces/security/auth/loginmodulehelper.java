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
import javax.security.auth.callback.CallbackHandler;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Does login module common work.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public interface LoginModuleHelper {

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param userName - the name of the user whose lock status is being chacked
   * @param sharedState - the shared state of the login modules.
   * 
   * @deprecated - this method is not supported starting from 7.10
   * @throws javax.security.auth.login.LoginException - if the user is locked
   */
  public void checkUserLockStatus(String userName, Map sharedState) throws LoginException;

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param certificate - the certificate that identifies the user whose lock status is being chacked
   * @param sharedState - the shared state of the login modules.
   *
   * @deprecated - this method is not supported starting from 7.10 
   * @throws javax.security.auth.login.LoginException - if the user is locked
   */
  public void checkUserLockStatus(X509Certificate certificate, Map sharedState) throws LoginException;

  /**
   * Checks user lock status. If user is not locked, returns silently, otherwise throws LoginException.
   *
   * @param userContext - the user context of the active user store
   * @param userInfo - the user info of the user whose lock status is being chacked
   * @param sharedState - the shared state of the login modules.
   * 
   * @throws javax.security.auth.login.LoginException - if the user is locked
   */
  public void checkUserLockStatus(UserContext userContext, UserInfo userInfo, Map sharedState) throws LoginException;

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param userName - the name of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks.
   * @return - true if the user password is not expired, false - otherwise
   *
   * @deprecated - this method is not supported starting from 7.10
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable.
   */
  public boolean changePasswordIfNeeded(String userName, CallbackHandler callbackHandler) throws LoginException;

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param certificate - the certificate of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks.
   * @return - true if the user password is not expired, false - otherwise
   *
   * @deprecated - this method is not supported starting from 7.10
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable.
   */
  public boolean changePasswordIfNeeded(X509Certificate certificate, CallbackHandler callbackHandler) throws LoginException;

  /**
   * Checks if a password change is required and if so, requests the client to provide a new password.
   *
   * @param userContext - the user context of the active user store
   * @param userInfo - the user info of the user whose password validity is being chacked
   * @param callbackHandler - the callback handler to handle the callbacks.
   * @return - true if the user password is not expired, false - otherwise
   * @throws javax.security.auth.login.LoginException - if the supplied new password is not acceptable.
   */
  public boolean changePasswordIfNeeded(UserContext userContext, UserInfo userInfo, CallbackHandler callbackHandler) throws LoginException;

  /**
   *  Checks if the user account is valid according to the values of its VALID_FROM and VALID_TO parameters.
   *
   * @param userName - the name of the user.
   * @param sharedState - the shared state of the login modules.
   * @return  true if the account is not valid, false otherwise.
   *
   * @deprecated - this method is not supported starting from 7.10
   * @throws LoginException - if some exception occurs in the process of verification.
   */
  public boolean isUserAccountExpired(String userName, Map sharedState) throws LoginException;

    /**
   *  Checks if the user account is valid according to the values of its VALID_FROM and VALID_TO parameters.
   *
   * @param userInfo - the name of the user.
   * @param userContext - the user context of the active user store
   * @param sharedState - the shared state of the login modules.
   * @return  true if the account is not valid, false otherwise.
   * @throws LoginException - if some exception occurs in the process of verification.
   */
  public boolean isUserAccountExpired(UserInfo userInfo, UserContext userContext, Map sharedState) throws LoginException;

  /**
   *  Refresh the specified user's entry in the user store cache.
   *
   * @param userName  the name of the user.
   *
   * @throws LoginException  if the refresh failed.
   */
  public void refreshUserInfo(String userName, Map sharedState) throws LoginException;

  /**
   * Writes a message to the log sistem, using the category and location, specified in security service.
   *
   * @param severity - the log level of the message
   * @param message - the message to be logged
   */
  public void logMessage(byte severity, String message);

  /**
   * Logs an exception, using the category and location, specified in security service.
   *
   * @param severity - the log level
   * @param throwable - the exception to be logged
   */
  public void logThrowable(byte severity, Throwable throwable);

  /**
   * This method is for throwing exceptions if the user credentials are not
   * correct. The method logs a message and then throws a new
   * javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param message - the message to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwNewLoginException(String message) throws LoginException;

  /**
   * This method is for throwing exceptions if the user credentials are not
   * correct. The method logs a message and then throws a new
   * javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param message - the message to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwNewLoginException(String message, byte cause) throws LoginException;

  /**
   * This method is for exceptions caused by the caller. The method logs the exception
   * and then throws a new javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param exception - the exception to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwUserLoginException(Exception exception) throws LoginException;

  /**
   * This method is for exceptions caused by the caller. The method logs the exception
   * and then throws a new javax.security.auth.login.LoginException with message "Access Denied!".
   *
   * @param exception - the exception to be logged.
   * @throws LoginException - always throws LoginException with message "Access Denied!"
   */
  public void throwUserLoginException(Exception exception, byte cause) throws LoginException;

  /**
   * Called by com.sap.engine.interfaces.security.auth.AbstractLoginModule to
   * verify if the caller has permission for some actions.
   */
  public void checkPermission();

}
