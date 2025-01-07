package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * Replaces every sequence of whitespace characters with a single space
 * and trims whitespace from both sides.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFNormalizeSpace implements XFunction {

  protected CharArray s = new CharArray(100);
  protected CharArray res = new CharArray(100);

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 0) || (a.length == 1));
  }

  public synchronized XObject execute(XObject[] a, XPathContext context) throws XPathException {
    s.clear();
    res.clear();

    if (a.length == 0) {
      s.set(context.dtm.getStringValue(context.node));
    } else {
      s.set(a[0].toXString().getValue());
    }

    if (s.getSize() == 0) {
      return context.getXFactCurrent().getXStringEmpty();
    }

    //    LogWriter.getSystemLogWriter().println("XFNormalizeSpace: s=[" + s + "]");
    char[] ca = s.getData();
    int calen = s.getSize();
    int caoff = s.getOffset();
    int i;
    for (i = 0; i < calen && Symbols.isWhitespace(ca[caoff + i]); i++) {
      ; 
    }

    // removed after a mailfrom Andreas Busalla - apache does it the same way ?!
    //    if ((i > 0) && (i == calen)) {
    //      res.append(' ');
    //    }
    if (i == calen) {
      return context.getXFactCurrent().getXString(res);
    }

    res.append(ca[caoff + i]);
    i++;

    for (; i < calen; i++) {
      if (Symbols.isWhitespace(ca[caoff + i])) {
        while (i < calen && Symbols.isWhitespace(ca[caoff + i])) {
          i++;
        }

        if (i < calen) {
          res.append(' ');
        }

        if (i == calen) {
          break;
        }
        i--;
      } else {
        res.append(ca[caoff + i]);
      }
    } 

    return context.getXFactCurrent().getXString(res);
  }

  public String getFunctionName() {
    return "normalize-space";
  }

}

