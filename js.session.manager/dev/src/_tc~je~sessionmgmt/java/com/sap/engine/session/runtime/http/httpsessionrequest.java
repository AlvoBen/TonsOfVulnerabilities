package com.sap.engine.session.runtime.http;

import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.bc.proj.jstartup.sadm.ShmWebSession;
import com.sap.engine.core.Names;
import com.sap.engine.session.CreateException;
import com.sap.engine.session.Session;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.SessionException;
import com.sap.engine.session.SessionExistException;
import com.sap.engine.session.SessionFactory;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.logging.Dump;
import com.sap.engine.session.runtime.IpProtectionException;
import com.sap.engine.session.runtime.MarkIdProtectionException;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.SessionRequest;
import com.sap.engine.session.trace.Trace;
import com.sap.engine.session.usr.UserContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;



public class HttpSessionRequest extends SessionRequest {
	protected static final Location loc = Location.getLocation(HttpSessionRequest.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
	protected int shmSlotIndex = -1;
    protected String debugTag = null;
    private Object storedData;
    
    //this is jsession id
  private String clientCookie;
  /**
   * Only current request should be ended
   */
  public static final int END_CURRENT = 0;

  /**
   * All request from the request queue should be ended
   */
  public static final int END_All = -1;
  
  public static final String POST_PRESERVED_PARAMETERS = "post_preserved_parameters";
  
  //this flag indicate is this request is a part of some session
  private boolean requested;
  
  //set to true if the session object is accessed during request processing
  private boolean sessionAccesed;
  
  private SessionFactory sfactory;
  
  //indicate is it a session monitored in the GST.
  protected boolean shmActivated;
  
  protected boolean isProtected = false;

  public void setDebugTag(String tag) {
    this.debugTag = tag;
  }

  public String getDebugTag() {
    return debugTag;
  }

  public boolean isDebug() {
		if (shmSlotIndex != -1) {
			try {
				return ShmWebSession.isDebug(shmSlotIndex);
			} catch (ShmException e) {
				loc.traceThrowableT(Severity.WARNING, "", e);
			}
		}

    return false;
  }

  public void setMonInfo(int monInfo) {
    shmSlotIndex = monInfo;
  }

  /**
   * Perform the session request and make this request active. The active
   * request goes as a part of the session and provide access to the session.
   *
   * @param domain    The session domain that consist the requested session
   * @param sessionId session key
   * @throws IllegalArgumentException if domain or sessionId are null
   * @throws IllegalStateException    if it is already requested
   */
  public synchronized void doSessionRequest(SessionDomain domain, String clientCookie, String sessionId) {
//    setRequestMetaData(this);
//    SessionExecContext.applySessionContext(domain.getEnclosingContext(), sessionId);
//    super.doSessionRequest(domain, sessionId);
    this.clientCookie = clientCookie;
    request(domain, sessionId);
    
  }

  public synchronized void doSessionRequest(SessionDomain domain, String sessionId) {
//  setRequestMetaData(this);
//  SessionExecContext.applySessionContext(domain.getEnclosingContext(), sessionId);
//  super.doSessionRequest(domain, sessionId);
    doSessionRequest(domain, sessionId, sessionId);
  }
  
  public boolean checkSessionID(String sessionId) {
     if (checkIp) {
      ClientContextImpl ctx = ClientContextImpl.getByClientId(sessionId);
      String clientIP = ctx == null ? null : ctx.getProtectionIp();
      if (protectionData != null) { // the request contains protection data
        String requestIp = ((String[]) protectionData)[0];
        if (requestIp != null) {
          if (clientIP != null) { // IP is already set, we'll check it
            if (!requestIp.equals(clientIP)) { // different IP !!!
              if (loc.beDebug()) {
                loc.debugT("checkSessionID : Illegal IP access. request IP: [" + requestIp + "] - client IP: [" + clientIP + "]. Method return false");
              }
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  public void applyUserContext() {
    SessionExecContext currentContextObject = SessionExecContext.getExecutionContext();
    if (checkIp) {
      String clientIP = currentContextObject.currentClientContext().getProtectionIp();
      if (protectionData != null) { // the request contains protection data
        String requestIp = ((String[]) protectionData)[0];
        if (requestIp != null) {
          if (clientIP != null) { // IP is allerady set, we'll check it
            if (!requestIp.equals(clientIP)) { // different IP !!!
              if (loc.beDebug()) {
                loc.debugT("Illegal IP access");
                loc.debugT("request IP: " + requestIp);
                loc.debugT("client  IP: " + clientIP);
              }
              throw new IpProtectionException("Attempt to access the session from different IP: " + requestIp);
            }
          } else { // will set this IP
            currentContextObject.currentClientContext().setProtectionIp(requestIp);
          }
        }
      }
    }
    
    //always check mark id - it should be set by markid cookie or web container
    //in the second one it is taken from jsessionid
      String clientMarkId = currentContextObject.currentClientContext().getMarkId();
    if (protectionData != null) {
      if (clientMarkId != null) { // will check the request mark ID
        String markId = ((String[]) protectionData)[1];
        if (markId != null) {
          if (!markId.equals(clientMarkId)) { // different mark ID
            if (loc.beDebug()) {
              loc.debugT("mark session IDs does not match");
              loc.debugT("request mark session ID: " + markId);
              loc.debugT("client  mark session ID: " + clientMarkId);
            }
            throw new MarkIdProtectionException(
                "mark Session Id does not match");
          }
        } else { // no request markID - this fails
          if (loc.beDebug()) {
            loc.debugT("no client mark Session Id");
            loc.debugT("request mark session ID: " + markId);
          }
          throw new MarkIdProtectionException(
              "no client mark Session Id, request mark ID: " + markId);
        }
      }
    } else {
      if (loc.beDebug()) {
        loc.debugT("Protection data is null. HttpSessionRequest has been released or protection data has not been set at all.");
      }
    }
    
    if (loc.bePath()) {
      loc.pathT("applyUserContext() is called:" + sessionId + "\n Current context object is :" + currentContextObject);
    }
    currentContextObject.applyUserContext(sessionId);
  }

  public synchronized void commit() {
    if (rtModel == null) {
      try {
        if (shmSlotIndex != -1) {
          if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("DEACTIVATE index: " + shmSlotIndex + " - " + this), true);
          ShmWebSession.deactivate(shmSlotIndex);
        }
      } catch (ShmException e) {
        RuntimeSessionModel.loc.traceThrowableT(Severity.ERROR,"",e);
      }
    }
    super.commit();
    debugTag = null;
  }

  /**
   * Used to store data in the request
   * @param data the data
   * @param timeout the timeout of the corresponding session model in seconds
   */
  public void storeData(Object data, int timeout) {
    storedData = data;
    try {
      rtModel = domain.runtimeSessionModel(sessionId, true);
      ((HttpRuntimeSessionModel) rtModel).activateShmSlot(this);
      ((HttpRuntimeSessionModel) rtModel).storeRuntimeModelAttributes(POST_PRESERVED_PARAMETERS,data);
      rtModel.setMaxInactiveInterval(timeout);
      ((HttpRuntimeSessionModel) rtModel).deactivateShmSlot(this, true);
      ((HttpRuntimeSessionModel) rtModel).setExpendable();
    } catch (Exception e) {
      RuntimeSessionModel.loc.traceThrowableT(Severity.ERROR, "", e);
    }
  }

  /**
   * Getter of the stored data
   * @return the stored data
   */
  public Object getData() {
	  Object toReturn = null;
      rtModel = domain.getRuntimeSessionModel(sessionId);
      if (rtModel != null){
	      	toReturn = ((HttpRuntimeSessionModel) rtModel).getRuntimeModelAttributes(POST_PRESERVED_PARAMETERS);
	  }
	  return toReturn;  
  }

  /**
   * Getter/remover of the stored data
   * @return the stored data
   */
  public Object removeData() {
    Object toReturn = storedData;
    storedData = null;
    try {
        rtModel = domain.getRuntimeSessionModel(sessionId);
        if (rtModel != null){
        	toReturn = ((HttpRuntimeSessionModel) rtModel).getRuntimeModelAttributes(POST_PRESERVED_PARAMETERS);
        	if (rtModel.session() == null) { // we can free the slot
        		rtModel.destroy();
        	}
        }
    } catch (Exception e) {
      RuntimeSessionModel.loc.traceThrowableT(Severity.ERROR, "", e);
    }
    return toReturn;
  }

  /**
   * Returns the session name of requested session object
   *
   * @return The name of the requested session object
   */
  public String getSessionId() {
    if (Trace.beDebug()) {
      Trace.trace("getSessionId" + this);
    }

    return sessionId;
  }
  
  public String getClientCookie() {
    if (Trace.beDebug()) {
      Trace.trace("getSessionId" + this);
    }
    return clientCookie;
    
  }
  
  /**
   * Returns the session domain that contains the requested session
   *
   * @return The session domain that contains the session
   */
  public SessionDomain getSessionDomain() {
    if (Trace.beDebug())
      Trace.trace("getSessionDomain" + this);

    return domain;
  }
  
  /**
   * Chek is the reqiest is a part of the session object
   *
   * @return true if the session was requested
   */
  public boolean isRequested() {
    return requested;
  }
  
  private void request(SessionDomain domain, String sessionId) throws IllegalArgumentException, IllegalStateException {

    if (domain == null || sessionId == null) {
      throw new IllegalArgumentException(
              "Illegal Arguments: null\n domain:" + domain
                      + "\nssessionId:" + sessionId);
    }

    if (requested) {
      throw new IllegalStateException(
              "The session is already requested. The request should be ended first "
                      + "before requesting new session.");
    }

    this.domain = domain;
    this.sessionId = sessionId;
    this.requested = true;
    //super.doSessionRequest(domain, sessionId);
    setRequestMetaData(this);
    SessionExecContext.applySessionContext(domain.getEnclosingContext(), sessionId);
    //super.doSessionRequest(domain, sessionId);
  }
  
  /**
   * End request to the current session and do request to the new session.
   *
   * @param domain    The session domain that consist the requested session
   * @param sessionId session key
   * @throws IllegalArgumentException if domain or sessionId are null
   * @throws IllegalStateException    if the session is in illegal state
   */
  public synchronized void reDoSessionRequest(SessionDomain domain,
                                              String sessionId) throws IllegalStateException {
//    if (isRequested()) {
//      if (domain.equals(this.domain) && this.sessionId.equals(sessionId)) {
//        return;
//      } else {
//        endRequest(0);
//      }
//    }
//
//    doSessionRequest(domain, sessionId);
    reDoSessionRequest(domain, sessionId, sessionId);
  }
  
  public synchronized void reDoSessionRequest(SessionDomain domain, String clientCookie,
      String sessionId) throws IllegalStateException {
    if (isRequested()) {
      if (domain.equals(this.domain) && this.sessionId.equals(sessionId)) {
        return;
      } else {
        endRequest(0);
      }
    }

    doSessionRequest(domain, clientCookie, sessionId);
  }
  
  /**
   * Create the new session and joins this request as a part of the session.
   *
   * @param domain    The session domain that consist the requested session
   * @param sessionId session key
   * @param factory   factory object used to create the session
   * @return the session
   * @throws IllegalArgumentException if domain or sessionId are null
   * @throws IllegalStateException    if this request is already joined to the session
   * @throws SessionExistException    if domain already contains the session with the same key.
   * @throws CreateException          if the session can not be created.
   */

  public synchronized Session requestNewSession(SessionDomain domain, String sessionId, SessionFactory factory)
          throws IllegalArgumentException, IllegalStateException, SessionExistException, CreateException {
    request(domain, sessionId);
    Session session = factory.getSession(sessionId);
    super.addNewSession(session);
    sessionAccesed = true;
    return session;
  }
  
  /**
   * Returns true if the session is accessed during the request.
   *
   * @return true if the session was accessed
   */
  public boolean isSessionAccessed() {
    return sessionAccesed;
  }
  
  /**
   * Returns the session requested from this session request. If there is no
   * valid session returns a new session created from the factory passed such
   * a parameter.
   *
   * @param factory -factory object used to create new session object if there is
   *                not a valid session
   * @return the session requested from this session request
   * @throws IllegalStateException if the session is not requested.
   * @throws SessionException      if the factory can not create the session object
   * @see SessionState#getSession(SessionFactory)
   */
  public Session getSession(SessionFactory factory) throws SessionException {
    if (!requested) {
      throw new IllegalStateException("The session isn't requested.");
    }
    sfactory = factory;

    Session s = super.session(factory);
    sessionAccesed = true;
    return s;

  }
  
  /**
   * Returns the session requested from this <code>SessionRequest</code>
   * object. If there is no valid session this method returns <tt>null</tt>
   * or creates a new session object if the <tt>create</tt> flag is set to
   * <tt>true</tt>.
   * <p/>
   * The implementaion of this method delagate the call to
   * <code>getSession(boolean)</code> of <code>SessionState</code> object
   * associated with this session request
   *
   * @param create If <tt>true</tt> the new session should be created if there
   *               is not a valid session.
   * @return The <tt>Session</tt> object requested from this request or null
   *         if there is no valid session and <tt>create</tt> falag is set
   *         to <tt>false</tt>.
   * @throws IllegalStateException If this is not active request. To be activated the
   *                               <tt>doSessionRequest</tt> should be performed firs.
   * @see SessionState#getSession(boolean)
   */
  public synchronized Session getSession(boolean create)
          throws IllegalStateException {
    if (!requested) {
      throw new IllegalStateException("The session isn't requested.");
    }

    Session s = null;
    try {
      SessionFactory f = create ? sfactory : null;
      s = super.session(f);
    } catch (CreateException e) {
      Trace.logException(e);
    }

    sessionAccesed = true;
    return s;

  }
  
  /**
   * Remove this request from the session. If there are no othere request that
   * are part of the session session goes to the inactive state automaticaly.
   *
   * @param action The action that should be performed
   *               <code>SessionRequset.END_CURRENT</code> end current session
   *               only or <code>SessionRequset.END_All</code> end all session
   *               from the stack.
   */
  public synchronized void endRequest(int action) {
    endCurrent();
  }
  
  public synchronized void release() {
    if (!requested) {
      return;
    }
    super.release();
    clear();
  }
  
  private void endCurrent() {
    commit();
    clear();
  }
  
  private void clear() {
    if (Trace.beDebug())
      Trace.trace("Clear:" + this);

    domain = null;
    sessionId = null;
    requested = false;
    sessionAccesed = false;
    shmActivated = false;
    protectionData = null;
    isProtected = false;
    sfactory = null;
    shmSlotIndex = -1;
    rtModel = null;
  }
  
  public boolean protectedAccess() {
    return isProtected;
  }
  
  /**
   * Returns the request description.
   *
   * @return request description.
   */
  public String requestDescription() {
    return "Http Session Request";
  }
  
  public void removeUserContext() {
    try {
      UserContext.getAccessor().apply(null);
    } catch (Exception e) {
      Trace.logException(e);
    }
  }
  
  public String toString() {
    SessionDomain d = domain;
    StringBuffer r = new StringBuffer(super.toString());
    r.append("[");
    r.append("sessionId=").append(sessionId).append(',');    
    String domainName = d != null? d.getName() : "<>";
    r.append("domain=").append(domainName).append(',');
    r.append("shmSlotIndex=").append(shmSlotIndex).append(',');
    r.append("debugTag=").append(debugTag).append(',');
    r.append("]");
    return r.toString();
  }
}
