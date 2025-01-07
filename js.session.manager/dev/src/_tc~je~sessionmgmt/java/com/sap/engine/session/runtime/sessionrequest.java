package com.sap.engine.session.runtime;

import com.sap.engine.session.*;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.tc.logging.Severity;


public class SessionRequest {
  protected SessionDomain domain;
  protected String sessionId;
  protected RuntimeSessionModel rtModel;
  Object requestMetaData;
  protected Object protectionData = null;  // TODO will be String[] {IP, markSessionID}

  protected static boolean checkIp;
  protected static boolean checkMarkId;


  public static boolean isCheckIpEnabled() {
    return checkIp;
  }

  public static void setCheckIpEnabled(boolean checkIp) {
    SessionRequest.checkIp = checkIp;
  }

  public static boolean isCheckMarkIdEnabled() {
    return checkMarkId;
  }

  public static void setCheckMarkIdEnabled(boolean checkMarkId) {
    SessionRequest.checkMarkId = checkMarkId;
  }

  public void doSessionRequest(SessionDomain domain, String sessionId) {
//    if (this.domain != domain || !sessionId.equals(this.sessionId)) {
//      IllegalStateException is = new IllegalStateException("Can't use <" + this.domain + ", " + this.sessionId + ">\n" +
//      "to request:" + domain + ":" + sessionId);
//      RuntimeSessionModel.loc.throwing(is);
//    }
    this.domain = domain;
    this.sessionId = sessionId;
  }

  public Object getRequestMetaData() {
    return requestMetaData;
  }

  public void setRequestMetaData(Object requestMetaData) {
    this.requestMetaData = requestMetaData;
  }

  /**
   * @deprecated to be removed ?
   */
  public void setMarkIdToClient(String markId) {
    SessionExecContext.getCurrent().currentClientContext().setMarkId(markId);
  }

  public String getMarkId() {
    return SessionExecContext.getCurrent().currentClientContext().getMarkId();
  }

  public void addNewSession(Session session) throws SessionExistException {
    try {
      rtModel = domain.addNewSession(sessionId, session);
      rtModel.activate(requestMetaData);
    } catch (SessionExistException e) {
      throw e;
    } catch (SessionException e) {
      throw new RuntimeException("", e);
    }
  }

  public synchronized Session session(SessionFactory factory) throws CreateException {
    try {
      if (rtModel == null) {
        if (RuntimeSessionModel.loc.beDebug()) {
          RuntimeSessionModel.loc.debugT("Init runtime session model for session request \n<" + this + ">");
        }
        rtModel = domain.runtimeSessionModel(sessionId, factory != null);
        if ( (rtModel == null) ||
             (factory == null && rtModel.isInvalidated()) ) { // the model is 'invalidated' if invalidating atm
          rtModel = null;
        	return null;
        } else {
          rtModel.activate(requestMetaData);
        }
      }
      return rtModel.getSession(factory);
    } catch (SessionDestroyedException e) {
      RuntimeSessionModel.loc.traceThrowableT(Severity.DEBUG, "", e);
      rtModel = null;
      if (factory == null) return null;
      Thread.yield();
      return session(factory);
    }

  }

  protected synchronized void sessionRenew() {
    try {
      if (rtModel == null) {
        if (RuntimeSessionModel.loc.beDebug()) {
          RuntimeSessionModel.loc.debugT("Init runtime session model for session request \n<" + this + ">");
        }
        try {
          rtModel = domain.runtimeSessionModel(sessionId, false);
        } catch (CreateException e) {
          // not posible
          RuntimeSessionModel.loc.traceThrowableT(Severity.DEBUG, "", e);
        }
        if (rtModel == null) {
          return;
        } else {
          rtModel.activate(requestMetaData);
        }

      }
      rtModel.renew();
    } catch (SessionDestroyedException e) {
      RuntimeSessionModel.loc.traceThrowableT(Severity.DEBUG, "", e);
      rtModel = null;
    }
  }

  public synchronized void commit() {
    if (rtModel != null) {
      RuntimeSessionModel rm = rtModel;
      domain = null;
      sessionId = null;
      rtModel = null;
      rm.commit(requestMetaData);
    } else {
      domain = null;
      sessionId = null;
    }
  }

  public void release() {
    if (rtModel != null) {
      RuntimeSessionModel rm = rtModel;
      domain = null;
      sessionId = null;
      rtModel = null;
      rm.release(requestMetaData);
    } else {
      domain = null;
      sessionId = null;
    }
  }
  
  public Object getProtectionData() {
    return protectionData;
  }

// removed because of performance issues with the SPECj
//  protected void finalize() throws Throwable {
//    if (rtModel != null) {
//      rtModel.commit(requestMetaData);
//    }
//    super.finalize();
//  }

}
