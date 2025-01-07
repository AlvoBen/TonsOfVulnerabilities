package com.sap.engine.lib.xsl.xpath.functions;

import java.util.Vector;

import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.XSLException;
import com.sap.engine.lib.xsl.xslt.XSLFunction;

public final class StylesheetFunction implements XFunction {

  private XSLFunction xslFunction = null;
//  private XPathContext currentContext = null;
//  private DTM dtm;

  public StylesheetFunction(XSLFunction xslFunction) throws XPathException {
    this.xslFunction = xslFunction;
    init();
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return true;
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    try {
      if (confirmArgumentTypes(a)) {
//        currentContext = context;
        return xslFunction.evaluate(context, createVector(a));
      } else {
        throw new XPathException("confirmArgumentTypes failed!!");
      }
    } catch (XSLException e) {
      throw new XPathException("XSL exception ocurred", e);
    }
  }

  public String getFunctionName() {
    return xslFunction.getName();
  }

  private void init() {

  }

  private static Vector createVector(XObject[] a) {
    Vector res = new Vector();

    for (int i = 0; i < a.length; i++) {
      res.add(a[i]);
    } 

    return res;
  }

}

