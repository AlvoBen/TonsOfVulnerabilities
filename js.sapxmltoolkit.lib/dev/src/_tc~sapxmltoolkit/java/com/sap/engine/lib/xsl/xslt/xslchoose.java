﻿/*
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
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLChoose extends XSLContentNode {

  public XSLChoose(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
  }

  //  
  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLChoose n = new XSLChoose(s, p, null);
  //    
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //    
  //  }
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    processFromFirst(xcont, node);
//    getFirst().process(xcont, node);
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLChoose"); //$JL-SYS_OUT_ERR$
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

