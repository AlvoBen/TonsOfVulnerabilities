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
package com.sap.engine.services.scheduler.impl.recurring;

import com.sap.engine.services.scheduler.impl.EntryProcessor;
import com.sap.scheduler.api.RecurringEntry;

/**
 * This class represents a recurring processor
 *
 * @author Nikolai Neichev
 */
public class RecurringProcessor implements EntryProcessor  {

  RecurringEntry entry;

  public RecurringProcessor(RecurringEntry entry) {
    this.entry = entry;
  }

  /**
   * Calculates the next execution time from the current time on
   * @return the next execution time from the current time on or 
   * NO_MORE_EXECUTION_TIMES if there is no next execution time
   */
  public long getNextExecutionTime() {
    return getNextExecutionTime(System.currentTimeMillis());
  }

  /**
   * Calculates the next execution time after the specified time
   * @param baseTime the specified time
   * @return the next execution time that is > baseTime or 
   * NO_MORE_EXECUTION_TIMES if there is no next execution time
   */
  public long getNextExecutionTime(long baseTime) {
    long start = entry.getStartTime().timeMillis();
    long next = 0;
    if (entry.getPeriod() == 0) { // special case only one iteration
      if (baseTime < entry.getEndTime().timeMillis()) {
        return entry.getEndTime().timeMillis();
      } else {
        return NO_MORE_EXECUTION_TIMES;    // expired
      }
    }
    if (baseTime >= start) {
      long mod = (baseTime - start) % entry.getPeriod();
      // it is 0 <= mod < entry.getPeriod()
      next = baseTime + (entry.getPeriod() - mod);
      // it is next > baseTime
    } else {
      next = start;
    }
    if ( (entry.getEndTime() != null) && (next > entry.getEndTime().timeMillis()) ) { // end time is set and already expired
      return NO_MORE_EXECUTION_TIMES;
    }
    return next;
  }


  // FOR TESTING
//  public static void main(String[] args) {
//
//    Calendar cal = Calendar.getInstance();
//    cal.setTimeInMillis(System.currentTimeMillis());
//    cal.set(Calendar.DAY_OF_MONTH, 9);
//    cal.set(Calendar.HOUR_OF_DAY, 0);
//    cal.set(Calendar.MINUTE, 0);
//    SchedulerTime startTime = new SchedulerTime(cal.getTime(), cal.getTimeZone());
//    cal.set(Calendar.DAY_OF_MONTH, 10);
//    cal.set(Calendar.HOUR_OF_DAY, 10);
//    cal.set(Calendar.MINUTE, 10);
//    SchedulerTime endTime = new SchedulerTime(cal.getTime(), cal.getTimeZone());
//    System.out.println("START : " + startTime.getTime());
//    System.out.println("END   : " + endTime.getTime());
//    RecurringEntry entry;
////    entry = new RecurringEntry(1000*60); // 1 min
////    entry = new RecurringEntry(startTime, 1000*60*30);        // 30 min
////    entry = new RecurringEntry(startTime, 1000*60*30, 1);        // 30 min
////    entry = new RecurringEntry(startTime, 1000*60*30, 26);        // 30 min
////    entry = new RecurringEntry(startTime, endTime, (long) 1000*60*30);        // 30 min
////    entry = new RecurringEntry(startTime, endTime, 54);        // 30 min
////    entry = new RecurringEntry(startTime, endTime, 0);        // 0 iter
////    entry = new RecurringEntry(startTime, endTime, (long) 0);        // 0 ms
////    entry = new RecurringEntry(null, endTime, (long) 1000);        // null start time
//    entry = new RecurringEntry(startTime, 20*1000, 50);        // 20 sec
//
//    RecurringProcessor processor = new RecurringProcessor(entry);
//    System.out.println("CURRENT TIME : " + new Date(System.currentTimeMillis()));
//    int cnt = 1;
//    long next = System.currentTimeMillis();
//    for (int i = 0; i < 100; i++) {
//      next = processor.getNextExecutionTime(next);
//      if (next != -1) {
//        System.out.println(cnt++ + "-NEXT AT : " + new Date(next));
//      } else {
//        System.out.println("NO NEXT EXECUTION !!!");
//        break;
//      }
//    }
//  }

}
