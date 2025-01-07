package com.sap.engine.cache.admin.impl;

import java.io.Serializable;
import java.util.Properties;

import com.sap.engine.cache.admin.RegionConfiguration;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
public class RegionConfigurationImpl implements RegionConfiguration, Serializable {

  static final long serialVersionUID = 3998660420639467275L;
  
  private Properties properties;
  private byte regionScope = -1;
  private byte invalidationScope;
  private String name;
  private int[] sizeQuota = new int[3];
  private int[] countQuota = new int[3];
  private int id;
  private transient EvictionPolicy evictionPolicy;
  private transient StoragePlugin storagePlugin;
  private boolean directInvalidation;
  private boolean trace;
  private boolean logging;
  private boolean synchronous;
  private boolean putIsModification = false; // By default put is not a modification operation unless it overwrites data
  private boolean senderIsReceiver = true; // By default the sender is a receiver of its own requests

  /**
   * Sets the configured scope of the region.
   *
   * @param scope Can be <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, <code>RegionConfigurationInfo.SCOPE_MACHINE</code> and
   * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>
   * @throws IllegalArgumentException Thrown if the <code>scope</code> is not a valid constant
   */
  public void setRegionScope(byte scope) {
    this.regionScope = scope;
  }

  /**
   * Sets the automatic invalidation scope configured for the region.
   *
   * @param scope Can be <code>RegionConfigurationInfo.SCOPE_NONE</code>, <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, <code>RegionConfigurationInfo.SCOPE_MACHINE</code> and
   * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>
   * @throws IllegalArgumentException Thrown if the <code>scope</code> is not a valid constant
   */
  public void setInvalidationScope(byte scope) {
    this.invalidationScope = scope;
  }

  /**
   * Sets the configured name of the region
   *
   * @param name The name can be 40 characters long
   * @throws IllegalArgumentException Thrown if <code>name</code> is bigger than 40 characters
   * @throws NullPointerException Thrown if <code>name</code> is null
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the configured maximum total size for the region's cached objects
   *
   * @param size The maximum total object sizes in bytes
   * @throws IllegalArgumentException Thrown if <code>size</code> is non-positive
   */
  public void setSizeQuota(int size, byte level) {
    this.sizeQuota[level] = size;
  }

  /**
   * Sets the configured maximum total count of the region's cached objects
   *
   * @param count The maximum total count
   * @throws IllegalArgumentException Thrown if <code>count</code> is non-positive
   */
  public void setCountQouta(int count, byte level) {
    this.countQuota[level] = count;
  }

  /**
   * Sets the id for the cache region
   *
   * @param id The cluster-wide unique id of the region
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Sets the eviction policy bound to the cache region
   *
   * @param evictionPolicy The eviction policy that admin module has chosen for this region
   * @throws NullPointerException Thrown if <code>evictionPolicy</code> is null
   */
  public void setEvictionPolicy(EvictionPolicy evictionPolicy) {
    this.evictionPolicy = evictionPolicy;
  }

  /**
   * Sets the storage plugin bound to the cache region
   *
   * @param storagePlugin The storage plugin that admin module has chosen for this region
   * @throws NullPointerException Thrown if <code>storagePlugin</code> null
   */
  public void setStoragePlugin(StoragePlugin storagePlugin) {
    this.storagePlugin = storagePlugin;
  }

  /**
   * Gets the storage plugin bound to the cache region
   *
   * @return The storage plugin that is configured for this region
   * @throws NullPointerException Thrown if <code>storagePlugin</code> null
   */
  public StoragePlugin getStoragePlugin() {
    return storagePlugin;
  }

  /**
   * Gets the eviction policy bound to the cache region
   *
   * @return The eviction policy that is configured for this region
   */
  public EvictionPolicy getEvictionPolicy() {
    return evictionPolicy;
  }

  /**
   * Sets the direct invalidation mode set to the region. If the mode is set to true,
   * the cached objects that are implementing <code>InvalidationListener</code> interface
   * will be invoked upon invalidation
   *
   */
  public void setDirectObjectInvalidationMode(boolean flag) {
    this.directInvalidation = flag;
  }

  /**
   * Sets the trace mode of the region. If true - all operations will be traced.
   *
   * @param flag Trace mode flag
   */
  public void getTraceMode(boolean flag) {
    this.trace = flag;
  }

  /**
   * Sets the logging mode of the region. If true - all operations will be logged.
   *
   * @param flag Logging mode flag
   */
  public void setLoggingMode(boolean flag) {
    this.logging = flag;
  }

  /**
   * Returns the configured scope of the region.
   *
   * @return Can be <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, <code>RegionConfigurationInfo.SCOPE_MACHINE</code> and
   * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>
   */
  public byte getRegionScope() {
    return regionScope;
  }

  /**
   * Returns the automatic invalidation scope configured for the region.
   *
   * @return Can be <code>RegionConfigurationInfo.SCOPE_NONE</code>, <code>RegionConfigurationInfo.SCOPE_LOCAL</code>, <code>RegionConfigurationInfo.SCOPE_MACHINE</code> and
   * <code>RegionConfigurationInfo.SCOPE_CLUSTER</code>
   */
  public byte getInvalidationScope() {
    return invalidationScope;
  }

  /**
   * Returns the configured name of the region
   *
   * @return The name can be 40 characters long
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the configured maximum total size for the region's cached objects
   *
   * @return The maximum total object sizes in bytes
   */
  public int getSizeQuota(byte level) {
    return sizeQuota[level];
  }

  /**
   * Returns the configured maximum total count of the region's cached objects
   *
   * @param level The level that the quota parameter will be returned for. There are three
   *
   * @return The maximum total count
   */
  public int getCountQuota(byte level) {
    return countQuota[level];
  }

  /**
   * Returns the id for the cache region
   *
   * @return The cluster-wide unique id of the region
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the direct invalidation mode set to the region. If the mode is set to true,
   * the cached objects that are implementing <code>InvalidationListener</code> interface
   * will be invoked upon invalidation
   *
   * @return Direct object invalidation mode
   */
  public boolean getDirectObjectInvalidationMode() {
    return directInvalidation;
  }

  /**
   * Returns the trace mode of the region. If true - all operations will be traced.
   *
   * @return Trace mode flag
   */
  public boolean getTraceMode() {
    return trace;
  }

  /**
   * Returns the logging mode of the region. If true - all operations will be logged.
   *
   * @return Logging mode flag
   */
  public boolean getLoggingMode() {
    return logging;
  }

  public void setSynchronous(boolean flag) {
    this.synchronous = flag;
  }

  public boolean isSynchronous() {
    return synchronous;
  }

  public String toString() {
    StringBuffer result = new StringBuffer("\n[CacheRegion] {");
    result.append("\n  Name                : " + name);
    result.append("\n  ID                  : " + id);
    result.append("\n  Size Quota L1       : " + sizeQuota[START_OF_EVICTION_THRESHOLD]);
    result.append("\n  Size Quota L2       : " + sizeQuota[UPPER_LIMIT_THRESHOLD]);
    result.append("\n  Size Quota L3       : " + sizeQuota[CRITICAL_LIMIT_THRESHOLD]);
    result.append("\n  Count Quota L1      : " + countQuota[START_OF_EVICTION_THRESHOLD]);
    result.append("\n  Count Quota L2      : " + countQuota[UPPER_LIMIT_THRESHOLD]);
    result.append("\n  Count Quota L3      : " + countQuota[CRITICAL_LIMIT_THRESHOLD]);

    result.append("\n  Region Scope        : ");
    if (regionScope == SCOPE_CLUSTER) result.append("CLUSTER");
    else if (regionScope == SCOPE_INSTANCE) result.append("MACHINE");
    else if (regionScope == SCOPE_LOCAL) result.append("LOCAL");
    else result.append("NONE");

    result.append("\n  Invalidation Scope  : ");
    if (invalidationScope == SCOPE_CLUSTER) result.append("CLUSTER");
    else if (invalidationScope == SCOPE_INSTANCE) result.append("MACHINE");
    else if (invalidationScope == SCOPE_LOCAL) result.append("LOCAL");
    else result.append("NONE");

    result.append("\n  Synchronous         : " + synchronous);
    result.append("\n  Direct Invalidation : " + directInvalidation);
    result.append("\n  Trace Mode          : " + trace);
    result.append("\n  Logging Mode        : " + logging);
    result.append("\n  Put Is Modification : " + putIsModification);
    result.append("\n  Sender Is Receiver  : " + senderIsReceiver);

    result.append("\n  Storage Plugin      : " + storagePlugin.getName());
    result.append("\n  Eviction Policy     : " + evictionPolicy.getName());
    
    result.append("\n}");
    return result.toString();
  }

	public boolean isClientDependent() {
		return false;
	}

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public Properties getProperties() {
    return properties;
  }

  /**
   * Checks wheather the cache region is configured not to receive its local 
   * notifications
   * 
   * @param flag the mode type <coede>true<code> means the sender will receive its notification messages, <code>false<code> the sender will receive its notifications. 
   */
  public void setSenderIsReceiverMode(boolean flag) {
  	senderIsReceiver = flag;
  }
	
  /**
   * Checks wheather put operation is treated as a modification even if the
   * operation did not overwrite an already cached object
   * 
   * @param flag the mode type <coede>true<code> means put is always a modification operation, <code>false<code> is not.  
   */
  public void setPutIsModificationMode(boolean flag) {
  	putIsModification = flag;
  }
	
  /**
   * Checks wheather the cache region is configured not to receive its local 
   * notifications
   * 
   * @return
   */
  public boolean getSenderIsReceiverMode() {
	return senderIsReceiver; 
  }
	
  /**
   * Checks wheather put operation is treated as a modification even if the
   * operation did not overwrite an already cached object 
   * 
   * @return
   */
  public boolean getPutIsModificationMode() {
    return putIsModification;
  }

//  /**
//   * 
//   */
//  public void setNotifyOneListenerPerInstance(boolean flag) {
//	// TODO Auto-generated method stub
//  }
	
}
