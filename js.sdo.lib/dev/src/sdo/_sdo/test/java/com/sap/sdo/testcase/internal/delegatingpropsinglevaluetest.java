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
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.projections.DelegatingDataStrategy;
import com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleIntf1;

/**
 * @author D042774
 *
 */
public class DelegatingPropSingleValueTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public DelegatingPropSingleValueTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DelegatingPropSingleValue _value;
    private GenericDataObject _data;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        DataObjectDecorator data =
            (DataObjectDecorator)_helperContext.getDataFactory().create(SimpleIntf1.class);
        SdoProperty nameProp = (SdoProperty)data.getInstanceProperty("name");
        data.set(nameProp, "aName");
        _data = data.getInstance();
        _value = new DelegatingPropSingleValue(
            new DelegatingDataStrategy((AbstractDataStrategy)_data.getDataStrategy()),
            (PropValue<Object>)_data.getPropValue(nameProp, true, false));
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _value = null;
        _data = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#checkSaveOldValue()}.
     */
    @Test
    public void testCheckSaveOldValue() {
        _value.checkSaveOldValue();
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#getConvertedValue(java.lang.Class)}.
     */
    @Test
    public void testGetConvertedValueClass() {
        assertEquals("aName", _value.getConvertedValue(String.class));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#getConvertedValue(int, java.lang.Class)}.
     */
    @Test
    public void testGetConvertedValueIntClass() {
        assertEquals("aName", _value.getConvertedValue(0, String.class));
        try {
            _value.getConvertedValue(1, String.class);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Index of single value properties must be 0 but is 1", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#getDataObject()}.
     */
    @Test
    public void testGetDataObject() {
        assertEquals(_data, _value.getDataObject());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#getIndexByPropertyValue(java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testGetIndexByPropertyValue() {
        assertEquals(-1, _value.getIndexByPropertyValue("", null));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#getOldValue()}.
     */
    @Test
    public void testGetOldValue() {
        assertEquals("aName", _value.getOldValue());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#getProperty()}.
     */
    @Test
    public void testGetProperty() {
        assertEquals(_data.getInstanceProperty("name"), _value.getProperty());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#getValue()}.
     */
    @Test
    public void testGetValue() {
        assertEquals("aName", _value.getValue());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#internAttachOpposite(com.sap.sdo.impl.objects.DataObjectDecorator)}.
     */
    @Test
    public void testInternAttachOpposite() {
        _value.internAttachOpposite(_data);
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#internDetachOpposite(com.sap.sdo.impl.objects.DataObjectDecorator)}.
     */
    @Test
    public void testInternDetachOpposite() {
        _value.internDetachOpposite(_data);
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#isMany()}.
     */
    @Test
    public void testIsMany() {
        assertEquals(false, _value.isMany());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#isModified()}.
     */
    @Test
    public void testIsModified() {
        assertEquals(false, _value.isModified());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#isReadOnly()}.
     */
    @Test
    public void testIsReadOnly() {
        assertEquals(false, _value.isReadOnly());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#isSet()}.
     */
    @Test
    public void testIsSet() {
        assertEquals(true, _value.isSet());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#remove(java.lang.Object)}.
     */
    @Test
    public void testRemove() {
        assertEquals(true, _value.isSet());
        _value.remove("aName");
        assertEquals(false, _value.isSet());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#set(int, java.lang.Object)}.
     */
    @Test
    public void testSet() {
        _value.set(0, "test");
        assertEquals("test", _value.getValue());
        try {
            _value.set(1, "test2");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Index of single value properties must be 0 but is 1", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#setValue(java.lang.Object)}.
     */
    @Test
    public void testSetValue() {
        _value.setValue("test");
        assertEquals("test", _value.getValue());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropSingleValue#unset()}.
     */
    @Test
    public void testUnset() {
        assertEquals(true, _value.isSet());
        _value.unset();
        assertEquals(false, _value.isSet());
        assertEquals(null, _value.getValue());
    }

}
