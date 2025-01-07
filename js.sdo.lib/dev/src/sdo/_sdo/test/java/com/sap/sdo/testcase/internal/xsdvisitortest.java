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

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import test.suite.BasicInternal;

import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OppositePropsA;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleMVIntf;
import com.sap.sdo.testcase.typefac.SimpleTypeLoopIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public class XsdVisitorTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XsdVisitorTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    @Test
    public void testSimpleAttrToXsd() throws Exception {
        logger.info("testSimpleAttrToXsd");
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        assertTrue("type was not provided", t != null);
        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(t));
        HelperContext newContext = SapHelperProvider.getNewContext();
        String xsd = newContext.getXSDHelper().generate(
            newContext.getXSDHelper().define(original));
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }

        HelperContext secondContext = SapHelperProvider.getNewContext();
        List types = secondContext.getXSDHelper().define(xsd);
        assertEquals(xsd, secondContext.getXSDHelper().generate(types));
    }
    @Test
    public void testSimpleMVToXsd() throws Exception {
        logger.info("testSimpleAttrToXsd");
        Type t = _helperContext.getTypeHelper().getType(SimpleMVIntf.class);
        assertTrue("type was not provided", t != null);
        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(t));
        HelperContext newContext = SapHelperProvider.getNewContext();
        String xsd = newContext.getXSDHelper().generate(
            newContext.getXSDHelper().define(original));
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }

        HelperContext secondContext = SapHelperProvider.getNewContext();
        List types = secondContext.getXSDHelper().define(xsd);
        assertEquals(xsd, secondContext.getXSDHelper().generate(types));
    }
    @Test
    public void testSimpleContainingToXsd() throws Exception {
        logger.info("testSimpleAttrToXsd");
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        assertTrue("type was not provided", t != null);
        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(t));
        String xsd = _helperContext.getXSDHelper().generate(
            _helperContext.getXSDHelper().define(original));
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }
        List types = _helperContext.getXSDHelper().define(xsd);
        assertEquals(xsd, _helperContext.getXSDHelper().generate(types));
    }
    @Test
    public void testSimpleLoopToXsd() throws Exception {
        logger.info("testLoopToXsd");
        Type t = _helperContext.getTypeHelper().getType(SimpleTypeLoopIntf.class);
        assertTrue("type was not provided", t != null);
        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(t));
        String xsd = _helperContext.getXSDHelper().generate(
            _helperContext.getXSDHelper().define(original));
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }
        List types = _helperContext.getXSDHelper().define(xsd);
        assertEquals(xsd, _helperContext.getXSDHelper().generate(types));
    }
    @Test
    public void testOppositePropsToXsd() throws Exception {
        logger.info("testOppositePropsToXsd");
        Type t = _helperContext.getTypeHelper().getType(OppositePropsA.class);
        assertTrue("type was not provided", t != null);
        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(t));
        HelperContext helperContext2 = SapHelperProvider.getNewContext();
        final List types2 = helperContext2.getXSDHelper().define(original);
        String xsd = helperContext2.getXSDHelper().generate(types2);
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }
        HelperContext helperContext3 = SapHelperProvider.getNewContext();
        List types = helperContext3.getXSDHelper().define(xsd);
        assertEquals(xsd, helperContext3.getXSDHelper().generate(types));
    }
    @Test
    public void testDataTypesToXsd() throws Exception {
        DataObject dataTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        dataTypeDO.set("open",false);
        dataTypeDO.set("sequenced",false);
        dataTypeDO.set("uri","com.sap.sdo");
        dataTypeDO.set("name","SimpleDataTypeExampleDataType");
        dataTypeDO.set(_helperContext.getTypeHelper().getOpenContentProperty(URINamePair.DATATYPE_JAVA_URI, TypeConstants.JAVA_CLASS),HexaStringSimpleType.class.getName());
        dataTypeDO.set("dataType",true);

        Type dataType = _helperContext.getTypeHelper().define(dataTypeDO);
        assertTrue("returned value is not a type",dataType instanceof Type);


        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",false);
        typeDO.set("sequenced",false);
        typeDO.set("uri","com.sap.sdo");
        typeDO.set("name","SimpleDataTypeExample");
        DataObject prop = typeDO.createDataObject("property");
        prop.set("name", "dataType");
        prop.set("type", dataType);

        Type type = _helperContext.getTypeHelper().define(typeDO);

        DataObject data = _helperContext.getDataFactory().create(type);
        data.setString("dataType", "f0c");
        Object result = data.get("dataType");
        assertTrue(result instanceof HexaStringSimpleType);
        assertEquals("f0c", result.toString());
        assertEquals("f0c", data.getString("dataType"));

        //TODO nice to have
        //assertSame(dataType, _helperContext.getTypeHelper().getType(HexaStringSimpleType.class));

        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(type));
        String xsd = _helperContext.getXSDHelper().generate(
            _helperContext.getXSDHelper().define(original));
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }
        List types = _helperContext.getXSDHelper().define(xsd);
        assertEquals(xsd, _helperContext.getXSDHelper().generate(types));

        HelperContext context = SapHelperProvider.getNewContext();
        List<Type> parsedTypes = context.getXSDHelper().define(xsd);
        assertNotNull(parsedTypes);
        assertEquals(2, parsedTypes.size());

        DataObject data2 = context.getDataFactory().create("com.sap.sdo","SimpleDataTypeExample");
        data2.setString("dataType", "f0c");
        Object result2 = data2.get("dataType");
        assertTrue(result2 instanceof HexaStringSimpleType);
        assertEquals("f0c", result2.toString());
        assertEquals("f0c", data2.getString("dataType"));

        assertEquals(xsd, context.getXSDHelper().generate(parsedTypes));
    }
    @Test
    public void testMetaDataXsd() throws Exception {
        logger.info("testSimpleAttrToXsd");
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(t));
        String xsd = _helperContext.getXSDHelper().generate(
            _helperContext.getXSDHelper().define(original));
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }
        List types = _helperContext.getXSDHelper().define(xsd);
        assertEquals(xsd, _helperContext.getXSDHelper().generate(types));
    }
    @Test
    public void testSimpleTypesXsd() throws Exception {
        logger.info("testSimpleAttrToXsd");
        Type t = _helperContext.getTypeHelper().getType(ISimpleTypeProperty.class);
        String original = _helperContext.getXSDHelper().generate(Collections.singletonList(t));
        String xsd = _helperContext.getXSDHelper().generate(
            _helperContext.getXSDHelper().define(original));
        assertNotNull(xsd);
        assertTrue(xsd.length() > 0);
        if (original.length() != xsd.length()) {
            assertEquals(original, xsd);
        }
        List types = _helperContext.getXSDHelper().define(xsd);
        assertEquals(xsd, _helperContext.getXSDHelper().generate(types));
    }
    private final static Logger logger = BasicInternal.logger;

    public static class HexaStringSimpleType {
        int _value;

        /**
         *
         */
        public HexaStringSimpleType(String hexadecimal) {
            super();
            _value = Integer.parseInt(hexadecimal, 16);
        }

        @Override
        public String toString() {
            return Integer.toHexString(_value);
        }
    }

}
