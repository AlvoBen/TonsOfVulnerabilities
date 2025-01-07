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

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

/**
 * @author Dimitar Velichkov (I033362) dimitar.velichkov@sap.com
 */
public class XMLInputFactoryImpl extends XMLInputFactory {
  
  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLStreamReader(java.io.Reader)
   */
  @Override
  public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
    if(reader == null){
      throw new NullPointerException("Error creating XMLStreamReader - Reader is null!");
    }
    
    return new XMLStreamReaderImpl(reader);
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLStreamReader(javax.xml.transform.Source)
   */
  @Override
  public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
    if(source == null){
      throw new NullPointerException("Error creating XMLStreamReader - Source is null!");
    }
    return new XMLStreamReaderImpl(source);
    
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLStreamReader(java.io.InputStream)
   */
  @Override
  public XMLStreamReader createXMLStreamReader(InputStream inStr) throws XMLStreamException {
    if(inStr == null){
      throw new NullPointerException("Error creating XMLStreamReader - InputStream is null!");
    }
    return new XMLStreamReaderImpl(inStr);
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLStreamReader(java.io.InputStream, java.lang.String)
   */
  @Override
  public XMLStreamReader createXMLStreamReader(InputStream inStr, String encoding) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLStreamReader(java.lang.String, java.io.InputStream)
   */
  @Override
  public XMLStreamReader createXMLStreamReader(String systemId, InputStream inStr) throws XMLStreamException {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLStreamReader(java.lang.String, java.io.Reader)
   */
  @Override
  public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLEventReader(java.io.Reader)
   */
  @Override
  public XMLEventReader createXMLEventReader(Reader arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLEventReader(java.lang.String, java.io.Reader)
   */
  @Override
  public XMLEventReader createXMLEventReader(String arg0, Reader arg1) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLEventReader(javax.xml.stream.XMLStreamReader)
   */
  @Override
  public XMLEventReader createXMLEventReader(XMLStreamReader arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLEventReader(javax.xml.transform.Source)
   */
  @Override
  public XMLEventReader createXMLEventReader(Source arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLEventReader(java.io.InputStream)
   */
  @Override
  public XMLEventReader createXMLEventReader(InputStream arg0) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLEventReader(java.io.InputStream, java.lang.String)
   */
  @Override
  public XMLEventReader createXMLEventReader(InputStream arg0, String arg1) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createXMLEventReader(java.lang.String, java.io.InputStream)
   */
  @Override
  public XMLEventReader createXMLEventReader(String arg0, InputStream arg1) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createFilteredReader(javax.xml.stream.XMLStreamReader, javax.xml.stream.StreamFilter)
   */
  @Override
  public XMLStreamReader createFilteredReader(XMLStreamReader arg0, StreamFilter arg1) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#createFilteredReader(javax.xml.stream.XMLEventReader, javax.xml.stream.EventFilter)
   */
  @Override
  public XMLEventReader createFilteredReader(XMLEventReader arg0, EventFilter arg1) throws XMLStreamException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#getXMLResolver()
   */
  @Override
  public XMLResolver getXMLResolver() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#setXMLResolver(javax.xml.stream.XMLResolver)
   */
  @Override
  public void setXMLResolver(XMLResolver arg0) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#getXMLReporter()
   */
  @Override
  public XMLReporter getXMLReporter() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#setXMLReporter(javax.xml.stream.XMLReporter)
   */
  @Override
  public void setXMLReporter(XMLReporter arg0) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#setProperty(java.lang.String, java.lang.Object)
   */
  @Override
  public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#getProperty(java.lang.String)
   */
  @Override
  public Object getProperty(String arg0) throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#isPropertySupported(java.lang.String)
   */
  @Override
  public boolean isPropertySupported(String arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#setEventAllocator(javax.xml.stream.util.XMLEventAllocator)
   */
  @Override
  public void setEventAllocator(XMLEventAllocator arg0) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see javax.xml.stream.XMLInputFactory#getEventAllocator()
   */
  @Override
  public XMLEventAllocator getEventAllocator() {
    // TODO Auto-generated method stub
    return null;
  } 

}
