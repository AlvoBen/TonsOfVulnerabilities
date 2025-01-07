package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFF implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    LogWriter.getSystemLogWriter().print("{Function f called with the following arguments:");

    for (int i = 0; i < a.length; i++) {
      LogWriter.getSystemLogWriter().println(" " + a[i]);
    } 

    return (a.length > 0) ? a[0] : (context.getXFactCurrent().getXString("nothing"));
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return true;
  }

  public String getFunctionName() {
    return "f";
  }

}

