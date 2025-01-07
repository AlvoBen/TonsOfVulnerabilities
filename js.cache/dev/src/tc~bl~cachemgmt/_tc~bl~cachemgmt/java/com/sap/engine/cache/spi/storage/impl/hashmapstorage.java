package com.sap.engine.cache.spi.storage.impl;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.cache.util.dump.LogUtil;
import com.sap.util.cache.AttributeNames;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;
import com.sap.util.cache.spi.Pluggable;
import com.sap.util.cache.spi.PluginContext;
import com.sap.util.cache.spi.storage.StoragePlugin;

/*
 * Created by IntelliJ IDEA.
 * User: Petio-P
 * Date: Feb 23, 2004
 * Time: 3:31:07 PM
 * To change this template use Options | File Templates.
 */
public class HashMapStorage implements StoragePlugin {
  
  private Map groups = null;
 
  protected Map hashmap = null;

  protected String regionName = null;


  public Pluggable getInstance() throws PluginException {
    return new HashMapStorage();
  }

  public void setPluginContext(PluginContext ctx) {
    this.regionName = ctx.getRegionName();
  }

  protected Map attributes = null;
  protected Map systemAttributes = null;
  private String name = null;


  private HashSet keys = null;

  public HashMapStorage() {
    this("HashMapStorage");
  }

  public HashMapStorage(String name) {
    this.name = name;
    this.keys = new HashSet();
  }

  public Object transport(Object cachedObject) {
    return null;
  }

  public Object recreateTransported(Object transportable) {
    return transportable;
  }

  public Object get(String key) {
    return hashmap.get(key);
  }

  /**
   * A get from the storage
   *
   * @param key The cached object key
   * @param copy Denotes the type of the returned object - a copy or an actual reference to the original copy
   * @return null if the object cannot be retreived, otherwise the cached object bound to the key
   * @throws NullPointerException if the key is null
   */
  public Object get(String key, boolean copy) {
    return get(key);
  }

  /**
   * Get of attributes from the storage
   *
   * @param key The cached object key that the method will return attributes for
   * @return The attributes of the cached object (may be null)
   */
  public Map getAttributes(String key, boolean copy) {
    Map result = (Map) attributes.get(key);
    return result;
  }

  public Map getSystemAttributes(String key, boolean copy) {
    Map result = (Map) systemAttributes.get(key);
    return result;
  }

  public void evict(String key) {
    // DumpWriter.dump("EVICTION (storage) : " + key);
    remove(key);
  }

  //!!! removes the key and the attributes
  public void remove(String key) {
    hashmap.remove(key);
    keys.remove(key);
    attributes.remove(key);
    // remove group info
    Map sysA = (Map) this.systemAttributes.get(key);
    if (sysA != null) {
      String oldGroup = (String) sysA.get(AttributeNames._GROUP_NAME);
      if (oldGroup != null) {
        Map oldGroupInfo = (Map) groups.get(oldGroup);
        if (oldGroupInfo != null) {
          oldGroupInfo.remove(key);
        }
      }
      systemAttributes.remove(key);
    }
  }
  
  public void invalidate(String key) {
    hashmap.remove(key);
  }

  public void put(String key, Object cachedObject) {
    hashmap.put(key, cachedObject);
    keys.add(key);
  }

  public void put(String key, Object cachedObject, Map attributes, Map systemAttributes) throws CacheException {
    hashmap.put(key, cachedObject);
    keys.add(key);
    putAttributes(key, attributes, systemAttributes);
  }

  public void put(String key, Object cachedObject, Map attributes, boolean system) throws CacheException {
    hashmap.put(key, cachedObject);
    keys.add(key);
    if (attributes != null) {
      if (system) {
        putSystemAttributes(key, attributes);
      } else {
        putAttributes(key, attributes);
      }
    }
  }

  /**
   * Put of attributes into the storage.
   *
   * @param key The cached object key that the attributes will belong to
   * @param attributes The attributes of the cached object, bound to the key
   */
  public void putAttributes(String key, Map attributes) {
    if (attributes != null) {
      keys.add(key);
      this.attributes.put(key, attributes);
    }
  }

  public void putSystemAttributes(String key, Map attributes) throws CacheException {
    if (attributes != null) {
      keys.add(key);
      Iterator ati = attributes.keySet().iterator();
      if (attributes.containsKey(AttributeNames._GROUP_NAME)) {
        String group = (String) attributes.get(AttributeNames._GROUP_NAME);

        Map sysA = (Map) this.systemAttributes.get(key);
        if (sysA != null) {
          String oldGroup = (String) sysA.get(AttributeNames._GROUP_NAME);
          if (oldGroup != null) {
            Map oldGroupInfo = (Map) groups.get(oldGroup);
            if (oldGroupInfo != null) {
              oldGroupInfo.remove(key);
            }
          }
        }

        Map groupInfo = (Map) groups.get(group);
        if (groupInfo == null) {
          groupInfo = new HashMap();
          groups.put(group, groupInfo);
        }
        groupInfo.put(key, key);
      }
      this.systemAttributes.put(key, attributes);
    } else {
      Map sysA = (Map) this.systemAttributes.get(key);
      if (sysA != null) {
        String oldGroup = (String) sysA.get(AttributeNames._GROUP_NAME);
        if (oldGroup != null) {
          Map oldGroupInfo = (Map) groups.get(oldGroup);
          if (oldGroupInfo != null) {
            oldGroupInfo.remove(key);
          }
        }
      }
      this.systemAttributes.remove(key);
      if (!hashmap.containsKey(key)) {
        keys.remove(key);
      }
    }
  }

  public void putAttributes(String key, Map attributes, Map systemAttributes) throws CacheException {
    if (attributes != null) {
      putAttributes(key, attributes);
    }
    if (systemAttributes != null) {
      putSystemAttributes(key, systemAttributes);
    }
  }

  public boolean exists(String key) {
    return hashmap.containsKey(key) || systemAttributes.containsKey(key) || attributes.containsKey(key);
  }

  public String getDescription() {
    return "Simple HashMap storage w/o transaction support \n\r No persistency \n\r No distribution \n\r No synchronization";
  }

  public String getName() {
    if (regionName != null) {
      return name + "@" + regionName;
    }
    return name;
  }

  public void shutdown() {
    hashmap = null;
    attributes = null;
    systemAttributes = null;
    keys = null;
    groups = null;
  }

  public void init(String name, Properties properties) {
    this.name = name;
  }

  public void start() {
    hashmap = new HashMap();
    keys = new HashSet();
    attributes = new HashMap();
    systemAttributes = new HashMap();
    groups = new HashMap();
  }

  public int getSize(String objectKey) {
    int size = 4;
    Object cachedObject = hashmap.get(objectKey);
    if (cachedObject == null) {
      size = 0;
    } else if (cachedObject instanceof byte[]) {
      size = ((byte[]) cachedObject).length;
    } else if (cachedObject instanceof char[]) {
      size = ((char[]) cachedObject).length * 2;
    } else if (cachedObject instanceof int[]) {
      size = ((int[]) cachedObject).length * 4;
    } else if (cachedObject instanceof long[]) {
      size = ((long[]) cachedObject).length * 8;
    } else {
      Map attributes = (Map)this.attributes.get(objectKey);
      if (attributes != null) {
        String weight = (String)attributes.get(AttributeNames.OBJECT_WEIGHT);
        if (weight != null) {
          size = (Integer.valueOf(weight)).intValue();
        }
      }
    }
    return size;
  }

  public int getAttributesSize(String objectKey) {
    return 0;
  }

  public void flush() throws CacheException {
  }

  public Set keySet() {
    return cloneSet(keys);
  }

  public void stop() {
  }

  public byte getScope() {
    return RegionConfigurationInfo.SCOPE_LOCAL;
  }
  
  // ------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------  
  // ------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------  
  // ------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------  
  // ------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------
  //------------------------------------------------------------------------------------------------------  

  /**
   * Gets the object associated with the specified key.
   *
   * @param group The cache group; if the <code>group</code> is not 
   *              <code>null</code>, the <code>key</code> must be in the
   *              group in order to a non-<code>null</code> return value;
   *              otherwise the whole cache region is considered
   * @param key   The cached object key
   * @param copy  indicates whether a copy of the original copy should
   *              be returned
   * 
   * @return the object associated with the specified key or 
   *         <code>null</code> if the object cannot be retrieved
   * 
   * @throws NullPointerException if the <code>group</code> or 
   *         <code>key</code> parameter is <code>null</code>
   */
  public Object get(String group, String key, boolean copy) {
    if (groups.containsKey(group)) {
      if (((Map)groups.get(group)).containsKey(key)) {
        return get(key, copy);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * Gets the user-defined attributes of the object associated with the 
   * specified key.
   *
   * @param group The cache group; if the <code>group</code> is not 
   *              <code>null</code>, the <code>key</code> must be in the
   *              group in order to a non-<code>null</code> return value;
   *              otherwise the whole cache region is considered
   * @param key   The cached object key the attributes are returned for
   * @param copy  indicates whether or not a copy of the attributes
   *              should be returned
   * 
   * @return The user-defined attributes of the cached object or 
   *         <code>null</code>
   * 
   * @throws NullPointerException if the <code>group</code> or the 
   *         <code>key</code> parameter is <code>null</code>
   */
  public Map getAttributes(String group, String key, boolean copy) {
    if (groups.containsKey(group)) {
      if (((Map)groups.get(group)).containsKey(key)) {
        return getAttributes(key, copy);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * Removes the elements inside the specified group. 
   * 
   * @param group the cache group to be deleted
   * 
   * @throws NullPointerException if the <code>group</code> parameter is
   *         set to <code>null</code>
   */        
  public void removeGroup(String group) {
    if (groups.containsKey(group)) {
      Iterator ingroup = ((Map) groups.get(group)).keySet().iterator();
      while (ingroup.hasNext()) {
        String key = ((String) ingroup.next()); 
        remove(key);
      }
      groups.remove(group);
    }
  }
      
  /**
   * Removes all elements in the cache region.
   */
  public void remove() {
    groups.clear();
    hashmap.clear();
    attributes.clear();
    keys.clear();
  }
      
  /**
   * Removes the elements inside the specified group.
   * 
   * @param group  the cache group to be deleted
   * @param delete if set to <code>true</code> the whole cache region is
   *               deleted; otherwise only the elements inside the groups
   *               are removed
   * 
   * @throws NullPointerException if the <code>group</code> parameter is
   *         set to <code>null</code>
   */
  public void removeGroup(String group, boolean delete) {
    removeGroup(group);
  }
      
  /**
   * Removes the object associated with the specified <code>key</code>
   * from the cache.
   *
   * @param group The cache group; if the <code>group</code> is not 
   *              <code>null</code>, the <code>key</code> must be in the
   *              group in order to get removed;
   *              otherwise the whole cache region is considered
   * @param key   The cached object key to be deleted
   *  
   * @throws NullPointerException if the <code>group</code> or the 
   *         <code>key</code> is <code>null</code>
   */
  public void remove(String group, String key) {
    if (groups.containsKey(group)) {
      if (((Map)groups.get(group)).containsKey(key)) {
        remove(key);
      }
    }
  }
  
  /**
   * Invalidates all elements in the specified group. The difference to a 
   * <code>removeGroup()</code> operation is that the <code>keys</code> and
   * the attributes remain in the cache.
   * 
   * @param group the group name
   * 
   * @throws NullPointerException if the <code>group</code> parameter is
   *         <code>null</code>
   */
  public void invalidateGroup(String group) {
    if (groups.containsKey(group)) {
      Iterator ingroup = ((Map) groups.get(group)).keySet().iterator();
      while (ingroup.hasNext()) {
        String key = ((String) ingroup.next()); 
        invalidate(key);
      }
    }
  }
  
  /**
   * Invalidates all elements in the cache. 
   */
  public void invalidate() {
    hashmap.clear();
  }
      
  /**
   * Invalidates the key of the specified group. The difference to a 
   * <code>removeGroup()</code> operation is that the <code>keys</code> and
   * the attributes remain in the cache.
   * 
   * @param group the group name
   * @param key   the key of the group to be returned
   * 
   * @throws NullPointerException if either argument is <code>null</code>
   */
  public void invalidate(String group, String key) {
    if (groups.containsKey(group)) {
      if (((Map)groups.get(group)).containsKey(key)) {
        hashmap.remove(key);
      }
    }
  }
      
  /**
   * Returns the number of cached objects.
   * 
   * @return the number of cached objects
   */
  public int size() {
    return hashmap.size();
  }
      
  /**
   * Returns the number of cached objects.
   * 
   * @param group the cache group whose number of objects should be returned;
   *              if set to <code>null</code> the whole cache region is
   *              considered
   * 
   * @return the number of cached objects
   * 
   * @throws NullPointerException if the <code>group</code> parameter 
   *         is <code>null</code>
   */
  public int size(String group) {
    if (groups.containsKey(group)) {
      return ((Map)groups.get(group)).size();
    } else {
      return 0;
    }
  }
      
  /**
   * Checks whether the storage plugin has stored some objects.
   * 
   * @return <code>true</code> if some objects are stored; otherwise
   *         <code>false</code> is returned 
   */
  public boolean isEmpty() {
    return hashmap.isEmpty();
  }
      
  /**
   * Checks whether the storage plugin has stored some objects.
   * 
   * @param group the cache group to be checked for emptiness; if 
   *              set to <code>null</code> the whole cache region is
   *              considered
   * 
   * @return <code>true</code> if some objects are stored; otherwise
   *         <code>false</code> is returned
   *  
   * @throws NullPointerException if the <code>group</code> parameter 
   *         is <code>null</code>
   */
  public boolean isEmpty(String group) {
    if (groups.containsKey(group)) {
      return ((Map)groups.get(group)).isEmpty();
    } else {
      return true;
    }
  }
      
  /**
   * Inserts a cache group.
   * 
   * @param group   the cache group to be inserted
   * 
   * @throws NullPointerException if the <code>group</code> parameter is 
   *         <code>null</code>
   */
  public void insertGroup(String group) {
    groups.put(group, new HashMap());  
  }
          
  /**
   * Returns a key set of specified group.
   * 
   * @return key set of the specified group
   * 
   * @throws NullPointerException if the specified group parameter is
   *         <code>null</code>
   */
  public Set keySet(String group) {
    if (group == null) {
      return cloneSet(keys);
    } else if (groups.containsKey(group)) {
      return cloneSet(((Map)groups.get(group)).keySet());
    } else {
      return new HashSet();
    }
  }
      
  /**
   * Returns the values of the whole cache region.
   * 
   * @return the values of the whole cache region as a collection
   */
  public Collection values() {
    return hashmap.values();
  }
      
  /**
   * Returns the values of the specified group.
   * 
   * @param group the values of the specified group as a collection
   * 
   * @throws NullPointerException if the specified <code>group</code> 
   *                              parameter is <code>null</code>
   */
  public Collection values(String group) {
    Collection result = new HashSet();
    if (groups.containsKey(group)) {
      Iterator ingroup = ((Map) groups.get(group)).keySet().iterator();
      while (ingroup.hasNext()) {
        String key = ((String) ingroup.next());
        Object value = hashmap.get(key);
        if (value != null) { 
          result.add(hashmap.get(key));
        }
      }
    }
    return result;
  }
      
  /**
   * Returns an entry set of the whole cache region.
   * 
   * @return the entry set of the whole cache region
   */
  public Set entrySet() {
    return ((Map)(((HashMap)hashmap).clone())).entrySet();
  }
  
  /**
   * Returns an entry set of the specified cache region.
   * 
   * @param group the group name whose entry set is to be returned
   * 
   * @return the entry set of the whole cache region
   */
  public Set entrySet(String group) {
    try {
      final Object[] keys = keySet(group).toArray();
      final Map.Entry[] entries = new Map.Entry[keys.length];
      
      for (int i = 0; i < keys.length; i++) {
        final Object k = keys[i];
        final Object v = hashmap.get(k);
        // create entry
        entries[i] = new Map.Entry() {
          Object mv = v;
					public int hashCode() { return k.hashCode(); }
					public Object getKey() { return k; }
					public Object getValue() { return v; }
					public boolean equals(Object o) {	return k == null ? false : k.equals(o); }
					public Object setValue(Object value) { Object ov = mv; mv = value; return ov; };
				};
      }
      
      return new Set() {
        public int size() {
          return keys.length;
        }

        public boolean isEmpty() {
          return keys.length == 0;
        }

        public boolean contains(Object o) {
          for (int i = 0; i < keys.length; i++) {
            if (o.equals(keys[i])) {
              return true;
            }
          }
          return false;
        }

        public Iterator iterator() {
          return new Iterator() {
            int cnt = 0;
            public boolean hasNext() {
              return cnt < keys.length;
            }
            public Object next() {
              cnt++;
              return keys[cnt - 1];
            }
            public void remove() {
              cnt++;
            }
          };
        }

        public Object[] toArray() {
          return keys;
        }

        public Object[] toArray(Object a[]) {
          return keys;
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
   * Gets a set of the available groups.
   * 
   * @return set of the available groups
   */
  public Set getGroupSet() {
    return cloneSet(groups.keySet());
  }
      
  /**
   * Checks whether the specified group exists.
   * 
   * @return <code>true</code> if the specified group already exists;
   *         otherwise <code>false</code> is returned
   * 
   * @throws NullPointerException if the <code>group</code> parameter
   *         is <code>null</code>
   */
  public boolean existsGroup(String group) {
    return groups.containsKey(group);
  }
  
  private Set cloneSet(Set origin) {
    try {
      final Object[] keys = origin.toArray();
      return new Set() {
        public int size() {
          return keys.length;
        }

        public boolean isEmpty() {
          return keys.length == 0;
        }

        public boolean contains(Object o) {
          for (int i = 0; i < keys.length; i++) {
            if (o.equals(keys[i])) {
              return true;
            }
          }
          return false;
        }

        public Iterator iterator() {
          return new Iterator() {
            int cnt = 0;
            public boolean hasNext() {
              return cnt < keys.length;
            }
            public Object next() {
              cnt++;
              return keys[cnt - 1];
            }
            public void remove() {
              cnt++;
            }
          };
        }

        public Object[] toArray() {
          return keys;
        }

        public Object[] toArray(Object a[]) {
          return keys;
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

	public boolean exists(String group, String key) {
    if (groups.containsKey(group)) {
      if (((Map)groups.get(group)).containsKey(key)) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
	}
      
}
