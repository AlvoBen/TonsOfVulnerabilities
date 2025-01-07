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

import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.data.DataHelperImpl;
import com.sap.sdo.impl.objects.CopyHelperImpl;
import com.sap.sdo.impl.objects.DataFactoryImpl;
import com.sap.sdo.impl.objects.EqualityHelperImpl;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.xml.XMLHelperImpl;
import com.sap.sdo.impl.xml.XSDHelperImpl;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * @author D042774
 *
 */
public class HelperContextTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public HelperContextTest(String pContextId, Feature pFeature) {
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
     * Test method for {@link com.sap.sdo.impl.context.HelperContextImpl#getCopyHelper()}.
     */
    @Test
    public void testGetCopyHelper() {
        CopyHelper copyHelper = _helperContext.getCopyHelper();
        assertSame(copyHelper, CopyHelperImpl.getInstance(_helperContext));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.context.HelperContextImpl#getDataFactory()}.
     */
    @Test
    public void testGetDataFactory() {
        DataFactory dataFactory = _helperContext.getDataFactory();
        assertSame(dataFactory, DataFactoryImpl.getInstance(_helperContext));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.context.HelperContextImpl#getDataHelper()}.
     */
    @Test
    public void testGetDataHelper() {
        DataHelper dataHelper = _helperContext.getDataHelper();
        assertSame(dataHelper, DataHelperImpl.getInstance(_helperContext));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.context.HelperContextImpl#getEqualityHelper()}.
     */
    @Test
    public void testGetEqualityHelper() {
        EqualityHelper eqHelper = _helperContext.getEqualityHelper();
        assertSame(eqHelper, EqualityHelperImpl.getInstance(_helperContext));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.context.HelperContextImpl#getTypeHelper()}.
     */
    @Test
    public void testGetTypeHelper() {
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        assertSame(typeHelper, TypeHelperImpl.getInstance((HelperContextImpl)_helperContext));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.context.HelperContextImpl#getXMLHelper()}.
     */
    @Test
    public void testGetXMLHelper() {
        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        assertSame(xmlHelper, XMLHelperImpl.getInstance(_helperContext));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.context.HelperContextImpl#getXSDHelper()}.
     */
    @Test
    public void testGetXSDHelper() {
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        assertSame(xsdHelper, XSDHelperImpl.getInstance(_helperContext));
    }

}
