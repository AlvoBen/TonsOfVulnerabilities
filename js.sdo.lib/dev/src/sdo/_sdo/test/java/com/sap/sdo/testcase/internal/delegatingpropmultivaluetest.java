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

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.projections.DelegatingDataStrategy;
import com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleMVIntf;

/**
 * @author D042774
 *
 */
public class DelegatingPropMultiValueTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public DelegatingPropMultiValueTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DelegatingPropMultiValue _value;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        DataObjectDecorator data =
            (DataObjectDecorator)_helperContext.getDataFactory().create(SimpleMVIntf.class);
        data.set("names", Collections.singletonList("test"));
        _value = new DelegatingPropMultiValue(
            new DelegatingDataStrategy(
                (AbstractDataStrategy)data.getInstance().getDataStrategy()),
            data.getInstanceProperties().indexOf(data.getInstanceProperty("names")));
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _value = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#size()}.
     */
    @Test
    public void testSize() {
        assertEquals(1, _value.size());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#set(int, java.lang.Object, com.sap.sdo.impl.objects.strategy.PropertyChangeContext)}.
     */
    @Test
    public void testSetIntObjectPropertyChangeContext() {
        assertEquals("test", _value.set(0, "other"));
        assertEquals("other", _value.get(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#add(int, java.lang.Object, com.sap.sdo.impl.objects.strategy.PropertyChangeContext)}.
     */
    @Test
    public void testAddIntObjectPropertyChangeContext() {
        _value.add(0, Collections.emptyList());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#remove(int, com.sap.sdo.impl.objects.strategy.PropertyChangeContext)}.
     */
    @Test
    public void testRemoveIntPropertyChangeContext() {
        assertEquals("test", _value.remove(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#saveOldValue()}.
     */
    @Test
    public void testSaveOldValue() {
        _value.checkSaveOldValue();
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#get(int)}.
     */
    @Test
    public void testGetInt() {
        assertEquals("test", _value.get(0));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#getOldValue()}.
     */
    @Test
    public void testGetOldValue() {
        assertEquals(Collections.singletonList("test"), _value.getOldValue());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#isModified()}.
     */
    @Test
    public void testIsModified() {
        assertEquals(false, _value.isModified());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.projections.DelegatingPropMultiValue#setValue(java.lang.Object)}.
     */
    @Test
    public void testSetValue() {
        _value.setValue(null);
    }

}
