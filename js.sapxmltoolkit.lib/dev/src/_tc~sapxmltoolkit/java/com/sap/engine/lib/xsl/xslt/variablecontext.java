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
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 0.0.1
 *
 * First Edition: 05.02.2001
 */
import java.util.Hashtable;

import com.sap.engine.lib.xsl.xpath.XPathContext;

public final class VariableContext {

  protected Hashtable bindings = null;
  protected XSLNode parent = null;
  protected XPathContext xcont = null;

  public VariableContext() {

  }

  public VariableContext(XSLNode parent) {
    this.parent = parent;
  }

  public void useXPathContext(XPathContext xcont) {
    this.xcont = xcont;
  }

  public com.sap.engine.lib.xsl.xpath.xobjects.XObject get(String name) {
    //    LogWriter.getSystemLogWriter().println("VariableContext getting var:" + name + ", parent=" + parent);
    if (parent == null) {
      return null;
    }

    try {
      XSLVariable xvar = parent.getVariable(name);
      return xvar.evaluate(xcont);
    } catch (Exception e) {
      //$JL-EXC$
      //in case there is a problem in evaluating the variable, just return null, will be handled
      // in a higher level
      // e.printStackTrace();
      return null;
    }
  }

}

