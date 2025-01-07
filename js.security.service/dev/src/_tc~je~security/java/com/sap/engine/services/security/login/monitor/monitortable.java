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

import java.util.Hashtable;
import java.util.Enumeration;


public class MonitorTable {
  private static Hashtable table = new Hashtable(20);
  private static PolicyConfigurationMonitor aggregate = new PolicyConfigurationMonitor("AggregatedData");

  public static PolicyConfigurationMonitor getMonitor(String policyConfiguration) {
    String policy = convertToJMXString(policyConfiguration);
    PolicyConfigurationMonitor monitor = (PolicyConfigurationMonitor) table.get(policy);
    if (monitor == null) {
      synchronized(table) {
        monitor = (PolicyConfigurationMonitor) table.get(policy);
        if (monitor == null) {
          monitor = new PolicyConfigurationMonitor(policy);
          table.put(policy, monitor);
        }
      }
    }
    return monitor;
  }

  public static void removePolicyConfigurationMonitor(String policy) {
    table.remove(policy);
  }

  public static PolicyConfigurationMonitor getAggregatedMonitor() {
    return aggregate;
  }

  public static String[] getAuthenticationStacks() {
    Enumeration keys   = null;
    String[]    stacks = null;
    synchronized(table) {
      stacks = new String[table.size()];
      keys   = table.keys();
    }
    int next = 0;
    while (keys.hasMoreElements()) {
      stacks[next++] = (String) keys.nextElement();
    }
    return stacks;
  }

  private static String convertToJMXString(String s) {
    StringBuffer buffer = new StringBuffer();
    char[] chars = s.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if ((chars[i] != ',') &&
          (chars[i] != '=') &&
          (chars[i] != ':') &&
          (chars[i] != '"') &&
          (chars[i] != '*') &&
          (chars[i] != '?')) {
        buffer.append(chars[i]);
      }
    }
    return buffer.toString();
  }
}
