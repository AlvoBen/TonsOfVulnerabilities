package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.EmptyOpenInterface;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class DataObjectAccessorExceptionsTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DataObjectAccessorExceptionsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject _dataObject;

    @Before
    public void setUp() throws Exception {
        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "ConversionsType");
        typeObject.set(TypeType.URI, "com.sap.sdo");

        for (Conversions conversions : Conversions.values()) {
            DataObject prop = typeObject.createDataObject("property");
            prop.set(PropertyType.NAME, "prop" + conversions.name());
            prop.set(
                PropertyType.TYPE,
                _helperContext.getTypeHelper().getType("commonj.sdo", conversions.name()));
            prop.setBoolean(PropertyType.NULLABLE, true);
        }
        Type type = _helperContext.getTypeHelper().define(typeObject);
        _dataObject = _helperContext.getDataFactory().create(type);

        _dataObject.setBoolean("prop" + Conversions.Boolean.name(), true);
        _dataObject.setByte("prop" + Conversions.Byte.name(), (byte)42);
        _dataObject.setBytes("prop" + Conversions.Bytes.name(), new byte[]{0,1,2});
        _dataObject.setChar("prop" + Conversions.Character.name(), 'a');
        _dataObject.setDate("prop" + Conversions.Date.name(), new Date());
        _dataObject.setString("prop" + Conversions.DateTime.name(), "2006-11-13T13:44:42");
        _dataObject.setString("prop" + Conversions.Day.name(), "---13");
        _dataObject.setBigDecimal("prop" + Conversions.Decimal.name(), new BigDecimal(8.15));
        _dataObject.setDouble("prop" + Conversions.Double.name(), 8.15);
        _dataObject.setString("prop" + Conversions.Duration.name(), "P13D");
        _dataObject.setFloat("prop" + Conversions.Float.name(), 8.15f);
        _dataObject.setInt("prop" + Conversions.Int.name(), 42);
        _dataObject.setBigInteger("prop" + Conversions.Integer.name(), new BigInteger("42"));
        _dataObject.setLong("prop" + Conversions.Long.name(), 42L);
        _dataObject.setString("prop" + Conversions.Month.name(), "--11");
        _dataObject.setString("prop" + Conversions.MonthDay.name(), "--11-13");
        _dataObject.setShort("prop" + Conversions.Short.name(), (short)2);
        _dataObject.setString("prop" + Conversions.String.name(), "42");
        _dataObject.setList("prop" + Conversions.Strings.name(), Arrays.asList(new String[]{"first","second"}));
        _dataObject.setString("prop" + Conversions.Time.name(), "13:44:42");
        _dataObject.setString("prop" + Conversions.Year.name(), "2006");
        _dataObject.setString("prop" + Conversions.YearMonth.name(), "2006-11");
        _dataObject.setString("prop" + Conversions.YearMonthDay.name(), "2006-11-13");
    }

    @After
    public void tearDown() throws Exception {
        _dataObject = null;
    }

    @Test
    public void testClassCastException() {
        DataObject dataObject = _helperContext.getDataFactory().create(EmptyOpenInterface.class);

        dataObject.set("intProp", 4);

        assertEquals(4, dataObject.get("intProp"));

        try {
            boolean boolValue = dataObject.getBoolean("intProp");
            fail("ClassCastException expected");
        } catch (ClassCastException cce) { //$JL-EXC$
            // expected
        }

        try {
            boolean boolValue = dataObject.getBoolean(0);
            fail("ClassCastException expected");
        } catch (ClassCastException cce) { //$JL-EXC$
            // expected
        }

        Property property = dataObject.getInstanceProperty("intProp");

        try {
            boolean boolValue = dataObject.getBoolean(property);
            fail("ClassCastException expected");
        } catch (ClassCastException cce) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testNullConversion() {
        assertEquals(true, _dataObject.getInstanceProperty("prop"+Conversions.String.name()).isNullable());
        _dataObject.setString("prop"+Conversions.String.name(), null);
        assertEquals((byte)0, _dataObject.getByte("prop"+Conversions.String.name()));
        assertEquals((char)0, _dataObject.getChar("prop"+Conversions.String.name()));
        assertEquals(0D, _dataObject.getDouble("prop"+Conversions.String.name()));
        assertEquals(0F, _dataObject.getFloat("prop"+Conversions.String.name()));
        assertEquals(0, _dataObject.getInt("prop"+Conversions.String.name()));
        assertEquals(0L, _dataObject.getLong("prop"+Conversions.String.name()));
        assertEquals((short)0, _dataObject.getShort("prop"+Conversions.String.name()));

        assertFalse(_dataObject.getBoolean("prop"+Conversions.String.name()));
    }

    @Test
    public void testDataHelperNullConversion() {
        assertEquals((byte)0, _helperContext.getDataHelper().convert(JavaSimpleType.BYTE, null));
        assertEquals((char)0, _helperContext.getDataHelper().convert(JavaSimpleType.CHARACTER, null));
        assertEquals(0D, _helperContext.getDataHelper().convert(JavaSimpleType.DOUBLE, null));
        assertEquals(0F, _helperContext.getDataHelper().convert(JavaSimpleType.FLOAT, null));
        assertEquals(0, _helperContext.getDataHelper().convert(JavaSimpleType.INT, null));
        assertEquals(0L, _helperContext.getDataHelper().convert(JavaSimpleType.LONG, null));
        assertEquals((short)0, _helperContext.getDataHelper().convert(JavaSimpleType.SHORT, null));

        assertFalse((Boolean)_helperContext.getDataHelper().convert(JavaSimpleType.BOOLEAN, null));
    }

    @Test
    public void testBooleanConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getBoolean(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Boolean",
                    conversion.possibleConverstions()[0] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Boolean", ex);
            }
            try {
                _dataObject.getBoolean("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Boolean",
                    conversion.possibleConverstions()[0] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Boolean", ex);
            }
            try {
                _dataObject.getBoolean(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Boolean",
                    conversion.possibleConverstions()[0] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Boolean", ex);
            }

            try {
                _dataObject.setBoolean(conversion.ordinal(), true);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Boolean to " + conversion.name(),
                    conversion.possibleConverstions()[0] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Boolean to " + conversion.name(), ex);
            }
            try {
                _dataObject.setBoolean("prop"+conversion.name(), true);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Boolean to " + conversion.name(),
                    conversion.possibleConverstions()[0] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Boolean to " + conversion.name(), ex);
            }
            try {
                _dataObject.setBoolean(_dataObject.getInstanceProperty("prop"+conversion.name()), true);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Boolean to " + conversion.name(),
                    conversion.possibleConverstions()[0] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Boolean to " + conversion.name(), ex);
            }
        }
    }

    @Test
    public void testByteConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getByte(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Byte",
                    conversion.possibleConverstions()[1] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Byte", ex);
            }
            try {
                _dataObject.getByte("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Byte",
                    conversion.possibleConverstions()[1] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Byte", ex);
            }
            try {
                _dataObject.getByte(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Byte",
                    conversion.possibleConverstions()[1] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Byte", ex);
            }

            try {
                _dataObject.setByte(conversion.ordinal(), (byte)42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Byte to " + conversion.name(),
                    conversion.possibleConverstions()[1] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Byte to " + conversion.name(), ex);
            }
            try {
                _dataObject.setByte("prop"+conversion.name(), (byte)42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Byte to " + conversion.name(),
                    conversion.possibleConverstions()[1] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Byte to " + conversion.name(), ex);
            }
            try {
                _dataObject.setByte(_dataObject.getInstanceProperty("prop"+conversion.name()), (byte)42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Byte to " + conversion.name(),
                    conversion.possibleConverstions()[1] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Byte to " + conversion.name(), ex);
            }
        }
    }

    @Test
    public void testCharacterConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getChar(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Character",
                    conversion.possibleConverstions()[2] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Character", ex);
            }
            try {
                _dataObject.getChar("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Character",
                    conversion.possibleConverstions()[2] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Character", ex);
            }
            try {
                _dataObject.getChar(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Character",
                    conversion.possibleConverstions()[2] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Character", ex);
            }

            try {
                _dataObject.setChar(conversion.ordinal(), 'a');
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Character to " + conversion.name(),
                    conversion.possibleConverstions()[2] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Character to " + conversion.name(), ex);
            }
            try {
                _dataObject.setChar("prop"+conversion.name(), 'a');
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Character to " + conversion.name(),
                    conversion.possibleConverstions()[2] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Character to " + conversion.name(), ex);
            }
            try {
                _dataObject.setChar(_dataObject.getInstanceProperty("prop"+conversion.name()), 'a');
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Character to " + conversion.name(),
                    conversion.possibleConverstions()[2] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Character to " + conversion.name(), ex);
            }
        }
    }

    @Test
    public void testDoubleConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getDouble(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Double",
                    conversion.possibleConverstions()[3] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Double", ex);
            }
            try {
                _dataObject.getDouble("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Double",
                    conversion.possibleConverstions()[3] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Double", ex);
            }
            try {
                _dataObject.getDouble(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Double",
                    conversion.possibleConverstions()[3] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Double", ex);
            }

            try {
                _dataObject.setDouble(conversion.ordinal(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Double to " + conversion.name(),
                    conversion.possibleConverstions()[3] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Double to " + conversion.name(), ex);
            }
            try {
                _dataObject.setDouble("prop"+conversion.name(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Double to " + conversion.name(),
                    conversion.possibleConverstions()[3] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Double to " + conversion.name(), ex);
            }
            try {
                _dataObject.setDouble(_dataObject.getInstanceProperty("prop"+conversion.name()), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Double to " + conversion.name(),
                    conversion.possibleConverstions()[3] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Double to " + conversion.name(), ex);
            }
}
    }

    @Test
    public void testFloatConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getFloat(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Float",
                    conversion.possibleConverstions()[4] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Float", ex);
            }
            try {
                _dataObject.getFloat("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Float",
                    conversion.possibleConverstions()[4] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Float", ex);
            }
            try {
                _dataObject.getFloat(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Float",
                    conversion.possibleConverstions()[4] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Float", ex);
            }

            try {
                _dataObject.setFloat(conversion.ordinal(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Float to " + conversion.name(),
                    conversion.possibleConverstions()[4] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Float to " + conversion.name(), ex);
            }
            try {
                _dataObject.setFloat("prop"+conversion.name(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Float to " + conversion.name(),
                    conversion.possibleConverstions()[4] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Float to " + conversion.name(), ex);
            }
            try {
                _dataObject.setFloat(_dataObject.getInstanceProperty("prop"+conversion.name()), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Float to " + conversion.name(),
                    conversion.possibleConverstions()[4] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Float to " + conversion.name(), ex);
            }
}
    }

    @Test
    public void testIntConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getInt(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Int",
                    conversion.possibleConverstions()[5] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Int", ex);
            }
            try {
                _dataObject.getInt("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Int",
                    conversion.possibleConverstions()[5] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Int", ex);
            }
            try {
                _dataObject.getInt(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Int",
                    conversion.possibleConverstions()[5] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Int", ex);
            }

            try {
                _dataObject.setInt(conversion.ordinal(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Int to " + conversion.name(),
                    conversion.possibleConverstions()[5] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Int to " + conversion.name(), ex);
            }
            try {
                _dataObject.setInt("prop"+conversion.name(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Int to " + conversion.name(),
                    conversion.possibleConverstions()[5] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Int to " + conversion.name(), ex);
            }
            try {
                _dataObject.setInt(_dataObject.getInstanceProperty("prop"+conversion.name()), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Int to " + conversion.name(),
                    conversion.possibleConverstions()[5] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Int to " + conversion.name(), ex);
            }
}
    }

    @Test
    public void testLongConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getLong(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Long",
                    conversion.possibleConverstions()[6] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Long", ex);
            }
            try {
                _dataObject.getLong("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Long",
                    conversion.possibleConverstions()[6] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Long", ex);
            }
            try {
                _dataObject.getLong(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Long",
                    conversion.possibleConverstions()[6] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Long", ex);
            }

            try {
                _dataObject.setLong(conversion.ordinal(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Long to " + conversion.name(),
                    conversion.possibleConverstions()[6] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Long to " + conversion.name(), ex);
            }
            try {
                _dataObject.setLong("prop"+conversion.name(), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Long to " + conversion.name(),
                    conversion.possibleConverstions()[6] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Long to " + conversion.name(), ex);
            }
            try {
                _dataObject.setLong(_dataObject.getInstanceProperty("prop"+conversion.name()), 42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Long to " + conversion.name(),
                    conversion.possibleConverstions()[6] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Long to " + conversion.name(), ex);
            }
}
    }

    @Test
    public void testShortConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getShort(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Short",
                    conversion.possibleConverstions()[7] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Short", ex);
            }
            try {
                _dataObject.getShort("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Short",
                    conversion.possibleConverstions()[7] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Short", ex);
            }
            try {
                _dataObject.getShort(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Short",
                    conversion.possibleConverstions()[7] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Short", ex);
            }

            try {
                _dataObject.setShort(conversion.ordinal(), (short)42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Short to " + conversion.name(),
                    conversion.possibleConverstions()[7] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Short to " + conversion.name(), ex);
            }
            try {
                _dataObject.setShort("prop"+conversion.name(), (short)42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Short to " + conversion.name(),
                    conversion.possibleConverstions()[7] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Short to " + conversion.name(), ex);
            }
            try {
                _dataObject.setShort(_dataObject.getInstanceProperty("prop"+conversion.name()), (short)42);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Short to " + conversion.name(),
                    conversion.possibleConverstions()[7] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Short to " + conversion.name(), ex);
            }
}
    }

    @Test
    public void testStringConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                String value = _dataObject.getString(conversion.ordinal());
                assertNotNull(
                    "Conversion " + conversion.name() + " to String returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to String",
                    conversion.possibleConverstions()[8] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to String", ex);
            }
            try {
                String value = _dataObject.getString("prop"+conversion.name());
                assertNotNull(
                    "Conversion " + conversion.name() + " to String returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to String",
                    conversion.possibleConverstions()[8] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to String", ex);
            }
            try {
                String value = _dataObject.getString(_dataObject.getInstanceProperty("prop"+conversion.name()));
                assertNotNull(
                    "Conversion " + conversion.name() + " to String returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to String",
                    conversion.possibleConverstions()[8] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to String", ex);
            }

            final String value;
            switch (conversion) {
                case Date:
                case DateTime:
                case Day:
                case Month:
                case MonthDay:
                case Time:
                case Year:
                case YearMonth:
                case YearMonthDay:
                    value = "2006-11-13T15:17:32";
                    break;
                case Duration:
                    value = "PT15S";
                    break;
                default:
                    value = "1";
                    break;
            }
            try {
                _dataObject.setString(conversion.ordinal(), value);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert String to " + conversion.name(),
                    conversion.possibleConverstions()[8] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert String to " + conversion.name(), ex);
            }
            try {
                _dataObject.setString("prop"+conversion.name(), value);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert String to " + conversion.name(),
                    conversion.possibleConverstions()[8] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert String to " + conversion.name(), ex);
            }
            try {
                _dataObject.setString(_dataObject.getInstanceProperty("prop"+conversion.name()), value);
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert String to " + conversion.name(),
                    conversion.possibleConverstions()[8] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert String to " + conversion.name(), ex);
            }
}
    }

    @Test
    public void testBytesConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                _dataObject.getBytes(conversion.ordinal());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Bytes",
                    conversion.possibleConverstions()[9] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Bytes", ex);
            }
            try {
                _dataObject.getBytes("prop"+conversion.name());
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Bytes",
                    conversion.possibleConverstions()[9] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Bytes", ex);
            }
            try {
                _dataObject.getBytes(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Bytes",
                    conversion.possibleConverstions()[9] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Bytes", ex);
            }

            try {
                _dataObject.getBytes(conversion.ordinal());
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Bytes",
                    conversion.possibleConverstions()[9] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Bytes", ex);
            }
            try {
                _dataObject.getBytes("prop"+conversion.name());
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Bytes",
                    conversion.possibleConverstions()[9] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Bytes", ex);
            }
            try {
                _dataObject.getBytes(_dataObject.getInstanceProperty("prop"+conversion.name()));
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Bytes",
                    conversion.possibleConverstions()[9] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Bytes", ex);
            }
        }
    }

    @Test
    public void testDecimalConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                BigDecimal value = _dataObject.getBigDecimal(conversion.ordinal());
                assertNotNull(
                    "Conversion " + conversion.name() + " to Decimal returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Decimal",
                    conversion.possibleConverstions()[10] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Decimal", ex);
            }
            try {
                BigDecimal value = _dataObject.getBigDecimal("prop"+conversion.name());
                assertNotNull(
                    "Conversion " + conversion.name() + " to Decimal returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Decimal",
                    conversion.possibleConverstions()[10] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Decimal", ex);
            }
            try {
                BigDecimal value = _dataObject.getBigDecimal(_dataObject.getInstanceProperty("prop"+conversion.name()));
                assertNotNull(
                    "Conversion " + conversion.name() + " to Decimal returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Decimal",
                    conversion.possibleConverstions()[10] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Decimal", ex);
            }

            try {
                _dataObject.setBigDecimal(conversion.ordinal(), new BigDecimal(42));
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Decimal to " + conversion.name(),
                    conversion.possibleConverstions()[10] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Decimal to " + conversion.name(), ex);
            }
            try {
                _dataObject.setBigDecimal("prop"+conversion.name(), new BigDecimal(42));
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Decimal to " + conversion.name(),
                    conversion.possibleConverstions()[10] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Decimal to " + conversion.name(), ex);
            }
            try {
                _dataObject.setBigDecimal(_dataObject.getInstanceProperty("prop"+conversion.name()), new BigDecimal(42));
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Decimal to " + conversion.name(),
                    conversion.possibleConverstions()[10] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Decimal to " + conversion.name(), ex);
            }
        }
    }

    @Test
    public void testIntegerConversions() throws Exception {
        for (Conversions conversion : Conversions.values()) {
            try {
                BigInteger value = _dataObject.getBigInteger(conversion.ordinal());
                assertNotNull(
                    "Conversion " + conversion.name() + " to Integer returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Integer",
                    conversion.possibleConverstions()[11] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Integer", ex);
            }
            try {
                BigInteger value = _dataObject.getBigInteger("prop"+conversion.name());
                assertNotNull(
                    "Conversion " + conversion.name() + " to Integer returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Integer",
                    conversion.possibleConverstions()[11] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Integer", ex);
            }
            try {
                BigInteger value = _dataObject.getBigInteger(_dataObject.getInstanceProperty("prop"+conversion.name()));
                assertNotNull(
                    "Conversion " + conversion.name() + " to Integer returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Integer",
                    conversion.possibleConverstions()[11] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Integer", ex);
            }

            try {
                _dataObject.setBigInteger(conversion.ordinal(), new BigInteger("42"));
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Integer to " + conversion.name(),
                    conversion.possibleConverstions()[11] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Integer to " + conversion.name(), ex);
            }
            try {
                _dataObject.setBigInteger("prop"+conversion.name(), new BigInteger("42"));
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Integer to " + conversion.name(),
                    conversion.possibleConverstions()[11] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Integer to " + conversion.name(), ex);
            }
            try {
                _dataObject.setBigInteger(_dataObject.getInstanceProperty("prop"+conversion.name()), new BigInteger("42"));
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Integer to " + conversion.name(),
                    conversion.possibleConverstions()[11] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Integer to " + conversion.name(), ex);
            }
        }
    }

    @Test
    public void testDateConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date value = _dataObject.getDate(conversion.ordinal());
                assertNotNull(
                    "Conversion " + conversion.name() + " to Date returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Date",
                    conversion.possibleConverstions()[12] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Date", ex);
            }
            try {
                Date value = _dataObject.getDate("prop"+conversion.name());
                assertNotNull(
                    "Conversion " + conversion.name() + " to Date returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Date",
                    conversion.possibleConverstions()[12] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Date", ex);
            }
            try {
                Date value = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                assertNotNull(
                    "Conversion " + conversion.name() + " to Date returned null",
                    value);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Date",
                    conversion.possibleConverstions()[12] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Date", ex);
            }

            try {
                _dataObject.setDate(conversion.ordinal(), new Date());
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Date to " + conversion.name(),
                    conversion.possibleConverstions()[12] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Date to " + conversion.name(), ex);
            }
            try {
                _dataObject.setDate("prop"+conversion.name(), new Date());
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Date to " + conversion.name(),
                    conversion.possibleConverstions()[12] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Date to " + conversion.name(), ex);
            }
            try {
                _dataObject.setDate(_dataObject.getInstanceProperty("prop"+conversion.name()), new Date());
            } catch (RuntimeException ex) {
                assertTrue(
                    "Could not convert Date to " + conversion.name(),
                    conversion.possibleConverstions()[12] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert Date to " + conversion.name(), ex);
            }
        }
    }

    @Test
    public void testDayConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Day returned null",
                    value);
                assertTrue(value, value.startsWith("---"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Day",
                    conversion.possibleConverstions()[13] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Day", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Day returned null",
                    value);
                assertTrue(value, value.startsWith("---"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Day",
                    conversion.possibleConverstions()[13] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Day", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Day returned null",
                    value);
                assertTrue(value, value.startsWith("---"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Day",
                    conversion.possibleConverstions()[13] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Day", ex);
            }
        }
    }

    @Test
    public void testDateTimeConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toDateTime(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to DateTime returned null",
                    value);
                assertTrue(value, value.charAt(10)=='T');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to DateTime",
                    conversion.possibleConverstions()[14] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to DateTime", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toDateTime(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to DateTime returned null",
                    value);
                assertTrue(value, value.charAt(10)=='T');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to DateTime",
                    conversion.possibleConverstions()[14] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to DateTime", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toDateTime(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to DateTime returned null",
                    value);
                assertTrue(value, value.charAt(10)=='T');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to DateTime",
                    conversion.possibleConverstions()[14] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to DateTime", ex);
            }
        }
    }

    @Test
    public void testDurationConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toDuration(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Duration returned null",
                    value);
                assertTrue(value, value.charAt(0)=='P');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Duration",
                    conversion.possibleConverstions()[15] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Duration", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toDuration(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Duration returned null",
                    value);
                assertTrue(value, value.charAt(0)=='P');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Duration",
                    conversion.possibleConverstions()[15] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Duration", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toDuration(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Duration returned null",
                    value);
                assertTrue(value, value.charAt(0)=='P');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Duration",
                    conversion.possibleConverstions()[15] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Duration", ex);
            }
        }
    }

    @Test
    public void testMonthConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toMonth(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Month returned null",
                    value);
                assertTrue(value, value.startsWith("--"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Month",
                    conversion.possibleConverstions()[16] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Month", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toMonth(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Month returned null",
                    value);
                assertTrue(value, value.startsWith("--"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Month",
                    conversion.possibleConverstions()[16] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Month", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toMonth(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Month returned null",
                    value);
                assertTrue(value, value.startsWith("--"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Month",
                    conversion.possibleConverstions()[16] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Month", ex);
            }
        }
    }

    @Test
    public void testMonthDayConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toMonthDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to MonthDay returned null",
                    value);
                assertTrue(value, value.startsWith("--"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to MonthDay",
                    conversion.possibleConverstions()[17] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to MonthDay", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toMonthDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to MonthDay returned null",
                    value);
                assertTrue(value, value.startsWith("--"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to MonthDay",
                    conversion.possibleConverstions()[17] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to MonthDay", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toMonthDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to MonthDay returned null",
                    value);
                assertTrue(value, value.startsWith("--"));
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to MonthDay",
                    conversion.possibleConverstions()[17] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to MonthDay", ex);
            }
        }
    }

    @Test
    public void testTimeConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toTime(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Time returned null",
                    value);
                assertTrue(value, value.charAt(2)==':');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Time",
                    conversion.possibleConverstions()[19] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Time", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toTime(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Time returned null",
                    value);
                assertTrue(value, value.charAt(2)==':');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Time",
                    conversion.possibleConverstions()[19] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Time", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toTime(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Time returned null",
                    value);
                assertTrue(value, value.charAt(2)==':');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Time",
                    conversion.possibleConverstions()[19] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Time", ex);
            }
        }
    }

    @Test
    public void testYearConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toYear(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Year returned null",
                    value);
                assertTrue(value, value.length()==4);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Year",
                    conversion.possibleConverstions()[20] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Year", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toYear(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Year returned null",
                    value);
                assertTrue(value, value.length()==4);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Year",
                    conversion.possibleConverstions()[20] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Year", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toYear(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to Year returned null",
                    value);
                assertTrue(value, value.length()==4);
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to Year",
                    conversion.possibleConverstions()[20] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to Year", ex);
            }
        }
    }

    @Test
    public void testYearMonthConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toYearMonth(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to YearMonth returned null",
                    value);
                assertTrue(value, value.charAt(4)=='-');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to YearMonth",
                    conversion.possibleConverstions()[21] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to YearMonth", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toYearMonth(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to YearMonth returned null",
                    value);
                assertTrue(value, value.charAt(4)=='-');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to YearMonth",
                    conversion.possibleConverstions()[21] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to YearMonth", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toYearMonth(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to YearMonth returned null",
                    value);
                assertTrue(value, value.charAt(4)=='-');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to YearMonth",
                    conversion.possibleConverstions()[21] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to YearMonth", ex);
            }
        }
    }

    @Test
    public void testYearMonthDayConversions() throws Exception {
        _dataObject.setString(Conversions.String.ordinal(), "2006-11-13");
        for (Conversions conversion : Conversions.values()) {
            try {
                Date date = _dataObject.getDate(conversion.ordinal());
                String value = _helperContext.getDataHelper().toYearMonthDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to YearMonthDay returned null",
                    value);
                assertTrue(value, value.charAt(4)=='-');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to YearMonthDay",
                    conversion.possibleConverstions()[22] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to YearMonthDay", ex);
            }
            try {
                Date date = _dataObject.getDate("prop"+conversion.name());
                String value = _helperContext.getDataHelper().toYearMonthDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to YearMonthDay returned null",
                    value);
                assertTrue(value, value.charAt(4)=='-');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to YearMonthDay",
                    conversion.possibleConverstions()[22] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to YearMonthDay", ex);
            }
            try {
                Date date = _dataObject.getDate(_dataObject.getInstanceProperty("prop"+conversion.name()));
                String value = _helperContext.getDataHelper().toYearMonthDay(date);
                assertNotNull(
                    "Conversion " + conversion.name() + " to YearMonthDay returned null",
                    value);
                assertTrue(value, value.charAt(4)=='-');
            } catch (ClassCastException ex) {
                assertTrue(
                    "Could not convert " + conversion.name() + " to YearMonthDay",
                    conversion.possibleConverstions()[22] == 0);
            } catch (Exception ex) {
                throw new Exception("Exception during convert " + conversion.name() + " to YearMonthDay", ex);
            }
        }
    }
}
