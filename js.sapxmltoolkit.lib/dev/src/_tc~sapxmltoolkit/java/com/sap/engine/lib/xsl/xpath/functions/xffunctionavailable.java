package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.QName;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFFunctionAvailable implements XFunction { //xxx

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException { //xxx
    if (a[0] instanceof XJavaObject) {
      XJavaObject xj = a[0].toXJavaObject();
      QName qname = (QName) xj.getObject();
      if (context.library.getFunction(qname) != null) {
        ;
      }
      return context.getXFactCurrent().getXBoolean(context.library.getFunction(qname) != null);
    } else {
      QName name = new QName().reuse(a[0].toXString().getValue());
      return context.getXFactCurrent().getXBoolean(context.library.getFunction(name) != null);
    }
  }

  public String getFunctionName() {
    return "function-available";
  }

}

