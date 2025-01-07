package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.util.LexicalParser;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-23
 * Time: 11:27:38
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsNotation extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    String string = LexicalParser.parseQName(strValue);
    if(string == null) {
      return(null);
    }
    value.addValue(NOTATION_RESTRICTION_VALUE_SPACE_ID, string);
    value.setLengthDeterminigValue(string);
    return(value);
  }
}
