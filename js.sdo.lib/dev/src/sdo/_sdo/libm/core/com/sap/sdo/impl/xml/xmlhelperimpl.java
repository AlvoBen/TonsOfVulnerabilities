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
package com.sap.sdo.impl.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.XMLReader;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.api.helper.util.SDOResult;
import com.sap.sdo.api.helper.util.SDOSource;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.XPathHelper;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.xml.sax.ContentHandlerStreamWriter;
import com.sap.sdo.impl.xml.sax.SDOResultImpl;
import com.sap.sdo.impl.xml.sax.SDOSourceImpl;
import com.sap.sdo.impl.xml.sax.SdoContentHandlerImpl;
import com.sap.sdo.impl.xml.sax.XmlReaderImpl;
import com.sap.sdo.impl.xml.stream.SdoStreamReader;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * @author D042774
 *
 */
public class XMLHelperImpl implements SapXmlHelper {
    private final static Logger LOGGER = Logger.getLogger(XPathHelper.class.getName());

    public static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    {
        XML_INPUT_FACTORY.setProperty("javax.xml.stream.supportDTD", false);
    }
    public static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    private final SapHelperContext _helperContext;

    private XMLHelperImpl(SapHelperContext pHelperContext) {
        _helperContext = pHelperContext;
    }

    public static XMLHelper getInstance(SapHelperContext pHelperContext) {
        // to avoid illegal instances
        XMLHelper xmlHelper = pHelperContext.getXMLHelper();
        if (xmlHelper != null) {
            return xmlHelper;
        }
        return new XMLHelperImpl(pHelperContext);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.lang.String)
     */
    public SapXmlDocument load(String pInputString) {
        try {
            XmlStaxReader staxReader = new XmlStaxReader(_helperContext);
            return staxReader.load(pInputString);
        } catch (IOException ex) {
            LOGGER.throwing(getClass().getName(), "load", ex);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.InputStream)
     */
    public SapXmlDocument load(InputStream pInputStream) throws IOException {
        XmlStaxReader staxReader = new XmlStaxReader(_helperContext);
        try {
            return staxReader.load(pInputStream, null, null);
        } finally {
            pInputStream.close();
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.InputStream, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(InputStream pInputStream, String pLocationURI, Object pOptions)
    throws IOException {
        XmlStaxReader staxReader = new XmlStaxReader(_helperContext);
        try {
            return staxReader.load(pInputStream, pLocationURI, getOptions(pOptions));
        } finally {
            pInputStream.close();
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.Reader, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(Reader pInputReader, String pLocationURI, Object pOptions)
    throws IOException {
        XmlStaxReader staxReader = new XmlStaxReader(_helperContext);
        try {
            return staxReader.load(pInputReader, pLocationURI, getOptions(pOptions));
        } finally {
            pInputReader.close();
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(javax.xml.transform.Source, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(Source pInputSource, String pLocationURI, Object pOptions) throws IOException {
        XmlStaxReader staxReader = new XmlStaxReader(_helperContext);
        return staxReader.load(pInputSource, pLocationURI, getOptions(pOptions));
    }

    private Map getOptions(Object pOptions) {
        if (pOptions instanceof Map) {
            return (Map)pOptions;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(javax.xml.stream.XMLStreamReader, java.lang.Object)
     */
    public Object load(XMLStreamReader pReader, Map pOptions) throws XMLStreamException {
        XmlStaxReader staxReader = new XmlStaxReader(_helperContext);
        return staxReader.loadDocumentPart(pReader, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(javax.xml.stream.XMLStreamReader, java.lang.String, java.lang.String, java.lang.Object)
     */
    public Object load(XMLStreamReader pReader, String pXsdTypeUri, String pXsdTypeName, Map pOptions) throws XMLStreamException {
        XmlStaxReader staxReader = new XmlStaxReader(_helperContext);
        return staxReader.loadDocumentPart(pReader, pXsdTypeUri, pXsdTypeName, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#save(java.lang.Object, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.xml.stream.XMLStreamWriter, java.lang.Object)
     */
    public void save(Object pData, String pElementUri, String pElementName, String pXsdTypeUri, String pXsdTypeName, XMLStreamWriter pWriter, Map pOptions) throws IOException {
        final DataObject data;
        if (pData instanceof DataObject) {
            data = (DataObject)pData;
            XMLDocument xmlDoc = createDocument(data, pElementUri, pElementName);
            xmlDoc.setXMLDeclaration(false);
            save(xmlDoc, pWriter, pOptions);
        } else {
            TypeHelperImpl typeHelper = (TypeHelperImpl)_helperContext.getTypeHelper();
            SdoType<Object> type = null;
            if (pXsdTypeName != null) {
                type = (SdoType<Object>)typeHelper.getTypeByXmlName(pXsdTypeUri, pXsdTypeName);
            }
            if (type == null) {
                Property element = typeHelper.getOpenContentPropertyByXmlName(pElementUri, pElementName, true);
                if (element != null) {
                    type = (SdoType<Object>)element.getType();
                }
            }
            if (type == null) {
                throw new IllegalArgumentException("Can not find type " +
                    pXsdTypeUri + '#' + pXsdTypeName + " for element " +
                    pElementUri + '#' + pElementName);
            }

            XmlStaxWriter staxWriter = new XmlStaxWriter(pWriter, _helperContext);
            try {
                staxWriter.generate(
                    pData,
                    type,
                    (pXsdTypeName != null ? new URINamePair(pXsdTypeUri, pXsdTypeName) : null),
                    new URINamePair(pElementUri, pElementName),
                    pOptions);
            } catch (XMLStreamException ex) { //$JL-EXC$
                throw new XmlParseException(ex);
            }
            try {
                pWriter.flush();
            } catch (XMLStreamException ex1) {
                throw new XmlParseException(ex1);
            }
        }
    }

    private void save(XMLDocument pXmlDocument, XMLStreamWriter pWriter, Object pOptions) throws IOException {
        String uri = pXmlDocument.getRootElementURI();
        if (uri == null) {
            uri = "";
        }
        Property prop =
            ((TypeHelperImpl)_helperContext.getTypeHelper()).getOpenContentPropertyByXmlName(
                uri,
                pXmlDocument.getRootElementName(),
                true);

        if (pXmlDocument.getRootObject() == null
                && (prop == null || !prop.isNullable())) {
            throw new IllegalArgumentException("the DataObject to be rendered must not be null");
        }
        XmlStaxWriter staxWriter = new XmlStaxWriter(pWriter, _helperContext);
        try {
            staxWriter.generate(pXmlDocument, prop, getOptions(pOptions));
        } catch (XMLStreamException ex) { //$JL-EXC$
            throw new XmlParseException(ex);
        }
        try {
            pWriter.flush();
        } catch (XMLStreamException ex1) {
            throw new XmlParseException(ex1);
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.DataObject, java.lang.String, java.lang.String)
     */
    public String save(DataObject pDataObject, String pRootElementURI, String pRootElementName) {
        StringWriter stringWriter = new StringWriter();
        try {
            save(createDocument(pDataObject, pRootElementURI, pRootElementName), stringWriter, null);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return stringWriter.toString();
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, java.io.OutputStream, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, OutputStream pOutputStream,
        Object pOptions) throws IOException {

        XMLStreamWriter writer;
        try {
            // This is not thread save! Bug in jdk 1.6.0_07.
            // writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(pOutputStream, pXmlDocument.getEncoding());

            String encoding = pXmlDocument.getEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            StreamResult streamResult = new StreamResult(new OutputStreamWriter(pOutputStream, encoding));
            writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(streamResult);

            // fallback for SAP implementation of StAX
            if (writer == null) {
                writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(pOutputStream, pXmlDocument.getEncoding());
            }

        } catch (XMLStreamException ex1) {
            throw new XmlParseException(ex1);
        }

        save(pXmlDocument, writer, pOptions);

        try {
            writer.close();
        } catch (XMLStreamException ex1) {
            throw new XmlParseException(ex1);
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, java.io.Writer, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, Writer pOutputWriter,
        Object pOptions) throws IOException {

        XMLStreamWriter writer;
        try {
            // This is not thread save! Bug in jdk 1.6.0_07.
            // writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(pOutputWriter);

            StreamResult streamResult = new StreamResult(pOutputWriter);
            writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(streamResult);

            if (writer == null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                String encoding = pXmlDocument.getEncoding();
                if (encoding != null) {
                    writer =
                        XML_OUTPUT_FACTORY.createXMLStreamWriter(
                            stream,
                            encoding);
                } else {
                    writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(stream);
                }
                save(pXmlDocument, writer, pOptions);
                if (encoding != null) {
                    pOutputWriter.write(stream.toString(encoding));
                } else {
                    pOutputWriter.write(stream.toString());
                }
            } else {
                save(pXmlDocument, writer, pOptions);
            }
        } catch (XMLStreamException ex1) {
            throw new XmlParseException(ex1);
        }
        try {
            writer.close();
        } catch (XMLStreamException ex1) {
            throw new XmlParseException(ex1);
        }
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.DataObject, java.lang.String, java.lang.String, java.io.OutputStream)
     */
    public void save(DataObject dataObject, String rootElementURI, String rootElementName, OutputStream outputStream) throws IOException {
        save(
            createDocument(dataObject, rootElementURI, rootElementName),
            outputStream,
            null);
    }

    DocumentBuilder _documentBuilder;

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, javax.xml.transform.Result, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, Result pOutputResult, Object pOptions) throws IOException {
        if (pOutputResult instanceof StreamResult) {
            StreamResult streamResult = (StreamResult)pOutputResult;
            Writer writer = streamResult.getWriter();
            if (writer != null) {
                // use the StreamResult directly
                try {
                    XMLStreamWriter xmlStreamWriter = XML_OUTPUT_FACTORY.createXMLStreamWriter(pOutputResult);
                    save(pXmlDocument, xmlStreamWriter, pOptions);
                    xmlStreamWriter.close();
                } catch (XMLStreamException e) {
                    throw new XmlParseException(e);
                }
            } else {
                // use the OutputStream-method to respect the encoding
                OutputStream outputStream = streamResult.getOutputStream();
                if (outputStream == null) {
                    outputStream = new ByteArrayOutputStream();
                    streamResult.setOutputStream(outputStream);
                }
                save(pXmlDocument, outputStream, pOptions);
            }
            return;
        } else if (pOutputResult instanceof StAXResult) {
            try {
                XMLStreamWriter writer = ((StAXResult)pOutputResult).getXMLStreamWriter();
                if (writer != null) {
                    save(pXmlDocument, writer, pOptions);
                    writer.close();
                    return;
                }
            } catch (XMLStreamException ex1) {
                throw new XmlParseException(ex1);
            }
        } else if (pOutputResult instanceof DOMResult) {
            DOMResult domResult = (DOMResult)pOutputResult;
            if (domResult.getNode() == null) {
                if (_documentBuilder == null) {
                    try {
                        _documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        throw new XmlParseException(e);
                    }
                }
                domResult.setNode(_documentBuilder.newDocument());
            }
            try {
                XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(pOutputResult);
                if (writer != null) {
                    save(pXmlDocument, new ComposedCharactersStreamWriter(writer), pOptions);
                    writer.close();
                    return;
                }
            } catch (XMLStreamException ex1) {
                //next try
            }
        } else if (pOutputResult instanceof SAXResult) {
            SAXResult saxResult = (SAXResult)pOutputResult;
            try {
                boolean supportQNames = false;
                Map options = getOptions(pOptions);
                if (options != null) {
                    supportQNames = Boolean.TRUE.equals(options.get("http://xml.org/sax/features/namespace-prefixes"));
                }
                XMLStreamWriter writer = new ContentHandlerStreamWriter(saxResult.getHandler(), supportQNames);
                if (writer != null) {
                    save(pXmlDocument, new ComposedCharactersStreamWriter(writer), pOptions);
                    writer.close();
                    return;
                }
            } catch (XMLStreamException ex1) {
                throw new XmlParseException(ex1);
            }
        }

        StAXSource source = new StAXSource(createXMLStreamReader(pXmlDocument, getOptions(pOptions)));
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, pOutputResult);
        } catch (TransformerException ex) {
            throw new XmlParseException(ex);
        }
    }


    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#createDocument(commonj.sdo.DataObject, java.lang.String, java.lang.String)
     */
    public XMLDocument createDocument(DataObject pDataObject,
        String pRootElementURI, String pRootElementName) {

        return new XMLDocumentImpl(pDataObject, pRootElementURI, pRootElementName);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#createXMLStreamReader(commonj.sdo.helper.XMLDocument, java.util.Map)
     */
    public XMLStreamReader createXMLStreamReader(XMLDocument xmlDocument,
        Map<String, Object> options) {

        return new SdoStreamReader(xmlDocument, options, _helperContext);
    }

    public XMLReader createXMLReader(XMLDocument pXmlDocument, Map<String, Object> pOptions) {
        return new XmlReaderImpl(pXmlDocument, _helperContext, pOptions);
    }

    public SDOContentHandler createContentHandler(Object pOptions) {
        return new SdoContentHandlerImpl(_helperContext, getOptions(pOptions));
    }

    @Override
    public SDOSource createSDOSource(XMLDocument pXmlDocument, Object pOptions) {
        XmlReaderImpl xmlReader = new XmlReaderImpl(pXmlDocument, _helperContext, getOptions(pOptions));
        return new SDOSourceImpl(xmlReader);
    }

    @Override
    public SDOResult createSDOResult(Object pOptions) {
        SdoContentHandlerImpl contentHandler = new SdoContentHandlerImpl(_helperContext, getOptions(pOptions));
        return new SDOResultImpl(contentHandler);
    }

}
