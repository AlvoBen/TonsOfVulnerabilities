/*
 * Copyright (c) 2002 by SAP Labs Sofia,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Sofia. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia.
 */
package com.sap.engine.lib.xml.parser;

import java.io.IOException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import com.sap.engine.lib.jaxp.TransformerImpl;

/**
 * @author Alexander Zubev
 */
public class SAXFilter implements ErrorListener, XMLFilter {

  XMLReader parent;
  TransformerImpl transformer;

  public SAXFilter(Templates templates) throws ParserException, TransformerConfigurationException {
    transformer = (TransformerImpl) templates.newTransformer();
  }

  public XMLReader getParent() {
    return parent;
  }

  public void setParent(XMLReader parent) {
    this.parent = parent;
  }

  public void setContentHandler(ContentHandler contenthandler) {
    parent.setContentHandler(contenthandler);
  }

  public ContentHandler getContentHandler() {
    return parent.getContentHandler();
  }

  public ErrorHandler getErrorHandler() {
    return parent.getErrorHandler();
  }

  public void setErrorHandler(ErrorHandler errorHandler) {
    parent.setErrorHandler(errorHandler);
  }

  public DTDHandler getDTDHandler() {
    return parent.getDTDHandler();
  }

  public void setDTDHandler(DTDHandler dtdHandler) {
    parent.setDTDHandler(dtdHandler);
  }

  public EntityResolver getEntityResolver() {
    return parent.getEntityResolver();
  }

  public void setEntityResolver(EntityResolver entityResolver) {
    parent.setEntityResolver(entityResolver);
  }

  public void parse(String systemId) throws IOException, SAXException {
    parse(new InputSource(systemId));
  }

  public boolean getFeature(String feature) throws SAXNotRecognizedException, SAXNotSupportedException {
    return parent.getFeature(feature);
  }

  public void setFeature(String feature, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    parent.setFeature(feature, value);
  }

  public Object getProperty(String property) throws SAXNotRecognizedException, SAXNotSupportedException {
    return parent.getProperty(property);
  }

  public void setProperty(String propertyName, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    parent.setProperty(propertyName, value);
  }

  public void parse(InputSource input) throws IOException, SAXException {
    ContentHandler contentHandler = getContentHandler();
    SAXResult result = new SAXResult();
    result.setHandler(contentHandler);

    if (parent.getErrorHandler() != null) {
      transformer.setErrorListener(this);
    }

    try {
      StreamSource source = new StreamSource();

      if (input.getCharacterStream() != null) {
        source.setReader(input.getCharacterStream());
      } else if (input.getByteStream() != null) {
        source.setInputStream(input.getByteStream());
      } else {
        source.setSystemId(input.getSystemId());
      }

      transformer.transform(source, result);
    } catch (TransformerException e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void error(TransformerException tre) throws TransformerException {
    SourceLocator loc = tre.getLocator();
    try {
      parent.getErrorHandler().error(new NestedSAXParseException(tre.getMessage(), loc.getPublicId(), loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber(), tre));
    } catch (SAXException saxe) {
      throw new TransformerException(saxe);
    }
  }

  public void warning(TransformerException tre) throws TransformerException {
    SourceLocator loc = tre.getLocator();
    try {
      parent.getErrorHandler().warning(new NestedSAXParseException(tre.getMessage(), loc.getPublicId(), loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber(), tre));
    } catch (SAXException saxe) {
      throw new TransformerException(saxe);
    }
  }

  public void fatalError(TransformerException tre) throws TransformerException {
    SourceLocator loc = tre.getLocator();
    SAXParseException ex = new NestedSAXParseException(tre.getMessage(), loc.getPublicId(), loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber(), tre);
    try {
      parent.getErrorHandler().fatalError(ex);
    } catch (SAXException saxe) {
      throw new TransformerException(saxe);
    }
    throw tre;
  }

}

