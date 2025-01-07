package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFOperatorStroke implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XNodeSet a0 = (XNodeSet) a[0];
    XNodeSet a1 = (XNodeSet) a[1];
    XNodeSet r = context.getXFactCurrent().getXNodeSet(a0);
    /*
     for (IntSetIterator i = a1.iterator(); i.hasNext(); ) {
     r.add(i.next());
     }
     */
    r.uniteWith(a1);
    return r;
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 2) && (a[0].getType() == XNodeSet.TYPE) && (a[1].getType() == XNodeSet.TYPE));
  }

  public int match(Object[] a, XPathContext c) {
    return ETItem.RES_UNDEFINED;
  }

  public String getFunctionName() {
    return "|";
  }

}

