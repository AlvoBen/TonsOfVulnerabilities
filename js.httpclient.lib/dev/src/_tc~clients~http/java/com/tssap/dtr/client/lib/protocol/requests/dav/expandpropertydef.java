package com.tssap.dtr.client.lib.protocol.requests.dav;

/** Helper class to build a tree of property tags*/
public class ExpandPropertyDef {

  /** The name attribute of the property */
  public String name;
  /** The namespace attribute of the property */
  public String namespaceURI;
  /** The list of children of this tag */
  public ExpandPropertyDef firstChild;
  /** The next neighbor of this tag */
  public ExpandPropertyDef next;
  /** Creates a property definition with given name*/
  public ExpandPropertyDef(String name) {
    this(name, null);
  }

  /** Creates a property definition with given name and namespace URI */
  public ExpandPropertyDef(String name, String namespaceURI) {
      int n = name.indexOf(':');
      this.name = (n>0)? name.substring(n+1) : name;
      this.namespaceURI = namespaceURI;
  }

  /** Appends a child to the list of children*/
  public void addChild(ExpandPropertyDef property) {
    ExpandPropertyDef child = firstChild;
    if (child!=null) {
      while (child.next!=null) {
        child = child.next;
      }
      child.next = property;
    } else {
      firstChild = property;
    }
  }
}