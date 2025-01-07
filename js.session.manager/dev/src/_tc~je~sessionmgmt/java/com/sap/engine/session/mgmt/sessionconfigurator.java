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

import com.sap.engine.core.session.ConfigurationEntryBuilder;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.runtime.SessionFailoverMode;
import com.sap.engine.session.spi.persistent.Storage;

import java.util.*;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class SessionConfigurator {
//  public static final String DOMAIN_SEPARATOR = "$";
  public static Map<String, ConfigurationEntry> staticConfigurations = Collections.synchronizedMap(new HashMap<String, ConfigurationEntry>());
  public static Map<String, ConfigurationEntry> configurations = Collections.synchronizedMap(new HashMap<String, ConfigurationEntry>());
  public static Storage storageSoftShutdown;
  public static EntryBuilder builder = null;

  public static void addConfigurationEntry(String key, ConfigurationEntry configuration) {
    configurations.put(key, configuration);
  }

  public static void addStaticConfigurationEntry(String key, ConfigurationEntry configuration) {
    staticConfigurations.put(key, configuration);
    addConfigurationEntry(key, configuration);
  }
  
  public static EntryBuilder entryBuilder(){
    if(builder == null){
      synchronized (SessionConfigurator.class) {
      	if(builder == null){
      		builder = new ConfigurationEntryBuilder();
      	}
      }      
    }
    return builder;
  }
  
  public static ConfigurationEntry getEntry(String key) {
    ConfigurationEntry entry = configurations.get(key);
    if (entry == null) {
      entry = staticConfigurations.get(key);
      if (entry != null) {
	addConfigurationEntry(key, entry);
      }
    }
    return entry;
  }
  
  public static Collection<ConfigurationEntry> entries() {
    return configurations.values();
  }

  public static ConfigurationEntry getParentEntry(String key) {
    ConfigurationEntry entry = null;
    do  {
      int idx = key.lastIndexOf(SessionDomain.SEPARATOR);
      if (idx == -1) {
        break;
      } else {
        key = key.substring(0, idx);
        entry = getEntry(key);
      }
    } while (entry == null);

    return entry;
  }

  /**
   * Search for ConfigurationEntry by the key and if the configuration is not found then search by its parent
   * @param key of the configuration
   * @return found ConfigurationEntry
   */
  public static ConfigurationEntry getConfigurationEntry(String key) {
    ConfigurationEntry entry = configurations.get(key);
    while (entry == null) {
      int idx = key.lastIndexOf(SessionDomain.SEPARATOR);
      if (idx == -1) {
        break;
      } else {
        key = key.substring(0, idx);
        entry = configurations.get(key);
      }
    }
    return entry;
  }

  /** The call of this method is initiated by deploy (XML settings for Aplication)
   *
   * @param key the key
   * @param mode the failover mode
   * @param storage the storrage type
   * @return  the configuration entry 
   */
  public static ConfigurationEntry addConfigurationEntry(String key, SessionFailoverMode mode, Storage storage) {
    ConfigurationEntry entry = configurations.get(key);

    if (entry == null) {
      entry = new ConfigurationEntry(key);
      configurations.put(key, entry);
    }

    if (entry.isInheritedFailoverMode() && mode != null) {
      entry.setFailoverMode(mode);
    }

    if (entry.isInheritedStorage() && storage != null) {
      entry.setPerStorage(storage);
    }

    return entry;
  }
  
  public static void removeConfigurationEntry(String key) {
    
    configurations.remove(key);
  }

  public static Storage buildStorageForType(int type) {
    return entryBuilder().buildStorageForType(type);
  }

  public static SessionFailoverMode buildFailoverModeForType(int type) {
    return entryBuilder().buildFailoverModeForType(type);
  }
}
