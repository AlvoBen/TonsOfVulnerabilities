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
import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public final class XSLCallTemplate extends XSLContentNode {

  private String name = null;
  //private Vector sortNodes = null;
  //protected int[] nodes = null;
  protected Vector params = null;
  protected Vector sendpar = null;
  protected Vector varvalues = null;

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLCallTemplate n = new XSLCallTemplate(s, p);
  //    n.name = name;
  //    copyXSLNodeVector(n.params, params, s, p);
  //    copyXSLNodeVector(n.sendpar, sendpar, s, p);
  //    
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //    
  //  }
  public XSLCallTemplate(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLCallTemplate(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    name = el.getAttribute("name");
    //sortNodes = new Vector();
    params = new Vector();
    sendpar = new Vector();
    varvalues = new Vector();
    XSLNode node = getFirst(), ntmp = null;
    ;
    node = node.getNext();

    while (node != null) {
      if (node instanceof XSLWithParam) {
        ntmp = node.getNext();
        node.remove();
        node.setNext(null);
        node.setPrev(null);
        params.add(node);
        node = ntmp;
      } else {
        node = node.getNext();
      }
    }
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    processFromFirst(xcont, node);
//    getFirst().process(xcont, node);
    //    LogWriter.getSystemLogWriter().println("########### Calling template : " + name);
    sendpar.clear();
    varvalues.clear();

    if (params != null && params.size() > 0) {
      for (int i = 0; i < params.size(); i++) {
//        String name = ((XSLVariable) params.get(i)).getName();
        XSLVariable xv = (XSLVariable) params.get(i);
        XObject varvalue = xv.evaluate(xcont);
        if (varvalue != null) { // && varvalue.getParentDeclared() == null) {
          varvalue.setParentDeclared(this);
          varvalues.addElement(varvalue);
        }
        sendpar.add(owner.getXSLVarEvalPool().getObject().reuse(xv.getName(), varvalue));
      } 
    }

    //   LogWriter.getSystemLogWriter().println("--------- Printing variables before call templates to:" + name + " ; ");
    //    for (int i=0; i<params.size();i++) {
    //      XSLVarEval var = (XSLVarEval)sendpar.get(i);
    //      LogWriter.getSystemLogWriter().println(var.getName() + " = " + var.evaluate(null) + ";" + " Var eval id: " + var.hashCode());
    //    }
    //    LogWriter.getSystemLogWriter().println("-------- Finished");
    //    for (int i=0; i<sendpar.size(); i++) {
    //      ((XSLVariable)sendpar.get(i)).process(xcont, node);
    //    }
    owner.callTemplate(name, xcont, sendpar);
    
    //Release param values
    for (int i = 0; i < varvalues.size(); i++) {
      XObject xo = (XObject) varvalues.elementAt(i);
      if (xo != null) {
        xo.closeAndRelease(this);
      }
    }
    varvalues.clear();
    
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLCallTemplate: name = " + name);
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public void addParam(XSLVariable var) {
    params.add(var);
  }

  public final static String[] REQPAR = {"name"};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

  public XSLNode changeOwner(XSLStylesheet newOwner) {
    super.changeOwner(newOwner);

    for (int i = 0; i < params.size(); i++) {
      ((XSLNode) params.get(i)).changeOwner(newOwner);
    } 

    return this;
  }

}

