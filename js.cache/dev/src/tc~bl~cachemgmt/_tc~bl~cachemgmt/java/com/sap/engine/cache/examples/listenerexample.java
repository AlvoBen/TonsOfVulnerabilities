/*
 * Created on 2004.7.19
 *
 */
package com.sap.engine.cache.examples;

import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.util.cache.CacheControl;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.InvalidationListener;


/**
 * @author petio-p
 *
 */
public class ListenerExample implements Example, InvalidationListener {

	/* (non-Javadoc)
	 * @see com.sap.engine.cache.examples.Example#work()
	 */
	public void work() {
    DumpWriter.dump("<ListenerExample> BEGIN");
    // We get the defined region
    CacheRegionFactory factory = CacheRegionFactory.getInstance();
    CacheRegion region = factory.getCacheRegion("ExampleRegion");
    if (region == null) {
      // Reuse RegionCreation example
      Example regionCreation = new RegionCreationExample();
      regionCreation.work();
      region = factory.getCacheRegion("ExampleRegion");
    }
    // We register <this> as a listener
    CacheControl control = region.getCacheControl();
    control.registerInvalidationListener(this);

    // Reuse SingleOperations example to provoke events
    Example simpleOperations = new SimpleOperationsExample();
    simpleOperations.work();
    DumpWriter.dump("<ListenerExample> END");
	}

	public void invalidate(String key, byte event) {
    switch (event) {
      case InvalidationListener.EVENT_ATT_CHANGE: DumpWriter.dump(  "EVENT: Attribute Change : " + key); break;
      case InvalidationListener.EVENT_INVALIDATION: DumpWriter.dump("EVENT: Invalidation     : " + key); break;
      case InvalidationListener.EVENT_MODIFICATION: DumpWriter.dump("EVENT: Modification     : " + key); break;
      case InvalidationListener.EVENT_REMOVAL: DumpWriter.dump(     "EVENT: Removal          : " + key); break;
      default: DumpWriter.dump(                                     "EVENT: Unknown          : " + key);
    }
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.cache.user.InvalidationListener#invalidate(java.lang.String, java.lang.Object)
	 */
	public void invalidate(String key, Object cachedObject) {
		// This one is currently not invoked
	}

}
