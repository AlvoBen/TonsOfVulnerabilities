package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
import java.util.*;
import com.sap.engine.lib.xml.*;
@Deprecated
public final class DocumentTypeImpl extends Base implements DocumentType {

  private String name;
  private String publicId;
  private String systemId;
  private NamedNodeMapImpl entities = (new NamedNodeMapImpl()).init();
  private NamedNodeMapImpl notations = (new NamedNodeMapImpl()).init();
  private DocumentImpl parent;
  private Vector temp1 = new Vector();
  private Vector temp2 = new Vector();
  private static final String[] STRING_ARRAY_0 = new String[0];
  /**
   * Hashes "elementQName attributeQName" to "defaultValue"
   */
  private Hashtable defaultAttributes = new Hashtable();
  /**
   * Contains all attributes in ATTLIST-s whose type is ID
   * encoded as follows: "elementname attributename"
   */
  private Vector idAttributes = new Vector();

  protected DocumentTypeImpl() {

  }

  protected DocumentTypeImpl(Document owner) {
    setOwnerDocument(owner);
  }

  protected DocumentTypeImpl init(String name, String publicId, String systemId, DocumentImpl parent) {
    idAttributes.clear();
    defaultAttributes.clear();
    return init(name, publicId, systemId, parent, true);
  }

  protected DocumentTypeImpl init(String name, String publicId, String systemId, DocumentImpl parent, boolean doCheck) {
    if (doCheck) {
      if (name != null) {
        if (!Symbols.isName(name)) {
          throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Invalid name for a document type node, '" + name + "'");
        }

        if (name.indexOf(':') != name.lastIndexOf(':')) {
          throw new DOMException(DOMException.NAMESPACE_ERR, "Invalid name for a document type node, '" + name + "'");
        }
      }
    }

    this.name = name;
    this.publicId = publicId;
    this.systemId = systemId;
    this.parent = parent;
    entities.init();
    notations.init();
    setOwnerDocument(parent);
    return this;
  }

  public NamedNodeMap getEntities() {
    return entities;
  }

  public NamedNodeMap getNotations() {
    return notations;
  }

  public String getInternalSubset() {
    return ""; //xxx
  }

  public String getName() {
    return name;
  }

  public String getPublicId() {
    return publicId;
  }

  public String getSystemId() {
    return systemId;
  }

  //===========
  //  public Node getParentNode() {
  //    return getOwnerDocument();
  //  }
//  public Node getNextSibling() {
//    DocumentImpl owner = (DocumentImpl) getOwnerDocument();
//    return (owner == null) ? null : owner.getChildAfter(this);
//  }

//  public Node getPreviousSibling() {
//    DocumentImpl owner = (DocumentImpl) getOwnerDocument();
//    return (owner == null) ? null : owner.getChildBefore(this);
//  }

  //  protected Document getOwnerDocument_internal() {
  //    return (parent == null) ? null : parent.getOwnerDocument_internal();
  //  }
  //
  //  public Document getOwnerDocument() {
  //    return (parent == null) ? null : parent.getOwnerDocument_internal();
  //  }
  public short getNodeType() {
    return DOCUMENT_TYPE_NODE;
  }

  public String getNodeName() {
    return name;
  }

  public Node cloneNode(boolean deep) {
    DocumentTypeImpl r = new DocumentTypeImpl();
    r.name = name;
    r.publicId = publicId;
    r.systemId = systemId;

    if (deep) {
      r.entities = entities.cloneDeep();
      r.notations = notations.cloneDeep();
    } else {
      r.entities = entities;
      r.notations = notations;
    }

    r.parent = null;
    r.setOwnerDocument(getOwnerDocument());
    return r;
  }

  protected void setParent(DocumentImpl parent) {
    this.parent = parent;
  }

  private String encodeElementAttributePair(String elementName, String attributeName) {
    return elementName + ' ' + attributeName;
  }

  protected void addIdAttribute(String elementName, String attributeName) {
    idAttributes.add(encodeElementAttributePair(elementName, attributeName));
  }

  protected boolean isIdAttribute(String elementName, String attributeName) {
    return idAttributes.contains(encodeElementAttributePair(elementName, attributeName));
  }

  protected void addDefaultAttribute(String elementName, String attributeName, String defaultValue) {
    defaultAttributes.put(encodeElementAttributePair(elementName, attributeName), defaultValue);
  }

  protected String getAttributeDefault(String elementName, String attributeName) {
    return (String) defaultAttributes.get(encodeElementAttributePair(elementName, attributeName));
  }

  protected String[][] getDefaultAttributes(String elementName) {
    temp1.clear(); // store qnames here
    temp2.clear(); // store values here
    String elementName_ = elementName + ' ';
    int lElementName_ = elementName_.length();

    for (Enumeration e = defaultAttributes.keys(); e.hasMoreElements();) {
      String k = (String) e.nextElement();

      if (k.startsWith(elementName_)) {
        temp1.add(k.substring(lElementName_));
        temp2.add(defaultAttributes.get(k));
      }
    } 

    int nTemp = temp1.size();

    if (nTemp == 0) {
      return null;
    }

    String[] qnames = ((String[]) temp1.toArray(STRING_ARRAY_0));
    String[] values = ((String[]) temp2.toArray(STRING_ARRAY_0));
    return new String[][] {qnames, values, };
  }

}

