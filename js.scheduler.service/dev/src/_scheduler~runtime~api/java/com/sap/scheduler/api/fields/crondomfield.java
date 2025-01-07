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
 * This class represents a Cron day of month field.
 */
public class CronDOMField extends CronField {

  /**
   * Constructs a day of month field from given cron
   *
   * @param str the cron field
   */
  public CronDOMField(String str) {
    super(FIELD_MONTH_DAY, str);
  }

  /**
   * Constructs a day of month field from given single value
   *
   * @param singleValue single value field entry
   */
  public CronDOMField(int singleValue) {
    super(FIELD_MONTH_DAY, singleValue);
  }

  /**
   * Constructs a day of month field from given range, e.g. [start - end]
   *
   * @param start the start value
   * @param end   the end value
   */
  public CronDOMField(int start, int end) {
    super(FIELD_MONTH_DAY, start, end);
  }

  /**
   * Constructs a day of month field from given range and step, e.g. [start - end]/step
   *
   * @param start the start value
   * @param end   the end value
   * @param step  the step value
   */
  public CronDOMField(int start, int end, int step) {
    super(FIELD_MONTH_DAY, start, end, step);
  }

  /**
   * Returns a string representation of a value
   *
   * @param val the integer value
   * @return the string representation
   */
  public static String getStringValue(int val) {
    if ((val < getMinValue(FIELD_MONTH_DAY)) || (val > getMaxValue(FIELD_MONTH_DAY))) {
      throw new IllegalArgumentException("Illegal day number : " + val);
    }
    String result = "";
    switch (val) {
      case 1:
        {
          result = "1-st";
          break;
        }
      case 2:
        {
          result = "2-nd";
          break;
        }
      case 3:
        {
          result = "3-rd";
          break;
        }
      default:
        {
          result = val + "-th";
        }
    }
    return result;
  }

}