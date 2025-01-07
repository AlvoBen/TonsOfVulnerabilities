package com.sap.engine.lib.xsl.xslt.output;

/**
 * Title:        xml2000
 * Description:  Helper class for DocHandlerToOuptut.
 *               Stores and delivers instances of Method and Indent implementations.
 *               Produces them only if requested.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
class InstanceHolder {

  private MethodXML methodXMLInstance = null;
  private MethodHTML methodHTMLInstance = null;
  private MethodText methodTextInstance = null;
  private MethodDump methodDumpInstance = null;
  private IndenterEmpty indenterEmptyInstance = null;
  private Indenter indenterImplInstance = new IndenterImpl1(); //xxx using imlp 0 because when useing 1 and buffering then information about disable and enable output escaping is lost

  public InstanceHolder() {

  }

  MethodXML getMethodXML() {
    if (methodXMLInstance == null) {
      methodXMLInstance = new MethodXML();
    }

    return methodXMLInstance;
  }

  MethodHTML getMethodHTML() {
    if (methodHTMLInstance == null) {
      methodHTMLInstance = new MethodHTML();
    }

    return methodHTMLInstance;
  }

  MethodText getMethodText() {
    if (methodTextInstance == null) {
      methodTextInstance = new MethodText();
    }

    return methodTextInstance;
  }

  MethodDump getMethodDump() {
    if (methodDumpInstance == null) {
      methodDumpInstance = new MethodDump();
    }

    return methodDumpInstance;
  }

  IndenterEmpty getIndenterEmpty() {
    if (indenterEmptyInstance == null) {
      indenterEmptyInstance = new IndenterEmpty();
    }

    return indenterEmptyInstance;
  }

  Indenter getIndenterImpl() {
    /*
     if (indenterImplInstance == null) {
     //indenterImplInstance = new IndenterImpl0();
     indenterImplInstance = new IndenterImpl1();
     }
     */
    return indenterImplInstance;
  }

}

