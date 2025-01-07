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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.util.SymbolTable;
import com.sap.engine.lib.xsl.xslt.output.Encoder;

/**
 * XMLTokenWriter Implementation.
 *
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public final class XMLTokenWriterImpl implements XMLTokenWriter {
  //delimeter char to be used!
  public char DELIMETER_CHAR = '\'';
  private String encoding;
  /**
   * This structures holds closing tags in stack alike char array structure.
   */
  private String currentName;
  private String namespace;
  //private HashMap namespaces;
  private HashMap currentPrefixes;
  private int generatedNSIndex;
  //private CharStacker closeStack;
  private StringStacker stack;
  private String[] attrNames;
  private String[] attrValues;
  private String[] attrUris;
  private int attrCount;
  private ArrayList perm;
  private boolean indent;
  StringBuffer indentString;
  private int INDENTCOUNT = 2;
  private char[] indentPiece;
  private int indentLevel;
  private boolean textContent;
  private Encoder enc;
  
  private static final String PREF  = "yq"; //Needs to be 2 charactres.
  
  //private StringBuffer buffer = new StringBuffer();

  private PHSet allnamespaces;
  
  //private StringBuffer buffer = new StringBuffer();
  private static final String[] CH_TO_ENT = new String[256];

  static {
    CH_TO_ENT['&'] = "&amp;";
    CH_TO_ENT['\''] = "&apos;";
    CH_TO_ENT['"'] = "&quot;";
    CH_TO_ENT['<'] = "&lt;";
    CH_TO_ENT['>'] = "&gt;";
  }

//  private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', };
  public void setIndent(boolean indent) {
    this.indent = indent;
  }

  public boolean getIndent() {
    return(indent);
  }

  /**
   * @deprecated Please use XMLTokenWriterFactory.newInstance() method.
   *
   */
  public XMLTokenWriterImpl() {
    indentLevel = 0;
    indentPiece = new char[INDENTCOUNT];
    for (int i=0; i<INDENTCOUNT; i++) {
      indentPiece[i] = ' ';
    }
    indent = false;
    //namespaces = new HashMap();
    allnamespaces = new PHSet();
    currentPrefixes = new HashMap();
    //closeStack = new CharStacker();
    stack = new StringStacker();
    attrNames = new String[512];
    attrValues = new String[512];
    attrUris = new String[512];
    attrCount = 0;
    perm = new ArrayList();
    indentString = new StringBuffer();
    enc = new Encoder();
  }
  
  /**
   * Returns prefix from namespace declaration.
   * @param namespace
   * @return
   */
  private String getPrefixInternal(String namespace) {
    for (int i=0; i<allnamespaces.currentSize; i++) {
      PrefixHolder ph = allnamespaces.get(i);
      if (ph.namespace.equals(namespace)) {
        return ph.top();
      }
    }
//    PrefixHolder pf = (PrefixHolder) namespaces.get(namespace);
//    if (pf != null) {
//      return pf.top();
//    }
    return null;
  }

  /**
   * Resizes up String array.
   */
//  private String[] resizeCharArray(String[] array, int newSize) {
//    String[] result = new String[newSize];
//    System.arraycopy(array,0,result,0,array.length);
//    return result;
//  }

  /**
   * Updates namespace mapping.
   */
  private void updateNamespacesDown() {
    int size = allnamespaces.size();
    PrefixHolder[] arr = allnamespaces.getContent();
    for (int i=0; i<size; i++) {
      PrefixHolder ph = arr[i];
      if (currentPrefixes.containsKey(ph.namespace)) {
        String prefix = (String) currentPrefixes.get(ph.namespace);
        ph.enter(prefix);
        // append attribute
        if (prefix.length() != 0) {
          attrNames[attrCount] = "xmlns:".concat(prefix);
        } else {
          attrNames[attrCount] = "xmlns";
        }
        attrValues[attrCount] = ph.namespace;
          attrUris[attrCount] = null;
        attrCount++;
        currentPrefixes.remove(ph.namespace);
      } else {
        ph.enter();
      }
    }
    Iterator it = currentPrefixes.keySet().iterator();
    while (it.hasNext()) {
      String namespace = (String) it.next();
      String prefix = (String) currentPrefixes.get(namespace);
      PrefixHolder ph = getPrefixHolder(namespace);
      allnamespaces.add(ph);
      ph.enter(prefix);
      // append attribute
      if (prefix.length() != 0) {
        attrNames[attrCount] = "xmlns:".concat(prefix);
      } else {
        attrNames[attrCount] = "xmlns";
      }
      attrValues[attrCount] = namespace;
      attrCount++;
    }
    currentPrefixes.clear();
  }

  private void updateNamespaceUp() {
    int size = allnamespaces.size();
    PrefixHolder[] arr = allnamespaces.getContent();
    int newSize = 0;
    for (int i=0; i<size; i++) {
      if (arr[i].leave()) {
        arr[i] = null;
      } else {
        if (i!=newSize) {
          arr[newSize] = arr[i];
          arr[i] = null;
        }
        newSize++;
      }
    }
    allnamespaces.setSize(newSize);
//    perm.clear();
//    Iterator it1 = namespaces.keySet().iterator();
//    while (it1.hasNext()) {
//      String namespace = (String) it1.next();
//      PrefixHolder ph = (PrefixHolder) namespaces.get(namespace);
//      if (ph.leave()) {
//        PrefixHolder.returnPrefixHolder(ph);
//        perm.add(namespace);
//      }
//    }
//    for (int i=0; i<perm.size(); i++) {
//      namespaces.remove(perm.get(i));
//    }
  }

  /**
   * Adds namespace prefix mapping.
   */
  private String generateNSPrefix(String namespace) {
    String prefix;
    prefix = PREF + String.valueOf(generatedNSIndex);
    generatedNSIndex++;
    currentPrefixes.put(namespace, prefix);
    return prefix;
  }
  
  private void checkCustomPrefix(String pref) {
//    if (pref.startsWith(PREF)) {
//      throw new IllegalArgumentException("Prefixes starting with '" + PREF + "' are reserved for internal use.");
//    }
  }
  
  /*
  private void appendNSDeclarations() throws IOException {
    Iterator i = currentPrefixes.keySet().iterator();
    while (i.hasNext()) {
      String namespace = (String) i.next();
      String prefix = (String) currentPrefixes.get(namespace);
      bufferedWriter.write(" xmlns:");
      bufferedWriter.write(prefix);
      bufferedWriter.write("=\'");
      bufferedWriter.write(namespace);
      bufferedWriter.write("\'");
    }
    currentPrefixes.clear();
  }
  */

  private void appendAttributes() throws IOException {
    for (int i=0; i<attrCount; i++) {
      if (attrValues[i] == null || attrNames[i] == null) {
        continue;
      }
      enc.out(' ');
      enc.out(attrNames[i]);
      enc.out('=');
      enc.out(DELIMETER_CHAR);
      //bufferedWriter.write(attrValues[i]);
//      enc.outEscaped(attrValues[i]);
      enc.outAttribute(attrValues[i], DELIMETER_CHAR);
      enc.out(DELIMETER_CHAR);
    }
    attrCount=0;
  }

  private void enterIndent() {
    indentLevel++;
    indentString.append(indentPiece);
  }

  private void leaveIndent() {
    indentLevel--;
    int perm = indentString.length();
    indentString.setLength(perm-INDENTCOUNT);
  }

  private SymbolTable symbolTable = new SymbolTable(1024);

  private CharArray caCurrentName = new CharArray();
  
  private void writeTagOpen() throws IOException {
    if (namespace != null && namespace.length() != 0) {
      String prefix = (String) currentPrefixes.get(namespace);
      if (prefix == null) {
        prefix = getPrefixInternal(namespace);
        //prefix = (String) currentPrefixes.get(namespace);
        if (prefix == null || currentPrefixes.containsValue(prefix)) {
          prefix = generateNSPrefix(namespace);
        }
      }
      if (prefix.length() > 0){
        caCurrentName.set(prefix);
        caCurrentName.append(':');
        caCurrentName.append(currentName);
      } else {
        caCurrentName.set(currentName);
      }
      
      //wtOpenSB.delete(0, wtOpenSB.)
      currentName = caCurrentName.getString(symbolTable);
    }
    updateAttributePrefixes();
    updateNamespacesDown();
    if (indent && textContent == false) {
      enc.out('\n');
      enc.out(indentString.toString());
    }
    enc.out('<');
    enc.out(currentName);
    stack.push(currentName);
    //closeStack.pushChars(currentName.toCharArray());
    //appendNSDeclarations(); Not useed
    if (handler!=null){
      attrCount = handler.handleAttributes(attrNames, attrValues, attrUris, attrCount, currentName, namespace, this);
    }
    appendAttributes();
    enc.out('>');
    //currentPrefixes.clear();
    // Pushes tag in stack
    currentName = null;
    namespace = null;
    if (indent) {
      enterIndent();
    }
  }

  private void writeTagClose() throws IOException {
    if (indent) {
      leaveIndent();
    }
    if (indent && textContent == false) {
      enc.out('\n');
      enc.out(indentString.toString());
    }
    String elementName = stack.pop(); 
    enc.out("</");
    enc.out(elementName);
    //bufferedWriter.write(closeStack.getChars(),closeStack.getBegin(),closeStack.getSize());
    enc.out('>');
    //closeStack.pop();
    textContent = false;
  }

  public void init(OutputStream output, Hashtable defaultPrefixes) {
    currentPrefixes.clear();
    this.attrCount = 0;
    this.generatedNSIndex = 1;
    this.indentLevel = 0;
    this.indent = false;
    enc.init(output);
    //this.output = output;
    //this.writer = new OutputStreamWriter(output);
    //this.bufferedWriter = new BufferedWriter(this.writer);
    this.currentName = null;
    stack.clear();
    //this.encoding = writer.getEncoding();
    //namespaces.clear();
    allnamespaces.clear();
    PrefixHolder ph = getPrefixHolder("http://www.w3.org/XML/1998/namespace");
    ph.enter("xml");
    allnamespaces.add(ph);
    appendNamespaces(defaultPrefixes);
//    Enumeration keys = defaultPrefixes.keys();
//    while (keys.hasMoreElements()) {
//      String key = (String) keys.nextElement();
//      String value = (String) defaultPrefixes.get(key);
//      //LogWriter.getSystemLogWriter().println(key+"="+value);
//      ph = getPrefixHolder(key);
//      ph.enter(value);
//      allnamespaces.add(ph);
//      //namespaces.put(key, ph);
//    }
  }
  
  /**
   * Appends external namespace declaration to XMLFragment.
   */
  public void appendNamespaces(Hashtable hash) {
    Enumeration keys = hash.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      if (getPrefixInternal(key)== null) {
        String value = (String) hash.get(key);
        checkCustomPrefix(value);
        
        PrefixHolder ph = getPrefixHolder(key);
        ph.enter(value);
        allnamespaces.add(ph);
      }            
    }    
  }

  public void init(OutputStream output) {
    currentPrefixes.clear();
    this.attrCount = 0;
    this.generatedNSIndex = 1;
    this.indentLevel = 0;
    this.indent = false;
    enc.init(output);
    //is.output = output;
    //this.writer = new OutputStreamWriter(output);
    //this.bufferedWriter = new BufferedWriter(this.writer);
    this.currentName = null;
    stack.clear();
    allnamespaces.clear();
    PrefixHolder ph = getPrefixHolder("http://www.w3.org/XML/1998/namespace");
    ph.enter("xml");
    allnamespaces.add(ph);

    this.encoding = "utf-8";
    //this.encoding = writer.getEncoding();
    //namespaces.clear();
  }

  public void init(OutputStream output, String encoding) {
    currentPrefixes.clear();
    this.attrCount = 0;
    this.generatedNSIndex = 1;
    this.indent = false;
    this.indentLevel = 0;
    enc.init(output, encoding);
    //this.output = output;
    //this.writer = new OutputStreamWriter(output,encoding);
    //this.bufferedWriter = new BufferedWriter(writer);
    this.currentName = null;
    stack.clear();
    //this.encoding = encoding;
    allnamespaces.clear();
    PrefixHolder ph = getPrefixHolder("http://www.w3.org/XML/1998/namespace");
    ph.enter("xml");
    allnamespaces.add(ph);
    //namespaces.clear();
    this.encoding = encoding.toUpperCase(Locale.ENGLISH);
  }

  public void init(OutputStream output, String encoding, Hashtable defaultPrefixes) {
    currentPrefixes.clear();
    this.attrCount = 0;
    this.generatedNSIndex = 1;
    this.indent = false;
    this.indentLevel = 0;
    enc.init(output, encoding);
    this.encoding = encoding.toUpperCase(Locale.ENGLISH);
    //this.output = output;
    //this.writer = new OutputStreamWriter(output,encoding);
    //this.bufferedWriter = new BufferedWriter(writer);
    this.currentName = null;
    stack.clear();
    //this.encoding = encoding;
    //namespaces.clear();
    allnamespaces.clear();
    PrefixHolder ph = getPrefixHolder("http://www.w3.org/XML/1998/namespace");
    ph.enter("xml");
    allnamespaces.add(ph);
    appendNamespaces(defaultPrefixes);
//    Enumeration keys = defaultPrefixes.keys();
//    while (keys.hasMoreElements()) {
//      String key = (String) keys.nextElement();
//      String value = (String) defaultPrefixes.get(key);
//      //LogWriter.getSystemLogWriter().println(key+"="+value);
//      ph = getPrefixHolder(key);
//      ph.enter(value);
//      allnamespaces.add(ph);
//      //namespaces.put(key, ph);
//    }
  }


  public void writeInitial() throws IOException {
    //this.bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    enc.out("<?xml version=\"1.0\" encoding=\""+this.encoding+"\"?>");
  }

  /**
   * Creates new element and enters into it's content. Namespace must be mapped
   */
  public void enter(String namespace, String localName) throws IOException {
    //LogWriter.getSystemLogWriter().println("XMLTokenWriter.enter: namespace=" + namespace + ", local=" + localName);
    if (currentName != null) {
      textContent = false;
      writeTagOpen();
    }
    this.currentName = localName;
    this.namespace = namespace;
  }

  /**
   * Flushes caches.
   */
  public void flush() throws IOException {
    try {
      enc.flush();
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException(e.toString());
    }
    //output.flush();
  }

  /**
   * Returns prefix for given namespace.
   * First searches in current element then in parent elements.
   * At the end returns null if not available.
   */
  public String getPrefixForNamespace(String namespace) throws IOException, IllegalStateException {
//  Aleksandar Aleksandrov 18.11.2004
//  fixes problem with getting namespace prefix for the last written element    
//     if (currentName == null) {
//       throw new IllegalStateException("Entering tag already output !");
//     }
    if (currentPrefixes.get(namespace) != null) {
      return (String) currentPrefixes.get(namespace);
    } else {
      return getPrefixInternal(namespace);
    }
  }

  /**
   * Leaves current element.
   */
  public void leave() throws IOException {
    if (currentName != null) {
      writeTagOpen();
      //textContent = false;
      textContent = true;
    }
    writeTagClose();
    updateNamespaceUp();
  }

  /**
   * Sets namespace for given prefix. If set null as prefix this undeclares given namespace.
   */
  public void setPrefixForNamespace(String prefix, String namespace) throws IOException, IllegalStateException {
    checkCustomPrefix(prefix);
    currentPrefixes.put(namespace, prefix);
  }
 
  /**
   * Sets prefixes to attributes.
   *
   */
  private void updateAttributePrefixes() {
    for (int i=0; i<attrCount; i++) {
      String namespace = attrUris[i];
      if (namespace != null && namespace.length()!=0) {
        String prefix = getPrefixInternal(namespace);
        if (prefix == null) {
          prefix = (String) currentPrefixes.get(namespace);
          if (prefix == null) {
            prefix = generateNSPrefix(namespace);
          }
        }
        attrNames[i] = prefix + ":"+ attrNames[i] ;
      }      
    }    
  }
  
  /**
   * Adds attribute to current element.
   */
  public void writeAttribute(String namespace, String name, String value) throws IOException, IllegalStateException {
    //LogWriter.getSystemLogWriter().println("XMLTokenWriterImpl.writeAttribute: ns=" + namespace +", name=" + name + ", value=" + value);
    /*
    if (namespace != null && namespace.length()!=0) {
      String prefix = getPrefixInternal(namespace);
      if (prefix == null) {
        prefix = (String) currentPrefixes.get(namespace);
        if (prefix == null) {
          prefix = generateNSPrefix(namespace);
        }
      }
      name = prefix + ":"+ name ;
    }*/
    attrNames[attrCount] = name;
    attrValues[attrCount] = value;
    attrUris[attrCount] = namespace;
    attrCount++;
  }

  /**
   * Writes comment into xml stream.
   */
  public void writeComment(String comment) throws IOException {
    if (currentName != null) {
      writeTagOpen();
      if (indent) {
        textContent = false;
        enc.out('\n');
      }
    }
    enc.out("<!-- ");
    enc.out(comment);
    enc.out(" -->");
    if (indent) {
      enc.out('\n');
    }

  }

  /**
   * Outputs text characters into XMLStream.
   */
  public void writeContentCData(char[] content) throws IOException {
    if (currentName != null) {
      writeTagOpen();
      textContent = true;
    }
    writeXMLEscaped(content);
    //bufferedWriter.write(content);
  }

  /**
   * Outputs characters from array buffer. Not CData section.
   */
  public void writeContentCData(char[] chars, int offset, int count) throws IOException {
    if (currentName != null) {
      writeTagOpen();
      textContent = true;
    }
    enc.outEscaped(chars,offset,count);
    //bufferedWriter.write(chars, offset, count);
  }

  /**
   * Outputs characters from array buffer. Not CData section.
   */
  public void writeContentCDataDirect(char[] chars, int offset, int count) throws IOException {
    if (currentName != null) {
      writeTagOpen();
      textContent = true;
    }

  enc.outDirect(chars, offset, count);
  }

  public void writeContentCDataDirect(char[] chars) throws IOException {
    writeContentCDataDirect(chars, 0, chars.length);
  }


  /**
   * Outputs text node into XML Stream.
   */
  public void writeContent(String content) throws IOException {
    if (currentName != null) {
      writeTagOpen();
      textContent = true;
    } else {
      if (content.length() != 0) textContent = true;
    }
    enc.outEscaped(content);
    //bufferedWriter.write(content);
  }

  public void writeXmlAttribute(String name, String value) throws IOException, IllegalStateException {
  }

  public void close() throws IOException {
    enc.close();
  }

  public void writeStatistics() {
    //closeStack.writeStatistics();
  }

//  private void writeXMLEscaped(String s) throws IOException {
//    int ls = s.length();
//    for (int i = 0; i < ls; i++) {
//      char ch = s.charAt(i);
//      if (ch < 256) {
//        if (CH_TO_ENT[ch] != null) {
//          enc.out(CH_TO_ENT[ch]);
//        } else {
//          enc.out(ch);
//        }
//      } else {
//        bufferedWriter.write("&#");
//        bufferedWriter.write(Integer.toString(ch));
//        bufferedWriter.write(';');
//      }
//    }
//  }

  private void writeXMLEscaped(char[] s,int offset, int count) throws IOException {
    enc.outEscaped(s, offset, count);
//    for (int i = 0; i < count; i++) {
//      char ch = s[i+offset];
//      if (ch < 256) {
//        if (CH_TO_ENT[ch] != null) {
//          bufferedWriter.write(CH_TO_ENT[ch]);
//        } else {
//          bufferedWriter.write(ch);
//        }
//      } else {
//        bufferedWriter.write("&#");
//    bufferedWriter.write(Integer.toString(ch));
//        bufferedWriter.write(';');
//      }
//    }
  }

  private void writeXMLEscaped(char[] s) throws IOException {
    enc.outEscaped(s, 0, s.length);
//    int ls = s.length;
//    for (int i = 0; i < ls; i++) {
//      char ch = s[i];
//      if (ch < 256) {
//        if (CH_TO_ENT[ch] != null) {
//          bufferedWriter.write(CH_TO_ENT[ch]);
//        } else {
//          bufferedWriter.write(ch);
//        }
//      } else {
//        bufferedWriter.write("&#");
//    bufferedWriter.write(Integer.toString(ch));
//        bufferedWriter.write(';');
//      }
//    }
  }

//  private String escapeXML(String s) {
//    //StringBuffer b = new StringBuffer();
//    buffer.setLength(0);
//    int ls = s.length();
//
//    for (int i = 0; i < ls; i++) {
//      char ch = s.charAt(i);
//
//      if (ch < 256) {
//        String ent = CH_TO_ENT[ch];
//
//        if (ent == null) {
//          buffer.append(ch);
//        } else {
//          buffer.append('&').append(ent).append(';');
//        }
//      } else {
//        buffer.append("&#").append(Integer.toString(ch)).append(';');
//      }
//    }
//
//    return buffer.toString();
//  }

  private  PrefixHolder getPrefixHolder(String namespace) {
    if (ctop == 0) {
      return new PrefixHolder(namespace);
    } else {
      ctop--;
      cache[ctop].namespace = namespace;
      return cache[ctop];
    }
  }

  private void  returnPrefixHolder(PrefixHolder ph) {
    if (ctop<cache.length) {
      ph.init();
      cache[ctop] = ph;
      ctop++;
    }
  }

  private PrefixHolder[] cache = new PrefixHolder[128];
  private int ctop = 0;

  
  
  public void outputElementWithNoIndent(Element elem) throws IOException {
    if (currentName != null) {
      writeTagOpen();
    }
    if(indent) {
      enc.out('\n');
      enc.out(indentString.toString());
    }
    boolean indentBcp = indent;
    indent = false;
    outputDomToWriter(this, elem);
    indent = indentBcp;
  }
  
  /**
   * Writer DOM Node to XMLTokenWriter and tries to preserve namespace associations.
   * @param writer
   * @param node
   * @throws IOException
   */
  public static void outputDomToWriter(XMLTokenWriter writer, Node node) throws IOException {
    if (node == null) {
      return;
    }
    switch (node.getNodeType()) {
      case Node.TEXT_NODE:
      case Node.ENTITY_REFERENCE_NODE: {
        writer.writeContent(node.getNodeValue());
        break;
      }
      case Node.COMMENT_NODE: {
        writer.writeComment(node.getNodeValue());
        break;
      }
      case Node.ELEMENT_NODE: {
        Element element = (Element) node;
        String localName = element.getLocalName();
        if (localName == null) {
          localName = element.getNodeName();
        }
        String namespace = element.getNamespaceURI();
        writer.enter(namespace,localName);
        NamedNodeMap attributes = element.getAttributes();
        for (int i=0; i<attributes.getLength(); i++) {
          Attr attribute = (Attr) attributes.item(i);
          if (attribute.getName().startsWith("xmlns:")) {
            // it is namespace declaration.
            String dprefix = DOM.qnameToLocalName(attribute.getName());
            String dnamespace = attribute.getValue();
            if (!dprefix.equals(writer.getPrefixForNamespace(dnamespace))) {
              writer.setPrefixForNamespace(dprefix,dnamespace);
            }
          } else if ("xmlns".equals(attribute.getName())) {
            // emptynamespace prefix
            String dnamespace = attribute.getValue();
            if (!"".equals(writer.getPrefixForNamespace(dnamespace))) {
              writer.setPrefixForNamespace("", dnamespace);
            }
          }
            else {
            writer.writeAttribute(attribute.getNamespaceURI(),attribute.getLocalName(),attribute.getValue());
            
          }
        }
        Node child = element. getFirstChild();
        while (child != null) {
          outputDomToWriter(writer,child);
          child = child.getNextSibling();
        }
        writer.leave();
        break;
      }

    }
  }

  private AttributeHandler handler;
  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.parser.tokenizer.XMLTokenWriter#setAttributeHandler(com.sap.engine.lib.xml.parser.tokenizer.AttributeHandler)
   */
  public void setAttributeHandler(AttributeHandler handler) {
    this.handler = handler;
  }
  
  public static void registerNamespaces(XMLTokenWriter writer, Set<String> nss) throws IOException {
    Iterator<String> itr = nss.iterator();
    String pref = "rn"; //Register Namespaces
    int count = 0;
    while (itr.hasNext()) {
      String ns = itr.next();
      if (ns.length() > 0) {
        if (writer.getPrefixForNamespace(ns) == null) {
          writer.setPrefixForNamespace(pref + (count++), ns);
        }
      }
    }
  }

  public void setProperty(String key, Object value) {
    if (INDENT.equals(key) && value != null && value instanceof String) {
      if ("true".equalsIgnoreCase((String) value)) {
        setIndent(true);
      } else {
        setIndent(false);
      }
    }
  }
  
  
}
