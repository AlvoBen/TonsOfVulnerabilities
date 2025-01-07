/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class DateTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DateTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;

    private IDateTest b;

    @Before
    public void setUp() throws Exception {
        b = (IDateTest)_helperContext.getDataFactory().create(IDateTest.class);
        d = (DataObject)b;
    }

    @Test
    public void testDateSimpleTypeSetters() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2005);
        c.set(Calendar.MONTH, 12);
        c.set(Calendar.DAY_OF_MONTH, 6);
        b.setAdate(c.getTime());
        assertEquals("adate should have changed", c.getTime(), b.getAdate());
        assertEquals("\"adate\" should have changed to 'b'", c.getTime(), d.getDate("adate"));
    }

    @Test
    public void testDateSimpleTypeConversions() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, 2005);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 6);
        c.set(Calendar.YEAR, 2006);
        d.setLong("adate", c.getTimeInMillis());
        assertEquals("adate should have changed", c.getTime(), b.getAdate());
        d.setString("adate", "1999-05-31T13:20:00Z");
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.set(Calendar.YEAR, 1999);
        c.set(Calendar.MONTH, 04);
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.HOUR_OF_DAY, 13);
        c.set(Calendar.MINUTE, 20);
        c.set(Calendar.SECOND, 00);
        // test difference is lower than 1 second, milliseconds are taken from current time
        assertTrue(
            "adate should have changed to " + c.getTime() + " but is " + b.getAdate(),
            (b.getAdate().getTime() - c.getTimeInMillis()) < 1000);
        d.setString("adate", "1999-05-31T13:20:00+01:00");
        c.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        c.set(Calendar.YEAR, 1999);
        c.set(Calendar.MONTH, 04);
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.HOUR_OF_DAY, 13);
        c.set(Calendar.MINUTE, 20);
        c.set(Calendar.SECOND, 00);
        // test difference is lower than 1 second, milliseconds are taken from current time
        assertTrue(
            "adate should have changed to " + c.getTime() + " but is " + b.getAdate(),
            (b.getAdate().getTime() - c.getTimeInMillis()) < 1000);
        d.setString("adate", "1999-05-31T13:20:00.111+01:00");
        c.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        c.set(Calendar.YEAR, 1999);
        c.set(Calendar.MONTH, 04);
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.HOUR_OF_DAY, 13);
        c.set(Calendar.MINUTE, 20);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.MILLISECOND, 111);
        assertEquals("adate should have changed", c.getTime(), b.getAdate());
        d.setString("adate", "2000-06-14T15:19:11");
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.clear();
        c.set(Calendar.YEAR, 2000);
        c.set(Calendar.MONTH, 5);
        c.set(Calendar.DAY_OF_MONTH, 14);
        c.set(Calendar.HOUR_OF_DAY, 15);
        c.set(Calendar.MINUTE, 19);
        c.set(Calendar.SECOND, 11);
        // test difference is lower than 1 second, milliseconds are taken from current time
        assertTrue(
            "adate should have changed to " + c.getTime() + " but is " + b.getAdate(),
            (b.getAdate().getTime() - c.getTimeInMillis()) < 1000);
        Date x = b.getAdate();
        d.setString("adate", d.getString("adate"));
        assertEquals(
                "adate should have no change between toString and Parsing.",
                x, b.getAdate());
        long l = d.getLong("adate");
        assertEquals("adate should have no long value", l, x.getTime());
        // extra (not in spec: conversion from calendar)
        d.set("adate", c);
        assertEquals("adate should have changed", c.getTime(), b.getAdate());

        // unable conversion
        try {
            d.getBigInteger("adate");
            fail("ClassCastException expected");
        } catch (ClassCastException cce) { //$JL-EXC$
            // expected
        }
    }
}
