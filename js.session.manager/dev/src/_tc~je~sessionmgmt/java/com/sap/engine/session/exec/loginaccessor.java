package com.sap.engine.session.exec;

import com.sap.engine.session.usr.LoginSessionInterface;
import com.sap.engine.session.usr.ClientContext;
import com.sap.engine.session.usr.UserContextException;
import com.sap.engine.session.trace.Locations;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;

import java.util.Collection;
import java.util.HashSet;
import java.io.IOException;

/**
 * Used by the security service to access the login session assigned to the thread
 */
public class LoginAccessor {

  static Location loc = Locations.LOGIN_LOC;

  /**
   * to be instantiated only from the package
  */
  LoginAccessor() {
  }

  /**
   * Used for login in the thread.
   * @return Login session instance is returned in order to be filled with the login info.
   */
  public LoginSessionInterface login() {
    LoginSessionInterface ls = getClientLoginSession();
    SessionExecContext currentContextObject = currentContextObject();
    if (loc.bePath()) {
      loc.pathT("login(): current client context: " + currentContextObject.currentClientContext());
    }
    if (ls.isAnonymous()) {
      LoginSessionInterface login = new LoginSessionImpl();
      currentContextObject.currentClientContext().setLoginSession((LoginSessionImpl) login);
      currentContextObject.setThreadLoginSession(login);
      if (currentContextObject.currentClientContext().getClientId() != null) {
        ClientContextImpl.clientContexts.put(currentContextObject.currentClientContext().getClientId(), currentContextObject.currentClientContext());
      }
      return login;
    } else {
      currentContextObject.setThreadLoginSession(ls);
      return ls;
    }
  }

  /**
   * Persists the client context in order to share it in the cluster
   */
  public void shareClientContextInCluster() {
//    currentContextObject().currentClientContext().persistClientContext();
  }

  /**
   * Returns the Login session asociated to the thread.
   * @return the login session
   */
  public LoginSessionInterface getThreadLoginSession() {
    if (loc.bePath()) {
      loc.pathT("getThreadLoginSession(): thread login session " + currentContextObject().getThreadLoginSession());
    }
    return currentContextObject().getThreadLoginSession();
  }

  /**
   * Returns the Login session associated to the client.
   * @return the login session
   */
  public LoginSessionInterface getClientLoginSession() {
    if (loc.bePath()) {
      loc.pathT("getClientLoginSession(): current client context " + currentContextObject().currentClientContext());
    }
    return currentContextObject().internalGetClientLoginSession();
  }

  /**
   * Logs out the client and the current thread
   */
  public void logout() {
    SessionExecContext current = currentContextObject();
    if (loc.bePath()) {
      loc.pathT("logout(): current client context : " + currentContextObject().currentClientContext() + " and threadloginsession: " + current.getThreadLoginSession()); 
    }
    current.currentClientContext().logout();
    current.setThreadLoginSession(ClientContextImpl.anonymousLoginSession);
  }

  /**
   * Sets the login session reference of the current session context to anonymous
   */
  public void setCurrentThreadAnonymous() {
    if (loc.bePath()) {
      loc.pathT("setCurrentThreadAnonymous(): current client context : " + currentContextObject().currentClientContext() + " and threadloginsession: " + currentContextObject().getThreadLoginSession());
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, "", new Exception());
      }
    }
    currentContextObject().setThreadLoginSession(ClientContextImpl.anonymousLoginSession);
  }
  
  /**
   * Empties the current context object so the same thread can be reused directly
   */
  public void emptyCurrentContextObject() {
    if (loc.bePath()) {
      loc.pathT("emptyCurrentContextObject(): current client context : " + currentContextObject().currentClientContext() + " and threadloginsession: " + currentContextObject().getThreadLoginSession());
    }
    currentContextObject().empty();
  }

  /**
   * Logs off the client with the specified client id
   * @param clientId the client id
   */
  public void terminate(String clientId) {
    ClientContextImpl context = ClientContextImpl.getByClientId(clientId);
    if (loc.bePath()) {
      loc.pathT("terminate(): client context : " + context);
    }
    context.logout();
  }

  /**
   * Sets an alias to the current client context
   * @param alias the alias
   */
  public void setAliasToClientContext(String alias) {
    if (loc.bePath()) {
      loc.pathT("setAliasToClientContext(): alias: " + alias+" for client context: " + currentContextObject().currentClientContext() + " and threadloginsession: ");
    }
    currentContextObject().currentClientContext().setAlias(alias);
  }

  /**
   * Applies the client context corresponding to the specified alias to the current thread
   * @param alias the alias
   */
  public void applyClientContextToCurrentThreadByAlias(String alias) {
    if (loc.bePath()) {
      loc.pathT("applyClientContextToCurrentThreadByAlias(): alias: " + alias+" for client context: " + currentContextObject().currentClientContext());
    }
    if (alias == null) {
      throw new IllegalArgumentException("alias is null");
    }
    String clientId = ClientContextImpl.getByAlias(alias).getClientId();
    applyClientContextToCurrentThreadById(clientId);
  }

  /**
   * Returns the client context corresponding to this alias
   * @param alias the alias
   * @return the client context
   */
  public ClientContext getClientContextByAlias(String alias) {
    ClientContextImpl context = ClientContextImpl.getByAlias(alias);
    if (loc.bePath()) {
      loc.pathT("getClientContextByAlias(): alias: " + alias +" for client context: " + context);
    }
    return context;
  }

  /**
   * Sets a clientId to the current client context
   * @param clientId the client id
   */
  public void setClientIdToClientContext(String clientId) {
    currentContextObject().currentClientContext().setClientId(clientId);
//    currentContextObject().currentClientContext().setAlias(clientId);
  }

  /**
   * Sets a markId to the current client context
   * @param markId the marlk id
   */
  public void setMarkIdToClientContext(String markId) {
    if (loc.bePath()) {
      loc.pathT("setMarkIdToClientContext(): markId: " + markId + " for client context: " + currentContextObject().currentClientContext());
    }
    currentContextObject().currentClientContext().setMarkId(markId);
  }

  /**
   * Applies the client context corresponding to the specified client Id to the current thread
   * (this method is invoked only when P4 is used)
   * 
   * @param clientId the client id
   */
  public void applyClientContextToCurrentThreadById(String clientId) {
    if (clientId == null) {
      throw new IllegalArgumentException("clientId is null");
    }
    if (loc.bePath()) {
      loc.pathT("apply client context to current thread by clientId: " + clientId);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, "?", new Exception());
      }
    }
    ClientContextImpl context = ClientContextImpl.getByClientId(clientId);
    if (loc.bePath()) {
      loc.pathT("context: " + context);
    }
    SessionExecContext currentSessionContext = currentContextObject();
    if (context == null) { // set to current context
      if (loc.bePath()) {
        loc.pathT("context is set by security : " + currentSessionContext.currentClientContext);
      }
      return;      
//      setClientIdToClientContext(clientId);
    } else {
      if (currentSessionContext.isClientContextApplied()) {
        if (loc.beDebug()) {
          loc.debugT("REMOVE thread reference from: " + currentSessionContext.currentClientContext);
        }        
        currentSessionContext.currentClientContext.removeThreadReference();
      }      
      if (loc.bePath()) {
        loc.pathT("context applied: " + context);
      }      
      currentSessionContext.currentClientContext = context;
      if (loc.beDebug()) {
        loc.debugT("ADD thread reference from: " + currentSessionContext.currentClientContext);
      }
      currentSessionContext.currentClientContext.addThreadReference();
    }
    currentSessionContext.setThreadLoginSession(currentSessionContext.currentClientContext.loginSession());    
    if (loc.bePath()) {
      loc.pathT("thread login session applied: " + currentSessionContext.getThreadLoginSession());
    }    
    if (currentSessionContext.currentClientContext != null) {
      ClientContextImpl.invalidationTask.scheduleForInactivityCheck(currentSessionContext.currentClientContext);
//      ((ClientContextImpl) context).lastAcessed = System.currentTimeMillis();
    }
  }


  /**
   * Returns the client context corresponding to the client id
   * @param clientId client id
   * @return the client context
   */
  public ClientContext getClientContextByClientId(String clientId) {
    ClientContext ctx = ClientContextImpl.getByClientId(clientId);
    if (loc.bePath()) {
      loc.pathT("getClientContextByClientId(): clientId: " + clientId + " client context: " + ctx);
    } 
    return ctx;
  }

  /**
   * Returns the client id of the current client context
   * @return the client id
   */
  public String getClientId() {
    if (loc.bePath()) {
      loc.pathT("getClientId(): for client context: " + currentContextObject().currentClientContext());
    } 
    return currentContextObject().currentClientContext().getClientId();
  }

  /**
   * Returns the alias of the current client context
   * @return the alias
   */
  public String getAlias() {
    if (loc.bePath()) {
      loc.pathT("getAlias(): for client context: " + currentContextObject().currentClientContext());
    } 
    String alias = currentContextObject().currentClientContext().getAlias();
    if (alias == null || alias.equals(currentContextObject().currentClientContext().getClientId())) {
      return null;
    } else {
      return alias;
    }
  }

  /**
   * Returns all existing client contexts
   * @return collection of client context objects
   */
  public Collection<ClientContextImpl> getAllClientContexts() {
    if (loc.bePath()) {
      loc.pathT("getAllClientContexts(): for client context: " + currentContextObject().currentClientContext());
    }
    return ClientContextImpl.clientContexts();
  }

  /**
   * Returns all not anonimous login sessions
   * @return collection of login sessions
   */
  public Collection<LoginSessionInterface> getClientLoginSessions() {
    HashSet<LoginSessionInterface> loginSessions = new HashSet<LoginSessionInterface>(ClientContextImpl.clientContexts().size());
    if (loc.bePath()) {
      loc.pathT("getClientLoginSessions(): for client context: " + currentContextObject().currentClientContext());
    }
    for (ClientContextImpl clientContext: getAllClientContexts()) {
      if ( !(clientContext.loginSession() == ClientContextImpl.anonymousLoginSession) ) { // not anonimous
        loginSessions.add(clientContext.loginSession());
      }
    }
    return loginSessions;
  }

  /**
   * Returns the login session of a client context
   * @param clientContext the specified client context
   * @return the login session
   */
  public LoginSessionInterface getLoginSession(ClientContext clientContext) {
    if (loc.bePath()) {
      loc.pathT("getLoginSession(): for client context: " + clientContext);
    } 
    return ((ClientContextImpl) clientContext).loginSession();
  }

  /**
   * Sets an attribute to the current client
   * @param attrName the attribute name
   * @param value the attribute value
   * @throws com.sap.engine.session.usr.UserContextException if some exception occurs
   * @throws java.io.IOException if any IO exception occures
   */
  public void setCurrentClientAttribute(String attrName, Object value) throws UserContextException, IOException {
    if (loc.bePath()) {
      loc.pathT("setCurrentClientAttribute(): for client context: " +currentContextObject().currentClientContext() + " attrname: " + attrName + " attrvalue: " + value);
    }
    currentContextObject().currentClientContext().addAttribute(attrName, value);
  }  

  /**
   * Returns an attribute from the current client attributes
   * @param attrName the attribute name
   * @return the attribute value
   * @throws com.sap.engine.session.usr.UserContextException if some exception occurs
   * @throws java.io.IOException if any IO exception occures
   */
  public Object getCurrentClientAttribute(String attrName) throws UserContextException, IOException {
    if (loc.bePath()) {
      loc.pathT("getCurrentClientAttribute(): for client context: " +currentContextObject().currentClientContext() + " attrname: " + attrName + " attrvalue: " + currentContextObject().currentClientContext().getAttribute(attrName));
    }
    return currentContextObject().currentClientContext().getAttribute(attrName);
  }

  /**
   * @deprecated use setMarkIdToClientContext() instead
   */
  public void setMarkIdToClient(String markId) {
    setMarkIdToClientContext(markId);
  }

  /**
   * Returns the mark Id of the current client, if set
   * @return the mark id
   */
  public String getMarkId() {
    if (loc.bePath()) {
      loc.pathT("getMarkId(): for client context: " + currentContextObject().currentClientContext());
    }
    return currentContextObject().currentClientContext().getMarkId();
  }

  private SessionExecContext currentContextObject() {
    if (SessionExecContext.threadContextProxy != null) {
      return SessionExecContext.threadContextProxy.currentContextObject();
    } else {
      IllegalStateException is = new IllegalStateException("Thread System not available.");
      loc.traceThrowableT(Severity.DEBUG,"",is);
      throw is;
    }
  }

  /**
   * This method is allowed to be called only once.
   * @param anonymousLoginSession the anonymous login reference
   * @return a Login Accessor object
   */
  public static synchronized LoginAccessor setAnonymousLoginSession(LoginSessionImpl anonymousLoginSession) {
    if (!ClientContextImpl.isAnonymousLoginSessionSet()) {
      loc.traceThrowableT(Severity.DEBUG, "Anonymous login session is set", new Exception());
      ClientContextImpl.anonymousLoginSession = anonymousLoginSession;
      return new LoginAccessor();
    } else {
      loc.traceThrowableT(Severity.FATAL, "Anonymous login session is allready set", new Exception());
      throw new IllegalStateException("Anonymous login session is allready set");
    }
  }

}