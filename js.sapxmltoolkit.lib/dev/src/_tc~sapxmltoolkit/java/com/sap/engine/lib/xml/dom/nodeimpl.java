package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
@Deprecated
public abstract class NodeImpl extends Base implements Node, NodeList {

  private static final int INITIAL_N_CHILDREN = 8;
  private Node[] children = null;
  private int nChildren;

  public NodeImpl() {

  }

  protected final void init() {
    nChildren = 0;
  }

  private void ensureChildren() {
    if (children == null) {
      children = new Node[INITIAL_N_CHILDREN];
    }

    if (children.length == nChildren) {
      Node[] old = children;
      children = new Node[2 * nChildren];
      System.arraycopy(old, 0, children, 0, nChildren);
    }
  }

  public Node appendChild(Node newChild) {
    if (newChild == null) {
      throw new IllegalArgumentException("DOM: A null child cannot be appended.");
    }

    if (newChild.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
      DocumentFragment fragment = (DocumentFragment) newChild;
      NodeList list = fragment.getChildNodes();
      int nList = list.getLength();
      Node[] temp = new Node[nList];

      for (int i = 0; i < nList; i++) {
        temp[i] = list.item(i);
      } 

      for (int i = 0; i < nList; i++) {
        appendChild(temp[i]);
      } 

      return newChild;
    }

    if ((newChild.getParentNode() != null) && !(newChild instanceof DocumentType)) {
      newChild.getParentNode().removeChild(newChild);
    }

    checkChild(newChild);
    checkCompatibleOwnerDocuments(this, newChild);

    if (newChild.getParentNode() != null) {
      newChild.getParentNode().removeChild(newChild);
    }

    ensureChildren();
    children[nChildren] = newChild;
    Base newChildBase = (Base) newChild; 
    if (nChildren > 0) {
      ((Base) children[nChildren - 1]).setNextSibling(newChildBase);
      newChildBase.setPreviousSibling(children[nChildren - 1]);
    }
    newChildBase.setNextSibling(null);
    nChildren++;
    newChildBase.setParent(this);
    return newChild;
  }

  public final NodeList getChildNodes() {
    return this;
  }

  public final Node getFirstChild() {
    if (nChildren == 0) {
      return null;
    }

    return children[0];
  }

  public final Node getLastChild() {
    if (nChildren == 0) {
      return null;
    }

    return children[nChildren - 1];
  }

  public final boolean hasChildNodes() {
    return (nChildren != 0);
  }

  // Overriden by DocumentImpl
  public Node insertBefore(Node newChild, Node refChild) {
    if (newChild == null) {
      throw new IllegalArgumentException("DOM: A null child cannot be inserted.");
    }

    if (newChild == refChild) {
      throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Cannot insert a child before itself.");
    }

    if (newChild.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
      DocumentFragment fragment = (DocumentFragment) newChild;
      NodeList list = fragment.getChildNodes();
      int nList = list.getLength();
      Node[] temp = new Node[nList];

      for (int i = 0; i < nList; i++) {
        temp[i] = list.item(i);
      } 

      for (int i = 0; i < nList; i++) {
        insertBefore(temp[i], refChild);
      } 

      return newChild;
    }

    if (newChild.getParentNode() != null) {
      newChild.getParentNode().removeChild(newChild);
    }

    checkChild(newChild);

    if (refChild == null) {
      appendChild(newChild);
      return newChild;
    }

    Document d1 = newChild.getOwnerDocument();
    Document d2 = refChild.getOwnerDocument();
    Document d3 = this.getOwnerDocument();

    /*
     if ((d1 != null) && (d2 != null) && (d1 != d2)) {
     throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
     "Cannot insert the newChild ("
     + nameOfNodeWithArticle(newChild)
     + ") before the refChild ("
     + nameOfNodeWithArticle(refChild)
     + "), their owner documents are incompatible.");
     }
     */
    if ((d3 != null) && (d1 != null) && (d1 != d2)) {
      throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "Cannot insert the newChild (" + nameOfNodeWithArticle(newChild) + ") to this node, the owner documents are incompatible.");
    }

    ensureChildren();

    if (newChild.getParentNode() != null) {
      newChild.getParentNode().removeChild(newChild);
    }

    for (int i = 0; i < nChildren; i++) {
      if (children[i] == refChild) {
        System.arraycopy(children, i, children, i + 1, nChildren - i);
        children[i] = newChild;
        Base newChildBase = (Base) newChild;
        if (i > 0) {
          ((Base) children[i - 1]).setNextSibling(newChild);
          newChildBase.setPreviousSibling(children[i - 1]);
        } else {
          ((Base) newChild).setPreviousSibling(null);
        }
        ((Base) children[i + 1]).setPreviousSibling(newChild);
        newChildBase.setNextSibling(children[i + 1]);
        
        newChildBase.setParent(this);
        nChildren++;
        return newChild;
      }
    } 

    throw new DOMException(DOMException.NOT_FOUND_ERR, "The refChild (" + nameOfNodeWithArticle(refChild) + ") is not a child of this node.");
  }

  public final Node removeChild(Node oldChild) {
    if (oldChild == null) {
      throw new IllegalArgumentException("DOM: Cannot remove a null child.");
    }

    for (int i = 0; i < nChildren; i++) {
      if (children[i] == oldChild) {
        System.arraycopy(children, i + 1, children, i, nChildren - i - 1);
        children[nChildren - 1] = null;        
        if (i > 0) {
          if (children[i - 1] != null) {
            ((Base) children[i - 1]).setNextSibling(children[i]);
          }
          if (children[i] != null) {
            ((Base) children[i]).setPreviousSibling(children[i - 1]);
          }
        } else {
          if (children[i] != null) {
            ((Base) children[i]).setPreviousSibling(null);
          }
        }
        if (i < nChildren - 2) {
          ((Base) children[i + 1]).setPreviousSibling(children[i]);
          if (children[i] != null) {
            ((Base) children[i]).setNextSibling(children[i + 1]);
          }
        } else if (children[i] != null) {
          if (children[i] != null) {
            ((Base) children[i]).setNextSibling(null);
          }
        }
        Base oldChildBase = ((Base) oldChild); 
        oldChildBase.setParent(null);
        oldChildBase.setPreviousSibling(null);
        oldChildBase.setNextSibling(null);
        nChildren--;
        return oldChild;
      }
    } 

    throw new DOMException(DOMException.NOT_FOUND_ERR, "The (" + nameOfNode(oldChild) + ") node you try to remove is not a child of this node.");
  }

  public final void removeChildren(Node firstChild, int length) {
    if (firstChild == null) {
      throw new IllegalArgumentException("DOM: Cannot remove a null child.");
    }

    boolean found = false;
    for (int i = 0; i < nChildren; i++) {
      if (children[i] == firstChild) {
        found = true;
        System.arraycopy(children, i + length, children, i, nChildren - i - length);
        for (int j = nChildren - length; j < nChildren; j++) {
          Base oldChildBase = ((Base) children[j]); 
//          oldChildBase.setParent(null);
//          oldChildBase.setPreviousSibling(null);
//          oldChildBase.setNextSibling(null);
          children[j] = null;
        }
        if (i > 0) {
          if (children[i - 1] != null) {
            ((Base) children[i - 1]).setNextSibling(children[i]);
          }
          if (children[i] != null) {
            ((Base) children[i]).setPreviousSibling(children[i - 1]);
          }
        } else {
          if (children[i] != null) {
            ((Base) children[i]).setPreviousSibling(null);
          }
        }
        if (i < nChildren - 1 - length) {
          if (children[i + 1] != null) {
            ((Base) children[i + 1]).setPreviousSibling(children[i]);
          }
          if (children[i] != null) {
            ((Base) children[i]).setNextSibling(children[i + 1]);
          }
        } else {
          if (children[i] != null) {
            ((Base) children[i]).setNextSibling(null);
          }
        }
//        ((Base) oldChild).setParent(null);
        nChildren -= length;
//        return oldChild;
      }
    } 
    if (!found) {
      throw new DOMException(DOMException.NOT_FOUND_ERR, "The (" + nameOfNode(firstChild) + ") node you try to remove is not a child of this node.");
    }
    
  }
  
  public final Node replaceChild(Node newChild, Node oldChild) {
    if (newChild == null) {
      throw new IllegalArgumentException("DOM: Cannot replace with a null node.");
    }

    if (oldChild == null) {
      throw new IllegalArgumentException("DOM: Cannot replace a null node.");
    }

    if (newChild.getParentNode() != null) {
      newChild.getParentNode().removeChild(newChild);
    }

    checkChild(newChild);
    Document d1 = newChild.getOwnerDocument();
    Document d2 = oldChild.getOwnerDocument();
    Document d3 = this.getOwnerDocument();

    if ((d1 != null) && (d2 != null) && (d1 != d2)) {
      throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "Cannot replace the oldChild (" + nameOfNodeWithArticle(oldChild) + ") with the newChild (" + nameOfNodeWithArticle(newChild) + "), their owner documents are incompatible.");
    }

    if ((d3 != null) && (d1 != null) && (d1 != d2)) {
      throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "Cannot replace with the newChild (" + nameOfNodeWithArticle(newChild) + ") in this node, the owner documents are incompatible.");
    }

    for (int i = 0; i < nChildren; i++) {
      if (children[i] == oldChild) {
        Base oldChildBase = ((Base) oldChild);
        Base newChildBase = ((Base) newChild); 
        oldChildBase.setParent(null);
        newChildBase.setParent(this);
        Base prev = (Base) oldChildBase.getPreviousSibling();
        Base next = (Base) oldChildBase.getNextSibling();
        if (prev != null) {
          prev.setNextSibling(newChildBase);
        }
        newChildBase.setPreviousSibling(prev);
        if (next != null) {
          next.setPreviousSibling(newChildBase);
        }
        newChildBase.setNextSibling(next);
        oldChildBase.setPreviousSibling(null);
        oldChildBase.setNextSibling(null);
        children[i] = newChild;
        return oldChild;
      }
    } 

    throw new DOMException(DOMException.NOT_FOUND_ERR, "The oldChild (" + nameOfNodeWithArticle(oldChild) + ") is not a child of this node.");
  }

//  protected final Node getChildAfter(Node node) {
//    if (children == null) {
//      return null;
//    }
//
//    for (int i = 0; i < nChildren - 1; i++) {
//      if (children[i] == node) {
//        return children[i + 1];
//      }
//    } 
//
//    return null;
//  }
//
//  protected final Node getChildBefore(Node node) {
//    if (children == null) {
//      return null;
//    }
//
//    for (int i = 1; i < nChildren; i++) {
//      if (children[i] == node) {
//        return children[i - 1];
//      }
//    } 
//
//    return null;
//  }

  // From interface NodeList - iterator for the children
  public final int getLength() {
    return nChildren;
  }

  public final Node item(int index) {
    if ((index < 0) || (index >= nChildren)) {
      return null;
    }

    return children[index];
  }

  protected final void transferData(NodeImpl source, NodeImpl destination, boolean deep) {
    destination.nChildren = source.nChildren;
    destination.children = new Node[source.nChildren + 1]; // + 1 to avoid a 0-sized array

    if (deep) {
      for (int i = 0; i < source.nChildren; i++) {
        destination.children[i] = source.children[i].cloneNode(true);
        if (i > 0) {
          ((Base) destination.children[i - 1]).setNextSibling(destination.children[i]);
          ((Base) destination.children[i]).setPreviousSibling(destination.children[i - 1]);
        } else {
          ((Base) destination.children[i]).setPreviousSibling(null);
        }
        ((Base) destination.children[i]).setParent(destination);
      } 
    } else {
      //System.arraycopy(source.children, 0, destination.children, 0, source.nChildren);
      destination.nChildren = 0;
    }
  }

  public final NodeList getElementsByTagName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("DOM: null is not a valid name.");
    }

    return (new NodeListImpl()).init(this, name).update();
  }

  public final NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
    /*
     if (namespaceURI == null) {
     throw new IllegalArgumentException("DOM: null is not a valid namespace uri.");
     }
     */
    if (localName == null) {
      throw new IllegalArgumentException("DOM: null is not a valid local name.");
    }

    return (new NodeListImpl()).init(this, namespaceURI, localName).update();
  }

  protected final void update(NodeListImpl list, String name) {
    if (name.equals("*")) {
      recAll(list, this);
    } else {
      rec(list, name, this);
    }
  }

  protected final void update(NodeListImpl list, String namespaceURI, String localName) {
    if (namespaceURI != null && namespaceURI.equals("*") && localName.equals("*")) {
      recAll(list, this);
    } else if (namespaceURI != null && namespaceURI.equals("*")) {
      recAllL(list, localName, this);
    } else if (localName.equals("*")) {
      recAllU(list, namespaceURI, this);
    } else {
      rec(list, namespaceURI, localName, this);
    }
  }

  private void rec(NodeListImpl list, String name, Node node) {
    NodeList c = node.getChildNodes();

    for (int i = 0; i < c.getLength(); i++) {
      Node x = c.item(i);

      if ((x.getNodeType() == ELEMENT_NODE) && name.equals(x.getNodeName())) {
        list.add(x);
      }

      rec(list, name, x);
    } 
  }

  private void recAll(NodeListImpl list, Node node) {
    NodeList c = node.getChildNodes();

    for (int i = 0; i < c.getLength(); i++) {
      Node x = c.item(i);

      if (x.getNodeType() == ELEMENT_NODE) {
        list.add(x);
      }

      recAll(list, x);
    } 
  }

  private void rec(NodeListImpl list, String uri, String local, Node node) {
    NodeList c = node.getChildNodes();

    for (int i = 0; i < c.getLength(); i++) {
      Node x = c.item(i);

      if ((x.getNodeType() == ELEMENT_NODE) && local.equals(x.getLocalName()) && areNamespaceURIEqual(uri, x.getNamespaceURI())) {
        list.add(x);
      }

      rec(list, uri, local, x);
    } 
  }

  private void recAllU(NodeListImpl list, String uri, Node node) {
    NodeList c = node.getChildNodes();

    for (int i = 0; i < c.getLength(); i++) {
      Node x = c.item(i);

      if ((x.getNodeType() == ELEMENT_NODE) && areNamespaceURIEqual(uri, x.getNamespaceURI())) {
        list.add(x);
      }

      recAllU(list, uri, x);
    } 
  }

  private void recAllL(NodeListImpl list, String local, Node node) {
    NodeList c = node.getChildNodes();

    for (int i = 0; i < c.getLength(); i++) {
      Node x = c.item(i);

      if ((x.getNodeType() == ELEMENT_NODE) && local.equals(x.getLocalName())) {
        list.add(x);
      }

      recAllL(list, local, x);
    } 
  }

  // Overridden by AttrImpl
  protected boolean checkChildNodeType(Node node) {
    /*
     int t = node.getNodeType();
     return  (t == ELEMENT_NODE) ||
     (t == PROCESSING_INSTRUCTION_NODE) ||
     (t == TEXT_NODE) ||
     (t == COMMENT_NODE) ||
     (t == ENTITY_REFERENCE_NODE) ||
     (t == DOCUMENT_FRAGMENT_NODE) ||
     (t == CDATA_SECTION_NODE);
     */
    switch (node.getNodeType()) {
      case ELEMENT_NODE:
      case PROCESSING_INSTRUCTION_NODE:
      case TEXT_NODE:
      case COMMENT_NODE:
      case ENTITY_REFERENCE_NODE:
      case DOCUMENT_FRAGMENT_NODE:
      case BinaryTextImpl.TYPE:
      case CDATA_SECTION_NODE: {
        return true;
      }
      default: {
        return false;
      }
    }
  }

  private void checkChild(Node node) {
    short t = node.getNodeType();

    if (!checkChildNodeType(node)) {
      throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "A node of type " + nameOfNode(node) + " cannot be used for this operation (append, insert or replace).");
    }

    Node x = node;
    int counter = 0;

    while (x != null) {
      if (x == this) {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Attempt to append, insert, or replace a node which is an " + "ancestor of this node.");
      }

      counter++;
      x = x.getParentNode();
    }

    x = this;

    while (x != null) {
      if (x == node) {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Attempt to append, insert, or replace a node which is an " + "descendant of this node.");
      }

      x = x.getParentNode();
    }
  }

  public void normalize() {
    if (getNodeType() != ELEMENT_NODE && getNodeType() != DOCUMENT_NODE) {
      return;
    }
    NodeList nd = getChildNodes();
    CharArray buf = null;
    if (this instanceof DocumentImpl){
      buf = ((DocumentImpl)this).normBuffer;
    } else {
      buf = ((DocumentImpl)getOwnerDocument()).normBuffer;
    }
    for (int i = 0; i < nd.getLength();) {
      if (i < nd.getLength() - 1) {
        if (nd.item(i).getNodeType() == TEXT_NODE) {
          int j;
          for (j = i; (j < nd.getLength() - 1) && nd.item(j + 1).getNodeType() == TEXT_NODE; j++) {
            buf.append(((Text) nd.item(j + 1)).getData());
          }
          ((Text) nd.item(i)).appendData(buf.toString());
          buf.clear();
          if (j > i) {
            removeChildren(nd.item(i + 1), j - i);
          } else {
            ((Base) nd.item(i)).normalize();
            i++;
          }
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
  
  public abstract String getNodeName();

  /* {
   throw new RuntimeException("DOM: There is no nodeName specified for this node. Please report a bug.");
   }
   */
  public abstract short getNodeType();

  /* {
   throw new RuntimeException("DOM: There is no nodeType specified for this node. Please report a bug.");
   }*/
  public Node cloneNode(boolean a) {
    throw new RuntimeException("DOM: This node cannot be cloned.");
  }

  protected void forcedAppendChild(Node child) {
    ensureChildren();
    children[nChildren] = child;
    if (nChildren > 0) {
      ((Base) children[nChildren - 1]).setNextSibling(child);
      ((Base) child).setPreviousSibling(children[nChildren - 1]);
    } else {
      ((Base) child).setPreviousSibling(null);
    }
    ((Base) child).setNextSibling(null);
    nChildren++;
  }

}

/*
 static short ATTRIBUTE_NODE
 The node is an Attr.
 static short CDATA_SECTION_NODE
 The node is a CDATASection.
 static short COMMENT_NODE
 The node is a Comment.
 static short DOCUMENT_FRAGMENT_NODE
 The node is a DocumentFragment.
 static short DOCUMENT_NODE
 The node is a Document.
 static short DOCUMENT_TYPE_NODE
 The node is a DocumentType.
 static short ELEMENT_NODE
 The node is an Element.
 static short ENTITY_NODE
 The node is an Entity.
 static short ENTITY_REFERENCE_NODE
 The node is an EntityReference.
 static short NOTATION_NODE
 The node is a Notation.
 static short PROCESSING_INSTRUCTION_NODE
 The node is a ProcessingInstruction.
 static short TEXT_NODE
 The node is a Text node.
 */

