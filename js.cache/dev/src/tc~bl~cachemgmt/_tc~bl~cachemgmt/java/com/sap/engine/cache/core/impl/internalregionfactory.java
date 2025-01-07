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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import com.sap.engine.cache.spi.storage.impl.FileStorage;
import com.sap.engine.cache.spi.storage.impl.HashMapStorage;
import com.sap.engine.cache.spi.storage.impl.DummyStorage;
import com.sap.engine.cache.spi.policy.impl.SimpleLRUEvictionPolicy;
import com.sap.engine.cache.spi.policy.impl.DummyEvictionPolicy;
import com.sap.engine.cache.util.ReferenceQueueRunnable;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.CacheFeatureSet;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.storage.StoragePlugin;
import com.sap.util.cache.spi.policy.EvictionPolicy;

/**
 * Date: Feb 26, 2004
 * Time: 5:19:06 PM
 * 
 * @author Petio Petev, i024139
 */

public class InternalRegionFactory extends CacheRegionFactory {

  protected static final ReferenceQueueRunnable referenceQueue = new ReferenceQueueRunnable();

  private static class FeatureSet implements CacheFeatureSet {

		public boolean isInvalidationListenerSupported() {
			return true;
		}

		public boolean isDirectObjectInvalidationSupported() {
			return true;
		}

		public boolean isClientSupported() {
			return false;
		}

		public boolean isSupported(String feature) {
      return CacheFeatureSet.DIRECT_OBJECT_INVALIDATION.equals(feature) ||
             CacheFeatureSet.INVALIDATION_LISTENER.equals(feature);
      }
		} 
  
  private static final FeatureSet featureSet = new FeatureSet();
 
  protected static CacheFactoryListener listener = null;

  protected static final Hashtable readyRegions = new Hashtable();

  static {
    {
      HashMapStorage hashMapStorage = new HashMapStorage("SoftStorage");
      PluggableFramework.putPluggable(hashMapStorage.getName(), hashMapStorage);
    }
    {
      CombinatorStorage combStorage = new CombinatorStorage();
      combStorage.init("CombinatorStorage", new Properties());
      PluggableFramework.putPluggable(combStorage.getName(), combStorage);
    }
    {
      FileStorage fileStorage = new FileStorage();
      fileStorage.init("FileStorage", new Properties());
      PluggableFramework.putPluggable(fileStorage.getName(), fileStorage);
    }
    {
      SimpleLRUEvictionPolicy policy = new SimpleLRUEvictionPolicy();
      Properties props = new Properties();
      policy.init("LRUEvictionPolicy", props);
      PluggableFramework.putPluggable(policy.getName(), policy);
    }

    {
      DummyEvictionPolicy policy = new DummyEvictionPolicy();
      PluggableFramework.putPluggable(policy.getName(), policy);
    }

    {
      DummyStorage storage = new DummyStorage();
      PluggableFramework.putPluggable(storage.getName(), storage);
    }
  }


  public InternalRegionFactory() {
    CacheRegionFactory.setInternalFactory(this);
  }

  public Iterator iterateRegions() {
    return ((Hashtable)readyRegions.clone()).keySet().iterator();
  }

  public CacheRegion getCacheRegion(String region) {
    CacheRegion result = (CacheRegion) readyRegions.get(region);
    // check caller for principal
    if (result != null) { 
	  RegionConfigurationInfo rci = result.getRegionConfigurationInfo();
	  if (rci instanceof RegionConfigurationInfoExtension) {
	    RegionConfigurationInfoExtension rcie = (RegionConfigurationInfoExtension) rci;
	    Properties properties = rcie.getProperties();
	    if (properties != null) {
	      String principal = properties.getProperty("_CACHE_PRINCIPAL");
	      if (principal != null) {
	        String defaultPrincipal = null;
	        if (listener != null) {
	          defaultPrincipal = listener.getClass().getName();
	        }
	        boolean ok = false;
	        SecurityException exception = new SecurityException("Cache Region Cannot be gotten without principal " + principal + "!");
	        StringWriter stringWriter = new StringWriter();
	        PrintWriter printWriter = new PrintWriter(stringWriter);
	        exception.printStackTrace(printWriter);
	        String exceptionString = new String(stringWriter.getBuffer());
	        StringReader stringReader = new StringReader(exceptionString);
	        BufferedReader reader = new BufferedReader(stringReader);
	        boolean hasMore = true;
	        try {
	          // skip first line because it contains the principal already :)
	          reader.readLine();
	        } catch (IOException e1) {
	          LogUtil.logTInfo(e1);
	        }
	        do {
	          String nextLine;
	          try {
	            nextLine = reader.readLine();
	          } catch (IOException e) {
	            LogUtil.logTInfo(e);
	            nextLine = null;
	          }
	          if (nextLine != null) {
	            if (nextLine.indexOf(principal) != -1) {
	              ok = true;
	              break;
	            }
	            if (defaultPrincipal != null) {
	              if (nextLine.indexOf(defaultPrincipal) != -1) {
	                ok = true;
	                break;
	              }
	            }
	          } else {
	            hasMore = false;
	          }
	        } while (hasMore);
	          
	        if (!ok) {
	          throw exception;
	        }
	      }
	    }
	  }
	  // notify listener if there is one set
	//    if (listener != null) {
	//      listener.onGetCacheRegion(region, result);
	//    }
    }
    return result;
  }

  public static final void setListener(CacheFactoryListener listener) {
    if (InternalRegionFactory.listener == null) {
      InternalRegionFactory.listener = listener;
    }
  }

  public static final ReferenceQueueRunnable getReferenceQueue() {
    return referenceQueue; 
  }

  public void defineRegion(String regionName, String storagePlugin, String evictionPolicy, RegionConfigurationInfo configuration) throws CacheException {
    if (regionName != null && storagePlugin != null && evictionPolicy != null && configuration != null) {
      if (readyRegions.get(regionName) == null) {
        CacheRegionImpl region = new CacheRegionImpl(regionName);
        region.init(configuration, storagePlugin, evictionPolicy);
        readyRegions.put(regionName, region);
      }
    } else {
      throw new CacheException("Cannot use NULL arguments: "  + (regionName == null ? "regionName, " : "") + (storagePlugin == null ? "storagePlugin, " : "") +
        (evictionPolicy == null ? "evictionPolicy, " : "") + (configuration == null ? "configuration, " : ""));
    }
  }

  //todo if already defined throw exception
  public void defineRegion(String regionName, String storagePlugin, String evictionPolicy, Properties properties) throws CacheException {
    if (properties == null) {
      properties = new Properties();
    }
    final int[] count = {
      Integer.parseInt(properties.getProperty(RegionConfigurationInfo.PROP_COUNT_START_OF_EVICTION_THRESHOLD, "200")),
      Integer.parseInt(properties.getProperty(RegionConfigurationInfo.PROP_COUNT_CRITICAL_LIMIT_THRESHOLD, "300")),
      Integer.parseInt(properties.getProperty(RegionConfigurationInfo.PROP_COUNT_UPPER_LIMIT_THRESHOLD, "400"))
    };
    final int[] size = {
      Integer.parseInt(properties.getProperty(RegionConfigurationInfo.PROP_SIZE_START_OF_EVICTION_THRESHOLD, "1048576")),
      Integer.parseInt(properties.getProperty(RegionConfigurationInfo.PROP_SIZE_CRITICAL_LIMIT_THRESHOLD, "2097152")),
      Integer.parseInt(properties.getProperty(RegionConfigurationInfo.PROP_SIZE_UPPER_LIMIT_THRESHOLD, "3145728"))
    };
    final boolean directInvalidation = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_DIRECT_INVALIDATION_MODE, "false"));
    final byte invalidationScope = Byte.parseByte(properties.getProperty(RegionConfigurationInfo.PROP_INVALIDATION_SCOPE, "1"));
    final byte regionScope = Byte.parseByte(properties.getProperty(RegionConfigurationInfo.PROP_REGION_SCOPE, "1"));
    final boolean loggingMode = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_LOGGING_MODE, "false"));
    final boolean synchronous = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_SYNCHRONOUS, "false"));
    final boolean clientDependent = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_IS_CLIENT_DEPENDENT, "false"));
    final boolean putIsModificationMode = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_PUT_IS_MODIFICATION_MODE, "false"));
    final boolean senderIsReceiverMode = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_SENDER_IS_RECEIVER_MODE, "true"));
//    final boolean dbIsClusterWide = "true".equals(properties.getProperty(RegionConfigurationInfo.PROP_DB_IS_CLUSTER_WIDE, "false"));
    final long cleanupInterval = Long.parseLong(properties.getProperty(RegionConfigurationInfo.PROP_CLEANUP_INTERVAL, "30000"));
    final Properties finalProperties = properties;
    RegionConfigurationInfo configuration = new RegionConfigurationInfoExtension() {
      public byte getRegionScope() { return regionScope; }
	  public byte getInvalidationScope() { return invalidationScope; }
	  public String getName() {	return null; }
	  public int getSizeQuota(byte level) { return size[level];	}
	  public int getCountQuota(byte level) { return count[level]; }
	  public int getId() { return 0; }
	  public boolean getDirectObjectInvalidationMode() { return directInvalidation;	}
	  public boolean getTraceMode() { return false; }
	  public boolean getLoggingMode() { return loggingMode; }
	  public boolean isSynchronous() { return synchronous; }
	  public boolean isClientDependent() { return clientDependent; }
      public long getCleanupInterval() { return cleanupInterval; }
      public Properties getProperties() { return finalProperties; }
      public boolean getPutIsModificationMode() { return putIsModificationMode; }
      public boolean getSenderIsReceiverMode() { return senderIsReceiverMode;}
//    public boolean getNotifyOneListenerPerInstanceMode() { return false; }
//  	  public boolean getDBIsClusterWide() {return dbIsClusterWide;}
    };
    defineRegion(regionName, storagePlugin, evictionPolicy, configuration);
  }

	public void initDefaultPluggables() { 

  }

  public void removeRegion(String regionName) {
    readyRegions.remove(regionName);
  }
  
	public CacheFeatureSet getFeatureSet() {
		return featureSet;
	}
  
  public void resizeRegion(String regionName, int count1, int count2, int count3, int size1, int size2, int size3) {
    CacheRegionImpl region = (CacheRegionImpl) readyRegions.get(regionName);
    region.resize(count1, count2, count3, size1, size2, size3);
  }

}
