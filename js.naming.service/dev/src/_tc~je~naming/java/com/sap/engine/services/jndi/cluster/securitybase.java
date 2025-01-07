/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.cluster;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.interfaces.security.ServiceAccessPermission;

import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.AccessListObject;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LoggingUtilities;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;


/**
 * This class hold information for the local machine (SID) + reference to
 * Object Factory for remote objects + reference to security service.
 *
 * @author Panayot Dobrikov, Petio Petev
 * @version 4.00
 */
public class SecurityBase {

  private final static Location LOG_LOCATION = Location.getLocation(SecurityBase.class);

  /**
   * Flag for using security
   */
  public static boolean WITHOUT_SECURITY = true;

  /**
   * Stores security ID
   */
  private static String sid = null;
  /**
   * Stores the singleton
   */
  private static SecurityBase sinfo = null;
  //used for checking the current user
  private static ThreadSystem threadSystem = null;
  //security contexts
  private static SecurityContext securityContext;
  //naming permissions - 2
  public static final String ID_JNDI_ALL_OPERATIONS_PERMISSION = "jndi_all_operations";
  public static final String ID_JNDI_GET_INITIAL_CONTEXT_PERMISSION = "jndi_get_initial_context";

  public static final String PERMISSION_NAME = "naming";

  /**
   * Constructor when initializing as service
   *
   * @param sc service context passed to the service
   */
  public SecurityBase(ApplicationServiceContext sc) {
    this(false, null, null, null, sc);
  }

  /**
   * Constructor
   *
   * @param setFactory Determines if to open and deal with object factory
   * @param db Persistent repositiory to use
   * @param rc JNDI handle for remote container
   * @param cpoc JNDI handle for CPO
   */
  public SecurityBase(boolean setFactory, JNDIPersistentRepository db, JNDIHandle rc, JNDIHandle cpoc, ApplicationServiceContext sc) {
    threadSystem = sc.getCoreContext().getThreadSystem();
    int ID = sc.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
    sid = (Integer.toString(ID));
    if (!WITHOUT_SECURITY) {
      securityContext = JNDIFrame.namingSecurityContext;
    }
    sinfo = this;
  }

  /**
   * Determines if the operation is legal
   *
   * @return "true" if legal
   */
  public static synchronized boolean isOperationLegal(String permission) throws SecurityException {
    if (WITHOUT_SECURITY) {
      return true;
    }
    ThreadContext threadContext = threadSystem.getThreadContext();
    if (threadContext == null) {//a system call ...
      return true;
    }

    SecurityContextObject securityContextObject = ((SecurityContextObject) threadContext.getContextObject("security"));
    if (securityContextObject == null) {//security service trouble
      return true;
    }
    SecuritySession securitySession = securityContextObject.getSession();
    if (securitySession == null) {//security service trouble
      return true;
    }

    if (LOG_LOCATION.bePath()) {
      LOG_LOCATION.pathT("USER NAME: " + securitySession.getPrincipal().getName() + " | checking : " + permission);
    }
    
    try {
      IUser user = UMFactory.getUserFactory().getUserByUniqueName(securitySession.getPrincipal().getName());
      boolean result = user.hasPermission("naming", new ServiceAccessPermission(PERMISSION_NAME, permission));

      if (result) {
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Has permission to execute operation");
        }
      } else {
        SecurityException se = new SecurityException("Operation is not allowed for user: " + securitySession.getPrincipal().getName() + ". Check whether the permission roles are assigned correctly for this user.");
        if (LOG_LOCATION.bePath()) {
          String dcName = LoggingUtilities.getDcNameByClassLoader(securitySession.getClass().getClassLoader());
          SimpleLogger.trace(Severity.PATH, LOG_LOCATION, dcName, LoggingUtilities.getCsnComponentByDCName(dcName), "ASJ.jndi.000032", "Has NO permission to execute operation", se);
        }
        throw se;
      }

      return result;
    } catch (UMException e) {
      SecurityException se = new SecurityException("Invalid user:" + securitySession.getPrincipal().getName() + ". Root cause is:" + e.toString(), e);
      String dcName = LoggingUtilities.getDcNameByClassLoader(securitySession.getClass().getClassLoader());
      SimpleLogger.trace(Severity.PATH, LOG_LOCATION, dcName, LoggingUtilities.getCsnComponentByDCName(dcName), "ASJ.jndi.000033", "Cannot get InitialContext, unable to identify/authenticate user", se);
      throw se;
    }
  }

  public static synchronized AccessListObject[][] getPrincipals() {
    AccessListObject[][] result = new AccessListObject[2][];
    result[0] = SecurityBase.getPrincipals(SecurityBase.ID_JNDI_ALL_OPERATIONS_PERMISSION);
    result[1] = SecurityBase.getPrincipals(SecurityBase.ID_JNDI_GET_INITIAL_CONTEXT_PERMISSION);
    return result;
  }

  public static synchronized AccessListObject[] getPrincipals(String permission) {
    if (WITHOUT_SECURITY) {
      return new AccessListObject[0];
    }
    return new AccessListObject[0];
  }

  /**
   * Gets the server info
   *
   * @return The server info collected
   */
  public static SecurityBase getServerInfo() {
    return sinfo;
  }

  /**
   * Closes the server info
   */
  public static void close() {
    sid = null;
    sinfo = null;
  }

}
