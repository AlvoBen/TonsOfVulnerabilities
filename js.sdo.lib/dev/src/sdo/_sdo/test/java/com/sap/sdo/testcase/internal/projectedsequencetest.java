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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.projections.DelegatingDataStrategy;
import com.sap.sdo.impl.objects.projections.ProjectedSequence;
import com.sap.sdo.impl.objects.projections.ProjectionDataStrategy;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenSequencedInterface;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

/**
 * @author D042774
 *
 */
public class ProjectedSequenceTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ProjectedSequenceTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private ProjectedSequence _sequence;
    private GenericDataObject _gdo;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        DataObjectDecorator data =
            (DataObjectDecorator)_helperContext.getDataFactory().create(OpenSequencedInterface.class);
        _gdo = data.getInstance();
        _sequence =
            new ProjectedSequence(
                new ProjectionDataStrategy(
                    _gdo,
                    new DelegatingDataStrategy((AbstractDataStrategy)_gdo.getDataStrategy())));
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _sequence = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testAddStringObject() {
        assertEquals(0, _sequence.size());
        assertEquals(true, _sequence.add("x", "aName"));
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(int, java.lang.Object)}.
     */
    @Test
    public void testAddIntObject() {
        assertEquals(0, _sequence.size());
        assertTrue(_sequence.add(0, _helperContext.getDataFactory().create(SimpleContainedIntf.class)));
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(commonj.sdo.Property, java.lang.Object)}.
     */
    @Test
    public void testAddPropertyObject() {
        assertEquals(0, _sequence.size());
        assertTrue(_sequence.add(_gdo.getInstanceProperty("inner"), _helperContext.getDataFactory().create(SimpleContainedIntf.class)));
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(int, java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testAddIntStringObject() {
        assertEquals(0, _sequence.size());
        _sequence.add(0, "x", "aName");
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(int, int, java.lang.Object)}.
     */
    @Test
    public void testAddIntIntObject() {
        assertEquals(0, _sequence.size());
        _sequence.add(0, ((SdoProperty)_gdo.getInstanceProperty("inner")).getIndex(), _helperContext.getDataFactory().create(SimpleContainedIntf.class));
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(int, commonj.sdo.Property, java.lang.Object)}.
     */
    @Test
    public void testAddIntPropertyObject() {
        assertEquals(0, _sequence.size());
        _sequence.add(0, _gdo.getInstanceProperty("inner"), _helperContext.getDataFactory().create(SimpleContainedIntf.class));
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(java.lang.String)}.
     */
    @Test
    public void testAddString() {
        assertEquals(0, _sequence.size());
        _sequence.add("text");
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#add(int, java.lang.String)}.
     */
    @Test
    public void testAddIntString() {
        assertEquals(0, _sequence.size());
        _sequence.add(0, "text");
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#addText(java.lang.String)}.
     */
    @Test
    public void testAddTextString() {
        assertEquals(0, _sequence.size());
        _sequence.addText("text");
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#addText(int, java.lang.String)}.
     */
    @Test
    public void testAddTextIntString() {
        assertEquals(0, _sequence.size());
        _sequence.addText(0, "text");
        assertEquals(1, _sequence.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#getProperty(int)}.
     */
    @Test
    public void testGetProperty() {
        _sequence.addText("text");
        Property prop = _gdo.getInstanceProperty("inner");
        _sequence.add(prop, _helperContext.getDataFactory().create(SimpleContainedIntf.class));
        try {
            _sequence.getProperty(0);
            fail("expected NullPointerException");
        } catch (NullPointerException ex) {
            if (ex.getMessage() != null) {
                assertEquals(
                    "while trying to invoke the method" +
                    " com.sap.sdo.impl.types.SdoProperty.isOpenContent()" +
                    " of an object loaded from local variable 'dProp'",
                    ex.getMessage());
            }
        }
        assertEquals(prop, _sequence.getProperty(1));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#getValue(int)}.
     */
    @Test
    public void testGetValue() {
        _sequence.addText("text");
        Property prop = _gdo.getInstanceProperty("inner");
        DataObject data = _helperContext.getDataFactory().create(SimpleContainedIntf.class);
        _sequence.add(prop, data);
        assertEquals("text", _sequence.getValue(0));
        assertEquals(data, _sequence.getValue(1));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#move(int, int)}.
     */
    @Test
    public void testMove() {
        _sequence.addText("text");
        Property prop = _gdo.getInstanceProperty("inner");
        _sequence.add(prop, _helperContext.getDataFactory().create(SimpleContainedIntf.class));
        _sequence.move(0, 1);
        assertEquals("text", _sequence.getValue(1));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#remove(int)}.
     */
    @Test
    public void testRemove() {
        _sequence.addText("text");
        Property prop = _gdo.getInstanceProperty("inner");
        _sequence.add(prop, _helperContext.getDataFactory().create(SimpleContainedIntf.class));
        _sequence.remove(0);
        assertEquals(prop, _sequence.getProperty(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#setValue(int, java.lang.Object)}.
     */
    @Test
    public void testSetValue() {
        _sequence.addText("text");
        assertEquals("text", _sequence.setValue(0, null));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.ProjectedSequence#size()}.
     */
    @Test
    public void testSize() {
        assertEquals(0, _sequence.size());
    }

}
