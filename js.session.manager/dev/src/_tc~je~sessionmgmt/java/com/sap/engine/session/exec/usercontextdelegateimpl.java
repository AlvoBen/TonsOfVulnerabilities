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

import java.util.Collection;

import com.sap.engine.session.trace.Locations;
import com.sap.engine.session.usr.*;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
// TODO - will remove this class, replace by Login Accessor
class UserContextDelegateImpl extends UserContextDelegate implements UserContextAccessor {

  private static Location loc = Locations.USER_LOC;

  protected UserContext getCurrentUserContext() {
    if (SessionExecContext.threadContextProxy != null) {
      return SessionExecContext.threadContextProxy .currentContextObject().currentClientContext();
    }
    loc.traceThrowableT(Severity.WARNING, "", new IllegalStateException("Thread Context system not available"));
    throw new IllegalStateException("Thread Context system not available");
  }

  protected UserContextAccessor getUserContextAccessor() {
    return this;
  }

  /**
   * @deprecated use getClientContext
   */
  public UserContext getUserContext(Object alias) throws SecurityException {
    return getClientContext(alias);
  }


  public UserContext getClientContext(Object clientId) throws SecurityException {
    ClientContextImpl clientContext;
    String clientKey;
    if (loc.bePath()) {
      loc.pathT("getUserContext(Object) is called:" + clientId);
    }
    if (clientId instanceof byte[]) {
      clientKey = asString((byte[]) clientId);
      clientContext = ClientContextImpl.getByClientId(clientKey);
      if (clientContext != null) {
        clientContext.lastAccessed = System.currentTimeMillis();
      }
    } else {
      clientKey = (String) clientId;
      clientContext =  ClientContextImpl.getByClientId(clientKey);
    }
    if (loc.bePath()) {
      loc.pathT("getUserContext(Object) returns:" + clientContext);
    }
    return clientContext;
  }

  /**
   * @deprecated will be removed
   */
  public void joinSession(Object alias, Object session) throws UserContextException {
    String clientKey;
    if (alias == null) {
      return;
    } else if (alias instanceof byte[]) {
      clientKey = asString((byte[]) alias);
    } else {
      clientKey = (String) alias;
    }
    // Ne se setva na current user context-a, zashtoto HTTP - to moje da ima UserContext za tozi
    // client no da ne applynat ako se e callnal application bez sec constrains
    ClientSession clSession;
    if (session instanceof ClientSession) {
      clSession = (ClientSession) session;
    } else {
      SessionExecContext.loc.infoT("joinSession(Object, Object) is called with:" + alias + " " + session);
      return;
    }
    ClientContextImpl usr = (ClientContextImpl) UserContext.getCurrentUserContext();
    if (usr != null && clientKey.equals(usr.getClientId())) {
      usr.addSession(clSession);
      return;
    }
    usr = ClientContextImpl.getByClientId(clientKey);
    if (usr != null) {
      usr.addSession(clSession);
    }
  }

  /**
   * @deprecated will be removed
   */
  public UserContext getByLoginSession(LoginSession session) throws SecurityException {
    return null;
  }

  /**
   * @deprecated will be removed
   */
  public UserContext createUserContext(LoginSession loginSession) throws UserContextException {
    return null;
  }

  public Collection userContexts() {
    return ClientContextImpl.clientContexts();
  }

  /**
   * @deprecated will be removed
   */
  public void addUserContextAlias(Object alias, UserContext context) throws UserContextException {
    if (context != null) {
      String userKey;
      if (alias instanceof byte[]) {
        userKey = asString((byte[]) alias);
        ((ClientContextImpl) context).lastAccessed = System.currentTimeMillis();
        ClientContextImpl.invalidationTask.scheduleForInactivityCheck((ClientContextImpl)context);
      } else {
        userKey = (String) alias;
      }
      ((ClientContextImpl) context).setClientId(userKey);
    }
  }

  public void removeAlias(Object alias) {

  }

  public void destroyUserContext(UserContext usrContext) throws UserContextException {
    if (usrContext != null) {
      ((ClientContextImpl) usrContext).destroy();
    }
  }


  /**
   * Apply the security session corresponding to the given clientId to the thread context
   * @param clientId the clientId
   * @return true - if successfully applied
   * @throws UserContextException if there is a problem
   */
  public boolean apply(Object clientId) throws UserContextException {
    SessionExecContext currentContextObject = SessionExecContext.getExecutionContext();
    if (loc.bePath()) {
      loc.pathT("apply(Object) is called:" + clientId +
              "\n Current context object is :" + currentContextObject);
    }
    if (clientId == null) {
      if (loc.bePath()) {        
        loc.pathT("apply(Object) is called with clientId null:" +
            "\n Current context object is:" + currentContextObject);
        if (loc.beDebug()) {
          loc.traceThrowableT(Severity.DEBUG, "stacktrace" , new Exception());
        }
      }
      currentContextObject.setThreadLoginSession(ClientContextImpl.anonymousLoginSession);
    } else {
      currentContextObject.setThreadLoginSession(currentContextObject.currentClientContext().loginSession());
    }
    return true;
  }

  public boolean apply() throws UserContextException {
    UserContext usr = SessionExecContext.getExecutionContext().applyClientContext();
    
    return usr != null;
  }

  public void empty() {
    try {
      apply(null);
    } catch (UserContextException e) {
      if (loc.beWarning()) {
        loc.traceThrowableT(Severity.WARNING, "Method apply(null) throws exception", e);
      }
    }
  }

  //Session - RuntimeSessionModel
  public void removeSession(Object session) {
    if (session instanceof ClientSession) {
      String clientId = ((ClientSession) session).getClientId();
      ClientContextImpl.getByClientId(clientId).removeSession((ClientSession)session);
    }
  }

  public void performLocked(String lock, Runnable action) throws UserContextException {
  }

  static String asString(byte[] bytes) {
    String hex = "0123456789ABCDEF";
    StringBuffer sb = new StringBuffer();
    int end = bytes.length;
    for (int c = 0; c < bytes.length; c += 16) {
      int count = 16;
      for (int j = c; --count >= 0 && j < end; j++) {
        int charAsInt = ((int) bytes[j]) & 0x00FF;
        sb.append(hex.charAt(charAsInt >> 4) + hex.charAt(charAsInt & 0x000F));
      }
    }
    return sb.toString();
  }

}
