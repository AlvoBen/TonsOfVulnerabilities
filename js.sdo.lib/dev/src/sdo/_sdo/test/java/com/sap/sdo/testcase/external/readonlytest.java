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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class ReadOnlyTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ReadOnlyTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testReadOnly() {
        DataObject dO = _helperContext.getDataFactory().create(ReadOnlyInterface.class);
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

        Sequence sequence = dO.getSequence();

        try {
            sequence.add("single", "test");
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

        try {
            sequence.add("multi", "test");
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) { //$JL-EXC$
            //expected
        }
        assertTrue("has changed", _helperContext.getEqualityHelper().equal(dOCopy, dO));

    }

    @Test
    public void testDelete() {
        DataObject propTypeObj = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        propTypeObj.set("name", "PropType");
        propTypeObj.set("uri", "com.sap.sdo.test.readonly");
        DataObject propObj = propTypeObj.createDataObject("property");
        propObj.set("name", "prop");
        propObj.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));

        DataObject typeObj = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObj.set("name", "Container");
        typeObj.set("uri", "com.sap.sdo.test.readonly");
        DataObject readWritePropObj = typeObj.createDataObject("property");
        readWritePropObj.set("name", "readWriteProp");
        readWritePropObj.set("type", propTypeObj);
        readWritePropObj.set("containment", true);
        readWritePropObj.set("many", true);
        DataObject readonlyPropObj = typeObj.createDataObject("property");
        readonlyPropObj.set("name", "readOnlyProp");
        readonlyPropObj.set("type", propTypeObj);
        readonlyPropObj.set("containment", true);
        readonlyPropObj.set("many", true);
        readonlyPropObj.set("readOnly", true);

        Type type = _helperContext.getTypeHelper().define(typeObj);
        assertNotNull(type);

        DataObject root = _helperContext.getDataFactory().create(type);

        List<DataObject> readWriteList = root.getList("readWriteProp");
        for (int i=0; i<5; ++i) {
            DataObject data = root.createDataObject("readWriteProp");
            data.set("prop", "readWrite"+i);
        }

        ((SapDataHelper)_helperContext.getDataHelper()).setReadOnlyMode(root, false);
        List<DataObject> readOnlyList = root.getList("readOnlyProp");
        for (int i=0; i<5; ++i) {
            DataObject data = root.createDataObject("readOnlyProp");
            data.set("prop", "readOnly"+i);
        }
        ((SapDataHelper)_helperContext.getDataHelper()).setReadOnlyMode(root, true);

        root.delete();

        assertNotNull(root);
        assertEquals(0, root.getList("readWriteProp").size());
        assertEquals(5, root.getList("readOnlyProp").size());
        for (DataObject data : (List<DataObject>)root.getList("readOnlyProp")) {
            assertEquals(null, data.get("prop"));
        }
    }
}
