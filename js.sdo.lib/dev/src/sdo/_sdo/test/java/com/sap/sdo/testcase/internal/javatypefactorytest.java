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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.Bool;
import com.sap.sdo.api.SchemaInfo;
import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.java.JavaTypeFactory;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class JavaTypeFactoryTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public JavaTypeFactoryTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private JavaTypeFactory _factory;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _factory = new JavaTypeFactory(_helperContext);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _factory = null;
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.JavaTypeFactory#getClassByQName(com.sap.sdo.impl.util.URINamePair, java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testGetClassByQName() {
        try {
            _factory.getClassByQName(new URINamePair("", "TestClass"), null, null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("cannot provide JAVA based type", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.JavaTypeFactory#getQNameFromClass(java.lang.Class)}.
     */
//    @Test
//    public void testGetQNameFromClass() {
//        assertEquals(null, _factory.getQNameFromClass(String.class));
//    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.JavaTypeFactory#createTypeFromClass(com.sap.sdo.impl.util.URINamePair, java.lang.Class)}.
     */
    @Test
    public void testCreateTypeFromClass() {
//        assertEquals(null, _factory.createTypeFromClass(null, String.class));

//        assertEquals(
//            null,
//            _factory.createTypeFromClass(_factory.getQNameFromClass(Corrupted.class), Corrupted.class));

//        try {
//            _factory.createTypeFromClass(_factory.getQNameFromClass(ICorrupted.class), ICorrupted.class);
//            fail("expected IllegalArgumentException");
//        } catch (IllegalArgumentException ex) {
//            assertEquals(
//                "Cannot define dataobject:  com.sap.sdo.testcase.internal.JavaTypeFactoryTest$Corrupted is not an interface",
//                ex.getMessage());
//        }

        try {
            _factory.createTypeFromClass(_factory.getQNameFromClass(IInvalidSchemaLocation.class), IInvalidSchemaLocation.class);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "java.net.URISyntaxException: Malformed escape pair at index 1: $%&",
                ex.getMessage());
        }

        assertNotNull(_factory.createTypeFromClass(_factory.getQNameFromClass(IValidSchemaLocation.class), IValidSchemaLocation.class));

        assertNotNull(_factory.createTypeFromClass(_factory.getQNameFromClass(IEmptySchemaLocation.class), IEmptySchemaLocation.class));
    }

    /**
     * Test method for {@link com.sap.sdo.impl.types.java.JavaTypeFactory#enrichType(java.util.Map)}.
     */
    @Test
    public void testEnrichType() {
        Map<Class,Type> map = new HashMap<Class,Type>();
        map.put(String.class, JavaSimpleType.STRING);
        _factory.enrichType(map);

        assertEquals(1, map.size());
        assertEquals(String.class, map.keySet().iterator().next());
        assertEquals(JavaSimpleType.STRING, map.values().iterator().next());

        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "MyType");
        typeObject.set(TypeType.URI, "");
        typeObject.set(TypeType.getJavaClassProperty(), Object.class.getName());
        _helperContext.getTypeHelper().define(typeObject);

        typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "MyType");
        typeObject.set(TypeType.URI, "");
        typeObject.set(TypeType.getJavaClassProperty(), String.class.getName());

        map.put(String.class, (Type)typeObject);
        _factory.enrichType(map);

        assertEquals(1, map.size());
        assertEquals(String.class, map.keySet().iterator().next());
        assertEquals(typeObject, map.values().iterator().next());

    }

    @SchemaInfo(schemaLocation="$%&")
    interface IInvalidSchemaLocation {
    }

    @SchemaInfo(schemaLocation="schemaLocation")
    interface IValidSchemaLocation extends IBase {
        List getList();
        void setList(List pList);

        @SdoPropertyMetaData(
            nullable=Bool.TRUE,
            xmlInfo = @XmlPropertyMetaData(
                xmlElement=false,
                xsdName="aList"
            )
        )
        List getAttributeList();
        void setAttributeList(List pAttributeList);
    }

    @SchemaInfo(schemaLocation="")
    interface IEmptySchemaLocation {
    }

    interface ICorrupted extends IBase{
        Corrupted getData();
        void setString(Corrupted pData);
    }

    interface IBase {
        String getName();
        void setName(String pName);
    }

    class Corrupted extends Base {
        String _string = null;

        public Corrupted(String pString) {
            setString(pString);
        }

        String getString() {
            return _string;
        }

        private void setString(String pString) {
            _string = pString;
        }
    }

    class Base {
        String _name = null;

        String getName() {
            return _name;
        }

        void setName(String pName) {
            _name = pName;
        }
    }
}
