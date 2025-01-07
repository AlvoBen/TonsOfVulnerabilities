package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.util.LexicalParser;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 06-Mar-02, 15:09:04
 */
public class FundamentalFacetsString extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    value.addValue(STRING_RESTRICTION_VALUE_SPACE_ID, strValue);
    value.setLengthDeterminigValue(strValue);
    return(value);
  }
}

