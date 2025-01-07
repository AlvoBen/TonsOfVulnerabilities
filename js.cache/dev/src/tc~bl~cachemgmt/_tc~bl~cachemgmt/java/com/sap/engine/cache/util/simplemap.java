/*
 * Created on 2004.7.7
 *
 */
package com.sap.engine.cache.util;

import java.util.Map;

/**
 * @author petio-p
 *
 */
public interface SimpleMap {
  
  public Map getAggregate();
  public Object get(Object key);
  public void put(Object key, Object value);
  public void remove(Object key);

}
