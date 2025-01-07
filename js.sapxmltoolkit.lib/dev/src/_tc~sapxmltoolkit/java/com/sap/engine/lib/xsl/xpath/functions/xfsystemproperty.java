package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFSystemProperty implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    String s = a[0].toXString().getValue().getString();

    if (s.equals("xsl:version")) {
      return context.getXFactCurrent().getXNumber(1.1);
    } else if (s.equals("xsl:vendor")) {
      return context.getXFactCurrent().getXString("InQMy Labs.");
    } else if (s.equals("xsl:vendor-url")) {
      return context.getXFactCurrent().getXString("http://www.sap.com");
    } else {
      String r = SystemProperties.getProperty(s);
      return context.getXFactCurrent().getXString((r == null) ? "" : r);
    }
  }

  public String getFunctionName() {
    return "system-property";
  }

}

