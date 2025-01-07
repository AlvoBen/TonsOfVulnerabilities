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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class YearMonthDayTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public YearMonthDayTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;

    private IYearMonthDayTest b;

    @Before
    public void setUp() throws Exception {
        b = (IYearMonthDayTest)_helperContext.getDataFactory().create(IYearMonthDayTest.class);
        d = (DataObject)b;
    }

    @Test
    public void testDateSimpleTypeSetters() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTimeZone(TimeZone.getTimeZone("PST"));
        c.set(Calendar.YEAR, 2005);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 6);
        d.set("adate", c);
        String ds = "2005-12-06";
        assertEquals("adate should have changed", ds, b.getAdate());
    }

    @Test
    public void testDateSimpleTypeConversions() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.YEAR, 2005);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 6);
        String ds = "2005-12-06";

        d.set("adate", c);
        assertEquals("adate should have changed", ds, b.getAdate());

        d.setDate("adate", c.getTime());
        assertEquals("adate should have changed", ds, b.getAdate());

        Date dt = d.getDate("adate");

        // test difference is lower than 1 day, time is taken from current time
        assertTrue(
            "adate should resolve to " + c.getTime() + " but is " + dt,
            (dt.getTime() - c.getTimeInMillis()) < (1000*60*60*24));
    }
}
