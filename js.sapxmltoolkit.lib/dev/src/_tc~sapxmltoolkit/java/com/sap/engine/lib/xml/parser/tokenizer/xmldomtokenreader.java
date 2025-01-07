/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.util.NS;
import com.sap.engine.lib.xsl.xslt.InternalAttributeList;

/**
 * XML DOM Element Token Reader.
 * @version 1.0
 * @author Chavdar Baikov, chavdar.baikov@sap.com
 */
public class XMLDOMTokenReader implements XMLTokenReaderDOM {
  
  public void init(InputSource input) {
    // TODO Auto-generated method stub
    
  }

  public void init(Reader reader) {
    // TODO Auto-generated method stub
    
  }

  private boolean omitComments = true;
  private Element rootElement = null;
  private int state = INITIAL;
  private Element currentElement = null;
  private Node currentNode = null;
  private CharArray currentCharacters = new CharArray();
  private int currentLevel = 0;
  private AttributesImpl attributes = null;
  private boolean append = false;
  private Hashtable namespaces;
  private Hashtable currentNamespaces;
  private ArrayList namespacesToRemove;
  private ArrayList<String[]> endedPrefMappings = new ArrayList();
  
  public XMLDOMTokenReader(Element element) {
    if (element == null) {
      throw new IllegalArgumentException("[NULL] Node passed for XML reading.");
    } 
    this.rootElement = element;
    this.attributes = new AttributesImpl();
    this.namespaces = new Hashtable();
    this.currentNamespaces = new Hashtable();
    this.namespacesToRemove = new ArrayList();     
  }
  
  /**
   * Sets flag for the xml reader to ommit comments in XML.
   * @param flag
   */
  public void setOmitComments(boolean flag) {
    this.omitComments = flag;
  }
  
  /**
   * Returns Ommit Comments flag.
   * @return
   */
  public boolean isOmitComments() {
    return this.omitComments;
  }
  
  /**
   * Default constructor, give the root element to be traversed here and table 
   * of predefines prefixes if required.
   * @param element
   * @param prefixes
   */
  public XMLDOMTokenReader(Element element, Hashtable prefixes) {
    if (element == null) {
      throw new IllegalArgumentException("[NULL] Node passed for XML reading.");
    } 
    this.rootElement = element;
    this.attributes = new AttributesImpl();
    this.namespaces = new Hashtable();
    setInitialPrefixes(prefixes);    
    this.currentNamespaces = new Hashtable();
    this.namespacesToRemove = new ArrayList();     
  }
  
  /**
   * Sets the initial known prefixes.
   */
  private void setInitialPrefixes(Hashtable hashtable) {
    if (hashtable != null) {
      Enumeration enum1 = hashtable.keys();
      while (enum1.hasMoreElements()) {
        String prefix = (String) enum1.nextElement();
        String namespace = (String) hashtable.get(prefix);
        PrefixHolder ph = new PrefixHolder(prefix);
        ph.enter(namespace);
        namespaces.put(prefix,ph);        
      }
    }
  }
  
  /**
   * Initializes the XMLReader from element.
   * @param element
   */
  public void init(Element element, Hashtable prefixes) {
    init(element);
    setInitialPrefixes(prefixes);
  }
      
  /**
   * Initializes the XMLReader from element.
   * @param element
   */
  public void init(Element element) {
    if (element == null) {
      throw new IllegalArgumentException("[NULL] Node passed for XML reading.");
    }     
    this.rootElement = element;
    this.currentElement = null;
    this.state = INITIAL;
    this.currentLevel = 0;
    this.attributes.clear();
    this.append = false;
    this.namespaces.clear();
    this.currentNamespaces.clear();
    this.namespacesToRemove.clear();
  }    
      

  /**
   * Call this to cause parsing begin.
   * @return
   * @throws ParserException
   */
  public int begin() throws ParserException {
    this.state = STARTELEMENT;
    currentElement = this.rootElement;
    currentNode = null;
    currentCharacters.clear();
    currentLevel ++;
    initAttributes();
    append = false;
    return this.state;    
  }
  
  /**
   * Updates the namespace list to move down.
   */
  private void namespacesDown() {
    Enumeration nsKeys = namespaces.keys();
    while (nsKeys.hasMoreElements()) {
      String prefix = (String) nsKeys.nextElement();
      if (!currentNamespaces.containsKey(prefix)) {
        PrefixHolder ph = (PrefixHolder) namespaces.get(prefix);
        ph.enter();
      }
    }
    nsKeys = currentNamespaces.keys();
    while (nsKeys.hasMoreElements()) {
      String prefix = (String) nsKeys.nextElement();
      String ns = (String) currentNamespaces.get(prefix);
      PrefixHolder ph = (PrefixHolder) namespaces.get(prefix);
      if (ph == null) {
        ph = new PrefixHolder(prefix);
        ph.enter(ns);
        namespaces.put(prefix,ph);
      } else {
        ph.enter(ns);
      }
    }
    //currentNamespaces.clear();
    //endedPrefMappings.clear();
  }
  
  /**
   * Updates the namespace list to move up - on element leave.
   */
  private void namespacesUp() {
    namespacesToRemove.clear();
    Enumeration enum1 = namespaces.keys();
    while (enum1.hasMoreElements()) {
      String prefix = (String) enum1.nextElement();
      PrefixHolder ph = (PrefixHolder) namespaces.get(prefix);
      if (ph.leave()) {
        namespacesToRemove.add(prefix);
      }
    }
    for (int i=0; i<namespacesToRemove.size(); i++) {
      String prefix = (String) namespacesToRemove.get(i);
      namespaces.remove(prefix);
    }
  }

  private void initEndedPrefixMappings(Element el) {
    this.endedPrefMappings.clear();
    NamedNodeMap attrs = el.getAttributes();
    int length = attrs.getLength();
    for (int i = 0; i < length; i++) {
      Attr attr = (Attr) attrs.item(i);
      if (NS.XMLNS.equals(attr.getNamespaceURI())) {
        String prefix = attr.getLocalName();
        if ("xmlns".equals(prefix)) {
          prefix = "";
        } 
        String ns = attr.getValue();
        this.endedPrefMappings.add(new String[]{prefix, ns});
      }
    }
  }
  /**
   * Inits attributes of the current element. May be called only on start element. 
   */
  private void initAttributes() {    
    attributes.clear();
    currentNamespaces.clear();        
    NamedNodeMap attributesNodeMap = currentElement.getAttributes();
    for(int i = 0; i < attributesNodeMap.getLength(); i++) {
      Node attrib = attributesNodeMap.item(i);
      String attribUri = attrib.getNamespaceURI();
      if(attribUri == null) {
        attribUri = "";
      }
      String attribLocalName = attrib.getLocalName();
      String attribQName = attrib.getNodeName();
      String attribValue = attrib.getNodeValue();
      if(attribUri.equals(NS.XMLNS)) {
        String prefix = attribQName.indexOf(':') < 0 ? "" : attribLocalName;
        String namespace = attribValue;
        currentNamespaces.put(prefix,namespace);  
      }
      if ("xmlns".equals(attribQName)) {
        String prefix = "";
        String namespace = attribValue;
        currentNamespaces.put(prefix,namespace); 
      }
      attributes.addAttribute(attribUri, attribLocalName, attribQName, "CDATA", attribValue);
    }
    namespacesDown();
  }  

  /**
   * Call this to release resources.
   * @throws ParserException
   */
  public void end() throws ParserException {
    currentCharacters.clear();
    this.state = EOF; 
    currentElement = null;
    currentNode = null;
    this.currentLevel = 0;
    attributes.clear();     
  }

  /**
   *   
   * @return
   */
  public Attributes getAttributes() {
    if (this.state == STARTELEMENT) {
      return this.attributes;
    }    
    return null;
  }

  /**
   * Returns the current nest level in the XML tree.
   * @return
   */
  public int getCurrentLevel() {
    return this.currentLevel;
  }

  /**
   * Returns the DOMRepresentation of the current element.
   * @param doc
   * @return
   * @throws ParserException
   */
  public Element getDOMRepresentation(Document doc) throws ParserException {
    if (this.state == STARTELEMENT) {
      this.state = ENDELEMENT;
      return this.currentElement;      
    } else {
      return null;
    }    
  }

  /**
   * 
   * @return
   */
  public InternalAttributeList getInternalAttributeList() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Returns current element local name.
   * @return
   */
  public String getLocalName() {
    if (this.state == STARTELEMENT || this.state == ENDELEMENT) {
      // current element can not be null in this state
      return currentElement.getLocalName();
    }
    return null;
  }

  /**
   * Returns current element local name.
   * Warning CharArray object is volatile. Calling .next() method can alter
   * char array contents.  
   * @return
   */
  public CharArray getLocalNameCharArray() {
    if (this.state == STARTELEMENT || this.state == ENDELEMENT) {
      return new CharArray(currentElement.getLocalName());
    }
    return null;
  }

  /**
   * This method return parent element of the current node.
   * @return
   */
  public String getParentElement() {
    Node node = null;
    if (this.state == STARTELEMENT || this.state == ENDELEMENT) {
      node = this.currentElement.getParentNode();
      if (node != null && node instanceof Element) {
        return ((Element) node).getLocalName();
      } else {
        return null;
      }
    } else {
      node = this.currentNode.getParentNode();
      if (node != null && node instanceof Element) {
        return ((Element) node).getLocalName();
      } else {
        return null;
      }
    }
  }

  /**
   * Returns the namespace mapped to specific prefix.
   * @param prefix
   * @return
   */
  public String getPrefixMapping(String prefix) {
    if (prefix == null) {
      prefix = "";
    }
    PrefixHolder ph = (PrefixHolder) namespaces.get(prefix);
    if (ph != null) {
      String top = ph.top();
      if (top.length() == 0) {
        return null;
      } else {
        return top;
      }
    } else {
      return null;
    }
  }

  /**
   * Returns current Element QName.
   * Returns current 
   * @return
   */
  public String getQName() {
    if (this.state == STARTELEMENT || this.state == ENDELEMENT) {
      return this.currentElement.getTagName();
    }
    return null;
  }

  /**
   * Returns current Element QNama as CharArray.
   * @return
   */
  public CharArray getQNameCharArray() {
    if (this.state == STARTELEMENT || this.state == ENDELEMENT) {      
      return new CharArray(currentElement.getTagName());
    }    
    return null;
  }

  /**
   * Returns current element state.
   * @return
   */
  public int getState() {
    return this.state;
  }

  /**
   * Returns the element namespace.
   * @return
   */
  public String getURI() {
    if (state == STARTELEMENT || state == ENDELEMENT) {
      // The current element should not be NULL here.
      return this.currentElement.getNamespaceURI();      
    } else {
      return null;      
    }
  }

  /**
   * @return
   */
  public CharArray getURICharArray() {
    if (state == STARTELEMENT || state == ENDELEMENT) {
      return new CharArray(this.currentElement.getNamespaceURI());
    }
    return null;
  }

  /**
   * Returns the value of current xml tag.
   * @return
   */
  public String getValue() {
    return currentCharacters.getStringFast();
  }

  /**
   * Returns the value of the current xml tag as CharArray.
   * @return
   */
  public CharArray getValueCharArray() {   
    return currentCharacters;
  }

  /**
   * Returns Concatenation of neibour Text nodes.
   * @return
   * @throws ParserException
   */
  public String getValuePassChars() throws ParserException {
    append = true;
    while (state == XMLTokenReader.CHARS) {
      next();
    }
    append = false;
    return currentCharacters.getStringFast();
  }

  /**
   * Returns Concatenation of neibour Text nodes.
   * @return
   * @throws ParserException
   */
  public CharArray getValuePassCharsCA() throws ParserException {
    append = true;
    while (state == XMLTokenReader.CHARS) {
      next();
    }
    append = false;
    return currentCharacters;
  }

  /**
   * @param input
   */
  public void init(InputStream input) {
    // TODO Auto-generated method stub
  }

  /**
   * Returns true if the value is whitespace.
   * @return
   */
  public boolean isWhitespace() {    
    return currentCharacters.isWhitespace();
  }

  /**
   * Searches next element start.
   * @return
   * @throws ParserException
   */
  public int moveToNextElementStart() throws ParserException {
    int code = this.next();

    while (code != XMLTokenReader.STARTELEMENT && code != XMLTokenReader.EOF) {
      code = this.next();
    }
    return code;
  }

  /**
   * Reads next XML Node from the XML Stream.
   * Note: Not ULTRA Optimized version but effective one.
   * @return
   * @throws ParserException
   */
  public int next() throws ParserException {
    if (currentElement != null) { // CURRENT ELEMENT
      if (this.state == ENDELEMENT) { // END ELEMENT
        currentLevel--;
        currentCharacters.clear();
        namespacesUp();
        if (currentElement == rootElement) { // IS IT THE ROOT?
          state = EOF;
          currentElement = null;
        } else { // NOT THE ROOT
          Node newNode = currentElement.getNextSibling();
          if (newNode == null) { // NO NEXT SIBLING
            currentElement = (Element) currentElement.getParentNode();
            initEndedPrefixMappings(currentElement);
            state = ENDELEMENT;
          } else {
            if (newNode instanceof Element) { // NEXT ELEMENT
              currentLevel++;
              currentElement = (Element) newNode;
              state = STARTELEMENT; 
              initAttributes();             
            } else { // NEXT NODE
              state = getNodeState(newNode);
              currentElement = null;
              currentNode = newNode;
              if (omitComments && state == COMMENT) {
                state = next();
              } else {
                appendNodeContent(currentNode);
              }
              
            }
          }
        } 
      } else { // START ELEMENT
        currentCharacters.clear();
        Node newNode = currentElement.getFirstChild();
        if (newNode == null) { // NO KIDS
          initEndedPrefixMappings(currentElement);
          state = ENDELEMENT;
        } else { // FIRST KID
          if (newNode instanceof Element) { // ELEMENT
            currentLevel++;
            state = STARTELEMENT;
            currentElement = (Element) newNode;
            initAttributes();                         
          } else { // NODE
            state = getNodeState(newNode);
            currentElement = null;
            currentNode = newNode;
            if (omitComments && state == COMMENT) {
              state = next();
            } else {
              appendNodeContent(currentNode);
            }            
          }
        }        
      }
    } else { // OVER A NODE
      Node newNode = currentNode.getNextSibling();      
      if (newNode == null) { // NO NEXT SIBLING
        newNode = currentNode.getParentNode();
        if (newNode instanceof Element) { // ONLY ELEMENTS CAN HAVE KIDS 
          currentElement = (Element) newNode;
          initEndedPrefixMappings(currentElement);
          currentNode = null;
          state = ENDELEMENT;          
        }
      } else { // NEXT SIBLING
        if (newNode instanceof Element) { // ELEMENT
          state = STARTELEMENT;          
          currentLevel++;
          currentElement = (Element) newNode;
          currentNode = null;
          initAttributes();                       
        } else { // NODE
          state = getNodeState(newNode);
          currentNode = newNode;
          if (omitComments && state == COMMENT) {
            state = next();
          } else {
            appendNodeContent(currentNode);
          }          
        }        
      }
    }     
    return state;
  }
  
  /**
   * Sets node character content.
   * @param node
   */
  private void appendNodeContent(Node node) {
    if (this.state == CHARS) {
      if (append) {
        currentCharacters.append(node.getNodeValue());
      } else {
        currentCharacters.set(node.getNodeValue());
      }
    }
    if (this.state == COMMENT) {
      currentCharacters.set(node.getNodeValue());
    }
    if (this.state == PI) {
      currentCharacters.set(node.getNodeValue());
    }
  }
  
  /**
   * Returns the type of the node passed.
   * @param node
   * @return
   * @throws ParserException
   */
  private int getNodeState(Node node) throws ParserException {
    switch (node.getNodeType()) {
      case Node.TEXT_NODE:
      case Node.CDATA_SECTION_NODE: 
      case Node.ENTITY_REFERENCE_NODE: {
        return CHARS;
      } 
      case Node.COMMENT_NODE: {
        return COMMENT;
      }      
      case Node.PROCESSING_INSTRUCTION_NODE: {
        return PI;
      }
    }
    throw new ParserException(" Unrecognized case: " + node.getNodeType(), -1, 0);
  }

  /**
   * Calls next untile event different that CHAR appears.
   * @throws ParserException
   */
  public void passChars() throws ParserException {
    while (this.state == CHARS) {
      next();
    }
  }

  /**
   * @param entityResolver
   * @throws ParserException
   */
  public void setEntityResolver(EntityResolver entityResolver)
    throws ParserException {
    // TODO Auto-generated method stub
  }

  /**
   * @param writer
   * @throws ParserException
   * @throws IOException
   */
  public void writeTo(Writer writer) throws ParserException, IOException {
    /*
    if (this.getState() != XMLTokenReader.CHARS && this.getState() != XMLTokenReader.STARTELEMENT) {
      throw new ParserException("Invalid reader state.", 0, 0);
    }
    
    if (this.getState() == XMLTokenReader.CHARS) { //read the chars and return
      writeCharsOrComment(writer);
      writer.flush();
    } else { //this is start element
      //writes first start element
      writer.write('<');
      CharArray charArr = this.getQNameCharArray();
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write(' ');
      //write the attributes
      Attributes atts = this.getAttributes();
      for (int i = 0; i < atts.getLength(); i++) {
        String qname = atts.getQName(i);
        if (! NS.XMLNS.equals(atts.getURI(i)) && ! "xmlns".equals(atts.getLocalName(i))) {//do not write ns declarations, they are written later
          writer.write(atts.getQName(i));
          writer.write("=\'");
          writer.write(atts.getValue(i));
          writer.write("\' ");
        }            
      }
      //appends all current prefix mappings into this first element
      Hashtable prefixMappings = namespaceHandler.getNamespaceMappings();
      Enumeration enum = prefixMappings.keys();
      while (enum.hasMoreElements()) {
        CharArray key = (CharArray) enum.nextElement();
        String namespace = (String) prefixMappings.get(key);
        if (namespace.length() != 0) {
          if (key.length() == 0) {
            writer.write("xmlns='");
            writer.write(namespace);
            writer.write("\' ");            
          } else {
            writer.write("xmlns:" + key + "='");
            writer.write(namespace);
            writer.write("\' ");            
          }
        }
      }
      writer.write('>');
      int level = 0; //counts start and end elements
      int code;       
      while (true) {
        code = this.next();
        switch (code) {
          case XMLTokenReader.COMMENT: {
            writeCharsOrComment(writer);
            continue;
          }
          case XMLTokenReader.CHARS: {
            writeCharsOrComment(writer);
            continue;
          }
          case XMLTokenReader.STARTELEMENT: {
            writeStartEndElement(writer);
            level++;
            continue;
          }
          case XMLTokenReader.ENDELEMENT: {
            writeStartEndElement(writer);
            if (level == 0) { //this is the last end element
              writer.flush();
              return;
            }
            level--;
            continue;
          }
          case XMLTokenReader.EOF: {
            throw new ParserException("Unexpexted EOF." , 0, 0);
          }
        }
      }
    } */
  }

  /**
   * Readers start/stop element from this reader and writes it into the writer.
   * 
   * @param writer writer into which the element is writen.
   */
  private void writeStartEndElement(Writer writer) throws IOException {
    char[] data;
    int size, offset;
    CharArray charArr;
    //writer start element
    if (this.getState() == XMLTokenReader.STARTELEMENT) {
      writer.write('<');
      charArr = this.getQNameCharArray();
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write(' ');
      Attributes atts = this.getAttributes();
      for (int i = 0; i < atts.getLength(); i++) {
        writer.write(atts.getQName(i));
        writer.write("=\'");
        writer.write(atts.getValue(i));
        writer.write("\' ");            
      }
      writer.write('>');
    } else { //this is end element
      writer.write("</");
      charArr = this.getQNameCharArray();
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write('>');
    }
  }
  /**
   * Writes char and comment data into the writer.
   * Parser should be in one of the states XMLTokenReader.CHARS or XMLTokenReader.COMMENT
   */
  private void writeCharsOrComment(Writer writer) throws IOException {
    CharArray charArr = getValueCharArray();
    if (this.getState() == XMLTokenReader.COMMENT) {
      writer.write("<!--");
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write("-->");
    } else { //this is char data
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());      
    }
  }

  /**
   * Currently available prefixies. 
   * @return map with key prefixies (String) and value namespaces (String).
   */
  public Map getNamespaceMappings() {
    Map res = new HashMap();
    Enumeration en = this.namespaces.keys();
    while (en.hasMoreElements()) {
      String k = (String) en.nextElement();
      PrefixHolder v = (PrefixHolder) this.namespaces.get(k);
      res.put(k, v.top());
    }
    return res;
  }

  public List<String> getPrefixesOnLastStartElement() {
     Iterator itr = this.currentNamespaces.keySet().iterator();
     List<String> res = new ArrayList();
     while(itr.hasNext()) {
       res.add((String) itr.next());
     }
     return res;
  }
  
  /**
   * List of prefix definitions. Where one prefix definition
   * is an array is size 2, where [0] is the prefix and [1] is the namespace.
   * @return
   */
  public List<String[]> getEndedPrefixMappings() {
    return this.endedPrefMappings;
  }

  public void setProperty(String key, Object value) {
    // TODO Auto-generated method stub
    
  }  
  

}
