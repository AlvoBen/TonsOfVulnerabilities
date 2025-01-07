package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.components.FundamentalFacets;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-23
 * Time: 11:30:29
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsInteger extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    BigDecimal decimal = LexicalParser.parseInteger(strValue);
    if(decimal == null) {
      return(null);
    }
    value.addValue(DECIMAL_RESTRICTION_VALUE_SPACE_ID, decimal);
    return(value);
  }
}
