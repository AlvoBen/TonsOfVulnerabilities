package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataFactoryImpl;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleTypesIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;

public class WrapperConvertTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public WrapperConvertTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testConvertWrapper2Simple() throws Exception {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE+"anyTypeExample.xsd");
        _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE+"AnyTypeCornerCase.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(),xmlUrl.toString(),null);
        assertNotNull(doc);
        assertNotNull(doc.getRootObject());
        assertEquals(1, doc.getRootObject().getType().getProperties().size());
        DataObject container = doc.getRootObject();
        List objs = container.getList("any");
        assertEquals(15, objs.size());

        assertEquals(5, container.getInt("any.0"));
        assertEquals("5", container.getString("any.0"));

        assertEquals("hello", container.getString("any.1"));

        assertEquals("", container.getString("any.2"));

        assertEquals(true, container.getBoolean("any.3"));
        assertEquals("true", container.getString("any.3"));

        assertEquals(new BigDecimal("3.14"), container.getBigDecimal("any.4"));
        assertEquals("3.14", container.getString("any.4"));

        assertEquals(5d, container.getDouble("any.5"));
        assertEquals("5.0", container.getString("any.5"));

        assertEquals(3.14f, container.getFloat("any.6"));
        assertEquals("3.14", container.getString("any.6"));

        assertEquals(new BigInteger("5"), container.getBigInteger("any.7"));
        assertEquals("5", container.getString("any.7"));

        assertEquals((byte)5, container.getByte("any.8"));
        assertEquals("5", container.getString("any.8"));

        assertEquals(5L, container.getLong("any.9"));
        assertEquals("5", container.getString("any.9"));

        assertEquals((short)5, container.getShort("any.10"));
        assertEquals("5", container.getString("any.10"));

        assertEquals("2007-03-08T09:17:00", container.getString("any.11"));

        byte[] bytes = container.getBytes("any.12");
        byte[] expected = new byte[]{0x5a};
        assertEquals(expected.length, bytes.length);
        for (int i=0; i<bytes.length; ++i) {
            assertEquals(expected[i], bytes[i]);
        }
        assertEquals("5A", container.getString("any.12"));

        bytes = container.getBytes("any.13");
        final String utf8 = new String(bytes, "UTF-8");
        String str = "Hätten Hüte ein ß im Namen, wären sie möglicherweise keine Hüte mehr,\r\n"
            + "sondern Hüße.\r\n";
        assertEquals(str, utf8);
        final String string = "48C3A47474656E2048C3BC74652065696E20C39F20696D204E616D656E2C2077C3A472656E20736965206DC3B6676C69636865727765697365206B65696E652048C3BC7465206D6568722C0D0A736F6E6465726E2048C3BCC39F652E0D0A";
        assertEquals(string, container.getString("any.13"));

        assertEquals("commonj.sdo#String", container.getString("any.14"));
    }

    @Test
    public void testSetSimple2Wrapper() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE+"anyTypeExample.xsd");
        _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        DataObject anyTypeContainer = _helperContext.getDataFactory().create("any.xsd", "AnyTypeContainer");
        List anyList = anyTypeContainer.getList("any");

        anyList.add("aString");
        DataObject aStringDO = (DataObject)anyList.get(0);
        assertEquals("aString", aStringDO.getString("value"));
        assertEquals(typeHelper.getType(String.class), aStringDO.getInstanceProperty("value").getType());

        anyList.add(5);
        DataObject intDO = (DataObject)anyList.get(1);
        assertEquals(5, intDO.getInt("value"));
        assertEquals(typeHelper.getType(Integer.class), intDO.getInstanceProperty("value").getType());
    }

    @Test
    public void testConvertSimple2Wrapper() {
        DataObject simpleTypesDO = _helperContext.getDataFactory().create(SimpleTypesIntf.class);
        SimpleTypesIntf simpleTypes = (SimpleTypesIntf)simpleTypesDO;
        simpleTypes.setBooleanObject(Boolean.TRUE);
        simpleTypes.setBoolean(true);
        simpleTypes.setByte((byte)0x7F);
        final byte[] bytes = new byte[] {0x7F, 0x6A};
        simpleTypes.setBytes(bytes);
        simpleTypes.setCharacter('A');
        simpleTypes.setCharacterObject(Character.valueOf('B'));
        Date date = new Date();
        simpleTypes.setDate(date);
        simpleTypes.setDay("---15");
        simpleTypes.setDecimal(new BigDecimal("15.1"));
        simpleTypes.setDoubleObject(Double.valueOf(15.2));
        simpleTypes.setDouble(15.3);
        simpleTypes.setDuration("P10M");
        simpleTypes.setFloatObject(Float.valueOf(15.4f));
        simpleTypes.setFloat(15.5f);
        simpleTypes.setIntObject(Integer.valueOf(16));
        simpleTypes.setInt(17);
        simpleTypes.setInteger(BigInteger.valueOf(18));
        simpleTypes.setLongObject(Long.valueOf(19));
        simpleTypes.setLong(20);
        simpleTypes.setMonthDay("--09-11");
        simpleTypes.setMonth("--09");
        final Object object = new BigDecimal("20.1");
        simpleTypes.setObject(object);
        simpleTypes.setShortObject(Short.valueOf((short)21));
        simpleTypes.setShort((short)22);
        simpleTypes.setString("hallo");
        simpleTypes.setStringMany(Arrays.asList(new String[] {"23", "24", "25"}));
        simpleTypes.setStringsAttr(Arrays.asList(new String[] {"26", "27", "28"}));
        simpleTypes.setStrings(Arrays.asList(new String[] {"29", "30", "31"}));
        simpleTypes.setTime("20:15:00");
        simpleTypes.setURI("http://www.sap.com/test");
        simpleTypes.setYear("2007");
        simpleTypes.setYearMonth("2007-09");
        simpleTypes.setYearMonthDay("2007-09-11");

        DataObject booleanObjectDO = simpleTypesDO.getDataObject("booleanObject");
        assertEquals(true, booleanObjectDO.getBoolean("value"));

        DataObject booleanDO = simpleTypesDO.getDataObject("boolean");
        assertEquals(true, booleanDO.getBoolean("value"));

        DataObject byteDO = simpleTypesDO.getDataObject("byte");
        assertEquals((byte)0x7F, byteDO.getByte("value"));

        DataObject bytesDO = simpleTypesDO.getDataObject("bytes");
        assertEquals(bytes, bytesDO.getBytes("value"));

        DataObject characterDO = simpleTypesDO.getDataObject("character");
        assertEquals('A', characterDO.getChar("value"));

        DataObject characterObjectDO = simpleTypesDO.getDataObject("characterObject");
        assertEquals('B', characterObjectDO.getChar("value"));

        DataObject dateDO = simpleTypesDO.getDataObject("date");
        assertEquals(date, dateDO.getDate("value"));

        DataObject dayDO = simpleTypesDO.getDataObject("day");
        assertEquals("---15", dayDO.getString("value"));

        DataObject decimalDO = simpleTypesDO.getDataObject("decimal");
        assertEquals(new BigDecimal("15.1"), decimalDO.getBigDecimal("value"));

        DataObject doubleObjectDO = simpleTypesDO.getDataObject("doubleObject");
        assertEquals(15.2, doubleObjectDO.getDouble("value"));

        DataObject doubleDO = simpleTypesDO.getDataObject("double");
        assertEquals(15.3, doubleDO.getDouble("value"));

        DataObject durationDO = simpleTypesDO.getDataObject("duration");
        assertEquals("P10M", durationDO.getString("value"));

        DataObject floatObjectDO = simpleTypesDO.getDataObject("floatObject");
        assertEquals(15.4f, floatObjectDO.getFloat("value"));

        DataObject floatDO = simpleTypesDO.getDataObject("float");
        assertEquals(15.5f, floatDO.getFloat("value"));

        DataObject intObjectDO = simpleTypesDO.getDataObject("intObject");
        assertEquals(16, intObjectDO.getInt("value"));

        DataObject intDO = simpleTypesDO.getDataObject("int");
        assertEquals(17, intDO.getInt("value"));

        DataObject integerDO = simpleTypesDO.getDataObject("integer");
        assertEquals(BigInteger.valueOf(18), integerDO.getBigInteger("value"));

        DataObject longObjectDO = simpleTypesDO.getDataObject("longObject");
        assertEquals(19l, longObjectDO.getLong("value"));

        DataObject longDO = simpleTypesDO.getDataObject("long");
        assertEquals(20l, longDO.getLong("value"));

        DataObject monthDayDO = simpleTypesDO.getDataObject("monthDay");
        assertEquals("--09-11", monthDayDO.getString("value"));

        DataObject monthDO = simpleTypesDO.getDataObject("month");
        assertEquals("--09", monthDO.getString("value"));

        DataObject objectDO = simpleTypesDO.getDataObject("object");
        assertEquals(object, objectDO.get("value"));

        DataObject shortObjectDO = simpleTypesDO.getDataObject("shortObject");
        assertEquals((short)21, shortObjectDO.getShort("value"));

        DataObject shortDO = simpleTypesDO.getDataObject("short");
        assertEquals((short)22, shortDO.getShort("value"));

        DataObject stringDO = simpleTypesDO.getDataObject("string");
        assertEquals("hallo", stringDO.getString("value"));

        DataObject stringManyDO = simpleTypesDO.getDataObject("stringMany.1");
        assertEquals("24", stringManyDO.getString("value"));

        DataObject stringsAttrDO = simpleTypesDO.getDataObject("stringsAttr");
        assertEquals("26 27 28", stringsAttrDO.getString("value"));

        DataObject stringsDO = simpleTypesDO.getDataObject("strings");
        assertEquals(Arrays.asList(new String[] {"29", "30", "31"}), stringsDO.getList("value"));

        DataObject timeDO = simpleTypesDO.getDataObject("time");
        assertTrue(timeDO.getString("value"), timeDO.getString("value").startsWith("20:15:00"));

        DataObject uriDO = simpleTypesDO.getDataObject("uRI");
        assertEquals("http://www.sap.com/test", uriDO.getString("value"));

        DataObject yearDO = simpleTypesDO.getDataObject("year");
        assertEquals("2007", yearDO.getString("value"));

        DataObject yearMonthDO = simpleTypesDO.getDataObject("yearMonth");
        assertEquals("2007-09", yearMonthDO.getString("value"));

        DataObject yearMonthDayDO = simpleTypesDO.getDataObject("yearMonthDay");
        assertEquals("2007-09-11", yearMonthDayDO.getString("value"));
    }

    @Test
    public void testSetWrapper2Simple() {
        DataObject simpleTypesDO = _helperContext.getDataFactory().create(SimpleTypesIntf.class);

        setWrapperProperty(simpleTypesDO, "booleanObject", Boolean.TRUE);
        setWrapperProperty(simpleTypesDO, "boolean", true);
        setWrapperProperty(simpleTypesDO, "byte", (byte)0x7F);
        setWrapperProperty(simpleTypesDO, "bytes", new byte[] {0x7F, 0x6A});
        setWrapperProperty(simpleTypesDO, "character", 'A');
        setWrapperProperty(simpleTypesDO, "characterObject", Character.valueOf('B'));
        setWrapperProperty(simpleTypesDO, "date", new Date());
        setWrapperProperty(simpleTypesDO, "day", "---15");
        setWrapperProperty(simpleTypesDO, "decimal", new BigDecimal("15.1"));
        setWrapperProperty(simpleTypesDO, "doubleObject", Double.valueOf(15.2));
        setWrapperProperty(simpleTypesDO, "double", 15.3);
        setWrapperProperty(simpleTypesDO, "duration", "P10M");
        setWrapperProperty(simpleTypesDO, "floatObject", Float.valueOf(15.4f));
        setWrapperProperty(simpleTypesDO, "float", 15.5f);
        setWrapperProperty(simpleTypesDO, "intObject", Integer.valueOf(16));
        setWrapperProperty(simpleTypesDO, "int", 17);
        setWrapperProperty(simpleTypesDO, "integer", BigInteger.valueOf(18));
        setWrapperProperty(simpleTypesDO, "longObject", Long.valueOf(19l));
        setWrapperProperty(simpleTypesDO, "long", 20l);
        setWrapperProperty(simpleTypesDO, "monthDay", "--09-11");
        setWrapperProperty(simpleTypesDO, "month", "--09");
        setWrapperProperty(simpleTypesDO, "object", new BigDecimal("20.1"));
        setWrapperProperty(simpleTypesDO, "shortObject", Short.valueOf((short)21));
        setWrapperProperty(simpleTypesDO, "short", (short)22);
        setWrapperProperty(simpleTypesDO, "string", "hallo");
        setWrapperProperty(simpleTypesDO, "stringsAttr", Arrays.asList(new String[] {"26", "27", "28"}));
        setWrapperProperty(simpleTypesDO, "strings", Arrays.asList(new String[] {"29", "30", "31"}));
        setWrapperProperty(simpleTypesDO, "time", "20:15:00.000Z");
        setWrapperProperty(simpleTypesDO, "uRI", "http://www.sap.com/test");
        setWrapperProperty(simpleTypesDO, "year", "2007");
        setWrapperProperty(simpleTypesDO, "yearMonth", "2007-09");
        setWrapperProperty(simpleTypesDO, "yearMonthDay", "2007-09-11");
    }

    private void setWrapperProperty(DataObject simpleTypesDO, String propertyName, Object value) {
        DataFactoryImpl dataFactory = (DataFactoryImpl)_helperContext.getDataFactory();
        final Type type = simpleTypesDO.getInstanceProperty(propertyName).getType();
        DataObject booleanObjectDO = dataFactory.createWrapper(type, value);
        simpleTypesDO.setDataObject(propertyName, booleanObjectDO);
        assertEquals(value, simpleTypesDO.get(propertyName));
    }

    @Test
    public void testDataFactoryCreate() {
        DataFactory dataFactory = _helperContext.getDataFactory();

        Type stringType = _helperContext.getTypeHelper().getType(String.class);
        DataObject do1 = dataFactory.create(stringType);

        assertEquals(stringType, do1.getInstanceProperty("value").getType());
        assertEquals(null, do1.get("value"));
        do1.setInt("value", 5);
        assertEquals("5", do1.get("value"));

        DataObject do2 = dataFactory.create("commonj.sdo", "Int");
        assertEquals(_helperContext.getTypeHelper().getType("commonj.sdo", "Int"), do2.getInstanceProperty("value").getType());
        assertEquals(0, do2.get("value"));
        do2.setString("value", "6");
        assertEquals(6, do2.get("value"));

        DataObject do3 = dataFactory.create(long.class);
        assertEquals(_helperContext.getTypeHelper().getType("commonj.sdo", "Long"), do3.getInstanceProperty("value").getType());
        assertEquals(0L, do3.get("value"));
        do3.setLong("value", 7L);
        assertEquals(7L, do3.get("value"));
    }

}
