package com.sap.engine.cache.junit;

import com.sap.util.cache.CacheControl;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Check for ADS.
 * 
 * @author SAP AG
 * @version 1.0
 */
public class ClusterCacheInvalidationCheck  {

  private final static String OBJ_NAME = "OBJECT_NAME"; //$NON-NLS-1$

  private final static String TOPIC_NODE_NAME = "NODE_NAME"; //$NON-NLS-1$

  private final static String TOPIC_NODE_CACHE_FILLED = "NODE_CACHE_FILLED"; //$NON-NLS-1$

  private final static String TOPIC_ERROR_TEXT = "ERROR_TEXT"; //$NON-NLS-1$

  private final static String BI_TEST_CACHE_REGION = "BI_SUPPORT_DESK"; //$NON-NLS-1$

  private static final String DEFAULT_STORAGE_PLUGIN = "HashMapStorage"; //$NON-NLS-1$

  private static final String DEFAULT_EVICTIAN_POLICY_PLUGIN = "SimpleLRU"; //$NON-NLS-1$

  private static Properties DEFAULT_CONFIG_PROPERTIES;

  static {
    // define cache region
    DEFAULT_CONFIG_PROPERTIES = new Properties();

    // cache scope is local
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_REGION_SCOPE, new Integer(RegionConfigurationInfo.SCOPE_INSTANCE).toString());

    // object number thresholds
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_COUNT_START_OF_EVICTION_THRESHOLD, "100"); //$NON-NLS-1$
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_COUNT_UPPER_LIMIT_THRESHOLD, "200"); //$NON-NLS-1$
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_COUNT_CRITICAL_LIMIT_THRESHOLD, "500"); //$NON-NLS-1$

    // memory size thresholds
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_SIZE_START_OF_EVICTION_THRESHOLD, new Integer(10000 * 1024).toString());
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_SIZE_UPPER_LIMIT_THRESHOLD, new Integer(20000 * 1024).toString());
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_SIZE_CRITICAL_LIMIT_THRESHOLD, new Integer(50000 * 1024).toString());

    // local invalidation messages
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_INVALIDATION_SCOPE, new Integer(RegionConfigurationInfo.SCOPE_CLUSTER).toString());

    // no synchronous invalidation
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_SYNCHRONOUS, "false"); //$NON-NLS-1$

    // no direct invalidation listener call
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_DIRECT_INVALIDATION_MODE, "false"); //$NON-NLS-1$

    // no logging
    DEFAULT_CONFIG_PROPERTIES.setProperty(RegionConfigurationInfo.PROP_LOGGING_MODE, "false"); //$NON-NLS-1$
  }



  /**
   * Constructor for check class.
   */
  public ClusterCacheInvalidationCheck() {

  }

  public String getDescription() {
    return "Cache Invalidation"; //$NON-NLS-1$
  }

  public void runCheck() throws Exception {

    // cache invalidation by single remove commands
    boolean continueFlag = checkSingleObjectInvalidate();
    if (!continueFlag)
      return;

    // check invlaidation by wildcard
    checkInvalidateByWildcard();
    
  }

  private boolean checkInvalidateByWildcard() {
  
    ArrayList nodeNames = new ArrayList();
    
    // fill cache    
    boolean continueFlag = fillCache(nodeNames);
    if (!continueFlag)
      return false;

    // remove alls objects from cache
    CacheControl cacheControl = getCacheControl();
    if(cacheControl==null) throw new RuntimeException("Error when getting cache control object"); //$NON-NLS-1$
    cacheControl.invalidate("**"); //$NON-NLS-1$
    
    // check empty cache
    checkForEmptyCache("remove by wildcard"); //$NON-NLS-1$

    return true;
    
  }

  private boolean checkSingleObjectInvalidate() {
    ArrayList nodeNames = new ArrayList();
    
    // fill cache    
    boolean continueFlag = fillCache(nodeNames);
    if (!continueFlag)
      return false;

    // remove objects from cache
    CacheControl cacheControl = getCacheControl();
    if(cacheControl==null) throw new RuntimeException("Error when getting cache control object"); //$NON-NLS-1$
    for(Iterator itr=nodeNames.iterator();itr.hasNext();){
      String nodeName = (String) itr.next();
      Map attributes = new HashMap();
      attributes.put(OBJ_NAME, nodeName);
      cacheControl.invalidate(attributes);
    }
    
    // check empty cache
    checkForEmptyCache("single object remove error"); //$NON-NLS-1$

    return true;
  }


  /**
   * Handler for fill cache requests.
   * 

   * @return Response topic data container.
   */
  public static void handleFillCacheTopic( ) {

    // get own cluster node id
    String nodeId = "nodeId";

    // get cache group
    CacheGroup cacheGroup = getCacheGroup();
    if (cacheGroup == null) {
      throw new RuntimeException();
    }

    // store node id in cache
    try {
      Map objectToStoreAttributes = new HashMap();
      objectToStoreAttributes.put(OBJ_NAME, nodeId);
      Object objectToStore = nodeId;
      String objectToStoreKey = nodeId;
      cacheGroup.put(objectToStoreKey, objectToStore, objectToStoreAttributes, true, true);
    } catch (CacheException ce) {
      throw new RuntimeException("Cannot fill cache"); //$NON-NLS-1$
    }

     handleCheckCacheTopic();
  }

  /**
   * Handler for ceck cache requests.
   * 

   * @return Response topic data container.
   */
  public static boolean handleCheckCacheTopic() {

    // get own cluster node id

    String nodeId = "nodeId";

    // get cache group
    CacheGroup cacheGroup = getCacheGroup();
    if (cacheGroup == null) {
      throw new RuntimeException("Cache group access not possible"); //$NON-NLS-1$
    }

    // get stored object
    Object obj = cacheGroup.get(nodeId);

    // return response message
    return (obj != null);


  }



  private static CacheControl getCacheControl() {

    // get cache region
    CacheRegion cacheRegion = getCacheRegion();
    if(cacheRegion==null) return null;
    
    // get cache group from cache region
    CacheControl cacheControl = cacheRegion.getCacheControl();  
    return cacheControl;

  }

  private static CacheGroup getCacheGroup() {

    // get cache region
    CacheRegion cacheRegion = getCacheRegion();
    if(cacheRegion==null) return null;
    
    // get cache group from cache region
    CacheGroup cacheGroup = cacheRegion.getCacheFacade();  
    return cacheGroup;

  }

  private static CacheRegion getCacheRegion() {

    // get factory for cache regions
    CacheRegionFactory cacheRegionFactory = CacheRegionFactory.getInstance();

    // get cache region
    CacheRegion cacheRegion = null;
    Exception exception= null;
    try {
      cacheRegion = cacheRegionFactory.getCacheRegion(ClusterCacheInvalidationCheck.BI_TEST_CACHE_REGION);
    } catch (Exception e) {
      exception = e;
    }
    
    // cache region does not exist -> create cache region
    if(cacheRegion==null || exception!=null){
      try {
        cacheRegionFactory.defineRegion(ClusterCacheInvalidationCheck.BI_TEST_CACHE_REGION, DEFAULT_STORAGE_PLUGIN, DEFAULT_EVICTIAN_POLICY_PLUGIN, DEFAULT_CONFIG_PROPERTIES);
        cacheRegion = cacheRegionFactory.getCacheRegion(ClusterCacheInvalidationCheck.BI_TEST_CACHE_REGION);
      } catch (CacheException e) {
        return null;
      }
    }
    
    return cacheRegion;

  }

  private boolean fillCache(ArrayList nodeNames) {

    for (int i = 0; i <= 10; i++) {
       nodeNames.add(new Integer(i).toString());
    }


     return true;
   }

  private void checkForEmptyCache(String errorText) {

    // send broadcast message
    // check response
    for (int i = 0; i < 10; ++i) {

//      checkForError(response);

//        addCheckError("Test of cluster invalidation failed for cluster node " + nodeName+" ("+errorText+")", "Cache invalidation messages are not propagated to all cluster nodes of your J2EE installation. This can lead to the usage of outdatet web template versions. Apply note 1086754.", new NoteLink(1087841)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    }

  }


}
