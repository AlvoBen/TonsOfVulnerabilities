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
package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import junit.framework.Assert;

import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;

import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SdoTestUtil extends Assert {
    private static final String XML_VERSION = "<?xml version=\"1.0\"?>";
    private static final String XML_VERSION_ENCODING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    protected static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    {
        XML_INPUT_FACTORY.setProperty("javax.xml.stream.supportDTD", false);
    }

    public static void checkContentHandler(XMLDocument pDoc, Map pOptions) throws Exception {
        if (pDoc != null && pDoc.getRootObject() != null) {
            GenericDataObject gdo = ((DataObjectDecorator)pDoc.getRootObject()).getInstance();
            SapXmlHelper helper = (SapXmlHelper)gdo.getHelperContext().getXMLHelper();

            SDOContentHandler contentHandler = helper.createContentHandler(pOptions);
            assertNotNull(contentHandler);

            StringWriter writer = new StringWriter();
            helper.save(pDoc, writer, pOptions);
            String original = writer.toString();

            SAXResult result = new SAXResult(contentHandler);
            helper.save(pDoc, result, pOptions);

            XMLDocument resultDoc = contentHandler.getDocument();
            assertNotNull(resultDoc);
            resultDoc.setXMLDeclaration(pDoc.isXMLDeclaration());
            resultDoc.setXMLVersion(pDoc.getXMLVersion());
            resultDoc.setEncoding(pDoc.getEncoding());

            writer = new StringWriter();
            helper.save(resultDoc, writer, pOptions);

            assertEquals(original, writer.toString());
        }

    }

    public static void checkXmlReader(XMLDocument pDoc, Map pOptions) throws IOException {
        if (pDoc != null && pDoc.getRootObject() != null) {
            GenericDataObject gdo = ((DataObjectDecorator)pDoc.getRootObject()).getInstance();
            SapXmlHelper helper = (SapXmlHelper)gdo.getHelperContext().getXMLHelper();

            Map<String, Object> options = new HashMap<String, Object>(2);
            options.put("http://xml.org/sax/features/namespace-prefixes", true);
            options.put(SapXmlHelper.OPTION_KEY_INDENT, null);

            XMLReader reader = helper.createXMLReader(pDoc, options);
            assertNotNull(reader);

            StringWriter writer = new StringWriter();

            helper.save(pDoc, writer, null);
            String original = writer.toString();
            if (original.startsWith(XML_VERSION)) {
                original = XML_VERSION_ENCODING + original.substring(XML_VERSION.length());
            } else if (original.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    || original.startsWith("<?xml version=\"1.0\" encoding=\"ASCII\"?>")) {
                original = XML_VERSION_ENCODING + original.substring(XML_VERSION_ENCODING.length());
            }

            writer = new StringWriter();
            SapXmlDocument doc = helper.load(new SAXSource(reader, new InputSource()), null, pOptions);
            doc.setXMLDeclaration(pDoc.isXMLDeclaration());
            helper.save(
                doc,
                writer,
                null);

            assertEquals(original, writer.toString());
        }
    }

    public static void compareReaders(XMLDocument pDoc) throws IOException, XMLStreamException {
        if (pDoc != null && pDoc.getRootObject() != null) {
            GenericDataObject gdo = ((DataObjectDecorator)pDoc.getRootObject()).getInstance();
            SapXmlHelper helper = (SapXmlHelper)gdo.getHelperContext().getXMLHelper();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Map<String, Object> options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_INDENT,null);
            helper.save(pDoc, out, options);

            XMLStreamReader xmlReader =
                XML_INPUT_FACTORY.createXMLStreamReader(
                    new ByteArrayInputStream(out.toByteArray()));

            if ("com.sap.engine.services.webservices.espbase.client.bindings.impl.XMLStreamReaderImpl"
                    .equals(xmlReader.getClass().getName())) {
                return;
            }
            if (!pDoc.isXMLDeclaration() && xmlReader.getVersion() != null) {
                pDoc.setXMLVersion(xmlReader.getVersion());
            }

            XMLStreamReader sdoReader = helper.createXMLStreamReader(pDoc, null);

            while (compareReaders(xmlReader, sdoReader));
        }
    }

    private static boolean compareReaders(XMLStreamReader xmlReader, XMLStreamReader sdoReader)
        throws XMLStreamException {

        assertEquals(message(xmlReader, sdoReader), xmlReader.getEventType(), sdoReader.getEventType());

        if (xmlReader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
            if (!("UTF-8".equals(xmlReader.getEncoding()) && sdoReader.getEncoding() == null)) {
                assertEquals(message(xmlReader, sdoReader), xmlReader.getEncoding(), sdoReader.getEncoding());
            }
            assertEquals(message(xmlReader, sdoReader), xmlReader.getVersion(), sdoReader.getVersion());
        }

        assertEquals(message(xmlReader, sdoReader), xmlReader.getCharacterEncodingScheme(), sdoReader.getCharacterEncodingScheme());
        //assertEquals(message(xmlReader, sdoReader), xmlReader.getLocation(), sdoReader.getLocation());

        assertEquals(message(xmlReader, sdoReader), xmlReader.isCharacters(), sdoReader.isCharacters());
        assertEquals(message(xmlReader, sdoReader), xmlReader.isEndElement(), sdoReader.isEndElement());
        assertEquals(message(xmlReader, sdoReader), xmlReader.isStandalone(), sdoReader.isStandalone());
        assertEquals(message(xmlReader, sdoReader), xmlReader.isStartElement(), sdoReader.isStartElement());
        assertEquals(message(xmlReader, sdoReader), xmlReader.isWhiteSpace(), sdoReader.isWhiteSpace());

        assertEquals(message(xmlReader, sdoReader), xmlReader.standaloneSet(), sdoReader.standaloneSet());
        //assertEquals(message(xmlReader, sdoReader), xmlReader.getPITarget(), sdoReader.getPITarget());
        //assertEquals(message(xmlReader, sdoReader), xmlReader.getPIData(), sdoReader.getPIData());

        assertEquals(message(xmlReader, sdoReader), xmlReader.hasName(), sdoReader.hasName());
        if (xmlReader.hasName()) {
            assertEquals(message(xmlReader, sdoReader), xmlReader.getLocalName(), sdoReader.getLocalName());
            String namespaceURI = xmlReader.getNamespaceURI();
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            assertEquals(message(xmlReader, sdoReader), namespaceURI, sdoReader.getNamespaceURI());
            assertEquals(message(xmlReader, sdoReader), xmlReader.getName(), sdoReader.getName());
        }
        assertEquals(message(xmlReader, sdoReader), xmlReader.getPrefix(), sdoReader.getPrefix());

        //attributes
        if (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
            compareAttributes(xmlReader, sdoReader);
        }

        //namespaces
        if (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT
                || xmlReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
            compareNamespaces(xmlReader, sdoReader);
        }

        //namespace context
        compareNamespaceContexts(xmlReader, sdoReader);

        assertEquals(message(xmlReader, sdoReader), xmlReader.hasNext(), sdoReader.hasNext());
        assertEquals(message(xmlReader, sdoReader), xmlReader.hasText(), sdoReader.hasText());

        if (xmlReader.hasText()) {
            StringBuilder text = new StringBuilder();
            while (xmlReader.hasText()) {
                text.append(xmlReader.getText());
                xmlReader.next();
            }
            StringBuilder sdoText = new StringBuilder();
            while (sdoReader.hasText()) {
                String textSnipped = sdoReader.getText();
                char[] charArray = new char[textSnipped.length()];
                sdoReader.getTextCharacters(sdoReader.getTextStart(), charArray, 0, sdoReader.getTextLength());
                assertEquals(textSnipped, String.valueOf(charArray));

                sdoText.append(textSnipped);
                sdoReader.next();
            }

            assertEquals(message(xmlReader, sdoReader), text.toString(), sdoText.toString());
            //assertEquals(message(xmlReader, sdoReader), xmlReader.getTextCharacters(), sdoReader.getTextCharacters());
        } else if (xmlReader.hasNext()) {
            xmlReader.next();
//            if (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT
//                    && sdoReader.getEventType() != XMLStreamConstants.START_DOCUMENT
//                    && (URINamePair.SCHEMA_SCHEMA.equalsUriName(
//                        xmlReader.getNamespaceURI(), xmlReader.getLocalName()))) {
//                int stack = 1;
//                while (stack > 0) {
//                    int event = xmlReader.next();
//                    if (event == XMLStreamConstants.START_ELEMENT) {
//                        ++stack;
//                    } else if (event == XMLStreamConstants.END_ELEMENT) {
//                        --stack;
//                    }
//
//                }
//                if (xmlReader.hasNext()) {
//                    xmlReader.next();
//                }
//                while (xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
//                    xmlReader.next();
//                }
//            }
            if (sdoReader.hasNext()) {
                sdoReader.next();
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;

//        if (sdoReader.hasNext()) {
//            int nextXmlEvent = xmlReader.getEventType();
//            int nextSdoEvent = sdoReader.next();
//            assertEquals(message(xmlReader, sdoReader), nextXmlEvent, nextSdoEvent);
//            // compareReaders(xmlReader, sdoReader);
//            return true;
//        }
//        return false;
    }

    private static void compareNamespaceContexts(XMLStreamReader xmlReader, XMLStreamReader sdoReader) {
        NamespaceContext xmlNsCtx = xmlReader.getNamespaceContext();
        NamespaceContext sdoNsCtx = sdoReader.getNamespaceContext();
        assertNotNull(xmlNsCtx);
        assertNotNull(sdoNsCtx);
        comparePrefix(message(xmlReader, sdoReader), XMLConstants.DEFAULT_NS_PREFIX, xmlNsCtx, sdoNsCtx);
        comparePrefix(message(xmlReader, sdoReader), XMLConstants.XML_NS_PREFIX, xmlNsCtx, sdoNsCtx);
        comparePrefix(message(xmlReader, sdoReader), XMLConstants.XMLNS_ATTRIBUTE, xmlNsCtx, sdoNsCtx);
        comparePrefix(message(xmlReader, sdoReader), "sdo", xmlNsCtx, sdoNsCtx);
        comparePrefix(message(xmlReader, sdoReader), "ns1", xmlNsCtx, sdoNsCtx);
        try {
            xmlNsCtx.getPrefix(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            sdoNsCtx.getPrefix(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    private static void comparePrefix(String message, String prefix, NamespaceContext xmlNsCtx,
        NamespaceContext sdoNsCtx) {
                String uri = xmlNsCtx.getNamespaceURI(prefix);
                assertEquals(message, (uri != null ? uri : ""), sdoNsCtx.getNamespaceURI(prefix));
                if (uri != null && uri.length() > 0) {
                    assertEquals(message, prefix, xmlNsCtx.getPrefix(uri));
                    Iterator prefixes = xmlNsCtx.getPrefixes(uri);
                    assertNotNull(prefixes);
                    assertEquals(message, true, prefixes.hasNext());
                    assertEquals(message, prefix, prefixes.next());
                    assertEquals(message, false, prefixes.hasNext());

                    assertEquals(message, prefix, sdoNsCtx.getPrefix(uri));
                    prefixes = sdoNsCtx.getPrefixes(uri);
                    assertNotNull(prefixes);
                    assertEquals(message, true, prefixes.hasNext());
                    assertEquals(message, prefix, prefixes.next());
                    assertEquals(message, false, prefixes.hasNext());
        }
    }

    private static void compareNamespaces(XMLStreamReader xmlReader, XMLStreamReader sdoReader) {
        int namespaceCount = xmlReader.getNamespaceCount();
        assertEquals(message(xmlReader, sdoReader), namespaceCount, sdoReader.getNamespaceCount());
        Map<String,String> nsMap = new HashMap<String,String>(namespaceCount);
        for (int i=0; i<namespaceCount; ++i) {
            String prefix = xmlReader.getNamespacePrefix(i);
            nsMap.put(prefix, xmlReader.getNamespaceURI(i));
            assertEquals(message(xmlReader, sdoReader), nsMap.get(prefix), xmlReader.getNamespaceURI(prefix));
        }
        for (int i=0; i<namespaceCount; ++i) {
            String prefix = sdoReader.getNamespacePrefix(i);
            assertEquals(message(xmlReader, sdoReader), nsMap.get(prefix), sdoReader.getNamespaceURI(i));
            assertEquals(message(xmlReader, sdoReader), nsMap.get(prefix), sdoReader.getNamespaceURI(prefix));
        }
    }

    private static void compareAttributes(XMLStreamReader xmlReader, XMLStreamReader sdoReader) {
        int attributeCount = xmlReader.getAttributeCount();
        assertEquals(message(xmlReader, sdoReader), attributeCount, sdoReader.getAttributeCount());
        for (int i=0; i<attributeCount; ++i) {
            String localName = xmlReader.getAttributeLocalName(i);
            String namespace = xmlReader.getAttributeNamespace(i);

            int sdoIndex = -1;
            for (int j=0; j<attributeCount; ++j) {
                String attributeNamespace = sdoReader.getAttributeNamespace(j);
                if (localName.equals(sdoReader.getAttributeLocalName(j))
                        && (attributeNamespace == namespace
                                || (attributeNamespace != null && attributeNamespace.equals(namespace))
                                || (namespace == null && attributeNamespace.length() == 0))) {
                    sdoIndex = j;
                    break;
                }
            }
            if (sdoIndex == -1) {
                fail(
                    message(xmlReader, sdoReader) +
                    " attribute not found: " + namespace + '#' + localName);
            }
            assertEquals(
                message(xmlReader, sdoReader),
                xmlReader.getAttributeValue(namespace, localName),
                sdoReader.getAttributeValue(namespace, localName));

            assertEquals(message(xmlReader, sdoReader), xmlReader.getAttributeName(i), sdoReader.getAttributeName(sdoIndex));
            String attributePrefix = xmlReader.getAttributePrefix(i);
            if (attributePrefix != null && attributePrefix.length() == 0) {
                attributePrefix = null;
            }
            assertEquals(message(xmlReader, sdoReader), attributePrefix, sdoReader.getAttributePrefix(sdoIndex));

            //assertEquals(message(xmlReader, sdoReader), xmlReader.getAttributeType(i), sdoReader.getAttributeType(i));
            assertEquals(message(xmlReader, sdoReader), xmlReader.getAttributeValue(i), sdoReader.getAttributeValue(sdoIndex));
            assertEquals(message(xmlReader, sdoReader), xmlReader.isAttributeSpecified(i), sdoReader.isAttributeSpecified(sdoIndex));
        }
    }

    private static String message(XMLStreamReader xmlReader, XMLStreamReader sdoReader) {
        String namespaceURI;
        String localName;
        try {
            namespaceURI = xmlReader.getNamespaceURI();
            localName = xmlReader.getLocalName();
        } catch (IllegalStateException ex) {
            namespaceURI = "";
            localName = "<start_document>";
        }
        return namespaceURI + '#' + localName +
            " - " + sdoReader.getNamespaceURI() + '#' + sdoReader.getLocalName() + '\n' +
            sdoReader.getLocation();
    }

}
