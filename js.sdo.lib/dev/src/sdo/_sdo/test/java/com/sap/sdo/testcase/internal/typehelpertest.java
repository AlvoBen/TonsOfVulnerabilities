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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import com.example.sca.Component;
import com.example.sca.ComponentType;
import com.example.sca.Interface;
import com.example.sca.JavaInterface;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenContent;

import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class TypeHelperTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public TypeHelperTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    @Test
    public void testURINamePair() {
        assertTrue("equality on " + URINamePair.STRING + " failed",
                URINamePair.STRING.equals(new URINamePair("commonj.sdo",
                        "String")));
    }
	@Test
    public void testAPIwithSchema() throws Exception {
        URL schemaUrl = getClass().getClassLoader().getResource(PACKAGE+"sca/sca.xsd");
        _helperContext.getXSDHelper().define(schemaUrl.openStream(), schemaUrl.toString());
		Type componentTypeType = _helperContext.getTypeHelper().getType(ComponentType.class);
		assertEquals(componentTypeType.getInstanceClass(), ComponentType.class);
		Type componentType = _helperContext.getTypeHelper().getType(Component.class);
		Type javaInterfaceType =_helperContext.getTypeHelper().getType(JavaInterface.class);
        URL url = getClass().getClassLoader().getResource(PACKAGE+"sca/Accounts.componentType");
        InputStream is = url.openStream();
        XMLDocument doc = _helperContext.getXMLHelper().load(is,null,null);
        assertSame(componentTypeType,doc.getRootObject().getType());
		ComponentType cType = (ComponentType)doc.getRootObject();
		Interface serviceInterface = cType.getService().get(0).getInterface();
		assertTrue(serviceInterface instanceof JavaInterface);
	}

    @Test
    public void testGetTypeWithClass() throws Exception {
        String typeName = "OpenContent";
        String typeUri = "com.sap.sdo.testcase.typefac";

        // could not test if type was available, otherwise it would call init
        //Type openContent = _helperContext.getTypeHelper().getType(typeUri, typeName);
        //assertEquals(null, openContent);

        Type openContent = _helperContext.getTypeHelper().getType(OpenContent.class);

        assertNotNull(openContent);
        assertEquals(typeName, openContent.getName());
        assertEquals(typeUri, openContent.getURI());
    }
}
