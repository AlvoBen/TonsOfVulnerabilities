/*
 * Created on 2005.5.25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.frame.core.cache;

import java.util.Map;
import java.util.Set;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CacheRegionInquiry {
  
  public static final String OP_OBJECT = "_OBJECT";
  public static final String OP_ATTRIBUTES = "_ATTRIBUTES";
  public static final String OP_SIZE = "_SIZE";
  public static final String OP_VERSION_COUNT = "_VERSION_COUNT";
  public static final String OP_TOTAL_SIZE = "_TOTAL_SIZE";

  /**
   * Returns all currently available cache regions' names
   * 
   * @return Region names (no specific order)
   */
  public String[] listCacheRegionNames();
  
  /**
   * Returns all available cache group names in a particular region
   * 
   * @param cacheRegionName The name of the region which group names will be returned for
   * @return Cache group names in the particular region (no specific order)
   * @throws CacheContextException If wrong region name is passed
   */
  public String[] listCacheGroups(String cacheRegionName) throws CacheContextException;
  
  /**
   * Returns all object keys in a particular region and group
   * 
   * @param cacheRegionName The name of the region which object keys will be returned for
   * @param cacheGroup The name of the cache group within the region. If null is passed, 
   * all object keys for the region will be returned
   * @return The object keys of selected region and group
   * @throws CacheContextException If a wrong region name or group name is passed
   */
  public String[] listObjectKeys(String cacheRegionName, String cacheGroup) throws CacheContextException;
  
  /**
   * Returns any additional object properties for a particular object key
   * 
   * @param cacheRegionName The name of the region which object keys will be taken account for
   * @param cacheGroup The name of the group within the region. If null is passed, all object keys are considered
   * @param objectKey The object key which additional properties will be returned for
   * @return The additional properties for the particular object key
   * @throws CacheContextException If the region name or group name is wrong or the object key was not found in the region/group
   */
  public Map getAdditionalObjectProperties(String cacheRegionName, String cacheGroup, String objectKey)  throws CacheContextException;
  
  /**
   * Returns any additional object properties for a set of object keys
   * 
   * @param cacheRegionName The name of the region which object keys will be taken account for
   * @param cacheGroup The name of the group within the region. If null is passed, all object keys are considered
   * @param objectKeys The object key set which additional properties will be returned for
   * @return The additional properties for the particular object key
   * @throws CacheContextException If the region name or group name is wrong or the object key was not found in the region/group
   */
  public Map getAdditionalObjectProperties(String cacheRegionName, String cacheGroup, Set objectKeys)  throws CacheContextException;
}
