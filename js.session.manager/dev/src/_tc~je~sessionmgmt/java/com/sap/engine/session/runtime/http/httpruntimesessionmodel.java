package com.sap.engine.session.runtime.http;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.bc.proj.jstartup.sadm.ShmWebSession;
import com.sap.engine.core.Names;
import com.sap.engine.session.CreateException;
import com.sap.engine.session.Session;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.logging.Dump;
import com.sap.engine.session.logging.LogFilteringSystem;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.SessionFailoverMode;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.usr.ClientContext;
import com.sap.engine.session.usr.ClientSession;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
public class HttpRuntimeSessionModel extends RuntimeSessionModel implements ClientSession {
  private static final Location loc = Location.getLocation(HttpRuntimeSessionModel.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  private ShmWebSession shmSlot;
  private boolean isExpandable;
  private static LinkedList<HttpRuntimeSessionModel> expendable;
  
  //this is the jsession id (for load balancing) or session id if the request is not http
  private ClientContext clientContext;
  private ConcurrentHashMap<String, Object> runtimeSessionModelAttributes = new ConcurrentHashMap<String, Object>();

  private boolean persistedInClientContext;
  private String clientCookie;
  //set to true when the shm slot is created - used in the shmActvate().
  //Because the shm slot is automatically activated when it is created for the first time in the JVM
  //the first postactivate should be skiped
//  private boolean skipShmActivation;

  public HttpRuntimeSessionModel(String sessionId, SessionDomain domain, SessionFailoverMode failoverMode) throws CreateException {
    super(sessionId, domain, failoverMode);

    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpRuntimeSessionModel(String, SessionDomain, SessionFailoverMode) is called." + this);
    }
  }

  public HttpRuntimeSessionModel(String sessionId, SessionDomain domain, Session session, SessionFailoverMode failoverMode) throws CreateException {
    super(sessionId, domain, session, failoverMode);

    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpRuntimeSessionModel(String, SessionDomain,Session, SessionFailoverMode) is called." + this);
    }
  }


  public void setMaxInactiveInterval(int p) {
    super.setMaxInactiveInterval(p);
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpRuntimeSessionModel.setMaxInactiveInterval(int)" + this + "< " + p + ">");
    }
    if (shmSlot != null) {
      shmSlot.setTimeout(p);
      try {
        if (isActive()){
        	shmSlot.store();
        }
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.INFO, "", e);
      }
    }
  }

  protected void beforeActivate(Object info) {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpSessionModel.beforeActivate(Object):" + this + " <" + info + ">");
    }

      try {
        activateShmSlot(info);
      } catch (ShmException e) {
        this.remove(); // destroy this RuntimeSessionModel object, because it's useless
        loc.traceThrowableT(Severity.DEBUG, "", e);
        
        throw new IllegalStateException("Cannot activate session: ", e);
      }

    if (!isActive()) {
      try {
        shmSlot.load();
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.WARNING, "Problem while loading shared memory slot ", e);
      }
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.pathT("HttpRuntimeSessionModel.beforeActivate(Object) shm slot is reloaded." + this);
      }
     
      if (!shmSlot.isCacheValid() && persistentModel() != null) {
        try {
          setSession(internalLoadSession());
          if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
            loc.pathT("HttpRuntimeSessionModel.beforeActivate(Object) session was reloaded." + this);
          }
          SessionExecContext.getExecutionContext().setIsLoadbalanced(true);
        } catch (PersistentStorageException e) {
          loc.traceThrowableT(Severity.WARNING, "Problem while reading session [" + compositeName() + "]", e);
        }
        shmSlot.setCacheValid(true);
        try {
          shmSlot.store();
        } catch (ShmException e) {
          loc.traceThrowableT(Severity.WARNING, "Problem while storing shared memory session [" + compositeName() + "]", e);
        }
      }
    }

    if (info instanceof HttpSessionRequest) {
      HttpSessionRequest req = (HttpSessionRequest) info;     
      String tag = req.getDebugTag();
      if (tag != null) {
        try {
          shmSlot = ShmWebSession.findSession(clientCookie, tag, getAliasHashCode());
        } catch (ShmException e) {
          loc.traceThrowableT(Severity.WARNING, "Cannot create Web session object", e);
        }
      }
    }
  }

  protected void postActivate(Object info, boolean rolback) {
  }

  protected void setSession(Session s) {
    super.setSession(s);

    if (s != null && clientContext == null) {
      clientContext = SessionExecContext.getExecutionContext().addSessionToClientContext(sessionId(), this);
    } else {
      if (loc.bePath()) {
        loc.pathT("session is set : " + s + " client context: " + clientContext);
      }  
    }

    if (isActive()) { 
      if (shmSlot != null) {
        shmSlot.setCacheValid(true);
        try {
          shmSlot.store();
        } catch (ShmException e) {
          loc.traceThrowableT(Severity.DEBUG, "", e);
        }
      }
    }
  }

  protected void postComit(Object info) {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpSessionModel.postComit(Object):" + this + " <" + info + ">");
    }
    if (info instanceof HttpSessionRequest) {
      HttpSessionRequest request = (HttpSessionRequest) info;
      try {
        deactivateShmSlot(request, true);
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.DEBUG, "", e);
      }
    } else {
      try {
        if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("DEACTIVATE " + shmSlot), true);
        shmSlot.deactivate();
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.DEBUG, "", e);
      }
    }
  }

  protected void postReleaseSession(Object info) {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpSessionModel.postReleaseSession(Object):" + this + " <" + info + ">");
    }
    if (info instanceof HttpSessionRequest) {
      HttpSessionRequest request = (HttpSessionRequest) info;
      try {
        deactivateShmSlot(request, false);
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.DEBUG, "", e);
      }
    } else {
      try {
        if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("DEACTIVATE " + shmSlot), true);
        shmSlot.deactivate();
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.DEBUG, "", e);
      }
    }
  }

  protected void postInvalidate() {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpSessionModel.postInvalidate():" + this);
    }
  }

  protected void beforeDestroy() {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpSessionModel.beforeDestroy():" + this);
    }
    if (shmSlot != null) {
      try {
        if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("CLOSE " + shmSlot.toString()), true);
        shmSlot.close();
        shmSlot = null;
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
  }

  protected void postDestroy() {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpSessionModel.postDestroy():" + this);
    }
    if (isExpandable){
    	expendables().remove(this);
    	isExpandable = false;
    }
  }

  public void beforeInvalidateClientSession() {
    if (!invalidated) {
      synchronized(this) {
        Session s = session();
        if (s != null) {
          s.beforeLogout();
        }
      }
    }
  }

  public void invalidateClientSession() {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("entern method invalidateClientSession");
    }
    if (!invalidated) {
      synchronized(this) {
        Session s = session();
        if (s != null){
          if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
            loc.pathT("session to be invalidated:\n");
            loc.pathT("sessionId: " + s.sessionId() + ", session domain " + s.domain());
          }
          s.invalidate();
        }
      }
    }
  }

  /**
   * This method is called when the AbstractSecuritySession is logged out
   * In this case all sessions should be invalidated, since the user logouts
   */  
  public void invalidateIfNotActive() {
    if (!invalidated) {
      if (!isActive()) {
        synchronized(this) {
          session().invalidate();
        }
      }
    }
  }

  public synchronized void persist() {
    super.persist();

    /* actualize the state in the Shm Slot */
    if (isSticky() != shmSlot.isSticky()) {
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.pathT("Setting sticky flag to : " + isSticky());
      }
      shmSlot.setSticky(isSticky());
      int backStore = ShmWebSession.BS_NONE;
      if (persistentModel != null) {
        String className = persistentModel.getClass().getName();
        if(className.contains("ShMemoryPersistentSessionModel")){
          backStore = ShmWebSession.BS_SHCLOSURE;
        } else if (className.contains("DBPersistentSessionModel")) {
          backStore = ShmWebSession.BS_DATABASE;
        } else if (className.contains("FilePersistentSessionModel")) {
          backStore = ShmWebSession.BS_FILE;
        } else if (className.contains("MemoryPersistentSessionModel")) {
          backStore = ShmWebSession.BS_SHMEM;
        }
      }
      shmSlot.setBackingStore(backStore);
      try {
        if (isActive()) shmSlot.store();
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }

    if (!isSticky() && clientContext != null && !persistedInClientContext) {
      try {
        clientContext.persistClientSession(this);
        persistedInClientContext = true;
      } catch (IOException e) {
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
  }

  private void activationFailed(ShmException e) throws ShmException {
    SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, loc, "ASJ.ses.rt0001", 
	"Can not create shm web object. Possible out of shm free slots. Check the shared memory configuration");
    loc.traceThrowableT(Severity.ERROR, "Cannot get Web session object [" + this + "]", e);
    throw e;
  }

  void activateShmSlot(Object request) throws ShmException {
    if (shmSlot == null) {
      
      clientCookie = request instanceof HttpSessionRequest ? ((HttpSessionRequest)request).getClientCookie() : sessionId();
      // we create new session
      if (request instanceof HttpSessionRequest && ((HttpSessionRequest) request).shmSlotIndex != -1) {
        //the request has JsessionID and the ICM has activated the shmWebSession slot
        //so we only get the slot, without activating it        
        try {
          shmSlot = ShmWebSession.getSession(((HttpSessionRequest)request).shmSlotIndex);
          if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("GET " + shmSlot + request), true);
        } catch (ShmException e) {
          activationFailed(e);
        }
      } else {
        //the request was new or dispatched to an application without already created session, 
        // so we will get one slot and activate it(by findSession())
        try {
          shmSlot = ShmWebSession.findSession(clientCookie, getAliasHashCode());
          if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("FIND " + shmSlot + request), true);
          shmSlot.setCacheValid(true);
        } catch (ShmException e) {
          if ( (expendables() == null) || (expendables().isEmpty()) ) {
            // no expendable RSMs
            activationFailed(e);
          } else {
            SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, loc, "ASJ.ses.rt0002", 
        	"Cannot create a shared memory Web object; possible lack of shared memory free slots. Trying to free some expendable slots");
            loc.warningT("Problems during creation of web shm slot. Attempting to free some expendable slots.");
            while (!expendables().isEmpty()) {
              try {
                // in case of hacker attack, this will slow down his requests also,
                // lowering the CPU consumption of the overloaded server and
                // giving enough time to the real request to login and continue
                Thread.sleep(1000);  
              } catch (InterruptedException ie) {
                // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
                // Please do not remove this comment!
              }
              expendables().getFirst().destroy();  // free one slot - the destroy() removes it from the expandable table
              try {
                shmSlot = ShmWebSession.findSession(clientCookie, getAliasHashCode());
                shmSlot.setCacheValid(true);
                break;
              } catch (ShmException ee) {
                // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
                // Please do not remove this comment!
              }
            }
            if (shmSlot == null) { // bad luck, we go out of memory
              activationFailed(e);
            }
          }
        }
      }
      // in case of failover and sticky session - the session slot is marked as corrupt,
      // so we'll re-use the slot 
      if(shmSlot.isCorrupt()) {
        shmSlot.setCorrupt(false);
        shmSlot.setSticky(true); // if it's not sticky this will change when persisting
        shmSlot.store();   // to be visible in the MMC
      }
      try{
	      if(shmSlot.getBackingStore() != ShmWebSession.BS_NONE){
	      	PersistentSessionModel model = persistentModel;
	      	persistentModel = domain.getSoftShutdownPersistentSessionModel(this);
		      setSession(internalLoadSession());
		      persistentModel = model;
		      SessionExecContext.getExecutionContext().setIsLoadbalanced(true);
	      }
      } catch (PersistentStorageException e) {
      	loc.traceThrowableT(Severity.WARNING, "Unable to load session", e);
      }
    } else {
      // we already have session
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.pathT("HttpRuntimeSessionModel.activateShmSlot(HttpSessionRequest) <" + request + ">");
      }
      if (request instanceof HttpSessionRequest && ((HttpSessionRequest)request).shmSlotIndex != -1) {
        // the request has JsessionID and the ICM has activated the shmWebSession slot, 
        // but the activated slot may not be the right one
        //  -it's possible the ICM to activate different alias, because of a lasy application startup(maybe fixed)
        // so we activate the slot by the given slotIndex 
        //   if the slot is different - transfer activation, otherwise does nothing
        if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("ACTIVATE index:" + ((HttpSessionRequest)request).shmSlotIndex + " - " + shmSlot + request), true);
        shmSlot.activate(((HttpSessionRequest)request).shmSlotIndex);
      } else  {
        // dispatch to application with already created session, so we activate it
        if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("ACTIVATE " + shmSlot + request), true);
        shmSlot.activate();
      }
    }

    String user = shmSlot.getUserName();
    ClientContextImpl current = ClientContextImpl.getByClientId(sessionId());
    if (current != null) {
      String usr = current.getUser();
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.pathT("HttpRuntimeSessionModel.activateShmSlot(HttpSessionRequest) set user to the Shm web session slot \n" +
                "Shm session user=" + user + ", current user=" + usr);
      }
      if (usr != null && !usr.equals(user)) {
        shmSlot.setUserName(usr);
      }
    }

    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("After HttpRuntimeSessionModel.activateShmSlot(HttpSessionRequest) " + this + " <" + request + ">");
    }
  }
  int getAliasHashCode() {
    Object alias = domain.getDomainAttribute(SessionDomain.SHARED_TABLE_ID);
    if (alias != null) {
      return alias.hashCode();
    }
    throw new IllegalArgumentException("The SessionDomain.SHARED_TABLE_ID attribute is not set.");
  }

  void deactivateShmSlot(HttpSessionRequest request, boolean commit) throws ShmException {
    ClientContextImpl current = SessionExecContext.getExecutionContext().currentClientContext();
    if (shmSlot != null) {
      if (commit) {
        if (session() == null) {
          if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
            loc.pathT("HttpRuntimeSessionModel.deactivateShmSlot(HttpSessionRequest, boolean) deactivate unchanged session "
                    + this + "<" + request + ">");
          }
          if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("DEACTIVATE " + shmSlot + request), true);
          shmSlot.deactivate(shmSlot.getSize(), shmSlot.isSticky(), shmSlot.getBackingStore());
          return;
        }
        int backStore = ShmWebSession.BS_NONE;
        boolean sticky = isSticky();
        if (persistentModel != null) {
          String className = persistentModel.getClass().getName();
          if(className.contains("ShMemoryPersistentSessionModel")){
            backStore = ShmWebSession.BS_SHCLOSURE;
          } else if (className.contains("DBPersistentSessionModel")) {
            backStore = ShmWebSession.BS_DATABASE;
          } else if (className.contains("FilePersistentSessionModel")) {
            backStore = ShmWebSession.BS_FILE;
          } else if (className.contains("MemoryPersistentSessionModel")) {
            backStore = ShmWebSession.BS_SHMEM;
          }
        }
        String user = shmSlot.getUserName();

        String usr = current != null ? current.getUser() : null;
        if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          loc.pathT("HttpRuntimeSessionModel.deactivateShmSlot(HttpSessionRequest, boolean)" + this + "<" + request + ">\n" +
                  "Shm session user=" + user + ", current user=" + usr + ", sticky=" + sticky + ", backingStore=" + backStore);
        }

        if (usr != null && !usr.equals(user)) {
          shmSlot.setUserName(usr);
        }
        if (maxInactiveInterval() != shmSlot.getTimeout()) {
          shmSlot.setTimeout((int) maxInactiveInterval() / 1000);
        }

        if (persistentModel() != null) {
          if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("DEACTIVATE " + shmSlot + request), true);
          shmSlot.deactivate(persistentModel().size(), sticky, backStore);
        } else {
          if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("DEACTIVATE " + shmSlot + request), true);
          shmSlot.deactivate(-1, sticky, backStore);
        }


      } else {
        if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          loc.pathT("HttpRuntimeSessionModel.deactivateShmSlot(HttpSessionRequest, boolean) deactivate hollow shm slot " +
                  this + " <" + request + ">");
        }
        if (Dump.activations_loc.beDebug()) Dump.out("---" + Thread.currentThread().getId(), new Exception("DEACTIVATE index: " + request.shmSlotIndex + " - " + request), true);
        ShmWebSession.deactivate(request.shmSlotIndex);
      }
      request.shmSlotIndex = -1;
    } else if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      loc.pathT("HttpRuntimeSessionModel.deactivateShmSlot(HttpSessionRequest, boolean)" + this + " <" + request + ">");
    }
  }

  LinkedList<HttpRuntimeSessionModel> expendables() {
    if (expendable == null) {
      expendable = new LinkedList<HttpRuntimeSessionModel>();
    }
    return expendable;
  }

  void setExpendable() {
    expendables().add(this);
    isExpandable = true;
  }

  protected void removeFromClientContext() {
    if (clientContext != null) {
      clientContext.removeClientSession(this);
      clientContext = null;
    } 
  }
  
  public String toString() {
    StringBuffer r = new StringBuffer(super.toString());
    r.append("shmSlot=").append(shmSlot).append("\n\r");
    if (clientContext != null) {
      r.append("client context hashcode : ").append(System.identityHashCode(clientContext)).append("\n\r");
    } else {
      r.append("no client context assigned").append("\n\r");
    }
    return r.toString();
  }

  public String getClientId() {
    return sessionId();
  }

  public boolean checkDestroy() {
    return (super.checkDestroy() && shmSlot.isClosed());
  }
  
  
  
  
  public void storeRuntimeModelAttributes(String key,Object data){
	  this.runtimeSessionModelAttributes.put(key, data);
  }
  
  
  public Object getRuntimeModelAttributes(String key){
	  return this.runtimeSessionModelAttributes.get(key);
  }
  
  public boolean isExpandable(){
	 return isExpandable; 
  }

  public ClientContext getClientContext() {
    return clientContext;
  }
  
}
