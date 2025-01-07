/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.api;


import com.sap.engine.lib.util.HashMapIntObject;
import com.sap.engine.lib.util.HashMapIntInt;

import java.io.Serializable;
import java.util.Calendar;

/**
 * This class is the base cron field class
 */
public abstract class CronField implements Serializable {

  public final static int INFINITY = -1;
  protected String strEntry = "";
  protected int fldCode;
  protected static int FIELD_OK = 0;

  public final static int FIELD_YEAR = 1;
  public final static int FIELD_MONTH = 2;
  public final static int FIELD_MONTH_DAY = 3;
  public final static int FIELD_WEEK_DAY = 4;
  public final static int FIELD_HOUR = 5;
  public final static int FIELD_MINUTE = 6;

  protected final static int INVALID_FIELD_CODE = 1;

  protected final static HashMapIntObject fieldCodesToErrMsgs = new HashMapIntObject();
  final static HashMapIntInt fieldCodesToMinVals = new HashMapIntInt();
  final static HashMapIntInt fieldCodesToMaxVals = new HashMapIntInt();

  // constants
  static {    
    fieldCodesToErrMsgs.put(FIELD_YEAR, "Invalid year field value");   
    fieldCodesToErrMsgs.put(FIELD_MONTH, "Invalid month field value");
    fieldCodesToErrMsgs.put(FIELD_MONTH_DAY, "Invalid day of month field value");
    fieldCodesToErrMsgs.put(FIELD_WEEK_DAY, "Invalid day of week field value");
    fieldCodesToErrMsgs.put(FIELD_HOUR, "Invalid hour field value");
    fieldCodesToErrMsgs.put(FIELD_MINUTE, "Invalid minute field value");

    fieldCodesToMinVals.put(FIELD_YEAR, 1970);   
    fieldCodesToMinVals.put(FIELD_MONTH, 0);    
    fieldCodesToMinVals.put(FIELD_MONTH_DAY, 1);
    fieldCodesToMinVals.put(FIELD_WEEK_DAY, 1);
    fieldCodesToMinVals.put(FIELD_HOUR, 0);
    fieldCodesToMinVals.put(FIELD_MINUTE, 0);

    fieldCodesToMaxVals.put(FIELD_YEAR, -1);
    fieldCodesToMaxVals.put(FIELD_MONTH, 11);
    fieldCodesToMaxVals.put(FIELD_MONTH_DAY, 31);
    fieldCodesToMaxVals.put(FIELD_WEEK_DAY, 7);
    fieldCodesToMaxVals.put(FIELD_HOUR, 23);
    fieldCodesToMaxVals.put(FIELD_MINUTE, 59);
  }

  /**
   * Constructs and validates a cron field from given cron
   *
   * @param fieldCode the field type code
   * @param str       the cron
   */
  protected CronField(int fieldCode, String str) {
    fldCode = fieldCode;
    this.strEntry = str;
    validate(str);
  }

  /**
   * Constructs and validates a cron field from given single value
   *
   * @param fieldCode   the field type code
   * @param singleValue the single value
   */
  protected CronField(int fieldCode, int singleValue) {
    this(fieldCode, "" + singleValue);
  }

  /**
   * Constructs and validates a cron field from given value range, e.g. [start - end]
   *
   * @param fieldCode the field type code
   * @param start     the start value
   * @param end       the end value
   */
  protected CronField(int fieldCode, int start, int end) {
    this(fieldCode, start + "-" + end);
  }

  /**
   * Constructs and validates a cron field from given value range and step, e.g. [start - end]/step
   *
   * @param fieldCode the field type code
   * @param start     the start value
   * @param end       the end value
   * @param step      the step
   */
  protected CronField(int fieldCode, int start, int end, int step) {
    this(fieldCode, start + "-" + end + "/" + step);
  }

  static void putErrMsg(int code, String msg) {
    fieldCodesToErrMsgs.put(code, msg);
  }

  static String getErrMsg(int code) {
    return (String) fieldCodesToErrMsgs.get(code);
  }

  /**
   * Validates a cron field
   *
   * @param str the cron field
   */
  private void validate(String str) {
    int valCode = validate(str, fldCode);
    if (valCode != FIELD_OK) 
        throw new IllegalArgumentException((String) fieldCodesToErrMsgs.get(fldCode));
  }

  // should be comma separated list
  protected static int validate(String field, int fieldCode) {
    String[] fieldParts = field.split(",");
    for (int i = 0; i < fieldParts.length; i++) {
      int retCode = validateFieldPart(fieldParts[i], fieldCode);
      if (retCode != FIELD_OK) return retCode;
    }
    return FIELD_OK;
  }

  // should be <*>, <.> slashed descriptor, dashed range or int val between min and max
  private static int validateFieldPart(String fieldPart, int fieldCode) {
    if (fieldPart.equals("*")) return FIELD_OK;
    if (fieldPart.equals(".")) return FIELD_OK;
    String[] slashed = fieldPart.split("/");
    if (slashed.length == 2) {
      int retCode = validateNumerator(slashed[0], fieldCode);
      if (retCode != FIELD_OK) return retCode;
      return validateDenominator(slashed[1], fieldCode);
    } else if (slashed.length != 1) {
      return INVALID_FIELD_CODE;
    }
    String[] dashed = fieldPart.split("-");
    if (dashed.length == 2) {
      return validateDashed(dashed, fieldCode);
    } else if (dashed.length == 1) {
      return validateVal(fieldPart, fieldCode);
    }
    return validateVal(fieldPart, fieldCode);
  }

  // should be * or dashed range
  private static int validateNumerator(String fieldPart, int fieldCode) {
    if (fieldPart.equals("*")) return FIELD_OK;
    
    // if no '-' is in the String --> no intervall is given
    if ( fieldPart.indexOf("-") == -1 ) return INVALID_FIELD_CODE;
    
    String[] dashed = fieldPart.split("-");
    if (dashed.length == 2) {
      return validateDashed(dashed, fieldCode);
    } else if (dashed.length == 1) {
      return validateVal(fieldPart, fieldCode);
    }
    return INVALID_FIELD_CODE;
  }

  private static int validateDashed(String[] dashed, int fieldCode) {
    int retCode = validateVal(dashed[0], fieldCode);
    if (retCode != FIELD_OK) return retCode;
    retCode = validateVal(dashed[1], fieldCode);
    if (retCode != FIELD_OK) return retCode;
    return (Integer.parseInt(dashed[0]) - Integer.parseInt(dashed[1]) < 0) ? FIELD_OK : INVALID_FIELD_CODE;
  }

  // should be int between min and max
  private static int validateVal(String val, int fieldCode) {
    try {
      int iVal = Integer.parseInt(val);
      int realMaxVal = (getMaxValue(fieldCode) == INFINITY) ? Integer.MAX_VALUE : getMaxValue(fieldCode);
      if ((iVal < getMinValue(fieldCode) || iVal > realMaxVal)) return INVALID_FIELD_CODE;
      return FIELD_OK;
    } catch (NumberFormatException e) {
      return INVALID_FIELD_CODE;
    }
  }

  // shlould be positive int
  private static int validateDenominator(String val, int fieldCode) {
    try {
      int iVal = Integer.parseInt(val);
      int realMaxVal = (getMaxValue(fieldCode) == INFINITY) ? Integer.MAX_VALUE : getMaxValue(fieldCode);      
      if (iVal < 2 || iVal > realMaxVal) 
          return INVALID_FIELD_CODE;
      return FIELD_OK;
    } catch (NumberFormatException e) {
      return INVALID_FIELD_CODE;
    }
  }
  // end validation


  /**
   * Getter method
   *
   * @return the minimal value of the set's domain
   */
  public int getMinValue() {
    return getMinValue(this.fldCode);
  }

  /**
   * Getter method
   *
   * @return the maximal value of the set's domain
   */
  public int getMaxValue() {
    return getMaxValue(this.fldCode);
  }

  /**
   * Getter method
   *
   * @param fieldCode the field type code
   * @return the minimal value of the specified field type
   */
  protected static int getMinValue(int fieldCode) {
    return fieldCodesToMinVals.get(fieldCode);
  }

  /**
   * Getter method
   *
   * @param fieldCode the field type code
   * @return the maximal value of the specified field type
   */
  protected static int getMaxValue(int fieldCode) {
    return fieldCodesToMaxVals.get(fieldCode);
  }

  /**
   * Returns a string representation of the cron field
   *
   * @return the string representation
   */
  public String toString() {
      if (strEntry.equals(".")) {
          return "*";
        } else {
          return strEntry;
        }
  }

  /**
   * Returns the cron field a DB persistable
   *
   * @return the DB persistable cron field
   */
  public String persistableValue() {
    if (strEntry.equals(".")) {
      return "*";
    } else {
      return strEntry;
    }
  }
}

