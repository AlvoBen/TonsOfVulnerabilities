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

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class BytesTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public BytesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final byte[] D1 = new byte[] { 1, 2, 3, 4 };
    private static final byte[] D2 = new byte[] { 4, 5, 2, 1 };
    private static final byte[] D3 = new byte[] {
        (byte)189, (byte)147, 43, (byte)228, (byte)234, 56, (byte)236, 62, 43, 63, 51,
        (byte)232, 45, 8, (byte)211, 49, (byte)240, (byte)177, 60, (byte)238 };
    private static final String S1 = "BD932BE4EA38EC3E2B3F33E82D08D331F0B13CEE";

    private DataObject d;

    private IBytesTest b;

    @Before
    public void setUp() throws Exception {
        b = (IBytesTest)_helperContext.getDataFactory().create(IBytesTest.class);
        d = (DataObject)b;
    }


    @Test
    public void testBytesSimpleTypeDefaults() {
        assertTrue("abytes has incorrect default", Arrays.equals(D1, b
                .getAbytes()));
        assertTrue("\"abytes\" has incorrect default", Arrays.equals(D1,
                d.getBytes("abytes")));
    }

    @Test
    public void testBytesSimpleTypeSetters() {
        b.setAbytes(D2);
        assertTrue("abytes should have changed to new value", Arrays
                .equals(D2, b.getAbytes()));
        assertTrue("\"abytes\" should have changed to new value", Arrays
                .equals(D2, d.getBytes("abytes")));
    }

    @Test
    public void testBytesSimpleTypeConversions() {
        d.setBigInteger("abytes", new BigInteger(D2));
        assertTrue("abyte should have changed by biginteger", Arrays
                .equals(D2, b.getAbytes()));
        BigInteger b1 = new BigInteger(D1);
        d.setBigInteger("abytes", b1);
        assertTrue("abyte should have changed by biginteger", Arrays
                .equals(D1, b.getAbytes()));
        BigInteger b2 = d.getBigInteger("abytes");
        assertTrue("abyte should have biginteger value " + b1
                + " but is " + b2, b2.equals(b1));
    }

    @Test
    public void testBytesSimpleTypeStringConversions() {
        d.setString("abytes", S1);
        byte[] array = b.getAbytes();
        assertEquals("abyte should have changed by string", D3.length, array.length);
        for (int i = 0; i < array.length; i++) {
            assertEquals("abyte should have changed by string at index " + i,
                D3[i], array[i]);
        }
        assertEquals("abyte should have changed by string", S1, d.getString("abytes"));

        b.setAbytes(D3);
        assertEquals("abyte should have changed by byte[]", S1, d.getString("abytes"));
        array = b.getAbytes();
        assertEquals("abyte should have changed by byte[]", D3.length, array.length);
        for (int i = 0; i < array.length; i++) {
            assertEquals("abyte should have changed by byte[] at index " + i,
                D3[i], array[i]);
        }
    }
}
