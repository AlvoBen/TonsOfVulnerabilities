/*==============================================================================
    File:         RegionConfigurationInfo.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #2 $
    $Date: 2004/08/12 $
==============================================================================*/
package com.sap.util.cache;

/**
 * The <code>RegionConfigurationInfo</code> interface constitutes a 
 * read-only interface to the current configuration of a specific 
 * cache region.
 * 
 * @author Petio Petev, Michael Wintergerst
 */
public interface RegionConfigurationInfo {

    /**
     * The <tt>none</tt> region scope.
     */
    public static final byte SCOPE_NONE = 0;

    /**
     * Cache region scope indicating that a cache region is local to 
     * a specific VM.
     */
    public static final byte SCOPE_LOCAL = 1;

    /**
     * Cache region scope indicating that a cache region is bound to a
     * specific application server instance.
     */
    public static final byte SCOPE_INSTANCE = 2;

    /**
     * Cache region scope indicating that a cache region is cluster-wide
     * available.
     */
    public static final byte SCOPE_CLUSTER = 3;

    /**
     * Indicator of the start of eviction limit where the background eviction
     * policy starts removing cached objects.
     */
    public static final byte START_OF_EVICTION_THRESHOLD = 0;
    
    /**
     * Indicator of the critical limit where eviction policy is called actively
     * on every <code>put()</code> operation.
     * The critical limit is always equal or less than the upper limit.
     */
    public static final byte CRITICAL_LIMIT_THRESHOLD = 1;
    
    /**
     * Indicator of the upper cache limit.
     */
    public static final byte UPPER_LIMIT_THRESHOLD = 2;
    
    public static final String PROP_DESCRIPTION =                       "_DESCRIPTION";
    public static final String PROP_PRINCIPAL =                         "_CACHE_PRINCIPAL";
    public static final String PROP_WEIGHT =                            "_WEIGHT";
    public static final String PROP_PARENT =                            "_PARENT";
    public static final String PROP_INHERITABLE =                       "_INHERITABLE";
    public static final String PROP_STORAGE_NAME =                      "_STORAGE_NAME";
    public static final String PROP_EVICTION_NAME =                     "_EVICTION_NAME";    
    public static final String PROP_COUNT_START_OF_EVICTION_THRESHOLD = "_COUNT_START_OF_EVICTION_THRESHOLD";
    public static final String PROP_COUNT_CRITICAL_LIMIT_THRESHOLD =    "_COUNT_CRITICAL_LIMIT_THRESHOLD";
    public static final String PROP_COUNT_UPPER_LIMIT_THRESHOLD =       "_COUNT_UPPER_LIMIT_THRESHOLD";
    public static final String PROP_SIZE_START_OF_EVICTION_THRESHOLD =  "_SIZE_START_OF_EVICTION_THRESHOLD";
    public static final String PROP_SIZE_CRITICAL_LIMIT_THRESHOLD =     "_SIZE_CRITICAL_LIMIT_THRESHOLD";
    public static final String PROP_SIZE_UPPER_LIMIT_THRESHOLD =        "_SIZE_UPPER_LIMIT_THRESHOLD";
    public static final String PROP_DIRECT_INVALIDATION_MODE =          "_DIRECT_INVALIDATION_MODE";
    public static final String PROP_INVALIDATION_SCOPE =                "_INVALIDATION_SCOPE";
    public static final String PROP_REGION_SCOPE =                      "_REGION_SCOPE";
    public static final String PROP_LOGGING_MODE =                      "_LOGGING_MODE";
    public static final String PROP_SYNCHRONOUS =                       "_SYNCHRONOUS";
    public static final String PROP_CLEANUP_INTERVAL =                  "_CLEANUP_INTERVAL";
    public static final String PROP_IS_CLIENT_DEPENDENT =               "_IS_CLIENT_DEPENDENT";
    public static final String PROP_SENDER_IS_RECEIVER_MODE = 			    "_SENDER_IS_RECEIVER_MODE";
    public static final String PROP_PUT_IS_MODIFICATION_MODE =			    "_PUT_IS_MODIFICATION_MODE";
    public static final String PROP_SIZE_CALCULATION_DEPTH =            "_SIZE_CALCULATION_DEPTH";
//    public static final String PROP_NOTIFY_ONE_LISTENER_PER_INSTANCE =  "_NOTIFY_ONE_LISTENER_PER_INSTANCE";

    public static final String EVICTION_LOCAL_LRU =                     "SimpleLRU";
    public static final String EVICTION_SHARED_LRU =                    "LRUEvictionPolicy";

    public static final String STORAGE_COMBINATOR =                     "CombinatorStorage";
    public static final String STORAGE_SHARED_MAP =                     "CommonSCMappableStoragePlugin";
    public static final String STORAGE_SHARED_COPY =                    "CommonSCCopyOnlyStoragePlugin";
    public static final String STORAGE_FILE =                           "FileStorage";
    public static final String STORAGE_DB =                             "DBStorage";               
    public static final String STORAGE_HASH_MAP =                       "HashMapStorage";
    
    public static final String DEFAULT_SIZE_CALCULATION_DEPTH =         "5";

    //  called by monitoring module and cache users

    /**
     * Returns the configured scope of the region. The possible configuration
     * scopes are <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, 
     * <code>RegionConfigurationInfo.SCOPE_INSTANCE</code> or
     * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>.
     *
     * @return  scope configured for the specific region
     */
    public byte getRegionScope();

    /**
     * Returns the automatic invalidation scope configured for the region.
     * The possible invalidation scopes are 
     * <code>RegionConfigurationInfo.SCOPE_NONE</code>, 
     * <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, 
     * <code>RegionConfigurationInfo.SCOPE_INSTANCE</code> or
     * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>.
     *
     * @return invalidation scope for this region
     */
    public byte getInvalidationScope();

    /**
     * Returns the configured name of the region
     *
     * @return The name can be 40 characters long
     */
    public String getName();

    /**
     * Returns the configured maximum total size for the region's cached
     * objects,
     *
     * @return The maximum total object sizes in bytes
     */
    public int getSizeQuota(byte level);

    /**
     * Returns the configured maximum total count of the region's cached 
     * objects.
     *
     * @param level The level that the quota parameter will be returned for
     *
     * @return The maximum total count
     */
    public int getCountQuota(byte level);

    /**
     * Returns the unique identifier for the cache region
     *
     * @return The cluster-wide unique id of the region
     */
    public int getId();

    /**
     * Returns the direct invalidation mode set to the region. If the mode is
     * set to <code>true</code>, the cached objects that are implementing
     * <code>InvalidationListener</code> interface will be invoked upon
     * invalidation.
     * 
     * <br>
     * <b>
     * Note that it depends on the implementation whether the direct object
     * invalidation mode is supported. If the implementation does not support
     * this feature, the method always returns <code>false</code>.
     * </b>
     *
     * @return the direct object invalidation mode
     */
    public boolean getDirectObjectInvalidationMode();

    /**
     * Returns the trace mode of the region. If set <to code>true</code>, all
     * operations will be traced.
     *
     * @return Trace mode flag
     */
    public boolean getTraceMode();

    /**
     * Returns the logging mode of the region. If set to <code>true</code>, all
     * operations will be logged.
     *
     * @return Logging mode flag
     */
    public boolean getLoggingMode();
    
    /**
     * Indicates whether or not invalidation messages are sent synchronously.
     * 
     * @return <code>true</code> if invalidation messages are sent
     *         synchronously; otherwise <code>false</code> is returned
     */
    public boolean isSynchronous();
    
    /**
     * Checks whether the cache region is configured to be client dependent.
     * <br>
     * <b>
     * Note that it depends on the implementation whether client dependent
     * cache regions are supported. If the implementation does not support
     * this feature, the method always returns <code>false</code>.
     * </b>
     * 
     * @return <code>true</code> if the cache region is client-aware;
     *         otherwise <code>false</code> is returned
     */
    public boolean isClientDependent();
    
    /**
     * Checks whether the cache region is configured not to receive its local 
     * notifications
     * 
     * @return
     */
    public boolean getSenderIsReceiverMode();
    
    /**
     * Checks whether put operation is treated as a modification even if the
     * operation did not overwrite an already cached object 
     * 
     * @return
     */
    public boolean getPutIsModificationMode();
    
//    /**
//     * Checks wheather only one invalidation listener is notified per instance in
//     * in case of shared memory
//     * 
//     * @return
//     */
//    public boolean getNotifyOneListenerPerInstanceMode();
    
}