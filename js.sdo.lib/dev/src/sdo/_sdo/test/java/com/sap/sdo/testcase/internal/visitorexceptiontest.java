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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.util.VisitorException;
import com.sap.sdo.testcase.SdoTestCase;

/**
 * @author D042774
 *
 */
public class VisitorExceptionTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public VisitorExceptionTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /**
     * Test method for {@link com.sap.sdo.impl.util.VisitorException#VisitorException()}.
     */
    @Test
    public void testVisitorException() {
        assertNotNull(new VisitorException());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.util.VisitorException#VisitorException(java.lang.String)}.
     */
    @Test
    public void testVisitorExceptionString() {
        VisitorException ex = new VisitorException("string");
        assertNotNull(ex);
        assertEquals("string", ex.getMessage());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.util.VisitorException#VisitorException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void testVisitorExceptionStringThrowable() {
        NullPointerException nullPointerEx = new NullPointerException();
        VisitorException ex = new VisitorException("string", nullPointerEx );
        assertNotNull(ex);
        assertEquals("string", ex.getMessage());
        assertSame(nullPointerEx, ex.getCause());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.util.VisitorException#VisitorException(java.lang.Throwable)}.
     */
    @Test
    public void testVisitorExceptionThrowable() {
        NullPointerException nullPointerEx = new NullPointerException();
        VisitorException ex = new VisitorException(nullPointerEx );
        assertNotNull(ex);
        assertSame(nullPointerEx, ex.getCause());
    }

}
