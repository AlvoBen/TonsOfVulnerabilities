package com.sap.engine.cache.core.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.cache.communication.Notification;
import com.sap.engine.cache.util.memory.SizeOf;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.HolderRuntimeException;
import com.sap.util.cache.exception.WriteException;

/**
 * @author Petev, Petio, i024139
 */
public class CacheFacadeImpl extends CacheGroupImpl implements CacheFacade {

  public CacheFacadeImpl(Notification notification, CacheRegionImpl region) {
    super(null, notification, region);
  }

  /**
   * A put in a specific group in the cache region. The behavior of this put is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on eviction policy's decision
   * @param group The group name that this object will belong to
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject, String group) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.put(key, cachedObject);
  }

  /**
   * A put in a specific group in the cache region. The behavior of this put is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The objects keys, and objects themselves. Later cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject, String group) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.putBundle(keyToObject);
  }

  /**
   * A put in a specific group in the cache region. The behavior of this put is defined by the two flag parameters.
   * These parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on eviction policy's decision
   * @param group The group name that this object will belong to
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * @param suppressInvalidation If true, no invalidation will be made, no listener on other node will receive any
   * events about putting / modification.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.put(key, cachedObject, synchronous, suppressInvalidation);
  }

  public void put(String key, Object cachedObject, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.put(key, cachedObject, synchronous, invalidationScope);
  }

  /**
   * A put in a specific group in the cache region. The behavior of this put is defined by the two flag parameters.
   * These parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The objects keys, and objects themselves. Later cache users can get a cached object using its key
   * @param group The group name that this object will belong to
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * @param suppressInvalidation If true, no invalidation will be made, no listener on other node will receive any
   * events about putting / modification.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.putBundle(keyToObject, synchronous, suppressInvalidation);
  }

  public void putBundle(Map keyToObject, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.putBundle(keyToObject, synchronous, invalidationScope);
  }

  /**
   * A put in a specific group in the cache region with cached object attributes. The behavior of this put
   * is up to the region <code>configuration</code>. To override the <code>configuration</code> cache users
   * may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on eviction policy's decision
   * @param attributes The attributes that will be assigned to the cached object. Attributes can be separately
   * modified.
   * @param group The group name that this object will belong to
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code>, <code>attributes</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject, Map attributes, String group) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.put(key, cachedObject, attributes);
  }

  /**
   * A put in a specific group in the cache region with cached object attributes. The behavior of this put
   * is up to the region <code>configuration</code>. To override the <code>configuration</code> cache users
   * may use other put methods.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The objects keys, and objects themselves. Later cache users can get a cached object using its key
   * @param keyToAttributes The attributes that will be assigned to the cached objects keys. Attributes can be separately
   * modified.
   * @param group The group name that this object will belong to
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code>, <code>attributes</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, String group) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.putBundle(keyToObject, keyToAttributes);
  }

  /**
   * A put in a specific group in the cache region cached object attributes. The behavior of this put is defined by the two flag parameters.
   * These parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param key The object key, later cache users can get a cached object using its key
   * @param cachedObject The object that will be or will not be cached depending on eviction policy's decision
   * @param attributes The attributes that will be assigned to the cached object. Attributes can be separately
   * modified.
   * @param group The group name that this object will belong to
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * @param suppressInvalidation If true, no invalidation will be made, no listener on other node will receive any
   * events about putting / modification.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code>, <code>attributes</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void put(String key, Object cachedObject, Map attributes, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.put(key, cachedObject, attributes, synchronous, suppressInvalidation);
  }

  public void put(String key, Object cachedObject, Map attributes, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.put(key, cachedObject, attributes, synchronous, invalidationScope);
  }

  /**
   * A put in a specific group in the cache region cached object attributes. The behavior of this put is defined by the two flag parameters.
   * These parameters override the region <code>configuration</code>.
   * If a put is made second time on the same <code>key</code> the new object will overwrite the already existing.
   *
   * @param keyToObject The objects keys, and objects themselves. Later cache users can get a cached object using its key
   * @param keyToAttributes The keys and attributes that will be assigned to the cached objects keys. Attributes can be separately
   * modified.
   * @param group The group name that this object will belong to
   * @param synchronous If true, the invalidation and the put will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * @param suppressInvalidation If true, no invalidation will be made, no listener on other node will receive any
   * events about putting / modification.
   * @throws NullPointerException if <code>key</code>, <code>cachedObject</code>, <code>attributes</code> or <code>group</code> is null
   * @throws WriteException if the put did not succeed
   */
  public void putBundle(Map keyToObject, Map keyToAttributes, String group, boolean synchronous, boolean suppressInvalidation) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.putBundle(keyToObject, keyToAttributes, synchronous, suppressInvalidation);
  }

  public void putBundle(Map keyToObject, Map keyToAttributes, String group, boolean synchronous, byte invalidationScope) throws CacheException {
    CacheGroup _group = region.getCacheGroup(group);
    _group.putBundle(keyToObject, keyToAttributes, synchronous, invalidationScope);
  }

  /**
   * A remove from the cache region. No or several objects will be removed depending on the group and
   * attributes pattern specified. If the pattern is a sub set of the attributes bound to an object key,
   * the cached object with that key is considered applying to the pattern and will be removed.
   * The behavior of this removal is up to the region <code>configuration</code>.
   * To override the <code>configuration</code> cache users may use another remove method.
   *
   * @param attributes The attributes that represent a pattern for objects targeted to be removed
   * If <code>null</code>, all object apply to them
   * @param group The group that objects must belong to in order to be removed
   * @throws NullPointerException if <code>attributes</code> or <code>group</code> is null
   * @throws CacheException if the remove did not succeed
   */
  public void remove(Map attributes, String group) {
    CacheGroup _group = region.getCacheGroup(group);
    _group.remove(attributes);
  }

  /**
   * A remove from the cache region. No or several objects will be removed depending on the group and
   * attributes pattern specified. If the pattern is a sub set of the attributes bound to an object key,
   * the cached object with that key is considered applying to the pattern and will be removed.
   * The behavior of this removal is defined by the two flag parameters. These parameters override
   * the region <code>configuration</code>.
   *
   * @param attributes The attributes that represent a pattern for objects targeted to be removed
   * If <code>null</code>, all object apply to them
   * @param synchronous If true, the invalidation and the removal will be synchronized. The scope of the invalidation
   * is defined by the region <code>configuration</code>.
   * @param suppressInvalidation If true, no invalidation will be made, no listener on other node will receive any
   * events about removal.
   * @throws NullPointerException if <code>attributes</code> or <code>group</code> is null
   * @throws CacheException if the remove did not succeed
   */
  public void remove(Map attributes, String group, boolean synchronous, boolean suppressInvalidation) {
    CacheGroup _group = region.getCacheGroup(group);
    _group.remove(attributes, synchronous, suppressInvalidation);
  }
  
  public void remove(Map attributes, String group, boolean synchronous, byte invalidationScope) {
    CacheGroup _group = region.getCacheGroup(group);
    _group.remove(attributes, synchronous, invalidationScope);
  }

  /**
   * Creates parent-child hierarchical relation respectively between the 
   * specified parent group the specified child group names
   * 
   * @param parentGroupName the name of the parent group in the relation
   * @param childGroupName the name of the child group in the relation
   */
  public void addChild(String parentGroupName, String childGroupName) throws CacheException {
    CacheGroup _group = region.getCacheGroup(parentGroupName);
    _group.addChild(childGroupName);
  }
      
  /**
   * Removes parent-child hierarchical relation respectively between the 
   * specified parent group the specified child group names
   * 
   * @param parentGroupName the name of the parent group in the relation
   * @param childGroupName the name of the child group in the relation
   */
  public void removeChild(String parentGroupName, String childGroupName) throws CacheException{
    CacheGroup _group = region.getCacheGroup(parentGroupName);
    _group.removeChild(childGroupName);
  }
  
  /**
   * Returns all groups names that are children of a group one 
   * current group and the one specified by name
   * 
   * @param groupName the name of the group to get children of
   * @throws CacheException
   */
  public Set getChildren(String groupName) throws CacheException {
    CacheGroup _group = region.getCacheGroup(groupName);
    return _group.getChildren();
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.CacheFacade#getOrPut(java.lang.String, java.lang.Object, java.lang.String)
   */
  public Object getOrPut(final String arg0, final Object arg1, final String arg2) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPutBundle(java.util.Map, java.lang.String)
   */
  public Map getOrPutBundle(final Map arg0, final String arg1) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPut(java.lang.String, java.lang.Object, java.lang.String, boolean, boolean)
   */
  public Object getOrPut(final String arg0, final Object arg1, final String arg2, final boolean arg3, final boolean arg4) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPut(java.lang.String, java.lang.Object, java.lang.String, boolean, byte)
   */
  public Object getOrPut(final String arg0, final Object arg1, final String arg2, final boolean arg3, final byte arg4) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPutBundle(java.util.Map, java.lang.String, boolean, boolean)
   */
  public Map getOrPutBundle(final Map arg0, final String arg1, final boolean arg2, final boolean arg3) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPutBundle(java.util.Map, java.lang.String, boolean, byte)
   */
  public Map getOrPutBundle(final Map arg0, final String arg1, final boolean arg2, final byte arg3) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPut(java.lang.String, java.lang.Object, java.util.Map, java.lang.String)
   */
  public Object getOrPut(final String arg0, final Object arg1, final Map arg2, final String arg3) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPutBundle(java.util.Map, java.util.Map, java.lang.String)
   */
  public Map getOrPutBundle(final Map arg0, final Map arg1, final String arg2) throws CacheException {
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
   * @see com.sap.util.cache.CacheFacade#getOrPut(java.lang.String, java.lang.Object, java.util.Map, java.lang.String, boolean, boolean)
   */
  public Object getOrPut(final String arg0, final Object arg1, final Map arg2, final String arg3, final boolean arg4, final boolean arg5) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1, arg2, arg3, arg4, arg5);
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
   * @see com.sap.util.cache.CacheFacade#getOrPut(java.lang.String, java.lang.Object, java.util.Map, java.lang.String, boolean, byte)
   */
  public Object getOrPut(final String arg0, final Object arg1, final Map arg2, final String arg3, final boolean arg4, final byte arg5) throws CacheException {
    final Object[] result = new Object[1];
    result[0] = get(arg0);
    if (result[0] == null) {
      region.execute(arg0, new Runnable() {
        public void run() {
          result[0] = get(arg0);
          if (result[0] == null) {
            try {
              put(arg0, arg1, arg2, arg3, arg4, arg5);
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
   * @see com.sap.util.cache.CacheFacade#getOrPutBundle(java.util.Map, java.util.Map, java.lang.String, boolean, boolean)
   */
  public Map getOrPutBundle(final Map arg0, final Map arg1, final String arg2, final boolean arg3, final boolean arg4) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
    if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0, arg1, arg2, arg3, arg4);
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
   * @see com.sap.util.cache.CacheFacade#getOrPutBundle(java.util.Map, java.util.Map, java.lang.String, boolean, byte)
   */
  public Map getOrPutBundle(final Map arg0, final Map arg1, final String arg2, final boolean arg3, final byte arg4) throws CacheException {
    final Map[] result = new Map[1];
    final Set keys = arg0.keySet();
    result[0] = getBundle(keys);
    if (result[0] == null) {
      region.execute(keys.toString(), new Runnable() {
        public void run() {
          result[0] = getBundle(keys);
          if (result[0] == null) {
            try {
              putBundle(arg0, arg1, arg2, arg3, arg4);
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

  public long getRegionSize() {
  	long result = 0;
  	Set groupNames = region.getCacheGroupNames();
  	Iterator iterator = groupNames.iterator();
  	while (iterator.hasNext()) {
  	  String groupName = (String) iterator.next();
  	  CacheGroup group = region.getCacheGroup(groupName);
  	  Set keys = group.keySet();
  	  Iterator keyIterator = keys.iterator();
  	  while (keyIterator.hasNext()) {
  	  	result += region.storage.size( (String) (keyIterator.next()));
  	  }
  	}
	return result;
  }
  
  public int getRegionCalculatedSize() throws IllegalArgumentException, IllegalAccessException {
    int size = 0;
    synchronized (eraseSync) {
      size = SizeOf.estimate(region.storage.values(), region.sizeCalcDepth, true);
    }
    return size;
  }
  
}
