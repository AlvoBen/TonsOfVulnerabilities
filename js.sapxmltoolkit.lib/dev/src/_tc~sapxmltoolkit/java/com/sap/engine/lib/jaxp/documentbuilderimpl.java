package com.sap.engine.lib.jaxp;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.*;

import com.sap.engine.lib.xml.dom.DOMImplementationImpl;
import com.sap.engine.lib.xml.dom.DocumentImpl;
import com.sap.engine.lib.xml.parser.DOMParser;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.xml.parser.NestedSAXParseException;
import com.sap.engine.lib.xml.parser.JAXPProperties;
import com.sap.engine.lib.xml.util.NestedParserConfigurationException;

/**
 * 
 * @author I024072
 * @deprecated Please use standard JAXP api instead.
 */
@Deprecated
public final class DocumentBuilderImpl extends DocumentBuilder {

  private DOMParser domParser = null;
  private EntityResolver entRes = null;

  protected DocumentBuilderImpl(DocumentBuilderFactoryImpl factory) throws ParserConfigurationException {
    try {
      domParser = new DOMParser();
      domParser.setFeature(Features.FEATURE_VALIDATION, factory.isValidating());
      try {
        domParser.setFeature(Features.FEATURE_TRIM_WHITESPACES, factory.isIgnoringElementContentWhitespace());
      } catch (NoSuchMethodError e) {
        //$JL-EXC$
        //added for compatibility with older releases of JAXP, where these methods are not supported, and neither required
      }
      try {
        domParser.setFeature(Features.FEATURE_NAMESPACES, factory.isNamespaceAware());
      } catch (NoSuchMethodError e) {
        //$JL-EXC$
        //added for compatibility with older releases of JAXP, where these methods are not supported, and neither required
      }
      try {
        domParser.setIgnoringComments(factory.isIgnoringComments());
      } catch (NoSuchMethodError e) {
        //$JL-EXC$
        //added for compatibility with older releases of JAXP, where these methods are not supported, and neither required
      }
      try {
        domParser.setFeature(Features.FEATURE_EXPANDING_REFERENCES, factory.isExpandEntityReferences());
      } catch (NoSuchMethodError e) {
        //$JL-EXC$
        //added for compatibility with older releases of JAXP, where these methods are not supported, and neither required
      }

			domParser.setAttributes(factory.attributes);
    } catch (ParserConfigurationException pce) {
      throw pce;
    } catch (Exception ex) {
      throw new NestedParserConfigurationException(ex);
    }
  }

//  private void setProperty(String name, Hashtable attributes) throws Exception {
//    Object value = attributes.get(name);
//    if(value != null) {
//      domParser.setProperty(name, value);
//      attributes.remove(name);
//    }
//  }
//
//  private void setFeature(String name, Hashtable attributes) throws Exception {
//    Object value = attributes.get(name);
//    if(value != null) {
//      domParser.setFeature(name, Features.createBooleanValue(value));
//      attributes.remove(name);
//    }
//  }

  public boolean isNamespaceAware() {
    try {
      return domParser.getFeature(Features.FEATURE_NAMESPACES);
    } catch (Exception e) {
      //$JL-EXC$
      return false;
    }
  }

  public void setEntityResolver(org.xml.sax.EntityResolver er) {
    entRes = er;
    domParser.setEntityResolver(er);
  }

  public Document newDocument() {
    return new DocumentImpl();
  }

  public DOMImplementation getDOMImplementation() {
    return new DOMImplementationImpl();
  }

  public void setErrorHandler(ErrorHandler eh) {
    if (eh == null) {
      throw new IllegalArgumentException();
    }

    domParser.setErrorHandler(eh);
    //errorHandler = eh;
  }

  public boolean isValidating() {
    try {
      return domParser.getFeature(Features.FEATURE_VALIDATION);
    } catch (Exception e) {
      //$JL-EXC$
      // the api does not allow for exceptions
      return false;
    }
  }

  public Document parse(InputSource is) throws IOException, SAXException, IllegalArgumentException {
    if (is == null) {
      throw new IllegalArgumentException();
    }

    try {
      Document r = domParser.parse(is);
      domParser.setDocument(null);
      return r;
    } catch (IOException e) {
      throw e;
    } catch (SAXException e) {
      throw e;
    } catch (Exception e) {
      throw new NestedSAXParseException("Parsing failed", e);
    }
  }

  public void setAdditionalDTDLocation(InputSource additionalDTDLocation) throws SAXNotRecognizedException {
    domParser.setProperty(JAXPProperties.PROPERTY_ADDITIONAL_DTD, additionalDTDLocation);
  }
}

