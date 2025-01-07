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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.DoubleMVIntf;
import com.sap.sdo.testcase.typefac.NestedMVInner;
import com.sap.sdo.testcase.typefac.SimpleMVIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;

public class ManyValuePropertyTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ManyValuePropertyTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testEmptyMVProperty() {
        DataObject d = _helperContext.getDataFactory().create(SimpleMVIntf.class);
        Object v = d.get("names");
        assertNotNull(v);
        assertTrue(
                "many-value property lookup should always return list",
                v instanceof List);
        Object l = d.getList("names");
        assertTrue(
                "many-value property lookup getList should always return list",
                l instanceof List);
    }

    @Test
    public void testSimpleInsert() {
        List a = Arrays.asList(new String[] { "one", "two", "three", "four" });
        DataObject d = _helperContext.getDataFactory().create(SimpleMVIntf.class);
        List b = d.getList("names");
        for (int i = 0; i < a.size(); i++) {
            b.add(a.get(i));
        }
        assertTrue("list does not match inserts", a.equals(b));
        assertTrue("sequence has wrong size", d.getSequence().size() == a
                .size());
    }

    @Test
    public void testSetCollection() {
        List a = Arrays.asList(new String[] { "a", "b", "c", "d" });
        Collection col = new TreeSet(a);
        DataObject d = _helperContext.getDataFactory().create(SimpleMVIntf.class);
        d.set("names", col);
        List b = d.getList("names");
        assertEquals("list does not match inserts", a, b);
        assertEquals("sequence has wrong size", a.size(), d.getSequence().size());
    }

    @Test
    public void testSetObject() {
        DataObject d = _helperContext.getDataFactory().create(SimpleMVIntf.class);
        d.set("names", "Object");
        //see Jira SDO-224
        assertEquals(Collections.singletonList("Object"), d.get("names"));
    }

    @Test
    public void testSetCollectionNonSeq() {
        List a = Arrays.asList(new String[] { "a", "b", "c", "d" });
        Collection col = new TreeSet(a);
        DataObject d = _helperContext.getDataFactory().create(NestedMVInner.class);
        d.set("mv", col);
        List b = d.getList("mv");
        assertEquals("list does not match inserts", a, b);
    }

    @Test
    public void testIndexAddAllSet() {
        List a = new ArrayList(Arrays.asList(new String[] { "a", "b", "c", "d" }));
        List a2 = Arrays.asList(new String[] { "e", "f"});
        DataObject d = _helperContext.getDataFactory().create(NestedMVInner.class);
        List b = d.getList("mv");
        b.addAll(a);
        assertEquals(a, b);

        a.addAll(2, a2);
        b.addAll(2, a2);
        assertEquals(a, b);

        a.set(1, "b2");
        b.set(1, "b2");
        assertEquals(a, b);
    }

    @Test
    public void testSetObjectNonSeq() {
        DataObject d = _helperContext.getDataFactory().create(NestedMVInner.class);
        d.set("mv", "Object");
        //see Jira SDO-224
        assertEquals(Collections.singletonList("Object"), d.get("mv"));
    }

    @Test
    public void testSimpleInsertAndDelete() {
        List a = Arrays.asList(new String[] { "one", "two", "three", "four" });
        DataObject d = _helperContext.getDataFactory().create(SimpleMVIntf.class);
        List b = d.getList("names");
        b.addAll(a);
        assertTrue("list does not match inserts", a.equals(b));
        b.removeAll(a);
        assertTrue("list not empty after removal", b.isEmpty());
        assertTrue("sequence not empty after removal", d.getSequence()
                .size() == 0);
    }

    @Test
    public void testDoubleInsertSeq() {
        List a1 = Arrays.asList(new String[] { "one", "two", "three", "four" });
        List a2 = Arrays.asList(new String[] { "up", "down", "left", "right" });
        DataObject d = _helperContext.getDataFactory().create(DoubleMVIntf.class);
        d.getList("names").addAll(a1);
        d.getList("places").addAll(a2);
        // sequence should be all a1 and then all a2
        assertEquals("list ist not equal", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
    }

    @Test
    public void testDoubleInsertMixed() {
        List a1 = Arrays.asList(new String[] { "one", "two", "three", "four" });
        List a2 = Arrays.asList(new String[] { "up", "down", "left", "right" });
        DataObject d = _helperContext.getDataFactory().create(DoubleMVIntf.class);
        List b = new ArrayList(a1.size() + a2.size());
        for (int i = 0; i < a1.size(); i++) {
            d.getList("names").add(a1.get(i));
            b.add(a1.get(i));
            d.getList("places").add(a2.get(i));
            b.add(a2.get(i));
        }
        assertEquals("list ist not equal", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
    }

    @Test
    public void testDoubleInsertMixedInsert() {
        List a1 = new ArrayList(Arrays.asList(new String[] { "one", "two",
                "three", "four" }));
        List a2 = new ArrayList(Arrays.asList(new String[] { "up", "down", "left", "right" }));
        DataObject d = _helperContext.getDataFactory().create(DoubleMVIntf.class);
        List b = new ArrayList(a1.size() + a2.size());
        for (int i = 0; i < a1.size(); i++) {
            d.getList("names").add(a1.get(i));
            b.add(a1.get(i));
            d.getList("places").add(a2.get(i));
            b.add(a2.get(i));
        }
        String s = "three point five";
        d.getList("names").add(3, s);
        a1.add(3, s);
        assertEquals("lists dont match", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
        d.getList("names").remove(3);
        a1.remove(3);
        assertEquals("lists dont match", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
        s = "middle";
        d.getList("places").add(0, s);
        a2.add(0, s);
        assertEquals("lists dont match", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
        s = "zero";
        d.getList("names").add(0, s);
        a1.add(0, s);
        assertEquals("lists dont match", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
        d.getList("names").remove(0);
        a1.remove(0);
        assertEquals("lists dont match", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
        d.getList("places").remove(0);
        a2.remove(0);
        assertEquals("lists dont match", a1, d.getList("names"));
        assertEquals("list ist not equal", a2, d.getList("places"));
        compareSeqListOrder(d.getSequence(), d.getList("names"), d.getInstanceProperty("names"));
        compareSeqListOrder(d.getSequence(), d.getList("places"), d.getInstanceProperty("places"));
    }

    @Test
    public void testDoubleInsertMixedSequence() {
        DataObject d = _helperContext.getDataFactory().create(DoubleMVIntf.class);
        Property namesProp = d.getInstanceProperty("names");
        Property placesProp = d.getInstanceProperty("places");
        Sequence sequence = d.getSequence();
        List<String> referenceList = new ArrayList<String>();
        sequence.add(namesProp, "n1");
        referenceList.add("n1");
        sequence.add(placesProp, "p1");
        referenceList.add("p1");
        sequence.add(placesProp, "p2");
        referenceList.add("p2");
        sequence.add(namesProp, "n2");
        referenceList.add("n2");
        sequence.add(placesProp, "p3");
        referenceList.add("p3");
        sequence.add(namesProp, "n3");
        referenceList.add("n3");

        compareSeqListOrder(d.getSequence(), d.getList(namesProp), namesProp);
        compareSeqListOrder(d.getSequence(), d.getList(placesProp), placesProp);
        compareSequenceRefList(sequence, referenceList);

        sequence.add(2, placesProp, "p1.5");
        referenceList.add(2, "p1.5");

        compareSeqListOrder(d.getSequence(), d.getList(namesProp), namesProp);
        compareSeqListOrder(d.getSequence(), d.getList(placesProp), placesProp);
        compareSequenceRefList(sequence, referenceList);

        sequence.add(7, namesProp, "n4");
        referenceList.add(7, "n4");

        compareSeqListOrder(d.getSequence(), d.getList(namesProp), namesProp);
        compareSeqListOrder(d.getSequence(), d.getList(placesProp), placesProp);
        compareSequenceRefList(sequence, referenceList);

        sequence.add(0, namesProp, "n0.5");
        referenceList.add(0, "n0.5");

        compareSeqListOrder(d.getSequence(), d.getList(namesProp), namesProp);
        compareSeqListOrder(d.getSequence(), d.getList(placesProp), placesProp);
        compareSequenceRefList(sequence, referenceList);

        sequence.remove(sequence.size() - 1);
        referenceList.remove(referenceList.size() - 1);

        compareSeqListOrder(d.getSequence(), d.getList(namesProp), namesProp);
        compareSeqListOrder(d.getSequence(), d.getList(placesProp), placesProp);

        compareSequenceRefList(sequence, referenceList);
        sequence.remove(0);
        referenceList.remove(0);

        compareSeqListOrder(d.getSequence(), d.getList(namesProp), namesProp);
        compareSeqListOrder(d.getSequence(), d.getList(placesProp), placesProp);
        compareSequenceRefList(sequence, referenceList);

        sequence.remove(4);
        referenceList.remove(4);

        compareSeqListOrder(d.getSequence(), d.getList(namesProp), namesProp);
        compareSeqListOrder(d.getSequence(), d.getList(placesProp), placesProp);
        compareSequenceRefList(sequence, referenceList);
    }

    @Test
    public void testRemove() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "remove.xsd");
        _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "remove.xml");
        DataObject l_dataRoot = _helperContext.getXMLHelper().load(xmlUrl.openStream()).getRootObject();
        DataObject l_dataItems = l_dataRoot.getDataObject(l_dataRoot.getType().getProperty("items"));
        List<DataObject> l_listDataItem = l_dataItems.getList(l_dataItems.getType().getProperty("item"));
        DataObject removed = l_listDataItem.remove(0);
        System.out.println(removed);
        assertNotNull(removed);
    }

    private void compareSequenceRefList(Sequence pSequence, List<String> pReferenceList) {
        assertEquals(pReferenceList.size(), pSequence.size());
        for (int i = 0; i < pSequence.size(); i++) {
            assertEquals(pReferenceList.get(i), pSequence.getValue(i));
        }
    }

    private void compareSeqListOrder(Sequence pSequence, List pList, Property pProperty) {
        int propIndex = 0;
        for (int seqIndex = 0; seqIndex < pSequence.size(); seqIndex++) {
            if (pSequence.getProperty(seqIndex).equals(pProperty)) {
                assertSame("Wrong order in sequence: seqIndex = " + seqIndex + "\n" +
                    pSequence + "\npropIndex = " + propIndex + "\n" + pList,
                    pSequence.getValue(seqIndex), pList.get(propIndex));
                propIndex++;
            }
        }
        assertEquals("Not all propvalues are in sequence", pList.size(), propIndex);
    }


}
