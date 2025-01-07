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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class XPathAccessIndexMapsTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XPathAccessIndexMapsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private DataObject _container;
    private DataObject _opposite;
    private DataObject _noIndex;

    /**
     * 1 is usefull for test coverage.
     * 100 is usefull for performance tests.
     */
    private int factor = 1;

    @Before
    public void setUp() throws Exception {
        String schemaFileName = PACKAGE + "IndexMaps.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is, null);

        String dataFileName = PACKAGE + "IndexMaps.xml";
        is = getClass().getClassLoader().getResourceAsStream(dataFileName);
        XMLDocument doc = _helperContext.getXMLHelper().load(is);

        DataObject rootElement = doc.getRootObject();

        _container = rootElement.getDataObject("container");
        _opposite = rootElement.getDataObject("opposite");
        _noIndex = rootElement.getDataObject("noIndex");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAccessByContainer() {
        DataObject root = _container;
        runSkript(root, "container");
    }

    @Test
    public void testAccessByOpposite() {
        DataObject root = _opposite;
        runSkript(root, "opposite ");
    }

    @Test
    public void testAccessByNoIndex() {
        DataObject root = _noIndex;
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
            DataObject contained1 = root.getDataObject("references[readOnlyString=\""+ i + "\"]");
            DataObject contained2 = root.getDataObject("references[readOnlyInteger="+ i + "]");
            DataObject contained3 = root.getDataObject("references[string=\""+ i + "\"]");
            DataObject contained4 = root.getDataObject("references[integer="+ i + "]");

            assertSame(contained1, contained2);
            assertSame(contained2, contained3);
            assertSame(contained3, contained4);

            assertEquals("" + i, contained1.getString("readOnlyString"));
            assertEquals(i, contained1.getInt("readOnlyInteger"));
            assertEquals("" + i, contained1.getString("string"));
            assertEquals(i, contained1.getInt("integer"));
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
        DataObject contained1 = root.getDataObject("references[integer=15]");

        contained1.setInt("integer", 2);
        assertEquals(null, root.get("references[integer=15]"));
        DataObject contained2 = root.getDataObject("references[integer=2]");
        assertSame(contained1, contained2);

        contained1.setInt("integer", 17);
        contained2 = root.getDataObject("references[integer=17]");
        //the other object with all values = 17 should be found earlier
        assertNotSame(contained1, contained2);

        contained1.setInt("integer", 15);
        contained2 = root.getDataObject("references[integer=15]");
        assertSame(contained1, contained2);

    }

//    @Test
//    public void testOpenProperty() {
//        for (int i = 0; i < 5*factor; i++) {
//            DataObject contained = (DataObject)_container.getList("references").get(i);
//            contained.set("open", "" + i);
//        }
//        for (int i = 5*factor; i < 10*factor; i++) {
//            DataObject contained = (DataObject)_container.getList("references").get(i);
//            contained.set("open", Integer.valueOf(i));
//        }
//        for (int i = 10*factor; i < 15*factor; i++) {
//            DataObject contained = (DataObject)_container.getList("references").get(i);
//            contained.set("open", Short.valueOf((short)i));
//        }
//        for (int i = 15*factor; i < 20*factor; i++) {
//            DataObject contained = (DataObject)_container.getList("references").get(i);
//            contained.set("open", Long.valueOf((long)i));
//        }
//        long timeIn = new Date().getTime();
//        for (int i = 0*factor; i < 20*factor; i++) {
//            DataObject contained1 = _container.getDataObject("references[open=\""+ i + "\"]");
//
//            assertEquals(i, ((DataObject)contained1).getInt("open"));
//
//            assertEquals("" + i, contained1.getString("readOnlyString"));
//            assertEquals(i, contained1.getInt("readOnlyInteger"));
//            assertEquals("" + i, contained1.getString("string"));
//            assertEquals(i, contained1.getInt("integer"));
//        }
//        long timeOut = new Date().getTime();
//        System.out.println("Open property: " + (timeOut - timeIn));
//    }

    public static void main(String[] args) throws Exception {
        XPathAccessIndexMapsTest test =
            new XPathAccessIndexMapsTest(SapHelperProvider.DEFAULT_CONTEXT_ID, null);
        test.factor = 100;
        test.setUp();
        test.testAccessByNoIndex();
        test.tearDown();
    }
}
