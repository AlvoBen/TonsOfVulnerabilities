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
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public final class XSLProcessingInstruction extends XSLContentNode {

  private CharArray name = new CharArray();
  private ETObject etname = null;

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLProcessingInstruction n = new XSLProcessingInstruction(s, p);
  //    n.name = name;
  //    n.etname = etname.cloneIt();
  //    //copyXSLNodeVector(n.sortNodes, sortNodes, s, p);
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  //
  public XSLProcessingInstruction(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLProcessingInstruction(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    String tempname = el.getAttribute("name");

    if (tempname.charAt(0) == '{' || tempname.charAt(tempname.length() - 1) == '}') {
      etname = owner.etBuilder.process(tempname.substring(1, tempname.length() - 1));
      //XObject xo = owner.getXPathProcessor().process(name, xcont, varContext);
      //name = xo.toXString().value.getString();
    } else {
      name.set(tempname);
    }
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    if (etname != null) {
      //name = name.substring(1, name.length()-1);
      XObject xo = owner.getXPathProcessor().process(etname, xcont, varContext);
      name.set(xo.toXString().getValue());
    }

    //owner.handleText("<?" + name + " ");
    CharArray carr = new CharArray();
    owner.getOutputProcessor().startAttributeValueProcessing(carr);
    processFromFirst(xcont, node);
//    if (getFirst() != null) {
//      getFirst().process(xcont, node);
//    }
    owner.getOutputProcessor().stopAttributeValueProcessing();
    owner.getOutputProcessor().processingInstruction(name, carr);
    //owner.handleText(carr.toString() + "?>");
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLProcessingInstruction  name = " + name);
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"name"};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

