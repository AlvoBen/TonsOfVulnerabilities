package com.sap.sdo.impl.xml.stream;

import static com.sap.sdo.api.util.URINamePair.SCHEMA_SCHEMA;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;
import com.sap.sdo.impl.xml.stream.adapters.impl.AbstractElementAdapter;
import com.sap.sdo.impl.xml.stream.adapters.impl.AdapterPool;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

public class SdoStreamReader implements XMLStreamReader {

    private final Map<String,Object> _properties = new HashMap<String,Object>();
    private final Deque<ElementAdapter> _elementStack = new ArrayDeque<ElementAdapter>();
    private final AdapterPool _adapterPool = new AdapterPool();
    private ElementAdapter _currentElement;
    private String _text;
    private String _encoding;
    private String _version;
    private int _currentEvent = START_DOCUMENT;

    /**
     * @param element
     */
    public SdoStreamReader(XMLDocument doc, Map<String,Object> options, HelperContext ctx) {
        _currentElement = AbstractElementAdapter.getElementAdapter(doc, ctx, _adapterPool);
        _elementStack.push(_currentElement);

        _encoding = doc.getEncoding();
        _version = doc.getXMLVersion();

        if (options != null) {
            _properties.putAll(options);
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#getProperty(java.lang.String)
     */
    public Object getProperty(String key) throws IllegalArgumentException {
        return _properties.get(key);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#next()
     */
    public int next() throws XMLStreamException {
        _text = null;
        if (_currentElement.isEnded()) {
            _elementStack.pop();
            if (!_elementStack.isEmpty()) {
                _currentElement.clear();
                _currentElement = _elementStack.peek();
            } else {
                _currentEvent = END_DOCUMENT;
                return _currentEvent;
            }
        }

        if (!_currentElement.isStarted()) {
            _currentElement.start();
            _currentEvent = START_ELEMENT;
        } else if (_currentElement.hasChild()) {
            ElementAdapter currentChildNode = _currentElement.nextChild();

            if (currentChildNode.isText()) {
                _text = currentChildNode.getValue();
                currentChildNode.clear();
                _currentEvent = CHARACTERS;
            } else {
                ElementAdapter newElement = currentChildNode;
                newElement.start();
                _currentElement = newElement;
                _elementStack.push(_currentElement);
                _currentEvent = START_ELEMENT;
            }

        } else {
            _currentElement.end();
            _currentEvent = END_ELEMENT;
        }
        return _currentEvent;
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#require(int, java.lang.String, java.lang.String)
     */
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (_currentEvent != type) {
            throw new XMLStreamException(
                "event type doesn't match, current is " +
                getEventTypeString() + "(" + _currentEvent + ")");
        }
        if (namespaceURI != null && !namespaceURI.equals(getNamespaceURI())) {
            throw new XMLStreamException("namespaceURI doesn't match, current is " + getNamespaceURI());
        }
        if (localName != null && !localName.equals(getLocalName())) {
            throw new XMLStreamException("localName doesn't match, current is " + getLocalName());
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#getElementText()
     */
    public String getElementText() throws XMLStreamException {
        if(getEventType() != START_ELEMENT) {
            throw new XMLStreamException(
                "parser must be on START_ELEMENT to read next text",
                getLocation());
        }
        int eventType = next();
        StringBuilder buf = new StringBuilder();
        while(eventType != END_ELEMENT ) {
            if(eventType == CHARACTERS) {
                buf.append(getText());
            } else if(eventType == END_DOCUMENT) {
                throw new XMLStreamException(
                    "unexpected end of document when reading element text content",
                    getLocation());
            } else if(eventType == START_ELEMENT) {
                throw new XMLStreamException(
                    "element text content may not contain START_ELEMENT",
                    getLocation());
            } else {
                throw new XMLStreamException("Unexpected event type "+eventType, getLocation());
            }
            eventType = next();
        }
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.stream.XMLStreamReader#nextTag()
     */
    public int nextTag() throws XMLStreamException {
        int eventType = next();
        if (eventType != START_ELEMENT && eventType != END_ELEMENT) {
            throw new XMLStreamException("expected start or end tag", getLocation());
        }
        return eventType;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.stream.XMLStreamReader#hasNext()
     */
    public boolean hasNext() throws XMLStreamException {
        return !(_elementStack.size() == 0 && _currentElement.isEnded());
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.stream.XMLStreamReader#close()
     */
    public void close() throws XMLStreamException {
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.stream.XMLStreamReader#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix) {
        return _currentElement.getNamespaceURI(prefix);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#isStartElement()
     */
    public boolean isStartElement() {
        return (_currentEvent == START_ELEMENT);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#isEndElement()
     */
    public boolean isEndElement() {
        return (_currentEvent == END_ELEMENT);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#isCharacters()
     */
    public boolean isCharacters() {
        return (_currentEvent == CHARACTERS);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#isWhiteSpace()
     */
    public boolean isWhiteSpace() {
        return isCharacters() && _text != null && _text.trim().length() == 0;
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamReader#getAttributeValue(java.lang.String, java.lang.String)
     */
    public String getAttributeValue(String ns, String local) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributeValue(ns, local);
    }

    public int getAttributeCount() {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributeCount();
    }

    public QName getAttributeName(int index) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributeName(index);
    }

    public String getAttributeNamespace(int index) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributeNamespace(index);
    }

    public String getAttributeLocalName(int index) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributeLocalName(index);
    }

    public String getAttributePrefix(int index) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributePrefix(index);
    }

    public String getAttributeType(int index) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        return _currentElement.getAttributeValue(index);
    }

    public boolean isAttributeSpecified(int index) {
        if (_currentEvent != START_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , ATTRIBUTE");
        }
        // True because the attribute is specified in the instance document.
        return true;
    }

    public int getNamespaceCount() {
        if (_currentEvent != START_ELEMENT && _currentEvent != END_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , END_ELEMENT , NAMESPACE");
        }
        return _currentElement.getNamespaceDeclarationCount();
    }

    public String getNamespacePrefix(int ns) {
        if (_currentEvent != START_ELEMENT && _currentEvent != END_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , END_ELEMENT , NAMESPACE");
        }
        return _currentElement.getNamespacePrefix(ns);
    }

    public String getNamespaceURI(int count) {
        if (_currentEvent != START_ELEMENT && _currentEvent != END_ELEMENT) {
            throw new IllegalStateException(
                "Current state is not among the states START_ELEMENT , END_ELEMENT , NAMESPACE");
        }
        return _currentElement.getNamespaceURI(count);
    }

    public NamespaceContext getNamespaceContext() {
        if (_currentEvent == START_DOCUMENT || _currentEvent == END_DOCUMENT) {
            SdoNamespaceContext ctx = new SdoNamespaceContext();
            if (SCHEMA_SCHEMA.equalsUriName(
                    _currentElement.getNamespaceURI(), _currentElement.getLocalName())) {
                ctx.initSchemaCtx();
            } else {
                ctx.initRootCtx();
            }
            return ctx;
        }
        return _currentElement.getNamespaceContext();
    }

    public int getEventType() {
        return _currentEvent;
    }

    private String getEventTypeString() {
        switch (_currentEvent){
            case XMLEvent.START_ELEMENT:
                return "START_ELEMENT";
            case XMLEvent.END_ELEMENT:
                return "END_ELEMENT";
            case XMLEvent.CHARACTERS:
                return "CHARACTERS";
            case XMLEvent.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLEvent.END_DOCUMENT:
                return "END_DOCUMENT";
        }
        return "UNKNOWN_EVENT_TYPE, " + String.valueOf(_currentEvent);
    }

    public String getText() {
        if (_currentEvent != CHARACTERS) {
            throw new IllegalStateException(
                "Current state " + getEventTypeString() +
                " is not among the states CHARACTERS, COMMENT, CDATA, SPACE," +
                " ENTITY_REFERENCE, DTD valid for getText()");
        }
        return _text;
    }

    public char[] getTextCharacters() {
        if (_currentEvent != CHARACTERS) {
            throw new IllegalStateException(
                "Current state " + getEventTypeString() +
                " is not among the states CHARACTERS, COMMENT, CDATA, SPACE," +
                " ENTITY_REFERENCE, DTD valid for getTextCharacters()");
        }
        if (_text == null) {
            return new char[0];
        }
        return _text.toCharArray();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
        throws XMLStreamException {

        if (_currentEvent != CHARACTERS) {
            throw new IllegalStateException(
                "Current state " + getEventTypeString() +
                " is not among the states CHARACTERS, CDATA, SPACE" +
                " valid for getTextCharacters()");
        }
        System.arraycopy(getTextCharacters(), sourceStart, target, targetStart, length);
        return length;
    }

    public int getTextStart() {
        if (_currentEvent != CHARACTERS) {
            throw new IllegalStateException(
                "Current state " + getEventTypeString() +
                " is not among the states CHARACTERS, COMMENT, CDATA, SPACE," +
                " ENTITY_REFERENCE, DTD valid for getTextStart()");
        }
        return 0;
    }

    public int getTextLength() {
        if (_currentEvent != CHARACTERS) {
            throw new IllegalStateException(
                "Current state " + getEventTypeString() +
                " is not among the states CHARACTERS, COMMENT, CDATA, SPACE," +
                " ENTITY_REFERENCE, DTD valid for getTextLength()");
        }
        if (_text != null) {
            return _text.length();
        }
        return 0;
    }

    public String getEncoding() {
        return _encoding;
    }

    public boolean hasText() {
        return _text != null;
    }

    public Location getLocation() {
        return new SdoLocation(this);
    }

    public String getLocationReference() {
        return _currentElement.generateReference();
    }

    public QName getName() {
        if (_currentEvent != START_ELEMENT && _currentEvent != END_ELEMENT) {
            throw new IllegalStateException(
                "expected start or end tag, but was " + getEventTypeString());
        }

        String local = _currentElement.getLocalName();
        String prefix = _currentElement.getNamespacePrefix();
        String ns = _currentElement.getNamespaceURI();

        if (ns != null) {
            if (prefix != null) {
                return new QName(ns, local, prefix);
            } else {
                return new QName(ns, local);
            }
        }

        return new QName(local);
    }

    public String getLocalName() {
        if (_currentEvent == START_ELEMENT
                || _currentEvent == END_ELEMENT) {
            return _currentElement.getLocalName();
        }
        return null;
    }

    public boolean hasName() {
        return (_currentEvent == START_ELEMENT || _currentEvent == END_ELEMENT);
    }

    public String getNamespaceURI() {
        if (_currentEvent == START_ELEMENT
                || _currentEvent == END_ELEMENT) {
            return _currentElement.getNamespaceURI();
        }
        return null;
    }

    public String getPrefix() {
        if (_currentEvent == START_ELEMENT
                || _currentEvent == END_ELEMENT) {
            return _currentElement.getNamespacePrefix();
        }
        return null;
    }

    public String getVersion() {
        return _version;
    }

    public boolean isStandalone() {
        return false;
    }

    public boolean standaloneSet() {
        return false;
    }

    public String getCharacterEncodingScheme() {
        return getEncoding();
    }

    public String getPITarget() {
        return null;
    }

    public String getPIData() {
        return null;
    }

    private static class SdoLocation implements Location {

        private final String _reference;

        /**
         * @param sdoStreamReader
         */
        public SdoLocation(SdoStreamReader sdoStreamReader) {
            _reference = sdoStreamReader.getLocationReference();
        }

        @Override
        public String toString() {
            return "Line number = " + getLineNumber() + "\n" +
                    "Column number = " + getColumnNumber() + "\n" +
                    "System Id = " + getSystemId() + "\n" +
                    "Public Id = " + getPublicId() + "\n" +
                    "CharacterOffset = " + getCharacterOffset() + "\n" +
                    "Reference = " + _reference + "\n";
        }

        public int getCharacterOffset() {
            return -1;
        }

        public int getColumnNumber() {
            return -1;
        }

        public int getLineNumber() {
            return -1;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return null;
        }
    }
}
