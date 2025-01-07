package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFConcat implements XFunction {

  private CharArray b = new CharArray(100);

  public synchronized XObject execute(XObject[] a, XPathContext context) throws XPathException {
    b.clear();

    for (int i = 0; i < a.length; i++) {
      //      LogWriter.getSystemLogWriter().println("XFConcat a["+i + "] = " + a[i].toXString());
      b.append(a[i].toXString().getValue());
    } 

    return context.getXFactCurrent().getXString(b);
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length >= 2);
  }

  public String getFunctionName() {
    return "concat";
  }

}

