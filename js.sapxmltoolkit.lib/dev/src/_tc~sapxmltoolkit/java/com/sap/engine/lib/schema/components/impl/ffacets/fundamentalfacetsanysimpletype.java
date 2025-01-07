package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.components.FundamentalFacets;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-1-20
 * Time: 10:40:22
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsAnySimpleType extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    return(value);
  }
}
