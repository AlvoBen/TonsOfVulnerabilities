/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.login;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 *  
 *
 *  @author Svetlana Stancheva
 *  @author Diana Berberova
 *  @version 6.40
 * 
 */
public class AuthenticationLog {
  
  public static Location location = null;
  public static Location locationTable = null;
  public static Category category = null;

  protected static final String LOGIN_OK = "LOGIN.OK";
  protected static final String LOGIN_FAILED = "LOGIN.FAILED";
  protected static final String LOGOUT_OK = "LOGOUT.OK";
  protected static final String LOGOUT_FAILED = "LOGOUT.FAILED";

  protected static final String PREFIX = "...";
  protected static final String EXCEPTION = "exception";
  protected static final String OK = "ok";

  protected static final int START_INDEX = 0;
  protected static final int NAME_INDEX = 3;
  protected static final int FLAG_INDEX = 72;
  protected static final int INITIALIZE_INDEX = 84;
  protected static final int LOGIN_INDEX = 96;
  protected static final int COMMIT_INDEX = 107;
  protected static final int CENTRAL_CHECKS_INDEX = 110;
  protected static final int ABORT_INDEX = 118;
  protected static final int DETAILS_INDEX = 129;
  protected static final int LOGOUT_INDEX = LOGIN_INDEX;
  protected static final int LOGOUT_DETAILS_INDEX = COMMIT_INDEX;

  private static final String NEWLINE = "\n";
  private static final String USER = "User: ";
  private static final String IP_ADDRESS = "IP Address: ";
  private static final String NOT_AVAILABLE = "N/A";
  private static final String AUTH_STACK = "Authentication Stack: ";
  private static final String LOGIN_LINE = "Login Module                                                               Flag        Initialize  Login      Commit     Abort      Details";
  private static final String LOGOUT_LINE = "Login Module                                                               Flag        Initialize  Logout     Details";
  private static final String TRUE = Boolean.TRUE.toString();

  public static final String AUTHENTICATION_CATEGORY = "Authentication";
  public static final String TABLE_AUTHENTICATION_LOCATION = AuthenticationTraces.LOGIN_CONTEXT_LOCATION + ".table";
  public static final String LOGIN_MODULE_DETAILS_MESSAGE = "_LoginModuleDetails";
  
  static {
    location = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);
    locationTable = Location.getLocation(TABLE_AUTHENTICATION_LOCATION);
    category = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);
  }

  public static final void log(int severity, boolean isLogin, String eventName, String userName, String ipAddress, String details, boolean centralChecksMade, String authStack, LoginModuleLoggingWrapperImpl[] loginModules) {
    int effectiveSeverity = category.getEffectiveSeverity();
    
    if (severity < effectiveSeverity) {
      return;  
    }

    String message = getEventInfo(isLogin, eventName, userName, ipAddress, details, centralChecksMade, authStack, loginModules);

    category.log(severity, locationTable, (Object)"", message);
  }
  
  public static final void trace(int severity, boolean isLogin, String eventName, String userName, String ipAddress, String details, boolean centralChecksMade, String authStack, LoginModuleLoggingWrapperImpl[] loginModules) {
    
    if (locationTable.beLogged(severity)) {
      String message = getEventInfo(isLogin, eventName, userName, ipAddress, details, centralChecksMade, authStack, loginModules);
      locationTable.logT(severity, message);
    }

  }
  
  public static final void trace(int severity, String message) {
    int effectiveSeverity = location.getEffectiveSeverity();
    
    if (severity < effectiveSeverity) {
      return;  
    }
    
    location.logT(severity, message);   
  }

  public static final void traceThrowable(int severity, String message, Exception e) {
    int effectiveSeverity = location.getEffectiveSeverity();
    
    if (severity < effectiveSeverity) {
      return;  
    }
    
    location.traceThrowableT(severity, message, e);   
  }

  private static String getEventInfo(boolean isLogin, String eventName, String userName, String ipAddress, String details, boolean centralChecksMade, String authStack, LoginModuleLoggingWrapperImpl[] loginModules) {
    StringBuffer info = new StringBuffer(200);

    if (eventName != null) {
      info.append(eventName);
    }

    info.append(NEWLINE);
    info.append(USER);
    if (userName != null) {
      info.append(userName);
    } else {
      info.append(NOT_AVAILABLE);
    }
    
    if (ipAddress != null) {
      info.append(NEWLINE);
      info.append(IP_ADDRESS);
      info.append(ipAddress);
    }

    if (authStack != null) {
      info.append(NEWLINE);
      info.append(AUTH_STACK);
      info.append(authStack);
    }

    info.append(NEWLINE);
    if ((loginModules != null) && loginModules.length > 0) {
      // title line
      info.append(NEWLINE);
      if (isLogin) {
        info.append(LOGIN_LINE);
      } else {
        info.append(LOGOUT_LINE);
      }

      for (int i = 0; i < loginModules.length; i++) {
        info.append(NEWLINE);
        info.append(String.valueOf(i + 1) + ". " + loginModules[i].getLogMessages());
      }

      if (centralChecksMade) {
        StringBuffer logMessages = new StringBuffer("Central Checks                                                                                                                      ");

        info.append(NEWLINE);
        if (details != null) {
          logMessages.replace(CENTRAL_CHECKS_INDEX, CENTRAL_CHECKS_INDEX + EXCEPTION.length(), EXCEPTION);
          logMessages.append(details);
        } else {
          logMessages.replace(CENTRAL_CHECKS_INDEX, CENTRAL_CHECKS_INDEX + TRUE.length(), TRUE);
        }
        info.append(logMessages);
      }
    } else {
      info.append(NEWLINE);
      info.append("The authentication stack does not contain any login modules.");
    }

    return info.toString();
  }
}
