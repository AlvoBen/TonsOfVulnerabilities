package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.components.FundamentalFacets;

import java.util.Calendar;

public final class FundamentalFacetsDateTime extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    Calendar calendar = LexicalParser.parseDateTime(strValue);
    if(calendar == null) {
      return(null);
    }
    value.addValue(DATE_TIME_RESTRICTION_VALUE_SPACE_ID, calendar);
    return(value);
  }
}
