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
package com.sap.scheduler.api.jver;

import com.sap.tc.jtools.jver.framework.Test;
import com.sap.scheduler.api.CronEntry;

public class CronEntryTest extends Test {

  public void test_EntryParsing1() {
    /*
    CronEntry entry =  new CronEntry("2006,2007,2008,2009,2010,2020-2030/5:*/                   //2:22:3:*:0");
      // expected values
    /*
    String persist = "2006,2007,2008,2009,2010,2020-2030/5:*/               //2:22:3:*:0";
    /*
    String years = "[2006 year,2007 year,2008 year,2009 year,2010 year,2020 year,2025 year,2030 year]";
    String months = "[January,March,May,July,September,November]";
    String daysOfMonth = "[22th]";
    String daysOfWeek = "[Tuesday]";
    String hours = "all";
    String minutes = "[0 min]";
    if ( !years.equalsIgnoreCase(entry.getYears().toString()) ) {
      verify(false, "Expected : " + years + " Actual : " + entry.getYears().toString());
    }
    if ( !months.equalsIgnoreCase(entry.getMonths().toString()) ) {
      verify(false, "Expected : " + months + " Actual : " + entry.getMonths().toString());
    }
    if ( !daysOfMonth.equalsIgnoreCase(entry.getDays_of_month().toString()) ) {
      verify(false, "Expected : " + daysOfMonth + " Actual : " + entry.getDays_of_month().toString());
    }
    if ( !daysOfWeek.equalsIgnoreCase(entry.getDays_of_week().toString()) ) {
      verify(false, "Expected : " + daysOfWeek + " Actual : " + entry.getDays_of_week().toString());
    }
    if ( !hours.equalsIgnoreCase(entry.getHours().toString()) ) {
      verify(false, "Expected : " + hours + " Actual : " + entry.getHours().toString());
    }
    if ( !minutes.equalsIgnoreCase(entry.getMinutes().toString()) ) {
      verify(false, "Expected : " + minutes + " Actual : " + entry.getMinutes().toString());
    }
    verify(true, "Parsed ok");
    */
  }

  public void test_EntryParsing2() {
    //CronEntry entry = new CronEntry("*/13:*:.:2,5:8-12,16-18:*/15,32");
      // expected values
    //String persist = "*/13:*:.:2,5:8-12,16-18:*/15,32";
    //String years = "*/13";
    /*
    String months = "all";
    String daysOfMonth = "not specified...";
    String daysOfWeek = "[Monday,Thursday]";
    String hours = "[8 h,9 h,10 h,11 h,12 h,16 h,17 h,18 h]";
    String minutes = "[0 min,15 min,30 min,32 min,45 min]";
    if ( !years.equalsIgnoreCase(entry.getYears().toString()) ) {
      verify(false, "Expected : " + years + " Actual : " + entry.getYears().toString());
    }
    if ( !months.equalsIgnoreCase(entry.getMonths().toString()) ) {
      verify(false, "Expected : " + months + " Actual : " + entry.getMonths().toString());
    }
    if ( !daysOfMonth.equalsIgnoreCase(entry.getDays_of_month().toString()) ) {
      verify(false, "Expected : " + daysOfMonth + " Actual : " + entry.getDays_of_month().toString());
    }
    if ( !daysOfWeek.equalsIgnoreCase(entry.getDays_of_week().toString()) ) {
      verify(false, "Expected : " + daysOfWeek + " Actual : " + entry.getDays_of_week().toString());
    }
    if ( !hours.equalsIgnoreCase(entry.getHours().toString()) ) {
      verify(false, "Expected : " + hours + " Actual : " + entry.getHours().toString());
    }
    if ( !minutes.equalsIgnoreCase(entry.getMinutes().toString()) ) {
      verify(false, "Expected : " + minutes + " Actual : " + entry.getMinutes().toString());
    }
    verify(true, "Parsed ok");
    */
  }

}
