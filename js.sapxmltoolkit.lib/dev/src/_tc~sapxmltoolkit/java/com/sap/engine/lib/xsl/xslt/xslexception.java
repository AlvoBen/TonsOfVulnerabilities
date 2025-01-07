package com.sap.engine.lib.xsl.xslt;

/**
 * @author Vladimir Savtchenko
 * @version 1.0
 * First Edition: 31.10.2000
 */
public class XSLException extends com.sap.engine.lib.xml.util.NestedException {

  public XSLException() {
    super();
  }

  public XSLException(String s) {
    super(s);
  }

  public XSLException(String s, Throwable thr) {
    super(s, thr);
  }

  public XSLException(Throwable thr) {
    super(thr);
  }
}

