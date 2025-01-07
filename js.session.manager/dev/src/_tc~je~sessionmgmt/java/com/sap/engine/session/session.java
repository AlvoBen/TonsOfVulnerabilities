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

import com.sap.engine.core.Names;
import java.io.Serializable;
import java.util.*;

import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.usr.UserContext;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.data.SessionChunk;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class present the user session from Session management point of view
 * Author: georgi-s
 * Date: Feb 24, 2004
 */
public abstract class Session implements Serializable {

  protected static final Location sessionLocation = Location.getLocation(Session.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  /*
  * monitor object used for internal synchronization. As an object exposed to the
  * applications it is dangerous to  synchronize to the Session object itself
  */
  protected final Object sessionMonitor = new String("SesMonitor");

  //indicate is the session is explicitly invalidated
  private transient boolean invalidated;

  //the creation time
  private long creationTime;

  int maxInactivInterval = -1;
  /**
   * to provide sessionId if the session model is still not set.
   * For example during the SessionFactory.getSession() method - if the factory call
   * the sessionId() method before returnning the session will thorw IllegalStateException
   */
  transient String sessionId;

  // is the chunk table updated
  private transient boolean chunkUpdated;

  private DomainReference domainRef;

  //todo for temporary usege should be removed from Web container
  private boolean isExtracted;

  transient SessionModel thisModel;

  public transient Object UserAlias;

  /**
   * mark that this is a new session. the session is new if it is created but it is still not
   * associated with a SessionModel. By deafault the session is valid still it is new
   */
  private transient boolean isNew;

  // used for tracking the changed chunks
  private transient HashSet<String> modifiedChunks;

  // used for tracking the changed chunks
  private transient HashSet<String> removedChunks;

  // transient, because they'll be stored in separate persistence chunks
  private transient HashMap<String, Object> chunks;

  /**
   * Create new session object
   *
   * @param sessionId - for backwards compatibility
   */
  public Session(String sessionId) {
    this.creationTime = System.currentTimeMillis();
    maxInactivInterval = -2;
    this.sessionId = sessionId;
    this.isNew = true;
  }

  public void setDomain(DomainReference domainRef) {
    this.domainRef = domainRef;
  }

  // instantiations
  private HashMap<String, Object> sessionChunks() {
    if (chunks == null) {
    	synchronized (this) {
    		if (chunks == null) {
    			chunks = new HashMap<String, Object>(2);
    		}
    	}
    }
    return chunks;
  }

  public void setPersistedChunks(HashMap<String, Object> chunks) {
    this.chunks = chunks;
  }

  private HashSet<String> modifiedChunks() {
    if (modifiedChunks == null) {
    	synchronized (this) {
    		if (modifiedChunks == null) {
    			modifiedChunks = new HashSet<String>(2);
    		}
    	}
    }
    return modifiedChunks;
  }

  private HashSet<String> removedChunks() {
    if (removedChunks == null) {
    	synchronized (this) {
    		if (removedChunks == null) {
    			removedChunks = new HashSet<String>(2);
    		}
    	}
    }
    return removedChunks;
  }

  public int chunkCount() {
    synchronized (sessionMonitor) {
      return sessionChunks().size();
    }
  }

  public Collection<String> getChunkNames() {
    synchronized (sessionMonitor) {
      return sessionChunks().keySet();
    }
  }

  public boolean hasModifyChunkNames() {
    synchronized (sessionMonitor) {
      return !modifiedChunks().isEmpty();
    }
  }

  public Collection<String> getModifyChunkNames() {
    synchronized (sessionMonitor) {
      return modifiedChunks();
    }
  }

  public void clearModifyChunkNames() {
    synchronized (sessionMonitor) {
      modifiedChunks().clear();
    }
  }

  public boolean hasRemoveChunkNames() {
    synchronized (sessionMonitor) {
      return !removedChunks().isEmpty();
    }
  }

  public Collection<String> getRemoveChunkNames() {
    synchronized (sessionMonitor) {
      return removedChunks();
    }
  }

  public void clearRemoveChunkNames() {
    synchronized (sessionMonitor) {
      removedChunks().clear();
    }
  }

  public Iterator chunksIterator() {
    Iterator<Object> iterator;
    synchronized (sessionMonitor) {
      iterator = sessionChunks().values().iterator();
    }
    HashSet<Object> chunkSet = new HashSet<Object>(sessionChunks().size());
    while (iterator.hasNext()) {
      Object element = iterator.next();
      if (element instanceof SessionChunk) {
        chunkSet.add(element);
      }
    }
    return chunkSet.iterator();
  }

  public HashMap<String, Object> chunks() {
    return sessionChunks();
  }


  public SessionModel getSessionModel() {
    return this.thisModel;
  }

  public SessionDomain domain() {
    if (thisModel != null) {
      return thisModel.mgmtModel().domain();
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  public boolean isExtracted() {
    return isExtracted;
  }

  /**
   * @param model the session model
   */
  void setSessionModel(SessionModel model) {
    if (thisModel == null || model == null) {
      this.thisModel = model;
      isNew = false;
    } else if(model != thisModel){  	
      IllegalStateException is = new IllegalStateException("Current active state of this session is present from:" +
              thisModel + "\n setRuntimeModel:" + model + " can't be performed.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, "", is);
      throw is;
    }
  }

  public void setExtracted(boolean extracted) {
    isExtracted = extracted;
  }

  /**
   * @return session id
   * @deprecated use sessionId() instead
   */
  public String getId() {
    return sessionId();
  }

//  // Not used  
//  public String _getId() {
//    if (thisModel != null) {
//      return thisModel.mgmtModel().sessionId();
//    } else {
//      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
//      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
//      throw is;
//    }
//  }

  public String sessionId() {
    if (thisModel != null) {
      return thisModel.mgmtModel().sessionId();
    } else if (sessionId != null) {
      return sessionId;
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  /**
   * @return last accessed time in ms
   */
  public long getLastAccessedTime() {
    if (thisModel != null) {
      return thisModel.mgmtModel().lastAccessedTime();
    } else {
      return -1;
//      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
//      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
//      throw is;
    }
  }

  /**
   * @return creation time in ms
   */
  public long getCreationTime() {
    if (thisModel != null) {
      return creationTime;
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  /**
   * Specifies the time, in seconds, between client requests before the Session Manager
   * will invalidate this session. A negative time indicates the session
   * should never timeout.
   *
   * @param interval - An integer specifying the number of seconds.
   */
  public void setMaxInactiveInterval(int interval) {
    if (thisModel != null) {
      maxInactivInterval = interval;
      this.maxInactivInterval = interval;
      thisModel.runtimeModel().setMaxInactiveInterval(interval);
    } else if (maxInactivInterval == -2) {
      maxInactivInterval = interval;
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  /**
   * Returns the maximum time interval, in seconds, that the Session Manager will
   * keep this session open between client accesses. After this interval, the Session
   * Manager will invalidate the session. The maximum time interval can be set
   * with the setMaxInactiveInterval method. A negative time indicates the
   * session should never timeout.
   *
   * @return an integer specifying the number of seconds this session remains
   *         open between client requests
   */
  public int getMaxInactiveInterval() {
    if (thisModel != null) {
      return (int) thisModel.runtimeModel().maxInactiveInterval() / 1000;
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  /**
   * Used from the RuntimeSessionModel when session is recovered
   * from the persistant storrage
   *
   * @return the deserialised maxInactivInterval value
   */
  public int getMaxInactiveIntervalField() {
    return maxInactivInterval;
  }


  /**
   * @return true if this is an valid session object
   */
  public final boolean isValid() {
    if (thisModel != null) {
      return thisModel.mgmtModel().isValid();
    } else {
      return isNew;
    }
  }

  public boolean expired() {
    return isValid();
  }

  protected PersistentSessionModel getPersistentModel() {
    if (thisModel != null) {
      return thisModel.persistentModel();
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  protected void renew() {
    if (thisModel != null) {
      thisModel.mgmtModel().renew();
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  public void invalidate() {
    // the internal synch monitor should be used instead of method
    // synchronization. Deadlock prevention
    SessionModel local = null;
    synchronized (sessionMonitor) {
      if (thisModel != null) {
        invalidated = true;
        if(thisModel.domain != null) {
          thisModel.domain.addInvalidatedSession();
        }
        local = thisModel;
      } else if (invalidated) {
        IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
        RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
        throw is;
      }
    }
    if (local != null) {
      local.invalidate();
    }
    invalidated();
  }

  public boolean isSticky() {
    if (thisModel != null) {
      return thisModel.runtimeModel().isSticky();
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  //todo get it from persistem model
  public int failoverScope() {
    return 0;
  }

  public void setSticky(boolean sticky) {
    if (thisModel != null) {
      thisModel.runtimeModel().setSticky(sticky);
    } else {
      IllegalStateException is = new IllegalStateException("Can't be accessed out of session request scope.");
      RuntimeSessionModel.loc.traceThrowableT(Severity.WARNING, this.toString(), is);
      throw is;
    }
  }

  protected abstract void invalidated();


  public void access() {
  }


  final void invalidateInternally() {
    invalidated();
    UserContext.getAccessor().removeSession(this);
  }

  /**
   * Checks if the delta failover is enabled
   * @return TRUE if the failover and the delta failover is enavled; FALSE if one of the previous is not enabled
   */
  protected boolean deltaFailoverEnabled() {
    if (thisModel != null) {
      return (thisModel.runtimeModel().persistentModel() != null) && RuntimeSessionModel.deltaFailoverEnabled;
    } else {
      return false;      
    }
  }

  protected boolean markGetChunk() {
    return RuntimeSessionModel.markGetChunk;
  }

  public void updateInternalLifecycle() {
  }

  public void initInternalLifecycle() {
  }

  public void addChunkData(String name, Object data) {
    chunkUpdated = true;
    synchronized (sessionMonitor) {
      sessionChunks().put(name, data);
      if (deltaFailoverEnabled()) {
        modifiedChunks().add(name);
        removedChunks().remove(name);
      }
    }
  }

  public Object getChunkData(String name) {
    chunkUpdated = true;
    synchronized (sessionMonitor) {
      Object chunk = sessionChunks().get(name);
      if (chunk == null && deltaFailoverEnabled()) { // try load from persistence
        try {
          chunk = getPersistentModel().getChunk(name);
          if (chunk != null) { //cache it
            sessionChunks().put(name, chunk);
            if (markGetChunk()) {
              modifiedChunks().add(name);
            }
          }
        } catch (PersistentStorageException pse) {
          RuntimeSessionModel.loc.traceThrowableT(Severity.ERROR, this.toString(), pse);
        }
      }
      return chunk;
    }
  }

  public Object removeChunk(String name) {
    chunkUpdated = true;
    synchronized (sessionMonitor) {
      if (deltaFailoverEnabled()) {
        removedChunks().add(name);
        modifiedChunks().remove(name);
      }
      return sessionChunks().remove(name);
    }
  }

  public boolean isChunksUpdated() {
    return chunkUpdated;
  }

  public void resetChunksUpdated() {
    chunkUpdated = false;
  }

  public void beforeLogout() {
  }

  public void addRuntimeDependency(DomainReference dRef, String lifecycleData) {

  }

  public void removeRuntimeDependancy(DomainReference dRef, String lifecycleData) {

  }

}
