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


import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SoapEncodingTest extends SdoTestCase {

    /**
     * @param pContextId
     * @param pFeature
     */
    public SoapEncodingTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBase64() throws Exception {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "soap11encoding.xsd");
        _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());

        xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "soapencodingtest.xsd");
        _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());

        Type base64Type = _helperContext.getTypeHelper().getType("http://schemas.xmlsoap.org/soap/encoding/", "base64");
        assertNotNull(base64Type);

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "soapencoding.xml");
        XMLDocument document = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        Property picture = root.getInstanceProperty("picture");
        assertNotNull(picture);
        SdoType<?> pictType = (SdoType<?>)picture.getType();
//        assertEquals(base64Type.getURI(), pictType.getURI());
//        assertEquals(base64Type.getName(), pictType.getName());
//        byte[] x = (byte[])root.get(picture);
//        System.out.println(Base64Util.encodeBase64(x));

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(document, writer, null);

        System.out.println(writer.toString());
    }
}
