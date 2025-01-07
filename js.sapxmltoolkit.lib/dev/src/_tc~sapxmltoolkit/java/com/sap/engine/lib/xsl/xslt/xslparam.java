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

public final class XSLParam extends XSLVariable {

  //  protected String select = null;
  //  protected String name = null;
  private XObject parameterValue = null;
  public boolean doNotEvaluate = false; // used when the parameter is a stylesheet function patameter

  public XSLParam(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    select = owner.etBuilder.process(el.getAttribute("select"));
    name = el.getAttribute("name");
  }

  public XObject evaluate(XPathContext xcont) throws XPathException, XSLException {
    if (doNotEvaluate) {
      return parameterValue;
    }

    //    LogWriter.getSystemLogWriter().println("Evaluating: " + getName() +  " varvalue: " + varvalue + " " + this.getClass().getName());
    //    if (this instanceof XSLWithParam  &&  ((XSLWithParam)this).isProcessed()) {
    //      varvalue =  ((XSLWithParam)this).getValue();
    //    }
    if (select != null && select.squery.length() > 0) {
      varvalue = owner.getXPathProcessor().process(select, xcont, varContext);
    } else {
      CharArray carr = new CharArray();
      //LogWriter.getSystemLogWriter().println("evaluating: "+ name );
      owner.getOutputProcessor().startAttributeValueProcessing(carr);
      processFromFirst(xcont, xcont.node);
      //getFirst().process(xcont, xcont.node);
      owner.getOutputProcessor().stopAttributeValueProcessing();
      //LogWriter.getSystemLogWriter().println("---: "+ name );
      varvalue = xcont.getXFactCurrent().getXString(carr);
    }

    //    value = varvalue;
    return varvalue;
  }

  public void setValue(XObject parameterValue) {
    this.parameterValue = parameterValue;
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    super.process(xcont, node);
    //if (getNext() != null) getNext().process(xcont, node);
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLParam: select = " + select + " , name = " + name);
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

}

