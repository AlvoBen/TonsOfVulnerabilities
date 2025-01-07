/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class FacadeBenchAdaptor extends BenchAdaptor {
  
  protected CacheFacade facade = null;
  protected static int factor = 10;

  public void init(CacheRegion region) {
    super.init(region);
    this.facade = region.getCacheFacade();
  }
  
  public int getFactor() {
    return factor;
  }
  
}
