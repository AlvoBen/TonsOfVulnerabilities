﻿package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.components.FundamentalFacets;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-23
 * Time: 11:32:00
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsNonPositiveInteger extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    BigDecimal decimal = LexicalParser.parseNonPositiveInteger(strValue);
    if(decimal == null) {
      return(null);
    }
    value.addValue(DECIMAL_RESTRICTION_VALUE_SPACE_ID, decimal);
    return(value);
  }
}
