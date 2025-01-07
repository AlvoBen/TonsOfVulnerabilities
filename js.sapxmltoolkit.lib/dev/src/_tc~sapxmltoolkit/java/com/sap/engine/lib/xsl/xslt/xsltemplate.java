package com.sap.engine.lib.xsl.xslt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.ETLocationStep;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.pool.XSLVarEvalPool;

/*
 * @author Vladimir Savtchenko
 * @version 1.0
 */
public final class XSLTemplate extends XSLContentNode implements Comparable {

  public static final String EMPTYSTRING = "";
  public ETObject match = null;
  public String name = null;
  public double priority = 0;
  public boolean bSpecifiedPriority = false;
  public String mode = null;
  protected boolean def = false;
  protected int importPrecedence = -1;
  protected int position = -1;
  private XSLTemplate ovrName = null;
  private XSLTemplate ovrMatch = null;
  private Stack templateVariables = new Stack();
  private String varSeparator = "<>";
  //protected static StaticDouble staticDouble = new StaticDouble();
  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLTemplate n = new XSLTemplate(s);
  //    n.match = match.cloneIt();
  //    n.name = name;
  //    n.priority = priority;
  //    n.mode = mode;
  //
  //    n.def = def;
  //    n.importPrecedence = importPrecedence;
  //    n.position = position;
  //
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  //protected Hashtable varContextDefault = new Hashtable();
  //protected Hashtable varContextCurrent = null;
  //protected Hashtable templateParams = new Hashtable();
  protected Vector templateParams = new Vector();
  
  private Hashtable fakeParams = new Hashtable(); //Keep variables and params - backwards compatibility mode with variable declarations
   

  //public Hashtable getVarContext() {
  //  return varContectCurrent;
  // }
  public void setPriority(String p) throws XSLException {
    if (p != null && p.length() > 0) {
      priority = owner.staticDouble.stringToDouble(p);
      bSpecifiedPriority = true;
    } else {
      if (match.et instanceof ETLocationStep) {
        ETLocationStep loc = (ETLocationStep) match.et;

        if (loc.getNodeTest().rawname.equals("*")) {
          priority = -0.25;
        } else if (loc.getNodeTest().localname.equals("*")) {
          priority = -0.12;
        } else if (loc.getNodeTest().localname.equals("node()") || loc.getNodeTest().localname.equals("text()") || loc.getNodeTest().localname.equals("comment()")) {
          priority = -0.5;
        } else {
          priority = 0;
        }
      } else {
        priority = 0.5;
      }
    }
  }

  public XSLTemplate(XSLStylesheet owner) throws XSLException {
    super(owner, null);
    TYPE = XSL_NODE_TEMPLATE;
  }

  public XSLTemplate(XSLStylesheet owner, Element el, int ip, int pos) throws XSLException {
    super(owner, null, el);
    TYPE = XSL_NODE_TEMPLATE;
    this.owner = owner;
    match = owner.etBuilder.process(el.getAttribute("match"));
    name = el.getAttribute("name");
    mode = el.getAttribute("mode");
    setPriority(el.getAttribute("priority"));
    importPrecedence = ip;
    position = pos;
    XSLNode node = getFirst(), ntmp = null;
    node = node.getNext();

    while (node != null && node instanceof XSLParam) {
      ntmp = node.getNext();
      node.remove();
      node.setNext(null);
      node.setPrev(null);
      //templateParams.add(((XSLParam)node).getName(), node);
      templateParams.add(node);
      getTemplateParamsStack().put(((XSLParam) node).getName(), new Stack());
      //XSLParam pr = (XSLParam)node;
      //LogWriter.getSystemLogWriter().println("Template: " + name + "  param name: " + pr.getName() + "  param select = " + pr.getSelect());
      //node = node.getNext();
      node = ntmp;
    }
  }

  public XSLTemplate(XSLStylesheet owner, String m, String n, double p, String mode, boolean def, int ip, int pos) throws XSLException {
    super(owner, null, null);
    TYPE = XSL_NODE_TEMPLATE;
    match = owner.etBuilder.process(m);
    name = n;
    priority = p;
    bSpecifiedPriority = true;
    mode = m;
    this.def = def;
    importPrecedence = ip;
    position = pos;
  }

  public String toString() {
    return "#XSLTemplate: match = " + match.squery + " name = " + name;
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLTemplate: match = " + match.squery + " name = " + name);
    getFirst().print(ind + "  ");
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    process(xcont, node, null);
  }

  public void pushTemplateVarStart() {
    //    LogWriter.getSystemLogWriter().println("Pushing varStart(): template - name=" + name + " match=" + match.squery);
    //    for (int i = 0; i < templateVariables.size(); i++) {
    //    }
    //    LogWriter.getSystemLogWriter().println("Varseparator:" + varSeparator.hashCode());
    templateVariables.push(varSeparator);
  }

  //
  //    for (int j=0; j < templateParams.size(); j++)  {
  //      XSLVariable xv = (XSLVariable)templateParams.get(j);
  //      xv = popVar(xv.getName());
  //      if (xv instanceof XSLVarEval) {
  //        owner.getXSLVarEvalPool().releaseObject((XSLVarEval)xv);
  //      }
  //    }
  //    LogWriter.getSystemLogWriter().println("Pooping all");
  //    String n = null;
  //    XSLVariable xv = null;
  //    while (!setVarStack.empty()) {
  //      n = (String)setVarStack.pop();
  //      LogWriter.getSystemLogWriter().println(n);
  //      xv = popVar(n);
  //      if (xv instanceof XSLVarEval) {
  //        owner.getXSLVarEvalPool().releaseObject((XSLVarEval)xv);
  //      }
  //    }
  public void popTemplateVars() throws XSLException {
    String a = null;

    //    LogWriter.getSystemLogWriter().println("Popping template params(): template - name=" + name + " match=" + match.squery);
    //    LogWriter.getSystemLogWriter().println("vars.peek():" + templateVariables.peek().hashCode());
    while (!(a = (String) templateVariables.pop()).equals(varSeparator)) {
      //      LogWriter.getSystemLogWriter().println(a);
      XSLVarEval xv = (XSLVarEval) popVar(a);
      xv.close(this);
      owner.getXSLVarEvalPool().releaseObject(xv);
    }
  }

  public void process(XPathContext xcont, int node, Vector params) throws XSLException, XPathException {
    //    clearTemplateVariableBindings();
    //    LogWriter.getSystemLogWriter().println("Processing template: name=" + name + ", match=" + match.squery + ", owner.id=" + owner.hashCode());
    pushTemplateVarStart();

    //    LogWriter.getSystemLogWriter().println("----------------vars.peek():" + templateVariables.peek().hashCode());
    //first check to see if any of the TemplateParams(TP) are in the Supplied Params (SP)
    //if there are some SP which aren't in the TP they are ignored
    for (int i = 0; i < templateParams.size(); i++) {
      XSLVariable xv = (XSLVariable) templateParams.get(i);
      int j;

      for (j = 0; params != null && j < params.size(); j++) {
        //if (params.get(j) == null) continue;
        String name = ((XSLVariable) params.get(j)).getName();

        if (name.equals(xv.getName())) {
          // if TPx == SPx .. then use the SPx and then delet it from 'params'
          // the SPx are evaluated by ApplyTemplates and CallTemplate .. so dont reevalute them
          //setVariable(name, (XSLVariable)params.get(j));
          XObject varvalue = ((XSLVariable) params.get(j)).evaluate(xcont);
          if (varvalue != null) { // && varvalue.getParentDeclared() == null) {
            varvalue.setParentDeclared(this);
          }
          setVariable(name, owner.getXSLVarEvalPool().getObject().reuse(name, varvalue));
          //params.set(j, null);
          break;
        }
      } 

      if (params == null || j == params.size()) {
        // if this TPx wasn't found in SP.. then use it ..but first evaluate it
        // this case can be combined with the case when params==null but this is much
        // clearer and a little more faster since it's broken into several instructions
        XObject varvalue = xv.evaluate(xcont);
        if (varvalue != null) { // && varvalue.getParentDeclared() == null) {
          varvalue.setParentDeclared(this);
        }
        setVariable(xv.getName(), owner.getXSLVarEvalPool().getObject().reuse(xv.getName(), varvalue));
      }
    } 

    // this just checks if more parameters were supplied to the template to remove the unneded
    // parameters... because they were got from the pool
    //    if (params != null) {
    //      for (int i=0; i < params.size(); i++) {
    //        if (params.get(i) != null) {
    //          owner.getXSLVarEvalPool().releaseObject((XSLVarEval)params.get(i));
    //        }
    //      }
    //    }
    //
    //
    //      int i = 0;
    //        for (i=0; i<params.size(); i++) {
    //          String name = ((XSLVariable)params.get(i)).getName();
    //          if (name.equals(xv.getName())) {
    //            //if one of the supplied temlate parameters has the same name as
    //            //one of the template parameters... use this parameter
    //            // but this parameter is alreadi XSLVarEval .. so don't take any new
    //            // XSLVarEvalOject from the pool
    //            setVariable(name, (XSLVariable)params.get(i));
    //  //            LogWriter.getSystemLogWriter().println("1Setting variable: " + xv.getName());
    //            //setVariable(xv.getName(), owner.getXSLVarEvalPool().getObject().reuse(xv.getName(), ((XSLVariable)params.get(i)).evaluate(xcont)));
    //            break;
    //          }
    //        }
    //        if (i == params.size()) {
    //          // if the supplied parameter wasn't in the list of template parameters
    //          // include it to the list of varibles.. but don't get a new XSLVarEval object from pool
    //          // because the object has been already got from there
    //  //            LogWriter.getSystemLogWriter().println("2Setting variable: " + xv.getName());
    //          //setVariable(xv.getName(), new XSLVarEval(xv.getName(), xv.evaluate(xcont)));
    //          setVariable(xv.getName, xv);
    //          //setVariable(xv.getName(), owner.getXSLVarEvalPool().getObject().reuse(xv.getName(), xv.evaluate(xcont)));
    //        }
    //      } else {
    //  //        LogWriter.getSystemLogWriter().println("3Setting variable: " + xv.getName() + " type: " + xv);
    //        setVariable(xv.getName(), owner.getXSLVarEvalPool().getObject().reuse(xv.getName(), xv.evaluate(xcont)));
    //        //setVariable(xv.getName(), new XSLVarEval(xv.getName(), xv.evaluate(xcont)));
    //      }
    //
    //    }
    //    LogWriter.getSystemLogWriter().println("Processing first...");
    //    LogWriter.getSystemLogWriter().println("---------------1-vars.peek():" + templateVariables.peek().hashCode());
    processFromFirst(xcont, node);
    //getFirst().process(xcont, node);
    //    LogWriter.getSystemLogWriter().println("Ready processing..");
    //
    //    LogWriter.getSystemLogWriter().println("---------------2-vars.peek():" + templateVariables.peek().hashCode());
    popTemplateVars();
  }

  public void setVariable(String name, XSLVariable var) {
    //    LogWriter.getSystemLogWriter().println("4Setting variable: " + name + " type: " + var);
    setVariableFromTemplate(name, var);
    templateVariables.push(name);
    //    if ((templateVariables.size() % 1000) == 0) {
    //      LogWriter.getSystemLogWriter().println(templateVariables.size());
    //    }
  }

  public String getName() {
    return name;
  }

  public boolean isMatchable() {
    if (match.squery == null || match.squery.length() == 0) {
      return false;
    } else {
      return true;
    }
  }

  public double getPriority() {
    return priority;
  }

  public String getMode() {
    if (mode == null) {
      return EMPTYSTRING;
    } else {
      return mode;
    }
  }

  public boolean isDefault() {
    return def;
  }

  public int compareTo(Object o) {
    XSLTemplate t = (XSLTemplate) o;

    if (importPrecedence != t.importPrecedence) {
      if (importPrecedence < t.importPrecedence) {
        return -1;
      } else {
        return 1;
      }
    }

    if (priority != t.priority) {
      //LogWriter.getSystemLogWriter().print("Comparing: " + priority + "<" + t.priority + "  -  ");
      if (priority < t.priority) {
        //LogWriter.getSystemLogWriter().println(true);
        return -1;
      } else {
        //LogWriter.getSystemLogWriter().println(false);
        return 1;
      }

      //return (int)((priority - t.priority)*100);
    }

    return position - t.position;
  }

  public boolean isSameMatch(XSLTemplate t) {
    if (t.match.squery.equals(match.squery) && ((t.mode == null && mode == null) || t.mode.equals(mode))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isSameName(XSLTemplate t) {
    if (name != null && name.length() > 0 && name.equals(t.name)) {
      return true;
    } else {
      return false;
    }
  }

  public void setOverloadByMatch(XSLTemplate t) {
    ovrMatch = t;
  }

  public void setOverloadByName(XSLTemplate t) {
    ovrName = t;
  }

  public void setPosition(int pos) {
    position = pos;
  }

  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {"match", "name", "priority", "mode"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

  public XSLNode changeOwner(XSLStylesheet newOwner) {
    super.changeOwner(newOwner);

    if (templateParams != null) {
      for (int i = 0; i < templateParams.size(); i++) {
        ((XSLNode) templateParams.get(i)).changeOwner(newOwner);
      } 
    }

    return this;
  }

  public void setBackwardsCompatibilityVar(String name, XSLVariable variable) {
    //Do not close old values, they will be closed when the real parent finishes     
    fakeParams.put(name, variable);
  }

  public Hashtable getBackwardsCompatibilityVar() {
    return fakeParams;
  }
}

