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
package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * The purpose of this class is to match a XPath expression
 * against a XML document. You can change the context by changing
 * the context node, position or size
 *
 * @author Vladimir Savchenko, vladimir.savchenko@sap.com
 * @version 1.0
 * @deprecated Please use JDK5 XPath api instead "javax.xml.xpath" package.
 */
@Deprecated
public class XPathMatcher {

  protected XPathProcessor xpp = null;
  protected XPathContext xpc = null;
  protected ETBuilder builder = new ETBuilder();
  protected DTM dtm = new DTM();
  protected DTMFactory dtmfact = new DTMFactory();
  protected NamespaceHandler nshandler = null;
  protected NamespaceManager nsmanager = null;

  /**
   * Construct a new XPathMatcher instance.
   *
   * @param   file  The XML file, which should be matched
   * @exception   FileNotFoundException  thrown if the file could not be found
   * @exception   XPathException  thrown if there was some error with the query
   */
  public XPathMatcher(String file) throws FileNotFoundException, XPathException {
    init(file);
  }

  public XPathMatcher(InputStream in) throws FileNotFoundException, XPathException {
    init(in);
  }

  /**
   * Initializes an existing XPathMatcher
   *
   * @param   file  The XML file, which should be matched
   * @exception   FileNotFoundException  thrown if the file could not be found
   * @exception   XPathException  thrown if there was some error with the query
   */
  public void init(String file) throws FileNotFoundException, XPathException {
    nshandler = new NamespaceHandler(null);
    nsmanager = new NamespaceManager();
    builder.setNamespaceStuff(nsmanager, nshandler);
    dtmfact.initialize(dtm, new NamespaceManager(), file);
    xpp = new XPathProcessor(dtm);
    xpc = dtm.getInitialContext();
  }

  public void init(InputStream in) throws XPathException {
    nshandler = new NamespaceHandler(null);
    nsmanager = new NamespaceManager();
    builder.setNamespaceStuff(nsmanager, nshandler);
    dtmfact.initialize(dtm, new NamespaceManager(), in);
    xpp = new XPathProcessor(dtm);
    xpc = dtm.getInitialContext();
  }

  /**
   * Processes a query and returns a XObject, which could be XNodeSet, XString, XBoolean, XNumber
   * See the documentations of these classes for more information
   *
   * @param   query  the query which should be matched
   * @return     the object resulting form matching this query
   * @exception   XPathException  thrown if there was some error with the query
   */
  public XObject process(String query) throws XPathException {
    return xpp.process(builder.process(query), xpc);
  }

  /**
   * Matches the specified query against the current context and returns a boolean result
   *
   * @param   query  the query which should be matched
   * @return     true or false
   * @exception   XPathException  thrown if there was some error with the query
   */
  public boolean match(String query) throws XPathException {
    return process(query).toXBoolean().getValue();
  }

  public void printET(String query) throws Exception {
    builder.process(query).print();
  }

  /**
   * You can use this method to set the current context. For example "/" sets the context to the root node
   * "/table" - sets the context to the first table node, "/table[name = 'Bob']" - sets the context to the
   * first table node which has a 'name' child with the value 'Bob'. In this way you can set the current
   * context to any node and then match a query using this context
   *
   * @param   query  the query which sets the context
   * @exception   XPathException  thrown if there was some error with the query
   */
  public void setContext(String query) throws XPathException {
    XNodeSet xns = (XNodeSet) xpp.process(builder.process(query), xpc);
    xpc.node = xns.firstInDocumentOrder();
  }

  /**
   * Set the context to a specified node of the DTM. See DTM documentation for more info
   *
   * @param   node  the new node
   */
  public void setContextNode(int node) {
    xpc.node = node;
  }

  /**
   * Set the size of the context
   *
   * @param   size  the new size
   */
  public void setContextSize(int size) {
    xpc.size = size;
  }

  /**
   * Sets the positoin of this node in the context. If the context size is >1 then this this is
   * used to identify the node
   *
   * @param   pos
   */
  public void setContextPosition(int pos) {
    xpc.position = pos;
  }

  public void addPrefixMapping(String prefix, String uri) {
    nshandler.add(new CharArray(prefix), new CharArray(uri));
  }

  public static void main(String args[]) throws Exception {
    //XPathMatcher m = new XPathMatcher("d:/develop/xml2000/tests/xsltmark2/testcases/db100.xml");
    XPathMatcher m = new XPathMatcher("c:/develop/xml2000/bugs/2001.05.04c/input.xml");
    //    LogWriter.getSystemLogWriter().println("" + m.match("//row[2]/firstname = \'Bob\'"));
    //    LogWriter.getSystemLogWriter().println("" + m.process("//row[2]/firstname"));
    //    LogWriter.getSystemLogWriter().println("" + m.process("concat(\'a\', \'b\')"));
    m.addPrefixMapping("cpx", "urn:sap-com:ifr:cp");
    LogWriter.getSystemLogWriter().println("" + m.process("/cpx:storedData"));
  }

}

