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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.CopyHelperDelegator;
import com.sap.sdo.impl.objects.EqualityHelperDelegator;
import com.sap.sdo.impl.types.TypeHelperDelegator;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.TypeHelper;

/**
 * @author D042774
 *
 */
public class DelegatorTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public DelegatorTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
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

    @Test
    public void testCopyHelperDelegator() {
        CopyHelper delegator = CopyHelperDelegator.getInstance();
        assertNotNull(delegator);
        assertTrue(
            delegator.getClass().getName(),
            delegator instanceof CopyHelperDelegator);

        assertTrue(
            delegator.toString(),
            delegator.toString().startsWith("com.sap.sdo.impl.objects.CopyHelperDelegator@"));
        assertTrue(
            delegator.toString(),
            delegator.toString().contains(" delegate: com.sap.sdo.impl.objects.CopyHelperImpl@"));
    }

    @Test
    public void testEqualityHelperDelegator() {
        EqualityHelper delegator = EqualityHelperDelegator.getInstance();
        assertNotNull(delegator);
        assertTrue(
            delegator.getClass().getName(),
            delegator instanceof EqualityHelperDelegator);

        assertTrue(
            delegator.toString(),
            delegator.toString().startsWith("com.sap.sdo.impl.objects.EqualityHelperDelegator@"));
        assertTrue(
            delegator.toString(),
            delegator.toString().contains(" delegate: com.sap.sdo.impl.objects.EqualityHelperImpl@"));
    }

    @Test
    public void testTypeHelperDelegator() {
        TypeHelper delegator = TypeHelperDelegator.getInstance();
        assertNotNull(delegator);
        assertTrue(
            delegator.getClass().getName(),
            delegator instanceof TypeHelperDelegator);

        assertTrue(
            delegator.toString(),
            delegator.toString().startsWith("com.sap.sdo.impl.types.TypeHelperDelegator@"));
        assertTrue(
            delegator.toString(),
            delegator.toString().contains(" delegate: com.sap.sdo.impl.types.TypeHelperImpl@"));
    }
}
