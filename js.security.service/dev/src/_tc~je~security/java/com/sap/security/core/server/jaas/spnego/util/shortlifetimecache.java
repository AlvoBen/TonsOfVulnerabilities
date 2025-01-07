package com.sap.security.core.server.jaas.spnego.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;

/**
 *  The purpose of this class to provide a cache facility for objects with
 *  a very short lifetime (milliseconds to some seconds).
 *  It is basically parametrized with two parameters (item lifetime and 
 *  cleanup). For default see {@link #ShortLifetimeCache(int,int,String)}.
 */
public class ShortLifetimeCache {
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_SPNEGO_LOCATION);

  private Map chacheMap = null;
  private long miliSecondsUntilCleanup = -1;
  private long itemLifeTime = -1;
  private long lastCleanupTime = -1;

  /**
   *  name of this cache. Helps to identify the cache in trace outputs.
   */
  protected String cacheName = null;

  /**
   *  Constructor. Creates a ShortLifetimeCache.
   *  @param msecsUntilCleanup milliseconds until the cache is scanned for
   *          outdated items. There is no thread that wakes up after a
   *          configurable idle time, but the scanning takes place when
   *          {@link #cleanup()} is explicitely called. So the user of this
   *          cache can call {@link cleanup()} regularly, if the configured
   *          time did not yet elapse the method returns immediately without
   *          doing any harm to performance.
   *  @param itemLifetime lifetime of one individual item (in milliseconds).
   *          If this lifetime has elapsed, lookups will not consider the
   *          item anymore. At the next call of {@link #cleanup()} it will
   *          be removed.
   *  @param name Name of the cache. Useful for trace outputs.
   */
  public ShortLifetimeCache(int msecsUntilCleanup, int itemLifetime, String name) {
    miliSecondsUntilCleanup = msecsUntilCleanup;
    itemLifeTime = itemLifetime;
    lastCleanupTime = System.currentTimeMillis();
    chacheMap = new HashMap();
    cacheName = name;
  }

  /**
   * Resets the parameters. Allows to change the customization of the 
   * thread after creation.
   * @param msecsUntilCleanup see constructor.
   * @param itemLifetime see constructor.
   */
  public void resetParameters(int msecsUntilCleanup, int itemLifetime) {
    synchronized (chacheMap) {
      if (msecsUntilCleanup != -1) {
        miliSecondsUntilCleanup = msecsUntilCleanup;
      }

      if (itemLifetime != -1) {
        itemLifeTime = itemLifetime;
      }
    }
  }

  /**
   * Performs a lookup on an object that has been stored previously
   * under the specified key.
   * @param key key to look after.
   * @return object that has been stored under the key.
   */
  public Object contains(Object key) {
    Object obj = null;

    synchronized (chacheMap) {
      ShortLifetimeCacheItem slci = (ShortLifetimeCacheItem) chacheMap.get(key);

      if (slci != null) {
        if (slci.isValid()) {
          obj = slci.getObject();
        } else {
          chacheMap.remove(key);
        }
      }
    }

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Cache size of cache " + cacheName + " after contains: " + chacheMap.size());
    }

    return obj;
  }

  /**
   *  If the cleanup idle time has elapsed (see constructor) the cache is
   *  scanned for outdated items. If not, it returns immediately. Please note
   *  that the scanning creates a monitor on the internal map object, so
   *  the cleanup parameter should not be chosen too small.
   */
  public void cleanup() {
    long currentTime = System.currentTimeMillis();
    long difference = currentTime - lastCleanupTime;

    // do a cleanup if internal 
    if (difference > miliSecondsUntilCleanup) {
      this.internalCleanup();
      lastCleanupTime = currentTime;
    }
  }

  /**
   *  Scans the cache for outdated objects and removes them.
   */
  private void internalCleanup() {
    ArrayList items2Delete = new ArrayList();
    Iterator iterator = null;
    Object obj = null;
    ShortLifetimeCacheItem slci = null;

    long currentTime = System.currentTimeMillis();

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Short lifetime cache " + cacheName + " cleans up. Size " + chacheMap.size());
    }

    synchronized (chacheMap) {
      iterator = chacheMap.keySet().iterator();

      while (iterator.hasNext()) {
        obj = iterator.next();
        slci = (ShortLifetimeCacheItem) chacheMap.get(obj);

        if (currentTime - slci.creationTime > itemLifeTime) {
          // Remember item for deletion
          items2Delete.add(obj);
        }
      }

      for (int idx = 0; idx < items2Delete.size(); idx++) {
        chacheMap.remove(items2Delete.get(idx));
      }
    }

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Short lifetime cache " + cacheName + " after cleanup: Size " + chacheMap.size());
    }
  }

  /**
   * Stores the object <it>value</it> under the key <it>key</it>. 
   * @param key key to store under.
   * @param value value to be stored.
   */
  public void put(Object key, Object value) {
    synchronized (chacheMap) {
      chacheMap.put(key, new ShortLifetimeCacheItem(value));
    }

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Cache size of cache " + cacheName + " after put: " + chacheMap.size());
    }
  }

  /**
   *  Deletes an item from the cache.
   *  @param key key to remove.
   */
  public void remove(Object key) {
    synchronized (chacheMap) {
      chacheMap.remove(key);
    }

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Cache size of cache " + cacheName + " after remove: " + chacheMap.size());
    }
  }

  class ShortLifetimeCacheItem {
    long creationTime;
    String token;
    Object cachedObject;

    ShortLifetimeCacheItem(Object obj) {
      creationTime = System.currentTimeMillis();
      cachedObject = obj;
    }

    boolean isValid() {
      long current = System.currentTimeMillis();
      return (current - creationTime < itemLifeTime);
    }

    Object getObject() {
      return cachedObject;
    }
  }

}
