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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class SdoNameTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public SdoNameTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

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
        _helperContext = null;
    }

    @Test
    public void testTypeWithSdoName() {
        DataObject typeObj = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObj.set(TypeType.NAME, "SdoNameTestType");
        typeObj.set(TypeType.URI, "com.sap.sdo.testcase");
        typeObj.setBoolean(TypeType.OPEN, true);

        DataObject attrObj = typeObj.createDataObject("property");
        attrObj.set(PropertyType.NAME, "attr");
        attrObj.set(PropertyType.TYPE, JavaSimpleType.STRING);
        attrObj.set(PropertyType.getXmlNameProperty(), "xmlAttr");
        attrObj.set(PropertyType.getXmlElementProperty(), false);

        DataObject propObj = typeObj.createDataObject("property");
        propObj.set(PropertyType.NAME, "prop");
        propObj.set(PropertyType.TYPE, JavaSimpleType.STRING);
        propObj.set(PropertyType.getXmlNameProperty(), "xmlProp");

        Type type = _helperContext.getTypeHelper().define(typeObj);

        DataObject rootPropObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        rootPropObj.set(PropertyType.NAME, "rootElement");
        rootPropObj.set(PropertyType.TYPE, type);
        rootPropObj.setBoolean(PropertyType.CONTAINMENT, true);
        rootPropObj.set(PropertyType.getXmlNameProperty(), "xmlRoot");

        Property rootProperty = _helperContext.getTypeHelper().defineOpenContentProperty("com.sap.sdo.testcase", rootPropObj);

        DataObject globalPropObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        globalPropObj.set(PropertyType.NAME, "globalProp");
        globalPropObj.set(PropertyType.TYPE, JavaSimpleType.STRING);
        globalPropObj.set(PropertyType.getXmlNameProperty(), "xmlGlobalProp");

        Property globalProperty = _helperContext.getTypeHelper().defineOpenContentProperty("com.sap.sdo.testcase", globalPropObj);

        System.out.println(_helperContext.getXSDHelper().generate(Collections.singletonList(type)));

        DataObject graph = _helperContext.getDataFactory().create(DataGraphType.getInstance());
        DataObject data = ((DataGraph)graph).createRootObject(type);
        data.set("prop", "test");
        data.set("attr", "attribute");
        data.set(globalProperty, "global");

        ChangeSummary cs = graph.getChangeSummary();
        cs.beginLogging();

        data.set("prop", "property");
        data.set("attr", "a string");
        data.set("globalProp", "changed global");

        final String xml = _helperContext.getXMLHelper().save(graph, null, "graph");
        System.out.println(xml);

        DataGraph dataGraph = (DataGraph)_helperContext.getXMLHelper().load(xml).getRootObject();
        DataObject root = dataGraph.getRootObject();
        Property attribute = root.getInstanceProperty("attr");
        assertEquals("attr", attribute.getName());
        assertEquals(JavaSimpleType.STRING, attribute.getType());
        assertEquals("xmlAttr", ((SdoProperty)attribute).getXmlName());
        assertFalse(((SdoProperty)attribute).isXmlElement());
        Property property = root.getInstanceProperty("prop");
        assertEquals("prop", property.getName());
        assertEquals(JavaSimpleType.STRING, property.getType());
        assertEquals("xmlProp", ((SdoProperty)property).getXmlName());
        Property global = root.getInstanceProperty("globalProp");
        assertEquals("globalProp", global.getName());
        assertEquals(JavaSimpleType.STRING, global.getType());
        assertEquals("xmlGlobalProp", ((SdoProperty)global).getXmlName());

        assertEquals("property", root.get("prop"));
        assertEquals("a string", root.get("attr"));
        assertEquals("changed global", root.get("globalProp"));

        dataGraph.getChangeSummary().undoChanges();

        assertEquals("test", root.get("prop"));
        assertEquals("attribute", root.get("attr"));
        assertEquals("global", root.get("globalProp"));

        System.out.println(root);
    }

    @Test
    public void testNameClash() {
        DataObject typeObj = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObj.set(TypeType.NAME, "NameClashTestType");
        typeObj.set(TypeType.URI, "com.sap.sdo.testcase");

        DataObject attrObj = typeObj.createDataObject("property");
        attrObj.set(PropertyType.NAME, "prop");
        attrObj.set(PropertyType.TYPE, JavaSimpleType.STRING);
        attrObj.set(PropertyType.getXmlElementProperty(), false);

        DataObject propObj = typeObj.createDataObject("property");
        propObj.set(PropertyType.NAME, "prop");
        propObj.set(PropertyType.TYPE, JavaSimpleType.STRING);

        Type type = _helperContext.getTypeHelper().define(typeObj);

        String xsd = _helperContext.getXSDHelper().generate(Collections.singletonList(type));
        assertNotNull(xsd);
        System.out.println(xsd);

        xsd = xsd.replace("com.sap.sdo.testcase", "com.sap.sdo.testcase2");

        List<Type> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(1, types.size());
        Type loaded = types.get(0);
        assertEquals("NameClashTestType", loaded.getName());
        assertEquals("com.sap.sdo.testcase2", loaded.getURI());
        List<Property> props = loaded.getProperties();
        assertNotNull(props);
        assertEquals(2, props.size());
        Set<Boolean> possibleValues =
            new HashSet<Boolean>(
                    Arrays.asList(
                        new Boolean[]{Boolean.TRUE, Boolean.FALSE}));
        for (Property property : props) {
            assertEquals("prop", property.getName());
            assertEquals(JavaSimpleType.STRING, property.getType());
            final Boolean xmlElement = ((SdoProperty)property).isXmlElement();
            assertTrue(possibleValues.contains(xmlElement));
            possibleValues.remove(xmlElement);
        }

        DataObject data = _helperContext.getDataFactory().create(type);
        data.set("prop", "test");
        data.set("@prop", "testie");
        System.out.println(_helperContext.getXMLHelper().save(data, null, "data"));

        List<Property> properties = data.getInstanceProperties();
        assertNotNull(properties);
        assertEquals(2, properties.size());
        int i = 0;
        for (Property property : properties) {
            data.set(property, "test" + i++);
        }
        String xml = _helperContext.getXMLHelper().save(data, null, "data");
        System.out.println(xml);

        DataObject reloaded = _helperContext.getXMLHelper().load(xml).getRootObject();
        System.out.println(_helperContext.getXMLHelper().save(reloaded, null, "data"));

        DataObject graphObj = _helperContext.getDataFactory().create(DataGraphType.getInstance());
        DataGraph graph = ((DataGraph)graphObj);
        DataObject root = graph.createRootObject(type);
        ChangeSummary cs = graph.getChangeSummary();

        root.set("@prop", "a1");
        root.set("prop", "p1");

        cs.beginLogging();

        root.set("@prop", "a2");
        root.set("prop", "p2");

        String savedGraph = _helperContext.getXMLHelper().save(graphObj, null, "graph");
        System.out.println(savedGraph);

        assertEquals(
            savedGraph,
            _helperContext.getXMLHelper().save(
                _helperContext.getXMLHelper().load(savedGraph).getRootObject(),
                null,
                "graph"));
    }
}
