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
import static org.junit.Assert.assertNull;

import javax.xml.stream.XMLStreamException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.xml.XmlParseException;
import com.sap.sdo.testcase.SdoTestCase;

/**
 * @author D042774
 *
 */
public class XmlParseExceptionTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XmlParseExceptionTest(String pContextId, Feature pFeature) {
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

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XmlParseException#XmlParseException()}.
     */
    @Test
    public void testXmlParseException() {
        XmlParseException ex = new XmlParseException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XmlParseException#XmlParseException(java.lang.String)}.
     */
    @Test
    public void testXmlParseExceptionString() {
        XmlParseException ex = new XmlParseException("message");
        assertEquals("message", ex.getMessage());
        assertNull(ex.getCause());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XmlParseException#XmlParseException(java.lang.Throwable)}.
     */
    @Test
    public void testXmlParseExceptionThrowable() {
        NullPointerException npe = new NullPointerException();
        XmlParseException ex = new XmlParseException(npe);
        assertNull(ex.getMessage());
        assertEquals(npe, ex.getCause());

        ex = new XmlParseException(new XMLStreamException(npe));
        if (ex.getMessage() != null) {
            assertEquals("java.lang.NullPointerException", ex.getMessage());
        }
        assertEquals(npe, ex.getCause());

        XMLStreamException streamException = new XMLStreamException();
        ex = new XmlParseException(streamException);
        assertNull(ex.getMessage());
        assertEquals(streamException, ex.getCause());
    }

    /**
     * Test method for {@link com.sap.sdo.impl.xml.XmlParseException#XmlParseException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void testXmlParseExceptionStringThrowable() {
        NullPointerException npe = new NullPointerException();
        XmlParseException ex = new XmlParseException("message", npe);
        assertEquals("message", ex.getMessage());
        assertEquals(npe, ex.getCause());

        ex = new XmlParseException("message", new XMLStreamException(npe));
        assertEquals("message", ex.getMessage());
        assertEquals(npe, ex.getCause());

        XMLStreamException streamException = new XMLStreamException();
        ex = new XmlParseException("message", streamException);
        assertEquals("message", ex.getMessage());
        assertEquals(streamException, ex.getCause());
    }
}
