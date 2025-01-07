package com.sap.engine.lib.jaxp;

import com.sap.engine.lib.xml.parser.Features;
import javax.xml.parsers.*;
import org.xml.sax.Parser;
import org.xml.sax.XMLReader;
import org.xml.sax.*;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Title:        JAXP
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 * @deprecated Please use standard JAXP api instead.
 */
@Deprecated
public class SAXParserImpl extends javax.xml.parsers.SAXParser {

  static ResourceBundle res = ResourceBundle.getBundle("com.sap.engine.lib.jaxp.Res", new Locale("", ""));
  private com.sap.engine.lib.xml.parser.SAXParser saxParser;

  SAXParserImpl(com.sap.engine.lib.xml.parser.SAXParser saxParser) throws SAXException, ParserConfigurationException {
    this.saxParser = saxParser;
  }

  public boolean isNamespaceAware() {
    try {
      return saxParser.getFeature(Features.FEATURE_NAMESPACES);
    } catch (SAXNotRecognizedException e) {
      //$JL-EXC$
      return false;
    } catch(SAXNotSupportedException e) {
      //$JL-EXC$
      return(false);
    }
  }

  public boolean isValidating() {
    try {
      return saxParser.getFeature(Features.FEATURE_VALIDATION);
    } catch (SAXNotRecognizedException e) {
      //$JL-EXC$
      return false;
    } catch(SAXNotSupportedException e) {
      //$JL-EXC$
      return(false);
    }
  }

  public void setProperty(String name, Object value) throws SAXNotSupportedException, SAXNotRecognizedException {
    saxParser.setProperty(name, value);
  }

  public Object getProperty(String name) throws SAXNotSupportedException, SAXNotRecognizedException {
    return saxParser.getProperty(name);
  }

  public Parser getParser() throws SAXException {
    return saxParser;
  }

  public XMLReader getXMLReader() throws SAXException {
    return saxParser;
  }

}

