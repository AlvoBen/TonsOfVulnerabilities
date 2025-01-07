package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFGenerateId implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 0) || ((a.length == 1) && (a[0].getType() == XNodeSet.TYPE)));
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    int x = context.node;

    if (a.length == 1) {
      XNodeSet xns = (XNodeSet) a[0];

      if (xns.isEmpty()) {
        return context.getXFactCurrent().getXStringEmpty();
      }

      x = xns.firstInDocumentOrder();
    }

    return context.getXFactCurrent().getXString("dtmId" + x);
  }

  public String getFunctionName() {
    return "generate-id";
  }

}

