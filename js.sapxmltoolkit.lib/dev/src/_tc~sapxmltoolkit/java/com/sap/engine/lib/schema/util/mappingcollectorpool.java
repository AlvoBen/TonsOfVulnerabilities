/*
 * Created on 2005-5-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.util;

import java.util.Vector;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class MappingCollectorPool {
  
  private Vector keys;
  private Vector values;
  
  public MappingCollectorPool() {
    keys = new Vector();
    values = new Vector();
  }
  
  public void put(Object key, Object value) {
    int index = keys.indexOf(key);
    if(index < 0) {
      keys.add(key);
      values.add(value);
    } else {
      keys.set(index, key);
      values.set(index, value);
    }
  }
  
  public Object get(Object key) {
    int index = keys.indexOf(key);
    return(index < 0 ? null : values.get(index));
  }
  
  public Vector keys() {
    return(keys);
  }
  
  public Vector values() {
    return(values);
  }
  
  public void clear() {
    keys.clear();
    values.clear();
  }
  
  public int size() {
    return(keys.size());
  }
  
  public Object remove(int index) {
    keys.remove(index);
    return(values.remove(index));
  }
}
