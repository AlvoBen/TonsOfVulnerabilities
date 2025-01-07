package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

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
public class EntityReferenceImpl extends NodeImpl implements EntityReference {

  private String name = null;
  //private Document ownerDocument;
  protected NodeImpl parent;

  protected EntityReferenceImpl() {

  }

  protected EntityReferenceImpl(Document owner) {
    setOwnerDocument(owner);
  }

  public final EntityReferenceImpl init(String name, Document owner) {
    this.ownerDocument = owner;
    this.name = name;
    return this;
  }

  public String getNodeName() {
    return name;
  }

  public void setNodeValue(String value) throws DOMException {
    // Nick: CTS works only without throwing an exception
    // throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "Entity content can not be modified.");
  }

  public short getNodeType() {
    return Node.ENTITY_REFERENCE_NODE;
  }

  public Node cloneNode(boolean deep) {
    EntityReferenceImpl result = new EntityReferenceImpl();
    result.init(this.name, ownerDocument);
    super.transferData(this, result, deep);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }

//  public final Node getNextSibling() {
//    return (parent == null) ? null : parent.getChildAfter(this);
//  }

//  public final Node getPreviousSibling() {
//    return (parent == null) ? null : parent.getChildBefore(this);
//  }

  protected final void setParent(NodeImpl parent) {
    this.parent = parent;
  }

}

