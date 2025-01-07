/*
* Copyright (c) 2005 by SAP AG, Walldorf.,
* http://www.sap.com
* All rights reserved.
*
* This software is the confidential and proprietary information
* of SAP AG, Walldorf. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms
* of the license agreement you entered into with SAP.
*
* Created on 2005.3.12
*/
package com.sap.engine.core.cache.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.xml.sax.SAXException;

import com.sap.engine.cache.admin.impl.RegionConfigurationImpl;
import com.sap.engine.cache.core.impl.CombinatorStorage;
import com.sap.engine.cache.core.impl.CombinatorStorageWriteTrue;
import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.core.impl.PluggableFramework;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.configuration.impl.addons.processors.CacheConfigurationProcessor;
import com.sap.engine.core.configuration.impl.addons.processors.CacheXML;
import com.sap.engine.frame.core.cache.CacheConfigurationDeploy;
import com.sap.engine.frame.core.cache.CacheContextException;
import com.sap.engine.frame.core.cache.CacheManagementNames.PARAMETERS;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author petio-p
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CCDeployImpl implements CacheConfigurationDeploy {

  public static final String CONFIG_INSTANCE_CACHE_ROOT = "cluster_config/system/instances/current_instance/cfg/kernel/Cache/cache-configuration.xml";
  
  private InternalRegionFactory regionFactory = null;
  private static final int PATH = Severity.PATH;
  private static CacheConfigurationProcessor configurationProcessor = null;
  private static ConfigurationHandlerFactory handlerFactory = null;
  private Map monitors = null;
  private Map weights = null;
  private Map<String, SizingData> defaultSizingData = null;
  private static final Location LOCATION = Location.getLocation(CCDeployImpl.class.getName(), Names.KERNEL_DC_NAME, Names.CACHE_MANAGER_CSN_COMPONENT);

  private double commonWeight;
  private int totalMemory;

  private void _trace(String what) {
    SimpleLogger.trace(PATH, LOCATION, what);
  }
  
  protected CCDeployImpl(Map monitors) {
    _trace("CCDeployImpl instance creation.");
    CacheContextException.setEnvironment();
    regionFactory = (InternalRegionFactory) InternalRegionFactory.getInstance();
    configurationProcessor = new CacheConfigurationProcessor();
    handlerFactory = (ConfigurationHandlerFactory) Framework.getManager(Names.CONFIGURATION_MANAGER);
    this.monitors = monitors;
    weights = new Hashtable();
    defaultSizingData = new Hashtable<String, SizingData>();
    commonWeight = 0;
    totalMemory = 0;
    _trace("CCDeployImpl instance created.");
  }
  
  private String _changeScope(String source) {
    String result = "1";
    if ("NONE".equals(source)) {
      result = new Integer(RegionConfigurationInfo.SCOPE_NONE).toString();
    } else if ("LOCAL".equals(source)) {
      result = new Integer(RegionConfigurationInfo.SCOPE_LOCAL).toString();
    } else if ("INSTANCE".equals(source)) {
      result = new Integer(RegionConfigurationInfo.SCOPE_INSTANCE).toString();
    } else if ("CLUSTER".equals(source)) {
      result = new Integer(RegionConfigurationInfo.SCOPE_CLUSTER).toString();
    }
    _trace("_changeScope: " + source + " -> " + result);
    return result;
  }
  
  private String _transformType(String type) {
    if (type == null) return null;
    String result = type;
    if (type.equals(CacheXML.TYPE_EVICTION_LOCAL_LRU)) {
      result = "SimpleLRU";
    } else if (type.equals(CacheXML.TYPE_EVICTION_SHARED_LRU)) {
      result = "LRUEvictionPolicy";
    } else if (type.equals(CacheXML.TYPE_STORAGE_HASH_MAP)) {
      result = "HashMapStorage";
    } else if (type.equals(CacheXML.TYPE_STORAGE_DB)) {
      result = "DBStorage";
    } else if (type.equals(CacheXML.TYPE_STORAGE_SOFT_MAP)) {
      result = "SoftStorage";
    } else if (type.equals(CacheXML.TYPE_STORAGE_SHARED_COPY)) {
      result = "CommonSCCopyOnlyStoragePlugin";
    } else if (type.equals(CacheXML.TYPE_STORAGE_SHARED_MAP)) {
      result = "CommonSCMappableStoragePlugin";
    } else if (type.equals(CacheXML.TYPE_STORAGE_FILE)) {
      result = "FileStorage";
    }
    _trace("_transformType: " + type + " -> " + result);
    return result;
  }
  
  protected void createRegion(String appName, String regionName, Map mRegion) throws CacheContextException {
    _trace("createRegion: name == " + regionName);
    Map mRegionCfg = (Map) mRegion.get(CacheXML.NAME_REGION);
    Map mStorageCfg = (Map) mRegion.get(CacheXML.NAME_STORAGE);
    Map mEvictionCfg = (Map) mRegion.get(CacheXML.NAME_EVICTION);
    
    if (mRegionCfg != null && mStorageCfg != null && mEvictionCfg != null) {
      Properties regionProperties = new Properties();

      // Scopes have to be changed from text to digits
      mRegionCfg.put(CacheXML.PROP_REGION_SCOPE, _changeScope((String) mRegionCfg.get(CacheXML.PROP_REGION_SCOPE)));
      mRegionCfg.put(CacheXML.PROP_INVALIDATION_SCOPE, _changeScope((String) mRegionCfg.get(CacheXML.PROP_INVALIDATION_SCOPE)));

      // Suck in and remove storage and eviction plug-ins types
      String storageName = (String) mRegionCfg.remove(CacheXML.PROP_STORAGE_NAME);
      String evictionName = (String) mRegionCfg.remove(CacheXML.PROP_EVICTION_NAME);

      // Add all properties as they are
      regionProperties.putAll(mRegionCfg);

      // Application name is the owner
      // regionProperties.setProperty(CacheXML.PROP_OWNER, appName);
      if (!regionProperties.containsKey("_OWNER")) { // The XML file has higher priority
        if (appName != null) {
          regionProperties.setProperty("_OWNER", appName);
        } else {
          regionProperties.setProperty("_OWNER", "UNKNOWN");
        }
      }

      // Map plug-in TYPES to real names;
      if (CacheXML.TYPE_STORAGE_COMB_WRITE_BACK.equals(storageName) ||
          CacheXML.TYPE_STORAGE_COMB_WRITE_THROUGH.equals(storageName)) {
        // we got combinator storage here
        String frontEnd = _transformType((String) mStorageCfg.remove("FrontendStoragePlugin"));
        frontEnd = frontEnd == null ? "HashMapStorage" : frontEnd;
        String backEnd = _transformType((String) mStorageCfg.remove("BackendStoragePlugin"));
        backEnd = backEnd == null ? "HashMapStorage" : backEnd;
        Properties properties = new Properties();
        properties.putAll(mStorageCfg);
        properties.put("FrontendStoragePlugin", frontEnd);
        properties.put("BackendStoragePlugin", backEnd);
        if (frontEnd != null && backEnd != null) {
          storageName = regionName + "_" + storageName;
          StoragePlugin combinatorStorage; 
          if (storageName.endsWith(CacheXML.TYPE_STORAGE_COMB_WRITE_BACK)) {
            // write-back
            combinatorStorage = new CombinatorStorage();
            try {
              combinatorStorage.init(storageName, properties);
            } catch (PluginException e) {
              SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
                  "createRegion()", e);
              throw new CacheContextException(LOCATION, CacheContextException.COMBINATOR_STORAGE_FAILED, new String[] {storageName, properties.toString()}, e);   
            }
          } else {
            // write-through
            combinatorStorage = new CombinatorStorageWriteTrue();
            try {
              combinatorStorage.init(storageName, properties);
            } catch (PluginException e) {
              SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
                  "createRegion()", e);
              throw new CacheContextException(LOCATION, CacheContextException.COMBINATOR_STORAGE_FAILED, new String[] {storageName, properties.toString()}, e);   
            }
          }
          PluggableFramework.putPluggable(storageName, combinatorStorage);
          _trace("createRegion: new storage plugin created: name == " + regionName + "; properties == " + properties.toString());
        }
      } else {
        // we got plain storage
        storageName = _transformType(storageName);
        storageName = storageName == null ? "HashMapStorage" : storageName;
      }

      evictionName = _transformType(evictionName);
      evictionName = evictionName == null ? "SimpleLRU" : evictionName;

      // storage plugin and eviction plugin are ready to be used by the region
      // now we define the region
    
      try {
        regionFactory.defineRegion(regionName, storageName, evictionName, regionProperties);
        _trace("createRegion: region defined: name == " + regionName 
            + "; storage == " + storageName 
            + "; eviction == " + evictionName 
            + "; properties == " + regionProperties.toString());
      } catch (CacheException e) {
        throw new CacheContextException(LOCATION, CacheContextException.CACHE_EXCEPTION_ON_CREATE, new String[] {regionName, storageName, evictionName}, e);   
      } catch (NullPointerException e) {
        throw new CacheContextException(LOCATION, CacheContextException.NULLPOINTER_EXCEPTION_ON_CREATE, new String[] {regionName, storageName, evictionName}, e);   
      }
      regionFactory.getCacheRegion(regionName);

      // get weight, calc common weight, resize all
      SizingData sD = new SizingData();
      sD.weight = Double.parseDouble((String) mRegionCfg.get(CacheXML.PROP_WEIGHT));
      sD.c1 = Integer.parseInt((String)mRegionCfg.get(CacheXML.PROP_COUNT_START_OF_EVICTION_THRESHOLD));
      sD.c2 = Integer.parseInt((String)mRegionCfg.get(CacheXML.PROP_COUNT_UPPER_LIMIT_THRESHOLD));
      sD.c3 = Integer.parseInt((String)mRegionCfg.get(CacheXML.PROP_COUNT_CRITICAL_LIMIT_THRESHOLD));
      sD.s1 = Integer.parseInt((String)mRegionCfg.get(CacheXML.PROP_SIZE_START_OF_EVICTION_THRESHOLD));
      sD.s2 = Integer.parseInt((String)mRegionCfg.get(CacheXML.PROP_SIZE_UPPER_LIMIT_THRESHOLD));
      sD.s3 = Integer.parseInt((String)mRegionCfg.get(CacheXML.PROP_SIZE_CRITICAL_LIMIT_THRESHOLD));

      commonWeight += sD.weight;
      weights.put(regionName, sD);
    }

    _trace("createRegion: region monitor registered in shared memory: name == " + regionName);
  }
  
  public void createRegions(String appName, Map mRegions) throws CacheContextException {
    _trace("createRegions: begin");
    if (mRegions != null) {
      Iterator regions = mRegions.entrySet().iterator();
      while (regions.hasNext()) {
        Map.Entry entry = (Map.Entry) regions.next();
        String regionName = (String) entry.getKey();
        Map mRegion = (Map) entry.getValue();
        createRegion(appName, regionName, mRegion);
      }
    }
    resizeRegions();
    _trace("createRegions: end");
  }
  
  protected void destroyRegion(String regionName, Map mRegion) {
    _trace("destroyRegion: begin: name == "+ regionName);
    
    Map mRegionCfg = (Map) mRegion.get(CacheXML.NAME_REGION);
    
    double weight = ((SizingData)weights.get(regionName)).weight;
    commonWeight -= weight;
    weights.remove(regionName);
    
    regionFactory.removeRegion(regionName);
    ShmMonitor monitor = (ShmMonitor) monitors.get(regionName);
    if (monitor != null) {
      monitor.close();
      monitors.remove(regionName);
    }
    _trace("destroyRegion: end: name == "+ regionName);
  }
  
  protected void destroyRegions(Map mRegions) throws CacheContextException {
    _trace("destroyRegions: begin");
    if (mRegions != null) {
      Iterator regions = mRegions.entrySet().iterator();
      try {
        while (regions.hasNext()) {
          Map.Entry entry = (Map.Entry) regions.next();
          String regionName = (String) entry.getKey();
          Map mRegion = (Map) entry.getValue();
          destroyRegion(regionName, mRegion);
        }
      } catch (ThreadDeath td) {
        throw td;
      } catch (Throwable t) {
        throw new CacheContextException(LOCATION, CacheContextException.EXCEPTION_DURING_DESTROY, t);
      }
    }
    resizeRegions();
    _trace("destroyRegions: end");
  }
  
  public void setGlobalConfiguration(Map mGlobal) {
    try {
      totalMemory = Integer.parseInt((String)mGlobal.get(CacheXML.GLOBAL_TOTAL_SIZE));
    } catch (NumberFormatException nfe) {
      CacheManagerImpl.traceT(nfe);
      totalMemory = 64 * 1024 * 1024; // default memory - 64 MB
    }
  }

  public String[] deploy(String appName, InputStream xmlStream, Configuration configuration) throws ConfigurationException, CacheContextException {
    _trace("deploy: begin");
    try {
      // parse and write to DB
      _trace("deploy: writing to DB: application name == " + appName);
      configurationProcessor._write(
          xmlStream, 
          configuration, 
          handlerFactory.getConfigurationHandler().openConfiguration(
              CONFIG_INSTANCE_CACHE_ROOT + "/" + 
              CacheXML.NAME_CENTRAL + "/" + CacheXML.NAME_REGIONS, 
              ConfigurationHandler.READ_ACCESS), appName, "application");
    } catch (SAXException e) {
      throw new CacheContextException(LOCATION, CacheContextException.CANNOT_PARSE_XML, new String[] {appName}, e);
    } catch (IOException e) {
      throw new CacheContextException(LOCATION, CacheContextException.STREAM_CORRUPTED, new String[] {appName}, e);
    }
    _trace("deploy: reading from DB: application name == " + appName);
    Map map = configurationProcessor._read(configuration);
    if (map != null) {
      Set set = ((Map) map.get(CacheXML.NAME_REGIONS)).keySet();
      int count = set.size();
      String[] result = new String[count];
      set.toArray(result);
      for (int i = 0; i < result.length; i++) {
        result[i] = "(" + appName + "):cache:" + result[i];
      }
      _trace("deploy: deployed following: " + set.toString());
      _trace("deploy: end");
      return result;
    } else {
      throw new CacheContextException(LOCATION, CacheContextException.NO_CONFIGURATION_FOUND, new String[] {appName});
    }
  }

  public void create(String appName, Configuration configuration) throws ConfigurationException, CacheContextException {
    _trace("create: begin");
    _trace("create: create regions: application name == " + appName);
    Map map = configurationProcessor._read(configuration);
    Map mGlobal = (Map) map.get(CacheXML.NAME_GLOBAL);
    Map mRegions = (Map) map.get(CacheXML.NAME_REGIONS);
    if (mGlobal != null) {
      throw new CacheContextException(LOCATION, CacheContextException.GLOBALS_MODIFIED, new String[] {appName});
    }
    if (mRegions != null) {
      try {
        createRegions(appName, mRegions);
      } catch (CacheContextException e) {
        CacheManagerImpl.traceT(e);
        throw new CacheContextException(LOCATION, CacheContextException.COULD_NOT_CREATE_REGIONS, new String[] {appName}, e);
      }
    } else {
      throw new CacheContextException(LOCATION, CacheContextException.NO_CONFIGURATION_FOUND, new String[] {appName});
    }
    _trace("create: end");
  }
  
  public void destroy(String appName, Configuration configuration) throws ConfigurationException, CacheContextException {
    _trace("destroy: begin");
    _trace("destroy: destroy regions: application name == " + appName);
    Map map = configurationProcessor._read(configuration);
    Map mRegions = (Map) map.get(CacheXML.NAME_REGIONS);
    if (mRegions != null) {
      try {
        destroyRegions(mRegions);
      } catch (CacheContextException e) {
        CacheManagerImpl.traceT(e);
        throw new CacheContextException(LOCATION, CacheContextException.COULD_NOT_DESTROY_REGIONS, new String[] {appName}, e);
      }
    } else {
      throw new CacheContextException(LOCATION, CacheContextException.NO_CONFIGURATION_FOUND, new String[] {appName});
    }
    _trace("destroy: end");
  }
  
  public void remove(String appName, Configuration configuration) throws ConfigurationException {
    _trace("remove: begin");
    _trace("remove: remove region configuration: application name == " + appName);
    configurationProcessor.remove(configuration);
    _trace("remove: end");
  }
 
  /**
   * Initializes a plugin, the methid is used by the 
   */
  public void registerPlugin(String pluginName, StoragePlugin pluginImpl) {
    
    PluggableFramework.putPluggable(pluginName, pluginImpl);

    // hardcoded
    if ("DBStorage".equals(pluginName)) {

      // Hardcode for PRT Runtime Cache
      Pluggable prtStorage = new CombinatorStorageWriteTrue();
      try {
        Properties props = new Properties();
        props.setProperty("CombinatorStorage.FRONTEND_STORAGE", "HashMapStorage");
        props.setProperty("CombinatorStorage.BACKEND_STORAGE", "DBStorage");
        prtStorage.init("PRTStorage", props);
        PluggableFramework.putPluggable("PRTStorage", prtStorage);
      } catch (PluginException e) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "init()", e);
      }
      
      // Hardcode for PRT Application Cache
      Pluggable prtAppStorage = new CombinatorStorageWriteTrue();
      try {
        Properties props = new Properties();
        props.setProperty("CombinatorStorage.FRONTEND_STORAGE", "HashMapStorage");
        props.setProperty("CombinatorStorage.BACKEND_STORAGE", "DBStorage");
        prtStorage.init("PRTAppStorage", props);
        PluggableFramework.putPluggable("PRTAppStorage", prtAppStorage);
      } catch (PluginException e) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "init()", e);
      }
    }

  }
  
//TODO This functionality MUST be revised.  
  public synchronized void resizeRegions() {
    Iterator iterator = weights.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      String regionName = (String) entry.getKey();
      SizingData sD = (SizingData) entry.getValue();
      double ratio = sD.weight / commonWeight;
      double q1 = ((double) sD.s1) / ((double) sD.s3); // quotients for factorization
      double q2 = ((double) sD.s2) / ((double) sD.s3);
      double r3 = totalMemory * ratio; // relative sizes;
      if (r3 < sD.s3) { // we have to resize this region
        double r1 = q1 * r3;
        double r2 = q2 * r3;
        regionFactory.resizeRegion(regionName, sD.c1, sD.c2, sD.c3, (int) r1, (int) r2, (int) r3);
        _trace("Size thresholds for cache region [" + regionName + 
            "] were automatically adjusted and region was resized");
      } else { // set sizes anyway in case of upsize
        regionFactory.resizeRegion(regionName, sD.c1, sD.c2, sD.c3, sD.s1, sD.s2, sD.s3);
      }
    }
  }
  
  private class SizingData {
    double weight;
    int s1, s2, s3;
    int c1, c2, c3;

    SizingData() {
    }

    SizingData(double weight, int c1, int c2, int c3, int s1, int s2, int s3) {
      this.weight = weight;
      this.c1 = c1;
      this.c2 = c2;
      this.c3 = c3;
      this.s1 = s1;
      this.s2 = s2;
      this.s3 = s3;
    }
    
  }
  
  /**
   * Returns an iterator of all existing and non-existing (only described in
   * configuration manager) cache regions. 
   * <p>
   * @return a <code>String</code> iterator of cache regions
   */
  public Iterator<String> iterateRegions() {
//  TODO  This iterator will be merged with the list of not yet created (but persisted) regions
    return regionFactory.iterateRegions();
  }
  
  /**
   * This method resizes the specified cache region with the given new count and
   * size thresholds. 
   * <p>
   * @param regionName  the name of the region to be resized.
   * @param count1      start of eviction <b>count</b> threshold
   * @param count2      upper limit <b>count</b> threshold
   * @param count3      critical limit <b>count</b> threshold
   * @param size1       start of eviction <b>size</b> threshold
   * @param size2       upper limit <b>size</b> threshold
   * @param size3       critical limit <b>size</b> threshold
   * 
   * @return <code>true</code> if the region was resized successfully or
   * <code>false</code> if some problem occurred or automatic resize adjusted
   * size thresholds before performing region resize.
   * 
   * @throws IllegalArgumentException if cache region does not exist.
   */
  public synchronized boolean resizeRegion(String regionName, int count1, int count2,
      int count3, int size1, int size2, int size3, boolean persist)
  throws IllegalArgumentException {
    boolean regionResized = true;
    
//  TODO Programatically created regions are not automatically resized if GLOBAL_TOTAL_SIZE is exceeded.
    SizingData sizingData = (SizingData) weights.get(regionName);
    if (sizingData != null) { // Assume that region is NOT created programatically
      sizingData.c1 = count1;
      sizingData.c2 = count2;
      sizingData.c3 = count3;
      sizingData.s1 = size1;
      sizingData.s2 = size2;
      sizingData.s3 = size3;
      resizeRegions();
      
      RegionConfigurationInfo regionInfo;
//    TODO Check for null to avoid try-catch block
      try {
        regionInfo = regionFactory.getCacheRegion(regionName).getRegionConfigurationInfo();
      } catch (NullPointerException e) {
        throw new IllegalArgumentException("Cache region [" + regionName + "] does not exist", e);
      }
      
      if (regionInfo.getSizeQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD) != size1 ||
            regionInfo.getSizeQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD) != size2 ||
            regionInfo.getSizeQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD) != size3) {
        regionResized = false;
      }
    } else { // Assume that region IS created programatically
//    TODO Check for null to avoid try-catch block
      try {
        regionFactory.resizeRegion(regionName, count1, count2, count3, size1, size2, size3);
      } catch (NullPointerException e) {
        throw new IllegalArgumentException("Cache region [" + regionName + "] does not exist", e);
      }
    }
    
    
    
    if(persist) {
//     TODO Persist new threshold data in DB 
      
    }
//  TODO  Method must throw exception if could not be persisted
    
    
    return regionResized;
  }
  
  /**
   * Removes all cached objects from a specified region.
   * <p>
   * WARNING: If a cache region is configured with instance or cluster
   * invalidation, this method can cause some performance degradation due to
   * increased cluster communication. 
   * <p>
   * @param regionName the name of the region to be cleared.
   * 
   * @throws IllegalArgumentException if cache region does not exist.
   */
  public synchronized void clearRegion(String regionName) throws IllegalArgumentException {
//  TODO NOTIFICATION?! See CacheGroup.clear() -> WARN in java doc / UI about cluster message storm
//  TODO Check for null to avoid try-catch block
    try {
      regionFactory.getCacheRegion(regionName).getCacheFacade().clear();
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("Cache region [" + regionName + "] does not exist", e);
    }
  }
  
  /**
   * Performs a best guess size calculation of the objects in the cache region.
   * <p>
   * WARNING: often usage of this method will cause serious performance degradation.
   *    
   * @param regionName the name of the region which size will be calculated.   
   *    
   * @return size of the cache region in bytes.
   * 
   * @throws IllegalArgumentException if cache region does not exist or some of
   *  the fields, that must be calculated, could not be traversed.
   * @throws IllegalAccessException if there are security restrictions.
   */
  public synchronized int calculateRegionSize(String regionName) throws IllegalArgumentException, IllegalAccessException {
    int regionSize = 0;
//  TODO Check for null to avoid try-catch block
    try {
      regionSize = regionFactory.getCacheRegion(regionName).getCacheFacade().getRegionCalculatedSize();
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("Cache region [" + regionName + "] does not exist", e);
    }
    
    return regionSize;
  }
  
  /**
   * Generates a <code>Map</code> of monitoring information for a specified
   * cache region. Keys of this <code>Map</code> are described in enumeration
   * {@link PARAMETERS}  
   * 
   * @param regionName the name of the region which monitoring data will be
   * generated and returned.   
   *    
   * @return a <code>Map<String, String></code> with monitoring information.
   * 
   * @throws IllegalArgumentException if cache region does not exist.
   */
  public synchronized Map<String, String> getMonitoringData(String regionName) throws  IllegalArgumentException {
    CacheRegion region = regionFactory.getCacheRegion(regionName);
    
    if (region == null) {
      throw new IllegalArgumentException("Cache region [" + regionName + "] does not exist");
    }
    
    SizingData defaultSizing = null;
    RegionConfigurationInfo regionInfo = region.getRegionConfigurationInfo();
    if((defaultSizing = defaultSizingData.get(regionName)) == null) {
      
//      TODO Use another data structure for default thresholds
      defaultSizing = new SizingData(0, // We don't need weight info.
          regionInfo.getCountQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD),
          regionInfo.getCountQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD),
          regionInfo.getCountQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD),
          regionInfo.getSizeQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD),
          regionInfo.getSizeQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD),
          regionInfo.getSizeQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD));
      
      defaultSizingData.put(regionName, defaultSizing);
    }
    
    Map<String, String> monData = new HashMap<String, String>();

    String owner = "Unknown";
    String description = regionInfo.getName();
    RegionConfigurationImpl extendedConfig = (RegionConfigurationImpl) regionInfo;
    Properties props = extendedConfig.getProperties();
    if (props != null) {
      description = props.getProperty(CacheXML.PROP_DESCRIPTION, description);
      owner = props.getProperty("_OWNER", owner);
    }
    
    monData.put(PARAMETERS.REGION_DESCRIPTION_STR.toString(), description);
    monData.put(PARAMETERS.REGION_OWNER_STR.toString(), owner);

//  TODO Additional logic for this property shall be added when threshold persistence is implemented.
    monData.put(PARAMETERS.PERSISTENT_STR.toString(), Boolean.toString(false));
    
    
//  TODO Add Thresholds to ShmMonitor
    ShmMonitor shmMonitor = null;
    if ((shmMonitor = (ShmMonitor)monitors.get(regionName)) != null) {
      monData.put(PARAMETERS.REGION_SIZE_STR.toString(), Integer.toString(shmMonitor.size()));
      monData.put(PARAMETERS.CACHED_OBJECTS_COUNT_STR.toString(), Integer.toString(shmMonitor.count()));
      monData.put(PARAMETERS.PUT_COUNT_STR.toString(), Integer.toString(shmMonitor.puts()));
      monData.put(PARAMETERS.GET_COUNT_STR.toString(), Integer.toString(shmMonitor.gets()));
      monData.put(PARAMETERS.HIT_RATIO_STR.toString(), Double.toString(shmMonitor.hitRate() / 10.)); // Hit Rate from per miles to percents
    }
    
    monData.put(PARAMETERS.COUNT_START_THRESHOLD_STR.toString(), Integer.toString(regionInfo.getCountQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD)));
    monData.put(PARAMETERS.COUNT_UPPER_THRESHOLD_STR.toString(), Integer.toString(regionInfo.getCountQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD)));
    monData.put(PARAMETERS.COUNT_CRITICAL_THRESHOLD_STR.toString(), Integer.toString(regionInfo.getCountQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD)));
    monData.put(PARAMETERS.SIZE_START_THRESHOLD_STR.toString(), Integer.toString(regionInfo.getSizeQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD)));
    monData.put(PARAMETERS.SIZE_UPPER_THRESHOLD_STR.toString(), Integer.toString(regionInfo.getSizeQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD)));
    monData.put(PARAMETERS.SIZE_CRITICAL_THRESHOLD_STR.toString(), Integer.toString(regionInfo.getSizeQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD)));
    
    monData.put(PARAMETERS.DEF_COUNT_START_THRESHOLD_STR.toString(), Integer.toString(defaultSizing.c1));
    monData.put(PARAMETERS.DEF_COUNT_UPPER_THRESHOLD_STR.toString(), Integer.toString(defaultSizing.c2));
    monData.put(PARAMETERS.DEF_COUNT_CRITICAL_THRESHOLD_STR.toString(), Integer.toString(defaultSizing.c3));
    monData.put(PARAMETERS.DEF_SIZE_START_THRESHOLD_STR.toString(), Integer.toString(defaultSizing.s1));
    monData.put(PARAMETERS.DEF_SIZE_UPPER_THRESHOLD_STR.toString(), Integer.toString(defaultSizing.s2));
    monData.put(PARAMETERS.DEF_SIZE_CRITICAL_THRESHOLD_STR.toString(), Integer.toString(defaultSizing.s3));
    
    return monData;
  }
  
}