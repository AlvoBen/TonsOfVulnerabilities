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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.FacetContainer;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class SimpleTypeTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SimpleTypeTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testUnresolvedReference() {
    	try {
    		_helperContext.getTypeHelper().getType(IUnresolvedReference.class);
    	} catch (IllegalArgumentException e) {
    		return;
    	}
    	fail("should have thrown an illegal argument exception");
    }

    @Test
    public void testUserDefinedSimpleType() throws Exception {
    	Type t = _helperContext.getTypeHelper().getType(ISimpleTypeProperty.class);
    	assertNotNull(t);
    	Property p = t.getProperty("x");
    	assertNotNull(p);
    	Type simpleType = p.getType();
    	assertNotNull(simpleType);
    	assertTrue(simpleType.isDataType());
    	assertNotNull(simpleType.getBaseTypes());
    	assertEquals(simpleType.getBaseTypes().size(),1);
    	final Type baseType = (Type)simpleType.getBaseTypes().get(0);
        assertEquals(_helperContext.getTypeHelper().getType(String.class), baseType);
    	assertEquals(baseType.getInstanceClass(), simpleType.getInstanceClass());
    	DataObject facet = (DataObject)((DataObject)simpleType).get(TypeType.FACETS);
    	assertNotNull(facet);
    	assertEquals(facet.getInt(TypeType.FACET_MAXLENGTH),40);

    	DataObject d = _helperContext.getDataFactory().create(t);
    	d.set("x","hdhdhdhhd");
    	assertEquals("hdhdhdhhd", d.get("x"));
    }
    @Test
    public void testFacets() throws Exception {
    	Type t = _helperContext.getTypeHelper().getType(FacetContainer.class);
    	FacetContainer c = (FacetContainer)_helperContext.getDataFactory().create(t);
    	c.setMinMaxLength("abc");
    	c.setMinMaxLength("abcde");
    	try {
    		c.setMinMaxLength("a");
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) { //$JL-EXC$

    	}
    	try {
    		c.setMinMaxLength("abcdef");
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) { //$JL-EXC$

    	}
    	c.setMinMaxLength(null);
    	c.setExactLength("abcd");
    	try {
    		c.setExactLength("abc");
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) { //$JL-EXC$

    	}
    	c.setEnum("a");
    	try {
    		c.setEnum("x");
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) {	 //$JL-EXC$
    	}
    	c.setMinMaxEx(4);
    	try {
    		c.setMinMaxEx(3);
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) {	 //$JL-EXC$
    	}
    	try {
    		c.setMinMaxEx(5);
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) {	 //$JL-EXC$
    	}

    }

    @Test
    public void testCopy() {
        ISimpleTypeProperty simple =
            (ISimpleTypeProperty)_helperContext.getDataFactory().create(ISimpleTypeProperty.class);

        simple.setX("simple");

        DataObject copy = _helperContext.getCopyHelper().copy((DataObject)simple);

        assertNotNull(copy);
        assertTrue(_helperContext.getEqualityHelper().equal((DataObject)simple, copy));
    }

    @Test
    public void testConvertUnknownData() throws DatatypeConfigurationException {
        SdoType<Object> objectType = (SdoType<Object>)_helperContext.getTypeHelper().getType(Object.class);
        DatatypeFactory factory = DatatypeFactory.newInstance();

        String time = "2008-07-21T12:51:56.000+02:00";
        XMLGregorianCalendar calendar = factory.newXMLGregorianCalendar(time);
        String convertedTime = objectType.convertToJavaClass(calendar, String.class);
        assertEquals(time, convertedTime);

        String dur = "P4Y3M";
        Duration duration = factory.newDurationYearMonth(dur);
        String convertedDur = objectType.convertToJavaClass(duration, String.class);
        assertEquals(dur, convertedDur);

        Date date = calendar.toGregorianCalendar().getTime();
        String convertedDate = objectType.convertToJavaClass(date, String.class);

        XMLGregorianCalendar convertedCalendar = factory.newXMLGregorianCalendar(convertedDate);
        assertEquals(date, convertedCalendar.toGregorianCalendar().getTime());
    }
}
