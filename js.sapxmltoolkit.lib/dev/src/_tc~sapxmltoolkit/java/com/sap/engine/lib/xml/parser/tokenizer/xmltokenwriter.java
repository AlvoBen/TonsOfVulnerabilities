/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * This is generic interface for writing XMLDocuments incrementally.
 * Output XML in stream in XML tokens.
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public interface XMLTokenWriter {
  
  public static final String INDENT = "indent";
  
  /**
   * Initialize XMLTokenWriter to output into OutputStream.
   * @param output
   */
  public void init(OutputStream output) throws IOException;
  
  /**
   * Initialize XMLTokenWriter to output into OutputStream.
   * @param output
   * @param encoding
   */
  public void init(OutputStream output,String encoding) throws IOException;
  
  /**
   * Initialize XMLTokenWriter to output into OutputStream with hash of predefined prefixes.
   * @param output
   * @param encoding
   * @param defaultPrefixes
   */
  public void init(OutputStream output, String encoding, Hashtable defaultPrefixes) throws IOException;
  
  /**
   * Initialize XMLTokenWriter to output into OutputStream with hash of predefined prefixes.
   * @param output
   * @param defaultPrefixes
   */
  public void init(OutputStream output, Hashtable defaultPrefixes) throws IOException;
  
  /**
   * Adds additional external namespaces after init method.
   * @param hash
   */
  public void appendNamespaces(Hashtable hash);
  
  /**
   * Creates new element and enters into it's content.
   */
  public void enter(String namespace, String localName) throws java.io.IOException;


  /**
   * Leaves current element.
   */
  public void leave() throws java.io.IOException, java.lang.IllegalStateException;


  /**
   * Flushes caches.
   */
  public void flush() throws java.io.IOException;


  /**
   * Returns prefix for given namespace.
   */
  public String getPrefixForNamespace(String namespace) throws java.io.IOException, java.lang.IllegalStateException;


  /**
   * Sets namespace for given prefix.
   */
  public void setPrefixForNamespace(String prefix, String namespace) throws java.io.IOException, java.lang.IllegalStateException;


  /**
   * Adds attribute to current element.
   */
  public void writeAttribute(String namespace, String name, String value) throws java.io.IOException, java.lang.IllegalStateException;


  /**
   * Outputs text node into XML Stream.
   */
  public void writeContent(String content) throws java.io.IOException;


  /**
   * Outputs CData as Characters not exlycitly represented as CData section.
   */
  public void writeContentCData(char[] chars) throws java.io.IOException;


  /**
   * Outputs CData as Characters not exlycitly represented as CData section.
   */
  public void writeContentCData(char[] chars, int offset, int count) throws java.io.IOException;
  /**
   * Outputs the data directly, w/o making any encoding and escaping checks
   */
  public void writeContentCDataDirect(char[] chars) throws java.io.IOException;

  
  /**
   * Outputs the data directly, w/o making any encoding and escaping checks
   */
  public void writeContentCDataDirect(char[] chars, int offset, int count) throws java.io.IOException;


  /**
   * Strites comment into xml stream.
   */
  public void writeComment(String comment) throws java.io.IOException;

  /**
   * Output attribute in xml namespace.
   * @param name
   * @param value
   * @throws java.io.IOException
   * @throws java.lang.IllegalStateException
   */
  public void writeXmlAttribute(String name, String value) throws java.io.IOException, java.lang.IllegalStateException;
  
  /**
   * Sets attribute handler for output attribute rearrange.
   * @param handler
   */
  public void setAttributeHandler(AttributeHandler handler);
  /**
   * Flushes and closes all handlers (encoders, streams, etc). 
   * @throws IOException
   */
  public void close() throws IOException;
  /**
   * Writes XML prolog (<?xml....?>); 
   * @throws IOException
   */
  public void writeInitial() throws IOException;
  
  /**
   * Sets XMLTokenWriter property value.
   * @param key
   * @param value
   */
  public void setProperty(String key, Object value);
  
}

