/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.login;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.jvm.Capabilities;

/**
 *  Session monitor encapsulates methods to monitor or log information regarding state of
 * security sessions and thread association.
 *
 * @author  Stephan Zlatarev
 * @version 7.10
 */
class SessionMonitor {

  private final static Location TRACER = SecurityContext.TRACER;
  private static final boolean IN_SERVER = SystemProperties.getBoolean("server"); //$NON-NLS-1$
  private static final boolean HAS_VM_MONITORING;

  static {
    boolean hasVMMonitoring = false;
    try {
      hasVMMonitoring = Capabilities.hasVmMonitoring();
    } catch (NoClassDefFoundError e) { // $JL-EXC$ Setting HAS_VM_MONITORING to false is sufficient
    }
    HAS_VM_MONITORING = hasVMMonitoring;
  }

  /**
   *  Sets the current user name as associated to the current thread in SAPJVM.
   *
   * @param username the display name of the current user
   */
  static void setCurrentUser(String username) {
    ThreadWrapper.setUser(username);

    if (HAS_VM_MONITORING) {  
      try {
        com.sap.jvm.monitor.vm.VmInfo.setUser(username);
      } catch (NoClassDefFoundError e) { // VmInfo may not be present in client's Java VM $JL-EXC$
        if (IN_SERVER && TRACER.beError()) {
          SimpleLogger.traceThrowable(Severity.ERROR, TRACER, e, "ASJ.secsrv.000202", "Could not associate user {0} to the current thread", new Object[] {username});
        }
      }
    }
  }
}