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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLText extends XSLNode {

  private boolean disableOutputEscaping = false;
  private CharArray data = null;

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLText n = new XSLText(s, p);
  //    n.disableOutputEscaping = disableOutputEscaping ;
  //    n.data = data.copy();
  //    
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public XSLText(XSLStylesheet owner, XSLNode parent, String data, boolean disesc) throws XSLException {
    super(owner, parent);
    this.data = new CharArray(data);
    this.data.clearStringValue();
    disableOutputEscaping = disesc;
  }

  public XSLText(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLText(XSLStylesheet owner, XSLNode parent, String data) throws XSLException {
    super(owner, parent);
    //    LogWriter.getSystemLogWriter().println("XSLText() data=[" + data + "]");
    this.data = new CharArray(data);
    this.data.clearStringValue();

  }

  public XSLText(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent);
    this.data = new CharArray("");
    this.data.clearStringValue();


    if (el.getAttribute("disable-output-escaping") != null) {
      disableOutputEscaping = (el.getAttribute("disable-output-escaping").equals("yes")) ? true : false;
    }

    NodeList nl = el.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      int type = nl.item(i).getNodeType();
      if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE) {
        data.append(((Text) nl.item(i)).getData());
      }
    } 

    if (data.length() > 0) {
      if (Symbols.isWhitespace(data.charAt(0)) || Symbols.isWhitespace(data.charAt(data.length() - 1))) {
        owner.setIsIndentAllowed(false);
      }
    }
    this.data.clearStringValue();

  }

  public void init(Element el) throws XSLException {
    if (el.getAttribute("disable-output-escaping") != null) {
      disableOutputEscaping = (el.getAttribute("disable-output-escaping").equals("yes")) ? true : false;
    }
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    //    if (owner.debug) {
          //LogWriter.getSystemLogWriter().println("XSLText::process(): -" + data + "-" + disableOutputEscaping);
    //    }
    owner.getOutputProcessor().characters(data, disableOutputEscaping);
    //owner.getOutputProcessor().setWhitespaceOnXML();
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLText: data = \'" + data + "\'");
    if (getNext() != null) {
      getNext().print(ind);
    }
    //LogWriter.getSystemLogWriter().println("  next ->\n" + getNext());
    //return "";
  }

  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {"disable-output-escaping"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

