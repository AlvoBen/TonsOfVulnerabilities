/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.trace;

import java.util.HashMap;

/**
 * Author: georgi-s
 * Date: 2005-7-21
 */
public class DebugInfo {
  public static HashMap<String, SessionDebug> sessionInfo = new HashMap<String, SessionDebug>();

  public static synchronized SessionDebug  get(String key) {
    SessionDebug sd= sessionInfo.get(key);
    if (sd == null) {
      sd = new SessionDebug(key);
      sessionInfo.put(key, sd);
    }
    return sd;
  }
}
