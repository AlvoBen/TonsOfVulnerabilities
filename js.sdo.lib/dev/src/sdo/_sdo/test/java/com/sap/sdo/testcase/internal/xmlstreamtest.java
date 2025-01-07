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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.util.Base64Util;
import com.sap.sdo.impl.xml.XmlParseException;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class XmlStreamTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XmlStreamTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String XS = "http://www.w3.org/2001/XMLSchema";

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXmlStreamWriter() throws Exception {
        SapXmlHelper sapXmlHelper = ((SapXmlHelper)_helperContext.getXMLHelper());
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(System.out);
        sapXmlHelper.save("test", "com.sap", "test", XS, "string", writer, null);
    }

    @Test
    public void testQnameIssue() throws Exception {
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();

        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(System.out);
        writer.writeStartDocument();
        writer.writeCharacters("\n");
        xmlHelper.save("http://qname-url#qname-localname", "urn:someNS", "wrapperElement", XS, "QName", writer, null);
        writer.writeEndDocument();
        writer.flush();
    }

    @Test
    public void testBase64BinaryIssue() throws Exception {
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(bo);
        writer.writeStartDocument();
        writer.writeCharacters("\n");
        byte[] bs = new byte[]{1, 2, 3, 4};
        xmlHelper.save(bs, "urn:someNS", "wrapperElement", XS, "base64Binary", writer, null);
        writer.writeEndDocument();
        writer.flush();

        System.out.println(bo.toString());

        ByteArrayInputStream io = new ByteArrayInputStream(bo.toByteArray());
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(io);
        while (reader.next() != XMLStreamReader.START_ELEMENT);
        Object obj = xmlHelper.load(reader, XS, "base64Binary", null);

        assertEquals(byte[].class, obj.getClass());
        byte[] out = (byte[])obj;
        assertEquals(bs.length, out.length);
        for (int i=0; i<out.length; ++i) {
            assertEquals(bs[i], out[i]);
        }
    }

    @Test
    public void testHexBinaryIssue() throws Exception {
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(bo);
        writer.writeStartDocument();
        writer.writeCharacters("\n");
        byte[] bs = new byte[]{1, 2, 3, 4};
        xmlHelper.save(bs, "urn:someNS", "wrapperElement", XS, "hexBinary", writer, null);
        writer.writeEndDocument();
        writer.flush();

        System.out.println(bo.toString());

        ByteArrayInputStream io = new ByteArrayInputStream(bo.toByteArray());
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(io);
        while (reader.next() != XMLStreamReader.START_ELEMENT);
        Object obj = xmlHelper.load(reader, XS, "hexBinary", null);

        assertEquals(byte[].class, obj.getClass());
        byte[] out = (byte[])obj;
        assertEquals(bs.length, out.length);
        for (int i=0; i<out.length; ++i) {
            assertEquals(bs[i], out[i]);
        }
    }

    @Test
    public void testLocalNamespaces() throws Exception {
        String xml = "<element xmlns:ns1=\"ns1_url\"></element>";

        XMLStreamReader reader =
            XMLInputFactory.newInstance().createXMLStreamReader(
                new ByteArrayInputStream(xml.getBytes("UTF-8")));

        while (reader.next() != XMLStreamReader.START_ELEMENT) {}

        assertEquals(1, reader.getNamespaceCount());
        assertEquals("ns1", reader.getNamespacePrefix(0));
        assertEquals("ns1_url", reader.getNamespaceURI(0));
        assertEquals("ns1_url", reader.getNamespaceURI("ns1"));

//        while (reader.next() != XMLStreamReader.END_ELEMENT) {}
//
//        assertEquals(3, reader.getNamespaceCount());
//        assertEquals("xsi", reader.getNamespacePrefix(0));
//        assertEquals("ns2", reader.getNamespacePrefix(1));
//        assertEquals("ns1", reader.getNamespacePrefix(2));
//
//        assertEquals("http://www.w3.org/2001/XMLSchema-instance", reader.getNamespaceURI(0));
//        assertEquals("ns2", reader.getNamespaceURI(1));
//        assertEquals("ns1", reader.getNamespaceURI(2));
//
//        assertEquals("http://www.w3.org/2001/XMLSchema-instance", reader.getNamespaceURI("xsi"));
//        assertEquals("ns2", reader.getNamespaceURI("ns2"));
//        assertEquals("ns1", reader.getNamespaceURI("ns1"));
    }

    @Test
    public void testBase64Coding() throws Exception {
        String str = "Hätten Hüte ein ß im Namen, wären sie möglicherweise keine Hüte mehr,\r\n"
                   + "sondern Hüße.\r\n";
        String base64 = "SMOkdHRlbiBIw7x0ZSBlaW4gw58gaW0gTmFtZW4sIHfDpHJlbiBzaWUgbcO2Z2xpY2hlcndlaXNlIGtlaW5lIEjDvHRlIG1laHIsDQpzb25kZXJuIEjDvMOfZS4NCg==";
        byte[] decodeBase64 = Base64Util.decodeBase64(base64);
        assertEquals(86, str.length());
        assertEquals(str, new String(decodeBase64, "UTF-8"));

        assertNull(Base64Util.encodeBase64(null));

        String encodeBase64 = Base64Util.encodeBase64(str.getBytes("UTF-8"));
        assertEquals(base64, encodeBase64);

        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte)i;
        }

        byte[] bytes2 = Base64Util.decodeBase64(Base64Util.encodeBase64(bytes));
        assertEquals(bytes.length, bytes2.length);
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = bytes[i];
            byte b2 = bytes2[i];
            assertEquals(b1, b2);
        }

        byte[] singleByte = new byte[]{42};
        String singleByteEncoded = Base64Util.encodeBase64(singleByte);
        assertTrue(singleByteEncoded.endsWith("=="));
        assertEquals(singleByte.length, Base64Util.decodeBase64(singleByteEncoded).length);
        assertEquals(singleByte[0], Base64Util.decodeBase64(singleByteEncoded)[0]);

        byte[] twoBytes = new byte[]{42, 42};
        String twoBytesEncoded = Base64Util.encodeBase64(twoBytes);
        assertTrue(twoBytesEncoded.endsWith("="));
        assertEquals(twoBytes.length, Base64Util.decodeBase64(twoBytesEncoded).length);
        assertEquals(twoBytes[0], Base64Util.decodeBase64(twoBytesEncoded)[0]);
        assertEquals(twoBytes[1], Base64Util.decodeBase64(twoBytesEncoded)[1]);

        byte[] threeBytes = new byte[]{42, 42, 42};
        String threeBytesEncoded = Base64Util.encodeBase64(threeBytes);
        assertEquals(threeBytes.length, Base64Util.decodeBase64(threeBytesEncoded).length);
        assertEquals(threeBytes[0], Base64Util.decodeBase64(threeBytesEncoded)[0]);
        assertEquals(threeBytes[1], Base64Util.decodeBase64(threeBytesEncoded)[1]);
        assertEquals(threeBytes[2], Base64Util.decodeBase64(threeBytesEncoded)[2]);
    }

    @Test
    public void testBase64BinaryRoundtrip() throws Exception {
        final String schemaFileName = PACKAGE + "Base64Binary.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        assertNotNull(types);
        assertEquals(1, types.size());

        // System.out.println(_helperContext.getXSDHelper().generate(types));

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

        String graphXmlCS = _helperContext.getXMLHelper().save(graph, null, "graph");
        System.out.println(graphXmlCS);

        XMLDocument doc = _helperContext.getXMLHelper().load(graphXmlCS);
        DataObject graph2 = doc.getRootObject();
        List<Property> properties = graph2.getInstanceProperties();
        assertEquals(4, properties.size());
        Property rootProp = properties.get(3);
        assertEquals("dataObject", rootProp.getName());
        assertEquals(true, rootProp.isMany());

        DataObject rootObject = ((DataGraph)doc.getRootObject()).getRootObject();
        assertEquals("616263", rootObject.getString("base64attribute"));
        assertEquals("313233", rootObject.getString("base64element"));
        assertEquals("313233616263", rootObject.getString("element/value"));

        assertEquals(
            graphXmlCS,
            _helperContext.getXMLHelper().save(doc.getRootObject(), null, "graph"));

        assertEquals(true, rootObject.getContainmentProperty().isMany());

        assertEquals(true, rootObject.getChangeSummary().getOldContainmentProperty(rootObject).isMany());

        ((DataGraph)doc.getRootObject()).getChangeSummary().undoChanges();
        ((DataGraph)doc.getRootObject()).getChangeSummary().endLogging();

        List<Property> properties2 = graph2.getInstanceProperties();
        assertEquals(4, properties2.size());
        Property rootProp2 = properties.get(3);
        assertEquals("dataObject", rootProp2.getName());
        assertEquals(true, rootProp2.isMany());

        assertEquals(true, rootObject.getContainmentProperty().isMany());

        assertEquals(
            graphXml,
            _helperContext.getXMLHelper().save(doc.getRootObject(), null, "graph"));
    }

    @Test
    public void testEmptyBase64Binary() {
        assertNull(Base64Util.encodeBase64(null));
        assertNull(Base64Util.encodeBase64(new byte[0]));
        byte[] bytes = Base64Util.decodeBase64(null);
        assertNotNull(bytes);
        assertTrue(bytes instanceof byte[]);
        assertEquals(0, bytes.length);
        bytes = Base64Util.decodeBase64("  ");
        assertNotNull(bytes);
        assertTrue(bytes instanceof byte[]);
        assertEquals(0, bytes.length);
    }

    @Test
    public void testMixedStreaming() throws Exception {
        final String schemaFileName = PACKAGE + "letterExample.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        assertNotNull(types);
        assertEquals(1, types.size());

        DataObject data = _helperContext.getDataFactory().create("letter.xsd", "FormLetter");
        Sequence seq = data.getSequence();

        seq.add("date", "August 1, 2003");
        seq.addText("\nMutual of Omaha\n");
        seq.addText("Wild Kingdom, USA\n");
        seq.addText("Dear ");
        seq.add("firstName", "Casy");
        seq.add("lastName", "Crocodile");
        seq.addText("\nPlease buy more shark repellent.\n");
        seq.addText("Your premium is past due.\n");

        assertEquals(8, seq.size());

        String xml = _helperContext.getXMLHelper().save(data, null, "letter");
        System.out.println(xml);

        DataObject rootObject = _helperContext.getXMLHelper().load(xml).getRootObject();
        Sequence sequence = rootObject.getSequence();
        assertNotNull(sequence);
        for (int i=0; i<sequence.size(); ++i) {
            System.out.println(sequence.getProperty(i) + ": " + sequence.getValue(i));
        }
        //assertEquals(8, sequence.size());
        assertEquals(xml,
            _helperContext.getXMLHelper().save(
                rootObject,
                null,
                "letter"));
    }

    @Test
    public void testWsdasSchema() throws Exception {
        final String schemaFileName = PACKAGE + "wsdas.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        assertNotNull(types);
        assertEquals(19, types.size());
    }

    @Test
    public void testPrimitiveTypeSerialization() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helperContext.getXMLHelper()).save(new Date(0), null, "date", URINamePair.SCHEMA_URI, "dateTime", writer, null);

        assertEquals(
            "<date>1970-01-01T00:00:00.000Z</date>\n",
            out.toString("UTF-8"));

        out = new ByteArrayOutputStream();
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helperContext.getXMLHelper()).save(
            "SMOkdHRlbiBIw7x0ZSBlaW4gw58gaW0gTmFtZW4sIHfDpHJlbiBzaWUgbcO2Z2xpY2hlcndlaXNlIGtlaW5lIEjDvHRlIG1laHIsDQpzb25kZXJuIEjDvMOfZS4NCg==",
            null, "base64Binary", URINamePair.SCHEMA_URI, "base64Binary", writer, null);

        assertEquals(
            "<base64Binary>SMOkdHRlbiBIw7x0ZSBlaW4gw58gaW0gTmFtZW4sIHfDpHJlbiBzaWUgbcO2Z2xpY2hlcndlaXNlIGtlaW5lIEjDvHRlIG1laHIsDQpzb25kZXJuIEjDvMOfZS4NCg==</base64Binary>\n",
            out.toString("UTF-8"));

        out = new ByteArrayOutputStream();
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helperContext.getXMLHelper()).save(
            new byte[]{'a','b','c'},
            null, "base64Binary", URINamePair.SCHEMA_URI, "base64Binary", writer, null);

        assertEquals(
            "<base64Binary>YWJj</base64Binary>\n",
            out.toString("UTF-8"));

        out = new ByteArrayOutputStream();
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helperContext.getXMLHelper()).save(
            new URINamePair("bla", "blub").toStandardSdoFormat(),
            null, "qname", URINamePair.SCHEMA_URI, URINamePair.SCHEMA_Q_NAME.getName(), writer, null);

        assertEquals(
            "<qname xmlns:ns1=\"bla\">ns1:blub</qname>\n",
            out.toString("UTF-8"));
    }

    @Test
    public void testPrimitiveTypeNullSerialization() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helperContext.getXMLHelper()).save(null, "elementUri", "elementName", URINamePair.SCHEMA_URI, "dateTime", writer, null);

        assertLineEquality(
            "<ns1:elementName" +
            " xmlns:ns1=\"elementUri\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xsi:nil=\"true\"" +
            "></ns1:elementName>\n",
            out.toString("UTF-8"));
    }

    @Test
    public void testComplexTypeNullSerialization() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helperContext.getXMLHelper()).save(null, "elementUri", "elementName", URINamePair.SCHEMA_URI, "anyType", writer, null);

        assertLineEquality(
            "<ns1:elementName" +
            " xmlns:ns1=\"elementUri\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xsi:nil=\"true\"" +
            "></ns1:elementName>\n",
            out.toString("UTF-8"));
    }

    @Test
    public void testNullableGlobalProp() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
        writer.setPrefix("xsi", URINamePair.XSI_URI);

        System.out.println(writer.getPrefix(URINamePair.XSI_URI));

        DataObject nullableObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        nullableObj.set(PropertyType.NAME, "nullable");
        nullableObj.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "Int"));
        nullableObj.set(PropertyType.NULLABLE, true);
        nullableObj.set(PropertyType.getXmlElementProperty(), true);
        Property nullableProp =
            _helperContext.getTypeHelper().defineOpenContentProperty(
                "com.sap.sdo.testcase.typefac", nullableObj);

        DataObject primitiveObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        primitiveObj.set(PropertyType.NAME, "primitive");
        primitiveObj.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "Int"));
        primitiveObj.set(PropertyType.NULLABLE, false);
        primitiveObj.set(PropertyType.getXmlElementProperty(), true);
        Property primitiveProp =
            _helperContext.getTypeHelper().defineOpenContentProperty(
                "com.sap.sdo.testcase.typefac", primitiveObj);

        ((SapXmlHelper)_helperContext.getXMLHelper()).save(null, "com.sap.sdo.testcase.typefac", nullableProp.getName(), null, null, writer, null);
        assertLineEquality(
            "<ns1:nullable" +
            " xmlns:ns1=\"com.sap.sdo.testcase.typefac\"" +
            " xsi:nil=\"true\"" +
            "></ns1:nullable>\n",
            out.toString("UTF-8"));


        try {
            ((SapXmlHelper)_helperContext.getXMLHelper()).save(null, "com.sap.sdo.testcase.typefac", primitiveProp.getName(), null, null, writer, null);
            fail("expected XmlParseException");
        } catch (XmlParseException ex) {
            assertEquals("element com.sap.sdo.testcase.typefac#primitive is not nillable", ex.getMessage());
        }
    }
}
