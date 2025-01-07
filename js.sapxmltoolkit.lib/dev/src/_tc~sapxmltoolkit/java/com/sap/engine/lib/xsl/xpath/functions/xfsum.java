package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.xobjects.IntArrayIterator;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFSum implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) {
    XNodeSet a0 = (XNodeSet) a[0];
    double r = 0.0;

    for (IntArrayIterator i = a0.iterator(); i.hasNext();) {
      r += context.getXFactCurrent().getXNumber(context.getXFactCurrent().getXString(context.dtm.getStringValue(i.next()))).getValue();
    } 

    return context.getXFactCurrent().getXNumber(r);
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 1) && (a[0].getType() == XNodeSet.TYPE));
  }

  public String getFunctionName() {
    return "sum";
  }

}

