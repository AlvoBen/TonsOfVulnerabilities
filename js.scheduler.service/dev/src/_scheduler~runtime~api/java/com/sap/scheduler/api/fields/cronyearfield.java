/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.api.fields;

import com.sap.scheduler.api.CronField;

/**
 * This class represents a Cron year field.
 */
public class CronYearField extends CronField {

  /**
   * Constructs a year field from given cron
   *
   * @param str the cron
   */
  public CronYearField(String str) {
    super(FIELD_YEAR, str);
  }

  /**
   * Constructs a month field from given single value
   *
   * @param singleValue the single value
   */
  public CronYearField(int singleValue) {
    super(FIELD_YEAR, singleValue);
  }

  /**
   * Constructs a month field from given value range, e.g [start - end]
   *
   * @param start the start value
   * @param end   the end value
   */
  public CronYearField(int start, int end) {
    super(FIELD_YEAR, start, end);
  }

  /**
   * Constructs a month field from given value range, e.g [start - end]/step
   *
   * @param start the start value
   * @param end   the end value
   * @param step  the step
   */
  public CronYearField(int start, int end, int step) {
    super(FIELD_YEAR, start, end, step);
  }

  /**
   * Returns a string representation of a year
   *
   * @param val the intefer value
   * @return the string representation
   */
  public static String getStringValue(int val) {
    return val + " year";
  }
}