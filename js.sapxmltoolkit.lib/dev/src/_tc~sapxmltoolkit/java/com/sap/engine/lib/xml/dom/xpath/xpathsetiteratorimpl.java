package com.sap.engine.lib.xml.dom.xpath;

//import org.w3c.dom.xpath.*;
import org.w3c.dom.*;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.DTM;

public class XPathSetIteratorImpl { // implements XPathSetIterator {

  private DTM dtm = null;
  private int count = 0;
  private int size = 0;
  private XNodeSet xo = null;
  private Document owner = null;
  
  public void setOwnerDocument(Document owner) {
    this.owner = owner;
  }

  public Node getOwnerDocument() {
    return owner;
  }

  XPathSetIteratorImpl(XNodeSet xo) {
    this.xo = xo;
    dtm = xo.dtm;
    dtm.initializeDOM();
    size = xo.size();
    count = 1;
  }

  public Node nextNode() throws DOMException {
    if (size - count >= 0) {
      DTM dtm = ((XNodeSet) xo).dtm;
      int[] ancestry = XPathExpressionImpl.getAncestry(dtm, ((XNodeSet) xo).getKth(count++));
      Node n = XPathExpressionImpl.getNodeFromAncestry(owner, ancestry);
      return n;
      //return dtm.domtree[((XNodeSet) xo).getKth(count++)];
    } else {
      throw new DOMException(DOMException.INVALID_STATE_ERR, "INVALID_STATE_ERR: No more elements.");
    }
  }

  public boolean hasNext() {
    if (size - count < 0) {
      return false;
    } else {
      return true;
    }
  }

}

