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
public class CommentImpl extends CharacterDataImpl implements Comment {

  protected CommentImpl() {

  }

  protected CommentImpl(Document owner) {
    setOwnerDocument(owner);
  }

  public String getNodeName() {
    return "#comment";
  }

  public short getNodeType() {
    return Node.COMMENT_NODE;
  }

  public Node cloneNode(boolean deep) {
    CommentImpl result = new CommentImpl(getOwnerDocument());
    result.init(super.getData(), null);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }

  public String toString() {
    return "#comment: " + data;
  }

}

