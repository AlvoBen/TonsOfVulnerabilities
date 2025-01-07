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

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.projections.DelegatingDataStrategy;
import com.sap.sdo.impl.objects.projections.ProjectionDataStrategy;
import com.sap.sdo.impl.objects.projections.ProjectionPropMultiValue;
import com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;

import commonj.sdo.DataObject;

/**
 * @author D042774
 *
 */
public class ProjectionPropSingleValueTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ProjectionPropSingleValueTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private ProjectionPropSingleValue _value;
    private GenericDataObject _gdo;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        DataObjectDecorator data = (DataObjectDecorator)_helperContext.getDataFactory().create(OpenInterface.class);
        _gdo = data.getInstance();
        _value =
            new ProjectionPropSingleValue(
                new ProjectionDataStrategy(_gdo, new DelegatingDataStrategy((AbstractDataStrategy)_gdo.getDataStrategy())),
                _gdo.getInstanceProperty("inner"),
                _gdo.getPropValueByPropNameOrAlias("inner"));
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _value = null;
        _gdo = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#checkSaveOldValue()}.
     */
    @Test
    public void testCheckSaveOldValue() {
        _value.checkSaveOldValue();
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#getConvertedValue(java.lang.Class)}.
     */
    @Test
    public void testGetConvertedValueClass() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _gdo.setDataObject("inner", inner);
        assertEquals(inner, _value.getConvertedValue(DataObject.class));

            _gdo.setString("x", "string");
            ProjectionPropSingleValue value = new ProjectionPropSingleValue(
                new ProjectionDataStrategy(_gdo, new DelegatingDataStrategy((AbstractDataStrategy)_gdo.getDataStrategy())),
                _gdo.getInstanceProperty("x"),
                _gdo.getPropValueByPropNameOrAlias("x"));
            assertEquals("string", value.getConvertedValue(String.class));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#getConvertedValue(int, java.lang.Class)}.
     */
    @Test
    public void testGetConvertedValueIntClass() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _gdo.setDataObject("inner", inner);
        assertEquals(inner, _value.getConvertedValue(0, DataObject.class));
        try {
            _value.getConvertedValue(1, DataObject.class);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Index of single value properties must be 0 but is 1", ex.getMessage());
        }

        _gdo.setString("x", "string");
        ProjectionPropSingleValue value = new ProjectionPropSingleValue(
            new ProjectionDataStrategy(_gdo, new DelegatingDataStrategy((AbstractDataStrategy)_gdo.getDataStrategy())),
            _gdo.getInstanceProperty("x"),
            _gdo.getPropValueByPropNameOrAlias("x"));
        assertEquals("string", value.getConvertedValue(0, String.class));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#getDataObject()}.
     */
    @Test
    public void testGetDataObject() {
        assertEquals(_gdo, _value.getDataObject());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#getIndexByPropertyValue(java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testGetIndexByPropertyValue() {
        assertEquals(-1, _value.getIndexByPropertyValue("name", "aName"));

        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setString("name", "aName");
        _gdo.setDataObject("inner", inner);
        assertEquals(0, _value.getIndexByPropertyValue("name", "aName"));
        assertEquals(-1, _value.getIndexByPropertyValue("name", "bName"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#getOldValue()}.
     */
    @Test
    public void testGetOldValue() {
        assertEquals(null, _value.getOldValue());

        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _gdo.setDataObject("inner", inner);
        assertEquals(inner, _value.getOldValue());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#getProperty()}.
     */
    @Test
    public void testGetProperty() {
        assertEquals(_gdo.getInstanceProperty("inner"), _value.getProperty());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#getValue()}.
     */
    @Test
    public void testGetValue() {
        assertEquals(null, _value.getValue());

        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _gdo.setDataObject("inner", inner);
        assertEquals(inner, _value.getValue());

        _gdo.setString("x", "string");
        ProjectionPropSingleValue value = new ProjectionPropSingleValue(
            new ProjectionDataStrategy(_gdo, new DelegatingDataStrategy((AbstractDataStrategy)_gdo.getDataStrategy())),
            _gdo.getInstanceProperty("x"),
            _gdo.getPropValueByPropNameOrAlias("x"));
        assertEquals("string", value.getValue());

        _gdo.setList("list", Arrays.asList("a", "b", "c"));
        ProjectionPropMultiValue multiValue = new ProjectionPropMultiValue(
            new ProjectionDataStrategy(_gdo, new DelegatingDataStrategy((AbstractDataStrategy)_gdo.getDataStrategy())),
            _gdo.getInstanceProperty("list"),
            (PropValue<List<Object>>)_gdo.getPropValueByPropNameOrAlias("list"));
        assertEquals(Arrays.asList("a", "b", "c"), multiValue.getValue());
        assertEquals(Arrays.asList("a", "b", "c"), multiValue.getConvertedValue(List.class));
        assertEquals(true, multiValue.isMany());
        assertEquals(3, multiValue.getValue().size());
        assertEquals(1, multiValue.getValue().indexOf("b"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#internAttachOpposite(com.sap.sdo.impl.objects.DataObjectDecorator)}.
     */
    @Test
    public void testInternAttachOpposite() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _value.internAttachOpposite((DataObjectDecorator)inner);
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#internDetachOpposite(com.sap.sdo.impl.objects.DataObjectDecorator)}.
     */
    @Test
    public void testInternDetachOpposite() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _value.internDetachOpposite((DataObjectDecorator)inner);
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#isMany()}.
     */
    @Test
    public void testIsMany() {
        assertEquals(false, _value.isMany());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#isModified()}.
     */
    @Test
    public void testIsModified() {
        assertEquals(false, _value.isModified());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#isReadOnly()}.
     */
    @Test
    public void testIsReadOnly() {
        assertEquals(false, _value.isReadOnly());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#isSet()}.
     */
    @Test
    public void testIsSet() {
        assertEquals(false, _value.isSet());

        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _gdo.setDataObject("inner", inner);
        assertEquals(true, _value.isSet());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#remove(java.lang.Object)}.
     */
    @Test
    public void testRemove() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _gdo.setDataObject("inner", inner);
        assertEquals(inner, _value.getValue());

        _value.remove(inner);
        assertEquals(null, _value.getValue());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#set(int, java.lang.Object)}.
     */
    @Test
    public void testSet() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _value.set(0, inner);
        assertEquals(inner, _value.getValue());

        try {
            _value.set(1, inner);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Index of single value properties must be 0 but is 1", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#setValue(java.lang.Object)}.
     */
    @Test
    public void testSetValue() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _value.setValue(inner);
        assertEquals(inner, _value.getValue());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectionPropSingleValue#unset()}.
     */
    @Test
    public void testUnset() {
        DataObject inner = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _value.setValue(inner);
        assertEquals(true, _value.isSet());
        _value.unset();
        assertEquals(false, _value.isSet());
        assertEquals(null, _value.getValue());
    }
}
