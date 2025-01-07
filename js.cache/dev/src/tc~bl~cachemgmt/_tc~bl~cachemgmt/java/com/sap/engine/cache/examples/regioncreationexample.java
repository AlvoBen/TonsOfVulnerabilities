/*
 * Created on 2004.7.19
 *
 */
package com.sap.engine.cache.examples;

import java.util.Iterator;
import java.util.Properties;

import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 */
public class RegionCreationExample implements Example {

	/* (non-Javadoc)
	 * @see com.sap.engine.cache.examples.Example#work()
	 */
	public void work() {
    
    DumpWriter.dump("<RegionCreation> BEGIN");

    // ExampleStub class will initialize the internal region factory, so we can directly
    // use RegionFactory here. This will return a working RegionFactory instance.
    CacheRegionFactory factory;
    factory = CacheRegionFactory.getInstance();
    
    // ExampleStub class will initialize the default plugins, so we can use them without
    // initialization code here.
    // The default storage plugins are as follows:
    //   "HashMapStorage" - storage implemntation using hashmaps
    //   "FileStorage" - storage implementation using files and java serialization
    //   "CombinatorStorage (SoftStorage + FileStorage) - spooler storage
    //   "SoftStorage" - storage implementation using memory sensitive maps
    // For this example we will use "HashMapStorage"
    // We will need a region name - in the case "ExampleRegion" and RegionConfigurationInfo instance.
    // We will use anonymous class for configuration. The user must configure 8 things when defining
    // region programmatically:
    //   * region scope
    //   * count quotas
    //   * size quotas
    //   * invalidation scope
    //   * synchronous invalidation
    //   * direct invalidation mode
    //   * logging mode
    //   * trace mode
    // In general, in J2EEngine environment such configurations will be written into the DB using 
    // configuration manager, so such code would be obsolete. Nevertheless - we cannot do that in
    // these examples.
    
    Properties confProps = new Properties();
    
    // Local scope (the only possible with "HashMapStorage"
    confProps.setProperty(RegionConfigurationInfo.PROP_REGION_SCOPE, new Integer(RegionConfigurationInfo.SCOPE_LOCAL).toString());
    // Example Quotas
    confProps.setProperty(RegionConfigurationInfo.PROP_COUNT_CRITICAL_LIMIT_THRESHOLD, "100");
    confProps.setProperty(RegionConfigurationInfo.PROP_COUNT_UPPER_LIMIT_THRESHOLD, "75");
    confProps.setProperty(RegionConfigurationInfo.PROP_COUNT_START_OF_EVICTION_THRESHOLD, "10");
    // Example quotas - 100KB, 750KB, 1000KB
    confProps.setProperty(RegionConfigurationInfo.PROP_SIZE_CRITICAL_LIMIT_THRESHOLD, new Integer(1000 * 1024).toString());
    confProps.setProperty(RegionConfigurationInfo.PROP_SIZE_UPPER_LIMIT_THRESHOLD, new Integer(750 * 1024).toString());
    confProps.setProperty(RegionConfigurationInfo.PROP_SIZE_START_OF_EVICTION_THRESHOLD, new Integer(100 * 1024).toString());
    // Local invalidation
    confProps.setProperty(RegionConfigurationInfo.PROP_INVALIDATION_SCOPE, new Integer(RegionConfigurationInfo.SCOPE_LOCAL).toString());
    // No synchronization
    confProps.setProperty(RegionConfigurationInfo.PROP_SYNCHRONOUS, "false");
    // No direct invalidation
    confProps.setProperty(RegionConfigurationInfo.PROP_DIRECT_INVALIDATION_MODE, "false");
    // No logging
    confProps.setProperty(RegionConfigurationInfo.PROP_LOGGING_MODE, "false");
    
    // Now, having a configuration we can define a region. As eviction policy the only one
    // currently present is used - SimpleLRU.
    try {
			factory.defineRegion("ExampleRegion", "HashMapStorage", "SimpleLRU", confProps);
		} catch (CacheException e) {
      LogUtil.logT(e);
		}
    
    
    // We will iterate all the regions to see if everithing went ok
    Iterator regions = factory.iterateRegions();
    while(regions.hasNext()) {
      CacheRegion current = (CacheRegion) regions.next();
      if (current.getRegionConfigurationInfo().getName().equals("ExampleRegion")) {
        String info =  current.getRegionConfigurationInfo().toString();
        DumpWriter.dump(info);
      }
    }
    
    // Now we have access to the region we just created.
    CacheRegion region = factory.getCacheRegion("ExampleRegion");
    
    // region is now useable for caching.
    
    DumpWriter.dump("<RegionCreation> END");
	}

}
