package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
@Deprecated
public final class ProcessingInstructionImpl extends Base implements ProcessingInstruction {

  private NodeImpl parent;
  private String target;
  private String data;

  protected ProcessingInstructionImpl() {

  }

  protected ProcessingInstructionImpl(Document owner) {
    setOwnerDocument(owner);
  }

  protected final ProcessingInstructionImpl init(String target, String data, NodeImpl parent) {
    this.target = target;
    this.data = data;
    this.parent = parent;
    return this;
  }

  public String getTarget() {
    return target;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public Node cloneNode(boolean deep) {
    return (new ProcessingInstructionImpl(getOwnerDocument())).init(target, data, 
    /*parent*/
    null);
  }

  public short getNodeType() {
    return PROCESSING_INSTRUCTION_NODE;
  }

  public String getNodeName() {
    return target;
  }

  public String getNodeValue() {
    return data;
  }

  //  protected Document getOwnerDocument_internal() {
  //    return parent.getOwnerDocument_internal();
  //  }
  //  
  //  public Document getOwnerDocument() {
  //    return parent.getOwnerDocument_internal();
  //  }
  public Node getParentNode() {
    return parent;
  }

  public void setNodeValue(String nodeValue) {
    data = nodeValue;
  }

//  public Node getNextSibling() {
//    return (parent == null) ? null : parent.getChildAfter(this);
//  }

//  public Node getPreviousSibling() {
//    return (parent == null) ? null : parent.getChildBefore(this);
//  }

  protected void setParent(NodeImpl parent) {
    this.parent = parent;
  }

  public String toString() {
    return "#pi:" + target + " = " + data;
  }

}

