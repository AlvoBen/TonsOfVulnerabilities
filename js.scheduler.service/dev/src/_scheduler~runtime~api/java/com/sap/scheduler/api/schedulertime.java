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

import java.util.TimeZone;
import java.util.Date;
import java.util.Calendar;
import java.io.Serializable;

/**
 * This class represents a scheduler time
 */
public final class SchedulerTime implements Comparable, Serializable {

  private final Calendar calendar;

  /**
   * Constructs a scheduler time object with the current time
   */
  public SchedulerTime() {
    calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
  }

  /**
   * Constructs a scheduler time object with specified time and time zone
   *
   * @param time     the time as a Date object
   * @param timeZone the time zone
   */
  public SchedulerTime(Date time, TimeZone timeZone) {
    if (time == null) throw new NullPointerException("time");
    if (timeZone == null) throw new NullPointerException("timeZone");
    this.calendar = Calendar.getInstance(timeZone);
    this.calendar.setTime(time);
  }

  /**
   * Constructs a scheduler time object with specified time and time zone
   *
   * @param time     the time in milliseconds
   * @param timeZone the time zone
   */
  public SchedulerTime(long time, TimeZone timeZone) {
    this(new Date(time), timeZone);
  }

  /**
   * Getter method
   *
   * @return the Date
   */
  public Date getTime() {
    return calendar.getTime();
  }

  /**
   * Getter method
   *
   * @return the time zone
   */
  public TimeZone getTimeZone() {
    return calendar.getTimeZone();
  }

  /**
   * Getter method
   *
   * @return the time in milliseconds
   */
  public long timeMillis() {
    return calendar.getTimeInMillis();
  }

  /**
   * Compares two scheduler time objects
   *
   * @param other the other object
   * @return compare value
   */
  public int compareTo(Object other) {
    SchedulerTime otherSchedulerTime = (SchedulerTime) other;
    final long thisValue = calendar.getTimeInMillis();
    final long otherValue = otherSchedulerTime.calendar.getTimeInMillis();
    return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
  }

  /**
   * Checks if the object is the same
   *
   * @param other the 'other' object
   * @return TRUE if the objects are identical, FALSE if not
   */
  public boolean equals(Object other) {
    if (other == null || !getClass().equals(other.getClass())) return false;
    return compareTo(other) == 0;
  }

  /**
   * Calculates a hashcode
   *
   * @return the hashcode
   */
  public int hashCode() {
    final long timeMillis = calendar.getTimeInMillis();
    return (int) timeMillis ^ (int) (timeMillis >> 32);
  }
}
