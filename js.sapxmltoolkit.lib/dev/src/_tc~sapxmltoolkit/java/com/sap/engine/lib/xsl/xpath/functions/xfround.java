package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNumber;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFRound implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XNumber a0xn = a[0].toXNumber();
    double a0 = a0xn.getValue();

    if ((a0 == Double.POSITIVE_INFINITY) || (a0 == Double.NEGATIVE_INFINITY) || (a0 == Double.NaN) || (a0 == -0.0) || (a0 == 0.0)) {
      return a0xn;
    }

    return context.getXFactCurrent().getXNumber(Math.floor(a0 + 0.5));
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public String getFunctionName() {
    return "round";
  }

}

