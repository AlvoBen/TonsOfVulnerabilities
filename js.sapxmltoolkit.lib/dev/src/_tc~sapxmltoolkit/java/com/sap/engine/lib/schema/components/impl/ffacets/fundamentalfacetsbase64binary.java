package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.components.FundamentalFacets;

/**
 * @author ivan-m
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public final class FundamentalFacetsBase64Binary extends FundamentalFacetsBasic {
	
  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    byte[] byteArr = LexicalParser.parseBase64Binary(strValue);
    if(byteArr == null) {
      return(null);
    }
    value.addValue(BASE_64_BINARY_RESTRICTION_VALUE_SPACE_ID, byteArr);
    value.setLengthDeterminigValue(byteArr);
    return(value);
  }
}
