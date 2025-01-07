/*
 * Copyright (c) 2006 by SAP Labs Bulgaria.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.lib.xml.parser.tokenizer;

import com.sap.engine.lib.xml.parser.binary.BinaryXmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Locale;

/**
 * BXML implemantation of XMLTokenWriter interface
 * 
 * @author Vladimir Videlov
 * @version 7.10
 */
public final class XMLBinaryTokenWriter implements XMLTokenWriter {
  private BinaryXmlWriter writer;

  public XMLBinaryTokenWriter() {
    writer = null;
  }

  public XMLBinaryTokenWriter(OutputStream output) throws IOException {
    writer = new BinaryXmlWriter(output);
    writer.init();
  }

  public void init(OutputStream output) throws IOException {
    if (writer == null) {
      writer = new BinaryXmlWriter(output);
      writer.init();
    } else {
      writer.reuse(output);
    }
  }

  public void init(OutputStream output, Hashtable defaultPrefixes) throws IOException {
    if (writer == null) {
      writer = new BinaryXmlWriter(output);
      writer.init();
    } else {
      writer.reuse(output);
    }
  }

  public void init(OutputStream output, String encoding) throws IOException {
    this.init(output);

    if (!encoding.toLowerCase(Locale.ENGLISH).equals("utf-8")) {
      throw new IllegalArgumentException("BXML writer supports only UTF-8 encoding");
    }
  }

  public void init(OutputStream output, String encoding, Hashtable defaultPrefixes) throws IOException {
    this.init(output);

    if (!encoding.toLowerCase(Locale.ENGLISH).equals("utf-8")) {
      throw new IllegalArgumentException("BXML writer supports only UTF-8 encoding");
    }
  }  
  
  public void appendNamespaces(Hashtable hash) {

  }

  public void writeInitial() throws IOException {

  }

  public void enter(String namespace, String localName) throws IOException {
    try {
      writer.writeStartElement(localName, namespace);
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  public void leave() throws IOException, IllegalStateException {
    try {
      writer.writeEndElement();
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  public void flush() throws IOException {
    writer.flush();
  }

  public void close() throws IOException {
    writer.close();
  }

  public String getPrefixForNamespace(String namespace) throws IOException, IllegalStateException {
    return writer.lookupNamespace(namespace);
  }

  public void setPrefixForNamespace(String prefix, String namespace) throws IOException, IllegalStateException {
    try {
      writer.addNamespaceMapping(prefix, namespace);
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  public void writeAttribute(String namespace, String name, String value) throws IOException, IllegalStateException {
    try {
      writer.writeAttribute(name, namespace, value);
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  public void writeContent(String content) throws IOException {
    try {
      writer.writeText(content);
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  public void writeContentCData(char[] chars) throws IOException {
    writeContentCData(chars, 0, chars.length);
  }

  public void writeContentCData(char[] chars, int offset, int count) throws IOException {
    writer.writeText(chars, offset, count);
  }

  public void writeContentCDataDirect(char[] chars) throws IOException {
    writeContentCDataDirect(chars, 0, chars.length);
  }

  public void writeContentCDataDirect(char[] chars, int offset, int count) throws IOException {
    //writer.encodeOutput = false;
    writer.writeText(chars, offset, count);
    //writer.encodeOutput = true;
  }

  public void writeComment(String comment) throws IOException {
      writer.writeComment(comment);
    }

  public void writeXmlAttribute(String name, String value) throws IOException, IllegalStateException {
    try {
      writer.writeAttribute(name, value);
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  public void setAttributeHandler(AttributeHandler handler) {

  }

  public void setProperty(String key, Object value) {
    // TODO Auto-generated method stub
    
  }
}
