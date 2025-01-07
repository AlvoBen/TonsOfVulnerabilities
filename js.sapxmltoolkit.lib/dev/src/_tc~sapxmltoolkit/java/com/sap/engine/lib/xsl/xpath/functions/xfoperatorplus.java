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
public final class XFOperatorPlus implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XNumber a0xn = a[0].toXNumber();
    XNumber a1xn = a[1].toXNumber();
    double a0 = a0xn.getValue();
    double a1 = a1xn.getValue();
    try {
      return context.getXFactCurrent().getXNumber(a0 + a1);
    } finally {
      if (a0xn != a[0]) {
        a0xn.close();
      }
      if (a1xn != a[1]) {
        a1xn.close();
      }
    }
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 2);
  }

  public String getFunctionName() {
    return "+";
  }

}

