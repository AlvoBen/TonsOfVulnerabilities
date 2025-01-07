package com.sap.engine.cache.core.impl;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.cache.admin.Monitor;
import com.sap.engine.cache.admin.impl.ElementConfigurationImpl;
import com.sap.engine.cache.communication.Notification;
import com.sap.engine.cache.communication.NotificationListener;
import com.sap.engine.cache.util.SoftHashMapEntry;
import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.AttributeNames;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.ElementConfiguration;
import com.sap.util.cache.ElementConfigurationInfo;
import com.sap.util.cache.InvalidationListener;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.HolderRuntimeException;
import com.sap.util.cache.exception.WriteException;
import com.sap.util.cache.spi.policy.ElementAttributes;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * Created by IntelliJ IDEA.
 * User: Petio-P
 * Date: Mar 15, 2004
 * Time: 11:10:39 AM
 * To change this template use Options | File Templates.
 */
public class CacheGroupImpl implements CacheGroup {

  private TimeInvalidationEntity timeInvalidationEntity;
  private String groupName = null;
  private HashMap groupAttribute = null;
  protected StoragePlugin storage = null;
  private EvictionPolicy eviction = null;
  private Monitor monitor = null;
  protected Notification notification = null;
  protected CacheRegionImpl region = null;
  private boolean synchronous = false;
  private boolean suppressInvalidation = false;
  protected HashSet keys = null;
  private EvictionWorker evictionWorker = null;
  private ElementConfiguration configuration;
  private boolean defaultGroup;
  private CacheFacadeImpl facade;
  private int regionId;
  private boolean directInvalidation;
  private String regionPrefix = null;

  protected Object eraseSync = null;
  
  private boolean firstTimer = true;
  
  public CacheGroupImpl(String groupName, Notification notification, CacheRegionImpl region) {
    this.groupName = groupName;
    this.regionPrefix = region.getRegionConfigurationInfo().getName() + "@";
    this.notification = notification;
    this.region = region;
    this.regionId = region.getRegionConfigurationInfo().getId();
    this.storage = region.getRegionConfiguration().getStoragePlugin();
    this.eviction = region.getRegionConfiguration().getEvictionPolicy();
    this.evictionWorker = region.evictionWorker;
    this.timeInvalidationEntity = region.timeInvalidationEntity;
    this.monitor = region.getMonitor();
    this.directInvalidation = region.getRegionConfigurationInfo().getDirectObjectInvalidationMode();
    this.eraseSync = region;
    if (groupName != null) {
      if (!storage.existsGroup(groupName)) {
        storage.insertGroup(groupName);
      }
      groupAttribute = new HashMap();
      groupAttribute.put(AttributeNames._GROUP_NAME, this.groupName);
      Set keySet = null;
      keySet = storage.keySet();
      Iterator keysIterator = keySet.iterator();
      while (keysIterator.hasNext()) {
        String key = (String) keysIterator.next();
        Map systemAttributes = storage.getSystemAttributes(key, false);
        if (systemAttributes != null) {
          String gName = (String) systemAttributes.get(AttributeNames._GROUP_NAME);
        }
      }

      facade = (CacheFacadeImpl) region.getCacheFacade();
      // use the same sync object (this was previously a bug!!!)
      defaultGroup = false;
    } else {
      Set keySet = null;
      keySet = storage.keySet();
      Iterator keysIterator = keySet.iterator();
      while (keysIterator.hasNext()) {
        String key = (String) keysIterator.next();
        Map systemAttributes = storage.getSystemAttributes(key, false);
        if (systemAttributes != null) {
          String gName = (String) systemAttributes.get(AttributeNames._GROUP_NAME);
          if (gName != null) {
            if (region.groups.get(gName) == null) {
              region.groups.put(gName, "<unprepared>");
            }
          }
        }
      }
      facade = (CacheFacadeImpl) this;
      defaultGroup = true;
    }
    suppressInvalidation = (region.getRegionConfigurationInfo().getInvalidationScope() == 0);
    synchronous = region.getRegionConfigurationInfo().isSynchronous();
    configuration = new ElementConfigurationImpl(CacheRegionImpl.background);
    ElementConfiguration defaultConfiguration = region.getElementConfiguration();
    configuration.setAbsEvictionTime(defaultConfiguration.getAbsEvictionTime());
    configuration.setTimeToLive(defaultConfiguration.getTimeToLive());
  }

  /**
   * Returns the element configuration of the cache region that the cache user is using.
   *
   * @return Cache user cache region element configuration.
   */
  public ElementConfigurationInfo getElementConfigurationInfo() {
    return getElementConfiguration(); // return same object as getElementConfiguration
  }

  /**
   * Returns the element configuration of specific cache group in the cache region that the cache user is using.
   *
   * @return Cache user cache region group element configuration.
   */
  public ElementConfiguration getElementConfiguration() {
    return configuration;
  }

  /**
   * A simple put in the cache region. The behavior of this put is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on resources
   * @throws NullPointerException if <code>key</code> or <code>cachedObject</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject) throws CacheException {
    put(key, cachedObject, null, synchronous, suppressInvalidation);
  }

  /**
   * A simple put in the cache region. The behavior of this put is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The objects keys, and objects themselves. Later cache users can get a cached object using its key
   * @throws NullPointerException if <code>key</code> or <code>cachedObject</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject) throws CacheException {
    putBundle(keyToObject,  null, synchronous, suppressInvalidation);
  }

  /**
   * A put in the cache region. The behavior of this put is defined by the two flag parameters. These
   * parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on resources
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * @param suppressInvalidation If true, no invalidation will be made, no listener on other node will receive any
   * events about putting / modification.
   * @throws NullPointerException if <code>key</code> or <code>cachedObject</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    put(key, cachedObject, null, synchronous, suppressInvalidation ? (byte)0 : (byte)-1);
  }

  public void put(String key, Object cachedObject, boolean synchronous, byte invalidationScope) throws CacheException {
    put(key, cachedObject, null, synchronous, invalidationScope);
  }

  /**
   * A put in the cache region. The behavior of this put is defined by the two flag parameters. These
   * parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The object key, and objects themselves. Later cache users can get a cached object using its key
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * @param suppressInvalidation If true, no invalidation will be made, no listener on other node will receive any
   * events about putting / modification.
   * @throws NullPointerException if <code>key</code> or <code>cachedObject</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    putBundle(keyToObject, null, synchronous, suppressInvalidation ? (byte)0 : (byte)-1);
  }

  public void putBundle(Map keyToObject, boolean synchronous, byte invalidationScope) throws CacheException {
    putBundle(keyToObject, null, synchronous, invalidationScope);
  }

  /**
   * A put in the cache region with cached object attributes. The behavior of this put is up to the region
   * <code>configuration</code>. To override the <code>configuration</code> cache users may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on eviction policy's decision
   * @param attributes The attributes that will be assigned to the cached object. Attributes can be separately
   * modified.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>attributes</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject, Map attributes) throws CacheException {
    put(key, cachedObject, attributes, synchronous, suppressInvalidation);
  }

  /**
   * A put in the cache region with cached object attributes. The behavior of this put is up to the region
   * <code>configuration</code>. To override the <code>configuration</code> cache users may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The object key, and objects themselves. Later cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the cached objects keys. Attributes can be separately
   * modified.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>attributes</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes) throws CacheException {
    putBundle(keyToObject, keyToAttributes, synchronous, suppressInvalidation);
  }

  /**
   * A put in the cache region cached object attributes. The behavior of this put is defined by the two flag parameters.
   * These parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on eviction policy's decision
   * @param attributes The attributes that will be assigned to the cached object. Attributes can be separately
   * modified.
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * events about putting / modification.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>attributes</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject, Map attributes, boolean synchronous, byte invalidationScope) throws CacheException {
    _put(key, cachedObject, groupAttribute, attributes, synchronous, invalidationScope, true);
  }

  /**
   * Return 
   * @return
   */
  public String getName() {
    return groupName; 
  }

  public void put(String key, Object cachedObject, Map attributes, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    if (suppressInvalidation) {
      put(key, cachedObject, attributes, synchronous, (byte)0);
    } else {
      put(key, cachedObject, attributes, synchronous, (byte)-1);
    }
  }
  
  private void _put(final String key, final Object cachedObject, Map systemAttributes, Map attributes, boolean synchronous, byte invalidationScope, boolean flush) throws CacheException {
    // generic put
    boolean modification = false;
    boolean newPUT = false;
    int oldSize = 0;
    int oldAttSize = 0;
    Object oldCachedObject = null;
    
    synchronized (eraseSync) {
      if (firstTimer && InternalRegionFactory.listener != null) {
        firstTimer = false;
        InternalRegionFactory.listener.onFirstUseCacheRegion(region.name, region);
      } 			 // stores in Hashtable could be not synchronized in the listener implementataion CacheManagerImpl      
      evictionWorker.active();   // has a synchronization by region in itself no need to be synchronizaed here
      if (storage.size() == 0) { // SCOPE = GROUP - NO SYNCHRONIZATION
        SoftHashMapEntry softGroup = new SoftHashMapEntry(groupName, this, region.groups, region.dungHill);
        region.groups.put(groupName, softGroup); // Make the groups Hashtable and do not synchronize
      } 			 // could be synchronized by groups... if 'groups' is Hashtable no synchronization
      if (storage.exists(key)) { // SCOPE = STORAGE - NO SYNCHRONIZATION
        modification = true;
        oldSize = storage.getSize(key); 			 // get from HashMap -> make it Hashtable
        oldAttSize = storage.getAttributesSize(key); // returns 0
        // save the old object for direct invalidation reasons
        if (directInvalidation) { // SCOPE = STORAGE
          oldCachedObject = storage.get(key, false); // 
          if (!(oldCachedObject instanceof InvalidationListener)) {
            oldCachedObject = null;
          }
        }
      } else {
      	newPUT = true;
      }
      if (groupAttribute != null) { // Scope = GROUP - Synchronize by 'groupAttribute'
        if (systemAttributes == null) {
          systemAttributes = new HashMap();
        }
        systemAttributes.putAll(groupAttribute); // the scope is the group, no need to synchronize on the region
      }
      
      final long ttl = configuration.getTimeToLive(); // group/facade TTL
      final long aet = configuration.getAbsEvictionTime(); // group/facade AET
      
      boolean setTTL = false;
      boolean setAET = false;
      
      if(ttl > 0) {
        if (attributes == null) {
          attributes = new HashMap();
//        The contract is to keep attribute values as String.
          attributes.put(AttributeNames.TTL, Long.toString(ttl));
          setTTL = true;
        } else if (attributes.get(AttributeNames.TTL) == null) {
//        The contract is to keep attribute values as String.
          attributes.put(AttributeNames.TTL, Long.toString(ttl));
          setTTL = true;
        }
      }
      
      if(aet > 0) {
        if (attributes == null) {
          attributes = new HashMap();
//        The contract is to keep attribute values as String.
          attributes.put(AttributeNames.AET, Long.toString(aet));
          setTTL = true;
        } else if (attributes.get(AttributeNames.AET) == null) {
//        The contract is to keep attribute values as String.
          attributes.put(AttributeNames.AET, Long.toString(aet));
          setTTL = true;
        }
      }

      _putInStorageRegEvictionAndTTL(key, cachedObject, systemAttributes, attributes, ttl, setTTL, aet, setAET);

      if (flush) {
        storage.flush(); // This is not synchronized in _putAttributes - does not need to be synchronized here too
      }
      if (modification) {
        monitor.onModify(oldSize, oldAttSize, storage.getSize(key), storage.getAttributesSize(key));
      } else {
        monitor.onPut(storage.getSize(key), storage.getAttributesSize(key), key.length() * 2);
      }
      if (invalidationScope == RegionConfigurationInfo.SCOPE_NONE) {
        return;
      }
      if (modification) { // SCOPE = NOTIFICATION
        notification.notify(region, storage, key, NotificationListener.EVENT_MODIFICATION, cachedObject, invalidationScope, synchronous);
        if (oldCachedObject != null) {
          InvalidationListener listener = (InvalidationListener) oldCachedObject;
          listener.invalidate(key, InvalidationListener.EVENT_MODIFICATION);
        }
      }
      if (newPUT && region.getRegionConfigurationInfo().getPutIsModificationMode()) {
      	notification.notify(region, storage, key, InvalidationListener.EVENT_PUT, cachedObject, invalidationScope, synchronous);
      }
    } // synchronization

  }
  
  private void _putInStorageRegEvictionAndTTL(String key, Object cachedObject, Map systemAttributes, Map attributes, long ttl, boolean setTTL, long aet, boolean setAET) throws CacheException {
    // only storage.put() throws CacheException in this method.
    if (systemAttributes != null) {
      storage.put(key, cachedObject, attributes, systemAttributes);
    } else {
      storage.put(key, cachedObject, attributes, false); 
    }
    
    if (attributes != null) {
      String token;
      if (!setTTL) {
        token = (String) attributes.get(AttributeNames.TTL);
        if (token != null) {
          try {
            ttl = Long.parseLong(token);
          } finally {
          }
        }
      }
      if (!setAET) {
        token = (String) attributes.get(AttributeNames.AET);
        if (token != null) {
          try {
            aet = Long.parseLong(token);
          } finally {
          }
        }
      }
    }
    
    ElementAttributes eAttr = new ElementAttributesImpl(storage.getSize(key), storage.getAttributesSize(key), ttl, aet);
    eviction.onPut(key, eAttr);
    
    if (aet == -1) {
      timeInvalidationEntity.registerTTL(key, ttl);
    } else if (ttl == -1) {
      timeInvalidationEntity.registerAET(key, aet);
    } else {
      if (System.currentTimeMillis() + ttl < aet) {
        timeInvalidationEntity.registerTTL(key, ttl);
      } else {
        timeInvalidationEntity.registerAET(key, aet);
      }
    }
  }

  /**
   * A put in the cache region cached object attributes. The behavior of this put is defined by the two flag parameters.
   * These parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The object key, and objects themselves. Later cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the cached objects keys. Attributes can be separately
   * modified.
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
      * events about putting / modification.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>attributes</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, boolean synchronous, byte invalidationScope) throws CacheException {
    Iterator keys = keyToObject.keySet().iterator();
    if (invalidationScope != RegionConfigurationInfo.SCOPE_NONE) {
      // begin message block in notification, this is needed to reduce traffic
      notification.beginEventBlock(region);
    }
    // iterate through the map and put objects without flushing the storage plugin
    while (keys.hasNext()) {
      String key = (String) keys.next();
      _put(
        key,
        keyToObject.get(key),
        groupAttribute,
        keyToAttributes == null ? null : (Map) keyToAttributes.get(key),
        false,
        invalidationScope,
        false);
    }
    storage.flush();
    if (invalidationScope != RegionConfigurationInfo.SCOPE_NONE) {
      // messages about all objects put will be sent as one message
      notification.endEventBlock(region, true, synchronous);
    }
  }

  public void putBundle(Map keyToObject, Map keyToAttributes, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    if (suppressInvalidation) {
      putBundle(keyToObject, keyToAttributes, synchronous, (byte)0);
    } else {
      putBundle(keyToObject, keyToAttributes, synchronous, (byte)-1);
    }
  }

  /**
   * Assigns attributes to a cached object key, disregarding of the existence of that cached object
   * (disregarding of predecessing puts). Attributes are overwritten if put second time. The behavior of this put is up
   * to the region <code>configuration</code>. To override the <code>configuration</code> cache users may use another put
   * method.
   *
   * @param key The object key that the attributes will be assigned to
   * @param attributes The attributes that will be assigned to the cached object
   * @throws NullPointerException if <code>key</code> or <code>attributes</code> is null
   */
  public void putAttributes(String key, Map attributes) throws CacheException {
    _putAttributes(key, attributes, true, true);
  }

  /**
   * Assigns attributes to a cached object key, disregarding of the existence of that cached object
   * (disregarding of predecessing puts). Attributes are overwritten if put second time. The behavior of this put is up
   * to the region <code>configuration</code>. To override the <code>configuration</code> cache users may use another put
   * method.
   *
   * @param keyToAttributes The attributes that will be assigned to the cached objects keys
   * @throws NullPointerException if <code>key</code> or <code>attributes</code> is null
   */
  public void putAttributesBundle(Map keyToAttributes) throws CacheException {
    Iterator keys = keyToAttributes.keySet().iterator();
    if (!suppressInvalidation) {
      // begin message block in notification, this is needed to reduce traffic
      notification.beginEventBlock(region);
    }
    // iterate through the map and put attributes without flushing the storage plugin
    while (keys.hasNext()) {
      String key = (String) keys.next();
      _putAttributes(key, (Map) keyToAttributes.get(key), false, true);
    }
    storage.flush();
    if (!suppressInvalidation) {
      // messages about all objects put will be sent as one message
      notification.endEventBlock(region, true, synchronous);
    }
  }
  
  private void _putAttributes(String key, Map attributes, boolean flush, boolean addToKeys) throws CacheException {
    // generic put attribute
    if (attributes != null) {
      Map _attributes = storage.getAttributes(key, true);
      if (_attributes == null) {
        _attributes = new HashMap();
      }
      _attributes.putAll(attributes);
      Map _systemAttributes = null;
      _systemAttributes = storage.getSystemAttributes(key, false);
      if (groupAttribute != null && (_systemAttributes == null || _systemAttributes.get(AttributeNames._GROUP_NAME) == null)) {
        Map temp = new HashMap();
        if (_systemAttributes != null) {
          temp.putAll(_systemAttributes);
        }
        temp.putAll(groupAttribute);
        
        _putStorageAndTiming(key, attributes, _attributes, temp);
      } else {
        _putStorageAndTiming(key, attributes, _attributes, null);
      }
      if (flush) {
        // this is used to notify the storage plugin about bulk operations
        storage.flush();
      }
    }
    // todo - notification about changes
  }
  
  private void _putStorageAndTiming(String key, Map newAttributes, Map mergedAttributes, Map systemAttributes) throws CacheException {
    synchronized (eraseSync) {
      
      long ttl = -1;
      long aet = -1;
      boolean setTiming = false;
      String token;
      Object value;
      
      if((value = newAttributes.get(AttributeNames.TTL)) != null) {
        try {
          token = (String) value;
          try {
            ttl = Long.parseLong(token);
            setTiming = true;
          } finally {
          }
        } catch (ClassCastException e) {
//        This additional check is done, because there are doubts, that someone puts attributes with Long values, but not String (as by contract).
          ttl = (Long) value;
          setTiming = true;
          mergedAttributes.put(AttributeNames.TTL, Long.toString(ttl));
          System.err.println("WARNING: Wrong type [Long] for [TTL] cache attribute in region [" +
              region.getRegionConfigurationInfo().getName() + "], key [" + key + "]. Stack trace follows: ");
          e.printStackTrace();
        }
      }
      
      if((value = newAttributes.get(AttributeNames.AET)) != null) {
        try {
          token = (String) value;
          try {
            aet = Long.parseLong(token);
            setTiming = true;
          } finally {
          }
        } catch (ClassCastException e) {
//        This additional check is done, because there are doubts, that someone puts attributes with Long values, but not String (as by contract).
          aet = (Long) value;
          setTiming = true;
          mergedAttributes.put(AttributeNames.AET, Long.toString(aet));
          System.err.println("WARNING: Wrong type [Long] for [AET] cache attribute in region [" +
              region.getRegionConfigurationInfo().getName() + "], key [" + key + "]. Stack trace follows: ");
          e.printStackTrace();
        }
      }
      
      if (systemAttributes != null) {
        storage.putAttributes(key, mergedAttributes, systemAttributes);
      } else {
        storage.putAttributes(key, mergedAttributes);
      }
      
      if (setTiming) {
//      Each change of timing attributes should be treated as new object put.
        ElementAttributes eAttr = new ElementAttributesImpl(storage.getSize(key), storage.getAttributesSize(key), ttl, aet);
        eviction.onPut(key, eAttr);
        
        if (aet == -1) {
          timeInvalidationEntity.registerTTL(key, ttl);
        } else if (ttl == -1) {
          timeInvalidationEntity.registerAET(key, aet);
        } else {
          if (System.currentTimeMillis() + ttl < aet) {
            timeInvalidationEntity.registerTTL(key, ttl);
          } else {
            timeInvalidationEntity.registerAET(key, aet);
          }
        }
      }
      
    }
  }

  public Object get(final String key, final boolean copy) {
    // generic get
//    final Object[] result = new Object[1];
//    if (region.getLockerHook() != null) {
//      try {
//        synchronized (eraseSync) {
//          region.execute(key, new Runnable() {
//            public void run() {
//              result[0] = storage.get(key, copy);
//              if (result[0] != null) eviction.onAccess(key);
//              monitor.onGet(result[0] != null);
//            }
//          });
//        }
//      } catch (CacheException e) {
//        // the exception will not be thrown
//        LogUtil.logT(e);
//        return null;
//      }
//    } else {
    Object result;
    synchronized (eraseSync) {
      if (firstTimer && InternalRegionFactory.listener != null) {
        firstTimer = false;
        InternalRegionFactory.listener.onFirstUseCacheRegion(region.name, region);
      }
      if (groupName == null) {
        result = storage.get(key, copy);
      } else {
        result = storage.get(groupName, key, copy);
      }
      if (result != null) eviction.onAccess(key);
    }
    monitor.onGet(result != null);
    return result;
  }

  /**
   * A simple get from the cache region.
   *
   * @param keys The keys of the cached objects that will be returned
   * @param copy Denotes the type of the returned object - a copy or an actual reference to the original copy (if the
   * storage plugin supports copying of objects)
   * @return The cached objects keys mapped to cached objects themselves (if available).
   * @throws NullPointerException if <code>key</code> is null
   */
  public Map getBundle(Set keys, boolean copy) {
    Map result = new HashMap();
    Iterator targetKeys = keys.iterator();
    // iterate through the set and add objects to the result
    while (targetKeys.hasNext()) {
      String key = (String) targetKeys.next();
      Object cachedObject = get(key, copy);
      if (cachedObject != null) {
        result.put(key, cachedObject);
      }
    }
    return result;
  }

  /**
   * A simple get from the cache region.
   *
   * @param keys The keys of the cached objects that will be returned
   * @return The cached objects keys mapped to cached objects themselves (if available).
   * @throws NullPointerException if <code>key</code> is null
   */
  public Map getBundle(Set keys) {
    return getBundle(keys, false);
  }

  /**
   * A simple get from the cache region. The behavior of this get is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another get method.
   *
   * @param key The key of the cached object that will be returned
   * @return The cached object (if available). It's up to the <code>configuration</code> if cache readers will be polled.
   * @throws NullPointerException if <code>key</code> is null
   */
  public Object get(String key) {
    return get(key, false);
  }

  /**
   * Gets the attributes bound to specific cached object key.
   *
   * @param key The key that cache user wants to get attributes of.
   * @param copy If true and the storage plugin supports it, a copy will be returned
   * @return The attributes bound to the cached object key or null if there are no attributes bound to the key
   * @throws NullPointerException if <code>key</code> is null
   */
  public Map getAttributes(String key, boolean copy) {
    // generic getAttribute
    Map attributes = storage.getAttributes(key, copy);
    return attributes;
  }

  public Map getSystemAttributes(String key, boolean copy) {
    // generic getAttribute
    Map attributes = storage.getSystemAttributes(key, copy);
    return attributes;
  }

  /**
   * Gets the attributes bound to specific cached object key.
   *
   * @param key The key that cache user wants to get attributes of.
   * @return The attributes bound to the cached object key or null if there are no attributes bound to the key
   * @throws NullPointerException if <code>key</code> is null
   */
  public Map getAttributes(String key) {
    // generic getAttribute
    return getAttributes(key, false);
  }

  public Map getSystemAttributes(String key) {
    // generic getAttribute
    return getSystemAttributes(key, false);
  }

  /**
   * A simple remove from the cache region. The behavior of this removal is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another remove method.
   *
   * @param key The object key of the cached object that cache user wants removed.
   * @throws NullPointerException if <code>key</code> is null
   * @throws CacheException if the remove did not succeed
   */
  public void remove(String key) {
    remove(key, synchronous, suppressInvalidation);
  }

  /**
   * A simple remove from the cache region. The behavior of this removal is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another remove method.
   *
   * @param keySet The objects keys of the cached objects that cache user wants removed.
   * @throws NullPointerException if <code>key</code> is null
   * @throws CacheException if the remove did not succeed
   */
  public void removeBundle(Set keySet) {
    removeBundle(keySet, synchronous, suppressInvalidation);
  }

  /**
   * A remove from the cache region. No or several objects will be removed depending on the attributes pattern
   * specified. If the pattern is a sub set of the attributes bound to an object key, the cached object
   * with that key is considered applying to the pattern and will be removed.
   * The behavior of this removal is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another remove method.
   *
   * @param attributes The attributes that represent a pattern for objects targeted to be removed
   */
  public void remove(Map attributes) {
    remove(attributes, synchronous, suppressInvalidation);
  }

  public void remove(String key, boolean synchronous, boolean suppressInvalidation) {
    remove(key, synchronous, suppressInvalidation ? (byte)0 : (byte)-1);
  }

  /**
   * A remove from the cache region. The behavior of this removal is defined by the two flag parameters. These
   * parameters override the region <code>configuration</code>.
   *
   * @param key The object key of the cached object that cache user wants removed.
   * @param synchronous If true, the invalidation and the removal will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * events about removal.
   * @throws NullPointerException if <code>key</code> is null
   */
  public void remove(String key, boolean synchronous, byte invalidationScope) {
    if (key != null) {
      if ("".equals(key)) {
        return;
      }
      int keyLength = key.length();
      char lastChar = key.charAt(keyLength - 1);
      if (lastChar == '*') {
        Set keysToRemove = new HashSet();
        Iterator keysIterator = keySet().iterator();
        key = key.substring(0, keyLength - 1);
        if (keyLength > 1) {
          if (key.charAt(keyLength - 2) == '*') {
            key = key.substring(0, keyLength - 2);
            while (keysIterator.hasNext()) {
              String currentKey = (String) keysIterator.next();
              if (currentKey.startsWith(key)) {
                keysToRemove.add(currentKey);
              }
            }
          } else {
            int lastSeparatorIndex = key.lastIndexOf('/');
            while (keysIterator.hasNext()) {
              String currentKey = (String) keysIterator.next();
              if ((currentKey.startsWith(key)) && (currentKey.lastIndexOf('/') == lastSeparatorIndex )) {
                keysToRemove.add(currentKey);
              }
            }
          }
        } else {
          while (keysIterator.hasNext()) {
            String currentKey = (String) keysIterator.next();
            int separatorIndex = currentKey.indexOf('/');
            if (separatorIndex == -1) {
              keysToRemove.add(currentKey);
            }
          }
        }
        Iterator removeIterator = keysToRemove.iterator();
        if (!suppressInvalidation) {
          // begin message block in notification, this is needed to reduce traffic
          notification.beginEventBlock(region);
        }
        while (removeIterator.hasNext()) {
          String keyToRemove = (String) removeIterator.next();
          _remove(keyToRemove, synchronous, invalidationScope, false);
        }
        try {
          storage.flush();
        } catch (CacheException e) {
          LogUtil.logT(e);
        }
        if (invalidationScope != RegionConfigurationInfo.SCOPE_NONE) {
          // messages about all objects removed will be sent as one message
          notification.endEventBlock(region, true, synchronous);
        }
      } else {
        _remove(key, synchronous, invalidationScope, true);
      }
    } else {
//      throw new NullPointerException("Cache object key cannot be null");
    }
  }

  public void _remove(final String key, boolean synchronous, byte invalidationScope, final boolean flush) {
    // generic remove
    int oldSize;
    int oldAttSize;
    Object oldCachedObject = null;
    synchronized (eraseSync) {
      if (firstTimer && InternalRegionFactory.listener != null) {
        firstTimer = false;
        InternalRegionFactory.listener.onFirstUseCacheRegion(region.name, region);
      }      
      timeInvalidationEntity.removeRegistered(key);
      if (storage.size() == 0) {
        SoftHashMapEntry softGroup = new SoftHashMapEntry(groupName, this, region.groups, region.dungHill);
        region.groups.put(groupName, softGroup);
      } // smells like a candidate to be removed - if hte group does not exist and you try to remove something from it you create the group, but as a result do not remove anything - this block allows creating a group on remove metod call
      String groupName = this.groupName; // ridiculous groupName = null <=> defaultGroup = true
      if (defaultGroup) { // can't get the idea
        Map systemAttributes = getSystemAttributes(key);
        if (systemAttributes != null) {
          groupName = (String) systemAttributes.get(AttributeNames._GROUP_NAME);
        }
      }
      oldSize = storage.getSize(key);
      oldAttSize = storage.getAttributesSize(key);
      oldCachedObject = groupName == null ? storage.get(key, false) : storage.get(groupName, key, false);
      boolean existed = false;
      if (oldCachedObject != null) {
        existed = true;
      }
      if (directInvalidation) {
        if (!(oldCachedObject instanceof InvalidationListener)) {
          oldCachedObject = null;
        }
      } else {
        oldCachedObject = null;
      }
      try {
        if (groupName == null) {
          storage.remove(key);
        } else {
          storage.remove(groupName, key);
        }
//        Useless call. StoragePlugin removes attributes on previous row. _putAttributes() ignores null attributes.
//          _putAttributes(key, null, false, false);
        if (flush) {
          storage.flush();
        }
        eviction.onRemove(key);
      } catch (CacheException e) {
        LogUtil.logTInfo(e);
      }
      if (existed) monitor.onRemove(oldSize, oldAttSize, key.length() * 2);
      if (invalidationScope != RegionConfigurationInfo.SCOPE_NONE) {
        notification.notify(region, storage, key, NotificationListener.EVENT_REMOVAL, null, invalidationScope, synchronous);
        if (oldCachedObject != null) {
          InvalidationListener listener = (InvalidationListener) oldCachedObject;
          listener.invalidate(key, InvalidationListener.EVENT_REMOVAL);
        }
      }
    }
  }

  public void evict(String key) throws CacheException {
    // delegate eviction to storage plugin, add monitoring info and maintain group keys
    int oldSize;
    int oldAttSize;
    Object oldCachedObject = null;
    synchronized (eraseSync) {
      timeInvalidationEntity.removeRegistered(key);
      String groupName = null;
      if (defaultGroup) {
        Map attributes = null;
        attributes = getSystemAttributes(key);
        if (attributes != null) {
          groupName = (String) attributes.get(AttributeNames._GROUP_NAME);
        }
      }
      oldSize = storage.getSize(key);
      oldAttSize = storage.getAttributesSize(key);
      oldCachedObject = storage.get(key, false);
      boolean existed = false;
      if (oldCachedObject != null) {
        existed = true;
      }
      if (directInvalidation) {
        if (!(oldCachedObject instanceof InvalidationListener)) {
          oldCachedObject = null;
        }
      } else {
        oldCachedObject = null;
      }
      eviction.onRemove(key);
      storage.evict(key);
      storage.flush();
      if (existed) monitor.onEvict(oldSize, oldAttSize, key.length() * 2);
      if (oldCachedObject != null) {
        InvalidationListener listener = (InvalidationListener) oldCachedObject;
        listener.invalidate(key, InvalidationListener.EVENT_INVALIDATION);
      }
    }
  }

  public void invalidate(String key) throws CacheException {
    // delegate invalidation to storage plugin, add monitoring info and maintain group keys
    int oldSize;
    int oldAttSize;
    Object oldCachedObject = null;
    synchronized (eraseSync) {
      if (firstTimer && InternalRegionFactory.listener != null) {
        firstTimer = false;
        InternalRegionFactory.listener.onFirstUseCacheRegion(region.name, region);
      }       
      timeInvalidationEntity.removeRegistered(key);
      String groupName = null;
      if (defaultGroup) {
        Map attributes = null;
        attributes = getSystemAttributes(key);
        if (attributes != null) {
          groupName = (String) attributes.get(AttributeNames._GROUP_NAME);
        }
      }
      oldSize = storage.getSize(key);
      oldAttSize = storage.getAttributesSize(key);
      oldCachedObject = storage.get(key, false);
      boolean existed = false;
      if (oldCachedObject != null) {
        existed = true;
      }
      if (directInvalidation) {
        if (!(oldCachedObject instanceof InvalidationListener)) {
          oldCachedObject = null;
        }
      } else {
        oldCachedObject = null;
      }

      eviction.onInvalidate(key);
      storage.invalidate(key);
      storage.flush();

      if (existed) monitor.onRemove(oldSize, oldAttSize, key.length() * 2);
      if (!region.getRegionConfigurationInfo().getSenderIsReceiverMode()) {
      	notification.notify(region, storage, key, NotificationListener.EVENT_INVALIDATION, null, RegionConfigurationInfo.SCOPE_CLUSTER, synchronous);
      }
      if (oldCachedObject != null) {
        InvalidationListener listener = (InvalidationListener) oldCachedObject;
        listener.invalidate(key, InvalidationListener.EVENT_INVALIDATION);
      }
    }
  }

  public void removeBundle(Set keySet, boolean synchronous, boolean suppressInvalidation) {
    removeBundle(keySet, synchronous, suppressInvalidation ? (byte)0 : (byte)-1);
  }
  /**
   * A remove from the cache region. The behavior of this removal is defined by the two flag parameters. These
   * parameters override the region <code>configuration</code>.
   *
   * @param keySet The objects keys of the cached objects that cache user wants removed.
   * @param synchronous If true, the invalidation and the removal will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * events about removal.
   * @throws NullPointerException if <code>key</code> is null
   */
  public void removeBundle(Set keySet, boolean synchronous, byte invalidationScope) {
    Iterator keys = keySet.iterator();
    if (!suppressInvalidation) {
      // begin message block in notification, this is needed to reduce traffic
      notification.beginEventBlock(region);
    }
    // iterate and remove without flushing the storage plugin
    while (keys.hasNext()) {
      String key = (String) keys.next();
      _remove(key, synchronous, invalidationScope, false);
    }
    try {
      storage.flush();
    } catch (CacheException e) {
      LogUtil.logTInfo(e);
    }
    if (invalidationScope != RegionConfigurationInfo.SCOPE_NONE) {
      // messages about all objects removed will be sent as one message
      notification.endEventBlock(region, true, synchronous);
    }
  }

  public void remove(Map attributes, boolean synchronous, boolean suppressInvalidation) {
    remove(attributes, synchronous, suppressInvalidation ? (byte)0 : (byte)-1);
  }
  /**
   * A remove from the cache region. No or several objects will be removed depending on the attributes pattern
   * specified. If the pattern is a sub set of the attributes bound to an object key, the cached object
   * with that key is considered applying to the pattern and will be removed.
   * The behavior of this removal is defined by the two flag parameters. These parameters override
   * the region <code>configuration</code>.
   *
   * @param attributes The attributes that represent a pattern for objects targeted to be removed
   * @param synchronous If true, the invalidation and the removal will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
      * events about removal.
   * @throws NullPointerException if <code>attributes</code> is null
   * @throws CacheException if the remove did not succeed
   */
  public void remove(Map attributes, boolean synchronous, byte invalidationScope) {
    // traverse subgroups if needed
    Map subgroupAttributes = storage.getSystemAttributes("#(" + groupName + ")", true);
    if (subgroupAttributes != null) {
      Iterator iter = subgroupAttributes.keySet().iterator();
      while (iter.hasNext()) {
        String subgroupName = (String)iter.next();
        CacheGroup cacheGroup = region.getCacheGroup(subgroupName);
        cacheGroup.remove(attributes, synchronous, invalidationScope);
      }
    }
    // remove with pattern
    Iterator keys = keySet().iterator();
    Set pattern = attributes == null ? null : attributes.keySet();
    if (invalidationScope != RegionConfigurationInfo.SCOPE_NONE) {
      // begin message block in notification, this is needed to reduce traffic
      notification.beginEventBlock(region);
    }

    // iterate through keys in the group
    while (keys.hasNext()) {
      String key = (String) keys.next();
      boolean comply = true;
      // if pattern == null, all objects in the group comply
      if (pattern != null) {
        // get attributes from storage, todo - see if it is feasible to keep attributes
        Map targetAttributes = storage.getAttributes(key, false);
        Iterator patternNames = pattern.iterator();
        // iterate through pattern, see if all attributes in pattern are attributes of objects
        while (patternNames.hasNext()) {
          String name = (String) patternNames.next();
          String targetValue = (String) targetAttributes.get(name);
          if (!(attributes.get(name).equals(targetValue))) {
            comply = false;
            break;
          }
        }
      }
      if (comply) {
        // remove without flushing the storage plugin
        _remove(key, synchronous, invalidationScope, false);
      }
    }
    try {
      storage.flush();
    } catch (CacheException e) {
      LogUtil.logTInfo(e);
    }
    if (!suppressInvalidation) {
      // messages about all objects removed will be sent as one message
      notification.endEventBlock(region, true, synchronous);
    }
  }

  /**
   * Returns true if the region contains no cached objects
   *
   * @return true if the region contains no cached objects
   */
  public boolean isEmpty() {
    synchronized (eraseSync) {
      if (groupName == null) {
        return storage.isEmpty();
      } else {
        return storage.isEmpty(groupName);
      }
    }
  }

  /**
   * Returns true if the region contains a cached object bound to the specified cached object key
   *
   * @param key The cached object key key whose presence in the region is to be tested.
   * @return true if the region contains a contains a cached object bound to the specified cached object key
   * @throws NullPointerException if <code>key</code> is null
   */
  public boolean containsKey(String key) {
    synchronized (eraseSync) {
      if (groupName == null) {
        return storage.exists(key);
      } else {
        return storage.exists(groupName, key);
      }
    }
  }

  /**
   * Removes all cached objects from the group/region
   *
   */
  public void clear() {
    Set keysToRemove;
    synchronized (eraseSync) {
      if (groupName == null) {
        keysToRemove = storage.keySet();
      } else {
        keysToRemove = storage.keySet(groupName);
      }
      Iterator removeIterator = keysToRemove.iterator();
      if (!suppressInvalidation) {
        // begin message block in notification, this is needed to reduce traffic
        notification.beginEventBlock(region);
      }
      while (removeIterator.hasNext()) {
        String keyToRemove = (String) removeIterator.next();
        timeInvalidationEntity.removeRegistered(keyToRemove);
        _remove(keyToRemove, synchronous, region.getRegionConfigurationInfo().getInvalidationScope(), false);
      }
      try {
        storage.flush();
      } catch (CacheException e) {
        LogUtil.logTInfo(e);
      }
      if (region.getRegionConfigurationInfo().getInvalidationScope() != RegionConfigurationInfo.SCOPE_NONE) {
        // messages about all objects removed will be sent as one message
        notification.endEventBlock(region, true, synchronous);
      }
    }
  }

  /**
   * Returns a set of the keys contained in the region.  The key set is a copy, so operations over the returned set
   * do not reflect in the region and vice-versa.
   *
   * @return a set of the keys contained in the region.
   */
  public Set keySet() {
    try {
      final Object[] keys;
      synchronized (eraseSync) {
        Set keySet = null;
        if (groupName == null) {
          keySet = storage.keySet();
        } else {
          keySet = storage.keySet(groupName);
        }
        keys = keySet.toArray();
      }
      return new Set() {

        Object[] localReference = keys;

        public int size() {
          return localReference.length;
        }

        public boolean isEmpty() {
          return localReference.length == 0;
        }

        public boolean contains(Object o) {
          for (int i = 0; i < localReference.length; i++) {
            if (o.equals(localReference[i])) {
              return true;
            }
          }
          return false;
        }

        public Iterator iterator() {
          return new Iterator() {

            int cnt = 0;

            public boolean hasNext() {
              return cnt < localReference.length;
            }

            public Object next() {
              cnt++;
              return localReference[cnt - 1];
            }

            public void remove() {
              cnt++;
            }

          };
        }

        public Object[] toArray() {
          return localReference;
        }

        public Object[] toArray(Object a[]) {
          String[] result = new String[localReference.length];
          System.arraycopy(localReference, 0, result, 0, localReference.length);
          return result;
        }

        public boolean add(Object o) {
          return false;
        }

        public boolean remove(Object o) {
          return false;
        }

        public boolean containsAll(Collection c) {
          Iterator cIterator = c.iterator();
          while (cIterator.hasNext()) {
            Object o = cIterator.next();
            if (!contains(o)) {
              return false;
            }
          }
          return true;
        }

        public boolean addAll(Collection c) {
          return false;
        }

        public boolean retainAll(Collection c) {
          return false;
        }

        public boolean removeAll(Collection c) {
          return false;
        }

        public void clear() {
        }

      };
    } catch (ConcurrentModificationException e) {
      LogUtil.logTInfo(e);
      return keySet();
    } catch (ArrayIndexOutOfBoundsException e) {
      LogUtil.logTInfo(e);
      return keySet();
    }
  }

  /**
   * Returns a collection of the cached objects contained in the region.  The collection is a copy, so operations
   * over the returned collection do not reflect in the region and vice-versa.
   *
   * @return a collection of the cached objects contained in the region.
   */
  public Collection values() {
    HashSet values = new HashSet();
    Iterator keys = this.keySet().iterator();
    // iterate through keys in the group and add cached objects to the result
    while (keys.hasNext()) {
      String key = (String) keys.next();
      values.add(get(key));
    }
    return values;
  }

  public void addChild(String childGroupName) throws CacheException {
    if (groupName != null) {
      if (!groupName.equals(childGroupName)) {
        Map attributes = storage.getSystemAttributes("#(" + groupName + ")", true);
        if (attributes == null) {
          attributes = new HashMap();
        }
        attributes.put(childGroupName, childGroupName);
        attributes.put(AttributeNames._GROUP_NAME, groupName);
        synchronized (eraseSync) {
          storage.putSystemAttributes("#(" + groupName + ")", attributes);
        }
      }
    }
  }

  public void removeChild(String childGroupName) throws CacheException {
    if (groupName != null) {
      if (!groupName.equals(childGroupName)) {
        Map attributes = storage.getSystemAttributes("#(" + groupName + ")", true);
        if (attributes != null) {
          attributes.remove(childGroupName);
          attributes.put(AttributeNames._GROUP_NAME, groupName);
          synchronized (eraseSync) {
            storage.putSystemAttributes("#(" + groupName + ")", attributes);
          }
        }
      }
    }
  }

  public Set getChildren() throws CacheException {
    //todo can return null!!! if we are not in group
    Set result = null;
    if (groupName != null) {
      Map attributes = storage.getSystemAttributes("#(" + groupName + ")", true);
      if (attributes != null) {
        attributes = (Map)((HashMap) attributes).clone(); // use clone in order to save group name attribute
        attributes.remove(AttributeNames._GROUP_NAME);
        result = new HashSet(attributes.values());
      } else {
        result = new HashSet();
      }
    }
    return result;
  }

  public CacheRegion getCacheRegion() {
	return region;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPut(java.lang.String, java.lang.Object)
   */
  public Object getOrPut(final String arg0, final Object arg1) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg1;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutBundle(java.util.Map)
   */
  public Map getOrPutBundle(final Map arg0) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
      if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg0;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPut(java.lang.String, java.lang.Object, boolean, boolean)
   */
  public Object getOrPut(final String arg0, final Object arg1, final boolean arg2, final boolean arg3) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1, arg2, arg3);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg1;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPut(java.lang.String, java.lang.Object, boolean, byte)
   */
  public Object getOrPut(final String arg0, final Object arg1, final boolean arg2, final byte arg3) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1, arg2, arg3);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg1;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutBundle(java.util.Map, boolean, boolean)
   */
  public Map getOrPutBundle(final Map arg0, final boolean arg1, final boolean arg2) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
    if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0, arg1, arg2);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg0;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutBundle(java.util.Map, boolean, byte)
   */
  public Map getOrPutBundle(final Map arg0, final boolean arg1, final byte arg2) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
    if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0, arg1, arg2);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg0;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPut(java.lang.String, java.lang.Object, java.util.Map)
   */
  public Object getOrPut(final String arg0, final Object arg1, final Map arg2) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1, arg2);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg1;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutBundle(java.util.Map, java.util.Map)
   */
  public Map getOrPutBundle(final Map arg0, final Map arg1) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
      if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0, arg1);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg0;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPut(java.lang.String, java.lang.Object, java.util.Map, boolean, boolean)
   */
  public Object getOrPut(final String arg0, final Object arg1, final Map arg2, final boolean arg3, final boolean arg4) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1, arg2, arg3, arg4);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg1;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPut(java.lang.String, java.lang.Object, java.util.Map, boolean, byte)
   */
  public Object getOrPut(final String arg0, final Object arg1, final Map arg2, final boolean arg3, final byte arg4) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1, arg2, arg3, arg4);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg1;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutBundle(java.util.Map, java.util.Map, boolean, boolean)
   */
  public Map getOrPutBundle(final Map arg0, final Map arg1, final boolean arg2, final boolean arg3) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
    if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0, arg1, arg2, arg3);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg0;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutBundle(java.util.Map, java.util.Map, boolean, byte)
   */
  public Map getOrPutBundle(final Map arg0, final Map arg1, final boolean arg2, final byte arg3) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
    if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0, arg1, arg2, arg3);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = arg0;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutAttributes(java.lang.String, java.util.Map)
   */
  public Map getOrPutAttributes(final String key, final Map attributes) throws CacheException {
    final Map[] result = new Map[1];
    result[0] = getAttributes(key);
    if (result[0] == null) {
      region.execute(key, new Runnable() {
        public void run() {
          result[0] = getAttributes(key);
          if (result[0] == null) {
            try {
              putAttributes(key, attributes);
            } catch (CacheException e) {
              throw new HolderRuntimeException(e);
            }
            result[0] = attributes;
          }
        }
      });
    }
    return result[0];
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheGroup#getOrPutAttributesBundle(java.util.Map)
   */
  public Map getOrPutAttributesBundle(final Map keyToAttributes) throws CacheException {
    final Map result = new HashMap();
    final Set keys = keyToAttributes.keySet();
    Iterator it = keys.iterator();
    while (it.hasNext()) {
      final String key = (String) it.next();
      final Object[] object = new Object[1];
      object[0] = getAttributes(key);
      if (object == null) {
        region.execute(key, new Runnable() {
          public void run() {
            object[0] = getAttributes(key);
            if (object == null) {
              try {
                putAttributes(key, (Map) keyToAttributes.get(key));
              } catch (CacheException e) {
                throw new HolderRuntimeException(e);
              }
              object[0] = keyToAttributes.get(key);
            }
          }
        });
      }
      result.put(key, object[0]);
    }
    return result;
  }

 }