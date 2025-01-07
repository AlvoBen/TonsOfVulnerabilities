package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFStringLength implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length <= 1);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    if (a.length == 0) {
      return context.getXFactCurrent().getXNumber(context.dtm.getStringValue(context.node).length());
    } else {
      XString a0xs = a[0].toXString();
      int len = a0xs.getValue().length();
      if (a0xs != a[0]) {
        a0xs.close();
      }
      return context.getXFactCurrent().getXNumber(len);
    }
  }

  public String getFunctionName() {
    return "string-length";
  }

}

