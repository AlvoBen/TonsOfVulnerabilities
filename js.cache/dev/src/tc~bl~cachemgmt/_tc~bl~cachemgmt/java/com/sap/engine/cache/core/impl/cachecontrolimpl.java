package com.sap.engine.cache.core.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.cache.communication.Notification;
import com.sap.engine.cache.communication.NotificationListener;
import com.sap.util.cache.AttributeNames;
import com.sap.util.cache.CacheControl;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.InvalidationListener;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.spi.policy.EvictionPolicy;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author Petev, Petio, i024139
 */
public class CacheControlImpl implements CacheControl, NotificationListener {

  private Notification notification;
  private CacheRegionImpl region;
  private EvictionPolicy eviction;
  private StoragePlugin storage;
  private int regionId = -1;
  private CacheFacade facade = null;
  private boolean synchronous = false;

  private HashSet listeners;

  public CacheControlImpl(Notification notification, CacheRegionImpl region) {
    this.notification = notification;
    this.region = region;
    this.storage = region.getRegionConfiguration().getStoragePlugin();
    this.eviction = region.getRegionConfiguration().getEvictionPolicy();
    this.regionId = region.getRegionConfigurationInfo().getId();
    this.synchronous = region.getRegionConfigurationInfo().isSynchronous();
    this.facade = region.getCacheFacade();
    this.listeners = new HashSet();
    notification.registerListener(region, this);
  }

  /**
   * The method is invoked when an event about changes in the cache region is due.
   *
   * @param regionId the id of the region being changed
   * @param key      The cached object key of the object that has been changed.
   * @param event    The type of the invalidation, can be
   *                 <code>InvalidationListener.EVENT_INVALIDATION</code> - if an explicit invalidation was done
   *                 <code>InvalidationListener.EVENT_REMOVAL</code> - if invalidation due to remove invokation was done
   *                 <code>InvalidationListener.EVENT_MODIFICATION</code> - if invalidation due to successive put invokation was done
   */
  public void invalidate(int regionId, String key, byte event, byte invalidationScope) {
    if (event > 0) { // not local
      if (event == NotificationListener.EVENT_REMOVAL) {
        region.getCacheFacade().remove(key, false, true);
      }
    } else {
      event = (byte) -event;
    }
    if (event == NotificationListener.EVENT_INTERNAL_INVALIDATION ||
        event == NotificationListener.EVENT_INVALIDATION) {
      region.invalidate(key);
    }
    if (event != NotificationListener.EVENT_INTERNAL_INVALIDATION) {
      Iterator _listeners = listeners.iterator();
      while (_listeners.hasNext()) {
        InvalidationListener invalidationListener = (InvalidationListener) _listeners.next();
        invalidationListener.invalidate(key, event);
      }
    }
  }

  /**
   * The method is invoked when an event about changes in the cache region is due. This method is called
   * only when the storage plugin supports transportation of objects
   *
   * @param regionId     the id of the region being changed
   * @param key          The cached object key of the object that has been changed
   * @param cachedObject The value of the cached object that has been changed
   */
  public void invalidate(int regionId, String key, Object cachedObject, byte invalidationScope) {
    Iterator _listeners = listeners.iterator();
    while (_listeners.hasNext()) {
      InvalidationListener invalidationListener = (InvalidationListener) _listeners.next();
      invalidationListener.invalidate(key, cachedObject);
    }
    eviction.onInvalidate(key);
  }

  /**
   * The method is invoked when an event about changes in the cache region is due.
   *
   * @param key   The cached object key of the object that has been changed.
   * @param event The type of the invalidation, can be
   *              <code>InvalidationListener.EVENT_INVALIDATION</code> - if an explicit invalidation was done
   *              <code>InvalidationListener.EVENT_REMOVAL</code> - if invalidation due to remove invokation was done
   *              <code>InvalidationListener.EVENT_MODIFICATION</code> - if invalidation due to successive put invokation was done
   */
  public void invalidate(String key, byte event, byte invalidationScope) {
    invalidate(-1, key, event, invalidationScope);
  }

  /**
   * The method is invoked when an event about changes in the cache region is due. This method is called
   * only when the storage plugin supports transportation of objects
   *
   * @param key          The cached object key of the object that has been changed
   * @param cachedObject The value of the cached object that has been changed
   */
  public void invalidate(String key, Object cachedObject, byte invalidationScope) {
    invalidate(-1, key, cachedObject, invalidationScope);
  }


  /**
   * Registers invalidation listener for the region. <code>InvalidationListener</code>-s
   * will get events when objects are removed, modified or explicitly invalidated
   *
   * @param iListener The <code>InvalidationListener</code> instance that the cache user provides
   * @throws NullPointerException if <code>iListener</code> is null
   */
  public void registerInvalidationListener(InvalidationListener iListener) {
    listeners.add(iListener);
  }

  /**
   * Unregisters invalidation listener from the region.
   *
   * @param iListener The <code>InvalidationListener</code> instance that will be unregistered
   * @throws NullPointerException if <code>iListener</code> is null
   */
  public void unregisterInvalidationListener(InvalidationListener iListener) {
    listeners.remove(iListener);
  }

  /**
   * Explicitly invalidates an object that is bound to the specified key. Explicit invalidation will
   * provoke events firing.
   * The behavior of this invalidation is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another invalidate method.
   *
   * @param key The key that cache user provides to specify the object that will be invalidated
   */
  public void invalidate(String key, byte invalidationScope) {
    invalidate(key, synchronous, invalidationScope);
  }

  /**
   * Explicitly invalidates objects that are bound to the specified keys. Explicit invalidation will
   * provoke events firing.
   * The behavior of this invalidation is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another invalidate method.
   *
   * @param keySet The keys that cache user provides to specify the objects that will be invalidated
   */
  public void invalidateBundle(Set keySet, byte invalidationScope) {
    invalidateBundle(keySet, synchronous, invalidationScope);
  }

  /**
   * Explicitly invalidates objects based on attributes pattern. If the pattern is a sub set of the attributes bound to an object key,
   * the cached object with that key is considered applying to the pattern and will be invalidated.
   * The behavior of this invalidation is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another invalidate method.
   *
   * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
   */
  public void invalidate(Map attributes, byte invalidationScope) {
    invalidate(attributes, synchronous, invalidationScope);
  }

  /**
   * Explicitly invalidates objects based on attributes pattern and a group. If the pattern is a sub set of the attributes bound to an
   * object key, belonging to the specified group the cached object with that key is considered applying to
   * the pattern and will be invalidated.
   * The behavior of this invalidation is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another invalidate method.
   *
   * @param attributes The attributes that represent a pattern for objects targeted to be invalidated
   * @param group      The group that objects must belong to in order to be invalidated
   *                   If <code>null</code>, all object apply to them
   */
  public void invalidate(Map attributes, String group, byte invalidationScope) {
    invalidate(attributes, group, synchronous, invalidationScope);

    CacheGroupImpl cacheGroup = (CacheGroupImpl) region.getCacheGroup(group);
    try {
      Set childrenSet = cacheGroup.getChildren();
      if (childrenSet != null) {
        Object[] children = childrenSet.toArray();
        for (int i = 0; i < children.length; i++) {
          if (children[i] instanceof String) {
            CacheGroup child = region.getCacheGroup((String) children[i]);
            if (child != null) {
              String grName = child.getName();
              invalidate(attributes, grName, invalidationScope);
            }
          }
        }
      }

    } catch (CacheException e) {
      e.printStackTrace();
    }

  }

  /**
   * Explicitly invalidates an object that is bound to the specified key. Explicit invalidation will
   * provoke events firing.
   * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
   * the region <code>configuration</code>.
   *
   * @param key         The key that cache user provides to specify the object that will be invalidated
   * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
   *                    is defined by the region <code>configuration</code>.
   */
  public void _invalidate(String key, boolean synchronous, byte invalidationScope) {
    notification.notify(region, storage, key, NotificationListener.EVENT_INVALIDATION, null, invalidationScope, synchronous);
  }

  public void invalidate(String key, boolean synchronous, byte invalidationScope) {
    if (key != null) {
      if ("".equals(key)) {
        return;
      }
                                     // THE 4 LINES AFTER THIS COMMENT ARE PATCH FOR BI
      if (key.equals("**")) {        // patch for BI scenario that was not tested at all
        invalidate(new HashMap());   // and led to escallation of 5 custoemrs
        return;                      // !!! DO NOT CHANGE OR THE ESCALLATIONS WILL COME BACK !!!
      }                              // BI contacts on the issue: Krannich, Bernd (D028923); Scheerer, Oliver (D027913); Matthee, Stephan (D032419)
                                     ////////////////////////////////////////////////////////////////////////
      int keyLength = key.length();
      char lastChar = key.charAt(keyLength - 1);
      if (lastChar == '*') {
        Set keysToInvalidate = new HashSet();
        Iterator keysIterator = facade.keySet().iterator();
        key = key.substring(0, keyLength - 1);
        if (keyLength > 1) {
          if (key.charAt(keyLength - 2) == '*') {
            key = key.substring(0, keyLength - 2);
            while (keysIterator.hasNext()) {
              String currentKey = (String) keysIterator.next();
              if (currentKey.startsWith(key)) {
                keysToInvalidate.add(currentKey);
              }
            }
          } else {
            int lastSeparatorIndex = key.lastIndexOf('/');
            while (keysIterator.hasNext()) {
              String currentKey = (String) keysIterator.next();
              if ((currentKey.startsWith(key)) && (currentKey.lastIndexOf('/') == lastSeparatorIndex)) {
                keysToInvalidate.add(currentKey);
              }
            }
          }
        } else {
          while (keysIterator.hasNext()) {
            String currentKey = (String) keysIterator.next();
            int separatorIndex = currentKey.indexOf('/');
            if (separatorIndex == -1) {
              keysToInvalidate.add(currentKey);
            }
          }
        }
        Iterator removeIterator = keysToInvalidate.iterator();
        notification.beginEventBlock(region);
        while (removeIterator.hasNext()) {
          String keyToInvalidate = (String) removeIterator.next();
          _invalidate(keyToInvalidate, synchronous, invalidationScope);
        }
        // messages about all objects removed will be sent as one message
        notification.endEventBlock(region, true, synchronous);
      } else {
        _invalidate(key, synchronous, invalidationScope);
      }
    } else {
      throw new NullPointerException("Cache object key cannot be null");
    }
  }


  /**
   * Explicitly invalidates objects that are bound to the specified keys. Explicit invalidation will
   * provoke events firing.
   * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
   * the region <code>configuration</code>.
   *
   * @param keySet      The keys that cache user provides to specify the objects that will be invalidated
   * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
   *                    is defined by the region <code>configuration</code>.
   */
  public void invalidateBundle(Set keySet, boolean synchronous, byte invalidationScope) {
    notification.beginEventBlock(region);
    Iterator keys = keySet.iterator();
    while (keys.hasNext()) {
      String key = (String) keys.next();
      notification.notify(region, storage, key, NotificationListener.EVENT_INVALIDATION, null, invalidationScope);
    }
    notification.endEventBlock(region, true, synchronous);
  }

  /**
   * Explicitly invalidates objects based on attributes pattern. If the pattern is a sub set of the attributes bound to an object key,
   * the cached object with that key is considered applying to the pattern and will be invalidated.
   * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
   * the region <code>configuration</code>.
   *
   * @param attributes  The attributes that represent a pattern for objects targeted to be invalidated
   * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
   */
  public void invalidate(Map attributes, boolean synchronous, byte invalidationScope) {
    // remove with pattern
//    Iterator keys = facade.keySet().iterator();
//    Set pattern = attributes == null ? null : attributes.keySet();
//    // begin message block in notification, this is needed to reduce traffic
//
//    notification.beginEventBlock(region); // BI problem about not tested scenario
//    // iterate through keys in the group
//    while (keys.hasNext()) {
//      String key = (String) keys.next();
//      boolean comply = true;
//      // if pattern == null, all objects in the group comply
//      if (pattern != null) {
//        Map targetAttributes = storage.getAttributes(key, false);
//        Iterator patternNames = pattern.iterator();
//        // iterate through pattern, see if all attributes in pattern are attributes of objects
//        while (patternNames.hasNext()) {
//          String name = (String) patternNames.next();
//          String targetValue = (String) targetAttributes.get(name);
//          if (!(attributes.get(name).equals(targetValue))) {
//            comply = false;
//            break;
//          }
//        }
//      }
//      if (comply) {
//        // remove without flushing the storage plugin
//        invalidate(key, invalidationScope);
//        String msg = "Invalidating - [key]:" + key + " [attributes]:" + attributes + " [invalidationScope]:" + invalidationScope;
//        try {
//			fout.write(msg.getBytes());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//      }
//    }
//    try {
//      String msg = "[" + System.currentTimeMillis() + "]"+ "CacheControlImpl#327 | ATTRIBUTES = " + attributes + " | INVALIDATION SCOPE = " + invalidationScope + "\n"; 
//	  fout.write(msg.getBytes());
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//    notification.endEventBlock(region, true, synchronous); // BI problem about not tested scenario

// THE SOURCE CODE ABOVE WAS THE ORIGINAL SOURCE CODE OF CML
// BELLOW IS A PATCHED VERSION OF THE ORIGINAL SOURCE CODE
// THE PATCH WAS DONE BECAUSE BI DID NOT TESTED AT ALL ONE OF THEIR SCENARIOS
// THIS LED TO DIRTY READ FROM THE CACHE - THE PROBLEM LED TO ESCALLATIONS FROM 5 CUSTOMERS
// BI CONTACTS ON THE ISSUE: Krannich, Bernd (D028923); Scheerer, Oliver (D027913); Matthee, Stephan (D032419) 

// !!! DO NOT CHANGE THE PATCH OR THE ESCALLATIONS WILL COME BACK !!!

    try {
      region.removeCacheGroup();
    } finally {
      notification.notify(region, storage, null, NotificationListener.EVENT_SEMANTIC_INVALIDATION, attributes, invalidationScope, synchronous);
    }
  }

  /**
   * Explicitly invalidates objects based on attributes pattern and a group. If the pattern is a sub set of the attributes bound to an
   * object key, belonging to the specified group the cached object with that key is considered applying to
   * the pattern and will be invalidated.
   * The behavior of this invalidation is defined by the flag parameter. The parameter overrides
   * the region <code>configuration</code>.
   *
   * @param attributes  The attributes that represent a pattern for objects targeted to be invalidated
   * @param group       The group that objects must belong to in order to be invalidated
   * @param synchronous If true, the invalidation will be synchronized. The scope of the invalidation
   *                    If <code>null</code>, all object apply to them
   */
  public void invalidate(Map attributes, String group, boolean synchronous, byte invalidationScope) {
    // traverse children if stated
    if (group.endsWith("**")) {
      group = group.substring(0, group.length() - 2);
      Map subgroupAttributes = storage.getSystemAttributes("#(" + group + ")", true);
      if (subgroupAttributes != null) {
        Iterator iter = subgroupAttributes.keySet().iterator();
        while (iter.hasNext()) {
          String subgroupName = (String) iter.next();
          invalidate(attributes, subgroupName + "**", synchronous, invalidationScope);
        }
      }
    }
    // invalidate with pattern
    CacheGroupImpl cacheGroup = (CacheGroupImpl) region.getCacheGroup(group);
    Iterator keys = cacheGroup.keySet().iterator();
    Set pattern = attributes == null ? null : attributes.keySet();
    // begin message block in notification, this is needed to reduce traffic
    notification.beginEventBlock(region);

    // iterate through keys in the group
    while (keys.hasNext()) {
      String key = (String) keys.next();
      boolean comply = true;
      // if pattern == null, all objects in the group comply
      if (pattern != null) {
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
        invalidate(key, invalidationScope);
      }
    }
    notification.endEventBlock(region, true, synchronous);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidate(java.lang.String)
    */
  public void invalidate(String key) {
    invalidate(key, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidateBundle(java.util.Set)
    */
  public void invalidateBundle(Set keySet) {
    invalidateBundle(keySet, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidate(java.util.Map)
    */
  public void invalidate(Map attributes) {
    invalidate(attributes, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidate(java.util.Map, java.lang.String)
    */
  public void invalidate(Map attributes, String group) {
    invalidate(attributes, group, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidate(java.lang.String, boolean)
    */
  public void invalidate(String key, boolean synchronize) {
    invalidate(key, synchronize, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidateBundle(java.util.Set, boolean)
    */
  public void invalidateBundle(Set keySet, boolean synchronize) {
    invalidateBundle(keySet, synchronize, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidate(java.util.Map, boolean)
    */
  public void invalidate(Map attributes, boolean synchronize) {
    invalidate(attributes, synchronize, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.CacheControl#invalidate(java.util.Map, java.lang.String, boolean)
    */
  public void invalidate(Map attributes, String group, boolean synchronize) {
    invalidate(attributes, group, synchronize, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.engine.cache.communication.NotificationListener#invalidate(int, java.lang.String, byte)
    */
  public void invalidate(int regionId, String key, byte event) {
    invalidate(regionId, key, event, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.engine.cache.communication.NotificationListener#invalidate(int, java.lang.String, java.lang.Object)
    */
  public void invalidate(int regionId, String key, Object cachedObject) {
    invalidate(regionId, key, cachedObject, (byte) -1);
  }

  /* (non-Javadoc)
    * @see com.sap.util.cache.InvalidationListener#invalidate(java.lang.String, java.lang.Object)
    */
  public void invalidate(String name, Object cachedObject) {
    invalidate(name, cachedObject, (byte) -1);
  }

}
