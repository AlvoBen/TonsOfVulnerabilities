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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class StringsTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public StringsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject d;

    private IStringsTest b;

    @Before
    public void setUp() throws Exception {
        b = (IStringsTest)_helperContext.getDataFactory().create(IStringsTest.class);
        d = (DataObject)b;
    }

    @Test
    public void testStringsSimpleTypeDefaults() {
        assertNull(
            "unset strings should return NULL, but was " + b.getStrings(),
            b.getStrings());

        assertNull(
            "unset \"strings\" should return NULL, but was " + d.get("strings"),
            d.get("strings"));
    }

    @Test
    public void testStringsSimpleTypeSetters() {
        b.setStrings(Arrays.asList(new String[]{"a", "b"}));
        assertNotNull("strings should have list, but was NULL", b.getStrings());
        assertTrue(
            "strings should have list with 2 elements, but was " + b.getStrings(),
            b.getStrings().size()==2);
        assertEquals("first element of list", "a" , b.getStrings().get(0));
        assertEquals("second element of list", "b" , b.getStrings().get(1));

        assertNotNull("\"strings\" should have list, but was NULL", d.get("strings"));
        assertTrue(
            "\"strings\" should have list with 2 elements, but was " + d.get("strings"),
            ((List<String>)d.get("strings")).size()==2);
        assertEquals("first element of list", "a" , ((List<String>)d.get("strings")).get(0));
        assertEquals("second element of list", "b" , ((List<String>)d.get("strings")).get(1));
}

    @Test
    public void testStringsSimpleTypeConversions() {
        d.setString("strings", "a b");
        assertTrue("strings should have changed to array containing 'a' and 'b', but was " + b.getStrings(),
            b.getStrings().size() == 2);
        assertTrue("strings should have changed to array containing 'a' and 'b', but was " + b.getStrings(),
            "a".equals(b.getStrings().get(0)));
        assertTrue("strings should have changed to array containing 'a' and 'b', but was " + b.getStrings(),
            "b".equals(b.getStrings().get(1)));
        String s = d.getString("strings");
        assertTrue("strings should have String value \"a b\"", "a b".equals(s));
    }
}
