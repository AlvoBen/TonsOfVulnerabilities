package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFTranslate implements XFunction {

  protected CharArray res = new CharArray(20);

  public synchronized XObject execute(XObject[] a, XPathContext context) throws XPathException {
    res.clear();
    XString a0xs = a[0].toXString();
    XString a1xs = a[1].toXString();
    XString a2xs = a[2].toXString();
    CharArray s0c = a0xs.getValue();
    CharArray s1 = a1xs.getValue();
    CharArray s2c = a2xs.getValue();
    char s0[] = s0c.getData();
    int s0off = s0c.getOffset();
    char s2[] = s2c.getData();
    int s2off = s2c.getOffset();
    int len0 = s0c.getSize();
    int len2 = s2c.getSize();

    for (int i = 0; i < len0; i++) {
      char ch = s0[s0off + i];
      int x = s1.indexOf(ch);

      if (x == -1) {
        res.append(ch);
      } else {
        if (x < len2) {
          res.append(s2[s2off + x]);
        }
      }
    } 

    try {
      return context.getXFactCurrent().getXString(res);
    } finally {
      if (a0xs != a[0]) {
        a0xs.close();
      }
      if (a1xs != a[1]) {
        a1xs.close();
      }
      if (a2xs != a[2]) {
        a1xs.close();
      }
    }
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 3);
  }

  public String getFunctionName() {
    return "translate";
  }

}

