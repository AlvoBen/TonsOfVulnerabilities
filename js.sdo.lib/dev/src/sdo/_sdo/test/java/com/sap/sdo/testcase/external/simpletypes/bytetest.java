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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class ByteTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public ByteTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;
    private IByteTest b;

    @Before
    public void setUp() throws Exception {
        b = (IByteTest)_helperContext.getDataFactory().create(IByteTest.class);
        d = (DataObject)b;
    }

    @After
    public void tearDown() throws Exception {
        b = null;
        d = null;
    }

    @Test
    public void testByteSimpleTypeDefaults() {
        assertTrue("abyte should have default 127", b.getAbyte() == 127);
        assertTrue("\"abyte\" should have default 127", d
                .getByte("abyte") == 127);
    }

    @Test
    public void testByteSimpleTypeSetters() {
        b.setAbyte((byte)32);
        assertTrue("abyte should have changed to 32", b.getAbyte() == 32);
        assertTrue("\"abyte\" should have changed to 32", d
                .getByte("abyte") == 32);
    }

    @Test
    public void testByteSimpleTypeConversions() {
        d.setDouble("abyte", 2d);
        assertTrue("abyte should have changed to 2", b.getAbyte() == 2);
        d.setFloat("abyte", 3f);
        assertTrue("abyte should have changed to 3", b.getAbyte() == 3);
        d.setInt("abyte", 4);
        assertTrue("abyte should have changed to 4", b.getAbyte() == 4);
        d.setLong("abyte", 5);
        assertTrue("abyte should have changed to 5", b.getAbyte() == 5);
        d.setShort("abyte", (short)6);
        assertTrue("abyte should have changed to 6", b.getAbyte() == 6);
        d.setString("abyte", "7");
        assertTrue("abyte should have changed to 7", b.getAbyte() == 7);
        String s = d.getString("abyte");
        assertTrue("abyte should have String value to \"7\"", "7"
                .equals(s));
        double dn = d.getDouble("abyte");
        assertTrue("abyte should have double value to 7", dn == 7d);
        float fn = d.getFloat("abyte");
        assertTrue("abyte should have float value to 7", fn == 7f);
        long ln = d.getLong("abyte");
        assertTrue("abyte should have long value to 7", ln == 7l);
        int in = d.getInt("abyte");
        assertTrue("abyte should have int value to 7", in == 7);
        short sn = d.getShort("abyte");
        assertTrue("abyte should have short value to 7", sn == 7);
        BigDecimal bdn = d.getBigDecimal("abyte");
        assertEquals("abyte should have big decimal value to 7", BigDecimal.valueOf(7), bdn);
        BigInteger bin = d.getBigInteger("abyte");
        assertTrue("abyte should have big integer value to 7", bin
                .equals(new BigInteger("7")));

        d.setString("abyte", "42");
        assertEquals((byte)42, b.getAbyte());
        d.setString("abyte", "-128");
        assertEquals((byte)-128, b.getAbyte());
        assertEquals(-128, d.getInt("abyte"));
        b.setAbyte((byte)42);
        assertEquals("42", d.getString("abyte"));
        b.setAbyte((byte)-128);
        assertEquals("-128", d.getString("abyte"));
        d.setInt("abyte", -127);
        assertEquals((byte)-127, b.getAbyte());
    }
}
