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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import com.sap.engine.lib.xml.parser.ActiveXMLParser;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.XMLParserConstants;
import com.sap.engine.lib.xml.parser.handlers.EmptyDocHandler;
import com.sap.engine.lib.xml.parser.handlers.INamespaceHandler;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandlerEx;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.CharArrayStack;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;
import com.sap.engine.lib.xml.util.SymbolTable;
import com.sap.engine.lib.xsl.xslt.InternalAttribute;
import com.sap.engine.lib.xsl.xslt.InternalAttributeList;

/**
 * 
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public class XMLTokenReaderImpl extends EmptyDocHandler implements XMLParserConstants, XMLTokenReader  {
  boolean ommitComments = true; // flag to ommit comments, by default it is true
  private boolean shouldFakeEnd = false;
  /* <?xml is allowed only in the first Processing Instruction */
  private boolean textDeclAllowed = true;
  private boolean endOfRootReached = false;
  /* The InputStream from which the XML document is read. */
  private InputStream xmlInputStream = null;
  private InputSource xmlInputSource = null;
  private Reader      xmlReader      = null;
  /* The current state of the reader. */
  private int currentState = XMLTokenReader.NOT_INITIAL;
  /* The parser ... it parses. */
  private XMLParser parser = null;
  /* Hashtable with namespace mappings */
  //private Hashtable prefixMapping;
  private NamespaceHandlerEx namespaceHandler = null;

  private CharArray uri = null;
  private CharArray localName = null;
  private CharArray qName = null;
  /* Contains the current character data. */
  private CharArray charData = new CharArray();
  /* This Vector stores the attributes of the current element. */
  //private AttributesImpl attribs = new AttributesImpl();
  private InternalAttributeList attribs = new InternalAttributeList();
  //private boolean firstElementPassed = false;
  private int depthReached = 0;
  private boolean append = false;
  //private INamespaceHandler prefixMapping = null;
  private String encoding = null;
  private String xmlVer = null;
  private String piData = null;
  private String piTarget = null;
  private SymbolTable symbolTable = new SymbolTable(2 * 1024);
  
  private List<CharArray> prefixesOfEmptyElement;
  private AttributesImpl retAttrs = null;
  
  private Hashtable<CharArray, String> prefixMappings; //key prefix, value ns
  
 // private List<String> prefixesOfLastEndElement = null;
  /**
   * @return Returns the piData.
   */
  public String getPiData() {
    return piData;
  }

  /**
   * @return Returns the piTarget.
   */
  public String getPiTarget() {
    return piTarget;
  }

  public INamespaceHandler getNamespaceHandler() {
    return this.namespaceHandler;
  }
  
  /**
   * Sets ommit comment flag.
   */
  public void setOmmitComments(boolean flag) {
    this.ommitComments = flag;
  }
  
  public boolean getOmmitComments() {
    return this.ommitComments;
  }
  
  /**
   * @deprecated Please use XMLTokenReaderFactory.newInstance()
   * @param xmlInputSource
   */
  public XMLTokenReaderImpl(InputSource xmlInputSource) {
    this();
    this.xmlInputSource = xmlInputSource;
  }
  
  /**
   * @deprecated Please use XMLTokenReaderFactory.newInstance().
   * @param parser
   */
  public XMLTokenReaderImpl(XMLParser parser) {
    this();
    this.parser = parser;

  }
 
  /**
   * @deprecated Please use XMLTokenReaderFactory.newInstance().
   * @param xmlInputStream
   */
  public XMLTokenReaderImpl(InputStream xmlInputStream) {
    this();
    this.xmlInputStream = xmlInputStream;
  }

  /**
   * @deprecated Please use XMLTokenReaderFactory.newInstance().
   * @param xmlReader
   */
  public XMLTokenReaderImpl(Reader xmlReader) {
    this();
    this.xmlReader = xmlReader;
  }

  /**
   * @deprecated please use XMLTokenReaderFactory.newInstance()
   *
   */
  public XMLTokenReaderImpl() {
    //prefixMapping = new Hashtable();
    //prefixMapping = new NamespaceHandlerEx(null);
  }
  
  public void reuse(InputStream stream,InputSource inputSource,Reader reader) {
    this.xmlInputSource = inputSource;
    this.xmlReader = reader;
    this.xmlInputStream = stream;
    this.append = false;
    //prefixMapping.clear();
    depthReached = 0;
    shouldFakeEnd = false;
    textDeclAllowed = true;
    endOfRootReached = false;
    xmlInputSource = null;
    currentState = XMLTokenReader.NOT_INITIAL;
    charData.clear();
  }
  
  public void init(Reader reader) {
    reuse(null,null,reader);
  }
  
  public void init(InputSource inputSource) {
    reuse(null,inputSource,null);
  }

  public void init(InputStream stream) {
    reuse(stream,null,null);
    //LogWriter.getSystemLogWriter().println("XMLTokenREaderImpl new");
  }

  /**
   *  Commences the parsing.
   *
   * @exception   com.sap.engine.lib.xml.parser.ParserException
   */
  public int begin() throws ParserException {
    try {
      parser = getParser();
      namespaceHandler = (NamespaceHandlerEx) parser.getNamespaceHandler();
      this.shouldFakeEnd = false;
      this.endOfRootReached = false;
      if (xmlInputSource == null) {
        if (xmlReader != null) {
          xmlInputSource = new InputSource(xmlReader);
        }
        if (xmlInputStream != null) {
          xmlInputSource = new InputSource(xmlInputStream);
        }

      }
      parser.activeParse(xmlInputSource, this);
      currentState = INITIAL;
      //prefixMapping.clear();
      return currentState;
    } catch (Exception e) {
//      e.printStackTrace();
      throw new ParserException(e);
    }
  }

  /**
   *  Forces the parser to make a forward step in the parsing process.
   *
   * @return the next state of the parser
   */
  public int next() throws ParserException {
    try {
// 23.11.2004 Aleksandar Aleksandrov - skips white spaces after root element
// which should not happen      
//      if (endOfRootReached) {
//        parser.scanS();
//      }
      if (parser.getDocFinished()) {
        currentState = EOF;
        parser.finalizeActiveParse();
        return EOF;
      }

      if (shouldFakeEnd) {
        shouldFakeEnd = false;
        currentState = XMLTokenReader.ENDELEMENT;
        depthReached--;
        return currentState;
      } else {
        this.prefixesOfEmptyElement = null;
      }

      int i = parser.checkMarkup();
      switch (i) {
        case M_PI: {
          parser.scanPI(textDeclAllowed);
          textDeclAllowed = false;
          return PI;
        }
        case M_LT: {
          parser.scanElement();
          return STARTELEMENT;
        }
        case M_COMMENT: {
          parser.scanComment();
          if (this.ommitComments == true) {
            return next();
          }
          return COMMENT;
        }
        case M_CHDATA: {
          parser.scanCharData();
          return CHARS;
        }
        case M_CDSECT: {
          parser.scanCDSect();
          return CHARS;
        }

        case M_ENDEL: {
          parser.scanEndTag();
          if (depthReached < 1) {
            endOfRootReached = true;
            parser.finalizeActiveScan();
          }
          return ENDELEMENT;
        }

        case M_CONTREF: {
          parser.handleContentReference(false);
          //Reference ref = parser.scanReference();
          return CHARS;
        }

        case M_DOCTYPE: {
          parser.scanDTD();
          if(parser.bSoapProcessing){
            return next();
          } else {
            return DTD;
          }
        }

        default: {
          throw new ParserException(" Unrecognized case: " + i, -1, 0);
        }
      }
    } catch (ParserException e) {
      throw e;
    } catch (Exception e) {
      throw new ParserException(e);
    }
  }

  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {
    this.uri = uri;
    this.localName = localName;
    this.qName = qname;
    attribs.clear();
    retAttrs = null;
    //charData.clear();
    charData.setSize(0);
    depthReached++;
    if (this.namespaceHandler.isPrefMappingChangedAndClear()){
    	prefixMappings = this.namespaceHandler.getNamespaceMappings(); //~
    }
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    attribs.addAttribute(uri, prefix, qname, localName, value);
  }

  public void startElementEnd(boolean isEmpty) throws Exception {
    currentState = STARTELEMENT;
    if (isEmpty) {
      shouldFakeEnd = true;
      this.prefixesOfEmptyElement = this.namespaceHandler.getPrefixesOnLastStartElement();
    }
    return;
  }

  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {
    this.uri = uri;
    this.localName = localName;
    this.qName = qname;
    if (isEmpty == false) {
      currentState = ENDELEMENT;
      depthReached--;
    } 
    if (this.namespaceHandler.isPrefMappingChangedAndClear()){
    	prefixMappings = this.namespaceHandler.getNamespaceMappings(); //~
    }
    return;
  }

  public void startDocument() throws Exception {
    currentState = INITIAL;
    return;
  }

  public void endDocument() throws Exception {
    currentState = EOF;
    return;
  }

  public void charData(CharArray carr, boolean bDisableOutputEscaping) throws Exception {
    if (append) {
      charData.append(carr);
    } else {
      charData.set(carr);
    }
    currentState = CHARS;
    return;
  }

  /**
   * @return Returns the encoding.
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * @return Returns the xmlVer.
   */
  public String getXmlVer() {
    return xmlVer;
  }

  /* Unaware of, curretly! */
  public void onXMLDecl(String version, String encoding, String ssdecl) throws Exception {
    //if(!parser.bSoapProcessing){
      xmlVer = version;
      this.encoding = encoding;
    //}
  }

  /* Unaware of, curretly! */
  public void onPI(CharArray target, CharArray data) throws Exception {  
    if(!parser.bSoapProcessing){
      currentState = PI;
      piTarget = target.getString();
      piData = data.getString();
    }
    
  }

  public void onComment(CharArray text) throws Exception {    
    if (this.ommitComments == false) {
      currentState = COMMENT;
      charData.set(text);
    }
  }

  /* Unaware of, curretly! */
  public void onCDSect(CharArray text) throws Exception {
    if (append) {
      charData.append(text);
    } else {
      charData.set(text);
    }
    currentState = XMLTokenReader.CHARS;
    return;
  }

  public void onDTDElement(CharArray name, CharArray model) throws Exception {
    if(!parser.bSoapProcessing){      
      currentState = XMLTokenReader.DTD;
    }
  }

  public void onDTDAttListStart(CharArray name) throws Exception {
  }

  public void onDTDAttListItem(CharArray name, CharArray attname, String type, String defDecl, CharArray vAttValue, String note) throws Exception {
  }

  public void onDTDAttListEnd() throws Exception {
  }

  public void onDTDEntity(Entity entity) throws Exception {
    if(!parser.bSoapProcessing){
      currentState = XMLTokenReader.DTD;
    }
  }

  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) throws Exception {
    if(!parser.bSoapProcessing){
      currentState = XMLTokenReader.DTD;  
    }
  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {
  }

  public void endDTD() throws Exception {
  }

  public void onContentReference(Reference ref) throws Exception {
  }

  /**
   *  Should be called after the contents of the whole document are read.
   */
  public void end() throws ParserException {
    if (this.currentState != EOF) {
      throw new ParserException("EOF not reached yet!", 0, 0);
    }

    try {
      parser.finalizeActiveParse();
    } catch (Exception e) {
      throw new ParserException(e.toString(), 0, 0);
    }
  }

  public int getState() {
    return currentState;
  }
  
  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current qualified name
   */
  public String getQName() {
    return qName.getString(symbolTable);
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current qualified name as a CharArray
   */
  public CharArray getQNameCharArray() {
    return qName;
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current local name
   */
  public String getLocalName() {
    return localName.getString(symbolTable);
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current local name as a CharArray
   */
  public CharArray getLocalNameCharArray() {
    return localName;
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current URI
   */
  public String getURI() {
    return uri.getString(symbolTable);
  }

  /**
   *  Consistent only in the START and END states.
   *
   *  @return the current URI as a CharArray
   */
  public CharArray getURICharArray() {
    return uri;
  }

  /**
   *  Consistent only in the START state.
   *  @return the attributes of the current element
   */
  public Attributes getAttributes() {
    if (this.currentState != XMLTokenReader.STARTELEMENT) {
      return null;
    }
    if (retAttrs != null) {
      return retAttrs;
    }
    
    AttributesImpl attr = new AttributesImpl();
    for (int i = 0; i < attribs.size(); i ++) {
      // We put as attribute type "CDATA" - this is the default attribute type if we do not read the DTD section of the XML document.
      InternalAttribute ia = attribs.get(i);
      attr.addAttribute((ia.uri == null ? null : ia.uri.getString(symbolTable)), 
                        (ia.localName.getString(symbolTable)), 
                        (ia.qname.getString(symbolTable)),
                        "CDATA", 
                        (ia.value.getString(symbolTable)));
    }
    this.retAttrs = attr;
    return retAttrs;
  }
  
  public InternalAttributeList getInternalAttributeList() {
    return attribs;
  }

  /**
   *  Consistent only in the CHARS state.
   *
   *  @return the current Character Data
   */
  public String getValue() {
    return charData.getStringFast();
  }

  public CharArray getValueCharArray() {
    return charData;
  }

  public CharArray getValuePassCharsCA() throws ParserException {
    append = true;
    while (currentState == XMLTokenReader.CHARS) {
      next();
    }
    append = false;
    if (bTrimWhitespaces) {
      charData.trim();
    }
    return charData;
  }

  public String getValuePassChars() throws ParserException {
    append = true;
    while (currentState == XMLTokenReader.CHARS) {
      next();
    }
    append = false;
    if (bTrimWhitespaces) {
      charData.trim();
    }
    return charData.getStringFast();
  }

  public boolean isWhitespace() {
    return charData.isWhitespace();
  }

  /**
   * Returns dom representation of current element.
   */
  public  Element getDOMRepresentation(Element element) throws ParserException {
    return XMLTokenReaderUtil.getDOMRepresentation(this, element);
  }
  
  /**
   * Returns dom representation of current element.
   */
  public  Element getDOMRepresentation(Document document) throws ParserException {
    if (this.getState() != XMLTokenReader.STARTELEMENT) {
      return null;
    }
    Element element = document.createElementNS(this.getURI(), this.getQName());
    return getDOMRepresentation(element);
  }
  
  public void writeTo(Writer writer) throws ParserException, IOException {
    XMLTokenReaderUtil.copyReader2Writer(this, writer);
  }


  public int moveToNextElementStart() throws ParserException {
    int code;

    while (true) {
      code = this.next();
      if (code == XMLTokenReader.STARTELEMENT) {
        return XMLTokenReader.STARTELEMENT;
      } else if (code == XMLTokenReader.EOF) {
        return XMLTokenReader.EOF;
      }
    }
  }


  public void startPrefixMapping(CharArray prefix, CharArray uri) throws Exception {
//    //if (docHandler != null){docHandler.startPrefixMapping(new CharArray(prefix), new CharArray(uri));}
//    String stringPrefix = prefix.getString();
//    String stringURI = uri.getString();
//    Stack mappings = (Stack) prefixMapping.get(stringPrefix);
//
//    if (mappings == null) {
//      mappings = new Stack();
//      prefixMapping.put(stringPrefix, mappings);
//    }
//
//    mappings.add(stringURI);
  }

  public void endPrefixMapping(CharArray prefix) throws Exception {
//    //if (docHandler != null){docHandler.endPrefixMapping(new CharArray(prefix));}
//    String stringPrefix = prefix.getString();
//    Stack mappings = (Stack) prefixMapping.get(stringPrefix);
//
//    if (mappings != null) {
//      mappings.pop();
//
//      if (mappings.isEmpty()) {
//        prefixMapping.remove(stringPrefix);
//      }
//    }
  }

  /**
   * Returns namespace mapping ot this prefix on current place
   */
//  public String getURI(String prefix) {
////    LogWriter.getSystemLogWriter().println("ActiveXMLParser prefixes: " + prefixMapping);
//    Stack mappings = (Stack) prefixMapping.get(prefix);
//
//    if (mappings != null) {
//      return (String) mappings.peek();
//    }
//
//    return null;
//  }

  public XMLParser getParser() throws ParserException {
    if (parser == null) {
      parser = new XMLParser();
      parser.setSoapProcessing(true);
      parser.setNamespaces(true);
    } else {
      //parser.finalizeActiveParse();
    }

    return parser;
  }

  public void setParser(XMLParser parser) {
    this.parser = parser;
  }


  private CharArray tempGetPrefix = new CharArray();
  public String getPrefixMapping(String prefix) {
    tempGetPrefix.set(prefix);
    //return namespaceHandler.get(tempGetPrefix).getString(symbolTable);
    //return getURI(prefix);
    return this.prefixMappings.get(tempGetPrefix); //~
  }

  /**
   * Passes characters. Stops if elemed start or end is met.
   */
  public void passChars() throws ParserException {
    while (currentState == XMLTokenReader.CHARS) {
      next();
    }
  }

  /**
   * Returns parent of current node.
   * @return
   */
  public String getParentElement() {
    CharArrayStack stack = parser.getElementStack();
    if (this.currentState == XMLTokenReader.STARTELEMENT) {
      CharArray current = new CharArray(stack.getTop());
      stack.matchTop(new CharArray(current));
      if (stack.isEmpty()) {
        stack.put(current);
        return null;
      } else {
        char[] top = stack.getTop();
        stack.put(current);
        return new String(top);
      }
    } else {
      if (stack.isEmpty()) {
        return null;
      } else {
        return new String(stack.getTop());
      }
    }
  }

  public static void main(String[] args) throws Exception {
    ActiveXMLParser parser = new ActiveXMLParser(new FileInputStream("D:\\develop\\schema_test\\test3.xml"));
    parser.begin();
//    int code = -1;
//    while ((code = parser.next()) != parser.EOF) {
//      ;
//    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader#getCurrentLevel()
   */
  public int getCurrentLevel() {
    // TODO Auto-generated method stub
    return depthReached;
  }

  public void setEntityResolver(EntityResolver entityResolver) throws ParserException {
    getParser().setEntityResolver(entityResolver);
  }
   
  private boolean bTrimWhitespaces = false;
  
  public void setProperty(String key, Object value) {
    try {    
      if (key.equals(Features.FEATURE_SOAP_DATA) && value != null && value instanceof String) {
        if ("true".equalsIgnoreCase((String) value)) {
          getParser().setSoapProcessing(true);       
        } else {
          getParser().setSoapProcessing(false);
        }
      }
      if (key.equals(Features.FEATURE_TRIM_WHITESPACES)) {
        bTrimWhitespaces = ((Boolean)value).booleanValue();
      } else {
        getParser().setProperty(key, value);
      }
    } catch (Exception e) {
      throw new UnsupportedOperationException("Property not supported.",e);
    }
    
  }
  /**
   * Currently available prefixies. 
   * @return map with key prefixies (String) and value namespaces (String).
   */
  public Map<String, String> getNamespaceMappings() {
	Hashtable ht = prefixMappings; //~
	//Hashtable ht = this.namespaceHandler.getNamespaceMappings();
    Map res = new HashMap();
    

    Enumeration en = ht.keys();
    while (en.hasMoreElements()) {
      CharArray k = (CharArray) en.nextElement();
      String v = (String) ht.get(k);
      res.put(k.getString(symbolTable), v);
    }
    return res;
  }
  
  public List<String> getPrefixesOnLastStartElement() {
    List<String> res = new ArrayList<String>();
    List<CharArray> charArrList; //~
    if (this.prefixesOfEmptyElement != null) {
      charArrList = this.prefixesOfEmptyElement;
    } else {
      charArrList = this.namespaceHandler.getPrefixesOnLastStartElement();
    }
    /*Iterator<CharArray> itr = charArrSet.iterator();
    while (itr.hasNext()) {
      CharArray next = itr.next(); 
      if (NamespaceHandlerEx.defaultPrefixName == next) {
        res.add("");
      } else {
        res.add(next.getString(symbolTable));
      }
    }*/
    for (CharArray prefix: charArrList){ //~
    	if (NamespaceHandlerEx.defaultPrefixName == prefix) {
    		res.add("");
    	} else {
            res.add(prefix.getString(symbolTable));
        }
    }
    return res;
  }
  
  /**
   * List of prefix definitions. Where one prefix definition
   * is an array is size 2, where [0] is the prefix and [1] is the namespace.
   * @return
   */
  public List<String[]> getEndedPrefixMappings() {
    return this.namespaceHandler.getEndedPrefixMappings();
  }
}
