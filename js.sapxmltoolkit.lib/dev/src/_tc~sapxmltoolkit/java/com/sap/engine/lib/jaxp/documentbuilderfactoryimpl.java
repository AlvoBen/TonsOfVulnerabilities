package com.sap.engine.lib.jaxp;

import javax.xml.parsers.*;

/**
 * SAP DocumentBuilder implementation.
 * @author I024072
 * @deprecated Please use standard JAXP api instead.
 */
@Deprecated
public final class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

	ParserAttributes attributes = new ParserAttributes(); 

  public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
    return new DocumentBuilderImpl(this);
  }

  public void setAttribute(String name, Object value) {
  	attributes.set(name, value);
  }

  public Object getAttribute(String name) {
    return(attributes.get(name));
  }
  
  public boolean getFeature(String name) throws ParserConfigurationException{
  	return false;
  }
  
  public void setFeature(String name, boolean value) throws ParserConfigurationException {
  	
  }
  
  
}

