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

/**
 * Defines trace locations for the authentication components.
 * 
 * @author Svetlana Stancheva
 * @version 7.10
 */
public interface AuthenticationTraces {
  
  public static final String ROOT_LOCATION = "com.sap.engine.services.security.authentication";
  public static final String LOGIN_CONTEXT_LOCATION = ROOT_LOCATION + ".logincontext";
  public static final String LOGIN_PROGRAMMATIC_LOCATION = ROOT_LOCATION + ".programmatic";
  public static final String LOGIN_PROGRAMMATIC_ALL_LOCATION = LOGIN_PROGRAMMATIC_LOCATION + ".severity_all";
  public static final String LOGIN_AUTHSCHEME_LOCATION = LOGIN_PROGRAMMATIC_LOCATION + ".authscheme";
  public static final String CALLBACK_HANDLER_LOCATION = ROOT_LOCATION + ".callbackhandler";
  public static final String LOGIN_MODULES_LOCATION = ROOT_LOCATION + ".loginmodule";
  public static final String LOGIN_MODULES_CERTIFICATE_LOCATION = LOGIN_MODULES_LOCATION + ".certificate";
  public static final String LOGIN_MODULES_TICKET_LOCATION = LOGIN_MODULES_LOCATION + ".ticket";
  public static final String LOGIN_MODULES_SPNEGO_LOCATION = LOGIN_MODULES_LOCATION + ".spnego";
  public static final String LOGIN_MODULES_P4_TICKET_LOCATION = LOGIN_MODULES_LOCATION + ".p4ticket";
  public static final String LOGON_APPLICATION_LOCATION = ROOT_LOCATION + ".logonapplication";
}
