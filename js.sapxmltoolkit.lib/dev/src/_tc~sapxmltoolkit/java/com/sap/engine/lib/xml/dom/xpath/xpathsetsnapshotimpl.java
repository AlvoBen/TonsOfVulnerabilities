package com.sap.engine.lib.xml.dom.xpath;

//import org.w3c.dom.xpath.*;
import org.w3c.dom.*;

/**
 *  Represents a snapshot of the set. Future modifications on
 *  the set shall not affect the snapshot.
 */
public class XPathSetSnapshotImpl { //implements XPathSetSnapshot {

  private NodeList set = null;

  XPathSetSnapshotImpl(NodeList set) {
    this.set = set;
  }

  public int getLength() {
    return set.getLength();
  }

  public Node item(int index) {
    try {
      return set.item(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      //$JL-EXC$

      return null;
    }
  }

  public NodeList getNodeList() {
    return set;
  }

}

