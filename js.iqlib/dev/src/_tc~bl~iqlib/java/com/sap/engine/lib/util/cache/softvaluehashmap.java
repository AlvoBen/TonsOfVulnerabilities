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
 * This is an implementation of SoftValueHashMap.
 *
 * WARNING: The class is not synchronized... !!! Dios, synchronize it, please
 *
 * The values of the entries are java.lang.ref.SoftReference instances this class
 * is very useful for implementing memmory sensitive cache.
 *
 * <p> Suppose that the garbage collector determines at a certain point in time
 * that an object is <a href="package-summary.html#reachability">softly
 * reachable</a>.  At that time it may choose to clear atomically all soft
 * references to that object and all soft references to any other
 * softly-reachable objects from which that object is reachable through a chain
 * of strong references.  At the same time or at some later time it will
 * enqueue those newly-cleared soft references that are registered with
 * reference queues.
 *
 * <p> All soft references to softly-reachable objects are guaranteed to have
 * been cleared before the virtual machine throws an
 * OutOfMemoryError.  Otherwise no constraints are placed upon the
 * time at which a soft reference will be cleared or the order in which a set
 * of such references to different objects will be cleared.  Virtual machine
 * implementations are, however, encouraged to bias against clearing
 * recently-created or recently-used soft references.
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class SoftValueHashMap {

  ///////////////////////////////////////////////////////////////////////////////////
  //                      SoftValuesHashMap global declarations                    //
  ///////////////////////////////////////////////////////////////////////////////////

  protected CacheListener cacheListener = null;

  /* Hash table mapping keys to SoftValues */
  private Hashtable hash;

  /* This is the group to which this weak part belongs to */
  private CacheGroup group = null;

//  /* It's an old idea it saves the using of thread but seems slow,
//   * this SoftValueHashMap will be used by cache so the slow idea is not good :)
//   *
//   * Remove all invalidated entries from the map, that is, remove all entries
//   * whose values have been discarded.  This method should be invoked once by
//   * each public mutator in this class.  We don't invoke this method in
//   * public accessors because that can lead to surprising
//   * ConcurrentModificationExceptions.
//   *
//   * This method is invoked when the Map field is going to be changed doesn't matter
//   * if the ReferenceQueue has or hasn't elements.
//   */
//  private void processQueue() {
//    SoftValue softValue;
//    while ((softValue = (SoftValue)queue.poll()) != null) {
//      hash.remove(softValue.key);
//    }
//  }

  ////////////////////////////////////////////////////////////////////////////////
  //                              Constructors                                  //
  ////////////////////////////////////////////////////////////////////////////////

  /**
   * Constructs a new, empty SoftValueHashMap with the given
   * initial capacity and the given load factor.
   *
   * @param  initialCapacity  The initial capacity of the
   *                          SoftValueHashMap
   *
   * @param  loadFactor       The load factor of the SoftValueHashMap
   *
   * @throws IllegalArgumentException  If the initial capacity is less than
   *                                   zero, or if the load factor is
   *                                   nonpositive
   */
  public SoftValueHashMap(int initialCapacity, float loadFactor, CacheGroup _group) {
    hash = new Hashtable(initialCapacity, loadFactor);
    this.group = _group;
  }

  /**
   * Constructs a new, empty SoftValueHashMap with the given
   * initial capacity and the default load factor, which is
   * 0.75.
   *
   * @param  initialCapacity  The initial capacity of the
   *                          SoftValueHashMap
   *
   * @throws IllegalArgumentException  If the initial capacity is less than
   *                                   zero
   */
  public SoftValueHashMap(int initialCapacity, CacheGroup _group) {
    hash = new Hashtable(initialCapacity);
    this.group = _group;
  }

  /**
   * Constructs a new, empty SoftValueHashMap with the default
   * initial capacity and the default load factor, which is
   * 0.75.
   */
  public SoftValueHashMap(CacheGroup _group) {
    hash = new Hashtable();
    this.group = _group;
  }

  ////////////////////////////////////////////////////////////////////////////////
  //                       Proxy to java.util.Map interfac                      //
  ////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the number of key-value mappings in this map.  If the
   * map contains more than Integer.MAX_VALUE elements, returns
   * Integer.MAX_VALUE.
   *
   * @return the number of key-value mappings in this map.
   */
  public int size() {
    return hash.size();
  }

  /**
   * Returns true if this map contains no key-value mappings.
   *
   * @return true if this map contains no key-value mappings.
   */
  public boolean isEmpty() {
    return hash.isEmpty();
  }

  /**
   * Returns true if this map contains a mapping for the specified
   * key.
   *
   * @param key key whose presence in this map is to be tested.
   * @return true if this map contains a mapping for the specified
   * key.
   *
   * @throws ClassCastException if the key is of an inappropriate type for
   * 		  this map.
   * @throws NullPointerException if the key is null and this map
   *            does not not permit null keys.
   */
  public boolean containsKey(Object key) {
    return hash.containsKey(key);
  }

  /**
   * Returns true if this map maps one or more keys to the
   * specified value.  More formally, returns true if and only if
   * this map contains at least one mapping to a value v such that
   * (value==null ? v==null : value.equals(v)).  This operation
   * will probably require time linear in the map size for most
   * implementations of the Map interface.
   *
   * @param value value whose presence in this map is to be tested.
   * @return true if this map maps one or more keys to the
   *         specified value.
   */
  public boolean containsValue(Object value) {
    return hash.containsValue(value);
  }

  /**
   * Returns the value to which this map maps the specified key.  Returns
   * null if the map contains no mapping for this key.  A return
   * value of null does not <i>necessarily</i> indicate that the
   * map contains no mapping for the key; it's also possible that the map
   * explicitly maps the key to null.  The containsKey
   * operation may be used to distinguish these two cases.
   *
   * @param key key whose associated value is to be returned.
   * @return the value to which this map maps the specified key, or
   *	       null if the map contains no mapping for this key.
   *
   * @throws ClassCastException if the key is of an inappropriate type for
   * 		  this map.
   * @throws NullPointerException key is null and this map does not
   *		  not permit null keys.
   *
   * @see #containsKey(Object)
   */
  public Object get(Object key) {
    SoftValue obj = (SoftValue) hash.get(key);

    if (obj == null)
      return null;

    return obj;
  }

  /**
   * Associates the specified value with the specified key in this map
   * (optional operation).  If the map previously contained a mapping for
   * this key, the old value is replaced.
   *
   * @param key key with which the specified value is to be associated.
   * @param value value to be associated with the specified key.
   * @return previous value associated with specified key, or null
   *	       if there was no mapping for key.  A null return can
   *	       also indicate that the map previously associated null
   *	       with the specified key, if the implementation supports
   *	       null values.
   *
   * @throws UnsupportedOperationException if the put operation is
   *	          not supported by this map.
   * @throws ClassCastException if the class of the specified key or value
   * 	          prevents it from being stored in this map.
   * @throws IllegalArgumentException if some aspect of this key or value
   *	          prevents it from being stored in this map.
   * @throws NullPointerException this map does not permit null
   *            keys or values, and the specified key or value is
   *            null.
   */
  public Object put(Object key, Object value, boolean _alwaysInWeak) {
    return hash.put(key, new SoftValue(value, group.queue, key, this, _alwaysInWeak)); // we put a SoftValue as value in the hash-table the SoftValue will
  }                                                               // be stored a ReferenceQueue when they are garbage collected

  /**
   * Removes the mapping for this key from this map if present (optional
   * operation).
   *
   * @param key key whose mapping is to be removed from the map.
   * @return previous value associated with specified key, or null
   *	       if there was no mapping for key.  A null return can
   *	       also indicate that the map previously associated null
   *	       with the specified key, if the implementation supports
   *	       null values.
   * @throws UnsupportedOperationException if the remove method is
   *         not supported by this map.
   */
  public Object remove(Object key) {
    return hash.remove(key);
  }

  /**
   * Copies all of the mappings from the specified map to this map
   * (optional operation).  These mappings will replace any mappings that
   * this map had for any of the keys currently in the specified map.
   *
   * @param t Mappings to be stored in this map.
   *
   * @throws UnsupportedOperationException if the putAll method is
   * 		  not supported by this map.
   *
   * @throws ClassCastException if the class of a key or value in the
   * 	          specified map prevents it from being stored in this map.
   *
   * @throws IllegalArgumentException some aspect of a key or value in the
   *	          specified map prevents it from being stored in this map.
   *
   * @throws NullPointerException this map does not permit null
   *            keys or values, and the specified key or value is
   *            null.
   */
  public void putAll(Map t) {
    throw new UnsupportedOperationException("Not implemented");
  }

  /**
   * Removes all mappings from this map (optional operation).
   *
   * @throws UnsupportedOperationException clear is not supported by this
   * 		  map.
   */
  public void clear() {
    hash.clear();
  }

  /**
   * Returns a set view of the keys contained in this map.  The set is
   * backed by the map, so changes to the map are reflected in the set, and
   * vice-versa.  If the map is modified while an iteration over the set is
   * in progress, the results of the iteration are undefined.  The set
   * supports element removal, which removes the corresponding mapping from
   * the map, via the Iterator.remove, Set.remove,
   * removeAll retainAll, and clear operations.
   * It does not support the add or addAll operations.
   *
   * @return a set view of the keys contained in this map.
   */
  public Set keySet() {
    return hash.keySet();
  }

  /**
   * Returns a collection view of the values contained in this map.  The
   * collection is backed by the map, so changes to the map are reflected in
   * the collection, and vice-versa.  If the map is modified while an
   * iteration over the collection is in progress, the results of the
   * iteration are undefined.  The collection supports element removal,
   * which removes the corresponding mapping from the map, via the
   * Iterator.remove, Collection.remove,
   * removeAll, retainAll and clear operations.
   * It does not support the add or addAll operations.
   *
   * @return a collection view of the values contained in this map.
   */
  public Collection values() {
    return hash.values();
  }

  /**
   * Returns a set view of the mappings contained in this map.  Each element
   * in the returned set is a Map.Entry.  The set is backed by the
   * map, so changes to the map are reflected in the set, and vice-versa.
   * If the map is modified while an iteration over the set is in progress,
   * the results of the iteration are undefined.  The set supports element
   * removal, which removes the corresponding mapping from the map, via the
   * Iterator.remove, Set.remove, removeAll,
   * retainAll and clear operations.  It does not support
   * the add or addAll operations.
   *
   * @return a set view of the mappings contained in this map.
   */
  public Set entrySet() {
    return hash.entrySet();
  }

  /**
   * Registers a listener to this SoftValueHashMap
   */
  protected void registerListener(CacheListener _cacheListener) {
    this.cacheListener = _cacheListener;
  }

}