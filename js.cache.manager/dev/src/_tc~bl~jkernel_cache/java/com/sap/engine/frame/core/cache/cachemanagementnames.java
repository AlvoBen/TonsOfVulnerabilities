package com.sap.engine.frame.core.cache;

public final class CacheManagementNames {

  /**
   * Object name of the global CML mBean
   */
  public static final String GLOBAL_CACHE_MANAGEMENT_MBEAN_NAME = ":j2eeType=SAP_GlobalCacheManagementMBean,name=GlobalCacheManagementBean,SAP_J2EECluster=BIN";

  /**
   * Object name of the local CML mBean
   */
  public static final String LOCAL_CACHE_MANAGEMENT_MBEAN_NAME = ":j2eeType=SAP_LocalCacheManagementMBean,name=LocalCacheManagementBean,SAP_J2EECluster=BIN,SAP_J2EEClusterNode=";
  
  /**
   * Pattern for the ObjectName for searching local CML mBeans 
   */
  public static final String LOCAL_CACHE_MANAGEMENT_MBEAN_NAME_PATTERN = ":*,j2eeType=SAP_LocalCacheManagementMBean";
  
  /**
   * Reason for failed region resize - the region is not present on this node
   */
  public static final String NO_SUCH_REGION_REASON_STR = "no_such_region";
  
  /**
   * Reason for unexpected resize - the resize of the region was performed, but the thresholds are automatically
   * adjusted to values, different from the parameters of the call
   */
  public static final String AUTOMATICALLY_ADJUSTED_REASON_STR = "automatically_adjusted_thresholds";

  /**
   * Enumeration containing all parameters of a CML region
   * <p/>
   * To be used in combination with the <tt>getParamsNames</tt> and <tt>getParamsDescriptions</tt> methods
   */
  public static enum PARAMETERS {
    REGION_DESCRIPTION_STR,
    REGION_OWNER_STR,
    SERVER_PROCESS_STR,
    REGION_SIZE_STR,
    CACHED_OBJECTS_COUNT_STR,
    PUT_COUNT_STR,
    GET_COUNT_STR,
    HIT_RATIO_STR,
    PERSISTENT_STR,
    COUNT_START_THRESHOLD_STR,
    COUNT_UPPER_THRESHOLD_STR,
    COUNT_CRITICAL_THRESHOLD_STR,
    SIZE_START_THRESHOLD_STR,
    SIZE_UPPER_THRESHOLD_STR,
    SIZE_CRITICAL_THRESHOLD_STR,
    DEF_COUNT_START_THRESHOLD_STR,
    DEF_COUNT_UPPER_THRESHOLD_STR,
    DEF_COUNT_CRITICAL_THRESHOLD_STR,
    DEF_SIZE_START_THRESHOLD_STR,
    DEF_SIZE_UPPER_THRESHOLD_STR,
    DEF_SIZE_CRITICAL_THRESHOLD_STR;

    public String getName() {
      switch (this) {
        case REGION_DESCRIPTION_STR: {
          return "Description";
        }
        case REGION_OWNER_STR: {
          return "Owner";
        }
        case SERVER_PROCESS_STR: {
          return "Process";
        }
        case REGION_SIZE_STR: {
          return "Size";
        }
        case CACHED_OBJECTS_COUNT_STR: {
          return "Cached Objects";
        }
        case PUT_COUNT_STR: {
          return "Puts";
        }
        case GET_COUNT_STR: {
          return "Gets";
        }
        case HIT_RATIO_STR: {
          return "Hit Ratio";
        }
        case PERSISTENT_STR: {
            return "Persistent";
          }
        case COUNT_START_THRESHOLD_STR: {
          return "Min Cached Objects";
        }
        case COUNT_UPPER_THRESHOLD_STR: {
          return "Max Cached Objects";
        }
        case COUNT_CRITICAL_THRESHOLD_STR: {
          return "Critical Cached Objects";
        }
        case SIZE_START_THRESHOLD_STR: {
          return "Min Size";
        }
        case SIZE_UPPER_THRESHOLD_STR: {
          return "Max Size";
        }
        case SIZE_CRITICAL_THRESHOLD_STR: {
          return "Critical Size";
        }
        case DEF_COUNT_START_THRESHOLD_STR: {
          return "Default Min Cached Objects";
        }
        case DEF_COUNT_UPPER_THRESHOLD_STR: {
          return "Default Max Cached Objects";
        }
        case DEF_COUNT_CRITICAL_THRESHOLD_STR: {
          return "Default Critical Cached Objects";
        }
        case DEF_SIZE_START_THRESHOLD_STR: {
          return "Default Min Size";
        }
        case DEF_SIZE_UPPER_THRESHOLD_STR: {
          return "Default Max Size";
        }
        case DEF_SIZE_CRITICAL_THRESHOLD_STR: {
          return "Default Critical Size";
        }
        default: {
          return "Unknown";
        }
      }
    }

    public String getDescription() {
      switch (this) {
        case REGION_DESCRIPTION_STR: {
          return "Description of the cache region";
        }
        case REGION_OWNER_STR: {
          return "Owner of the cache region";
        }
        case SERVER_PROCESS_STR: {
          return "Cluster id of the node on which is the cache";
        }
        case REGION_SIZE_STR: {
          return "Size of the cache region";
        }
        case CACHED_OBJECTS_COUNT_STR: {
          return "Number of objects in the cache region";
        }
        case PUT_COUNT_STR: {
          return "Number of put operations";
        }
        case GET_COUNT_STR: {
          return "Number of get operations";
        }
        case HIT_RATIO_STR: {
          return "Cache hit ratio";
        }
        case PERSISTENT_STR: {
            return "Should the region be persisted in the database";
        }
        case COUNT_START_THRESHOLD_STR: {
          return "Minimal number of cache objects in the region";
        }
        case COUNT_UPPER_THRESHOLD_STR: {
          return "Maximal number of cache objects in the region";
        }
        case COUNT_CRITICAL_THRESHOLD_STR: {
          return "Critical number of cache objects in the region";
        }
        case SIZE_START_THRESHOLD_STR: {
          return "Minimal size of the region";
        }
        case SIZE_UPPER_THRESHOLD_STR: {
          return "Maximal size of the region";
        }
        case SIZE_CRITICAL_THRESHOLD_STR: {
          return "Critical size of the region";
        }
        case DEF_COUNT_START_THRESHOLD_STR: {
          return "Default minimal number of cache objects in the region";
        }
        case DEF_COUNT_UPPER_THRESHOLD_STR: {
          return "Default maximal number of cache objects in the region";
        }
        case DEF_COUNT_CRITICAL_THRESHOLD_STR: {
          return "Default critical number of cache objects in the region";
        }
        case DEF_SIZE_START_THRESHOLD_STR: {
          return "Default minimal size of the region";
        }
        case DEF_SIZE_UPPER_THRESHOLD_STR: {
          return "Default maximal size of the region";
        }
        case DEF_SIZE_CRITICAL_THRESHOLD_STR: {
          return "Default critical size of the region";
        }
        default: {
          return "Unknown";
        }
      }
    }
  }

  /**
   * Enumeration containing all operations that can be invoked on the CML mBeans
   * <p/>
   * To be used in combination with the <tt>getOperationName</tt>, <tt>getOperationSigantureLength</tt> and
   * <tt>getOperationSignature</tt> methods
   */
  public static enum OPERATIONS {
    RETRIEVE_REGION_NAMES,
    RETRIVE_MONITORING_DATA,
    CLEAR_REGION,
    RESIZE_REGION,
    CALCULATE_REGION_SIZE;

    public String getName() {
      switch (this) {
        case RETRIEVE_REGION_NAMES: {
          return "retrieveRegionNames";
        }
        case RETRIVE_MONITORING_DATA: {
          return "retrieveMonitoringData";
        }
        case CLEAR_REGION: {
          return "clearRegion";
        }
        case RESIZE_REGION: {
          return "resizeRegion";
        }
        case CALCULATE_REGION_SIZE: {
          return "calculateRegionSize";
        }
        default: {
          return "Unknown";
        }
      }
    }

    public String[] getSignature() {
      switch (this) {
        case RETRIEVE_REGION_NAMES: {
          return new String[]{};
        }
        case RETRIVE_MONITORING_DATA: {
          return new String[]{String.class.getName()};
        }
        case CLEAR_REGION: {
          return new String[]{String.class.getName()};
        }
        case RESIZE_REGION: {
          return new String[]{String.class.getName(), int.class.getName(), int.class.getName(), int.class.getName(),
                              int.class.getName(), int.class.getName(), int.class.getName()};
        }
        case CALCULATE_REGION_SIZE: {
          return new String[]{String.class.getName()};
        }
        default: {
          return new String[]{};
        }
      }
    }

    public int getSignatureLength() {
      return this.getSignature().length;
    }
  }
}
