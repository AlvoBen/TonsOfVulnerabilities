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

import com.sap.engine.session.*;
import com.sap.engine.session.scope.ScopeObserver;
import com.sap.engine.session.scope.Scope;
import com.sap.engine.session.scope.exception.AlreadySetScopeException;
import com.sap.engine.session.usr.*;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.http.HttpRuntimeSessionModel;
import com.sap.engine.session.trace.Locations;
import com.sap.engine.session.trace.Trace;
import com.sap.engine.session.trace.ThrTrace;
import com.sap.engine.session.data.LifecycleManagedData;
import com.sap.engine.session.data.SessionChunk;
import com.sap.engine.system.ThreadWrapper;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;
import java.text.MessageFormat;
/*
 * This class present the Session execution context of the user.
 * And is the session management thread context object.
 * The execution context cover the session state of the user in
 * different components (for example HttpSession in diff applications).
 *
 * Author: i024157 /Georgi Stanev/, Nikolai Neichev
 */
public class SessionExecContext implements ContextObject {
  public static Location loc = Locations.EXEC_LOC;
  protected static ThreadContextProxy threadContextProxy = null;
  public static boolean isEnabledSharedMemoryMonitoring;

  static final String WEB_SESSION_CONTEXT = "HTTP_Session_Context";
  static final ConcurrentHashMap<String, ClientContext> clContexts = new ConcurrentHashMap<String, ClientContext>();
  
  
  private static final Collection<SessionChunk> emptySessionChunkCollection = Collections.emptySet();
  
  public static void setThreadContextProxyImpl(ThreadContextProxy impl) {
    threadContextProxy = impl;
    ClientContextImpl.init();
  }

  Map<String, ScopeObserver> getScopes() {
    if (scopes == null) {
      synchronized(this) {
        if (scopes == null) {
          scopes = Collections.synchronizedMap(new HashMap<String, ScopeObserver>(3));
        }
      }
    }
    return scopes;
  }

  public static SessionExecContext getExecutionContext() {
    if (threadContextProxy != null) {
      return threadContextProxy.currentContextObject();
    }
    return new SessionExecContext("System");
  }  

  /**
   * Apply the session context for a client (in terms of http client, p4 client...). One thread can be run on only one
   * session context.
   * @param sessionContext current Session Context
   * @param clientId client Id
   * @return current Session context object
   */
  public static SessionExecContext applySessionContext(SessionContext sessionContext, String clientId) {
    if (loc.bePath()) {
      loc.entering("applySessionContext(SessionContext sessionContext, String clientId)\n" +
      sessionContext + "," + clientId);
    }
    if (threadContextProxy != null) {
      SessionExecContext sessionContextObject = threadContextProxy.currentContextObject();
      if (sessionContextObject.clientContextId == null ) {
        sessionContextObject.sessionContext = sessionContext;
        sessionContextObject.clientContextId = clientId;
        ClientContextImpl ctx = ClientContextImpl.getByClientId(clientId);
        if (ctx != null) {
          // TODO - dead code ? ako sessionContextObject.clientContextId == null => 
          // sessionContextObject.isClientContextApplied() = false
          if (sessionContextObject.isClientContextApplied() && (sessionContextObject.currentClientContext() != ctx)) {
            sessionContextObject.currentClientContext().removeThreadReference();
          }
          // TODO - if-a e dead code - ako sessionContextObject.clientContextId == null =>
          // sessionContextObject.currentClientContext e vinagu null 
          if (sessionContextObject.currentClientContext != ctx) {
            ctx.addThreadReference();
          }
          sessionContextObject.currentClientContext = ctx;
        } else {
          sessionContextObject.currentClientContext().setClientId(clientId);
        }
        ThreadWrapper.setSessionID("" + clientId.hashCode());        
        if (loc.bePath()) {
          loc.exiting(sessionContextObject);
        }
        return sessionContextObject;
      } else if ( sessionContextObject.clientContextId.equals(clientId)) {
        return sessionContextObject;
      } else {
        IllegalStateException is = null;
        StringBuilder log = new StringBuilder("Session context is already set.\n");
        if(sessionContextObject.sessionContext == null){
          log.append("Can't change from <null:" + 
              sessionContextObject.clientContextId + "> to <");
        }else{
          log.append("Can't change from <" + sessionContextObject.sessionContext.getName() + ":" + 
              sessionContextObject.clientContextId + "> to <");
        }
        if(sessionContext == null){
          log.append("null:" + clientId + ">");
        }else{
          log.append(sessionContext.getName() + ":" + clientId + ">");
        }
        is = new IllegalStateException(log.toString());
        loc.traceThrowableT(Severity.DEBUG,"",is);
        throw is;
      }
    }
    IllegalStateException is = new IllegalStateException("Thread System not available.");
    loc.traceThrowableT(Severity.DEBUG,"",is);
    throw is;

  }
  
  @Deprecated
  public static boolean applySessionContext(String clientId) {
    throw new UnsupportedOperationException("method is not used");
  }

  public static ClientContextImpl applyClientContext(ClientContextImpl clientContextToApply) {
    if (clientContextToApply == null) {
      return null;
    }
    ClientContextImpl currentContext = SessionExecContext.getExecutionContext().currentClientContext;
    
    SessionExecContext.getExecutionContext().currentClientContext = clientContextToApply;
    
    SessionExecContext.getExecutionContext().clientContextId = clientContextToApply.getClientId();
    
    return currentContext;
  }

  public boolean applyUserContext(Object clientId) {
    if (loc.bePath()) {
      loc.pathT("applyUserContext(Object) is called:" + clientId +
              "\n Current context object is :" + this);
    }
    if (clientId == null) {
      if (loc.bePath()) {        
        loc.pathT("applyUserContext(Object) is called with clientId null:" +
            "\n Current context object is:" + this);
        if (loc.beDebug()) {
          loc.traceThrowableT(Severity.DEBUG, "stacktrace" , new Exception());
        }
      }
      setThreadLoginSession(ClientContextImpl.anonymousLoginSession);
    } else {
      if (isClientContextApplied()) {
        setThreadLoginSession(currentClientContext().loginSession());
      } else {
        setThreadLoginSession(ClientContextImpl.anonymousLoginSession);
      } 
    } 
    
    return true;
  }

  /* keeps all registered Scope Objects for the currenct Execution Context */
  private Map<String, ScopeObserver> scopes;

  public LoginSessionInterface threadLoginSession = ClientContextImpl.anonymousLoginSession;

  /* registered Scope for run */
  Scope scopeForRun = null;

  String key;

  SessionContext sessionContext;

  String clientContextId;

  /**
   * the user context that belongs to this client context if there is not user context for this client the current one
   * (from current thread) is associated with it
   */           
  ClientContextImpl currentClientContext;

  boolean isLoadbalanced;

  public SessionExecContext() {
  }  

  public SessionExecContext(String contextId) {
    this.key = contextId;
  }

  public ClientContextImpl currentClientContext() {
    if (currentClientContext == null) { 
      synchronized(this) {
        if (currentClientContext == null) {
          //TODO - prisvoiavaneto na currentClientContext da se napravi v setMethod, koito shte increazeva count-a
          currentClientContext = new ClientContextImpl();
          currentClientContext.addThreadReference();
        }
      }
    }
    return currentClientContext;
  }

  public boolean isClientContextApplied() {
    return currentClientContext != null;
  }

  // TODO - use this method in order to refresh the context from the DB only once(performance otimisation)  
  
//  private boolean refreshed = false;

//  private ClientContextImpl getByClientIdRefresh(String clientId) {
//    if (refreshed) {
//      return ClientContextImpl.clientContexts.get(clientId);
//    } else {
//      refreshed = true;
//      return ClientContextImpl.getByClientId(clientId);
//    }
//  }
  
  
  //check for usages. It seems this method is not used
  public UserContext applyClientContext() {
    if (loc.bePath()) {
      loc.entering("applyClientContext()");
    }

    //check za novata impl
    if (threadContextProxy != null) {
      ClientContextImpl usr = ClientContextImpl.getByClientId(clientContextId);
      if (loc.bePath()) {
        loc.exiting(usr);
      }
      return usr;
    }
    IllegalStateException is = new IllegalStateException("Thread System not available.");
    loc.traceThrowableT(Severity.DEBUG,"",is);
    throw is;
  }

  public ClientContext addSessionToClientContext(String sessionId, ClientSession session) {
    if (sessionId == null) {
      return null;
    }
    if (threadContextProxy != null) {
//      if (isClientContextApplied() && !currentClientContext.isLoggedOut()) {
//        if (loc.beDebug())
//          loc.debugT("Add client session " + session + " to current session context:" + currentClientContext());
//        //add session to current client context        
        return currentClientContext().internalAddSession(session);
//      } else {
//        ClientContextImpl clientContext = ClientContextImpl.getByClientId(clientId);
//        if (clientContext != null) {
//          if (loc.beDebug())
//            loc.debugT("Add client session " + session + " to current session context:" + clientContext);
//          //add session to current client context        
//          return clientContext.internalAddSession(session);
//        } else {
//          empty();
//          currentClientContext().setClientId(clientId);
//          return currentClientContext().internalAddSession(session);
//        }
//      } 
    }
    return null;
  }

  public void sheduleSessionPasivation(RuntimeSessionModel session) {
    if (threadContextProxy != null) {
      threadContextProxy.sheduleSessionPassivation(session);
    }
  }

  public Scope getScope(String scopeType){
    if(scopeType.equalsIgnoreCase(Scope.CLIENT_CONTEXT_SCOPE_TYPE)){
      return UserContext.getCurrentUserContext().getCurrentScope();
    }
    ScopeObserver _observer = getScopes().get(scopeType);
    return ( _observer == null ? null :_observer.getScope());
  }

  public void registerScope(String type, ScopeObserver observer) throws AlreadySetScopeException {
    if (getScopes().containsKey(type)) {
      String msg = MessageFormat.format("There is already set Scope<{0}> in the Context for the input type<{1}>", observer.getScope() , type);
      if(loc.beDebug()){
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      throw new AlreadySetScopeException(msg);
    }
    getScopes().put(type, observer);
  }

  public ScopeObserver unregisterScope(String type){
    return getScopes().remove(type);
  }

  public synchronized void setScopeForRun(Scope scope){
    this.scopeForRun = scope;
  }

  public synchronized Scope removeScopeForRun(){
    Scope temp = this.scopeForRun;
    this.scopeForRun = null;
    return temp;
  }

  public synchronized Scope getScopeForRun(){
    return this.scopeForRun;
  }

  public final void setContextId(String id) {
    this.key = id;
  }

  public final String contextId() {
    return key;
  }

  public final void setIsLoadbalanced(boolean flag) {
    isLoadbalanced = flag;
  }

  public final boolean isLoadbalanced() {
    return isLoadbalanced;
  }

  void clear() {
    if (isClientContextApplied() && currentClientContext().isConcernedForAdditionalStates()) {
      currentClientContext().exitsThread();
    }
    key = "";
    sessionContext = null;
    clientContextId = null;
    scopeForRun = null;
    getScopes().clear();
  }

  /**
   * Sets the max counter limit and resets the count to 0
   * @param limit the limit / -1 value for no limit
   */
  public synchronized void setMaxCounterLimit(int limit) {
    if (!isClientContextApplied()) {
      throw new IllegalStateException("No associated user");
    } else {
      currentClientContext().setMaxCounterLimit(limit);
    }
  }

  /**
   * Gets the counter limit
   * @return the limit
   */
  public synchronized int getMaxCounterLimit() {
    if (!isClientContextApplied()) {
      throw new IllegalStateException("No associated user");
    } else {
      return currentClientContext().getMaxCounterLimit();
    }
  }

  /**
   * Increases the session counter by 1
   * @throws CounterLimitException if the counting limit is reached
   */
  public synchronized void increaseCounter() throws CounterLimitException {
    if (!isClientContextApplied()) {
      throw new IllegalStateException("No associated user");
    } else {
      currentClientContext().increaseCounter();
    }
  }

  /**
   * Decreases the session counter by 1
   */
  public synchronized void decreaseCounter() {
    if (!isClientContextApplied()) {
      throw new IllegalStateException("No associated user");
    } else {
      currentClientContext().decreaseCounter();
    }
  }

  /**
   * Gets the current counter value
   * @return the counter value
   */
  public synchronized int getCurrentCount() {
    if (!isClientContextApplied()) {
      throw new IllegalStateException("No associated user");
    } else {
      return currentClientContext().getCurrentCount();
    }
  }

  /**
   * Gets the left count
   * @return the left count
   */
  public synchronized int getLeftCount() {
    if (!isClientContextApplied()) {
      throw new IllegalStateException("No associated user");
    } else {
      return currentClientContext().getLeftCount();
    }
  }

  public final SessionChunk getSessionChunk(String domain, String sessionId, String chunk) throws IllegalStateException, SessionException {
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(sessionId);
        try {
          Session session;
          try {
           session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(sessionId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(sessionId);
          }
          if (session != null) {
            return (SessionChunk) session.getChunkData(chunk);
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    throw new IllegalStateException("The thread is out of session scope.\nSession Domain = " + domain + " sessionId = "
            + sessionId + " chunk name = " + chunk);

  }

  public final SessionChunk getSessionChunk(String domain, String chunk) throws IllegalStateException, SessionException {
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    return getSessionChunk(domain, clientId, chunk);
    
  }
  
  /**
   * Does nothing and always returns FALSE
   * @deprecated will be removed
   */
  public boolean isSessionChunkLimitReached(String domain, String sessionId) throws IllegalStateException, SessionException {
    return false;
  }

  public final void addSessionChunk(String domain, String chunkName, SessionChunk chunk) throws IllegalStateException, SessionException {
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    addSessionChunk(domain, clientId, chunkName, chunk);
  }
  
  public final void addSessionChunk(String domain, String sessionId, String chunkName, SessionChunk chunk) throws IllegalStateException, SessionException {
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(sessionId);
        try {
          Session session = holder.getSession();
          if (session != null) {
            if(loc.bePath()){
              loc.pathT("\r\nAdd chunk with name - " + chunkName + "\r\n" +
                        " to session with id - " + sessionId + "\r\n" +
                        " and domain - " + session.domain());
            }
            session.addChunkData(chunkName, chunk);
            return;
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    throw new IllegalStateException("The thread is out of session scope.\nSession Domain = " + domain + " sessionId = "
            + sessionId + " chunk name = " + chunkName);
  }

  public final void removeSessionChunk(String domain, String chunkName) throws IllegalStateException, SessionException {
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    removeSessionChunk(domain, clientId, chunkName);  
  }
  
  public final void removeSessionChunk(String domain, String sessionId, String chunkName) throws IllegalStateException, SessionException {
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(sessionId);
        try {
          Session session;
          try {
           session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(sessionId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(sessionId);
          }
          if (session != null) {
            session.removeChunk(chunkName);
            return;
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    throw new IllegalStateException("The thread is out of session scope.\nSession Domain = " + domain + " \nSessionId = "
            + sessionId + " \nChunk name = " + chunkName);
  }

  public final void setSessionTimeout(String domain, int timeout) throws IllegalStateException, SessionException {
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    setSessionTimeout(domain, clientId, timeout);
  }

  public final void setSessionTimeout(String domain, String sessionId, int timeout) throws IllegalStateException, SessionException {
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
        if (context != null) {
          SessionDomain dom = context.findSessionDomain(domain);
          if (dom != null) {
            SessionHolder holder = dom.getSessionHolder(sessionId);
            try {
              Session session;
              try {
               session = holder.getSession();
              } catch (SessionNotFoundException e) {
                session = dom.getSessionImmutable(sessionId);
              } catch (IllegalStateException e) {
                session = dom.getSessionImmutable(sessionId);
              }
              if (session != null) {
                session.setMaxInactiveInterval(timeout);
                return;
              }
            } finally {
              holder.releaseAccess();
            }
          } else {
            throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
          }
        } else {
          throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
        }
        throw new IllegalStateException("The thread is out of session scope.\nSession Domain = " + domain + " \nSessionId = "
                + sessionId);
    }

   /**
   * Add runtime session dependancy from the <code>LifecycleManagedData</code> object to the specified <code>
   * SessionDomain </code>.
   *
   * @param domain     <code>SessionDomainName</code> path
   * @param sessionId session Name
   * @param chunkName chunk's name
   * @param moduleName the name of the module from with
   * @throws IllegalStateException ->
   * @throws SessionException ->
   */
  public final void addRuntimeSessionDependency(String domain, String sessionId, String chunkName, String moduleName) throws IllegalStateException, SessionException {
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      SessionDomain dependentModule = context.findSessionDomain(moduleName);
      if (dependentModule == null) {
        SessionException ex = new SessionException("No such module:" + moduleName);
        if (Trace.beDebug())
          Trace.logError(ex);

        throw ex;
      }
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(sessionId);
        try {
          Session session;
          try {
            session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(sessionId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(sessionId);
          }
          if (session != null) {
            session.addRuntimeDependency(dependentModule.getReference(), chunkName);
            return;
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    throw new IllegalStateException("The thread is out of session scope.\nSession Domain = " + domain + " \nSessionId = "
            + sessionId + " \nChunk name = " + chunkName);
  }

  public final void addRuntimeSessionDependency(String domain, String chunkName, String moduleName) throws IllegalStateException, SessionException {
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    addRuntimeSessionDependency(domain, clientId, chunkName, moduleName);
  }
  
  public final void removeRuntimeSessionDependency(String domain, String sessionId, String chunkName, String moduleName) throws IllegalStateException, SessionException {
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      SessionDomain dependentModule = context.findSessionDomain(moduleName);
      if (dependentModule == null) {
        SessionException ex = new SessionException("No such module:" + moduleName);
        if (Trace.beDebug())
          Trace.logError(ex);

        throw ex;
      }
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(sessionId);
        try {
          Session session;
          try {
            session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(sessionId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(sessionId);
          }
          if (session != null) {
            session.removeRuntimeDependancy(dependentModule.getReference(), chunkName);
            return;
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    throw new IllegalStateException("The thread is out of session scope.\nSession Domain = " + domain + " \nSessionId = "
            + sessionId + " \nChunk name = " + chunkName);
  }

  public void concernAdditionalStates(AdditionalNotification notificator){
    if (isClientContextApplied()) {
      currentClientContext().concernAdditionalStates(notificator);
    } else {
      if(loc.beDebug()){
        loc.debugT("the User Context is null! and the notification will not work!");
        loc.traceThrowableT(Severity.ERROR, "JUST LOG", new Exception());
      }
    }
  }

  LoginSessionInterface internalGetThreadLoginSession() {
    return threadLoginSession;
  }

  LoginSessionInterface internalGetClientLoginSession() {
    if (isClientContextApplied()) {
      return currentClientContext().loginSession();
    } else {
      return ClientContextImpl.anonymousLoginSession; 
    }
  }

 // ------------------------------------- ThreadContext ---------------------------------------

  public static final String THREAD_CONTEXT_OBJECT_NAME = "session.thread.context";

  public static SessionExecContext getCurrent() {
    if (SessionExecContext.threadContextProxy != null) {
      return SessionExecContext.threadContextProxy.currentContextObject();
    }
    return null;
  }

  private String tenancyID = null;

  HashSet<RuntimeSessionModel> sessionsToPersist;

  public String getTenancyID() {
    return tenancyID;
  }

  public void setTenancyID(String tenancyID) {
    this.tenancyID = tenancyID;
  }

  public void shedule(RuntimeSessionModel session) {
    if (sessionsToPersist == null) {
      sessionsToPersist = new HashSet<RuntimeSessionModel>(2);
    }
    sessionsToPersist.add(session);
  }

  public ContextObject childValue(ContextObject parent, ContextObject child) {
    SessionExecContext childContext;
    if (child != null) {
      childContext = ((SessionExecContext)child);
    } else {
      childContext = new SessionExecContext();
    }
    SessionExecContext parentContext = (SessionExecContext) parent;
    // nest all values
    childContext.setThreadLoginSession(parentContext.getThreadLoginSession());
    childContext.clientContextId = parentContext.clientContextId;
    childContext.sessionContext = parentContext.sessionContext;
    if (isClientContextApplied()) {
      childContext.currentClientContext = parentContext.currentClientContext;
      currentClientContext().addThreadReference();
    }
    childContext.tenancyID = parentContext.tenancyID;
    Map<String, ScopeObserver> parentContextScopes = null;
    if(parentContext.scopes != null){
      parentContextScopes = Collections.synchronizedMap(new HashMap<String, ScopeObserver>(parentContext.scopes));
    }
    childContext.scopes = parentContextScopes;
    childContext.scopeForRun = parentContext.scopeForRun;
    childContext.key = parentContext.key;
    childContext.isLoadbalanced = parentContext.isLoadbalanced;
    childContext.tenancyID = parentContext.tenancyID;
    childContext.sessionsToPersist = parentContext.sessionsToPersist;

    return childContext;
  }

  public ContextObject getInitialValue() {
    return new SessionExecContext();
  }

  public void empty() {
    try {
      if (sessionsToPersist != null) {
        for (RuntimeSessionModel session : sessionsToPersist) {
          session.persistAndCommit(null);
        }
      }
    } catch(Throwable t) {
      Trace.logException(t);
    } finally{
      sessionsToPersist = null;
    }
    // make all values null
    // TODO remove na currentClientContext da se napravi prez method, kideto shte decrease-va count-a
    if (isClientContextApplied()) {
      currentClientContext().persistScopeObserver();      
      currentClientContext().removeThreadReference();
    }
    currentClientContext = null;
    clientContextId = null;
    sessionContext = null;
    scopes = null;
    setThreadLoginSession(ClientContextImpl.anonymousLoginSession);
    scopeForRun = null;
    key = null;
    isLoadbalanced = false;
    tenancyID = null;
    sessionsToPersist = null;
  }
  

  public Collection<SessionChunk> getSessionChunks(String sessionDomain, String clientId) throws SessionException {
    Collection<SessionChunk> sessionChunks = new HashSet<SessionChunk>();
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(sessionDomain);
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(clientId);
        
        try {
          Session session;
          try {
           session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(clientId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(clientId);
          }
          if (session != null) {
            for(Object sessionChunk : session.chunks().values()){
              if(sessionChunk instanceof SessionChunk){
                if(!(sessionChunk instanceof LifecycleManagedData)){
                  sessionChunks.add((SessionChunk) sessionChunk);
                }
              }
            }
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + sessionDomain);
      } 
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    return sessionChunks;
  }

  /*
   * return Collection of session chunks corresponding to the given clientId
   */
  public Collection<SessionChunk> getSessionChunks(String clientId) throws SessionException{
  	if (clientId == null) {
  		return emptySessionChunkCollection;
  	}
	  
    Collection<SessionChunk> sessionChunks = new HashSet<SessionChunk>();
    ClientContextImpl context = null;
    if (clientId.equals(clientContextId) && isClientContextApplied()) {
    	context = currentClientContext;
    }
    if (context == null){
    	context = ClientContextImpl.getByClientId(clientId);
    }
    if (context != null){
      for(Object appSession : context.getAppSessions()){
        if(appSession instanceof HttpRuntimeSessionModel){
          HttpRuntimeSessionModel rsm = (HttpRuntimeSessionModel) appSession;
          if (rsm.session() != null) {
            for(Object sessionChunk : rsm.session().chunks().values()){
              if(sessionChunk instanceof SessionChunk){
                if(!(sessionChunk instanceof LifecycleManagedData)){
                  sessionChunks.add((SessionChunk) sessionChunk);
                }
              }
            }
          }
        }
      }
    }else {
      throw new SessionNotFoundException("Client context not found:" + clientId);
    }
    return sessionChunks;
  }
  
  /*
   * @return  the number of all session chunks corresponding to the given clientId
   *          <code>0</code> if there is no such clientId
   */
  public int getNumberOfSessionChunks(String domain) throws SessionException {
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    return getSessionChunks(domain, clientId).size();
  }
  
  public int getNumberOfSessionChunks() throws SessionException {
	String clientId = currentClientContext().getClientId();  
	if (clientId == null) {
	      clientId = clientContextId;
	}
    return getSessionChunks(clientId).size();
  }
  
//  public int getNumberOfSessionChunks(String domain){
//    
//  }
  
  public int getSessionTimeout(String domain, String clientId) throws IllegalStateException, SessionException {
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(clientId);
        try {
          Session session;
          try {
           session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(clientId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(clientId);
          }
          if (session != null) {
            return session.getMaxInactiveInterval();
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    return 0;
  }
  
  public int getSessionTimeout(String domain) throws IllegalStateException, SessionException {
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    return getSessionTimeout(domain, clientId);
  }
  
  public void addLifecycleManagedAttribute(String domain, String dataName, LifecycleManagedData data)  throws IllegalStateException, SessionException{
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(clientId);
        try {
          Session session;
          try {
           session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(clientId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(clientId);
          }
          if(session instanceof AppSession){
            ((AppSession) session).addLifecycleManagedAttribute(dataName, data);
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
  }
  
  public LifecycleManagedData removeLifecycleManagedAttribute(String domain, String dataName) throws IllegalStateException, SessionException{
    LifecycleManagedData data = null;
    SessionContext context = SessionContextFactory.getInstance().getSessionContext(WEB_SESSION_CONTEXT, false);
    String clientId = currentClientContext().getClientId();
    if (clientId == null) {
      clientId = clientContextId;
    }
    if (context != null) {
      SessionDomain dom = context.findSessionDomain(domain);
      if (dom != null) {
        SessionHolder holder = dom.getSessionHolder(clientId);
        try {
          Session session;
          try {
           session = holder.getSession();
          } catch (SessionNotFoundException e) {
            session = dom.getSessionImmutable(clientId);
          } catch (IllegalStateException e) {
            session = dom.getSessionImmutable(clientId);
          }
          if(session instanceof AppSession){
            data = ((AppSession) session).removeLifecycleManagedAttribute(dataName);
          }
        } finally {
          holder.releaseAccess();
        }
      } else {
        throw new SessionNotFoundException("Session domain not found:" + WEB_SESSION_CONTEXT + "/" + domain);
      }
    } else {
      throw new SessionNotFoundException("Session context not found:" + WEB_SESSION_CONTEXT);
    }
    return data;
  }
  
  /**
   * This variable contains execution details for remote method caller, 
   * when current thread is in processing remote request.
   * @see com.sap.engine.session.exec.ExecutionDetails
   */
  private ExecutionDetails<?> details = null;
  
  /**
   * Get details for runtime, when caller thread is in remote call. 
   * Returns null otherwise. 
   * @return ExecutionDetails<?> with remote caller details, if caller thread is in processing remote call;
   *         otherwise returns null;
   */
  public ExecutionDetails<?> getDetails(){
    return details;
  }
  
  /**
   * This method set execution details for remote communication, when thread enter in processing remote request.
   * The same method is used to erase the details, when thread exit from processing remote communication.
   * @param d initialized details for current thread or 
   *          null when thread exits from processing remote request to erase details.
   */
  public void setDetails(ExecutionDetails<?> d){
    this.details = d;
  }

  LoginSessionInterface getThreadLoginSession() {
    return threadLoginSession;
  }

  void setThreadLoginSession(LoginSessionInterface threadLoginSession) {
    if (threadLoginSession != null) {
      this.threadLoginSession = threadLoginSession;
      if (threadLoginSession.equals(ClientContextImpl.anonymousLoginSession) ||
          threadLoginSession.getPrincipal() == null ) {
        ThreadWrapper.setUser("");
      } else {
        ThreadWrapper.setUser(threadLoginSession.getPrincipal().getName());
      }
      if (loc.beDebug()) {
        loc.debugT("SessionContextObject setThreadLoginSession(). ThreadLoginSession: " + threadLoginSession);
      }
    }
  }
}
