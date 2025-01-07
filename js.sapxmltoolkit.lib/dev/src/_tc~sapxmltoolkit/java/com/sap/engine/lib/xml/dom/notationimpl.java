package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
@Deprecated
public final class NotationImpl extends Base implements Notation {

  //private Document ownerDocument;
  private String name;
  private String publicId;
  private String systemId;

  protected NotationImpl() {

  }

  protected NotationImpl(Document owner) {
    setOwnerDocument(owner);
  }

  protected NotationImpl init(String name, String publicId, String systemId) {
    this.name = name;
    this.publicId = publicId;
    this.systemId = systemId;
    return this;
  }

  public String getPublicId() {
    return publicId;
  }

  public String getSystemId() {
    return systemId;
  }

  public Node appendChild(Node newChild) {
    throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Notation can not have child nodes.");
  }

  public Node cloneNode(boolean deep) {
    NotationImpl result = new NotationImpl(getOwnerDocument()).init(name, publicId, systemId);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }

  public NamedNodeMap getAttributes() {
    return null;
  }

  public Node getFirstChild() {
    return null;
  }

  public Node getLastChild() {
    return null;
  }

  public java.lang.String getLocalName() {
    return null;
  }

  public java.lang.String getNamespaceURI() {
    return null;
  }

  public Node getNextSibling() {
    return null;
  }

  public short getNodeType() {
    return NOTATION_NODE;
  }

  public String getNodeName() {
    return name;
  }

  public java.lang.String getNodeValue() {
    return null;
  }

  public Node getParentNode() {
    return null;
  }

  public java.lang.String getPrefix() {
    return null;
  }

  public Node getPreviousSibling() {
    return null;
  }

  public boolean hasAttributes() {
    return false;
  }

  public boolean hasChildNodes() {
    return false;
  }

  public Node insertBefore(Node newChild, Node refChild) {
    return null; //xxx
  }

  public void normalize() {
    //xxx
  }

  public Node removeChild(Node oldChild) {
    return null; //xxx
  }

  public Node replaceChild(Node newChild, Node oldChild) {
    return null; //xxx
  }

  public void setNodeValue(java.lang.String nodeValue) {
    //xxx
  }

}

