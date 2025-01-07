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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.schema.Attribute;
import com.sap.sdo.api.types.schema.ComplexType;
import com.sap.sdo.api.types.schema.Element;
import com.sap.sdo.api.types.schema.ExplicitGroup;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.xml.XSDHelperImpl;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.internal.j1.sdo.ICity;
import com.sap.sdo.testcase.internal.j1.sdo.ICustomer;
import com.sap.sdo.testcase.internal.j1.sdo.IHotel;
import com.sap.sdo.testcase.internal.j1.sdo.IHotelRoomInfo;
import com.sap.sdo.testcase.internal.j1.sdo.IReservation;
import com.sap.sdo.testcase.internal.j1.sdo.ISequencedCity;
import com.sap.sdo.testcase.internal.j1.sdo.ITrip;
import com.sap.sdo.testcase.internal.j1.sdo.ITripReservation;
import com.sap.sdo.testcase.typefac.OpenContent;
import com.sap.sdo.testcase.typefac.SimpleTypesIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class XsdGenerationTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XsdGenerationTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXsdGenerationRoundtrip() {
        // this is not a real test but shows a hole in the spec
        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "RoundtripTestType");
        typeObject.set(TypeType.URI, "com.sap.sdo.testcase");
        typeObject.set(TypeType.OPEN, true);

        DataObject propObject = typeObject.createDataObject("property");
        propObject.set(PropertyType.NAME, "prop");
        propObject.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "String"));

        Type original = _helperContext.getTypeHelper().define(typeObject);
        String firstSchema = _helperContext.getXSDHelper().generate(Collections.singletonList(original));

        System.out.println(firstSchema);
        System.out.println(original.getProperty("prop").isMany());

        int index = firstSchema.indexOf("RoundtripTestType");
        String updated = firstSchema.substring(0, index) + "Second" + firstSchema.substring(index);

        Type firstRound = (Type)_helperContext.getXSDHelper().define(new StringReader(updated), null).get(0);
        String secondSchema = _helperContext.getXSDHelper().generate(Collections.singletonList(firstRound));

        System.out.println(secondSchema);
        System.out.println(firstRound.getProperty("prop").isMany());

        index = secondSchema.indexOf("SecondRoundtripTestType");
        updated = secondSchema.substring(0, index) + "Third" + secondSchema.substring(index + 6);

        Type secondRound = (Type)_helperContext.getXSDHelper().define(new StringReader(updated), null).get(0);
        String thirdSchema = _helperContext.getXSDHelper().generate(Collections.singletonList(secondRound));

        System.out.println(thirdSchema);
        System.out.println(secondRound.getProperty("prop").isMany());
    }

    @Test
    public void testIdrefsGeneration() {
        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "IdTestType");
        typeObject.set(TypeType.URI, "com.sap.sdo.testcase");

        DataObject propObject = typeObject.createDataObject("property");
        propObject.set(PropertyType.NAME, "id");
        propObject.set(PropertyType.TYPE, JavaSimpleType.STRING);
        propObject.set(PropertyType.KEY, true);
        propObject.set(PropertyType.getXsdTypeProperty(), URINamePair.SCHEMA_ID.toStandardSdoFormat());
        propObject.set(PropertyType.getXmlElementProperty(), true);

        Type idType = _helperContext.getTypeHelper().define(typeObject);

        assertNotNull(idType);
        assertEquals("IdTestType", idType.getName());
        assertEquals("com.sap.sdo.testcase", idType.getURI());
        List<Property> props = idType.getProperties();
        assertNotNull(props);
        assertEquals(1, props.size());
        for (Property property : props) {
            assertEquals("id", property.getName());
            assertEquals(JavaSimpleType.STRING, property.getType());
        }

        typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "IdrefsTestType");
        typeObject.set(TypeType.URI, "com.sap.sdo.testcase");

        propObject = typeObject.createDataObject("property");
        propObject.set(PropertyType.NAME, "idrefs");
        propObject.set(PropertyType.TYPE, idType);
        propObject.set(PropertyType.CONTAINMENT, false);
        propObject.set(PropertyType.MANY, true);
        propObject.set(PropertyType.getXmlElementProperty(), false);

        Type idrefsType = _helperContext.getTypeHelper().define(typeObject);

        assertNotNull(idrefsType);
        assertEquals("IdrefsTestType", idrefsType.getName());
        assertEquals("com.sap.sdo.testcase", idrefsType.getURI());
        props = idrefsType.getProperties();
        assertNotNull(props);
        assertEquals(1, props.size());
        for (Property property : props) {
            assertEquals("idrefs", property.getName());
            assertEquals(idType, property.getType());
            assertFalse(property.isContainment());
            assertTrue(property.isMany());
            assertFalse(((SdoProperty)property).isXmlElement());
        }

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        		   + "<xsd:schema targetNamespace=\"com.sap.sdo.testcase\""
                   + " xmlns:tns=\"com.sap.sdo.testcase\""
                   + " xmlns:sdox=\"commonj.sdo/xml\""
                   + " xmlns:sdoj=\"commonj.sdo/java\""
                   + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                   + ">\n"
                   + "<xsd:complexType name=\"IdrefsTestType\">\n"
                   + "    <xsd:attribute name=\"idrefs\" type=\"xsd:IDREFS\" sdox:propertyType=\"tns:IdTestType\"/>\n"
                   + "</xsd:complexType>\n"
                   + "<xsd:complexType name=\"IdTestType\">\n"
                   + "    <xsd:sequence>\n"
                   + "        <xsd:element name=\"id\" type=\"xsd:ID\" minOccurs=\"0\"/>\n"
                   + "    </xsd:sequence>\n"
                   + "</xsd:complexType>\n"
                   + "</xsd:schema>\n";

        assertLineEquality(xsd, _helperContext.getXSDHelper().generate(Collections.singletonList(idrefsType)));
    }

    @Test
    public void testIdrefGeneration() {
        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "IdTestType");
        typeObject.set(TypeType.URI, "com.sap.sdo.testcase");

        DataObject propObject = typeObject.createDataObject("property");
        propObject.set(PropertyType.NAME, "id");
        propObject.set(PropertyType.TYPE, JavaSimpleType.STRING);
        propObject.set(PropertyType.KEY, true);
        propObject.set(PropertyType.getXsdTypeProperty(), URINamePair.SCHEMA_ID.toStandardSdoFormat());
        propObject.set(PropertyType.getXmlElementProperty(), true);

        Type idType = _helperContext.getTypeHelper().define(typeObject);

        assertNotNull(idType);
        assertEquals("IdTestType", idType.getName());
        assertEquals("com.sap.sdo.testcase", idType.getURI());
        List<Property> props = idType.getProperties();
        assertNotNull(props);
        assertEquals(1, props.size());
        for (Property property : props) {
            assertEquals("id", property.getName());
            assertEquals(JavaSimpleType.STRING, property.getType());
        }

        typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "IdrefTestType");
        typeObject.set(TypeType.URI, "com.sap.sdo.testcase");

        propObject = typeObject.createDataObject("property");
        propObject.set(PropertyType.NAME, "idref");
        propObject.set(PropertyType.TYPE, idType);
        propObject.set(PropertyType.CONTAINMENT, false);
        propObject.set(PropertyType.MANY, false);
        propObject.set(PropertyType.getXmlElementProperty(), false);

        Type idrefType = _helperContext.getTypeHelper().define(typeObject);

        assertNotNull(idrefType);
        assertEquals("IdrefTestType", idrefType.getName());
        assertEquals("com.sap.sdo.testcase", idrefType.getURI());
        props = idrefType.getProperties();
        assertNotNull(props);
        assertEquals(1, props.size());
        for (Property property : props) {
            assertEquals("idref", property.getName());
            assertEquals(idType, property.getType());
            assertFalse(property.isContainment());
            assertFalse(property.isMany());
            assertFalse(((SdoProperty)property).isXmlElement());
        }

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                   + "<xsd:schema targetNamespace=\"com.sap.sdo.testcase\""
                   + " xmlns:tns=\"com.sap.sdo.testcase\""
                   + " xmlns:sdox=\"commonj.sdo/xml\""
                   + " xmlns:sdoj=\"commonj.sdo/java\""
                   + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                   + ">\n"
                   + "<xsd:complexType name=\"IdrefTestType\">\n"
                   + "    <xsd:attribute name=\"idref\" type=\"xsd:IDREF\" sdox:propertyType=\"tns:IdTestType\"/>\n"
                   + "</xsd:complexType>\n"
                   + "<xsd:complexType name=\"IdTestType\">\n"
                   + "    <xsd:sequence>\n"
                   + "        <xsd:element name=\"id\" type=\"xsd:ID\" minOccurs=\"0\"/>\n"
                   + "    </xsd:sequence>\n"
                   + "</xsd:complexType>\n"
                   + "</xsd:schema>\n";

        assertLineEquality(xsd, _helperContext.getXSDHelper().generate(Collections.singletonList(idrefType)));
    }

    @Test
    public void testSimpleTypesGeneration() {
        Type type = _helperContext.getTypeHelper().getType(SimpleTypesIntf.class);
        String schema = _helperContext.getXSDHelper().generate(Collections.singletonList(type));
        System.out.println(schema);
        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(schema);
        Schema schemaObj = (Schema)xmlDocument.getRootObject();
        ComplexType complexType = (ComplexType)schemaObj.getDataObject("complexType[name='SimpleTypesIntf']");
        ExplicitGroup sequence = complexType.getComplexTypeSequence();
        assertEquals(33, sequence.getElement().size());
        // check against table final 2.1.0 page 109
        checkElementType(sequence, "booleanObject", "boolean");
        checkElementType(sequence, "boolean", "boolean");
        checkElementType(sequence, "byteObject", "byte");
        checkElementType(sequence, "byte", "byte");
        checkElementType(sequence, "bytes", "hexBinary");
        checkElementType(sequence, "character", "string");
        checkElementType(sequence, "characterObject", "string");
        checkElementType(sequence, "date", "dateTime");
        checkElementType(sequence, "day", "gDay");
        checkElementType(sequence, "decimal", "decimal");
        checkElementType(sequence, "doubleObject", "double");
        checkElementType(sequence, "double", "double");
        checkElementType(sequence, "duration", "duration");
        checkElementType(sequence, "floatObject", "float");
        checkElementType(sequence, "float", "float");
        checkElementType(sequence, "intObject", "int");
        checkElementType(sequence, "int", "int");
        checkElementType(sequence, "integer", "integer");
        checkElementType(sequence, "longObject", "long");
        checkElementType(sequence, "long", "long");
        checkElementType(sequence, "monthDay", "gMonthDay");
        checkElementType(sequence, "month", "gMonth");
        checkElementType(sequence, "object", "anySimpleType");
        checkElementType(sequence, "shortObject", "short");
        checkElementType(sequence, "short", "short");
        checkElementType(sequence, "string", "string");
        checkElementType(sequence, "stringMany", "string");
        checkElementType(sequence, "strings", "string");
        checkElementType(sequence, "time", "time");
        checkElementType(sequence, "uRI", "anyURI");
        checkElementType(sequence, "year", "gYear");
        checkElementType(sequence, "yearMonth", "gYearMonth");
        checkElementType(sequence, "yearMonthDay", "date");

        checkAttributeType(complexType, "stringsAttr", "string");
    }

    private void checkElementType(ExplicitGroup pSequence, String pElementName, String pXsdTypeName) {
        Element element = (Element)pSequence.getDataObject("element[name='" + pElementName + "']");
        URINamePair typeUnp = URINamePair.fromStandardSdoFormat(element.getElementType());
        assertEquals(typeUnp.toStandardSdoFormat(), URINamePair.SCHEMA_URI, typeUnp.getURI());
        assertEquals(typeUnp.toStandardSdoFormat(), pXsdTypeName, typeUnp.getName());
    }

    private void checkAttributeType(ComplexType pComplexType, String pAttributeName, String pXsdTypeName) {
        Attribute attribute = (Attribute)pComplexType.getDataObject("attribute[name='" + pAttributeName + "']");
        URINamePair typeUnp = URINamePair.fromStandardSdoFormat(attribute.getAttributeType());
        assertEquals(typeUnp.toStandardSdoFormat(), URINamePair.SCHEMA_URI, typeUnp.getURI());
        assertEquals(typeUnp.toStandardSdoFormat(), pXsdTypeName, typeUnp.getName());
    }

    @Test
    public void testMixedType() throws Exception {
        final String schemaFileName = PACKAGE + "letterExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<xsd:schema" +
                        " targetNamespace=\"letter.xsd\"" +
                        " xmlns:tns=\"letter.xsd\"" +
                        " xmlns:sdox=\"commonj.sdo/xml\"" +
                        " xmlns:sdoj=\"commonj.sdo/java\"" +
                        " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                        ">\n" +
                     "<xsd:element name=\"letters\" type=\"tns:FormLetter\"/>\n" +
                     "<xsd:complexType name=\"FormLetter\" mixed=\"true\" sdox:sequence=\"true\">\n" +
                     "    <xsd:choice maxOccurs=\"unbounded\">\n" +
                     "        <xsd:element name=\"date\" type=\"xsd:string\" sdox:many=\"false\"/>\n" +
                     "        <xsd:element name=\"firstName\" type=\"xsd:string\" sdox:many=\"false\"/>\n" +
                     "        <xsd:element name=\"lastName\" type=\"xsd:string\" sdox:many=\"false\"/>\n" +
                     "        <xsd:any minOccurs=\"0\" namespace=\"##other\" processContents=\"lax\"/>\n" +
                     "        <xsd:any minOccurs=\"0\" namespace=\"##targetNamespace\" processContents=\"lax\"/>\n" +
                     "    </xsd:choice>\n" +
                     "    <xsd:anyAttribute namespace=\"##any\" processContents=\"lax\"/>\n" +
                     "</xsd:complexType>\n" +
                     "</xsd:schema>\n";

        assertLineEquality(xsd, _helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testAbstractType() throws Exception {
        final String schemaFileName = PACKAGE + "ExtensibleDocumented.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xsd = readFile(url);

        boolean foundTExtensibleDocumented = false;
        for (Type type : types) {
            if ("tExtensibleDocumented".equals(type.getName())) {
                String generateXsd = _helperContext.getXSDHelper().generate(Collections.singletonList(type));
                assertLineEquality(xsd, generateXsd);
                foundTExtensibleDocumented = true;
            }
        }
        assertTrue("Didn't parse type 'tExtensibleDocumented'.", foundTExtensibleDocumented);
    }

    @Test
    public void testExtendedInstanceClass() throws Exception {
        final String schemaFileName = PACKAGE + "ExtendedInstanceClass.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xsd = readFile(url);

        String generateXsd = _helperContext.getXSDHelper().generate(types);
        assertLineEquality(xsd, generateXsd);

        DataObject data = _helperContext.getDataFactory().create("com.sap.test1", "MyContainer");
        data.set("primitive", 42);
        assertEquals(42, data.get("primitive"));
        assertEquals("42", data.getString("primitive"));
        try {
            data.set("primitive", -42);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '-42' of type com.sap.test1#MyNewInt fails validation check: MinInclusive=-1",
                ex.getMessage());
        }
        try {
            data.set("primitive", "fünf");
            fail("expected ClassCastException");
        } catch (ClassCastException ex) {
            assertEquals("For input string: \"fünf\"", ex.getMessage());
        }
    }

    @Test
    public void testSimpleContent() throws Exception {
        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<xsd:schema" +
                            " targetNamespace=\"ext.xsd\"" +
                            " xmlns:tns=\"ext.xsd\"" +
                            " xmlns:sdox=\"commonj.sdo/xml\"" +
                            " xmlns:sdoj=\"commonj.sdo/java\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                            ">\n" +
                        "<xsd:element name=\"extended\" type=\"tns:ExtendedSimpleType\"/>\n" +
                        "<xsd:complexType name=\"ExtendedSimpleType\">\n" +
                        "    <xsd:simpleContent>\n" +
                        "        <xsd:extension base=\"xsd:positiveInteger\">\n" +
                        "            <xsd:attribute name=\"meta1\" type=\"xsd:string\"/>\n" +
                        "            <xsd:attribute name=\"meta2\" type=\"xsd:int\"/>\n" +
                        "        </xsd:extension>\n" +
                        "    </xsd:simpleContent>\n" +
                        "</xsd:complexType>\n" +
                        "<xsd:element name=\"restricted\" type=\"tns:RestrictedSimpleType\"/>\n" +
                        "<xsd:complexType name=\"RestrictedSimpleType\">\n" +
                        "    <xsd:simpleContent>\n" +
                        "        <xsd:extension base=\"tns:ExtendedSimpleType\"></xsd:extension>\n" +
                        "    </xsd:simpleContent>\n" +
                        "</xsd:complexType>\n" +
                        "<xsd:element name=\"container\">\n" +
                        "    <xsd:complexType sdox:name=\"+container\" sdox:aliasName=\"container\">\n" +
                        "        <xsd:sequence>\n" +
                        "            <xsd:element name=\"extended\" type=\"tns:ExtendedSimpleType\" minOccurs=\"0\" form=\"qualified\"/>\n" +
                        "            <xsd:element name=\"restricted\" type=\"tns:RestrictedSimpleType\" minOccurs=\"0\" form=\"qualified\"/>\n" +
                        "        </xsd:sequence>\n" +
                        "    </xsd:complexType>\n" +
                        "</xsd:element>\n" +
                        "</xsd:schema>\n";

        String generateXsd = _helperContext.getXSDHelper().generate(types);
        assertLineEquality(xsd, generateXsd);
    }

    @Test
    public void testUnqualifiedElementForm() throws Exception {
        final String schemaFileName = PACKAGE + "ElementQualAttributeUnqual.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<xsd:schema" +
                            " targetNamespace=\"com.sap.test.eqau\"" +
                            " xmlns:tns=\"com.sap.test.eqau\"" +
                            " xmlns:sdox=\"commonj.sdo/xml\"" +
                            " xmlns:sdoj=\"commonj.sdo/java\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                            " elementFormDefault=\"qualified\"" +
                            ">\n" +
                        "<xsd:element name=\"aElement\" type=\"tns:aType\"/>\n" +
                        "<xsd:complexType name=\"aType\">\n" +
                        "    <xsd:sequence>\n" +
                        "        <xsd:element name=\"aElement\" type=\"tns:aType\" minOccurs=\"0\"/>\n" +
                        "        <xsd:element name=\"a\" type=\"tns:aType\" minOccurs=\"0\"/>\n" +
                        "        <xsd:element name=\"b\" type=\"tns:bType\" minOccurs=\"0\"/>\n" +
                        "        <xsd:element name=\"c\" type=\"xsd:string\" minOccurs=\"0\" form=\"unqualified\"/>\n" +
                        "        <xsd:element name=\"refElement\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
                        "    </xsd:sequence>\n" +
                        "    <xsd:attribute name=\"a1\" type=\"xsd:string\"/>\n" +
                        "    <xsd:attribute name=\"a2\" type=\"xsd:string\"/>\n" +
                        "    <xsd:attribute name=\"a3\" type=\"xsd:string\" form=\"qualified\"/>\n" +
                        "    <xsd:attribute name=\"refAttribute\" type=\"xsd:string\" form=\"qualified\"/>\n" +
                        "</xsd:complexType>\n" +
                        "<xsd:element name=\"bElement\" type=\"tns:bType\"/>\n" +
                        "<xsd:complexType name=\"bType\" sdox:sequence=\"true\">\n" +
                        "    <xsd:choice maxOccurs=\"unbounded\">\n" +
                        "        <xsd:element name=\"a\" type=\"tns:aType\" sdox:many=\"false\"/>\n" +
                        "        <xsd:any minOccurs=\"0\" namespace=\"##other\" processContents=\"lax\"/>\n" +
                        "        <xsd:any minOccurs=\"0\" namespace=\"##local\" processContents=\"lax\"/>\n" +
                        "    </xsd:choice>\n" +
                        "    <xsd:attribute name=\"b1\" type=\"xsd:string\"/>\n" +
                        "    <xsd:attribute name=\"b2\" type=\"xsd:string\"/>\n" +
                        "    <xsd:anyAttribute namespace=\"##any\" processContents=\"lax\"/>\n" +
                        "</xsd:complexType>\n" +
                        "</xsd:schema>\n";

        String generateXsd = _helperContext.getXSDHelper().generate(types);
        assertLineEquality(xsd, generateXsd);
    }

    @Test
    public void testPropertyRenaming() throws Exception {
        final String schemaFileName = PACKAGE + "propertyRenaming.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<xsd:schema" +
                            " targetNamespace=\"com.sap.test\"" +
                            " xmlns:tns=\"com.sap.test\"" +
                            " xmlns:sdox=\"commonj.sdo/xml\"" +
                            " xmlns:sdoj=\"commonj.sdo/java\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                            ">\n" +
                        "<xsd:element name=\"property\" sdox:name=\"refElement\" type=\"tns:typeA\"/>\n" +
                        "<xsd:complexType name=\"typeA\">\n" +
                        "    <xsd:sequence>\n" +
                        "        <xsd:element name=\"property\" sdox:name=\"localElement\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
                        "        <xsd:element name=\"property\" sdox:name=\"refElement\" type=\"tns:typeA\" minOccurs=\"0\" form=\"qualified\"/>\n" +
                        "    </xsd:sequence>\n" +
                        "    <xsd:attribute name=\"property\" type=\"xsd:string\" form=\"qualified\"/>\n" +
                        "    <xsd:attribute name=\"property\" sdox:name=\"localAttribute\" type=\"xsd:int\"/>\n" +
                        "</xsd:complexType>\n" +
                        "<xsd:attribute name=\"property\" type=\"xsd:string\"/>\n" +
                        "</xsd:schema>\n";

        String generateXsd = _helperContext.getXSDHelper().generate(types);
        assertLineEquality(xsd, generateXsd);

        HelperContext newContext = SapHelperProvider.getNewContext();
        newContext.getXSDHelper().define(generateXsd);

        URL fileUrl = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/propertyRenaming.xml");
        XMLDocument document = newContext.getXMLHelper().load(fileUrl.openStream(), fileUrl.toString(), null);

        StringWriter stringWriter = new StringWriter();
        newContext.getXMLHelper().save(document, stringWriter, null);
        System.out.println(stringWriter.toString());

        DataObject root = document.getRootObject();

        Property rootRefElement = root.getInstanceProperty("refElement");
        assertEquals(false, rootRefElement.isOpenContent());
        assertEquals(false, rootRefElement.isMany());

        Type typeA = newContext.getTypeHelper().getType("com.sap.test", "typeA");
        assertEquals(typeA, rootRefElement.getType());
        assertEquals(rootRefElement, typeA.getProperty("refElement"));

        Property rootLocalElement = root.getInstanceProperty("localElement");
        assertEquals(false, rootLocalElement.isOpenContent());
        assertEquals(false, rootLocalElement.isMany());

        Type typeString = newContext.getTypeHelper().getType("commonj.sdo", "String");
        assertEquals(typeString, rootLocalElement.getType());
        assertEquals(rootLocalElement, typeA.getProperty("localElement"));

        assertEquals("elmentAText", root.getString("localElement"));
        assertEquals("elmentAText", root.get(rootLocalElement));

        DataObject aValue = root.getDataObject("refElement");

        assertEquals(typeA, aValue.getType());

        Property aValueRefAttribute = aValue.getInstanceProperty("property");
        assertEquals(false, aValueRefAttribute.isOpenContent());
        assertEquals(false, aValueRefAttribute.isMany());

        assertEquals(aValueRefAttribute, typeA.getProperty("property"));
        assertEquals(typeString, aValueRefAttribute.getType());

        assertEquals("fünf", aValue.getString("property"));
        assertEquals("fünf", aValue.get(aValueRefAttribute));

        Property aValueLocalAttribute = aValue.getInstanceProperty("localAttribute");
        assertEquals(false, aValueLocalAttribute.isOpenContent());
        assertEquals(false, aValueLocalAttribute.isMany());

        Type typeInt = newContext.getTypeHelper().getType("commonj.sdo", "Int");
        assertEquals(aValueLocalAttribute, typeA.getProperty("localAttribute"));
        assertEquals(typeInt, aValueLocalAttribute.getType());

        assertEquals(5, aValue.getInt("localAttribute"));
        assertEquals(Integer.valueOf(5), aValue.get(aValueLocalAttribute));

        assertEquals("more text", aValue.getString("localElement"));
    }

    @Test
    public void testValuePropWithoutXsdType() throws Exception {
        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<xsd:schema" +
                            " targetNamespace=\"\"" +
                            " xmlns:sdox=\"commonj.sdo/xml\"" +
                            " xmlns:sdoj=\"commonj.sdo/java\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                            ">\n" +
                        "<xsd:simpleType name=\"SimpleType\">\n" +
                        "    <xsd:restriction base=\"xsd:string\"></xsd:restriction>\n" +
                        "</xsd:simpleType>\n" +
                        "</xsd:schema>\n";

        List<Type> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(1, types.size());
        assertLineEquality(xsd, _helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testSimpleListType() throws Exception {
        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<xsd:schema" +
                            " targetNamespace=\"\"" +
                            " xmlns:sdox=\"commonj.sdo/xml\"" +
                            " xmlns:sdoj=\"commonj.sdo/java\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                            ">\n" +
                        "<xsd:simpleType name=\"SimpleListType\">\n" +
                        "    <xsd:list itemType=\"xsd:int\"/>\n" +
                        "</xsd:simpleType>\n" +
                        "</xsd:schema>\n";

        List<Type> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(2, types.size());
        assertLineEquality(xsd, _helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testReferencedType() throws Exception {
        String schemaFileName = PACKAGE + "employeeImportedExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        schemaFileName = PACKAGE + "IPO.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        schemaFileName = PACKAGE + "referencingType.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xsd = readFile(url);

        assertNotNull(types);
        assertEquals(2, types.size());
        Deque<Map<String,String>> stack = new ArrayDeque<Map<String,String>>();
        Map<String, String> uriNsMap = new HashMap<String,String>();
        uriNsMap.put("employeeimportedexample", "employeeimportedexample");
        uriNsMap.put("ipo", "ipo");
        stack.push(uriNsMap);
        for (Type type : types) {
            if ("ReferencingType".equals(type.getName())) {
                assertLineEquality(
                    xsd,
                    ((XSDHelperImpl)_helperContext.getXSDHelper()).generate(
                        Collections.singletonList(type),
                        new HashMap<String,String>(),
                        new ArrayList<Property>(),
                        stack));
            }
        }
    }

    @Test
    public void testHotelSchema() throws Exception {
        List<Type> types = new ArrayList<Type>();
        types.add(_helperContext.getTypeHelper().getType(ICity.class));
        types.add(_helperContext.getTypeHelper().getType(ICustomer.class));
        types.add(_helperContext.getTypeHelper().getType(IHotel.class));
        types.add(_helperContext.getTypeHelper().getType(IHotelRoomInfo.class));
        types.add(_helperContext.getTypeHelper().getType(IReservation.class));
        types.add(_helperContext.getTypeHelper().getType(ISequencedCity.class));
        types.add(_helperContext.getTypeHelper().getType(ITrip.class));
        types.add(_helperContext.getTypeHelper().getType(ITripReservation.class));

        System.out.println(_helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testGlobalProperty() {
        Type type = _helperContext.getTypeHelper().getType(OpenContent.class);
        Property globalAttr = _helperContext.getTypeHelper().getOpenContentProperty(type.getURI(), "attribute");
        String xsd = _helperContext.getXSDHelper().generate(Collections.singletonList(globalAttr.getContainingType()));
        System.out.println(xsd);

        HelperContext newHelperContext = SapHelperProvider.getNewContext();
        newHelperContext.getXSDHelper().define(xsd);
        Property globalAttr2 = newHelperContext.getTypeHelper().getOpenContentProperty(type.getURI(), "attribute");
        assertNotNull(globalAttr2);
    }
}
