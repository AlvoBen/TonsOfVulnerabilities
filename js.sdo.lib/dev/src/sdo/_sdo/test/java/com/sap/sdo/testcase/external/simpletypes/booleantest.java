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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class BooleanTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public BooleanTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;

    private IBooleanTest b;

    @Before
    public void setUp() throws Exception {
        b = (IBooleanTest)_helperContext.getDataFactory().create(IBooleanTest.class);
        d = (DataObject)b;
    }



    @Test
    public void testBooleanSimpleTypeDefaults() {
        assertTrue("aboolean should have default true", b.getAboolean());
        assertTrue("\"aboolean\" should have default true", d.getBoolean("aboolean"));
        assertTrue("bboolean should have default false", !b
                        .getBboolean());
    }

    @Test
    public void testBooleanSimpleTypeSetters() {
        b.setAboolean(false);
        assertTrue("aboolean should have changed to false", !b
                .getAboolean());
        assertTrue("\"aboolean\" should have changed to false", !d
                .getBoolean("aboolean"));
        d.setBoolean("aboolean", true);
        assertTrue("aboolean should have changed to false", b
                .getAboolean());
        assertTrue("\"aboolean\" should have changed to false", d
                .getBoolean("aboolean"));
    }

    @Test
    public void testBooleanSimpleTypeConversions() {
        d.setString("aboolean", "false");
        assertTrue("aboolean should have changed to false", !b
                .getAboolean());
        d.setString("aboolean", "true");
        assertTrue("aboolean should have changed to true", b
                .getAboolean());
        String s = d.getString("aboolean");
        assertTrue("aboolean should have string value true but is " + s,
                "true".equals(s));
        b.setAboolean(false);
        s = d.getString("aboolean");
        assertTrue("aboolean should have string value false but is " + s,
                "false".equals(s));

        // Number String
        d.setString("aboolean", "1");
        assertEquals(true, b.getAboolean());
        d.setString("aboolean", "0");
        assertEquals(false, b.getAboolean());

        // Character
        d.setChar("aboolean", '1');
        assertEquals(true, b.getAboolean());
        assertEquals('1', d.getChar("aboolean"));
        d.setChar("aboolean", '0');
        assertEquals(false, b.getAboolean());
        assertEquals('0', d.getChar("aboolean"));

        // Number
        d.setInt("aboolean", 1);
        assertEquals(true, b.getAboolean());
        assertEquals(1, d.getInt("aboolean"));
        d.setInt("aboolean", 0);
        assertEquals(false, b.getAboolean());
        assertEquals(0, d.getInt("aboolean"));

        // these Number tests are optional, but it is more consistent
        d.setBigDecimal("aboolean", BigDecimal.ONE);
        assertEquals(true, b.getAboolean());
        assertEquals(BigDecimal.ONE, d.getBigDecimal("aboolean"));
        d.setBigDecimal("aboolean", BigDecimal.ZERO);
        assertEquals(false, b.getAboolean());
        assertEquals(BigDecimal.ZERO, d.getBigDecimal("aboolean"));

        d.setBigInteger("aboolean", BigInteger.ONE);
        assertEquals(true, b.getAboolean());
        assertEquals(BigInteger.ONE, d.getBigInteger("aboolean"));
        d.setBigInteger("aboolean", BigInteger.ZERO);
        assertEquals(false, b.getAboolean());
        assertEquals(BigInteger.ZERO, d.getBigInteger("aboolean"));

        d.setByte("aboolean", (byte)1);
        assertEquals(true, b.getAboolean());
        assertEquals((byte)1, d.getByte("aboolean"));
        d.setByte("aboolean", (byte)0);
        assertEquals(false, b.getAboolean());
        assertEquals((byte)0, d.getByte("aboolean"));

        d.setDouble("aboolean", new Double(1));
        assertEquals(true, b.getAboolean());
        assertEquals(new Double(1), d.getDouble("aboolean"));
        d.setDouble("aboolean", new Double(0));
        assertEquals(false, b.getAboolean());
        assertEquals(new Double(0), d.getDouble("aboolean"));

        d.setFloat("aboolean", new Float(1));
        assertEquals(true, b.getAboolean());
        assertEquals(new Float(1), d.getFloat("aboolean"));
        d.setFloat("aboolean", new Float(0));
        assertEquals(false, b.getAboolean());
        assertEquals(new Float(0), d.getFloat("aboolean"));

        d.setLong("aboolean", 1L);
        assertEquals(true, b.getAboolean());
        assertEquals(1L, d.getLong("aboolean"));
        d.setLong("aboolean", 0L);
        assertEquals(false, b.getAboolean());
        assertEquals(0L, d.getLong("aboolean"));

        d.setShort("aboolean", (short)1);
        assertEquals(true, b.getAboolean());
        assertEquals((short)1, d.getShort("aboolean"));
        d.setShort("aboolean", (short)0);
        assertEquals(false, b.getAboolean());
        assertEquals((short)0, d.getShort("aboolean"));

    }

    @Test
    public void testDefaultValues() {
        assertEquals(true, b.getAboolean());
        assertEquals(false, b.getBboolean());
        assertEquals(false, b.getCboolean());
        assertEquals(true, d.get("aboolean"));
        assertEquals(false, d.get("bboolean"));
        assertEquals(false, d.get("cboolean"));
    }

    @Test
    public void testSetNull() {
        assertEquals(false, d.isSet("aboolean"));
        b.setAboolean(true);
        assertEquals(true, b.getAboolean());
        assertEquals(true, d.isSet("aboolean"));
        try {
            d.set("aboolean", null);
            fail("RuntimeException expected");
        } catch (RuntimeException e) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testUnInitialized() {
        assertEquals(false, d.getBoolean("cboolean"));
    }
}
