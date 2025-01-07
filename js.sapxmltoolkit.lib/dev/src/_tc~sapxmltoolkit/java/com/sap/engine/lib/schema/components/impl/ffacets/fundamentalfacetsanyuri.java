package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.components.FundamentalFacets;
import com.sap.engine.lib.schema.util.LexicalParser;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-23
 * Time: 11:26:45
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsAnyURI extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    String string = LexicalParser.parseURIReference(strValue);
    if(string == null) {
      return(null);
    }
    value.addValue(ANY_URI_RESTRICTION_VALUE_SPACE_ID, string);
    value.setLengthDeterminigValue(string);
    return(value);
  }
}
