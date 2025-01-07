package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
@Deprecated
public abstract class CharacterDataImpl extends Base implements CharacterData {

  protected NodeImpl parent;
  protected String data;

  public CharacterDataImpl() {

  }

  protected CharacterDataImpl(Document owner) {
    setOwnerDocument(owner);
  }

  protected void clear() {
    parent = null;
    data = null;
  }

  protected final CharacterDataImpl init(String data, NodeImpl parent) {
    this.data = data;
    this.parent = parent;
    return this;
  }

  public final void appendData(java.lang.String arg) {
    data = data.concat(arg);
  }

  public final void deleteData(int offset, int count) {
    if ((offset < 0) || (count < 0) || (offset >= data.length())) {
      throw new DOMException(DOMException.INDEX_SIZE_ERR, "Cannot delete data, index out of bounds.");
    }

    if (offset + count >= data.length()) {
      data = data.substring(0, offset);
    } else {
      data = data.substring(0, offset) + data.substring(offset + count);
    }
  }

  public final String getData() {
    return data;
  }

  public final int getLength() {
    return data.length();
  }

  public final void insertData(int offset, String arg) {
    if ((offset < 0) || (offset > data.length())) {
      throw new DOMException(DOMException.INDEX_SIZE_ERR, "Cannot insert data, index out of bounds.");
    }

    data = data.substring(0, offset) + arg + data.substring(offset);
  }

  public final void replaceData(int offset, int count, java.lang.String arg) {
    if ((offset < 0) || (count < 0) || (offset >= data.length())) {
      throw new DOMException(DOMException.INDEX_SIZE_ERR, "Cannot replace data, index out of bounds.");
    }

    if (offset + count >= data.length()) {
      data = data.substring(0, offset) + arg;
    } else {
      data = data.substring(0, offset) + arg + data.substring(offset + count);
    }
  }

  public final void setData(java.lang.String data) {
    this.data = data;
  }

  public final String substringData(int offset, int count) {
    if ((offset < 0) || (count < 0) || (offset >= data.length())) {
      throw new DOMException(DOMException.INDEX_SIZE_ERR, "Cannot take substring, index out of bounds.");
    }

    if (offset + count >= data.length()) {
      return data.substring(offset);
    } else {
      return data.substring(offset, offset + count);
    }
  }

  //  protected final Document getOwnerDocument_internal() {
  //    if (parent==null) {
  //      return null;
  //    }
  //    return parent.getOwnerDocument_internal();
  //  }
  //
  //  public final Document getOwnerDocument() {
  //    return (parent == null) ? null : parent.getOwnerDocument_internal();
  //  }
  protected final void setParent(NodeImpl parent) {
    this.parent = parent;
  }

//  public final Node getNextSibling() {
//    return (parent == null) ? null : parent.getChildAfter(this);
//  }

//  public final Node getPreviousSibling() {
//    return (parent == null) ? null : parent.getChildBefore(this);
//  }

  public final String getNodeValue() {
    return data;
  }

  public final Node getParentNode() {
    return parent;
  }

  public final void setNodeValue(String value) {
    data = value;
  }

  public void normalize() {
    if (getNodeType() != ELEMENT_NODE && getNodeType() != DOCUMENT_NODE) {
      return;
    }
    NodeList nd = getChildNodes();

    for (int i = 0; i < nd.getLength();) {
      if (i < nd.getLength() - 1) {
        if ((nd.item(i).getNodeType() == TEXT_NODE) && (nd.item(i + 1).getNodeType() == TEXT_NODE)) {
          ((Text) nd.item(i)).appendData(((Text) nd.item(i + 1)).getData());
          removeChild(nd.item(i + 1));
        } else {
          ((Base) nd.item(i)).normalize();
          i++;
        }
      } else {
        ((Base) nd.item(i)).normalize();
        i++;
      }
    } 
  }

}

