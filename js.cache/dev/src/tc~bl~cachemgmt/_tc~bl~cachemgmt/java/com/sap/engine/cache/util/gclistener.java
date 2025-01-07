/*
 * Created on 2004.7.7
 *
 */
package com.sap.engine.cache.util;

/**
 * @author petio-p
 *
 */
public interface GCListener {

	/**
   * Called when an object with specific key is garbage collected
   * 
	 * @param key The key of the object that is garbage collected
	 */
	void garbageCollected(Object key, SimpleMap map);

}
