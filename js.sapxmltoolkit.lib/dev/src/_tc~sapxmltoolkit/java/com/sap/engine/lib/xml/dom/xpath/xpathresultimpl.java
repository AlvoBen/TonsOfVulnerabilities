package com.sap.engine.lib.xml.dom.xpath;

//import org.w3c.dom.xpath.*;
import org.w3c.dom.*;
import com.sap.engine.lib.xml.dom.NodeListImpl;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XBoolean;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;
import com.sap.engine.lib.xsl.xpath.xobjects.XNumber;
import com.sap.engine.lib.xsl.xpath.functions.XFDump;

/**
 *  An object of this class wraps up a result of an XPath query.
 */
public class XPathResultImpl { //implements XPathResult {

  /**
   * This code does not represent a specific type. An evaluation of an XPath
   * expression will never produce this type. If this type is requested,
   * then the evaluation must return whatever type naturally results from
   * evaluation of the expression.
   */
  public static final short ANY_TYPE = 0;
  /**
   * The result is a number as defined by XPath 1.0.
   */
  public static final short NUMBER_TYPE = 1;
  /**
   * The result is a string as defined by XPath 1.0.
   */
  public static final short STRING_TYPE = 2;
  /**
   * The result is a boolean as defined by XPath 1.0.
   */
  public static final short BOOLEAN_TYPE = 3;
  /**
   * The result is a node set as defined by XPath 1.0.
   */
  public static final short NODE_SET_TYPE = 4;
  /**
   * The result is a single node, which may be any node of the node set
   * defined by XPath 1.0, or null if the node set is empty. This is a
   * convenience that permits optimization where the caller knows that no
   * more than one such node exists because evaluation can stop after
   * finding the one node of an expression that would otherwise return a
   * node set (of type <code>NODE_SET_TYPE</code>).
   * <br>Where it is possible that multiple nodes may exist and the first
   * node in document order is required, a <code>NODE_SET_TYPE</code>
   * should be processed using an ordered iterator, because there is no
   * order guarantee for a single node.
   */
  public static final short SINGLE_NODE_TYPE = 5;
  private int type = -1;
  private XObject xo = null;
  private XNodeSet set = null;
  private Throwable anXPathException = null;
  private Document owner = null;

  XPathResultImpl(XObject xo) {
    this.xo = xo;
  }

  public XPathResultImpl() {

  }

  void reuse(XObject xo) {
    this.type = -1;
    this.xo = null;
    this.set = null;
    this.anXPathException = null;
    this.xo = xo;
  }
  
  public void setOwnerDocument(Document owner) {
    //LogWriter.getSystemLogWriter().println("XPathResultImpl: changing owner !!!!!!!!!!!!!!");
    this.owner = owner;
  }
  
  public Node getOwnerDocument() {
    return owner;
  }
  
  
  public short getResultType() {
    int t = xo.getType();

    switch (t) {
      case XNumber.TYPE: {
        return NUMBER_TYPE;
      }
      case XString.TYPE: {
        return STRING_TYPE;
      }
      case XBoolean.TYPE: {
        return BOOLEAN_TYPE;
      }
      case XNodeSet.TYPE: {
        return (((XNodeSet) xo).size() == 1) ? SINGLE_NODE_TYPE : NODE_SET_TYPE;
      }
      default: {
        throw new RuntimeException("Bad type of an XObject.");
      }
    }
  }

  public double getNumberValue() throws XPathException {
    if (xo instanceof XNumber) {
      return ((XNumber) xo).getValue();
    }

    throw new XPathException((short) 2, "TYPE_ERR: not a number");
  }

  public String getStringValue() throws XPathException {
    if (xo instanceof XString) {
      return (((XString) xo).getValue()).toString();
    }

    throw new XPathException((short) 2, "TYPE_ERR: not a string");
  }

  public boolean getBooleanValue() throws XPathException {
    if (xo instanceof XBoolean) {
      return ((XBoolean) xo).getValue();
    }

    throw new XPathException((short) 2, "TYPE_ERR: not a boolean");
  }

  public Node getSingleNodeValue() throws XPathException {
    if (xo instanceof XNodeSet) {
      if (((XNodeSet) xo).size() < 1) {
        return null;
      }

      DTM dtm = ((XNodeSet) xo).dtm;
      dtm.initializeDOM();
      return dtm.domtree[((XNodeSet) xo).getKth(1)];
    }

    throw new XPathException((short) 2, "TYPE_ERR: not a nodeset");
  }

  public XPathSetIteratorImpl getSetIterator(boolean ordered) throws XPathException, DOMException {
    //    if (xo instanceof XNodeSet) {
    //      DTM dtm = ((XNodeSet)xo).dtm;
    //      dtm.initializeDOM();
    //      NodeListImpl nli = new NodeListImpl();
    //      int size = ((XNodeSet)xo).size();
    //      for(int i = 1; i < size + 1; i++) {
    //        nli.add(dtm.domtree[((XNodeSet)xo).getKth(i)]);
    //      }
    //return new XPathSetIteratorImpl((NodeImpl)dtm.domtree[0], nli);
    //      return new XPathSetIteratorImpl(owner, nli);
    //    }  
    //      
    //    throw new XPathException((short)2, "TYPE_ERR: not a nodeset");
    if (xo instanceof XNodeSet) {
      XPathSetIteratorImpl xpsi = new XPathSetIteratorImpl((XNodeSet) xo);
      xpsi.setOwnerDocument(owner);
      return xpsi;
    } else {
      throw new XPathException((short) 2, "TYPE_ERR: not a nodeset");
    }
  }

  public XPathSetSnapshotImpl getSetSnapshot(boolean ordered) throws XPathException, DOMException {
//    if (xo instanceof XNodeSet) {
//      DTM dtm = ((XNodeSet) xo).dtm;
//      dtm.initializeDOM();
//      NodeListImpl nli = new NodeListImpl();
//      int size = ((XNodeSet) xo).size();
//
//      for (int i = 1; i < size + 1; i++) {
//        //        LogWriter.getSystemLogWriter().println("<XPathResultImpl A>: " + dtm.domtree[((XNodeSet)xo).getKth(i)]);
//        nli.add(dtm.domtree[((XNodeSet) xo).getKth(i)]);
//      } 
//
//      return new XPathSetSnapshotImpl(nli);
      
      if (xo instanceof XNodeSet) {
        DTM dtm = ((XNodeSet) xo).dtm;
        dtm.initializeDOM();
        NodeListImpl nli = new NodeListImpl();
        int size = ((XNodeSet) xo).size();
        for (int i = 1; i < size + 1; i++) {
        //        LogWriter.getSystemLogWriter().println("<XPathResultImpl A>: " + dtm.domtree[((XNodeSet)xo).getKth(i)]);
                nli.add(dtm.domtree[((XNodeSet) xo).getKth(i)]);
//          int[] ancestry = XPathExpressionImpl.getAncestry(dtm, ((XNodeSet) xo).getKth(i));
//          Node n = XPathExpressionImpl.getNodeFromAncestry(owner, ancestry);
//          nli.add(n);
        } 
        return new XPathSetSnapshotImpl(nli);
        
      }
  

    throw new XPathException((short) 2, "TYPE_ERR: not a nodeset");
  }

  public String toString() {
    try {
      switch (xo.getType()) {
        case XNumber.TYPE: {
          return xo.toXString().toString();
        }
        case XString.TYPE: {
          return xo.toString();
        }
        case XBoolean.TYPE: {
          return xo.toXString().toString();
        }
        case XNodeSet.TYPE: {
          return XFDump.toString(((XNodeSet) xo).dtm, xo);
        }
        default: {
          return "???";
        }
      }
    } catch (com.sap.engine.lib.xsl.xpath.XPathException e) {
      //$JL-EXC$

      e.printStackTrace();
      return e.toString();
    }
  }

}

