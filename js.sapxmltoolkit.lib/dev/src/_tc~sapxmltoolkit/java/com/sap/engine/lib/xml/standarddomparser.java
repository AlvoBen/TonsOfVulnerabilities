/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.xml.parser.NestedSAXParseException;
import com.sap.engine.lib.xml.parser.JAXPProperties;

/**
 * The xml validating parser.
 * It's also an error handler for all errors, occurred in parsing a xml source.
 *
 * @author Monika Kovachka
 * @version 4.0.0
 *
 */
public class StandardDOMParser implements ErrorHandler {

  DocumentBuilderFactory factory = null;
  DocumentBuilder builder = null;
  public static final String INQMY_PARSER = "server.parser.inqmy";
  public static final String SAP_DTD_PREFIX = "file://sap_dtd_prefix/";
  public static StandardEntityResolver stdentres = new StandardEntityResolver();
  public EntityResolver entres = stdentres;
  private ErrorHandler errhand = this;
  private String systemId = null;
  private boolean reportErrors = true;

  public StandardDOMParser() {
    this(false);
  }
  
  public StandardDOMParser(boolean useSapXMLToolkit) {
    try {
      if (SystemProperties.getProperty(INQMY_PARSER) != null || useSapXMLToolkit) {
        try {
          factory = (DocumentBuilderFactory) Class.forName("com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl").newInstance();
        } catch (Exception e) {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw, true);
          e.printStackTrace(pw);
          String msg = sw.toString();
          pw.close();
          throw new RuntimeException(msg);
        }
      } else {
        factory = DocumentBuilderFactory.newInstance();
      }      

      factory.setExpandEntityReferences(true);
      builder = factory.newDocumentBuilder();
      setValidation(false);
      //setReadDTD(false);
    } catch (Throwable e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      e.printStackTrace(pw);
      String msg = sw.toString();
      pw.close();
      throw new RuntimeException(msg);
    }
  }

  static public void trimWhiteSpaces(Node node) {
    if (node instanceof Document) {
      trimWhiteSpaces(((Document) node).getDocumentElement());
    } else if (node instanceof Element) {
      NodeList nl = ((Element) node).getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {
        trimWhiteSpaces(nl.item(i));
      }
    } else if (node instanceof Text) {
      Text tx = (Text) node;
      try {
        tx.setData(trimWhiteSpace(tx.getData()));
      } catch (Exception e) {
        // Exception cannot be thrown on this place
        e.printStackTrace();
        throw new RuntimeException("StandardDOMParser.trimWhiteSpaceNodes: Unexpected Exception, when trimming white-space: " + e);

      }
    }
  }

  public Document parse(String uri) throws SAXException, IOException {
    if (systemId != null) {
      throw new SAXException("The method parse(InputStream) should be used instead of parse(String), when the systemId is set");
    }
    return parse(new InputSource(uri));
  }

  public Document parse(InputStream is) throws SAXException, IOException {
    InputSource isource = new InputSource(is);
    isource.setSystemId(SAP_DTD_PREFIX);
    return parse(isource);
  }

  public Document parse(InputSource is) throws SAXException, IOException {
    Document doc = null;
    FileInputStream fin = null;
    try {
      builder.setEntityResolver(entres);
      builder.setErrorHandler(this);
      reportErrors = true;
      if (systemId != null) {
        is.setSystemId(systemId);
      }
      if (is.getByteStream() != null) {
        is.setByteStream(new StandardInputStream(is.getByteStream()));
      } else if (is.getSystemId() != null) {
        try {
          fin = new FileInputStream(is.getSystemId());
          is.setByteStream(new StandardInputStream(fin));
        } catch (Exception e) {
          //$JL-EXC$
          
          // in this case nothing will be done. the InputStream
          // will be sent to the parser, which will try again to parse
          // it. If he is unsuccessfull too, then the exception
          // will be thrown by the parser.
        }
      }
      doc = builder.parse(is);
    } catch (IOException e) {
      throw e;
    } catch (Exception ex) {
      throw new NestedSAXParseException(ex);
    } finally  {
      if (fin != null) {
        fin.close();
      }
    }
    doc.normalize();
    trimWhiteSpaces(doc);
    return doc;
  }

  public void setSystemId(String s) {
    systemId = s;
  }

  public String getSystemId() {
    return systemId;
  }

  public void warning(SAXParseException ex) {
  }

  public void error(SAXParseException exception) throws SAXException {
    if (reportErrors && exception.toString().indexOf("must match DOCTYPE root \"null\"") > -1) {
      reportErrors = false;
    } else if (reportErrors) {
      throw exception;
    }
  }

  public void fatalError(SAXParseException ex) throws SAXException {
    throw ex;
  }

  public void setValidation(boolean value) throws SAXException {
    try {
//      LogWriter.getSystemLogWriter().println(">>>>> StandardDOMParser.setValidation=" + value + " id = " + this.hashCode());
      if (!value) {
        reportErrors = false;
//        setEntityResolver(new EntityResolver() {
//          public InputSource resolveEntity(String a, String b) {
//            return new InputSource(new StringReader(" "));
//            
//          }
//        });
      } 
//      else {
//        setEntityResolver(stdentres);
//      }
      factory.setValidating(value);
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void setAdditionalDTDLocation(InputSource additionalDTD) throws SAXException {
    try {
      factory.setAttribute(JAXPProperties.PROPERTY_ADDITIONAL_DTD, additionalDTD);
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void setFeature(String feature, boolean value) throws SAXException {
    try {
      if (feature.equals("http://xml.org/sax/features/namespaces")) {
        setNamespaces(value);
      } else if (feature.equals("http://xml.org/sax/features/validation")) {
        setValidation(value);
      } else if (feature.equals("http://inqmy.org/dom/features/trim-white-spaces")) {
        if (factory.getClass().getName().equals("com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl")) {
          factory.setAttribute(feature, new Boolean(value));
        }

        //else {
        //  throw new SAXNotSupportedException(feature);
        // }
      } else if (feature.equals("http://apache.org/xml/features/nonvalidating/load-external-dtd")) {
        if (factory.getClass().getName().equals("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl")) {
          factory.setAttribute(feature, new Boolean(value));
        } else if (factory.getClass().getName().equals("com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl")) {
           factory.setAttribute("http://inqmy.org/sax/features/read-dtd", new Boolean(value));
        }

        //else {
        // throw new SAXNotSupportedException(feature);
        // }
      } else if (feature.equals("http://inqmy.org/xml/features/close-streams")) {
		    try	{
          factory.setAttribute(feature, new Boolean(value));
    		}	catch (Exception e) {
          //$JL-EXC$
		    }
      } else {
        factory.setAttribute(feature, new Boolean(value));
        //throw new SAXNotSupportedException(feature);
      }

      try {
        builder = factory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        throw new NestedSAXParseException(e);
      }
    } catch (NoSuchMethodError e) {
      //$JL-EXC$
   	  // added for compatibility with older versions of JAXP.
    }
  }

  public void setNamespaces(boolean value) throws SAXException {
    try {
      factory.setNamespaceAware(value);
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void setParserProxy(String host, int port) throws SAXException {
    setProperty("http://inqmy.org/sax/properties/proxy-host", host);
    setProperty("http://inqmy.org/sax/properties/proxy-port", new Integer(port));
  }

  public void setProperty(String property, Object data) throws SAXException {
    if (property.equals("http://xml.org/sax/features/namespaces")) {
      setNamespaces(((Boolean) data).booleanValue());
    } else if (property.equals("http://xml.org/sax/features/validation")) {
      setValidation(((Boolean) data).booleanValue());
    } else if(property.equals(JAXPProperties.PROPERTY_ADDITIONAL_DTD)) {
      if(!(data instanceof InputSource)) {
        throw new SAXNotSupportedException("The value of property " + JAXPProperties.PROPERTY_ADDITIONAL_DTD + " has to be an instance of class org.xml.sax.InputSource.");
      }
      setAdditionalDTDLocation((InputSource)data);
    } else {
      throw new SAXNotSupportedException(property);
    }

    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void setParserAlternativeDTD(String uri) throws SAXException {
    setProperty("http://inqmy.org/sax/properties/alternative-dtd", uri);
  }

  public void setBackwardsCompatibilityMode(boolean value) throws SAXException {
    setFeature(Features.FEATURE_BACKWARDS_COMPATIBILITY_MODE, value);
  }

  //
  public void setUseProxy(boolean value) throws SAXException {
    setFeature("http://inqmy.org/sax/features/use-proxy", value);
  }

  public void setReadDTD(boolean value) throws SAXException {
    if (!value) {
      setEntityResolver(new EntityResolver() {
        public InputSource resolveEntity(String a, String b) {
          return new InputSource(new StringReader(" "));
          
        }
      });
    } 
  }
    

  public void setCloseStreams(boolean value) throws SAXException {
    setFeature("http://inqmy.org/xml/features/close-streams", value);
  }

  public static Document createDocument() {
    try {
      DocumentBuilderFactory factory = null;

      if (SystemProperties.getProperty(INQMY_PARSER) != null) {
        try {
          factory = (DocumentBuilderFactory) Class.forName("com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl").newInstance();
        } catch (Exception e) {
          //$JL-EXC$
          //in clase we cannot load the SAP implementation, use the JAXP one, and whateveer exception it throws
          e.printStackTrace();
          factory = DocumentBuilderFactory.newInstance();
        }
      } else {
        factory = DocumentBuilderFactory.newInstance();
      }

      return factory.newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Could not create a new Document. Exception: " + e);
    }
  }

  static public String trimWhiteSpace(String data) {
    int b, e;
    if (data == null) {
      return "";
    }
    for (b = 0; b < data.length() && isWhiteSpaceChar(data.charAt(b)); b++) {
      ;
    }
    for (e = data.length() - 1; e > 0 && e > b && isWhiteSpaceChar(data.charAt(e)); e--) {
      ;
    }
    data = data.substring(b, e + 1);
    return data;
  }

  static public boolean isWhiteSpace(String str) {
    if (str == null) {
      return false;
    } else {
      for (int i = 0; i < str.length(); i++) {
        if (!isWhiteSpaceChar(str.charAt(i))) {
          return false;
        }
      }
    }

    return true;
  }

  static public boolean isWhiteSpaceChar(char ch) {
    if (ch == 0x20 || ch == 0xD || ch == 0xA || ch == 0x9) {
      return true;
    } else {
      return false;
    }
  }

  public void setEntityResolver(EntityResolver r) {
    entres = r;
  }

  /**
   * @return Returns the errhand.
   */
  public ErrorHandler getErrorHandler() {
    return errhand;
  }

  /**
   * @param errhand The errhand to set.
   */
  public void setErrorHandler(ErrorHandler errhand) {
    this.errhand = errhand;
  }

    
}

