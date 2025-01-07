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
import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.dom.DocumentImpl;
import com.sap.engine.lib.xml.dom.ElementImpl;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XBoolean;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public final class XSLWhen extends XSLContentNode {

  private ETObject test = null;

  public XSLWhen(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLWhen(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    try {
      test = owner.etBuilder.process(el.getAttribute("test"));
    } catch (XPathException e) {
      throw new XSLException("Could not load XSLWhen. Stylesheet: " + ((DocumentImpl)el.getOwnerDocument()).getLocation() + ", element:\n" + el, e);
    }
  }

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLWhen n = new XSLWhen(s, p);
  //    n.test = test.cloneIt();
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    //LogWriter.getSystemLogWriter().println("#XSLWhen: test = " + test);
    XObject xo = owner.getXPathProcessor().process(test, xcont, varContext);
    XBoolean xb = xo.toXBoolean();

    if (xb.getValue()) {
      processFromFirst(xcont, node);
      setProcessNext(false);
//      getFirst().process(xcont, node);
    } 
//      if (getNext() != null) {
//        getNext().process(xcont, node);
//      }

    if (xb != xo) {
      xb.close();
    }
    xo.close();
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLWhen: test = " + test); //$JL-SYS_OUT_ERR$
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"test"};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

