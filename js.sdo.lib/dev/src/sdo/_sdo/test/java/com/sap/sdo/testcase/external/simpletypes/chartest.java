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
package com.sap.sdo.testcase.external.simpletypes;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class CharTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public CharTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;

    private ICharTest b;

    @Before
    public void setUp() throws Exception {
        b = (ICharTest)_helperContext.getDataFactory().create(ICharTest.class);
        d = (DataObject)b;
    }

    @Test
    public void testCharSimpleTypeDefaults() {
        assertTrue("achar should have defaul 'a', but was "+b.getAchar(), b.getAchar() == 'a');
        assertTrue("\"achar\" should have default 'a'", d
                .getChar("achar") == 'a');
    }

    @Test
    public void testCharSimpleTypeSetters() {
        b.setAchar('b');
        assertTrue("achar should have changed to 'b'",
                b.getAchar() == 'b');
        assertTrue("\"achar\" should have changed to 'b'", d
                .getChar("achar") == 'b');
    }

    @Test
    public void testCharSimpleTypeConversions() {
        d.setString("achar", "c");
        assertTrue("achar should have changed to 'c'",
                b.getAchar() == 'c');
        String s = d.getString("achar");
        assertTrue("achar should have String value \"c\"", "c".equals(s));
    }
}
