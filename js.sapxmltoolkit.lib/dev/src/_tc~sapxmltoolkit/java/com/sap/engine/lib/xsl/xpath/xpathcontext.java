package com.sap.engine.lib.xsl.xpath;

import java.util.Map;

import com.sap.engine.lib.xsl.xpath.xobjects.XObjectFactory;
import com.sap.engine.lib.xsl.xslt.VariableContext;
import com.sap.engine.lib.xsl.xslt.XSLNode;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;

/**
 * This is a structure that represents the context in which the
 * XPath query is evaluated.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @author Vladimir Savtchenko, vladimir.savchenko@sap.com
 * @version July 2001
 */
public final class XPathContext {

  public DTM dtm;
  public int node;
  public int size;
  public int position;
  public VariableContext variableBindings;
  public LibraryManager library;
  public Map namespaceDeclarations;
  public int globalCurrentNode;
  public XObjectFactory xfactMatch = null;
  public XObjectFactory xfactProcess = null;
  //public XObjectFactory xfactDefault = null;
  public XObjectFactory xfactCurrent = null;
  public int factsize = 1000;
  public XSLStylesheet owner = null;
  public long contextId = -1;
  public XSLNode currentNode;

  public XPathContext() {

  }

  public XPathContext(XPathContext c) {
    reuse(c);
  }

  public XPathContext(DTM dtm, int node, int size, int position, VariableContext variableBindings, LibraryManager library, Map namespaceDeclarations, int globalCurrentNode, XSLNode currentNode) {
    //this.xfactDefault = new XObjectFactory(factsize);
    xfactProcess = new XObjectFactory(factsize);
    xfactMatch = xfactProcess;
    contextId = System.currentTimeMillis();
    reuse(dtm, node, size, position, variableBindings, library, namespaceDeclarations, globalCurrentNode, xfactProcess, xfactMatch, xfactProcess, null, contextId, currentNode);
  }

  public XPathContext(DTM dtm, int node, int size, int position, VariableContext variableBindings, LibraryManager library, Map namespaceDeclarations, int globalCurrentNode, XObjectFactory xfactProcess, XObjectFactory xfactMatch, XSLStylesheet owner, XSLNode currentNode) {
//    this.xfactDefault = new XObjectFactory(factsize);
    contextId = System.currentTimeMillis();
    reuse(dtm, node, size, position, variableBindings, library, namespaceDeclarations, globalCurrentNode, xfactProcess, xfactMatch, xfactProcess, owner, contextId, currentNode);
  }

//  public XPathContext(XObjectFactory factory, DTM dtm, int node, int size, int position, VariableContext variableBindings, LibraryManager library, Map namespaceDeclarations, int globalCurrentNode, XObjectFactory xfactProcess, XObjectFactory xfactMatch, XSLStylesheet owner, XSLNode currentNode) {
//    this.xfactDefault = factory;
//    contextId = System.currentTimeMillis();
//    reuse(dtm, node, size, position, variableBindings, library, namespaceDeclarations, globalCurrentNode, xfactProcess, xfactMatch, xfactDefault, owner, contextId, currentNode);
//  }

//  private static Random random = new Random();
  private static long nextContextID = 0; 
  
  private XPathContext reuse(DTM dtm, int node, int size, int position, VariableContext variableBindings, LibraryManager library, Map namespaceDeclarations, int globalCurrentNode, XObjectFactory xfactProcess, XObjectFactory xfactMatch, XObjectFactory xfactCurrent, XSLStylesheet owner, long contextId, XSLNode currentNode) {
    this.dtm = dtm;
    this.node = node;
    this.size = size;
    this.position = position;
    this.variableBindings = variableBindings;
    this.library = library;
    this.namespaceDeclarations = namespaceDeclarations;
    this.globalCurrentNode = globalCurrentNode;
    this.xfactMatch = xfactMatch;
    this.xfactProcess = xfactProcess;
    this.xfactCurrent = xfactCurrent;
    this.owner = owner;
//    this.contextId = contextId;
    synchronized (XPathContext.class) {
      this.contextId = ++nextContextID;
    }
//    synchronized (random) {
//      this.contextId = random.nextLong();
//    }
    //this.contextId = System.currentTimeMillis();
    this.currentNode = currentNode;
    return this;
  }

  public XPathContext reuse(XPathContext c) {
    reuse(c.dtm, c.node, c.size, c.position, c.variableBindings, c.library, c.namespaceDeclarations, c.globalCurrentNode, c.xfactProcess, c.xfactMatch, c.xfactCurrent, c.owner, c.contextId, c.currentNode);
    return this;
  }

  public XObjectFactory getXFactCurrent() {
    return xfactCurrent;
  }

  /**
   * Constructs an XPathContext whose only node is n.
   * Size and position both become 1.
   * All other fields are copied from the first argument.
   */
  public XPathContext(XPathContext c, int n) {
    reuse(c.dtm, n, 1, 1, c.variableBindings, c.library, c.namespaceDeclarations, c.globalCurrentNode, c.xfactProcess, c.xfactMatch, c.xfactCurrent, c.owner, c.contextId, c.currentNode);
  }

  public XPathContext reuse(XPathContext c, int n, int pos) {
    reuse(c.dtm, n, 1, pos, c.variableBindings, c.library, c.namespaceDeclarations, c.globalCurrentNode, c.xfactProcess, c.xfactMatch, c.xfactCurrent, c.owner, c.contextId, c.currentNode);
    return this;
  }

  public XPathContext(XPathContext c, int n, int pos) {
    reuse(c, n, pos);
  }

  public XPathContext(XPathContext c, int n, int pos, int s) {
    reuse(c, n, pos, s);
  }

  public XPathContext reuse(XPathContext c, int n, int pos, int s) {
    reuse(c.dtm, n, s, pos, c.variableBindings, c.library, c.namespaceDeclarations, c.globalCurrentNode, c.xfactProcess, c.xfactMatch, c.xfactCurrent, c.owner, c.contextId, c.currentNode);
    return this;
  }

  public void setGlobalCurrentNode(int value) {
    globalCurrentNode = value;
  }

  public void setXFactProcess() {
    xfactCurrent = xfactProcess;
  }
}

