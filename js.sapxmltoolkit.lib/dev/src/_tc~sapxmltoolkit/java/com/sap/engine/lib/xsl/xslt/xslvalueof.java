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
 * <!ELEMENT xsl:value-of EMPTY>
 * <!ATTLIST xsl:value-of
 *   select %expr; #REQUIRED
 *   disable-output-escaping (yes|no) "no"
 * >
 *
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 0.0.1
 *
 *
 * First Edition: 15.01.2001
 *
 */
import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;

public final class XSLValueOf extends XSLNode {

  private ETObject select = null;
  private boolean disableOutputEscaping = false;

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLValueOf n = new XSLValueOf(s, p);
  //    n.disableOutputEscaping = disableOutputEscaping ;
  //    n.select = select.cloneIt();
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public XSLValueOf(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLValueOf(XSLStylesheet owner, XSLNode parent, String s) throws XSLException {
    super(owner, parent);
    select = owner.etBuilder.process(s);
  }

  public XSLValueOf(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent);
    init(el);
  }

  public void init(Element el) throws XSLException {
    select = owner.etBuilder.process(el.getAttribute("select"));

    if (el.getAttribute("disable-output-escaping") != null) {
      disableOutputEscaping = (el.getAttribute("disable-output-escaping").equals("yes")) ? true : false;
    }
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    xcont.currentNode = this;
    //LogWriter.getSystemLogWriter().println("XSLValueOf.process(): before processing:" + select.squery);
    XObject xo = owner.getXPathProcessor().process(select, xcont, varContext);

    if (!(xo instanceof XJavaObject)) {
      XString xs = xo.toXString();

     // LogWriter.getSystemLogWriter().println("XSLValueOf.process(): xs is " + xs.getValue());
    
      owner.getOutputProcessor().characters(xs.getValue(), disableOutputEscaping);

      if (xo != xs) {
        xs.close();
      }

      xo.close();
    }

//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLValueOf: select = \'" + select + "\'");
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"select"};
  public final static String[] OPTPAR = {"disable-output-escaping"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

