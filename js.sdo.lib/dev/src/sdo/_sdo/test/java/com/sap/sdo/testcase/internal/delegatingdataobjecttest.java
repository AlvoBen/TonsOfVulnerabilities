/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.DataObjectType;
import com.sap.sdo.impl.types.builtin.DelegatingDataObject;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleExtension;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class DelegatingDataObjectTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DelegatingDataObjectTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String TYPE_NAME = "SimpleDataType";
    private static final String TYPE_URI = "com.sap.sdo.testcase.internal";

    private DataObject _data;
    private DelegatingDataObject _delegating;

    private BigDecimal _decimal = new BigDecimal(42);
    private BigInteger _integer = new BigInteger("43");
    private boolean _boolean = true;
    private byte _byte = (byte)44;
    private byte[] _bytes = new byte[]{45};
    private char _char = 'c';
    private Date _date = new Date();
    private double _double = 46d;
    private float _float = 47f;
    private int _int = 48;
    private List<String> _list = Arrays.asList(new String[]{"49"});
    private long _long = 50l;
    private short _short = (short)51;
    private String _string = "string";
    private DataObject _dataObject = null;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        Type type = getType();

        _data = _helperContext.getDataFactory().create(type);
        _data.setBigDecimal("decimal", _decimal);
        _data.setBigInteger("integer", _integer);
        _data.setBoolean("boolean", _boolean);
        _data.setByte("byte", _byte);
        _data.setBytes("bytes", _bytes);
        _data.setChar("char", _char);
        _data.setDate("date", _date);
        _data.setDouble("double", _double);
        _data.setFloat("float", _float);
        _data.setInt("int", _int);
        _data.setList("list", _list);
        _data.setLong("long", _long);
        _data.setShort("short", _short);
        _data.setString("string", _string);

        _dataObject = _helperContext.getDataFactory().create(OpenType.getInstance());
        _dataObject.setString("prop", "a");
        _data.setDataObject("dataObject", _dataObject);

        _delegating = new DataGraphType.DataGraphLogic(_data);
        _delegating.createDataObject("containment");
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _data = null;
        _delegating = null;
    }

    /**
     * @return
     */
    private Type getType() {
        Type type = _helperContext.getTypeHelper().getType(TYPE_URI, TYPE_NAME);

        if (type == null) {
            DataObject typeObj = _helperContext.getDataFactory().create("commonj.sdo", "Type");
            typeObj.set(TypeType.NAME, TYPE_NAME);
            typeObj.set(TypeType.URI, TYPE_URI);
            DataObject decimalProp = typeObj.createDataObject("property");
            decimalProp.set(PropertyType.NAME, "decimal");
            decimalProp.set(PropertyType.TYPE, JavaSimpleType.DECIMAL);
            DataObject integerProp = typeObj.createDataObject("property");
            integerProp.set(PropertyType.NAME, "integer");
            integerProp.set(PropertyType.TYPE, JavaSimpleType.INTEGER);
            DataObject boolProp = typeObj.createDataObject("property");
            boolProp.set(PropertyType.NAME, "boolean");
            boolProp.set(PropertyType.TYPE, JavaSimpleType.BOOLEAN);
            DataObject byteProp = typeObj.createDataObject("property");
            byteProp.set(PropertyType.NAME, "byte");
            byteProp.set(PropertyType.TYPE, JavaSimpleType.BYTE);
            DataObject bytesProp = typeObj.createDataObject("property");
            bytesProp.set(PropertyType.NAME, "bytes");
            bytesProp.set(PropertyType.TYPE, JavaSimpleType.BYTES);
            DataObject charProp = typeObj.createDataObject("property");
            charProp.set(PropertyType.NAME, "char");
            charProp.set(PropertyType.TYPE, JavaSimpleType.CHARACTER);
            DataObject dateProp = typeObj.createDataObject("property");
            dateProp.set(PropertyType.NAME, "date");
            dateProp.set(PropertyType.TYPE, JavaSimpleType.DATE);
            DataObject doubleProp = typeObj.createDataObject("property");
            doubleProp.set(PropertyType.NAME, "double");
            doubleProp.set(PropertyType.TYPE, JavaSimpleType.DOUBLE);
            DataObject floatProp = typeObj.createDataObject("property");
            floatProp.set(PropertyType.NAME, "float");
            floatProp.set(PropertyType.TYPE, JavaSimpleType.FLOAT);
            DataObject intProp = typeObj.createDataObject("property");
            intProp.set(PropertyType.NAME, "int");
            intProp.set(PropertyType.TYPE, JavaSimpleType.INT);
            DataObject listProp = typeObj.createDataObject("property");
            listProp.set(PropertyType.NAME, "list");
            listProp.set(PropertyType.TYPE, JavaSimpleType.STRING);
            listProp.set(PropertyType.MANY, true);
            DataObject longProp = typeObj.createDataObject("property");
            longProp.set(PropertyType.NAME, "long");
            longProp.set(PropertyType.TYPE, JavaSimpleType.LONG);
            DataObject shortProp = typeObj.createDataObject("property");
            shortProp.set(PropertyType.NAME, "short");
            shortProp.set(PropertyType.TYPE, JavaSimpleType.SHORT);
            DataObject stringProp = typeObj.createDataObject("property");
            stringProp.set(PropertyType.NAME, "string");
            stringProp.set(PropertyType.TYPE, JavaSimpleType.STRING);
            DataObject dataObjProp = typeObj.createDataObject("property");
            dataObjProp.set(PropertyType.NAME, "dataObject");
            dataObjProp.set(PropertyType.TYPE, DataObjectType.getInstance());
            DataObject containmentProp = typeObj.createDataObject("property");
            containmentProp.set(PropertyType.NAME, "containment");
            containmentProp.set(
                PropertyType.TYPE,
                _helperContext.getTypeHelper().getType(SimpleAttrIntf.class));
            containmentProp.set(PropertyType.CONTAINMENT, true);

            type = _helperContext.getTypeHelper().define(typeObj);
        }

        return type;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#createDataObject(int, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCreateDataObjectIntStringString() {
        Type type = _helperContext.getTypeHelper().getType(SimpleExtension.class);
        DataObject newObject =
            _delegating.createDataObject(
                getIndex("containment"),
                type.getURI(),
                type.getName());
        assertNotNull(newObject);
        assertEquals(type, newObject.getType());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#createDataObject(commonj.sdo.Property, commonj.sdo.Type)}.
     */
    @Test
    public void testCreateDataObjectPropertyType() {
        Type type = _helperContext.getTypeHelper().getType(SimpleExtension.class);
        DataObject newObject =
            _delegating.createDataObject(
                getProperty("containment"), type);
        assertNotNull(newObject);
        assertEquals(type, newObject.getType());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#createDataObject(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCreateDataObjectStringStringString() {
        Type type = _helperContext.getTypeHelper().getType(SimpleExtension.class);
        DataObject newObject =
            _delegating.createDataObject("containment", type.getURI(), type.getName());
        assertNotNull(newObject);
        assertEquals(type, newObject.getType());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#delete()}.
     */
    @Test
    public void testDelete() {
        for (Property prop : (List<Property>)_data.getInstanceProperties()) {
            assertEquals(true, _data.isSet(prop));
        }
        _delegating.delete();
        for (Property prop : (List<Property>)_data.getInstanceProperties()) {
            assertEquals(false, _data.isSet(prop));
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBigDecimal(int)}.
     */
    @Test
    public void testGetBigDecimalInt() {
        assertEquals(_decimal, _delegating.getBigDecimal(getIndex("decimal")));

        BigDecimal newDecimal = new BigDecimal(13);
        _delegating.setBigDecimal(getIndex("decimal"), newDecimal);
        assertEquals(newDecimal, _data.getBigDecimal("decimal"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBigDecimal(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBigDecimalProperty() {
        assertEquals(_decimal, _delegating.getBigDecimal(getProperty("decimal")));

        BigDecimal newDecimal = new BigDecimal(14);
        _delegating.setBigDecimal(getProperty("decimal"), newDecimal);
        assertEquals(newDecimal, _data.getBigDecimal("decimal"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBigDecimal(java.lang.String)}.
     */
    @Test
    public void testGetBigDecimalString() {
        assertEquals(_decimal, _delegating.getBigDecimal("decimal"));

        BigDecimal newDecimal = new BigDecimal(15);
        _delegating.setBigDecimal("decimal", newDecimal);
        assertEquals(newDecimal, _data.getBigDecimal("decimal"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBigInteger(int)}.
     */
    @Test
    public void testGetBigIntegerInt() {
        assertEquals(_integer, _delegating.getBigInteger(getIndex("integer")));

        BigInteger newInteger = new BigInteger("16");
        _delegating.setBigInteger(getIndex("integer"), newInteger);
        assertEquals(newInteger, _data.getBigInteger("integer"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBigInteger(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBigIntegerProperty() {
        assertEquals(_integer, _delegating.getBigInteger(getProperty("integer")));

        BigInteger newInteger = new BigInteger("16");
        _delegating.setBigInteger(getProperty("integer"), newInteger);
        assertEquals(newInteger, _data.getBigInteger("integer"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBigInteger(java.lang.String)}.
     */
    @Test
    public void testGetBigIntegerString() {
        assertEquals(_integer, _delegating.getBigInteger("integer"));

        BigInteger newInteger = new BigInteger("16");
        _delegating.setBigInteger("integer", newInteger);
        assertEquals(newInteger, _data.getBigInteger("integer"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBoolean(int)}.
     */
    @Test
    public void testGetBooleanInt() {
        assertEquals(_boolean, _delegating.getBoolean(getIndex("boolean")));

        boolean newBoolean = !_boolean;
        _delegating.setBoolean(getIndex("boolean"), newBoolean);
        assertEquals(newBoolean, _data.getBoolean("boolean"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBoolean(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBooleanProperty() {
        assertEquals(_boolean, _delegating.getBoolean(getProperty("boolean")));

        boolean newBoolean = !_boolean;
        _delegating.setBoolean(getProperty("boolean"), newBoolean);
        assertEquals(newBoolean, _data.getBoolean("boolean"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBoolean(java.lang.String)}.
     */
    @Test
    public void testGetBooleanString() {
        assertEquals(_boolean, _delegating.getBoolean("boolean"));

        boolean newBoolean = !_boolean;
        _delegating.setBoolean("boolean", newBoolean);
        assertEquals(newBoolean, _data.getBoolean("boolean"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getByte(int)}.
     */
    @Test
    public void testGetByteInt() {
        assertEquals(_byte, _delegating.getByte(getIndex("byte")));

        byte newByte = (byte)17;
        _delegating.setByte(getIndex("byte"), newByte);
        assertEquals(newByte, _data.getByte("byte"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getByte(commonj.sdo.Property)}.
     */
    @Test
    public void testGetByteProperty() {
        assertEquals(_byte, _delegating.getByte(getProperty("byte")));

        byte newByte = (byte)18;
        _delegating.setByte(getProperty("byte"), newByte);
        assertEquals(newByte, _data.getByte("byte"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getByte(java.lang.String)}.
     */
    @Test
    public void testGetByteString() {
        assertEquals(_byte, _delegating.getByte("byte"));

        byte newByte = (byte)19;
        _delegating.setByte("byte", newByte);
        assertEquals(newByte, _data.getByte("byte"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBytes(int)}.
     */
    @Test
    public void testGetBytesInt() {
        assertEquals(_bytes, _delegating.getBytes(getIndex("bytes")));

        byte[] newBytes = new byte[]{20};
        _delegating.setBytes(getIndex("bytes"), newBytes);
        assertEquals(newBytes, _data.getBytes("bytes"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBytes(commonj.sdo.Property)}.
     */
    @Test
    public void testGetBytesProperty() {
        assertEquals(_bytes, _delegating.getBytes(getProperty("bytes")));

        byte[] newBytes = new byte[]{21};
        _delegating.setBytes(getProperty("bytes"), newBytes);
        assertEquals(newBytes, _data.getBytes("bytes"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getBytes(java.lang.String)}.
     */
    @Test
    public void testGetBytesString() {
        assertEquals(_bytes, _delegating.getBytes("bytes"));

        byte[] newBytes = new byte[]{22};
        _delegating.setBytes("bytes", newBytes);
        assertEquals(newBytes, _data.getBytes("bytes"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getChangeSummary()}.
     */
    @Test
    public void testGetChangeSummary() {
        assertEquals(_data.getChangeSummary(), _delegating.getChangeSummary());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getChar(int)}.
     */
    @Test
    public void testGetCharInt() {
        assertEquals(_char, _delegating.getChar(getIndex("char")));

        char newChar = 'n';
        _delegating.setChar(getIndex("char"), newChar);
        assertEquals(newChar, _data.getChar("char"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getChar(commonj.sdo.Property)}.
     */
    @Test
    public void testGetCharProperty() {
        assertEquals(_char, _delegating.getChar(getProperty("char")));

        char newChar = 'o';
        _delegating.setChar(getProperty("char"), newChar);
        assertEquals(newChar, _data.getChar("char"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getChar(java.lang.String)}.
     */
    @Test
    public void testGetCharString() {
        assertEquals(_char, _delegating.getChar("char"));

        char newChar = 'p';
        _delegating.setChar("char", newChar);
        assertEquals(newChar, _data.getChar("char"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getContainer()}.
     */
    @Test
    public void testGetContainer() {
        assertEquals(_data.getContainer(), _delegating.getContainer());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getContainmentProperty()}.
     */
    @Test
    public void testGetContainmentProperty() {
        assertEquals(
            _data.getContainmentProperty(),
            _delegating.getContainmentProperty());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDataGraph()}.
     */
    @Test
    public void testGetDataGraph() {
        assertEquals(_data.getDataGraph(), _delegating.getDataGraph());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDataObject(int)}.
     */
    @Test
    public void testGetDataObjectInt() {
        assertEquals(_dataObject, _delegating.getDataObject(getIndex("dataObject")));

        DataObject newDataObject = _helperContext.getDataFactory().create(OpenType.getInstance());
        newDataObject.setString("prop", "b");
        _delegating.setDataObject(getIndex("dataObject"), newDataObject);
        assertEquals(newDataObject, _data.getDataObject("dataObject"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDataObject(commonj.sdo.Property)}.
     */
    @Test
    public void testGetDataObjectProperty() {
        assertEquals(_dataObject, _delegating.getDataObject(getProperty("dataObject")));

        DataObject newDataObject = _helperContext.getDataFactory().create(OpenType.getInstance());
        newDataObject.setString("prop", "c");
        _delegating.setDataObject(getProperty("dataObject"), newDataObject);
        assertEquals(newDataObject, _data.getDataObject("dataObject"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDataObject(java.lang.String)}.
     */
    @Test
    public void testGetDataObjectString() {
        assertEquals(_dataObject, _delegating.getDataObject("dataObject"));

        DataObject newDataObject = _helperContext.getDataFactory().create(OpenType.getInstance());
        newDataObject.setString("prop", "d");
        _delegating.setDataObject("dataObject", newDataObject);
        assertEquals(newDataObject, _data.getDataObject("dataObject"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDate(int)}.
     */
    @Test
    public void testGetDateInt() {
        assertEquals(_date, _delegating.getDate(getIndex("date")));

        Date newDate = new Date();
        _delegating.setDate(getIndex("date"), newDate);
        assertEquals(newDate, _data.getDate("date"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDate(commonj.sdo.Property)}.
     */
    @Test
    public void testGetDateProperty() {
        assertEquals(_date, _delegating.getDate(getProperty("date")));

        Date newDate = new Date();
        _delegating.setDate(getProperty("date"), newDate);
        assertEquals(newDate, _data.getDate("date"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDate(java.lang.String)}.
     */
    @Test
    public void testGetDateString() {
        assertEquals(_date, _delegating.getDate("date"));

        Date newDate = new Date();
        _delegating.setDate("date", newDate);
        assertEquals(newDate, _data.getDate("date"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDouble(int)}.
     */
    @Test
    public void testGetDoubleInt() {
        assertEquals(_double, _delegating.getDouble(getIndex("double")));

        double newDouble = 23d;
        _delegating.setDouble(getIndex("double"), newDouble);
        assertEquals(newDouble, _data.getDouble("double"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDouble(commonj.sdo.Property)}.
     */
    @Test
    public void testGetDoubleProperty() {
        assertEquals(_double, _delegating.getDouble(getProperty("double")));

        double newDouble = 24d;
        _delegating.setDouble(getProperty("double"), newDouble);
        assertEquals(newDouble, _data.getDouble("double"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getDouble(java.lang.String)}.
     */
    @Test
    public void testGetDoubleString() {
        assertEquals(_double, _delegating.getDouble("double"));

        double newDouble = 25d;
        _delegating.setDouble("double", newDouble);
        assertEquals(newDouble, _data.getDouble("double"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getFloat(int)}.
     */
    @Test
    public void testGetFloatInt() {
        assertEquals(_float, _delegating.getFloat(getIndex("float")));

        float newFloat = 26f;
        _delegating.setFloat(getIndex("float"), newFloat);
        assertEquals(newFloat, _data.getFloat("float"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getFloat(commonj.sdo.Property)}.
     */
    @Test
    public void testGetFloatProperty() {
        assertEquals(_float, _delegating.getFloat(getProperty("float")));

        float newFloat = 27f;
        _delegating.setFloat(getProperty("float"), newFloat);
        assertEquals(newFloat, _data.getFloat("float"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getFloat(java.lang.String)}.
     */
    @Test
    public void testGetFloatString() {
        assertEquals(_float, _delegating.getFloat("float"));

        float newFloat = 28f;
        _delegating.setFloat("float", newFloat);
        assertEquals(newFloat, _data.getFloat("float"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getInstanceProperties()}.
     */
    @Test
    public void testGetInstanceProperties() {
        assertEquals(_data.getInstanceProperties(), _delegating.getInstanceProperties());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getInstanceProperty(java.lang.String)}.
     */
    @Test
    public void testGetInstanceProperty() {
        assertEquals(
            _data.getInstanceProperty("string"),
            _delegating.getInstanceProperty("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getInt(int)}.
     */
    @Test
    public void testGetIntInt() {
        assertEquals(_int, _delegating.getInt(getIndex("int")));

        int newInt = 29;
        _delegating.setInt(getIndex("int"), newInt);
        assertEquals(newInt, _data.getInt("int"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getInt(commonj.sdo.Property)}.
     */
    @Test
    public void testGetIntProperty() {
        assertEquals(_int, _delegating.getInt(getProperty("int")));

        int newInt = 30;
        _delegating.setInt(getProperty("int"), newInt);
        assertEquals(newInt, _data.getInt("int"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getInt(java.lang.String)}.
     */
    @Test
    public void testGetIntString() {
        assertEquals(_int, _delegating.getInt("int"));

        int newInt = 31;
        _delegating.setInt("int", newInt);
        assertEquals(newInt, _data.getInt("int"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getList(int)}.
     */
    @Test
    public void testGetListInt() {
        assertEquals(_list, _delegating.getList(getIndex("list")));

        List<String> newList = Arrays.asList(new String[]{"32"});
        _delegating.setList(getIndex("list"), newList);
        assertEquals(newList, _data.getList("list"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getList(commonj.sdo.Property)}.
     */
    @Test
    public void testGetListProperty() {
        assertEquals(_list, _delegating.getList(getProperty("list")));

        List<String> newList = Arrays.asList(new String[]{"33"});
        _delegating.setList(getProperty("list"), newList);
        assertEquals(newList, _data.getList("list"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getList(java.lang.String)}.
     */
    @Test
    public void testGetListString() {
        assertEquals(_list, _delegating.getList("list"));

        List<String> newList = Arrays.asList(new String[]{"34"});
        _delegating.setList("list", newList);
        assertEquals(newList, _data.getList("list"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getLong(int)}.
     */
    @Test
    public void testGetLongInt() {
        assertEquals(_long, _delegating.getLong(getIndex("long")));

        long newLong = 35l;
        _delegating.setLong(getIndex("long"), newLong);
        assertEquals(newLong, _data.getLong("long"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getLong(commonj.sdo.Property)}.
     */
    @Test
    public void testGetLongProperty() {
        assertEquals(_long, _delegating.getLong(getProperty("long")));

        long newLong = 36l;
        _delegating.setLong(getProperty("long"), newLong);
        assertEquals(newLong, _data.getLong("long"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getLong(java.lang.String)}.
     */
    @Test
    public void testGetLongString() {
        assertEquals(_long, _delegating.getLong("long"));

        long newLong = 37l;
        _delegating.setLong("long", newLong);
        assertEquals(newLong, _data.getLong("long"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getProperty(java.lang.String)}.
     */
    @Test
    public void testGetProperty1() {
        assertEquals(
            _data.getProperty("string"),
            _delegating.getProperty("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getSequence()}.
     */
    @Test
    public void testGetSequence() {
        assertEquals(_data.getSequence(), _delegating.getSequence());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getSequence(int)}.
     */
    @Test
    public void testGetSequenceInt() {
        assertEquals(
            _data.getSequence(getIndex("dataObject")),
            _delegating.getSequence(getIndex("dataObject")));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getSequence(commonj.sdo.Property)}.
     */
    @Test
    public void testGetSequenceProperty() {
        assertEquals(
            _data.getSequence(getProperty("dataObject")),
            _delegating.getSequence(getProperty("dataObject")));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getSequence(java.lang.String)}.
     */
    @Test
    public void testGetSequenceString() {
        assertEquals(
            _data.getSequence("dataObject"),
            _delegating.getSequence("dataObject"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getShort(int)}.
     */
    @Test
    public void testGetShortInt() {
        assertEquals(_short, _delegating.getShort(getIndex("short")));

        short newShort = (short)38;
        _delegating.setShort(getIndex("short"), newShort);
        assertEquals(newShort, _data.getShort("short"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getShort(commonj.sdo.Property)}.
     */
    @Test
    public void testGetShortProperty() {
        assertEquals(_short, _delegating.getShort(getProperty("short")));

        short newShort = (short)39;
        _delegating.setShort(getProperty("short"), newShort);
        assertEquals(newShort, _data.getShort("short"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getShort(java.lang.String)}.
     */
    @Test
    public void testGetShortString() {
        assertEquals(_short, _delegating.getShort("short"));

        short newShort = (short)40;
        _delegating.setShort("short", newShort);
        assertEquals(newShort, _data.getShort("short"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getString(int)}.
     */
    @Test
    public void testGetStringInt() {
        assertEquals(_string, _delegating.getString(getIndex("string")));

        String newString = "newString";
        _delegating.setString(getIndex("string"), newString);
        assertEquals(newString, _data.getString("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getString(commonj.sdo.Property)}.
     */
    @Test
    public void testGetStringProperty() {
        assertEquals(_string, _delegating.getString(getProperty("string")));

        String newString = "newString1";
        _delegating.setString(getProperty("string"), newString);
        assertEquals(newString, _data.getString("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getString(java.lang.String)}.
     */
    @Test
    public void testGetStringString() {
        assertEquals(_string, _delegating.getString("string"));

        String newString = "newString2";
        _delegating.setString("string", newString);
        assertEquals(newString, _data.getString("string"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#getType()}.
     */
    @Test
    public void testGetType() {
        assertEquals(_data.getType(), _delegating.getType());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#unset(int)}.
     */
    @Test
    public void testUnsetInt() {
        assertEquals(true, _delegating.isSet(getIndex("string")));
        _delegating.unset(getIndex("string"));
        assertEquals(false, _delegating.isSet(getIndex("string")));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#unset(commonj.sdo.Property)}.
     */
    @Test
    public void testUnsetProperty() {
        assertEquals(true, _delegating.isSet(getProperty("string")));
        _delegating.unset(getProperty("string"));
        assertEquals(false, _delegating.isSet(getProperty("string")));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.builtin.DelegatingDataObject#unset(java.lang.String)}.
     */
    @Test
    public void testUnsetString() {
        assertEquals(true, _delegating.isSet("string"));
        _delegating.unset("string");
        assertEquals(false, _delegating.isSet("string"));
    }

    /**
     * @return
     */
    private int getIndex(String pProperty) {
        return ((SdoProperty)getProperty(pProperty)).getIndex();
    }

    /**
     * @param pProperty
     * @return
     */
    private Property getProperty(String pProperty) {
        return _delegating.getInstanceProperty(pProperty);
    }
}
