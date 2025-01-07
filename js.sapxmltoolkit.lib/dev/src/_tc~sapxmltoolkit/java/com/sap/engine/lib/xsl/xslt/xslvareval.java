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
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public final class XSLVarEval extends XSLVariable {

  protected XObject value = null;
  public final static String ES = "";

  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
    throw new XSLException("Trying to clone a XSLVarEval object.");
  }

  public XSLVarEval() throws XSLException {
    super(ES);
  }

  public XSLVarEval reuse(String aname, XObject value) throws XSLException {
    //    if (this.value != null) {
    //      this.value.setClosed(false);
    //      this.value.close();
    //    }
    this.value = value.setClosed(true);
    name = aname;
    //    parent.setVariable(name, this);
    return this;
  }

  public XSLVarEval(String name, XObject value) throws XSLException {
    super(name);
    this.value = value.setClosed(true);
  }

  public XObject evaluate(XPathContext xcont) throws XPathException, XSLException {
    return value;
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {

  }

  public void print(String ind) {

  }

  public void close() throws XPathException {
    throw new RuntimeException("Close without parameters should not be invoked");
  }

  public void close(XSLNode closingParent) throws XPathException {
    if (this.value != null) {
      this.value.closeAndRelease(closingParent);
      this.value = null;
    }
  }
}

