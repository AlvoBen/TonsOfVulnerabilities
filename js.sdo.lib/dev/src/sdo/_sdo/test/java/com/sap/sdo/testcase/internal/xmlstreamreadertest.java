package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;

public class XMLStreamReaderTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XMLStreamReaderTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testElement() throws IOException, XMLStreamException {
        final String schemaFileName = PACKAGE + "ElementUnqualAttributeUnqual.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        final String xmlFileName = PACKAGE + "WsElementUnqualAttributeUnqual.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(xmlUrl.openStream());
        while(reader.next()!= XMLStreamConstants.START_ELEMENT) {}
        reader.nextTag();
        assertEquals("aElement", reader.getLocalName());

        SapXmlHelper sapXmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        DataObject dataObject = (DataObject)sapXmlHelper.load(reader, null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());

        String xml =_helperContext.getXMLHelper().save(dataObject, "com.sap.test.euau", "aElement");
        System.out.println(xml);

        assertSame(_helperContext.getTypeHelper().getType("com.sap.test.euau", "aType"), dataObject.getType());
    }

    @Test
    public void testDataObjectType() throws IOException, XMLStreamException {
        final String schemaFileName = PACKAGE + "ElementUnqualAttributeUnqual.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        final String xmlFileName = PACKAGE + "WsElementUnqualAttributeUnqual.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(xmlUrl.openStream());
        while(reader.next()!= XMLStreamConstants.START_ELEMENT) {}
        reader.nextTag();
        assertEquals("aElement", reader.getLocalName());

        SapXmlHelper sapXmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        DataObject dataObject = (DataObject)sapXmlHelper.load(reader, "com.sap.test.euau", "aType", null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());

        String xml =_helperContext.getXMLHelper().save(dataObject, "com.sap.test.euau", "aElement");
        System.out.println(xml);

        assertSame(_helperContext.getTypeHelper().getType("com.sap.test.euau", "aType"), dataObject.getType());
    }

    @Test
    public void testSimpleType() throws IOException, XMLStreamException {
        final String schemaFileName = PACKAGE + "WsSimple.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        final String xmlFileName = PACKAGE + "WsSimple.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(xmlUrl.openStream());
        while(reader.next()!= XMLStreamConstants.START_ELEMENT) {}
        reader.nextTag();
        assertEquals("intSimpleContent", reader.getLocalName());

        SapXmlHelper sapXmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        Object object = sapXmlHelper.load(reader, "http://www.w3.org/2001/XMLSchema", "int", null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
        assertEquals("intSimpleContent", reader.getLocalName());
        assertEquals(Integer.valueOf(555), object);

        reader.nextTag();
        assertEquals("qnameSimpleContent", reader.getLocalName());

        Object object2 = sapXmlHelper.load(reader, "http://www.w3.org/2001/XMLSchema", "QName", null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
        assertEquals("qnameSimpleContent", reader.getLocalName());
        assertEquals("com.sap.test.ws#testQname", object2);

        reader.nextTag();
        assertEquals("nillableContent", reader.getLocalName());

        Object object3 = sapXmlHelper.load(reader, "http://www.w3.org/2001/XMLSchema", "int", null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
        assertEquals("nillableContent", reader.getLocalName());
        assertNull(object3);
    }

    @Test
    public void testSimpleElement() throws IOException, XMLStreamException {
        final String schemaFileName = PACKAGE + "WsSimple.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        final String xmlFileName = PACKAGE + "WsSimple.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(xmlUrl.openStream());
        while(reader.next()!= XMLStreamConstants.START_ELEMENT) {}
        reader.nextTag();
        assertEquals("intSimpleContent", reader.getLocalName());

        SapXmlHelper sapXmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        Object object = sapXmlHelper.load(reader, null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
        assertEquals("intSimpleContent", reader.getLocalName());
        assertEquals(Integer.valueOf(555), object);

        reader.nextTag();
        assertEquals("qnameSimpleContent", reader.getLocalName());

        Object object2 = sapXmlHelper.load(reader, null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
        assertEquals("qnameSimpleContent", reader.getLocalName());
        assertEquals("com.sap.test.ws#testQname", object2);

        reader.nextTag();
        assertEquals("nillableContent", reader.getLocalName());

        Object object3 = sapXmlHelper.load(reader, null);

        assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
        assertEquals("nillableContent", reader.getLocalName());
        assertNull(object3);
    }
}
