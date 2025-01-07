package com.sap.engine.lib.xsl.xpath.xobjects;

import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.jaxp.DocHandlerResult;
import com.sap.engine.lib.jaxp.TransformerFactoryImpl;
import com.sap.engine.lib.jaxp.TransformerImpl;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;

/**
 *  Represents an External (Java) Object which is used in handling of external functions
 *
 */


public final class XJavaObject extends XObject implements ETItem {

  public static final int TYPE = 6;

  private XPathContext context = null;
  private Object object = null;
  private XNodeSet set = null;
  private DTM dtm = null;

  public XJavaObject() {
  }

  protected XJavaObject reuse(Object o) {
    this.object = o;
    this.dtm = null;
    return this;
  }

  public Object getObject() {
    return object;
  }

  public XObject evaluate(XPathContext x) throws XPathException {
    return this;
  }

  public boolean match(XPathContext x) throws XPathException {
    throw new XPathException("Objects of type XJavaObject cannot be matched agains a XPathContext");
  }


  public boolean isNodeOrNodeList() {
    return (object instanceof NodeList || object instanceof Node);
  }  


  //public XNodeSet toXNodeSet() throws XPathException {
  //     if (context == null) {
  //       throw new XPathException("Cannot get a nodeset from a java object without context.");
  //     }
  //     if (object instanceof Node) {
  //       return set.reuse(factory.getXNodeSet(context.dtm, getNode((Node) object)));
  //     } else if (object instanceof NodeList) {
  //       int length = ((NodeList) object).getLength();
  //       set.reuse(context.dtm);
  //       for (int i = 0; i < length; i ++ ) {
  //         set.add(getNode(((NodeList) object).item(i)));        
  //       }
  //       return set;
  //     } else {
  //       throw new XPathException("Cannot get a nodeset from java object that is neither a Node nor a NodeList.");
  //     }
  //   }


  public XNodeSet toXNodeSet() throws XPathException {
    if (context == null) {
      throw new XPathException("Cannot get a nodeset from a java object without context.");
    }

    //DTM dtm = context.dtm;

    if (object instanceof DocumentFragment) {



      //       dtm = getDTMFromFragment(((DocumentFragment) object));
      getDTM(((Node) object));
      //dtm = this.dtm;
      //       LogWriter.getSystemLogWriter().println("XJavaObject: owner is: " + ((Node) object).getOwnerDocument());
      //       //return set.reuse(factory.getXNodeSet(dtm, getNode((Node) object, dtm)));
      //       LogWriter.getSystemLogWriter().println("XJAvaObject.toXNodeSet: getNode is " + getNode((Node) object, dtm));
      int node = getNode((Node) object, dtm);
      set = factory.getXNodeSet(dtm, node);
      return set;

    } else if (object instanceof NodeList) {
      int length = ((NodeList) object).getLength();
      if (length > 0) {
        getDTM(((NodeList) object).item(0).getOwnerDocument());
        //dtm = this.dtm;
      } 
      set.reuse(dtm);
      for (int i = 0; i < length; i ++ ) {
        //LogWriter.getSystemLogWriter().println("XJavaObject next item is: #" + ((NodeList) object).item(i) + "#");
        set.add(getNode(((NodeList) object).item(i), dtm));        
      }
      return set;
    } else if (object instanceof Node) {
      getDTM(((Node) object).getOwnerDocument());
      //dtm = this.dtm;
      return set.reuse(factory.getXNodeSet(dtm, getNode((Node) object, dtm)));
    } else if (object == null) {
      return set.reuse(dtm);
    } else {
      throw new XPathException("Cannot get a nodeset from java object that is neither a Node nor a NodeList.");
    }

  }


//private DTM getDTMFromFragment(DocumentFragment df) throws XPathException {
//try {
//DTM dtm = new DTM();
//NamespaceManager nm = new NamespaceManager();
//DTMFactory fact = new DTMFactory();
//fact.initialize(dtm, nm);
//TransformerImpl tr = (TransformerImpl) new TransformerFactoryImpl().newTransformer();
//Document doc  = new DocumentImpl();
//doc.importNode(df, true);
//doc.appendChild(df);
//LogWriter.getSystemLogWriter().println("XJAvaObject doc is " + doc);
//DOMSource source = new DOMSource(doc);
//DocHandlerResult res = new DocHandlerResult(fact);
//tr.transform(source, res);
//return dtm;
//} catch (javax.xml.transform.TransformerConfigurationException e) {
//throw new XPathException(e.toString());
//} catch (javax.xml.transform.TransformerException e) {
//throw new XPathException(e.toString());
//}



//}

  private void getDTM(Node node) throws XPathException {
    if (this.dtm != null) {
      return;
    }
    try {
      DTM dtm = new DTM();
      NamespaceManager nm = new NamespaceManager();
      DTMFactory fact = new DTMFactory();
      fact.initialize(dtm, nm);
      TransformerImpl tr = (TransformerImpl) new TransformerFactoryImpl().newTransformer();
      DOMSource source = new DOMSource(node);
      DocHandlerResult res = new DocHandlerResult(fact);
      tr.transform(source, res);
      this.dtm = dtm;
    } catch (javax.xml.transform.TransformerConfigurationException e) {
      throw new XPathException(e.toString(), e);
    } catch (javax.xml.transform.TransformerException e) {
      throw new XPathException(e.toString(), e);
    }
  }


  private int getNode(Node node, DTM dtm) {
    int[] ancestry = XNodeSet.getAncestry((Node)object);
    //     LogWriter.getSystemLogWriter().println("XJavaObject: XnodeSet is");
    //     for (int i = 0; i < ancestry.length; i ++) {
    //       LogWriter.getSystemLogWriter().println(ancestry[i]);
    //     }
    int n = XNodeSet.getNodeFromAncestry(dtm, ancestry);
    //     LogWriter.getSystemLogWriter().println("XJavaObject node is: " + n);
    return n;
  }

  public void setContext(XPathContext context) {
    this.context = context;
    this.set = context.getXFactCurrent().getXNodeSet();
  }


  public int getType() {
    return TYPE;
  }

  public void print(int i) {
  }

  public XString toXString() {
    if (object == null) {
      return factory.getXString("");
    } else if (object.toString() == null) {
      return factory.getXString("");
    } else {
      return factory.getXString(object.toString());
    }
  }

  public XJavaObject toXJavaObject() throws XPathException {
    return this;
  }

  public XBoolean toXBoolean() throws XPathException {
    // return factory.getXBoolean(this);
    if ( object instanceof Boolean){
      Boolean bObject = (Boolean)object;
      return 	factory.getXBoolean(bObject.booleanValue());      
    }else{
      return super.toXBoolean();
    }
  }
}

