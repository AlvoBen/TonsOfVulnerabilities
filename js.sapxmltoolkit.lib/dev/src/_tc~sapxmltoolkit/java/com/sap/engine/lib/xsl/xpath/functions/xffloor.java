package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFFloor implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    return context.getXFactCurrent().getXNumber(Math.floor(a[0].toXNumber().getValue()));
  }

  public String getFunctionName() {
    return "floor";
  }

}

