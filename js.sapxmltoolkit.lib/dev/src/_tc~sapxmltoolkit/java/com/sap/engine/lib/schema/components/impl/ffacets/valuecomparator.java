package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-2-26
 * Time: 14:42:29
 * To change this template use Options | File Templates.
 */
public final class ValueComparator implements Constants {

  public static int compare(Value value1, Value value2) {
    if(value1 == null || value2 == null ) {
      return(COMPARE_RESULT_NOT_EQUAL);
    }
    return(value1.compare(value2));
  }
}
