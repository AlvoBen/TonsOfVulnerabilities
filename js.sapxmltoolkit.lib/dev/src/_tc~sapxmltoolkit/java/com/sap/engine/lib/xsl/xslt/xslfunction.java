package com.sap.engine.lib.xsl.xslt;

import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public class XSLFunction extends XSLContentNode {

  protected String name = null;
  protected XObject resValue = null;
  protected Vector functionParameters = new Vector();
  protected Vector functionVariables = new Vector();
  protected Vector functionMessages = new Vector();
  protected XSLResult functionResult = null;

  public XSLFunction(XSLStylesheet owner) throws XSLException {
    super(owner, null);
  }

  public XSLFunction(XSLStylesheet owner, Element el) throws XSLException {
    super(owner, null, el);
    //    LogWriter.getSystemLogWriter().println("XSLFunction.<init>");
    name = el.getAttribute("name");
    //parent.setVariable(name, this);
    XSLNode node = getFirst();
    XSLNode ntmp = null;
    node = node.getNext();

    while (node != null && node instanceof XSLParam) {
      ntmp = node.getNext();
      node.remove();
      node.setNext(null);
      node.setPrev(null);
      //templateParams.add(((XSLParam)node).getName(), node);
      functionParameters.add(node);
      //getTemplateParamsStack().put(((XSLParam)node).getName(), new Stack());
      //XSLParam pr = (XSLParam)node;
      //LogWriter.getSystemLogWriter().println("Template: " + name + "  param name: " + pr.getName() + "  param select = " + pr.getSelect());
      //node = node.getNext();
      node = ntmp;
    }

    while (node != null && (node instanceof XSLVariable || node instanceof XSLMessage)) {
      ntmp = node.getNext();
      node.remove();
      node.setNext(null);
      node.setPrev(null);
      ((XSLVariable) node).processBeforeEvaluating = true;

      //templateParams.add(((XSLParam)node).getName(), node);
      if (node instanceof XSLVariable) {
        functionVariables.add(node);
      } else {
        functionMessages.add(node);
      }

      //getTemplateParamsStack().put(((XSLParam)node).getName(), new Stack());
      //XSLParam pr = (XSLParam)node;
      //LogWriter.getSystemLogWriter().println("Template: " + name + "  param name: " + pr.getName() + "  param select = " + pr.getSelect());
      //node = node.getNext();
      node = ntmp;
    }

    functionResult = (XSLResult) node;
  }

  public XSLFunction(String name) throws XSLException {
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

  public XObject evaluate(XPathContext xcont, Vector params) throws XPathException, XSLException {
    //Thread.dumpStack();
    if (params.size() != functionParameters.size()) {
      throw new XSLException("Stylesheet function invocation \"" + getName() + "\" : actual and formal parameter lists do not match.");
    }

    for (int i = 0; i < functionParameters.size(); i++) {
      ((XSLParam) functionParameters.get(i)).setValue((XObject) params.get(i));
      ((XSLParam) functionParameters.get(i)).doNotEvaluate = true;
      setVariable(((XSLParam) functionParameters.get(i)).getName(), (XSLParam) functionParameters.get(i));
    } 

    for (int i = 0; i < functionVariables.size(); i++) {
      //((XSLVariable)functionVariables.get(i)).setValue((XObject)params.get(i));
      //LogWriter.getSystemLogWriter().println("XSLFunction: Setting variable " + ((XSLVariable)functionVariables.get(i)).getName() );
      functionResult.setVariable(((XSLVariable) functionVariables.get(i)).getName(), (XSLVariable) functionVariables.get(i));
    } 

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
    //    return varvalue;
    functionResult.processStart();
    functionResult.process(xcont, 0);
    functionResult.processEnd();
    XObject result = functionResult.evaluate(xcont);
    //    LogWriter.getSystemLogWriter().println("XSLFunction.evaluate returning result: " +  result);
    return result;
    //throw new XSLException("XSLFunctions still not evaluatable");
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLFunction: name = " + name);
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

  public void close() throws XPathException {

  }

}

