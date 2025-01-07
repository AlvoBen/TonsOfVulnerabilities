package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFSubstringBefore implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 2);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XString a0xs = a[0].toXString();
    XString a1xs = a[1].toXString();
    CharArray s0 = a0xs.getValue();
    CharArray s1 = a1xs.getValue();
    //    LogWriter.getSystemLogWriter().println("XFSubstringBefore: s0=" + s0 + ", s1=" + s1 + ".");
    int p = s0.indexOf(s1);
    //    LogWriter.getSystemLogWriter().println("XFSubstringBefore: p=" + p);
    //int p = StringSearching.instance.search(s0, s1);
    try {
      if (p > 0) { // if p == 0, we have to return "" again
        return context.getXFactCurrent().getXString(s0, 0, p);
      } else {
        return context.getXFactCurrent().getXStringEmpty();
      }
    } finally {
      if (a0xs != a[0]) {
        a0xs.close();
      }
      if (a1xs != a[1]) {
        a1xs.close();
      }
    }
  }

  public String getFunctionName() {
    return "substring-before";
  }

}

