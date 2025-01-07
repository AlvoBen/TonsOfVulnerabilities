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

/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class Locations {

  public static final Location SESSION_LOC = Location.getLocation("com.sap.engine.session", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  public static final Location RUNTIME_LOC  = Location.getLocation("com.sap.engine.session.runtime", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  public static final Location EXEC_LOC = Location.getLocation("com.sap.engine.session.exec", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  public static final Location USER_LOC  = Location.getLocation("com.sap.engine.session.usr", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  public static final Location LOGIN_LOC  = Location.getLocation("com.sap.engine.session.login", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);  


//  public static void fileDebug(Location location, String file) {
//    Properties logProps = new Properties();
//    logProps.setProperty("formatter[HumanReadable]", "TraceFormatter");
//    logProps.setProperty("formatter[HumanReadable].pattern", "%d [%p]: %t%l: %m");
//
//    logProps.setProperty("log[trc]", "FileLog");
//    logProps.setProperty("log[trc].formatter", "formatter[HumanReadable]");
//    logProps.setProperty("log[trc].pattern", file);
//    logProps.setProperty("log[trc].limit", "10000000");
//    logProps.setProperty("log[trc].cnt", "5");
//
//    logProps.setProperty("com.sap.engine.services.rmi_p4.logs", "log[trc]");
//    logProps.setProperty("com.sap.engine.services.rmi_p4.severity", "ERROR");
//    new PropertiesConfigurator(logProps).configure();//$JL-CONSOLE_LOG$

//  }

//  public static boolean bePath(Location loc, String etry) {
//    return false;
//  }

//   public static boolean beInfo(Location loc, String entry) {
//     return false;
//   }

  
}
