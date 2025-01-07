package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.components.FundamentalFacets;

import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-23
 * Time: 11:21:21
 * To change this template use Options | File Templates.
 */
public final class FundamentalFacetsGMonthDay extends FundamentalFacetsBasic {

  protected Value parse(String strValue, Value value) {
    value.addValue(ANY_SIMPLE_TYPE_RESTRICTION_VALUE_SPACE_ID, strValue);
    Calendar calendar = LexicalParser.parseMonthDayTimeZone(strValue);
    if(calendar == null) {
      return(null);
    }
    value.addValue(G_MONTH_DAY_RESTRICTION_VALUE_SPACE_ID, calendar);
    return(value);
  }
}
