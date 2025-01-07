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
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 0.0.1
 *
 *
 * First Edition: 15.01.2001
 *
 */
import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.pool.ObjectPool;

public final class XSLApplyTemplates extends XSLContentNode {

  private ETObject select = null;
  private String mode = null;
//  private XNodeSet intSet = null;
  private Vector sortNodes = new Vector();
  protected Vector params = new Vector();
  protected Sorter sorter = null;
//  protected Vector sendpar = null;
//  protected Vector valuesVector = null;
  protected ObjectPool sendParPool = null;

  public XSLApplyTemplates(XSLStylesheet owner, XSLNode parent, String s, String m) throws XSLException {
    super(owner, parent, null);
    select = owner.etBuilder.process(s);
    mode = m;
//    sendpar = new Vector();
    sendParPool = new ObjectPool(Vector.class, 1, 2);

  }

  public XSLApplyTemplates(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    select = owner.etBuilder.process(el.getAttribute("select"));
    mode = el.getAttribute("mode");
    XSLNode node = getFirst(), ntmp = null;
    node = node.getNext();
//    sendpar = new Vector();
    sendParPool = new ObjectPool(Vector.class, 1, 2);


    while (node != null) {
      if (node instanceof XSLSort) {
        ntmp = node.getNext();
        node.remove();
        node.setNext(null);
        node.setPrev(null);
        sortNodes.add(node);
        node = ntmp;
      } else {
        node = node.getNext();
      }
    }
  }

//  private XSLApplyTemplates(XSLStylesheet o, XSLNode p) throws XSLException {
//    super(o, p);
//  }

  //private int[] nodes = new int[50];
  //private int pNodes = 0;
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    xcont.currentNode = this;
    //LogWriter.getSystemLogWriter().println("XSLApplyTemplates.process, select=" + select.squery + ", freemem= " + Runtime.getRuntime().freeMemory());
    //Thread.dumpStack();
   // owner.freeMem();
    params.clear();

    if (select != null && select.squery.length() > 0) {
      XObject xo = owner.getXPathProcessor().process(select, xcont, varContext);
      
      if (xo.getType() == XJavaObject.TYPE) {
        xo = xo.toXNodeSet();
      }
      if (xo.getType() != XNodeSet.TYPE) {
        throw new XSLException("Select Query does not evaluate to a NodeSet");
      }

      //((XNodeSet) xo).checkOrder();
      //((XNodeSet) xo).sort();
      //      LogWriter.getSystemLogWriter().println("XSLApplyTemplates.process 0, ");
      //      if (((XNodeSet)xo).size() == 0) {
      //        xo.close();
      //        return;
      //      }
      owner.stack = ((XNodeSet) xo).getNodes(owner.stack, owner.pStack);
      xo.close();
    } else {
      owner.stack = xcont.dtm.getChildNodes(xcont.node, owner.stack, owner.pStack);
    }

    //    LogWriter.getSystemLogWriter().println("XSLApplyTemplates.process 1, ");    
    int count = 0;

    while (owner.stack[owner.pStack + count] != -1) {
      count++;
    }

    //LogWriter.getSystemLogWriter().println("XSLApplyTemplates.process !!!!!!!!!!!     COUNT =" + count);
    if (count > 0) {
      if ((sortNodes != null) && (sortNodes.size() > 0)) {
        if (sorter == null) {
          sorter = new Sorter();
        }

        sorter.sort(xcont, owner.stack, owner.pStack, count, sortNodes, owner.getXPathProcessor());
      }

      int pStack0 = owner.pStack;
      owner.pStack += count + 1;
      processFromFirst(xcont, node);
      
      //getFirst().process(xcont, node);
      Vector sendpar = (Vector)sendParPool.getObject();
      sendpar.clear();
      Vector valuesVector = (Vector) sendParPool.getObject();
      valuesVector.clear();
      //valuesVector = new Vector();
      //LogWriter.getSystemLogWriter().println("XSLApplyTemplates.process. sendpar=" + this.hashCode());

      //      LogWriter.getSystemLogWriter().println("XSLApplyTemplates.process 2, ");
      if ((params != null) && (params.size() > 0)) {
        for (int i = 0; i < params.size(); i++) {
//          String name = ((XSLVariable) params.get(i)).getName();
          XSLVariable xv = (XSLVariable) params.get(i);
          XObject varvalue = xv.evaluate(xcont);
          if (varvalue != null) { // && varvalue.getParentDeclared() == null) {
            varvalue.setParentDeclared(this);
            valuesVector.addElement(varvalue);
          }
          sendpar.add(owner.getXSLVarEvalPool().getObject().reuse(xv.getName(), varvalue));
        } 

        //      LogWriter.getSystemLogWriter().println("--------- Printing variables before apply templates to:" + select.squery);
        //      for (int i=0; i<params.size();i++) {
        //        XSLVarEval var = (XSLVarEval)sendpar.get(i);
        //        LogWriter.getSystemLogWriter().println(var.getName() + " = " + var.evaluate(null) + ";" + " Var eval id: " + var.hashCode());
        //      }
        //      LogWriter.getSystemLogWriter().println("-------- Finished");
      }

      //      LogWriter.getSystemLogWriter().println("XSLApplyTemplates.process 3, ");
      owner.process(xcont, owner.stack, pStack0, count, sendpar, mode, select);

      //Release param values
      for (int i = 0; i < valuesVector.size(); i++) {
        XObject xo = (XObject) valuesVector.elementAt(i);
        if (xo != null) {
          xo.closeAndRelease(this);
        }
      }
      
      sendParPool.releaseObject(sendpar);
      sendParPool.releaseObject(valuesVector);
      owner.pStack -= count + 1;
    }

//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLApplyTemplates: select = " + select + " , mode = " + mode);
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public void addParam(XSLVariable var) {
    //    LogWriter.getSystemLogWriter().println("XSLApplyTemplates adding var: " + var.getName());
    params.add(var);
  }

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLApplyTemplates n = new XSLApplyTemplates(s, p);
  //    n.select = select.cloneIt();
  //    n.mode = mode;
  //    copyXSLNodeVector(n.sortNodes, sortNodes, s, p);
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {"select", "mode"};

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

