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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.ReadOnlyContainedIntf;
import com.sap.sdo.testcase.typefac.ReadOnlyContainingIntf;

import commonj.sdo.DataObject;

/**
 * @author D042774
 *
 */
public class DataObjectDeleteTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DataObjectDeleteTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private ReadOnlyContainingIntf _outer;
    private ReadOnlyContainedIntf _innerReadOnly;
    private ReadOnlyContainedIntf _inner;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _outer = (ReadOnlyContainingIntf)_helperContext.getDataFactory().create(ReadOnlyContainingIntf.class);
        _innerReadOnly = (ReadOnlyContainedIntf)_helperContext.getDataFactory().create(ReadOnlyContainedIntf.class);
        _inner = (ReadOnlyContainedIntf)_helperContext.getDataFactory().create(ReadOnlyContainedIntf.class);

        _innerReadOnly.setStringB("B");
        _inner.setStringB("B");

        _outer.setInner(_inner);

        // setup read-only part
        ((DataObjectDecorator)_innerReadOnly).getInstance().setReadOnlyMode(false);
        ((DataObjectDecorator)_inner).getInstance().setReadOnlyMode(false);
        ((DataObjectDecorator)_outer).getInstance().setReadOnlyMode(false);

        ((DataObject)_innerReadOnly).setString("stringA", "A");
        ((DataObject)_inner).setString("stringA", "A");

        ((DataObject)_outer).setDataObject("innerReadOnly", (DataObject)_innerReadOnly);

        ((DataObjectDecorator)_innerReadOnly).getInstance().setReadOnlyMode(true);
        ((DataObjectDecorator)_inner).getInstance().setReadOnlyMode(true);
        ((DataObjectDecorator)_outer).getInstance().setReadOnlyMode(true);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDataObjectDelete() {
        assertNotNull(_outer);
        assertNotNull(_innerReadOnly);
        assertNotNull(_inner);

        assertSame(_innerReadOnly, _outer.getInnerReadOnly());
        assertSame(_inner, _outer.getInner());
        assertTrue(((DataObject)_outer).getInstanceProperty("innerReadOnly").isReadOnly());
        assertFalse(((DataObject)_outer).getInstanceProperty("inner").isReadOnly());

        assertEquals("A", _innerReadOnly.getStringA());
        assertEquals("B", _innerReadOnly.getStringB());
        assertTrue(((DataObject)_innerReadOnly).getInstanceProperty("stringA").isReadOnly());
        assertFalse(((DataObject)_innerReadOnly).getInstanceProperty("stringB").isReadOnly());
        assertTrue(((DataObject)_innerReadOnly).isSet("stringA"));
        assertTrue(((DataObject)_innerReadOnly).isSet("stringB"));

        assertEquals("A", _inner.getStringA());
        assertEquals("B", _inner.getStringB());
        assertTrue(((DataObject)_inner).getInstanceProperty("stringA").isReadOnly());
        assertFalse(((DataObject)_inner).getInstanceProperty("stringB").isReadOnly());
        assertTrue(((DataObject)_inner).isSet("stringA"));
        assertTrue(((DataObject)_inner).isSet("stringB"));

        ((DataObject)_innerReadOnly).delete();
        ((DataObject)_inner).delete();

        assertEquals("A", _innerReadOnly.getStringA());
        assertTrue(((DataObject)_innerReadOnly).isSet("stringA"));
        assertNull(_innerReadOnly.getStringB());
        assertFalse(((DataObject)_innerReadOnly).isSet("stringB"));

        assertEquals("A", _inner.getStringA());
        assertTrue(((DataObject)_inner).isSet("stringA"));
        assertNull(_inner.getStringB());
        assertFalse(((DataObject)_inner).isSet("stringB"));

        assertSame(_innerReadOnly, _outer.getInnerReadOnly());
        assertTrue(((DataObject)_outer).isSet("innerReadOnly"));
        assertNull(_outer.getInner());
        assertFalse(((DataObject)_outer).isSet("inner"));

    }
}
