package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import java.util.*;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFElementAvailable implements XFunction {

  public static final HashSet AVAILABLE = new HashSet();

  static {
    AVAILABLE.add("apply-imports");
    AVAILABLE.add("apply-templates");
    AVAILABLE.add("attribute");
    AVAILABLE.add("attribute-set");
    AVAILABLE.add("call-template");
    AVAILABLE.add("choose");
    AVAILABLE.add("comment");
    AVAILABLE.add("copy");
    AVAILABLE.add("copy-of");
    AVAILABLE.add("decimal-format");
    AVAILABLE.add("element");
    AVAILABLE.add("fallback");
    AVAILABLE.add("for-each");
    AVAILABLE.add("if");
    AVAILABLE.add("import");
    AVAILABLE.add("include");
    AVAILABLE.add("key");
    AVAILABLE.add("message");
    AVAILABLE.add("namespace-alias");
    //AVAILABLE.add("number");
    AVAILABLE.add("otherwise");
    AVAILABLE.add("output");
    AVAILABLE.add("param");
    AVAILABLE.add("preserve-space");
    AVAILABLE.add("processing-instruction");
    AVAILABLE.add("sort");
    AVAILABLE.add("strip-space");
    AVAILABLE.add("stylesheet");
    AVAILABLE.add("template");
    AVAILABLE.add("text");
    AVAILABLE.add("value-of");
    AVAILABLE.add("variable");
    AVAILABLE.add("when");
    AVAILABLE.add("with-param");
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException { //xxx
    return context.getXFactCurrent().getXBoolean(AVAILABLE.contains(a[0].toXString().getValue().getString()));
  }

  public String getFunctionName() {
    return "element-available";
  }

}

