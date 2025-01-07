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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.FacetTestType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class FacetsTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public FacetsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    private static final int LENGTH = 3;
    private static final List<String> PATTERN = Collections.singletonList("t[eu]st");
    private static final List<String> ENUMERATION = Arrays.asList(new String[]{"test", "tast"});
    private static final int MAX_LENGTH = 4;
    private static final int MIN_LENGTH = 3;
    private static final int MAX_EXCLUSIVE = 21;
    private static final int MIN_EXCLUSIVE = 3;
    private static final int FRACTION_DIGITS = 2;
    private static final int TOTAL_DIGITS = 3;
    private static final int MAX_INCLUSIVE = 20;
    private static final int MIN_INCLUSIVE = 2;
    private Type _type;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataObject decimalInclusiveTypeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        decimalInclusiveTypeObject.set(TypeType.NAME, "DecimalInclusiveFacetType");
        decimalInclusiveTypeObject.set(TypeType.URI, "com.sap.sdo.testcase");
        decimalInclusiveTypeObject.set(TypeType.DATA_TYPE, true);
        decimalInclusiveTypeObject.set(TypeType.BASE_TYPE, Collections.singletonList(JavaSimpleType.DECIMAL));

        DataObject decimalInclusiveFacets = _helperContext.getDataFactory().create(TypeType.getInstance().getFacetsType());
        decimalInclusiveFacets.set(TypeType.FACET_MININCLUSIVE, MIN_INCLUSIVE);
        decimalInclusiveFacets.set(TypeType.FACET_MAXINCLUSIVE, MAX_INCLUSIVE);
        decimalInclusiveFacets.set(TypeType.FACET_TOTALDIGITS, TOTAL_DIGITS);
        decimalInclusiveTypeObject.set(TypeType.getInstance().getFacetsProperty(), decimalInclusiveFacets);

        String save = _helperContext.getXMLHelper().save(decimalInclusiveTypeObject, "commonj.sdo", "type");
        XMLDocument load = _helperContext.getXMLHelper().load(save);
        assertEquals(save, _helperContext.getXMLHelper().save(load.getRootObject(), "commonj.sdo", "type"));

        DataObject decimalExclusiveTypeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        decimalExclusiveTypeObject.set(TypeType.NAME, "DecimalExclusiveFacetType");
        decimalExclusiveTypeObject.set(TypeType.URI, "com.sap.sdo.testcase");
        decimalExclusiveTypeObject.set(TypeType.DATA_TYPE, true);
        decimalExclusiveTypeObject.set(TypeType.BASE_TYPE, Collections.singletonList(JavaSimpleType.DECIMAL));

        DataObject decimalExclusiveFacets = _helperContext.getDataFactory().create(TypeType.getInstance().getFacetsType());
        decimalExclusiveFacets.set(TypeType.FACET_MINEXCLUSIVE, MIN_EXCLUSIVE);
        decimalExclusiveFacets.set(TypeType.FACET_MAXEXCLUSIVE, MAX_EXCLUSIVE);
        decimalExclusiveFacets.set(TypeType.FACET_FRACTIONDIGITS, FRACTION_DIGITS);
        decimalExclusiveTypeObject.set(TypeType.getInstance().getFacetsProperty(), decimalExclusiveFacets);

        save = _helperContext.getXMLHelper().save(decimalExclusiveTypeObject, "commonj.sdo", "type");
        load = _helperContext.getXMLHelper().load(save);
        assertEquals(save, _helperContext.getXMLHelper().save(load.getRootObject(), "commonj.sdo", "type"));

        DataObject stringTypeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        stringTypeObject.set(TypeType.NAME, "StringFacetType");
        stringTypeObject.set(TypeType.URI, "com.sap.sdo.testcase");
        stringTypeObject.set(TypeType.DATA_TYPE, true);
        stringTypeObject.set(TypeType.BASE_TYPE, Collections.singletonList(JavaSimpleType.STRING));

        DataObject stringFacets = _helperContext.getDataFactory().create(TypeType.getInstance().getFacetsType());
        stringFacets.set(TypeType.FACET_MINLENGTH, MIN_LENGTH);
        stringFacets.set(TypeType.FACET_MAXLENGTH, MAX_LENGTH);
        stringFacets.set(TypeType.FACET_ENUMERATION, ENUMERATION);
        stringFacets.set(TypeType.FACET_PATTERN, PATTERN);
        stringTypeObject.set(TypeType.getInstance().getFacetsProperty(), stringFacets);

        save = _helperContext.getXMLHelper().save(stringTypeObject, "commonj.sdo", "type");
        load = _helperContext.getXMLHelper().load(save);
        assertEquals(save, _helperContext.getXMLHelper().save(load.getRootObject(), "commonj.sdo", "type"));

        DataObject lengthTypeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        lengthTypeObject.set(TypeType.NAME, "LengthFacetType");
        lengthTypeObject.set(TypeType.URI, "com.sap.sdo.testcase");
        lengthTypeObject.set(TypeType.DATA_TYPE, true);
        lengthTypeObject.set(TypeType.BASE_TYPE, Collections.singletonList(JavaSimpleType.STRING));

        DataObject lengthFacets = _helperContext.getDataFactory().create(TypeType.getInstance().getFacetsType());
        lengthFacets.set(TypeType.FACET_LENGTH, LENGTH);
        lengthTypeObject.set(TypeType.getInstance().getFacetsProperty(), lengthFacets);

        save = _helperContext.getXMLHelper().save(lengthTypeObject, "commonj.sdo", "type");
        load = _helperContext.getXMLHelper().load(save);
        assertEquals(save, _helperContext.getXMLHelper().save(load.getRootObject(), "commonj.sdo", "type"));

        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "FacetTestType");
        typeObject.set(TypeType.URI, "com.sap.sdo.testcase");

        DataObject decimalInclusivePropObject = typeObject.createDataObject("property");
        decimalInclusivePropObject.set(PropertyType.NAME, "decimalInclusiveProp");
        decimalInclusivePropObject.set(PropertyType.TYPE, _helperContext.getTypeHelper().define(decimalInclusiveTypeObject));

        DataObject decimalExclusivePropObject = typeObject.createDataObject("property");
        decimalExclusivePropObject.set(PropertyType.NAME, "decimalExclusiveProp");
        decimalExclusivePropObject.set(PropertyType.TYPE, _helperContext.getTypeHelper().define(decimalExclusiveTypeObject));

        DataObject stringPropObject = typeObject.createDataObject("property");
        stringPropObject.set(PropertyType.NAME, "stringProp");
        stringPropObject.set(PropertyType.TYPE, _helperContext.getTypeHelper().define(stringTypeObject));

        DataObject lengthPropObject = typeObject.createDataObject("property");
        lengthPropObject.set(PropertyType.NAME, "lengthProp");
        lengthPropObject.set(PropertyType.TYPE, _helperContext.getTypeHelper().define(lengthTypeObject));

        save = _helperContext.getXMLHelper().save(typeObject, "commonj.sdo", "type");
        load = _helperContext.getXMLHelper().load(save);
        assertEquals(save, _helperContext.getXMLHelper().save(load.getRootObject(), "commonj.sdo", "type"));

        _type = _helperContext.getTypeHelper().define(typeObject);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        _type = null;
    }

    @Test
    public void testXsdFacetGeneration() throws Exception {
        String xsd = _helperContext.getXSDHelper().generate(Collections.singletonList(_type));
        assertNotNull(xsd);

        xsd = xsd.replace("com.sap.sdo.testcase", "com.sap.sdo.testcase2");

        List<DataObject> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(5, types.size());

        // JavaGeneratorVisitor visitor = new JavaGeneratorVisitor("com.sap.sdo.testcase.typefac", "test");
        // visitor.generate(types);

        for (DataObject object : types) {
            final DataObject facet = object.getDataObject(TypeType.getInstance().getFacetsProperty());
            if ("FacetTestType".equals(((Type)object).getName())) {
                assertNull(facet);
            } else if ("DecimalInclusiveFacetType".equals(((Type)object).getName())) {
                assertEquals(MIN_INCLUSIVE, facet.getInt(TypeType.FACET_MININCLUSIVE));
                assertEquals(MAX_INCLUSIVE, facet.getInt(TypeType.FACET_MAXINCLUSIVE));
                assertEquals(TOTAL_DIGITS, facet.getInt(TypeType.FACET_TOTALDIGITS));
            } else if ("DecimalExclusiveFacetType".equals(((Type)object).getName())) {
                assertEquals(MIN_EXCLUSIVE, facet.getInt(TypeType.FACET_MINEXCLUSIVE));
                assertEquals(MAX_EXCLUSIVE, facet.getInt(TypeType.FACET_MAXEXCLUSIVE));
                assertEquals(FRACTION_DIGITS, facet.getInt(TypeType.FACET_FRACTIONDIGITS));
            } else if ("StringFacetType".equals(((Type)object).getName())) {
                assertEquals(MIN_LENGTH, facet.getInt(TypeType.FACET_MINLENGTH));
                assertEquals(MAX_LENGTH, facet.getInt(TypeType.FACET_MAXLENGTH));
                assertEquals(ENUMERATION, facet.getList(TypeType.FACET_ENUMERATION));
                assertEquals(PATTERN, facet.getList(TypeType.FACET_PATTERN));
            } else if ("LengthFacetType".equals(((Type)object).getName())) {
                assertEquals(LENGTH, facet.getInt(TypeType.FACET_LENGTH));
            } else {
                fail("Unrecognized type: " + ((Type)object).getName());
            }
        }
    }

    @Test
    public void testJavaFacetGeneration() throws Exception {
        DataObject data = _helperContext.getDataFactory().create(FacetTestType.class);
        assertNotNull(data);

        DataObject decimalIncType = (DataObject)data.getInstanceProperty("decimalInclusiveProp").getType();
        DataObject facet = decimalIncType.getDataObject(TypeType.getInstance().getFacetsProperty());
        assertEquals(MIN_INCLUSIVE, facet.getInt(TypeType.FACET_MININCLUSIVE));
        assertEquals(MAX_INCLUSIVE, facet.getInt(TypeType.FACET_MAXINCLUSIVE));
        assertEquals(TOTAL_DIGITS, facet.getInt(TypeType.FACET_TOTALDIGITS));

        DataObject decimalExcType = (DataObject)data.getInstanceProperty("decimalExclusiveProp").getType();
        facet = decimalExcType.getDataObject(TypeType.getInstance().getFacetsProperty());
        assertEquals(MIN_EXCLUSIVE, facet.getInt(TypeType.FACET_MINEXCLUSIVE));
        assertEquals(MAX_EXCLUSIVE, facet.getInt(TypeType.FACET_MAXEXCLUSIVE));
        assertEquals(FRACTION_DIGITS, facet.getInt(TypeType.FACET_FRACTIONDIGITS));

        DataObject stringType = (DataObject)data.getInstanceProperty("stringProp").getType();
        facet = stringType.getDataObject(TypeType.getInstance().getFacetsProperty());
        assertEquals(MIN_LENGTH, facet.getInt(TypeType.FACET_MINLENGTH));
        assertEquals(MAX_LENGTH, facet.getInt(TypeType.FACET_MAXLENGTH));
        assertEquals(ENUMERATION, facet.getList(TypeType.FACET_ENUMERATION));
        assertEquals(PATTERN, facet.getList(TypeType.FACET_PATTERN));

        DataObject lengthType = (DataObject)data.getInstanceProperty("lengthProp").getType();
        facet = lengthType.getDataObject(TypeType.getInstance().getFacetsProperty());
        assertEquals(LENGTH, facet.getInt(TypeType.FACET_LENGTH));

    }

    @Test
    public void testDecimalInclusiveCheck() {
        DataObject data = _helperContext.getDataFactory().create(_type);
        Property prop = data.getInstanceProperty("decimalInclusiveProp");
        assertNotNull(prop);
        data.set(prop, 17);
        try {
            data.set(prop, 1);
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '1' of type com.sap.sdo.testcase#DecimalInclusiveFacetType fails validation check: MinInclusive=" + MIN_INCLUSIVE,
                ex.getMessage());
        }
        try {
            data.set(prop, 42);
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '42' of type com.sap.sdo.testcase#DecimalInclusiveFacetType fails validation check: MaxInclusive=" + MAX_INCLUSIVE,
                ex.getMessage());
        }
        try {
            data.set(prop, 17.25);
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '17.25' of type com.sap.sdo.testcase#DecimalInclusiveFacetType fails validation check: TotalDigits=" + TOTAL_DIGITS,
                ex.getMessage());
        }
    }

    @Test
    public void testDecimalExclusiveCheck() {
        DataObject data = _helperContext.getDataFactory().create(_type);
        Property prop = data.getInstanceProperty("decimalExclusiveProp");
        assertNotNull(prop);
        data.set(prop, 17);
        try {
            data.set(prop, 1);
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '1' of type com.sap.sdo.testcase#DecimalExclusiveFacetType fails validation check: MinExclusive=" + MIN_EXCLUSIVE,
                ex.getMessage());
        }
        try {
            data.set(prop, 42);
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '42' of type com.sap.sdo.testcase#DecimalExclusiveFacetType fails validation check: MaxExclusive=" + MAX_EXCLUSIVE,
                ex.getMessage());
        }
        try {
            data.set(prop, "17.125");
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '17.125' of type com.sap.sdo.testcase#DecimalExclusiveFacetType fails validation check: FractionDigits=" + FRACTION_DIGITS,
                ex.getMessage());
        }
    }

    @Test
    public void testStringCheck() {
        DataObject data = _helperContext.getDataFactory().create(_type);
        Property prop = data.getInstanceProperty("stringProp");
        assertNotNull(prop);
        data.set(prop, "test");
        try {
            data.set(prop, 1);
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '1' of type com.sap.sdo.testcase#StringFacetType fails validation check: MinLength=" + MIN_LENGTH,
                ex.getMessage());
        }
        try {
            data.set(prop, "testtest");
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value 'testtest' of type com.sap.sdo.testcase#StringFacetType fails validation check: MaxLength=" + MAX_LENGTH,
                ex.getMessage());
        }
        try {
            data.set(prop, "tust");
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value 'tust' of type com.sap.sdo.testcase#StringFacetType fails validation check: Enumeration=" + ENUMERATION,
                ex.getMessage());
        }
        try {
            data.set(prop, "tast");
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value 'tast' of type com.sap.sdo.testcase#StringFacetType fails validation check: Pattern=" + PATTERN,
                ex.getMessage());
        }
    }

    @Test
    public void testLengthCheck() {
        DataObject data = _helperContext.getDataFactory().create(_type);
        Property prop = data.getInstanceProperty("lengthProp");
        assertNotNull(prop);
        data.set(prop, "abc");
        try {
            data.set(prop, "test");
            fail("Expected IllegalArgumentException because facet failed.");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value 'test' of type com.sap.sdo.testcase#LengthFacetType fails validation check: Length=" + LENGTH,
                ex.getMessage());
        }
    }

    @Test
    public void testPropertyObject() {
        Property decimalInclusiveProp = _type.getProperty("decimalInclusiveProp");
        assertNotNull(decimalInclusiveProp);
        Type propType = decimalInclusiveProp.getType();
        DataObjectDecorator propObj = (DataObjectDecorator)decimalInclusiveProp;
        Type dataObjectType = propObj.getType();
        Type gdoType = propObj.getInstance().getType();

        System.out.println(propType);
        System.out.println(dataObjectType);
        System.out.println(gdoType);
    }

    @Test
    public void testJavaClassPropertyTest() {
        assertEquals(BigInteger.class, JavaSimpleType.INTEGER.getInstanceClass());
        assertEquals(long.class, JavaSimpleType.LONG.getInstanceClass());

        DataObject restrictedIntegerTypeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        restrictedIntegerTypeObject.set(TypeType.NAME, "RestrictedIntegerType");
        restrictedIntegerTypeObject.set(TypeType.URI, "com.sap.sdo.testcase");
        restrictedIntegerTypeObject.set(TypeType.DATA_TYPE, true);
        restrictedIntegerTypeObject.set(TypeType.BASE_TYPE, Collections.singletonList(JavaSimpleType.INTEGER));
        restrictedIntegerTypeObject.set(TypeType.getJavaClassProperty(), int.class.toString());

        DataObject facets = _helperContext.getDataFactory().create(TypeType.getInstance().getFacetsType());
        facets.set(TypeType.FACET_MININCLUSIVE, Integer.MIN_VALUE);
        facets.set(TypeType.FACET_MAXINCLUSIVE, Integer.MAX_VALUE);
        restrictedIntegerTypeObject.set(TypeType.getInstance().getFacetsProperty(), facets);

        Type restrictedIntegerType = _helperContext.getTypeHelper().define(restrictedIntegerTypeObject);
        assertEquals(int.class, restrictedIntegerType.getInstanceClass());

        DataObject restrictedLongTypeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        restrictedLongTypeObject.set(TypeType.NAME, "RestrictedLongType");
        restrictedLongTypeObject.set(TypeType.URI, "com.sap.sdo.testcase");
        restrictedLongTypeObject.set(TypeType.DATA_TYPE, true);
        restrictedLongTypeObject.set(TypeType.BASE_TYPE, Collections.singletonList(JavaSimpleType.LONG));
        restrictedLongTypeObject.set(TypeType.getJavaClassProperty(), int.class.toString());

        facets = _helperContext.getDataFactory().create(TypeType.getInstance().getFacetsType());
        facets.set(TypeType.FACET_MININCLUSIVE, Integer.MIN_VALUE);
        facets.set(TypeType.FACET_MAXINCLUSIVE, Integer.MAX_VALUE);
        restrictedLongTypeObject.set(TypeType.getInstance().getFacetsProperty(), facets);

        Type restrictedLongType = _helperContext.getTypeHelper().define(restrictedLongTypeObject);
        assertEquals(int.class, restrictedLongType.getInstanceClass());
    }

    @Test
    public void testSchemaJavaClassPropertyTest() throws Exception {
        assertSame(BigInteger.class, JavaSimpleType.INTEGER.getInstanceClass());
        assertSame(long.class, JavaSimpleType.LONG.getInstanceClass());

        URL schemaUrl = getClass().getClassLoader().getResource(PACKAGE+"restricted.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(schemaUrl.openStream(), schemaUrl.toString());
        assertNotNull(types);
        assertEquals(4, types.size());
        for (Type type : types) {
            assertSame(type.getName(), int.class, type.getInstanceClass());
        }
    }
    @Test
    public void testSchemaJavaClassPropertyNoIntTest() throws Exception {
        assertSame(BigInteger.class, JavaSimpleType.INTEGER.getInstanceClass());
        assertSame(long.class, JavaSimpleType.LONG.getInstanceClass());

        URL schemaUrl = getClass().getClassLoader().getResource(PACKAGE+"restrictedButNoInt.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(schemaUrl.openStream(), schemaUrl.toString());
        assertNotNull(types);
        assertEquals(4, types.size());
        for (Type type : types) {
            assertNotSame(type.getName(), int.class, type.getInstanceClass());
        }
    }
}
