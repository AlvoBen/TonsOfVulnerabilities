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
package com.sap.engine.services.jndi;

import javax.naming.Context;
import javax.naming.NoPermissionException;

import com.sap.engine.services.jndi.implserver.ServerContextImpl;
import com.sap.engine.services.jndi.implserver.ServerContextInface;
import com.sap.engine.services.jndi.implserver.ServerContextRedirectableImpl;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;

import java.lang.SecurityException;

import com.sap.engine.services.jndi.implclient.LoginHelper;
import com.sap.engine.services.jndi.cluster.SecurityBase;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Hashtable;

/**
 * This class implements JNDIProxy interface.
 * It is ClusterPortableObject used to obtain naming trough client
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class JNDIProxyImpl implements JNDIProxy {

	private final static Location LOG_LOCATION = Location.getLocation(JNDIProxyImpl.class);
	
  /**
   * Persistent storage connection
   */
  protected static JNDIPersistentRepository db = null;
  /**
   * Handle to jndi root object
   */
  protected static JNDIHandle rootObject;
  /**
   * Handle to jndi root container
   */
  protected static JNDIHandle jndiRootContainer;

  /**
   * Constructor
   *
   * @param db set persistent storage connection for later use
   * @param rootObject set handle to jndi root object for later use
   * @param jndiRootContainer set handle to jndi root container for later use
   * @param remote flags if there is no need of real proxy
   */
  public JNDIProxyImpl(JNDIPersistentRepository db, JNDIHandle rootObject, JNDIHandle jndiRootContainer) throws java.rmi.RemoteException {
    this.db = db;
    this.rootObject = rootObject;
    this.jndiRootContainer = jndiRootContainer;
  }

  /**
   * Returns new server context implementaiton
   *
   * @return ServerContextInface
   */
  public ServerContextInface getNewServerContext() {
    // remote user, domain true (all remote users get naming root => domain = true)
    return getNewServerContext(true, true);
  }

  /**
   * Returns new server context implementaiton
   *
   * @param remote flags if there is no need of real proxy
   * @return ServerContextInface
   */
  private ServerContextInface getNewServerContext(boolean remote, boolean onlyLookUpOperation, boolean redirectable) {
    try {
      if (redirectable) {
        return new ServerContextRedirectableImpl(db.getNewConnection(), rootObject, jndiRootContainer, remote, onlyLookUpOperation);
      } else {
        return new ServerContextImpl(db.getNewConnection(), rootObject, jndiRootContainer, remote, onlyLookUpOperation);
      }
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Error in obtaining new connection to the JNDI Registry Service.", e);
      }
      RuntimeException re = new RuntimeException("Error in obtaining new connection to the JNDI Registry Service.", e);
      throw re;
    }
  }
  
  public ServerContextInface getNewServerContext(boolean remote, boolean domain) {
    return checkPermissions(remote, domain, true);
  } 

  private ServerContextInface checkPermissions(boolean remote, boolean domain, boolean redirectable) {
    // security authorization check
    if (!remote) {
      //if (JNDIFrame.threadSystem.getThreadContext() == null || !domain) {
      // system thread => no security check is made
      // or application thread + application will use only it's context => no security checks
      try {
        return new ServerContextImpl(db.getNewConnection(), rootObject, jndiRootContainer, remote, false);
      } catch (Exception e) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Error in obtaining new connection to the JNDI Registry Service.", e);
        }
        RuntimeException re = new RuntimeException("Error in obtaining new connection to the JNDI Registry Service.", e);
        throw re;
      }
      //}
    }

    boolean onlyLookupAllowed = false;

    try {
      if (!SecurityBase.WITHOUT_SECURITY) {
        // check for jndi_get_initial_context permission
        SecurityBase.isOperationLegal(SecurityBase.ID_JNDI_GET_INITIAL_CONTEXT_PERMISSION);
          try {
            //check for jndi_all_operations permission
            //if the user has no permissions -> throws SecurityException 
            SecurityBase.isOperationLegal(SecurityBase.ID_JNDI_ALL_OPERATIONS_PERMISSION); 
          } catch (SecurityException se) {
            //the user can't perform all operations - only lookup
            onlyLookupAllowed = true;
            if (LOG_LOCATION.bePath()) {
              LOG_LOCATION.pathT("Only lookup permission is granted.");
            }
          }
        }
      return getNewServerContext(remote, onlyLookupAllowed, redirectable);
    } catch (RuntimeException re) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "Exception during permissions check.", re);
      throw re;
    }
  }
    
 public ServerContextInface getNewServerContext(boolean remote) {
    return getNewServerContext(remote, false);
  }

  public ServerContextInface getNewServerContext(boolean remote, String user, String pass, boolean beaLoggedIn) {
    // have to make login
    if (user != null && pass != null) {
      Hashtable env = new Hashtable();
      env.put(Context.SECURITY_PRINCIPAL, user);
      env.put(Context.SECURITY_CREDENTIALS, pass);
      LoginHelper loginContext = new LoginHelper();
      try {
        loginContext.serverSideLogin(env);
      } catch (NoPermissionException npe) {
        SecurityException se = new SecurityException("Cannot get InitialContext, operation is not allowed.", npe);
        LOG_LOCATION.traceThrowableT(Severity.PATH, "Cannot get InitialContext, operation is not allowed.", npe);
        throw se;
      }
    }

    return checkPermissions(remote, true, false); // here the authorization check is made!!!
  }

}

