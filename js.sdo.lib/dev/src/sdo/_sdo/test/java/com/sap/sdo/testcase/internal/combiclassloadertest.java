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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataFactoryImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.testcase.SdoTestCase;

/**
 * @author D042774
 *
 */
public class CombiClassLoaderTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public CombiClassLoaderTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private Constructor _constructor;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        DataFactoryImpl dataFactory = (DataFactoryImpl)_helperContext.getDataFactory();
        Class[] classes = dataFactory.getClass().getDeclaredClasses();
        assertNotNull(classes);
        assertEquals(1, classes.length);

        Constructor[] constructors = classes[0].getConstructors();
        assertNotNull(constructors);
        assertEquals(1, constructors.length);
        _constructor = constructors[0];
        _constructor.setAccessible(true);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _constructor = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.DataFactoryImpl.CombiClassLoader#hashCode()}.
     */
    @Test
    public void testHashCode() throws Exception {
        Object newInstance = _constructor.newInstance(getClass().getClassLoader());
        assertNotNull(newInstance);
        assertEquals(
            "com.sap.sdo.impl.objects.DataFactoryImpl$CombiClassLoader",
            newInstance.getClass().getName());
        assertEquals(
            getClass().getClassLoader().hashCode() ^ DataObjectDecorator.class.getClassLoader().hashCode(),
            newInstance.hashCode());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.DataFactoryImpl.CombiClassLoader#loadClass(java.lang.String)}.
     */
    @Test
    public void testLoadClassString() throws Exception {
        ClassLoader newInstance = (ClassLoader)_constructor.newInstance(getClass().getClassLoader());
        assertNotNull(newInstance);
        assertEquals(
            "com.sap.sdo.impl.objects.DataFactoryImpl$CombiClassLoader",
            newInstance.getClass().getName());

        assertEquals(String.class, newInstance.loadClass("java.lang.String"));
        try {
            newInstance.loadClass("non.existing.Class");
            fail("expected ClassNotFoundException");
        } catch (ClassNotFoundException ex) {
            assertEquals("non.existing.Class", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.objects.DataFactoryImpl.CombiClassLoader#equals(java.lang.Object)}.
     * @throws Exception
     */
    @Test
    public void testEqualsObject() throws Exception {
        Object newInstance = _constructor.newInstance(getClass().getClassLoader());
        assertNotNull(newInstance);
        assertEquals(
            "com.sap.sdo.impl.objects.DataFactoryImpl$CombiClassLoader",
            newInstance.getClass().getName());
        assertTrue(newInstance.equals(newInstance));
        assertFalse(newInstance.equals(this));
    }

}
