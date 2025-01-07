/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.refgraph.impl.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luchesar Cekov
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable {
  private static final long serialVersionUID = 393675624003824532L;

  private transient Map<E, Object> map;
  
  private volatile int sizeCounter;

  // Dummy value to associate with an Object in the backing Map
  private static final Object PRESENT = new Object();

  public ConcurrentHashSet() {
    map = new ConcurrentHashMap<E, Object>();
  }

  public ConcurrentHashSet(Collection<? extends E> c) {
    map = new ConcurrentHashMap<E, Object>(Math.max((int) (c.size() / .75f) + 1, 16));
    addAll(c);
  }

  public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
    map = new ConcurrentHashMap<E, Object>(initialCapacity, loadFactor, concurrencyLevel);
  }

  public ConcurrentHashSet(int initialCapacity) {
    map = new ConcurrentHashMap<E, Object>(initialCapacity);
  }

  public Iterator<E> iterator() {
    return map.keySet().iterator();
  }

  public int size() {
    return sizeCounter;
  }

  public boolean isEmpty() {
    return sizeCounter == 0;
  }

  public boolean contains(Object o) {
    return map.containsKey(o);
  }

  public boolean add(E o) {
	if (map.put(o, PRESENT) == null) {
		sizeCounter++;
		return true;
	}
	return false;
  }

  public boolean remove(Object o) {
	if (map.remove(o) == PRESENT) {
		sizeCounter--;
		return true;
	}
	return false;
  }

  public void clear() {
    map.clear();
    sizeCounter = 0;
  }
}