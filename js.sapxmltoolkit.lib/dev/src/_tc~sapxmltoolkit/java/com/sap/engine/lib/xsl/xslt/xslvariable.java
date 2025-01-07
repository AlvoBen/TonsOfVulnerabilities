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
import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;

public class XSLVariable extends XSLContentNode {
  
  protected ETObject select = null;
  protected String name = null;
  protected XObject varvalue = null;
  public boolean processBeforeEvaluating = false; // proerty used to evaluate variables in functions

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLVariable n = new XSLVariable(s, p);
  //    n.select = select.cloneIt();
  //    n.name = name;
  //    
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public XSLVariable(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLVariable(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    //LogWriter.getSystemLogWriter().println("--" + el.getAttribute("select"));
    name = el.getAttribute("name");
    try {
      select = owner.etBuilder.process(el.getAttribute("select"));
    } catch (Exception e) {
      throw new XSLException("Error while initializing variable '" + name +"', select='" + el.getAttribute("select") + "' : " + e.getMessage(), e);
    }
    name = el.getAttribute("name");
    //parent.setVariable(name, this);
  }

  public XSLVariable(String name) throws XSLException {
    super(null, null, null);
    this.name = name;
  }

  public boolean isResultTree() {
    if (getFirst().getNext() != null) {
      return true;
    } else {
      return false;
    }
  }

  public String getName() {
    return name;
  }

  public XObject evaluate(XPathContext xcont) throws XPathException, XSLException {
    if (processBeforeEvaluating) {
      processStart();
      process(xcont, 0);
      processEnd();
    }

    //    LogWriter.getSystemLogWriter().println("Evaluating: " + getName() +  " varvalue: " + varvalue + " " + this.getClass().getName());
    //    if (this instanceof XSLWithParam  &&  ((XSLWithParam)this).isProcessed()) {
    //      return ((XSLWithParam)this).getValue();
    //    }
    //    if (select != null && select.squery.length() > 0) {  
    //      return owner.getXPathProcessor().process(select, xcont, varContext);
    //    } else {
    //      CharArray carr = new CharArray();
    //      //LogWriter.getSystemLogWriter().println("evaluating: "+ name );
    //      owner.getOutputProcessor().startAttributeValueProcessing(carr);
    //      getFirst().process(xcont, xcont.node);
    //      owner.getOutputProcessor().stopAttributeValueProcessing();
    //      //LogWriter.getSystemLogWriter().println("---: "+ name );
    //    varvalue = xcont.getXFactCurrent().getXString("Asasas");
    //    }
    //    LogWriter.getSystemLogWriter().println("Returning: " + varvalue);
    return varvalue;
  }

  public ETObject getSelect() {
    return select;
  }

  static int i = 0;

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    xcont.currentNode = this;
    //Thread.dumpStack();
    //xcont.variableBindings.put(name, this);
//    LogWriter.getSystemLogWriter().println("XSLVariable: Processing: " + getName());
//    if ("DocLocation-Location111".equals(getName())) {
//      LogWriter.getSystemLogWriter().println(owner.getOutputProcessor().bProcessAttributeValueTemplate);
//      Thread.dumpStack();
//    }
    if (this instanceof XSLWithParam && ((XSLWithParam) this).isProcessed()) {
      varvalue = ((XSLWithParam) this).getValue().setClosed(true);
    } else if (select != null && select.squery.length() > 0) {
      varvalue = owner.getXPathProcessor().process(select, xcont, varContext).setClosed(true);
    } else {
//      CharArray carr = new CharArray();
      //LogWriter.getSystemLogWriter().println("evaluating: "+ name );
      DTM dtm = xcont.dtm;
      int startIndex = dtm.size;
      
      /* In case the output processor is configured to ouptut to some specific chararray,
         it is reset to output to the DTM factory. Afterwards (below) this is reverted, 
         and the DTM contains the subtree, appended at its end (startIndex will be the index
         of the root node.)*/
      boolean attrvProc = owner.getOutputProcessor().getBProcessAVT(); 
      owner.getOutputProcessor().startAttributeValueProcessing(null);      
      owner.getOutputProcessor().setBProcessAVT(false);
      owner.getOutputProcessor().setDisableOutputEscaping(true);
      
      DocHandler orgDocHandler = owner.getOutputProcessor().getDocHandler();
      DTMFactory dtmfact = owner.getDTMFactory();
      DTM oldDTM = dtmfact.getDTM();
      dtmfact.setDTM(dtm);
      //      LogWriter.getSystemLogWriter().println("XSLVariable: using dtmfact: " + dtmfact + ", dtm.id= " + dtm.hashCode());
      owner.getOutputProcessor().setDocHandler(dtmfact);
      try {
        dtmfact.startDocument();
        processFromFirst(xcont, xcont.node);
        //getFirst().process(xcont, xcont.node);
        dtmfact.endDocument();
      } catch (Exception e) {
        throw new XSLException("", e);
      }
      
      dtmfact.setDTM(oldDTM);
      
      /*Now reset the output processor to its original state.*/
      owner.getOutputProcessor().setDocHandler(orgDocHandler);
      owner.getOutputProcessor().setDisableOutputEscaping(false);
      owner.getOutputProcessor().stopAttributeValueProcessing(false);
      owner.getOutputProcessor().setBProcessAVT(attrvProc);
      
      //      int fc = startIndex;
      //      LogWriter.getSystemLogWriter().println("XSLVarianle. fc= " + fc);
      //      LogWriter.getSystemLogWriter().println("XSLVariable.process: name=" + name +", nodeType[fc]=" + dtm.nodeType[fc] + ", stringValue="+ dtm.getStringValue(fc));
      //      LogWriter.getSystemLogWriter().println("XSLVariable.process: name=" + name +", nextSibling[fc]=" + dtm.nextSibling[fc] );
      //      LogWriter.getSystemLogWriter().println("XSLVariable.process: name=" + name +", nodeType[fc+2]=" + dtm.nodeType[fc+2] + ", nodename=" + dtm.name[fc+2]);
      //      LogWriter.getSystemLogWriter().println("XSLVariable.process: name=" + name +", nextSibling[fc]=" + dtm.nextSibling[fc] );
      int x;
      for (x = dtm.firstChild[startIndex]; x != -1 && dtm.nodeType[x] == dtm.TEXT_NODE; x = dtm.nextSibling[x]) {
        ; 
      }

      //      LogWriter.getSystemLogWriter().println("XSLVariable.process: name=" + name +", x=" + x);

      if (x == -1) {
        varvalue = xcont.getXFactCurrent().getXString();
        

        CharArray ca = ((XString) varvalue).getValue();

        for (x = dtm.firstChild[startIndex]; x != -1; x = dtm.nextSibling[x]) {
//          ca.append(dtm.getStringValue(x));
          dtm.appendStringValue(x, ca);
        } 

//        LogWriter.getSystemLogWriter().println("XSLVariable: varvalue is " + varvalue);

        varvalue.setClosed(true);
      } else {
        XNodeSet r = xcont.getXFactCurrent().getXNodeSet(dtm);
        //LogWriter.getSystemLogWriter().println("XSLVariable.process: process to Nodeset: dtm="+r.dtm);
        r.clear();

        for (int i = startIndex; i != -1; i = dtm.nextSibling[i]) {
          r.add(i);
        } 

        varvalue = r;
        r.setClosed(true);
        xcont.dtm = dtm;
      }

      //
      //LogWriter.getSystemLogWriter().println("XSLVariable: Value of : " + getName() + " is = " + varvalue.toXString() );
    }

    if (varvalue != null && varvalue.getParentDeclared() == null) {
      if (parent != null) {
        varvalue.setParentDeclared(parent);
      } else {
        varvalue.setTopVariableValue(true);
      }
    }
    if (parent != null) {
      XSLVarEval varEval = owner.getXSLVarEvalPool().getObject().reuse(name, varvalue);
      parent.setVariable(name, varEval);
      //LogWriter.getSystemLogWriter().println("XSLVAriable: setting variable: name=" + name + " varvalue=" + varvalue);
    }
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLVariable: select = " + select + " , name = " + name);
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"name"};
  public final static String[] OPTPAR = {"select"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

  public void close() throws XPathException {

  }

}



