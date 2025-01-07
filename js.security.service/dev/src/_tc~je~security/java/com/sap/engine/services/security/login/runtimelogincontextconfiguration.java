/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Feb 13, 2008 by I032049
 *   
 */
 
package com.sap.engine.services.security.login;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class RuntimeLoginContextConfiguration extends Configuration {

  private Map<String, AppConfigurationEntry[]> configurations = null;
  
  public RuntimeLoginContextConfiguration() {
    this.configurations = new HashMap<String, AppConfigurationEntry[]>();
  }
  
  public RuntimeLoginContextConfiguration(Map<String, AppConfigurationEntry[]> configurations) {
    this.configurations = configurations;
  }
  
  @Override
  public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
    return configurations.get(name);
  }

  @Override
  public void refresh() {
  }
  
  public void addConfiguration (String name, AppConfigurationEntry[] entries) {
    configurations.put(name, entries);
  }
  
  public void removeConfiguration (String name) {
    configurations.remove(name);
  }  
}
