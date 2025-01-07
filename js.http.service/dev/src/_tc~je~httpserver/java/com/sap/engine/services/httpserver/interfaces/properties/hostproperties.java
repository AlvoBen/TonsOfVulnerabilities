package com.sap.engine.services.httpserver.interfaces.properties;

/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

public interface HostProperties {
  public String getHostName();

  public boolean isKeepAliveEnabled();

  public boolean isList();

  public boolean isLogEnabled();

  public boolean isUseCache();

  public String getStartPage();

  public String getRootDir();

  public String[] getAliasNames();

  public String getAliasValue(String key);

  public boolean isApplicationAlias(String key);

  public boolean isApplicationAliasEnabled(String key);
}
