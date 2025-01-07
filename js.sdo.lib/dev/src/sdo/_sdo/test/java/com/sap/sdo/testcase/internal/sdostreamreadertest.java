/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.types.simple.ListSimpleType;
import com.sap.sdo.impl.xml.stream.SdoNamespaceContext;
import com.sap.sdo.impl.xml.stream.SdoStreamReader;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.LoggingRootIntf;
import com.sap.sdo.testcase.typefac.NonSequencedInheritedIntf;
import com.sap.sdo.testcase.typefac.NonSequencedParentIntf;
import com.sap.sdo.testcase.typefac.OpenInterface;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SdoStreamReaderTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SdoStreamReaderTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private SapXmlHelper _helper;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _helper  = (SapXmlHelper)_helperContext.getXMLHelper();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _helper = null;
    }

    @Test
    public void testReader() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        SdoProperty globalProperty =
            (SdoProperty)_helperContext.getXSDHelper().getGlobalProperty(
                doc.getRootElementURI(),
                doc.getRootElementName(),
                true);
        assertNotNull(globalProperty);

        String original =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());

        XMLStreamReader reader = new SdoStreamReader(doc, null, _helperContext);
//        assertEquals(true, reader.hasNext());
//
//        do {
//            if (reader.hasName()) {
//                System.out.println("name: " + reader.getName());
//            }
//            if (reader.hasText()) {
//                System.out.println("text: " + reader.getText());
//            }
//            reader.next();
//        } while (reader.hasNext());
//
//        elementAdapter = new ElementAdapter(globalProperty, doc.getRootObject());
//        reader = new SdoStreamReader(elementAdapter);
        doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertEquals(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testMultiValue() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ContactList.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ContactList.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        SdoProperty globalProperty =
            (SdoProperty)_helperContext.getXSDHelper().getGlobalProperty(
                doc.getRootElementURI(),
                doc.getRootElementName(),
                true);
        assertNotNull(globalProperty);

        String original =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        System.out.println(original);

        XMLStreamReader reader = new SdoStreamReader(doc, null, _helperContext);

        doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertEquals(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testXsiType() throws Exception {
        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyType.NAME, "typeProp");
        propObj.set(
            PropertyType.TYPE,
            _helperContext.getTypeHelper().getType(
                URINamePair.TYPE.getURI(),
                URINamePair.TYPE.getName()));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        propObj.set(PropertyType.getXsdTypeProperty(), URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat());
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty(
                "com.sap.sdo.testcase.typefac", propObj);

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.set(prop, null);

        String original = _helper.save(data, "com.sap.sdo.testcase.typefac", "root");
        XMLDocument doc = _helper.load(original);
        assertNotNull(doc);
        DataObject reloaded = doc.getRootObject();
        assertNotNull(reloaded);

        XMLStreamReader reader = new SdoStreamReader(doc, null, _helperContext);

        doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertEquals(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testXsiType2() throws Exception {
        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyType.NAME, "globalQNameProp");
        // propObj.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "URI"));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        // propObj.set(PropertyType.MANY, true);
        propObj.set(PropertyType.getXsdTypeProperty(), URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat());
        ListSimpleType type = new ListSimpleType(
            (SdoType)_helperContext.getTypeHelper().getType("commonj.sdo", "URI"),
            new URINamePair("com.sap.sdo.testcase.typefac", "URIS"),
            _helperContext);
        type.setXsdType(URINamePair.SCHEMA_Q_NAME);
        propObj.set(PropertyType.TYPE, type);
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty(
                "com.sap.sdo.testcase.typefac", propObj);

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        List<String> types = new ArrayList<String>();
        types.add(new URINamePair(data.getType().getURI(), data.getType().getName()).toStandardSdoFormat());
        types.add(new URINamePair(prop.getType().getURI(), prop.getType().getName()).toStandardSdoFormat());
        data.setList(prop, types);

        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        loggingRoot.setOpenInterface((OpenInterface)data);

        String original = _helper.save((DataObject)loggingRoot, "com.sap.sdo.testcase.typefac", "root");

        XMLDocument doc = _helper.load(original);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        XMLStreamReader reader = new SdoStreamReader(doc, null, _helperContext);

        doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());

        assertEquals(original, xml);

        System.out.println(xml);

    }

    @Test
    public void testXsiNil() throws Exception {
        final String schemaFileName = PACKAGE + "ProcessLeaveRequest.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertFalse(types.isEmpty());

        Type context =
            _helperContext.getTypeHelper().getType(
                "http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest",
                "Context_466FB40FA8360D30198D11DC83BC003005C5EA9E");
        assertNotNull(context);

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.set("prop", _helperContext.getDataFactory().create(context));

        String original = _helper.save(data, "com.sap.sdo.testcase.typefac", "root");
        XMLDocument doc = _helper.load(original);
        assertNotNull(doc);
        DataObject reloaded = doc.getRootObject();
        assertNotNull(reloaded);

        XMLStreamReader reader = new SdoStreamReader(doc, null, _helperContext);

        doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertEquals(original, xml);
        System.out.println(xml);

    }

    @Test
    public void testNilString() throws Exception {
        String original =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:type=\"xsd:string\" xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        XMLDocument xmlDocument = _helper.load(original);
        assertLineEquality(
            original,
            _helper.save(
                xmlDocument.getRootObject(),
                xmlDocument.getRootElementURI(),
                xmlDocument.getRootElementName()));

        XMLStreamReader reader = new SdoStreamReader(xmlDocument, null, _helperContext);

        XMLDocument doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);

        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertLineEquality(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testNilAnyType() throws Exception {
        String original =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        Map options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        XMLDocument xmlDocument = _helper.load(new StringReader(original), null, options);
        assertLineEquality(
            original,
            _helper.save(
                xmlDocument.getRootObject(),
                xmlDocument.getRootElementURI(),
                xmlDocument.getRootElementName()));

        XMLStreamReader reader = new SdoStreamReader(xmlDocument, null, _helperContext);

        XMLDocument doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, options);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertLineEquality(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testNilInteger() throws Exception {
        String original =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:type=\"xsd:integer\" xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        XMLDocument xmlDocument = _helper.load(original);
        assertLineEquality(
            original,
            _helper.save(
                xmlDocument.getRootObject(),
                xmlDocument.getRootElementURI(),
                xmlDocument.getRootElementName()));

        XMLStreamReader reader = new SdoStreamReader(xmlDocument, null, _helperContext);

        XMLDocument doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertLineEquality(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testNonContainment() throws Exception {
        NonSequencedParentIntf do1 = (NonSequencedParentIntf)_helperContext.getDataFactory().create(NonSequencedParentIntf.class);
        NonSequencedInheritedIntf do2 = (NonSequencedInheritedIntf)_helperContext.getDataFactory().create(NonSequencedInheritedIntf.class);
        do1.setName("Parent");
        do1.getReferenced().add(do2);
        do1.getContained().add(do2);
        do2.setName("Child");
        do2.setMoreInfo("extends NonSequencedParentIntf");
        String original = _helperContext.getXMLHelper().save((DataObject)do1, "com.sap.sdo.testcase.typefac", "SequencedParentIntf");

//        SapXmlDocument doc = ((XMLHelperDelegator)_helperContext.getXMLHelper()).load(original);
//        assertNotNull(doc);
//        String xml =
//            _helperContext.getXMLHelper().save(
//                doc.getRootObject(),
//                doc.getRootElementURI(),
//                doc.getRootElementName());
//        assertEquals(original, xml);


        XMLDocument xmlDocument =
            _helper.createDocument(
                (DataObject)do1,
                "com.sap.sdo.testcase.typefac",
                "SequencedParentIntf");
        assertNotNull(xmlDocument);
        XMLStreamReader reader = new SdoStreamReader(xmlDocument, null, _helperContext);

        XMLDocument doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertEquals(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testReference() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo", "DataGraphType");
        Type nonSequencedParentIntfType = _helperContext.getTypeHelper().getType(NonSequencedParentIntf.class);
        NonSequencedParentIntf do1 = (NonSequencedParentIntf)dataGraph.createRootObject(nonSequencedParentIntfType);
        NonSequencedInheritedIntf do2 = (NonSequencedInheritedIntf)_helperContext.getDataFactory().create(NonSequencedInheritedIntf.class);
        do1.setName("Parent");
        do1.getReferenced().add(do2);
        do1.getContained().add(do2);
        do2.setName("Child");
        do2.setMoreInfo("extends SequencedParentIntf");
        do2.setAttribute("Attribute");
        do2.setExtraAttribute("Extra");
        dataGraph.getChangeSummary().beginLogging();
        do2.setName("Child2");
        do2.setAttribute("Attribute2");
        do2.setExtraAttribute("Extra2");

        String original = _helperContext.getXMLHelper().save((DataObject)dataGraph, "commonj.sdo", "datagraph");

        XMLDocument xmlDocument =
            _helper.createDocument((DataObject)dataGraph, "commonj.sdo", "datagraph");
        assertNotNull(xmlDocument);
        XMLStreamReader reader = new SdoStreamReader(xmlDocument, null, _helperContext);

        XMLDocument doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);
        assertNotNull(doc);
        String xml =
            _helperContext.getXMLHelper().save(
                doc.getRootObject(),
                doc.getRootElementURI(),
                doc.getRootElementName());
        assertEquals(original, xml);
        System.out.println(xml);
    }

    @Test
    public void testXmlStreamTransformation() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        String original = _helper.save(root, doc.getRootElementURI(), doc.getRootElementName());

        SdoStreamReader reader = new SdoStreamReader(doc, null, _helperContext);

        doc = _helperContext.getXMLHelper().load(new StAXSource(reader), null, null);

        assertNotNull(doc);
        root = doc.getRootObject();
        assertNotNull(root);

        assertEquals(
            original,
            _helper.save(root, doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testBase64Binary() throws Exception {
        final String schemaFileName = PACKAGE + "Base64Binary.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        assertNotNull(types);
        assertEquals(1, types.size());

        DataObject graph = _helperContext.getDataFactory().create(DataGraphType.getInstance());
        DataObject root = ((DataGraph)graph).createRootObject("com.sap.sdo.typefaq", "MyBase64Type");
        root.set("base64element", "616263"); //abc - YWJj
        root.set("base64attribute", "313233"); //123 - MTIz

        DataObject data = _helperContext.getDataFactory().create(OpenType.getInstance());
        final Type type = _helperContext.getTypeHelper().getType(URINamePair.DATATYPE_URI, "Bytes");
        final DataObject propObj = _helperContext.getDataFactory().create(PropertyType.getInstance());
        propObj.set(PropertyType.NAME,"value");
        propObj.set(PropertyType.TYPE, type);
        propObj.setBoolean(PropertyType.getXmlElementProperty(), true);
        propObj.setBoolean(PropertyType.getSimpleContentProperty(),true);
        URINamePair xsdUnp = new URINamePair(URINamePair.SCHEMA_URI, "base64Binary");
        if (!SchemaTypeFactory.getInstance().getXsdName(type.getURI(), type.getName()).equals(xsdUnp)) {
            propObj.set(PropertyType.getXsdTypeProperty(), xsdUnp.toStandardSdoFormat());
        }
        Property property = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        data.set(property, "616263313233"); // abc123 - YWJjMTIz
        root.set("element", data);

        String graphXml = _helperContext.getXMLHelper().save(graph, null, "graph");

        root.getChangeSummary().beginLogging();
        root.set("base64attribute", "616263"); //abc - YWJj
        root.set("base64element", "313233"); //123 - MTIz
        data.set(property, "313233616263"); // 123abc - MTIzYWJj

        XMLDocument doc = _helper.createDocument(graph, null, "graph");
        String original = _helper.save(graph, doc.getRootElementURI(), doc.getRootElementName());
        System.out.println(original);

        SdoStreamReader reader = new SdoStreamReader(doc, null, _helperContext);

        doc = _helper.load(new StAXSource(reader), null, null);

        assertNotNull(doc);
        root = doc.getRootObject();
        assertNotNull(root);

        assertEquals(
            original,
            _helper.save(root, doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testReferenceIncludingPrefix() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "globalChild.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "globalChildReferenced.xsd");
        types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        DataObject root = _helperContext.getDataFactory().create(types.get(0));
        DataObject child = root.createDataObject("child");
        child.setString("name", "ein Name");

        root.setDataObject("ref1", child);
        root.setDataObject("ref2", child);

        String xml = _helperContext.getXMLHelper().save(root, "test.sap.com", "root");
        System.out.println(xml);

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);

        Feature.XML_STREAM_READER.testFeature(doc, null);
    }

    @Test
    public void testPI() throws Exception {
        //dummy so far ...

        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader = _helper.createXMLStreamReader(doc, null);
        assertNotNull(reader);

        assertEquals(null, reader.getPIData());
        assertEquals(null, reader.getPITarget());
    }

    @Test
    public void testProperties() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        assertEquals("value", reader.getProperty("key"));
        assertEquals(null, reader.getProperty("prop"));
    }

    @Test
    public void testRequire() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        reader.require(XMLStreamConstants.START_DOCUMENT, null, null);
        try {
            reader.require(XMLStreamConstants.START_ELEMENT, null, null);
            fail("XMLStreamException expected");
        } catch (XMLStreamException ex) {
            assertEquals(
                "event type doesn't match, current is START_DOCUMENT(7)",
                ex.getMessage());
        }
        try {
            reader.require(XMLStreamConstants.START_DOCUMENT, "", null);
            fail("XMLStreamException expected");
        } catch (XMLStreamException ex) {
            assertEquals(
                "namespaceURI doesn't match, current is null",
                ex.getMessage());
        }
        try {
            reader.require(XMLStreamConstants.START_DOCUMENT, null, "");
            fail("XMLStreamException expected");
        } catch (XMLStreamException ex) {
            assertEquals(
                "localName doesn't match, current is null",
                ex.getMessage());
        }
    }

    @Test
    public void testGetElementText() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        try {
            reader.getElementText();
            fail("XMLStreamException expected");
        } catch (XMLStreamException ex) {
            assertEquals(
                "ParseError at [row,col]:[-1,-1]\nMessage: parser must be on START_ELEMENT to read next text",
                ex.getMessage());
        }
        reader.next();
        try {
            reader.getElementText();
            fail("XMLStreamException expected");
        } catch (XMLStreamException ex) {
            assertEquals(
                "ParseError at [row,col]:[-1,-1]\nMessage: element text content may not contain START_ELEMENT",
                ex.getMessage());
        }
    }

    @Test
    public void testNextTag() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        assertEquals(1, reader.nextTag());
        assertEquals(1, reader.nextTag());
        assertEquals(1, reader.nextTag());
        try {
            reader.nextTag();
            fail("XMLStreamException expected");
        } catch (XMLStreamException ex) {
            assertEquals(
                "ParseError at [row,col]:[-1,-1]\nMessage: expected start or end tag",
                ex.getMessage());
        }

    }

    @Test
    public void testWrongCurrentState() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        try {
            reader.getAttributeValue(null, null);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getAttributeCount();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getAttributeName(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getAttributeNamespace(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getAttributeLocalName(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getAttributePrefix(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getAttributeType(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getAttributeValue(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.isAttributeSpecified(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE",
                ex.getMessage());
        }
        try {
            reader.getNamespaceCount();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , END_ELEMENT , NAMESPACE",
                ex.getMessage());
        }
        try {
            reader.getNamespacePrefix(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , END_ELEMENT , NAMESPACE",
                ex.getMessage());
        }
        try {
            reader.getNamespaceURI(0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state is not among the states START_ELEMENT , END_ELEMENT , NAMESPACE",
                ex.getMessage());
        }
    }

    @Test
    public void testGetEventTypeString() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        try {
            reader.getText();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state START_DOCUMENT is not among the states CHARACTERS," +
                " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getText()",
                ex.getMessage());
        }

        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
                assertNotNull(reader.getText());
                try {
                    reader.getName();
                    fail("IllegalStateException expected");
                } catch (IllegalStateException ex) {
                    assertEquals(
                        "expected start or end tag, but was CHARACTERS",
                        ex.getMessage());
                }
            } else if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                try {
                    reader.getText();
                    fail("IllegalStateException expected");
                } catch (IllegalStateException ex) {
                    assertEquals(
                        "Current state START_ELEMENT is not among the states CHARACTERS," +
                        " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getText()",
                        ex.getMessage());
                }
            } else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                try {
                    reader.getText();
                    fail("IllegalStateException expected");
                } catch (IllegalStateException ex) {
                    assertEquals(
                        "Current state END_ELEMENT is not among the states CHARACTERS," +
                        " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getText()",
                        ex.getMessage());
                }
            } else {
                try {
                    reader.getText();
                    fail("IllegalStateException expected");
                } catch (IllegalStateException ex) {
                    assertEquals(
                        "Current state END_DOCUMENT is not among the states CHARACTERS," +
                        " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getText()",
                        ex.getMessage());
                }
            }
        }
    }

    @Test
    public void testUnavailableText() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        try {
            reader.getText();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state START_DOCUMENT is not among the states CHARACTERS," +
                " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getText()",
                ex.getMessage());
        }
        try {
            reader.getTextCharacters();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state START_DOCUMENT is not among the states CHARACTERS," +
                " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getTextCharacters()",
                ex.getMessage());
        }
        try {
            reader.getTextCharacters(0, new char[0], 0, 0);
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state START_DOCUMENT is not among the states CHARACTERS," +
                " CDATA, SPACE valid for getTextCharacters()",
                ex.getMessage());
        }
        try {
            reader.getTextStart();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state START_DOCUMENT is not among the states CHARACTERS," +
                " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getTextStart()",
                ex.getMessage());
        }
        try {
            reader.getTextLength();
            fail("IllegalStateException expected");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Current state START_DOCUMENT is not among the states CHARACTERS," +
                " COMMENT, CDATA, SPACE, ENTITY_REFERENCE, DTD valid for getTextLength()",
                ex.getMessage());
        }
    }

    @Test
    public void testNamespaceContext() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        XMLStreamReader reader =
            _helper.createXMLStreamReader(doc, Collections.singletonMap("key", (Object)"value"));
        assertNotNull(reader);

        SdoNamespaceContext nsCtx = (SdoNamespaceContext)reader.getNamespaceContext();
        assertNotNull(nsCtx);

        try {
            nsCtx.getNamespaceURI(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("illegal prefix: null", ex.getMessage());
        }
        try {
            nsCtx.getPrefixes(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("illegal namespaceURI: null", ex.getMessage());
        }
        assertEquals(XMLConstants.NULL_NS_URI, nsCtx.getPrefix(XMLConstants.DEFAULT_NS_PREFIX));
        Iterator<String> prefixes = nsCtx.getPrefixes(XMLConstants.DEFAULT_NS_PREFIX);
        assertEquals(true, prefixes.hasNext());
        assertEquals(XMLConstants.NULL_NS_URI, prefixes.next());
        assertEquals(false, prefixes.hasNext());

        nsCtx.addPrefix(XMLConstants.DEFAULT_NS_PREFIX, "test");
        assertEquals("test", nsCtx.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX));
    }
}
