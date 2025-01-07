package com.sap.engine.lib.jaxp;

import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.xml.util.NestedParserConfigurationException;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;


/**
 * Title:        JAXP
 * Description:  Stores features in a hashtable, and sets them when a new
 *               SAXParser instance is requested.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 * @deprecated Please use standard JAXP api instead.
 */
@Deprecated
public final class SAXParserFactoryImpl extends SAXParserFactory {

  static ResourceBundle res = ResourceBundle.getBundle("com.sap.engine.lib.jaxp.Res", new Locale("", ""));
  Hashtable features = new Hashtable(); // Hashes feature names to Booleans

  public SAXParserFactoryImpl() {

  }

  public void setFeature(String name, boolean value) throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
    if (!Features.RECOGNIZED.contains(name)) {
      throw new SAXNotRecognizedException(name);
    }
    if (!Features.SUPPORTED.contains(name)) {
      throw new SAXNotSupportedException(name);
    }
    features.put(name, newBoolean(value));
  }

  public boolean getFeature(String name) throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
    if (!Features.RECOGNIZED.contains(name)) {
      throw new SAXNotRecognizedException(name);
    }
    if (!Features.SUPPORTED.contains(name)) {
      throw new SAXNotSupportedException(name);
    }
    Boolean b = (Boolean) features.get(name);
    return (b == null) ? false : b.booleanValue();
  }

  public SAXParser newSAXParser() throws SAXException, ParserConfigurationException {
    try {
      com.sap.engine.lib.xml.parser.SAXParser parser = new com.sap.engine.lib.xml.parser.SAXParser();

      for (Enumeration e = features.keys(); e.hasMoreElements();) {
        String s = (String) e.nextElement();
        try {
          parser.setFeature(s, ((Boolean) features.get(s)).booleanValue());
        } catch (SAXException ex1) {
          throw ex1;
        }
      } 

      return new com.sap.engine.lib.jaxp.SAXParserImpl(parser);
    } catch (Exception ex2) {
      throw new NestedParserConfigurationException(ex2);
    }
  }

  public void setValidating(boolean f) {
    super.setValidating(f);
    try {
      this.setFeature(Features.FEATURE_VALIDATION, f);
    } catch (Exception e) {
      //$JL-EXC$
      // Shouldn't happen
      throw new RuntimeException(e.toString());
    }
  }

  public void setNamespaceAware(boolean f) {
    super.setNamespaceAware(f);
    try {
      this.setFeature(Features.FEATURE_NAMESPACES, f);
    } catch (Exception e) {
      //$JL-EXC$
      // Shouldn't happen
      throw new RuntimeException(e.toString());
    }
  }

  private Boolean newBoolean(boolean b) {
    return b ? Boolean.TRUE : Boolean.FALSE;
  }

}

