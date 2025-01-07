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

import com.sap.engine.session.trace.Locations;
import com.sap.engine.session.runtime.http.HttpSessionDomain;
import com.sap.engine.session.runtime.ejb.EjbSessionDomain;
import com.sap.engine.session.monitoring.impl.ContextMonitoringNode;
import com.sap.engine.session.monitoring.MonitoredObject;

import java.util.Iterator;
import java.util.HashMap;

/**
 * The <code>SessionContext</code> class is the facade and the user interface
 * for getting <code>SessionDomains</code> instances. It is the main entry
 * point into the overall session management functionality from a user's point
 * of view.
 * 
 * Author: georgi-s Date: Apr 22, 2004
 */
public class SessionContext implements MonitoredObject {
  public static final String HTTP_CONTEXT = "HTTP_Session_Context";
  public static final String EJB_CONTEXT =  "/Service/EJB";

  /*
  * The name of the context
  */
  private String name;

  /**
   * the type of the context
   * 0 - generic
   * 1 - Http context
   * 2 - EJB context
   */
  private int type;


  /* ContextMonitoringNode for nwa administration */
  private ContextMonitoringNode<SessionContext> monitoringNode = null;

  /*
  * the rootDomains
  */
  private HashMap<String, SessionDomain> rootDomains = new HashMap<String, SessionDomain>();

  /**
   * Factory pattern used from application to obtain the session context.
   * Invoking this method is equivelent to: </blockquote>
   *
   * <pre>
   * SessionContextFactory.getInstance().getSessionContext(contextName, true);
   * </pre>
   *
   * </blockquote>
   *
   * @param contextName
   *            The name of the newly created context.
   * @return SessionContext object.
   * @deprecated replaced from SessionContextFactory.getSessionContext(String, boolean);
   */
  public static SessionContext obtainSessionContext(String contextName) {
    return SessionContextFactory.getInstance().getSessionContext( contextName, true);
  }

  /**
   * Create session context with the given name
   *
   * @param name
   *            the name of context
   */
  protected SessionContext(String name) {
    this.name = name;
    if (name.equals(HTTP_CONTEXT)) {
      type = 1;
    } else if (name.equals(EJB_CONTEXT)) {
      type = 2;
    }
    this.monitoringNode = new ContextMonitoringNode<SessionContext>(name, this);
  }

  /**
   * Returns the name of this <code>SessionContext</code> object
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  private void updateMonitoringNode(){
    this.monitoringNode.setReferent(this);
  }

  public ContextMonitoringNode getMonitoredObject(){
    updateMonitoringNode();
    return this.monitoringNode;
  }

  public ContextMonitoringNode<SessionContext> getMonitoringNode(){
    updateMonitoringNode();
    return this.monitoringNode;
  }

  /**
   * Creates new SessionDomain with the given name and default type. Invoking
   * this method is equivalent to: <blockquote>
   *
   * <pre>
   * createSessionDomain(name, getDefaultType())
   * </pre>
   *
   * </blockquote>
   *
   * @param name the name of the domain to create; may not be null
   * @return the newly created domain.
   *
   * @throws DomainExistException if domain with specified name already exist
   * @throws CreateException if domain cannot be created.
   * @throws IllegalArgumentException if the name argument contains the <code>SessionDomain.SEPARATOR</code>
   * @throws NullPointerException if the provided <code>name</code> parameter is null.
   */
  public synchronized final SessionDomain createSessionDomain(String name) throws CreateException {
    if (Locations.SESSION_LOC.beDebug())
		  Locations.SESSION_LOC.debugT("Create new Session Domain;" + name);

			SessionDomain domain = rootDomains.get(name);
			if (domain == null || domain.isDestroyed()) {
        switch (type) {
          case 0:
            domain = new SessionDomain(name, this, null);
            break;
          case 1:
            domain = new HttpSessionDomain(name, this, null);
            break;
          case 2:
            domain = new EjbSessionDomain(name, this, null);

        }

				rootDomains.put(name, domain);
      } else {
        DomainExistException de = new DomainExistException(name);
        Locations.SESSION_LOC.throwing(de);
        throw de;
			}

      if (Locations.SESSION_LOC.beDebug())
        Locations.SESSION_LOC.debugT(" New Domain is created:" + domain);

      return domain;

  }

  /**
   * Looks for existing session domain with specified name. Returns null if
   * domain does not exist;
   *
   * @param path -
   *            specify the absolute name of the session domain. The
   *            <code>SessionDomain.SEPARATOR</code> char is used to
   *            separate the names in the names sequence.
   *
   * @return SessionDomain with specified absolute name if exist or null
   */
  public SessionDomain findSessionDomain(String path){
    if (path == null) {
      return null;
    }
    int pos = path.indexOf(SessionDomain.SEPARATOR);
    String name;
    SessionDomain domain;
    if (pos != -1) {
      name = path.substring(0, pos);
      domain = rootDomains.get(name);
      if (domain != null) {
        return domain.findSubDomain(path.substring(pos + 1));
      }
    } else {
      domain = rootDomains.get(path);
    }
    return domain;
  }

  /**
   * Returns an iterator over the available session domains. The iterator
   * elements are <code>SessionDomain</code> instances.
   *
   * @return iterator over the available session domains.
   */
  public Iterator<SessionDomain> rootDomains() {
    return rootDomains.values().iterator();
  }

  protected HashMap<String, SessionDomain> getRootDomains() {
    return rootDomains;
  }
  
  /**
   * Destroy the context.
   */
  public void destroy() {

  	Iterator<SessionDomain> it = rootDomains();
  	while(it.hasNext()){
  		SessionDomain sesionDomain = it.next();
  		it.remove();
  		sesionDomain.destroy();
  	}

    rootDomains = null;
    SessionContextFactory.getInstance().removeContext(name);
    name = null;
    monitoringNode = null;
  }
  
}