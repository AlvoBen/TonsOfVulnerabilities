/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import java.util.Hashtable;

import com.sap.util.cache.CacheRegion;

public class HashtableGetEntity extends FacadeBenchAdaptor {
  
  Hashtable _facade = new Hashtable();
  
  private void populate() {
    for (int i = 0; i < factor; i++) {
      _facade.put(Names.key[i], CommonPool.cachedObject);
    }
  }
  
  private void cleanup() {
    for (int i = 0; i < factor; i++) {
      _facade.remove(Names.key[i]);
    }
  }
  
  public void init(CacheRegion region) {
    super.init(region);
    populate();
  }
  
  public void close() {
    super.close();
    cleanup();
  }
  
  public void singleOperation() {
    for (int i = 0; i < factor; i++) {
      _facade.get(Names.key[i]);
    }
  }
  
  public String getName() {
    return "Hashtable Get Operations / Sec";
  }

}
