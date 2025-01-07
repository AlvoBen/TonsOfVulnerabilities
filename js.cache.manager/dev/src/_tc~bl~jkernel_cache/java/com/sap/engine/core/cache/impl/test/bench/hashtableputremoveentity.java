/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import java.util.Hashtable;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HashtablePutRemoveEntity extends FacadeBenchAdaptor {

  private Hashtable _facade = new Hashtable();
  
  public void singleOperation() {
    for (int i = 0; i < factor; i++) {
      _facade.put(Names.key[i], CommonPool.cachedObject);
      _facade.remove(Names.key[i]);
    }
  }

  public String getName() {
    return "Hashtable Put + Remove Operations / sec";
  }

}
