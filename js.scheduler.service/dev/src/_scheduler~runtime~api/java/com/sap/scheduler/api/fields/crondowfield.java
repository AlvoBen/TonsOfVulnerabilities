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
 * This class represents a Cron day of week field.
 */
public class CronDOWField extends CronField {

  // the day names
  private static String[] days = {"Sunday",
                                  "Monday",
                                  "Tuesday",
                                  "Wednesday",
                                  "Thursday",
                                  "Friday",
                                  "Saturday"};

  /**
   * Constructs a day of week field from given cron
   *
   * @param str the cron
   */
  public CronDOWField(String str) {
    super(FIELD_WEEK_DAY, str);
  }

  /**
   * Constructs a day of week field from given single value
   *
   * @param singleValue the single value
   */
  public CronDOWField(int singleValue) {
    super(FIELD_WEEK_DAY, singleValue);
  }

  /**
   * Constructs a day of week field from given value range, e.g. [start - end]
   *
   * @param start the start value
   * @param end   the end value
   */
  public CronDOWField(int start, int end) {
    super(FIELD_WEEK_DAY, start, end);
  }

  /**
   * Constructs a day of week field from given value range and step, e.g. [start - end]/step
   *
   * @param start the start value
   * @param end   the end value
   * @param step  the step value
   */
  public CronDOWField(int start, int end, int step) {
    super(FIELD_WEEK_DAY, start, end, step);
  }

  /**
   * Returns a string representation of a corresponding day
   *
   * @param val the integer value
   * @return the day name
   */
  public static String getStringValue(int val) {
    if ((val < getMinValue(FIELD_WEEK_DAY)) || (val > getMaxValue(FIELD_WEEK_DAY))) {
      throw new IllegalArgumentException("Illegal day number : " + val);
    }
    return days[val - 1];
  }

}
