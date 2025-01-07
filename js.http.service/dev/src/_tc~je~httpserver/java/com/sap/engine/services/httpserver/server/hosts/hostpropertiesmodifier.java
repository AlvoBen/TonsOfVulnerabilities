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
package com.sap.engine.services.httpserver.server.hosts;

import com.sap.engine.services.httpserver.interfaces.exceptions.IllegalHostArgumentException;
import com.sap.engine.frame.core.configuration.ConfigurationException;

public interface HostPropertiesModifier {

  public void setKeepAliveEnabled(boolean keepAliveEnabled) throws ConfigurationException;

  public void setList(boolean list) throws ConfigurationException;

  public void setLogEnabled(boolean enableLog) throws ConfigurationException;

  public void setUseCache(boolean useCache) throws ConfigurationException;

  public void setStartPage(String startPage) throws ConfigurationException;

  public void setRootDir(String vDir) throws ConfigurationException;

  public void addHttpAlias(String alias, String value) throws ConfigurationException, IllegalHostArgumentException;

  public void removeHttpAlias(String alias) throws ConfigurationException;

  public void changeHttpAlias(String alias, String value) throws ConfigurationException;

  /**
   * @deprecated use HostPropertiesModifier.enableApplicationAlias(String alias, boolean persistent)
   *             where you can specify to update or not the configuration.
   */
  public void enableApplicationAlias(String alias) throws ConfigurationException;

  public void enableApplicationAlias(String alias, boolean persistent) throws ConfigurationException;
  
  public void enableAllApplicationAliases(String appName, String[] aliasesCanonicalized, boolean persistent) throws ConfigurationException;

  public void disableApplicationAlias(String alias) throws ConfigurationException;
}
