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
public final class XFOperatorMinus implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XNumber a0xn = a[0].toXNumber();
    XNumber a1xn = null;
    try {
      double a0 = a0xn.getValue();

      if (a.length == 2) {
        a1xn = a[1].toXNumber();
        double a1 = a1xn.getValue();
        return context.getXFactCurrent().getXNumber(a0 - a1);
      }

      return context.getXFactCurrent().getXNumber(-a0);
    } finally {
      if (a0xn != a[0]) {
        a0xn.close();
      }
      if (a1xn != null && a1xn != a[1]) {
        a1xn.close();
      }
    }
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 2) || (a.length == 1));
  }

  public String getFunctionName() {
    return "-";
  }

}

