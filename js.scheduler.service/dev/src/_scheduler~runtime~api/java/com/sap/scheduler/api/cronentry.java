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

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import com.sap.scheduler.api.fields.CronDOMField;
import com.sap.scheduler.api.fields.CronDOWField;
import com.sap.scheduler.api.fields.CronHourField;
import com.sap.scheduler.api.fields.CronMinuteField;
import com.sap.scheduler.api.fields.CronMonthField;
import com.sap.scheduler.api.fields.CronYearField;

/**
 * This class represents a cron entry
 */
public class CronEntry implements Serializable {
  // the entry time zone
  private TimeZone timeZone;
  // the fields
  private CronYearField years;
  private CronMonthField months;
  private CronDOMField days_of_month;
  private CronDOWField days_of_week;
  private CronHourField hours;
  private CronMinuteField minutes;
  // some constants
  private final static int VAL_SUCCESS = 0;
  private final static int INVALID_FIELDS_NUM = 7;
  private final static int INCOMPATIBLE_MONTH_DAY_AND_WEEK_DAY = 8;

  static {
    CronField.putErrMsg(INVALID_FIELDS_NUM, "The number of the fields must be exactly six and they must be separated by colon.");
    CronField.putErrMsg(INCOMPATIBLE_MONTH_DAY_AND_WEEK_DAY, "Either the day of month or the day of week must be specified");
  }

  /**
   * Constructs a cron entry object from given cron entry with the default time zone
   *
   * @param cron_entry the cron entry
   */
  public CronEntry(String cron_entry) {
    this(cron_entry, TimeZone.getDefault());
  }

  /**
   * Validates a cron entry
   *
   * @param entry the cron entry
   * @return integer array with results from all the fields
   */
  protected int[] validate(String entry) {
    String[] fields = entry.split(":");
    if (fields.length != 6) {
      int[] errCodes = {INVALID_FIELDS_NUM};
      return errCodes;
    }

    boolean monthWeekDaySpecConflict = (fields[2].equals(".") && fields[3].equals("."));

    int[] errCodes = new int[monthWeekDaySpecConflict ? 7 : 6];
    errCodes[0] = CronField.validate(fields[0], CronField.FIELD_YEAR);
    errCodes[1] = CronField.validate(fields[1], CronField.FIELD_MONTH);
    errCodes[2] = CronField.validate(fields[2], CronField.FIELD_MONTH_DAY);
    errCodes[3] = CronField.validate(fields[3], CronField.FIELD_WEEK_DAY);
    errCodes[4] = CronField.validate(fields[4], CronField.FIELD_HOUR);
    errCodes[5] = CronField.validate(fields[5], CronField.FIELD_MINUTE);
    if (monthWeekDaySpecConflict) errCodes[6] = INCOMPATIBLE_MONTH_DAY_AND_WEEK_DAY;

    for (int i = 0; i < errCodes.length; i++) {
      if (errCodes[i] != CronField.FIELD_OK) {
        return errCodes;
      }
    }
    errCodes = new int[1];
    errCodes[0] = VAL_SUCCESS;
    return errCodes;
  }

  /**
   * Constructs a cron entry object from given cron entry and specified time zone
   *
   * @param cron_entry the cron entry
   * @param tz         the time zone
   */
  public CronEntry(String cron_entry, TimeZone tz) {
    if (tz == null) throw new NullPointerException("The time zone must not be null");
    timeZone = tz;
    int[] errCodes = validate(cron_entry);

    if (errCodes[0] != VAL_SUCCESS) {
      String exceptionMsg = "Invalid cron entry: \n";
      for (int i = 0; i < errCodes.length; i++) {
        if (errCodes[i] != CronField.FIELD_OK) exceptionMsg += "\t" + CronField.getErrMsg(errCodes[i]) + "\n";
      }
      throw new IllegalArgumentException(exceptionMsg);
    }

    String[] fields = cron_entry.split(":");

    years = new CronYearField(fields[0]);
    months = new CronMonthField(fields[1]);

    String dom = fields[2];
    String dow = fields[3];

    if (dom.equals("*") && !dow.equals("*")) {
      dom = ".";
    }
    if (!dom.equals("*") && dow.equals("*")) {
      dow = ".";
    }
    days_of_month = new CronDOMField(dom);
    days_of_week = new CronDOWField(dow);

    hours = new CronHourField(fields[4]);
    minutes = new CronMinuteField(fields[5]);
    
    validateDate(cron_entry);
  }

  /**
   * Constructs a cron entry object from given fields and the default time zone
   *
   * @param years   the years field
   * @param months  the months field
   * @param dom     the days of month field
   * @param dow     the days of week field
   * @param hours   the hours field
   * @param minutes the minutes field
   */
  public CronEntry(CronYearField years, CronMonthField months, CronDOMField dom, CronDOWField dow, CronHourField hours, CronMinuteField minutes) {
    this(years, months, dom, dow, hours, minutes, TimeZone.getDefault());
  }

  /**
   * Constructs a cron entry object from given fields and specified time zone
   *
   * @param years   the years field
   * @param months  the months field
   * @param dom     the days of month field
   * @param dow     the days of week field
   * @param hours   the hours field
   * @param minutes the minutes field
   * @param tz      the specified time zone
   */
  public CronEntry(CronYearField years, CronMonthField months, CronDOMField dom, CronDOWField dow, CronHourField hours, CronMinuteField minutes, TimeZone tz) {
    if (tz == null) throw new NullPointerException("time zone");
    this.timeZone = tz;
    setYears(years);
    setMonths(months);
    if (dom.persistableValue().equals("*") && !dow.persistableValue().equals("*")) {
      dom = new CronDOMField(".");
    }
    if (!dom.persistableValue().equals("*") && dow.persistableValue().equals("*")) {
      dow = new CronDOWField(".");
    }
    setDays_of_month(dom);
    setDays_of_week(dow);
    setHours(hours);
    setMinutes(minutes);
    
    StringBuilder sb = new StringBuilder();
    sb.append(years.strEntry).append(":");
    sb.append(months.strEntry).append(":");
    sb.append(dom.strEntry).append(":");
    sb.append(dow.strEntry).append(":");
    sb.append(hours.strEntry).append(":");
    sb.append(minutes.strEntry);
    
    validateDate(sb.toString());
  }

  /**
   * Getter method
   *
   * @return the entry time zone
   */
  public TimeZone getTimeZone() {
    return timeZone;
  }

  /**
   * Setter method
   *
   * @param years the years field
   */
  protected void setYears(CronYearField years) {
    if (years == null) throw new NullPointerException("'years' value is null");
    this.years = years;
  }

  /**
   * Setter method
   *
   * @param months the months fiels
   */
  protected void setMonths(CronMonthField months) {
    if (years == null) throw new NullPointerException("'months' value is null");
    this.months = months;
  }

  /**
   * Setter method
   *
   * @param days_of_month the days of month field
   */
  protected void setDays_of_month(CronDOMField days_of_month) {
    if (years == null) throw new NullPointerException("'days of month' value is null");
    this.days_of_month = days_of_month;
  }

  /**
   * Setter method
   *
   * @param days_of_week the days of week field
   */
  protected void setDays_of_week(CronDOWField days_of_week) {
    if (years == null) throw new NullPointerException("'days of week' value is null");
    this.days_of_week = days_of_week;
  }

  /**
   * Setter method
   *
   * @param hours the hours field
   */
  protected void setHours(CronHourField hours) {
    if (years == null) throw new NullPointerException("'hours' value is null");
    this.hours = hours;
  }

  /**
   * Setter method
   *
   * @param minutes the minutes field
   */
  protected void setMinutes(CronMinuteField minutes) {
    if (years == null) throw new NullPointerException("'minutes' value is null");
    this.minutes = minutes;
  }

  /**
   * Getter method
   *
   * @return the years field
   */
  public CronYearField getYears() {
    return years;
  }

  /**
   * Getter method
   *
   * @return the months field
   */
  public CronMonthField getMonths() {
    return months;
  }

  /**
   * Getter method
   *
   * @return the days of month field
   */
  public CronDOMField getDays_of_month() {
    return days_of_month;
  }

  /**
   * Getter method
   *
   * @return the days of week field
   */
  public CronDOWField getDays_of_week() {
    return days_of_week;
  }

  /**
   * Getter method
   *
   * @return the hours field
   */
  public CronHourField getHours() {
    return hours;
  }

  /**
   * Getter method
   *
   * @return the minutes field
   */
  public CronMinuteField getMinutes() {
    return minutes;
  }

  /**
   * Returns the entry as a DB persistable
   *
   * @return the DB persistable entry
   */
  public String persistableValue() {
    String dom = days_of_month.toString();
    String dow = days_of_week.toString();

    if (dom.equals(".")) dom = "*";
    if (dow.equals(".")) dow = "*";

    return years.toString() + ":" +
            months.toString() + ":" +
            dom + ":" +
            dow + ":" +
            hours.toString() + ":" +
            minutes.toString();
  }

  /**
   * Returns a string representation of the entry
   *
   * @return the string representation
   */
  public String toString() {
    String res = "Persistable : " + persistableValue() + "\r\n" +
            "Years : " + getYears() + "\r\n" +
            "Months : " + getMonths() + "\r\n" +
            "Days of month : " + getDays_of_month() + "\r\n" +
            "Days of week : " + getDays_of_week() + "\r\n" +
            "Hours : " + getHours() + "\r\n" +
            "Minutes : " + getMinutes() + "\r\n";
    return res;
  }

  
  /**
   * Compares a CronEntry with this.
   * 
   * @param entry the CronEntry to compare
   * @return true if the CronEntry are equals in case of its members otherwise false.
   */
  public boolean compareCronEntry(CronEntry entry) {
      if ( !persistableValue().equals(entry.persistableValue()) ) {
          return false;
      }
      return true;
  }
  
  
  /**
   * Method validates if the year, month and dayOfMonth are set uniquely if
   * it is valid, e.g. the 30th of February is always invalid
   * 
   * @param dateStr the incoming CronEntry in the format *:*:*:*:*:*
   * 
   * @throws IllegalArgumentException if the incoming CronEntry is semantical incorrect
   */
  private void validateDate(String dateStr) throws IllegalArgumentException {
      Calendar c = Calendar.getInstance();
      c.setLenient(false);
      
      // check month and dom
      String[] s = dateStr.split(":");
      String year = s[0];
      String month = s[1];
      String dom = s[2];
      
      int monthVal;
      int domVal;
      int yearVal;
      
      try {
          monthVal = Integer.parseInt(month);
          domVal = Integer.parseInt(dom);
      } catch (NumberFormatException nfe) {
          // $JL-EXC$
          // semantic can not be validated, because values are not unique
          return;
      }        
      // validate month and dayOfMonth, take a leap year as dummy-value
      c.set(2000, monthVal, domVal, 0, 0, 0);
      // here the exception will be thrown if the combination for month and dom is invalid generally
      c.getTime();
      
      //
      // validate now also against the year
      try {
          yearVal = Integer.parseInt(year);
      } catch (NumberFormatException nfe) {
          // $JL-EXC$
          // semantic can not be validated, because values are not unique
          return;
      }
      
      c.clear();
      // validate year, month and dayOfMonth
      c.set(yearVal, monthVal, domVal, 0, 0, 0);
      // here the exception will be thrown if the combination for year, month and dom is invalid generally
      c.getTime();        
  }
  
}
