/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
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

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SubstitutionGroupTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SubstitutionGroupTest(String pContextId, Feature pFeature) {
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

    @Test
    public void testSubstitutionGroup() throws Exception {
        final String schemaFileName = PACKAGE + "substitutionGroup.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        DataObject data = _helperContext.getDataFactory().create("test.com.sap.sdo", "testDO");
        assertNotNull(data);

        DataObject someB = _helperContext.getDataFactory().create("test.com.sap.sdo", "B");
        assertNotNull(someB);

        someB.set("element", true);
        someB.set("extension", 42);

        data.set("a", someB);

        String xml = _helperContext.getXMLHelper().save(data, "test.com.sap.sdo", "testDo");
        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        DataObject loadedB = root.getDataObject("a");
        assertNotNull(loadedB);

        assertEquals(someB.getType(), loadedB.getType());
        assertEquals(true, loadedB.getBoolean("element"));
        assertEquals(42, loadedB.getInt("extension"));
    }

    @Test
    public void testSubstitutionGroupA() throws Exception {
        final String schemaFileName = PACKAGE + "substitutionGroup.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xml = "<ns1:testDo xmlns:ns1=\"test.com.sap.sdo\" xsi:type=\"ns1:testDO\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "  <ns1:a xsi:type=\"ns1:B\">" +
                    "    <ns1:element>true</ns1:element>" +
                    "    <ns1:extension>42</ns1:extension>" +
                    "  </ns1:a>" +
                    "</ns1:testDo>";

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        DataObject refA = root.getDataObject("a");
        assertNotNull(refA);
        assertEquals(_helperContext.getTypeHelper().getType("test.com.sap.sdo", "B"), refA.getType());
        assertEquals(true, refA.getBoolean("element"));
        assertEquals(42, refA.getInt("extension"));
    }

    @Test
    public void testSubstitutionGroupB() throws Exception {
        final String schemaFileName = PACKAGE + "substitutionGroup.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xml = "<ns1:testDo xmlns:ns1=\"test.com.sap.sdo\" xsi:type=\"ns1:testDO\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "  <ns1:b>" +
                    "    <ns1:element>true</ns1:element>" +
                    "    <ns1:extension>42</ns1:extension>" +
                    "  </ns1:b>" +
                    "</ns1:testDo>";

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        DataObject refA = root.getDataObject("a");
        assertNotNull(refA);
        assertEquals(_helperContext.getTypeHelper().getType("test.com.sap.sdo", "B"), refA.getType());
        assertEquals(true, refA.getBoolean("element"));
        assertEquals(42, refA.getInt("extension"));
    }

    @Test
    public void testSubstitutionGroupC() throws Exception {
        final String schemaFileName = PACKAGE + "substitutionGroup.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xml = "<ns1:testDo xmlns:ns1=\"test.com.sap.sdo\" xsi:type=\"ns1:testDO\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "  <ns1:c>" +
                    "    <ns1:element>true</ns1:element>" +
                    "    <ns1:extension>42</ns1:extension>" +
                    "  </ns1:c>" +
                    "</ns1:testDo>";

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        DataObject refA = root.getDataObject("a");
        assertNotNull(refA);
        assertEquals(_helperContext.getTypeHelper().getType("test.com.sap.sdo", "B"), refA.getType());
        assertEquals(true, refA.getBoolean("element"));
        assertEquals(42, refA.getInt("extension"));
    }

    @Test
    public void testSimpleTypeSubstGroupName() throws Exception {
        final String schemaFileName = PACKAGE + "substitutionGroup.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xml = "<ns1:nameDo xmlns:ns1=\"test.com.sap.sdo\" xsi:type=\"ns1:nameType\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "  <ns1:name>Hans Mustermann</ns1:name>" +
                    "</ns1:nameDo>";

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        String refName = root.getString("name");
        assertEquals("Hans Mustermann", refName);
    }

    @Test
    public void testSimpleTypeSubstGroupSurname() throws Exception {
        final String schemaFileName = PACKAGE + "substitutionGroup.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        String xml = "<ns1:nameDo xmlns:ns1=\"test.com.sap.sdo\" xsi:type=\"ns1:nameType\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "  <ns1:surname>Mustermann</ns1:surname>" +
                    "</ns1:nameDo>";

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        String refName = root.getString("name");
        assertEquals("Mustermann", refName);
    }
}
