/*
 * Created on 2004.7.19
 *
 */
package com.sap.engine.cache.examples;

import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.exception.CacheException;

/**
 * @author petio-p
 *
 */
public class SimpleOperationsExample implements Example {

	public void work() {
    DumpWriter.dump("<SimpleOperations> BEGIN");
    
    // Now we assume we have a working region "ExampleRegion"
    CacheRegionFactory factory = CacheRegionFactory.getInstance();
    CacheRegion region = factory.getCacheRegion("ExampleRegion");
    if (region == null) {
      // We will reuse region creation example to create cache region to work with
      Example regionCreation = new RegionCreationExample();
      regionCreation.work();
      region = factory.getCacheRegion("ExampleRegion");
    }
		
    // The CacheFacade is the user facade that can be used for simplest operations
    CacheFacade facade = region.getCacheFacade();

    // ---------------------------------------------------------
    //
    // CONTAINS KEY
    //
    // ---------------------------------------------------------
    // We check for presence of KEY1 in the cache
    DumpWriter.dump("CONTAINS(KEY)?: "); // <============
    if (facade.containsKey("KEY1")) {
      DumpWriter.dump("KEY1 exists in the cache");
    } else {
      DumpWriter.dump("KEY1 does NOT exist in the cache");
    }
    
    // ---------------------------------------------------------
    //
    // PUT
    //
    // ---------------------------------------------------------
    // We put a newly created object in the cache under the name KEY1
    DumpWriter.dump("PUT(KEY1, OBJ1)");
    try {
			facade.put("KEY1", new CObject("OBJ1")); // <============
		} catch (CacheException e) {
      LogUtil.logT(e);
			// Could not put the object into the storage
		}
    
    // We check for presence of KEY1 in the cache
    DumpWriter.dump("CONTAINS(KEY)?: ");
    if (facade.containsKey("KEY1")) {
      DumpWriter.dump("KEY1 exists in the cache");
    } else {
      DumpWriter.dump("KEY1 does NOT exist in the cache");
    }
    
    // ---------------------------------------------------------
    //
    // GET
    //
    // ---------------------------------------------------------
    // Now we check the contents of the object in the cache
    DumpWriter.dump("GET(KEY): ");
    Object cachedObject = facade.get("KEY1"); // <============
    DumpWriter.dump("KEY1: " + cachedObject);
    
    // ---------------------------------------------------------
    //
    // PUT (REWRITE)
    //
    // ---------------------------------------------------------
    // We rewrite object in the cache under the name KEY1
    DumpWriter.dump("PUT(KEY1, OBJ2)");
    try {
      facade.put("KEY1", new CObject("OBJ2")); // <============
    } catch (CacheException e) {
      // Could not put the object into the storage
      LogUtil.logT(e);
    }

    // Now we check the contents of the object in the cache
    DumpWriter.dump("GET(KEY): ");
    cachedObject = facade.get("KEY1");
    DumpWriter.dump("KEY1: " + cachedObject);

    // ---------------------------------------------------------
    //
    // REMOVE
    //
    // ---------------------------------------------------------
    // We remove the object from the cache
    DumpWriter.dump("REMOVE(KEY1)");
  	facade.remove("KEY1"); // <============

    // We check for presence of KEY1 in the cache
    DumpWriter.dump("CONTAINS(KEY)?: ");
    if (facade.containsKey("KEY1")) {
      DumpWriter.dump("KEY1 exists in the cache");
    } else {
      DumpWriter.dump("KEY1 does NOT exist in the cache");
    }

    DumpWriter.dump("<SimpleOperations> END");
	}

}
