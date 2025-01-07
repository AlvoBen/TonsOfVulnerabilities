package com.sap.engine.lib.xsl.xpath.functions;

import java.text.DecimalFormat;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFFormatNumber implements XFunction {

  //DecimalFormat f = new DecimalFormat();

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 2) || (a.length == 3));
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException { //xxx
    double d = a[0].toXNumber().getValue();
    String s = a[1].toXString().getValue().getString();
    DecimalFormat f = null;

    if (a.length == 3) {
      f = context.owner.getDecimalFormat(a[2].toXString().getValue().getString());
      f.applyPattern(s);

      //      LogWriter.getSystemLogWriter().println("Requested format from stylesheet: " + f);
      if (f == null) {
        throw new XPathException("DecimalFormat: '" + a[2].toXString().getValue().getString() + "' not found");
      }
    } else {
      f = new DecimalFormat(s);
    }

    //    LogWriter.getSystemLogWriter().println("Formatting number: " + d + " = " + f.format(d));
    return context.getXFactCurrent().getXString(f.format(d));
  }

  public String getFunctionName() {
    return "format-number";
  }

}

