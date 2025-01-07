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
package com.sap.engine.services.scheduler.impl.cron;

import java.util.Calendar;
import java.util.Date;

import com.sap.engine.services.scheduler.impl.EntryProcessor;
import com.sap.scheduler.api.CronEntry;

/**
 * This class represents a cron processor
 *
 * @author Nikolai Neichev
 */
public class CronProcessor implements EntryProcessor {

  final CronEntryWrapper entryWrapper;
  final long MINUTE = 1000 * 60;
  final long HOUR = MINUTE * 60;
  final long DAY = HOUR * 24;
  final long MONTH = DAY * 31;
  final long YEAR = DAY * 366;
  Calendar nextDate;
  Calendar dayResolverCalendar;  

  public CronProcessor(CronEntry entry) {
    this.entryWrapper = new  CronEntryWrapper(entry);
    nextDate = Calendar.getInstance(entry.getTimeZone());
	// we need to set this calendar which processes the next valid 
	// date to non-lenient. Thus if there is a date calculated for the 
	// 31th of April it will NOT be considered as 1st of May. 
    nextDate.setLenient(false);
    nextDate.set(Calendar.SECOND, 0);
    nextDate.set(Calendar.MILLISECOND, 0);
    dayResolverCalendar = Calendar.getInstance(entry.getTimeZone());
  }

  /**
   * Calculates the next execution time from the current moment
   * @return the next execution time; NO_MORE_EXECUTION_TIMES if no next execution
   */
  public long getNextExecutionTime() {
    return getNextExecutionTime(System.currentTimeMillis());
  }

  /**
   * Calculates the next execution time after the specified time
   * @param baseTime the specified base date
   * @return the next execution time that is > baseTime; NO_MORE_EXECUTION_TIMES if no next execution
   */
  public long getNextExecutionTime(long baseTime) {
    int year;
    entryWrapper.getYears().reset();
    _year: while ((year = entryWrapper.getYears().getNext()) > -1) {                               // YEARS
      nextDate.set(Calendar.YEAR, year);
      int month;
      entryWrapper.getMonths().reset();
      _month: while ((month = entryWrapper.getMonths().getNext()) > -1) {                           // MONTHS
        nextDate.set(Calendar.MONTH, month);
        int day_of_month;
        dayResolverCalendar.set(Calendar.YEAR, year);
        dayResolverCalendar.set(Calendar.MONTH, month);
          // we need an empty CRON field to fill the needed values from DAYS_OF_MONTH combined WITH DAYS_OF_WEEK
        CronTimesGenerator days_resolver = new CronTimesGenerator(null, 1, dayResolverCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int[] dom_values = entryWrapper.getDays_of_month().getValues();  // day of month values
        
        // check here for the wildcard in the fields dom & dow. If one wildcard is set
        // in these fields, we ignore the field with the wildcard. If both fields are 
        // filled with wildcards we extract both of them
        if ( !entryWrapper.getDays_of_month().isWildCard() ) {
            for (int i = 0; i < dom_values.length; i++) {
              try {
                days_resolver.insert(dom_values[i]);
              } catch (IllegalArgumentException iae) {
                // $JL-EXC$
              }
            }
        } else if ( entryWrapper.getDays_of_month().isWildCard() && entryWrapper.getDays_of_week().isWildCard()){
          days_resolver.fillAllValues();
        }
        
        if ( !entryWrapper.getDays_of_week().isWildCard() || 
                (entryWrapper.getDays_of_month().isWildCard() && entryWrapper.getDays_of_month().isWildCard()) ) {
          days_resolver = addDaysOfWeek(days_resolver, year, month);
        }
        
        days_resolver.reset();
        _day: while ((day_of_month = days_resolver.getNext()) > -1) {           // DAYS
          nextDate.set(Calendar.DAY_OF_MONTH, day_of_month);
          int hour;
          entryWrapper.getHours().reset();
          _hour: while ((hour = entryWrapper.getHours().getNext()) > -1) {                         // HOURS
            nextDate.set(Calendar.HOUR_OF_DAY, hour);
            int minute;
            entryWrapper.getMinutes().reset();
            while ((minute = entryWrapper.getMinutes().getNext()) > -1) {                   // MUNUTES
              nextDate.set(Calendar.MINUTE, minute);
              // bigger time check
              
              long nextTime = baseTime;
              try {
            	  nextTime = nextDate.getTimeInMillis();
              } catch (IllegalArgumentException iae) {
            	  // $JL-EXC$
            	  // we need to set this calendar which processes the next valid 
            	  // date to non-lenient. Thus if there is a date calculated for the 
            	  // 31th of April it will NOT be considered as 1st of May. The 
            	  // exception here will be thrown if there are dates calculated and
            	  // do not exist, e.g. 31th of April
              }
              if ( nextTime > baseTime) {
                return nextTime;
              }           
              else {
                long diff = baseTime - nextTime;
                if (diff > YEAR) {
                  continue _year;
                } else if (diff > MONTH) {
                  continue _month;
                } else if (diff > DAY) {
                  continue _day;
                } else if (diff > HOUR){
                  continue _hour;
                }
              }
            }
          }
        }
      }
    }
    return NO_MORE_EXECUTION_TIMES;
  }

  /**
   * Transfers the days of week to days of month for the coresponding month
   * @param cronFieldWrapper the cron set with days of month
   * @param year the year
   * @param month the month
   * @return the cron set with days of week transfered to days of month
   */
  public CronTimesGenerator addDaysOfWeek(CronTimesGenerator cronFieldWrapper, int year, int month) {
    Calendar tempDate = Calendar.getInstance();
    tempDate.set(Calendar.YEAR, year);
    tempDate.set(Calendar.MONTH, month);
    
    // Customer CSN 3216489 2008(Dirk Marwinski)
    // if we don't set the day of month here the day of month of the 
    // current month is assumes. This may lead to an overflow in 
    // months with 31 days when searching for fire times/dates in 
    // February (IllegalArgumentException) which kills the scheduler
    // thread. I actually have no idea why this is calculated at all...
    // 30. Feb 2008 -> 1 March -> 31 March   <<>> February only has
    // 29 days...
    //
    tempDate.set(Calendar.DAY_OF_MONTH, 1);
      // month day count
    int dayCnt = tempDate.getActualMaximum(Calendar.DAY_OF_MONTH);
    for (int i = 0; i < dayCnt; i++) {
      tempDate.set(Calendar.DAY_OF_MONTH, i + 1);
      int day_of_week = tempDate.get(Calendar.DAY_OF_WEEK);
      if (entryWrapper.getDays_of_week().contains(day_of_week)) {
        cronFieldWrapper.insert(tempDate.get(Calendar.DAY_OF_MONTH));
      }
    }
    return cronFieldWrapper;        
  }


//  // --------------------------------------- FOR TESTING ----------------------------------------------
//  public static void main(String[] args) {
//    //  <year>:<month>:<day of month>:<day of week>:<hour>:<minute>
//    // ------:   0-11:           1-X:          1-7:  0-23:    0-59
//    CronProcessor processor;
////     !!!!! ako iskame samo edin tip dni da setvame triabva drugia da e s  '.' - t.e. unspecified
//    CronEntry entry1 = new CronEntry("*:*:.:1:8:30");
//    CronEntry entry2 = new CronEntry("2006-2008:1,8:17:.:1:0");
//    CronEntry entry3 = new CronEntry("2006-2008:1,8:17:2:1:0");
//    CronEntry entry4 = new CronEntry("*/3:1:*:6:8-12,16-18:*/15,32");
//    CronEntry entry5 = new CronEntry("*:*/2:22:.:0:0");
//    CronEntry entry6 = new CronEntry("*/5:*/2:22:*:0:0");
//    CronEntry entry7 = new CronEntry("*:0,1,2:*/3:6:0:0");
//    CronEntry entry8 = new CronEntry("2006,2007,2008,2009,2010:*/2:22:.:0:0");
//    CronEntry entry9 = new CronEntry("2006,2007,2008,2009,2010,2020-2030:*/2:22:.:0:0");
//    CronEntry entry0 = new CronEntry("2006,2007,2008,2009,2010,2020-2030/5:*/2:22:.:0:0");
//    CronEntry entry10 = new CronEntry("*:*:*:*:0:0");
//    CronEntry entry11 = new CronEntry("*:*:6:*:0:0");
//    CronEntry entry12 = new CronEntry("*:*:*:6:0:0");
//    CronEntry entry13 = new CronEntry("2006-2014:*:*:*:*:*/2");
//    CronEntry entry14 = new CronEntry("*:*:*:*:*:*");
//    CronEntry entry = entry1;
//    processor = new CronProcessor(entry);
//    System.out.println("Persistable : " + entry.persistableValue());
//    System.out.println("Years : " + entry.getYears());
//    System.out.println("Months : " + entry.getMonths());
//    System.out.println("Days of month : " + entry.getDays_of_month());
//    System.out.println("Days of week : " + entry.getDays_of_week());
//    System.out.println("Hours : " + entry.getHours());
//    System.out.println("Minutes : " + entry.getMinutes());
//
//    long currentTime = System.currentTimeMillis();
//    System.out.println("CURRENT TIME : " + new Date(currentTime));
//    int cnt = 1;
//    long next = processor.getNextExecutionTime(currentTime);
//    System.out.println(cnt++ + "-NEXT AT : " + new Date(next));
//    for (int i = 0; i < 99; i++) {
//      next = processor.getNextExecutionTime(next);
//      if (next != -1) {
//        System.out.println(cnt++ + "-NEXT AT : " + new Date(next));
//      } else {
//        System.out.println("NO NEXT EXECUTION !!!");
//        break;
//      }
//    }
//
//    System.out.println("------------------------------------ from time 0");
//
//    cnt = 1;
//    next = processor.getNextExecutionTime(0);
//    System.out.println(cnt++ + "-NEXT AT : " + new Date(next));
//    for (int i = 0; i < 99; i++) {
//      next = processor.getNextExecutionTime(next);
//      if (next != -1) {
//        System.out.println(cnt++ + "-NEXT AT : " + new Date(next));
//      } else {
//        System.out.println("NO NEXT EXECUTION !!!");
//        break;
//      }
//    }
//
//    System.out.println("TOTAL TIME : " + (System.currentTimeMillis() - currentTime));
//
//  }


}