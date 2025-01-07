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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import com.example.myPackage.PurchaseOrderType;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.CopyTestIntf;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.SequencedOppositeIntf;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleIntf1;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.XMLDocument;

public class CopyHelperTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public CopyHelperTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testShallowCopySimple() {
        CopyHelper copyHelper = _helperContext.getCopyHelper();
        EqualityHelper equalityHelper = _helperContext.getEqualityHelper();
        SimpleAttrIntf source = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        source.setName("myName");
        source.setData("myData");
        source.setGreen(true);

        Sequence sourceSequence = ((DataObject)source).getSequence();
        assertEquals(2, sourceSequence.size());
        assertEquals("myData", sourceSequence.getValue(0));
        assertEquals(Boolean.TRUE, sourceSequence.getValue(1));

        SimpleAttrIntf target = (SimpleAttrIntf)copyHelper.copyShallow((DataObject)source);

        List<Property> properties = ((DataObject)source).getInstanceProperties();
        for (Property property: properties) {
            assertEquals(((DataObject)source).get(property), ((DataObject)target).get(property));
        }

        assertTrue(equalityHelper.equalShallow((DataObject)source, (DataObject)target));


    }

    @Test
    public void testCopySimple() {
        CopyHelper copyHelper = _helperContext.getCopyHelper();
        EqualityHelper equalityHelper = _helperContext.getEqualityHelper();
        SimpleAttrIntf source = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        source.setName("myName");
        source.setData("myData");
        source.setGreen(true);

        Sequence sourceSequence = ((DataObject)source).getSequence();
        assertEquals(2, sourceSequence.size());
        assertEquals("myData", sourceSequence.getValue(0));
        assertEquals(Boolean.TRUE, sourceSequence.getValue(1));

        SimpleAttrIntf target = (SimpleAttrIntf)copyHelper.copy((DataObject)source);

        List<Property> properties = ((DataObject)source).getInstanceProperties();
        for (Property property: properties) {
            assertEquals(((DataObject)source).get(property), ((DataObject)target).get(property));
        }

        assertTrue(equalityHelper.equal((DataObject)source, (DataObject)target));


    }

    @Test
    public void testCopyComplex() {

        DataObject rootObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level1aObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level1bObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level2aObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level2bObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        ((SequencedOppositeIntf)rootObject).getMv().add((SequencedOppositeIntf)level1aObject);
        ((SequencedOppositeIntf)rootObject).getMv().add((SequencedOppositeIntf)level1bObject);
        ((SequencedOppositeIntf)level1aObject).getMv().add((SequencedOppositeIntf)level2aObject);
        ((SequencedOppositeIntf)level1bObject).getMv().add((SequencedOppositeIntf)level2bObject);

        ((SequencedOppositeIntf)rootObject).setName("rootObject");
        ((SequencedOppositeIntf)level1aObject).setName("level1aObject");
        ((SequencedOppositeIntf)level1bObject).setName("level1bObject");
        ((SequencedOppositeIntf)level2aObject).setName("level2aObject");
        ((SequencedOppositeIntf)level2bObject).setName("level2bObject");

        SequencedOppositeIntf rootObjectCopy = (SequencedOppositeIntf)_helperContext.getCopyHelper().copy(rootObject);
        assertNotSame(rootObject, rootObjectCopy);

        assertTrue("\nSource: " + rootObject + "\nTarget: " + rootObjectCopy,
            _helperContext.getEqualityHelper().equal(rootObject, (DataObject)rootObjectCopy));

        assertEquals(rootObject.toString(), rootObjectCopy.toString());

    }

    @Test
    public void testCopyShallowComplex() {

        DataObject rootObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level1aObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level1bObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        ((SequencedOppositeIntf)rootObject).getMv().add((SequencedOppositeIntf)level1aObject);
        ((SequencedOppositeIntf)rootObject).getMv().add((SequencedOppositeIntf)level1bObject);

        ((SequencedOppositeIntf)rootObject).setName("rootObject");
        ((SequencedOppositeIntf)level1aObject).setName("level1aObject");
        ((SequencedOppositeIntf)level1bObject).setName("level1bObject");

        SequencedOppositeIntf rootObjectCopy = (SequencedOppositeIntf)_helperContext.getCopyHelper().copyShallow(rootObject);
        assertNotSame(rootObject, rootObjectCopy);

        assertEquals(rootObjectCopy.toString(), 1, ((DataObject)rootObjectCopy).getSequence().size());
        assertEquals(rootObjectCopy.toString(), "rootObject", rootObjectCopy.getName());

    }

    @Test
    public void testOpenProperty() {
        DataObject data1 = _helperContext.getDataFactory().create(SimpleIntf1.class);
        DataObject data2 = _helperContext.getDataFactory().create(SimpleIntf1.class);
        DataObject openIntf = _helperContext.getDataFactory().create(OpenInterface.class);
        DataObject pData = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pData.set("name", "b");
        pData.set("type", data1.getType());
        pData.setBoolean("containment", true);
        pData.setBoolean("many", true);
        Property prData = _helperContext.getTypeHelper().defineOpenContentProperty(null, pData);
        List<DataObject> l = openIntf.getList(prData);
        assertFalse(openIntf.getInstanceProperties().contains(prData));
        l.add(data1);
        l.add(data2);
        assertTrue(openIntf.getInstanceProperties().contains(prData));
        DataObject openIntfCopy = _helperContext.getCopyHelper().copy(openIntf);
        assertNotSame(openIntf, openIntfCopy);

        assertTrue("\nSource: " + openIntf + "\nTarget: " + openIntfCopy,
            _helperContext.getEqualityHelper().equal(openIntf, openIntfCopy));
    }

    @Test
    public void testOpenPropertyWithLogging() {
        DataObject dataGraph = _helperContext.getDataFactory().create(DataGraph.class);

        DataObject data1 = _helperContext.getDataFactory().create(SimpleIntf1.class);

        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set("name", "root");
        propObj.set("type", data1.getType());
        propObj.setBoolean("containment", true);
        propObj.setBoolean("many", true);
        Property property = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);

        final ChangeSummary changeSummary = dataGraph.getChangeSummary();
        changeSummary.beginLogging();
        assertEquals(false, changeSummary.isModified(dataGraph));

        List rootList = dataGraph.getList(property);
        assertEquals(false, changeSummary.isModified(dataGraph));

        assertFalse(dataGraph.getInstanceProperties().contains(property));
        rootList.add(data1);
        assertEquals(true, changeSummary.isModified(dataGraph));

        assertTrue(dataGraph.getInstanceProperties().contains(property));
        assertEquals(data1, ((DataGraph)dataGraph).getRootObject());
    }

    @Test
    public void testPoint4a() {
        CopyTestIntf x = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf a = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf b = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf c = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);

        x.getChildren().add(a);
        a.getChildren().add(b);
        a.getChildren().add(c);
        b.setUnidirectionalRef(c);

        CopyTestIntf a1 = (CopyTestIntf)_helperContext.getCopyHelper().copy((DataObject)a);

        assertTrue("\nSource: " + a + "\nTarget: " + a1,
            _helperContext.getEqualityHelper().equal((DataObject)a, (DataObject)a1));
        CopyTestIntf b1 = a1.getChildren().get(0);
        CopyTestIntf c1 = a1.getChildren().get(1);

        assertNotSame(a, a1);
        assertNotSame(b, b1);
        assertNotSame(c, c1);
        assertSame(c1, b1.getUnidirectionalRef());
        assertEquals(null, ((DataObject)a1).getContainer());
    }

    @Test
    public void testPoint4b() {
        CopyTestIntf x = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf a = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf b = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf c = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);

        x.getChildren().add(a);
        x.getChildren().add(c);
        a.getChildren().add(b);
        b.setUnidirectionalRef(c);

        CopyTestIntf a1 = (CopyTestIntf)_helperContext.getCopyHelper().copy((DataObject)a);

        assertTrue("\nSource: " + a + "\nTarget: " + a1,
            _helperContext.getEqualityHelper().equal((DataObject)a, (DataObject)a1));
        CopyTestIntf b1 = a1.getChildren().get(0);
        CopyTestIntf c1 = b1.getUnidirectionalRef();

        assertNotSame(a, a1);
        assertNotSame(b, b1);
        assertSame(c, c1);
        assertEquals(null, ((DataObject)a1).getContainer());
    }

    @Test
    public void testPoint6() {
        CopyTestIntf x = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf a = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf b = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);
        CopyTestIntf c = (CopyTestIntf)_helperContext.getDataFactory().create(CopyTestIntf.class);

        x.getChildren().add(a);
        a.getChildren().add(b);
        b.setUnidirectionalRef(c);

        try {
            CopyTestIntf a1 = (CopyTestIntf)_helperContext.getCopyHelper().copy((DataObject)a);
            //should never reach the next lines, but to check what is c1
            CopyTestIntf b1 = a1.getChildren().get(0);
            CopyTestIntf c1 = b1.getUnidirectionalRef();
            assertSame(c, c1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            //expected
        }

    }

    @Test
    public void testIntegerRestrictedToInt() throws IOException {
        // load schema by schemaLocation
        URL url = getClass().getClassLoader().getResource(PACKAGE + "ipo_neu.xml");
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);

        DataObject original = document.getRootObject();
        DataObject copy = _helperContext.getCopyHelper().copy(original);

        assertTrue(_helperContext.getEqualityHelper().equal(original, copy));
    }

    @Test
    public void testPOExample() throws Exception {
        final String schemaFileName = PACKAGE + "sdoAnnotationsExample.xsd";
        URL url  = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"poExample.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(uri.openStream(),null,null);

        DataObject original = doc.getRootObject();
        com.sap.xml.datatype.GregorianCalendar orderDateOriginal = ((PurchaseOrderType)original).getOrderDate();
        assertEquals(orderDateOriginal.get(GregorianCalendar.YEAR), 1999); // "1999-10-20");
        assertEquals(orderDateOriginal.get(GregorianCalendar.MONTH), 10);
        assertEquals(orderDateOriginal.get(GregorianCalendar.DAY_OF_MONTH), 20);

        DataObject copy = _helperContext.getCopyHelper().copy(original);
        com.sap.xml.datatype.GregorianCalendar orderDateCopy = ((PurchaseOrderType)copy).getOrderDate();
        assertEquals(orderDateCopy.get(GregorianCalendar.YEAR), 1999); // "1999-10-20");
        assertEquals(orderDateCopy.get(GregorianCalendar.MONTH), 10);
        assertEquals(orderDateCopy.get(GregorianCalendar.DAY_OF_MONTH), 20);

        assertNotSame(orderDateOriginal, orderDateCopy);
    }


}
