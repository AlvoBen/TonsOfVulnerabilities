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
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLSort extends XSLNode {

//  private boolean disableOutputEscaping = false;
//  private String data = null;
  private ETObject select = null;
  private String lang = null;
  private String order = null;
  private String dataType = null;
  private String caseOrder = null;
  private int iOrder = ORD_ASC;
  private int iCase = CASE_UP;
  private int iType = TYPE_TEXT;
  public final static int ORD_ASC = 1;
  public final static int ORD_DESC = -1;
  public final static int CASE_UP = 1;
  public final static int CASE_DOWN = 2;
  public final static int TYPE_TEXT = 1;
  public final static int TYPE_NUMBER = 2;

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLSort n = new XSLSort(s, p);
  //    n.disableOutputEscaping = disableOutputEscaping ;
  //    n.data = data;
  //    n.select = select.cloneIt();
  //    n.lang = lang;
  //    n.order = order;
  //    n.dataType = dataType;
  //    n.caseOrder = caseOrder;
  //    n.iOrder = iOrder;
  //    n.iCase = iCase;
  //    n.iType = iType;
  //    
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public int getOrder() {
    return iOrder;
  }

  public int getDataType() {
    return iType;
  }

  public int getCaseOrder() {
    return iCase;
  }

  public ETObject getSelect() {
    return select;
  }

  public String getLang() {
    return lang;
  }

  public XSLSort(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLSort(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent);
    //select = el.getAttribute("select");
    select = owner.etBuilder.process((el.getAttribute("select").length() == 0) ? "." : el.getAttribute("select"));
    //select = owner.etBuilder.process(el.getAttribute("select"));
    lang = el.getAttribute("lang");
    order = el.getAttribute("order");
    caseOrder = el.getAttribute("case-order");
    dataType = el.getAttribute("data-type");
    if (order != null && order.equals("ascending")) {
      iOrder = ORD_ASC;
    } else if (order != null && order.equals("descending")) {
      iOrder = ORD_DESC;
    }
    if (caseOrder != null && caseOrder.equals("upper-first")) {
      iCase = CASE_UP;
    } else if (caseOrder != null && caseOrder.equals("lower-first")) {
      iCase = CASE_DOWN;
    }
    if (dataType != null && dataType.equals("text")) {
      iType = TYPE_TEXT;
    } else if (dataType != null && dataType.equals("number")) {
      iType = TYPE_NUMBER;
    }
  }

  //public void process1(XPathContext 
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLSort: select=" + select + ", lang=" + lang + ", order=" + order + ", case-order=" + caseOrder);
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"select"};
  public final static String[] OPTPAR = {"select", "lang", "data-type", "order", "case-order"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

