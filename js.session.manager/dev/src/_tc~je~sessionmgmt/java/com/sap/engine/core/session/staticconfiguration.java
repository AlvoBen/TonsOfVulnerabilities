/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session;

import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.failover.FailoverConfig;
import com.sap.engine.session.logging.LogFilteringSystem;
import com.sap.engine.session.runtime.SessionLogFilter;

import java.io.File;

/**
 * Author: georgi-s
 * Date: 2005-6-3
 */
public class StaticConfiguration {
  protected static int usrPerScope = FailoverConfig.VM_LOCAL;

  protected static String fRoot = (new StringBuffer(".").append(File.separatorChar).append("..").append(File.separator).append("sessionFailover").append(File.separator)).toString();

  /**
   * value for Explicitly Enabled Failover 
   */
  private static boolean isFailoverEnable = false;

  /**
   * value for Explicitly Disabled Failover
   */
  private static boolean disableFailover = false;

  // register log filters
  static {
      // this filter will be used for filtering session logs
    LogFilteringSystem.registerFilter(new SessionLogFilter("RuntimeSessionFilter"));
  }

  public static int usrContextPersistentScope() {
    return usrPerScope;
  }

  /**
   * called in case of enabled failover with file storage
   * 
   * @return the path to the failover root directory (excluding the path to the current directory)
   */
  public static String fileSystemRoot() {
    return fRoot;
  }

  /**
   * @return  <code>true</code> if the failover for session management is explicitly enabled; 
   *          <code>false</code> otherwise.
   */
  public static boolean isFailoverEnable() {
    return isFailoverEnable;
  }

  /**
   * called only if the failover for session management is explicitly enabled
   * mark that the failover is enabled
   */
  protected static void enableFailover(){
    isFailoverEnable = true;
  }

  /**
   * called only if the failover for session management is explicitly disabled
   * mark that the failover is disabled
   */
  protected static void disableFailover(){
    disableFailover = true;
    SessionDomain.disableFailover = true;
  }

  /**
   * @return  <code>true</code> if the failover for session management is explicitly disabled; 
   *          <code>false</code> otherwise.
   */
  public static boolean isFailoverDisable(){
    return disableFailover;
  }
}
