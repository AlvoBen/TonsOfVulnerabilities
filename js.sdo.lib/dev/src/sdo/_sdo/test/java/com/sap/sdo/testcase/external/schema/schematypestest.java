package com.sap.sdo.testcase.external.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.types.schema.Annotation;
import com.sap.sdo.api.types.schema.Appinfo;
import com.sap.sdo.api.types.schema.ComplexType;
import com.sap.sdo.api.types.schema.ExplicitGroup;
import com.sap.sdo.api.types.schema.Import;
import com.sap.sdo.api.types.schema.LocalComplexType;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.types.schema.TopLevelComplexType;
import com.sap.sdo.api.types.schema.TopLevelElement;
import com.sap.sdo.api.types.schema.Union;
import com.sap.sdo.api.types.schema.Wildcard;
import com.sap.sdo.api.types.schema.hfp.HasFacet;
import com.sap.sdo.api.types.schema.hfp.HasProperty;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.util.VisitorException;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class SchemaTypesTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public SchemaTypesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }


    @Test
    public void testReadSchemaElementQualAttributeQual() throws IOException {
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        typeHelper.getType(Schema.class);
        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String path = "com/sap/sdo/testcase/schemas/";
        URL xsdUrl = getClass().getClassLoader().getResource(path + "ElementQualAttributeQual.xsd");
        XMLDocument document = xmlHelper.load(xsdUrl.openStream(), xsdUrl.toString(), "parseXSD");

        StringWriter xml = new StringWriter();
        xmlHelper.save(document, xml, null);
        System.out.println(xml);

    }


    @Test
    public void testReadSchemaExtendedInstanceClass() throws IOException {
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        typeHelper.getType(Schema.class);
        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        String path = "com/sap/sdo/testcase/schemas/";
        URL xsdUrl = getClass().getClassLoader().getResource(path + "ExtendedInstanceClass.xsd");
        XMLDocument document = xmlHelper.load(xsdUrl.openStream(), xsdUrl.toString(), "parseXSD");

        StringWriter xml = new StringWriter();
        xmlHelper.save(document, xml, null);
        System.out.println(xml);

    }

    @Test
    public void testSequenced() throws IOException {
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type type = typeHelper.getType(ExplicitGroup.class);
        assertTrue(type.isSequenced());
    }

    @Test
    public void testSchemaExtension() throws IOException {
        HelperContext helperContext = _helperContext;

        // this has to be done once
        DataObject propertyDo = helperContext.getDataFactory().create("commonj.sdo", "Property");
        propertyDo.setString("name", "extensionAtt");
        Type type = helperContext.getTypeHelper().getType("commonj.sdo", "String");
        propertyDo.set("type", type);
        Property xmlElementProperty = helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "xmlElement");
        propertyDo.setBoolean(xmlElementProperty, false);
        helperContext.getTypeHelper().defineOpenContentProperty("http://com.cap/extension", propertyDo);

        // now the new Property is available in the TypeHelper
        Property extensionProperty = helperContext.getTypeHelper().getOpenContentProperty("http://com.cap/extension", "extensionAtt");

        // usage of the new Property
        TopLevelElement element = (TopLevelElement)helperContext.getDataFactory().create(TopLevelElement.class);
        element.setName("element_name");
        ((DataObject)element).setString(extensionProperty, "my_extension");
        Schema schema = (Schema)helperContext.getDataFactory().create(Schema.class);
        schema.getElement().add(element);
        System.out.println(_helperContext.getXMLHelper().save(schema, ((DataObject)schema).getType().getURI(), "schema"));
    }

    @Test
    public void testSchemaSchema() throws IOException {
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        String path = "com/sap/sdo/testcase/external/schema/";
        typeHelper.getType(Schema.class);
        typeHelper.getType(HasFacet.class);
        typeHelper.getType(HasProperty.class);
        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        URL xsdUrl = getClass().getClassLoader().getResource(path + "XMLSchema.xsd");
        XMLDocument document = xmlHelper.load(xsdUrl.openStream(), xsdUrl.toString(), "parseXSD");

        StringWriter xml = new StringWriter();
        xmlHelper.save(document, xml, null);
        System.out.println(xml);
    }

    public void xtestSchemaHasFacetAndProperty() throws IOException, VisitorException {
        String path = "com/sap/sdo/testcase/external/schema/";
        URL hfpUrl = getClass().getClassLoader().getResource(path + "XMLSchema-hasFacetAndProperty.xsd");
        List types = _helperContext.getXSDHelper().define(hfpUrl.openStream(), hfpUrl.toString());
        InterfaceGenerator interfaceGenerator = ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        interfaceGenerator.addPackage("http://www.w3.org/2001/XMLSchema-hasFacetAndProperty", "com.sap.sdo.api.types.schema.hfp");
        List<String> classNames = interfaceGenerator.generate(types);

        System.out.println(classNames);
    }

    @Test
    public void testSequenceMove() {
        Schema schema = (Schema)_helperContext.getDataFactory().create(Schema.class);
        TopLevelElement element1 = (TopLevelElement)_helperContext.getDataFactory().create(TopLevelElement.class);
        element1.setName("element1");
        schema.getElement().add(element1);
        TopLevelElement element2 = (TopLevelElement)_helperContext.getDataFactory().create(TopLevelElement.class);
        element2.setName("element2");
        schema.getElement().add(element2);
        Import importStatement = (Import)_helperContext.getDataFactory().create(Import.class);
        importStatement.setNamespace("com.sap.test");
        schema.getImport().add(importStatement);
        int size = ((DataObject)schema).getSequence().size();
        if (size > 1) {
            // move to the top
            ((DataObject)schema).getSequence().move(0, size - 1);
        }
        assertSame(importStatement, ((DataObject)schema).getSequence().getValue(0));
    }

    @Test
    public void testDerivationSet() {
        ComplexType complexType = (ComplexType)_helperContext.getDataFactory().create(TopLevelComplexType.class);
        complexType.setBlock(Collections.singletonList("restriction"));
        assertEquals(Collections.singletonList("restriction"), complexType.getBlock());
    }

    @Test
    public void testLang() {
        Schema schema = (Schema)_helperContext.getDataFactory().create(Schema.class);
        SdoType schemaType = (SdoType)schema.getType();
        DataObject langProp = (DataObject)schemaType.getProperty("lang");
        assertNotNull(langProp);
        String ref = langProp.getString(PropertyType.getReferenceProperty());
        assertEquals("http://www.w3.org/XML/1998/namespace#lang", ref);

        Property prop = schemaType.getPropertyFromXmlName("http://www.w3.org/XML/1998/namespace", "lang", false);
        assertEquals(langProp, prop);

        schema.setLang("EN");
        String xsd = _helperContext.getXMLHelper().save(schema, schema.getType().getURI(), "schema");
        System.out.println(xsd);
        assertTrue(xsd.contains("xml:lang"));

        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xsd);
        Schema schema2 = (Schema)xmlDocument.getRootObject();
        assertEquals("EN", schema2.getLang());
        String xsd2 = _helperContext.getXMLHelper().save(schema2, schema2.getType().getURI(), "schema");
        System.out.println(xsd2);
        assertTrue(xsd2.contains("xml:lang"));

    }

    @Test
    public void testDerivationSetGeneric() {
        DataObject complexType = _helperContext.getDataFactory().create(TopLevelComplexType.class);
        complexType.setString("block", "restriction");
        assertEquals("restriction", complexType.getString("block"));
    }

    @Test
    public void testWildcard() {
        Wildcard wildcard = (Wildcard)_helperContext.getDataFactory().create(Wildcard.class);
        assertEquals(Collections.singletonList("##any"), wildcard.getNamespace());
        try {
            wildcard.getNamespace().add("http://test");
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            //expected
        }

        Wildcard wildcard2 = (Wildcard)_helperContext.getDataFactory().create(Wildcard.class);
        assertEquals(Collections.singletonList("##any"), wildcard2.getNamespace());

        List<String> namespace = new ArrayList<String>();
        namespace.add("http://test");
        wildcard.setNamespace(namespace);
        assertEquals(Collections.singletonList("http://test"), wildcard.getNamespace());
        wildcard.getNamespace().add("http://test2");
        assertEquals(2, wildcard.getNamespace().size());

        List<String> namespace2 = new ArrayList<String>();
        namespace2.add("http://test");
        wildcard2.setNamespace(namespace2);
        assertEquals(Collections.singletonList("http://test"), wildcard2.getNamespace());
        namespace2.add("http://test2");
        assertEquals(wildcard2.getNamespace().toString(), 2, wildcard2.getNamespace().size());
    }

    @Test
    public void testFormDefault() {
        Schema schema = (Schema)_helperContext.getDataFactory().create(Schema.class);
        assertEquals("unqualified", schema.getElementFormDefault());
        assertEquals("unqualified", schema.getAttributeFormDefault());
        schema.setElementFormDefault("qualified");
        schema.setAttributeFormDefault("qualified");
        assertEquals("qualified", schema.getElementFormDefault());
        assertEquals("qualified", schema.getAttributeFormDefault());
        try {
            schema.setElementFormDefault(null);
            assertEquals("unqualified", schema.getElementFormDefault());
        } catch (Exception e) {
            // expected
        }
        try {
            schema.setAttributeFormDefault(null);
            assertEquals("unqualified", schema.getAttributeFormDefault());
        } catch (Exception e) {
            // expected
        }
        try {
            schema.setElementFormDefault("true");
            fail("exception expected");
        } catch (Exception e) {
            // expected
        }
        try {
            schema.setAttributeFormDefault("true");
            fail("exception expected");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testAppInfo1() {
        Schema schema = (Schema)_helperContext.getDataFactory().create(Schema.class);
        Annotation annotation = (Annotation)_helperContext.getDataFactory().create(Annotation.class);
        Appinfo appInfo = (Appinfo)_helperContext.getDataFactory().create(Appinfo.class);
        appInfo.setSource("http://sap.com/xi/TextID");
        Sequence sequence = ((DataObject)appInfo).getSequence();
        sequence.addText("value");
        schema.getAnnotation().add(annotation);
        annotation.getAppinfo().add(appInfo);
        String xml = _helperContext.getXMLHelper().save(schema, "http://www.w3.org/2001/XMLSchema", "schema");
        System.out.println(xml);
        assertTrue(xml.contains(">value<"));
    }

    @Test
    public void testAppInfo2() {
        HelperContext helperContext = _helperContext;

        Schema schema = (Schema)helperContext.getDataFactory().create(Schema.class);
        Annotation annotation = (Annotation)helperContext.getDataFactory().create(Annotation.class);
        DataObject appInfo = helperContext.getDataFactory().create(Appinfo.class);
        ((Appinfo)appInfo).setSource("http://sap.com/xi/LifeCycleInfo");

        DataObject properties = appInfo.createDataObject(getMixedTextProperty("urn:com-sap:ifr:v2:wsdl", "properties", true));
        DataObject category = properties.createDataObject(getMixedTextProperty("urn:com-sap:ifr:v2:wsdl", "category", true));
        category.getSequence().addText("ifmmessif");

        DataObject lifeCycleInfo = properties.createDataObject(getMixedTextProperty("urn:com-sap:ifr:v2:wsdl", "lifeCycleInfo", true));
        DataObject objectState = lifeCycleInfo.createDataObject(getMixedTextProperty(null, "objectState", true));

        DataObject runtimeVersion = properties.createDataObject(getMixedTextProperty("urn:com-sap:ifr:v2:wsdl", "runtimeVersion", true));
        runtimeVersion.setString(getAttributeProperty(null, "uri"), "urn:sap-com:soap:application:esr:server:710");

        DataObject additionalProperties = appInfo.createDataObject(getMixedTextProperty(null, "additionalProperties", true));
        additionalProperties.setString(getAttributeProperty(null, "attribute"), "test");
        additionalProperties.getSequence().addText("addition");

        schema.getAnnotation().add(annotation);
        annotation.getAppinfo().add((Appinfo)appInfo);
        String xml = helperContext.getXMLHelper().save(schema, "http://www.w3.org/2001/XMLSchema", "schema");
        System.out.println(xml);
    }

    private Property getMixedTextProperty(String uri, String name, boolean many) {
        HelperContext helperContext = _helperContext;

        DataObject propertyDo = helperContext.getDataFactory().create("commonj.sdo", "Property");
        propertyDo.setString("name", name);
        Type type = helperContext.getTypeHelper().getType("commonj.sdo", "Text");
        propertyDo.set("type", type);
        propertyDo.set("many", many);
        propertyDo.set("containment", true);
        Property xmlElementProperty = helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "xmlElement");
        propertyDo.setBoolean(xmlElementProperty, true);

        if (uri != null) {
            Property refProperty = helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "ref");
            // this simulates a reference to a TopLevelElement and so the namespace is always rendered
            String ref = uri + '#' + name;
            propertyDo.setString(refProperty, ref);
        }

        return helperContext.getTypeHelper().defineOpenContentProperty(null, propertyDo);
    }

    private Property getAttributeProperty(String uri, String name) {
        HelperContext helperContext = _helperContext;

        DataObject propertyDo = helperContext.getDataFactory().create("commonj.sdo", "Property");
        propertyDo.setString("name", name);
        Type type = helperContext.getTypeHelper().getType("commonj.sdo", "String");
        propertyDo.set("type", type);
        propertyDo.set("many", false);
        propertyDo.set("containment", false);
        Property xmlElementProperty = helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "xmlElement");
        propertyDo.setBoolean(xmlElementProperty, false);

        if (uri != null) {
            Property refProperty = helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "ref");
            // this simulates a reference to a TopLevelAttribute and so the namespace is always rendered
            String ref = uri + '#' + name;
            propertyDo.setString(refProperty, ref);
        }

        return helperContext.getTypeHelper().defineOpenContentProperty(null, propertyDo);
    }

    @Test
    public void testDocumentation() {
        HelperContext helperContext = _helperContext;

        Schema schema = (Schema)helperContext.getDataFactory().create(Schema.class);
        Annotation annotation = (Annotation)helperContext.getDataFactory().create(Annotation.class);
        DataObject appInfo = helperContext.getDataFactory().create(Appinfo.class);

        Property documentationProp = helperContext.getTypeHelper().getOpenContentProperty(schema.getType().getURI(), "documentation");
        DataObject documentation = appInfo.createDataObject(documentationProp);
        final String greeting = "<greeting>Hello, world!</greeting>";
        documentation.getSequence().addText(greeting);

        schema.getAnnotation().add(annotation);
        annotation.getAppinfo().add((Appinfo)appInfo);
        String xml = helperContext.getXMLHelper().save(schema, "http://www.w3.org/2001/XMLSchema", "schema");
        System.out.println(xml);

        XMLDocument document = helperContext.getXMLHelper().load(xml);

        DataObject documentation2 = document.getRootObject().getDataObject("annotation.0/appinfo.0/documentation.0");
        assertNotNull(documentation2);

        Sequence sequence = documentation2.getSequence();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.size(); i++) {
            sb.append(sequence.getValue(i));
            assertNull(sequence.getProperty(i));
        }

        assertEquals(greeting, sb.toString());

    }


    @Test
    public void testOpenContent() {
        LocalComplexType localComplexType = (LocalComplexType)_helperContext.getDataFactory().create(LocalComplexType.class);
        Property nameProp = _helperContext.getTypeHelper().getOpenContentProperty(URINamePair.PROP_XML_NAME.getURI(), URINamePair.PROP_XML_NAME.getName());
        int oldsize = ((DataObject)localComplexType).getInstanceProperties().size();
        assertNull(((DataObject)localComplexType).getString(nameProp));
        int newsize = ((DataObject)localComplexType).getInstanceProperties().size();
        assertEquals(oldsize, newsize);
    }

    @Test
    public void testAnnotationsExample() throws IOException {
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        typeHelper.getType(Schema.class);
        final String schemaFileName = "com/sap/sdo/testcase/schemas/sdoAnnotationsExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        XMLDocument document = xmlHelper.load(url.openStream(), url.toString(), "parseXSD");
        Schema schema = (Schema)document.getRootObject();
        LocalComplexType complexType = (LocalComplexType)((DataObject)schema).getDataObject("complexType[name=\"Items\"]/sequence/element[name=\"item\"]/complexType");
        assertNotNull(complexType);
        System.out.println(_helperContext.getXMLHelper().save(complexType, complexType.getType().getURI(), "complexType"));
        Property sdoXmlNameProp = typeHelper.getOpenContentProperty("commonj.sdo/xml", "name");
        assertNotNull(sdoXmlNameProp);
        String sdoName = ((DataObject)complexType).getString(sdoXmlNameProp);
        assertEquals("Item", sdoName);
    }

    @Test
    public void testComplexType() {
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type complexType = typeHelper.getType(ComplexType.class);
        Property mixedProp = complexType.getProperty("mixed");
        Type booleanType = typeHelper.getType(boolean.class);
        assertEquals(booleanType, mixedProp.getType());

        Property abstractProp = complexType.getProperty("abstract");
        assertEquals(booleanType, abstractProp.getType());

    }

    @Test
    public void testUnionTypeType() {
        Union union = (Union)_helperContext.getDataFactory().create(Union.class);
        List<String> qNames = new ArrayList<String>();
        qNames.add("http://www.w3.org/2001/XMLSchema#string");
        qNames.add("http://www.w3.org/2001/XMLSchema#integer");
        union.setMemberTypes(qNames);
        String xsd = _helperContext.getXMLHelper().save(union, "http://www.w3.org/2001/XMLSchema", "union");
        assertTrue(xsd, xsd.contains("xsd:string xsd:integer"));

        union.setString("memberTypes", "http://www.w3.org/2001/XMLSchema#integer http://www.w3.org/2001/XMLSchema#string");
        String xsd2 = _helperContext.getXMLHelper().save(union, "http://www.w3.org/2001/XMLSchema", "union");
        assertTrue(xsd2, xsd2.contains("xsd:integer xsd:string"));
    }


}
