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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.LifecycleManagedData;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.runtime.http.HttpRuntimeSessionModel;
import com.sap.engine.session.trace.Trace;
import com.sap.engine.session.usr.ClientContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Author: georgi-s Date: Feb 24, 2004
 */
public abstract class AppSession extends Session {

  protected static final Location loc = Location.getLocation(AppSession.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  
  
  private HashSet<String> attributeNames;
  private transient Hashtable<String, Object> attributes;
  private HashSet<String> lifecycleManagedObjectNames;
  private transient HashMap<String, LifecycleManagedDataEntry> lifecycleManagedObjects;
  private boolean invalidateATM;
  private Thread invalidationThread;

  public AppSession(String sessionId) {
    super(sessionId);
  }

  protected HashSet<String> attributeNames() {
    if (attributeNames == null) {
      synchronized (sessionMonitor) {
        if (attributeNames == null) {
          attributeNames = new HashSet<String>(2);
        }
      }
    }
    return attributeNames;
  }

  private Hashtable<String, Object> attributes() {
    if (attributes == null) {
      synchronized (sessionMonitor) {
        if (attributes == null) {
          attributes = new Hashtable<String, Object>(2);
        }
      }
    }
    return attributes;
  }

  private HashSet<String> lifecycleManagedObjectNames() {
    if (lifecycleManagedObjectNames == null) {
      synchronized (sessionMonitor) {
        if (lifecycleManagedObjectNames == null) {
          lifecycleManagedObjectNames = new HashSet<String>(2);
        }
      }
    }
    return lifecycleManagedObjectNames;
  }

  public Object getAttribute(String name) {
    if (Trace.beDebug()) {
      Trace.trace("<AppSession> getAttribute:" + name);
    }
    checkState();
    Object attribute = attributes().get(name);
    if (attribute == null) { // try to get it from the persistence
      attribute = getChunkData(name);
      if (attribute != null) {
        attributes().put(name, attribute);
      }
    }
    if (Trace.beDebug()) {
      Trace.trace("<AppSession> getAttribute returns :" + attributes().get(name));
    }
    return attribute;
  }

  public void setAttribute(String name, Object value) {
    if (Trace.beDebug()) {
      Trace.trace("<AppSession> setAttribute:" + name + " " + value);
    }
    checkState();
    if (isValid()) {
      addChunkData(name, value);
      attributes().put(name, value);
      attributeNames().add(name);
    } else {
      throw new IllegalStateException();
    }
  }

  public Object getRemoveAttribute(String name) {
    checkState();
    if (isValid()) {
      removeChunk(name);
      attributeNames().remove(name);
      return attributes().remove(name);
    } else {
      throw new IllegalStateException();
    }
  }

  public void invalidate() {

    ClientContextImpl clientContextApplied = null;
    // if ClientContext is not assigned to the current Thread
    if (thisModel instanceof HttpRuntimeSessionModel) {
      ClientContext cc = ((HttpRuntimeSessionModel) thisModel).getClientContext();
      if (cc instanceof ClientContextImpl) {
        clientContextApplied = SessionExecContext.applyClientContext((ClientContextImpl) cc);
      }
    }
    try {
      invalidateATM = true;
      invalidationThread = Thread.currentThread();
      if (hasLifecycleManagedAttributes()) {
        synchronized (sessionMonitor) {
          // call a registered LifeCycleManagedObjects before invalidate the session
          Set<String> chunksToRemove = new HashSet<String>();
          for (String name : lifecycleManagedObjectNames()) {
            LifecycleManagedDataEntry dataE = getLifecycleManagedDataEntry(name);
            chunksToRemove.add(name);
            // removeChunk(name);
            try {
              if (dataE != null) {
                dataE.expire();
              }
            } catch (Exception ex) {
              if (sessionLocation.beError()) {
                sessionLocation.errorT("invalidate()", "Error during LifecycleManagedData.expire() method\n " + "Session:LifecycleManagedData", new Object[] { this, dataE.getData() });
                sessionLocation.traceThrowableT(Severity.ERROR, "Invalidation failed with exception: ", ex);
              }
            }
          }
          for (String name : chunksToRemove) {
            removeChunk(name);
          }
        }
      }
      super.invalidate();
    } finally {
      invalidationThread = null;
      SessionExecContext.applyClientContext(clientContextApplied);
    }
  }

  public void addLifecycleManagedAttribute(String name, LifecycleManagedData value) {
    checkState();
    synchronized (sessionMonitor) {
      if (lifecycleManagedObjects == null) {
        lifecycleManagedObjects = new HashMap<String, LifecycleManagedDataEntry>();
        // schedule regular check because the LifecycleManagedObject is added to the session
        scheduleRegularTimeoutCheck(10 * 1000); // 10 sec
      }
      lifecycleManagedObjects.put(name, new LifecycleManagedDataEntry(this, name, value));
      super.addChunkData(name, value);
      lifecycleManagedObjectNames().add(name);
    }
  }

  /**
   * This methods schedule reqular timeout check for internal LifecycleManaged data
   * associated with this session object.
   * @param period check period
   */
  void scheduleRegularTimeoutCheck(int period) {
    checkState();
    thisModel.runtimeModel().scheduleRegularTimeout(period);
  }

  private LifecycleManagedDataEntry getLifecycleManagedDataEntry(String name) {
    if (!hasLifecycleManagedAttributes() || !lifecycleManagedObjectNames.contains(name)) {
      return null;
    }
    LifecycleManagedDataEntry entry = null;
    if (lifecycleManagedObjects != null) {
      synchronized (sessionMonitor) {
        entry = lifecycleManagedObjects.get(name);
      }
    }

    if (entry == null) { // try to load it from the store
      entry = (LifecycleManagedDataEntry) getChunkData(name);
    }

    if (entry != null && !entry.isValid()) {
      removeChunk(name);
      try {
        entry.expire();
      } catch (Exception ex) {
        if (sessionLocation.beWarning()) {
          sessionLocation.warningT("getLifecycleManagedDataEntry()", "Error during LifecycleManagedData.getLifecycleManagedDataEntry() method\n " + "Session:LifecycleManagedData", new Object[] { this, entry.getData() });
        }
      }
      entry = null;
    }
    return entry;
  }

  public LifecycleManagedData getLifecycleManagedAttribute(String name) {
    checkState();
    LifecycleManagedDataEntry entry = getLifecycleManagedDataEntry(name);
    return entry != null ? entry.getData() : null;
  }

  public LifecycleManagedData removeLifecycleManagedAttribute(String name) {
    checkState();
    synchronized (sessionMonitor) {
      LifecycleManagedData data = getLifecycleManagedAttribute(name);
      removeChunk(name);
      return data;
    }
  }

  public void addRuntimeDependency(DomainReference dRef, String lifecycleData) {
    if (Trace.beDebug()) {
      Trace.trace("Add runtime Dependency from: " + lifecycleData + " to " + dRef);
    }    
    if (hasLifecycleManagedAttributes()) {
      checkState();
      LifecycleManagedDataEntry entry;
      synchronized (sessionMonitor) {
         entry = getLifecycleManagedDataEntry(lifecycleData);
      }
      if (entry != null) {
        entry.setDependancy(dRef);
      } else if (Trace.beDebug()) {
        Trace.trace("Add runtime dependancy: No such lifecycle managed data" + lifecycleData);
      }
    }

  }

  public void removeRuntimeDependancy(DomainReference dRef, String lifecycleData) {
    if (Trace.beDebug()) {
      Trace.trace("Remove runtime Dependency from: " + lifecycleData + " to " + dRef);
    }
    if (hasLifecycleManagedAttributes()) {
      checkState();
      LifecycleManagedDataEntry entry;      
      synchronized (sessionMonitor) {
        entry = getLifecycleManagedDataEntry(lifecycleData);
      }
      if (entry != null) {
        entry.removeDependancy(dRef);
      } else if (Trace.beDebug()) {
        Trace.trace("Remove runtime dependancy: No such lifecycle managed data" + lifecycleData);
      }
    }

  }

  public Object removeChunk(String name) {
    if (hasLifecycleManagedAttributes()) {
      checkState();
      synchronized (sessionMonitor) {
        if (lifecycleManagedObjects != null) {
          LifecycleManagedDataEntry entry = lifecycleManagedObjects.remove(name);
          if (entry != null) {
            entry.clearDependants();
          }
        }
        lifecycleManagedObjectNames().remove(name);
      }
    }
    return super.removeChunk(name);
  }

  public void beforeLogout() {
    if (hasLifecycleManagedAttributes()) {
      checkState();
      synchronized (sessionMonitor) {
        Set<String> chunksToRemove = new HashSet<String>();
        for (String name : lifecycleManagedObjectNames()) {
          LifecycleManagedDataEntry dataE = getLifecycleManagedDataEntry(name);
          chunksToRemove.add(name);
          try {
            if (dataE != null) {
              dataE.expire();
            }
          } catch (Exception ex) {
            if (sessionLocation.beError()) {
              sessionLocation.errorT("beforeLogout()", "Error during LifecycleManagedData.expire() method\n " + "Session:LifecycleManagedData", new Object[] { this, dataE.getData() });
            }
          }
        }
        for (String name : chunksToRemove) {
          removeChunk(name);
        }
      }
    }
  }

  public void updateInternalLifecycle() {
    if (hasLifecycleManagedAttributes()) {
      checkState();
      synchronized (sessionMonitor) {
        HashSet<String> clonedLifecycleManagedObjectNames = (HashSet<String>) lifecycleManagedObjectNames().clone();
        for (String name : clonedLifecycleManagedObjectNames) {
          getLifecycleManagedDataEntry(name);
        }
      }
    }
  }

  public void initInternalLifecycle() {
    if (hasLifecycleManagedAttributes()) {
      checkState();
      synchronized (sessionMonitor) {
        if (lifecycleManagedObjectNames().size() != 0) {
          for (String name : lifecycleManagedObjectNames()) {
            addLifecycleManagedAttribute(name, (LifecycleManagedData) getChunkData(name));
          }
        }
      }
    }
  }

  public boolean hasLifecycleManagedAttributes() {
    return (lifecycleManagedObjectNames != null) && (lifecycleManagedObjectNames.size() > 0);
  }
  
  private void checkState() {
    if (invalidateATM && !Thread.currentThread().equals(invalidationThread)) {
      if (invalidationThread == null) {
        if (loc.beWarning()) {
          loc.warningT("Access to session data of invalidated session is not allowed.");
        }
        throw new IllegalStateException("Access to session data of invalidated session is not allowed.");                
      } else {
        if (loc.beWarning()) {
          loc.warningT("Concurrent access to session data, while invalidation is in progress, is not allowed.");
        }
        throw new IllegalStateException("Access to the session data is not alowed, because it is being invalidated by another thread : " + invalidationThread);
      }
    }
  }  

}
