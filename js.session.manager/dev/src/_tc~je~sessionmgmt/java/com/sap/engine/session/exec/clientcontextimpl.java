/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.exec;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.security.auth.Subject;

import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionContextFactory;
import com.sap.engine.session.callback.Callback;
import com.sap.engine.session.callback.CallbackException;
import com.sap.engine.session.callback.CallbackHandler;
import com.sap.engine.session.monitoring.MonitoredObject;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.engine.session.monitoring.impl.UserContextMonitoringNode;
import com.sap.engine.session.scope.Scope;
import com.sap.engine.session.scope.ScopeObserver;
import com.sap.engine.session.scope.exception.NotSupportedScopeTypeException;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.trace.Locations;
import com.sap.engine.session.usr.*;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class represents the client in the corresponding session context object
 * 
 * @author Nikolai Neichev, Petar Petrov
 */
public class ClientContextImpl extends UserContext implements Serializable, ClientContext, MonitoredObject {

  private static final long serialVersionUID = 7593685473661512021L;
  public static final Location loc = Locations.USER_LOC;
  private static Timer contextInvalidationTimer;  
  static ClientContextInvalidationTask invalidationTask;
  private static int INVALIDATION_TASK_PERIOD = 30 * 60 * 1000;
  
  //current client context ip which is propagated through execution details of
  //SessionExecContext (CSM ThreadContext)
  private String ip;

  static void init() {
    UserContext.setDelegate(new UserContextDelegateImpl());
    contextInvalidationTimer = new Timer("Session ClientContextObjectInvalidationTask executor timer", true);
    invalidationTask = new ClientContextInvalidationTask();
    contextInvalidationTimer.schedule(invalidationTask, INVALIDATION_TASK_PERIOD, INVALIDATION_TASK_PERIOD);
    /* register ClientContext Scope Type */
    Scope.registerScopeType(Scope.CLIENT_CONTEXT_SCOPE_TYPE);
  }

  private static final String CLIENT_CONTEXT_CHUNK_NAME = "SS";
  private static final String LOGIN_SESSION_CHUNK_NAME = "LS";

  static final ConcurrentHashMap<String, ClientContextImpl> clientContexts = new ConcurrentHashMap<String, ClientContextImpl>();
  private static final ConcurrentHashMap<String, String> aliasToClientID = new ConcurrentHashMap<String, String>();

  private static PersistentDomainModel contextsPersistence;

  public static void setPersistentModel(PersistentDomainModel persModel) {
    contextsPersistence = persModel;
  }

  /**
   * @deprecated use getByClientId
   */
  static ClientContextImpl getClientContext(String key) {
    throw new IllegalStateException("This method is deprecated, please use getByClientId()");
  }

  /**
   * @deprecated use getByClientId
   */
  public static ClientContextImpl getClientContext(String key, ClientContextImpl inst) {
    throw new IllegalStateException("This method is deprecated, please use getByClientId()");
  }

  private synchronized static ClientContextImpl loadClientContext(String clientId, ClientContextImpl defaultClientContext) {
    if (loc.bePath()) {
      loc.entering("load client context<" + clientId + ">");
    }
    ClientContextImpl clientContext = clientContexts.get(clientId);
    if ( (contextsPersistence != null) &&
         ((clientContext == null) ||
            ( (clientContext.getAlias() != null) &&
              (clientContext.hasApplicationSessions)) ) ) { 
      if (loc.bePath()) {
        loc.pathT("client context not found in memory, or found and has ALIAS, checking DB<" + clientId + ">");
      }
      try {
        PersistentSessionModel persModel = contextsPersistence.getModel(clientId);
        if (persModel != null) { // context exists in the DB
          if (loc.bePath()) {
            loc.pathT("client context found in DB<" + clientId + "> , reloading...");
          }
//          // reloading login session from DB
          LoginSessionImpl reloadedLoginSession = (LoginSessionImpl) persModel.getChunk(LOGIN_SESSION_CHUNK_NAME);
          if (clientContext == null) {
            clientContext = (ClientContextImpl) persModel.getChunk(CLIENT_CONTEXT_CHUNK_NAME);
            clientContext.setLoginSession(reloadedLoginSession);
          } else {  // TODO Login session version check
            clientContext.setLoginSession(reloadedLoginSession);
          }
          if (loc.bePath()) {
            loc.pathT("context to return: " + clientContext);
          }
        } else { // context is not in the DB
          if (loc.bePath()) {
            loc.pathT("client context not found in DB<" + clientId + ">");
          }
          // we should remove the client context from VM - saml2 auth case only(with alias)
          if (clientContext != null && clientContext.persistedContext != null) {
            if (loc.bePath()) {
              loc.pathT("client context was saved or load before<" + clientId + ">");
            }
            // context was saved or loaded before
            if (!clientContext.loginSession().isAnonymous()) {
              if (loc.bePath()) {
                loc.pathT("client context not anonymous, will remove it: " + clientContext);
              }
              // only if not anonymous, otherwise we loose the whole client context
              clientContext.logout();
              return null;
            } else { // anonymous client context - no need to remove it
              if (loc.bePath()) {
                loc.pathT("client context is anonymous, will return: " + clientContext);
              }
              return clientContext;
            }
          } else { // context was never persisted - so no DB refresh
            if (loc.bePath()) {
              loc.pathT("client context never persisted, returning: " + clientContext);
            }
            return clientContext;
          }
        }
        if (clientContext != null) {
          /* set the loaded persistent Model in the raised UC */
          clientContext.persistedContext = persModel;
          /* load UC Scope Observer */
          raiseObserver(clientContext, persModel.getChunk(Scope.CLIENT_CONTEXT_SCOPE_NAME));
        } else if (defaultClientContext != null) {
          clientContext = defaultClientContext;
        } else {
          return null;
        }
        clientContexts.put(clientId, clientContext);
        return clientContext;
      } catch (PersistentStorageException e) {
        loc.infoT("Can't get persistent model for client context:" + clientId);
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
    if (loc.bePath()) {
      loc.pathT("client context to return: " + clientContext);
    }
    return clientContext;
  }

  private synchronized static ClientContextImpl loadAliasToClientId(String alias) {
    if (loc.bePath()) {
      loc.entering("Reload the alias mapping for <" + alias + ">");
    }
    ClientContextImpl clientContext = null;
    if (contextsPersistence != null) {
      PersistentSessionModel aliasMapping;
      try {
        aliasMapping = contextsPersistence.getModel("ALIAS:" + alias);
        if (aliasMapping != null) {
          String clientId = aliasMapping.getLockInfo();
          if (clientId != null) {
            aliasToClientID.put(alias, clientId);
            clientContext = getByClientId(clientId);
          }
        }
      } catch (PersistentStorageException e) {
        loc.infoT("Can't reload Alias table");
        loc.traceThrowableT(Severity.WARNING, "", e);
      }     
    }
    return clientContext;
  }

  private static void raiseObserver(ClientContextImpl clientContext, Object scopeObserver) {
    if (scopeObserver != null) {
      Scope.registerScopeType(Scope.CLIENT_CONTEXT_SCOPE_TYPE);
      clientContext.scopeObserver = (ScopeObserver) scopeObserver;
      if (loc.bePath()) {
        loc.pathT("Restore Observer:" + clientContext.scopeObserver);
      }
    } else { // will create new scopeObserver
      try {
        clientContext.scopeObserver = Scope.createScope(clientContext.toString(), Scope.CLIENT_CONTEXT_SCOPE_TYPE);
        if (loc.bePath()) {
          loc.pathT("No stored observer, new is created: " + clientContext.scopeObserver);
        }
      } catch (NotSupportedScopeTypeException e) {
        if (loc.beDebug()) {
          loc.throwing(e);
        }
      }
    }
  }

  
  public void removeAllClientContextData() {
    // clear the DB from the context data
    if (persistedContext != null) {
      try {
        if (loc.bePath()) {
          loc.pathT("removing client context from the DB<" + clientId + ">");
          if (loc.beDebug()) {
            loc.traceThrowableT(Severity.DEBUG, "", new Exception("trace"));
          }
        }
        persistedContext.destroy();
        persistedContext = null;
      } catch (PersistentStorageException e) {
        loc.traceThrowableT(Severity.ERROR, "", e);
      }
    } else if (contextsPersistence != null) {
      if (loc.bePath()) {
        loc.pathT("checking DB for existing client context with id <" + clientId + ">");
      }
      try {
        persistedContext = contextsPersistence.getModel(clientId);
        if (persistedContext != null) {
          if (loc.bePath()) {
            loc.pathT("client context found in DB, will remove it <" + clientId + ">");
          }
          persistedContext.destroy();
          persistedContext = null;
        }
      } catch (PersistentStorageException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    } else {
      if (loc.bePath()) {
        loc.pathT("client context persistence is not available.");
      }
    }

    // clear the alias from DB if exists
    if (isAliasToClientIdMappingSet) {
      if (contextsPersistence != null) {
        PersistentSessionModel aliasMapping;
        try {
          aliasMapping = contextsPersistence.getModel("ALIAS:" + getAlias());
          if (aliasMapping != null) {
            aliasMapping.destroy();
          }
        } catch (PersistentStorageException e) {
          loc.infoT("Can't reload Alias table");
          loc.traceThrowableT(Severity.WARNING, "", e);
        }
        aliasToClientID.remove(getAlias());
      }
    }
    
    invalidationTask.removeScheduledContext(this);
    clientContexts.remove(clientId);
    //clientId = null;
    ip = null;
  }

  /**
   * @deprecated not used anymore
   * @param clientId
   * @return
   */
  private static UserContext removeClientContext(String clientId) {
    if (loc.bePath()) {
      loc.pathT("remove client context<" + clientId + ">");
    }
    ClientContextImpl cc = clientContexts.get(clientId);
    cc.removeAllClientContextData();
    return cc;
  }

  public static Collection<ClientContextImpl> clientContexts() {
    return clientContexts.values();
  }

  public static LoginSessionImpl anonymousLoginSession;

  public static boolean isAnonymousLoginSessionSet() {
    return anonymousLoginSession != null;
  }

  public static LoginSessionImpl getAnonimousLoginSession() {
    return anonymousLoginSession;
  }

  public static ClientContextImpl getByClientId(String clientId) {
    return getByClientId(clientId, null);
  }

  static ClientContextImpl getByClientId(String clientId, ClientContextImpl inst) {
    if (loc.bePath()) {
      loc.pathT("get client context by clientId<" + clientId + ">");
      loc.traceThrowableT(Severity.DEBUG, "get by client ID", new Exception("trace"));
    }
    if (clientId == null) {
      return null;
    }
    // search for client context in VM
    ClientContextImpl ctx = clientContexts.get(clientId);
    if (ctx == null) {
      if (loc.bePath()) {
        loc.pathT("client context not found in memory, attempt to load<" + clientId + ">");
      }
      // context not found in VM -> load from DB
      ctx = loadClientContext(clientId, inst);
      if (loc.bePath()) {
        loc.pathT("client context loaded from DB: " + ctx);
      }
    } else if ((ctx.getAlias() != null) && (contextsPersistence != null) && (ctx.hasApplicationSessions)) {
      if (loc.bePath()) {
        loc.pathT("client context found in memory and has ALIAS, checking DB<" + clientId + ">");
      }
      // context found in VM
      try {
        PersistentSessionModel persModel = contextsPersistence.getModel(clientId);
        // 
        if (persModel != null) { // context found in the DB
          LoginSessionImpl reloadedLoginSession = (LoginSessionImpl) persModel.getChunk(LOGIN_SESSION_CHUNK_NAME);
          if (reloadedLoginSession != null) {
              ctx.setLoginSession(reloadedLoginSession);
            // TODO reload scopeObserver
            if (loc.bePath()) {
              loc.pathT("client context existing in DB, returning reloaded: " + ctx);
            }
            return ctx;
          }
        }
        if (!ctx.loginSession().isAnonymous()) {
          if (loc.bePath()) {
            loc.pathT("client context not in DB and not anonymous, removing<" + clientId + ">");
          }
          // context not found in DB - remove refresh only if not anonymous, otherwise we lose the whole client context
//          clientContexts.get(clientId).logout(); // TODO izlishen get ?
          ctx.logout();
          return null;
        }
      } catch (PersistentStorageException e) {
        loc.infoT("Can't get persistent model for client context:" + clientId);
        loc.traceThrowableT(Severity.WARNING, "", e);
        return null;
      }
    }
    if (ctx != null && ctx.loggedOut) { // if the context is loggedOut it shouldn't be returned
      if (loc.bePath()) {
        loc.pathT("the context id loggedOut so we won't return it: " + ctx);
      }
      ctx = null;
    } else {
      if (loc.bePath()) {
        loc.pathT("returning client context: " + ctx);
      }
    }
    return ctx;
  }

  static ClientContextImpl getByAlias(String alias) {
    if (alias == null) {
      return null;
    }
    String clientId = aliasToClientID.get(alias);
    if (clientId == null) {
      return loadAliasToClientId(alias);
    } else {
      return getByClientId(clientId);
    }
  }

  private MonitoringNode monitoringNode = null;

  // will be used by the WD to count their sessions
  private int counter;
  private int counterLimit = -1;

  boolean additionalConcerned = false;

  private AdditionalNotification notificator = null;

  private String clientId;
  
 

private String alias;

  private String markId; // used from the web container and security for security session tracking
  private String protectionIp; // used from the web container for security reasons

  long creationTime = System.currentTimeMillis();
  long lastAccessed = -1;
  private transient LoginSessionImpl loginSession;
  private boolean loggedOut;
  private boolean hasApplicationSessions = false;
  private ScopeObserver scopeObserver = null;
    //can't be transient, because it's used for synchronization monitor
  private final String appSessionsMonitor = "monitor-" + this.hashCode();   

  /* transient fields */
  private transient AtomicInteger threadReferenceCount;
  private transient PersistentSessionModel persistedContext;
  private transient HashMap<String, Object> attributes;
  private transient HashMap<String, Object> localAttributes;
  private transient HashSet<ClientSession> appSessions;

  public ClientContextImpl() {
  }

  public long getCreationTime() {
  	return creationTime;
  }
  
  public long getLastAcessed() {
    if (lastAccessed == -1) {
      return System.currentTimeMillis();
    } else {
      return lastAccessed;
    }
  }
  private AtomicInteger threadReferenceCount() {
    if (threadReferenceCount == null) {
      synchronized (this) {
        if (threadReferenceCount == null) {
          threadReferenceCount = new AtomicInteger(0);
        }
      }
    }
    return threadReferenceCount;
  }

  private HashMap<String, Object> clientAttributes() {
    if (attributes == null) {
      synchronized (this) {
        if (attributes == null) {
          attributes = new HashMap<String, Object>(1);
        }
      }
    }
    return attributes;
  }

  private HashMap<String, Object> localAttributes() {
    if (localAttributes == null) {
      synchronized (this) {
        if (localAttributes == null) {
          localAttributes = new HashMap<String, Object>(3);
        }
      }
    }
    return localAttributes;
  }

  private HashSet<ClientSession> applicationSessions() {
    if (appSessions == null) {
      synchronized (this) {
        if (appSessions == null) {
          appSessions = new HashSet<ClientSession>(3);
        }
      }
    }
    return appSessions;
  }

  public void setProtectionIp(String protectionIp) {
    this.protectionIp = protectionIp;
  }

  public String getProtectionIp() {
    return protectionIp;
  }

  public void setMarkId(String markId) {
    this.markId = markId;
  }

  public String getMarkId() {
    return markId;
  }

  public synchronized void setClientId(String id) {
    if (clientId != null && !clientId.equals(id)) {
      IllegalStateException ie = new IllegalStateException("Changing client id is not allowed:" + clientId + " ->" + id);
      loc.traceThrowableT(Severity.WARNING, "", ie);
      throw ie;
    }
    clientId = id;
    if (loc.bePath()) {
      loc.pathT("client context assigned with clientId <" + clientId + "> " + this);
      loc.traceThrowableT(Severity.DEBUG, "set client ID", new Exception("trace"));
    }
    clientContexts.put(clientId, this);
    addAliasToClientId(alias, clientId);
    setIP();
  }

  

public synchronized void setAlias(String alias) {
    if (this.alias == null || this.alias.equals(clientId)){
      this.alias = alias;
      if (loc.bePath()) {
        loc.pathT("client context assigned with ALIAS <" + alias + ">" + this);
      }
      addAliasToClientId(this.alias, clientId);
    }
  }

  private boolean isAliasToClientIdMappingSet;

  private void addAliasToClientId(String alias, String clientId) {
    if (persistedContext == null) {
      if (alias != null && clientId != null && !loginSession().isAnonymous()) {
        persistClientContext();
      }
    }
    if (isAliasToClientIdMappingSet) { // mapping is already set
      return;
    }
    if ((alias != null) && (clientId != null) && !alias.equals(clientId)) {
      aliasToClientID.put(alias, clientId);
      storeAliasToClientId();
    }
  }

  /**
   * This method is called when the Client context should be shared in the cluster. 
   * Client context is shared when: 
   * - clientId is set 
   * and 
   * - Have same shared application session state that belongs to the client 
   * or 
   * - has a registered call back
   */
  protected synchronized void persistClientContext() {
    if (contextsPersistence != null) {
      try {
        try {
          persistedContext = contextsPersistence.createModel(clientId);
        } catch (PersistentStorageException e) {
          loc.infoT("Can't create persistent model for client context:" + clientId + "\n Get existing one.");
          loc.traceThrowableT(Severity.WARNING, "", e);
          persistedContext = contextsPersistence.getModel(clientId);
        }
        if (loc.bePath()) {
          loc.pathT("persisting client context: " + this);
        }
        persistedContext.setChunk(CLIENT_CONTEXT_CHUNK_NAME, this);
        persistedContext.setChunk(LOGIN_SESSION_CHUNK_NAME, loginSession);
        if (scopeObserver != null) {
          storeObserver(persistedContext, Scope.CLIENT_CONTEXT_SCOPE_NAME, scopeObserver);
        }
        // TODO - must call persistLoginSession() , when we separate the login session and the client context;
        Set<Map.Entry<String, Object>> set = clientAttributes().entrySet();
        for (Map.Entry<String, Object> entry : set) {
          if (entry.getValue() instanceof SoftHashMapEntry) {
            addAsChunk(entry.getKey(), ((SoftHashMapEntry) entry.getValue()).get());
            ((SoftHashMapEntry) entry.getValue()).loosen();
          } else {
            addAsChunk(entry.getKey(), entry.getValue());
          }
        }
      } catch (PersistentStorageException e) {
        SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, loc, "ASJ.ses.ps0008", 
            "Can not store client context for user : " + clientId + ". Problem with the persisting. Check the persistance state");
        loc.infoT("Can't create persistent model for client context:" + clientId);
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
  }

  private synchronized void storeAliasToClientId() {
    PersistentSessionModel aliasMapping = null;
    if (contextsPersistence != null) {
      try {
        try {
          aliasMapping = contextsPersistence.createModel("ALIAS:" + alias);
        } catch (PersistentStorageException e) {
          loc.infoT("Can't get persistent model for alias table. Trying to create one.");
          loc.traceThrowableT(Severity.WARNING, "", e);
          aliasMapping = contextsPersistence.getModel("ALIAS:" + alias);
        }
      } catch (PersistentStorageException e) {
        loc.infoT("Can't create persistent model for alias table");
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    } else {
      // impossible to get the model
      return;
    }

    if (aliasMapping != null) {
      try {
        if (loc.bePath()) {
          loc.pathT("Storing alias to client ID mapping:" + alias + "->" + clientId);
        }
        aliasMapping.lock(clientId);
        isAliasToClientIdMappingSet = true;
      } catch (PersistentStorageException e) {
        SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, loc, "ASJ.ses.ps0008", 
            "Can not store alias mapping to clientID. Problem with the persisting. Check the persistance state");
        loc.infoT("Can't store the alias mapping");
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
  }

  /**
   * update Observer in the storage
   * 
   * @param persistentModel - the persistent model of the ClientContext
   * @param chunkName observer chunk name
   * @param toStore observer
   * @throws PersistentStorageException - if there are problems with storage
   */
  private void storeObserver(PersistentSessionModel persistentModel, String chunkName, ScopeObserver toStore) throws PersistentStorageException {
    if (loc.bePath()) {
      loc.pathT("Store Scopes Observer:" + toStore);
    }
    if (toStore.isModified()) {
      persistentModel.setChunk(chunkName, toStore);
    }
  }

  public String getClientId() {
    return clientId;
  }

  public String getAlias() {
    return alias;
  }

  /**
   * @deprecated use loginSession()
   */
  public LoginSession getLoginSession() {
    return loginSession.getLoginSession();
  }

  public LoginSessionInterface loginSession() {
    if (loginSession == null) {
      loginSession = anonymousLoginSession;
    }
    return loginSession;
  }

  void setLoginSession(LoginSessionImpl session) {
    this.loginSession = session;
    this.loginSession.setClientContext(this);
    loggedOut = false;
    if (loc.bePath()) {
      loc.pathT("login session set to client context: " + this);
    }
  }

  public void addCallbackLocal(Callback callback) throws UserContextException {
  }

  public synchronized void addLocalAttribute(String key, Object value) throws UserContextException {
    if (value == null) {
      removeLocalAttribute(key);
    } else {
      localAttributes().put(key, value);
    }
  }

  public synchronized Object getLocalAttribute(String key) {
    return localAttributes().get(key);
  }

  private void removeLocalAttribute(String key) {
    localAttributes().remove(key);
  }

  public synchronized void addCallback(Callback callback) throws UserContextException, IOException {
    if (callback == null)
      return;
    if (clientAttributes().containsValue(callback)) {
      Set<Map.Entry<String, Object>> as = clientAttributes().entrySet();
      Object value;
      for (Map.Entry<String, Object> entry : as) {
        value = entry.getValue();
        if (value != null && !(value instanceof SoftHashMapEntry)) {
          if (value.equals(callback)) {
            clientAttributes().put(entry.getKey(), callback);
            addAsChunk(entry.getKey(), clientAttributes());
            break;
          }
        }
      }
    } else {
      String callbackKey = callback.getClass() + ":" + System.identityHashCode(callback);
      Object ob = clientAttributes().put(callbackKey, callback);
      addAsChunk(callbackKey, callback);
      if (ob != null) {
        loc.infoT("Dublicate key:" + callbackKey + "\n" + clientAttributes());
      }
    }
  }

  public synchronized void removeCallback(Callback callback) throws IOException {
    if (callback == null)
      return;

    if (clientAttributes().containsValue(callback)) {
      Set<Map.Entry<String, Object>> as = clientAttributes().entrySet();
      Object value;
      for (Map.Entry<String, Object> entry : as) {
        value = entry.getValue();
        if (value != null && !(value instanceof SoftHashMapEntry)) {
          if (value.equals(callback)) {
            clientAttributes().remove(entry.getKey());
            removeChunk(entry.getKey());
            break;
          }
        }
      }
    }
  }

  public synchronized void addAttribute(String key, Object value) throws UserContextException, IOException {
    if (loc.bePath()) {
      loc.pathT("adding attribute to client context<" + clientId + ">" + "\r\n" + key + " : " + value);
    }
    if (value == null) {
      removeAttribute(key);
    }
    if (value instanceof Callback) {
      clientAttributes().put(key, value);
      addAsChunk(key, value);
    } else {
      SoftHashMapEntry entry = new SoftHashMapEntry(value);
      clientAttributes().put(key, entry);
      if (addAsChunk(key, value))
        entry.loosen();
    }
  }

  public synchronized Object getAttribute(String key) throws UserContextException, IOException {
    if (loc.bePath()) {
      loc.pathT("getting attribute from client context<" + clientId + ">");
    }
    Object result = clientAttributes().get(key);
    if (result instanceof SoftHashMapEntry)
      result = ((SoftHashMapEntry) result).get();
    if (result == null && persistedContext != null) {
      result = persistedContext.getChunk(key);
      if (result != null) {
        Object entry = result instanceof Callback ? result : new SoftHashMapEntry(result);
        clientAttributes().put(key, entry);
      }
    }
    if (loc.bePath()) {
      loc.pathT("returning attibute<" + clientId + ">" + "\r\n" + key);
    }
    return result;
  }

  /**
   * 
   * @param key   key of the attribute
   * @param attribute object that should be stored as attribute
   * @return true if chunk is stored
   * @throws PersistentStorageException when problems with persistence occur
   */
  private boolean addAsChunk(String key, Object attribute) throws PersistentStorageException {
    if (persistedContext != null) {
      persistedContext.setChunk(key, attribute);
      return true;
    }

    return false;
  }

  private void removeChunk(String key) throws PersistentStorageException {
    if (persistedContext != null) {
      persistedContext.removeChunk(key);
    }
  }

  private void removeAttribute(String key) throws PersistentStorageException {
    clientAttributes().remove(key);
    removeChunk(key);
  }

  public void addSession(ClientSession session) {
    if (loc.bePath()) {
      loc.pathT("add session to client context: " + session);
    }
    if (session != null) {
      synchronized (appSessionsMonitor) {
        applicationSessions().add(session);
        invalidationTask.removeScheduledContext(this); // no need to be scheduled, the context will be cleaned up when the session expires 
        hasApplicationSessions = true;
      }
    }
    if (loc.bePath()) {
      loc.pathT("client context after add session: " + this);
    }
  }
  
  protected ClientContext internalAddSession(ClientSession session) {
    if (loc.bePath()) {
      loc.pathT("add session to client context: " + session);
    }
    if (session != null) {
      synchronized (appSessionsMonitor) {
        // in case the last session was invalidated in this request(specJ scenario), the context was loggedOut and has to be removed        
        if (loggedOut) {// we have to free the current client context(logged out) and create new one
          if (loc.bePath()) {
            loc.pathT("context has been logged out: " + this);
          }
          SessionContext sessionContext = SessionExecContext.getExecutionContext().sessionContext;
          String clientID = clientId;          
          SessionExecContext.getExecutionContext().empty();
          ClientContextImpl newContext = new ClientContextImpl();
          if (clientID == null) { // logout() is invoked, we'll use the sessionid for ClientID
            clientID = session.getSessionId();
          }          
          newContext.setClientId(clientID);          
          newContext.addSession(session);          
          newContext.setIP();
          SessionExecContext.applySessionContext(sessionContext, clientID);
          return newContext;
        } else { // context is not loggedOut 
          applicationSessions().add(session);
          invalidationTask.removeScheduledContext(this); // no need to be scheduled, the context will be cleaned up when the session expires
          hasApplicationSessions = true;
        }        
      }
    }
    if (loc.bePath()) {
      loc.pathT("client context after add session: " + SessionExecContext.getExecutionContext().currentClientContext());
    }
    return this;
  }  

  public int appSessionsSize() {
    if (hasApplicationSessions) {
      return applicationSessions().size();
    } else {
      return 0;
    }
  }

  public int runtimeSessionsSize() {
    return threadReferenceCount().intValue();
  }

  synchronized void addThreadReference() {
    if (loc.bePath()) {
      loc.pathT("adding thread reference: " + this);
   
      
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, "", new Exception("trace"));
      }
    }   
    if (loc.beInfo()) {
      try {
        loc.infoT("Add thread reference to the ClientContext with user: " + loginSession.getSubjectHolder().getPrincipal().getName() 
            + " (clientId: " + this.clientId + ")");              
      } catch (NullPointerException e) {
        loc.infoT("Add thread reference to the ClientContext with user: null");
      }
   }    
    threadReferenceCount().incrementAndGet();
  }

  void removeThreadReference() {
    if (loc.bePath()) {
      loc.pathT("removing thread reference: " + this);
    }    
    if (loc.beInfo()) {     
      try {
        loc.infoT("Removing thread reference from the ClientContext with user: " 
            + loginSession.getSubjectHolder().getPrincipal().getName() + " (clientId: " + this.clientId + ")");             
      } catch (NullPointerException e) {
        loc.infoT("Removing thread reference from the ClientContext with user: null");
      }
    }
    int currentThreadReferenceCount = threadReferenceCount().decrementAndGet();
    if (currentThreadReferenceCount == 0) { // last thread working with this context
      if (loggedOut) {
        try {
          destroy();
        } catch (UserContextException e) {
          loc.traceThrowableT(Severity.WARNING, "Client context failed to destroy : " + this, e);
        }
      } else if (loginSession().isAnonymous() && !hasApplicationSessions) {
        if (getClientId() != null) { // context with client id, may be still used
          invalidationTask.scheduleForInactivityCheck(this);
        } else { // empty context
          try {
            destroy();
          } catch (UserContextException e) {
            loc.traceThrowableT(Severity.WARNING, "Client context failed to destroy : " + this, e);
          }          
        }
        
      }
    }
    lastAccessed = System.currentTimeMillis();
    
    
    
    
    
//    if (loggedOut ||
//    // memory leak prevention:
//        // if there is a request with jsessionID to a public resource and no session is created,
//        // the client context has to be removed or scheduled for inactivity check, or it will stay forever
//        (loginSession().isAnonymous() && !hasApplicationSessions)) { 
//      if (getClientId() != null )  {
//        invalidationTask.scheduleForInactivityCheck(this);
//      } else {
//        if (currentThreadReferenceCount == 0) {
//          try {
//            destroy();
//          } catch (UserContextException e) {
//            loc.traceThrowableT(Severity.WARNING, "User context failed to destroy : " + this, e);
//          }
//          return;
//        }
//        synchronized (appSessionsMonitor) {
//          if (hasApplicationSessions) {
//            // avoiding concurrent modification exception
//            HashSet<ClientSession> sessions = new HashSet<ClientSession>(applicationSessions());
//  
//            // call before invalidate to all
//            for (ClientSession clientSession : sessions) {
//              try {
//                clientSession.beforeInvalidateClientSession();
//              } catch (Exception ex) {
//                loc.traceThrowableT(Severity.WARNING, "", ex);
//              }
//            }
//            // invalidate only if it's not active
//            for (ClientSession clientSession : sessions) {
//              try {
//                clientSession.invalidateIfNotActive();
//              } catch (Exception ex) {
//                loc.traceThrowableT(Severity.WARNING, "", ex);
//              }
//            }
//          }
//        }
//      }
//    } else if (currentThreadReferenceCount == 0 && getClientId() == null) {
//      try {
//        destroy();
//      } catch (UserContextException e) {
//        loc.traceThrowableT(Severity.WARNING, "User context failed to destroy : " + this, e);
//      }
//    }
//    lastAcessed = System.currentTimeMillis();
  }

  public void removeSession(ClientSession session) {
    if (hasApplicationSessions) {
      synchronized (appSessionsMonitor) {
        if (applicationSessions().remove(session) && applicationSessions().isEmpty()) {
          hasApplicationSessions = false;
          logout();
        }
      }
    }
  }

  public void logout() {
    if (loggedOut) {
      if (loc.bePath()) {
        loc.pathT("logout() invoked allready, will return:" + this);
        if (loc.beDebug()) {
          loc.traceThrowableT(Severity.DEBUG, "", new Exception("trace"));
        }
      }
      return; // already logged out
    } else {
      loggedOut = true;
      if (getClientId() != null) {
        removeAllClientContextData();
      }
    }
    if (loc.bePath()) {
      loc.pathT("logout() invoked:" + this);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, "", new Exception("trace"));
      }
    }
    if (hasApplicationSessions) {
      synchronized (appSessionsMonitor) {
        // new HashSet can't be avoided, because of concurrent modification exception
        HashSet<ClientSession> sessions = new HashSet<ClientSession>(applicationSessions());

        // to phase loginSession invalidation
        for (ClientSession clientSession : sessions) {
          try {
            clientSession.beforeInvalidateClientSession();
          } catch (Exception ex) {
            loc.traceThrowableT(Severity.DEBUG, "", ex);
          }
        }
        for (ClientSession clientSession : sessions) {
          try {
            clientSession.invalidateClientSession();
          } catch (Exception ex) {
            loc.traceThrowableT(Severity.DEBUG, "", ex);
          }
        }
      }
      applicationSessions().clear();
      hasApplicationSessions = false;
    }
    
    loginSession = anonymousLoginSession;

    if (loc.bePath()) {
      loc.pathT("checking reference count: " + this);
    }
    
    if (threadReferenceCount().intValue() == 0) { // if the logout is invoked from different client(Administrator)
      try {
        destroy();
      } catch (UserContextException e) {
        loc.traceThrowableT(Severity.WARNING, "User context failed to destroy : " + this, e);
      }
    } else { // the current thread is logged out
      SessionExecContext.threadContextProxy.currentContextObject().setThreadLoginSession(anonymousLoginSession);
    }
  }

  public synchronized void destroy() throws UserContextException {
    if (loc.bePath()) {
      loc.pathT("destroy client context <" + this + ">");
    }

     if (getClientId() != null && !loggedOut){
       removeAllClientContextData();
       if (loc.bePath()) {
         loc.pathT("destroy client context when there is no associated user");
       }
     }





    Set<Map.Entry<String, Object>> set = clientAttributes().entrySet();
    CallbackHandler handler;

    for (Map.Entry<String, Object> entry : set) {
      if (entry.getValue() instanceof Callback) {
        handler = UserContext.getCallbackHandler(((Callback) entry.getValue()).handlerName());
        try {
          handler.handle((Callback) entry.getValue());
        } catch (CallbackException e) {
          loc.traceThrowableT(Severity.ERROR, "", e);
        } catch (Exception e) {
          loc.traceThrowableT(Severity.ERROR, "", e);
        }
      }
    }
    clientAttributes().clear();
  }

  public Subject getSubject() {
    return loginSession().getSubject();
  }

  public void persistClientSession(ClientSession session) throws IOException {
    if (getAlias() == null) {
      setAlias(clientId);
    }
    if (hasApplicationSessions && applicationSessions().contains(session)) {
      addAsChunk(session.getSessionId(), SessionContextFactory.getInstance().lockInfo());
    }
  }

  public void removeClientSession(ClientSession session) {
    if (loc.bePath()) {
      loc.pathT("Remove client session:" + session + " from:" + this);
    }
    if (hasApplicationSessions) {
      boolean needsLogout = false;
      synchronized (appSessionsMonitor) {
        if (applicationSessions().remove(session) && applicationSessions().isEmpty()) {
          hasApplicationSessions = false;
          needsLogout = true;
          if (loc.bePath()) {
            loc.pathT("client context has no more sessions: " + this);
          }          
        }
      }
      if (needsLogout) {
        logout();
      }
    }

  }

  /**
   * Checks if the client context is eligible for cleanup because of in
   * @return true if it has been inactive for a period of time (1 hour), and has no sessions, and hs no attributes
   */
  public boolean isContextExpired() {
    boolean timeoutOK = (lastAccessed != -1) && (lastAccessed + INVALIDATION_TASK_PERIOD) < System.currentTimeMillis();
    boolean sessionsOK = ( (appSessions == null) || appSessions.isEmpty() );
    boolean attributesOK = ( (attributes == null) || attributes.isEmpty() ); // hak - ?
    boolean expiredOK = timeoutOK && sessionsOK && attributesOK;
    if (expiredOK) {
      return true;
    } else {
      return false;
    }
  }

  public String toString() {
    String CRLF = "\r\n";
    StringBuilder sb = new StringBuilder(CRLF);
    sb.append(super.toString()).append(CRLF);
    sb.append("clientId=" + clientId).append(CRLF);
    sb.append("alias=" + alias).append(CRLF);
    sb.append("login session=" + loginSession).append(CRLF);
    sb.append("user=" + getUser()).append(CRLF);
    sb.append("hasApplicationSessions=" + hasApplicationSessions).append(CRLF);
    sb.append("threadReferenceCount=" + threadReferenceCount().intValue()).append(CRLF);
    if (hasApplicationSessions) {
      sb.append("applicationSessions=" + applicationSessions()).append(CRLF);
    }
    sb.append("persistent Model=" + persistedContext).append(CRLF);
    sb.append("last accessed = " + new Date(lastAccessed)).append(CRLF);
    sb.append("client ip = " + (this.ip != null ? ip : "N/A"));
    return sb.toString();
  }

  public static class SoftHashMapEntry extends SoftReference {

    private int hashCode;

    private String toString;

    // just to keep hard ref to the object
    private transient Object referent;

    public SoftHashMapEntry(Object referent) {
      super(referent);
      if (referent != null) {
        hashCode = referent.hashCode();
        toString = referent.getClass() + ":" + System.identityHashCode(referent);
        this.referent = referent;
      }
    }

    public int hashCode() {
      return hashCode;
    }

    public boolean equals(Object obj) {
      Object referent = get();
      return referent != null && referent.equals(obj);
    }

    public void loosen() {
      this.referent = null;
    }

    public String toString() {
      return toString + " ";
    }
  }

  public void concernAdditionalStates(AdditionalNotification notificator) {
    this.notificator = notificator;
    this.additionalConcerned = true;
  }

  public boolean isConcernedForAdditionalStates() {
    return this.additionalConcerned;
  }

  public void exitsThread() {
    this.notificator.clearNotification(this);
  }

  public Scope getCurrentScope() {
    if (scopeObserver == null) {
      synchronized (this) {
        if (scopeObserver == null) {
          try {
            this.scopeObserver = Scope.createScope(this.toString(), Scope.CLIENT_CONTEXT_SCOPE_TYPE);
          } catch (NotSupportedScopeTypeException e) {
            if (loc.beDebug()) {
              loc.throwing(e);
            }
            return null;
          }
        }
      }
    }
    return scopeObserver.getScope();
  }

  public Set getAppSessions() {
    if (hasApplicationSessions) {
      return (HashSet) applicationSessions().clone();
    } else {
      return new HashSet();
    }
  }

  public void updateMonitoringObject() {
    if (this.monitoringNode == null) {
      this.monitoringNode = new UserContextMonitoringNode(this.toString(), this);
    }
  }

  public MonitoringNode getMonitoredObject() {
    updateMonitoringObject();
    return this.monitoringNode;
  }

  public String getPersistentModel() {
    if (this.persistedContext != null) {
      return this.persistedContext.toString();
    }
    return null;
  }

  /**
   * Sets the max counter limit
   * 
   * @param limit the limit / -1 value for no limit
   */
  public void setMaxCounterLimit(int limit) {
    this.counterLimit = limit;
  }

  /**
   * Gets the counter limit
   * 
   * @return the limit
   */
  public int getMaxCounterLimit() {
    return counterLimit;
  }

  /**
   * Increases the loginSession counter by 1
   * 
   * @throws CounterLimitException if the counting limit is reached
   */
  public void increaseCounter() throws CounterLimitException {
    if (counter == counterLimit) {
      throw new CounterLimitException("Session counter limit is reached : " + counterLimit);
    }
    counter++;
  }

  /**
   * Decreases the loginSession counter by 1
   */
  public void decreaseCounter() {
    counter--;
  }

  /**
   * Gets the current counter value
   * 
   * @return the counter value
   */
  public int getCurrentCount() {
    return counter;
  }

  /**
   * Gets the left count
   * 
   * @return the left count
   */
  public int getLeftCount() {
    return counterLimit - counter;
  }

  void persistLoginSession() {
    if (persistedContext != null) {
      try {
        persistedContext.setChunk(LOGIN_SESSION_CHUNK_NAME, loginSession);
      } catch (PersistentStorageException e) {
        loc.infoT("Can't persist Login Session for client context:" + clientId);
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    } else if (clientId != null && getAlias() != null) {
      persistClientContext();
    }
  }
  
  void persistScopeObserver(){
    if (persistedContext != null) {
      try {
        if (scopeObserver != null) {
          storeObserver(persistedContext, Scope.CLIENT_CONTEXT_SCOPE_NAME, scopeObserver);
        }
      } catch (PersistentStorageException e) {
        loc.infoT("Can't persist Scope Observer for client context:" + clientId);
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
  }

  //The ip is set during setClientID call
  //by consulting the execution details of the running thread.
  public String getIP() {
	return ip;
  }
  
  //This method sets the IP of the client context
  private void setIP() {
	  ExecutionDetails details =  SessionExecContext.getExecutionContext().getDetails();
	    if (details != null){
	    	this.ip = details.getHost();
	    }
  }
  
}
