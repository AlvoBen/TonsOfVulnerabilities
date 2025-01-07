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

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.PasswordChangeCallback;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * PrivilegedExceptionAction for calling login modules.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public class CheckAction implements PrivilegedExceptionAction {

  protected static final byte LOCK_ACTION = 0;
  protected static final byte CHANGE_PASSWORD_ACTION = 1;
  protected static final byte REFRESH_ACTION = 2;
  protected static final byte EXPIRED_ACCOUNT_ACTION = 3;
  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);

  private UserContext userContext = null;
  private UserInfo userInfo = null;
  private CallbackHandler callbackHandler = null;
  private LoginModuleHelperImpl moduleHelper = null;
  private Map sharedState = null;
  private String userName = null;
  private byte type = -1;

  public CheckAction() {
  }

  public Object run() throws LoginException {
    switch (type) {
      case LOCK_ACTION: {
        checkUserLockStatus(userContext, userInfo);
        break;
      }
      case CHANGE_PASSWORD_ACTION: {
        return new Boolean(changePasswordIfNeeded(userContext, userInfo, callbackHandler, moduleHelper));
      }
      case REFRESH_ACTION: {
        refreshUserInfo();
        break;
      }
      case EXPIRED_ACCOUNT_ACTION: {
        return new Boolean(isUserAccountExpired(userInfo));
      }
      default: {
      }
    }

    return null;
  }

  protected CheckAction getAction(UserContext userContext, String userName, Map sharedState) {
    this.userContext = userContext;
    this.userName = userName;
    this.sharedState = sharedState;
    this.type = REFRESH_ACTION;
    return this;
  }

  protected CheckAction getAction(UserInfo userInfo, UserContext userContext, Map sharedState) {
    this.userContext = userContext;
    this.userInfo = userInfo;
    this.sharedState = sharedState;
    this.type = EXPIRED_ACCOUNT_ACTION;
    return this;
  }

  protected CheckAction getAction(UserContext userContext, UserInfo userInfo, Map sharedState) {
    this.userContext = userContext;
    this.userInfo = userInfo;
    this.sharedState = sharedState;
    this.type = LOCK_ACTION;
    return this;
  }

  protected CheckAction getAction(UserContext userContext, UserInfo userInfo, CallbackHandler callbackHandler, LoginModuleHelperImpl moduleHelper) {
    this.userContext = userContext;
    this.userInfo = userInfo;
    this.callbackHandler = callbackHandler;
    this.moduleHelper = moduleHelper;
    this.type = CHANGE_PASSWORD_ACTION;
    return this;
  }

  private boolean isUserAccountExpired(UserInfo userInfo) {
    boolean isExpired = false;
    Object status = null;

    if (sharedState != null) {
      status = sharedState.get(LoginModuleHelperImpl.ACCOUNT_EXPIRED);
    }

    if (status != null) {
      isExpired = ((Boolean) status).booleanValue();
    } else {
      long currentTime = System.currentTimeMillis();

      if (userContext.isUserPropertySupported(UserContext.PROPERTY_VALID_FROM_DATE, UserContext.OPERATION_READ)) {
        Object object = userInfo.readUserProperty(UserContext.PROPERTY_VALID_FROM_DATE);

        if (object != null) {
          long fromDate = ((Date) object).getTime();

          if (fromDate > currentTime) {
            isExpired = true;
          }
        }
      }

      if (userContext.isUserPropertySupported(UserContext.PROPERTY_VALID_TO_DATE, UserContext.OPERATION_READ)) {
        Object object = userInfo.readUserProperty(UserContext.PROPERTY_VALID_TO_DATE);

        if (object != null) {
          long toDate = ((Date) object).getTime();

          if (toDate < currentTime) {
            isExpired = true;
          }
        }
      }

      if (sharedState != null) {
        sharedState.put(LoginModuleHelperImpl.ACCOUNT_EXPIRED, new Boolean(isExpired));
      }
    }

    return isExpired;
  }

  private void checkUserLockStatus(UserContext userContext, UserInfo userInfo) throws LoginException {
    int lockStatus = -1;
    Object status = null;

    if (userContext.isUserPropertySupported(UserContext.PROPERTY_LOCK_STATUS, UserContext.OPERATION_READ)) {
      Object object = userInfo.readUserProperty(UserContext.PROPERTY_LOCK_STATUS);

      if (object != null) {
        lockStatus = ((Integer) object).intValue();

        if (lockStatus == UserContext.LOCKED_BY_ADMIN) {
          throw new BaseLoginException("User is locked.", LoginExceptionDetails.USER_IS_LOCKED);
        }
      }
    }
  }

  private boolean changePasswordIfNeeded(UserContext userContext, UserInfo userInfo, CallbackHandler callbackHandler, LoginModuleHelperImpl moduleHelper) throws LoginException {
  	if (!isPasswordChangeRequired(userContext, userInfo)) {
      return true;
    }
    
    //check if password change is needed in case of SSO
  	if( !moduleHelper.isPasswordChangeForced() ) {
  	  return true;	
  	}

  	TextOutputCallback messageCallback = new TextOutputCallback(TextOutputCallback.INFORMATION, "You must change your password");
    PasswordCallback passwordCallback = new PasswordCallback("Old Password: ", true);
    PasswordChangeCallback changePasswordCallback = new PasswordChangeCallback("New Password: ", true);
    PasswordChangeCallback changePasswordCallbackConfirm = new PasswordChangeCallback("Confirm New Password: ", true);

    try{
    	callbackHandler.handle(new Callback[] {messageCallback});
    } catch (UnsupportedCallbackException ex) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.INFO, "The current callback handler does not support TextOutputCallback.", ex);
      }
    } catch (IOException e) {
      throw new BaseLoginException("Exception occurred when handling the credentials for password change.", e, LoginExceptionDetails.IO_EXCEPTION);
    }

    try {
      callbackHandler.handle(new Callback[] {passwordCallback, changePasswordCallback, changePasswordCallbackConfirm});
    } catch (UnsupportedCallbackException e) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, "The current callback handler does not support com.sap.engine.lib.security.PasswordChangeCallback and the user will not be requested for password change on login even if his password has expired.", e);
      }
      return false;
    } catch (IOException e) {
      throw new BaseLoginException("Exception occurred when handling the credentials for password change.", e, LoginExceptionDetails.IO_EXCEPTION);
    }

    char[] password = passwordCallback.getPassword();
    char[] newpassword = changePasswordCallback.getPassword();
    char[] newpasswordconfirm = changePasswordCallbackConfirm.getPassword();

    passwordCallback.clearPassword();
    changePasswordCallback.clearPassword();
    changePasswordCallbackConfirm.clearPassword();

    if (password == null || password.length == 0) {
      throw new BaseLoginException("Missing Password", LoginExceptionDetails.CHANGE_PASSWORD_NO_PASSWORD);
		}
    
    if (newpassword == null || newpassword.length == 0) {
      throw new BaseLoginException("Missing new password", LoginExceptionDetails.CHANGE_PASSWORD_NO_NEW_PASSWORD);
 		}
    
    if (newpasswordconfirm == null || newpasswordconfirm.length == 0) {
      throw new BaseLoginException("Missing password confirmation", LoginExceptionDetails.CHANGE_PASSWORD_NO_CONFIRM_PASSWORD);
   	}
    
    if (!equals(newpassword, newpasswordconfirm)) {
      throw new BaseLoginException("New password and password confirmation are not identical", LoginExceptionDetails.CHANGE_PASSWORD_NO_IDENTICAL_PASSWORDS);
    }

    try {
			userInfo.setPassword(password, newpassword);
			return true;
    } catch (SecurityException e) {
      throw new BaseLoginException("Cannot set new password: " + e.getMessage(), e, LoginExceptionDetails.CHANGE_PASSWORD_FAILED);
    }
  }

  private boolean isPasswordChangeRequired(UserContext userContext, UserInfo userInfo) {
    if (userContext.isUserPropertySupported(UserContext.PROPERTY_FORCE_TO_CHANGE_PASSWORD, UserContext.OPERATION_READ) && userContext.isUserPropertySupported(UserContext.PROPERTY_LAST_CHANGED_PASSWORD_DATE, UserContext.OPERATION_READ)) {
      Long longTime = (Long) userInfo.readUserProperty(UserContext.PROPERTY_FORCE_TO_CHANGE_PASSWORD);

      if (longTime != null) {
        long time = longTime.longValue();

        if (time == Long.MAX_VALUE) {
          return false;
        } else {
          return true;
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  private void refreshUserInfo() {
    if (sharedState != null) {
      Object status = sharedState.get(LoginModuleHelperImpl.REFRESH_DONE);

      if (status == null) {
        userContext.refresh(userName);
        sharedState.put(LoginModuleHelperImpl.REFRESH_DONE, userName);
      }
    } else {
      userContext.refresh(userName);
    }
  }
  public boolean equals(char[] a1, char[] a2) {
    return Arrays.equals(a1, a2);
//    if (a1 == a2) {
//      return true;
//    }
//    try {
//      if (a1.length == a2.length) {
//        for (int i = 0; i < a1.length; i++) {
//          if (a1[i] != a2[i]) {
//            return false;
//          }
//        }
//        return true;
//      }
//    } catch (NullPointerException e) {
//    }
//    return false;
  }
}
