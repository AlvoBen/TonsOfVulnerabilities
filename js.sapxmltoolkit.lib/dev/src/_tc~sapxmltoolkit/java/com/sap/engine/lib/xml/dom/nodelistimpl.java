package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

// This class must have the possibility to be used stand-alone - in order to manage Extensions to the xslt engine
@Deprecated
public final class NodeListImpl implements NodeList, java.io.Serializable {

  private static final int BY_NAME_ONLY = 1;
  private static final int BY_URI_AND_LOCALNAME = 2;
  private int searchType;
  private String soughtName;
  private String soughtNamespaceURI;
  private String soughtLocalName;
  private DocumentImpl document;
  private NodeImpl node;
  private int modificationCounter;
  private Node[] a = new Node[16];
  private int na;

  public NodeListImpl() {

  }

  protected NodeListImpl init(NodeImpl node, String name) {
    this.node = node;
    document = (DocumentImpl) ((Base) node).getOwnerDocument_internal();
    searchType = BY_NAME_ONLY;
    soughtName = name;
    soughtNamespaceURI = null;
    soughtLocalName = null;
    return this;
  }

  protected NodeListImpl init(NodeImpl node, String namespaceURI, String localName) {
    this.node = node;
    document = (DocumentImpl) ((Base) node).getOwnerDocument_internal();
    searchType = BY_URI_AND_LOCALNAME;
    soughtName = null;
    soughtNamespaceURI = namespaceURI;
    soughtLocalName = localName;
    return this;
  }

  public void add(Node x) {
    if (na == a.length) {
      Node[] old = a;
      a = new Node[2 * na];
      System.arraycopy(old, 0, a, 0, na);
    }

    a[na] = x;
    na++;
  }

  public int getLength() {
    if ((document != null) && (modificationCounter != document.getModificationCounter())) {
      update();
    }

    return na;
  }

  public Node item(int index) {
    if ((document != null) && (modificationCounter != document.getModificationCounter())) {
      update();
    }

    return a[index];
  }

  protected NodeListImpl update() {
    na = 0;

    if (searchType == BY_NAME_ONLY) {
      node.update(this, soughtName);
    } else if (searchType == BY_URI_AND_LOCALNAME) {
      node.update(this, soughtNamespaceURI, soughtLocalName);
    } else {
      //xxx
    }

    return this;
  }

}

