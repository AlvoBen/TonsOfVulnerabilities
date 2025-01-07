package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sap.demo.test12.p491158754BC80170AB1311DDB9BE003005F636A2.I_Request;
import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.builtin.DataObjectType;
import com.sap.sdo.impl.types.java.InterfaceGeneratorImpl;
import com.sap.sdo.testcase.NullSchemaResolver;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.internal.XsdVisitorTest.HexaStringSimpleType;
import com.sap.sdo.testcase.typefac.SimpleDataTypeExample;
import com.sap.xi.appl.se.global.I_BasicBusinessDocumentMessageHeader;
import com.sap.xi.appl.se.global.I_BusinessDocumentMessageId;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class InterfaceGeneratorTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public InterfaceGeneratorTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testPackageInXsd() throws IOException {

        final String schemaFileName = PACKAGE + "sdoAnnotationsExample.xsd";

        File dir = new File("C:/test/com/example/myPackage");
        if (dir.exists()) {
            for (File file: dir.listFiles()) {
                if (file.getName().endsWith(".java")) {
                    file.delete();
                }
            }
        }

        URL xsdUrl = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(xsdUrl.openStream(),xsdUrl.toString());

        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        InterfaceGenerator interfaceGenerator = typeHelper.createInterfaceGenerator("c:\\test");
        List<String> classNames = interfaceGenerator.generate(types);
        for (String className: classNames) {
            assertTrue("expected package : " + "com.example.myPackage" + " but was " + className,
                className.startsWith("com.example.myPackage"));
        }

    }

    @Test
    public void testDataObjectType() throws Exception {
        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        assertNotNull(typeHelper);
        InterfaceGenerator generator = typeHelper.createInterfaceGenerator("C:/test");
        assertNotNull(generator);

        List<String> classNames = generator.generate(DataObjectType.getInstance());
        assertNotNull(classNames);
        assertEquals(0, classNames.size());
    }

    @Test
    public void testGenerateAnnotationsFlag() throws Exception {
        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        assertNotNull(typeHelper);
        InterfaceGenerator generator = typeHelper.createInterfaceGenerator("C:/test");
        assertNotNull(generator);

        generator.setGenerateAnnotations(true);
        assertEquals(true, generator.getGenerateAnnotations());
        generator.setGenerateAnnotations(false);
        assertEquals(false, generator.getGenerateAnnotations());
    }

    @Test
    public void testGetPackage() throws Exception {
        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        assertNotNull(typeHelper);
        InterfaceGeneratorImpl generator =
            (InterfaceGeneratorImpl)typeHelper.createInterfaceGenerator("C:/test");
        assertNotNull(generator);

        assertEquals("com.sap.test", generator.getPackage("com.sap.test"));
        assertEquals("com.sap.test", generator.getPackage("http://test.sap.com"));
        assertEquals("com.sap.test", generator.getPackage("urn:com:sap:test"));
    }

    @Test
    public void testManyValuedSimpleType() throws IOException {
        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();

        DataObject typeObject = _helperContext.getDataFactory().create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        typeObject.setString(TypeConstants.NAME, "ManyValuedSimpleTypePropTest");
        typeObject.setString(TypeConstants.URI, "com.sap.sdo.testcase.internal");

        DataObject propObject = typeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.setString(PropertyConstants.NAME, "manyValuedSimple");
        propObject.setBoolean(PropertyConstants.MANY, true);
        propObject.set(PropertyConstants.TYPE, typeHelper.getType(int.class));

        Type type = typeHelper.define(typeObject);

        InterfaceGeneratorImpl generator =
            (InterfaceGeneratorImpl)typeHelper.createInterfaceGenerator("C:/test");
        generator.generate(type);

        String generatedClass = readFile(new File("C:/test/com/sap/sdo/testcase/internal/ManyValuedSimpleTypePropTest.java"));
        assertFalse(generatedClass.contains("java.util.List<int>"));
        assertTrue(generatedClass.contains("sdoType = \"commonj.sdo#Int\""));
        assertTrue(generatedClass.contains("java.util.List<Integer>"));

        Property property = type.getProperty("manyValuedSimple");
        assertEquals(true, property.isMany());
        assertEquals(int.class, property.getType().getInstanceClass());
        assertEquals(Integer.class, propObject.get(PropertyConstants.JAVA_CLASS));

        assertEquals(null, property.getDefault());
    }

    @Test
    public void testListSimpleTypeInt() throws IOException {

        final String schemaFileName = PACKAGE + "ListSimpleTypeInt.xsd";

        URL xsdUrl = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(xsdUrl.openStream(),xsdUrl.toString());

        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        InterfaceGenerator interfaceGenerator = typeHelper.createInterfaceGenerator("c:\\test");
        interfaceGenerator.generate(types);

        String generatedClass = readFile(new File("C:/test/com/sap/sdo/testcase/internal/Container.java"));
        assertFalse(generatedClass.contains("java.util.List<int>"));
        assertTrue(generatedClass.contains("java.util.List<Integer>"));

    }

    @Test
    public void testSimpleType() throws Exception {
        DataObject dataTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        dataTypeDO.set("open",false);
        dataTypeDO.set("sequenced",false);
        dataTypeDO.set("uri","com.sap.sdo.testcase.typefac");
        dataTypeDO.set("name","SimpleDataTypeExampleDataType");
        dataTypeDO.set(_helperContext.getTypeHelper().getOpenContentProperty(URINamePair.DATATYPE_JAVA_URI, TypeConstants.JAVA_CLASS),HexaStringSimpleType.class.getName());
        dataTypeDO.set("dataType",true);

        Type dataType = _helperContext.getTypeHelper().define(dataTypeDO);
        assertTrue("returned value is not a type",dataType instanceof Type);


        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",false);
        typeDO.set("sequenced",false);
        typeDO.set("uri","com.sap.sdo.testcase.typefac");
        typeDO.set("name","SimpleDataTypeExample");
        DataObject prop = typeDO.createDataObject("property");
        prop.set("name", "dataType");
        prop.set("type", dataType);

        Type type = _helperContext.getTypeHelper().define(typeDO);

        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        InterfaceGenerator interfaceGenerator = typeHelper.createInterfaceGenerator("c:\\test");
        List<String> classNames = interfaceGenerator.generate(Arrays.asList(type));
    }

    @Test
    public void testParseOfSimpleType() throws Exception {
        Type type = _helperContext.getTypeHelper().getType(SimpleDataTypeExample.class);

        //TODO needs to be extended
        //System.out.println(_helperContext.getXSDHelper().generate(Arrays.asList(type)));
    }

    @Test
    public void testExtendedSimpleContentGeneration() throws Exception {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "ComplexSimplePolymorphismAnnotated.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        InterfaceGenerator interfaceGenerator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        interfaceGenerator.addPackage("", "com.sap.sdo.testcase.typefac");
        interfaceGenerator.addSchemaLocation("","/com/sap/sdo/testcase/schemas/ComplexSimplePolymorphismAnnotated.xsd");
        List<String> classNames = interfaceGenerator.generate(types);
    }

    @Test
    public void testAnonymousTypesGeneration() throws Exception {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "Anonymous.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        InterfaceGenerator interfaceGenerator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        interfaceGenerator.addSchemaLocation("com.sap.sdo.testcase.anonymous","/" + PACKAGE + "Anonymous.xsd");
        List<String> classNames = interfaceGenerator.generate(types);
        assertTrue(classNames.contains("com.sap.sdo.testcase.anonymous.EGlobal"));
        assertTrue(classNames.contains("com.sap.sdo.testcase.anonymous.GlobalType"));
    }

    @Test
    public void testAnonymousTypesGenerationClasses() throws Exception {
        Type eGlobal = _helperContext.getTypeHelper().getType(com.sap.sdo.testcase.anonymous.EGlobal.class);
        Type globalType = (Type)eGlobal.getBaseTypes().get(0);
        assertEquals(eGlobal.getInstanceProperties(), globalType.getInstanceProperties());
        assertSame(com.sap.sdo.testcase.anonymous.GlobalType.class, globalType.getInstanceClass());
        Type globalList = globalType.getProperty("value").getType();
        assertSame(List.class, globalList.getInstanceClass());
        Type globalAttribute = globalType.getProperty("globalAttribute").getType();
        assertSame(List.class, globalAttribute.getInstanceClass());
    }

    @Test
    public void testPropsWithSameNames() throws Exception {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        InterfaceGenerator interfaceGenerator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        interfaceGenerator.addSchemaLocation("com.sap.sdo.testcase.anonymous","/" + PACKAGE + "SameNames.xsd");
        try {
            List<String> classNames = interfaceGenerator.generate(types);
            fail("Exception expected");
        } catch (Exception e) {

        }
    }

    @Test
    public void testGalaxyRenaming() throws Exception {
        List<Type> types = new ArrayList<Type>();
        URL url = getClass().getClassLoader().getResource(PACKAGE + "rename/schema1.sdoxsd");
        SapXsdHelper xsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        types.addAll(xsdHelper.define(url.openStream(), url.toString()));
        url = getClass().getClassLoader().getResource(PACKAGE + "rename/schema5.sdoxsd");
        types.addAll(xsdHelper.define(url.openStream(), url.toString()));
        url = getClass().getClassLoader().getResource(PACKAGE + "rename/schema4.sdoxsd");
        Map options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_SCHEMA_RESOLVER, new NullSchemaResolver());
        types.addAll(xsdHelper.define(url.openStream(), url.toString(), options));

        InterfaceGenerator interfaceGenerator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        List<String> classNames = interfaceGenerator.generate(types);
//        assertTrue(classNames.contains("com.sap.sdo.testcase.anonymous.EGlobal"));
//        assertTrue(classNames.contains("com.sap.sdo.testcase.anonymous.GlobalType"));
    }

    @Test
    public void testGalaxyRenamedInterfaces() {
        I_BasicBusinessDocumentMessageHeader header = (I_BasicBusinessDocumentMessageHeader)
            _helperContext.getDataFactory().create(I_BasicBusinessDocumentMessageHeader.class);
        header.setEUuid(         "myUUID123456789012345678901234567890");
        header.setEReferenceUuid("myReferenceUuid012345678901234567890");

        I_BusinessDocumentMessageId id = (I_BusinessDocumentMessageId)
            _helperContext.getDataFactory().create(I_BusinessDocumentMessageId.class);
        id.set_SchemeId("mySchemeId");
        id.set_SchemeAgencyId("mySchemeAgencyId");
        id.set_SchemeAgencySchemeAgencyId("SAI");
        header.setEId(id);

        I_BusinessDocumentMessageId referenceId = (I_BusinessDocumentMessageId)
            _helperContext.getDataFactory().create(I_BusinessDocumentMessageId.class);
        id.set_SchemeId("mySchemeId2");
        id.set_SchemeAgencyId("mySchemeAgencyId2");
        id.set_SchemeAgencySchemeAgencyId("RID");
        header.setEReferenceId(referenceId);

        I_Request request = (I_Request)_helperContext.getDataFactory().create(I_Request.class);
        request.setEHeader(header);
    }

    @Test
    public void testExtendedSimpleTypeGeneration() throws Exception {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/typefac/cc/extendedSimpleType.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        InterfaceGenerator interfaceGenerator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        interfaceGenerator.addSchemaLocation("ext.xsd", "extendedSimpleType.xsd");
        interfaceGenerator.addPackage("ext.xsd", "com.sap.sdo.testcase.typefac.cc");
        List<String> classNames = interfaceGenerator.generate(types);
        assertTrue(classNames.contains("com.sap.sdo.testcase.typefac.cc.EContainer"));
    }



}
