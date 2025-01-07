package com.sap.engine.lib.xsl.xslt.output;

import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;

/**
 * Outputs canonical xml
 * 
 * @author Bogomil Kovachev
 * @author Aleksandar Aleksandrov
 */
public class CanonicalDocHandlerSerializer extends DocHandlerSerializer {

  /**
	 * Canonical XML specifocation says:
	 * 
	 * "Also, a trailing #xA is rendered after the closing PI symbol for PI
	 * children of the root node with a lesser document order than the document
	 * element, and a leading #xA is rendered before the opening PI symbol of PI
	 * children of the root node with a greater document order than the document
	 * element."
	 * 
	 * (It says the same about comment nodes).
	 */
  protected boolean documentReached = false;
  protected int depthCounter = 0;
  protected boolean commented = true;
  protected SortableVector currentAttribs = null;

  protected Stack namespacesStack = new Stack();

  public void init() {
    documentReached = false;
    depthCounter = 0;
    commented = true;
    if (currentAttribs != null) {
      currentAttribs.clear();
    }

    holder = new InstanceHolder();
    method = holder.getMethodXML();

    indenter = holder.getIndenterEmpty();
    cDataSectionElements.clear(); // = new HashSet();
    outputProperties = null;
    options.clear(); // = new HashSet();
    firstElementReached = false;
    options.addAll(Options.DEFAULT);
    encoder.setOwner(this);
    method.setOwner(this);
    bMustCloseOnEnd = false;
//    normalizeStack(namespacesStack);
//    namespacesStack.clear();
//    normalizeStack(charArrayStack);
//    normalizeStack(attribReprStack);
  }

  public void init(OutputStream stream, Properties prop) throws OutputException {
    init();
    setOutputProperties(prop);
    setOutputStream(stream);
  }

  public CanonicalDocHandlerSerializer() {
    super();
  }

  public CanonicalDocHandlerSerializer(Writer writer, Properties outputProperties) throws OutputException {
    this();
    setOutputProperties(outputProperties);
    setWriter(writer);
  }

  public CanonicalDocHandlerSerializer(OutputStream outputStream, Properties outputProperties) throws OutputException {
    this();
    setOutputProperties(outputProperties);
    setOutputStream(outputStream);
  }

  public void retainComments(boolean $commented) {
    this.commented = $commented;
  }

  public void startDTD(String name, String publicId, String systemId) throws OutputException {}

  public void endDTD() throws OutputException {}

  public void startElementStart(CharArray uri, CharArray localName, CharArray qName) throws Exception {
    documentReached = true;
    depthCounter++;

    if (!firstElementReached) {
      firstElementReached = true;
      startDocument_internal(localName.getString());
    }

    indenter.startElement0(uri, localName, qName);
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    AttributeRepr toAdd;
    if (SignatureContext.XMLNS.equals(prefix)&&value.equals(Constants.XML_NAMESPACE)&&SignatureContext.XML.equals(localName)){
      return;
    }
//    if (uri == null) {
////TODO: use pooling mechanism for charArrays - after attribute is added release      
//      toAdd = new AttributeRepr(null, localName.copy(), qname.copy(), value.copy());
//    } else {
//      toAdd = new AttributeRepr(uri.toString(), localName.copy(), qname.copy(), value.copy());
//    }
    toAdd = getAttribRepr(uri, localName, qname, value);
    
    if (currentAttribs == null) {
      currentAttribs = new SortableVector(3, 5);
    }
    currentAttribs.add(toAdd);
  }

  public void startElementEnd(boolean b) throws Exception {
    boolean notCreated = true;
    Hashtable currentNamespaces = namespacesStack.isEmpty() ? SignatureContext.EMPTY : (Hashtable) namespacesStack.peek();
    if ((currentAttribs != null) && (currentAttribs.size() > 0)) {
      currentAttribs.sort(SignatureContext.ac);
      int length = currentAttribs.size();

      for (int i = 0; i < length; i++) {
        AttributeRepr a = (AttributeRepr) currentAttribs.elementAt(i);
        if (SignatureContext.XMLNS.equals(a.prefix)) {
          if (!a.value.equals(currentNamespaces.get(a.localName))) {
            if (notCreated) {
              currentNamespaces = (Hashtable) currentNamespaces.clone();
              notCreated = false;
            }
            currentNamespaces.put(a.localName, a.value);
          } else {
           // releaseAttribRepr(a);
            continue;
          }
        } else if (((a.prefix == null)||(a.prefix.length()==0)) && (SignatureContext.XMLNS.equals(a.localName))) {
          Object o = currentNamespaces.get(SignatureContext.DEFAULT);
          if (((o == null) && (a.value.length() != 0)) || ((o != null) && (!a.value.equals(o)))) {
            if (notCreated) {
              currentNamespaces = (Hashtable) currentNamespaces.clone();
              notCreated = false;
            }
            currentNamespaces.put(SignatureContext.DEFAULT, a.value);
          } else {
//            releaseAttribRepr(a);
            continue;
          }
        }
        indenter.attribute(copyCharArray(a.uri), a.localName, a.qname, a.value);
      //  releaseAttribRepr(a); 
      }
//      releaseAttribRepr(currentAttribs);
      currentAttribs.clear();
//TODO: free char arrays from pool!      
    }
    namespacesStack.push(currentNamespaces);
    indenter.startElement1(false);
  }

  public void endElement(CharArray uri, CharArray localName, CharArray qName, boolean b) throws Exception {
    namespacesStack.pop();
    depthCounter--;
    indenter.endElement(uri, localName, qName, false);
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    if (!firstElementReached) {
      firstElementReached = true;
      startDocument_internal("");
    }

    if (target.equals(javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING)) {
      encoder.disableOutputEscaping();
      return;
    }

    if (target.equals(javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING)) {
      encoder.enableOutputEscaping();
      return;
    }

    if (documentReached && depthCounter == 0) {
      method.characters(new char[] { 0xA }, 0, 1);
    }

    indenter.processingInstruction(target, data);

    if (!documentReached) {
      method.characters(new char[] { 0xA }, 0, 1);
    }
  }

  public void onCDSect(CharArray text) throws Exception {
    characters(text.getData(), text.getOffset(), text.length());
  }

  public void onDTDEntity(Entity entity) throws Exception {}

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {}

  public void onComment(CharArray text) throws Exception {
    if (commented) {
      if (documentReached && depthCounter == 0) {
        method.characters(new char[] { 0xA }, 0, 1);
      }
      super.onComment(text);
      if (!documentReached) {
        method.characters(new char[] { 0xA }, 0, 1);
      }

    }
  }
  
  CharArray copyCharArray(CharArray ch){
    //TODO: use pool!
//    if (charArrayStack.isEmpty()){
      return ch.copy();
//    } else {
//      CharArray chAr;
////      synchronized (charArrayStack){
//       chAr = (CharArray) charArrayStack.pop();
////      }
//      chAr.set(ch);
//      return chAr;
//    }
  }
  
  CharArray copyCharArray(String ch){
    //TODO: use pool!
//    if (charArrayStack.isEmpty()){
      return new CharArray(ch);
//    } else {
//      CharArray chAr;
////      synchronized (charArrayStack){
//       chAr = (CharArray) charArrayStack.pop();
////      }
//      chAr.set(ch);
//      return chAr;
//    }
  }
  
  
//  CharArray lcl = new CharArray("");
  
  AttributeRepr getAttribRepr(CharArray uri,CharArray localName, CharArray qname, CharArray value){
    //if (attribReprStack.isEmpty()){
      return new AttributeRepr(uri==null?null:uri.toString(), copyCharArray(localName), copyCharArray(qname), copyCharArray(value));
//    } else {
//      AttributeRepr chAr;
//      CharArray pref;
////      synchronized(attribReprStack){
//        chAr = (AttributeRepr) attribReprStack.pop();
////      }
//      if (chAr.prefix==null){
//        if (charArrayStack.isEmpty()){
//          pref = new CharArray();
//        } else {
//          pref = (CharArray) charArrayStack.pop();
//          pref.clear();
//        }
//        
//// if this is namespace declaration - default - used in current namespaces          
//        if (XMLNS.equals(chAr.localName)){
//          chAr.value = copyCharArray(value);
//          chAr.localName = copyCharArray(localName);
//        } else {
//          chAr.value.set(value);
//          chAr.localName.set(localName);
//        }
//        
//      } else {
//        pref = chAr.prefix;
////        if this is namespace declaration - used in current namespaces                    
//        if (XMLNS.equals(pref)){
//          chAr.value = copyCharArray(value);
//          chAr.localName = copyCharArray(localName);
//        } else {
//          chAr.value.set(value);
//          chAr.localName.set(localName);
//        }          
//        pref.clear();
//      }
////      synchronized (lcl){
//        lcl.clear();
//        qname.parseNS(pref, lcl);
////      }
//      chAr.uri = uri==null?null:uri.toString();
//      if (qname==XMLNS){
//        chAr.qname = copyCharArray(qname);
//      } else {
//        chAr.qname.set(qname);
//      }
//
//      
//      
//      
//      chAr.prefix = pref;
//
//      if (chAr.prefix.length()==0) {
////        synchronized(charArrayStack){
//          charArrayStack.push(chAr.prefix);
////        }
//        chAr.prefix = null;
//      }      
//      return chAr;
//    }
  }
  
//  void releaseAttribRepr(AttributeRepr repr){
////    synchronized(attribReprStack){
//      attribReprStack.push(repr);
////    }
//  }
//  
//  void releaseAttribRepr(Vector repr){
////    synchronized(attribReprStack){
//      attribReprStack.addAll(repr);
////    }
//  }
  
  void normalizeVector(Vector s){
//    synchronized (s){
      if (s==null){
        return;
      }
      if (s.capacity() > SignatureContext.MAX_STACK_SIZE) {
        s.setSize(SignatureContext.MAX_STACK_SIZE);
        s.trimToSize();
      }
      s.clear();
//    }
  }
  
  public void release(){
    normalizeVector(namespacesStack);
    normalizeVector(currentAttribs);
    documentReached = false;
    depthCounter = 0;
    commented = true;
    holder = null;
    method = null;
    indenter = null;
    cDataSectionElements.clear(); // = new HashSet();
    outputProperties = null;
    options.clear();     
  }

}
