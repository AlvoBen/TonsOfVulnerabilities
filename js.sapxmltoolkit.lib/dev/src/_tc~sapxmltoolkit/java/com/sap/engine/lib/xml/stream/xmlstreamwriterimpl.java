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

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenWriter;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenWriterFactory;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenWriterImpl;

/**
 * @author Dimitar Velichkov (I033362) dimitar.velichkov@sap.com
 */
public class XMLStreamWriterImpl implements XMLStreamWriter {
  private XMLTokenWriter tw;
  boolean isEmptyStarted = false;
  
  public XMLStreamWriterImpl(OutputStream outStr) {
    try 
    {
      XMLTokenWriterFactory factory = XMLTokenWriterFactory.getInstance();
      tw = factory.createWriter();
      ((XMLTokenWriterImpl) tw).DELIMETER_CHAR = '\"'; //Do this because of SDO. Currenly they are the only one who use this constructor.
      tw.init(outStr);
    } catch (IOException ioE) {
      throw new RuntimeException(ioE);
    }
    
  }

  public XMLStreamWriterImpl(OutputStream outStr, String encoding) {
    try {
      XMLTokenWriterFactory factory = XMLTokenWriterFactory.getInstance();
      tw = factory.createWriter();
      ((XMLTokenWriterImpl) tw).DELIMETER_CHAR = '\"'; //Do this because of SDO. Currenly they are the only one who use this constructor.
      tw.init(outStr, encoding);
    } catch (IOException ioE) {
      throw new RuntimeException(ioE);
    }
  }
//  /**
//   * Constructs instances with already in use writer. That's why 
//   * the writer's init() method is not called.
//   * @param tWrt
//   */
//  public XMLStreamWriterImpl(XMLTokenWriterImpl tWrt) {
//    this.tw = tWrt;
//  }

  public XMLStreamWriterImpl(XMLTokenWriter tWrt) {
    this.tw = tWrt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String)
   */
  public void writeStartElement(String localName) throws XMLStreamException {
    closeEmptyElement();
    try {
      tw.enter(null, localName);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String,
   *      java.lang.String)
   */
  public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
    closeEmptyElement();
    try {
      tw.enter(namespaceURI, localName);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
    closeEmptyElement();
   try{
     tw.enter(namespaceURI, localName);    
     if(namespaceURI != null && namespaceURI.length() > 0 && prefix != null) {
       if (! prefix.equals(tw.getPrefixForNamespace(namespaceURI))) { //remap prefix only if it is necessary
         tw.setPrefixForNamespace(prefix, namespaceURI);  
       }
     }               
   }catch (IOException e){
     throw new XMLStreamExceptionExt(e);
   }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String,
   *      java.lang.String)
   */
  public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
    writeStartElement(namespaceURI, localName);
    this.isEmptyStarted = true;
    //writeEndElement();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void writeEmptyElement(String prefix, String namespaceURI, String localName) throws XMLStreamException {
    writeStartElement(prefix, namespaceURI, localName);
    this.isEmptyStarted = true;
//    writeEndElement();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String)
   */
  public void writeEmptyElement(String localName) throws XMLStreamException {
    writeStartElement(localName);
    this.isEmptyStarted = true;
//    writeEndElement();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeEndElement()
   */
  public void writeEndElement() throws XMLStreamException {
    this.closeEmptyElement();
    try {
      tw.leave();
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeEndDocument()
   */
  public void writeEndDocument() throws XMLStreamException {
    closeEmptyElement();
    try {
      tw.close();
    } catch (IOException e) {
     throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#close()
   */
  public void close() throws XMLStreamException {
    try {
      tw.close();
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#flush()
   */
  public void flush() throws XMLStreamException {
    try {
      tw.flush();
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String,
   *      java.lang.String)
   */
  public void writeAttribute(String localName, String value) throws XMLStreamException {
    try {
      tw.writeAttribute(null, localName, value);   
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
    try {
      tw.setPrefixForNamespace(prefix, namespaceURI);
      tw.writeAttribute(namespaceURI, localName, value);    
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
    try {
      tw.writeAttribute(namespaceURI, localName, value);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeNamespace(java.lang.String,
   *      java.lang.String)
   */
  public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
    if (arg0 == null || "xmlns".equals(arg0) || "".equals(arg0)) {
      writeDefaultNamespace(arg1);
    }
    try {
      tw.setPrefixForNamespace(arg0, arg1);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeDefaultNamespace(java.lang.String)
   */
  public void writeDefaultNamespace(String arg0) throws XMLStreamException {
    try {
      tw.setPrefixForNamespace("", arg0);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeComment(java.lang.String)
   */
  public void writeComment(String comment) throws XMLStreamException {
    closeEmptyElement();
    try {
      tw.writeComment(comment);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String)
   */
  public void writeProcessingInstruction(String arg0) throws XMLStreamException {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String,
   *      java.lang.String)
   */
  public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
    // TODO Auto-generated method stub
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeCData(java.lang.String)
   */
  public void writeCData(String arg0) throws XMLStreamException {
    closeEmptyElement();
    try {
      tw.writeContentCData(arg0.toCharArray());
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeDTD(java.lang.String)
   */
  public void writeDTD(String arg0) throws XMLStreamException {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeEntityRef(java.lang.String)
   */
  public void writeEntityRef(String arg0) throws XMLStreamException {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeStartDocument()
   */
  public void writeStartDocument() throws XMLStreamException {
    try {
      tw.writeInitial();
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String)
   */
  public void writeStartDocument(String version) throws XMLStreamException {
    if (version != null && !version.equals("1.0")) {
      throw new XMLStreamExceptionExt("Only XML version 1.0 is currently supported");
    }

    try {
      tw.writeInitial();
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String,
   *      java.lang.String)
   */
  public void writeStartDocument(String encoding, String version) throws XMLStreamException {
    if (version != null && !version.equals("1.0")) {
      throw new XMLStreamExceptionExt("Only XML version 1.0 is currently supported");
    }
    //tw.init(outStr, encoding);
    try {
      tw.writeInitial();
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeCharacters(java.lang.String)
   */
  public void writeCharacters(String text) throws XMLStreamException {
    closeEmptyElement();
    try {
      tw.writeContent(text);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#writeCharacters(char[], int, int)
   */
  public void writeCharacters(char[] src, int start, int len) throws XMLStreamException {
    closeEmptyElement();
    this.writeCharacters(new String(src, start, len));
//    try {
//      tw.writeContentCData(src, start, len);
//    } catch (IOException e) {
//      throw new XMLStreamExceptionExt(e);
//    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#getPrefix(java.lang.String)
   */
  public String getPrefix(String uri) throws XMLStreamException { 

    String px = null;    
    try {
      px = tw.getPrefixForNamespace(uri);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e); 
    }    
    return px;    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#setPrefix(java.lang.String,
   *      java.lang.String)
   */
  public void setPrefix(String prefix, String uri) throws XMLStreamException {
    
    try {
      tw.setPrefixForNamespace(prefix, uri);
    } catch (IOException e) {
      throw new XMLStreamExceptionExt(e);
    }
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#setDefaultNamespace(java.lang.String)
   */
  public void setDefaultNamespace(String arg0) throws XMLStreamException {
    writeDefaultNamespace(arg0);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#setNamespaceContext(javax.xml.namespace.NamespaceContext)
   */
  public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#getNamespaceContext()
   */
  public NamespaceContext getNamespaceContext() {
    
    
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamWriter#getProperty(java.lang.String)
   */
  public Object getProperty(String arg0) throws IllegalArgumentException {
    // TODO Auto-generated method stub
    
    return null;
  }
  
  private void closeEmptyElement() throws XMLStreamException {
    if (this.isEmptyStarted) {
      try {
        tw.leave();
      } catch (IOException e) {
        throw new XMLStreamExceptionExt(e);
      }
      this.isEmptyStarted = false;
    }
  }
}
