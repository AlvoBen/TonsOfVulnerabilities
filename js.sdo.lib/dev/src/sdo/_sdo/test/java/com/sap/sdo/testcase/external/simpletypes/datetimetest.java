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

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class DateTimeTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DateTimeTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;

    private IDateTimeTest b;

    @Before
    public void setUp() throws Exception {
        b = (IDateTimeTest)_helperContext.getDataFactory().create(IDateTimeTest.class);
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
        c.set(Calendar.MINUTE, 34);
        // System.err.println(c.getTimeZone());
        // System.err.println(c.getTime());
        d.set("adate", c);
        String ds = "2005-12-06T08:34:00.000Z";
        assertEquals("adate should have changed", ds, b.getAdate());
    }

    @Test
    public void testDateSimpleTypeConversions() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTimeZone(TimeZone.getTimeZone("PST"));
        c.set(Calendar.YEAR, 2005);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 6);
        c.set(Calendar.MINUTE, 34);
        d.set("adate", c);
        String ds = "2005-12-06T08:34:00.000Z";

        d.setDate("adate", c.getTime());
        assertEquals("adate should have changed", ds, b.getAdate());
    }
}
