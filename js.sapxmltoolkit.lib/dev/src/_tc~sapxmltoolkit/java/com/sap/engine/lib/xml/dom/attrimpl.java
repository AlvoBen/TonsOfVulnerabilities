package com.sap.engine.lib.xml.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;


import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.util.NS;

/**
 * Title:        xml2000
 * Description:  org.w3c.dom.* ;
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Chavdar Baykov, Chavdarb@abv.bg
 * @version      August 2001
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public class AttrImpl extends NodeImpl implements Attr {

  private String qname = null;
  private String localName = null;
  private String prefix = null;
  private String namespaceURI = null;
  private String value = "";
  private NodeImpl ownerElement = null;
  boolean specified = true;
  private boolean isDefaultAttribute = false;
  AttrImpl overridden = null;
  private boolean bUseNS = false;

  public AttrImpl() {
    setParent(null);
  }

  public AttrImpl(Document owner) {
    setParent(null);
    setOwnerDocument(owner);
  }

  private void checkQualifedName(String namespaceURI, String qualifiedName, boolean useNS) {
    if (qualifiedName == null) {
      throw new IllegalArgumentException();
    }

    if (!Symbols.isName(qualifiedName)) {
      throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "'" + qualifiedName + "' is not a valid name.");
    }

    if (!useNS) {
      return;
    }

    if (namespaceURI == null) {
      namespaceURI = "";
    }

    int indexOfColon = qualifiedName.indexOf(':');

    if (indexOfColon != qualifiedName.lastIndexOf(':')) {
      throw new DOMException(DOMException.NAMESPACE_ERR, "More than one ':' in qualified name, '" + qualifiedName + "'");
    }

    if (indexOfColon != -1) {
      String prefix = qualifiedName.substring(0, indexOfColon);

      if (prefix.equals("xml")) {
        if (!namespaceURI.equals(NS.XML)) {
          throw new DOMException(DOMException.NAMESPACE_ERR, "Prefix is 'xml', but URI is not '" + NS.XML + "' " + "in the qualified name, '" + qualifiedName + "'");
        }
      }

      if (prefix.equals("xmlns")) {
        if (!namespaceURI.equals(NS.XMLNS)) {
          throw new DOMException(DOMException.NAMESPACE_ERR, "Prefix is 'xmlns', but URI is not '" + NS.XMLNS + "' " + "in the qualified name, '" + qualifiedName + "'");
        }
      }

      if (namespaceURI.length() == 0) {
        throw new DOMException(DOMException.NAMESPACE_ERR, "There is a prefix, but the URI is null");
      }
    } else {
      if (qualifiedName.equals("xmlns")) {
        if (!namespaceURI.equals(NS.XMLNS)) {
          throw new DOMException(DOMException.NAMESPACE_ERR, "QName is 'xmlns', but URI is not '" + NS.XMLNS + "' " + "in the qualified name, '" + qualifiedName + "'");
        }
        //namespaceURI = NS.XMLNS;
      }
    }
  }

  public final AttrImpl init(String uri, String qname, boolean useNS, String value, NodeImpl owner, boolean doCheck, boolean isSpecified) {
    if (doCheck) {
      checkQualifedName(uri, qname, useNS);
    }

    if (!useNS) {
      // Fix it up - because some classes rely on the presence of a
      // local name for "xmlns:..." and "xmlns" attributes
      if (qname.startsWith("xmlns")) {
        useNS = true;
        uri = NS.XMLNS;
      }
    }
    bUseNS = useNS;

    this.qname = qname;
    this.ownerElement = owner;

    if (useNS) {
      prefix = DOM.qnameToPrefix(qname);

      if ((prefix != null) && (prefix.length() == 0)) {
        prefix = null;
      }

      this.localName = DOM.qnameToLocalName(qname);
      this.namespaceURI = uri;
    } else {
      prefix = null;
      this.localName = null;
      this.namespaceURI = null;
    }

    this.value = (value == null) ? "" : value;
    this.specified = isSpecified;
    this.isDefaultAttribute = !isSpecified;
    return this;
  }

  public final AttrImpl init(String name, NodeImpl owner) {
    init(null, name, false, null, owner, true, true);
    return this;
  }

  public final AttrImpl init(String name, NodeImpl owner, String value) {
    return init(null, name, false, value, owner, true, true);
  }

  public final AttrImpl init(String uri, String name, NodeImpl owner) {
    return init(uri, name, true, null, owner, true, true);
  }

  public final AttrImpl init(String uri, String name, NodeImpl owner, String value) {
    return init(uri, name, true, value, owner, true, true);
  }

  public String getName() {
    return qname;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Element getOwnerElement() {
    return (ElementImpl) ownerElement;
  }

  public boolean getSpecified() {
    return (!isDefaultAttribute) && specified;// && (overridden != null);
  }

  public String getNodeName() {
    return qname;
  }

  public String getNodeValue() throws DOMException {
    return value;
  }

  public void setNodeValue(String value) throws DOMException {
    this.value = value;
  }

  public short getNodeType() {
    return Node.ATTRIBUTE_NODE;
  }

  public void setOwner(NodeImpl owner) {
    this.ownerElement = owner;
  }

  public Node cloneNode(boolean deep) {
    AttrImpl result = new AttrImpl();
    result.init(this.namespaceURI, this.qname, bUseNS, this.value, null, false, this.specified);
    transferData(this, result, deep);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }

  public String getNamespaceURI() {
    if ((namespaceURI == null) || (namespaceURI.length() == 0)) {
      return null;
    }

    return namespaceURI;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) throws DOMException {
    if ((prefix == null) || com.sap.engine.lib.xml.Symbols.isName(prefix) == false) {
      throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Invalid prefix");
    }

    if ((namespaceURI == null) || (prefix.equals("xml") && !namespaceURI.equals(NS.XML)) || (prefix.equals("xmlns") && !namespaceURI.equals(NS.XMLNS)) || (qname.equals("xmlns"))) {
      throw new DOMException(DOMException.NAMESPACE_ERR, "Invalid prefix: 'xml', 'xmlns', or a QName that is 'xmlns'");
    }

    if (!Symbols.isNCName(prefix)) {
      throw new DOMException(DOMException.NAMESPACE_ERR, "Invalid prefix, '" + prefix + "'");
    }

    this.prefix = prefix.trim();
    repairName();
  }

  public String getLocalName() {
    return localName;
  }

  /**
   * Repairs qName when it seems broken
   */
  private void repairName() {
    if (prefix == null) {
      return;
    }

    if (prefix.equals("")) {
      prefix = null;
      return;
    }

    qname = prefix + localName;
  }

  public String toString() {
    String ret = getName() + "='" + getValue() + "'";
    return ret;
  }

  public void normalize() {

  }

  protected boolean checkChildNodeType(Node node) {
    int t = node.getNodeType();
    return (t == Node.TEXT_NODE) || (t == Node.ENTITY_REFERENCE_NODE);
  }

  protected boolean isDefaultAttribute() {
    return isDefaultAttribute;
  }

  protected void setDefaultAttribute(boolean defaultAttribute) {
    isDefaultAttribute = defaultAttribute;
  }

  protected AttrImpl getOverridden() {
    return overridden;
  }

  protected void setOverriden(AttrImpl overriden) {
    this.overridden = overriden;
  }

	public TypeInfo getSchemaTypeInfo() {
		return null;
	}

	public boolean isId() {
		return false;
	}

}

