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
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public final class XSLWithParam extends XSLVariable {

  //  protected String select = null;
  //  protected String name = null;
  protected XObject value = null;
  protected boolean processed = false;

  public XSLWithParam(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    select = owner.etBuilder.process(el.getAttribute("select"));
    name = el.getAttribute("name");
  }

  public XObject getValue() {
    return value;
  }

  public boolean isProcessed() {
    return processed;
  }

  public XObject evaluate(XPathContext xcont) throws XPathException, XSLException {
    //   LogWriter.getSystemLogWriter().println("Evaluating: " + getName() +  " varvalue: " + varvalue + " " + this.getClass().getName());
    if (isProcessed()) {
      varvalue = value.setClosed(true);
    }

    if (select != null && select.squery.length() > 0) {
      varvalue = owner.getXPathProcessor().process(select, xcont, varContext).setClosed(true);
    } else {
      CharArray carr = new CharArray();
      //LogWriter.getSystemLogWriter().println("evaluating: "+ name );
      owner.getOutputProcessor().startAttributeValueProcessing(carr);
      owner.getOutputProcessor().setDisableOutputEscaping(true);
      processFromFirst(xcont, xcont.node);
      //getFirst().process(xcont, xcont.node);
      owner.getOutputProcessor().setDisableOutputEscaping(false);
      owner.getOutputProcessor().stopAttributeValueProcessing();
      //LogWriter.getSystemLogWriter().println("---: "+ name );
      varvalue = xcont.getXFactCurrent().getXString(carr).setClosed(true);
    }

    value = varvalue;
    //    LogWriter.getSystemLogWriter().println("XSLWithParam: Value of : " + getName() + " is = " + varvalue.toXString() );
    return varvalue;
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    //    LogWriter.getSystemLogWriter().println("Processing with-param var: " + getName());
    processed = false;
    value = evaluate(xcont);
    processed = true;

    if (parent instanceof XSLApplyTemplates) {
      ((XSLApplyTemplates) parent).addParam(this);
    } else if (parent instanceof XSLCallTemplate) {
      ((XSLCallTemplate) parent).addParam(this);
    }

//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLWithParam: select = " + select + " , name = " + name); //$JL-SYS_OUT_ERR$
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

}

