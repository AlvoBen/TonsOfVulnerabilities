package com.sap.engine.lib.xsl.xpath;

import java.util.Hashtable;

import org.w3c.dom.Document;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.VariableContext;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;

/**
 * The basic class that accepts queries and returns resulting XPath objects.
 * Does caching.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @author Vladimir Savtchenko, vladimir.savchenko@sap.com
 * @version June 2001
 */
public 
/*final*/
class XPathProcessor {

  protected CharArray key = new CharArray();
  public DTM dtm;
//  private ETBuilder builder = new ETBuilder();

  public XPathProcessor() {

  }

  public XPathProcessor(DTM dtm) {
    reuse(dtm);
  }

  public void reuse(DTM dtm) {
    this.dtm = dtm;
  }

  public XObject process(ETObject query, XPathContext context) throws XPathException {
    context.globalCurrentNode = context.node;
    context.setXFactProcess();
    XObject x = innerProcess(query, context);
    return x;
  }

  public XObject process(ETObject query, XPathContext context, VariableContext vc) throws XPathException {
    context.variableBindings = vc;
    vc.useXPathContext(context);
    //LogWriter.getSystemLogWriter().println("XpathProcessor.process squery=" + query.squery);
    return process(query, context);
  }

  private XObject innerProcess(ETObject q, XPathContext c) throws XPathException {
    try {
      return q.et.evaluate(c);
    } catch (NullPointerException e) {
      throw new XPathException("Error parsing query", e);
    }
  }

  public XObject process(ETObject query) throws XPathException {
    return process(query, dtm.getInitialContext());
  }

  public void init(Document doc) throws XPathException {
    throw new XPathException("Initializing the XPath processor through DOM is not supported.");
  }

  public DTM getDTM() {
    return dtm;
  }

  protected CharArray makeKey(XPathContext x, CharArray query, String mode) {
    key.reuse();
    key.set(query);
    key.append('[');
    key.append('[');
    key.appendInteger(x.node);
    key.append('[');
    key.append('[');
    key.append(mode);
    return key;
  }


  public boolean match(ETObject eo, XPathContext context0, String mode) throws XPathException {
    return match(eo, context0, mode);
  }
  
  private int isSubset(ETObject root, ETObject child) throws XPathException {
    if (root == null) {
      return -1;
    }

    if (root.et instanceof ETSlash) {
      ETSlash et1 = (ETSlash)root.et;
      if (et1.left instanceof ETSlash) {
        ETSlash et2 = (ETSlash)et1.left;
        if (et2.isUnary == true && et2.right instanceof ETLocationStep && ((ETLocationStep)et2.right).axisType == ETItem.AXIS_DOS && child.et instanceof ETPredicate) {
          if (et1.right.equals(child.et)) {
            return 1;
          } else {
            return 0;
            
          }
        }
      }
    }
    
    return -1;
  }
  public boolean match(ETObject eo, XPathContext context0, String mode, ETObject select) throws XPathException {
    if (eo.squery.equals("/")) {
      return (context0.node == 0);
    } else if (eo.squery.equals("*|/")) {
      return (context0.node == 0) || (context0.dtm.nodeType[context0.node] == DTM.ELEMENT_NODE);
    }

    switch (isSubset(select, eo)) {
      case 1: return true;
      case 0: return false;
      case -1: break;
      default: break;
    
    }

    return eo.et.match(context0);
  }

  public void initializeProcess() {

  }

  public void finalizeLocal() {

  }

}

