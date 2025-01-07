/**
 * Copyright (c) 2002 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.security.remote.authentication;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.Serializable;
import java.util.Properties;
import java.util.Map;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public class RemoteAppConfigurationEntry implements Serializable {

  static final long serialVersionUID = 9150184898356412446L;

  private int flag;
  private String className = null;
  private String loginModule = null;
  private Properties map = null;

  public RemoteAppConfigurationEntry() {
  }

  public RemoteAppConfigurationEntry(AppConfigurationEntry entry, String loginModuleName) {
    className = entry.getLoginModuleName();
    loginModule = loginModuleName;

    map = new Properties();
    map.putAll(entry.getOptions());

    if (entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL) {
      flag = 0;
    } else if (entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
      flag = 1;
    } else if (entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
      flag = 2;
    } else if (entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) {
      flag = 3;
    }
  }

  public AppConfigurationEntry getAppConfigurationEntry() {
    return new AppConfigurationEntry(getLoginModuleName(), getControlFlag(), getOptions());
  }

  public String getClassName() {
    return className;
  }

  public String getLoginModuleName() {
    return loginModule;
  }

  public AppConfigurationEntry.LoginModuleControlFlag getControlFlag() {
    switch (flag) {
      case 0: {
        return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
      }
      case 1: {
        return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
      }
      case 2: {
        return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
      }
      case 3: {
        return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
      }
    }

    return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
  }

  public Map getOptions() {
    return map;
  }

}