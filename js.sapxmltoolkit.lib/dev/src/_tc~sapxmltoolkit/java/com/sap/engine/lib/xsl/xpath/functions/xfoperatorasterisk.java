package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFOperatorAsterisk implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    double a0 = (a[0].toXNumber()).getValue();
    double a1 = (a[1].toXNumber()).getValue();
    return context.getXFactCurrent().getXNumber(a0 * a1);
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 2);
  }

  public String getFunctionName() {
    return "*";
  }

}

