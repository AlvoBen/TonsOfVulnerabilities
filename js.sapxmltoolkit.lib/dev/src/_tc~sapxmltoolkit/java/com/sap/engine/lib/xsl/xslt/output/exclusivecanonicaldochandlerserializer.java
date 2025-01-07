/*
 * Created on 2004-3-12
 * 
 * @author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xsl.xslt.output;
import java.io.OutputStream;
import java.util.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class ExclusiveCanonicalDocHandlerSerializer extends CanonicalDocHandlerSerializer {
  protected String[] inclusiveNamespaces = null;
  protected TreeMap usedNamespaces = new TreeMap();
  protected Hashtable currentNamespaces = null;
  protected Hashtable namespacesInScope = null;
  
  public void init() {
    super.init();
    inclusiveNamespaces = null;
  }
  public void init(OutputStream stream, Properties prop) throws OutputException {
    super.init(stream, prop);
    inclusiveNamespaces = null;
  }
  public void init(String[] inclusiveNamespaces) {
    super.init();
    this.inclusiveNamespaces = inclusiveNamespaces;
  }
  public void init(OutputStream stream, Properties prop, String[] inclusiveNamespaces, Hashtable namespacesInScope) throws OutputException {
    super.init(stream, prop);
    this.inclusiveNamespaces = inclusiveNamespaces;
    this.namespacesInScope = namespacesInScope;
  }
  //TODO: see which namespace is this element !!!
  public void startElementStart(CharArray uri, CharArray localName, CharArray qName) throws Exception {
    super.startElementStart(uri, localName, qName);
    usedNamespaces.clear();
    if ((namespacesInScope != null)&&(inclusiveNamespaces != null)){
      for (int i=0;i<inclusiveNamespaces.length;i++){
        if (namespacesInScope.containsKey(inclusiveNamespaces[i])){
          usedNamespaces.put(inclusiveNamespaces[i], namespacesInScope.get(inclusiveNamespaces[i]));
        }
      }
      namespacesInScope = null;
    }
    int i = qName.indexOf(':');
//    Hashtable 
    currentNamespaces = namespacesStack.isEmpty() ? SignatureContext.EMPTY : (Hashtable) namespacesStack.peek();
    if (i != -1) {
      //      CharArray temp = new CharArray();
      //      temp.substring(qName,0,i);
      String temp = qName.toString().substring(0, i);
      String tempuri = (String) currentNamespaces.get(temp);
      if (!SignatureContext.equals(tempuri, uri.getString())) {
        usedNamespaces.put(temp, uri.getString());
      }
    } else {
      String tempuri = (String) currentNamespaces.get(SignatureContext.DEFAULT_STR);
      if (!SignatureContext.equals(tempuri, uri.getString())) {
        usedNamespaces.put(SignatureContext.DEFAULT_STR, uri.getString());
      }
    }
  }
  public void startElementEnd(boolean b) throws Exception {
//    Hashtable 
//    currentNamespaces = namespacesStack.isEmpty() ? EMPTY : (Hashtable) namespacesStack.peek();
    if ((currentAttribs != null) && (currentAttribs.size() > 0)) {
      int length = currentAttribs.size();
      // used namespaces in this element
      for (int i = 0; i < length; i++) {
        AttributeRepr a = (AttributeRepr) currentAttribs.elementAt(i);
        CharArray namespaceDecl = SignatureContext.getNamespaceDeclaration(a.prefix, a.localName);
        if (namespaceDecl == null) {
          // this prefix is used in this element attribute axis -so add in used
          // namespaces if
          // it is not declared in any output ancestor or is in the inclusive
          // namesapce list
          //TODO: XMLNS, Id, etc - isSpecial!!!!
          if ((a.prefix!=null)&&(a.prefix.length() != 0)) {
            //CharArray prefix = ((a.prefix == null) || (a.prefix.length() == 0)) ? DEFAULT : a.prefix;
            String tempuri = (String) currentNamespaces.get(a.prefix.toString());
            if (!SignatureContext.equals(tempuri, a.uri)) {
              usedNamespaces.put(a.prefix.toString(), (a.uri == null) ? "" : a.uri);
            }
          }
        } else {
          if (!SignatureContext.XMLNS.equals(a.prefix)) {
            String tempuri = (String) currentNamespaces.get(namespaceDecl.toString());
            if (!SignatureContext.equals(tempuri, a.uri)) {
              usedNamespaces.put(namespaceDecl.toString(), (a.uri == null) ? "" : a.uri);
            }
          }
        }
      }
      currentAttribs.ensureCapacity(currentAttribs.size() + usedNamespaces.size());
      Iterator enum1 = usedNamespaces.keySet().iterator();
      
      if (enum1.hasNext()) {
        currentNamespaces = (Hashtable) currentNamespaces.clone();
        while (enum1.hasNext()) {
          String name = (String) enum1.next();
          String uri = (String) usedNamespaces.get(name);
          AttributeRepr rep = null;
          if (SignatureContext.XML.equals(name)&&Constants.XML_NAMESPACE.equals(uri)){
            continue;
          } else if (SignatureContext.DEFAULT.equals(name)) {
            rep = new AttributeRepr(null, SignatureContext.XMLNS, SignatureContext.XMLNS, copyCharArray(uri));
          } else {
            CharArray qname = new CharArray(10, 6 + name.length());
            qname.append(SignatureContext.XMLNS);
            qname.append(':');
            qname.append(name);
            rep = new AttributeRepr(null, copyCharArray(name), qname, copyCharArray(uri));
          }
          currentAttribs.addElement(rep);
          currentNamespaces.put(name.toString(), uri);
        }
      }
      currentAttribs.sort(SignatureContext.ac);
      length = currentAttribs.size();
      for (int i = 0; i < length; i++) {
        AttributeRepr a = (AttributeRepr) currentAttribs.elementAt(i);
        indenter.attribute(copyCharArray(a.uri), a.localName, a.qname, a.value);
    //    releaseAttribRepr(a);
      }
//      releaseAttribRepr(currentAttribs);
      currentAttribs.clear();
    } else if (usedNamespaces.size()>0) {
      Iterator enum1 = usedNamespaces.keySet().iterator();
      if (enum1.hasNext()) {
        currentNamespaces = (Hashtable) currentNamespaces.clone();
        while (enum1.hasNext()) {
          String name = (String) enum1.next();
          String uri = (String) usedNamespaces.get(name);
          if (SignatureContext.DEFAULT.equals(name)) {
            indenter.attribute(null, SignatureContext.XMLNS, SignatureContext.XMLNS, copyCharArray(uri));
          } else {
            CharArray qname = new CharArray(10, 6 + name.length());
            qname.append(SignatureContext.XMLNS);
            qname.append(':');
            qname.append(name);
            indenter.attribute(null, copyCharArray(name), qname, copyCharArray(uri));
          }
          currentNamespaces.put(name.toString(), uri);
        }
      }
    }
    namespacesStack.push(currentNamespaces);
    indenter.startElement1(false);
  }
  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    CharArray namespaceDecl = SignatureContext.getNamespaceDeclaration(prefix, localName);
    if (namespaceDecl != null){
      if (isInclusive(namespaceDecl)){
        String tempuri = (String) currentNamespaces.get(namespaceDecl.toString());
        if (!SignatureContext.equals(tempuri, value.toString())) {
          usedNamespaces.put(namespaceDecl.toString(), value.toString());
        }
      }
      return;
    }
    AttributeRepr toAdd;
//    if (uri == null) {
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
  protected boolean isInclusive(CharArray name) {
    return (inclusiveNamespaces != null) && (Arrays.binarySearch(inclusiveNamespaces, name.toString()) >= 0);
  }
  
  public void release(){
    super.release();
    currentNamespaces = null;
    usedNamespaces.clear();
  }

}
