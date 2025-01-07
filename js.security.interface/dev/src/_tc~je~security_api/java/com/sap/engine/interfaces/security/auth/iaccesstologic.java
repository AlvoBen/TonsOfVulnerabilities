/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.auth;

import com.sap.security.api.IUser;
import com.sap.security.api.logon.IAuthScheme;

public interface IAccessToLogic {

  /**
   * Not used.
   * 
   * @deprecated
   */
  public static final String ENV_LOGONSERVLET         = "logonservlet";
  /**
   * Not used.
   * 
   * @deprecated
   */
  public static final String ENV_LOGONCERTSERVLET     = "logoncertservlet";
  /**
   * Not used.
   * 
   * @deprecated
   */
  public static final String ENV_LOGONCOMPONENT       = "logoncomponent";
  /**
   * Not used.
   * 
   * @deprecated
   */
  public static final String ENV_LOGONCERTCOMPONENT   = "logoncertcomponent";

  public String getContextURI();

  public IUser getActiveUser();

  public String getAlias(String alias);
  public String getAlias(String context, String event);
  public String getRequiredAuthScheme();
  public IAuthScheme[] getAuthSchemes();
  public boolean isAction(String s);

}