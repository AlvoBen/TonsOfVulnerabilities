package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xslt.QName;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * Represents a location step, which is a pair of an axis and a node-test.
 * <p>
 * <i>axis</i><b>::</b><i>node-test</i>
 * </p>
 *
 * @see ETItem
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class ETLocationStep implements ETItem {

  public String axis;
  public QName nodeTest;
  protected int axisType;
  protected int nodeTestType;
  protected CharArray pitarget = null;
  public static final int ETLS_ALL = 1; // *
  public static final int ETLS_ALLNS = 6; // *
  public static final int ETLS_NODE = 2; // node()
  public static final int ETLS_COMMENT = 3; // comment()
  public static final int ETLS_TEXT = 4; // text()
  public static final int ETLS_PI = 5; // processing-instruction(??)
  public static final CharArray CH_ALL = new CharArray("*");
  public static final CharArray CH_NODE = new CharArray("node");
  public static final CharArray CH_COMMENT = new CharArray("comment");
  public static final CharArray CH_TEXT = new CharArray("text");
  public static final CharArray CH_PI = new CharArray("processing-instruction");

  public ETLocationStep(String axis, QName nodeTest) {
    this.axis = axis;
    this.nodeTest = nodeTest;
    axisType = AXIS_UNDEFINED;

    if (nodeTest.rawname.equals(NTSTR_STAR)) {
      nodeTestType = NT_ALL;
    } else if (nodeTest.localname.equals(NTSTR_STAR)) {
      nodeTestType = NT_ALLNS;
    } else if (nodeTest.localname.equals(NTSTR_NODE)) {
      nodeTestType = NT_NODE;
    } else if (nodeTest.localname.equals(NTSTR_COMMENT)) {
      nodeTestType = NT_COMMENT;
    } else if (nodeTest.localname.equals(NTSTR_TEXT)) {
      nodeTestType = NT_TEXT;
    } else if (nodeTest.localname.startsWith(CH_PI)) {
      nodeTestType = NT_PI;
      CharArray pt = nodeTest.localname;

      //      LogWriter.getSystemLogWriter().println("ETLS: localname=" + nodeTest.localname);
      for (int i = 0; i < pt.length(); i++) {
        //          LogWriter.getSystemLogWriter().println("ETLS: pi[i] = " + pt.charAt(i));
        if (pt.charAt(i) == '\'' || pt.charAt(i) == '\"') {
          //          LogWriter.getSystemLogWriter().println("ETLS: pi[i] = yooooooooo");
          pitarget = new CharArray();

          for (++i; pt.charAt(i) != '\'' && pt.charAt(i) != '\"' && i < pt.length(); i++) {
            pitarget.append(pt.charAt(i));
          } 

          //          LogWriter.getSystemLogWriter().println("ETLS: pitarget=" + pitarget);
        }
      } 
    } else {
      nodeTestType = NT_UNDEFINED;
    }

    if (axis.equals(XNodeSet.AXISSTR_CHILD)) {
      axisType = AXIS_CHILD;
    } else if (axis.equals(AXISSTR_ATTRIBUTE)) {
      axisType = AXIS_ATTRIBUTE;
    } else if (axis.equals(AXISSTR_DOS)) {
      axisType = AXIS_DOS;
    }

    //    LogWriter.getSystemLogWriter().println("ETLS: <iniT>:" + nodeTest.uri + "," + nodeTest.localname + ", " + nodeTest.prefix);
    //    print(1);
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
    LogWriter.getSystemLogWriter().println("ETLocationStep(axis='" + axis + "', nodeTest='" + nodeTest + "')");
  }

  public XObject evaluate(XPathContext context) throws XPathException {
    //    LogWriter.getSystemLogWriter().println("ETLS.eval:"); print(1);
    XNodeSet xn = context.getXFactCurrent().getXNodeSet().reuse(context, axis, nodeTest);
    //    LogWriter.getSystemLogWriter().println("ETLS.eval:OK"); 
    //xn.checkOrder();
    return xn;
  }

  public QName getNodeTest() {
    return nodeTest;
  }

  public boolean match(XPathContext c) throws XPathException {
    //    if (c.node < 0) return false;
    if (axisType == AXIS_CHILD && c.dtm.nodeType[c.node] != DTM.ATTRIBUTE_NODE) {
      switch (nodeTestType) {
        case NT_ALL: {
          return c.dtm.nodeType[c.node] == DTM.ELEMENT_NODE;
        }
        case NT_NODE: {
          return c.node != 0;
        }
        case NT_TEXT: {
          return c.dtm.nodeType[c.node] == DTM.TEXT_NODE;
        }
        case NT_PI: {
          //          LogWriter.getSystemLogWriter().println("ETLS: pitarget=" + pitarget + ", localname=" + c.dtm.name[c.node].rawname);
          return c.dtm.nodeType[c.node] == DTM.PROCESSING_INSTRUCTION_NODE && (pitarget == null || (pitarget != null && pitarget.equals(c.dtm.name[c.node].localname)));
        }
        //return c.dtm.nodeType[c.node] == DTM.PROCESSING_INSTRUCTION_NODE;
        case NT_COMMENT: {
          return c.dtm.nodeType[c.node] == DTM.COMMENT_NODE;
        }
      }

      try {
        if (c.dtm.name[c.node] != null && nodeTest.localname.equals(CH_ALL) && c.dtm.name[c.node].uri.equals(nodeTest.uri)) {
          return true;
        }

        return (c.dtm.name[c.node] != null && c.dtm.name[c.node].uri.equals(nodeTest.uri) && c.dtm.name[c.node].localname.equals(nodeTest.localname)) ? true : false;
      } catch (NullPointerException e) {
        throw e;
      }
    } else if (axisType == AXIS_ATTRIBUTE && c.dtm.nodeType[c.node] == DTM.ATTRIBUTE_NODE) {
      if (nodeTestType == NT_ALL) {
        return true;
      }

      return (c.dtm.name[c.node] != null && c.dtm.name[c.node].uri.equals(nodeTest.uri) && c.dtm.name[c.node].localname.equals(nodeTest.localname)) ? true : false;
    } else {
      return false;
    }
  }

  public boolean equals(Object o) {
    if (! (o instanceof ETLocationStep)) {
      return false;
    }
    
    ETLocationStep e = (ETLocationStep) o;
    return e.axisType == axisType && e.nodeTestType == nodeTestType && e.nodeTest.equals(nodeTest);
  }

  public int hashCode(){
    throw new UnsupportedOperationException("Not implemented for usage in hash-based collections");
  }
}

