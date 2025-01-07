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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.builtin.ChangeSummaryType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.CharObjectSimpleType;
import com.sap.sdo.impl.types.simple.DurationSimpleType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class BuiltinTypeTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public BuiltinTypeTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testChangeSummaryType() {
        ChangeSummary data = (ChangeSummary)_helperContext.getDataFactory().create(ChangeSummaryType.getInstance());

        assertNull(ChangeSummaryType.getInstance().convertFromJavaClass(null));
        try {
            ChangeSummaryType.getInstance().convertFromJavaClass("string");
            fail("ClassCastException expected");
        } catch (ClassCastException ex) { //$JL-EXC$
            // expected
        }
        ChangeSummary cs = ChangeSummaryType.getInstance().convertFromJavaClass(data);
        assertSame(data, cs);

        assertNull(ChangeSummaryType.getInstance().convertToJavaClass(null, Integer.class));
        try {
            ChangeSummaryType.getInstance().convertToJavaClass(data, Integer.class);
            fail("ClassCastException expected");
        } catch (ClassCastException ex) { //$JL-EXC$
            // expected
        }
        cs = ChangeSummaryType.getInstance().convertToJavaClass(data, ChangeSummary.class);
        assertSame(data, cs);
        assertEquals(
            "change summary",
            ChangeSummaryType.getInstance().convertToJavaClass(data, String.class));

        assertEquals(ChangeSummaryType.getInstance(), ChangeSummaryType.getInstance().readResolve());
    }

    @Test
    public void testDataGraphType() {
        DataGraph data = (DataGraph)_helperContext.getDataFactory().create(DataGraphType.getInstance());

        assertNull(DataGraphType.getInstance().convertFromJavaClass(null));
        try {
            DataGraphType.getInstance().convertFromJavaClass("string");
            fail("ClassCastException expected");
        } catch (ClassCastException ex) { //$JL-EXC$
            // expected
        }
        DataGraph dg = DataGraphType.getInstance().convertFromJavaClass(data);
        assertSame(data, dg);

        assertNull(DataGraphType.getInstance().convertToJavaClass(null, Integer.class));
        try {
            DataGraphType.getInstance().convertToJavaClass(data, Integer.class);
            fail("ClassCastException expected");
        } catch (ClassCastException ex) { //$JL-EXC$
            // expected
        }
        dg = DataGraphType.getInstance().convertToJavaClass(data, DataGraph.class);
        assertSame(data, dg);

    }

    @Test
    public void testPropertyType() {
        Property data = (Property)_helperContext.getDataFactory().create(PropertyType.getInstance());

        assertNull(PropertyType.getInstance().convertFromJavaClass(null));
        try {
            PropertyType.getInstance().convertFromJavaClass("string");
            fail("ClassCastException expected");
        } catch (ClassCastException ex) { //$JL-EXC$
            // expected
        }
        Property dg = PropertyType.getInstance().convertFromJavaClass(data);
        assertSame(data, dg);
    }

    @Test
    public void testTypeType() {
        Type data = (Type)_helperContext.getDataFactory().create(TypeType.getInstance());

        assertNull(TypeType.getInstance().convertFromJavaClass(null));
        try {
            TypeType.getInstance().convertFromJavaClass("string");
            fail("ClassCastException expected");
        } catch (ClassCastException ex) { //$JL-EXC$
            // expected
        }
        Type dg = TypeType.getInstance().convertFromJavaClass(data);
        assertSame(data, dg);
    }

    @Test
    public void testClassType() {
        assertEquals(null, JavaSimpleType.CLASS.convertFromJavaClass(null));
        assertEquals(String.class, JavaSimpleType.CLASS.convertFromJavaClass(String.class));
        assertEquals(String.class, JavaSimpleType.CLASS.convertFromJavaClass("java.lang.String"));
        try {
            JavaSimpleType.CLASS.convertFromJavaClass("invalid class name");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("invalid class name is not a class name", ex.getMessage());
        }
        try {
            JavaSimpleType.CLASS.convertFromJavaClass(false);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.Boolean to java.lang.Class", ex.getMessage());
        }

        assertEquals(null, JavaSimpleType.CLASS.convertToJavaClass(null, null));
        assertEquals(String.class, JavaSimpleType.CLASS.convertToJavaClass(String.class, Class.class));
        assertEquals(
            String.class.getName(),
            JavaSimpleType.CLASS.convertToJavaClass(String.class, String.class));
        try {
            JavaSimpleType.CLASS.convertToJavaClass(String.class, Boolean.class);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.Class to java.lang.Boolean", ex.getMessage());
        }
    }

    @Test
    public void testObjectSimpleType() {
        Object anObject = new Object();

        assertEquals(null, JavaSimpleType.OBJECT.convertFromJavaClass(null));

        assertEquals(null, JavaSimpleType.OBJECT.convertToJavaClass(null, null));
        assertEquals(anObject, JavaSimpleType.OBJECT.convertToJavaClass(anObject, Object.class));
        assertEquals(anObject.toString(), JavaSimpleType.OBJECT.convertToJavaClass(anObject, String.class));
        try {
            JavaSimpleType.OBJECT.convertToJavaClass(anObject, Boolean.class);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.Object to java.lang.Boolean", ex.getMessage());
        }

        assertSame(anObject, JavaSimpleType.OBJECT.copy(anObject, true));
    }

    @Test
    public void testUriSimpleType() {
        String string = "a string";

        assertEquals(null, JavaSimpleType.URI.convertFromJavaClass(null));
        assertEquals(string, JavaSimpleType.URI.convertFromJavaClass(string));

        assertEquals(null, JavaSimpleType.URI.convertToJavaClass(null, null));
        assertEquals(string, JavaSimpleType.URI.convertToJavaClass(string, String.class));
        try {
            JavaSimpleType.URI.convertToJavaClass(string, Boolean.class);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.String to java.lang.Boolean", ex.getMessage());
        }
    }

    @Test
    public void testDurationSimpleType() {
        try {
            DurationSimpleType.convertToCalendar(null, null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("not a valid duration: \"null\"", ex.getMessage());
        }
        try {
            DurationSimpleType.convertToCalendar("", null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("not a valid duration: \"\"", ex.getMessage());
        }

        assertEquals(null, JavaSimpleType.DURATION.convertFromJavaClass(null));
        try {
            JavaSimpleType.DURATION.convertFromJavaClass("");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("not a valid duration: \"\"", ex.getMessage());
        }

        assertEquals(null, JavaSimpleType.DURATION.convertToJavaClass(null, null));
        assertEquals("a string", JavaSimpleType.DURATION.convertToJavaClass("a string", String.class));
        try {
            JavaSimpleType.DURATION.convertToJavaClass("", Boolean.class);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.String to java.lang.Boolean", ex.getMessage());
        }
    }

    @Test
    public void testTimeSimpleType() {
        String timeStr = "00:00:00.000Z";
        Time time = JavaSimpleType.TIME.convertToJavaClass(timeStr, Time.class);
        assertEquals(timeStr, JavaSimpleType.TIME.convertFromJavaClass(time));
    }

    @Test
    public void testCharObjectSimpleType() {
        JavaSimpleType<Character> nillableType = JavaSimpleType.CHARACTEROBJECT.getNillableType();
        assertNotNull(nillableType);
        assertTrue(nillableType instanceof CharObjectSimpleType);

        Character character = 'c';

        assertEquals(null, JavaSimpleType.CHARACTEROBJECT.convertFromJavaClass(null));
        assertEquals(character, JavaSimpleType.CHARACTEROBJECT.convertFromJavaClass(character));
        assertEquals(character, JavaSimpleType.CHARACTEROBJECT.convertFromJavaClass(character.toString()));
        try {
            JavaSimpleType.CHARACTEROBJECT.convertFromJavaClass("a String");
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.String to java.lang.Character", ex.getMessage());
        }

        assertEquals(null, JavaSimpleType.CHARACTEROBJECT.convertToJavaClass(null, null));
        assertEquals(character, JavaSimpleType.CHARACTEROBJECT.convertToJavaClass(character, Character.class));
        assertEquals(character.toString(), JavaSimpleType.CHARACTEROBJECT.convertToJavaClass(character, String.class));
        try {
            JavaSimpleType.CHARACTEROBJECT.convertToJavaClass(character, Boolean.class);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.Character to java.lang.Boolean", ex.getMessage());
        }
    }

    @Test
    public void testDateSimpleType() {
        Date date = new Date();

        assertEquals(null, JavaSimpleType.DATE.convertFromJavaClass(null));
        assertEquals(date, JavaSimpleType.DATE.convertFromJavaClass(date));
        assertEquals(date, JavaSimpleType.DATE.convertFromJavaClass(JavaSimpleType.DATE.toString(date)));
        assertEquals(date, JavaSimpleType.DATE.convertFromJavaClass(new Long(date.getTime())));
        assertEquals(date, JavaSimpleType.DATE.convertFromJavaClass(new java.sql.Date(date.getTime())));
//        assertEquals(null, JavaSimpleType.DATE.convertFromJavaClass(Boolean.FALSE));
        try {
            JavaSimpleType.DATE.convertFromJavaClass("a string");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("invalid date string: a string", ex.getMessage());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(JavaSimpleType.DATE.convertFromJavaClass("--12"));
        assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
        calendar.setTime(JavaSimpleType.DATE.convertFromJavaClass("--12-01:00"));
        assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
        calendar.setTime(JavaSimpleType.DATE.convertFromJavaClass("--12-03"));
        assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));

        assertEquals(null, JavaSimpleType.DATE.convertToJavaClass(null, null));
        assertEquals(date, JavaSimpleType.DATE.convertToJavaClass(date, Date.class));
        assertEquals(
            JavaSimpleType.DATE.toString(date),
            JavaSimpleType.DATE.convertToJavaClass(date, String.class));
        assertEquals(new Long(date.getTime()), JavaSimpleType.DATE.convertToJavaClass(date, Long.class));
        assertEquals(
            new java.sql.Date(date.getTime()),
            JavaSimpleType.DATE.convertToJavaClass(date, java.sql.Date.class));
        try {
            JavaSimpleType.DATE.convertToJavaClass(date, Boolean.class);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.util.Date to java.lang.Boolean", ex.getMessage());
        }

        assertEquals(date, JavaSimpleType.DATE.copy(date, false));
    }

    @Test
    public void testDateTimeSimpleType() {
        Date date = new Date();
        String dateStr = JavaSimpleType.DATETIME.toString(date);

        assertEquals(null, JavaSimpleType.DATETIME.convertFromJavaClass(null));
        assertEquals(dateStr, JavaSimpleType.DATETIME.convertFromJavaClass(date));
        assertEquals(dateStr, JavaSimpleType.DATETIME.convertFromJavaClass(dateStr));
        assertEquals(dateStr, JavaSimpleType.DATETIME.convertFromJavaClass(new java.sql.Date(date.getTime())));
        try {
            JavaSimpleType.DATETIME.convertFromJavaClass(new Long(date.getTime()));
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.Long to java.lang.String", ex.getMessage());
        }

        assertEquals(null, JavaSimpleType.DATETIME.convertToJavaClass(null, null));
        assertEquals(date, JavaSimpleType.DATETIME.convertToJavaClass(dateStr, Date.class));
        assertEquals(dateStr, JavaSimpleType.DATETIME.convertToJavaClass(dateStr, String.class));
        assertEquals(
            new java.sql.Date(date.getTime()),
            JavaSimpleType.DATETIME.convertToJavaClass(dateStr, java.sql.Date.class));
        try {
            JavaSimpleType.DATETIME.convertToJavaClass(dateStr, Boolean.class);
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("Can not convert from java.lang.String to java.lang.Boolean", ex.getMessage());
        }
        try {
            JavaSimpleType.DATETIME.convertToJavaClass("a string", Date.class);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("invalid date string: a string", ex.getMessage());
        }
    }

    @Test
    public void testListSimpleType() {
        try {
            new ListSimpleType(DataGraphType.getInstance(), _helperContext);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("cannot create simple type list from complex type commonj.sdo#DataGraphType", ex.getMessage());
        }

        ListSimpleType listType = new ListSimpleType(JavaSimpleType.BOOLEAN, _helperContext);
        assertEquals(null, listType.convertFromJavaClass(null));

        assertEquals(null, listType.convertToJavaClass(null, null));
        List<String> list = new ArrayList<String>();
        assertEquals(list, listType.convertToJavaClass(list, List.class));

        assertEquals(true, listType.isAbstract());
        List copy = listType.copy(list, false);
        assertNotNull(copy);
        assertEquals(0, copy.size());
        assertNotSame(list, copy);

        assertEquals(listType, listType.readResolve());
    }

    @Test
    public void testBytesSimpleType() {
        byte[] bytes = new byte[]{42};

        assertEquals(null, JavaSimpleType.BYTES.convertFromJavaClass(null));
        assertEquals(bytes, JavaSimpleType.BYTES.convertFromJavaClass(bytes));

        assertEquals(null, JavaSimpleType.BYTES.convertToJavaClass(null, null));
        assertEquals(bytes, JavaSimpleType.BYTES.convertToJavaClass(bytes, byte[].class));

        byte[] copy = JavaSimpleType.BYTES.copy(bytes, false);
        assertNotNull(copy);
        assertEquals(bytes.length, copy.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], copy[i]);
        }

        assertNull(JavaSimpleType.BYTES.getFacets());
        assertNull(JavaSimpleType.BYTES.getIdProperty());
    }

    @Test
    public void testDecimalSimpleType() {
        BigDecimal decimal = new BigDecimal(42);

        assertEquals(null, JavaSimpleType.DECIMAL.convertFromJavaClass(null));
        assertEquals(decimal, JavaSimpleType.DECIMAL.convertFromJavaClass(decimal));

        assertEquals(null, JavaSimpleType.DECIMAL.convertToJavaClass(null, null));
        assertEquals(decimal, JavaSimpleType.DECIMAL.convertToJavaClass(decimal, BigDecimal.class));

        BigDecimal copy = JavaSimpleType.DECIMAL.copy(decimal, false);
        assertEquals(decimal, copy);
    }

    @Test
    public void testIntegerSimpleType() {
        BigInteger decimal = new BigInteger("42");

        assertEquals(null, JavaSimpleType.INTEGER.convertFromJavaClass(null));
        assertEquals(decimal, JavaSimpleType.INTEGER.convertFromJavaClass(decimal));

        assertEquals(null, JavaSimpleType.INTEGER.convertToJavaClass(null, null));
        assertEquals(decimal, JavaSimpleType.INTEGER.convertToJavaClass(decimal, BigInteger.class));

        BigInteger copy = JavaSimpleType.INTEGER.copy(decimal, false);
        assertEquals(decimal, copy);
    }

    @Test
    public void testBooleanObjectType() {
        assertEquals(null, JavaSimpleType.BOOLEANOBJECT.convertFromJavaClass(null));
        assertEquals(Boolean.TRUE, JavaSimpleType.BOOLEANOBJECT.convertFromJavaClass(Boolean.TRUE));

        assertEquals(null, JavaSimpleType.BOOLEANOBJECT.convertToJavaClass(null, null));
        assertEquals(Boolean.TRUE, JavaSimpleType.BOOLEANOBJECT.convertToJavaClass(Boolean.TRUE, Boolean.class));
    }

    @Test
    public void testDoubleObjectType() {
        assertEquals(null, JavaSimpleType.DOUBLEOBJECT.convertFromJavaClass(null));
        assertEquals(42d, JavaSimpleType.DOUBLEOBJECT.convertFromJavaClass(42d));

        assertEquals(null, JavaSimpleType.DOUBLEOBJECT.convertToJavaClass(null, null));
        assertEquals(42d, JavaSimpleType.DOUBLEOBJECT.convertToJavaClass(42d, Double.class));
    }

    @Test
    public void testFloatObjectType() {
        assertEquals(null, JavaSimpleType.FLOATOBJECT.convertFromJavaClass(null));
        assertEquals(42f, JavaSimpleType.FLOATOBJECT.convertFromJavaClass(42f));

        assertEquals(null, JavaSimpleType.FLOATOBJECT.convertToJavaClass(null, null));
        assertEquals(42f, JavaSimpleType.FLOATOBJECT.convertToJavaClass(42f, Float.class));
    }

    @Test
    public void testShortObjectType() {
        assertEquals(null, JavaSimpleType.SHORTOBJECT.convertFromJavaClass(null));
        assertEquals(Short.valueOf((short)42), JavaSimpleType.SHORTOBJECT.convertFromJavaClass((short)42));

        assertEquals(null, JavaSimpleType.SHORTOBJECT.convertToJavaClass(null, null));
        assertEquals(Short.valueOf((short)42), JavaSimpleType.SHORTOBJECT.convertToJavaClass((short)42, Short.class));
    }

    @Test
    public void testByteObjectType() {
        assertEquals(null, JavaSimpleType.BYTEOBJECT.convertFromJavaClass(null));
        assertEquals(Byte.valueOf((byte)42), JavaSimpleType.BYTEOBJECT.convertFromJavaClass((byte)42));

        assertEquals(null, JavaSimpleType.BYTEOBJECT.convertToJavaClass(null, null));
        assertEquals(Byte.valueOf((byte)42), JavaSimpleType.BYTEOBJECT.convertToJavaClass((byte)42, Byte.class));
    }

    @Test
    public void testLongObjectType() {
        assertEquals(null, JavaSimpleType.LONGOBJECT.convertFromJavaClass(null));
        assertEquals(new Long(42), JavaSimpleType.LONGOBJECT.convertFromJavaClass(42l));

        assertEquals(null, JavaSimpleType.LONGOBJECT.convertToJavaClass(null, null));
        assertEquals(new Long(42), JavaSimpleType.LONGOBJECT.convertToJavaClass(42l, Long.class));
    }

    @Test
    public void testIntObjectType() {
        assertEquals(null, JavaSimpleType.INTOBJECT.convertFromJavaClass(null));
        assertEquals(new Integer(42), JavaSimpleType.INTOBJECT.convertFromJavaClass(42));

        assertEquals(null, JavaSimpleType.INTOBJECT.convertToJavaClass(null, null));
        assertEquals(new Integer(42), JavaSimpleType.INTOBJECT.convertToJavaClass(42, Integer.class));
    }
}
