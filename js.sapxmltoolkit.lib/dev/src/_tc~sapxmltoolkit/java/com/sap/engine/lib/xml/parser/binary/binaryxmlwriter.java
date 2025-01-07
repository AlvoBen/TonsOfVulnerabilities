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
package com.sap.engine.lib.xml.parser.binary;

import com.sap.engine.lib.util.HashMapObjectIntPositive;
import com.sap.engine.lib.util.HashMapObjectObject;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.binary.common.Constants;
import com.sap.engine.lib.xml.parser.binary.common.EntryTypes;
import com.sap.engine.lib.xml.parser.binary.handlers.BinaryNamespaceHandler;
import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Encoding;
import com.sap.engine.lib.xml.parser.pool.CharArrayPool;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * BXML Writer
 *
 * @author Vladimir Videlov
 * @version 7.10
 */
public final class BinaryXmlWriter {
  protected BufferedOutputStream stream;

  protected CharArray dataBuffer;
  protected CharArrayPool dataPool;

  protected HashMapObjectIntPositive hashData;
  protected HashMapObjectObject bufferData;

  protected BinaryNamespaceHandler nsHandler;

  protected int currentID;
  protected int prefixID;

  private EncodingHandler encodingHandler = new EncodingHandler();
  private Encoding encoding = encodingHandler.getEncoding(Constants.BXML_ENCODING);

  private ByteArrayOutputStream buffStream = new ByteArrayOutputStream();
  private byte encodingBuff[] = new byte[10];

  public BinaryXmlWriter(OutputStream output) throws IOException {
    stream = new BufferedOutputStream(output);

    init();

    // write binary xml header
    if (output != null) {
      writeBinaryHeader();
    }
  }

  public void init() {
    dataBuffer = new CharArray(500, 500);
    dataPool = new CharArrayPool(100, 100);

    hashData = new HashMapObjectIntPositive();
    bufferData = new HashMapObjectObject();

    nsHandler = new BinaryNamespaceHandler();

    currentID = Constants.START_ID_VALUE - 1;
    prefixID = 0;
  }

  public void clear() {
    dataBuffer.clear();
    dataPool.release();

    hashData.clear();
    bufferData.clear();

    nsHandler.clear();
  }

  public void reuse(OutputStream output) throws IOException {
    stream = new BufferedOutputStream(output);

    //dataBuffer.reuse();
    dataBuffer.setSize(0);
    dataPool.releaseAllObjects();

    hashData.clear();
    bufferData.clear();

    nsHandler.reuse();

    currentID = Constants.START_ID_VALUE - 1;
    prefixID = 0;

    if (output != null) {
      writeBinaryHeader();
    }    
  }

  public void flush() {
    try {
      stream.flush();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void close() {
    try {
      stream.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public String lookupPrefix(String nsURI) {
    return (nsHandler.getPrefix(bufferData(nsURI))).getStringFast();
  }

  public String lookupNamespace(String prefix) {
    return (nsHandler.getURI(bufferData(prefix))).getStringFast();
  }

  public CharArray lookupPrefix(CharArray nsURI) {
    return nsHandler.getPrefix(nsURI).copy();
  }

  public CharArray lookupNamespace(CharArray prefix) {
    return nsHandler.getURI(prefix).copy();
  }

  public BinaryNamespaceHandler getNamespaceHandler() {
    return nsHandler;
  }

  public void addNamespaceMapping(String prefix, String uri) throws IOException {
    addNamespaceMapping(bufferData(prefix), bufferData(uri));
  }

  public void addNamespaceMapping(CharArray prefix, CharArray uri) throws IOException {
    if (!nsHandler.isUriMapped(uri) || !nsHandler.isPrefixMapped(prefix)) {
      nsHandler.add(prefix, uri);
      writeDeclNamespaceEntry(handleStringID(prefix), handleStringID(uri));
    }
  }

  public void writeAttribute(String localName, String value) throws IllegalArgumentException, IOException {
    if (localName == null || value == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    writeAttributeEntry(handleStringID(bufferData(localName)));
    writeTextEntry(value, EntryTypes.AText);
  }

  public void writeAttribute(CharArray localName, CharArray value) throws IllegalArgumentException, IOException {
    if (localName == null || localName.equals("") || value == null) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    writeAttributeEntry(handleStringID(bufferData(localName)));
    writeTextEntry(value, EntryTypes.AText);
  }

  public void writeAttribute(String localName, String nsURI, String value) throws IllegalArgumentException, IOException {
    if (nsURI == null || nsURI.equals("")) {
      writeAttribute(localName, value);
      return;
    }

    if (localName == null || value == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    // handle "xmlns" namespace mapping
    if (XMLParser.sXMLNSNamespace.equals(nsURI)) {
      int lastMID = nsHandler.getLastMappingID();

      if (localName.equals("xmlns")) { // default mapping
        if (lastMID != handleDefaultNamespaceID(bufferData(value), true)) { // new NS mapping
          writeOpenNamespaceEntry(nsHandler.getLastMappingID());
        }
      } else { // already mapped by the node entry
        if (lastMID != handleNamespaceID(bufferData(localName), bufferData(value), true)) { // new NS mapping
          writeOpenNamespaceEntry(nsHandler.getLastMappingID());
        }
      }

      // ignore attribute entry for namespace mapping
      return;
    }

    writeAttributeEntry(handleStringID(bufferData(localName)), handleNamespaceID(bufferData(nsURI), false));
    writeTextEntry(value, EntryTypes.AText);
  }

  public void writeAttribute(CharArray localName, CharArray nsURI, CharArray value) throws IllegalArgumentException, IOException {
    if (nsURI == null || nsURI.equals("")) {
      writeAttribute(localName, value);
      return;
    }

    if (localName == null || value == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    // handle "xmlns" namespace mapping
    if (XMLParser.crXMLNSNamespace.equals(nsURI)) {
      int lastMID = nsHandler.getLastMappingID();

      if (localName.equals("xmlns")) { // default mapping
        if (lastMID != handleDefaultNamespaceID(bufferData(value), true)) { // new NS mapping
          writeOpenNamespaceEntry(nsHandler.getLastMappingID());
        }
      } else { // already mapped by the node entry
        if (lastMID != handleNamespaceID(bufferData(value), true)) { // new NS mapping
          writeOpenNamespaceEntry(nsHandler.getLastMappingID());
        }
      }

      // ignore attribute entry for namespace mapping
      return;
    }

    writeAttributeEntry(handleStringID(bufferData(localName)), handleNamespaceID(bufferData(nsURI), false));
    writeTextEntry(value, EntryTypes.AText);
  }

  public void writeComment(String comment) throws IllegalArgumentException, IOException {
    if (comment == null) {
      throw new IllegalArgumentException("Invalid comment applied");
    }

    writeCommentEntry(comment);
  }

  public void writeComment(CharArray comment) throws IllegalArgumentException, IOException {
    if (comment == null) {
      throw new IllegalArgumentException("Invalid comment applied");
    }

    writeCommentEntry(comment);
  }

  public void writeElementValue(String localName, String value) throws IllegalArgumentException, IOException {
    if (localName == null || value == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    writeElementEntry(handleStringID(bufferData(localName)));
    writeTextEntry(value, EntryTypes.EText);
    writeEndElementEntry();
  }

  public void writeElementValue(CharArray localName, CharArray value) throws IllegalArgumentException, IOException {
    if (localName == null || value == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    writeElementEntry(handleStringID(bufferData(localName)));
    writeTextEntry(value, EntryTypes.EText);
    writeEndElementEntry();
  }

  public void writeElementValue(String localName, String nsURI, String value) throws IllegalArgumentException, IOException {
    if (nsURI == null || nsURI.equals("")) {
      writeElementValue(localName, value);
      return;
    }

    if (localName == null || value == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    nsHandler.levelUp();

    writeElementEntry(handleStringID(bufferData(localName)), handleNamespaceID(bufferData(nsURI), true));
    writeTextEntry(value, EntryTypes.EText);
    writeEndElementEntry();
  }

  public void writeElementValue(CharArray localName, CharArray nsURI, CharArray value) throws IllegalArgumentException, IOException {
    if (nsURI == null || nsURI.equals("")) {
      writeElementValue(localName, value);
      return;
    }

    if (localName == null || value == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid localname or value applied");
    }

    nsHandler.levelUp();

    writeElementEntry(handleStringID(bufferData(localName)), handleNamespaceID(bufferData(nsURI), true));
    writeTextEntry(value, EntryTypes.EText);
    writeEndElementEntry();
  }

  public void writeStartElement(String localName) throws IllegalArgumentException, IOException {
    if (localName == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid local name applied");
    }

    nsHandler.levelUp();

    writeElementEntry(handleStringID(bufferData(localName)));
  }

  public void writeStartElement(CharArray localName) throws IllegalArgumentException, IOException {
    if (localName == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid local name applied");
    }

    nsHandler.levelUp();

    writeElementEntry(handleStringID(bufferData(localName)));
  }

  public void writeStartElement(String localName, String nsURI) throws IllegalArgumentException, IOException {
    if (nsURI == null || nsURI.equals("")) {
      writeStartElement(localName);
      return;
    }

    if (localName == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid local name applied");
    }

    nsHandler.levelUp();

    writeElementEntry(handleStringID(bufferData(localName)), handleNamespaceID(bufferData(nsURI), true));
  }

  public void writeStartElement(CharArray localName, CharArray nsURI) throws IllegalArgumentException, IOException {
    if (nsURI == null || nsURI.equals("")) {
      writeStartElement(localName);
      return;
    }

    if (localName == null || localName.equals("")) {
      throw new IllegalArgumentException("Invalid local name applied");
    }

    nsHandler.levelUp();

    writeElementEntry(handleStringID(bufferData(localName)), handleNamespaceID(bufferData(nsURI), true));
  }

  public void writeEndElement() throws IOException {
    nsHandler.levelDown();

    writeEndElementEntry();
  }

  public void writeText(char[] chars, int offset, int count) throws IOException {
    writeTextEntry(chars, offset, count, EntryTypes.EText);
  }

  public void writeText(String text) throws IOException {
    writeTextEntry(text, EntryTypes.EText);
  }

  public void writeText(CharArray text) throws IOException {
    writeTextEntry(text, EntryTypes.EText);
  }

  public void writeBinary(byte[] data) throws IOException {
    writeBinary(data, 0, data.length);
  }

  public void writeBinary(byte[] data, int start, int count) throws IOException {
    writeBinaryEntry(data, start, count);
  }

  public void writeStringReference(int refID) throws IOException {
    writeRefEntry(refID);
  }

  public void writeProcessingInstruction(String target, String data) throws IllegalArgumentException, IOException {
    if (target == null || target.equals("")) {
      throw new IllegalArgumentException("Invalid processing instruction target applied!");
    }

    writeProcessingInstructionEntry(bufferData(target), bufferData(data));
  }

  public void writeProcessingInstruction(CharArray target, CharArray data) throws IllegalArgumentException, IOException {
    if (target == null || target.equals("")) {
      throw new IllegalArgumentException("Invalid processing instruction target applied!");
    }

    writeProcessingInstructionEntry(bufferData(target), bufferData(data));
  }

  private void writeEncodedID(int id) throws IOException {
    int len;

    if ((len = encoding.reverseEncode(encodingBuff, id)) == Encoding.UNSUPPORTED_CHAR) {
      throw new IOException("Unable to encode ID in UTF-8");
    } else {
      stream.write(encodingBuff, 0, len);
    }
  }

  private void writeBytes(byte[] bytes) throws IOException {
    writeBytes(bytes, 0, bytes.length);
  }

  private void writeBytes(byte[] bytes, int index, int count) throws IOException {
    stream.write(bytes, index, count);
  }

  private void writeBinaryHeader() throws IOException {
    // write binary xml declaration bytes first then headers
    stream.write(Constants.BXML_DECL);

    writeHeaders();
  }

  private void writeHeaders() throws IOException {
    // write the version header
    stream.write(EntryTypes.Header);

    writeCharArray(Constants.BXML_HEADER_VERSION);
    writeCharArray(Constants.BXML_VERSION);

    // write the encoding header
    stream.write(EntryTypes.Header);

    writeCharArray(Constants.BXML_HEADER_ENCODING);
    writeCharArray(Constants.BXML_ENCODING);
  }

  private void writeString(char[] chars, int offset, int count) throws IOException {
    char ch;

    buffStream.reset();

    for (int i = offset; i < chars.length && i < offset + count; i++) {
      ch = chars[i];

      if (ch < 0x80) {
        buffStream.write((byte) ch);
      } else {
        int len = encoding.reverseEncode(encodingBuff, ch);

        if (len == Encoding.UNSUPPORTED_CHAR) {
          throw new IOException("Not supported character applied! Unable to write it to the output stream.");
        } else {
          switch (len) {
            case 1: {
              buffStream.write(encodingBuff[0]);
              break;
            }
            case 2: {
              buffStream.write(encodingBuff[0]);
              buffStream.write(encodingBuff[1]);
              break;
            }
            default: {
              for (int j = 0; j < len; j++) {
                buffStream.write(encodingBuff[j]);
              }
            }
          }
        }
      }
    }

    // write the length plus the encoded data
    writeEncodedID(buffStream.size());
    buffStream.writeTo(stream);
  }

  private void writeString(String value) throws IOException {
    char ch;

    buffStream.reset();

    for (int i = 0; i < value.length(); i++) {
      ch = value.charAt(i);

      if (ch < 0x80) {
        buffStream.write((byte) ch);
      } else {
        int len = encoding.reverseEncode(encodingBuff, ch);

        if (len == Encoding.UNSUPPORTED_CHAR) {
          throw new IOException("Not supported character applied! Unable to write it to the output stream.");
        } else {
          switch (len) {
            case 1: {
              buffStream.write(encodingBuff[0]);
              break;
            }
            case 2: {
              buffStream.write(encodingBuff[0]);
              buffStream.write(encodingBuff[1]);
              break;
            }
            default: {
              for (int j = 0; j < len; j++) {
                buffStream.write(encodingBuff[j]);
              }
            }
          }
        }
      }
    }

    // write the length plus the encoded data
    writeEncodedID(buffStream.size());
    buffStream.writeTo(stream);
  }

  private void writeCharArray(CharArray value) throws IOException {
    char[] data = value.getData();

    int begin = value.getOffset();
    int end = begin + value.getSize();

    char ch;

    buffStream.reset();

    for (int i = begin; i < end; i++) {
      ch = data[i];

      if (ch < 0x80) {
        buffStream.write((byte) ch);
      } else {
        int len = encoding.reverseEncode(encodingBuff, ch);

        if (len == Encoding.UNSUPPORTED_CHAR) {
          throw new IOException("Not supported character applied! Unable to write it to the output stream.");
        } else {
          switch (len) {
            case 1: {
              buffStream.write(encodingBuff[0]);
              break;
            }
            case 2: {
              buffStream.write(encodingBuff[0]);
              buffStream.write(encodingBuff[1]);
              break;
            }
            default: {
              for (int j = 0; j < len; j++) {
                buffStream.write(encodingBuff[j]);
              }
            }
          }
        }
      }
    }

    // write the length plus the encoded data
    writeEncodedID(buffStream.size());
    buffStream.writeTo(stream);
  }

  private void writeStringEntry(CharArray value) throws IOException {
    stream.write(EntryTypes.String);

    writeCharArray(value);
  }

  private void writeTextEntry(char[] chars, int offset, int count, byte type) throws IOException {
    stream.write(type);
    writeString(chars, offset, count);
  }

  private void writeTextEntry(String text, byte type) throws IOException {
    stream.write(type);
    writeString(text);
  }

  private void writeTextEntry(CharArray text, byte type) throws IOException {
    stream.write(type);
    writeCharArray(text);
  }

  private void writeBinaryEntry(byte[] data, int start, int count) throws IOException {
    stream.write(EntryTypes.Binary);

    writeEncodedID(count);
    writeBytes(data, start, count);
  }

  private void writeRefEntry(int refID) throws IOException {
    stream.write(EntryTypes.Ref);

    writeEncodedID(refID);
  }

  private void writeAttributeEntry(int localNameID) throws IOException {
    writeAttributeEntry(localNameID, Constants.NO_NAMESPACE_ID);
  }

  private void writeAttributeEntry(int localNameID, int mappingID) throws IOException {
    stream.write(EntryTypes.Attribute);

    writeEncodedID(localNameID);
    writeEncodedID(mappingID);
  }

  private void writeCommentEntry(CharArray value) throws IOException {
    stream.write(EntryTypes.Comment);

    writeCharArray(value);
  }

  private void writeCommentEntry(String value) throws IOException {
    stream.write(EntryTypes.Comment);

    writeString(value);
  }

  private void writeProcessingInstructionEntry(CharArray target, CharArray data) throws IOException {
    stream.write(EntryTypes.PI);

    writeCharArray(target);
    writeCharArray(data);
  }

  private void writeElementEntry(int localNameID) throws IOException {
    writeElementEntry(localNameID, Constants.NO_NAMESPACE_ID);
  }

  private void writeElementEntry(int localNameID, int mappingID) throws IOException {
    stream.write(EntryTypes.Element);

    writeEncodedID(localNameID);
    writeEncodedID(mappingID);
  }

  private void writeEndElementEntry() throws IOException {
    stream.write(EntryTypes.EndElement);
  }

  private void writeDeclNamespaceEntry(int prefixID, int uriID) throws IOException {
    stream.write(EntryTypes.DeclNamespace);

    writeEncodedID(prefixID);
    writeEncodedID(uriID);
  }

  private void writeOpenNamespaceEntry(int mappingID) throws IOException {
    stream.write(EntryTypes.OpenNamespace);

    writeEncodedID(mappingID);
  }

  private int handleStringID(CharArray data) throws IOException {
    int result;

    if ((result = hashData.get(data)) == -1) {
      result = addHashData(data);
      writeStringEntry(data);
    }

    return result;
  }

  private int handleNamespaceID(CharArray uri, boolean openNS) throws IOException {
    return handleNamespaceID(null, uri, openNS);
  }

  private int handleNamespaceID(CharArray prefix, CharArray uri, boolean openNS) throws IOException {
    int result;

    if (!nsHandler.isUriMapped(uri)) {
      if (prefix == null || prefix.equals("")) {
        prefix = generatePrefix();
      }

      nsHandler.add(prefix, uri);

      result = nsHandler.getLastMappingID();
      int prefixID = handleStringID(prefix);
      int uriID = handleStringID(uri);

      writeDeclNamespaceEntry(prefixID, uriID);

      if (openNS) {
        nsHandler.open(prefix, uri);
      }
    } else {
      result = nsHandler.getMappingID(uri);
    }

    return result;
  }

  private int handleDefaultNamespaceID(CharArray uri, boolean openNS) throws IOException {
    int result;

    if (!nsHandler.isUriMapped(uri)) {
        nsHandler.addDefault(uri);

        result = nsHandler.getLastMappingID();
        int prefixID = handleStringID(nsHandler.defaultPrefixName);
        int uriID = handleStringID(uri);

        writeDeclNamespaceEntry(prefixID, uriID);

        if (openNS) {
          nsHandler.open(nsHandler.defaultPrefixName, uri);
        }
    } else {
      result = nsHandler.getMappingID(uri);
    }

    return result;
  }

  private CharArray generatePrefix() {
    return bufferData("ns" + String.valueOf(prefixID++));
  }

  private int addHashData(CharArray data) {
    hashData.put(data, ++currentID);
    return currentID;
  }

  private CharArray bufferData(String data) {
    CharArray result;

    if ((result = (CharArray) bufferData.get(data)) == null) {
      result = dataPool.getObject();

      int offset = dataBuffer.getSize();
      dataBuffer.append(data);
      result.substring(dataBuffer, offset, offset + data.length());

      result.bufferHash();

      bufferData.put(data, result);
    }

    return result;
  }

  private CharArray bufferData(CharArray data) {
    if (!data.hashReady) {
      data.bufferHash();
    }

    return data;
  }

  /***
  private CharArray bufferData(char[] data, int offset, int count) {
    CharArray result = dataPool.getObject();

    int bufferOffset = dataBuffer.getSize();
    dataBuffer.append(data, offset, count);
    result.substring(dataBuffer, bufferOffset, bufferOffset + data.length);

    result.bufferHash();

    return result;
  }
  /***/
}