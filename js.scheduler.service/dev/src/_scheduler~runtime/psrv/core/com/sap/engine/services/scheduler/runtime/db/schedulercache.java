package com.sap.engine.services.scheduler.runtime.db;

import java.util.ArrayList;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;


public class SchedulerCache {
    
    //logging and tracing
    private final static Location location = Location.getLocation(SchedulerCache.class);
    private final static Category category = LoggingHelper.SYS_SERVER;
  
    public static final String CACHE_REGION_NAME = "NetWeaverSchedulerCacheRegion";
    private CacheFacade m_cache = null;
    // in case we search for SchedulerDefinitions with SchedulerID, or with user- 
    // or SchedulerDefinition-name we need prefixes for the several Strings
    private static final String SCHEDULER_DEF_ID_PREFIX   = "S_ID_";
    private static final String SCHEDULER_DEF_USER_PREFIX = "S_USER_";
    private static final String SCHEDULER_DEF_NAME_PREFIX = "S_NAME_";
    // the prefix for JobDefinitions
    private static final String JOB_DEFINITION_PREFIX = "JD_";
    
    
    public SchedulerCache() {     
        CacheRegionFactory factory = CacheRegionFactory.getInstance();          
        CacheRegion region = factory.getCacheRegion(CACHE_REGION_NAME);
        
        if (region != null) {
            m_cache = region.getCacheFacade();
            
            if (location.beDebug()) {
                RegionConfigurationInfo config = region.getRegionConfigurationInfo();
                location.debugT("Configuration for region \"" + CACHE_REGION_NAME + "\": " + config);
            }
        }
    }
    
    
    // -------------------------------------------------------------------
    // caching methods for JobDefinitions
    // -------------------------------------------------------------------
  
    public void cache_put(ArrayList<JobDefinition> defs) {
        if (m_cache != null) {
            try {
                for (JobDefinition def : defs) {
                  m_cache.put(JOB_DEFINITION_PREFIX+def.getJobDefinitionId().toString(), def);
                }
            } catch (CacheException we) {
                category.logThrowableT(Severity.ERROR, location, "Cache write operation failed: ", we);
            }
        }
    }
    
    public JobDefinition cache_get(JobDefinitionID id) {
        if (m_cache != null) {
            return (JobDefinition)m_cache.get(JOB_DEFINITION_PREFIX+id.toString());
        } else {
            return null;
        }
    }
    
    public void cache_invalidate(JobDefinitionID id) {
        if (m_cache != null) {
          m_cache.remove(JOB_DEFINITION_PREFIX+id.toString(), false, false);
        }
    }
  
    public void cache_invalidate(JobDefinition def) {
        cache_invalidate(def.getJobDefinitionId());
    }
    
    
    
    // -------------------------------------------------------------------
    // caching methods for SchedulerDefinition
    // -------------------------------------------------------------------
    
    public void cache_put(SchedulerDefinition[] defs) {
        for (int i = 0; i < defs.length; i++) {
            cache_put(defs[i]);
        }
    }
  
    public void cache_put(SchedulerDefinition def) {
        if (m_cache != null) {
            try {
                m_cache.put(SCHEDULER_DEF_ID_PREFIX  +def.getId().toString(), def);
                m_cache.put(SCHEDULER_DEF_NAME_PREFIX+def.getName(), def);
                m_cache.put(SCHEDULER_DEF_USER_PREFIX+def.getUser(), def);
            } catch (CacheException we) {
                category.logThrowableT(Severity.ERROR, location, "Cache write operation failed: ", we);
            }
        }
    }
    
    public SchedulerDefinition cache_getSchedulerDefinitionByID(SchedulerID id) {
        if (m_cache != null) {
            return (SchedulerDefinition)m_cache.get(SCHEDULER_DEF_ID_PREFIX+id.toString());
        } else {
            return null;
        }
    }
    
    public SchedulerDefinition cache_getSchedulerDefinitionByUser(String user) {
        if (m_cache != null) {
            return (SchedulerDefinition)m_cache.get(SCHEDULER_DEF_USER_PREFIX+user);
        } else {
            return null;
        }
    }
    
    public SchedulerDefinition cache_getSchedulerDefinitionByName(String name) {
        if (m_cache != null) {
            return (SchedulerDefinition)m_cache.get(SCHEDULER_DEF_NAME_PREFIX+name);
        } else {
            return null;
        }
    }
    
    private void cache_invalidate(String key) {
        if (m_cache != null) {
          m_cache.remove(key, false, false);
        }
    }
  
    public void cache_invalidate(SchedulerDefinition def) {
        cache_invalidate(SCHEDULER_DEF_ID_PREFIX  +def.getId().toString());
        cache_invalidate(SCHEDULER_DEF_USER_PREFIX+def.getUser());
        cache_invalidate(SCHEDULER_DEF_NAME_PREFIX+def.getName());
    }
    
}
