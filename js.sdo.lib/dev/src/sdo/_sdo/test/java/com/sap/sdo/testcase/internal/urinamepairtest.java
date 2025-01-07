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

import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.util.URINamePair;

/**
 * @author D042774
 *
 */
public class UriNamePairTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link com.sap.sdo.impl.util.URINamePair#URINamePair(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testURINamePairStringString() {
        try {
            new URINamePair(null, null);
            fail("expected NullPointerException");
        } catch (NullPointerException ex) {
            assertEquals("name is null", ex.getMessage());
        }
    }
    
    @Test
    public void testEscape() throws URISyntaxException {
        URINamePair unp = new URINamePair("demo.sap.com/test12/", "#response");
        String sdo = unp.toStandardSdoFormat();
        assertEquals("demo.sap.com/test12/#\\#response", sdo);
        assertEquals(unp, URINamePair.fromStandardSdoFormat(sdo));
        
        unp = new URINamePair("demo.sap.com/test12/", "response#value");
        sdo = unp.toStandardSdoFormat();
        assertEquals("demo.sap.com/test12/#response\\#value", sdo);
        assertEquals(unp, URINamePair.fromStandardSdoFormat(sdo));

        unp = new URINamePair("demo.sap.com/test12/", "response#");
        sdo = unp.toStandardSdoFormat();
        assertEquals("demo.sap.com/test12/#response\\#", sdo);
        assertEquals(unp, URINamePair.fromStandardSdoFormat(sdo));

        unp = new URINamePair("demo.sap.com/test12/", "#response#value#");
        sdo = unp.toStandardSdoFormat();
        assertEquals("demo.sap.com/test12/#\\#response\\#value\\#", sdo);
        assertEquals(unp, URINamePair.fromStandardSdoFormat(sdo));

        unp = new URINamePair("demo.sap.com/test12/", "##response##value##");
        sdo = unp.toStandardSdoFormat();
        assertEquals("demo.sap.com/test12/#\\#\\#response\\#\\#value\\#\\#", sdo);
        assertEquals(unp, URINamePair.fromStandardSdoFormat(sdo));
    }

}
