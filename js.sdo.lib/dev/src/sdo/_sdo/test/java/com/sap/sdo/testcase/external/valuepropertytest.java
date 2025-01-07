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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class ValuePropertyTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ValuePropertyTest(String pContextId, Feature pFeature) {
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
    public void testValueProperty() throws Exception {
        String xml = "<element " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
                "xsi:type=\"xs:string\">42</element>";
        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);
        compareReaders(doc);
        assertEquals("element", doc.getRootElementName());
        String rootElementURI = doc.getRootElementURI();
        if (rootElementURI != null) {
            assertEquals("", rootElementURI);
        }

        DataObject wrapper = doc.getRootObject();
        assertNotNull(wrapper);

        // first we get a property with name 'value'
        DataObject valueProp = (DataObject)wrapper.getInstanceProperty(TypeConstants.VALUE);
        assertNotNull(valueProp);

        // if it is a simple content property it would be flagged as 'simple content'.
        // the flag is a open content property
        Property simpleContentFlag =
            _helperContext.getTypeHelper().getOpenContentProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_SIMPLE_CONTENT);
        assertNotNull(simpleContentFlag);

        // now we could check if the property is really a value-property
        assertEquals(true, valueProp.getBoolean(simpleContentFlag));

        // just to prove
        assertEquals("42", wrapper.getString((Property)valueProp));
    }

    @Test
    public void testSimpleTypeCreation() throws Exception {
        DataObject l_data = _helperContext.getDataFactory().create("commonj.sdo", "String");
        l_data.set("value", "foo");
        //System.out.println(_helperContext.getXMLHelper().save(l_data, "sap.com/test", "root"));

        // first we get a property with name 'value'
        DataObject valueProp = (DataObject)l_data.getInstanceProperty(TypeConstants.VALUE);
        assertNotNull(valueProp);

        // if it is a simple content property it would be flagged as 'simple content'.
        // the flag is a open content property
        Property simpleContentFlag =
            _helperContext.getTypeHelper().getOpenContentProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_SIMPLE_CONTENT);
        assertNotNull(simpleContentFlag);

        // now we could check if the property is really a value-property
        assertEquals(true, valueProp.getBoolean(simpleContentFlag));

        // just to prove
        assertEquals("foo", l_data.get((Property)valueProp));
    }

    @Test
    public void testDelayedSimpleTypeCreation() throws Exception {
        DataObject l_dataProperty = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        l_dataProperty.set(PropertyConstants.TYPE_STR, _helperContext.getTypeHelper().getType(URINamePair.STRING.getURI(), URINamePair.STRING.getName()));
        l_dataProperty.set(PropertyConstants.NAME_STR, "value");
        _helperContext.getTypeHelper().defineOpenContentProperty(null, l_dataProperty);

        DataObject l_data = _helperContext.getDataFactory().create("commonj.sdo", "String");
        l_data.set("value", "foo");
        //System.out.println(_helperContext.getXMLHelper().save(l_data, "sap.com/test", "root"));

        // first we get a property with name 'value'
        DataObject valueProp = (DataObject)l_data.getInstanceProperty(TypeConstants.VALUE);
        assertNotNull(valueProp);

        // if it is a simple content property it would be flagged as 'simple content'.
        // the flag is a open content property
        Property simpleContentFlag =
            _helperContext.getTypeHelper().getOpenContentProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_SIMPLE_CONTENT);
        assertNotNull(simpleContentFlag);

        // now we could check if the property is really a value-property
        assertEquals(true, valueProp.getBoolean(simpleContentFlag));

        // just to prove
        assertEquals("foo", l_data.get((Property)valueProp));
    }

    @Test
    public void testAcceleratedSimpleTypeCreation() throws Exception {
        DataObject l_data = _helperContext.getDataFactory().create("commonj.sdo", "String");
        l_data.set("value", "foo");

        DataObject l_dataProperty = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        l_dataProperty.set(PropertyConstants.TYPE_STR, _helperContext.getTypeHelper().getType(URINamePair.STRING.getURI(), URINamePair.STRING.getName()));
        l_dataProperty.set(PropertyConstants.NAME_STR, "value");
        Property valueProp = _helperContext.getTypeHelper().defineOpenContentProperty(null, l_dataProperty);
        assertNotNull(valueProp);

        // if it is a simple content property it would be flagged as 'simple content'.
        // the flag is a open content property
        Property simpleContentFlag =
            _helperContext.getTypeHelper().getOpenContentProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_SIMPLE_CONTENT);
        assertNotNull(simpleContentFlag);

        // now we could check if the property is really a value-property
        assertEquals(false, ((DataObject)valueProp).getBoolean(simpleContentFlag));
    }

    @Test
    public void testNilableTypeCreation() throws Exception {
        DataObject l_dataPropertyValue = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        l_dataPropertyValue.set(PropertyConstants.TYPE_STR, _helperContext.getTypeHelper().getType(URINamePair.STRING.getURI(), URINamePair.STRING.getName()));
        l_dataPropertyValue.set(PropertyConstants.NAME_STR, "value");
        l_dataPropertyValue.set(PropertyConstants.NULLABLE_STR, false);
        Property simpleContentFlag =
            _helperContext.getTypeHelper().getOpenContentProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_SIMPLE_CONTENT);
        l_dataPropertyValue.set(simpleContentFlag, true);
        Property l_propertyValue = _helperContext.getTypeHelper().defineOpenContentProperty(null, l_dataPropertyValue);

        DataObject l_data = _helperContext.getDataFactory().create("commonj.sdo", "String");
        assertEquals(true, l_data.getInstanceProperty("value").isNullable());
        l_data.set("value", "foobar");
        assertEquals("foobar", l_data.get("value"));
        assertEquals(true, l_data.getInstanceProperty("value").isNullable());
    }

    @Test
    public void testNonNilableTypeCreation() throws Exception {
        DataObject l_data = _helperContext.getDataFactory().create("commonj.sdo", "Int");
        assertEquals(0, l_data.get("value"));
        assertEquals(false, l_data.getInstanceProperty("value").isNullable());
    }
}
