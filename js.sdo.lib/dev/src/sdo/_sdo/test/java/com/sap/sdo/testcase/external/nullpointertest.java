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
package com.sap.sdo.testcase.external;

import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * Tests JIRA issue SDO-60.
 * @author D042807
 *
 */
public class NullPointerTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public NullPointerTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testSetNull() {
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        DataObject d = _helperContext.getDataFactory().create(t);
        DataObject d2 = _helperContext.getDataFactory().create(t);

        try {
            d.set((String)null, "value");
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.set((Property)null, "value");
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.setDataObject((Property)null, d2);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.setString((Property)null, "value");
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.setList((String)null, Collections.emptyList());
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testGetNull() {
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        DataObject d = _helperContext.getDataFactory().create(t);
        try {
            d.get((String)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.get((Property)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.getDataObject((Property)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.getString((Property)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.getList((String)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testIsSetNull() {
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        DataObject d = _helperContext.getDataFactory().create(t);
        try {
            d.isSet((String)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.isSet((Property)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testUnsetNull() {
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        DataObject d = _helperContext.getDataFactory().create(t);
        try {
            d.unset((String)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
        try {
            d.unset((Property)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) { //$JL-EXC$
            // expected
        }
    }
}
