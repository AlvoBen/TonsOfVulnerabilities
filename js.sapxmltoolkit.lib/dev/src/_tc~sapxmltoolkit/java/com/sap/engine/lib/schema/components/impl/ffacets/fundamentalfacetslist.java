/*
 * Created on 2005-9-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.components.impl.ffacets;

import java.util.StringTokenizer;

import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.xml.Symbols;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class FundamentalFacetsList extends FundamentalFacetsBasic {
  
  private static final FundamentalFacetsList sceleton = new FundamentalFacetsList(); 
  
  private FundamentalFacetsList() {
  }
  
  public static FundamentalFacetsList newInstance() {
    return(sceleton);
  }
  
  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    value.addValue(STRING_RESTRICTION_VALUE_SPACE_ID, strValue);
    value.setLength(determineLength(strValue));
    return(value);
  }
  
  private int determineLength(String strValue) {
    if(strValue.length() == 0) {
      return(0);
    }
    int length = 0;
    boolean incrementLength = false;
    for(int i = 0; i < strValue.length(); i++) {
      if(!Symbols.isWhitespace(strValue.charAt(i))) {
        if(!incrementLength) {
          length++;
          incrementLength = true;
        }
      } else if(incrementLength) {
        incrementLength = false;
      }
    }
    return(length);
  }
}
