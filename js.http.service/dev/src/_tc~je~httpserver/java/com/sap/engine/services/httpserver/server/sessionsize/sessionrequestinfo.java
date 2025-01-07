package com.sap.engine.services.httpserver.server.sessionsize;

import java.util.Hashtable;

/**
 * Data holder class for the info for the current request and session; Used when
 * invoking the methods of the <CODE>SessionSizeManager</CODE> for calculation
 * the session size
 * 
 * @author Violeta Uzunova (I024174)
 *
 */
public class SessionRequestInfo {
  
  private int requestId;                      // the id of the request 
  private String aliasName;                   // the currently active web application 
  private boolean isSessionValid;             // if session is valid (not expired) 
  private String sessionId;                   // the session id
  private Hashtable<String, Object> chunks;   // a hashtable with session attributes
  private Object session;                     // the session as an object
  
  public int getRequestId() {
    return requestId;
  }
  
  public void setRequestId(int requestId) {
    this.requestId = requestId;
  }
  
  public String getAliasName() {
    return aliasName;
  }
  
  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Hashtable<String, Object> getChunks() {
    return chunks;
  }

  public void setChunks(Hashtable<String, Object> chunks) {
    this.chunks = chunks;
  }

  public Object getSession() {
    return session;
  }

  public void setSession(Object session) {
    this.session = session;
  }

  public boolean isSessionValid() {
    return isSessionValid;
  }

  public void setSessionValid(boolean isSessionValid) {
    this.isSessionValid = isSessionValid;
  }  
}
