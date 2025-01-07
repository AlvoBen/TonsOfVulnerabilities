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
package com.sap.sdo.testcase.internal.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.strategy.pojo.PojoDataObjectFactory;
import com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class PojoDataStrategyTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public PojoDataStrategyTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    PojoDataStrategy _strategy = null;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _strategy = new PojoDataStrategy(null, null);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _strategy = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#addOpenProperty(commonj.sdo.Property)}.
     */
    @Test
    public void testAddOpenProperty() {
        try {
            _strategy.addOpenProperty(null);
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals("addOpenProperty", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#reactivateOpenPropValue(com.sap.sdo.impl.objects.PropValue)}.
     */
    @Test
    public void testReactivateOpenPropValue() {
        try {
            _strategy.reactivateOpenPropValue(null);
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals("reactivateOpenPropValue", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#removeOpenPropValue(int)}.
     */
    @Test
    public void testRemoveOpenPropValue() {
        try {
            _strategy.removeOpenPropValue(0);
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals("removeOpenPropValue", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#createDataObject(commonj.sdo.Property, commonj.sdo.Type)}.
     */
    @Test
    public void testCreateDataObject() {
        try {
            _strategy.createDataObject(null, null);
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals("createDataObject", ex.getMessage());
        }

        Pojo pojo = new Pojo();
        Type pojoType = _helperContext.getTypeHelper().getType(IPojo.class);
        DataObject data = PojoDataObjectFactory.createDataObject(pojoType, pojo);
        Map<Object, DataObject> pojoToDataObject = new HashMap<Object, DataObject>();
        pojoToDataObject.put(pojo, data);
        assertSame(data, PojoDataObjectFactory.createDataObject(pojoType, pojo, pojoToDataObject));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#refineType(commonj.sdo.Type, commonj.sdo.Type)}.
     */
    @Test
    public void testRefineType() {
        assertEquals(_strategy, _strategy.refineType(null, null));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#getPojoValue(commonj.sdo.Property)}.
     */
    @Test
    public void testGetPojoValue() {
        DataObject data = PojoDataObjectFactory.createDataObject(
            _helperContext.getTypeHelper().getType(IPojo.class), new Pojo());
        assertEquals(false, data.getBoolean("boolean"));

        DataObject invalidData = PojoDataObjectFactory.createDataObject(
            _helperContext.getTypeHelper().getType(IInvalidPojo.class), new Pojo());

        try {
            assertEquals(null, invalidData.getString("noGetter"));
        } catch (RuntimeException e) {
            // expected
        }
        try {
            assertEquals(null, invalidData.getString("privateGetter"));
        } catch (RuntimeException e) {
            // expected
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#setPojoValue(commonj.sdo.Property, java.lang.Object)}.
     */
    @Test
    public void testSetPojoValue() {
        DataObject invalidData = PojoDataObjectFactory.createDataObject(
            _helperContext.getTypeHelper().getType(IInvalidPojo.class), new Pojo());

        try {
            invalidData.setString("noGetter", "string");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("No getter for NoGetter", ex.getMessage());
        }
        try {
            invalidData.setString("privateGetter", "string");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("No getter for PrivateGetter", ex.getMessage());
        }

        try {
            invalidData.setString("noSetter", "string");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("No setter for NoSetter", ex.getMessage());
        }
        try {
            invalidData.setString("privateSetter", "string");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("No setter for PrivateSetter", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.strategy.pojo.PojoDataStrategy#getDataObject(java.lang.Object, com.sap.sdo.impl.objects.PropValue)}.
     */
    @Test
    public void testGetDataObjectObjectPropValueOfQ() {
        assertEquals(null, _strategy.getDataObject(null, null));
    }

    @Test
    public void testSingleValueIsSet() {
        DataObject data = PojoDataObjectFactory.createDataObject(
            _helperContext.getTypeHelper().getType(IPojo.class), new Pojo());

        assertEquals(false, data.isSet("boolean"));
        assertEquals(false, data.isSet("child"));

        DataObject child = PojoDataObjectFactory.createDataObject(
            _helperContext.getTypeHelper().getType(IPojo.class), new Pojo());
        data.setDataObject("child", child);

        assertEquals(true, data.isSet("child"));

        data.setDataObject("child", null);
        assertEquals(null, data.get("child"));
    }

    interface IInvalidPojo {
        String getNoGetter();
        void setNoGetter(String pStr);

        String getPrivateGetter();
        void setPrivateGetter(String pStr);

        String getNoSetter();
        void setNoSetter(String pStr);

        String getPrivateSetter();
        void setPrivateSetter(String pStr);
    }

    interface IPojo {
        boolean isBoolean();

        void setBoolean(boolean pBool);

        IPojo getChild();

        void setChild(IPojo pChild);
    }

    public static class Pojo implements IPojo {
        private boolean _boolean = false;
        private IPojo _child = null;

        public boolean isBoolean() {
            return _boolean;
        }

        public void setBoolean(boolean pBool) {
            _boolean = pBool;
        }

        private String getPrivateGetter() {
            return null;
        }

        public String getNoSetter() {
            return null;
        }

        public String getPrivateSetter() {
            return null;
        }

        private void setPrivateSetter(String pStr) {
        }

        /* (non-Javadoc)
         * @see com.sap.sdo.testcase.internal.pojo.PojoDataStrategyTest.IPojo#getChild()
         */
        public IPojo getChild() {
            return _child;
        }

        /* (non-Javadoc)
         * @see com.sap.sdo.testcase.internal.pojo.PojoDataStrategyTest.IPojo#setChild(com.sap.sdo.testcase.internal.pojo.PojoDataStrategyTest.IPojo)
         */
        public void setChild(IPojo pChild) {
            _child = pChild;
        }
    }
}
