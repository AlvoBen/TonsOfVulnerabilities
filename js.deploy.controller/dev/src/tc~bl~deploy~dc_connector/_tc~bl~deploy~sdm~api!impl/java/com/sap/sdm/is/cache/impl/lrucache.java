package com.sap.sdm.is.cache.impl;

import java.util.HashMap;
import java.util.Map;

import com.sap.sdm.is.cache.Cache;
import com.sap.sdm.util.log.Trace;

/**
 * @author Java Change Management Sep 23, 2003
 */
final class LruCache implements Cache {
	private final static Trace trace = Trace.getTrace(LruCache.class);

	private final int maxSize;
	private Map newMap = new HashMap();
	private Map oldMap = new HashMap();

	LruCache(int maxSize) {
		this.maxSize = maxSize;

		trace.debug("LruCache created with maxSize=" + maxSize);
	}

	public synchronized Object put(Object key, Object value) {
		// value objects are not traced deliberately
		trace.debug("Associate key '" + key + "' to some value");

		Object previous = get(key);

		newMap.put(key, value);
		if (getCacheSize() >= maxSize) {
			trace.debug("Max cache size reached");
			rotateMaps();
		}

		return previous;
	}

	public synchronized Object get(Object key) {
		Object value;
		if (newMap.containsKey(key) == true) {
			trace.debug("Key '" + key + "' contained in new map");
			value = newMap.get(key);
		} else if (oldMap.containsKey(key) == true) {
			trace.debug("Key '" + key + "' contained in old map");
			value = oldMap.get(key);
			oldMap.remove(key);
			newMap.put(key, value);
		} else {
			trace.debug("Cache miss for key '" + key + "'");
			value = null;
		}

		return value;
	}

	private int getCacheSize() {
		return oldMap.size() + newMap.size();
	}

	private void rotateMaps() {
		trace.debug("Rotate maps. Old map: " + oldMap.size() + ", new map: "
				+ newMap.size());
		Map tmp = newMap;
		oldMap = newMap;
		newMap = new HashMap();
	}
}
