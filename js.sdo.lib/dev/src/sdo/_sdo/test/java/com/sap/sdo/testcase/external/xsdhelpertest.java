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
package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.schema.ExplicitGroup;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.SimpleChild;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleIntf1;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;

/**
 * @author D042774
 *
 */
public class XSDHelperTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XSDHelperTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
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

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#getAppinfo(commonj.sdo.Property, java.lang.String)}.
     */
    @Test
    public void testGetAppinfoPropertyString() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertNotNull(type);
        Property prop = type.getProperty("name");
        assertNotNull(prop);
        assertEquals(null, _helperContext.getXSDHelper().getAppinfo(prop, null));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#getAppinfo(commonj.sdo.Type, java.lang.String)}.
     */
    @Test
    public void testGetAppinfoTypeString() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertNotNull(type);
        assertEquals(null, _helperContext.getXSDHelper().getAppinfo(type, null));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#getLocalName(commonj.sdo.Property)}.
     */
    @Test
    public void testGetLocalNameProperty() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertNotNull(type);
        Property prop = type.getProperty("name");
        assertNotNull(prop);
        assertEquals("name", _helperContext.getXSDHelper().getLocalName(prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#getLocalName(commonj.sdo.Type)}.
     */
    @Test
    public void testGetLocalNameType() {
        Type type = _helperContext.getTypeHelper().getType("commonj.sdo", "Bytes");
        assertNotNull(type);
        //see SDO-269
        assertEquals("hexBinary", _helperContext.getXSDHelper().getLocalName(type));
//        assertEquals("Bytes", _helperContext.getXSDHelper().getLocalName(type));

        type = _helperContext.getTypeHelper().getType(OpenInterface.class);
        assertNotNull(type);
        assertEquals("OpenInterface", _helperContext.getXSDHelper().getLocalName(type));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#getNamespaceURI(commonj.sdo.Property)}.
     */
    @Test
    public void testGetNamespaceURI() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertNotNull(type);
        Property prop = type.getProperty("name");
        assertNotNull(prop);
        assertEquals("", _helperContext.getXSDHelper().getNamespaceURI(prop));

        type = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        assertNotNull(type);
        prop = type.getProperty("inner");
        assertNotNull(prop);
        assertEquals("", _helperContext.getXSDHelper().getNamespaceURI(prop));

        type = _helperContext.getTypeHelper().getType(SimpleChild.class);
        assertNotNull(type);
        prop = type.getProperty("parent");
        assertNotNull(prop);
        assertEquals("", _helperContext.getXSDHelper().getNamespaceURI(prop));

        type = _helperContext.getTypeHelper().getType(ExplicitGroup.class);
        assertNotNull(type);
        prop = type.getProperty("all");
        assertNotNull(prop);
        assertEquals("http://www.w3.org/2001/XMLSchema", _helperContext.getXSDHelper().getNamespaceURI(prop));

        type = _helperContext.getTypeHelper().getType(Schema.class);
        assertNotNull(type);
        prop = type.getProperty("lang");
        assertNotNull(prop);
        assertEquals("http://www.w3.org/XML/1998/namespace", _helperContext.getXSDHelper().getNamespaceURI(prop));

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        assertNotNull(data);
        data.set("testProp", "testValue");
        prop = data.getInstanceProperty("testProp");
        assertNotNull(prop);
        assertEquals(null, _helperContext.getXSDHelper().getNamespaceURI(prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#isAttribute(commonj.sdo.Property)}.
     */
    @Test
    public void testIsAttribute() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertNotNull(type);
        Property prop = type.getProperty("name");
        assertNotNull(prop);
        assertEquals(false, _helperContext.getXSDHelper().isAttribute(prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#isElement(commonj.sdo.Property)}.
     */
    @Test
    public void testIsElement() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertNotNull(type);
        Property prop = type.getProperty("name");
        assertNotNull(prop);
        assertEquals(true, _helperContext.getXSDHelper().isElement(prop));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#isXSD(commonj.sdo.Type)}.
     */
    @Test
    public void testIsXSD() {
        Type type = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        assertNotNull(type);
        assertEquals(false, _helperContext.getXSDHelper().isXSD(type));

        final String schemaFileName = PACKAGE + "companyExample.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertFalse(types.isEmpty());
        for (Type type2 : types) {
            assertEquals(true, _helperContext.getXSDHelper().isXSD(type2));
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#define(java.lang.String, java.util.Map)}.
     * @throws Exception
     */
    @Test
    public void testDefineStringMap() throws Exception {
        String schemaFileName = PACKAGE + "companyExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final String schema = readFile(url);
        List<? extends Type> types = ((SapXsdHelper)_helperContext.getXSDHelper()).define(schema,null);
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertEquals(3, types.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#define(java.io.Reader, java.lang.String, java.util.Map)}.
     * @throws Exception
     */
    @Test
    public void testDefineReaderStringMap() throws Exception {
        String schemaFileName = PACKAGE + "companyExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<? extends Type> types =
            ((SapXsdHelper)_helperContext.getXSDHelper()).define(
                new InputStreamReader(url.openStream(), "UTF-8"),null,null);
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertEquals(3, types.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#define(java.io.InputStream, java.lang.String, java.util.Map)}.
     * @throws Exception
     */
    @Test
    public void testDefineInputStreamStringMap() throws Exception {
        String schemaFileName = PACKAGE + "companyExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<? extends Type> types =
            ((SapXsdHelper)_helperContext.getXSDHelper()).define(url.openStream(),null,null);
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertEquals(3, types.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#define(java.util.List, java.util.Map)}.
     * @throws Exception
     */
    @Test
    public void testDefineListOfSchemaMap() throws Exception {
        String schemaFileName = PACKAGE + "companyExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<? extends Type> types =
            ((SapXsdHelper)_helperContext.getXSDHelper()).define(
                Collections.singletonList(
                    (Schema)_helperContext.getXMLHelper().load(url.openStream()).getRootObject())
                ,null);
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertEquals(3, types.size());
    }

    @Test
    public void testDateTimeType() throws Exception {
        String schemaFileName = PACKAGE + "dateTime.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertEquals(3, types.size());

        DataObject root = null;
        DataObject data = null;
        for (Type type : types) {
            if ("LOCALNORMALISED_DateTime".equals(type.getName())) {
                data = _helperContext.getDataFactory().create(type);
            } else if ("RootType".equals(type.getName())) {
                root = _helperContext.getDataFactory().create(type);
            }
        }
        assertNotNull(root);
        assertNotNull(data);

        data.set("value", "2007-08-27T16:48:07.220+02:00");
        data.set("timeZoneCode", "GMT+2");

        List<Property> rootProps = root.getInstanceProperties();
        assertNotNull(rootProps);
        assertEquals(1, rootProps.size());
        root.set(rootProps.get(0), data);

        String xml = _helperContext.getXMLHelper().save(root, null, "root");
        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertEquals(
            xml,
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName()));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XSDHelperDelegator#toString()}.
     */
    @Test
    public void testToString() {
        assertNotNull(_helperContext.getXSDHelper().toString());
    }

    @Test
    public void testDefaultValues() throws Exception {
        String schemaFileName = PACKAGE + "DefaultValues.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        String xsd = _helperContext.getXSDHelper().generate(types);
        xsd = xsd.replace("com.sap.test", "com.sap.test2");

        List<Type> readTypes = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(readTypes);
        assertEquals(types.size(), readTypes.size());

        for (Property prop : (List<Property>)readTypes.get(0).getProperties()) {
            Object defaultValue = types.get(0).getProperty(prop.getName()).getDefault();
            assertNotNull(defaultValue);
            assertEquals(defaultValue, prop.getDefault());
        }
        assertEquals(xsd, _helperContext.getXSDHelper().generate(readTypes));
    }

    @Test
    public void testSdoTypeName() throws Exception {
        final String schemaFileName = PACKAGE + "sdoTypeName.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        Type type = types.get(0);
        assertEquals("sdoTypeName", type.getName());
        assertEquals("typeName", _helperContext.getXSDHelper().getLocalName(type));
        assertLineEquality(readFile(url), _helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testSdoTypeAlias() throws Exception {
        final String schemaFileName = PACKAGE + "sdoTypeAlias.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        Type type = types.get(0);
        List<String> aliasNames = type.getAliasNames();
        assertNotNull(aliasNames);
        assertEquals(1, aliasNames.size());
        assertEquals("sdoTypeName", aliasNames.get(0));
        assertEquals("typeName", type.getName());
        assertEquals("typeName", _helperContext.getXSDHelper().getLocalName(type));
        assertSame(type, _helperContext.getTypeHelper().getType(type.getURI(), "typeName"));
        assertSame(type, _helperContext.getTypeHelper().getType(type.getURI(), "sdoTypeName"));
        List<SdoProperty> props = type.getProperties();
        assertNotNull(props);
        assertEquals(2, props.size());
        SdoProperty element = props.get(0);
        assertEquals(true, element.isXmlElement());
        List<String> elementAlias = element.getAliasNames();
        assertNotNull(elementAlias);
        assertEquals(1, elementAlias.size());
        assertEquals("sdoProperty", elementAlias.get(0));
        assertEquals("property", element.getName());
        SdoProperty attribute = props.get(1);
        assertEquals(false, attribute.isXmlElement());
        List<String> attrAlias = attribute.getAliasNames();
        assertNotNull(attrAlias);
        assertEquals(1, attrAlias.size());
        assertEquals("sdoAttribute", attrAlias.get(0));
        assertEquals("attribute", attribute.getName());
        Property global = _helperContext.getXSDHelper().getGlobalProperty("", "element", true);
        assertNotNull(global);
        assertSame(global, _helperContext.getTypeHelper().getOpenContentProperty("", "element"));
        List<String> globalAlias = global.getAliasNames();
        assertNotNull(globalAlias);
        assertEquals(1, globalAlias.size());
        assertEquals("sdoElement", globalAlias.get(0));
        assertEquals("element", global.getName());
        Property sdoGlobal = _helperContext.getTypeHelper().getOpenContentProperty("", "sdoElement");
        assertNotNull(sdoGlobal);
        // assertSame(sdoGlobal, _helperContext.getTypeHelper().getOpenContentProperty("", "sdoElement"));
        List<String> sdoGlobalAlias = sdoGlobal.getAliasNames();
        assertNotNull(sdoGlobalAlias);
        assertEquals(1, sdoGlobalAlias.size());
        assertEquals("sdoElement", sdoGlobalAlias.get(0));
        assertEquals("element", sdoGlobal.getName());

        assertLineEquality(readFile(url), _helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testListSimpleType() throws Exception {
        final String schemaFileName = PACKAGE + "ListSimpleTypeInt.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);

        Type containerType = _helperContext.getTypeHelper().getType("com.sap.sdo.testcase.internal", "Container");
        Type listType = containerType.getProperty("x").getType();
        assertEquals(List.class, listType.getInstanceClass());

        String schema = _helperContext.getXSDHelper().generate(types);

        HelperContext newHelperContext = SapHelperProvider.getNewContext();

        List<Type> types2 = newHelperContext.getXSDHelper().define(schema);
        assertNotNull(types2);

        Type containerType2 = newHelperContext.getTypeHelper().getType("com.sap.sdo.testcase.internal", "Container");
        Type listType2 = containerType.getProperty("x").getType();
        assertEquals(List.class, listType2.getInstanceClass());

        DataObject container = newHelperContext.getDataFactory().create(containerType2);
        container.setString("x", "0 8 15");

        List<Integer> integers = (List<Integer>)container.get("x");
        assertEquals(integers.toString(), 3, integers.size());
        assertEquals(Integer.valueOf(0), integers.get(0));
        assertEquals(Integer.valueOf(8), integers.get(1));
        assertEquals(Integer.valueOf(15), integers.get(2));

    }

    @Test
    public void testPropsWithSameNames() throws Exception {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        List<Type> types = xsdHelper.define(url.openStream(), url.toString());
        Type sameNames = typeHelper.getType("com.sap.sdo.testcase.anonymous", "sameNames");
        List<Property> properties = sameNames.getProperties();
        assertEquals(4, properties.size());
        Property prop0 = properties.get(0);
        Property prop1 = properties.get(1);
        Property prop2 = properties.get(2);
        Property prop3 = properties.get(3);
        assertEquals("prop", prop0.getName());
        assertEquals("prop", prop1.getName());
        assertEquals("prop", prop2.getName());
        assertEquals("prop", prop3.getName());
        assertEquals("com.sap.sdo.testcase.anonymous", xsdHelper.getNamespaceURI(prop0));
        assertEquals("", xsdHelper.getNamespaceURI(prop1));
        assertEquals("com.sap.sdo.testcase.anonymous", xsdHelper.getNamespaceURI(prop2));
        assertEquals("", xsdHelper.getNamespaceURI(prop3));
        assertEquals(true, xsdHelper.isElement(prop0));
        assertEquals(true, xsdHelper.isElement(prop1));
        assertEquals(true, xsdHelper.isAttribute(prop2));
        assertEquals(true, xsdHelper.isAttribute(prop3));

        SapXsdHelper sapXsdHelper = (SapXsdHelper)xsdHelper;
        assertSame(prop0, sapXsdHelper.getProperty(sameNames, "com.sap.sdo.testcase.anonymous", "prop", true));
        assertSame(prop1, sapXsdHelper.getProperty(sameNames, "", "prop", true));
        assertSame(prop2, sapXsdHelper.getProperty(sameNames, "com.sap.sdo.testcase.anonymous", "prop", false));
        assertSame(prop3, sapXsdHelper.getProperty(sameNames, "", "prop", false));

        DataObject sameNamesDO = _helperContext.getDataFactory().create(sameNames);

        assertSame(prop0, sapXsdHelper.getInstanceProperty(sameNamesDO, "com.sap.sdo.testcase.anonymous", "prop", true));
        assertSame(prop1, sapXsdHelper.getInstanceProperty(sameNamesDO, "", "prop", true));
        assertSame(prop2, sapXsdHelper.getInstanceProperty(sameNamesDO, "com.sap.sdo.testcase.anonymous", "prop", false));
        assertSame(prop3, sapXsdHelper.getInstanceProperty(sameNamesDO, "", "prop", false));

    }

    @Test
    public void testReadPropsWithSameNamesWOSchema() throws Exception {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xml");
        SapXsdHelper sapXsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);
        DataObject sameNamesDO = document.getRootObject();

        StringWriter genXML = new StringWriter();
        _helperContext.getXMLHelper().save(document, genXML, null);

        Property prop0 = sapXsdHelper.getInstanceProperty(sameNamesDO, "com.sap.sdo.testcase.anonymous", "prop", true);
        Property prop1 = sapXsdHelper.getInstanceProperty(sameNamesDO, "", "prop", true);
        Property prop2 = sapXsdHelper.getInstanceProperty(sameNamesDO, "com.sap.sdo.testcase.anonymous", "prop", false);
        Property prop3 = sapXsdHelper.getInstanceProperty(sameNamesDO, "", "prop", false);

        assertEquals(5, sameNamesDO.getInt(prop0));
        assertEquals("elem", sameNamesDO.get(prop1));
        assertEquals(2, sameNamesDO.getInt(prop2));
        assertEquals("attr", sameNamesDO.get(prop3));
    }

    @Test
    public void testReadPropsWithSameNamesWithSchema() throws Exception {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xsd");
        SapXsdHelper sapXsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        sapXsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());
        URL url = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xml");
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);
        DataObject sameNamesDO = document.getRootObject();

        StringWriter genXML = new StringWriter();
        _helperContext.getXMLHelper().save(document, genXML, null);

        Property prop0 = sapXsdHelper.getInstanceProperty(sameNamesDO, "com.sap.sdo.testcase.anonymous", "prop", true);
        Property prop1 = sapXsdHelper.getInstanceProperty(sameNamesDO, "", "prop", true);
        Property prop2 = sapXsdHelper.getInstanceProperty(sameNamesDO, "com.sap.sdo.testcase.anonymous", "prop", false);
        Property prop3 = sapXsdHelper.getInstanceProperty(sameNamesDO, "", "prop", false);

        assertEquals(5, sameNamesDO.getInt(prop0));
        assertEquals("elem", sameNamesDO.get(prop1));
        assertEquals(2, sameNamesDO.getInt(prop2));
        assertEquals("attr", sameNamesDO.get(prop3));
    }

    @Test
    public void testTranslateNames() throws IOException {
        SapXsdHelper sapXsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        URINamePair xsdString = sapXsdHelper.getXsdName(URINamePair.STRING);
        assertEquals(URINamePair.SCHEMA_URI, xsdString.getURI());
        assertEquals("string", xsdString.getName());

        Type stringType = _helperContext.getTypeHelper().getType(URINamePair.STRING.getURI(), URINamePair.STRING.getName());
        assertEquals(URINamePair.SCHEMA_URI, sapXsdHelper.getNamespaceURI(stringType));
        assertEquals("string", sapXsdHelper.getLocalName(stringType));

        assertEquals(URINamePair.STRING, sapXsdHelper.getSdoName(xsdString));

        assertEquals(URINamePair.URI, sapXsdHelper.getSdoName(URINamePair.SCHEMA_Q_NAME));

        final String schemaFileName = PACKAGE + "sdoTypeName.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = sapXsdHelper.define(url.openStream(), url.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        Type type = types.get(0);
        assertEquals("sdoTypeName", type.getName());
        assertEquals("typeName", sapXsdHelper.getLocalName(type));

        URINamePair sdoUnp = new URINamePair(type.getURI(), type.getName());
        URINamePair xsdUnp = new URINamePair(sapXsdHelper.getNamespaceURI(type), sapXsdHelper.getLocalName(type));

        assertEquals(sdoUnp, sapXsdHelper.getSdoName(xsdUnp));
        assertEquals(xsdUnp, sapXsdHelper.getXsdName(sdoUnp));
    }

    @Test
    public void testContainsSchemaLocation() throws IOException {
        SapXsdHelper sapXsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        final String schemaFileName = PACKAGE + "source.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        assertEquals(false, sapXsdHelper.containsSchemaLocation("http://www.source.com", url.toString()));
        sapXsdHelper.define(url.openStream(), url.toString());
        assertEquals(true, sapXsdHelper.containsSchemaLocation("http://www.source.com", url.toString()));
    }

    @Test
    public void testSchemaLocationWithSpaces1() throws IOException {
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(PACKAGE + "NewXMLSchema.xsd");
        URL url1 = classLoader.getResource(PACKAGE + "NewXML  Schema1.xsd");
        xsdHelper.define(url1.openStream(), null);
        xsdHelper.define(url.openStream(), null);
    }

    @Test
    public void testSchemaLocationWithSpaces2() throws IOException {
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(PACKAGE + "NewXMLSchema.xsd");
        xsdHelper.define(url.openStream(), url.toString());
    }

    @Test
    public void testSchemaLocationWithSpaces3() throws IOException {
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(PACKAGE + "NewXMLSchema.xsd");
        xsdHelper.define(url.openStream(), PACKAGE + "NewXMLSchema.xsd");
    }
}
