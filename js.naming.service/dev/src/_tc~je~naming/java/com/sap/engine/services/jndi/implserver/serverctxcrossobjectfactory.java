/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.jndi.implserver;

import com.sap.engine.interfaces.cross.CrossObjectFactory;
import com.sap.engine.services.jndi.cluster.SecurityBase;

import java.lang.SecurityException;

import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.JNDIFrame;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/*
*
*
* @author Elitsa-P
* @version 6.30
*/
public class ServerCtxCrossObjectFactory implements CrossObjectFactory {

	private final static Location LOG_LOCATION = Location.getLocation(ServerCtxCrossObjectFactory.class);

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
   * Flags the usage of JNDIException
   */
  private boolean remote;

  /**
   * Constructor
   *
   * @param db set persistent storage connection for later use
   * @param rootObject set handle to jndi root object for later use
   * @param jndiRootContainer set handle to jndi root container for later use
   * @param remote flags if there is no need of real proxy
   */
  public ServerCtxCrossObjectFactory(JNDIPersistentRepository db, JNDIHandle rootObject, JNDIHandle jndiRootContainer, boolean remote) throws java.rmi.RemoteException {
    //    super(remote);
    this.db = db;
    this.rootObject = rootObject;
    this.jndiRootContainer = jndiRootContainer;
    this.remote = remote;
  }

  public Object getObject(String s) {
    boolean onlyLookupAllowed = false;

    try {
      if (!SecurityBase.WITHOUT_SECURITY) {
        // check for jndi_all_operations permission
        if (SecurityBase.isOperationLegal(SecurityBase.ID_JNDI_GET_INITIAL_CONTEXT_PERMISSION)) {

          if (!SecurityBase.isOperationLegal(SecurityBase.ID_JNDI_ALL_OPERATIONS_PERMISSION)) {
            onlyLookupAllowed = true;
          }

        } else {
          SecurityException se = new SecurityException("Cannot get InitialContext, operation is not allowed.");
          throw se;
        }
      }
      return new ServerContextRedirectableImpl(db.getNewConnection(), rootObject, jndiRootContainer, true, onlyLookupAllowed);
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      RuntimeException re = new RuntimeException("Error in obtaining new connection to the JNDI Registry Service.", e);
      throw re;
    }

  }

}
