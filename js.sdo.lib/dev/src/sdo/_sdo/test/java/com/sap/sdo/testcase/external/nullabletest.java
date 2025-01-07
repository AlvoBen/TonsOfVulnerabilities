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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.external.simpletypes.IBooleanTest;
import com.sap.sdo.testcase.typefac.InheritenceA;
import com.sap.sdo.testcase.typefac.InheritenceB;
import com.sap.sdo.testcase.typefac.InheritenceC;
import com.sap.sdo.testcase.typefac.InheritenceD;
import com.sap.sdo.testcase.typefac.MultipleInheritence;
import com.sap.sdo.testcase.typefac.SimpleChild;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleIntf1;
import com.sap.sdo.testcase.typefac.SimpleParent;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 *
 */
public class NullableTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public NullableTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    @Test
    public void testComplexTypesAreNullableByDefault() {
        DataObject d = _helperContext.getDataFactory().create(SimpleContainingIntf.class);
        assertTrue(d.getProperty("inner").isNullable());
    }
    @Test
    public void testPrimativesAreNotNullable() {
        DataObject d = _helperContext.getDataFactory().create(IBooleanTest.class);
        assertFalse(d.getProperty("aboolean").isNullable());
    }
    @Test
    public void testStringsAreNullable() {
        DataObject d = _helperContext.getDataFactory().create(SimpleContainingIntf.class);
        assertTrue(d.getProperty("x").isNullable());
    }
    @Test
    public void testNillablesAreNullable() {
        DataObject d = _helperContext.getDataFactory().create(SimpleContainingIntf.class);
        assertTrue(d.getProperty("x").isNullable());
    }
	@Test
    public void testNils() throws Exception {
        final String schemaFileName = PACKAGE + "NillableType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"Nillables.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(uri.openStream(),null,null);
        assertEquals(doc.getEncoding(),"UTF-8");
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject container = doc.getRootObject();
        assertEquals(0,container.getInt("int"));
        assertNull(container.get("intObj"));
        assertTrue(container.isSet("intObj"));
        assertNull(container.get("longObj"));
        assertTrue(container.isSet("longObj"));
        assertNull(container.get("floatObj"));
        assertTrue(container.isSet("floatObj"));
        assertNull(container.get("stringObj"));
        assertTrue(container.isSet("stringObj"));
    }
	@Test
    public void testXsdExample() {
        final String schemaFileName = PACKAGE + "PurchaseOrderWithNillables.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
    	_helperContext.getXSDHelper().define(is,null);
    	Type t = _helperContext.getTypeHelper().getType("IpoWithNills.xsd","ActionType");
    	assertNotNull("could not find type",t);
    	assertTrue(t.getProperty("User").isNullable());
    	assertFalse(t.getProperty("Date").isNullable());
    	DataObject o = _helperContext.getDataFactory().create(t);
    	String s = _helperContext.getXMLHelper().save(o, "IpoWithNills.xsd", "action");
    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:action xmlns:ns1=\"IpoWithNills.xsd\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"></ns1:action>\n";
    	assertLineEquality(xml, s);
    	o.set("User", null);
        try {
            o.set("Date", null);
            assertEquals(false, o.isSet("Date"));
        } catch (Exception e) { //$JL-EXC$
            // expected
        }
    	s = _helperContext.getXMLHelper().save(o, "IpoWithNills.xsd", "action");
    	String exp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    	"<ns1:action xmlns:ns1=\"IpoWithNills.xsd\""+
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
        " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"+
    	"  <ns1:User xsi:nil=\"true\"></ns1:User>\n"+
    	"</ns1:action>\n";
    	assertLineEquality(exp, s);
	}
	@Test
    public void testMultipleInheritance() {
		Type t = _helperContext.getTypeHelper().getType(MultipleInheritence.class);
		assertEquals(2,t.getBaseTypes().size());
		Type bt1 = (Type)t.getBaseTypes().get(0);
		Type bt2 = (Type)t.getBaseTypes().get(1);
		assertEquals(bt1.getProperties().size()+bt2.getProperties().size(),t.getProperties().size()-t.getDeclaredProperties().size());

		MultipleInheritence mi = (MultipleInheritence)_helperContext.getDataFactory().create(t);
		DataObject child = ((DataObject)mi).createDataObject("child");
		assertEquals(child, mi.getChild());
		mi.setData("hello");
		assertEquals("hello",mi.getData());
		Property dataProp = ((DataObject)mi).getInstanceProperty("data");
		assertEquals(mi.getData(), ((DataObject)mi).get(dataProp));
		String s = _helperContext.getXMLHelper().save((DataObject)mi, "multiTest.xsd", "mi");
		DataObject obj = _helperContext.getXMLHelper().load(s).getRootObject();
		assertTrue(_helperContext.getEqualityHelper().equal((DataObject)mi, obj));
	}

    @Test
    public void testMultipleInheritance2() {
        Type t = _helperContext.getTypeHelper().getType(MultipleInheritence.class);
        Type bt1 = _helperContext.getTypeHelper().getType(SimpleIntf1.class);
        Type bt2 = _helperContext.getTypeHelper().getType(SimpleParent.class);

        Property propChildT = t.getProperty("child");
        Property propChildBt2 = bt2.getProperty("child");

        assertEquals(propChildT, propChildBt2);

        DataObject parent = _helperContext.getDataFactory().create(t);
        DataObject child = _helperContext.getDataFactory().create(propChildBt2.getType());

        parent.set(propChildBt2, child);

        assertSame(child, parent.get(propChildBt2));
        assertSame(child, parent.get(propChildBt2));

        ((MultipleInheritence)parent).setChild(null);

        assertSame(null, parent.get(propChildBt2));
        assertSame(null, parent.get(propChildBt2));

        ((SimpleParent)parent).setChild((SimpleChild)child);

        assertSame(child, parent.get(propChildBt2));
        assertSame(child, parent.get(propChildBt2));

    }

    @Test
    public void testMultipleInheritanceDiamond() {
        Type tA = _helperContext.getTypeHelper().getType(InheritenceA.class);
        Type tB = _helperContext.getTypeHelper().getType(InheritenceB.class);
        Type tC = _helperContext.getTypeHelper().getType(InheritenceC.class);
        Type tD = _helperContext.getTypeHelper().getType(InheritenceD.class);

        Property propAtA = tA.getProperty("a");
        Property propAtB = tB.getProperty("a");
        Property propAtC = tC.getProperty("a");
        Property propAtD = tD.getProperty("a");

        assertEquals(propAtA, propAtB);
        assertEquals(propAtB, propAtC);
        assertEquals(propAtC, propAtD);

        assertEquals(4, tD.getProperties().size());

        DataObject objectD = _helperContext.getDataFactory().create(InheritenceD.class);
        ((InheritenceD)objectD).setA("A");

        assertEquals("A", objectD.get(propAtD));
        assertEquals("A", objectD.get(propAtC));
        assertEquals("A", objectD.get(propAtB));
        assertEquals("A", objectD.get(propAtA));

        Property propBtB = tB.getProperty("b");
        Property propBtD = tD.getProperty("b");

        ((InheritenceD)objectD).setB("B");

        assertEquals("B", objectD.get(propBtD));
        assertEquals("B", objectD.get(propBtB));

        Property propCtC = tC.getProperty("c");
        Property propCtD = tD.getProperty("c");

        ((InheritenceD)objectD).setC("C");

        assertEquals("C", objectD.get(propCtD));
        assertEquals("C", objectD.get(propCtC));

        Property propDtD = tD.getProperty("d");

        ((InheritenceD)objectD).setD("D");

        assertEquals("D", objectD.get(propDtD));

        DataObject objectC = _helperContext.getDataFactory().create(InheritenceC.class);
        try {
            objectC.set(propBtB, "C");
            fail("Property b is not define on type InheritenceC");
        } catch (RuntimeException e) { //$JL-EXC$
            // expected
        }
    }
}
