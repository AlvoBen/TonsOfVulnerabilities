package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.components.FundamentalFacets;
import com.sap.engine.lib.schema.util.LexicalParser;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-23
 * Time: 11:22:24
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsBoolean extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    Boolean booleanObj = LexicalParser.parseBoolean(strValue);
    if(booleanObj == null) {
      return(null);
    }
    value.addValue(BOOLEAN_RESTRICTION_VALUE_SPACE_ID, booleanObj);
    return(value);
  }
}
