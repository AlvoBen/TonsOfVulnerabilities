package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFLang implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    CharArray s = a[0].toXString().getValue();
    int x = context.node;
    DTM dtm = context.dtm;

    while (x != DTM.NONE) {
      int a0 = dtm.getAttributesStartIndex(x);
      int a1 = dtm.getAttributesEndIndex(x);

      for (int i = a0; i <= a1; i++) {
        if (dtm.name[i].rawname.equals("lang")) {
          return context.getXFactCurrent().getXBoolean(dtm.getStringValue(i).equalsIgnoreCase(s));
        }
      } 

      x = dtm.parent[x];
    }

    return context.getXFactCurrent().getXBoolean(false);
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public String getFunctionName() {
    return "lang";
  }

}

