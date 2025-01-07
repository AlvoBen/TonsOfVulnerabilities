package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public final class NamedNodeMapImpl implements NamedNodeMap, java.io.Serializable {

  private Node[] a = new Node[16];
  private int na;

  protected NamedNodeMapImpl() {

  }

  protected NamedNodeMapImpl init() {
    na = 0;
    return this;
  }

  public int getLength() {
    return na;
  }

  public Node getNamedItem(String name) {
    int p = find(name);
    return (p == -1) ? null : a[p];
  }

  public Node getNamedItemNS(String namespaceURI, String localName) {
    int p = findNS(namespaceURI, localName);
    return (p == -1) ? null : a[p];
  }

  public Node item(int index) {
    if ((index < 0) || (index >= na)) {
      return null;
    }

    return a[index];
  }

  public Node removeNamedItem(String name) {
    int p = find(name);

    if (p == -1) {
      throw new DOMException(DOMException.NOT_FOUND_ERR, "There is no named item '" + name + "' in this NamedNodeMap.");
    }

    Node r = a[p];
    na--;
    a[p] = a[na];
    return r;
  }

  public Node removeNamedItemNS(String namespaceURI, String localName) {
    int p = findNS(namespaceURI, localName);

    if (p == -1) {
      throw new DOMException(DOMException.NOT_FOUND_ERR, "There is no named item with local name '" + localName + "' in this NamedNodeMap.");
    }

    Node r = a[p];
    na--;
    a[p] = a[na];
    return r;
  }

  public Node setNamedItem(Node arg) {
    if ((arg.getNodeType() == Node.ATTRIBUTE_NODE) && (((AttrImpl) arg).getOwnerElement() != null)) {
      // Can this ever be reached?
      throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR, "The attribute already belongs to another element.");
    }

    int p = find(arg.getNodeName());

    if (p == -1) {
      ensure();
      a[na] = arg;
      na++;
      return null;
    } else {
      Node r = a[p];
      a[p] = arg;
      return r;
    }
  }

  public Node setNamedItemNS(Node arg) {
    if ((arg.getNodeType() == Node.ATTRIBUTE_NODE) && (((AttrImpl) arg).getOwnerElement() != null)) {
      // Can this ever be reached?
      throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR, "The attribute already belongs to another element.");
    }

    int p = findNS(arg.getNamespaceURI(), arg.getLocalName());

    if (p == -1) {
      ensure();
      a[na] = arg;
      na++;
      return null;
    } else {
      Node r = a[p];
      a[p] = arg;
      return r;
    }
  }

  private int find(String name) {
    for (int i = 0; i < na; i++) {
      if (a[i].getNodeName().equals(name)) {
        return i;
      }
    } 

    return -1;
  }

  private int findNS(String uri, String local) {
    for (int i = 0; i < na; i++) {
      if (Base.areNamespaceURIEqual(a[i].getNamespaceURI(), uri) && 
      //a[i].getNamespaceURI().equals(uri) &&
      a[i].getLocalName().equals(local)) {
        return i;
      }
    } 

    return -1;
  }

  private void ensure() {
    if (na == a.length) {
      Node[] old = a;
      a = new Node[na * 2];
      System.arraycopy(old, 0, a, 0, na);
    }
  }

  protected NamedNodeMapImpl cloneDeep() {
    NamedNodeMapImpl r = new NamedNodeMapImpl();
    r.na = na;
    r.a = new Node[na];

    for (int i = 0; i < na; i++) {
      r.a[i] = a[i].cloneNode(true);
    } 

    return r;
  }

}

