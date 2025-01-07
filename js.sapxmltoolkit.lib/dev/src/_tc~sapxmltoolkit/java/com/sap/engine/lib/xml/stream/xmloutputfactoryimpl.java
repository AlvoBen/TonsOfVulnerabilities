/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xml.stream;

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

/**
 * @author Dimitar Velichkov (I033362) dimitar.velichkov@sap.com
 */
public class XMLOutputFactoryImpl extends XMLOutputFactory {

  public static final String IS_REPAIRING_NAMESPACES = "javax.xml.stream.isRepairingNamespaces";
  
  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLStreamWriter(java.io.Writer)
   */
  @Override
  public XMLStreamWriter createXMLStreamWriter(Writer w) throws XMLStreamException {         
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLStreamWriter(java.io.OutputStream)
   */
  @Override
  public XMLStreamWriter createXMLStreamWriter(OutputStream str) throws XMLStreamException {
    if(str == null){
      throw new XMLStreamException("Cannot create streaming writer with null stream!");
    }
    
    return new XMLStreamWriterImpl(str);    
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLStreamWriter(java.io.OutputStream, java.lang.String)
   */
  @Override
  public XMLStreamWriter createXMLStreamWriter(OutputStream str, String enc) throws XMLStreamException {
    if(str == null){
      throw new XMLStreamException("Cannot create streaming writer with null stream!");
    }
    return new XMLStreamWriterImpl(str, enc);    
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLStreamWriter(javax.xml.transform.Result)
   */
  @Override
  public XMLStreamWriter createXMLStreamWriter(Result arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLEventWriter(javax.xml.transform.Result)
   */
  @Override
  public XMLEventWriter createXMLEventWriter(Result arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLEventWriter(java.io.OutputStream)
   */
  @Override
  public XMLEventWriter createXMLEventWriter(OutputStream arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLEventWriter(java.io.OutputStream, java.lang.String)
   */
  @Override
  public XMLEventWriter createXMLEventWriter(OutputStream arg0, String arg1) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#createXMLEventWriter(java.io.Writer)
   */
  @Override
  public XMLEventWriter createXMLEventWriter(Writer arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#setProperty(java.lang.String, java.lang.Object)
   */
  @Override
  public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#getProperty(java.lang.String)
   */
  @Override
  public Object getProperty(String prop) throws IllegalArgumentException {
   
    if(prop == null || !prop.equals(IS_REPAIRING_NAMESPACES)){
      throw new IllegalArgumentException("Property " + prop + " is not supported!");
    }
    
    //TokenWriter always 'repairs' namespaces  
    return new Boolean(true);
    
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLOutputFactory#isPropertySupported(java.lang.String)
   */
  @Override
  public boolean isPropertySupported(String prop) {
       
    return (prop != null && prop.equals(IS_REPAIRING_NAMESPACES)) ? new Boolean(true) : new Boolean(false);
    
  }

}
