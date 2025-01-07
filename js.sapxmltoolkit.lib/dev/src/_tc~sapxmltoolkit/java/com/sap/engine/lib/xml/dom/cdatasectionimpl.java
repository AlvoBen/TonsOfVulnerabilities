package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
@Deprecated
public class CDATASectionImpl extends TextImpl implements CDATASection {

  protected CDATASectionImpl(Document owner) {
    setOwnerDocument(owner);
  }

  protected CDATASectionImpl() {

  }

  public String getNodeName() {
    return "#cdata-section";
  }

  public short getNodeType() {
    return Node.CDATA_SECTION_NODE;
  }

  public Node cloneNode(boolean deep) {
    CDATASectionImpl result = new CDATASectionImpl(getOwnerDocument());
    result.init(data, null);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }

  public String toString() {
    return "#cdata: " + data;
  }

}

