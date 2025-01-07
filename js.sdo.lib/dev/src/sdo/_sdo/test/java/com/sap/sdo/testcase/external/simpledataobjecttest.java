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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.IPolymorphicProperty;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.OpenSequencedInterface;
import com.sap.sdo.testcase.typefac.OppositePropsA;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleExtension;
import com.sap.sdo.testcase.typefac.SimpleIntf1;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;

public class SimpleDataObjectTest extends SdoTestCase {
	/**
     * @param pHelperContext
     */
    public SimpleDataObjectTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    @Test
    public void testPropertyIsAnAttribute() throws Exception {
        logger.info("testPropertyIsAnAttribute");
        DataObject d = _helperContext.getDataFactory().create(SimpleAttrIntf.class);
        assertTrue("name should be an attribute",!((DataObject)d.getType().getProperty("name")).getBoolean("xmlElement"));
        ((SimpleAttrIntf)d).setName("nameAttr");
        for (int i=0; i<d.getSequence().size(); i++) {
        	if (d.getSequence().getProperty(i).getName().equals("name")) {
        		fail("name is an attribute and should not be in the sequence");
        	}
        }
	}
	@Test
    public void testInheritence() throws Exception {
        logger.info("testInheritence");
        DataObject d = _helperContext.getDataFactory().create(SimpleExtension.class);
        assertTrue("name should be an attribute",!((DataObject)d.getType().getProperty("name")).getBoolean("xmlElement"));
        ((SimpleAttrIntf)d).setName("nameAttr");
        assertEquals("nameAttr",d.get("name"));
        ((SimpleExtension)d).setSubProp("subProp");
        assertEquals("subProp",d.get("subProp"));
	}
	@Test
    public void testPolymorphicProperty() throws Exception {
        DataObject d = _helperContext.getDataFactory().create(IPolymorphicProperty.class);
        DataObject subclass = _helperContext.getDataFactory().create(SimpleExtension.class);
        ((IPolymorphicProperty)d).setA((SimpleExtension)subclass);
        assertEquals(subclass,d.get("a"));
	}
    @Test
    public void testSimpleTypeDO() throws Exception {
        logger.info("testSimpleTypeDO");
        DataObject d = _helperContext.getDataFactory().create(SimpleIntf1.class);
        assertTrue("name should be an xml element", !((DataObject)d.getType().getProperty("name")).isSet("xmlElement") || ((DataObject)d.getType().getProperty("name")).getBoolean("xmlElement"));
        // check default...
        Object v = d.get("id");
        assertEquals(SimpleIntf1.DEFAULT_ID, v);

        // boolean readOnlyFailed = false;
        // try
        // {
        // d.set("id","test");
        // }
        // catch (IllegalArgumentException e)
        // {
        // readOnlyFailed = true;
        // }
        // assertTrue("no exception when trying to set read-only property
        // id",readOnlyFailed);
        d.set("name", "NAME");
        v = d.get("name");
        assertTrue("result for \"name\" must be string",
                v instanceof String);
        assertTrue("result should equal \"NAME\"", "NAME".equals(v));
        assertTrue("data object sequence should have size 1", d
                .getSequence().size() == 1);
        d.unset("name");
        v = d.get("name");
        assertTrue("result for \"name\" should be null but is " + v,
                v == null);
        assertTrue("data object sequence should have size 0", d
                .getSequence().size() == 0);
        d.set("data", "DATA");
        v = d.get("data");
        assertTrue("result for \"data\" must be string",
                v instanceof String);
        assertTrue("result should equal \"DATA\"", "DATA".equals(v));
        assertTrue("data object sequence should have size 1", d
                .getSequence().size() == 1);
        d.unset("data");
        v = d.get("data");
        assertTrue("result for \"data\" must be string",
                v instanceof String);
        assertTrue("result should equal \"" + SimpleIntf1.DEFAULT_DATA
                + "\"", SimpleIntf1.DEFAULT_DATA.equals(v));
        assertTrue("data object sequence should have size 0", d
                .getSequence().size() == 0);
        d.set("name", "NAME");
        d.set("data", "DATA");
        assertTrue("data object sequence should have size 2", d
                .getSequence().size() == 2);
    }

    @Test
    public void testSimpleListAccess() throws Exception {
        //Spec. page 28
        //Mixing single-valued and multi-valued Property access
        logger.info("testSimpleListAccess");
        DataObject d = _helperContext.getDataFactory().create(SimpleIntf1.class);
        try {
            Object l = d.getList("name");
            fail("ClassCastException expected for a single value property");
        } catch (ClassCastException ex) { //$JL-EXC$
            //expected
        }
        try {
            ((SimpleIntf1)d).setName("aName");
            Object l = d.getList("name");
            fail("ClassCastException expected for a single value property");
        } catch (ClassCastException ex) { //$JL-EXC$
            //expected
        }
    }

    @Test
    public void testSimpleTypeDONested() throws Exception {
        logger.info("testSimpleTypeDONested");
        DataObject d = _helperContext.getDataFactory().create(SimpleContainingIntf.class);
        d.createDataObject("inner");
        DataObject i = d.getDataObject("inner");
        assertTrue(
                "lookup after creation of nested data object returned null",
                i != null);
        Type ti = _helperContext.getTypeHelper().getType(SimpleContainedIntf.class);
        assertTrue("type of contained data object incorrect"
                + i.getType(), ti.equals(i.getType()));
        d.set("inner/name", "NAME");
        Object v = d.get("inner/name");
        assertTrue("result for \"inner/name\" must be string",
                v instanceof String);
        assertTrue("result should equal \"NAME\"", "NAME".equals(v));
    }
    @Test
    public void testOppositeProperties() {
    	logger.info("testOppositeProperties");
    	Type t = _helperContext.getTypeHelper().getType(OppositePropsA.class);
    	Property p = t.getProperty("bs");
    	assertNotNull(p.getOpposite());
    	assertEquals(p.getOpposite().getName(),"a");
    	assertEquals(p.getOpposite().getOpposite(),p);
    }
    @Test
    public void testDataObjectFromInterfaceType() {
        logger.info("testDataObjectFromInterfaceType");
        DataObject d = _helperContext.getDataFactory().create(SimpleIntf1.class);
        assertTrue("data object must implement interface",
                d instanceof SimpleIntf1);
        SimpleIntf1 e = (SimpleIntf1)d;
        // TODO:
        //assertTrue("result should equal \"" + SimpleIntf1.DEFAULT_ID
        //        + "\"", SimpleIntf1.DEFAULT_ID.equals(e.getId()));

        e.setName("NAME");
        Object v = d.get("name");
        assertTrue("result for \"name\" must be string",
                v instanceof String);
        assertTrue("result should equal \"NAME\"", "NAME".equals(v));
        assertTrue("data object sequence should have size 1", d
                .getSequence().size() == 1);
        assertTrue("typed and dynamic lookup should be identical", v
                .equals(e.getName()));
        d.unset("name");
        v = e.getName();
        assertTrue("result for \"name\" should be null but is " + v,
                v == null);
        assertTrue("data object sequence should have size 0", d
                .getSequence().size() == 0);
        d.set("data", "DATA");
        v = e.getData();
        assertTrue("result for \"data\" must be string",
                v instanceof String);
        assertTrue("result should equal \"DATA\"", "DATA".equals(v));
        assertTrue("data object sequence should have size 1", d
                .getSequence().size() == 1);
        d.unset("data");
        v = e.getData();
        // TODO:
        //assertTrue("result for \"data\" must be string",
        //        v instanceof String);
        //assertTrue("result should equal \"" + SimpleIntf1.DEFAULT_DATA
        //        + "\"", SimpleIntf1.DEFAULT_DATA.equals(v));
        //assertTrue("data object sequence should have size 0", d
        //        .getSequence().size() == 0);
    }

    @Test
    public void testCreateOpenPropertiesOnDemand() {
        Type type = _helperContext.getTypeHelper().getType(OpenInterface.class);
        DataObject d1 = _helperContext.getDataFactory().create(type);
        DataObject d2 = _helperContext.getDataFactory().create(type);
        DataObject d3 = _helperContext.getDataFactory().create(type);
        DataObject d4 = _helperContext.getDataFactory().create(type);

        d1.set("single", d2);
        Property single = d1.getProperty("single");
        assertSame(type, single.getType());
        assertEquals(false, single.isMany());
        assertSame(d2, d1.getDataObject(single));

        List<DataObject> dataObjects = new ArrayList<DataObject>();
        dataObjects.add(d3);
        dataObjects.add(d4);
        d1.set("many", dataObjects);
        Property many = d1.getProperty("many");
        assertSame(type, many.getType());
        assertEquals(true, many.isMany());
        assertEquals(dataObjects, d1.getList(many));

        DataObject d5 = _helperContext.getDataFactory().create(SimpleAttrIntf.class);
        dataObjects.add(d5);
        try {
            d1.set("error", dataObjects);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            //expected
        }

        d1.unset(single);
        assertEquals(false, d1.getInstanceProperties().contains(single));
        assertEquals(false, d1.isSet(single));

        List manyValues = d1.getList(many);
        d1.unset(many);
        assertEquals(false, d1.isSet(many));
        assertEquals(false, d1.getInstanceProperties().contains(many));

        manyValues.add(d3);
        assertEquals(true, d1.isSet(many));
        assertEquals(true, d1.getInstanceProperties().contains(many));
    }

    @Test
    public void testCreateOpenPropertiesOnDemandSequenced() {
        Type type = _helperContext.getTypeHelper().getType(OpenSequencedInterface.class);
        DataObject d1 = _helperContext.getDataFactory().create(type);
        DataObject d2 = _helperContext.getDataFactory().create(type);
        DataObject d3 = _helperContext.getDataFactory().create(type);
        DataObject d4 = _helperContext.getDataFactory().create(type);

        d1.set("single", d2);
        Property single = d1.getInstanceProperty("single");
        assertSame(type, single.getType());
        assertEquals(false, single.isMany());
        assertSame(d2, d1.getDataObject(single));

        List<DataObject> dataObjects = new ArrayList<DataObject>();
        dataObjects.add(d3);
        dataObjects.add(d4);
        d1.set("many", dataObjects);
        Property many = d1.getInstanceProperty("many");
        assertSame(type, many.getType());
        assertEquals(true, many.isMany());
        assertEquals(dataObjects, d1.getList(many));

        DataObject d5 = _helperContext.getDataFactory().create(SimpleAttrIntf.class);
        dataObjects.add(d5);
        try {
            d1.set("error", dataObjects);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            //expected
        }

        d1.unset(single);
        assertEquals(false, d1.getInstanceProperties().contains(single));
        assertEquals(false, d1.isSet(single));

        List manyValues = d1.getList(many);
        d1.unset(many);
        assertEquals(false, d1.isSet(many));
        assertEquals(false, d1.getInstanceProperties().contains(many));

        manyValues.add(d3);
        assertEquals(true, d1.isSet(many));
        assertEquals(true, d1.getInstanceProperties().contains(many));
    }

    @Test
    public void testReplaceOpenPropertiesOnDemandSequenced() {
        Type type = _helperContext.getTypeHelper().getType(OpenSequencedInterface.class);
        DataObject d1 = _helperContext.getDataFactory().create(type);
        DataObject d2 = _helperContext.getDataFactory().create(type);
        DataObject d3 = _helperContext.getDataFactory().create(type);
        DataObject d4 = _helperContext.getDataFactory().create(type);

        d1.set("single", d2);
        Property single = d1.getInstanceProperty("single");
        assertSame(type, single.getType());
        assertEquals(true, single.isContainment());
        assertEquals(false, single.isMany());
        assertSame(d2, d1.getDataObject(single));

        assertSame(d1, d2.getContainer());
        assertSame(single, d2.getContainmentProperty());

        d1.set(single, d3);
        assertSame(null, d2.getContainer());
        assertSame(null, d2.getContainmentProperty());

        assertSame(d1, d3.getContainer());
        assertSame(single, d3.getContainmentProperty());

        assertSame(d3, d1.getDataObject(single));
    }

    @Test
    public void testCreateOpenPropertiesOnDemandAtSequence() {
        Type type = _helperContext.getTypeHelper().getType(OpenSequencedInterface.class);
        DataObject d1 = _helperContext.getDataFactory().create(type);
        Sequence sequence = d1.getSequence();
        sequence.add(0, "onDemand1", "value1");
        assertEquals(1, sequence.size());
        assertEquals("value1", sequence.getValue(0));
        sequence.add(0, "onDemand2", 2);
        assertEquals(2, sequence.size());
        assertEquals(2, sequence.getValue(0));
        sequence.add(2, "onDemand3", "three");
        assertEquals(3, sequence.size());
        assertEquals("three", sequence.getValue(2));
        sequence.add(0, "onDemand3", "zero");
        sequence.add(2, "onDemand1", "value0");
        sequence.add(5, "onDemand2", 5);

        assertEquals("zero", sequence.getValue(0));
        assertEquals(2, sequence.getValue(1));
        assertEquals("value0", sequence.getValue(2));
        assertEquals("value1", sequence.getValue(3));
        assertEquals("three", sequence.getValue(4));
        assertEquals(5, sequence.getValue(5));

        List onDemand1 = d1.getList("onDemand1");
        assertEquals(2, onDemand1.size());
        assertEquals("value0", onDemand1.get(0));
        assertEquals("value1", onDemand1.get(1));

        List onDemand2 = d1.getList("onDemand2");
        assertEquals(2, onDemand2.size());
        assertEquals(2, onDemand2.get(0));
        assertEquals(5, onDemand2.get(1));

        List onDemand3 = d1.getList("onDemand3");
        assertEquals(2, onDemand3.size());
        assertEquals("zero", onDemand3.get(0));
        assertEquals("three", onDemand3.get(1));
    }

    @Test
    public void testOpenContentSequenced() {
        DataObject dataObject = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        assertNotNull(dataObject);

        dataObject.setString("stringA", "A");
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        dataObject.setDataObject("data", data);

        Sequence sequence = dataObject.getSequence();
        assertNotNull(sequence);
        assertEquals(2, sequence.size());
        assertEquals("A", sequence.getValue(0));
        assertFalse(sequence.getProperty(0).isMany());
        assertSame(data, sequence.getValue(1));
        assertFalse(sequence.getProperty(1).isMany());

        sequence.add("multi", "first");
        assertTrue(sequence.getProperty(2).isMany());
        assertEquals("first", sequence.getValue(2));
        List<String> list = dataObject.getList("multi");
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("first", list.get(0));

        assertEquals(3, sequence.size());

        Property propInner = dataObject.getInstanceProperty("inner");
        DataObject inner = dataObject.createDataObject(propInner, propInner.getType());
        assertNotNull(inner);

        assertEquals(4, sequence.size());
        assertSame(inner, sequence.getValue(3));

        Property propMulti = dataObject.getInstanceProperty("multi");
        assertEquals(String.class, propMulti.getType().getInstanceClass());
        assertEquals(false, propMulti.isContainment());
        final List instanceProperties = dataObject.getInstanceProperties();
        int indexMulti = instanceProperties.indexOf(propMulti);

        sequence.add(indexMulti, (Object)"second");
        assertEquals(5, sequence.size());
        assertSame("second", sequence.getValue(4));

        assertEquals(2, list.size());
        assertEquals("second", list.get(1));

        Property propData = dataObject.getInstanceProperty("data");
        assertEquals(true, propData.isContainment());

        sequence.add("datas", data);

        Property propDatas = dataObject.getInstanceProperty("datas");
        assertEquals(false, propDatas.isContainment());
        assertSame(data, dataObject.get("datas[1]"));

        assertSame(data, dataObject.get("data"));

        Type typeDatas = propDatas.getType();
        dataObject.createDataObject(propDatas.getName(), typeDatas.getURI(), typeDatas.getName());
        assertNotNull(dataObject.get("datas[2]"));
        assertSame(typeDatas, dataObject.getDataObject("datas[2]").getType());

        dataObject.createDataObject(instanceProperties.indexOf(propDatas), typeDatas.getURI(), typeDatas.getName());
        assertNotNull(dataObject.get("datas[3]"));
        assertSame(typeDatas, dataObject.getDataObject("datas[3]").getType());
    }

    @Test
    public void testOpenContent() {
        DataObject dataObject = _helperContext.getDataFactory().create(OpenInterface.class);
        assertNotNull(dataObject);

        dataObject.set("a", "A");
        dataObject.setString("b", "B");
        dataObject.setDataObject("data", _helperContext.getDataFactory().create(OpenInterface.class));

        assertTrue(dataObject.getInstanceProperty("a").isOpenContent());
        assertTrue(dataObject.getInstanceProperty("b").isOpenContent());
        assertFalse(dataObject.getInstanceProperty("x").isOpenContent());
        assertTrue(dataObject.getInstanceProperty("data").isOpenContent());
        assertFalse(dataObject.getInstanceProperty("inner").isOpenContent());
    }

    @Test
    public void testOpenContentContainment() {
        DataObject dataObject = _helperContext.getDataFactory().create(OpenInterface.class);
        assertNotNull(dataObject);
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        assertNotNull(data);

        dataObject.setDataObject("containedProp", data);
        dataObject.setDataObject("referencedProp", data);
        assertSame(data, dataObject.getDataObject("containedProp"));
        assertSame(data, dataObject.getDataObject("referencedProp"));
        assertTrue(dataObject.getInstanceProperty("containedProp").isContainment());
        assertFalse(dataObject.getInstanceProperty("referencedProp").isContainment());
    }

    @Test
    public void testHiddenOpenContent() {
        DataObject dataObject = _helperContext.getDataFactory().create(OpenInterface.class);
        assertNotNull(dataObject);

        dataObject.setString("x", "defined");

        DataObject openProp = _helperContext.getDataFactory().create(PropertyType.getInstance());
        assertNotNull(openProp);
        openProp.set(PropertyType.NAME, "x");
        openProp.set(PropertyType.ALIAS_NAME, Arrays.asList(new String[]{"z"}));
        openProp.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType(String.class));

        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty("com.sap.sdo.test", openProp);

        dataObject.setString(prop, "open");

        assertEquals("defined", dataObject.getString("x"));
        assertEquals("open", dataObject.getString("z"));
    }

    @Test
    public void testInstanceClassOfDataObject() {
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        assertNotNull(data);
        Type type = data.getType();
        assertNotNull(type);
        assertEquals("OpenInterface", type.getName());
        assertSame(OpenInterface.class, type.getInstanceClass());

        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "Type");
        typeObject.set(TypeType.URI, "com.sap");
        DataObject prop = typeObject.createDataObject("property");
        prop.set(PropertyType.NAME, "name");
        prop.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType(String.class));
        DataObject prop2 = typeObject.createDataObject("property");
        prop2.set(PropertyType.NAME, "prop");
        prop2.set(PropertyType.CONTAINMENT, true);
        prop2.set(PropertyType.TYPE, typeObject);
        Type genType = _helperContext.getTypeHelper().define(typeObject);

        DataObject outer = _helperContext.getDataFactory().create(genType);
        assertNotNull(outer);
        outer.setString("name", "outer");
        type = outer.getType();
        assertNotNull(type);
        assertEquals("Type", type.getName());
        assertNull(type.getInstanceClass());

        DataObject inner = _helperContext.getDataFactory().create(genType);
        assertNotNull(inner);
        inner.setString("name", "inner");

        outer.setDataObject("prop", inner);
    }

    @Test
    public void testUnstructuredText() {
        DataObject dataObject = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        assertNotNull(dataObject);

        Sequence sequence = dataObject.getSequence();
        sequence.addText("text1");
        ((OpenSequencedInterface)dataObject).setX("X");
        sequence.addText("text2");

        assertEquals(3, sequence.size());
        assertEquals("text1", sequence.getValue(0));
        assertEquals("X", sequence.getValue(1));
        assertEquals("text2", sequence.getValue(2));
        assertNull(sequence.getProperty(0));
        assertSame(dataObject.getInstanceProperty("x"), sequence.getProperty(1));
        assertNull(sequence.getProperty(2));
    }

    private final static Logger logger = Logger
            .getLogger(SimpleDataObjectTest.class.getName());
}
