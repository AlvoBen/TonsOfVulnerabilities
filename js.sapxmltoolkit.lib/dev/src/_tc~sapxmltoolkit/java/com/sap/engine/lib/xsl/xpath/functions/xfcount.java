package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFCount implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    if (a[0].getType() == XNodeSet.TYPE) {
      return context.getXFactCurrent().getXNumber(((XNodeSet) a[0]).count());
    } else {   // a[0] is a XJavaObject
      return context.getXFactCurrent().getXNumber(a[0].toXNodeSet().count());
    }
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    //return ((a.length == 1) && (a[0].getType() == XNodeSet.TYPE));
    return ((a.length == 1) && (a[0].getType() == XNodeSet.TYPE || a[0].getType() == XJavaObject.TYPE));
  }

  public String getFunctionName() {
    return "count";
  }

}

