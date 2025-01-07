package com.sap.engine.cache.util;

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Petev, Petio, i024139
 */
public class SoftEntry extends SoftReference implements SimpleEntry {

  private Object key = null;
  private SimpleMap map = null;

  public SoftEntry(Object key, Object value, SimpleMap map, ReferenceQueue queue) {
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
