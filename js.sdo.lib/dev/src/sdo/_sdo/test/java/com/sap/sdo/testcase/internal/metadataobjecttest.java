package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.builtin.MetaDataType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.Property;

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

/**
 * @author D042774
 *
 */
public class MetaDataObjectTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public MetaDataObjectTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private MetaDataType _type;
    private Property _prop;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _type =
            (MetaDataType)_helperContext.getTypeHelper().getType(
                URINamePair.CHANGESUMMARY_TYPE.getURI(),
                URINamePair.CHANGESUMMARY_TYPE.getName());
        _prop = (Property)_type.getProperties().get(0);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _type = null;
        _prop = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#set(java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testSetStringObject() {
        try {
            _type.set("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#isSet(java.lang.String)}.
     */
    @Test
    public void testIsSetString() {
        assertFalse(_type.isSet("string"));
        assertFalse(_type.isSet((String)null));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#unset(java.lang.String)}.
     */
    @Test
    public void testUnsetString() {
        try {
            _type.unset("string");
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBoolean(java.lang.String)}.
     */
    @Test
    public void testGetBooleanString() {
        try {
            _type.getBoolean("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getByte(java.lang.String)}.
     */
    @Test
    public void testGetByteString() {
        try {
            _type.getByte("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getChar(java.lang.String)}.
     */
    @Test
    public void testGetCharString() {
        try {
            _type.getChar("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDouble(java.lang.String)}.
     */
    @Test
    public void testGetDoubleString() {
        try {
            _type.getDouble("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getFloat(java.lang.String)}.
     */
    @Test
    public void testGetFloatString() {
        try {
            _type.getFloat("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getInt(java.lang.String)}.
     */
    @Test
    public void testGetIntString() {
        try {
            _type.getInt("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getLong(java.lang.String)}.
     */
    @Test
    public void testGetLongString() {
        try {
            _type.getLong("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getShort(java.lang.String)}.
     */
    @Test
    public void testGetShortString() {
        try {
            _type.getShort("string");
            fail("NullPointerException expected");

        } catch (NullPointerException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBytes(java.lang.String)}.
     */
    @Test
    public void testGetBytesString() {
        assertNull(_type.getBytes("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBigDecimal(java.lang.String)}.
     */
    @Test
    public void testGetBigDecimalString() {
        assertNull(_type.getBigDecimal("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBigInteger(java.lang.String)}.
     */
    @Test
    public void testGetBigIntegerString() {
        assertNull(_type.getBigInteger("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDataObject(java.lang.String)}.
     */
    @Test
    public void testGetDataObjectString() {
        assertNull(_type.getDataObject("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDate(java.lang.String)}.
     */
    @Test
    public void testGetDateString() {
        assertNull(_type.getDate("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getString(java.lang.String)}.
     */
    @Test
    public void testGetStringString() {
        assertNull(_type.getString("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getList(java.lang.String)}.
     */
    @Test
    public void testGetListString() {
        assertNull(_type.getList("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getSequence(java.lang.String)}.
     */
    @Test
    public void testGetSequenceString() {
        assertNull(_type.getSequence("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBoolean(java.lang.String, boolean)}.
     */
    @Test
    public void testSetBooleanStringBoolean() {
        try {
            _type.setBoolean("string", false);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setByte(java.lang.String, byte)}.
     */
    @Test
    public void testSetByteStringByte() {
        try {
            _type.setByte("string", (byte)0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setChar(java.lang.String, char)}.
     */
    @Test
    public void testSetCharStringChar() {
        try {
            _type.setChar("string", '0');
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDouble(java.lang.String, double)}.
     */
    @Test
    public void testSetDoubleStringDouble() {
        try {
            _type.setDouble("string", 0D);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setFloat(java.lang.String, float)}.
     */
    @Test
    public void testSetFloatStringFloat() {
        try {
            _type.setFloat("string", 0F);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setInt(java.lang.String, int)}.
     */
    @Test
    public void testSetIntStringInt() {
        try {
            _type.setInt("string", 0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setLong(java.lang.String, long)}.
     */
    @Test
    public void testSetLongStringLong() {
        try {
            _type.setLong("string", 0L);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setShort(java.lang.String, short)}.
     */
    @Test
    public void testSetShortStringShort() {
        try {
            _type.setShort("string", (short)0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBytes(java.lang.String, byte[])}.
     */
    @Test
    public void testSetBytesStringByteArray() {
        try {
            _type.setBytes("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBigDecimal(java.lang.String, java.math.BigDecimal)}.
     */
    @Test
    public void testSetBigDecimalStringBigDecimal() {
        try {
            _type.setBigDecimal("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBigInteger(java.lang.String, java.math.BigInteger)}.
     */
    @Test
    public void testSetBigIntegerStringBigInteger() {
        try {
            _type.setBigInteger("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDataObject(java.lang.String, commonj.sdo.DataObject)}.
     */
    @Test
    public void testSetDataObjectStringDataObject() {
        try {
            _type.setDataObject("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDate(java.lang.String, java.util.Date)}.
     */
    @Test
    public void testSetDateStringDate() {
        try {
            _type.setDate("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setString(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testSetStringStringString() {
        try {
            _type.setString("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setList(java.lang.String, java.util.List)}.
     */
    @Test
    public void testSetListStringList() {
        try {
            _type.setList("string", null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#set(int, java.lang.Object)}.
     */
    @Test
    public void testSetIntObject() {
        try {
            _type.set(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#isSet(int)}.
     */
    @Test
    public void testIsSetInt() {
        assertFalse(_type.isSet(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#unset(int)}.
     */
    @Test
    public void testUnsetInt() {
        try {
            _type.unset(0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBoolean(int)}.
     */
    @Test
    public void testGetBooleanInt() {
        try {
            _type.getBoolean(100);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getByte(int)}.
     */
    @Test
    public void testGetByteInt() {
        assertEquals((byte)0, _type.getByte(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getChar(int)}.
     */
    @Test
    public void testGetCharInt() {
        assertEquals((char)0, _type.getChar(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDouble(int)}.
     */
    @Test
    public void testGetDoubleInt() {
        assertEquals(0D, _type.getDouble(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getFloat(int)}.
     */
    @Test
    public void testGetFloatInt() {
        assertEquals(0F, _type.getFloat(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getInt(int)}.
     */
    @Test
    public void testGetIntInt() {
        assertEquals(0, _type.getInt(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getLong(int)}.
     */
    @Test
    public void testGetLongInt() {
        assertEquals(0L, _type.getLong(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getShort(int)}.
     */
    @Test
    public void testGetShortInt() {
        assertEquals((short)0, _type.getShort(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBytes(int)}.
     */
    @Test
    public void testGetBytesInt() {
        assertNull(_type.getBytes(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBigDecimal(int)}.
     */
    @Test
    public void testGetBigDecimalInt() {
        assertNull(_type.getBigDecimal(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBigInteger(int)}.
     */
    @Test
    public void testGetBigIntegerInt() {
        assertNull(_type.getBigInteger(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDataObject(int)}.
     */
    @Test
    public void testGetDataObjectInt() {
        assertNull(_type.getDataObject(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDate(int)}.
     */
    @Test
    public void testGetDateInt() {
        assertNull(_type.getDate(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getString(int)}.
     */
    @Test
    public void testGetStringInt() {
        try {
            _type.getString(100);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getList(int)}.
     */
    @Test
    public void testGetListInt() {
        try {
            _type.getList(100);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) { //$JL-EXC$
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getSequence(int)}.
     */
    @Test
    public void testGetSequenceInt() {
        assertNull(_type.getSequence(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBoolean(int, boolean)}.
     */
    @Test
    public void testSetBooleanIntBoolean() {
        try {
            _type.setBoolean(0, false);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setByte(int, byte)}.
     */
    @Test
    public void testSetByteIntByte() {
        try {
            _type.setByte(0, (byte)0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setChar(int, char)}.
     */
    @Test
    public void testSetCharIntChar() {
        try {
            _type.setChar(0, '0');
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDouble(int, double)}.
     */
    @Test
    public void testSetDoubleIntDouble() {
        try {
            _type.setDouble(0, 0D);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setFloat(int, float)}.
     */
    @Test
    public void testSetFloatIntFloat() {
        try {
            _type.setFloat(0, 0F);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setInt(int, int)}.
     */
    @Test
    public void testSetIntIntInt() {
        try {
            _type.setInt(0, 0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setLong(int, long)}.
     */
    @Test
    public void testSetLongIntLong() {
        try {
            _type.setLong(0, 0L);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setShort(int, short)}.
     */
    @Test
    public void testSetShortIntShort() {
        try {
            _type.setShort(0, (short)0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBytes(int, byte[])}.
     */
    @Test
    public void testSetBytesIntByteArray() {
        try {
            _type.setBytes(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBigDecimal(int, java.math.BigDecimal)}.
     */
    @Test
    public void testSetBigDecimalIntBigDecimal() {
        try {
            _type.setBigDecimal(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBigInteger(int, java.math.BigInteger)}.
     */
    @Test
    public void testSetBigIntegerIntBigInteger() {
        try {
            _type.setBigInteger(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDataObject(int, commonj.sdo.DataObject)}.
     */
    @Test
    public void testSetDataObjectIntDataObject() {
        try {
            _type.setDataObject(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDate(int, java.util.Date)}.
     */
    @Test
    public void testSetDateIntDate() {
        try {
            _type.setDate(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setString(int, java.lang.String)}.
     */
    @Test
    public void testSetStringIntString() {
        try {
            _type.setString(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setList(int, java.util.List)}.
     */
    @Test
    public void testSetListIntList() {
        try {
            _type.setList(0, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#set(commonj.sdo.Property, java.lang.Object)}.
     */
    @Test
    public void testSetPropertyObject() {
        try {
            _type.set(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#isSet(commonj.sdo.Property)}.
     */
    @Test
    public void testIsSetProperty() {
        assertFalse(_type.isSet(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#unset(commonj.sdo.Property)}.
     */
    @Test
    public void testUnsetProperty() {
        try {
            _type.unset(_prop);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBoolean(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBooleanProperty() {
        try {
            _type.getBoolean(_prop);
            fail("ClassCastException(\"java.util.Collections$EmptyList\") expected");
        } catch (ClassCastException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("java.util.Collections$EmptyList"));
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getByte(commonj.sdo.Property)}.
     */
    @Test
    public void testGetByteProperty() {
        assertEquals((byte)0, _type.getByte(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getChar(commonj.sdo.Property)}.
     */
    @Test
    public void testGetCharProperty() {
        assertEquals((char)0, _type.getChar(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDouble(commonj.sdo.Property)}.
     */
    @Test
    public void testGetDoubleProperty() {
        assertEquals(0D, _type.getDouble(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getFloat(commonj.sdo.Property)}.
     */
    @Test
    public void testGetFloatProperty() {
        assertEquals(0F, _type.getFloat(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getInt(commonj.sdo.Property)}.
     */
    @Test
    public void testGetIntProperty() {
        assertEquals(0, _type.getInt(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getLong(commonj.sdo.Property)}.
     */
    @Test
    public void testGetLongProperty() {
        assertEquals(0L, _type.getLong(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getShort(commonj.sdo.Property)}.
     */
    @Test
    public void testGetShortProperty() {
        assertEquals((short)0, _type.getShort(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBytes(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBytesProperty() {
        assertNull(_type.getBytes(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBigDecimal(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBigDecimalProperty() {
        assertNull(_type.getBigDecimal(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getBigInteger(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBigIntegerProperty() {
        assertNull(_type.getBigInteger(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDataObject(commonj.sdo.Property)}.
     */
    @Test
    public void testGetDataObjectProperty() {
        try {
            _type.getDataObject(_prop);
            fail("ClassCastException(\"java.util.Collections$EmptyList\") expected");
        } catch (ClassCastException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("java.util.Collections$EmptyList"));
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDate(commonj.sdo.Property)}.
     */
    @Test
    public void testGetDateProperty() {
        try {
            _type.getDate(_prop);
            fail("ClassCastException(\"java.util.Collections$EmptyList\") expected");
        } catch (ClassCastException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("java.util.Collections$EmptyList"));
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getString(commonj.sdo.Property)}.
     */
    @Test
    public void testGetStringProperty() {
        assertEquals("[]", _type.getString(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getList(commonj.sdo.Property)}.
     */
    @Test
    public void testGetListProperty() {
        assertSame(Collections.EMPTY_LIST, _type.getList(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getSequence(commonj.sdo.Property)}.
     */
    @Test
    public void testGetSequenceProperty() {
        assertNull(_type.getSequence(_prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBoolean(commonj.sdo.Property, boolean)}.
     */
    @Test
    public void testSetBooleanPropertyBoolean() {
        try {
            _type.setBoolean(_prop, false);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setByte(commonj.sdo.Property, byte)}.
     */
    @Test
    public void testSetBytePropertyByte() {
        try {
            _type.setByte(_prop, (byte)0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setChar(commonj.sdo.Property, char)}.
     */
    @Test
    public void testSetCharPropertyChar() {
        try {
            _type.setChar(_prop, '0');
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDouble(commonj.sdo.Property, double)}.
     */
    @Test
    public void testSetDoublePropertyDouble() {
        try {
            _type.setDouble(_prop, 0D);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setFloat(commonj.sdo.Property, float)}.
     */
    @Test
    public void testSetFloatPropertyFloat() {
        try {
            _type.setFloat(_prop, 0F);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setInt(commonj.sdo.Property, int)}.
     */
    @Test
    public void testSetIntPropertyInt() {
        try {
            _type.setInt(_prop, 0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setLong(commonj.sdo.Property, long)}.
     */
    @Test
    public void testSetLongPropertyLong() {
        try {
            _type.setLong(_prop, 0L);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setShort(commonj.sdo.Property, short)}.
     */
    @Test
    public void testSetShortPropertyShort() {
        try {
            _type.setShort(_prop, (short)0);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBytes(commonj.sdo.Property, byte[])}.
     */
    @Test
    public void testSetBytesPropertyByteArray() {
        try {
            _type.setBytes(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBigDecimal(commonj.sdo.Property, java.math.BigDecimal)}.
     */
    @Test
    public void testSetBigDecimalPropertyBigDecimal() {
        try {
            _type.setBigDecimal(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setBigInteger(commonj.sdo.Property, java.math.BigInteger)}.
     */
    @Test
    public void testSetBigIntegerPropertyBigInteger() {
        try {
            _type.setBigInteger(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDataObject(commonj.sdo.Property, commonj.sdo.DataObject)}.
     */
    @Test
    public void testSetDataObjectPropertyDataObject() {
        try {
            _type.setDataObject(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setDate(commonj.sdo.Property, java.util.Date)}.
     */
    @Test
    public void testSetDatePropertyDate() {
        try {
            _type.setDate(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setString(commonj.sdo.Property, java.lang.String)}.
     */
    @Test
    public void testSetStringPropertyString() {
        try {
            _type.setString(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#setList(commonj.sdo.Property, java.util.List)}.
     */
    @Test
    public void testSetListPropertyList() {
        try {
            _type.setList(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#createDataObject(java.lang.String)}.
     */
    @Test
    public void testCreateDataObjectString() {
        try {
            _type.createDataObject((String)null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#createDataObject(int)}.
     */
    @Test
    public void testCreateDataObjectInt() {
        assertNull(_type.createDataObject(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#createDataObject(commonj.sdo.Property)}.
     */
    @Test
    public void testCreateDataObjectProperty() {
        try {
            _type.createDataObject(_prop);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#createDataObject(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCreateDataObjectStringStringString() {
        try {
            _type.createDataObject(null, null, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#createDataObject(int, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCreateDataObjectIntStringString() {
        try {
            _type.createDataObject(0, null, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#createDataObject(commonj.sdo.Property, commonj.sdo.Type)}.
     */
    @Test
    public void testCreateDataObjectPropertyType() {
        try {
            _type.createDataObject(_prop, null);
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#delete()}.
     */
    @Test
    public void testDelete() {
        try {
            _type.delete();
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getContainer()}.
     */
    @Test
    public void testGetContainer() {
        assertNull(_type.getContainer());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getContainmentProperty()}.
     */
    @Test
    public void testGetContainmentProperty() {
        assertNull(_type.getContainmentProperty());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getDataGraph()}.
     */
    @Test
    public void testGetDataGraph() {
        assertNull(_type.getDataGraph());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getType()}.
     */
    @Test
    public void testGetType() {
        assertSame(TypeType.getInstance(), _type.getType());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getSequence()}.
     */
    @Test
    public void testGetSequence() {
        assertNull(_type.getSequence());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getRootObject()}.
     */
    @Test
    public void testGetRootObject() {
        assertNull(_type.getRootObject());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#getChangeSummary()}.
     */
    @Test
    public void testGetChangeSummary() {
        assertNull(_type.getChangeSummary());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.MetaDataObject#detach()}.
     */
    @Test
    public void testDetach() {
        try {
            _type.detach();
            fail("IllegalArgumentException(\"Attempt to modify predefined type\") expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("Attempt to modify predefined type", ex.getMessage());
        }
    }

}
