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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
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
public class PropertyRenamingTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public PropertyRenamingTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String FILE_NAME =
        "com/sap/sdo/testcase/schemas/propertyRenaming.xml";

    @Test
    public void testReadXml() throws IOException {
        URL url = getClass().getClassLoader().getResource(FILE_NAME);
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);

        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(document, stringWriter, null);
        System.out.println(stringWriter.toString());

        DataObject root = document.getRootObject();

        Property rootRefElement = root.getInstanceProperty("refElement");
        assertEquals(false, rootRefElement.isOpenContent());
        assertEquals(false, rootRefElement.isMany());

        Type typeA = _helperContext.getTypeHelper().getType("com.sap.test", "typeA");
        assertEquals(typeA, rootRefElement.getType());
        assertEquals(rootRefElement, typeA.getProperty("refElement"));

        Property rootLocalElement = root.getInstanceProperty("localElement");
        assertEquals(false, rootLocalElement.isOpenContent());
        assertEquals(false, rootLocalElement.isMany());

        Type typeString = _helperContext.getTypeHelper().getType("commonj.sdo", "String");
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

        Type typeInt = _helperContext.getTypeHelper().getType("commonj.sdo", "Int");
        assertEquals(aValueLocalAttribute, typeA.getProperty("localAttribute"));
        assertEquals(typeInt, aValueLocalAttribute.getType());

        assertEquals(5, aValue.getInt("localAttribute"));
        assertEquals(Integer.valueOf(5), aValue.get(aValueLocalAttribute));

        assertEquals("more text", aValue.getString("localElement"));
    }

}
