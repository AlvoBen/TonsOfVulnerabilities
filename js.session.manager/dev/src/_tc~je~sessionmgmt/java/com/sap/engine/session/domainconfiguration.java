/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session;

import java.util.HashMap;

/**
 * Author: Georgi-S
 * Date: 2005-4-5
 */
public class DomainConfiguration extends HashMap {

  public static final String SESSION_INVALIDATION_PERIOD = "session.invalidationPeriod";
  public static final String SESSION_INVALIDATION_DELAY = "session.invalidationDelay";
  public static final String SYNCHRONIZATION_STRATEGY = "session.synchronizationStrategy";

  public static final int SYNCHRONIZATION_STRATEGY_USER = 0;
  public static final int SYNCHRONIZATION_STRATEGY_DOMAIN = 1;

  //def session invalidation period in sec.
  public static int INVALIDATION_PERIOD = 60;
  //def session invalidation delay in sec.
  public static int INVALIDATION_DELAY = 60;

  public synchronized int intParameter(String key, int def) {
    Object value = get(key);
    if (value != null) {
      if (value instanceof Integer) {
        return (Integer) value;
      } else if (value instanceof String) {
         try {
           return Integer.parseInt((String) value);
         } catch (NumberFormatException ex) {
           return def;
         }
      }
    }
    return def;
  }

  public synchronized void add(String key, int value) {
    put(key, value);
  }

  public synchronized void setSessionInvalidationPeriod(int period) {
    add(SESSION_INVALIDATION_PERIOD, period);
  }

  public synchronized int sessionInvalidationPeriod() {
    return intParameter(SESSION_INVALIDATION_PERIOD, INVALIDATION_PERIOD);
  }

  public synchronized void setSessionInvalidationDelay(int delay) {
    add(SESSION_INVALIDATION_DELAY, delay);
  }

  public synchronized int sessionInvalidationDealy() {
    return intParameter(SESSION_INVALIDATION_DELAY, INVALIDATION_DELAY);
  }

  public synchronized DomainConfiguration copy() {
    DomainConfiguration config = new DomainConfiguration();
    config.putAll(this);
    return config;
  }


}
