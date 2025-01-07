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
public final class XFOperatorMod implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
      XNumber xa0 = a[0].toXNumber();
      XNumber xa1 = a[1].toXNumber();
    try {
      //    double a0 = a[0].toXNumber().getValue();
      //    double a1 = a[1].toXNumber().getValue();
      double a0 = xa0.getValue();
      double a1 = xa1.getValue();
      //context.getXFactCurrent().releaseXObject(xa0);
      //context.getXFactCurrent().releaseXObject(xa1);
    
      int sgn_a0 = (a0 < 0) ? (-1) : 1;
      a0 = Math.abs(a0);
      a1 = Math.abs(a1);
      return context.getXFactCurrent().getXNumber((a0 - (Math.floor(a0 / a1) * a1)) * sgn_a0);
    } finally {
      if (xa0 != a[0]) {
        xa0.close();
      } 
      if (xa1 != a[1]) {
        xa1.close();
      }
    }
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 2);
  }

  public String getFunctionName() {
    return "mod";
  }

}

