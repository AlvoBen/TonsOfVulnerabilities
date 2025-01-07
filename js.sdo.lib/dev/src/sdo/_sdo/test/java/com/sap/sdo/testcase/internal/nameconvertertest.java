/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.internal;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.impl.types.java.NameConverter;

/**
 * @author D042774
 *
 */
public class NameConverterTest extends TestCase {
    NameConverter _converter = null;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        _converter = NameConverter.CONVERTER;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        _converter = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.NameConverter#normalizeClassname(java.lang.String)}.
     */
    @Test
    public void testNormalizeClassname() {
        assertEquals("ClassName", _converter.normalizeClassname("ClassName"));
        assertEquals("ClassName[]", _converter.normalizeClassname("ClassName[]"));
        assertEquals("ClassName", _converter.normalizeClassname("java.lang.ClassName"));
        assertEquals("ClassName[]", _converter.normalizeClassname("[LClassName;"));
        assertEquals("byte[]", _converter.normalizeClassname("[BClassName;"));
        assertEquals("[XClassName;", _converter.normalizeClassname("[XClassName;"));
        assertEquals("Class_Name", _converter.normalizeClassname("Class_Name"));
        assertEquals("Class.Name", _converter.normalizeClassname("Class.Name"));
        assertEquals("Class-Name", _converter.normalizeClassname("Class-Name"));
        assertEquals("Class Name", _converter.normalizeClassname("Class Name"));
        assertEquals("Class%Name", _converter.normalizeClassname("Class%Name"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.NameConverter#toClassName(java.lang.String)}.
     */
    @Test
    public void testToClassName() {
        assertEquals("MyClass", _converter.toClassName("MY_CLASS"));
        assertEquals("MyClass", _converter.toClassName("my_class"));
        assertEquals("MyClass", _converter.toClassName("MyClass"));
        assertEquals("I1StClass", _converter.toClassName("1st_CLASS"));
        assertEquals("MyClass", _converter.toClassName("MY.CLASS"));
        assertEquals("MyClass", _converter.toClassName("MY-CLASS"));
        assertEquals("MyClass", _converter.toClassName("MY CLASS"));
        assertEquals("My_Class", _converter.toClassName("My%Class"));
        assertEquals("My\u02bcClass", _converter.toClassName("My\u02bcClass"));
        assertEquals("My\u01f1Class", _converter.toClassName("My\u01f2Class"));
        assertEquals("My_EClass", _converter.toClassName("My&$Class"));
        assertEquals("My_Class", _converter.toClassName("My&.Class"));
        assertEquals("MySimpleClass", _converter.toClassName("My_simpleClass"));
        assertEquals("MYc", _converter.toClassName("MYc"));
        assertEquals("I49115623E9F69Cf2Ab1111Dda32B003005F636A2", _converter.toClassName("49115623E9F69CF2AB1111DDA32B003005F636A2"));
        assertEquals("I_Response", _converter.toClassName("#response"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.NameConverter#toPropertyName(java.lang.String)}.
     */
    @Test
    public void testToPropertyName() {
        assertEquals("myProp", _converter.toPropertyName("MY_PROP"));
        assertEquals("myProp", _converter.toPropertyName("my_prop"));
        assertEquals("myProp", _converter.toPropertyName("MyProp"));
        assertEquals("1StProp", _converter.toPropertyName("1st_PROP"));
        assertEquals("myProp", _converter.toPropertyName("MY.PROP"));
        assertEquals("myProp", _converter.toPropertyName("MY-PROP"));
        assertEquals("myProp", _converter.toPropertyName("MY PROP"));
        assertEquals("my_Prop", _converter.toPropertyName("MY%PROP"));
        assertEquals("clazz", _converter.toPropertyName("Class"));
        assertEquals("", _converter.toPropertyName(""));
        assertEquals("", _converter.toPropertyName("."));
        assertEquals("eSample_SapComId",  _converter.toPropertyName("$sample/sap.com:Id"));
        assertEquals("eDemoSapCom_Test12_491158754Bc80170Ab1311Ddb9Be003005F636A2Response",  _converter.toPropertyName("$demo.sap.com/test12/491158754BC80170AB1311DDB9BE003005F636A2:response"));
        assertEquals("_SchemeAgencyId", _converter.toPropertyName("%schemeAgencyID"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.NameConverter#toVariableName(java.lang.String)}.
     */
    @Test
    public void testToVariableName() {
        assertEquals("myVar", _converter.toVariableName("MY_VAR"));
        assertEquals("myVar", _converter.toVariableName("my_var"));
        assertEquals("myVar", _converter.toVariableName("myVar"));
        assertEquals("_1StVar", _converter.toVariableName("1st_Var"));
        assertEquals("myVar", _converter.toVariableName("MY.VAR"));
        assertEquals("myVar", _converter.toVariableName("MY-VAR"));
        assertEquals("myVar", _converter.toVariableName("MY VAR"));
        assertEquals("my_Var", _converter.toVariableName("MY%VAR"));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.NameConverter#toPackageName(java.lang.String)}.
     */
    @Test
    public void testToPackageName() {
        assertEquals("myPACKAGE", _converter.toPackageName("MY_PACKAGE"));
        assertEquals("myPACKAGE", _converter.toPackageName("my_PACKAGE"));
        assertEquals("myPackage", _converter.toPackageName("MyPackage"));
        assertEquals("p1stPACKAGE", _converter.toPackageName("1st_PACKAGE"));
        assertEquals("myPACKAGE", _converter.toPackageName("MY.PACKAGE"));
        assertEquals("myPACKAGE", _converter.toPackageName("MY-PACKAGE"));
        assertEquals("myPACKAGE", _converter.toPackageName("MY PACKAGE"));
        assertEquals("my_PACKAGE", _converter.toPackageName("MY%PACKAGE"));
    }

}
