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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleTypeLoopIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class DataObjectContainmentTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public DataObjectContainmentTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testAssociationOnOpenType() {
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        assertTrue("returned value is not a type",typeDO instanceof Type);
        typeDO.set("open",true);
        typeDO.set("name","OpenType");
        typeDO.set("uri","commonj.sdo");
        Type t = _helperContext.getTypeHelper().define(typeDO);
        _helperContext.getTypeHelper().define((DataObject)t);
        assertTrue("returned value is not a type",t instanceof Type);
        DataObject d = _helperContext.getDataFactory().create(t);

        DataObject p1 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p1.set("name", "co");
        p1.set("type", t);
        p1.setBoolean("containment", true);
        DataObject c = _helperContext.getDataFactory().create(t);
        Property pr = _helperContext.getTypeHelper().defineOpenContentProperty(null, p1);
        assertTrue("generated property is not containment", pr
                .isContainment());
        d.setDataObject(pr, c);
        assertTrue("parent container association incorrect "
                + c.getContainer(), c.getContainer() == d);
        d.unset(pr);

        DataObject p2 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p2.set("name", "co");
        p2.set("type", t);
        p2.setBoolean("containment", false);
        pr = _helperContext.getTypeHelper().defineOpenContentProperty(null, p2);
        d.setDataObject(pr, c);
        assertEquals("parent container association should be null", null, c.getContainer());
        d.unset(pr);

        DataObject p3 = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p3.set("name", "co");
        p3.set("type", t);
        p3.setBoolean("containment", true);
        pr = _helperContext.getTypeHelper().defineOpenContentProperty(null, p3);
        assertSame(pr, p1);
        d.setDataObject(pr, c);
        assertTrue("parent container association incorrect "
                + c.getContainer(), c.getContainer() == d);
    }

    @Test
    public void testAssociationFromInterface() {
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        DataObject d = _helperContext.getDataFactory().create(t);
        d.createDataObject("inner");
        SimpleContainingIntf di = (SimpleContainingIntf)d;
        DataObject d2 = (DataObject)di.getInner();
        assertTrue("inner object should have outer containment " + d, d2
                .getContainer() == d);
    }

    @Test
    public void testTypeInCircles() {
        Type t = _helperContext.getTypeHelper().getType(SimpleTypeLoopIntf.class);
        DataObject d = _helperContext.getDataFactory().create(t);
        d.createDataObject("inner");
        SimpleTypeLoopIntf di = (SimpleTypeLoopIntf)d;
        DataObject d2 = (DataObject)di.getInner();
        assertTrue("inner object should have outer containment " + d, d2.getContainer().equals(d));
    }
    @Test
    public void testMoveOfContainment() {
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        DataObject d1 = _helperContext.getDataFactory().create(t);
        DataObject d2 = _helperContext.getDataFactory().create(t);
        DataObject di = d1.createDataObject("inner");
        assertSame("inner object should have outer containment", d1, di.getContainer());

        d2.setDataObject("inner", di);
        assertSame("inner object should have outer containment ", d2, di.getContainer());
        assertSame("inner object should have left d1", null, d1.get("inner"));
        assertTrue("inner object should have entered d2",
                d2.get("inner") == di);
        d1.setDataObject("inner", di);
        assertTrue("inner object should have outer containment " + d1, di
                .getContainer() == d1);
        assertTrue("inner object should have left d2",
                d2.get("inner") == null);
        assertTrue("inner object should have entered d1",
                d1.get("inner") == di);
    }

    @Test
    public void testMoveOfContainmentInManyValue() {
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",true);
        typeDO.set("name","OpenType");
        typeDO.set("uri","commonj.sdo");
        Type ot = _helperContext.getTypeHelper().define(typeDO);
        DataObject p = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p.set("name", "mv");
        p.set("type", ot);
        p.setBoolean("many", true);
        p.setBoolean("containment", true);
        Property pr = _helperContext.getTypeHelper().defineOpenContentProperty(null, p);
        DataObject d1 = _helperContext.getDataFactory().create(ot);
        DataObject d2 = _helperContext.getDataFactory().create(ot);
	    DataObject id = _helperContext.getDataFactory().create("commonj.sdo","Property");
	    id.set("name","id");
	    id.set("type",_helperContext.getTypeHelper().getType(String.class));
        DataObject mv1 = d1.createDataObject(pr);
        Property idProperty = _helperContext.getTypeHelper().defineOpenContentProperty(null, id);
        mv1.set(idProperty, "1");
        assertEquals("d1 should have 1 child do " + d1, 1, d1.getList(pr).size());

        assertEquals(0, d1.getInstanceProperties().indexOf(pr));
        DataObject mv2 = d1.createDataObject(0);
        mv2.set(idProperty, "2");
        assertEquals("d1 should have two child dos " + d1, 2, d1.getList("mv").size());
        assertEquals(0, mv2.getInstanceProperties().indexOf(id));
        assertEquals(true, mv2.isSet(0));
        mv2.unset(0);
        assertEquals(false, mv2.isSet(idProperty));
        mv2.set(idProperty, "2");

        List nv = new ArrayList();
        nv.add(d1.getList("mv").get(0));
        d2.set(pr, nv);
        assertTrue("d1 should have one child do",
                d1.getList("mv").size() == 1);
        DataObject dt = (DataObject)d2.getList("mv").get(0);
        assertTrue("wrong containment", dt.getContainer() == d2);
        String t = ((DataObject)d1.getList("mv").get(0)).getString("id");
        assertTrue("d1 should have child do with id 2", "2".equals(t));
        List v = d2.getList("mv");
        assertTrue("d2 should have one child do", v.size() == 1);
        t = ((DataObject)d2.getList("mv").get(0)).getString("id");
        assertTrue("d2 should have child do with id 1", "1".equals(t));
        d2.createDataObject("mv").set(idProperty, "3");
        t = ((DataObject)d2.getList("mv").get(0)).getString("id");
        assertTrue("d2 should have child do with id 1", "1".equals(t));
        t = ((DataObject)d2.getList("mv").get(1)).getString("id");
        assertTrue("d2 should have child do with id 3", "3".equals(t));
        d1.set(pr, d2.getList(pr));
        assertFalse("d2 should have no children anymore", d2.isSet("mv"));
        assertTrue("d1 should have two children",
                d1.getList("mv").size() == 2);
        t = ((DataObject)d1.getList("mv").get(0)).getString("id");
        assertTrue("d1 should have child do with id 1", "1".equals(t));
        t = ((DataObject)d1.getList("mv").get(1)).getString("id");
        assertTrue("d1 should have child do with id 3", "3".equals(t));
        dt = (DataObject)d1.getList("mv").get(0);
        assertTrue("wrong containment", dt.getContainer() == d1);
        dt = (DataObject)d1.getList("mv").get(1);
        assertTrue("wrong containment", dt.getContainer() == d1);
    }
}
