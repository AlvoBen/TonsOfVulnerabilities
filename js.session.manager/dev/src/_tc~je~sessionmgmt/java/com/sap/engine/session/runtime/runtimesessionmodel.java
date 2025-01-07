/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime;

import com.sap.engine.session.*;
import com.sap.engine.session.usr.ClientContext;
import com.sap.engine.session.usr.UserContext;
import com.sap.engine.session.monitoring.MonitoredObject;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.engine.session.monitoring.impl.AppSessionMonitoringNode;
import com.sap.engine.session.monitoring.impl.AbstractMonitoringNode;
import com.sap.engine.session.logging.LogFilteringSystem;
import com.sap.engine.session.logging.LogMessageSystem;
import com.sap.engine.session.runtime.http.HttpRuntimeSessionModel;
import com.sap.engine.session.runtime.timeout.TimeoutProcessor;
import com.sap.engine.session.runtime.timeout.ListEntry;
import com.sap.engine.session.trace.ThrTrace;
import com.sap.engine.session.mgmt.MgmtModel;
import com.sap.engine.session.callback.CallbackHandler;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;

/*
 * @author Georgi Stanev, Nikolai Neichev
 */
public class RuntimeSessionModel extends SessionModel implements MgmtModel, MonitoredObject {

  public static final Location loc = Location.getLocation("com.sap.engine.session.runtime", "kernel.sda", "BC-JAS-COR-SES");
  public static final String SESSION_CHUNK_NAME = "$S";
  public static final String CHUNKS_CHUNK_NAME = "$C";


  // public - set from Manager
  public static TimeoutProcessor timeoutProcessor;
  public static boolean deltaFailoverEnabled;
  public static boolean markGetChunk;

  private static CallbackHandler defaultHandler = new SessionInvalidationCallback();
  private String sessionId;
  private SessionWatchDog watchDog;
  private SessionInvalidator sessionInvalidator;
  private SessionFailoverMode failoverMode;
  private final Lock sessionMonitor = new ReentrantLock();
  private boolean ready;
  private boolean destroyed;
  protected boolean invalidated;
  /* it is sticky in the creation time. The flag is changed after first persist operation
   *  0x00 - not sticky session
   *  0xF? - user defined sticky session
   *  0x?F - runtime defined sticky session
   */
  private int stickyFlag = 0x0F;
  private int activeCounter;
  private long creationTime = System.currentTimeMillis();
  private long expirationTime;
  private long lastAccessedTime;
  private long invalidatorExecutionTime;
  private long invalidatorActualExecutionTime;
  private long watchdogSetTime;
  private long watchdogExecutionTime;
  private long watchdogActualExecutionTime;
  private long maxInactiveIntervalSetTime;
  private long watchdogCancelTime;
  private volatile long maxInactiveInterval;
  private Thread invalidationThread;
  private boolean invalidatorScheduled;
  private MonitoringNode monitoringNode = null;
  
  /**
   * Used for storing the failed persistent attempts during a recording.
   */
  public static HashSet<String> failedPersistance = new HashSet<String>();  

  public RuntimeSessionModel(String sessionId, SessionDomain domain, SessionFailoverMode failoverMode) {
    this.sessionId = sessionId;
    this.domain = domain;
    this.failoverMode = failoverMode;
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel(String, SessionDomain, SessionFailoverMode) is called.:" + this, null);
    }
  }

  public RuntimeSessionModel(String sessionId, SessionDomain domain, Session session, SessionFailoverMode failoverMode) {
    this(sessionId, domain, failoverMode);
    ready = true;
    if (session != null) {
      session.setDomain(domain.getReference());
      setSession(session);
    } else {
      NullPointerException npe = new NullPointerException("Can't create RuntimeSessionModel with session NULL.");
      if (loc.beDebug() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.traceThrowableT(Severity.DEBUG, LogMessageSystem.composeLogMessage(logIdentificator(), "session is null"), npe);
      }
      throw npe;
    }
    try {
      persistentModel = domain.getPersistentSessionModel(this);
    } catch (PersistentStorageException e) {
      if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e);
      }
      stickyFlag = stickyFlag | 0x0F; //runtime sticky
    }
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel(String, SessionDomain, Session, SessionFailoverMode) is called.:" + this, null);
    }
  }

  public String getSessionId() {
    return sessionId;
  }

  public long getCreationTime() {
    return creationTime;
  }

  protected Session internalLoadSession() throws PersistentStorageException {
    Session session = (Session) persistentModel().getChunk(SESSION_CHUNK_NAME);
    if (session != null) {
      if (!deltaFailoverEnabled) {
        session.setPersistedChunks((HashMap<String, Object>) persistentModel().getChunk(CHUNKS_CHUNK_NAME));
      }
    }
    return session;
  }

  protected void internalPersistSession() throws PersistentStorageException {
    //store the session object
    persistentModel().setChunk(SESSION_CHUNK_NAME, session());

    if (deltaFailoverEnabled) {
      // remove removed session chunks
      if (session().hasRemoveChunkNames()) {
        if (loc.beDebug() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          loc.debugT(LogMessageSystem.composeLogMessage(logIdentificator(), "Chunks to remove from the persistance : " + session().getRemoveChunkNames()));
        }
        for (String chunkName : session().getRemoveChunkNames()) {
          persistentModel().removeChunk(chunkName);
        }
        session().clearRemoveChunkNames();
      }

      // store the session chunks if modified
      if (session().hasModifyChunkNames()) {
        if (loc.beDebug() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          loc.debugT(LogMessageSystem.composeLogMessage(logIdentificator(), "Chunks to persist : " + session().getChunkNames()));
        }
        for (String chunkName : session().getModifyChunkNames()) {
          persistentModel().setChunk(chunkName, session().getChunkData(chunkName));
        }
        session().clearModifyChunkNames();
      }
    } else if (session().isChunksUpdated()){ // no delta failover and chunk table is updated
      //store the session object
      persistentModel().setChunk(CHUNKS_CHUNK_NAME, session().chunks());
    }
    session().resetChunksUpdated();
  }

  //called in synchronized(this) block to initialize the model
  private void postInit(SessionFactory factory) throws CreateException {
    if (destroyed) {
      SessionDestroyedException sde = new SessionDestroyedException("Session is destroyed");
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("RuntimeSessionModel.postInit(SessionFactory):" + this + "<" + factory + ">\n" + "throws SessionDestroyedException", sde);
      }
      throw sde;
    }

    try {
      persistentModel = domain.getPersistentSessionModel(this);
    } catch (PersistentStorageException e) {
      if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator(), "Problem creating persistent session model:" + compositeName()), e);
      }
      stickyFlag = stickyFlag | 0x0F; //runtime sticky
    }

    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.postInit(SessionFactory) loading session:" + this, null);
    }
    Session session = session();
    
    // If there is a persistent model for this session first we try to load it
    if (persistentModel() != null) {
      try {
        session = internalLoadSession();
        stickyFlag = stickyFlag & 0xF0; // runtime not sticky
      } catch (PersistentStorageException e) {
        if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator(), "Problem loading session " + compositeName() + " - " + e.getMessage()), e);
        }
        try {
          persistentModel().destroy();
          persistentModel = domain.getPersistentSessionModel(this);
        } catch (PersistentStorageException ex) {
          if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
            loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator(), "Problem recreating persistent session model:" + compositeName()), ex);
          }
          stickyFlag = stickyFlag | 0x0F; //runtime sticky
        }
      }
    }

    /*
      check is the session is available
    */
    if (session == null && factory != null) {
      session = factory.getSession(sessionId);
    }

    if (session != null) {
      session.setDomain(domain.getReference());
      if ((persistentModel() != null) && (persistentModel().expTime() > 0)) { // expTime = 0 means that this persistent model is created now
        if (persistentModel().expTime() < System.currentTimeMillis()) { // expired
          // this is the case when this session is moved to another server node and never accessed,
          // so it is detected by the check task and created, in order to be cleaned up from the storrage
          setMaxInactiveInterval(5); // this should clean the session up
          lastAccessedTime = 1;
        } else {
          // the session is not yet expired, updating the correct last access time
          lastAccessedTime = persistentModel().expTime() - session.getMaxInactiveIntervalField();
        }
      } else {
        lastAccessedTime = System.currentTimeMillis();
      }
      setSession(session);
      ready = true;
      // initialize the lifecycle objects, after the session is ready for use
      session.initInternalLifecycle();
    } else {
      // session object is not available - go to destroy this runtime model
//      destroy();
//      throw new SessionDestroyedException(this.toString());
    }

    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPath("State after RuntimeSessionModel.postInit(SessionFactory):" + this);
    }

  }

  protected synchronized void activate(Object info) {
    if (destroyed) {
      SessionDestroyedException sde = new SessionDestroyedException("Session is destroyed");
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("RuntimeSessionModel.activate(Object):" + this + " <" + info + ">\n" + "throws SessionDestroyedException", sde);
      }
      throw sde;
    }
    beforeActivate(info);

    activeCounter++;
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.activate(object):" + this + " <" + info + ">", null);
    }
    postActivate(info, false);
  }

  public boolean isActive() {
    return activeCounter != 0;
  }

  boolean isDestroyed() {
    return destroyed;
  }

  /*
   -1 - no session object
    0 - valid session
    1 - expired
    2 - invalidated (the session.invalidate is finished)
  */
  private byte state() {
    if (session() == null) {
      return -1;
    } else if (invalidated) {
      return 2;
    } else if (!isActive() && maxInactiveInterval >= 0) {
      if (lastAccessedTime > 0 && (System.currentTimeMillis() >= lastAccessedTime + maxInactiveInterval)) {
        //expired session but should be possible to access from the invalidation thread
        if (invalidationThread != null && invalidationThread == Thread.currentThread()) {
          return 0;
        }
        return 1;
      }
    }
    return 0;
  }

  private String stateString() {
    switch (state()) {
      case -1: {
        return "No session object";
      }
      case 0: {
        return "valid";
      }
      case 1: {
        return "expired";
      }
      case 2: {
        return "invalidated";
      }
    }
    return "Impossible state";
  }

  public Session getSession(SessionFactory factory) throws CreateException {
    Session storred = null;
    synchronized (this) {
      if (destroyed) {
        SessionDestroyedException sde = new SessionDestroyedException("Session is destroyed");
        if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          logPathDebug("RuntimeSessionModel.getSession(SessionFactory):" + this + " <" + factory + ">" + "throws SessionDestroyedException", sde);
        }
        throw sde;
      }

      if (!ready) {
        postInit(factory);
      }

      int state = state();
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("RuntimeSessionModel.getSession(SessionFactory) is called:" + this +  " <" + factory + ">", null);
      }
      switch (state) {
        case 0:      // valid
          return session();
        case 1:      // expired
          storred = session(); //keep the expired session in order to invalidate it later
          setSession(null); // remove session
      }

      if (factory != null) {
        sessionMonitor.lock();
        try {
          //should be checked again
          if (session() == null) {
            setSession(factory.getSession(sessionId));
            invalidated = false;
          }
        } finally {
          sessionMonitor.unlock();
        }
      } else {
        return null;
      }
    }

    // if there is an expired session invalidate it
    if (storred != null) {
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPath("RuntimeSessionModel.getSession(SessionFactory) invalidate expired session:" + storred);
      }
      storred.invalidate();
    }
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPath("State After RuntimeSessionModel.getSession(SessionFactory) " + this);
    }

    return session();
  }

  /**
   * Used from the scheduled persistence on thread end.
   *
   * @param info the meta-data info
   */
  public synchronized void persistAndCommit(Object info) {
    if (destroyed) {
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("RuntimeSessionModel.comit(Object) is called for destroyed model:" + this + " <" + info + ">", null);
      }
      return;
    }
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.commit(Object) is called:" + this + " <" + info + ">", null);
    }

    if (activeCounter == 1) { // final request
      if (invalidated || session() == null) {
        postComit(info);
        activeCounter--;        
        //should be removed from JAVA heap - doesn't refer to real session object
        if (sessionMonitor.tryLock()) {
          // no thread that is creating the session object in the same time
          try {
            destroy();
          } finally {
            sessionMonitor.unlock();
          }
        }
        return;
      } else { // persisting
        persist();
      }
    } else if (activeCounter <= 0) { // troubles
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("Trying to deactivate inactive session:" + this);
      }
      return;
    }
    activeCounter--;
    postComit(info);
  }

  public synchronized void commit(Object info) {
    if (destroyed) {
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("RuntimeSessionModel.comit(Object) is called for destroyed model:" + this + " <" + info + ">", null);
      }
      return;
    }
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.commit(Object) is called:" + this + " <" + info + ">", null);
    }

    if (activeCounter == 1) { // final request
      if ((invalidated || session() == null) && !isExpandable())  {
    	//  
        postComit(info);
        activeCounter--;
        //should be removed from JAVA heap - doesn't refer to real session object
        if (sessionMonitor.tryLock()) {
          // no thread that is creating the session object in the same time
          try {
            destroy();
          } finally {
            sessionMonitor.unlock();
          }
        }
        return;
      } else { // schedule for persistence
        lastAccessedTime = System.currentTimeMillis();
        failoverMode.schedule(this);
      }
    } else if (activeCounter <= 0) { // troubles
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("Trying to deactivate inactive session:" + this);
      }
      return;
    }

    setIsNew(false);
    activeCounter--;
    postComit(info);
  }

  /**
   * This method is overrided in HttpRuntimeSessionModel
   * Only HttpRuntimeSessionModel could be expandables.
   * @return true 
   */
   protected boolean isExpandable() {
     return false; 
   }

/**
   * in contrast to commit:
   * - doesn't update last accessed time
   * - doesn't schedule for persistence
   *
   * @param info - request info object
   */
  public synchronized void release(Object info) {
    if (activeCounter > 0) {
      activeCounter--;
      postReleaseSession(info);
    } else if (activeCounter <= 0) {
      if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.warningT(LogMessageSystem.composeLogMessage(logIdentificator(), "Try to deactivate inactive session:" + this));
        if (loc.beDebug()) {
          loc.traceThrowableT(Severity.DEBUG, LogMessageSystem.composeLogMessage(logIdentificator()), new IllegalStateException(this.toString()));
        }
      }
      return;
    }
    if (!isActive()) {
      if (invalidated || isNew()) {
        //should be removed from JAVA heap - doesn't refer to real session object
        if (sessionMonitor.tryLock()) {
          // no thread that is creating the session object in the same time
          try {
            destroy();
          } finally {
            sessionMonitor.unlock();
          }
        }
      }
    }
  }

  public synchronized void persist() {
    if (destroyed) {
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("RuntimeSessionModel.persist() is called for destroyed model:" + this, null);
      }
      return;
    }
    if (state() == 0 && stickyFlag < 0xF0 && session() != null && persistentModel() != null) {
      // stickyFlag < 0xF0 means that the session is not user defined sticky
      try {
        if (loc.beDebug() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          logPathDebug("Persisting session : " + session() + " chunks: " + session().getChunkNames(), null);
        }
        internalPersistSession();
        persistentModel().unlock();
        stickyFlag = stickyFlag & 0xF0; //set as runtime not sticky
      } catch (PersistentStorageException e) {
        failedPersistance.add(domain().getName());
        domain().addNonSerializableSession();
        SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, loc, "ASJ.ses.ps0009", 
            "Can not persist session. Problem during persisting. Check the persistence state");
        if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
          loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e);
        }
        stickyFlag = stickyFlag | 0x0F; //set as runtime sticky session
      }
    }
  }

  /**
   * Invoked when the application is stopped
   * @return true if it was needed
   */
  public boolean invalidateIfNeeded() {
    if (persistentModel() == null || isSticky()) {
      try {
        // apply the user to the invalidating thread
        SessionExecContext.getExecutionContext().applyUserContext(sessionId);
        synchronized (this) {
          session().invalidate();
        }
      } finally {
        // apply to Guest, because usually the stop is called with Guest
        SessionExecContext.getExecutionContext().applyUserContext(null);
      }
      return true;
    } else {
      setSession(null);
      ready = false;
      return false;
    }
  }

  protected  void invalidate() {
    synchronized (this) {
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        logPathDebug("RuntimeSessionModel.invalidate() is called:" + this, null);
      }
      try {
        invalidated = true;
        setSession(null);
        if (persistentModel() != null) {
          try {
            persistentModel().destroy();
            persistentModel = domain.getPersistentSessionModel(this);
          } catch (PersistentStorageException e1) {
            if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
              loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e1);
            }
            stickyFlag = stickyFlag | 0x0F; //set as sticky session
          }
        }
      } finally {
        postInvalidate();
      }
      destroy();
    }
    if (!Thread.currentThread().equals(invalidationThread)) {
      removeFromClientContext();
    }
  }

  protected synchronized void remove() {
    destroy();
  }

  public final void destroy() {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.destroy() is called:" + this, null);
    }
    if (domain == null || isActive()) {
      return;
    }
    beforeDestroy();
    domain.removeSession(this);
    ready = false;
    destroyed = true;
    failoverMode = null;
    if (watchDog != null) {
      watchdogCancelTime = System.currentTimeMillis();
      watchDog.cancel();
    }
    domain = null;
    PersistentSessionModel pm = persistentModel();
    persistentModel = null;
    try {
      if (pm != null) {
        pm.destroy();
      }
    } catch (PersistentStorageException e) {
      if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e);
      }
    }
    postDestroy();
  }

  private void setWatchDogPeriod(long delay, long period) {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.setWatchDogPeriod() is called:" + this + "delay/period: " + delay + "/" + period, null);
    }
    if (watchDog != null) {
      if (watchDog.isRegular()) {
        //can not preset the time out period for regular watchDog
        lastAccessedTime = System.currentTimeMillis();
        return;
      }
      watchdogCancelTime = System.currentTimeMillis();
      watchDog.cancel();
    }
    if (delay >= 0 && period > 0) {
      watchDog = new SessionWatchDog();
      long currentTime = System.currentTimeMillis();
      watchdogSetTime = currentTime;
      expirationTime = currentTime + delay;
      watchDog.setExpirationTime(expirationTime);
      watchDog.setMaxInactiveInteval(maxInactiveInterval);
      if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
        loc.pathT("Scheduling watchdog for session: " + sessionId);
      }
      timeoutProcessor.schedule(watchDog);
    }

  }

  public void scheduleRegularTimeout(int period) {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.scheduleRegularTimeout() is called:" + this + " period: " + period, null);
    }
    if (watchDog != null) {
      watchdogCancelTime = System.currentTimeMillis();
      watchDog.cancel(); // cancel the timer task and reschedule
    }
    watchDog = new SessionWatchDog();
    watchDog.setRegular(true);
    timeoutProcessor.timerSchedule(watchDog, 0, period);
  }

  /**
   * ************* before/post methods***********************************
   */

  /**
   * @param info the info
   */
  protected void beforeActivate(Object info) {
  }

  protected void postActivate(Object info, boolean rolback) {
  }

  protected void postComit(Object info) {
  }

  protected void postReleaseSession(Object info) {
  }

  protected void beforeDestroy() {
  }

  protected void postDestroy() {
  }

  protected void postInvalidate() {
  }

  /**
   * *********** Session model*********************
   */

  public PersistentSessionModel persistentModel() {
    return super.persistentModel();
  }

  public MgmtModel mgmtModel() {
    return this;
  }

  protected RuntimeSessionModel runtimeModel() {
    return this;
  }

  public String sessionId() {
    return sessionId;
  }

  public String compositeName() {
    StringBuffer name = new StringBuffer("[ctx:").append(domain.getEnclosingContext().getName()).append("]");
    name.append(domain.getEnclosingContext().getName()).append("\\");
    name.append(sessionId);
    return name.toString();
  }

  public SessionDomain domain() {
    return domain;
  }

  public long lastAccessedTime() {
    if (session() != null) {
      return lastAccessedTime;
    } else {
      return -1;
    }
  }

  public long maxInactiveInterval() {
    return maxInactiveInterval;
  }

  public synchronized void setMaxInactiveInterval(int p) {
    if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(this)) {
      logPathDebug("RuntimeSessionModel.setMaxInactiveInterval() is called:" + this + " period: " + p, null);
    }
    long currentTime = System.currentTimeMillis();
    maxInactiveIntervalSetTime = currentTime;
    maxInactiveInterval = p * 1000;
    long delay = maxInactiveInterval - (currentTime - lastAccessedTime);
    delay = delay < 0 ? maxInactiveInterval : delay;
    // +1000 ms, because of the first commit is allways after the scheduling
    setWatchDogPeriod(delay + 1000, maxInactiveInterval);
  }

  public boolean isValid() {
    return state() == 0;
  }

  /**
   * Check if the session is invalidated
   *
   * @return TRUE if it is invalidated
   */
  public boolean isInvalidated() {
    int state = state();
    return (state == 1 || state == 2); // 1 - expired, 2 - invalidated
  }

  public synchronized void renew() {
    creationTime = System.currentTimeMillis();
    lastAccessedTime = System.currentTimeMillis();
  }

  public boolean isSticky() {
    return stickyFlag > 0;
  }

  /**
   * Define session as sticky session. When this method is called with parameter true
   * the session is set as "user defined sticky session" - in terms that the user decide that the session
   * should be sticky for some period in contrast to "runtime sticky session" when the session is marked as
   * sticky because it is not possible to be persisted (not serializable).
   *
   * @param flag - sticky flag.
   */
  public synchronized void setSticky(boolean flag) {
    if (flag) {
      stickyFlag = stickyFlag | 0xF0;  //user defined sticky - 0xF?
    } else {
      stickyFlag = stickyFlag & 0x0F; // user defined not sticky - 0x0?
    }
  }

  /**
   * Check if the session is destroyed fine
   * !!! use this method only to confirm destroy !!!
   * The HTTP and EJB models should override this method and do the required check.
   *
   * @return true if the session is destroyed fine
   */
  public boolean checkDestroy() {
    return destroyed;
  }

  private String logIdent = null;

  private String logIdentificator() {
    if (logIdent == null) {
      logIdent = LogMessageSystem.getLogIdentificator(domain(), sessionId());
    }
    return logIdent;
  }
  
  protected void removeFromClientContext(){
  }

  private void logPath(String message) {
    loc.pathT(LogMessageSystem.composeLogMessage(logIdentificator(), message));
  }

  private void logPathDebug(String message) {
    logPathDebug(message, null);
  }

  private void logPathDebug(String message, Exception exc) {
    String logMessage = LogMessageSystem.composeLogMessage(logIdentificator(), message);
    if (loc.beDebug()) {
      if (exc == null) {
        exc = new ThrTrace();
      }
      loc.traceThrowableT(Severity.DEBUG, logMessage, exc);
    } else {
      loc.pathT(logMessage);
    }
  }

  public String toString() {
    String NL = "\r\n";
    StringBuffer sb = new StringBuffer(super.toString());
    if (destroyed) {
      sb.append(NL).append("Session is DESTROYED");
    } else {
      sb.append(NL).append("session id=").append(sessionId).append(',');
      SessionDomain d = domain;
      String dName = d != null ? d.getName() : "n/a";
      sb.append(NL).append("domain=").append(dName).append(',');
      sb.append(NL).append("creation time=").append(new Date(creationTime)).append(',');
      sb.append(NL).append("last accessed time=").append(new Date(lastAccessedTime)).append(',');
      sb.append(NL).append("sticky=").append(isSticky()).append(',');
      sb.append(NL).append("invalidated=").append(isInvalidated()).append(',');
      sb.append(NL).append("expandable=").append(isExpandable()).append(',');
      sb.append(NL).append("active requests=").append(activeCounter).append(',');
      sb.append(NL).append("state=").append(stateString());
      sb.append(NL).append("pers model=").append(persistentModel).append(',');
      sb.append(NL).append("failover mode=").append(failoverMode);
      sb.append(NL).append("Session=").append(session());
    }

    return NL + sb.toString() + NL;
  }

  /**
   * @return additional info that will be added to the toString method
   */
  protected String additionalInfo() {
    return null;
  }

  private SessionInvalidator sessionInvalidator() {
    if (sessionInvalidator == null) {
      sessionInvalidator = new SessionInvalidator();
    }
    return sessionInvalidator;
  }

  /**
   * Used for invalidating of the session
   */
  public class SessionInvalidator implements Runnable {

    private CallbackHandler getCallbackHandler() {
      CallbackHandler callbackHandler = (CallbackHandler) domain.configuration().get(SessionHolder.CALLBACK_HANDLER);
      return (callbackHandler != null) ? callbackHandler : defaultHandler;
    }

    private void invalidateAssertion() {
      long currTime = System.currentTimeMillis();
      long check = currTime - expirationTime;
      // invalidating earlier or more than 60 seconds larer or in debug mode
      if (((check < 0) || (check > 60 * 1000)) && loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
        String newLine = "\r\n";
        StringBuilder sb = new StringBuilder(newLine);
        sb.append("Session invalidator executed ");
        if (check < 0) { // before expiration time
          sb.append(-check / 1000).append(" seconds before expiration !").append(newLine);
        } else if (check > 60 * 1000) { // delayed more than 60 seconds
          sb.append(check / 1000).append(" seconds after expiration !").append(newLine);
        } else {
          sb.append("right on time.");
        }
        sb.append(newLine);
        Date time = new Date();
        time.setTime(creationTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - creation time").append(newLine);
        time.setTime(expirationTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - expiration time").append(newLine);
        time.setTime(lastAccessedTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - last access time").append(newLine);
        time.setTime(maxInactiveIntervalSetTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - max inactive interval set time").append(newLine);
        sb.append("MaxInactiveInterval : ").append(maxInactiveInterval).append(newLine);
        time.setTime(watchdogSetTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - watchdog set time").append(newLine);
        time.setTime(watchdogExecutionTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - watchdog execution time").append(newLine);
        time.setTime(watchdogActualExecutionTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - watchdog execution sync time").append(newLine);
        time.setTime(watchdogCancelTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - watchdog cancel time").append(newLine);
        time.setTime(invalidatorExecutionTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - invalidator execution time").append(newLine);
        time.setTime(invalidatorActualExecutionTime);
        sb.append(time.toString()).append("|").append(time.getTime()).append(" - invalidator execution sync time / current time").append(newLine);
        loc.warningT(LogMessageSystem.composeLogMessage(logIdentificator(),sb.toString()));
      }
    }

    public void run() {  // session timeout
      invalidatorExecutionTime = System.currentTimeMillis();
      SessionHolder holder;
      if (!RuntimeSessionModel.this.watchDog.isRegular() &&
              loc.bePath() &&
              LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
        logPath("Session invalidator executed for session: " + sessionId);
      }
      ClientContextImpl clientContextApplied = null;
      try {
        boolean isInvalidationComplete = false;
        synchronized (RuntimeSessionModel.this) {
            // apply the user to the invalidating thread
          try {
            SessionExecContext.getExecutionContext().applyUserContext(sessionId);
            if (runtimeModel() instanceof HttpRuntimeSessionModel) {
              ClientContext cc = ((HttpRuntimeSessionModel) runtimeModel()).getClientContext();
              if (cc instanceof ClientContextImpl) {
                clientContextApplied = SessionExecContext.applyClientContext((ClientContextImpl) cc);
              }
            }
          } catch (IllegalStateException e) {
            if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
              loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e);
            }
          }
          invalidatorActualExecutionTime = System.currentTimeMillis();
          boolean isValid = isValid();
          if (destroyed) { // if already destroyed
            return;
          } else if (invalidated) { // if already invalidated but not destroyed - just destroy
            destroy();
            return;
          }
          invalidationThread = Thread.currentThread();
          holder = domain.getSessionHolder(sessionId);
          if (!isValid && !isActive()) {
            try {
              if (!RuntimeSessionModel.this.watchDog.isRegular()) { // if not regular
                invalidateAssertion();
              }
              getCallbackHandler().handle(holder);
              isInvalidationComplete = true;
            } catch (Throwable e) {
              if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
                loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e);
              }
            } finally {
              if (holder != null && !((AbstractSessionHolder) holder).isApplied()) {
                holder.releaseAccess();
              } else {
                destroy();
              }
            }
          } else {
            Session s = session();
            if (s == null) {
              return;
            }
            
            if(!isActive()){
            	s.updateInternalLifecycle();
            }
            if (s.isChunksUpdated()) {
              try {
                holder.commitAccess();
              } catch (SessionException e) {
                if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
                  loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e);
                }
              } catch (IOException e) {
                if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
                  loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), e);
                }
              }
            }
          }
        }
        if(isInvalidationComplete){
          RuntimeSessionModel.this.removeFromClientContext();
        }
      } catch (Throwable t) {
        if (loc.beWarning() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
          loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), t);
        }
      } finally {
        invalidationThread = null;
          // free the invalidating thread
        UserContext.getAccessor().empty();
        SessionExecContext.applyClientContext(clientContextApplied);
        invalidatorScheduled = false;
      }
    }

    public String info() {
      return domain() == null ? "unknown domain" : domain().getName();
    }
  }

  /**
   * Used for watching when the session has to be invalidated When that time comes, it registers a SessionInvalidator for execution
   */
  public class SessionWatchDog extends ListEntry {

    /**
     * TRUE if there are lifecycle objects in the chunks, so the session is checked regularly
     */
    boolean isRegular = false;

    public boolean isRegular() {
      return isRegular;
    }

    public void setRegular(boolean regular) {
      isRegular = regular;
    }

	private void scheduleSessionInvalidatorForExecution() {
		if (!invalidatorScheduled) {
			invalidatorScheduled = true;
			timeoutProcessor.executeSessionInvalidator(sessionInvalidator());
		}
	}

    public void run() { // timeout
      watchdogExecutionTime = System.currentTimeMillis();
      if (!isRegular && loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
        logPathDebug("Watchdog executed...");
      }
      try {
        synchronized (RuntimeSessionModel.this) {
          watchdogActualExecutionTime = System.currentTimeMillis();
          if (invalidated) {
            destroy();
          }
          if (isRegular) { // fire the invalidator of a regular session
            if (!isRegular && loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
              logPathDebug("Session invalidator schduled");
            }
            scheduleSessionInvalidatorForExecution();
          } else if (!isValid()) {  // invalidating session
            if (!isRegular && loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
              logPathDebug("Session invalidator schduled");
            }
			scheduleSessionInvalidatorForExecution();
          } else if (!isActive()) {
            // in case the session is updated, and the new expiration time is more than 5% after the original period - reschedule
            long inactiveInterval = (System.currentTimeMillis() - lastAccessedTime);
            long delta = (maxInactiveInterval - inactiveInterval);
            long deltaPercent = 100 * delta / maxInactiveInterval;
            if (delta <= 0) { // not deltaPercent because it's rounded to 0 even if it is bigger, because it is integer
              if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
                logPathDebug("Session invalidator schduled");
              }
              scheduleSessionInvalidatorForExecution();
            } else if (deltaPercent < 95) { // if the wait overtime is more than 5%, reschedule it
              if (loc.bePath() && LogFilteringSystem.getFilter("RuntimeSessionFilter").toLog(RuntimeSessionModel.this)) {
                logPathDebug("Rescheduling watchdog");
              }
              setWatchDogPeriod(delta, maxInactiveInterval);
            }
          } else {// if it is active, it will be collected by the next watchdog run
            expirationTime += maxInactiveInterval; // expiration time is the next expected timer invocation
          }
        }
      } catch (Throwable t) {
        loc.traceThrowableT(Severity.WARNING, LogMessageSystem.composeLogMessage(logIdentificator()), t);
      }
    }
  }

  public boolean isInvalidateCalled(){
    return invalidated;
  }  

  private void updateMonitoringNode() {
    if (this.monitoringNode == null) {
      this.monitoringNode = new AppSessionMonitoringNode<RuntimeSessionModel>(this.getSessionId(), this);
    } else {
      ((AbstractMonitoringNode) this.monitoringNode).setReferent(this);
    }
  }

  public MonitoringNode getMonitoredObject() {
    updateMonitoringNode();
    return this.monitoringNode;
  }

	public void setFailoverMode(SessionFailoverMode failoverMode) {
		this.failoverMode = failoverMode;
	}

}