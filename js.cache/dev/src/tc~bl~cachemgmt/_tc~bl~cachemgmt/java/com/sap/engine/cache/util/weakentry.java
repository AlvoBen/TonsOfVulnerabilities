package com.sap.engine.cache.util;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Petev, Petio, i024139
 */
public class WeakEntry extends WeakReference implements SimpleEntry {

  private Object key = null;
  private SimpleMap map = null;

  public WeakEntry(Object key, Object value, SimpleMap map, ReferenceQueue queue) {
    super(value, queue);
    this.key = key;
    this.map = map;
  }

  public Object getKey() {
    return key;
  }

	/* (non-Javadoc)
	 * @see com.sap.engine.cache.util.SimpleEntry#getSimpleMap()
	 */
	public SimpleMap getSimpleMap() {
    return map;
	}

}
