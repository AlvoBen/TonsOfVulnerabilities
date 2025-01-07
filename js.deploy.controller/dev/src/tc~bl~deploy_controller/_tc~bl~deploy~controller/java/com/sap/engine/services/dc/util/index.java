package com.sap.engine.services.dc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Title: J2EE Deployment Team Description: Provides an index functionality for
 * instances of <code>Object</code>. <code>Index</code> represents a
 * multi-valued mapping such that any instance of <code>Object</code> (a key
 * object) is mapped to zero or more different instances of <code>Object</code>
 * (the value objects). Both key and value objects may be <code>null</code>.
 * <code>Index</code> also provides methods for modification of and access to
 * the represented mapping.
 * 
 * <p>
 * Two instances of value objects <code>valueOne</code> and
 * <code>valueTwo</code> are regarded as different, if either exactly one of
 * them is <code>null</code> or <code>valueOne.equals(valueTwo)</code> returns
 * <code>false</code>.
 * </p>
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class Index {
	/**
	 * The data structure of this <code>Index</code>. <code>index</code>
	 * consists of <code>Object</code>-<code>Set</code> mappings, with
	 * <code>Object</code> representing the key object and <code>Set</code>
	 * representing the set of value objects.
	 */
	private Map index = new HashMap();

	private Index() {
	}

	/**
	 * Creates an instance of <code>Index</code> such that any key object is
	 * mapped to exactly zero value objects.
	 * 
	 * @return an initial instance of <code>Index</code>
	 */
	public final static Index createIndex() {
		return new Index();
	}

	/**
	 * Adds a value object to this <code>Index</code> such that the value object
	 * is mapped by the key object. More formally, after invoking
	 * <code>addToIndex(key, value)</code> the condition
	 * <code>getObjectsIndexedBy(key).contains(value) == true</code> holds.
	 * 
	 * @param key
	 *            the key object
	 * @param value
	 *            the value object
	 */
	public void addToIndex(Object key, Object value) {
		getValueSet(key).add(value);
	}

	/**
	 * Adds an empty entry in the <code>Index</code>. More formally, after
	 * invoking the operation <code>getObjectsIndexedBy(key)</code> the result
	 * will be an empty <code>Set</code>.
	 * 
	 * @param key
	 *            the key object
	 */
	public void addEmptyEntryToIndex(Object key) {
		if (index.get(key) == null) {
			createInitialValueSet(key);
		}
	}

	/**
	 * Removes a value object from this <code>Index</code> such that the value
	 * objects is not mapped by the key object (anymore). More formally, after
	 * invoking <code>removeFromIndex(key, value)</code> the condition
	 * <code>getObjectsIndexedBy(key).contains(value) == false</code> holds.
	 * 
	 * @param key
	 *            the key object
	 * @param value
	 *            the value object
	 */
	public void removeFromIndex(Object key, Object value) {
		getValueSet(key).remove(value);
	}

	/**
	 * Removes the entry (key&values) object from this <code>Index</code>. More
	 * formally, after invoking the operation
	 * <code>getObjectsIndexedBy(key)</code> the result will be an empty
	 * <code>Set</code>.
	 * 
	 * @param key
	 *            the key object
	 * @param value
	 *            the value object
	 */
	public void removeFromIndex(Object key) {
		index.remove(key);
	}

	/**
	 * Returns an immutable <code>Set</code> of value objects that are mapped by
	 * the key object in this <code>Index</code>. In case no value objects are
	 * mapped by the key object, an empty <code>Set</code> is returned.
	 * 
	 * @param key
	 *            the key object
	 * @return an immutable <code>Set</code> of all value objects mapped by the
	 *         specified key object
	 */
	public Set getObjectsIndexedBy(Object key) {
		return Collections.unmodifiableSet(getValueSet(key));
	}

	/**
	 * Initializes this <code>Index</code> such that any key object is mapped
	 * onto exactly zero value objects. More formally, after invoking
	 * <code>clear()</code> on this <code>Index</code>, for any key object
	 * <code>key</code> and any value object <code>value</code> the condition
	 * <code>getObjectsIndexedBy(key).contains(value) == false</code> holds.
	 */
	public void clear() {
		index.clear();
	}

	private Set getValueSet(Object key) {
		if (index.get(key) == null) {
			createInitialValueSet(key);
		}

		return (Set) index.get(key);
	}

	private void createInitialValueSet(Object key) {
		index.remove(key);
		index.put(key, new HashSet());

		return;
	}

}
