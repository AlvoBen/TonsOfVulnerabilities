package com.sap.sdo.impl.xml.sax;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

public class XmlReaderImpl implements XMLReader {

    private static final String PROPERTIES_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    private static final String FEATURES_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String FEATURES_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    final private HelperContext _helperContext;
    final private Map _options = new HashMap();
    final private XMLDocument _xmlDocument;
    boolean _supportQNames = false;
    private LexicalHandler _lexicalHandler;
    private ErrorHandler _errorHandler;
    // we will store this value but never use it by ourselves.
    private EntityResolver _entityResolver;
    private DTDHandler _dtdHandler;
    // SAX allows ContentHandler to be changed during the parsing,
    // but SDO doesn't. So this repeater will sit between those
    // two components.
    private XMLFilterImpl _repeater = new XMLFilterImpl();

    public XmlReaderImpl(final XMLDocument pXmlDocument, HelperContext pHelperContext, final Map pOptions) {
        _xmlDocument = pXmlDocument;
        _helperContext = pHelperContext;
        if (pOptions != null) {
            _options.putAll(pOptions);
        }
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException {
        if(name.equals(FEATURES_NAMESPACES))
            return true;
        if(name.equals(FEATURES_NAMESPACE_PREFIXES))
            return _supportQNames;
        throw new SAXNotRecognizedException(name);
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
        if(name.equals(FEATURES_NAMESPACES) && value)
            return;
        if(name.equals(FEATURES_NAMESPACE_PREFIXES)) {
            _supportQNames = value;
            return;
        }
        throw new SAXNotRecognizedException(name);
    }

    public Object getProperty(String name) throws SAXNotRecognizedException {
        if( PROPERTIES_LEXICAL_HANDLER.equals(name) ) {
            return _lexicalHandler;
        }
        throw new SAXNotRecognizedException(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException {
        if( PROPERTIES_LEXICAL_HANDLER.equals(name) ) {
            _lexicalHandler = (LexicalHandler)value;
            return;
        }
        throw new SAXNotRecognizedException(name);
    }

    public void setEntityResolver(EntityResolver resolver) {
        _entityResolver = resolver;
    }
    public EntityResolver getEntityResolver() {
        return _entityResolver;
    }

    public void setDTDHandler(DTDHandler handler) {
        _dtdHandler = handler;
    }
    public DTDHandler getDTDHandler() {
        return _dtdHandler;
    }

    public void setContentHandler(ContentHandler handler) {
        _repeater.setContentHandler(handler);
    }
    public ContentHandler getContentHandler() {
        return _repeater.getContentHandler();
    }

    public void setErrorHandler(ErrorHandler handler) {
        _errorHandler = handler;
    }
    public ErrorHandler getErrorHandler() {
        return _errorHandler;
    }

    public void parse(InputSource input) throws SAXException {
        parse();
    }

    public void parse(String systemId) throws SAXException {
        parse();
    }

    public void parse() throws SAXException {
        // parses a content object by using the given marshaller
        // SAX events will be sent to the repeater, and the repeater
        // will further forward it to an appropriate component.
        try {
            SAXResult saxResult = new SAXResult(_repeater);
            if (!_options.containsKey(FEATURES_NAMESPACE_PREFIXES)) {
                _options.put(FEATURES_NAMESPACE_PREFIXES, _supportQNames);
            }
            _helperContext.getXMLHelper().save(_xmlDocument, saxResult, _options);
        } catch( IOException e ) {
            // wrap it to a SAXException
            e.printStackTrace();
            SAXParseException se =
                new SAXParseException( e.getMessage(),
                    null, null, -1, -1, e );

            // if the consumer sets an error handler, it is our responsibility
            // to notify it.
            if(_errorHandler!=null)
                _errorHandler.fatalError(se);

            // this is a fatal error. Even if the error handler
            // returns, we will abort anyway.
            throw se;
        }
    }

    public Map getOptions() {
        return _options;
    }

    public XMLDocument getDocument() {
        return _xmlDocument;
    }

}
