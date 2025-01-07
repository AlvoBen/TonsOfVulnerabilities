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

import com.sap.engine.lib.util.HashMapObjectObject;
import com.sap.engine.lib.util.Stack;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.binary.common.Constants;
import com.sap.engine.lib.xml.parser.binary.common.EntryTypes;
import com.sap.engine.lib.xml.parser.binary.common.MappingData;
import com.sap.engine.lib.xml.parser.binary.exceptions.BinaryXmlException;
import com.sap.engine.lib.xml.parser.binary.handlers.BinaryNamespaceHandler;
import com.sap.engine.lib.xml.parser.binary.pools.MappingDataPool;
import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Encoding;
import com.sap.engine.lib.xml.parser.pool.CharArrayPool;
import org.w3c.dom.Node;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Vector;

/**
 * BXML Reader
 * 
 * @author Vladimir Videlov
 * @version 7.10
 */
public final class BinaryXmlReader {
  protected BufferedInputStream stream;

  protected CharArray dataBuffer;

  protected HashMapObjectObject bufferData;

  protected CharArrayPool dataPool;
  protected MappingDataPool mappingPool;

  protected Vector dataTable = new Vector();

  protected BinaryNamespaceHandler nsHandler;

  private Vector nodeLocalNames;
  private Vector nodeMappings;

  private MappingData nsDeclAttribute;

  public boolean isStartElement;

  protected short nodeType;

  protected CharArray prefix;
  protected CharArray nsURI;
  protected CharArray localName;
  protected Object nodeValue;

  protected final CharArray textLocalName = new CharArray("#text");
  protected final CharArray commentLocalName = new CharArray("#comment");

  private byte entryType;
  private byte valueType;

  private int currentID;

  private EncodingHandler encodingHandler = new EncodingHandler();
  private Encoding encoding = encodingHandler.getEncoding(Constants.BXML_ENCODING);

  private byte encodingBuff[] = new byte[10];

  protected CharArray bxmlVersion;
  protected CharArray bxmlEncoding;

  protected int level;

  public BinaryXmlReader(InputStream input) throws BinaryXmlException, IOException {
    stream = new BufferedInputStream(input);

    init();

    // read binary xml headers
    if (input != null) {
      readBinaryHeader();
    }
  }

  public void init() {
    dataBuffer = new CharArray(500, 500);
    bufferData = new HashMapObjectObject();

    dataPool = new CharArrayPool(100, 100);
    mappingPool = new MappingDataPool(100, 100);

    nsHandler = new BinaryNamespaceHandler();

    nodeLocalNames = new Vector();
    nodeMappings = new Vector();

    currentID = Constants.START_ID_VALUE - 1;

    dataTable.setSize(currentID + 1);
    dataTable.set(currentID, nsHandler.defaultPrefixName);

    level = 0;
  }

  public void clear() {
    dataBuffer.clear();
    bufferData.clear();

    dataPool.release();
    mappingPool.release();

    nsHandler.clear();

    nodeLocalNames.clear();
    nodeMappings.clear();

    dataTable.clear();

    nsDeclAttribute = null;
    level = 0;
  }

  public void reuse(InputStream input) throws BinaryXmlException, IOException {
    stream = new BufferedInputStream(input);

    //dataBuffer.reuse();
    dataBuffer.setSize(0);
    bufferData.clear();

    dataPool.releaseAllObjects();
    mappingPool.releaseAllObjects();

    nsHandler.reuse();

    nodeLocalNames.clear();
    nodeMappings.clear();

    currentID = Constants.START_ID_VALUE - 1;

    // read binary xml headers
    if (input != null) {
      readBinaryHeader();
    }

    nsDeclAttribute = null;
    level = 0;
  }

  public void close() {
    try {
      stream.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public String lookupNamespace(String prefix) {
    CharArray prefix0 = dataPool.getObject();
    prefix0.set(prefix);

    return nsHandler.getURI(prefix0).getStringFast();
  }

  public String lookupPrefix(String uriNS) {
    CharArray uriNS0 = dataPool.getObject();
    uriNS0.set(uriNS);

    return nsHandler.getPrefix(uriNS0).getStringFast();
  }

  public CharArray lookupNamespace(CharArray prefix) {
    return nsHandler.getURI(prefix).copy();
  }

  public CharArray lookupPrefix(CharArray uriNS) {
    return nsHandler.getPrefix(uriNS).copy();
  }

  public BinaryNamespaceHandler getNamespaceHandler() {
    return nsHandler;
  }

  public boolean read() throws IOException, BinaryXmlException {
    if (isStartElement && nsDeclAttribute != null) { // checks if attribute is read
      if (nsDeclAttribute.prefix != null && nsDeclAttribute.prefix != nsHandler.defaultPrefixName) {
        prefix = XMLParser.caXMLNS;
        localName = nsDeclAttribute.prefix;
      } else {
        prefix = null;
        localName = XMLParser.caXMLNS;
      }

      nsURI = XMLParser.crXMLNSNamespace;
      nodeValue = nsDeclAttribute.uri;

      nodeType = Node.ATTRIBUTE_NODE;
      valueType = EntryTypes.CharArray;

      // reset the NS declaration attribute
      nsDeclAttribute = null;

      return true;
    }

    stream.mark(1);
    entryType = -1; // end of processing if read fails

    try {
      entryType = (byte) stream.read(); // for type
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    switch (entryType) {
      case EntryTypes.Element: {
        nodeType = Node.ELEMENT_NODE;
        isStartElement = true;

        readElementEntry();
        return true;
      }
      case EntryTypes.EndElement: {
        nodeType = Node.ELEMENT_NODE;
        isStartElement = false;

        readEndElementEntry();
        return true;
      }
      case EntryTypes.Attribute: {
        nodeType = Node.ATTRIBUTE_NODE;

        readAttributeEntry();
        return true;
      }
      case EntryTypes.EText: {
        stream.reset();

        nodeType = Node.TEXT_NODE;
        isStartElement = false;

        readTextEntry();
        return true;
      }
      case EntryTypes.AText: {
        throw new BinaryXmlException("Incorrect format. Unexpected attribute text value, it must follow on an attribute entry.");
      }
      case EntryTypes.Binary: {
        stream.reset();

        nodeType = Node.TEXT_NODE;
        isStartElement = false;

        readBinaryEntry();
        return true;
      }
      case EntryTypes.Ref: {
        stream.reset();

        nodeType = Node.TEXT_NODE;
        isStartElement = false;

        readRefEntry();
        return true;
      }
      case EntryTypes.Comment: {
        nodeType = Node.COMMENT_NODE;
        isStartElement = false;

        readCommentEntry();
        return true;
      }
      case EntryTypes.PI: {
        nodeType = Node.PROCESSING_INSTRUCTION_NODE;
        isStartElement = false;

        readProcessingInstructionEntry();
        return true;
      }
      case EntryTypes.String: {
        nodeType = -1;

        readStringEntry();
        return true;
      }
      case EntryTypes.DeclNamespace: {
        nodeType = -1;

        readDeclNamespaceEntry();
        return true;
      }
      case EntryTypes.OpenNamespace: {
        nodeType = -1;

        readOpenNamespaceEntry();
        return true;
      }
      case -1: { // end of file
        nodeType = -1;
        return false;
      }
      default: {
        nodeType = -1;

        skipContent();
        return true;
      }
    }
  }

  private void readBinaryHeader() throws IOException, BinaryXmlException {
    // read binary xml declaration bytes first
    stream.mark(4);
    stream.read(encodingBuff, 0, 4);

    if (encodingBuff[0] != Constants.BXML_DECL[0] || encodingBuff[1] != Constants.BXML_DECL[1] ||
        encodingBuff[2] != Constants.BXML_DECL[2] || encodingBuff[3] != Constants.BXML_DECL[3]) {

      stream.reset(); // return the stream at the marked position and throw exception
      throw new BinaryXmlException("Invalid binary XML stream. Header declaration is incorrect.");
    }

    readBinaryHeaders();
  }

  private void readBinaryHeaders() throws IOException, BinaryXmlException {
    while (stream.read() == EntryTypes.Header) {
      CharArray type = readCharArray();

      if (type.equals(Constants.BXML_HEADER_VERSION)) { // version header info
        bxmlVersion = readCharArray();

        if (!bxmlVersion.equalsIgnoreCase(Constants.BXML_VERSION)) {
          throw new BinaryXmlException("Not supported BXML version");
        }
      } else if (type.equals(Constants.BXML_HEADER_ENCODING)) { // encoding header info
        bxmlEncoding = readCharArray();
      }

      stream.mark(1);
    }

    stream.reset();
  }

  private CharArray readCharArray() throws IOException {
    CharArray result = dataPool.getObject();

    int bytesToRead = readEncodedID(); // reads the characters bytes length

    int size = dataBuffer.getSize();
    int length = 0;

    int ch;

    if (bytesToRead > 0) {
      while ((ch = stream.read()) != -1) {
        if ((ch = encoding.process((byte) ch)) != Encoding.NEEDS_MORE_DATA) {
          dataBuffer.append((char) ch);
          length++;
        }

        if (--bytesToRead == 0) {
          break;
        }
      }
    }

    result.substring(dataBuffer, size, size + length);

    return result;
  }

  private long skipContent() throws IOException {
    return stream.skip(readEncodedID());
  }

  private int readEncodedID() throws IOException {
    int data;

    while ((data = stream.read()) != -1) {
      if ((data = encoding.process((byte) data)) != Encoding.NEEDS_MORE_DATA) {
        return data;
      }
    }

    return -1;
  }

  private void readElementEntry() throws IOException {
    int localNameID = readEncodedID();
    int mappingID = readEncodedID();

    //nsHandler.levelUp();

    prefix = null;
    nsURI = null;
    localName = getData(localNameID);

    MappingData mapping;

    if (mappingID != Constants.NO_NAMESPACE_ID) {
      mapping = nsHandler.getMappingData(mappingID);

      prefix = mapping.prefix;
      nsURI = mapping.uri;

      // activates the NS
      if (!nsHandler.isMappingOpened(mappingID)) {
        nsHandler.open(mapping);
      }
    } else { // no ns mapping data
      mapping = mappingPool.getObject(null, null);
    }

    nodeValue = null;
    valueType = EntryTypes.Undefined;

    //nodeMappings.push(mapping);
    //nodeLocalNames.push(localName);

    level++;

    if (level >= nodeMappings.size()) {
      nodeMappings.setSize(level + 1);
      nodeLocalNames.setSize(level + 1);
    }

    nodeMappings.setElementAt(mapping, level);
    nodeLocalNames.setElementAt(localName, level);
  }

  private void readEndElementEntry() {
    prefix = null;
    nsURI = null;
    localName = null;
    nodeValue = null;
    valueType = EntryTypes.Undefined;

    MappingData mapping = (MappingData) nodeMappings.get(level);

    if (mapping != null) {
      prefix = mapping.prefix;
      nsURI = mapping.uri;
    }

    localName = (CharArray) nodeLocalNames.get(level);

    level--;

    //nsHandler.levelDown();
  }

  private void readAttributeEntry() throws IOException, BinaryXmlException {
    int localNameID = readEncodedID();
    int mappingID = readEncodedID();

    prefix = null;
    nsURI = null;
    localName = getData(localNameID);

    nodeValue = readValue(EntryTypes.AText);
    valueType = EntryTypes.CharArray;

    MappingData mapping;

    if (mappingID != Constants.NO_NAMESPACE_ID) {
      mapping = nsHandler.getMappingData(mappingID);

      prefix = mapping.prefix;
      nsURI = mapping.uri;
    }
  }

  private Object readValue(byte expectedTextType) throws IOException, BinaryXmlException {
    Object result = null;

    int size = dataBuffer.getSize();
    int length = 0;
    
    boolean valueCollected = false;

    valueType = EntryTypes.Undefined;
    stream.mark(1);

    while ((entryType = (byte) stream.read()) != -1) {
      switch (entryType) {
        case EntryTypes.EText : {
          if (valueType == EntryTypes.Undefined) {
            if (expectedTextType != EntryTypes.EText) {
              throw new BinaryXmlException("Incorrect text value type. Element text value expected.");
            }
            valueType = EntryTypes.EText;
          } else if (valueType != EntryTypes.EText) {
            if (expectedTextType != EntryTypes.AText) { // attribute value ends
              throw new BinaryXmlException("Mixture of different types in value is forbidden");
            }
            valueCollected = true;
            break;
          }

          length += readCharArray().length();
          break;
        }
        case EntryTypes.AText : {
          if (expectedTextType != EntryTypes.AText) {
            throw new BinaryXmlException("Incorrect text value type. Attribute text value expected.");
          }

          if (valueType == EntryTypes.Undefined) {
            valueType = EntryTypes.AText;
          } else if (valueType != EntryTypes.AText) {
            throw new BinaryXmlException("Mixture of different types in value is forbidden");
          }

          length += readCharArray().length();
          break;
        }
        case EntryTypes.Binary : {
          if (valueType != EntryTypes.Undefined && valueType != EntryTypes.Binary) {
            throw new BinaryXmlException("Mixture of different types in value is forbidden");
          }

          valueType = EntryTypes.Binary;
          length = readEncodedID();
          result = new byte[length];
          stream.read((byte[]) result, 0, length);

          stream.mark(1);
          valueCollected = true;

          break;
        }
        case EntryTypes.Ref : {
          if (valueType != EntryTypes.Undefined) {
            throw new BinaryXmlException("Mixture of different types in value is forbidden");
          }

          valueType = EntryTypes.Ref;
          result = new Integer(readEncodedID());

          break;
        }
        default: {
          valueCollected = true;
          break;
        }
      }

      if (valueCollected) {
        stream.reset();
        break;
      }

      stream.mark(1);
    }

    if (valueType == EntryTypes.EText || valueType == EntryTypes.AText) { // concatinate the result text
      result = dataPool.getObject();
      ((CharArray) result).substring(dataBuffer, size, size + length);
    }

    return result;
  }

  private void readTextEntry() throws IOException, BinaryXmlException {
    prefix = null;
    nsURI = null;
    localName = textLocalName;

    nodeValue = readValue(EntryTypes.EText);

    if (valueType != EntryTypes.EText) {
      throw new BinaryXmlException("Incorrect text entry format. Text value expected.");
    }

    valueType = EntryTypes.CharArray;
  }

  private void readBinaryEntry() throws IOException, BinaryXmlException {
    prefix = null;
    nsURI = null;
    localName = null;

    nodeValue = readValue(EntryTypes.Binary);

    if (valueType != EntryTypes.Binary) {
      throw new BinaryXmlException("Incorrect binary entry format. Binary value expected.");
    }
  }

  private void readRefEntry() throws IOException, BinaryXmlException {
    prefix = null;
    nsURI = null;
    localName = null;

    nodeValue = readValue(EntryTypes.Ref);

    if (valueType != EntryTypes.Ref) {
      throw new BinaryXmlException("Incorrect reference entry format. Reference value expected.");
    }
  }

  private void readCommentEntry() throws IOException {
    prefix = null;
    nsURI = null;
    localName = commentLocalName;

    nodeValue = readCharArray();
    valueType = EntryTypes.CharArray;
  }

  private void readProcessingInstructionEntry() throws IOException {
    prefix = null;
    nsURI = null;

    localName = readCharArray(); // reads target
    nodeValue = readCharArray(); // reads data

    valueType = EntryTypes.CharArray;
  }

  private void readStringEntry() throws IOException {
    nodeValue = readCharArray();
    valueType = EntryTypes.CharArray;

    addTableData((CharArray) nodeValue);

    prefix = null;
    nsURI = null;
    localName = null;
    nodeValue = null;
  }

  private void readDeclNamespaceEntry() throws IOException {
    prefix = getData(readEncodedID());
    nsURI = getData(readEncodedID());

    nsDeclAttribute = nsHandler.add(prefix, nsURI);

    prefix = null;
    nsURI = null;
    localName = null;
    nodeValue = null;

    valueType = EntryTypes.Undefined;
  }

  private void readOpenNamespaceEntry() throws IOException {
    nsHandler.open(readEncodedID());

    prefix = null;
    nsURI = null;
    localName = null;
    nodeValue = null;

    valueType = EntryTypes.Undefined;
  }

  public short getNodeType() {
    return nodeType;
  }

  public byte getValueType() {
    return valueType;
  }

  public String getLocalName() {
    return (localName != null) ? localName.getStringFast() : null;
  }

  public CharArray getLocalName0() {
    return (localName != null) ? localName.copy() : null;
  }

  public String getQName() {
    String result = null;

    if (localName != null) {
      if (prefix != null && prefix != nsHandler.defaultPrefixName) {
        result = prefix.getStringFast() + ":" + localName.getStringFast();
      } else {
        result = localName.getStringFast();
      }
    }

    return result;
  }

  public CharArray getQName0() {
    CharArray result = null;

    if (localName != null) {
      if (prefix != null && prefix != nsHandler.defaultPrefixName) {
        result = new CharArray();
        result.append(prefix);
        result.append(":");
        result.append(localName);
      } else {
        result = localName.copy();
      }
    }

    return result;
  }

  public String getPrefix() {
    return (prefix != null && prefix != nsHandler.defaultPrefixName) ? prefix.getStringFast() : null;
  }

  public CharArray getPrefix0() {
    return (prefix != null && prefix != nsHandler.defaultPrefixName) ? prefix.copy() : null;
  }

  public String getNamespaceURI() {
    return (nsURI != null) ? nsURI.getStringFast() : "";
  }

  public CharArray getNamespaceURI0() {
    return (nsURI != null) ? nsURI.copy() : null;
  }

  public Object getValue() {
    Object result;

    if (nodeValue instanceof CharArray) {
      result = ((CharArray) nodeValue).copy();
    } else {
      result = nodeValue;
    }

    return result;
  }

  public String getTextValue() {
    String result = null;

    if (valueType == EntryTypes.CharArray) {
      result = ((CharArray) nodeValue).getStringFast();
    }

    return result;
  }

  public CharArray getTextValue0() {
    CharArray result = null;

    if (valueType == EntryTypes.CharArray) {
      result = ((CharArray) nodeValue).copy();
    }

    return result;
  }

  protected int addTableData(CharArray data) {
    // increment the ID
    currentID++;

    // adds data to the table
    dataTable.setSize(currentID + 1);
    dataTable.set(currentID, data);

    return currentID;
  }

  protected CharArray getData(int id) {
    return (CharArray) dataTable.get(id);
  }

  protected String bufferData(CharArray data) {
    String result;

    if ((result = (String) bufferData.get(data)) == null) {
      result = data.getStringFast();
      bufferData.put(data, result);
    }

    return result;
  }

  public int getLastStringID() {
    return currentID;
  }

  public int getLastMappingID() {
    return nsHandler.getLastMappingID();
  }

  public int getCurrentLevel() {
    return level;
  }
}