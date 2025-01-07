/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.external.simpletypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataHelper;

/**
 * @author D042774
 *
 */
public class DateConversionsTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public DateConversionsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataHelper _dataHelper;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _dataHelper = _helperContext.getDataHelper();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _dataHelper = null;
    }

    @Test
    public void testMonthDayRoundtrip() {
        String monthDay = "--02-28";
        Date date = _dataHelper.toDate(monthDay);
        assertEquals(monthDay, _dataHelper.toMonthDay(date));
    }

    @Test
    public void testTimeRoundtrip() {
        String time = "12:00:00.000Z";
        assertEquals(time, _dataHelper.toTime(_dataHelper.toDate(time)));
    }

    @Test
    public void testZoneOffset() {
        // DateTime
        assertEquals(false, _dataHelper.toCalendar("2008-05-05T15:13:30").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("2008-05-05T15:13:30+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("2008-05-05T15:13:30+02:00").get(Calendar.ZONE_OFFSET));

        // Time
        assertEquals(false, _dataHelper.toCalendar("15:13:30").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("15:13:30+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("15:13:30+02:00").get(Calendar.ZONE_OFFSET));

        // YearMonthDay
        assertEquals(false, _dataHelper.toCalendar("2008-05-05").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("2008-05-05+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("2008-05-05+02:00").get(Calendar.ZONE_OFFSET));

        // Year
        assertEquals(false, _dataHelper.toCalendar("2008").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("2008+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("2008+02:00").get(Calendar.ZONE_OFFSET));

        // YearMonth
        assertEquals(false, _dataHelper.toCalendar("2008-05").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("2008-05+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("2008-05+02:00").get(Calendar.ZONE_OFFSET));

        // Month
        assertEquals(false, _dataHelper.toCalendar("--05").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("--05+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("--05+02:00").get(Calendar.ZONE_OFFSET));

        // MonthDay
        assertEquals(false, _dataHelper.toCalendar("--05-05").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("--05-05+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("--05-05+02:00").get(Calendar.ZONE_OFFSET));

        // Day
        assertEquals(false, _dataHelper.toCalendar("---05").isSet(Calendar.ZONE_OFFSET));
        assertEquals(true, _dataHelper.toCalendar("---05+02:00").isSet(Calendar.ZONE_OFFSET));
        assertEquals(7200000, _dataHelper.toCalendar("---05+02:00").get(Calendar.ZONE_OFFSET));
    }

    @Test
    public void testCalendarConversion() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.DAY_OF_MONTH, 6);
        c.set(Calendar.MONTH, Calendar.MAY);
        c.set(Calendar.YEAR, 2008);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 41);
        c.set(Calendar.SECOND, 42);

        // DateTime
        assertEquals("2008-05-06T09:41:42", _dataHelper.toDateTime((Calendar)c.clone()));

        // Time
        assertEquals("09:41:42", _dataHelper.toTime((Calendar)c.clone()));

        // YearMonthDay
        assertEquals("2008-05-06", _dataHelper.toYearMonthDay((Calendar)c.clone()));

        // Year
        assertEquals("2008", _dataHelper.toYear((Calendar)c.clone()));

        // YearMonth
        assertEquals("2008-05", _dataHelper.toYearMonth((Calendar)c.clone()));

        // Month
        assertEquals("--05", _dataHelper.toMonth((Calendar)c.clone()));

        // MonthDay
        assertEquals("--05-06", _dataHelper.toMonthDay((Calendar)c.clone()));

        // Day
        assertEquals("---06", _dataHelper.toDay((Calendar)c.clone()));
    }

    @Test
    public void testDateConversion() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.DAY_OF_MONTH, 6);
        c.set(Calendar.MONTH, Calendar.MAY);
        c.set(Calendar.YEAR, 2008);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 41);
        c.set(Calendar.SECOND, 42);
        c.set(Calendar.ZONE_OFFSET, 0);
        c.set(Calendar.DST_OFFSET, 0);

        // DateTime
        assertEquals("2008-05-06T09:41:42.000Z", _dataHelper.toDateTime(c.getTime()));

        // Time
        assertEquals("09:41:42.000Z", _dataHelper.toTime(c.getTime()));

        // YearMonthDay
        assertEquals("2008-05-06", _dataHelper.toYearMonthDay(c.getTime()));

        // Year
        assertEquals("2008", _dataHelper.toYear(c.getTime()));

        // YearMonth
        assertEquals("2008-05", _dataHelper.toYearMonth(c.getTime()));

        // Month
        assertEquals("--05", _dataHelper.toMonth(c.getTime()));

        // MonthDay
        assertEquals("--05-06", _dataHelper.toMonthDay(c.getTime()));

        // Day
        assertEquals("---06", _dataHelper.toDay(c.getTime()));
    }

    @Test
    public void testStringConversion() {
        DataObject dateTime = _helperContext.getDataFactory().create(IDateTimeTest.class);
        dateTime.set("adate", "2008-05-06T09:41:42.000Z");
        assertEquals("2008-05-06T09:41:42.000Z", dateTime.get("adate"));
        dateTime.set("adate", "2008-05-06T09:41:42");
        assertEquals("2008-05-06T09:41:42", dateTime.get("adate"));

        DataObject month = _helperContext.getDataFactory().create(IMonthTest.class);
        month.set("adate", "--05");
        assertEquals("--05", month.get("adate"));
        month.set("adate", "2008-05-06T09:41:42");
        assertEquals("--05", month.get("adate"));
        try {
            month.set("adate", "09:41:42");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "invalid commonj.sdo#Month string: 09:41:42",
                ex.getMessage());
        }
    }

    @Test
    public void testConversionRoundtrip() {
        String dateStr = "2008-06-03";
        // Tue Jun 03 02:00:00 CEST 2008
        Date date = new Date(1212451200000l);

        DataHelper dataHelper = _helperContext.getDataHelper();
        assertEquals(dateStr, dataHelper.toYearMonthDay(dataHelper.toDate(dateStr)));

        assertEquals(dateStr, dataHelper.toYearMonthDay(date));
        assertEquals(date, dataHelper.toDate(dateStr));

        assertEquals(dateStr, JavaSimpleType.YEARMONTHDAY.convertFromJavaClass(date));
        assertEquals(date, JavaSimpleType.YEARMONTHDAY.convertToJavaClass(dateStr, Date.class));
    }

    @Test
    public void testGMonthConversion() throws Exception {
        Calendar cal = _helperContext.getDataHelper().toCalendar("2006-03-31T03:30:45.123Z");
        String month = _helperContext.getDataHelper().toMonth(cal);
        assertEquals("--03Z", month);
    }
}
