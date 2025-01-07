package com.sap.engine.lib.schema.components;

import com.sap.engine.lib.schema.validator.regexp.RegularExpression;

/**
 * @author Nick Nickolov
 * @version November 2001
 */
public interface Facet extends Base {

  public static final String[] NAMES_OF_FACETS = {"NONE", "minExclusive", "minInclusive", "maxExclusive", "maxInclusive", "totalDigits", "fractionDigits", "length", "minLength", "maxLength", "pattern", "enumeration", "whiteSpace", };

  int F_MIN_EXCLUSIVE = 1;
  int F_MIN_INCLUSIVE = 2;
  int F_MAX_EXCLUSIVE = 3;
  int F_MAX_INCLUSIVE = 4;
  int F_TOTAL_DIGITS = 5;
  int F_FRACTION_DIGITS = 6;
  int F_LENGTH = 7;
  int F_MIN_LENGTH = 8;
  int F_MAX_LENGTH = 9;
  int F_PATTERN = 10;
  int F_ENUMERATION = 11;
  int F_WHITESPACE = 12;
  
  public String getName();

  public String getValue();

  public boolean isFixed();

  public boolean match(Facet facet);
  
  public RegularExpression getRegularExpression();
}