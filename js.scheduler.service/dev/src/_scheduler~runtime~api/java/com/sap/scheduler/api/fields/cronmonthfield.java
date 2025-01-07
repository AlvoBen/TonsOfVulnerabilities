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
 * This class represents a Cron month field.
 */
public class CronMonthField extends CronField {

  // the month names
  private final static String[] months = {"January",
                                          "February",
                                          "March",
                                          "April",
                                          "May",
                                          "June",
                                          "July",
                                          "August",
                                          "September",
                                          "October",
                                          "November",
                                          "December"};

  /**
   * Constructs a month field from given cron
   *
   * @param str the cron
   */
  public CronMonthField(String str) {
    super(FIELD_MONTH, str);
  }

  /**
   * Constructs a month field from given single value
   *
   * @param singleValue the single value
   */
  public CronMonthField(int singleValue) {
    super(FIELD_MONTH, singleValue);
  }

  /**
   * Constructs a month field from given value range, e.g. [start - end]
   *
   * @param start the start value
   * @param end   the end value
   */
  public CronMonthField(int start, int end) {
    super(FIELD_MONTH, start, end);
  }

  /**
   * Constructs a month field from given single value and step, e.g. [start-end]/step
   *
   * @param start the start value
   * @param end   the end value
   * @param step  the step
   */
  public CronMonthField(int start, int end, int step) {
    super(FIELD_MONTH, start, end, step);
  }

  /**
   * Returns a string representation of a month
   *
   * @param val the integer value
   * @return the month name
   */
  public static String getStringValue(int val) {
    if ((val < getMinValue(FIELD_MONTH)) || (val > getMaxValue(FIELD_MONTH))) {
      throw new IllegalArgumentException("Illegal month number : " + val);
    }
    return months[val];
  }

}
