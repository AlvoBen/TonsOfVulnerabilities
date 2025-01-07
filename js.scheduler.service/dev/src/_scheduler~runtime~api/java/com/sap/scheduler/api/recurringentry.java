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
import java.util.TimeZone;

/**
 * This class represents a recurring entry
 *
 * @author Nikolai Neichev
 */
public class RecurringEntry implements Serializable {

  final SchedulerTime startTime;
  final SchedulerTime endTime;
  final long period;

  /**
   * Constructs an entry with a specified period. The task created with this entry will expire every <c>period</c> milliseconds.
   * This constructor sets the start time of this entry to the current time. Thus calling <c>getStartTime</c> would return the
   * time at the moment of creating the object. This entry has no end time i.e. it will continue to execute in the infinity.
   * The time zone returned by <c>TimeZone.getDefault()</c> is used to set the start time.
   * @param period the entry period
   * @throws IllegalArgumentException - if <c>period<c> is less then, or equal to 0;
   */
  public RecurringEntry(long period) throws IllegalArgumentException {
  	this(new SchedulerTime(System.currentTimeMillis(), TimeZone.getDefault()), null, period);
  }

  /**
   * Constrcuts a new entry that will cause one expiration at the specified time. This entry sets both the start time
   * and end time to the time specified by <c>expirationTime</c> parameter. The wait period is set to 0.
   * @param expirationTime - time at which this entry will timeout.
   * @throws NullPointerException - if <c>expirationTime</c> is null
   */
  public RecurringEntry(SchedulerTime expirationTime) {
  	this (expirationTime, expirationTime, 0);
  }
  
  
  /**
   * Constructs an entry with a spesified start time, period and no end time. This entry will execute in the infinity per
   * intervals with length specified by the <c>period</c> parameter.
   * @param startTime - the entry start time
   * @param period - the period between to successive timeouts
   * @throws IllegalArgumentException - if period is less or equal to 0.
   * @throws NullPointerException - if <c>startTime<c> is null
   */
  public RecurringEntry(SchedulerTime startTime, long period) throws IllegalArgumentException, NullPointerException {
  	this(startTime, null, period);
  }

  
  /**
   * Constructs an entry with specified start time, end time and period. All other constructs default to this one by passing
   * appropriate parameters. 
   * @param startTime - the entry start time. This cannot be null.
   * @param endTime the entry end time. If null than this entry is infinite entry.
   * @param period the entry period - the period between two successive timeouts. If 0 than <c>startTime</c> must equal
   * <c>endTime</c> and this entry will cause a single expiration at time <c>startTime</c>, which equals <c>endTime</c>
   * @throws IllegalArgumentException - if <c>period</c> is negative. Or if <c>period</c> is 0 but <c>startTime</c> does
   * not equal <c>endTime</c>. Thrown if <c>startTime</c> is not equal to <c>endTime</c> but <c>period</c> is 0. Also
   * thrown if the duration specified by <c>period</c> parameter is longer than the period between <c>startTime</c>
   * and <c>endTime</c>
   * @throws NullPointerException - if <c>startTime</c> is null.
   */
  public RecurringEntry(SchedulerTime startTime, SchedulerTime endTime, long period) {
    if (startTime == null) throw new NullPointerException("start time");
    final boolean startIsEnd = startTime.equals(endTime);
    if (startIsEnd && period != 0)
    	throw new IllegalArgumentException("endTime is equal to startTime but period is greater than 0");
    if (!startIsEnd && period == 0)
    	throw new IllegalArgumentException("endTime is not equal to startTime but the period is 0");
    if (endTime != null) {
    	if(!(endTime.getTimeZone().equals(startTime.getTimeZone())) ) {
    		throw new IllegalArgumentException("Start and end time zones are different !");
    	}
    	if (endTime.timeMillis() < startTime.timeMillis()) {
    		throw new IllegalArgumentException("End time is sooner that start time !");
    	}
    	if (endTime.timeMillis() - startTime.timeMillis() < period) {
    		throw new IllegalArgumentException("specified period is longer than the period between startTime and endTime");
    	}
    }
    if (period < 0) throw new IllegalArgumentException("period is negative. It shoud be equal to 0 or greater");
    this.startTime = startTime;
    this.endTime = endTime;
    this.period = period;
  }

  /**
   * Constructor a entry with specified start time, end time and iterations. The period is infered from
   * the iterations by the formula period = (start_time - end_time) / (iterations - 1)
   * @param startTime - the start time
   * @param endTime - the end time
   * @param iterations  - the iterations
   * @throws IllegalArgumentException - if <c>iteratons<c> < 1, or if <c>startTime</c> is later than <c>endTime</c>.
   * Thrown if <c>iterations</c> equals 1 but <c>startTime</c> is different than <c>endTime</c>
   * @throws NullPointerException - if startTime or endTime is null.
   */
  public RecurringEntry(SchedulerTime startTime, SchedulerTime endTime, int iterations)
  throws IllegalArgumentException, NullPointerException {
  	this (startTime, endTime, iterations == 1 ? 0 : (endTime.timeMillis() - startTime.timeMillis()) / (iterations - 1));
  }

  /**
   * Constructs an entry with specified start time, period and iterations. The end time is infered from the iterations
   * by the formula end_time = start_time + period * (iterations - 1).
   * @param startTime -  the start time
   * @param period - the period between two successive timeouts. Must be greater or equal to 0. If zero than this would
   * be single expiration entry with expiration time equal to <c>startTime</c>.
   * @param iterations - number of iterations the iterations. Must not be negative. This is ignored if <c>period</c> is 0. 
   * @throws IllegalArgumentException iae - if <c>iterations</c> is less than 2 and <c>period</c> is not 0 (i.e.) the
   * iterations are not ignored. Thrown  if <c>period</c> is negative. 
   * @throws NullPointerException npe - if <c>startTime</c> is null
   */
  public RecurringEntry(SchedulerTime startTime, long period, int iterations)
  throws IllegalArgumentException, NullPointerException {
  	this (startTime, new SchedulerTime(startTime.timeMillis() + (iterations - 1) * period, startTime.getTimeZone()), period);
  }
  

  /**
   * Getter method
   * @return the entry start time
   */
  public SchedulerTime getStartTime() {
    return startTime;
  }

  /**
   * Getter method
   * @return the entry end time
   */
  public SchedulerTime getEndTime() {
    return endTime;
  }

  /**
   * Getter method
   * @return the entry petiod
   */
  public long getPeriod() {
    return period;
  }

  /**
   * Returns a string representarion of the recuring entry
   * @return the string representation
   */
  public String toString() {
    String result = "START TIME:  " + startTime.getTime() + "\r\n";
    if (endTime != null) {
      result +=     "END TIME:    " + endTime.getTime() + "\r\n";
    } else {
      result +=     "END TIME:    infinite..." + "\r\n";
    }
    result +=       "PERIOD (ms): " + period;
    return result;
  }
  
  
  /**
   * Compares a RecurringEntry with this.
   * 
   * @param entry the RecurringEntry to compare
   * @return true if the RecurringEntry are equals in case of StartTime, EndTime
   *              and period. otherwise false.
   */
  public boolean compareRecurringEntry(RecurringEntry entry) {
      // equals is overwritten in SchedulerTime and compares the Time in millis in UTC
      if (this.getStartTime() != null) {
          if ( !this.getStartTime().equals(entry.getStartTime()) ) {
              return false;
          }
      }
      if (this.getEndTime() != null) {
          if ( !this.getEndTime().equals(entry.getEndTime()) ) {
              return false;
          }
      }
      if ( this.period != entry.period ) { 
          return false;
      }
      return true;
  }

}
