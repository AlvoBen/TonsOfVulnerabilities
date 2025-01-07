package com.sap.sdo.impl.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ComposedCharactersStreamWriter implements XMLStreamWriter {
    int _level = 0;
    final private XMLStreamWriter _delegate;
    private String _characters;
    final private StringBuilder _composedCharacters = new StringBuilder();

    public ComposedCharactersStreamWriter(final XMLStreamWriter pDelegate) {
        _delegate = pDelegate;
    }

    public void close() throws XMLStreamException {
        _delegate.close();
    }

    public void flush() throws XMLStreamException {
        _delegate.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return _delegate.getNamespaceContext();
    }

    public String getPrefix(String pUri) throws XMLStreamException {
        return _delegate.getPrefix(pUri);
    }

    public Object getProperty(String pName) throws IllegalArgumentException {
        return _delegate.getProperty(pName);
    }

    public void setDefaultNamespace(String pUri) throws XMLStreamException {
        _delegate.setDefaultNamespace(pUri);
    }

    public void setNamespaceContext(NamespaceContext pContext) throws XMLStreamException {
        _delegate.setNamespaceContext(pContext);
    }

    public void setPrefix(String prefix, String pUri) throws XMLStreamException {
        _delegate.setPrefix(prefix, pUri);
    }

    public void writeAttribute(String prefix, String pNamespaceURI, String pLocalName, String pValue) throws XMLStreamException {
        _delegate.writeAttribute(prefix, pNamespaceURI, pLocalName, pValue);
    }

    public void writeAttribute(String pNamespaceURI, String pLocalName, String pValue) throws XMLStreamException {
        _delegate.writeAttribute(pNamespaceURI, pLocalName, pValue);
    }

    public void writeAttribute(String pLocalName, String pValue) throws XMLStreamException {
        _delegate.writeAttribute(pLocalName, pValue);
    }

    public void writeCData(String pData) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeCData(pData);
    }

    public void writeCharacters(char[] pText, int pStart, int pLen) throws XMLStreamException {
        if (_characters != null) {
            _composedCharacters.append(_characters);
            _characters = null;
        }
        _composedCharacters.append(pText, pStart, pLen);
    }

    public void writeCharacters(String pText) throws XMLStreamException {
        if (_characters == null) {
            if (_composedCharacters.length() == 0) {
                _characters = pText;
            } else {
                _composedCharacters.append(pText);
            }
        } else {
            _composedCharacters.append(_characters);
            _characters = null;
            _composedCharacters.append(pText);
        }
    }

    public void writeComment(String pData) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeComment(pData);
    }

    public void writeDefaultNamespace(String pNamespaceURI) throws XMLStreamException {
        _delegate.writeDefaultNamespace(pNamespaceURI);
    }

    public void writeDTD(String pDtd) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeDTD(pDtd);
    }

    public void writeEmptyElement(String prefix, String pLocalName, String pNamespaceURI) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeEmptyElement(prefix, pLocalName, pNamespaceURI);
    }

    public void writeEmptyElement(String pNamespaceURI, String pLocalName) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeEmptyElement(pNamespaceURI, pLocalName);
    }

    public void writeEmptyElement(String pLocalName) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeEmptyElement(pLocalName);
    }

    public void writeEndDocument() throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeEndDocument();
    }

    public void writeEndElement() throws XMLStreamException {
        cleanUpCharacters();
        _level--;
        _delegate.writeEndElement();
    }

    public void writeEntityRef(String pName) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeEntityRef(pName);
    }

    public void writeNamespace(String prefix, String pNamespaceURI) throws XMLStreamException {
        _delegate.writeNamespace(prefix, pNamespaceURI);
    }

    public void writeProcessingInstruction(String pTarget, String pData) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeProcessingInstruction(pTarget, pData);
    }

    public void writeProcessingInstruction(String pTarget) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeProcessingInstruction(pTarget);
    }

    public void writeStartDocument() throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeStartDocument();
    }

    public void writeStartDocument(String pEncoding, String pVersion) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeStartDocument(pEncoding, pVersion);
    }

    public void writeStartDocument(String pVersion) throws XMLStreamException {
        cleanUpCharacters();
        _delegate.writeStartDocument(pVersion);
    }

    public void writeStartElement(String prefix, String pLocalName, String pNamespaceURI) throws XMLStreamException {
        cleanUpCharacters();
        _level++;
        _delegate.writeStartElement(prefix, pLocalName, pNamespaceURI);
    }

    public void writeStartElement(String pNamespaceURI, String pLocalName) throws XMLStreamException {
        cleanUpCharacters();
        _level++;
        _delegate.writeStartElement(pNamespaceURI, pLocalName);
    }

    public void writeStartElement(String pLocalName) throws XMLStreamException {
        cleanUpCharacters();
        _level++;
        _delegate.writeStartElement(pLocalName);
    }
    
    private void cleanUpCharacters() throws XMLStreamException {
        if (_level > 0) {
            if (_characters != null) {
                try {
                    _delegate.writeCharacters(_characters);
                } finally {
                    _characters = null;
                }
            } else if (_composedCharacters.length() > 0) {
                try {
                    _delegate.writeCharacters(_composedCharacters.toString());
                } finally {
                    _composedCharacters.setLength(0);
                }
            }
        } else {
            _characters = null;
            _composedCharacters.setLength(0);
        }
    }
}
