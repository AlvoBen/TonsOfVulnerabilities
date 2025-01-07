package com.sap.engine.services.scheduler.impl.cron;
import com.sap.scheduler.api.CronEntry;

/**
 * @author Stefan Dimov
 * @version 1.0
 */
class CronEntryWrapper {
  private CronEntry entry;

  private CronTimesGenerator yearsWrapper;
  private CronTimesGenerator monthsWrapper;
  private CronTimesGenerator monthDaysWrapper;
  private CronTimesGenerator weekDaysWrapper;
  private CronTimesGenerator hoursWrapper;
  private CronTimesGenerator minutesWrapper;


  CronEntryWrapper(CronEntry entry) {
    this.entry = entry;
    yearsWrapper = new CronTimesGenerator(entry.getYears());
    monthsWrapper = new CronTimesGenerator(entry.getMonths());
    monthDaysWrapper = new CronTimesGenerator(entry.getDays_of_month());
    weekDaysWrapper = new CronTimesGenerator(entry.getDays_of_week());
    hoursWrapper = new CronTimesGenerator(entry.getHours());
    minutesWrapper = new CronTimesGenerator(entry.getMinutes());
  }

  public CronTimesGenerator getYears() {
    return yearsWrapper;
  }

  public CronTimesGenerator getMonths() {
    return monthsWrapper;
  }

  public CronTimesGenerator getDays_of_month() {
    return monthDaysWrapper;
  }

  public CronTimesGenerator getDays_of_week() {
    return weekDaysWrapper;
  }

  public CronTimesGenerator getHours() {
    return hoursWrapper;
  }

  public CronTimesGenerator getMinutes() {
    return minutesWrapper;
  }

}
