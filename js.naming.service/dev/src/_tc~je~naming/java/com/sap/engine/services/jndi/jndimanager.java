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

import javax.naming.directory.*;

import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.Serializator;

import javax.naming.NamingException;

import com.sap.engine.services.jndi.persistentimpl.memory.JNDIMemoryImpl;
import com.sap.engine.services.jndi.cluster.SecurityBase;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.cache.CacheCommunicatorImpl;
import com.sap.engine.services.jndi.implserver.ServerCtxCrossObjectFactory;
import com.sap.engine.frame.container.ApplicationContainerContext;
import com.sap.engine.interfaces.cross.CrossObjectFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import java.util.Properties;


/**
 * This class starts and initializes JNDI with used persistense storage.
 * Check if JNDI runs for a first time in cluster, for first time in the current server, etc.
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class JNDIManager extends com.sap.engine.services.jndi.NamingManager {

	private final static Location LOG_LOCATION = Location.getLocation(JNDIManager.class);

  /**
   * Persistent storage instance
   */
  public static JNDIPersistentRepository db = null;
  /**
   * Reference to JNDIProxy object
   */
  private volatile JNDIProxy proxy;
  /**
   * Reference to CrossObjectFactory object
   */
  private CrossObjectFactory objFactoryImpl;
  /**
   * Instance of JNDI Core context
   */
  private ApplicationContainerContext containerCtx = null;
  private CacheCommunicatorImpl communicator = null;

  /**
   * Constructor
   *
   * @throws javax.naming.NamingException
   */
  public JNDIManager() {
  }

  static synchronized void init() {
    if (manager == null) {
      manager = new JNDIManager();
    }
  }

  /**
   * Starts JNDI, return JDNIProxy which encapsulate Context instance
   *
   * @throws NamingException
   */
  public JNDIProxy start() throws javax.naming.NamingException {
    try {
      /* If db is null => database is already open */
      if (this.db == null) {
        /* first initialization */
        // MEMORY implementation used
        this.db = new JNDIMemoryImpl();
        db.open();
      }

      // DB is already open on this machine, now check if this is first run in cluster
      JNDIHandle rootc = db.getRootContainer();
      JNDIHandle rooto = db.findObject(rootc, "root");
      JNDIHandle jndirootc = null;

      // initialize Information about this server
      if (!SecurityBase.WITHOUT_SECURITY) {
        SecurityBase x = SecurityBase.getServerInfo();

        if (x == null) {
          throw new NamingException("The JNDI Registry Service is not started.");
        }
      }

      if (rooto == null) {
        // running JNDI for the first time in cluster

        rooto = db.bindObject(rootc, "root", DirObject.getNewDirObject(new BasicAttributes(), null), Constants.NOT_REPLICATED_OPERATION);

        byte[] data = null;
        try {
          data = Serializator.toByteArray(new Properties());
        } catch (javax.naming.NamingException je) {
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.traceThrowableT(Severity.PATH, "", je);
          }
          data = new byte[0];
        }
        jndirootc = db.createContainer("", data, false);

        db.linkObjectToContainer(rooto, jndirootc, Constants.NOT_REPLICATED_OPERATION);
        ((JNDIMemoryImpl) db).setCommunicator(communicator);
      } else {
        /* Cluster already started */
        // init root section
        JNDIHandle tempr = db.findObject(rootc, "root");
        jndirootc = db.getLinkedContainer(tempr);
        ((JNDIMemoryImpl) db).setCommunicator(communicator);
      }

      this.proxy = new JNDIProxyImpl(this.db, rooto, jndirootc);
      this.objFactoryImpl = new ServerCtxCrossObjectFactory(this.db, rooto, jndirootc, true);
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("JNDIProxy initialized successfully.");
      }
      if (LOG_LOCATION.beDebug()) {
        LOG_LOCATION.debugT("Proxy field is INITIALIZED. Classloader of proxy class is [" + proxy.getClass().getClassLoader() + "] hashcode[" + proxy.hashCode() + "] instance[" + proxy + "] |||| Classloader of NamingManager class is [" + this.getClass().getClassLoader() + "] hashcode[" + this.hashCode() + "] instance[" + this + "]");
      }
      return this.proxy;
    } catch (java.rmi.RemoteException re) {
      if (LOG_LOCATION.beError()) {
        SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,re, "ASJ.jndi.000021", "Exception when building the internal structure of the naming system. Exception is: [{0}]. The JNDI Registry Service cannot be started",  new Object[] { re});
      }
      NamingException ne = new NamingException("RemoteException when starting the JNDI Registry Service.");
      ne.setRootCause(re);
      throw ne;
    } catch (Exception e) {
      if (LOG_LOCATION.beError()) {
        SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,e, "ASJ.jndi.000022", "Exception when building the internal structure of the naming system. Exception is: [{0}]. The JNDI Registry Service cannot be started",  new Object[] { e});
      }
      NamingException ne = new NamingException("Exception when starting the JNDI Registry Service.");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Stops JNDI, close factories, etc.
   *
   * @throws NamingException
   */
  public void stop() throws javax.naming.NamingException {
    // Stop Object Factories, and then persistent
    try {
      db = null;
      com.sap.engine.services.jndi.NamingManager.manager = null;
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception in resource releasing when stopping the JNDI Registry Service. Exception is: " + e.toString(), e);
      }
      NamingException ne = new NamingException("Exception in resource releasing when stopping the JNDI Registry Service. Exception is: ");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Return JNDIProxy built in the start method
   *
   * @return JNDIProxy
   * @throws NamingException
   */
  public JNDIProxy getProxy() throws javax.naming.NamingException {
    if (this.proxy != null) {
      return this.proxy;
    } else {
      if (LOG_LOCATION.beDebug()) {
        LOG_LOCATION.debugT("Proxy field is NULL, will throw exception. Classloader of NamingManager class is [" + this.getClass().getClassLoader() + "] hashcode[" + this.hashCode() + "] instance[" + this + "]");
      }
      throw new NamingException("The JNDI Registry Service is not started.");
    }
  }

  public CrossObjectFactory getObjectFactory() throws javax.naming.NamingException {
    if (this.objFactoryImpl != null) {
      return this.objFactoryImpl;
    } else {
      throw new NamingException("The JNDI Registry Service is not started.");
    }
  }

  /**
   * JNDI Frame sets ContainerContext for inner use
   *
   * @param containerCtx Container Context for JNDI service
   */
  public void setContainerContext(ApplicationContainerContext containerCtx) {
    this.containerCtx = containerCtx;
  }

  public void setCommunicator(CacheCommunicatorImpl cc) {
    this.communicator = cc;
  }

}

