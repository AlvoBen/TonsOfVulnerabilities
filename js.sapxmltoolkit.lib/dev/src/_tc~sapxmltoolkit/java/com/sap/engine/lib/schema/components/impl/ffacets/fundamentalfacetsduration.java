package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.components.FundamentalFacets;
import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.util.Duration;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-19
 * Time: 11:20:24
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsDuration extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, value);
    Duration duration = LexicalParser.parseDuration(strValue);
    if(duration == null) {
      return(null);
    }
    value.addValue(DURATION_RESTRICTION_VALUE_SPACE_ID, duration);
    return(value);
  }
}
