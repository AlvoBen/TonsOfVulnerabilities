/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.session;

import com.sap.tc.logging.Severity;
import com.sap.engine.session.trace.Locations;

import java.util.Iterator;
import java.util.HashMap;


/**
 * The <code>SessioContextFactory</code> class is the user interface for
 * getting <code>SessionContext</code> instances.
 * 
 * @author georgi-s
 *  
 */
public abstract class SessionContextFactory {

  private static SessionContextFactory instance = null;

  private final HashMap<String, SessionContext> contexts = new HashMap<String, SessionContext>(2);

  protected SessionContextFactory() {
    setInstance(this);
  }
  /**
   * This method is used from session management core implementation to set
   * the factory instance.
   *
   * @param inst
   *            <code>SessionContextFactory</code> instance
   */
  protected static synchronized void setInstance(SessionContextFactory inst) {
    if (instance == null) {
      if (Locations.SESSION_LOC.beDebug()) {
        Locations.SESSION_LOC.debugT("SessionContextFactory initialization:" + inst);
      }
      instance = inst;
    } else {
      Locations.SESSION_LOC.traceThrowableT(Severity.INFO, "Trying to override SessionContextFactory implementation",
              new IllegalStateException());
    }
  }

  /**
   * Returns an instance of the session context factory.
   *
   * @return instance of the <code>SessionContextFactory</code>
   * @throws IllegalStateException
   *             if the session manager is not started.
   */
  public static SessionContextFactory getInstance() {
    if (instance != null) {
      return instance;
    }
    IllegalStateException is = new IllegalStateException(
        "The session management is not configured");
    Locations.SESSION_LOC.traceThrowableT(Severity.WARNING, "", is);
    throw is;
  }

  /**
   * Returns an instance to already created, or create new session context if
   * <code>create</code> flag is set <code>true</code> and conext doesn't
   * exist. If <code>create</code> parameter is <code>false</code> and
   * session context doesn't exist the <code>null</code> is returned.
   *
   * @param name
   *            the name of the session domain.
   * @param create
   *            <code>true</code> to create new session domain if not exist.
   * @return session context instance or <code>null</code>.
   * @throws NullPointerException
   *             if the provided <code>name</code> parameter is
   *             <code>null</code>.
   */
  public SessionContext getSessionContext(String name, boolean create) {
    if (name == null) {
        NullPointerException npe = new NullPointerException("SessionContext name is null.");
        if (Locations.SESSION_LOC.beDebug()) {
           Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG,"", npe);
        }
      throw npe;
		}

    if (Locations.SESSION_LOC.bePath()) {
      Locations.SESSION_LOC.pathT("getSessionContext(String, boolean) <" + name + "," + create + ">\n" +
      "availabel contexts:\n" + contexts);
    }

    SessionContext context = contexts.get(name);
		if (context == null && create) {
			synchronized (contexts) {
				context = contexts.get(name);
				if (context == null) {
					context = new SessionContext(name);
					contexts.put(name, context);
				}
			}
		}
		return context;
  }

  /**
   * Returns an iterator over the available session contexts. The iterator
   * elements are <code>SessionContext</code> instances.
   *
   * @return iterator over the available session contexts.
   */
  public Iterator contexts() {
    return contexts.values().iterator();
  }

  protected SessionContext removeContext(String name) {
    return contexts.remove(name);
  }

  /**
   * @return Return locking information used for synchronization in cluster environment from Session management
   * infrastructure. This info should be unique  identification of the cluster node.
   */
  public abstract String lockInfo();
}