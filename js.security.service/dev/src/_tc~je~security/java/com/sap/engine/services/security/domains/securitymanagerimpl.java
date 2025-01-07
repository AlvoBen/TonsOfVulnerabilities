/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.domains;

import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.HashSet;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * 
 * @version 6.30
 * @author  Stephan Zlatarev
 */
public class SecurityManagerImpl extends SecurityManager {

  public static final String IGNORE_ALL = "all";
  
  private static boolean skipAll = false;
  private static HashSet skip = new HashSet();

  private Object lock = new Object();
  private ThreadLocal loop = new ThreadLocal();
  
  private static final String SECURITY_MANAGER_LOCATION = "com.sap.engine.services.security.manager";
  private static final Location LOCATION = Location.getLocation(SECURITY_MANAGER_LOCATION);

  public void checkPermission(Permission permission) {
    if (enterLoopless()) {
      try {
        if (skip(permission)) {
          trace(permission, true);
        } else {
          trace(permission, false);
          super.checkPermission(permission);
        }
      } catch (AccessControlException ace) {
        if(LOCATION.beInfo()){
          LOCATION.traceThrowableT(Severity.INFO, "Access denied: {0}", new Object[] { permission.toString()}, ace);
        }
        throw ace;
      } finally {
        exitLoopless();
      }
    }
  }

  public void checkPermission(Permission permission, AccessControlContext context) {
    if (enterLoopless()) {
      try {
        if (skip(permission)) {
          trace(permission, true);
        } else {
          trace(permission, false);
          super.checkPermission(permission, context);
        }
      } catch (AccessControlException ace) {
        if(LOCATION.beInfo()){
          LOCATION.traceThrowableT(Severity.INFO, "Access denied: {0}", new Object[] { permission.toString()}, ace);
        }
        throw ace;
      } finally {
        exitLoopless();
      }
    }
  }

  public static void setIgnored(String permissionClass, boolean flag) {
    if (IGNORE_ALL.equalsIgnoreCase(permissionClass)) {
      skipAll = flag;
    } else {
      if (flag) {
        skip.add(permissionClass);
      } else {
        skip.remove(permissionClass);
      }
    }
  }

  public static String[] getIgnored() {
    if (skipAll) {
      return new String[] { IGNORE_ALL };
    } else {
      String[] result = new String[skip.size()];
      skip.toArray(result);
      return result;
    }
  }

  private final void trace(Permission permission, boolean skipped) {
    if (LOCATION.beInfo()) {
      if (skipped) {
        LOCATION.infoT("Skip permission: {0}", new Object[] { permission.toString()});
      } else {
        LOCATION.infoT("Check permission: {0}", new Object[] { permission.toString() });
      }
    }
      
    if (LOCATION.beDebug()) {
      Class previous = null;
      Class[] classes = super.getClassContext();
      for (int i = 0; i < classes.length; i++) {
        if (previous != classes[i]) {
          LOCATION.debugT("[{0}]: {1}", new Object[] { new Integer(i), classes[i].getName() });
          previous = classes[i];
        }
      }
    }
  }

  private final boolean skip(Permission permission) {
    if (skipAll) {
      return true;
    } else {
      return skip.contains(permission.getClass().getName());
    }
  }

  private boolean enterLoopless() {
    if (loop.get() == null) {
      loop.set(lock);
      return true;
    } else {
      return false;
    }
  }

  private void exitLoopless() {
    loop.set(null);
  }
}