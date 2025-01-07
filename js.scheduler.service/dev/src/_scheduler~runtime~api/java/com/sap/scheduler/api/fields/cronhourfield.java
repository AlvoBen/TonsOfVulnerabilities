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
 * This class represents a Cron hour field.
 */
public class CronHourField extends CronField {

  /**
   * Constructs a hour field from given cron
   *
   * @param str the cron
   */
  public CronHourField(String str) {
    super(FIELD_HOUR, str);
  }

  /**
   * Constructs a hour field from given single value
   *
   * @param singleValue the single value
   */
  public CronHourField(int singleValue) {
    super(FIELD_HOUR, singleValue);
  }

  /**
   * Constructs a hour field from given value range, e.g. [start - end]
   *
   * @param start the start value
   * @param end   the end value
   */
  public CronHourField(int start, int end) {
    super(FIELD_HOUR, start, end);
  }

  /**
   * Constructs a hour field from given value range and step, e.g. [start - end]/step
   *
   * @param start the start value
   * @param end   the end value
   * @param step  the step value
   */
  public CronHourField(int start, int end, int step) {
    super(FIELD_HOUR, start, end, step);
  }

  /**
   * Returns a string representation of a hour
   *
   * @param val the integer value
   * @return the string representation
   */
  public static String getStringValue(int val) {
    if ((val < getMinValue(FIELD_HOUR)) || (val > getMaxValue(FIELD_HOUR))) {
      throw new IllegalArgumentException("Illegal hour : " + val);
    }
    return val + " h";
  }

}
