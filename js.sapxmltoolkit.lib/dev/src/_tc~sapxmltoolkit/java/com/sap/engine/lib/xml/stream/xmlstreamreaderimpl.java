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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReaderFactory;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReaderImpl;

import com.sap.engine.lib.xml.util.NS;

/**
 * @author Dimitar Velichkov (I033362) dimitar.velichkov@sap.com
 */
public class XMLStreamReaderImpl implements XMLStreamReader {

  public static final String NOTATIONS = "javax.xml.stream.notations";
  public static final String ENTITIES = "javax.xml.stream.entities";

  private static final int[][] tokenToStreamMatrix = new int[8][1];
  private static Hashtable<Integer, Integer> streamToTokenMap;

  static {
    tokenToStreamMatrix[XMLTokenReader.INITIAL][0] = XMLStreamReader.START_DOCUMENT;
    tokenToStreamMatrix[XMLTokenReader.EOF][0] = XMLStreamReader.END_DOCUMENT;
    tokenToStreamMatrix[XMLTokenReader.STARTELEMENT][0] = XMLStreamReader.START_ELEMENT;
    tokenToStreamMatrix[XMLTokenReader.ENDELEMENT][0] = XMLStreamReader.END_ELEMENT;
    tokenToStreamMatrix[XMLTokenReader.CHARS][0] = XMLStreamReader.CHARACTERS;
    tokenToStreamMatrix[XMLTokenReader.COMMENT][0] = XMLStreamReader.COMMENT;
    tokenToStreamMatrix[XMLTokenReader.PI][0] = XMLStreamReader.PROCESSING_INSTRUCTION;
    tokenToStreamMatrix[XMLTokenReader.DTD][0] = XMLStreamReader.DTD;

    streamToTokenMap = new Hashtable<Integer, Integer>(15);
    streamToTokenMap.put(XMLStreamReader.ATTRIBUTE, XMLTokenReader.STARTELEMENT);
    streamToTokenMap.put(XMLStreamReader.CDATA, XMLTokenReader.CHARS);
    streamToTokenMap.put(XMLStreamReader.CHARACTERS, XMLTokenReader.CHARS);
    streamToTokenMap.put(XMLStreamReader.COMMENT, XMLTokenReader.COMMENT);
    streamToTokenMap.put(XMLStreamReader.DTD, XMLTokenReader.DTD);
    streamToTokenMap.put(XMLStreamReader.END_DOCUMENT, XMLTokenReader.EOF);
    streamToTokenMap.put(XMLStreamReader.END_ELEMENT, XMLTokenReader.ENDELEMENT);
    streamToTokenMap.put(XMLStreamReader.ENTITY_DECLARATION, XMLTokenReader.CHARS);
    streamToTokenMap.put(XMLStreamReader.ENTITY_REFERENCE, XMLTokenReader.CHARS);
    streamToTokenMap.put(XMLStreamReader.NAMESPACE, XMLTokenReader.STARTELEMENT);
    streamToTokenMap.put(XMLStreamReader.NOTATION_DECLARATION, XMLTokenReader.CHARS);
    streamToTokenMap.put(XMLStreamReader.PROCESSING_INSTRUCTION, XMLTokenReader.PI);
    streamToTokenMap.put(XMLStreamReader.SPACE, XMLTokenReader.CHARS);
    streamToTokenMap.put(XMLStreamReader.START_DOCUMENT, XMLTokenReader.INITIAL);
    streamToTokenMap.put(XMLStreamReader.START_ELEMENT, XMLTokenReader.STARTELEMENT);

  }

  private XMLTokenReader tr = null;
  private QName currentEl = null;;
  private String charData = null;
  private int state;

  // input, only one should be non-null after initializing
  private InputSource inSource;

  private AttributesImpl attrs = new AttributesImpl();

  private int textStart = 0;

  private Map namespaceMap;

  private List prefixesOnLastStartElement;

  private List<String[]> endedPrefixesOnLastEndElement;

  /**
   * Constructs instance with already in use reader.
   * @param tReader
   */
  public XMLStreamReaderImpl(XMLTokenReader tReader) {
    tr = tReader;
    state = tReader.getState();
    if (state == XMLTokenReader.STARTELEMENT) {
      // attrs = tr.getAttributes();
      this.copyAttribsToInternalVariable(tr.getAttributes());
    }

    if (state == XMLTokenReader.STARTELEMENT || state == XMLTokenReader.ENDELEMENT) {
      currentEl = new QName(tr.getURI(), tr.getLocalName());

    }

    if (state == XMLTokenReader.CHARS || state == XMLTokenReader.COMMENT) {
      charData = tr.getValue();
    }
  }

  protected XMLStreamReaderImpl(InputStream inStr) {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();    
    XMLTokenReaderImpl tr = (XMLTokenReaderImpl) factory.createReader(inStr);
    
    tr.setOmmitComments(false);

    try {
      state = tr.begin();
      tr.getParser().bSoapProcessing = false;
    } catch (ParserException e) {
      throw new IllegalStateException(e);
    }
    this.tr = tr;
  }

  protected XMLStreamReaderImpl(Reader rdr) {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();    
    XMLTokenReaderImpl tr = (XMLTokenReaderImpl) factory.createReader(rdr);

    tr.setOmmitComments(false);

    try {
      state = tr.begin();
      tr.getParser().bSoapProcessing = false;
    } catch (ParserException e) {
      throw new IllegalStateException(e);
    }
    this.tr = tr;
  }

  protected XMLStreamReaderImpl(Source source) {

    inSource = new InputSource(source.getSystemId());
    if (source instanceof StreamSource) {

      inSource.setByteStream(((StreamSource) source).getInputStream());
      
      XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
      XMLTokenReaderImpl tr = (XMLTokenReaderImpl) factory.createReader(inSource);
      
      tr.setOmmitComments(false);
      tr.init(inSource.getByteStream());
      try {
        state = tr.begin();
        tr.getParser().bSoapProcessing = false;
      } catch (ParserException e) {
        throw new IllegalStateException(e);
      }
      this.tr = tr;
    } else {

      throw new IllegalArgumentException("Only XMLStreamReader from StreamSource is supported.");
      // Transformer trans =
      // TransformerFactoryImpl.newInstance().newTransformer();
      // ByteArrayOutputStream outStr = new ByteArrayOutputStream();
      // trans.transform(source, outStr);

    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getProperty(java.lang.String)
   */
  public Object getProperty(String prop) throws IllegalArgumentException {

    if (prop == null) {
      throw new IllegalArgumentException("Property name cannot be null!");
    }

    // notation and entities - only for event parser?
    String propValue = null;
    if (state == XMLTokenReader.DTD) {
      if (prop.equals(NOTATIONS)) {

        propValue = null;

      } else if (prop.equals(ENTITIES)) {

        propValue = null;

      }
    }
    return propValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#next()
   */
  public int next() throws XMLStreamException {

    try {
      state = tr.next();
    } catch (ParserException e) {
      throw new XMLStreamExceptionExt(e);

    }

    if (state == XMLTokenReader.EOF) {
      return tokenToStreamMatrix[state][0];
    }

    endedPrefixesOnLastEndElement = null;
    if (state == XMLTokenReader.STARTELEMENT) {
      // attrs = tr.getAttributes();
      this.copyAttribsToInternalVariable(tr.getAttributes());
    }

    if (state == XMLTokenReader.STARTELEMENT || state == XMLTokenReader.ENDELEMENT) {
      currentEl = new QName(tr.getURI(), tr.getLocalName());
      this.namespaceMap = null; // invalidate the internally cached
                                // NamespaceMappings
      prefixesOnLastStartElement = null; // invalidate the cache
    }

    if (state == XMLTokenReader.CHARS || state == XMLTokenReader.COMMENT) {
      charData = tr.getValue();
    }

    return tokenToStreamMatrix[state][0];
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#require(int, java.lang.String,
   * java.lang.String)
   */
  public void require(int type, String localName, String namespaceURI) throws XMLStreamException {

    if (type != streamToTokenMap.get(type)) {
      throw new XMLStreamExceptionExt("The required value 'type' is not matched!");
    }

    if (state == XMLTokenReader.STARTELEMENT || state == XMLTokenReader.ENDELEMENT) {
      if (localName != null && !localName.equals(tr.getLocalName())) {
        throw new XMLStreamExceptionExt("The required value 'localName' is not matched!");
      }
      if (namespaceURI != null && !namespaceURI.equals(tr.getURI())) {
        throw new XMLStreamExceptionExt("The required value 'Namespace URI' is not matched!");
      }

    } else if (localName != null || namespaceURI != null) {
      throw new XMLStreamExceptionExt("Cannot test for localName or NS URI equality if not in STARTELEMENT "
          + " or ENDELEMENT states!");
    }

    // if we get here, everything is matched and we're fine. This method is
    // idiotic, it throws an exception
    // instead of just returning true/false.

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getElementText()
   */
  public String getElementText() throws XMLStreamException {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new XMLStreamExceptionExt("Parser is not at the beginning of an element!");
    }

    String elText = null;
    String elName = tr.getLocalName();
    StringBuffer res = new StringBuffer();

    while (state != XMLTokenReader.ENDELEMENT) {
      try {
        state = tr.next();
      } catch (ParserException e) {
        throw new XMLStreamExceptionExt(e);
      }
      if (state == XMLTokenReader.EOF || state == XMLTokenReader.STARTELEMENT) {
        throw new XMLStreamExceptionExt("Unexpected state reached while reading XML stream, did not find "
            + "closing tag for element " + elName);
      } else if (state == XMLTokenReader.CHARS) {
        elText = tr.getValue();
        res.append(elText);
      }
    }
    return res.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#nextTag()
   */
  public int nextTag() throws XMLStreamException {
    int state = next();

    while (state != XMLStreamReader.START_ELEMENT && state != XMLStreamReader.END_ELEMENT) {
      if (state == END_DOCUMENT) {
        throw new XMLStreamExceptionExt("expected start or end tag");
      }
      state = next();
    }

    return tokenToStreamMatrix[state][0];
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#hasNext()
   */

  public boolean hasNext() throws XMLStreamException {
    return (state != XMLTokenReader.EOF);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#close()
   */
  public void close() throws XMLStreamException {
    try {
      tr.end();
    } catch (ParserException e) {
      throw new XMLStreamExceptionExt(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getNamespaceURI(java.lang.String)
   */
  public String getNamespaceURI(String prefix) {
    if (prefix == null) {
      throw new IllegalArgumentException("Cannot return Namespace URI for null prefix!");
    }
    return tr.getPrefixMapping(prefix);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#isStartElement()
   */
  public boolean isStartElement() {
    return state == XMLTokenReader.STARTELEMENT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#isEndElement()
   */
  public boolean isEndElement() {
    return state == XMLTokenReader.ENDELEMENT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#isCharacters()
   */
  public boolean isCharacters() {
    return state == XMLTokenReader.CHARS;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#isWhiteSpace()
   */
  public boolean isWhiteSpace() {
    return (state == XMLTokenReader.CHARS) && (tr.isWhitespace());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributeValue(java.lang.String,
   * java.lang.String)
   */
  public String getAttributeValue(String namespaceUri, String localName) {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser is not in START_ELEMENT state!");
    }

    namespaceUri = namespaceUri != null ? namespaceUri : "";
    return attrs.getValue(namespaceUri, localName);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributeCount()
   */
  public int getAttributeCount() {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser not in START_ELEMENT state!");
    }
    if (attrs == null) {
      return 0;
    }
    return attrs.getLength();

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributeName(int)
   */
  public QName getAttributeName(int index) {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser not in START_ELEMENT state!");
    }

    String fake = attrs.getQName(index);
    String realUri = fake.indexOf(":") != -1 ? getNamespaceURI(fake.substring(0, fake.indexOf(':'))) : "";
    String realName = fake.indexOf(":") != -1 ? fake.substring(fake.indexOf(':') + 1) : fake;

    return new QName(realUri, realName);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributeNamespace(int)
   */
  public String getAttributeNamespace(int index) {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser not in START_ELEMENT state!");
    }

    return attrs.getURI(index);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributeLocalName(int)
   */
  public String getAttributeLocalName(int index) {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser not in START_ELEMENT state!");
    }

    return attrs.getLocalName(index);

  }

  private String getPrefixForNS(String nsURI) {
    if (nsURI == null || nsURI.length() == 0) {
      return null;
    }

    String prefix = null;
    Map mappings = getNamespaceMappings();

    Iterator allNS = mappings.keySet().iterator();
    while (allNS.hasNext()) {

      String tryThis = (String) allNS.next();
      String mapped = (String) mappings.get(tryThis);
      if (mapped != null && mapped.equals(nsURI)) {
        prefix = tryThis;
        break;
      }
    }
    return prefix;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributePrefix(int)
   */
  public String getAttributePrefix(int index) {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser not in START_ELEMENT state!");
    }

    return getPrefixForNS(attrs.getURI(index));

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributeType(int)
   */
  public String getAttributeType(int index) {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser not in START_ELEMENT state!");
    }

    return attrs.getType(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getAttributeValue(int)
   */
  public String getAttributeValue(int index) {
    if (state != XMLTokenReader.STARTELEMENT) {
      throw new IllegalStateException("Parser not in START_ELEMENT state!");
    }

    return attrs.getValue(index);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#isAttributeSpecified(int)
   */
  public boolean isAttributeSpecified(int index) {
    // FIXME: how do we implement this?
    return false;

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getNamespaceCount()
   */
  public int getNamespaceCount() {
    if (state != XMLTokenReader.STARTELEMENT && state != XMLTokenReader.ENDELEMENT) {
      throw new IllegalStateException("Valid states for this method are STARTELEMENT and ENDELEMENT");
    }
    if (state == XMLTokenReader.STARTELEMENT) {
      return getPrexiesOnLastStartElement().size();
    } else { // this shoudl be end
      return getEndedPrefixMappings().size();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getNamespacePrefix(int)
   */
  public String getNamespacePrefix(int index) {
    if (state != XMLTokenReader.STARTELEMENT && state != XMLTokenReader.ENDELEMENT) {
      throw new IllegalStateException("Cannot get prefix if not in STARTELEMENT or ENDELEMENT");
    }

    if (state == XMLTokenReader.STARTELEMENT) {
      List tmpRef = getPrexiesOnLastStartElement();
      if (index >= tmpRef.size()) {
        throw new ArrayIndexOutOfBoundsException("No prefix defined at position " + index);
      }
      return (String) tmpRef.get(index);
    } else { // this should be end
      List<String[]> tmp = getEndedPrefixMappings();
      if (index >= tmp.size()) {
        throw new ArrayIndexOutOfBoundsException("No prefix defined at position " + index);
      }
      return tmp.get(index)[0]; // index '0' is the prefix
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getNamespaceURI(int)
   */
  public String getNamespaceURI(int index) {

    if (state != XMLTokenReader.STARTELEMENT && state != XMLTokenReader.ENDELEMENT) {
      throw new IllegalStateException("Cannot get namespace if not in STARTELEMENT or ENDELEMENT");
    }

    if (state == XMLTokenReader.STARTELEMENT) {
      List tmpRef = getPrexiesOnLastStartElement();
      if (index >= tmpRef.size()) {
        throw new ArrayIndexOutOfBoundsException("No URI defined at position " + index);
      }
      Map maps = getNamespaceMappings();
      return (String) maps.get(tmpRef.get(index));
    } else { // this should be ENDELEMENT
      List<String[]> tmp = getEndedPrefixMappings();
      if (index >= tmp.size()) {
        throw new ArrayIndexOutOfBoundsException("No URI defined at position " + index);
      }
      return tmp.get(index)[1]; // index[1] should be the namespace
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getNamespaceContext()
   */
  public NamespaceContext getNamespaceContext() {

    final Map namespaces = getNamespaceMappings();

    return new NamespaceContext() {
      public String getNamespaceURI(String prefix) {
        return (String) namespaces.get(prefix);
      };

      public String getPrefix(String namespaceURI) {
        return getPrefixForNS(namespaceURI);
      };

      public Iterator getPrefixes(String namespaceURI) {
        // Hashtable<CharArray, String> maps = iHandler.getNamespaceMappings();
        // ArrayList<String> allKeys = new
        // ArrayList<String>(maps.keySet().size());
        // for (CharArray pfx : maps.keySet()) {
        // allKeys.add(pfx.getString());
        // }
        //
        // return allKeys.iterator();
        ArrayList res = new ArrayList();
        Iterator itr = namespaces.keySet().iterator();
        while (itr.hasNext()) {
          String tmpP = (String) itr.next();
          String value = (String) namespaces.get(tmpP);
          if (value.equals(namespaceURI)) {
            res.add(tmpP);
          }
        }
        return res.iterator();
      };
    };

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getEventType()
   */
  public int getEventType() {
    return tokenToStreamMatrix[tr.getState()][0];
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getText()
   */
  public String getText() {
    if (state != XMLTokenReader.CHARS && state != XMLTokenReader.COMMENT) {
      throw new IllegalStateException("Parser state is not CHARACTERS");
    }
    return charData;

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getTextCharacters()
   */
  public char[] getTextCharacters() {
    if (state != XMLTokenReader.CHARS) {
      throw new IllegalStateException("Parser state is not CHARACTERS");
    }

    // a copy is created inside getValue()
    return tr.getValue().toCharArray();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getTextCharacters(int, char[], int,
   * int)
   */
  public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
    // FIXME: let's do this
    if (targetStart < 0 || targetStart > length) {
      throw new IndexOutOfBoundsException("Start index is greater than length");
    }

    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getTextStart()
   */
  public int getTextStart() {

    textStart = 0;
    // String val = tr.getValue();
    // while (textStart < val.length() &&
    // (Character.isSpaceChar(val.charAt(textStart)) ||
    // Character.isISOControl(val.charAt(textStart)))) {
    // textStart++;
    // }
    //
    // if (textStart == val.length()) {
    // return 0;
    // }

    return textStart;

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getTextLength()
   */
  public int getTextLength() {
    if (state != XMLTokenReader.CHARS) {
      throw new IllegalStateException("Parser state is not CHARACTERS");
    }

    return charData.length() - textStart;

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getEncoding()
   */
  public String getEncoding() {
    if (tr instanceof XMLTokenReaderImpl) {
      return ((XMLTokenReaderImpl) tr).getEncoding();
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#hasText()
   */
  public boolean hasText() {
    return (state == XMLTokenReader.CHARS) || (state == XMLTokenReader.COMMENT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getLocation()
   */
  public Location getLocation() {

    int tmpLineN = -1;
    int tmpColN = -1;
    try {
      if (tr instanceof XMLTokenReaderImpl) {
        XMLTokenReaderImpl trImpl = (XMLTokenReaderImpl) tr;
        tmpLineN = trImpl.getParser().getLineNumber();
        tmpColN = trImpl.getParser().getColumnNumber();
      }
    } catch (ParserException e) {
      return null;
    }

    final String publicId;
    final String sysId;
    if (inSource != null) {
      publicId = inSource.getPublicId();
      sysId = inSource.getSystemId();
    } else {
      publicId = null;
      sysId = null;
    }

    final int LineNo = tmpLineN;
    final int ColNo = tmpColN;

    return new Location() {

      public int getLineNumber() {
        return LineNo;
      };

      public int getColumnNumber() {
        return ColNo;
      }

      public int getCharacterOffset() {
        return -1;
      }

      public String getPublicId() {

        return publicId;
      }

      public String getSystemId() {

        return sysId;
      };
    };

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getName()
   */
  public QName getName() {
    if (state != XMLTokenReader.STARTELEMENT && state != XMLTokenReader.ENDELEMENT) {
      throw new IllegalStateException("Parser is not at the beginning or end of the element!");
    }

    return currentEl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getLocalName()
   */
  public String getLocalName() {
    if (state != XMLTokenReader.STARTELEMENT && state != XMLTokenReader.ENDELEMENT) {
      throw new IllegalStateException("Parser is not at the beginning or end of the element!");
    }

    return tr.getLocalName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#hasName()
   */
  public boolean hasName() {
    return (state == XMLTokenReader.STARTELEMENT) || (state == XMLTokenReader.ENDELEMENT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getNamespaceURI()
   */
  public String getNamespaceURI() {
    if (state != XMLTokenReader.STARTELEMENT && state != XMLTokenReader.ENDELEMENT) {
      throw new IllegalStateException("Parser is not at the beginning or end of the element!");
    }

    return tr.getURI();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getPrefix()
   */
  public String getPrefix() {
    String prefix = null;
    if (state == XMLTokenReader.STARTELEMENT || state == XMLTokenReader.ENDELEMENT) {
      prefix = getPrefixForNS(tr.getURI());
    }

    return prefix;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getVersion()
   */
  public String getVersion() {
    if (tr instanceof XMLTokenReaderImpl) {
      return ((XMLTokenReaderImpl) tr).getXmlVer();
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#isStandalone()
   */
  public boolean isStandalone() {
    // hard-code the default, less-restrictive value
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#standaloneSet()
   */
  public boolean standaloneSet() {
    return isStandalone();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getCharacterEncodingScheme()
   */
  public String getCharacterEncodingScheme() {
    if (tr instanceof XMLTokenReaderImpl) {
      return ((XMLTokenReaderImpl) tr).getEncoding();
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getPITarget()
   */
  public String getPITarget() {
    if (tr instanceof XMLTokenReaderImpl) {
      return ((XMLTokenReaderImpl) tr).getPiTarget();
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.XMLStreamReader#getPIData()
   */
  public String getPIData() {
    if (tr instanceof XMLTokenReaderImpl) {
      return ((XMLTokenReaderImpl) tr).getPiData();
    } else {
      return null;
    }
  }

  private Map getNamespaceMappings() {
    if (this.namespaceMap != null) {
      return this.namespaceMap;
    }
    Map res;
    try {
      res = (Map) this.tr.getNamespaceMappings();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    this.namespaceMap = res;
    return res;
  }

  private List getPrexiesOnLastStartElement() {
    if (prefixesOnLastStartElement != null) {
      return prefixesOnLastStartElement;
    }
    List res;
    try {
      res = (List) this.tr.getPrefixesOnLastStartElement();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    this.prefixesOnLastStartElement = res;
    return res;
  }

  private List<String[]> getEndedPrefixMappings() {
    if (endedPrefixesOnLastEndElement != null) {
      return endedPrefixesOnLastEndElement;
    }
    List res;
    try {
      res = (List) this.tr.getEndedPrefixMappings();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    this.endedPrefixesOnLastEndElement = res;
    return res;
  }

  private void copyAttribsToInternalVariable(Attributes orig) {
    this.attrs.clear();

    if (orig != null) {
      for (int i = 0; i < orig.getLength(); i++) {
        if (!NS.XMLNS.equals(orig.getURI(i))) { // this is not a prefix
                                                // definition
          this.attrs.addAttribute(orig.getURI(i), orig.getLocalName(i), orig.getQName(i), orig.getType(i), orig.getValue(i));
        }
      }
    }
  }
  // private Method getNamespaceMappings;
  // private Method getPrefixesOnLastStartElement;
  // private Method getEndedPrefixMappings;
  //  
  // private void obtainReflectionMethods() {
  // Method m;
  // try {
  // m = this.tr.getClass().getMethod("getNamespaceMappings", null);
  // } catch (NoSuchMethodException mE) {
  // m = null;
  // }
  // if (m == null) {
  // throw new UnsupportedOperationException("XMLTokenReader implementation '" +
  // this.tr + "' does not provide method 'getNamespaceMappings(): Map'");
  // }
  // this.getNamespaceMappings = m;
  //
  // try {
  // m = this.tr.getClass().getMethod("getPrefixesOnLastStartElement", null);
  // } catch (NoSuchMethodException mE) {
  // m = null;
  // }
  // if (m == null) {
  // throw new UnsupportedOperationException("XMLTokenReader implementation '" +
  // this.tr + "' does not provide method 'getPrefixesOnLastStartElement()'");
  // }
  // this.getPrefixesOnLastStartElement = m;
  //    
  // try {
  // m = this.tr.getClass().getMethod("getEndedPrefixMappings", null);
  // } catch (NoSuchMethodException mE) {
  // m = null;
  // }
  // if (m == null) {
  // throw new UnsupportedOperationException("XMLTokenReader implementation '" +
  // this.tr + "' does not provide method 'getEndedPrefixMappings()'");
  // }
  // this.getEndedPrefixMappings = m;
  // }
}