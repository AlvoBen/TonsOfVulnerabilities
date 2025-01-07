package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.QName;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFLocalName implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length <= 1);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    if (a.length == 0) {
      return getLocalName(context.node, context);
    } else {
      XNodeSet a0 = a[0].toXNodeSet();  //has to throw an exception if a[0] is not a NodeSet or a apropriate XJavaObject

      if (a0.isEmpty()) {
        return context.getXFactCurrent().getXStringEmpty();
      } else {
        return getLocalName(a0.firstInDocumentOrder(), context);
      }
    }
  }

  private static XObject getLocalName(int node, XPathContext context) {
    QName qname = context.dtm.name[node];
    return qname == null 
      ? context.getXFactCurrent().getXStringEmpty()
      : context.getXFactCurrent().getXString(qname.localname);
  }

  public String getFunctionName() {
    return "local-name";
  }

}

