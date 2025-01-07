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

import com.sap.engine.session.runtime.SessionRequest;
import com.sap.engine.session.trace.Locations;
import com.sap.tc.logging.Severity;

import java.io.IOException;

/**
 * Author: georgi-s
 * Date: 2005-4-1
 */
public abstract class AbstractSessionHolder extends SessionRequest implements SessionHolder {

  public AbstractSessionHolder() {
  }

  public AbstractSessionHolder(String sessionId, SessionDomain domain) {
    if (Locations.SESSION_LOC.bePath()) {
      Locations.SESSION_LOC.pathT("AbstractSessionHolder is created:" + this + "["+ sessionId + "," + domain + "]");
      Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG, this.toString(), new Exception());
    }
    doSessionRequest(domain, sessionId);
  }

  /* (non-Javadoc)
    * @see com.sap.engine.session.SessionHolder#getSessionName()
    */
  public String getSessionName() {
    return sessionId;
  }

  /* (non-Javadoc)
  * @see com.sap.engine.session.SessionHolder#getSession(com.sap.engine.session.SessionFactory)
  */
  public Session getSession(SessionFactory factory) throws SessionException {
    return super.session(factory);
  }

  /* (non-Javadoc)
  * @see com.sap.engine.session.SessionHolder#commitAccess()
  */
  public void commitAccess() throws SessionException, IOException {
    super.commit();
  }

  /* (non-Javadoc)
  * @see com.sap.engine.session.SessionHolder#commited()
  */
  public boolean commited() {
    return domain == null;
  }

  /* (non-Javadoc)
  * @see com.sap.engine.session.SessionHolder#releaseAccess()
  */
  public void releaseAccess() {
    super.release();
  }

  /* (non-Javadoc)
  * @see com.sap.engine.session.state.SessionRequest#requestDescription()
  */
  public String requestDescription() {
    return "Session Holder.";
  }

  /* (non-Javadoc)
  * @see com.sap.engine.session.SessionHolder#getSession()
  */
  public Session getSession() throws SessionNotFoundException {
     if (Locations.SESSION_LOC.bePath()) {
      Locations.SESSION_LOC.pathT("getSession() is called:" + this + "["+ sessionId + "," + domain + "]");
      Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG, this.toString(), new Exception());
    }
    Session session;
    try {
      session = super.session(null);
      if (Locations.SESSION_LOC.bePath()) {
        Locations.SESSION_LOC.pathT(this + "["+ sessionId + "," + domain + "] " + session);
        Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG, this.toString(), new Exception());
      }
    } catch (CreateException e) {
      Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG, this.toString(), e);
      throw new SessionNotFoundException(domain.getName() + "/" + sessionId + "(" + sessionId.hashCode() + ")");
    }
    if (session != null) {
      return session;
    } else {
      throw new SessionNotFoundException(domain.getName() + "/" + sessionId + "(" + sessionId.hashCode() + ")");
    }
  }
  
  /*
   * Check if there is a session associated with this SessionHolder.
   * If so then creates RuntimeSessionModel for it.
   * RuntimeSessionModel allows a timeout session to expire. 

   * @return <tt>true</tt> if session exists
   */
  public boolean sessionExist(){
    if (Locations.SESSION_LOC.bePath()) {
      Locations.SESSION_LOC.pathT("doSession() is called:" + this + "["+ sessionId + "," + domain + "]");
      Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG, this.toString(), new Exception());
    }
    Session session = null;
    try {
      session = super.session(null);
      if (Locations.SESSION_LOC.bePath()) {
        Locations.SESSION_LOC.pathT(this + "["+ sessionId + "," + domain + "] " + session);
        Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG, this.toString(), new Exception());
      }
    } catch (CreateException e) {
      Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG, this.toString(), e);
    }
    if(session != null){
    	return true;
    } else{
    	return false;
    }
  }
  
  protected Session getSessionInternal() throws SessionNotFoundException {
    Session session = domain._getSessionInternal(getSessionName());
    if (session != null) {
      return session;
    } else {
      throw new SessionNotFoundException(getSessionName());
    }
  }

  public Session expire(boolean invalidate) {
    Session session = domain._getSessionInternal(getSessionName());
    if (session != null && invalidate) {
      domain.addExpiredSession();
      session.invalidate();
    }
    return session;
  }

  public void beforeLogout() {
    Session session = domain._getSessionInternal(getSessionName());
    if (session != null) {
      session.beforeLogout();
    }
  }

  protected void sessionRenew() {
    super.sessionRenew();
  }

  public void remove() throws SessionException {
    SessionDomain dom = this.domain;
    String sessId = sessionId;
    releaseAccess();
    dom.removeSession(sessId);
  }

  public String handlerName() {
    return CALLBACK_HANDLER;
  }

  public abstract boolean isApplied();

}
