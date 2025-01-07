/*
 * Created on 2005-9-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.components.impl.ffacets;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class FundamentalFacetsUnion extends FundamentalFacetsString {

  private static final FundamentalFacetsUnion sceleton = new FundamentalFacetsUnion(); 
  
  private FundamentalFacetsUnion() {
  }
  
  public static FundamentalFacetsUnion newInstance() {
    return(sceleton);
  }
  
}
