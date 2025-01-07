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
import java.util.Vector;
import java.util.Date;
import java.util.Iterator;

/**
 * Author: georgi-s
 * Date: 2005-7-21
 */
public class SessionDebug {
  Vector actions = new Vector();
  HashMap details = new HashMap();
  private String key;


  public SessionDebug(String key) {
    this.key = key;
  }



  public void addAction(String action) {
    Date date = new Date(System.currentTimeMillis());
    String t = date + "  |  " + action;
    actions.add(t);
    details.put(t, new Exception());
  }


  public String toString() {
    StringBuffer result = new StringBuffer("\n<");
    result.append(key);
    result.append(">\n");
    Iterator iterator = actions.iterator();
    while (iterator.hasNext()) {
      result.append("      ");
      result.append(iterator.next());
      result.append("\n");
    }
    return result.toString();
  }

  public Exception getDetailedStack(String key) {
    return (Exception) details.get(key);
  }
}
