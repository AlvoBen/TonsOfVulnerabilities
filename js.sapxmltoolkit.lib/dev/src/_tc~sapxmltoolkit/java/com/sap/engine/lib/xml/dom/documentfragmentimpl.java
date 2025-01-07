package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
@Deprecated
public class DocumentFragmentImpl extends NodeImpl implements DocumentFragment {

  private NodeImpl parent = null;

  protected DocumentFragmentImpl() {

  }

  protected DocumentFragmentImpl(Document owner) {
    setOwnerDocument(owner);
  }

  public DocumentFragmentImpl init(NodeImpl owner) {
    super.init();
    this.parent = owner;
    return this;
  }

  public String getNodeName() {
    return "#document-fragment";
  }

  public short getNodeType() {
    return Node.DOCUMENT_FRAGMENT_NODE;
  }

//  public Node getNextSibling() {
//    return (parent == null) ? null : parent.getChildAfter(this);
//  }

//  public Node getPreviousSibling() {
//    return (parent == null) ? null : parent.getChildBefore(this);
//  }

  public Node cloneNode(boolean deep) {
    DocumentFragmentImpl result = new DocumentFragmentImpl(getOwnerDocument());
    result.init(null);
    transferData(this, result, deep);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }

  protected boolean checkChildNodeType(Node node) {
    int t = node.getNodeType();
    return super.checkChildNodeType(node) && (t != Node.ENTITY_NODE);
  }

  public String toString() {
    StringBuffer result = new StringBuffer(1000);
    NodeList children = getChildNodes();

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        result.append(children.item(i).toString());
      } 
    }

    return result.toString();
  }

}

