/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.mgmt;

import com.sap.engine.session.spi.persistent.Storage;
import com.sap.engine.session.runtime.SessionFailoverMode;


import java.util.HashMap;

/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class ConfigurationEntry {
  private  HashMap<String, Object> attributes = new HashMap<String, Object>(0);

  private Storage perStorage;
  private SessionFailoverMode failoverMode;
  private String domain;

  private Storage inheritedStorage;
  private SessionFailoverMode inheritedFailoverMode;

  public ConfigurationEntry(String domain) {
    this.domain = domain;
  }

  public ConfigurationEntry(String domain, Storage perStorage, SessionFailoverMode failoverMode) {
    this.perStorage = perStorage;
    this.failoverMode = failoverMode;
    this.domain = domain;
  }

  public ConfigurationEntry(String domain, HashMap<String, Object> attributes, Storage perStorage) {
    this.attributes = new HashMap<String, Object>(attributes);
    this.perStorage = perStorage;
    this.domain = domain;
  }

  public void setPerStorage(Storage perStorage) {
    this.perStorage = perStorage;
  }

  public void setFailoverMode(SessionFailoverMode failoverMode) {
    this.failoverMode = failoverMode;
  }

  public synchronized Object addAttribute(String key, Object attr) {
    return attributes.put(key, attr);
  }

  public synchronized Object getAttribute(String key) {
    return attributes.get(key);
  }

  public synchronized Object removeAttribute(String key) {
    return attributes.remove(key);
  }


  // can return null if the persistent storage is not configured
  public Storage getConfiguredPersistentStorage() {
    if (perStorage != null) {
      return perStorage;
    } else {
      return getInheritedPersistentStorage();
    }
  }

  public Storage getInheritedPersistentStorage() {
    if (inheritedStorage == null) {
      ConfigurationEntry parent = SessionConfigurator.getParentEntry(domain);
       if (parent != null && parent != this) {
         inheritedStorage = parent.getConfiguredPersistentStorage();
       }
    }

    return inheritedStorage;
  }


  public boolean isInheritedStorage() {
    return perStorage == null;
  }

  public SessionFailoverMode getSessionFailoverMode() {
    if (failoverMode != null) {
      return failoverMode;
    } else {
      return getInheritedSessionFailoverMode();
    }
  }

  private SessionFailoverMode getInheritedSessionFailoverMode() {
    if (inheritedFailoverMode == null) {
      ConfigurationEntry parent = SessionConfigurator.getParentEntry(domain);
      if (parent != null && parent != this) {
        inheritedFailoverMode = parent.getSessionFailoverMode();
      }
    }

    return inheritedFailoverMode;
  }

  public boolean isInheritedFailoverMode() {
    return failoverMode == null;
  }


  public String describe() {
    String storageName = null;

    Storage storage = getConfiguredPersistentStorage();
    if (storage != null) {
      storageName = storage.getClass().getName();
      int index = storageName.lastIndexOf('.');
      if (index > -1) {
        storageName = storageName.substring(index+1);
      }
    }

    String modeName = null;
    SessionFailoverMode mode = getSessionFailoverMode();
    if (mode != null) {
      modeName = mode.getClass().getName();
      int index = modeName.lastIndexOf('.');
      if (index > -1) {
        modeName = modeName.substring(index+1);
      }
    }

    return domain+" "+storageName+ " "+modeName;
  }
}
