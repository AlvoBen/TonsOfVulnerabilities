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
 * This class represents a Cron minute field.
 */
public class CronMinuteField extends CronField {

  /**
   * Constructs a minute field from given cron
   *
   * @param str the cron
   */
  public CronMinuteField(String str) {
    super(FIELD_MINUTE, str);
  }

  /**
   * Constructs a minute field from given single value
   *
   * @param singleValue the single value
   */
  public CronMinuteField(int singleValue) {
    super(FIELD_MINUTE, singleValue);
  }

  /**
   * Constructs a minute field from given value range , e.g. [start - end]
   *
   * @param start the start value
   * @param end   the end game
   */
  public CronMinuteField(int start, int end) {
    super(FIELD_MINUTE, start, end);
  }

  /**
   * Constructs a minute field from given value range , e.g. [start - end]/step
   *
   * @param start the start time
   * @param end   the end time
   * @param step  the step
   */
  public CronMinuteField(int start, int end, int step) {
    super(FIELD_MINUTE, start, end, step);
  }

  /**
   * Returns a string representation of a minute
   *
   * @param val the integer value
   * @return the string representation
   */
  public static String getStringValue(int val) {
    if ((val < getMinValue(FIELD_MINUTE)) || (val > getMaxValue(FIELD_MINUTE))) {
      throw new IllegalArgumentException("Illegal minute : " + val);
    }
    return val + " min";
  }

}
