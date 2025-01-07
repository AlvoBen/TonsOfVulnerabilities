package com.sap.engine.lib.xsl.xslt;

import org.w3c.dom.Element;

import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XBoolean;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 */
public final class XSLIf extends XSLContentNode {

  private ETObject test = null;

  public XSLIf(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLIf(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    test = owner.etBuilder.process(el.getAttribute("test"));
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    XObject xo = owner.getXPathProcessor().process(test, xcont, varContext);
    XBoolean xb = xo.toXBoolean();

    if (xb.getValue()) {
      processFromFirst(xcont, node);
//      getFirst().process(xcont, node);
    }

    if (xb != xo) {
      xb.close();
    }
    xo.close();

//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"test"};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

