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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.NestedMVInner;
import com.sap.sdo.testcase.typefac.NestedMVOuter;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleMVIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class XPathAccessTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XPathAccessTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testOneLevelNav() {
        SimpleMVIntf i = (SimpleMVIntf)_helperContext.getDataFactory()
                .create(SimpleMVIntf.class);
        List<String> l = i.getNames();
        assertTrue("initially list should be of size 0", l.size() == 0);
        l.add("a");
        assertTrue("list should be of size 1", l.size() == 1);
        l.add("b");
        assertTrue("list should be of size 2", l.size() == 2);
        DataObject d = (DataObject)i;
        String s = d.getString("sdopath:names.0");
        assertTrue("path sdopath:names.0 resolved to " + s, "a".equals(s));
        s = d.getString("names[1]");
        assertTrue("path names[1] resolved to " + s, "a".equals(s));
        s = d.getString("SDOPath:names.1");
        assertTrue("path SDOPath:names.1 resolved to " + s, "b".equals(s));
        s = d.getString("names[2]");
        assertTrue("path names[2] resolved to " + s, "b".equals(s));
        s = d.getString("SdoPath:names.2");
        assertNull("path SdoPath:names.2 resolved to " + s, s);
        s = d.getString("names[3]");
        assertNull("path names[3] resolved to " + s, s);
    }

    @Test
    public void testNestedMultiValue() {
        DataObject d1 = _helperContext.getDataFactory().create(NestedMVOuter.class);
        DataObject d2 = d1.createDataObject("inner");
        assertTrue("inner should implement NexteMVInner",
                d2 instanceof NestedMVInner);
        DataObject d3 = d1.createDataObject("inner");
        assertTrue("inner should implement NexteMVInner",
                d3 instanceof NestedMVInner);
        assertTrue("inner list should have size 2",
                d1.getList("inner").size() == 2);
        Object o = d1.get("sdopath:inner.0");
        assertTrue("path sdopath:inner.0 resolved to " + o, o == d2);
        o = d1.get("sdopath:inner.1");
        assertTrue("path sdopath:inner.1 resolved to " + o, o == d3);
        List<String> l = d2.getList("mv");
        o = d1.get("sdopath:inner.0");
        o = d1.getList("inner");
        o = d1.getDataObject("sdopath:inner.0");
        o = d1.getList("sdopath:inner.0/mv");
        assertTrue("path sdopath:inner.0/mv resolved to " + o, o == l);
        l.add("d2_0");
        l.add("d2_1");
        o = d1.getList("sdopath:inner.0/mv");
        assertTrue("path sdopath:inner.0/mv resolved to " + o, o == l);
        assertTrue("list should be of size 2", l.size() == 2);
        assertTrue("path sdopath:inner.0/mv should be list of size 2",
                d1.getList("sdopath:inner.0/mv").size() == 2);
        o = d1.get("sdopath:inner.0/mv.1");
        assertTrue("path sdopath:inner.0/mv.1 resolved to " + o, "d2_1".equals(o));
        l = d3.getList("mv");
        l.add("d3_1");
        l.add("d3_2");
        l.add("d3_3");
        o = d1.get("sdopath:inner.1/mv.2");
        assertTrue("path sdopath:inner.1/mv should be list of size 3",
                d1.getList("sdopath:inner.1/mv").size() == 3);
        assertTrue("path sdopath:inner.1/mv.2 resolved to " + o, "d3_3"
                        .equals(o));
        // search
        d2.set("id", "d2");
        d3.set("id", "d3");
        o = d1.get("inner[id='d2']");
        assertTrue("path inner[id='d2'] resolved to " + o, o == d2);
        o = d1.get("inner[id='d3']");
        assertTrue("path inner[id='d3'] resolved to " + o, o == d3);
        o = d1.get("inner[id=\"d2\"]");
        assertTrue("path inner[id=\"d2\"] resolved to " + o, o == d2);
        o = d1.get("inner[id=\"d3\"]");
        assertTrue("path inner[id=\"d3\"] resolved to " + o, o == d3);
        o = d1.get("sdopath:inner.0/../inner.1");
        assertTrue("path sdopath:inner.0/../inner.1 resolved to " + o, o == d3);
        DataObject inner = (DataObject)d1.get("sdopath:inner.0");
        assertTrue("path sdopath:inner.0 resolved to " + inner, inner == d2);
        o = inner.get("../inner[id='d3']");
        assertTrue("path inner[id='d3'] resolved to " + o, o == d3);
        o = inner.get("/inner[id='d3']");
        assertTrue("path /inner[id='d3'] resolved to " + o, o == d3);
    }

    @Test
    public void testSetMultiValue() {
        SimpleMVIntf i = (SimpleMVIntf)_helperContext.getDataFactory().create(SimpleMVIntf.class);
        List<String> l = i.getNames();
        l.add("a");
        l.add("b");
        assertEquals(2, l.size());
        DataObject d = (DataObject)i;
        d.set("names[1]", "a1");
        assertEquals("a1", d.get("names[1]"));
        d.set("names[2]", "a2");
        assertEquals("a2", d.get("names[2]"));

        try {
            d.set("names[3]", "a3");
            fail("Exception expected");
        } catch (RuntimeException e) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testIsSetMultiValue() {
        SimpleMVIntf i = (SimpleMVIntf)_helperContext.getDataFactory().create(SimpleMVIntf.class);
        List<String> l = i.getNames();
        l.add("a");
        l.add("b");
        assertEquals(2, l.size());
        DataObject d = (DataObject)i;
        assertEquals(true, d.isSet("names[1]"));
        assertEquals(true, d.isSet("names[2]"));
        assertEquals(false, d.isSet("names[3]"));
        assertEquals(true, d.isSet("names.0"));
        assertEquals(true, d.isSet("names.1"));
        assertEquals(false, d.isSet("names.2"));

    }

    @Test
    public void testUnSetMultiValue() {
        SimpleMVIntf i = (SimpleMVIntf)_helperContext.getDataFactory().create(SimpleMVIntf.class);
        List<String> l = i.getNames();
        l.add("a");
        l.add("b");
        assertEquals(2, l.size());
        DataObject d = (DataObject)i;
        d.unset("names[1]");
        assertEquals(1, l.size());
        assertEquals("b", d.get("names[1]"));
        try {
            d.unset("names[99]");
            fail("Exception expected");
        } catch (RuntimeException e) { //$JL-EXC$
            // expected
        }

    }

    @Test
    public void testSchemeBehavior() {
        SimpleMVIntf i = (SimpleMVIntf)_helperContext.getDataFactory()
            .create(SimpleMVIntf.class);
        List<String> l = i.getNames();
        assertTrue("initially list should be of size 0", l.size() == 0);
        l.add("a");
        assertTrue("list should be of size 1", l.size() == 1);
        DataObject d = (DataObject)i;
        String s = d.getString("sdo:names[1]");
        assertTrue("path sdo:names[1] resolved to " + s, "a".equals(s));
        s = d.getString("sdo:/names[1]");
        assertTrue("path sdo:/names[1] resolved to " + s, "a".equals(s));
        s = d.getString("sdopath:names.0");
        assertTrue("path sdopath:names.0 resolved to " + s, "a".equals(s));
        s = d.getString("sdopath:/names.0");
        assertTrue("path sdopath:/names.0 resolved to " + s, "a".equals(s));
        s = d.getString("sdopath:/names.0");
        assertTrue("path sdopath:/names.0 resolved to " + s, "a".equals(s));
        s = d.getString("bla:names.0");
        assertNull("path bla:names.0 resolved to " + s, s);
    }

    @Test
    public void testAttributes() {
        SimpleAttrIntf i = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        i.setData("data");
        i.setGreen(true);
        i.setName("name");
        String s = ((DataObject)i).getString("@data");
        //assertEquals("path @data wasn't resolved correctly ", "data", s);
        assertEquals("path @data wasn't resolved correctly ", null, s);
        s = ((DataObject)i).getString("@name");
        assertEquals("path @name wasn't resolved correctly ", "name", s);
        boolean b = ((DataObject)i).getBoolean("@green");
        //assertTrue("path @green wasn't resolved correctly ", b);
        assertFalse("path @green wasn't resolved correctly ", b);
    }

    @Test
    public void testIntegerValues() {
        Type type = _helperContext.getTypeHelper().getType(OpenInterface.class);
        DataObject outer = _helperContext.getDataFactory().create(type);
        DataObject inner = _helperContext.getDataFactory().create(type);
        DataObject pInner = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pInner.set("name", "innerElement");
        pInner.set("type", type);
        pInner.setBoolean("containment", true);
        pInner.set("many", true);
        Property prInner = _helperContext.getTypeHelper().defineOpenContentProperty(null, pInner);
        assertTrue("generated property is not containment", prInner.isContainment());
        assertTrue("generated property is not many", prInner.isMany());
        List<OpenInterface> list = new ArrayList<OpenInterface>(1);
        list.add((OpenInterface)inner);
        outer.setList(prInner, list);
        DataObject pInt = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pInt.set("name", "intA");
        pInt.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "Integer"));
        pInt.setBoolean("containment", true);
        Property prInt =
            _helperContext.getTypeHelper().defineOpenContentProperty(null, pInt);
        inner.setInt(prInt, 42);
        DataObject data = outer.getDataObject("innerElement[intA=42]");
        assertEquals("path innerElement[intA=42] wasn't resolved correctly ", inner, data);
        data = outer.getDataObject("innerElement[intA = 42]");
        assertEquals("path innerElement[intA = 42] wasn't resolved correctly ", inner, data);
        data = outer.getDataObject("innerElement[intA=1]");
        assertNull("path innerElement[intA=1] wasn't resolved correctly: " + data, data);
    }

    @Test
    public void testDoubleValues() {
        Type type = _helperContext.getTypeHelper().getType(OpenInterface.class);
        DataObject outer = _helperContext.getDataFactory().create(type);
        DataObject inner = _helperContext.getDataFactory().create(type);
        DataObject pInner = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pInner.set("name", "innerElement");
        pInner.set("type", type);
        pInner.setBoolean("containment", true);
        pInner.set("many", true);
        Property prInner = _helperContext.getTypeHelper().defineOpenContentProperty(null, pInner);
        assertTrue("generated property is not containment", prInner.isContainment());
        assertTrue("generated property is not many", prInner.isMany());
        List<OpenInterface> list = new ArrayList<OpenInterface>(1);
        list.add((OpenInterface)inner);
        outer.setList(prInner, list);
        DataObject pFloat = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pFloat.set("name", "intA");
        pFloat.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "Double"));
        pFloat.setBoolean("containment", true);
        Property prFloat =
            _helperContext.getTypeHelper().defineOpenContentProperty(null, pFloat);
        inner.setDouble(prFloat, 47.11);
        DataObject data = outer.getDataObject("innerElement[intA=47.11]");
        assertEquals("path innerElement[intA=47.11] wasn't resolved correctly ", inner, data);
        data = outer.getDataObject("innerElement[intA = 47.11]");
        assertEquals("path innerElement[intA = 47.11] wasn't resolved correctly ", inner, data);
        data = outer.getDataObject("innerElement[intA=1]");
        assertNull("path innerElement[intA=1] wasn't resolved correctly: " + data, data);
    }

    @Test
    public void testBooleanValues() {
        Type type = _helperContext.getTypeHelper().getType(OpenInterface.class);
        DataObject outer = _helperContext.getDataFactory().create(type);
        DataObject inner = _helperContext.getDataFactory().create(type);
        DataObject pInner = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pInner.set("name", "innerElement");
        pInner.set("type", type);
        pInner.setBoolean("containment", true);
        pInner.set("many", true);
        Property prInner = _helperContext.getTypeHelper().defineOpenContentProperty(null, pInner);
        assertTrue("generated property is not containment", prInner.isContainment());
        assertTrue("generated property is not many", prInner.isMany());
        List<OpenInterface> list = new ArrayList<OpenInterface>(1);
        list.add((OpenInterface)inner);
        outer.setList(prInner, list);
        DataObject pBool = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pBool.set("name", "boolA");
        pBool.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "Boolean"));
        pBool.setBoolean("containment", true);
        Property prBool =
            _helperContext.getTypeHelper().defineOpenContentProperty(null, pBool);
        inner.setBoolean(prBool, true);
        DataObject data = outer.getDataObject("innerElement[boolA=true]");
        assertEquals("path innerElement[boolA=true] wasn't resolved correctly ", inner, data);
        data = outer.getDataObject("innerElement[boolA = true]");
        assertEquals("path innerElement[boolA = true] wasn't resolved correctly ", inner, data);
        data = outer.getDataObject("innerElement[boolA=false]");
        assertNull("path innerElement[boolA=false] wasn't resolved correctly ", data);
    }

    @Test
    public void testErrorHandling() {
        DataObject d1 = _helperContext.getDataFactory().create(NestedMVOuter.class);
        DataObject d2 = d1.createDataObject("inner");
        assertTrue("inner should implement NexteMVInner",
                d2 instanceof NestedMVInner);
        DataObject d3 = d1.createDataObject("inner");
        assertTrue("inner should implement NexteMVInner",
                d3 instanceof NestedMVInner);
        assertTrue("inner list should have size 2",
                d1.getList("inner").size() == 2);
        List<String> l = d2.getList("mv");
        Object o = d1.getList("sdopath:inner.0/mv");
        assertTrue("path sdopath:inner.0/mv resolved to " + o, o == l);
        l.add("d2_0");
        l.add("d2_1");
        o = d1.getList("sdopath:inner.0/test/mv");
        assertNull("path sdopath:inner.0/test/mv resolved to " + o, o);
        o = d1.getDataObject("inner[1");
        assertNull("path inner[1 resolved to " + o, o);
        o = d1.getDataObject("sdopath:inner.0/../inner[1");
        assertNull("path sdopath:inner.0/../inner[1 resolved to " + o, o);
        o = d1.getDataObject("sdopath:inner.0/../inner[x]");
        assertNull("path sdopath:inner.0/../inner[x] resolved to " + o, o);
        o = d1.getDataObject("sdopath:inner.0/../inner.x");
        assertNull("path sdopath:inner.0/../inner.x resolved to " + o, o);
        o = d1.getDataObject("sdopath:inner.0/../inner[bla=bla]");
        assertNull("path sdopath:inner.0/../inner[bla=bla] resolved to " + o, o);
        o = d1.getDataObject("1nner");
        assertNull("path 1nner resolved to " + o, o);
        o = d1.getString("sdopath:inner.0/id.0");
        assertNull("path sdopath:inner.0/id.0 resolved to " + o, o);
        o = d1.getDataObject("inner[1d=0]");
        assertNull("path 1nner resolved to " + o, o);
        o = d1.getString("inner/id[text=0]");
        assertNull("path inner/id[text=0] resolved to " + o, o);

        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        SimpleContainedIntf contained =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        containing.setX("x");
        containing.setInner(contained);
        contained.setName("name");
        assertEquals(
            "path inner[name='name'] not resolved", contained,
            ((DataObject)containing).getDataObject("inner[name='name']"));

        Type type = _helperContext.getTypeHelper().getType(OpenInterface.class);
        DataObject openIntf = _helperContext.getDataFactory().create(type);
        DataObject pBool = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pBool.set("name", "bool");
        pBool.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "Boolean"));
        pBool.setBoolean("containment", true);
        pBool.set("many", true);
        Property prBool =
            _helperContext.getTypeHelper().defineOpenContentProperty(null, pBool);
        List<Boolean> list = new ArrayList<Boolean>(1);
        list.add(true);
        openIntf.setList(prBool, list);
        assertNull(
            "path bool[value=true] resolved",
            openIntf.getDataObject("bool[value=true]"));

    }

    @Test
    public void testEscapedPropertyNames() {
        DataObject data = _helperContext.getDataFactory().create(
            _helperContext.getTypeHelper().getType(OpenInterface.class));
        DataObject pData = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pData.set("name", "abc.def");
        pData.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        pData.setBoolean("containment", true);
        pData.set("many", true);
        Property prData = _helperContext.getTypeHelper().defineOpenContentProperty(null, pData);
        assertTrue("generated property is not many", prData.isMany());
        List<String> list = new ArrayList<String>(1);
        list.add("test");
        data.setList(prData, list);
        assertEquals("abc.def[1]", "test", data.getString("abc.def[1]"));
        assertEquals("xpath:abc.def[1]", "test", data.getString("xpath:abc.def[1]"));
        assertEquals("sdo:abc.def[1]", "test", data.getString("sdo:abc.def[1]"));
        assertEquals("sdopath:abc\\.def[1]", "test", data.getString("sdopath:abc\\.def[1]"));
        assertEquals("sdopath:abc\\.def.0", "test", data.getString("sdopath:abc\\.def.0"));
    }

    @Test
    public void testEscapedPropertyNames2() {
        DataObject data = _helperContext.getDataFactory().create(
            _helperContext.getTypeHelper().getType(OpenInterface.class));
        DataObject pData = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pData.set("name", "abc.def.ghijk.lmno.äöü┴┬├");
        pData.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        pData.setBoolean("containment", true);
        pData.set("many", true);
        Property prData = _helperContext.getTypeHelper().defineOpenContentProperty(null, pData);
        assertTrue("generated property is not many", prData.isMany());
        List<String> list = new ArrayList<String>(1);
        list.add("test");
        data.setList(prData, list);
        assertEquals("sdopath:abc\\.def\\.ghijk\\.lmno\\.äöü┴┬├.0",
            "test",
            data.getString("sdopath:abc\\.def\\.ghijk\\.lmno\\.äöü┴┬├.0"));
    }

    @Test
    public void testInvalidPropertyNames() {
        DataObject data = _helperContext.getDataFactory().create(
            _helperContext.getTypeHelper().getType(OpenInterface.class));
        DataObject pData = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pData.set("name", "-prop");
        pData.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        pData.setBoolean("containment", true);
        Property prData = _helperContext.getTypeHelper().defineOpenContentProperty(null, pData);
        data.setString(prData, "test");
        DataObject pData2 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pData2.set("name", "a");
        pData2.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        pData2.setBoolean("containment", true);
        Property prData2 = _helperContext.getTypeHelper().defineOpenContentProperty(null, pData2);
        data.setString(prData2, "test");
        assertEquals("test", data.getString("-prop"));
        assertNull("data.getString(\"a[-prop=0]\") doesn't return null", data.getString("a[-prop=0]"));
    }

}
