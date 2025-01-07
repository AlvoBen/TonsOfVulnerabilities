package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.util.LexicalParser;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-18
 * Time: 17:15:31
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsHexBinary extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    byte[] byteArr = LexicalParser.parseHexBinary(strValue);
    if(byteArr == null) {
      return(null);
    }
    value.addValue(HEX_BINARY_RESTRICTION_VALUE_SPACE_ID, byteArr);
    value.setLengthDeterminigValue(byteArr);
    return(value);
  }
}
