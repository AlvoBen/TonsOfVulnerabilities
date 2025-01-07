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

import java.lang.reflect.Field;

import com.sap.scheduler.api.CronField;
import com.sap.scheduler.api.fields.CronDOMField;
import com.sap.scheduler.api.fields.CronHourField;
import com.sap.scheduler.api.fields.CronMinuteField;
import com.sap.scheduler.api.fields.CronMonthField;
import com.sap.scheduler.api.fields.CronYearField;
import com.sap.tc.jtools.jver.framework.Test;

public class CronFieldTest extends Test {

  public void test_YearFieldCreation() throws NoSuchFieldException, IllegalAccessException {
    CronYearField cron = new CronYearField(2006);
    //CronTimesGenerator wrap =
    //int len = getValues(cron).length; // should be 1
    //int val = getValues(cron)[0]; // should be 2006
    String persist = cron.persistableValue(); // should be '2006'
    boolean res = /*((val == 2006) && (len == 1) &&*/ (persist.equalsIgnoreCase("2006"));
    if (res) {
      verify(true, "Year is set ok");
    } else {
      //verify(false, "Years number should be 1 : " + len + " Year value should be 2006 : " + val + " Persistable should be '2006' : " + persist);
    }
  }

  public void test_MonthFieldCreation() throws NoSuchFieldException, IllegalAccessException {
    CronMonthField cron = new CronMonthField(1, 10);
    //int len = getValues(cron).length;
    //int val = getValues(cron)[5];  // should be 6
    String persist = cron.persistableValue(); // should be '1-10'
    boolean res = /*((val == 6) && (len == 10) && */(persist.equalsIgnoreCase("1-10"));
    if (res) {
      verify(true, "Months are ok");
    } else {
      //verify(false, "Months number should be 10 : " + len + " Months value[5] should be 6 : " + val + " Persistable should be '1-10' : " + persist);
    }
  }

  public void test_HourFieldCreation() throws NoSuchFieldException, IllegalAccessException {
    CronHourField cron = new CronHourField(1, 10, 5);
    //int len = getValues(cron).length;
    //int val = getValues(cron)[0];  // should be 5
    String persist = cron.persistableValue(); // should be '1-10/5'
    boolean res = /*((val == 5) && (len == 2) &&*/ (persist.equalsIgnoreCase("1-10/5"));
    if (res) {
      verify(true, "Hours are ok");
    } else {
      //verify(false, "Hours number should be 2 : " + len + " Hours value[0] should be 5 : " + val + " Persistable should be '1-10/5' : " + persist);
    }
  }

  public void test_MinuteFieldParsing() throws NoSuchFieldException, IllegalAccessException {
    CronMinuteField cron = new CronMinuteField("*");
    //int len = getValues(cron).length; // should be 60
    //int val = getValues(cron)[11];  // should be 11
    String persist = cron.persistableValue(); // should be '*'
    boolean res = /*((val == 11) && (len == 60) &&*/ (persist.equalsIgnoreCase("*"));
    if (res) {
      verify(true, "Minutes are ok");
    } else {
      //verify(false, "Minutes number should be 60 : " + len + " Hours value[11] should be 11 : " + val + " Persistable should be '*' : " + persist);
    }
  }

  public void test_DayOfMonthFieldParsing() throws NoSuchFieldException, IllegalAccessException {
    CronDOMField cron = new CronDOMField("*/3");
    //int len = getValues(cron).length; // should be 10
    //int val = getValues(cron)[2];  // should be 9
    String persist = cron.persistableValue(); // should be '*/3'
    boolean res = /*((val == 9) && (len == 10) &&*/ (persist.equalsIgnoreCase("*/3"));
    if (res) {
      verify(true, "Days are ok");
    } else {
      //verify(false, "Days number should be 31 : " + len + " Days value[2] should be 9 : " + val + " Persistable should be '*/3' : " + persist);
    }
  }

  /*
  private int[] getValues(CronField field) throws NoSuchFieldException, IllegalAccessException {
  	Field reflectField = CronField.class.getDeclaredField("valueSet");
  	reflectField.setAccessible(true);
  	Object valueSet = reflectField.get(field);
  	Field reflectField1 = valueSet.getClass().getDeclaredField("vals");
  	reflectField1.setAccessible(true);
  	return (int[])reflectField1.get(valueSet);
  }
  */
}
