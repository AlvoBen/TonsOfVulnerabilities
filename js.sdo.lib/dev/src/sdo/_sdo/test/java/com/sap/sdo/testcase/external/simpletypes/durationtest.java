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
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class DurationTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DurationTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;

    private IDurationTest b;

    @Before
    public void setUp() throws Exception {
        b = (IDurationTest)_helperContext.getDataFactory().create(IDurationTest.class);
        d = (DataObject)b;
    }

    @Test
    public void testType() {
        Property p = d.getProperty("aDuration");
        Type duration = _helperContext.getTypeHelper().getType("commonj.sdo", "Duration");
        assertEquals("type should be duration", duration, p.getType());
    }

    @Test
    public void testDateSimpleTypeSetters() {
        // as usual see also:
        // http://www.w3.org/TR/2001/REC-xmlschema-0-20010502/
        String s = "P1Y2M3DT10H30M12.300S";
        b.setADuration(s);
        assertEquals("aduration should have changed", s, b.getADuration());
        assertEquals("\"adate\" should have changed", s, d.getString("aDuration"));

        // unable conversion
        try {
            d.setLong("aDuration", 0);
            fail("conversion from long to Duration is not specified.");
        } catch (RuntimeException ex) { //$JL-EXC$
            // expected
        }
}

    @Test
    public void testDateSimpleTypeConversions() {
        String s = "P1Y2M3DT10H30M12.3S";
        b.setADuration(s);
        Calendar c = Calendar.getInstance();
        c.setLenient(true);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.add(Calendar.YEAR, 1);
        c.add(Calendar.MONTH, 2);
        c.add(Calendar.DAY_OF_MONTH, 3);
        c.add(Calendar.HOUR_OF_DAY, 10);
        c.add(Calendar.MINUTE, 30);
        c.add(Calendar.SECOND, 12);
        c.add(Calendar.MILLISECOND, 300);
        // test difference is lower than 1 second, milliseconds are taken from current time
        assertTrue(
            "\"aDuration\" expected " + c.getTime() + " but was " + d.getDate("aDuration"),
            (d.getDate("aDuration").getTime() - c.getTimeInMillis()) < 1000);

        c.clear();
        c.setLenient(true);
        c.set(Calendar.YEAR, 1971);
        c.set(Calendar.MONTH, 3);
        c.set(Calendar.DAY_OF_MONTH, 12);
        c.set(Calendar.HOUR_OF_DAY, 5);
        c.set(Calendar.MINUTE, 7);
        c.set(Calendar.SECOND, 45);
        c.set(Calendar.MILLISECOND, 455);
        String s1 = "P466DT5H7M45.455S";
        String s2 = "P1Y3M11DT5H7M45.455S";
        d.setDate("aDuration", c.getTime());
        String result = b.getADuration();
        if (result == null || result.length()<2 || result.charAt(1)=='4') {
            assertEquals("\"aDuration\"", s1, result);
        } else {
            assertEquals("\"aDuration\"", s2, result);
        }

        // unable conversion
        try {
            d.getBigInteger("aDuration");
            fail("ClassCastException expected");
        } catch (ClassCastException cce) { //$JL-EXC$
            // expected
        }
    }
}
