package com.sap.engine.lib.xsl.xslt;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xslt.pool.XSLVarEvalPool;

/**
 * Base class for all XSLT Nodes. This class is the base class for XSLT Nodes.
 * It provides methods for creating list of nodes, traversing and processing
 * this list and also handles the management of variables defined on different
 * levels in the stylesheet
 *
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 1.0
 */
public abstract class XSLNode {

  public static final CharArray EMPTY_CARR = new CharArray().setStatic();
  private int current;
  protected XSLNode parent = null;
  protected XSLNode first = null;
  protected XSLNode last = null;
  protected XSLNode next = null;
  protected XSLNode prev = null;
  private XSLNode head = null;
  protected XSLStylesheet owner = null;
  protected VariableContext varContext = new VariableContext(this);
  private Hashtable templateParamsStack = null;
  private Hashtable paramsTable = null;
  //private XSLVariable xv = null;
  private Stack paramsStack = null;
  public static final int XSL_NODE_GENERIC = 0;
  public static final int XSL_NODE_TEMPLATE = 1;
  public int TYPE = XSL_NODE_GENERIC;
  private boolean processNext = true;

  public XSLNode() {

  }

  public XSLNode(XSLStylesheet owner, XSLNode parent) throws XSLException {
    this(owner, parent, null);
  }

  public XSLNode(XSLStylesheet owner, XSLNode parent, Node node) throws XSLException {
    head = null;
    current = 0;
    current = 0;
    this.owner = owner;
    this.parent = parent;
    first = head;
    last = head;

    if (node != null && node.getNodeType() == Node.ELEMENT_NODE && node.getNamespaceURI() != null && node.getNamespaceURI().equals(XSLStylesheet.sXSLNamespace)) {
      //      LogWriter.getSystemLogWriter().println("XSLNode.init - checking Parameters for node: "+ node.getNodeName());
      Element el = (Element) node;
      String[] parReq = getRequiredParams();
      String[] parOpt = getOptionalParams();
      NamedNodeMap nn = el.getAttributes();

      //check if all required attributes are present
      for (int j = 0; j < parReq.length; j++) {
        if (nn.getNamedItem(parReq[j]) == null) {
          throw new XSLException("Attribute '" + parReq[j] + "' is required for element '" + el.getNodeName() + '\'');
        }
      } 

      //check if there are attributes which should not be there
      for (int i = 0; i < nn.getLength(); i++) {
//        Node n = nn.item(i);

        if (nn.item(i).getNamespaceURI() == null) {
          boolean found = false;

          for (int j = 0; j < parReq.length; j++) {
            if (parReq[j].equals(nn.item(i).getNodeName())) {
              found = true;
              break;
            }

            if (!found) {
              for (int k = 0; k < parOpt.length; k++) {
                //                LogWriter.getSystemLogWriter().println("Checking optional Parameters: " + parOpt[k]);
                if (parOpt[k].equals(nn.item(i).getNodeName())) {
                  found = true;
                  break;
                }
              } 
            }

            if (!found) {
              throw new XSLException("Attribute '" + nn.item(i).getNodeName() + "' not allowed for element '" + el.getNodeName() + '\'');
            }
          } 
        }
      } 
    }
  }

  /**
   * This method
   *
   * @param   xcont
   * @param   node
   * @exception   XSLException
   * @exception   XPathException
   */
  public abstract void process(XPathContext xcont, int node) throws XSLException, XPathException;
  
//  public abstract void process(XPathContext xcont, int node) throws XSLException, XPathException;    

//  final public void process(XPathContext xcont, int node) throws XSLException, XPathException {
//    processStart();
//    processInternal(xcont, node);
//    processEnd();
//  }
  
  final public void processStart() {
    if (owner == null) {
      return;
    }
    if (paramsStack == null) {
      paramsStack = new Stack();
    }
    paramsTable = (Hashtable) owner.getHashTablesPool().getObject();
    paramsTable.clear();
    paramsStack.push(paramsTable);
  }

  final public void processEnd() throws XSLException {
    if (owner == null) {
      return;
    }
    popVariables();
    Hashtable oldTable = (Hashtable) paramsStack.pop();
    owner.getHashTablesPool().releaseObject(oldTable);
    if (paramsStack.empty()) {
      paramsTable = null;
    } else {
      paramsTable = (Hashtable) paramsStack.peek();
    }
  }
  
  final private void popVariables() throws XSLException {
    if (paramsTable == null || paramsTable.size() == 0) {
      return;
    }
    XSLVarEvalPool pool = owner.getXSLVarEvalPool();
    Enumeration keys = paramsTable.keys();
    XSLTemplate template = getParentTemplate();
    Hashtable fakeParams;
    if (template != null) {
      fakeParams = template.getBackwardsCompatibilityVar();
    } else {
      fakeParams = null;
    }
    
    while (keys.hasMoreElements()) {
      String varName = (String) keys.nextElement();
      if (fakeParams != null && fakeParams.containsKey(varName)) {
        continue;
      }
      XSLVariable var = (XSLVariable) paramsTable.get(varName);
      if (var instanceof XSLVarEval) {
        XSLVarEval varEval = (XSLVarEval) var;
        varEval.close(this);
//        LogWriter.getSystemLogWriter().println("XSLNode.popTemplateVars()> " + varEval.name + " = " + varEval.value + " (var> " + varEval.hashCode() + ")");
        pool.releaseObject(varEval);
      }
    }
  }
  

  /**
   *
   *
   * @param   ind
   */
  public abstract void print(String ind);

  /**
   *
   *
   * @return
   */
  protected Hashtable getTemplateParamsStack() {
    if (templateParamsStack == null) {
      templateParamsStack = new Hashtable();
    }

    return templateParamsStack;
  }

  /**
   * Appends a <code>XSLNode</code> to the list of the current node. If the appended node
   * also contains a list of nodes, then the whole list is appended to this node.
   *
   * @param   child  the node that will be appended.
   */
  public void append(XSLNode child) {
    getLast().setNext(child);
    child.setPrev(last);
    last = child;

    while (last.getNext() != null) {
      last = last.getNext();
    }
  }

  /**
   * Removes the current node from the list of nodes and sets the previous and next
   * members of this node to <code>null</code>
   *
   */
  public void remove() {
    prev.setNext(next);
    if (next != null) {
      next.setPrev(prev);
    }
    prev = null;
    next = null;
  }

  /**
   * Returns the next node in the node list
   *
   * @return     the next node in the list or <code>null</code> if this is the last node in the list
   */
  public XSLNode getNext() {
    return next;
  }

  /**
   * Returns the previous node in the node list
   *
   * @return     the previous node in the node list or <code>null</code> if this is the first node.
   *             Typicaly every node list begins with a <code>XSLHeadNode</code> node
   */
//  public XSLNode getPrev() {
//    return prev;
//  }

  public void setPrev(XSLNode value) {
    prev = value;
  }

  /**
   * Returns the parent of the current node. The parent of the top-level nodes is a
   * <code>XSLStylesheet</code>, which does not extend <code>XSLNode</code>
   *
   * @return     the parent of the current node
   */
  public XSLNode getParent() {
    return parent;
  }

  /**
   * Returns the <code>XSLStylesheet</code>, which owns this node
   *
   * @return
   */
  public XSLStylesheet getOwner() {
    return owner;
  }

  /**
   * Returns the first node in the nodelist.
   *
   * @return     the first node in the nodelist of this node
   */
  public XSLNode getFirst() {
    if (head == null) {
      head = new XSLHeadNode();
      first = head;
      last = head;
    }

    return head;
  }
  
  final public void processFromFirst(XPathContext xcont, int node) throws XSLException, XPathException {
    //Thread.dumpStack();
    XSLNode n = getFirst();
    while (n != null) {
      n.processStart();
      n.process(xcont, node);
      n.processEnd();
      if (!n.isProcessNext()) {
        n.setProcessNext(true);
        break;
      }
      n = n.getNext();
    }
  }

  /**
   * Returns the last node in the node list. Used when appending nodes.
   *
   * @return     the last node in the nodelist.
   */
  public XSLNode getLast() {
    if (last == null) {
      getFirst(); //?
    }

    return last;
  }

  public void setFirst(XSLNode n) {
    first = n;
  }

  public void setLast(XSLNode n) {
    last = n;
  }

  public void setNext(XSLNode n) {
    next = n;
  }

  public void setOwner(XSLStylesheet owner) {
    this.owner = owner;
  }

  /**
   * Returns the index of the specified <code>XSLAttribute</code> in the Vector containing XSLAttributes
   *
   * @param   v  the vector containing <code>XSLAttribute</code> nodes
   * @param   x  the <code>XSLAttribute</code> which index should be searched
   * @return     the index
   * @exception   ClassCastException thrown if there are other nodes in the vector
   */
  public int findXSLAttribute(Vector v, XSLAttribute x) {
    for (int i = 0; i < v.size(); i++) {
      //      LogWriter.getSystemLogWriter().println("v i: " +((XSLAttribute) v.get(i)).getName());
      //      LogWriter.getSystemLogWriter().println("x: " +x.getName());
      if (((XSLAttribute) v.get(i)).getName().equals(x.getName())) {
        return i;
      }
    } 

    return -1;
  }

  final public XSLVariable getVariable(String name) throws XSLException {
    if (paramsTable != null && paramsTable.size() > 0) { //look up from local vars
      if (paramsTable.containsKey(name)) {
        return (XSLVariable) paramsTable.get(name); 
      }
    }
    if (templateParamsStack != null) {
      if (templateParamsStack.containsKey(name)) {
        return (XSLVariable) ((Stack) templateParamsStack.get(name)).peek(); 
      }
    }
    XSLNode parent = this.parent;
    if (parent != null) {
      return parent.getVariable(name);
    } else {
      if (this instanceof XSLTemplate) {
        Hashtable fakeParams = ((XSLTemplate) this).getBackwardsCompatibilityVar();
        XSLVariable var = (XSLVariable) fakeParams.get(name);
        if (var != null) {
          return var;
        }
      }
      
      return owner.getVariable(name);
    }
  }

  /**
   * Pops a variable from it's stack
   *
   * @param   name  the name of the variable
   * @return        a <code>XSLVariable</code> object
   */
  public XSLVariable popVar(String name) {
    XSLVariable xv = null;

    if (templateParamsStack == null) {
      templateParamsStack = new Hashtable();
    }

    if (templateParamsStack.get(name) != null) {
      xv = (XSLVariable) ((Stack) templateParamsStack.get(name)).pop();
    }

    return xv;
  }

  /**
   * This method is called from inside a node. It finds the XSLTemplate node containing
   * the current node, and adds this variable to the variables of the template
   *
   * @param   name  the name of the variable
   * @param   var   the variable
   */
  public void setVariable(String name, XSLVariable var) {
/*    XSLNode n = this;
    while (n != null && n.TYPE != XSL_NODE_TEMPLATE) {
      n = n.parent;
    }
    if (n == null) {
      owner.setVariable(name, var);
    } else {
      n.setVariable(name, var);
    }*/
//    LogWriter.getSystemLogWriter().println("XSLNode.setVariable()> " + name + " = " + var.varvalue + " (var> " + var.hashCode() + ")");
    paramsTable.put(name, var);
    
    XSLTemplate template = getParentTemplate();
    if (template != null) {
      template.setBackwardsCompatibilityVar(name, var);
    }
  }
  
  public XSLTemplate getTemplate() {
    XSLNode n = this;
    while (n != null && n.TYPE != XSL_NODE_TEMPLATE) {
      n = n.parent;
    }
    if (n == null) {
      return null;
    } else {
      return (XSLTemplate) n;
    }
  }
  
  private XSLTemplate getParentTemplate() {
    if (this instanceof XSLTemplate || parent == null) {
      return null;
    }
    if (parent instanceof XSLTemplate) {
      return (XSLTemplate) parent;
    }
    return parent.getParentTemplate();
  }

  /**
   * Sets a <code>XSLVariable</code> from a <code>XSLTemplate</code>.
   * This method is called only if the current node is a <code>XSLTemplate</code> node
   * calling <code>setVariable</code> finds the <code>XSLTemplate</code> owner and calls
   * his <code>setVariableFromTemplate</code> method
   *
   * @param   name  name of the variable
   * @param   var   the variable
   */
  public void setVariableFromTemplate(String name, XSLVariable var) {
    if (templateParamsStack == null) {
      templateParamsStack = new Hashtable();
    }

    if (templateParamsStack.get(name) == null) {
      templateParamsStack.put(name, new Stack());
    }

    ((Stack) templateParamsStack.get(name)).push(var);
  }

  /**
   * Returns the <code>VariableContext</code> of this node.
   *
   * @return     the <code>VariableContext</code> of this node
   */
  public VariableContext getVarContext() {
    return varContext;
  }

  public abstract String[] getRequiredParams();

  public abstract String[] getOptionalParams();

  public String realBaseURI;
   
  public XSLNode changeOwner(XSLStylesheet newOwner) {
    if (realBaseURI == null && owner != null) {
      realBaseURI = owner.getBaseURI();
    }
    owner = newOwner;
    if (getFirst() != null) {
      getFirst().changeOwner(newOwner);
    }
    if (getNext() != null) {
      getNext().changeOwner(newOwner);
    }
    return this;
  }

  /**
   * @return
   */
  public boolean isProcessNext() {
    return processNext;
  }

  /**
   * @param b
   */
  public void setProcessNext(boolean b) {
    processNext = b;
  }

}

