package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

/**
 * Wraps another NodeList and gives access only to its Element members;
 * immutable.
 *
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version Feb 27, 2002, 2:23:04 PM
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public final class ElementOnlyNodeListImpl implements NodeList, java.io.Serializable {

  private NodeList list; //$JL-SER$
  private int[] a;
  private int n;

  public ElementOnlyNodeListImpl(NodeList list) {
    if (list == null) {
      n = 0;
      a = new int[0];
      return;
    }

    this.list = list;
    n = 0;
    int nList = list.getLength();
    a = new int[nList];

    for (int i = 0; i < nList; i++) {
      Node node = list.item(i);

      if (node.getNodeType() == Node.ELEMENT_NODE) {
        a[n] = i;
        n++;
      }
    } 
  }

  public int getLength() {
    return n;
  }

  public Node item(int index) {
    if ((index < 0) || (index >= n)) {
      throw new IllegalArgumentException();
    }

    return list.item(a[index]);
  }

}

