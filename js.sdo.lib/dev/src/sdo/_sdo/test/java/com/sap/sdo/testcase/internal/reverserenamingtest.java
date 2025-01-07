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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.NamingHandler;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenInterface;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class ReverseRenamingTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ReverseRenamingTest(String pContextId, Feature pFeature) {
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
     * Test method for {@link com.sap.sdo.impl.xml.XmlStaxReader.DefaultRenamingHandler#getAliasName(java.lang.String, commonj.sdo.DataObject)}.
     */
    @Test
    public void testGetAliasName() throws Exception {
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.setString("x", "name");

        data.setString("foo", "bar");

        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyConstants.NAME, "attribute");
        propObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "Int"));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        data.set(prop, 42);

        String xml = _helperContext.getXMLHelper().save(data, null, "data");
        System.out.println(xml);

        Map<?,?> options =
            Collections.singletonMap(
                SapXmlHelper.OPTION_KEY_NAMING_HANDLER,
                new NamingHandler() {

                    public void nameOpenContentProperty(DataObject propObj, String uri, String localName, boolean isElement, DataObject parent) {
                        propObj.set(PropertyConstants.NAME, localName);
                        if (parent != null) {
                            assertEquals("OpenInterface", parent.getType().getName());
                            if (isElement) {
                                assertEquals("foo", localName);
                                propObj.getList(PropertyConstants.ALIAS_NAME).add("alias");
                            } else {
                                assertEquals("attribute", localName);
                                propObj.getList(PropertyConstants.ALIAS_NAME).add("attrAlias");
                            }
                        } else {
                            assertEquals("data", localName);
                        }
                    }

                });

        DataObject loaded =
            _helperContext.getXMLHelper().load(new StringReader(xml), null, options).getRootObject();
        assertEquals("name", loaded.getString("x"));
        assertEquals("bar", loaded.getString("foo"));
        assertEquals("bar", loaded.getString("alias"));
        assertEquals(42, loaded.getInt("attribute"));
        assertEquals(42, loaded.getInt("attrAlias"));
    }

    @Test
    public void testElementAlias() throws Exception {
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.setString("x", "name");

        data.setString("foo", "bar");

        String xml = _helperContext.getXMLHelper().save(data, null, "data");
        System.out.println(xml);

        Map<?,?> options =
            Collections.singletonMap(
                SapXmlHelper.OPTION_KEY_NAMING_HANDLER,
                new NamingHandler() {

                    public void nameOpenContentProperty(DataObject propObj, String uri, String localName, boolean isElement, DataObject parent) {
                        propObj.set(PropertyConstants.NAME, localName);
                        if (parent != null) {
                            assertEquals("OpenInterface", parent.getType().getName());
                            if (isElement) {
                                assertEquals("foo", localName);
                                propObj.getList(PropertyConstants.ALIAS_NAME).add("x");
                            }
                        } else {
                            assertEquals("data", localName);
                        }
                    }

                });

        DataObject loaded =
            _helperContext.getXMLHelper().load(new StringReader(xml), null, options).getRootObject();
        assertEquals("name", loaded.getString("x"));
        assertEquals("bar", loaded.getString("foo"));
        assertEquals("name", loaded.getString("x"));
        List<String> aliasNames = loaded.getInstanceProperty("foo").getAliasNames();
        assertNotNull(aliasNames);
        assertEquals(1, aliasNames.size());
        assertEquals("x", aliasNames.get(0));
    }

    @Test
    public void testAttributeAlias() throws Exception {
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.setString("x", "name");

        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyConstants.NAME, "attribute");
        propObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "Int"));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        data.set(prop, 42);

        String xml = _helperContext.getXMLHelper().save(data, null, "data");
        System.out.println(xml);

        Map<?,?> options =
            Collections.singletonMap(
                SapXmlHelper.OPTION_KEY_NAMING_HANDLER,
                new NamingHandler() {

                    public void nameOpenContentProperty(DataObject propObj, String uri, String localName, boolean isElement, DataObject parent) {
                        propObj.set(PropertyConstants.NAME, localName);
                        if (parent != null) {
                            assertEquals("OpenInterface", parent.getType().getName());
                            if (!isElement) {
                                assertEquals("attribute", localName);
                                propObj.getList(PropertyConstants.ALIAS_NAME).add("x");
                            }
                        } else {
                            assertEquals("data", localName);
                        }
                    }

                });

        DataObject loaded =
            _helperContext.getXMLHelper().load(new StringReader(xml), null, options).getRootObject();
        assertEquals("name", loaded.getString("x"));
        assertEquals(42, loaded.getInt("attribute"));
        assertEquals("name", loaded.getString("x"));
        List<String> aliasNames = loaded.getInstanceProperty("attribute").getAliasNames();
        assertNotNull(aliasNames);
        assertEquals(1, aliasNames.size());
        assertEquals("x", aliasNames.get(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XmlStaxReader.DefaultRenamingHandler#getAliasName(java.lang.String, commonj.sdo.DataObject)}.
     */
    @Test
    public void testXmlName() throws Exception {
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.setString("x", "name");

        data.setString("foo", "bar");

        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyConstants.NAME, "attribute");
        propObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "Int"));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        data.set(prop, 42);

        String xml = _helperContext.getXMLHelper().save(data, null, "data");
        System.out.println(xml);

        Map<?,?> options =
            Collections.singletonMap(
                SapXmlHelper.OPTION_KEY_NAMING_HANDLER,
                new NamingHandler() {

                    public void nameOpenContentProperty(DataObject propObj, String uri, String localName, boolean isElement, DataObject parent) {
                        propObj.set(PropertyType.getXmlNameProperty(), localName);
                        if (parent != null) {
                            assertEquals("OpenInterface", parent.getType().getName());
                            if (isElement) {
                                assertEquals("foo", localName);
                                propObj.set(PropertyConstants.NAME, localName+"Element");
                                propObj.getList(PropertyConstants.ALIAS_NAME).add("alias");
                            } else {
                                assertEquals("attribute", localName);
                                propObj.set(PropertyConstants.NAME, localName+"Attribute");
                                propObj.getList(PropertyConstants.ALIAS_NAME).add("attrAlias");
                            }
                        } else {
                            assertEquals("data", localName);
                            propObj.set(PropertyConstants.NAME, localName);
                        }
                    }

                });

        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(new StringReader(xml), null, options);
        DataObject loaded = xmlDocument.getRootObject();

        assertEquals("name", loaded.getString("x"));
        assertEquals("bar", loaded.getString("fooElement"));
        assertEquals("bar", loaded.getString("alias"));
        assertEquals(42, loaded.getInt("attributeAttribute"));
        assertEquals(42, loaded.getInt("attrAlias"));
        SapXsdHelper xsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        assertEquals("attribute", xsdHelper.getLocalName(loaded.getInstanceProperty("attributeAttribute")));
        assertEquals("foo", xsdHelper.getLocalName(loaded.getInstanceProperty("fooElement")));

//TODO uncomment this later!
//        assertSame(loaded.getInstanceProperty("attributeAttribute"), xsdHelper.getInstanceProperty(loaded, null, "attribute", false));
//        assertSame(loaded.getInstanceProperty("fooElement"), xsdHelper.getInstanceProperty(loaded, null, "foo", true));

        StringWriter xml2 = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument, xml2, null);
        assertEquals(xml, xml2.toString());
    }

}
