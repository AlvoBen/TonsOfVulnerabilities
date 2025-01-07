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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.xi.Envelope;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class XiMessage30Test extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XiMessage30Test(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String XI_PACKAGE = PACKAGE + "xi/";

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
    public void testXiMessage() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(XI_PACKAGE + "xiMessage30.xsd");
        List<?> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(84, types.size());

        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("C:/test/xiMessage30");
        generator.generate((List)types);
    }

    @Test
    public void testPerformTestAndGetObject() throws Exception {
        URL url = getClass().getClassLoader().getResource(XI_PACKAGE + "outCopy.txt");
        DataObject data = createSDO(url.openStream(), getClass().getClassLoader().getResource(XI_PACKAGE + "xiMessage30.xsd"));
        assertNotNull(data);
        assertTrue(data instanceof Envelope);
        Envelope env = (Envelope)data;
        assertNotNull(env.getHeader());
        assertNotNull(env.getHeader().getMain());
        System.out.println(env.getHeader().getMain());
    }

    private DataObject createSDO(InputStream xmlStream, URL xsdUrl) throws IOException {
        if (defineSchema(xsdUrl)) {
            //setGlobalElement();
            XMLDocument doc = _helperContext.getXMLHelper().load(xmlStream);
            return doc.getRootObject();
        } else {
            return null;
        }
    }

    private boolean defineSchema(URL schema) throws IOException {
        _helperContext.getTypeHelper().getType(Envelope.class);

        List<?> types = _helperContext.getXSDHelper().define(schema.openStream(), schema.toString());

        //---------------------------
        DataObject propObj = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObj.set(PropertyConstants.NAME, "Envelope");
        propObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType(((Type) types.get(33)).getURI(), ((Type) types.get(33)).getName()));
        Property value = _helperContext.getTypeHelper().defineOpenContentProperty("http://schemas.xmlsoap.org/soap/envelope/", propObj);
        //---------------------------

        if (!(types == null)) {
            return true;
        } else {
            return false;
        }
    }
}
