package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.xobjects.*;
import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xml.util.*;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 25-Mar-02, 09:30:49
 */
public final class XFId implements XFunction {

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XNodeSet xns = null;
    if (a[0].getType() == XJavaObject.TYPE) {
      if (((XJavaObject) a[0]).isNodeOrNodeList()) {
        xns = a[0].toXNodeSet();
      }
    }
    if (a[0].getType() == XNodeSet.TYPE) {
      xns = (XNodeSet) a[0];
    }
    
    if (xns != null) {
      XNodeSet result = context.getXFactCurrent().getXNodeSet();
      result.setForward(true);
      result.clear();

      for (IntArrayIterator i = xns.iterator(); i.hasNext();) {
        int x = i.next();
        String s = context.dtm.getStringValue(x).toString();
        result.uniteWith(execute0(s, context));
      } 

      result.sort();
      return result;
    }

    String s = a[0].toXString().toString();
    return execute0(s, context);
  }

  public XNodeSet execute0(String s, XPathContext context) throws XPathException {
    XNodeSet result = context.getXFactCurrent().getXNodeSet();
    result.clear();
    result.setForward(true);
    String[] ids = ListSplitter.split(s);

    for (int i = 0; i < ids.length; i++) {
      int y = context.dtm.getElementById(ids[i]);

      if (y != -1) {
        result.add(y);
      }
    } 

    result.sort();
    return result;
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public String getFunctionName() {
    return "id";
  }

}

