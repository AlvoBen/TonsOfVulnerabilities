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
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import commonj.sdo.impl.HelperProviderImpl;

/**
 * @author D042774
 *
 */
public class HelperProviderTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public HelperProviderTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#copyHelper()}.
     */
    @Test
    public void testCopyHelper() {
        assertNotNull(HelperProvider.getCopyHelper());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#dataFactory()}.
     */
    @Test
    public void testDataFactory() {
        assertNotNull(HelperProvider.getDataFactory());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#dataHelper()}.
     */
    @Test
    public void testDataHelper() {
        assertNotNull(HelperProvider.getDataHelper());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#equalityHelper()}.
     */
    @Test
    public void testEqualityHelper() {
        assertNotNull(HelperProvider.getEqualityHelper());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#typeHelper()}.
     */
    @Test
    public void testTypeHelper() {
        assertNotNull(HelperProvider.getTypeHelper());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#xmlHelper()}.
     */
    @Test
    public void testXmlHelper() {
        assertNotNull(HelperProvider.getXMLHelper());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#xsdHelper()}.
     */
    @Test
    public void testXsdHelper() {
        assertNotNull(HelperProvider.getXSDHelper());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#resolvable()}.
     */
    @Test
    public void testResolvable() {
        assertNull(HelperProvider.createResolvable());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#resolvable(java.lang.Object)}.
     */
    @Test
    public void testResolvableObject() {
        assertNull(HelperProvider.createResolvable(null));
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#helperContext()}.
     */
    @Test
    public void testHelperContext() {
        assertNotNull(HelperProvider.getDefaultContext());
    }

    /**
     * Test method for {@link commonj.sdo.impl.HelperProviderImpl#HelperProviderImpl()}.
     */
    @Test
    public void testHelperProviderImpl() {
        assertNotNull(new HelperProviderImpl());
    }

    @Test
    public void testNewContextClassloader() {
        assertNotNull(SapHelperProvider.getNewContext(Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testDefaultContextClassloader() {
        assertNotNull(SapHelperProvider.getDefaultContext(Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testNewContextBaseContext() {
        assertNotNull(SapHelperProvider.getNewContext(_helperContext));
    }

    @Test
    public void testGetContextIdClassloader() {
        assertNotNull(SapHelperProvider.getContext("context_id", Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testGetContextIdBaseContext() {
        assertNotNull(SapHelperProvider.getContext("context_id", _helperContext));
    }

    @Test
    public void testRemoveContextIdBaseContext() {
        assertEquals(false, SapHelperProvider.removeContext("context_id2", Thread.currentThread().getContextClassLoader()));
        SapHelperProvider.getContext("context_id2", Thread.currentThread().getContextClassLoader());
        assertEquals(true, SapHelperProvider.removeContext("context_id2", Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testSerializeContexts() throws Exception {
        HelperContext ctx = SapHelperProvider.getNewContext();
        DataObject propObj = ctx.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyConstants.NAME, "globalProp");
        propObj.set(PropertyConstants.URI, "test.com.sap.sdo");
        propObj.set(PropertyConstants.TYPE, ctx.getTypeHelper().getType("commonj.sdo", "String"));
        Property property = ctx.getTypeHelper().defineOpenContentProperty("test.com.sap.sdo", propObj);
        assertNotNull(property);
        assertNull(((DataObject)property).getContainer());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SapHelperProvider.serializeContexts(Collections.singletonList(ctx), out);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        List<HelperContext> contexts = SapHelperProvider.deserializeContexts(in);
        assertNotNull(contexts);
        assertEquals(1, contexts.size());
        
        SapHelperProvider.removeContext(ctx);
        ctx = null;
        
        in = new ByteArrayInputStream(out.toByteArray());
        contexts = SapHelperProvider.deserializeContexts(in);
        assertNotNull(contexts);
        assertEquals(1, contexts.size());
        ctx = contexts.get(0);
        
        property = ctx.getTypeHelper().getOpenContentProperty("test.com.sap.sdo", "globalProp");
        assertNotNull(property);
        assertNull(((DataObject)property).getContainer());        
    }
}
