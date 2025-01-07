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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.cache.admin.Monitor;
import com.sap.engine.cache.admin.RegionConfiguration;
import com.sap.engine.cache.admin.impl.ElementConfigurationImpl;
import com.sap.engine.cache.admin.impl.LocalMonitor;
import com.sap.engine.cache.admin.impl.RegionConfigurationImpl;
import com.sap.engine.cache.communication.Notification;
import com.sap.engine.cache.communication.impl.LocalNotification;
import com.sap.engine.cache.core.LockerHook;
import com.sap.engine.cache.job.Background;
import com.sap.engine.cache.job.impl.BackgroundExactImpl;
import com.sap.engine.cache.util.ReferenceQueueRunnable;
import com.sap.engine.cache.util.SoftHashMapEntry;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheControl;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.ElementConfiguration;
import com.sap.util.cache.ExtendedCacheControl;
import com.sap.util.cache.ExtendedCacheFacade;
import com.sap.util.cache.ExtendedCacheGroup;
import com.sap.util.cache.PrimitiveCacheControl;
import com.sap.util.cache.PrimitiveCacheFacade;
import com.sap.util.cache.PrimitiveCacheGroup;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * Date: Feb 26, 2004
 * Time: 4:48:19 PM
 * 
 * @author Petio Petev, i024139
 */

public class CacheRegionImpl implements CacheRegion {
  
  private String regionPrefix;
  private RegionConfigurationImpl configuration;
  private EvictionPolicy policy;
  protected StoragePlugin storage;
  private CacheFacade cacheFacade;

  private Monitor monitor;
  
  protected static HotspotLocker hotspotLocker = new HotspotLocker();
  protected ReferenceQueueRunnable dungHill = null;
    
  EvictionWorker evictionWorker = null;
//  TODO CleanerTask seems to be useless and does not work properly. Instead, TimeInvalidationEntity is designed for TTL/AET functionality.  
//  CleanerTask cleaner = null;
  TimeInvalidationEntity timeInvalidationEntity = null;

  private static Notification notification = null;
  protected static Background background = null;
  private static boolean ready = false;

  protected String name = null;
  private ElementConfiguration eConfiguration;
  protected HashMap groups;
  private CacheControl cacheControl;

  private PluginContext pluginContext = null;
  
  protected int sizeCalcDepth = Integer.parseInt(RegionConfigurationInfo.DEFAULT_SIZE_CALCULATION_DEPTH);

  private static void initStatic() {
    ready = true; //todo where is synchronization, ready is redundant it could be put in static block
    notification = new LocalNotification();
    background = new BackgroundExactImpl();
  }

  public static Notification getNotification() {
    if (ready == false) {
      initStatic();
    }
    return notification;
  }

  public CacheRegionImpl(String name) {
    if (ready == false) {
      initStatic();
    }
    RegionConfigurationImpl configuration = new RegionConfigurationImpl();
    ElementConfiguration eConfiguration = new ElementConfigurationImpl(background);

    pluginContext = new PluginContextImpl(name, name.hashCode());

//    if ("default".equals(name)) {
//      policy = PluggableFramework.getEvictionPolicy("SimpleLRU"); // new SimpleLRUEvictionPolicy();
//      storage = PluggableFramework.getStoragePlugin("HashMapStorage"); // new HashMapStorage("default");
//
//      try {
//        policy = (EvictionPolicy) policy.getInstance();
//        policy.setPluginContext(pluginContext);
//        storage = (StoragePlugin) storage.getInstance();
//        storage.setPluginContext(pluginContext);
//        policy.start();
//        storage.start();
//      } catch (PluginException e) {
//        LogUtil.logT(e);
//        System.exit(101);
//      }
//
//      configuration.setCountQouta(200, RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
//      configuration.setCountQouta(300, RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
//      configuration.setCountQouta(400, RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
//      configuration.setDirectObjectInvalidationMode(false);
//      configuration.setEvictionPolicy(policy);
//      configuration.setId(pluginContext.getRegionID());
//      configuration.setInvalidationScope(RegionConfiguration.SCOPE_CLUSTER);
//      configuration.setLoggingMode(false);
//      configuration.setName("default");
//      configuration.setRegionScope(RegionConfiguration.SCOPE_LOCAL);
//      configuration.setSizeQuota(1024 * 1024 * 1, RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
//      configuration.setSizeQuota(1024 * 1024 * 2, RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
//      configuration.setSizeQuota(1024 * 1024 * 4, RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
//      configuration.setStoragePlugin(storage);
//      configuration.setDirectObjectInvalidationMode(true);
//      configuration.setPutIsModificationMode(false);
//      configuration.setSenderIsReceiverMode(true);
//
//      eConfiguration.setAbsEvictionTime(-1);
//      eConfiguration.setTimeToLive(-1);
//      groups = new HashMap();
//    } else {
      // todo - call init to get group info from storage plugin
      groups = new HashMap();
//    }
    this.name = name;
    this.configuration = configuration;
    this.eConfiguration = eConfiguration;
    regionPrefix = name + "@";
    
    // The reference queue for the cache groups must be started in a thread 
    // through the thread manager. This is a dull implementation
    dungHill = InternalRegionFactory.referenceQueue;//new ReferenceQueueRunnable();
  }

  public RegionConfiguration getRegionConfiguration() {
    return configuration;
  }

  public void init() { //it is called only from the other init method
    this.monitor = new LocalMonitor(this.name);
    timeInvalidationEntity = new TimeInvalidationEntity(this);
    evictionWorker = new EvictionWorker(this, 1000, true);
//  TODO CleanerTask seems to be useless and does not work properly. Instead, TimeInvalidationEntity is designed for TTL/AET functionality.
//    cleaner = new CleanerTask(this); // create a cleaner for this region
    background.registerTask(evictionWorker);
//    background.registerTask(cleaner); // register the cleaner as task

//    if (!storage.getName().startsWith("SoftStorage") || !storage.getDescription().startsWith("SoftStorage")) {
//      dungHill.stop();
//      dungHill = null;
//    }
    cacheControl = new CacheControlImpl(notification, this);
    // todo - read configuration data, fill groups, etc.
  }

  public void resize(int count1, int count2, int count3, int size1, int size2, int size3) {
    configuration.setSizeQuota(size1, RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
    configuration.setSizeQuota(size2, RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
    configuration.setSizeQuota(size3, RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
    configuration.setCountQouta(count1, RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
    configuration.setCountQouta(count2, RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
    configuration.setCountQouta(count3, RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
    evictionWorker.resize();
  }
  
  public void init(RegionConfigurationInfo configuration, String storage, String policy) throws PluginException {
    for (byte n = 0; n < 3; n++) {
      this.configuration.setCountQouta(configuration.getCountQuota(n), n);
      this.configuration.setSizeQuota(configuration.getSizeQuota(n), n);
    }
    this.configuration.setDirectObjectInvalidationMode(configuration.getDirectObjectInvalidationMode());
    this.configuration.setInvalidationScope(configuration.getInvalidationScope());
    this.configuration.setLoggingMode(configuration.getLoggingMode());
    this.configuration.setRegionScope(configuration.getRegionScope());
    this.configuration.setSynchronous(configuration.isSynchronous());
    this.configuration.setPutIsModificationMode(configuration.getPutIsModificationMode());
    this.configuration.setSenderIsReceiverMode(configuration.getSenderIsReceiverMode());

    this.configuration.setId(pluginContext.getRegionID());
    this.configuration.setName(pluginContext.getRegionName()); //why not directly name???
    
    if (configuration instanceof RegionConfigurationInfoExtension) {
      Properties properties = ((RegionConfigurationInfoExtension) configuration).getProperties();
      this.configuration.setProperties(properties);
      // initialize property-only configurations
      if (properties != null) {
        // SENDER_IS_RECEIVER_MODE
        if (properties.get(RegionConfigurationInfo.PROP_SENDER_IS_RECEIVER_MODE) != null) {
          //todo ako propertito ne e string tuka shte se hvurli nullpointer... zashto se vzima edno i sushto neshto 3 puti...
          String senderIsReceiver = properties.getProperty(RegionConfigurationInfo.PROP_SENDER_IS_RECEIVER_MODE);
          if (senderIsReceiver.length() > 0) {
          	boolean result = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_SENDER_IS_RECEIVER_MODE, "true"));
          	((RegionConfiguration) this.configuration).setSenderIsReceiverMode(result);
          }
        }

        // PUT_IS_MODIFICATION_MODE
        if (properties.get(RegionConfigurationInfo.PROP_PUT_IS_MODIFICATION_MODE) != null) {
          //todo kato gornoto property
          String putIsModification = properties.getProperty(RegionConfigurationInfo.PROP_PUT_IS_MODIFICATION_MODE);
          if (putIsModification.length() > 0) {
          	boolean result = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_PUT_IS_MODIFICATION_MODE, "false"));
            ((RegionConfiguration) this.configuration).setPutIsModificationMode(result);
          }
        }
        
        // SIZE_CALCULATION_DEPTH
        String strSizeCalcDepth = properties.getProperty(RegionConfigurationInfo.PROP_SIZE_CALCULATION_DEPTH);
        int depth;
        try {
          if ((strSizeCalcDepth == null) ||
              ((depth = Integer.parseInt(strSizeCalcDepth)) < 2)) {
            properties.setProperty(RegionConfigurationInfo.PROP_SIZE_CALCULATION_DEPTH,
                                   RegionConfigurationInfo.DEFAULT_SIZE_CALCULATION_DEPTH);
//            TODO Add trace/log
          } else {
            sizeCalcDepth = depth;
          }
        } catch (NumberFormatException e) {
          properties.setProperty(RegionConfigurationInfo.PROP_SIZE_CALCULATION_DEPTH,
                                  RegionConfigurationInfo.DEFAULT_SIZE_CALCULATION_DEPTH);
//          TODO add trace/log
        }
        
      }
    }

    EvictionPolicy basePolicy = PluggableFramework.getEvictionPolicy(policy);
    if (basePolicy == null) {
      throw new PluginException("evictionPolicy = ["+ policy +"] does not exist");
    }
    StoragePlugin baseStorage = PluggableFramework.getStoragePlugin(storage);
    if (baseStorage == null) {
      throw new PluginException("storagePlugin = ["+ storage +"]  does not exist");
    }
    basePolicy = (EvictionPolicy) basePolicy.getInstance();
    basePolicy.setPluginContext(pluginContext);
    baseStorage = (StoragePlugin) baseStorage.getInstance();
    baseStorage.setPluginContext(pluginContext);
    basePolicy.start();
    baseStorage.start();

    this.storage = baseStorage;
    this.policy = basePolicy;
    
    this.configuration.setEvictionPolicy(this.policy);
    this.configuration.setStoragePlugin(this.storage);
        
    init();
  }

  /**
   * Returns the configuration of the cache region that the cache user is using.
   *
   * @return Cache user cache region configuration.
   */
  public RegionConfigurationInfo getRegionConfigurationInfo() {
    return configuration;
  }

  /**
   * Returns <code>CacheControl</code> bound to this region
   * @return the cache control instance of the region
   */
  public CacheControl getCacheControl() {
    return cacheControl;
  }

  /**
   * Returns a cache facade bound to this region
   *
   * @return The cache facade that cache user will use later to make basic cache operations
   */
  public CacheFacade getCacheFacade() {
    if (cacheFacade == null) {
      cacheFacade = new CacheFacadeImpl(notification, this);
    }
    return cacheFacade;
  }

  /**
   * Returns a named cache group bound to this region
   *
   * @param group The cache group that the user will use, if the parameter is null returns the CacheFacade
   * @return The cache group identified by the name. Cache user will use later to make basic cache operations over groups
   * @throws NullPointerException if <code>group</code> is null
   */
  public CacheGroup getCacheGroup(String group) {
    CacheGroup result = null;
    if (group != null) {
      Object object = groups.get(group);
      // if the groupname exists, but the group is unprepared, create it
      if (!(object instanceof String)) {
        if (object instanceof SoftHashMapEntry) {
          result = (CacheGroup) ((SoftHashMapEntry) groups.get(group)).get();
        } else {
            result = (CacheGroup) groups.get(group);
        }
      }
      
      if (result == null) {
        result = new CacheGroupImpl(group, notification, this);
        groups.put(group, result);
      }
    } else {
      result = getCacheFacade();
    }
    return result;
  }

  /**
   * Returns the names of all existing groups in the region
   *
   * @return The names of all existing groups in the region
   */
  public Set getCacheGroupNames() {
    if (!groups.containsKey(null)) {
      groups.put(null, getCacheFacade());
    }
    Set groupSet = storage.getGroupSet();
    Iterator iter = groupSet.iterator();
    while (iter.hasNext()) {
      String element = (String) iter.next();
      if (!groups.containsKey(element)) {
        groups.put(element, getCacheGroup(element));
      }
    }
    return groups.keySet();
  }

  /**
   * Stops the region
   */
  public void close() {
    evictionWorker.stop();
//    if (dungHill != null) {
//    dungHill.stop();
//    }
    policy.stop();
    storage.stop();
    InternalRegionFactory.readyRegions.remove(this.name);
  }

  /**
   * This method is called by eviction policies when there is need to evict something
   *
   * @param key The cached object key of the cached object to be evicted
   */

  public void evict(String key) {
    try {
      if (cacheFacade instanceof CacheFacadeImpl) {
        ((CacheFacadeImpl) cacheFacade).evict(key);
      }
    } catch (CacheException e) {
      LogUtil.logT(e);
    }
  }

  public void invalidate(String key) {
    try {
      if (cacheFacade instanceof CacheFacadeImpl) {
        ((CacheFacadeImpl) cacheFacade).invalidate(key);
      }
    } catch (CacheException e) {
      LogUtil.logT(e);
    }
  }

  public Monitor getMonitor() {
    return monitor;
  }

  public ElementConfiguration getElementConfiguration() {
    return eConfiguration;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheRegion#removeCacheGroup()
   */
  public void removeCacheGroup() {
    cacheFacade.remove("**");
    groups.clear();
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheRegion#removeCacheGroup(boolean, boolean)
   */
  public void removeCacheGroup(boolean synchronous, boolean suppressInvalidation) {
    cacheFacade.remove("**", synchronous, suppressInvalidation);
    groups.clear();
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheRegion#removeCacheGroup(java.lang.String)
   */
  public void removeCacheGroup(String groupName) {
    
    Object cacheGroupObj = groups.get(groupName);
    
    if (cacheGroupObj instanceof CacheGroup) {
      CacheGroup cacheGroup = (CacheGroup) cacheGroupObj;
      if (cacheGroup != null) {
        cacheGroup.remove((Map)null);
        groups.remove(groupName);
      }
    }
    
    if (cacheGroupObj instanceof SoftHashMapEntry) {
      CacheGroup cacheGroup = (CacheGroup) ((SoftHashMapEntry) cacheGroupObj).get();
      if (cacheGroup != null) {
        cacheGroup.remove((Map)null);
        groups.remove(groupName);
      }
    }
    
    if (cacheGroupObj instanceof String) {
      groups.remove(groupName);      
    }
    
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheRegion#removeCacheGroup(java.lang.String, boolean, boolean)
   */
  public void removeCacheGroup(String groupName, boolean synchronous, boolean suppressInvalidation) {
    Object cacheGroupObj = groups.get(groupName);
    
    if (cacheGroupObj instanceof CacheGroup) {
      CacheGroup cacheGroup = (CacheGroup) cacheGroupObj; 
      if (cacheGroup != null) {
        cacheGroup.remove((Map)null, synchronous, suppressInvalidation);
        groups.remove(groupName);
      }
    }
    
    if (cacheGroupObj instanceof SoftHashMapEntry) {
      CacheGroup cacheGroup = (CacheGroup) ((SoftHashMapEntry) cacheGroupObj).get();
      if (cacheGroup != null) {
        cacheGroup.remove((Map)null, synchronous, suppressInvalidation);
        groups.remove(groupName);
      }
    }
    
    if (cacheGroupObj instanceof String) {
      groups.remove(groupName);
    }

  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheRegion#removeCacheGroup(boolean, byte)
   */
  public void removeCacheGroup(boolean synchronous, byte invalidationScope) {
    cacheFacade.remove("**", synchronous, invalidationScope);
    groups.clear();
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheRegion#removeCacheGroup(java.lang.String, boolean, byte)
   */
  public void removeCacheGroup(String groupName, boolean synchronous, byte invalidationScope) {

    Object cacheGroupObj = groups.get(groupName);
    
    if (cacheGroupObj instanceof CacheGroup) {
      CacheGroup cacheGroup = (CacheGroup) groups.get(groupName);
      if (cacheGroup != null) {
        cacheGroup.remove((Map)null, synchronous, invalidationScope);
        groups.remove(groupName);
      }
    }
    
    if (cacheGroupObj instanceof SoftHashMapEntry) {
      CacheGroup cacheGroup = (CacheGroup) ((SoftHashMapEntry) cacheGroupObj).get();
      if (cacheGroup != null) {
        cacheGroup.remove((Map) null, synchronous, invalidationScope);
        groups.remove(groupName);  
      }
    }
    
    if (cacheGroupObj instanceof String) {
      groups.remove(groupName);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheRegion#execute(java.lang.String, java.lang.Runnable)
   */
  public synchronized void execute(String name, Runnable runnable) throws CacheException {
    hotspotLocker.execute(regionPrefix + name, runnable);
  }

  public String toString() {
    return super.toString() + " Storage: " + storage + " Eviction: " + policy;    
  }

  // Extensions
  
  public ExtendedCacheFacade getExtendedCacheFacade() {
    return new ExtendedCacheFacadeImpl(getCacheFacade());
  }

  public ExtendedCacheGroup getExtendedCacheGroup(String group) {
    return new ExtendedCacheGroupImpl(getCacheGroup(group));
  }

  public ExtendedCacheControl getExtendedCacheControl() {
    return new ExtendedCacheControlImpl(getCacheControl());
  }

  public PrimitiveCacheControl getPrimitiveCacheControl() {
    return new PrimitiveCacheControlImpl(getCacheControl());
  }

  public PrimitiveCacheFacade getPrimitiveCacheFacade() {
    return new PrimitiveCacheFacadeImpl(getCacheFacade());
  }

  public PrimitiveCacheGroup getPrimitiveCacheGroup(String arg0) {
    return new PrimitiveCacheGroupImpl(getCacheGroup(arg0));
  }
  
}
