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
package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * section 9.10 of SDO 2.1
 * @author D042774
 *
 */
public class XmlWithoutSchemaTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XmlWithoutSchemaTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String FILE_NAME =
        "com/sap/sdo/testcase/schemas/poExampleWithoutXSD.xml";
    private XMLDocument _doc;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        loadDocument();
    }

    private void loadDocument() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(FILE_NAME);

        _doc = _helperContext.getXMLHelper().load(stream);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _doc = null;
    }

    @Test
    public void testRoundTrip() {
        String xml = _helperContext.getXMLHelper().save(_doc.getRootObject(), null, "purchaseOrder");
        System.out.println(xml);
        assertTrue(xml.contains("ns1:orderDate"));
        assertTrue(xml.contains("ns2:zip"));
        assertTrue(xml.contains("xml:lang"));
        assertTrue(xml.contains("ns3:country"));
        String xml2 = _helperContext.getXMLHelper().save(
            _helperContext.getXMLHelper().load(xml).getRootObject(),
                null,
                "purchaseOrder");
        System.out.println(xml2);

        assertEquals(xml, xml2);
    }

    @Test
    public void testRootObject() {
        assertNotNull(_doc);
        DataObject root = _doc.getRootObject();
        assertNotNull(root);
        Type type = root.getType();
        assertNotNull(type);
        assertTrue(type.isOpen());
        assertTrue(type.isSequenced());
        assertTrue(_helperContext.getXSDHelper().isMixed(type));
    }

    @Test
    public void testElementWithURI() throws Exception {
        DataObject p = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        p.set("name", "zip");
        p.set(_helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "xmlElement"), true);
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty("com.sap.sdo.test.zip", p);

        loadDocument();

        DataObject shipTo = _doc.getRootObject().getDataObject("shipTo");
        assertNotNull(shipTo);
        Property zip = shipTo.getInstanceProperty("zip");
        assertNotNull(zip);
        assertSame(prop, zip);
        assertSame(
            _helperContext.getXSDHelper().getGlobalProperty("com.sap.sdo.test.zip", "zip", true),
            zip);
    }

    @Test
    public void testAttributeWithURI() throws Exception {
        DataObject p = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "YearMonthDay"));
        p.set("name", "orderDate");
        p.set(_helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "xmlElement"), false);
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty("com.sap.sdo.test.date", p);

        loadDocument();

        Property orderDate = _doc.getRootObject().getInstanceProperty("orderDate");
        assertNotNull(orderDate);
        assertSame(prop, orderDate);
        assertSame(
            _helperContext.getXSDHelper().getGlobalProperty("com.sap.sdo.test.date", "orderDate", false),
            orderDate);
    }

    @Test
    public void testAttribute() {
        DataObject billTo = _doc.getRootObject().getDataObject("billTo");
        assertNotNull(billTo);
        Property country = billTo.getInstanceProperty("country");
        assertNotNull(country);
        assertEquals("country", country.getName());
        assertTrue(country.isOpenContent());
        assertSame(JavaSimpleType.STRING, country.getType());
    }

    @Test
    public void testElement() {
        Property billTo = _doc.getRootObject().getInstanceProperty("billTo");
        assertNotNull(billTo);
        assertEquals("billTo", billTo.getName());
        assertTrue(billTo.isOpenContent());
        assertTrue(billTo.isContainment());
    }

    @Test
    public void testMultipleElements() {
        DataObject items = _doc.getRootObject().getDataObject("items");
        assertNotNull(items);
        Property item = items.getInstanceProperty("item");
        assertNotNull(item);
        assertEquals("item", item.getName());
        assertTrue(item.isOpenContent());
        assertTrue(item.isContainment());
        assertTrue(item.isMany());
    }

    @Test
    public void testPropertiesType() {
        Property billTo = _doc.getRootObject().getInstanceProperty("billTo");
        assertNotNull(billTo);
        Type type = billTo.getType();
        assertNotNull(type);

        DataObject billToDo = _doc.getRootObject().getDataObject(billTo);
        assertNotNull(billToDo);

        Type billToType = billToDo.getType();
        assertTrue(billToType.isOpen());
        assertTrue(billToType.isSequenced());
        assertTrue(_helperContext.getXSDHelper().isMixed(billToType));

        Property street = billToDo.getInstanceProperty("street");
        assertNotNull(street);
        assertSame(JavaSimpleType.STRING, street.getType());

        Property zip = billToDo.getInstanceProperty("zip");
        assertNotNull(zip);
        assertSame(JavaSimpleType.INTEGER, zip.getType());
    }

    @Test
    public void testWithoutSimplify() throws MalformedURLException, IOException {
        String original = readFile(getClass().getClassLoader().getResource(
            "com/sap/sdo/testcase/schemas/ElementQualAttributeQualExtended.xml"));
        Map options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        XMLDocument document = _helperContext.getXMLHelper().load(new StringReader(original), null, options);
        StringWriter output = new StringWriter();
        _helperContext.getXMLHelper().save(document, output, options);
        assertLineEquality(original, output.toString());
    }
}
