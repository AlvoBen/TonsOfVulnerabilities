/*
 * Created on 2005-6-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.components.FundamentalFacets;
import com.sap.engine.lib.schema.validator.ReusableObjectsPool;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class FundamentalFacetsBasic implements FundamentalFacets {

  public Value parse(String strValue) {
    return(parse(strValue, (ReusableObjectsPool)null));
  }
  
  public Value parse(String strValue, ReusableObjectsPool pool) {
    Value value = createValue(pool);
    return(parse(strValue, value));
  }
  
  private Value createValue(ReusableObjectsPool pool) {
    return(pool == null ? new Value() : pool.getFFacetValue());
  }
  
  protected abstract Value parse(String strValue, Value value);
}
