package com.sap.engine.lib.schema.util;

import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-22
 * Time: 9:11:27
 * @deprecated Please use javax.xml.datatype.Duration instead
 */
@Deprecated
public final class Duration {

  public static final int COMPARE_LESS = -1;
  public static final int COMPARE_EQUAL = 0;
  public static final int COMPARE_GREATER = 1;
  public static final int COMPARE_NOT_EQUAL = 2;

  private int yearsDuration;
  private int monthsDuration;
  private int daysDuration;
  private int hoursDuration;
  private int minutesDuration;
  private double secondsDuration;
  private boolean isPositiveDuration;
  private GregorianCalendar calendar;
  
  private static long nonDurationTimeInMillis;
  
  static {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.clear();
    calendar.set(Calendar.YEAR, 0);
    calendar.set(Calendar.MONTH, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.DAY_OF_MONTH, 0);
    nonDurationTimeInMillis = calendar.getTimeInMillis();
  }
  
  public void setYearsDuration(int yearsDuration) {
    this.yearsDuration = yearsDuration;
  }

  public void setMonthsDuration(int monthsDuration) {
    this.monthsDuration = monthsDuration;
  }

  public void setDaysDuration(int daysDuration) {
    this.daysDuration = daysDuration;
  }

  public void setHoursDuration(int hoursDuration) {
    this.hoursDuration = hoursDuration;
  }

  public void setMinutesDuration(int minutesDuration) {
    this.minutesDuration = minutesDuration;
  }

  public void setSecondsDuration(double secondsDuration) {
    this.secondsDuration = secondsDuration;
  }

  public void setPositiveDuration(boolean isPositiveDuration) {
    this.isPositiveDuration = isPositiveDuration;
  }

  public int getYearsDuration() {
    return(yearsDuration);
  }

  public int getMonthsDuration() {
    return(monthsDuration);
  }

  public int getDaysDuration() {
    return(daysDuration);
  }

  public int getHoursDuration() {
    return(hoursDuration);
  }

  public int getMinutesDuration() {
    return(minutesDuration);
  }

  public double getSecondsDuration() {
    return(secondsDuration);
  }

  public boolean isPositiveDuration() {
    return(isPositiveDuration);
  }

  public int compare(Duration duration) {
    if(duration == null) {
      return(COMPARE_NOT_EQUAL);
    }
    if(isPositiveDuration && !duration.isPositiveDuration()) {
      return(COMPARE_GREATER);
    }
    if(!isPositiveDuration && duration.isPositiveDuration()) {
      return(COMPARE_LESS);
    }
    calendar = getCalendar();
    GregorianCalendar cmpCalendar = duration.getCalendar();
    int compareResult = compareCalendars(calendar, cmpCalendar);
    if(compareResult == COMPARE_EQUAL) {
      return(COMPARE_EQUAL);
    }
    if(isPositiveDuration) {
      return(compareResult);
    }
    return(compareResult == COMPARE_LESS ? COMPARE_GREATER : COMPARE_LESS);
  }

  private int compareCalendars(GregorianCalendar calendar1, GregorianCalendar calendar2) {
    if(calendar1.before(calendar2)) {
      return(COMPARE_LESS);
    }
    if(calendar1.after(calendar2)) {
      return(COMPARE_GREATER);
    }
    return(COMPARE_EQUAL);
  }
  
  public long getTimeInMillis() {
    return(Math.abs(getCalendar().getTimeInMillis() - nonDurationTimeInMillis));
  }
  
  private void produceCalendar() {
    if(calendar == null) {
      calendar = new GregorianCalendar();
      calendar.clear();
      calendar.set(Calendar.YEAR, yearsDuration);
      calendar.set(Calendar.MONTH, monthsDuration);
      calendar.set(Calendar.SECOND, (int)secondsDuration);
      calendar.set(Calendar.MINUTE, minutesDuration);
      calendar.set(Calendar.HOUR_OF_DAY, hoursDuration);
      calendar.set(Calendar.DAY_OF_MONTH, daysDuration);
    }
  }
  
//  private void produceCalendar() {
//    if(calendar == null) {
//      
//        
//      //months
//      double carry = (int)(monthsDuration / 13);
//      int calendarMonths = (int)(monthsDuration - carry * 12);
////      if(calendarMonths == 0) {
////        calendarMonths = 1;
////      }
//
//      //years
//      int calendarYears = (int)(yearsDuration + carry);
//      if(calendarYears == 0) {
//        calendarYears = 1;
//      }
//
//      //seconds
//      carry = (int)(secondsDuration / 60);
//      int calendarSeconds = (int)(secondsDuration - carry * 60);
//
//      //minutes
//      double temp = minutesDuration + carry;
//      carry = (int)(temp / 60);
//      int calendarMinutes = (int)(temp - carry * 60);
//
//      //hours
//      temp = hoursDuration + carry;
//      carry = (int)(temp / 24);
//      int calendarHours = (int)(temp - carry * 24);
//
//      //days
//      temp = daysDuration + carry;
//      double processingCalendarDays = 0;
//      if(temp > 28) {
//        while(true) {
//          int maxDaysOfMonth = 0;
//          if(calendarMonths == 1 || calendarMonths == 3 || calendarMonths == 5 || calendarMonths == 7 || calendarMonths == 8 || calendarMonths == 10 || calendarMonths == 12) {
//            maxDaysOfMonth = 31;
//          } else if(calendarMonths == 4 || calendarMonths == 6 || calendarMonths == 9 || calendarMonths == 11) {
//            maxDaysOfMonth = 30;
//          } else {
//            maxDaysOfMonth = calendarYears % 4 == 0 ? 29 : 28;
//          }
//          temp -= maxDaysOfMonth;
//          if(temp <= 0) {
//            break;
//          } else {
//            processingCalendarDays = temp;
//            calendarMonths++;
//            if(calendarMonths == 13) {
//              calendarMonths = 1;
//              calendarYears++;
//            }
//          }
//        }
//      } else {
//        processingCalendarDays = temp;
//      }
//      int calendarDays = (int)processingCalendarDays;
//      calendar = new GregorianCalendar();
//      calendar.clear();
//      calendar.set(Calendar.YEAR, calendarYears);
//      calendar.set(Calendar.MONTH, calendarMonths);
//      calendar.set(Calendar.SECOND, calendarSeconds);
//      calendar.set(Calendar.MINUTE, calendarMinutes);
//      calendar.set(Calendar.HOUR_OF_DAY, calendarHours);
//      calendar.set(Calendar.DAY_OF_MONTH, calendarDays);
//    }
//  }
  
  public GregorianCalendar getCalendar() {
    if(calendar == null) {
      produceCalendar();
    }
    return(calendar);
  }

  public boolean isInitialized() {
    return(yearsDuration != -1 || monthsDuration != -1 || daysDuration != -1 || hoursDuration != -1 || minutesDuration != -1 || secondsDuration != -1);
  }

  public String toString() {
    return("[years : " + (yearsDuration == -1 ? 0 : yearsDuration) + "; months : " + (monthsDuration == -1 ? 0 : monthsDuration) + "; days : " + (daysDuration == -1 ? 0 : daysDuration) + "; hours : " + (hoursDuration == -1 ? 0 : hoursDuration) + "; minutes : " + (minutesDuration == -1 ? 0 : minutesDuration) + "; seconds : " + (secondsDuration == -1 ? 0 : secondsDuration) + "]");
  }
}
