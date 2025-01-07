/*
 * Created on 2004.8.17
 *
 */
package com.sap.engine.cache.examples.migration.ume;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.sap.engine.cache.admin.Monitor;
import com.sap.engine.cache.admin.impl.MonitorsAccessor;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheControl;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.spi.policy.ElementAttributes;

/**
 * @author petio-p
 *
 */
public class BaseTechCacheWrapper implements ICache {


  private String mCacheID;
  private CacheFacade mCacheAccess;
  
  private CacheControl mCacheControl;
  
  private Monitor mStatistics;
  private int mInitialSize;
  
  private ElementAttributes mDefaultAttributes;
  
  private RegionConfigurationInfo mConfiguration;
    

// Initalize IUMTrace mTrace
//  private static IUMTrace          mTrace;
//
//  static
//  {
//    //get the trace
//    mTrace = InternalUMFactory.getTrace(VERSIONSTRING);
//  }
    
  public BaseTechCacheWrapper() {
  }

  private CacheFacade getCacheFacade() {
    return mCacheAccess;
  }

  public void cleanup() {
    // nothing to do
  }

  public Object get(Object _key) {
    if (!(_key instanceof String)) {
      return null;
    } else {
      String key = (String) _key;
      return mCacheAccess.get(key);
    }
  }

  public boolean clearStatistics() {
    // cannot clear - breaks consistency?!
    // maybe I do not understand this one
    return false;
  }

  public int getMaxSize() {
    return mConfiguration.getCountQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
  }


  public boolean invalidate() {
    mCacheAccess.clear();
    return true;
  }

  public boolean invalidateLocally(Object _key) {
    if (!(_key instanceof String)) {
      return false;
    } else {
      String key = (String) _key;
      mCacheAccess.remove(key, false, RegionConfigurationInfo.SCOPE_LOCAL);
      return true;
    }
  }
    
  public boolean invalidateLocally() {
    // TODO scopes
    mCacheAccess.clear();
    return true;
  }

  public boolean invalidate(Object _key) {
    if (!(_key instanceof String)) {
      return false;
    } else {
      String key = (String) _key;
      mCacheAccess.remove(key);
      return true;
    }
  }

  public void put(Object _key, Object entry, int secondsToLive) {
    // TODO - nonstring keys
    if (!(_key instanceof String)) {
      return;
    } else {
      String key = (String) _key;
      HashMap attrs = null;
      if (secondsToLive <= 0) {
        attrs = new HashMap();
      } else {
        attrs = new HashMap();
        attrs.put("_TTL", "" + (secondsToLive * 1000));
        attrs.put("weight","1");
      }
      try {
        mCacheAccess.put(key, entry, attrs); // TODO - per element attributes
      } catch (CacheException ce) {
        // throw new UMRuntimeException(ce,"Cannot put object \""+key+"\" in BaseTech Cache \""+mCacheID+"\"");
        LogUtil.logT(ce);
      }
    }
  }
    
  public long getReadCount() {
    // not implemented, TODO - reconsider
    return mStatistics.utilization();
  }

  public long getWriteCount() {
    return mStatistics.puts();
  }

  public long getHitCount() {
    // not implemented, returns hitcount instead
    return mStatistics.hitRate();
  }

  public long getLoadCount() {
    return mStatistics.count();
  }

  public long getMissCount() {
    // not implemented, returns 1000 - hitcount instead
    return 1000 - mStatistics.hitRate();
  }

  public void put(Object key, Object entry) {
    this.put(key, entry, -1);
  }

  public void initialize(int initialSize, int maxLifeTimeInSeconds, String owner, boolean useNotification) {
    Properties allInOne = new Properties();
    // IUMParameters params = UMFactory.getProperties();
//    if (useNotification) {
//      String serviceDomainName = params.get("ume.cache.distributable_cache.service_domain_name","UME_ServiceDomain");
//      String serviceDomainHost = params.get("ume.cache.distributable_cache.service_domain_host","localhost");
//      int serviceDomainPort = params.getNumber("ume.cache.distributable_cache.service_domain_port",9999);
//      String consistencyDomainName = params.get("ume.cache.distributable_cache.consistency_domain_name","UME_ConsistencyDomain");
//      String consistencyDomainClass = params.get("ume.cache.distributable_cache.consistency_domain_class");
//      allInOne.setProperty("consistency-domain.domain.name",serviceDomainName);
//      allInOne.setProperty("consistency-domain.domain.host",serviceDomainHost);
//      allInOne.setProperty("consistency-domain.domain.port",""+serviceDomainPort);
//      allInOne.setProperty("consistency-domain.name",consistencyDomainName);
//      if (consistencyDomainClass != null) {
//        allInOne.setProperty("consistency-domain.class",consistencyDomainClass);
//      } else {
//        allInOne.setProperty("consistency-domain.class", "com.sap.ip.basecomps.consistency.impl.None");
//      }
//    } else {
//      allInOne.setProperty("consistency-domain.class", "com.sap.ip.basecomps.consistency.impl.None");
//    }
    //allInOne.setProperty("cache-loader.class","TestClass$TestLoader");
    if (maxLifeTimeInSeconds <= 0) {
      allInOne.setProperty("lifetime",""+(-1));
    } else {
      allInOne.setProperty("lifetime",""+(1000 * maxLifeTimeInSeconds));
    }
    if (initialSize <= 0) {
//      if (mTrace.beInfo()) {
//        mTrace.infoT("initialize","Initial size for cache (owner="+owner+") is negative or 0. Using default 500.");
//      }
      initialSize = 500;
    }
    allInOne.setProperty("maxweight",""+initialSize);

    Properties props = new Properties();
    if (maxLifeTimeInSeconds > 0) {
      props.put("lifetime",""+(maxLifeTimeInSeconds * 1000));
    } else {
      props.put("lifetime","-1");
    } 
    props.put("weight","1");
    mDefaultAttributes = null; // new ElementAttributes(props);

    mInitialSize = initialSize;
    ElementAttributes attr = null; // new ElementAttributes(allInOne);

    mCacheID = "UME_Cache_" + owner;
    
    try {
//      CacheRegionFactory.defineRegion(mCacheID,attr);

      RegionConfigurationInfo configuration = new RegionConfigurationInfo() {
      
        // Local scope (the only possible with "HashMapStorage"
        public byte getRegionScope() { return RegionConfigurationInfo.SCOPE_LOCAL; }
        // Example Quotas
        public int getCountQuota(byte level) {
          switch (level) {
            case RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD: return 10;
            case RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD: return 75;
            case RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD: return 100;
            default: return 0; // impossible
          }
        }
        // Example quotas - 100KB, 750KB, 1000KB
        public int getSizeQuota(byte level) { 
          switch (level) {
            case RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD: return 100 * 1024;
            case RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD: return 750 * 1024;
            case RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD: return 1000 * 1024;
            default: return 0; // impossible
          }
        }
        // Invalidation messages throughout the whole cluster
        public byte getInvalidationScope() { return RegionConfigurationInfo.SCOPE_LOCAL; }
        // No synchronization
        public boolean isSynchronous() { return false; }
        // No direct invalidation
        public boolean getDirectObjectInvalidationMode() { return false; }
        // No logging
        public boolean getLoggingMode() { return false; }
        // No tracing
        public boolean getTraceMode() { return false; }

        // this will not reflect real configuration as they are rewritten when region is defined
        // programmatically
        public String getName() { return null; }
        public int getId() { return 0; }
        public boolean isClientDependent() {
          return false;
        }
        public boolean getPutIsModificationMode() { return false; }
        public boolean getSenderIsReceiverMode() { return true; }

      };
      CacheRegionFactory.getInstance().defineRegion(mCacheID, "HashMapStorage", "SimpleLRU", configuration);
    } catch (CacheException ce) {
      // throw new UMRuntimeException(cnaex,"BaseTech Cache creation failed. generated ID: \""+mCacheID+"\"");
      LogUtil.logT(ce);
    }        
    mCacheAccess = CacheRegionFactory.getInstance().getCacheRegion(mCacheID).getCacheFacade();
    mStatistics = MonitorsAccessor.getMonitor(mCacheID);
  }

  public Enumeration getKeys() {
    return new Enumeration() {
      
      Iterator localIterator = null;
      
			public boolean hasMoreElements() {
        if (localIterator == null) {
          localIterator = mCacheAccess.keySet().iterator();
        }
        return localIterator.hasNext(); 
			}

			public Object nextElement() {
        if (localIterator == null) {
          localIterator = mCacheAccess.keySet().iterator();
        }
        return localIterator.next();
			}
		};
  }
    
}
