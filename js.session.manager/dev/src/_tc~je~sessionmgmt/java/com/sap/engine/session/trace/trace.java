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

import com.sap.engine.core.Names;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Author: georgi-s
 * Date: May 3, 2004
 */
public class Trace {
  private static Location loc = Location.getLocation("com.sap.engine.session", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  public static boolean trace = false;//Boolean.getBoolean("SessionLog");
  public static String[] tabs = {"\t", "\t\t", "\t\t\t", "\t\t\t]\t"};

  public static void trace(String msg) {
    loc.logT(Severity.DEBUG, msg);
//    if (!trace) return;
//    try {
//      Date d =  new Date(System.currentTimeMillis());
//      FileOut.out.println(d + "|[Session Manger]:" + msg);
//    } catch (Exception e) {
//      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//    }
  }

  public static boolean beDebug() {
    return loc.beDebug();
  }

  public static void logException(Throwable e) {
    loc.traceThrowableT(Severity.DEBUG,  "", e);
//  if (!trace) return;
//    try {
//      FileOut.out.println(new Date(System.currentTimeMillis()));
//      e.printStackTrace(FileOut.out);
//    } catch (Exception e1) {
//      e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
//    }
  }

   public static void logError(String msg) {
    loc.logT(Severity.WARNING, msg);
//    try {
//      Date d =  new Date(System.currentTimeMillis());
//      FileOut.out.println(d + "|[Session Manger]:" + msg);
//    } catch (Exception e) {
//      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//    }
  }



  public static void logError(Throwable e) {
    loc.traceThrowableT(Severity.WARNING,  "", e);
//    try {
//      FileOut.out.println(new Date(System.currentTimeMillis()));
//      e.printStackTrace(FileOut.out);
//    } catch (Exception e1) {
//      e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
//    }
  }
}
