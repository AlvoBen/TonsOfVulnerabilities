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

import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.binary.BinaryXmlReader;
import com.sap.engine.lib.xml.parser.binary.common.MappingData;
import com.sap.engine.lib.xml.parser.binary.handlers.BinaryNamespaceHandler;
import com.sap.engine.lib.xml.parser.binary.exceptions.BinaryXmlException;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.util.NS;
import com.sap.engine.lib.xsl.xslt.InternalAttributeList;
import com.sap.engine.lib.util.ArrayInt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * BXML implementation of XMLTokenReader interface
 * 
 * @author Vladimir Videlov
 * @version 7.10 
 */
public final class XMLBinaryTokenReader implements XMLTokenReader {
  private BinaryXmlReader reader;

  private int currentState;

  private CharArray charData = new CharArray();
  //private CharArray prevParent = null;

  private AttributesImpl attris = new AttributesImpl();

  private Stack parentNodes = new Stack();

  private boolean skipReading = false;

  public XMLBinaryTokenReader() {
    currentState = NOT_INITIAL;
  }

  public XMLBinaryTokenReader(InputStream input) throws IOException {
    this();
    try {
      reader = new BinaryXmlReader(input);
    } catch (BinaryXmlException e) {
      throw new IOException("XML Binary Stream Issue ! "+e.getMessage());
    }
  }

  public int begin() throws ParserException {
    currentState = INITIAL;

    charData.clear();
    parentNodes.clear();

    //prevParent = null;
    skipReading = false;

    return currentState;
  }

  public void end() throws ParserException {
    if (currentState != EOF) {
      throw new ParserException("EOF not reached", 0, 0);
    }
  }

  public void init(InputStream input) {
    currentState = NOT_INITIAL;

    try {
      if (reader == null) {
        reader = new BinaryXmlReader(input);
      } else {
        reader.reuse(input);
      }
    } catch (BinaryXmlException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public int next() throws ParserException {
    try {
      boolean reserved = true;

      while (reserved) {
        if (skipReading || reader.read()) {
          skipReading = false;

          switch (reader.getNodeType()) {
            case Node.ELEMENT_NODE: {
              if (reader.isStartElement) {
                charData.setSize(0);
                currentState = STARTELEMENT;
                //parentNodes.push(prevParent);
                //prevParent = reader.getQName0();
              } else {
                currentState = ENDELEMENT;
                //parentNodes.pop();
              }

              reserved = false;
              break;
            }
            case Node.TEXT_NODE: currentState = CHARS; reserved = false; break;
            case Node.COMMENT_NODE: currentState = COMMENT; reserved = false; break;
          }
        } else {
          currentState = EOF;
          break;
        }
      }
    } catch (Exception ex) {
      throw new ParserException(ex);
    }

    return currentState;
  }

  public String getPrefixMapping(String prefix) {
    return reader.lookupNamespace(prefix);
  }

  public String getValue() {
    return reader.getTextValue();
  }
  
  public Attributes getAttributes() {
    if (this.currentState != STARTELEMENT) {
      return null;
    }

    try {
      if (reader.getNodeType() == Node.ELEMENT_NODE) {
        attris.clear();

        while (reader.read()) {
          if (reader.getNodeType() == Node.ATTRIBUTE_NODE) {
            attris.addAttribute(reader.getNamespaceURI(), reader.getLocalName(), reader.getQName(), "CDATA", reader.getTextValue());
          } else if (skipNextReading()) {
            break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return attris;
  }

  public InternalAttributeList getInternalAttributeList() {
    if (this.currentState != STARTELEMENT) {
      return null;
    }

    InternalAttributeList attributes = new InternalAttributeList();

    try {
      while (reader.read()) {
        if (reader.getNodeType() == Node.ATTRIBUTE_NODE) {
          attributes.addAttribute(reader.getNamespaceURI0(), reader.getPrefix0(), reader.getQName0(), reader.getLocalName0(), reader.getTextValue0());
        } else if (skipNextReading()) {
          break;
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return attributes;
  }

  public String getURI() {
    return reader.getNamespaceURI();
  }

  public String getLocalName() {
    return reader.getLocalName();
  }

  public String getQName() {
    return reader.getQName();
  }

  public CharArray getURICharArray() {
    return reader.getNamespaceURI0();
  }

  public CharArray getLocalNameCharArray() {
    return reader.getLocalName0();
  }

  public CharArray getQNameCharArray() {
    return reader.getQName0();
  }

  public CharArray getValueCharArray() {
    return reader.getTextValue0();
  }

  public int getState() {
    return currentState;
  }

  public boolean isWhitespace() {
    return (reader.getTextValue0() == null || reader.getTextValue0().isWhitespace());
  }

  public Element getDOMRepresentation(Document document) throws ParserException {
    if (this.getState() != STARTELEMENT) {
      return null;
    }

    return fillElement(document.createElementNS(this.getURI(), this.getQName()));
  }

  protected Element fillElement(Element element) throws ParserException {
    String attribUri, attribQName, attribValue;

    Attributes attributes = getAttributes();

    int len = attributes.getLength();

    for (int i = 0; i < len; i++) {
      attribUri = attributes.getURI(i);
      attribQName = attributes.getQName(i);
      attribValue = attributes.getValue(i);

      if (attribQName.indexOf(':') != -1) {
        element.setAttributeNS(attribUri, attribQName, attribValue);
      } else {
        element.setAttribute(attribQName, attribValue);
      }
    }

    int state;

    while (true) {
      state = this.next();

      switch (state) {
        case COMMENT: {
          element.appendChild(element.getOwnerDocument().createComment(getValue()));
          break;
        }
        case CHARS: {
          element.appendChild(element.getOwnerDocument().createTextNode(getValue()));
          break;
        }
        case STARTELEMENT: {
          Element child = element.getOwnerDocument().createElementNS(this.getURI(), this.getQName());
          element.appendChild(fillElement(child));
          break;
        }
        case ENDELEMENT: {
          return element;
        }
        case EOF: {
          throw new ParserException("Unexpexted EOF",0,0);
        }
      }
    }
  }

  public int moveToNextElementStart() throws ParserException {
    while (true) {
      next();
      if (currentState == STARTELEMENT || currentState == EOF) {
        break;
      }
    }

    return currentState;
  }

  public void passChars() throws ParserException {
    while (currentState == CHARS) {
      next();
      //skipNextReading();
    }
  }

  public CharArray getValuePassCharsCA() throws ParserException {
    while (currentState == CHARS) {
      charData.append(reader.getTextValue0());
      next();
      //skipNextReading();
    }

    return charData;
  }

  public String getValuePassChars() throws ParserException {
    return getValuePassCharsCA().getString();
  }

  public String getParentElement() {
    String result = null;

    if (!parentNodes.isEmpty()) {
     result = ((CharArray) parentNodes.peek()).getStringFast();
    }

    return result;
  }

  
  public List<String[]> getEndedPrefixMappings() {
    throw new UnsupportedOperationException();
  }

  public Map<String, String> getNamespaceMappings() {
    throw new UnsupportedOperationException();
  }

  public List<String> getPrefixesOnLastStartElement() {
    throw new UnsupportedOperationException();
  }

  private boolean skipNextReading() {
    switch (reader.getNodeType()) {
      case Node.ELEMENT_NODE: case Node.TEXT_NODE: case Node.COMMENT_NODE: skipReading = true; break;
      default: skipReading = false; break;
    }

    return skipReading;
  }

  public int getCurrentLevel() {
    return reader.getCurrentLevel();
  }

  public void setEntityResolver(EntityResolver resolver) {

  }

  public void writeTo(Writer writer) throws ParserException, IOException {
    if (this.getState() != CHARS && this.getState() != STARTELEMENT) {
      throw new ParserException("Invalid reader state", 0, 0);
    }

    if (this.getState() == CHARS) { //read the chars and return
      writeCharsOrComment(writer);
      writer.flush();
    } else { // This is start element
      // Map all current prefix into effPrefMapsAttr table
      Hashtable effPrefMapsAttr = new Hashtable();

      BinaryNamespaceHandler nsHandler = reader.getNamespaceHandler();
      ArrayInt mappingIDs = nsHandler.getLevelMappingIDs();
      MappingData mapData;

      for (int i = 0; i < mappingIDs.size(); i++) {
        mapData = nsHandler.getMappingData(mappingIDs.get(i));

        if (mapData.uri.length() != 0) {
          if (mapData.prefix.length() == 0) {
            effPrefMapsAttr.put("xmlns", mapData.uri);
          } else {
            effPrefMapsAttr.put("xmlns:" + mapData.prefix, mapData.uri);
          }
        }
      }

      // Writes first start element
      writer.write('<');
      CharArray charArr = this.getQNameCharArray();
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write(' ');

      // Write the attributes
      Attributes atts = this.getAttributes();

      for (int i = 0; i < atts.getLength(); i++) {
        if (!NS.XMLNS.equals(atts.getURI(i)) && !"xmlns".equals(atts.getLocalName(i))) { // Do not write ns declarations, they are written later
          writer.write(atts.getQName(i));
          writer.write("=\'");
          writer.write(atts.getValue(i));
          writer.write("\' ");
        } else { // Apply the current element pref declaration over the declaration decared earlier
          effPrefMapsAttr.put(atts.getQName(i), atts.getValue(i));
        }
      }

      // Write effective prefixes
      String prefD;
      Enumeration en = effPrefMapsAttr.keys();

      while (en.hasMoreElements()) {
        prefD = (String) en.nextElement();
        writer.write(prefD + "='");
        writer.write((String) effPrefMapsAttr.get(prefD));
        writer.write("' ");
      }

      writer.write('>');
      int level = 0; // Counts start and end elements
      int code;

      while (true) {
        code = this.next();

        switch (code) {
          case COMMENT: {
            writeCharsOrComment(writer);
            continue;
          }
          case CHARS: {
            writeCharsOrComment(writer);
            continue;
          }
          case STARTELEMENT: {
            writeStartEndElement(writer);
            level++;
            continue;
          }
          case ENDELEMENT: {
            writeStartEndElement(writer);
            if (level == 0) { // This is the last end element
              writer.flush();
              return;
            }
            level--;
            continue;
          }
          case EOF: {
            throw new ParserException("Unexpexted EOF" , 0, 0);
          }
        }
      }
    }    
  }

  private void writeCharsOrComment(Writer writer) throws IOException {
    CharArray charData = getValueCharArray();

    if (this.getState() == COMMENT) {
      writer.write("<!--");
      writer.write(charData.getData(), charData.getOffset(), charData.getSize());
      writer.write("-->");
    } else { // This is char data
      writer.write(charData.getData(), charData.getOffset(), charData.getSize());
    }
  }

  private void writeStartEndElement(Writer writer) throws IOException {
    CharArray charData;

    // Write start element
    if (this.getState() == STARTELEMENT) {
      writer.write('<');

      charData = this.getQNameCharArray();

      writer.write(charData.getData(), charData.getOffset(), charData.getSize());
      writer.write(' ');

      Attributes atts = this.getAttributes();

      for (int i = 0; i < atts.getLength(); i++) {
        writer.write(atts.getQName(i));
        writer.write("=\'");
        writer.write(atts.getValue(i));
        writer.write("\' ");
      }

      writer.write('>');
    } else { // This is end element
      writer.write("</");

      charData = this.getQNameCharArray();

      writer.write(charData.getData(), charData.getOffset(), charData.getSize());
      writer.write('>');
    }
  }
  
  

  public void setProperty(String key, Object value) {
    // TODO Auto-generated method stub
    
  }

  public void init(InputSource input) {
    // TODO Auto-generated method stub
    
  }

  public void init(Reader reader) {
    // TODO Auto-generated method stub
    
  }
}