/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xsl.xslt;

/**
 *
 <!ELEMENT xsl:copy-of EMPTY>
 <!ATTLIST xsl:copy-of select %expr; #REQUIRED>
 *
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 0.0.1
 *
 *
 * First Edition: 17.01.2001
 *
 */
import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public final class XSLForEach extends XSLContentNode {

  private ETObject select = null;
  private Vector sortNodes = null;
  private Sorter sorter = new Sorter();

  //protected int[] nodes = null;
  //
  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLForEach n = new XSLForEach(s, p);
  //    n.select = select.cloneIt();
  //    copyXSLNodeVector(n.sortNodes, sortNodes, s, p);
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public XSLForEach(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLForEach(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    select = owner.etBuilder.process(el.getAttribute("select"));
    sortNodes = new Vector();
    XSLNode node = getFirst(), ntmp = null;
    ;
    node = node.getNext();

    while (node != null) {
      if (node instanceof XSLSort) {
        ntmp = node.getNext();
        node.remove();
        node.setNext(null);
        node.setPrev(null);
        sortNodes.add(node);
        node = ntmp;
      } else {
        node = node.getNext();
      }
    }
  }

  //int nodes[] = new int[1000];
  //XPathContext context = new XPathContext();
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    xcont.currentNode = this;
    XObject xo = owner.getXPathProcessor().process(select, xcont, varContext);

    if (xo.getType() == XJavaObject.TYPE) {
      ((XJavaObject) xo).setContext(xcont);
      xo = xo.toXNodeSet();
    }
    
    if (xo.getType() != XNodeSet.TYPE) {
      throw new XSLException("Select Query does not evaluate to a NodeSet");
    }

    XNodeSet xns = (XNodeSet) xo;

    if (xns.size() > 0) {
      owner.stack = xns.getNodes(owner.stack, owner.pStack);
      int count = xns.size();

      if (sortNodes.size() > 0) {
        if (sorter == null) {
          sorter = new Sorter();
        }

        sorter.sort(xcont, owner.stack, owner.pStack, count, sortNodes, owner.getXPathProcessor());
      }

      int pStack0 = owner.pStack;
      owner.pStack += count + 1;
      int pEnd = pStack0 + count;

      for (int i = pStack0; i < pEnd; i++) {
        //getFirst().process(new XPathContext(xcont, owner.stack[i], i - pStack0 + 1, count), owner.stack[i]);
        XPathContext xpctx = new XPathContext(xcont, owner.stack[i], i - pStack0 + 1, count);
        xpctx.dtm = xns.dtm;
        processFromFirst(xpctx, owner.stack[i]);
        //getFirst().process(xpctx, owner.stack[i]);
      } 

      owner.pStack -= count + 1;
    }

    xo.close();
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLForEach: select = " + select); //$JL-SYS_OUT_ERR$
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"select"};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}


