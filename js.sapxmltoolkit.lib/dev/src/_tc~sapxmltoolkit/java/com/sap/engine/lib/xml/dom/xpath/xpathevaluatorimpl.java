package com.sap.engine.lib.xml.dom.xpath;

import org.w3c.dom.*;
//import org.w3c.dom.xpath.*;
import com.sap.engine.lib.xsl.xpath.XPathProcessor;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.ETBuilder;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.util.DOMToDocHandler;

public class XPathEvaluatorImpl { //implements XPathEvaluator {

  protected XPathProcessor xpp = null;
  protected XPathContext xpc = null;
  protected ETBuilder builder = new ETBuilder();
  protected DTM dtm = new DTM();
  protected DTMFactory dtmfact = new DTMFactory();
  protected DOMToDocHandler dtdh = new DOMToDocHandler();
  protected NamespaceHandler nshandler = null;
  protected NamespaceManager nsmanager = null;

  public XPathExpressionImpl createExpression(String expression, XPathNSResolverImpl resolver) throws XPathException, DOMException {
    return new XPathExpressionImpl(expression, resolver);
  }

  public XPathResultImpl createResult() {
    return new XPathResultImpl();
  }

  public XPathNSResolverImpl createNSResolver(Node nodeResolver) {
    return new XPathNSResolverImpl(nodeResolver);
  }

  public XPathResultImpl evaluate(String expression, Node contextNode, XPathNSResolverImpl resolver, short type, XPathResultImpl result) throws XPathException, DOMException {
    return ((new XPathExpressionImpl(expression, resolver)).evaluate(contextNode, type, result));
  }

}

