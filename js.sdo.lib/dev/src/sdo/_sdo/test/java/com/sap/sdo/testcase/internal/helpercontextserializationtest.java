package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.java.InterfaceGeneratorImpl;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.types.simple.ListSimpleType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.DataGraphRootIntf;
import com.sap.sdo.testcase.typefac.DoubleMVIntf;
import com.sap.sdo.testcase.typefac.SequencedOppositeIntf;
import com.sap.sdo.testcase.typefac.SimpleTypesIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;

public class HelperContextSerializationTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public HelperContextSerializationTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }


    public static final String XSD = "com/sap/sdo/api/types/ctx/Context.xsd";

    @Test
    public void testInterfaceGeneration() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(XSD);

        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        SapXmlDocument xmlDocument = xmlHelper.load(xsdUrl.openStream(), xsdUrl.toString(), options);
        List types = xmlDocument.getDefinedTypes();
        Map namespaceToProperties = xmlDocument.getDefinedProperties();

        InterfaceGeneratorImpl javaGenerator = new InterfaceGeneratorImpl("C:/temp", namespaceToProperties);
        javaGenerator.addSchemaLocation("http://sap.com/sdo/api/types/ctx", "Context.xsd");
        javaGenerator.generate(types);

        //TODO compare it with sources, like schema-classes
    }

    @Test
    public void testSerializeContext() {
        _helperContext.getTypeHelper().getType(DoubleMVIntf.class);
        _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        _helperContext.getTypeHelper().getType(SimpleTypesIntf.class);


        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        Property defaultRootNameProp = _helperContext.getTypeHelper().getOpenContentProperty("com.sap.sdo.testcase.typefac", "defaultRootName");
        assertEquals("defaultRootName", defaultRootNameProp.getName());
        assertEquals("com.sap.sdo.testcase.typefac", defaultRootNameProp.getContainingType().getURI());
        assertEquals(true, defaultRootNameProp.isOpenContent());
    }

    @Test
    public void testSerializeEmptyContext() {
        StringWriter xml = new StringWriter();
        String id = _helperContext.getId();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        SapHelperProvider.removeContext(_helperContext);
        List<HelperContext> helperContexts = SapHelperProvider.deserializeContexts(new StringReader(xml.toString()));
        assertEquals(1, helperContexts.size());
        SapHelperContext hc = (SapHelperContext)helperContexts.get(0);
        assertEquals(id, hc.getId());
    }

    @Test
    public void testSerializeContextAndParse() throws IOException {
        _helperContext.getTypeHelper().getType(DoubleMVIntf.class);
        _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        _helperContext.getTypeHelper().getType(SimpleTypesIntf.class);


        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        HelperContext newHelperContext = SapHelperProvider.getNewContext();
//        TypeHelper newTypeHelper = newHelperContext.getTypeHelper();
//        newTypeHelper.getType(HelperContexts.class);
        SapXmlDocument xmlDocument = (SapXmlDocument)newHelperContext.getXMLHelper().load(xml.toString());
        StringWriter stringWriter = new StringWriter();
        newHelperContext.getXMLHelper().save(xmlDocument, stringWriter, null);
        System.out.println(stringWriter.toString());

        List<DataObject> types = xmlDocument.getDefinedTypes();
        assertEquals(4, types.size());
    }

    @Test
    public void testDeserializeContext() {
        _helperContext.getTypeHelper().getType(DoubleMVIntf.class);
        _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        _helperContext.getTypeHelper().getType(SimpleTypesIntf.class);


        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml.toString());
        String contextId = SapHelperProvider.getContextId(_helperContext);
        SapHelperProvider.removeContext(_helperContext);

        List<HelperContext> helperContexts = SapHelperProvider.deserializeContexts(new StringReader(xml.toString()));

        assertEquals(1, helperContexts.size());
        final HelperContext newHelperContext = helperContexts.get(0);
        assertSame(SapHelperProvider.getContext(contextId), newHelperContext);
        assertNotSame(_helperContext, newHelperContext);

        DataFactory newDataFactory = _helperContext.getDataFactory();
        TypeHelper newTypeHelper = _helperContext.getTypeHelper();
        DataObject doubleMVIntf = newDataFactory.create("com.sap.sdo.testcase.typefac", "DoubleMVIntf");
        DataObject dataGraphRootIntf = newDataFactory.create("com.sap.sdo.testcase.typefac", "DataGraphRootIntf");

        assertTrue(doubleMVIntf instanceof DoubleMVIntf);
        assertTrue(dataGraphRootIntf instanceof DataGraphRootIntf);

        Property prop = newTypeHelper.getOpenContentProperty("com.sap.sdo.testcase.typefac", "defaultRootName");
        assertEquals("com.sap.sdo.testcase.typefac", _helperContext.getXSDHelper().getNamespaceURI(prop));

        Type sequencedOppositeIntfType = newTypeHelper.getType("com.sap.sdo.testcase.typefac", "SequencedOppositeIntf");
        Property mvProp = sequencedOppositeIntfType.getProperty("mv");
        Property svProp = sequencedOppositeIntfType.getProperty("sv");

        assertSame(svProp, mvProp.getOpposite());
        assertSame(mvProp, svProp.getOpposite());

    }

    @Test
    public void testListSimpleType() throws IOException {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/SOAP.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        String contextId = SapHelperProvider.getContextId(_helperContext);
        SapHelperProvider.removeContext(_helperContext);

        assertNotSame(_helperContext, SapHelperProvider.getContext(contextId));

        List<HelperContext> helperContexts = SapHelperProvider.deserializeContexts(new StringReader(xml.toString()));

        assertEquals(1, helperContexts.size());
        final HelperContext newHelperContext = helperContexts.get(0);
        assertSame(SapHelperProvider.getContext(contextId), newHelperContext);
        assertNotSame(_helperContext, newHelperContext);

        TypeHelper newTypeHelper = newHelperContext.getTypeHelper();
        Type encodingStyle = newTypeHelper.getType("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");

        assertEquals(List.class, encodingStyle.getInstanceClass());
        List<String> encodings = (List<String>)newHelperContext.getDataHelper().convert(encodingStyle, "http://schemas.xmlsoap.org/soap/encoding/ another/encoding");
        assertEquals(2, encodings.size());
        assertEquals("http://schemas.xmlsoap.org/soap/encoding/", encodings.get(0));
        assertEquals("another/encoding", encodings.get(1));

        StringWriter newXml = new StringWriter();
        SapHelperProvider.serializeContexts(Collections.singletonList(newHelperContext), newXml);
        assertEquals(xml.toString(), newXml.toString());

        assertSame(SapHelperProviderImpl.getCoreContext(), ((HelperContextImpl)newHelperContext).getParent());

        assertSame(newHelperContext, ((SdoType)encodingStyle).getHelperContext());

        Type baseType = (Type)encodingStyle.getBaseTypes().get(0);
        assertTrue(baseType.getClass().getName(), baseType instanceof ListSimpleType);
        Type itemType = ((ListSimpleType)baseType).getItemType();
        assertSame(JavaSimpleType.URI, itemType);
    }

    @Test
    public void testFacetXsd() {
        Type facetType = TypeType.getFacetsType();
        String xsd = _helperContext.getXSDHelper().generate(Collections.singletonList(facetType));
        System.out.println(xsd);
    }

    @Test
    public void testSerializeType() throws Exception {
        Type dataGraphRoot = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        Type sequencedOpposite = _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class);

        String dataGraphRootSer = _helperContext.getXMLHelper().save(
            (DataObject)dataGraphRoot, URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());

        String sequencedOppositeSer = _helperContext.getXMLHelper().save(
            (DataObject)sequencedOpposite, URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());

        HelperContext newCtx = SapHelperProvider.getNewContext();
        XMLDocument dataGraphRootDoc;
        try {
            dataGraphRootDoc = newCtx.getXMLHelper().load(dataGraphRootSer);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testWriteReadContextWithSchema1() throws IOException {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/SOAP.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/wsdl.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        SapHelperProvider.removeContext(_helperContext);
        List<HelperContext> contexts = SapHelperProvider.deserializeContexts(new StringReader(xml.toString()));
        assertEquals(1, contexts.size());

        HelperContext newHelperContext = contexts.get(0);
        StringWriter xml2 = new StringWriter();
        SapHelperProvider.serializeContexts(Collections.singletonList(newHelperContext), xml2);
        assertEquals(xml.toString(), xml2.toString());

    }

    @Test
    public void testWriteReadContextWithSchema2() throws IOException {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/sca10/sca-core.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/sca10/sca-interface-java.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        SapHelperProvider.removeContext(_helperContext);
        List<HelperContext> contexts = SapHelperProvider.deserializeContexts(new StringReader(xml.toString()));
        assertEquals(1, contexts.size());

        HelperContext newHelperContext = contexts.get(0);
        StringWriter xml2 = new StringWriter();
        SapHelperProvider.serializeContexts(Collections.singletonList(newHelperContext), xml2);
        assertEquals(xml.toString(), xml2.toString());

        Property interfaceJavaProp = newHelperContext.getTypeHelper().getOpenContentProperty("http://www.osoa.org/xmlns/sca/1.0", "interface.java");
        Property javaNameProp = ((DataObject)interfaceJavaProp).getInstanceProperty("javaName");
        Property realJavaNameProp = newHelperContext.getTypeHelper().getOpenContentProperty(URINamePair.DATATYPE_JAVA_URI, PropertyConstants.JAVA_NAME);
        assertSame(realJavaNameProp, javaNameProp);
    }

    @Test
    public void testWriteReadContextWithSchema3() throws IOException {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/Groups.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        SapHelperProvider.removeContext(_helperContext);
        List<HelperContext> contexts = SapHelperProvider.deserializeContexts(new StringReader(xml.toString()));
        assertEquals(1, contexts.size());

        HelperContext newHelperContext = contexts.get(0);
        StringWriter xml2 = new StringWriter();
        SapHelperProvider.serializeContexts(Collections.singletonList(newHelperContext), xml2);
        assertEquals(xml.toString(), xml2.toString());

    }


    @Test
    public void testDeserializeContextInto() throws IOException {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/SOAP.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        StringWriter xml = new StringWriter();
        SapHelperProvider.serializeContexts((List)Collections.singletonList(_helperContext), xml);
        System.out.println(xml);

        HelperContext newHelperContext = SapHelperProvider.getNewContext();
        SapHelperProvider.deserializeContextInto(new StringReader(xml.toString()), newHelperContext);


        TypeHelper newTypeHelper = newHelperContext.getTypeHelper();
        Type encodingStyle = newTypeHelper.getType("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");

        assertEquals(List.class, encodingStyle.getInstanceClass());
        List<String> encodings = (List<String>)newHelperContext.getDataHelper().convert(encodingStyle, "http://schemas.xmlsoap.org/soap/encoding/ another/encoding");
        assertEquals(2, encodings.size());
        assertEquals("http://schemas.xmlsoap.org/soap/encoding/", encodings.get(0));
        assertEquals("another/encoding", encodings.get(1));

        StringWriter newXml = new StringWriter();
        SapHelperProvider.serializeContexts(Collections.singletonList(newHelperContext), newXml);

        assertSame(SapHelperProviderImpl.getCoreContext(), ((HelperContextImpl)newHelperContext).getParent());

        assertSame(newHelperContext, ((SdoType)encodingStyle).getHelperContext());

        Type baseType = (Type)encodingStyle.getBaseTypes().get(0);
        assertTrue(baseType.getClass().getName(), baseType instanceof ListSimpleType);
        Type itemType = ((ListSimpleType)baseType).getItemType();
        assertSame(JavaSimpleType.URI, itemType);
    }

}
