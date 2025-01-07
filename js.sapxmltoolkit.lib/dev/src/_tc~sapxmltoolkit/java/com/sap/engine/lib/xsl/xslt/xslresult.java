package com.sap.engine.lib.xsl.xslt;

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

public class XSLResult extends XSLContentNode {

  protected ETObject select = null;
  protected String name = null;
  protected String type = null;
  protected XObject resValue = null;

  public XSLResult(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLResult(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    String selectString = el.getAttribute("select");
    select = owner.etBuilder.process(selectString);
    name = el.getAttribute("name");
    type = el.getAttribute("type");
  }

  public XSLResult(String name) throws XSLException {
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
    //    LogWriter.getSystemLogWriter().println("Evaluating: " + getName() +  " resValue: " + resValue + " " + this.getClass().getName());
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
    //    resValue = xcont.getXFactCurrent().getXString("Asasas");
    //    }
    return resValue;
  }

  public ETObject getSelect() {
    return select;
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    //xcont.variableBindings.put(name, this);
    if (select != null && select.squery.length() > 0) {
      resValue = owner.getXPathProcessor().process(select, xcont, varContext).setClosed(true);
    } else {
//      CharArray carr = new CharArray();
      //LogWriter.getSystemLogWriter().println("evaluating: "+ name );
      DTM dtm = xcont.dtm;
      int startIndex = dtm.size;
      owner.getOutputProcessor().startAttributeValueProcessing(null);
      owner.getOutputProcessor().setDisableOutputEscaping(true);
      DocHandler orgDocHandler = owner.getOutputProcessor().getDocHandler();
      DTMFactory dtmfact = owner.getDTMFactory();
      dtmfact.setDTM(dtm);
      //      LogWriter.getSystemLogWriter().println("XSLResult: using dtmfact: " + dtmfact + ", dtm.id= " + dtm.hashCode());
      owner.getOutputProcessor().setDocHandler(dtmfact);
      try {
        dtmfact.startDocument();
        processFromFirst(xcont, xcont.node);
        //getFirst().process(xcont, xcont.node);
        dtmfact.endDocument();
      } catch (Exception e) {
        throw new XSLException("", e);
      }
      owner.getOutputProcessor().setDocHandler(orgDocHandler);
      owner.getOutputProcessor().setDisableOutputEscaping(false);
      owner.getOutputProcessor().stopAttributeValueProcessing(false);
//      int fc = startIndex;
      //      LogWriter.getSystemLogWriter().println("XSLVarianle. fc= " + fc);
      //      LogWriter.getSystemLogWriter().println("XSLResult.process: name=" + name +", nodeType[fc]=" + dtm.nodeType[fc] + ", stringValue="+ dtm.getStringValue(fc));
      //      LogWriter.getSystemLogWriter().println("XSLResult.process: name=" + name +", nextSibling[fc]=" + dtm.nextSibling[fc] );
      //      LogWriter.getSystemLogWriter().println("XSLResult.process: name=" + name +", nodeType[fc+2]=" + dtm.nodeType[fc+2] + ", nodename=" + dtm.name[fc+2]);
      //      LogWriter.getSystemLogWriter().println("XSLResult.process: name=" + name +", nextSibling[fc]=" + dtm.nextSibling[fc] );
      int x;
      for (x = dtm.firstChild[startIndex]; x != -1 && dtm.nodeType[x] == dtm.TEXT_NODE; x = dtm.nextSibling[x]) {
        ; 
      }

      //      LogWriter.getSystemLogWriter().println("XSLResult.process: name=" + name +", x=" + x);
      if (x == -1) {
        resValue = xcont.getXFactCurrent().getXString();
        CharArray ca = ((XString) resValue).getValue();

        for (x = dtm.firstChild[startIndex]; x != -1; x = dtm.nextSibling[x]) {
          ca.append(dtm.getStringValue(x));
        } 

        resValue.setClosed(true);
      } else {
        XNodeSet r = xcont.getXFactCurrent().getXNodeSet(dtm);
        //LogWriter.getSystemLogWriter().println("XSLResult.process: process to Nodeset: dtm="+r.dtm);
        r.clear();

        //        LogWriter.getSystemLogWriter().println("XSLResult.process  1 .got nodeset count is: " + r.count());
        for (int i = startIndex; i != -1; i = dtm.nextSibling[i]) {
          //          LogWriter.getSystemLogWriter().println("XSLResult.filling nodeset i=" +i + ", nodeName=" + dtm.name[i]);
          r.add(i);
        } 

        resValue = r;
        r.setClosed(true);
        //        LogWriter.getSystemLogWriter().println("XSLResult.process  2 .got nodeset count is: " + r.count());
      }

      //
      //LogWriter.getSystemLogWriter().println("XSLResult: Value of : " + getName() + " is = " + resValue.toXString() );
    }

    if (parent != null) {
      parent.setVariable(name, owner.getXSLVarEvalPool().getObject().reuse(name, resValue));
    }
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLResult: select = " + select + " , name = " + name); //$JL-SYS_OUT_ERR$
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] OPTPAR = {"select"};

  public String[] getRequiredParams() {
    return new String[] {};
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

  public void close() throws XPathException {

  }

}

