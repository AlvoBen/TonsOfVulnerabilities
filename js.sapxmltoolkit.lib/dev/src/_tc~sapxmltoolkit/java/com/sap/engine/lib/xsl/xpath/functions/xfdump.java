package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.IntArrayIterator;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFDump implements XFunction {

  private StringBuffer buffer = new StringBuffer(100);
  private int[] temp = null;
  private int nTemp;
  private DTM dtm;

  public static String toString(DTM dtm, XObject xo) throws XPathException {
    return new XFDump().execute(new XObject[] {xo}, dtm.getInitialContext()).toString();
  }

  public synchronized XObject execute(XObject[] a, XPathContext context) throws XPathException {
    int t = a[0].getType();

    if (t != XNodeSet.TYPE) {
      XObject r = a[0].toXString();
      //LogWriter.getSystemLogWriter().println("DUMP IN STYLESHEET: " + r);
      return r;
    }

    dtm = context.dtm;
    XNodeSet xns = (XNodeSet) a[0];
    DTM dtm = xns.dtm;

    if (temp == null) {
      temp = new int[100];
    }

    buffer.setLength(0);
    buffer.append("{\n");

    for (IntArrayIterator i = xns.sensitiveIterator(); i.hasNext();) {
      int x = i.next();
      buffer.append("  ");

      if (x != 0) {
        pathTo(x);
      } else {
        buffer.append("/");
      }

      buffer.append('\n');
    } 

    buffer.append("}");
    //LogWriter.getSystemLogWriter().println("DUMP IN STYLESHEET: " + buffer.toString());
    return context.getXFactCurrent().getXString(buffer.toString());
  }

  private void pathTo(int x) {
    if (x == -1) {
      return;
    }

    if (x == 0) {
      return;
    }

    int t = dtm.nodeType[x];
    int p = dtm.parent[x];
    String name;
    try {
      name = dtm.name[x].rawname.getString();
    } catch (NullPointerException _) {
      //$JL-EXC$
      name = "";
    }

    if (t == DTM.ATTRIBUTE_NODE) {
      pathTo(p);
      buffer.append("/@").append(name);
    } else if (t == DTM.ELEMENT_NODE) {
      pathTo(p);
      buffer.append("/").append(name);
    } else if (t == DTM.COMMENT_NODE) {
      pathTo(p);
      buffer.append("/comment()");
    } else if (t == DTM.PROCESSING_INSTRUCTION_NODE) {
      pathTo(p);
      buffer.append("/processing-instruction('").append(name).append("')");
    } else if (t == DTM.TEXT_NODE) {
      pathTo(p);
      buffer.append("/text()");
    } else {
      pathTo(p);
      buffer.append("/?");
    }

    int countSiblings = 0;
    int countPrecedingSiblings = 0;

    for (int y = dtm.previousSibling[x]; y != -1; y = dtm.previousSibling[y]) {
      try {
        if (name.equals(dtm.name[y].rawname.getString())) {
          countSiblings++;
          countPrecedingSiblings++;
        }
      } catch (NullPointerException e) {
        //$JL-EXC$
       }
    } 

    for (int y = dtm.nextSibling[x]; y != -1; y = dtm.nextSibling[y]) {
      try {
        if (name.equals(dtm.name[y].rawname.getString())) {
          countSiblings++;
        }
      } catch (NullPointerException e) {
        //$JL-EXC$

      }
    } 

    if (countSiblings != 0) {
      buffer.append('[').append(countPrecedingSiblings + 1).append(']');
    }
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public String getFunctionName() {
    return "dump";
  }

}

