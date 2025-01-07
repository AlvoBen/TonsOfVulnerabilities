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
public final class XFName implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 0) || ((a.length == 1) && (a[0].getType() == XNodeSet.TYPE)));
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    if (a.length == 0) {
      if ((context.dtm.name[context.node] == null) || (context.dtm.name[context.node].rawname.length() == 0)) {
        return context.getXFactCurrent().getXStringEmpty();
      } else {
        return context.getXFactCurrent().getXString(context.dtm.name[context.node].rawname);
      }
    } else {
      XNodeSet a0 = a[0].toXNodeSet(); //has to throw an exception if a[0] is not a NodeSet or a apropriate XJavaObject

      if (a0.isEmpty()) {
        return context.getXFactCurrent().getXStringEmpty();
      } else {
        int n = a0.firstInDocumentOrder();

        if (context.dtm.name[n] == null || context.dtm.name[n].rawname.length() == 0) {
          return context.getXFactCurrent().getXStringEmpty();
        } else {
          return context.getXFactCurrent().getXString(context.dtm.name[n].rawname);
        }
      }
    }
  }

  public String getFunctionName() {
    return "name";
  }

}

