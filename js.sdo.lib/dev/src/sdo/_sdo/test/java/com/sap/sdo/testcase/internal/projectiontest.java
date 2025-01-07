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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.OpenSequencedInterface;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Sequence;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class ProjectionTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ProjectionTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject _data;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _data = _helperContext.getDataFactory().create(OpenInterface.class);
        _data.setString("x", "string");
        DataObject inner = _data.createDataObject("inner");
        inner.setString("name", "a name");

        _data.setList("list", Arrays.asList(1,2,3,4,5));
        assertNotNull(_data.getList("list"));
        assertEquals(5, _data.getList("list").size());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _data = null;
    }

    @Test
    public void testCreateProjection() {
        assertEquals(null, ((SapDataFactory)_helperContext.getDataFactory()).project(null));
        assertSame(_data, ((SapDataFactory)_helperContext.getDataFactory()).project(_data));

        HelperContext ctx = SapHelperProvider.getNewContext();
        ctx.getTypeHelper().getType(OpenInterface.class);
        DataObject projection = ((SapDataFactory)ctx.getDataFactory()).project(_data);
        assertEquals("string", projection.getString("x"));
        DataObject inner = projection.getDataObject("inner");
        assertNotNull(inner);
        assertEquals("a name", inner.getString("name"));
        List<Integer> list = projection.getList("list");
        assertNotNull(list);
        assertEquals(5, list.size());
        for(int i=0; i<5; ++i) {
            assertEquals(new Integer(i+1), list.get(i));
        }
        projection.setString("onDemand", "on demand");
        assertEquals("on demand", projection.getString("onDemand"));
        assertEquals(true, _data.isSet("onDemand"));
        assertEquals("on demand", _data.getString("onDemand"));

        projection.unset("inner");
        assertEquals(null, _data.getDataObject("inner"));
        assertEquals(null, projection.getDataObject("inner"));
        DataObject newInner = projection.createDataObject("inner");
        newInner.setString("name", "another name");
        assertEquals("another name", _data.getString("inner/name"));
    }

    @Test
    public void testCreateProjectionType() {
        HelperContext ctx = SapHelperProvider.getNewContext();
        DataObject typeObj = ctx.getDataFactory().create("commonj.sdo", "Type");
        typeObj.set(TypeConstants.NAME, "OpenInterface");
        typeObj.set(TypeConstants.URI, "com.sap.sdo.testcase.typefac");
        typeObj.set(TypeConstants.OPEN, true);
        DataObject xObj = typeObj.createDataObject("property");
        xObj.set(PropertyConstants.NAME, "x");
        xObj.set(PropertyConstants.TYPE, ctx.getTypeHelper().getType("commonj.sdo", "String"));
        DataObject innerObj = typeObj.createDataObject("property");
        innerObj.set(PropertyConstants.NAME, "inner");
        innerObj.set(PropertyConstants.TYPE, ctx.getTypeHelper().getType(SimpleContainedIntf.class));
        innerObj.set(PropertyConstants.CONTAINMENT, false);

        ctx.getTypeHelper().define(typeObj);

        DataObject projection = ((SapDataFactory)ctx.getDataFactory()).project(_data);
        assertEquals("string", projection.getString("x"));
        DataObject inner = projection.getDataObject("inner");
        assertNotNull(inner);
        assertEquals("a name", inner.getString("name"));

        projection.setString("onDemand", "on demand");
        assertEquals("on demand", projection.getString("onDemand"));
        assertEquals(true, _data.isSet("onDemand"));
        assertEquals("on demand", _data.getString("onDemand"));
    }

    @Test
    public void testSequenced() {
        DataObject data = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        HelperContext ctx = SapHelperProvider.getNewContext();
        ctx.getTypeHelper().getType(OpenSequencedInterface.class);
        DataObject projection = ((SapDataFactory)ctx.getDataFactory()).project(data);

        Sequence seq = projection.getSequence();
        assertNotNull(seq);
        assertEquals(0, seq.size());
        data.getSequence().addText("text");
        assertEquals(1, seq.size());
        GenericDataObject projectionGdo = ((DataObjectDecorator)projection).getInstance();
        assertEquals(1, projectionGdo.getOldSequence().size());

        assertSame(projectionGdo.getDataStrategy(), projectionGdo.getDataStrategy().refineType(null, null));
    }

    @Test
    public void testCast() {
        assertEquals(null, ((SapDataFactory)_helperContext.getDataFactory()).cast(null));
        assertSame(_data, ((SapDataFactory)_helperContext.getDataFactory()).cast(_data));

        DataObject typeObj = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObj.set(TypeConstants.NAME, "MyType");
        typeObj.set(TypeConstants.URI, "com.sap.sdo.testcase.internal");
        DataObject xObj = typeObj.createDataObject("property");
        xObj.set(PropertyConstants.NAME, "x");
        xObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "String"));

        _helperContext.getTypeHelper().define(typeObj);

        MyType myData = new MyType();
        myData.setX("string");

        try {
            DataObject cast = ((SapDataFactory)_helperContext.getDataFactory()).cast(myData);
            assertNotNull(cast);
            assertEquals("string", cast.getString("x"));
        } catch (UnsupportedOperationException ex) {
            assertEquals(null, ex.getMessage());
        }
    }

    public static class MyType {
        String _x;

        public String getX() {
            return _x;
        }

        public void setX(String pX) {
            _x = pX;
        }
    }
}
