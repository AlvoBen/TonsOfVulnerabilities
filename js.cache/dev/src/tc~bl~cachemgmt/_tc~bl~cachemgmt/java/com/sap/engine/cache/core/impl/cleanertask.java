/*
 * Created on 2004-10-10
 *
 */
package com.sap.engine.cache.core.impl;


import java.util.Map;
import java.util.Set;

import com.sap.engine.cache.job.Task;
import com.sap.util.cache.AttributeNames;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;


/**
 * @author ilian-n
 *
 * This task executes itself periodically and cleans the cashe from objects which
 * time to live (TTL) value has expired.
 */
public class CleanerTask implements Task {
  
  static final long serialVersionUID = 4585292944361585673L;

  // the default repeat interval of the task in seconds
  private final int DEFAULT_REPEAT_INTERVAL = 1800000; 
  private final String TASK_NAME = "CACHE_CLEANER_TASK";
  
  private int repeatInterval = DEFAULT_REPEAT_INTERVAL;
  private transient CacheRegionImpl region = null;
//  private EvictionPolicy eviction = null;
  private transient CacheFacade facade = null;
  
  public CleanerTask(CacheRegion region) {
    facade = region.getCacheFacade();
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.cache.job.Task#getName()
   */
  public String getName() {
    return TASK_NAME;
  }
  /* (non-Javadoc)
   * @see com.sap.engine.cache.job.Task#repeatable()
   */
  public boolean repeatable() {
    return true;
  }
  /* (non-Javadoc)
   * @see com.sap.engine.cache.job.Task#getInterval()
   */
  public int getInterval() {
    return repeatInterval;
  }
  /* (non-Javadoc)
   * @see com.sap.engine.cache.job.Task#getScope()
   */
  public byte getScope() {
    return Task.SCOPE_EVERY_NODE;
  }
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
	try {
	  Set keys = facade.keySet();
	    
	  Object en[] = keys.toArray();
	  for (int i = 0; i<en.length; i++) {
	    Map attributes = facade.getAttributes((String) en[i]);
	    if (attributes != null) {
	    try {
	      Long aet = (Long) attributes.get(AttributeNames.AET);
	      Long ttl = (Long) attributes.get(AttributeNames.TTL);
  	              if ((aet != null && aet.longValue() > System.currentTimeMillis()) ||
  	                  (ttl != null && ttl.longValue() < System.currentTimeMillis() - repeatInterval)) {
		    facade.remove((String) en[i]);
		  }
	    } catch (ClassCastException cce) {		// ClassCastException - most probably an invalid parameter was put
  	            // Nothing to be done here. Object shall remain in cache. Cycle shall continue.
  	            // TODO Bigger changes are expected. CleanerTask functionality shall be revised!
	    }
	    }
	  }
	} catch (Throwable t) {  					// whatever problem appears do not let this thread to die
  	  System.err.println("[Centralized Cache Management] Unexpeted problem appeared while processing [TTL] tasks in CML region [" +
  	      facade.getCacheRegion().getRegionConfigurationInfo().getName() +
          "]. Most probably a cache user has stored an invalid attribute. The central cache has automatically recovered.");
  	  t.printStackTrace();
	}
  }

}
