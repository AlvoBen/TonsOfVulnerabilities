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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapDataHelper;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.ReadOnlyInterface;

import commonj.sdo.DataObject;
import commonj.sdo.Sequence;

/**
 * @author D042774
 *
 */
public class ReadOnlyExtendedTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ReadOnlyExtendedTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testReadOnly() {
        DataObject dO = _helperContext.getDataFactory().create(ReadOnlyInterface.class);
        ((SapDataHelper)_helperContext.getDataHelper()).setReadOnlyMode(dO, false);
        Sequence sequence = dO.getSequence();
        sequence.add("multi", "1");
        sequence.add("single", "2");
        sequence.add("multi", "3");
        ((SapDataHelper)_helperContext.getDataHelper()).setReadOnlyMode(dO, true);

        DataObject dOCopy = _helperContext.getCopyHelper().copy(dO);
        assertTrue(_helperContext.getEqualityHelper().equal(dOCopy, dO));

        try {
            dO.set("single", "test");
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        List<String> multi = new ArrayList();
        multi.add("1");
        multi.add("3");
        dO.set("multi", multi);

        multi.add("1.");

        try {
            dO.set("multi", multi);
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        multi = dO.getList("multi");

        try {
            multi.add("1.");
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        sequence = dO.getSequence();

        try {
            sequence.add("single", "test");
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected because of read-only
        } catch(IllegalStateException e) {
            //expected because it is already set
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        try {
            sequence.add(1, "multi", "test");
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        try {
            sequence.remove(0);
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        try {
            sequence.remove(1);
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        try {
            multi.remove(1);
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));
    }
}
