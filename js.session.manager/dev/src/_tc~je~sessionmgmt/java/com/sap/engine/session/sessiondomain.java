/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session;


import com.sap.engine.core.Names;
import com.sap.engine.session.spi.persistent.*;
import com.sap.engine.session.spi.session.SessionModel;
import com.sap.engine.session.trace.Trace;
import com.sap.engine.session.trace.Locations;
import com.sap.engine.session.util.SessionEnumeration;
import com.sap.engine.session.runtime.OnRequestFailoverMode;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.SessionFailoverMode;
import com.sap.engine.session.runtime.http.HttpRuntimeSessionModel;
import com.sap.engine.session.mgmt.ConfigurationEntry;
import com.sap.engine.session.mgmt.SessionConfigurator;
import com.sap.engine.session.failover.FailoverConfig;
import com.sap.engine.session.monitoring.impl.DomainMonitoringNode;
import com.sap.engine.session.monitoring.MonitoredObject;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents a Session domain, which consists of a set of
 * key-to-session object bindings. A SessioDomain is created as a child of an
 * existing SessionDomain, which forms a hierarchy starting with the root
 * SessionDomain, created from
 * <code>com.sap.engine.session.SessionContext.createSessionDomain</code>
 * method.
 *
 * The session domain is a base configurable unit in the Session Management
 * architecture. It can contain different configuration policy objects (provided
 * by the domain provider) that determine its behavior. For example, a threshold
 * policy that manages the count of session objects stored in the domain, a
 * persistence policy to define the persistent storage to be used, invalidation
 * policy and so on.
 *
 * The SessionDomain object is not distributed between different JVM in the
 * cluster environment. The user is responsible to create corresponding domain
 * structure in the every one JVM enlisted in the cluster.
 *
 * Author: georgi-s Date: Feb 20, 2004
 */
public class SessionDomain implements MonitoredObject {
   public static final Location loc = Location.getLocation(SessionDomain.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  /**
   * The default name separator. This char is used to separate the names in
   * the path.
   */
  public static final char SEPARATOR = '%';

  /**
   * The name of the domain attribute used to identify the alias of the domain
   * in Global Session Table. If the container use the session load balancing
   * based on GST it should set an attribute with this name and
   * <code>String</code> value - the alias of the domain.
   */
  public static final String SHARED_TABLE_ID = "ShTable_alias";


  /**
   * The name of the domain attribute that present default session factory. If
   * the attribute is set the domain can use it to create a session objects.
   * The value of this attribute should be an instance of
   * <code>SessionFactory</code> interface.
   *
   *  SessionRequest#getSession(boolean)
   */
  public static final String SESSION_FACTORY = "Session_Factory";

  public static final String SESSION_HOLDER_FACTORY = "session.SessionHolderFactory";

  // The name of the domain
  private String name;

  protected long creationTime = System.currentTimeMillis();

  //used to build tree structure of the Session domains
  protected SessionDomain parent;

  protected HashMap<String, SessionDomain> childs = new HashMap<String, SessionDomain>(0);

  //contains domain specific attributes.
  protected Hashtable<String, Object> attributes = new Hashtable<String,Object>(3);


  //reference element refer to this session domain.
  private DomainReference reference;

  private PersistentDomainModel persModel;

  protected SessionFailoverMode failoverMode = new SessionFailoverMode();

  private final Map<LifecycleManagedDataEntry, Object> dependants = Collections.synchronizedMap(new WeakHashMap<LifecycleManagedDataEntry, Object>(1));

  protected SessionHolderFactory holderFactory = SessionHolderFactoryImpl.getDefaultFactory();

  //indicate that the domain is destroyed
  private boolean destroyed;

  private String path;

  private ConcurrentHashMap<String, RuntimeSessionModel> activeRuntimeSessionModels = new ConcurrentHashMap<String, RuntimeSessionModel>(5);

  private SessionContext context;

  /* DomainMonitoringNode for nwa administration */
  private DomainMonitoringNode monitoringNode = null;

  protected DomainConfiguration configuration = new DomainConfiguration();
  
  public static boolean disableFailover;
  
  /**
   * counts how much times the persistence of sessions from this domain has failed
   * i.e. the number of "persisted" non-serializable sessions
   */
  private AtomicInteger nonSerializableSessionsCount = new AtomicInteger();
  
  /**
   * number of sessions from this domain which are expired
   * 
   * i.e. number of sessions from this domain  invalidated because
   * their are not requested during their maxInactiveInterval
   */ 
  private AtomicInteger expiredSessionsCount = new AtomicInteger();
  
  /**
   * number of all sessions from this domain which are invalidated
   * includes expired sessions, sessions which method invalidate is called explicitly,
   * sessions invalidated when the client context is destroyed and so on
   */
  private AtomicInteger invalidatedSessionsCount = new AtomicInteger();

  protected SessionDomain(String name, SessionContext context, SessionDomain parent) {
    this.parent = parent;
    this.name = name;
    this.context = context;
    ConfigurationEntry configEntry = SessionConfigurator.getConfigurationEntry(context.getName() + SessionDomain.SEPARATOR + path());
    if (configEntry != null) {
    	applyConfiguration(configEntry);
    }
    this.monitoringNode = new DomainMonitoringNode(context.getName(), name);

  }

  /**
   * Returns the name of the Session Domain.
   *
   * @return The name of the Session Domain
   */
  public String getName() {
    return name;
  }

  private void updateMonitoringNode(){
    this.monitoringNode.setReferent(this);
  }

  public DomainMonitoringNode getMonitoringNode(){
    updateMonitoringNode();
    return this.monitoringNode;
  }

  public MonitoringNode getMonitoredObject() {
    updateMonitoringNode();
    return this.monitoringNode;
  }

  /**
   * Get the full (absolute) path to the domain.
   *
   * @return the path in the tree
   */
  public synchronized String path() {
    if (path == null) {
      if (parent != null) {
        path = parent.path() + SessionDomain.SEPARATOR + name;
      } else {
        path = name;
      }
    }
    return path;
  }

  /**
   * Returns serializable reference <code>DomainReference</code> object.
   *
   *
   * @return The <code>DomainReference</code> object that refer to this
   *         Session Domain
   */
  public DomainReference getReference() {
    if (reference == null) {
    	synchronized (this) {
    		if (reference == null) {
    			reference = new DomainReference(this);
    		}
    	}
    }
    return reference;
  }

  public DomainConfiguration configuration() {
    return configuration;
  }

  public String getSessionDomainType() {
    return "SessionDomain";
  }

  public SessionContext getEnclosingContext() {
    return context;
  }

  public void createSessionOnNode(String serverNodeID, String sessionID) throws SessionException {
    ClusterEnv.getInstance().createSessionOnNode(this,serverNodeID,sessionID);
  }



  /**
   * Binds an attribute to this session domain. The attributes are stored
   * locally for the domain.
   *
   * @param attribute the attribute name
   * @param value the attribute value
   * @throws IllegalStateException if the domain is destroyed
   */
  public void setDomainAttribute(String attribute, Object value)
      throws IllegalStateException {
    if (!destroyed) {
      if (attribute.equals(SESSION_HOLDER_FACTORY) && value instanceof SessionHolderFactory) {
        holderFactory = (SessionHolderFactory) value;
      }
      attributes.put(attribute, value);
    } else {
      throw new IllegalStateException("The domain <" + path()
          + "> is destroyed.");
    }
  }

  /**
   * Returns the object bounds as attribute with specified name in this
   * session domain or null if no attribute is bound under the name.
   *
   * @param attribute
   *            a string specifying the name of the attribute.
   * @return the attribute with specified name.
   * @throws IllegalStateException -
   *             if the domain is destroyed
   */
  public Object getDomainAttribute(String attribute)
      throws IllegalStateException {
    return attributes.get(attribute);
  }

  /**
   * Create new <code>SessionDomain</code> object as a child of the session
   * SessionDomain or return the child object if already exist.
   *
   * @param name -
   *            Session domain's name to be created
   * @return New <code>SessionDoamin</code> object
   * @throws CreateException
   *             if the domain cannot be created
   * @throws IllegalStateException
   *             if the domain is destroyed
   */
  public final synchronized SessionDomain createSubDomain(String name)
      throws CreateException, IllegalStateException {
    return createSubDomain(name, this.configuration.copy());
  }

  public final synchronized SessionDomain createSubDomain(String name, DomainConfiguration config) throws CreateException, IllegalStateException {
    if (destroyed) {
      throw new IllegalStateException("The domain <" + path() + "> is destroyed.");
    }
    SessionDomain domain = childs.get(name);
    if (domain != null && !domain.isDestroyed()) {
      return domain;
    }
    domain = subDomainInstance(name, context);
    domain.configuration = config;
    childs.put(name, domain);
    Object handlerFactory = getDomainAttribute(SessionDomain.SESSION_HOLDER_FACTORY);
    if (handlerFactory != null) {
      domain.setDomainAttribute(SessionDomain.SESSION_HOLDER_FACTORY, handlerFactory);
    }
    return domain;
  }

  protected SessionDomain subDomainInstance(String name, SessionContext context) {
    return new SessionDomain(name, context, this);
  }

  /**
   * Returns the subdomain with specified name or null if no subdomain is
   * bound.
   *
   * @param path
   *            the full path name
   * @return the sub domain with the specified path
   */
  public final SessionDomain findSubDomain(String path) {
    if (path == null || path.length() == 0) {
      return this;
    }
    int pos = path.indexOf(SessionDomain.SEPARATOR);
    String subDomain;
    SessionDomain domain;
    if (pos != -1) {
      subDomain = path.substring(0, pos);
      domain = childs.get(subDomain);
      if (domain != null) {
        return domain.findSubDomain(path.substring(pos + 1));
      }
    } else {
      domain = childs.get(path);
    }
    return domain;
  }

  public Iterator subDomains() {
    return new HashMap<String, SessionDomain>(childs).values().iterator();
  }

  public SessionDomain parent() {
    return parent;
  }

  public void setConfiguration(String configParam, Object value) {
    configuration.put(configParam, value);
  }
  
  public void setConfiguration(String configParam, int value) {	      
    if (configParam != null) {
      configParam = configParam.trim().toLowerCase();
      Storage storage = null;
      SessionFailoverMode mode = null;
      if (configParam.equals(FailoverConfig.FAILOVER_SCOPE)) {
    	  if(!disableFailover){
    		  storage = SessionConfigurator.buildStorageForType(value);
    	  }
      } else if (configParam.equals(FailoverConfig.FAILOVER_CONFIGURATION)) {
        mode = SessionConfigurator.buildFailoverModeForType(value);
      }
      ConfigurationEntry configEntry = SessionConfigurator.addConfigurationEntry(context.getName() + SessionDomain.SEPARATOR + path(), mode, storage);
      applyConfiguration(configEntry);
    }
  }
  
  private void applyConfiguration(ConfigurationEntry configEntry){
	  Storage storage = configEntry.getConfiguredPersistentStorage();
    if (storage != null) {
      try {
        persModel = storage.getDomainModel(getEnclosingContext().getName(), path());
      } catch (PersistentStorageException e) {
        Trace.logError(e);
      }
    }
    failoverMode = configEntry.getSessionFailoverMode();
  }

  /**
   * Destroy the <code>SessionDomain</code> instance. In result of this
   * method all children of this domain will be destroyed too. The sessions
   * stored in the domain will be removed from the back-end storage without
   * invalidation.
   */
  public synchronized void destroy() {
    if (loc.bePath()) {
      loc.pathT("Destroying session domain:" + this + "\n" + "sub domains:\n" +
      childs + "\n Active Sessions:\n" + activeRuntimeSessionModels);
    }
    if (destroyed) {
      return;
    }
    destroyed = true;
    //destroy children
    Iterator itr = childs.values().iterator();
    for (SessionDomain child ; itr.hasNext();) {
      child = (SessionDomain) itr.next();
      if (loc.bePath()) {
        loc.pathT("Destroying:" + path() + " \\  " + child.path());
      }
      child.destroy();
    }
    Iterator<RuntimeSessionModel> rmodels = activeRuntimeSessionModels.values().iterator();
    com.sap.engine.session.SessionModel rm;
    while (rmodels.hasNext()) {
      rm = rmodels.next();
      if (loc.bePath()) {
        loc.pathT("Invalidating session:" + rm.session());
      }
      rm.invalidate();
    }

    synchronized (dependants) {
      for (LifecycleManagedDataEntry entry : dependants.keySet()) {
        try {
          entry.domainDestroied();
        } catch (Exception e) {
          if (loc.beDebug()) {
            loc.debugT("Exception during event invocation: " + e);
          }
        }
      }
    }
    if (parent != null) {
      parent.removeSubDomain(this.name);
    } else {
      getEnclosingContext().getRootDomains().remove(name); // the domain should exist here
    }
    SessionConfigurator.removeConfigurationEntry(context.getName() + SessionDomain.SEPARATOR + path());
    activeRuntimeSessionModels.clear();
    dependants.clear();
  }

  /**
   * Check is it a destroyed <code>SessionDomain</code> instance.
   *
   * @return <code>true</code> if the domain is destroyed.
   */
  public synchronized boolean isDestroyed() {
    return destroyed;
  }

  protected void removeSubDomain(String name) {
    childs.remove(name);
  }

  protected SessionModel sessionModel(Session session) {
    return null;
  }

  /**
   * Add session object to the domain. in result of this operation the session
   * object is stored in the back-end store system but it is not activated,
   * this means that all changes of the session object, made after invoking
   * <code>addNewSession</code> method will be lost. If the user want to
   * change the session after adding to the session domain it should make a
   * <code>SessionRequest</code> for this session or get a
   * <code>SessionHolder</code> instance for the session.
   *
   * @param sessionId the session id
   * @param session <code>Session</code> instance to be added to the domain
   *
   * @throws SessionException if the session object is invalidated.
   * @throws IllegalStateException  if the session domain is destroyed.
   * @return the runtime session model
   */
  public final RuntimeSessionModel addNewSession(String sessionId, Session session) throws SessionException, IllegalStateException {
    if (destroyed) {
      throw new IllegalStateException("The domain <" + path() + "> is destroyed.");
    }
    RuntimeSessionModel runtimeModel = createRuntimeSessionModel(sessionId, session);
    synchronized(syncMonitor) {
      if (activeRuntimeSessionModels.contains(session.sessionId())) {
        throw new SessionExistException(session.sessionId());
      }
      activeRuntimeSessionModels.put(session.sessionId(), runtimeModel);
    }
    return runtimeModel;
  }

  /**
   * Returns the SessionHolder for the session. The session holder present the
   * native implementation of the SessionRequest that can be used from the
   * containers to access the session.
   *
   * @param sessionId the session id
   * @return the <code>SessionHolder</code> instance for the session with
   *         specified sessionId
   */
  public final SessionHolder getSessionHolder(String sessionId) {
    return holderFactory.getInstance(sessionId, this);
  }

  /**
   * This method create a new session object using the
   * <code>SessionFactory</code> passed as argument. The created session is
   * in active state and can be retrieved from returned
   * <code>SessionHolder</code> object.
   *
   * @param sessionId -
   *            the session identifier
   * @param factory -
   *            the <code>SessionFactory</code> instance used to create the
   *            session object
   *
   * @return the <code>SessionHolder</code> instance for the newly created
   *         session object
   * @throws SessionException
   *             if the session cannot be created.
   * @throws SessionExistException -
   *             if the session with the same name already exist.
   */
  public final SessionHolder createNewSession(String sessionId, SessionFactory factory) throws SessionException {
    SessionHolderImpl holder = new SessionHolderImpl();
    holder.doSessionRequest(this, sessionId);
    holder.addNewSession(factory.getSession(sessionId));
    return holder;
  }

  /**
   * Remove the session with specified name from the domain. In result of
   * method invocation the session object is removed from the back-end storage
   * system without session invalidation. If the session is in active state
   * during the method execution the session will be removed from the back-end
   * store and marked for remove, so it will be not stored when goes to the
   * inactive state.
   *
   * @param name
   *            The session that should be removed
   */
  public final void removeSession(String name) {
    com.sap.engine.session.SessionModel rm = activeRuntimeSessionModels.get(name);
    if (rm != null) {
      rm.remove();
    } else if (persModel != null) {
      try {
        PersistentSessionModel sModel = persModel.getModel(name);
        if (sModel != null)
          sModel.destroy();
      } catch (PersistentStorageException e) {
        Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG,"",e);
      }

    }

  }

  private boolean active = true;

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  /**
   * Removes all active sessions
   */
  public void removeActiveSessions() {
    synchronized (syncMonitor) {
      setActive(false);
      for (RuntimeSessionModel model : activeRuntimeSessionModels.values()) {
        try {
          model.invalidateIfNeeded();
        } catch (Exception e) {
          // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
          // Please do not remove this comment!
        }
      }
    }
    synchronized (dependants) {
      for (LifecycleManagedDataEntry entry : dependants.keySet()) {
        try {
          entry.domainDestroied();
        } catch (Exception e) {
          if (loc.beDebug()) {
            loc.debugT("Exception during event invocation: " + e);
          }
        }
      }
    }
    dependants.clear();
  }

  /**
   * Returns the snapshot of the session object. Any changes over this object will
   * not affect the back-end storage.
   *
   * @param sessionId the session id
   * @return  an immutable instance of a session for the given ID
   * @throws SessionException if the session can't be created
   */
  public Session getSessionImmutable(String sessionId) throws SessionException {
    RuntimeSessionModel rm = activeRuntimeSessionModels.get(sessionId);
    if (rm != null) {
      return rm.getSession(null);
    }
    return null;
  }


  /**
   * Returns <tt>true</tt> if this domain contains session binding for the
   * specified key.
   *
   * @param sessionId
   *            The key whose presence in this domain is to be tested.
   * @return <tt>true</tt> if this domain contains session binding for the
   *         specified key
   */
  public boolean containsSession(String sessionId) {
    return activeRuntimeSessionModels.containsKey(sessionId);
  }

  /**
   * Invalidate specified session object. In result of method invocation the
   * specified session object will go to the invalid state and will be removed
   * from the domain.
   *
   * @param session the session
   */
  public final void invalidateSession(Session session) {
    try {
      if (Trace.beDebug())
        Trace.trace("invalidate session:" + session.sessionId());

      session.invalidateInternally();
    } finally {
      removeSession(session.sessionId());
    }
  }

  /**
   * Adds relation between child and parent sessions
   *
   * @param parent
   *            the parent session, should be active session (means that at
   *            least one session request is a part of the session in the
   *            moment.
   * @param child
   *            the child session, should be active session, from this domain
   * @param strength
   *            the strength of the relation -
   *            <code>SessionRelation.WEAK</code>,
   *            <code>SessionRelation.SOFT</code> or
   *            <code>SessionRelation.STRONG</code>.
   * @throws IllegalArgumentException
   *             if some of the sessions is not active or if the child is not
   *             a session from this domain.
   */
  public void addAsChild(Session parent, Session child, byte strength)
      throws IllegalArgumentException {
  }

  /**
   * Remove relation from parent to child session with specified session id.
   * The parent session should be active session. The <code>child</childe>
   * should identify session from this session domain.
   *
   * @param parent the parent session
   * @param child the child session identifier
   * @throws IllegalArgumentException if the parent is not an active session
   * object.
   */
  public void removeChild(Session parent, String child)
      throws IllegalArgumentException {
  }

  /**
   * Returns the number of session objects
   *
   * @return the number of sessions
   */
  public int size() {
    return activeRuntimeSessionModels.size();
  }

  /**
   * The size of the session object.
   * Some implementations may not support this functionality.
   *
   * @param session - the session that should be expected
   * @return the session object size.
   * @deprecated replaced from size(String sessionId);
   */
  public int size(Session session) {
    PersistentSessionModel pm = session.getPersistentModel();
    if (pm != null) {
      return 1; // TODO size pm.
    }
    return 0;
  }

  /**
   * Returns the size of the session in the domain with specified session
   * identificator
   *
   * @param sessionId
   *            The session identificator.
   * @return the size of the session or <tt>-1</tt> if this functionality is
   *         not supported or if no session is present .
   */
  public int size(String sessionId) {
    return 0;
  }

  /**
   *
   * @return enumeration of the existing sessions
   * @deprecated
   */
  public  SessionEnumeration enumerateSessions() {

    return new SessionEnumeration() {
      Iterator itr = sessions();

      public SessionHolder getSessionHolder(Session session) {
        return null;
      }

      public void reset() {
      }

      public void release() {
      }

      public boolean hasMoreElements() {
        return itr.hasNext();
      }

      public Object nextElement() {
        return itr.next();
      }
    };
  }

  /**
   * Returns an iterator over the available sessions. The iterator elements
   * are <code>Session</code> >instances.
   *
   * @return iterator over available sessions.
   */
  public Iterator sessions() {
    HashSet<Session> temp = new HashSet<Session>();
    for (RuntimeSessionModel s : activeRuntimeSessionModels.values()) {
      try {
        Session session = s.getSession(null);
        if (session != null) {
          temp.add(session);
        }
      } catch (CreateException e) {
        // It is not possible because the getSession methods is called with null argument
	// $JL-EXC$
      }
    }
    return temp.iterator();
  }
  /**
   * Returns an iterator over the available sessions including active sessions
   * that are not submitted to the storage. The iterator elements
   * are <code>Session</code> >instances.
   *
   * @return  iterator over available sessions.
   */
  public  Iterator allSessions() {
//   HashSet<Session> temp = new HashSet<Session>();
//    Session session;
//    for (RuntimeSessionModel s : activeSessions.values()) {
//        if (!s.isNew()) {
//           session = s.session();
//          if (session != null) {
//            temp.add(session);
//          }
//        }
//    }
    return sessions();
  }


  public  Iterator expiredSessions() {
    return null;
  }

  /**
   *
   * @return key's enumeration
   * @deprecated replacement keys();
   */
  public  Enumeration enumerateKeys() {
    return null;
  }

  /**
   * Returns a key iterator over the available sessions. The iterator elements
   * are <code>java.lang.String</code> instances.
   *
   * @return key iterator over available sessions.
   */
  public Iterator keys() {
    synchronized(syncMonitor) {
      return new HashMap<String, RuntimeSessionModel>(activeRuntimeSessionModels).keySet().iterator();
    }
  }

  void addDependentObject(LifecycleManagedDataEntry dep) {
    dependants.put(dep, null);
  }

  void removeDependentObject(LifecycleManagedDataEntry dep) {
    dependants.remove(dep);
  }

  private final Object syncMonitor = new Object();

  public RuntimeSessionModel runtimeSessionModel(String sessionId, SessionFactory factory, Object metaData)
          throws  CreateException {
    RuntimeSessionModel runtimeModel = activeRuntimeSessionModels.get(sessionId);
    if (runtimeModel == null) {
      synchronized(syncMonitor) {
        runtimeModel = activeRuntimeSessionModels.get(sessionId);
        if (runtimeModel == null && factory != null ) {
          runtimeModel = createRuntimeSessionModel(sessionId);
          activeRuntimeSessionModels.put(sessionId, runtimeModel);
        }
      }
    }
    return runtimeModel;
  }

  public RuntimeSessionModel runtimeSessionModel(String sessionId, boolean create)
          throws  CreateException {

    RuntimeSessionModel runtimeModel = activeRuntimeSessionModels.get(sessionId);
    if (runtimeModel == null) {
      synchronized(syncMonitor) {
        runtimeModel = activeRuntimeSessionModels.get(sessionId);
        if (runtimeModel == null) { 	// before - AND (create || persModel != null)
          runtimeModel = createRuntimeSessionModel(sessionId);
          activeRuntimeSessionModels.put(sessionId, runtimeModel);
        }
      }
    }
    if (loc.bePath()) {
      loc.pathT("runtimeSessionModel(String, boolean) returns:" + runtimeModel);
    }
    return runtimeModel;

  }
  
  
 public RuntimeSessionModel getRuntimeSessionModel(String sessionId){
	 return activeRuntimeSessionModels.get(sessionId);
 }
  
  
  protected RuntimeSessionModel createRuntimeSessionModel(String sessionId) throws CreateException {
    RuntimeSessionModel runtimeModel;
    if (context.getName().contains("HTTP")) {
      runtimeModel =  new HttpRuntimeSessionModel(sessionId, this,  failoverMode);
    } else {
      runtimeModel =  new RuntimeSessionModel(sessionId, this, failoverMode);
    }
    return runtimeModel;
  }

  protected RuntimeSessionModel createRuntimeSessionModel(String sessionId, Session session) throws CreateException {
    RuntimeSessionModel runtimeModel;
    if (context.getName().contains("HTTP")) {
      runtimeModel =  new HttpRuntimeSessionModel(sessionId, this, session, failoverMode);
    } else {
      runtimeModel =  new RuntimeSessionModel(sessionId, this, session, failoverMode);
    }
    return runtimeModel;
  }

  public void removeSession(RuntimeSessionModel session) {
    activeRuntimeSessionModels.remove(session.sessionId());
  }

  public PersistentSessionModel getPersistentSessionModel(RuntimeSessionModel session) throws PersistentStorageException {
    if (persModel != null) {
      return persModel.createModel(session.sessionId());
    }  else {
      return null;
    }
  }
  
  public PersistentSessionModel getSoftShutdownPersistentSessionModel(RuntimeSessionModel session) throws PersistentStorageException {
    PersistentDomainModel persModel = SessionConfigurator.storageSoftShutdown.getDomainModel(getEnclosingContext().getName(), path());
    return persModel.createModel(session.sessionId());
  }

  public Session _getSessionInternal(String sessionName) {
    RuntimeSessionModel runtimeModel = activeRuntimeSessionModels.get(sessionName);
    if (runtimeModel != null) {
        return runtimeModel.session();
    }
    return null;
  }

  /**
   * Checks whether this domain is failover enabled
   * @return TRUE if failover is enabled
   */
  public boolean isFailoverEnabled() {
    return persModel != null;
  }
  
  /**
   * adds one to the number of unsuccessfully serialized sessions for this domain
   */
  public void addNonSerializableSession() {
    nonSerializableSessionsCount.incrementAndGet();
  }

  /**
   * @return the number of unsuccessfully serialized sessions for this domain
   */
  public int nonSerializableSessionsCount() {
    return nonSerializableSessionsCount.get();
  }
  
  /**
   * increase the number of expired sessions in this domain with one
   */
  public void addExpiredSession() {
    expiredSessionsCount.incrementAndGet();
  }
  
  /**
   * @return number of expired sessions in this domain
   */
  public int expiredSessionsCount() {
    return expiredSessionsCount.intValue();
  }
  
  /**
   * increase the number of invalidated sessions in this domain with one
   */
  public void addInvalidatedSession() {
    invalidatedSessionsCount.incrementAndGet();
  }
  
  /**
   * @return number of invalidated sessions in this domain
   */
  public int invalidatedSessionsCount() {
    return invalidatedSessionsCount.intValue();
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer(super.toString());
    sb.append("\n");
    sb.append("Domain path:");
    sb.append(path());
    sb.append(" destroyed:");
    sb.append(destroyed);
    return sb.toString() ;
  }

  public boolean failoverState(int scope, Storage storage){
  	boolean isOK = true;
  	try {
  		PersistentDomainModel oldPersModel = persModel;
			persModel = storage.getDomainModel(getEnclosingContext().getName(), path());
			if(activeRuntimeSessionModels != null){
		  	for(RuntimeSessionModel session : activeRuntimeSessionModels.values()){
		  		PersistentSessionModel oldPersistentModel = session.persistentModel;
		  		boolean isSticky = session.isSticky();
		  		session.persistentModel = getPersistentSessionModel(session);
		  		OnRequestFailoverMode mode = new OnRequestFailoverMode();
		  		session.setFailoverMode(mode);
		  		session.setSticky(false);
		  		SessionHolder holder = this.getSessionHolder(session.getSessionId());
					try {
						mode.schedule((RuntimeSessionModel)holder.getSession().getSessionModel());
					} catch (SessionNotFoundException e) {
						e.printStackTrace();
					}
					holder.releaseAccess();
					session.persistentModel = oldPersistentModel;
					if(session.isSticky()){
						isOK = false;
					}else{
						session.setSticky(isSticky);
					}
		  	}
			}
			persModel = oldPersModel;
		} catch (PersistentStorageException e) {
			isOK = false;
		}  	
  	return isOK;
  }
  
}