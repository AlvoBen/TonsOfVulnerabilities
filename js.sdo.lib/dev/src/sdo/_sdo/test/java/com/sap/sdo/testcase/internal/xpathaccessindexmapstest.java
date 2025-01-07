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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.IndexMapsContained;
import com.sap.sdo.testcase.typefac.IndexMapsContainer;
import com.sap.sdo.testcase.typefac.IndexMapsNoIndex;
import com.sap.sdo.testcase.typefac.IndexMapsOpposite;

import commonj.sdo.DataObject;

public class XPathAccessIndexMapsTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XPathAccessIndexMapsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private IndexMapsContainer _container;
    private IndexMapsOpposite _opposite;
    private IndexMapsNoIndex _noIndex;

    /**
     * 1 is usefull for test coverage.
     * 100 is usefull for performance tests.
     */
    private int factor = 1;

    @Before
    public void setUp() throws Exception {
        _container = (IndexMapsContainer)_helperContext.getDataFactory().create(IndexMapsContainer.class);
        _opposite = (IndexMapsOpposite)_helperContext.getDataFactory().create(IndexMapsOpposite.class);
        _noIndex = (IndexMapsNoIndex)_helperContext.getDataFactory().create(IndexMapsNoIndex.class);
        for (int i = 0; i < 20*factor; i++) {
            DataObjectDecorator containedDo = (DataObjectDecorator)_helperContext.getDataFactory().create(IndexMapsContained.class);
            IndexMapsContained contained = (IndexMapsContained)containedDo;

            containedDo.getInstance().setReadOnlyMode(false);
            containedDo.set("readOnlyString", "" + i);
            containedDo.set("readOnlyInteger", i);
            containedDo.getInstance().setReadOnlyMode(true);

            contained.setString("" + i);
            contained.setInteger(i);

            _container.getReferences().add(contained);
            _opposite.getReferences().add(contained);
            _noIndex.getReferences().add(contained);
        }


    }

    @Test
    public void testAccessByContainer() {
        DataObject root = (DataObject)_container;
        runSkript(root, "container");
    }

    @Test
    public void testAccessByOpposite() {
        DataObject root = (DataObject)_opposite;
        runSkript(root, "opposite ");
    }

    @Test
    public void testAccessByNoIndex() {
        DataObject root = (DataObject)_noIndex;
        runSkript(root, "no index ");
    }

    private void runSkript(DataObject root, String rootName) {
        System.out.println();
        System.out.print("1st access by " + rootName + ": ");
        access(root);
        System.out.print("2nd access by " + rootName + ": ");
        access(root);
        resortValues(root);
        System.out.println("Resort values");
        System.out.print("1st access by " + rootName + ": ");
        access(root);
        System.out.print("2nd access by " + rootName + ": ");
        access(root);
        System.out.println("Change values");
        changeProperty(root);
        System.out.print("1st access by " + rootName + ": ");
        access(root);
        System.out.print("2nd access by " + rootName + ": ");
        access(root);

    }

    private void access(DataObject root) {
        long timeIn = new Date().getTime();
        for (int i = 0; i < 20*factor; i++) {
            IndexMapsContained contained1 = (IndexMapsContained)root.get("references[readOnlyString=\""+ i + "\"]");
            IndexMapsContained contained2 = (IndexMapsContained)root.get("references[readOnlyInteger="+ i + "]");
            IndexMapsContained contained3 = (IndexMapsContained)root.get("references[string=\""+ i + "\"]");
            IndexMapsContained contained4 = (IndexMapsContained)root.get("references[integer="+ i + "]");

            assertSame(contained1, contained2);
            assertSame(contained2, contained3);
            assertSame(contained3, contained4);

            assertEquals("" + i, contained1.getReadOnlyString());
            assertEquals(Integer.valueOf(i), contained1.getReadOnlyInteger());
            assertEquals("" + i, contained1.getString());
            assertEquals(Integer.valueOf(i), contained1.getInteger());
        }
        long timeOut = new Date().getTime();
        System.out.println(timeOut - timeIn);
    }

    private void resortValues(DataObject root) {
        List values = root.getList("references");
        List newValues = new ArrayList(values.size());
        for (int i = values.size() - 1; i >= 0; i--) {
            newValues.add(values.get(i));
        }
        root.setList("references", newValues);
    }

    private void changeProperty(DataObject root) {
        IndexMapsContained contained1 = (IndexMapsContained)root.get("references[integer=15]");

        contained1.setInteger(2);
        assertEquals(null, root.get("references[integer=15]"));
        IndexMapsContained contained2 = (IndexMapsContained)root.get("references[integer=2]");
        assertSame(contained1, contained2);

        contained1.setInteger(17);
        contained2 = (IndexMapsContained)root.get("references[integer=17]");
        //the other object with all values = 17 should be found earlier
        assertNotSame(contained1, contained2);

        contained1.setInteger(15);
        contained2 = (IndexMapsContained)root.get("references[integer=15]");
        assertSame(contained1, contained2);

    }

    @Test
    public void testOpenProperty() {
        for (int i = 0; i < 5*factor; i++) {
            DataObject contained = (DataObject)_container.getReferences().get(i);
            contained.set("open", "" + i);
        }
        for (int i = 5*factor; i < 10*factor; i++) {
            DataObject contained = (DataObject)_container.getReferences().get(i);
            contained.set("open", Integer.valueOf(i));
        }
        for (int i = 10*factor; i < 15*factor; i++) {
            DataObject contained = (DataObject)_container.getReferences().get(i);
            contained.set("open", Short.valueOf((short)i));
        }
        for (int i = 15*factor; i < 20*factor; i++) {
            DataObject contained = (DataObject)_container.getReferences().get(i);
            contained.set("open", Long.valueOf(i));
        }
        long timeIn = new Date().getTime();
        for (int i = 0*factor; i < 20*factor; i++) {
            IndexMapsContained contained1 = (IndexMapsContained)((DataObject)_container).get("references[open=\""+ i + "\"]");

            assertEquals(i, ((DataObject)contained1).getInt("open"));

            assertEquals("" + i, contained1.getReadOnlyString());
            assertEquals(Integer.valueOf(i), contained1.getReadOnlyInteger());
            assertEquals("" + i, contained1.getString());
            assertEquals(Integer.valueOf(i), contained1.getInteger());
}
        long timeOut = new Date().getTime();
        System.out.println("Open property: " + (timeOut - timeIn));
    }

}
