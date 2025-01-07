package com.sap.engine.lib.xml.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.util.NS;
@Deprecated
public class ElementImpl extends NodeImpl implements Element, NodeList {

  private static final int INITIAL_N_ATTRIBUTES = 4;
  private NodeImpl parent;
  private String name;
  private String prefix;
  private String uri;
  private String local;
  private AttrImpl[] attributes = null;
  private int nAttributes;
  private NamedNodeMapImplForAttributes map = null;

  public ElementImpl() {

  }

  protected ElementImpl(Document owner) {
    setOwnerDocument(owner);
  }

  protected void updateDefaultAttributesFromDTD(DocumentTypeImpl dtd) {
    // Clear all default attributes
    int i = 0;

    while (i < nAttributes) {
      if (attributes[i].isDefaultAttribute()) {
        nAttributes--;
        attributes[i] = attributes[nAttributes];
      } else {
        attributes[i].setOverriden(null);
        i++;
      }
    }

    // Retrieve new default attributes and add them
    if (dtd == null) {
      return;
    }

    String[][] a = dtd.getDefaultAttributes(name);

    if (a == null) {
      return;
    }

    String[] aQNames = a[0];
    String[] aValues = a[1];

    for (i = 0; i < aQNames.length; i++) {
      AttrImpl attrImpl = new AttrImpl(getOwnerDocument()).init(DOM.qnameToURI(aQNames[i], this), aQNames[i], true, aValues[i], this, false, false);
      attrImpl.setDefaultAttribute(true);
      boolean becomesOverridden = false;

      for (int j = 0; j < nAttributes; j++) {
        if (attributes[j].getNodeName().equals(aQNames[i])) {
          attributes[j].setOverriden(attrImpl);
          becomesOverridden = true;
          break;
        }
      }

      if (!becomesOverridden) {
        // add it
        if (attributes == null) {
          attributes = new AttrImpl[INITIAL_N_ATTRIBUTES];
        }

        ensureAttributes();
        attributes[nAttributes] = attrImpl;
        nAttributes++;
      }
    }
  }

  private void checkQualifiedName(String namespaceURI, String qualifiedName, boolean useNS) {
    if (namespaceURI == null) {
      namespaceURI = "";
    }

    if (qualifiedName == null) {
      throw new IllegalArgumentException();
    }

    if (!Symbols.isName(qualifiedName)) {
      throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "'" + qualifiedName + "' is not a valid name.");
    }

    if (!useNS) {
      return;
    }

    int indexOfColon = qualifiedName.indexOf(':');

    if (indexOfColon != qualifiedName.lastIndexOf(':')) {
      throw new DOMException(DOMException.NAMESPACE_ERR, "More than one ':' in qualified name, '" + qualifiedName + "'");
    }

    if (indexOfColon != -1) {
//      String prefix = qualifiedName.substring(0, indexOfColon);

      if (qualifiedName.startsWith("xml:")/*prefix.equals("xml")*/) {
        if (!namespaceURI.equals(NS.XML)) {
          throw new DOMException(DOMException.NAMESPACE_ERR, "Prefix is 'xml', but URI is not '" + NS.XML + "' " + "in the qualified name, '" + qualifiedName + "'");
        }
      }

      if (namespaceURI.length() == 0) {
        throw new DOMException(DOMException.NAMESPACE_ERR, "There is a prefix, but the URI is null");
      }
    }
  }

  public final ElementImpl init(String uri, String qname, boolean useNS, NodeImpl parent, boolean doCheck) {
    super.init();

    if (doCheck) {
      checkQualifiedName(uri, qname, useNS);
    }

    this.parent = parent;
    this.name = qname;

    if (useNS) {
      this.prefix = DOM.qnameToPrefix(qname);

      if ((prefix != null) && (prefix.length() == 0)) {
        prefix = null;
      }

      this.local = DOM.qnameToLocalName(qname);
      this.uri = uri;
    } else {
      this.prefix = null;
      this.local = null;
      this.uri = null;
    }

    nAttributes = 0;
    return this;
  }

  public final ElementImpl init(String name, NodeImpl parent) {
    return init(null, name, false, parent, true);
  }

  public final ElementImpl init(String uri, String qname, NodeImpl parent) {
    return init(uri, qname, true, parent, true);
  }

  public String getAttribute(String name) {
    Attr attr = this.getAttributeNode(name);
    return (attr == null) ? "" : attr.getValue();
  }

  public String getAttributeNS(String namespaceURI, String localName) {
    Attr attr = this.getAttributeNodeNS(namespaceURI, localName);
    return (attr == null) ? "" : attr.getValue();
  }

  public Attr getAttributeNode(String name) {
    for (int i = 0; i < nAttributes; i++) {
      if (attributes[i].getName().equals(name)) {
        return attributes[i];
      }
    }

    return null;
  }

  public Attr getAttributeNodeNS(String namespaceURI, String localName) {
    for (int i = 0; i < nAttributes; i++) {
      Node attr = attributes[i];
      String attrLocalName = attr.getLocalName();

      if (attrLocalName != null) {
        if (attrLocalName.equals(localName) && areNamespaceURIEqual(attr.getNamespaceURI(), namespaceURI)) {
          return (Attr) attr;
        }
      }
    }

    return null;
  }

  public String getTagName() {
    return name;
  }

  public boolean hasAttribute(String name) {
    Attr attr = this.getAttributeNode(name);
    return (attr != null);
  }

  public boolean hasAttributeNS(String namespaceURI, String localName) {
    Attr attr = this.getAttributeNodeNS(namespaceURI, localName);
    return (attr != null);
  }

  public void removeAttribute(String name) {
    Attr attr = this.getAttributeNode(name);
    if (attr != null) {
    this.removeAttributeNode(attr);
  }
  }

  public void removeAttributeNS(String namespaceURI, String localName) {
    Attr attr = this.getAttributeNodeNS(namespaceURI, localName);

    if (attr != null) {
      this.removeAttributeNode(attr);
    }
  }

  public Attr removeAttributeNode(Attr oldAttr) {
    AttrImpl oldAttr1 = (AttrImpl) oldAttr;

    if (oldAttr1.isDefaultAttribute()) {
      return oldAttr; // do not remove default attributes
    }

    for (int i = 0; i < nAttributes; i++) {
      if (attributes[i] == oldAttr) {
        if (oldAttr1.getOverridden() == null) {
          // detach it and delete it
          oldAttr1.setOwner(null);
          nAttributes--;
          attributes[i] = attributes[nAttributes];
          return oldAttr;
        } else {
          // detach it and replace it with the default
          oldAttr1.setOwner(null);
          attributes[i] = oldAttr1.getOverridden();
          return oldAttr1;
        }
      }
    }

    throw new DOMException(DOMException.NOT_FOUND_ERR, "Attempt to remove an attribute from an element which is not its owner.");
  }

  public void setAttribute(String name, String value) {
    AttrImpl attr = (AttrImpl) getAttributeNode(name);

    if (attr != null) {
      if (attr.isDefaultAttribute()) {
        if (attr.getValue().equals(value)) {
          return; // attempt to override the default value with the same value - ignore it
        }
      } else {
        attr.setNodeValue(value);
        return; // use the same node but change the value
      }
    }

    attr = new AttrImpl(getOwnerDocument()).init(name, this, value);
    setAttributeNode(attr);
  }

  public void setAttributeNS(String namespaceURI, String qname, String value) {
    AttrImpl attr = (AttrImpl) getAttributeNodeNS(namespaceURI, qname);

    if (attr != null) {
      if (attr.isDefaultAttribute()) {
        if (attr.getValue().equals(value)) {
          return; // attempt to override the default value with the same value - ignore it
        }
      } else {
        attr.setNodeValue(value);
        return; // use the same node but change the value
      }
    }

    attr = new AttrImpl(getOwnerDocument()).init(namespaceURI, qname, this, value);
    setAttributeNodeNS(attr);
  }

  private AttrImpl replaceAttr(int index, AttrImpl x) {
    AttrImpl y = attributes[index];

    if (y.isDefaultAttribute()) {
      x.setOverriden(y);
      attributes[index] = x;
      return null;
    } else {
      AttrImpl yOverridden = y.getOverridden();
      y.setOverriden(null);
      y.setOwner(null);
      x.setOverriden(yOverridden);
      x.setOwner(this);
      attributes[index] = x;
      return y;
    }
  }

  public Attr setAttributeNode(Attr newAt) {
    //AttrImpl newAttr = (AttrImpl)newAttr1;
    if (newAt == null) {
      throw new IllegalArgumentException("A null attribute cannot be set.");
    }

    AttrImpl newAttr1 = (AttrImpl) newAt;
    Element oe = newAttr1.getOwnerElement();

    if ((oe != null) && (oe != this)) {
      throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR, "The attribute node being set already belongs to another element.");
    }

    checkCompatibleOwnerDocuments(newAttr1, this);
    String newAttrName = newAttr1.getNodeName();

    for (int i = 0; i < nAttributes; i++) {
      if (newAttrName.equals(attributes[i].getNodeName())) {
        return replaceAttr(i, newAttr1);
      }
    }

    ensureAttributes();
    attributes[nAttributes] = newAttr1;
    newAttr1.setOwner(this);
    nAttributes++;
    return null;
  }

  public Attr setAttributeNodeNS(Attr newAt) {
    if (newAt == null) {
      throw new IllegalArgumentException("A null attribute cannot be set.");
    }

    AttrImpl newAttr1 = (AttrImpl) newAt;
    Element oe = newAttr1.getOwnerElement();

    if ((oe != null) && (oe != this)) {
      throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR, "The attribute node being set already belongs to another element.");
    }

    checkCompatibleOwnerDocuments(newAttr1, this);
    String newAttrUri = newAttr1.getNamespaceURI();
    newAttrUri = (newAttrUri == null) ? "" : newAttrUri;
    String newAttrLocal = newAttr1.getLocalName();

    for (int i = 0; i < nAttributes; i++) {
      String uri = attributes[i].getNamespaceURI();
      uri = (uri == null) ? "" : uri;

      if (uri.equals(newAttrUri) && attributes[i].getLocalName().equals(newAttrLocal)) {
        return replaceAttr(i, newAttr1);
      }
    }

    ensureAttributes();
    attributes[nAttributes] = newAttr1;
    newAttr1.setOwner(this);
    nAttributes++;
    return null;
  }

  private void ensureAttributes() {
    if (attributes == null) {
      attributes = new AttrImpl[INITIAL_N_ATTRIBUTES];
    }

    if (attributes.length == nAttributes) {
      AttrImpl[] old = attributes;
      attributes = new AttrImpl[2 * nAttributes];
      System.arraycopy(old, 0, attributes, 0, nAttributes);
    }
  }

  public void setPrefix(String prefix) {
    if (prefix == null) {
      throw new IllegalArgumentException();
    }

    if (!com.sap.engine.lib.xml.Symbols.isName(prefix)) {
      throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "'" + prefix + "' is not a valid prefix.");
    }

    if ((prefix.indexOf(':') != -1) || (prefix.equals("xml") && !NS.XML.equals(uri)) || (prefix.equals("xmlns") && !NS.XMLNS.equals(uri))) {
      throw new DOMException(DOMException.NAMESPACE_ERR, "'" + prefix + "' is not a valid prefix.");
    }

    if ((uri == null) || (uri.length() == 0)) {
      throw new DOMException(DOMException.NAMESPACE_ERR, "Can't set a prefix to an element whose uri is null");
    }

    this.prefix = prefix;
  }

  public void setNodeValue(String value) {

  }

  public short getNodeType() {
    return ELEMENT_NODE;
  }

  public String getNodeName() {
    return name;
  }

  public Node cloneNode(boolean deep) {
    ElementImpl r = new ElementImpl(getOwnerDocument());
    ensureAttributes();
    super.transferData(this, r, deep);
    r.parent = null;
    r.nAttributes = nAttributes;
    r.attributes = new AttrImpl[attributes.length];
    r.map = null;

    if (deep) {
      for (int i = 0; i < nAttributes; i++) {
        r.attributes[i] = (AttrImpl) attributes[i].cloneNode(true);
        ((AttrImpl) r.attributes[i]).setOwner(r);
      }
    } else {
      System.arraycopy(attributes, 0, r.attributes, 0, nAttributes);
    }

    r.name = name;
    r.prefix = prefix;
    r.uri = uri;
    r.local = local;
    r.setOwnerDocument(getOwnerDocument());
    return r;
  }

  public NamedNodeMap getAttributes() {
    if (map == null) {
      map = new NamedNodeMapImplForAttributes();
    }

    return map;
  }

  public String getLocalName() {
    return local;
  }

  public String getNamespaceURI() {
    if (uri != null && uri.length() == 0) {
      return null;
    } else {
      return uri;
    }
  }

//  public Node getNextSibling() {
//    return (parent == null) ? null : parent.getChildAfter(this);
//  }

//  public Node getPreviousSibling() {
//    return (parent == null) ? null : parent.getChildBefore(this);
//  }

  public Node getParentNode() {
    return parent;
  }

  public String getPrefix() {
    return prefix;
  }

  public boolean hasAttributes() {
    return (nAttributes != 0);
  }

  // Inner class
  public class NamedNodeMapImplForAttributes implements NamedNodeMap, java.io.Serializable {

    private NamedNodeMapImplForAttributes() {

    }

//    private NamedNodeMapImplForAttributes init() {
//      return this;
//    }

    // From interface NamedNodeMap - iterator for the attributes
    public int getLength() {
      return nAttributes;
    }

    public Node getNamedItem(String name) {
      return getAttributeNode(name);
    }

    public Node getNamedItemNS(String namespaceURI, String localName) {
      return getAttributeNodeNS(namespaceURI, localName);
    }

    public Node item(int index) {
      if ((index < 0) || (index >= nAttributes)) {
        return null;
      }

      return attributes[index];
    }

    public Node removeNamedItem(String name) {
      Attr r = getAttributeNode(name);

      if (r == null) {
        throw new DOMException(DOMException.NOT_FOUND_ERR, "There is no named item '" + name + "' in this NamedNodeMap.");
      }

      removeAttributeNode(r);
      return r;
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) {
      Attr r = getAttributeNodeNS(namespaceURI, localName);

      if (r == null) {
        throw new DOMException(DOMException.NOT_FOUND_ERR, "There is no named item '" + name + "' in this NamedNodeMap.");
      }

      removeAttributeNode(r);
      return r;
    }

    public Node setNamedItem(Node arg) {
      checkCompatibleOwnerDocuments(ElementImpl.this, arg);
      return setAttributeNode((Attr) arg);
    }

    public Node setNamedItemNS(Node arg) {
      checkCompatibleOwnerDocuments(ElementImpl.this, arg);
      return setAttributeNodeNS((Attr) arg);
    }

  }

  ;
  protected void setParent(NodeImpl parent) {
    this.parent = parent;
  }

  public String toString() {
    return serializeNode(this);
  }

  public static void main(String[] args) throws Exception {
    SystemProperties.setProperty(javax.xml.parsers.DocumentBuilderFactory.class.getName(), com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl.class.getName());
    Document d = (Document) DOM.parseXMLFromAString("<!DOCTYPE a [<!ELEMENT a EMPTY><!ATTLIST a x CDATA '123'>]><a/>");
    Element a = d.getDocumentElement();
    LogWriter.getSystemLogWriter().println(a.getAttribute("x"));
    a.setAttribute("x", "567");
    LogWriter.getSystemLogWriter().println(a.getAttribute("x"));
    a.removeAttribute("x");
    LogWriter.getSystemLogWriter().println(a.getAttribute("x"));
  }
  
  //jdk 5.0 org.w3c.dom.Element methods - not implemented!
  
  public TypeInfo getSchemaTypeInfo(){
  	return null;
  }


  public void setIdAttribute(String name, boolean isId) throws DOMException {
  	
  }

 
  public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
  	
  }

  
  public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException{
  	
  }
  
 
}

