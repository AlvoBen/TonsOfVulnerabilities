package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFContains implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    return context.getXFactCurrent().getXBoolean(a[0].toXString().getValue().indexOf(a[1].toXString().getValue()) != -1);
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 2);
  }

  public String getFunctionName() {
    return "contains";
  }

}

