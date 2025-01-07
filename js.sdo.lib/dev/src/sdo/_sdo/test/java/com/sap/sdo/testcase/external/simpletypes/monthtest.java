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
package com.sap.sdo.testcase.external.simpletypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
public class MonthTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public MonthTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private IMonthTest _b = null;
    private DataObject _d = null;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _b = (IMonthTest)_helperContext.getDataFactory().create(IMonthTest.class);
        _d = (DataObject)_b;
    }

    /*
     * @see TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _b = null;
        _d = null;
    }

    /*
     * Test method for 'com.sap.sdo.impl.types.simple.AbstractDTSimpleType.convertToJavaClass(Object, Class<T>) <T>'
     */
    @Test
    public void testConvertToJavaClass() {
        assertNull(_b.getAdate());

        _b.setAdate(null);
        assertNull(_b.getAdate());

        String ds = "--01";
        _b.setAdate(ds);
        assertEquals("adate should have changed", ds, _b.getAdate());

        // unable conversion
        try {
            _d.setLong("adate", 0);
            fail("conversion from long to Duration is not specified.");
        } catch (RuntimeException ex) { //$JL-EXC$
            // expected
        }
    }

    /*
     * Test method for 'com.sap.sdo.impl.types.simple.AbstractDTSimpleType.convertFromJavaClass(Object)'
     */
    @Test
    public void testConvertFromJavaClass() {
        String ds = "--01";
        _b.setAdate(ds);
        assertEquals("adate should have changed", ds, _d.getString("adate"));

        // unable conversion
        try {
            _d.getBigInteger("adate");
            fail("ClassCastException expected");
        } catch (ClassCastException cce) { //$JL-EXC$
            // expected
        }

    }

    @Test
    public void testXmlParsing() {
        String xml = "<element" +
                            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                        "<gMonth xsi:type=\"xsd:gMonth\">--12-01:00</gMonth>" +
                     "</element>";

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);
        assertEquals("element", doc.getRootElementName());
        assertTrue(
            "expected default namespace, but uri was: " + doc.getRootElementURI(),
            doc.getRootElementURI() == null || "".equals(doc.getRootElementURI()));
        DataObject element = doc.getRootObject();
        assertNotNull(element);

        System.out.println(element.get("gMonth"));
    }
}
