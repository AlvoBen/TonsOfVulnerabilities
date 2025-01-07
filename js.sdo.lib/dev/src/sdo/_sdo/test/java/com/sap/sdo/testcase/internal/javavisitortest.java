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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.java.InterfaceGeneratorImpl;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.FacetTestType;
import com.sap.sdo.testcase.typefac.OpenContent;
import com.sap.sdo.testcase.typefac.OppositePropsA;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleMVIntf;
import com.sap.sdo.testcase.typefac.SimpleSdoNameIntf;
import com.sap.sdo.testcase.typefac.SimpleTypeLoopIntf;
import com.sap.sdo.testcase.typefac.SimpleTypeWithSchema;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class JavaVisitorTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public JavaVisitorTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    public Property _javaClassProp;

    @Before
    public void setUp() throws Exception {
        _javaClassProp = _helperContext.getTypeHelper().getOpenContentProperty(URINamePair.DATATYPE_JAVA_URI, TypeConstants.JAVA_CLASS);
    }
    @Test
    public void testSimpleAttrToJava() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);
    }
    @Test
    public void testSimpleMVToJava() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleMVIntf.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);
    }
    @Test
    public void testSimpleContainingToJava() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);
    }
    @Test
    public void testSimpleLoopToXsd() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleTypeLoopIntf.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);
    }
    @Test
    public void testOppositePropsToXsd() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(OppositePropsA.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);
    }
    @Test
    public void testSdoName() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleSdoNameIntf.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);
    }
    @Test
    public void testFacets() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(ISimpleTypeProperty.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);

        t = _helperContext.getTypeHelper().getType(FacetTestType.class);
        assertNotNull("type was not provided", t);
        visitor.generate(t);
    }
    @Test
    public void testSchemaInfo() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleTypeWithSchema.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addSchemaLocation("com.sap.sdo.tests.java", "../schemas/empty.xsd");
        generator.generate(t);
    }
    @Test
    public void testOpenContentAnnotation() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(OpenContent.class);
        assertNotNull("type was not provided", t);
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(t);
    }
    @Test
    public void testDataTypesToJava() throws Exception {
        DataObject baseTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        baseTypeDO.set("open",false);
        baseTypeDO.set("sequenced",false);
        baseTypeDO.set("uri","com.sap.sdo");
        baseTypeDO.set("name","BaseDataTypeExampleDataType");
        baseTypeDO.set(_javaClassProp,String.class.getName());
        baseTypeDO.set("dataType",true);

        DataObject dataTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        dataTypeDO.set("open",false);
        dataTypeDO.set("sequenced",false);
        dataTypeDO.set("uri","com.sap.sdo");
        dataTypeDO.set("name","SimpleDataTypeExampleDataType");
        dataTypeDO.set(_javaClassProp,String.class.getName());
        dataTypeDO.set("dataType",true);
        dataTypeDO.set("baseType", Arrays.asList(new DataObject[]{baseTypeDO, baseTypeDO}));

        try {
            _helperContext.getTypeHelper().define(dataTypeDO);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "DataType cannot have multiple inheritence: com.sap.sdo:SimpleDataTypeExampleDataType",
                ex.getMessage());
        }

        dataTypeDO.set("baseType", Collections.emptyList());

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
        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate(type);
    }

    @Test
    public void testInstanceClass() throws Exception {
        DataObject dataTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        dataTypeDO.set("open",false);
        dataTypeDO.set("sequenced",false);
        dataTypeDO.set("uri","com.sap.sdo");
        dataTypeDO.set("name","InvalidDataType");
        dataTypeDO.set("dataType",true);

        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",false);
        typeDO.set("sequenced",false);
        typeDO.set("uri","com.sap.sdo");
        typeDO.set("name","InvalidDataTypeExample");
        DataObject prop = typeDO.createDataObject("property");
        prop.set("name", "dataType");
        prop.set("type", dataTypeDO);

        InterfaceGenerator visitor = getInterfaceGenerator();
        try {
            visitor.generate((SdoType)typeDO);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("No instanceClass specified for InvalidDataType", ex.getMessage());
        }
    }

    @Test
    public void testMultipleInheritence() throws Exception {
        DataObject base1TypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        base1TypeDO.set("open",false);
        base1TypeDO.set("sequenced",false);
        base1TypeDO.set("uri","com.sap.sdo");
        base1TypeDO.set("name","BaseOneType");
        base1TypeDO.set(_javaClassProp,String.class.getName());
        base1TypeDO.set("dataType",true);

        DataObject base2TypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        base2TypeDO.set("open",false);
        base2TypeDO.set("sequenced",false);
        base2TypeDO.set("uri","com.sap.sdo");
        base2TypeDO.set("name","BaseTwoType");
        base2TypeDO.set(_javaClassProp,String.class.getName());
        base2TypeDO.set("dataType",true);

        DataObject dataTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        dataTypeDO.set("open",false);
        dataTypeDO.set("sequenced",false);
        dataTypeDO.set("uri","com.sap.sdo");
        dataTypeDO.set("name","MultipleInheritenceType");
        dataTypeDO.set("dataType",true);
        dataTypeDO.set(_javaClassProp,String.class.getName());
        dataTypeDO.set("baseType", Arrays.asList(new DataObject[]{base1TypeDO, base2TypeDO}));

        InterfaceGenerator visitor = getInterfaceGenerator();
        visitor.generate((SdoType)dataTypeDO);

        try {
            _helperContext.getTypeHelper().define(dataTypeDO);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "DataType cannot have multiple inheritence: com.sap.sdo:MultipleInheritenceType",
                ex.getMessage());
        }

        dataTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        dataTypeDO.set("open",false);
        dataTypeDO.set("sequenced",false);
        dataTypeDO.set("uri","com.sap.sdo");
        dataTypeDO.set("name","MultipleInheritenceComplexType");
        dataTypeDO.set("dataType",false);
        dataTypeDO.set("baseType", Arrays.asList(new DataObject[]{base1TypeDO, base2TypeDO}));

        _helperContext.getTypeHelper().define(dataTypeDO);
        visitor.generate((SdoType)dataTypeDO);
    }

    @Test
    public void testGetDirectory() throws Exception {
        String root = "c:\\test";
        String path1 = "com\\sap\\sdo\\tests";
        String path = path1 + "\\test";
        File dir = new File(root+"\\"+path1);
        dir.mkdir();
        File file = new File(root+"\\"+path);
        file.createNewFile();
        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator(root);
        try {
            ((InterfaceGeneratorImpl)generator).getDirectory(path);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Directory "+root+"\\"+path+" could not be created.",
                ex.getMessage());
        }
    }

    @Test
    public void testGetPackage() {
        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        assertEquals("", ((InterfaceGeneratorImpl)generator).getPackage(""));
        try {
            ((InterfaceGeneratorImpl)generator).getPackage(null);
            fail("expected NullPointerException");
        } catch (NullPointerException ex) {
            if (ex.getMessage() != null) {
                assertEquals(
                    "while trying to invoke the method" +
                    " java.lang.String.matches(java.lang.String)" +
                    " of an object loaded from local variable 'namespace'",
                    ex.getMessage());
            }
        }
        assertEquals("com.sap.sdo", ((InterfaceGeneratorImpl)generator).getPackage("com.sap.sdo"));
        assertEquals("com.sap.sdo", ((InterfaceGeneratorImpl)generator).getPackage("urn://sdo.sap.com"));
        assertEquals("com.sap.sdo", ((InterfaceGeneratorImpl)generator).getPackage("com/sap/sdo"));
        assertEquals("com_sap_sdo", ((InterfaceGeneratorImpl)generator).getPackage("com\\sap\\sdo"));
    }

    private InterfaceGenerator getInterfaceGenerator() {
        InterfaceGenerator interfaceGenerator = ((SapTypeHelper)_helperContext.getTypeHelper())
            .createInterfaceGenerator("c:\\test");
        interfaceGenerator.addPackage("com.sap.sdo.testcase.typefac", "com.sap.sdo.tests.java");
        return interfaceGenerator;
    }

}
