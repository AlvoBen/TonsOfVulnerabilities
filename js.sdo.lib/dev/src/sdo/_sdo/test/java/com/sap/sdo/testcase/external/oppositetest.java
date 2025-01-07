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

import static com.sap.sdo.api.util.URINamePair.PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.ValidationHelper;
import com.sap.sdo.impl.objects.ValidationHelper.ValidationException;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OppositePropsA;
import com.sap.sdo.testcase.typefac.OppositePropsB;
import com.sap.sdo.testcase.typefac.OppositePropsContainA;
import com.sap.sdo.testcase.typefac.OppositePropsContainB;
import com.sap.sdo.testcase.typefac.SequencedOppositeIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class OppositeTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public OppositeTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /*
     * @see TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOppositeAccess() {
        OppositePropsA oppA = (OppositePropsA)_helperContext.getDataFactory().create(OppositePropsA.class);
        OppositePropsB oppB1 = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        OppositePropsB oppB2 = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        Property propBs = ((DataObject)oppA).getProperty("bs");
        Property propA = ((DataObject)oppB1).getProperty("a");
        assertEquals("opposite property is not equal", propA, propBs.getOpposite());
        assertEquals("opposite property is not equal", propBs, propA.getOpposite());
        ((DataObject)oppB1).set("a", oppA);
        ((DataObject)oppB2).set("a", oppA);
        assertEquals("oppB1 doesn't contain expected object", oppA, oppB1.getA());
        assertEquals("oppB2 doesn't contain expected object", oppA, oppB2.getA());
        assertNotNull("a list should be returned", oppA.getBs());
        assertFalse("list of opposites is empty", oppA.getBs().isEmpty());
        for (OppositePropsB b : oppA.getBs()) {
            assertTrue("list elements aren't one of oppB1 or oppB2",
                oppB1.equals(b) || oppB2.equals(b));
        }
        oppA.getBs().clear();
        assertFalse("property is not empty", ((DataObject)oppA).isSet("bs"));
        assertFalse("property is not empty", ((DataObject)oppB1).isSet("a"));
        assertFalse("property is not empty", ((DataObject)oppB2).isSet("a"));
    }

    @Test
    public void testSequencedOppositeAccess() {
        SequencedOppositeIntf object1 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object2 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object3 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        Property svProperty = ((DataObject)object1).getProperty("sv");
        Property mvProperty = ((DataObject)object1).getProperty("mv");

        assertFalse(svProperty.isContainment());
        assertTrue(mvProperty.isContainment());
        assertEquals(svProperty, mvProperty.getOpposite());
        assertEquals(mvProperty, svProperty.getOpposite());

        object1.setName("object1");
        object2.setName("object2");
        object3.setName("object3");

        List<SequencedOppositeIntf> values = new ArrayList<SequencedOppositeIntf>();
        values.add(object2);
        values.add(object3);
        object1.setMv(values);

        assertEquals(object1.toString(), 3, ((DataObject)object1).getSequence().size());
        assertEquals("object1", ((DataObject)object1).getSequence().getValue(0));
        assertEquals(object2, ((DataObject)object1).getSequence().getValue(1));
        assertEquals(object3, ((DataObject)object1).getSequence().getValue(2));

        assertEquals(object1, object2.getSv());
        assertEquals(object1, object3.getSv());

        values = new ArrayList<SequencedOppositeIntf>();
        values.add(object3);
        values.add(object2);
        object1.setMv(values);

        assertEquals(object1.toString(), 3, ((DataObject)object1).getSequence().size());
        assertEquals("object1", ((DataObject)object1).getSequence().getValue(0));
        assertEquals(object3, ((DataObject)object1).getSequence().getValue(1));
        assertEquals(object2, ((DataObject)object1).getSequence().getValue(2));

        assertEquals(object1, object2.getSv());
        assertEquals(object1, object3.getSv());

        object2.setSv(object3);

        assertEquals(object1.toString(), 2, ((DataObject)object1).getSequence().size());
        assertEquals("object1", ((DataObject)object1).getSequence().getValue(0));
        assertEquals(object3, ((DataObject)object1).getSequence().getValue(1));

        assertEquals(object3, object2.getSv());

        assertEquals(object2.toString(), 1, ((DataObject)object2).getSequence().size());
        assertEquals("object2", ((DataObject)object2).getSequence().getValue(0));

        assertEquals(object3.toString(), 2, ((DataObject)object3).getSequence().size());
        assertEquals("object3", ((DataObject)object3).getSequence().getValue(0));
        assertEquals(object2, ((DataObject)object3).getSequence().getValue(1));

        ((DataObject)object3).detach();

        assertEquals(object1.toString(), 1, ((DataObject)object1).getSequence().size());
        assertEquals("object1", ((DataObject)object1).getSequence().getValue(0));

        assertEquals(object3.toString(), 2, ((DataObject)object3).getSequence().size());
        assertEquals("object3", ((DataObject)object3).getSequence().getValue(0));
        assertEquals(object2, ((DataObject)object3).getSequence().getValue(1));

        values = new ArrayList<SequencedOppositeIntf>();
        values.add(object1);
        values.add(object2);
        object3.setMv(values);

        assertEquals(object3.toString(), 3, ((DataObject)object3).getSequence().size());
        assertEquals("object3", ((DataObject)object3).getSequence().getValue(0));
        assertEquals(object1, ((DataObject)object3).getSequence().getValue(1));
        assertEquals(object2, ((DataObject)object3).getSequence().getValue(2));

        ((DataObject)object2).detach();

        assertEquals(object2.toString(), 1, ((DataObject)object2).getSequence().size());
        assertEquals("object2", ((DataObject)object2).getSequence().getValue(0));

        assertEquals(object3.toString(), 2, ((DataObject)object3).getSequence().size());
        assertEquals("object3", ((DataObject)object3).getSequence().getValue(0));
        assertEquals(object1, ((DataObject)object3).getSequence().getValue(1));

    }

    @Test
    public void testSequencedOppositeAccess2() {
        SequencedOppositeIntf object1 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object2 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object3 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        Property svProperty = ((DataObject)object1).getProperty("sv");
        Property mvProperty = ((DataObject)object1).getProperty("mv");

        assertFalse(svProperty.isContainment());
        assertTrue(mvProperty.isContainment());
        assertEquals(svProperty, mvProperty.getOpposite());
        assertEquals(mvProperty, svProperty.getOpposite());

        object1.setName("object1");
        object2.setName("object2");
        object3.setName("object3");

        object2.setSv(object3);

        assertEquals(object3, object2.getSv());

        assertEquals(object2.toString(), 1, ((DataObject)object2).getSequence().size());
        assertEquals("object2", ((DataObject)object2).getSequence().getValue(0));
    }

    @Test
    public void testSequencedOppositeAccessClear() {
        SequencedOppositeIntf object1 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object2 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        Property svProperty = ((DataObject)object1).getProperty("sv");
        Property mvProperty = ((DataObject)object1).getProperty("mv");

        assertFalse(svProperty.isContainment());
        assertTrue(mvProperty.isContainment());
        assertEquals(svProperty, mvProperty.getOpposite());
        assertEquals(mvProperty, svProperty.getOpposite());

        object1.setName("object1");
        object2.setName("object2");

        List<SequencedOppositeIntf> values = new ArrayList<SequencedOppositeIntf>();
        values.add(object2);
        object1.setMv(values);

        assertEquals(object1, ((DataObject)object2).getContainer());

        values = object1.getMv();
        values.clear();

        assertEquals(null, ((DataObject)object2).getContainer());
    }


    @Test
    public void testContainmentViolation() {
        SequencedOppositeIntf object1 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object2 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object3 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        object1.setName("object1");
        object2.setName("object2");
        object3.setName("object3");

        object1.getMv().add(object2);
        object2.getMv().add(object3);

        try {
            object3.getMv().add(object1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            // expected
        }
        assertEquals(0, object3.getMv().size());
    }

    @Test
    public void testContainmentViolation2() {
        SequencedOppositeIntf object1 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object2 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object3 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object4 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        object1.setName("object1");
        object2.setName("object2");
        object3.setName("object3");

        object1.getMv().add(object2);
        object2.getMv().add(object3);

        List<SequencedOppositeIntf> values = new ArrayList<SequencedOppositeIntf>(2);
        values.add(object4);
        values.add(object1);

        try {
            object3.setMv(values);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            // expected
        }
        assertEquals(0, object3.getMv().size());

    }

    @Test
    public void testContainmentViolationByOpposite() {
        SequencedOppositeIntf object1 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object2 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf object3 = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        object1.setName("object1");
        object2.setName("object2");
        object3.setName("object3");

        object1.getMv().add(object2);
        object2.getMv().add(object3);

        try {
            object1.setSv(object3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            // expected
        }
        assertEquals(0, object3.getMv().size());
    }

    @Test
    public void testBrokenOpposite() throws Exception {
        OppositePropsA oppositeA = (OppositePropsA)_helperContext.getDataFactory().create(OppositePropsA.class);
        OppositePropsB oppositeB = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        assertNotNull(oppositeA);
        assertNotNull(oppositeB);
        GenericDataObject gdo = ((DataObjectDecorator)oppositeA).getInstance();
        assertNotNull(gdo);
        gdo.addToPropertyWithoutCheck(gdo.getInstanceProperty("bs"), oppositeB);
        try {
            ValidationHelper.validateConstraints((DataObject)oppositeA);
            fail("ValidationException expected!");
        } catch (ValidationException ex) {
            assertEquals(
                "DataObject is not contained by the opposite Property",
                ex.getMessage());
        }

        oppositeA = (OppositePropsA)_helperContext.getDataFactory().create(OppositePropsA.class);
        oppositeB = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        assertNotNull(oppositeA);
        assertNotNull(oppositeB);
        gdo = ((DataObjectDecorator)oppositeB).getInstance();
        assertNotNull(gdo);
        gdo.setPropertyWithoutCheck(gdo.getInstanceProperty("a"), oppositeA);
        try {
            ValidationHelper.validateConstraints((DataObject)oppositeB);
            fail("ValidationException expected!");
        } catch (ValidationException ex) {
            assertEquals(
                "DataObject is not contained by the opposite Property",
                ex.getMessage());
        }
    }

    @Test
    public void testOppositeSerialization() throws Exception {
        OppositePropsContainA a =
            (OppositePropsContainA)_helperContext.getDataFactory().create(OppositePropsContainA.class);
        OppositePropsContainB b =
            (OppositePropsContainB)_helperContext.getDataFactory().create(OppositePropsContainB.class);
        assertNotNull(a);
        assertNotNull(b);
        ((DataObjectDecorator)a).getInstance().setReadOnlyMode(false);
        a.getBs().add(b);
        ((DataObjectDecorator)a).getInstance().setReadOnlyMode(true);
        assertSame(a, b.getA());

        Type aType = ((DataObject)a).getType();
        DataObject propObj = _helperContext.getDataFactory().create(PROPERTY.getURI(), PROPERTY.getName());
        propObj.set(PropertyConstants.NAME, "a");
        propObj.set(PropertyConstants.TYPE, aType);
        propObj.set(PropertyConstants.CONTAINMENT, true);
        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(aType.getURI(), propObj);
        String xsd =
            _helperContext.getXSDHelper().generate(
                Collections.singletonList(aType));
        String xml = _helperContext.getXMLHelper().save((DataObject)a, aType.getURI(), prop.getName());
        assertNotNull(xsd);
        assertNotNull(xml);
        System.out.println(xsd);
        System.out.println(xml);

        HelperContext context = SapHelperProvider.getNewContext();
        List<Type> types = context.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(2, types.size());

        XMLDocument doc = context.getXMLHelper().load(xml);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);
        assertEquals("OppositePropsContainA", root.getType().getName());
        Property bsProp = root.getType().getProperty("bs");
        assertNotNull(bsProp);
        Property aProp = bsProp.getOpposite();
        assertNotNull(aProp);
        assertSame(bsProp.getType(), aProp.getContainingType());
        assertEquals("OppositePropsContainB", bsProp.getType().getName());
        assertSame(bsProp, aProp.getOpposite());
    }
}
