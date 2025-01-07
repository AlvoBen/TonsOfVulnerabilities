/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.util.cache;

import java.util.*;

/**
 * This class is LRU caching system. It is using a combination between a
 * week hash table and a LRU queue. The system is initialized with the maximal size
 * of the LRU queue [MAX].
 *
 * <p>When an element is going to be cached there are 3 possible cases :
 *
 * <p>1. The number of the elements in the LRU queue is below MAX and the object is
 * normally cached then it is added in the head of the LRU queue
 *
 * <p>2. The number of the elements in the LRU queue is above MAX and the object is
 * normally cached then it is added in the head of the LRU queue the last element
 * of the LRU queue is moved to a Weak-Hashtable.
 *
 * <p>3. The object is "weakly" ceched then it is added only in a Weak-HhashTable
 *
 * <p>When an element is looked for by key then it is looked for in a hard-reference
 * hash table which is used for fast search of cached objects. The search in the
 * hash table could be done in two ways by key or by object :
 *
 *<li><p>1 If the object is found it is returned to the user and the same object is
 *    moved in the head of the LRU queue
 *    <p>2 If the object is not found in the hard reference hash table then
 *    it is looked for in the weak-reference hash table
 *<li><p>2.1 If the object is found in the weak reference hash table then it is moved
 *    to the hard reference hash table and is put in the head of the LRU queue
 *<li><p>2.2 The object is not found in the weak reference hash table then a special
 *    object is returnet to the user (according to the implementation of the
 *    ObjectCreator interface) this object is put in the head of the LRU queue and
 *    in the hard reference hash table.
 *
 *  Here is an example code for creating a new cache:
 *
 * CacheGroup group = new CacheGroup();
 * Thread thread = new Thread(group);
 * thread.start();
 * Cache cache = new Cache(group);
 * // ... performs some operations with the cache
 *
 *
 *<li><p>NOTE: The class is final it could not be extended !
 *  The reason for that is that the core of the logic of this LRU system must be kept
 *  constant (for example the LRU algorithm of the CacheQueue) in the same time the
 *  system is conceptualy good to be an individual unit in the classes which are using it.
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */

public class Cache {

  /* Factory for objects */
//  private ObjectCreator objectFactory = null;

  /*
   * Hashtable holding hard references and providing faster search in the LRU queue.
   * This is the hard part of the cache
   */
  private Map cache = new Hashtable();

  /* LRU queue of the cache */
  private CacheQueue cacheLRU = new CacheQueueLRU();

  /*
   * Hashtable for holding soft references. This is the "soft" part of the cache
   * This object implements Runnable someone must start the thread
   */
  private SoftValueHashMap softCache = null;

  /* Group to which belongs this cache system */
  private CacheGroup group = null;

  /*indicates if this cache has a weak part or not*/
  private boolean withoutWeakPart = false;

  /**
   * Constructor creating a new cache without weak part
   * when creating an instance with this constructor you does not have
   * to start a thread because it is needed only when soft part of the cache is used
   *
   * Example:
   *
   * Cache myCache = new Cache();
   * // at this line myCache is ready to be used
   *
   * NOTE: This is the only constructor that can create cache without weak part
   * each of the other constructors creates and weak part for the cache and when
   * you are not using this constructor you must start and CacheGroup
   */
  public Cache() {
    this(new CacheQueueLRU());
    this.withoutWeakPart = true;
  }

  public Cache(int min, int max) {
    this(new CacheQueueLRU(min, max));
    this.withoutWeakPart = true;
  }

  /**
   * Constructor
   *
   * @param   _group Initial Group for Cache Manager
   */
  public Cache(/*ObjectCreator factory, */CacheGroup _group) {
    this(new CacheQueueLRU());
//    this.objectFactory = factory;
    this.group = _group;
    softCache = new SoftValueHashMap(group);
  }

  /**
   * Constructor
   *
   * @param   min Minimal size for Cache Queue
   * @param   max Maximal size for Cache Queue
   * @param   _group Initial Group for Cache Manager
   *
   */
  public Cache(int min, int max, /*ObjectCreator factory, */CacheGroup _group) {
    this(new CacheQueueLRU(min, max));
//    this.objectFactory = factory;
    this.group = _group;
    this.softCache = new SoftValueHashMap(group);
  }

  /**
   * Private constructor for avoid the user directly to access the LRU queue
   *
   * @param   cacheLRU LRU Queue
   */
  private Cache(CacheQueue cacheLRU) {
    this.cacheLRU = cacheLRU;
  }

  /**
   * Return item from cache, update corresponding queue, returns null if
   * object is not cached in the hard part oh the cache. This method looks
   * for the object iff it is in the hard part of the cache. This method
   * does not look up for the object in the weak part of the cache.
   *
   * @param   key Object
   * @return Cached Object
   */
  public synchronized Object lookupCache(Object key) {
    CacheQueueItem cItem = (CacheQueueItem) cache.get(key);

    if (cItem != null) {
      cacheLRU.update(cItem);
      return cItem.value;
    }
    return null;
//    return objectFactory.createObjectByKey(key);
  }

  /**
   * Add object in cache with data _value,  this method MUST be invoked only if
   * lookupCache fail (return null). The item is added as a hard reference in the cache.
   * If LRU cache is overfilled then the last object in the queue will be moved in the
   * soft part of the cache.
   *
   * @param   _key  the key of the item
   * @param   _value the value of the item
   */

  public synchronized void addCache(Object _key, Object _value) {
    if (!withoutWeakPart && group.stopFlag) {
      throw new CacheGroupException();
    }

    CacheQueueItem qi = new CacheQueueItem(); // create a new cache item
    qi.value = _value;
    qi.key = _key;
    qi.cacheType = CacheQueueItem.CACHED_DATA;

    if (cache.containsKey(_key)) {
      remove(_key);
      cacheLRU.add(qi);
      cache.put(_key, qi);
      return;
    }

    synchronized (cacheLRU) {
      CacheQueueItem removed = cacheLRU.add(qi); // put it in the Queue returns the last element
      cache.put(_key, qi);                       // put the object in the Hashtable
      //qi = null;                               // which is removed if the queue was full before adding
      if (removed != null) {                      // if last in the queue was removed then
         cache.remove(removed.key);               // we remove it from the hard cache
         if (withoutWeakPart) {
           ((StableCacheObject) removed.value).cacheFinalization();
         } else if (removed.value != null){
             softCache.put(removed.key, removed.value, false); // and put it in the soft cache
         }
         removed = null;
      }
    }
  }

  /**
   * This is a method for weak caching
   *
   * @param _key the key to which the value will be mapped
   * @param _value the Object that will be cached
   */
  public void addToWeakCache(Object _key, Object _value) {

    if (withoutWeakPart) {
      throw new RuntimeException("This instance of Cache does not have weak part. The operation is not alowed");
    }

    if (group.stopFlag) {
      throw new CacheGroupException();
    }
    softCache.put(_key, _value, true);
  }

  /**
   * By a given key returns the object that was cached if there
   * is no object mapped to this key then the cache will return a
   * default object generated by the implementation of ObjectGenerator
   * which was given when the instantion of the CacheManager was created.
   *
   * @param _key an object
   * @return cached object
   */
  public synchronized Object getByKey(Object _key) {

    if (!withoutWeakPart && group.stopFlag) {
      throw new CacheGroupException();
    }
    if (cache.containsKey(_key)) {                              // if there is an object mapped in the hard cache, take it
      CacheQueueItem cItem = (CacheQueueItem) cache.get(_key);  //in cache we add CacheQueueItems
      cacheLRU.update(cItem);                                   // move the object you get at first position in the LRU queue
      return cItem.value;                                       // return the object
    } else if (!withoutWeakPart) {
        if (softCache != null && softCache.containsKey(_key)) { // if there is an object mapped in the soft cache take it
          SoftValue result = (SoftValue) softCache.get(_key);   // take the object
          if (result != null && !result.alwaysInWeak) {         // if the object was not GC and if it is not always weak
            Object value = result.get();						
            if (value != null) {
              addCache(_key, result.get());                     // cache the object in the hard cache
            }
            softCache.remove(_key);                             // remove the object from the soft cache
          }
	  if (result != null) {
            return result.get();                                  // return the object
	  }
	  return null;
        }
    }
    // there is no value mapped fo this key
    return null;
  }

  /**
   * Removes an entry from the cache
   *
   * @param _key the key to a value which should be deleted
   * @return true if an entry was removed otherwise false
   */
  public synchronized boolean remove(Object _key) {

    if (!withoutWeakPart && group.stopFlag) {
      throw new CacheGroupException();
    }
    CacheQueueItem qi = null;
    boolean result = false;
    if (cache.containsKey(_key)) {
      qi = (CacheQueueItem) cache.remove(_key);
      cacheLRU.remove(qi);
      qi = null;
      result = true;
    } else  if (!withoutWeakPart && softCache.containsKey(_key)) {
              softCache.remove(_key);
              return true;
            }
    return result;
  }

  /**
   * Returns the size of the cache
   */
  public int allSize() {
    if (!withoutWeakPart) {
      return cache.size() + softCache.size();
    }

    return cache.size();
  }

  /**
   * Returns the size of the stable part of the cache
   */
  public int stableSize() {
    return cache.size();
  }

  /**
   * Returns the keys of the stable part of the cache
   */
  public synchronized Iterator stableKeyIterator() {
    return cache.keySet().iterator();
  }

  /**
   * Returns the keys of the weak part of the cache
   */
  public synchronized Iterator weakKeyIterator() {
    if (!withoutWeakPart) {
      return softCache.keySet().iterator();
    }
    return (new Vector()).iterator();  //empty iterator
  }

  /**
   * Returns the keys of the stable part of the cache
   */
  public synchronized Set stableKeySet() {
    return cache.keySet();
  }

  /**
   * Returns the keys of the weak part of the cache
   */
  public Set weakKeySet() {
    if (!withoutWeakPart) {
      return softCache.keySet();
    }
    return new HashSet(); //empty set
  }

  /**
   * Returns the keys of the stable+weak part of the cache
   */
  public synchronized Iterator keyIterator() {
    return new Iterator() {
      Iterator i1 = stableKeyIterator();
      Iterator i2 = weakKeyIterator();

      public boolean hasNext() {
        if (i1.hasNext()) {
          return true;
        }
        if (i2.hasNext()) {
          return true;
        }
        return false;
      }

      public Object next() {
        if (i1.hasNext()) {
          return i1.next();
        }
        if (i2.hasNext()) {
          return i2.next();
        }
        return null;
      }

      public void remove() {
      }
    };
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the values of the stable part of the cache
   */
  public synchronized Iterator stableValueIterator() {
    return cache.values().iterator();
  }

  /**
   * Returns the keys of the weak part of the cache
   */
  public synchronized Iterator weakValueIterator() {
    if (!withoutWeakPart) {
      return softCache.values().iterator();
    }
    return (new Vector()).iterator();  //empty iterator
  }

  /**
   * Returns the values of the stable+weak part of the cache
   */
  public synchronized Iterator valueIterator() {
    return new Iterator() {
      Iterator i1 = stableValueIterator();
      Iterator i2 = weakValueIterator();

      public boolean hasNext() {
        if (i1.hasNext()) {
          return true;
        }
        if (i2.hasNext()) {
          return true;
        }
        return false;
      }

      public Object next() {
        if (i1.hasNext()) {
          return ((CacheQueueItem)i1.next()).value;
        }
        if (i2.hasNext()) {
          return ((SoftValue) i2.next()).get();
        }
        return null;
      }

      public void remove() {
      }
    };
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Registers a listener to this cache instence
   *
   * @param cacheListener implementation
   * @throws RuntimeException if this cache instance has already been registered a listener
   */
  public void registerCacheListener(CacheListener cacheListener) {
    if (softCache.cacheListener != null) {
      throw new RuntimeException("SAP J2EE EENGINE|CACHE CONFIGURATION MANAGER| This cache has already been registered a listener");
    }
    softCache.registerListener(cacheListener);
  }

}