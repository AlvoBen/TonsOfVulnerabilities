package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFSubstring implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 2) || (a.length == 3));
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    try {
      XString a0xs = a[0].toXString();
      XNumber a1xn = a[1].toXNumber();
      CharArray s = a0xs.getValue();
      int pStart = (int) a1xn.getValue();
      pStart--;

      if (pStart < 0) {
        pStart = 0;
      }

      if (a.length == 3) {
        XNumber a2xn = a[2].toXNumber();
        int sLength = (int) a2xn.getValue();
        //LogWriter.getSystemLogWriter().println(s + " start=" + pStart + " length=" + sLength);
        XString ret = context.getXFactCurrent().getXString(s, pStart, pStart + sLength);
        if (a0xs != a[0]) {
          a0xs.close();
        }
        if (a1xn != a[1]) {
          a1xn.close();
        }
        if (a2xn != a[2]) {
          a2xn.close();
        }
        return ret;
      } else {
        XString ret = context.getXFactCurrent().getXString(s, pStart, s.getSize());
        if (a0xs != a[0]) {
          a0xs.close();
        }
        if (a1xn != a[1]) {
          a1xn.close();
        }
        return ret;
      }
    } catch (IndexOutOfBoundsException e) {
      //$JL-EXC$
      //performance
      return context.getXFactCurrent().getXStringEmpty();
    }
  }

  public String getFunctionName() {
    return "substring";
  }

}

