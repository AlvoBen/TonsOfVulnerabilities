/*
 * Created on 2004-10-20
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.cache.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * @author ilian-n
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SoftHashMapEntry extends SoftReference {

  private Object key = null;
  private HashMap map = null;

  public SoftHashMapEntry(Object key, Object value, HashMap map, ReferenceQueue queue) {
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
  public HashMap getHashMap() {
    return map;
  }  

}
