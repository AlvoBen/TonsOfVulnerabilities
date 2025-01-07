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
package com.sap.engine.services.security.login.monitor;

public class PolicyConfigurationMonitor {
  private String name = null;

  public PolicyConfigurationMonitor(String name) {
    this.name = name;
  }

  public String getPolicyConfigurationName() {
    return name;
  }

  public int successLogonCount   = 0;
  public int totalSessionCount   = 0;
  public int tiemoutSessionCount = 0;
  public int logoffSessionCount  = 0;
  public int invalidSessionCount = 0;
  public int failedLogonCount    = 0;
}
