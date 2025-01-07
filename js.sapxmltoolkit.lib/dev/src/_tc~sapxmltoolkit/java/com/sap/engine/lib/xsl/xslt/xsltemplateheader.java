package com.sap.engine.lib.xsl.xslt;

import com.sap.engine.lib.xsl.xpath.ETLocationStep;
import com.sap.engine.lib.xsl.xpath.ETObject;

public class XSLTemplateHeader implements Comparable {

  public String sMode;
  public String sName;
  public boolean bIsDefault;
  public XSLTemplate template;
  public double priority;
  public int importPrecedence;
  public ETObject match;
  public boolean bMatchable;
  public int position = -1;

  public XSLTemplateHeader(ETObject etobj, XSLTemplate templ, int pos) {
    template = templ;
    match = etobj;
    bIsDefault = templ.def;
    sMode = templ.mode;

    if (sMode == null) {
      sMode = "";
    }

    importPrecedence = templ.importPrecedence;
    priority = templ.priority;
    position = pos;
    sName = templ.name;

    if (!templ.bSpecifiedPriority) {
      if (match.et instanceof ETLocationStep) {
        ETLocationStep loc = (ETLocationStep) match.et;

        if (loc.getNodeTest().rawname.equals("*")) {
          priority = -0.25;
        } else if (loc.getNodeTest().localname.equals("*")) {
          priority = -0.12;
        } else if (loc.getNodeTest().localname.equals("node()") || loc.getNodeTest().localname.equals("text()") || loc.getNodeTest().localname.equals("comment()")) {
          priority = -0.5;
        } else {
          priority = 0;
        }
      } else {
        priority = 0.5;
      }
    }// else 

    bMatchable = (match.squery == null || match.squery.length() == 0) ? false : true;
  }

  //  public boolean isMatchable() {
  //    return bMatchable;
  //  //    if (match.squery == null || match.squery.length() == 0) {
  //  //      return false;
  //  //    } else {
  //  //      return true;
  //  //    }
  //  }
  //  public boolean isDefault() {
  //    return bIsDefault;
  //  }
  public void setPriority(String p) throws XSLException {
    if (p != null && p.length() > 0) {
      priority = template.owner.staticDouble.stringToDouble(p);
    } else {
      if (match.et instanceof ETLocationStep) {
        ETLocationStep loc = (ETLocationStep) match.et;

        if (loc.getNodeTest().localname.equals("*")) {
          priority = -0.25;
        } else if (loc.getNodeTest().localname.equals("node()") || loc.getNodeTest().localname.equals("text()") || loc.getNodeTest().localname.equals("comment()")) {
          priority = -0.5;
        } else {
          priority = 0;
        }
      } else {
        priority = 0.5;
      }
    }
  }

  public int compareTo(Object o) {
    XSLTemplateHeader t = (XSLTemplateHeader) o;

    if (importPrecedence != t.importPrecedence) {
      if (importPrecedence < t.importPrecedence) {
        return -1;
      } else {
        return 1;
      }
    }

    if (priority != t.priority) {
      //LogWriter.getSystemLogWriter().print("Comparing: " + priority + "<" + t.priority + "  -  ");
      if (priority < t.priority) {
        //LogWriter.getSystemLogWriter().println(true);
        return -1;
      } else {
        //LogWriter.getSystemLogWriter().println(false);
        return 1;
      }

      //return (int)((priority - t.priority)*100);
    }

    return position - t.position;
  }

  public String toString() {
    return "#XSLTemplateHeader: match=" + match.squery + " name=" + sName + " prior= " + priority;
  }

}

