package com.sap.sdo.impl.xml.sax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.sap.sdo.impl.xml.stream.SdoNamespaceContext;

public class ContentHandlerStreamWriter implements XMLStreamWriter {

    private final ContentHandler _contentHandler;
    private int _level = -1;
    private boolean _startElement = false;
    private boolean _endElement = false;
    private List<Element> _elements = new ArrayList<Element>();
    private SdoNamespaceContext _rootNamespaceContext = new SdoNamespaceContext();
    private final boolean _supportQNames;

    public ContentHandlerStreamWriter(final ContentHandler pContentHandler, boolean pSupportQNames) {
        _contentHandler = pContentHandler;
        _supportQNames = pSupportQNames;
    }

    public void close() throws XMLStreamException {
    }

    public void flush() throws XMLStreamException {
    }

    public NamespaceContext getNamespaceContext() {
        return getElement().getModifiableNamespaceContext();
    }

    public String getPrefix(String pUri) throws XMLStreamException {
        if (_level < 0) {
            return _rootNamespaceContext.getPrefix(pUri);
        }
        return getElement().getPrefix(pUri);
    }

    public Object getProperty(String pName) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDefaultNamespace(String pUri) throws XMLStreamException {
        setPrefix(XMLConstants.DEFAULT_NS_PREFIX, pUri);
    }

    public void setNamespaceContext(NamespaceContext pContext)
        throws XMLStreamException {
        if (pContext instanceof SdoNamespaceContext) {
            _rootNamespaceContext = (SdoNamespaceContext)pContext;
        }
    }

    public void setPrefix(String prefix, String pUri) throws XMLStreamException {
        if (_level < 0) {
            _rootNamespaceContext.addPrefix(prefix, pUri);
            return;
        }
        getElement().setPrefix(prefix, pUri);
    }

    public void writeAttribute(String pLocalName, String pValue)
        throws XMLStreamException {
        getElement().addAttribute(XMLConstants.NULL_NS_URI, pLocalName, pLocalName, pValue);
    }

    public void writeAttribute(String pNamespaceURI, String pLocalName,
        String pValue) throws XMLStreamException {
        String qName = _supportQNames?getQName(getPrefix(pNamespaceURI), pLocalName):"";
        getElement().addAttribute(pNamespaceURI, pLocalName, qName, pValue);
    }

    public void writeAttribute(String prefix, String pNamespaceURI,
        String pLocalName, String pValue) throws XMLStreamException {
        setPrefix(prefix, pNamespaceURI);
        String qName = _supportQNames?getQName(prefix, pLocalName):"";
        getElement().addAttribute(pNamespaceURI, pLocalName, qName, pValue);
    }

    public void writeCData(String pData) throws XMLStreamException {
        writeCharacters(pData);
    }

    public void writeCharacters(String pText) throws XMLStreamException {
        checkDeferredElement();
        try {
            _contentHandler.characters(pText.toCharArray(), 0, pText.length());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeCharacters(char[] pText, int pStart, int pLen)
        throws XMLStreamException {
        try {
            _contentHandler.characters(pText, pStart, pLen);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeComment(String pData) throws XMLStreamException {
        checkDeferredElement();
    }

    public void writeDTD(String pDtd) throws XMLStreamException {
    }

    public void writeDefaultNamespace(String pNamespaceURI)
        throws XMLStreamException {
        writeNamespace(XMLConstants.DEFAULT_NS_PREFIX, pNamespaceURI);
    }

    public void writeEmptyElement(String pLocalName) throws XMLStreamException {
        writeStartElement(pLocalName);
        _endElement = true;
    }

    public void writeEmptyElement(String pNamespaceURI, String pLocalName)
        throws XMLStreamException {
        writeStartElement(pNamespaceURI, pLocalName);
        _endElement = true;
    }

    public void writeEmptyElement(String prefix, String pLocalName,
        String pNamespaceURI) throws XMLStreamException {
        writeStartElement(prefix, pLocalName, pNamespaceURI);
        _endElement = true;
    }

    public void writeEndDocument() throws XMLStreamException {
        checkDeferredElement();
        try {
            _contentHandler.endDocument();
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeEndElement() throws XMLStreamException {
        checkDeferredElement();
        endElement();
    }

    public void writeEntityRef(String pName) throws XMLStreamException {
        checkDeferredElement();
        try {
            _contentHandler.skippedEntity(pName);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeNamespace(String prefix, String pNamespaceURI)
        throws XMLStreamException {
        setPrefix(prefix, pNamespaceURI);
    }

    public void writeProcessingInstruction(String pTarget)
        throws XMLStreamException {
        writeProcessingInstruction(pTarget, null);
    }

    public void writeProcessingInstruction(String pTarget, String pData)
        throws XMLStreamException {
        checkDeferredElement();
        try {
            _contentHandler.processingInstruction(pTarget, pData);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeStartDocument() throws XMLStreamException {
        try {
            _contentHandler.startDocument();
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeStartDocument(String pVersion) throws XMLStreamException {
        writeStartDocument();
    }

    public void writeStartDocument(String pEncoding, String pVersion)
        throws XMLStreamException {
        writeStartDocument();
    }

    public void writeStartElement(String pLocalName) throws XMLStreamException {
        initElement(XMLConstants.NULL_NS_URI, pLocalName, pLocalName);
        _startElement = true;
    }

    public void writeStartElement(String pNamespaceURI, String pLocalName)
        throws XMLStreamException {
        String qName = _supportQNames?getQName(getPrefix(pNamespaceURI), pLocalName):"";
        initElement(pNamespaceURI, pLocalName, qName);
        _startElement = true;
    }

    public void writeStartElement(String prefix, String pLocalName,
        String pNamespaceURI) throws XMLStreamException {
        String qName = _supportQNames?getQName(prefix, pLocalName):"";
        initElement(pNamespaceURI, pLocalName, qName);
        setPrefix(prefix, pNamespaceURI);
        _startElement = true;
    }

    private void initElement(String pNamespaceURI, String pLocalName, String pQName) throws XMLStreamException {
        checkDeferredElement();
        SdoNamespaceContext namespaceContext;
        if (_level < 0) {
            namespaceContext = _rootNamespaceContext;
        } else {
            namespaceContext = getElement().getNamespaceContext();
        }
        _level++;
        if (_level == _elements.size()) {
            _elements.add(new Element());
        }
        getElement().init(pNamespaceURI, pLocalName, pQName, namespaceContext);
    }

    private Element getElement() {
        return _elements.get(_level);
    }

    private String getQName(String pPrefix, String pLocalName) {
        if (pPrefix == null || pPrefix.length() == 0) {
            return pLocalName;
        }
        return pPrefix + ':' + pLocalName;
    }

    private void startElement() throws XMLStreamException {
        _startElement = false;
        _endElement = false;
        Element element = getElement();
        try {
            if (_level == 0) {
                startPrefixMappings(_rootNamespaceContext);
            }
            startPrefixMappings(element._namespaceContext);
            _contentHandler.startElement(element.getUri(), element.getLocalName(), element.getQName(), element.getAttributes());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void startPrefixMappings(SdoNamespaceContext namespaceContext)
        throws SAXException {
        if (namespaceContext != null) {
            int namespaceCount = namespaceContext.getNamespaceDeclarationCount();
            for (int i = 0; i < namespaceCount; i++) {
                _contentHandler.startPrefixMapping(
                    namespaceContext.getNamespacePrefix(i), namespaceContext.getNamespaceURI(i));
            }
        }
    }

    private void endElement() throws XMLStreamException {
        _startElement = false;
        _endElement = false;
        Element element = getElement();
        try {
            _contentHandler.endElement(element.getUri(), element.getLocalName(), element.getQName());
            endPrefixMappings(element._namespaceContext);
            if (_level == 0) {
                endPrefixMappings(_rootNamespaceContext);
            }
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
        element.clear();
        _level--;
    }

    private void endPrefixMappings(SdoNamespaceContext namespaceContext)
        throws SAXException {
        if (namespaceContext != null) {
            int namespaceCount = namespaceContext.getNamespaceDeclarationCount();
            for (int i = 0; i < namespaceCount; i++) {
                _contentHandler.endPrefixMapping(namespaceContext.getNamespacePrefix(i));
            }
        }
    }

    private void checkDeferredElement() throws XMLStreamException {
        if (_endElement) {
            endElement();
        } else if (_startElement) {
            startElement();
        }
    }

    private static class Element {

        SdoNamespaceContext _parentNamespaceContext;
        SdoNamespaceContext _namespaceContext;
        AttributesImpl _attributes = new AttributesImpl();
        String _uri;
        String _localName;
        String _qName;

        public void init(String pUri, String pLocalName, String pQName, SdoNamespaceContext pParentNamespaceContext) {
            _uri = pUri;
            _localName = pLocalName;
            _qName = pQName;
            _parentNamespaceContext = pParentNamespaceContext;
        }

        public void clear() {
            _uri = null;
            _localName = null;
            _parentNamespaceContext = null;
            _namespaceContext = null;
            _attributes.clear();
        }

        public String getLocalName() {
            return _localName;
        }

        public String getUri() {
            return _uri;
        }

        public String getQName() {
            return _qName;
        }

        public void addAttribute(String pNamespaceURI, String pLocalName, String pQName, String pValue) {
            _attributes.addAttribute(pNamespaceURI, pLocalName, pQName, "CDATA", pValue);
        }

        public SdoNamespaceContext getModifiableNamespaceContext() {
            if (_namespaceContext == null) {
                _namespaceContext = new SdoNamespaceContext(_parentNamespaceContext);
            }
            return _namespaceContext;
        }

        public SdoNamespaceContext getNamespaceContext() {
            return _namespaceContext!=null?_namespaceContext:_parentNamespaceContext;
        }

        public void setPrefix(String pPrefix, String pUri) {
            String oldUri = getUri(pPrefix);
            if (oldUri == null || !oldUri.equals(pUri)) {
                getModifiableNamespaceContext().addPrefix(pPrefix, pUri);
            }
        }

        public String getPrefix(String pUri) {
            SdoNamespaceContext namespaceContext = getNamespaceContext();
            return namespaceContext.getPrefix(pUri);
        }

        private String getUri(String pPrefix) {
            SdoNamespaceContext namespaceContext = getNamespaceContext();
            return namespaceContext.getNamespaceURI(pPrefix);
        }

        public Attributes getAttributes() {
            return _attributes;
        }
    }

}
