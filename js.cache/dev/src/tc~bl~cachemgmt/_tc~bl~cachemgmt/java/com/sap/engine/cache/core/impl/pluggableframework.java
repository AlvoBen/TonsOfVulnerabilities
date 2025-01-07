/*
 * Copyright (c) 2004 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.cache.core.impl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.cache.spi.policy.impl.SimpleLRUEvictionPolicy;
import com.sap.engine.cache.spi.storage.impl.HashMapStorage;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * Date: Feb 26, 2004
 * Time: 5:05:08 PM
 * 
 * @author Petio Petev, i024139
 */

public class PluggableFramework {

  private static Hashtable storageTable = new Hashtable();

  private static Hashtable evictionTable = new Hashtable();

  static {
    try {
      EvictionPolicy policy = new SimpleLRUEvictionPolicy();
      StoragePlugin storage = new HashMapStorage();
      policy.init(policy.getName(), new Properties());
      storage.init(storage.getName(), new Properties());
      storageTable.put(storage.getName(), storage);
      evictionTable.put(policy.getName(), policy);
    } catch (PluginException e) {
      LogUtil.logT(e);
      System.exit(101);
    }
  }
  
  public static Set listStorageNames() {
    return storageTable.keySet();
  }

  public static Set listEvictionNames() {
    return evictionTable.keySet();
  }

  public static StoragePlugin getStoragePlugin(String name) {
    return (StoragePlugin) storageTable.get(name);
  }

  public static EvictionPolicy getEvictionPolicy(String name) {
    return (EvictionPolicy) evictionTable.get(name);
  }

  public static void putStoragePlugin(String name, StoragePlugin storage) {
    if (name != null && storage != null) {
      storageTable.put(name, storage);
    }
  }

  public static void putEvictionPolicy(String name, EvictionPolicy policy) {
    if (name != null && policy != null) {
      evictionTable.put(name, policy);
    }
  }

  public static void putPluggable(String name, Pluggable pluggable) {
    if (pluggable instanceof StoragePlugin) {
      putStoragePlugin(name, (StoragePlugin) pluggable);
    } else if (pluggable instanceof EvictionPolicy) {
      putEvictionPolicy(name, (EvictionPolicy) pluggable);
    }
  }
  
  protected static void listStorages() {
    DumpWriter.dump("Storages: ");
    Enumeration en = storageTable.keys();
    while (en.hasMoreElements()) {
      String name = (String) en.nextElement();
      DumpWriter.dump("  " + name);
    }
  }

  protected static void listPolicies() {
    DumpWriter.dump("Policies: ");
    Enumeration en = evictionTable.keys();
    while (en.hasMoreElements()) {
      String name = (String) en.nextElement();
      DumpWriter.dump("  " + name);
    }
  }

}
