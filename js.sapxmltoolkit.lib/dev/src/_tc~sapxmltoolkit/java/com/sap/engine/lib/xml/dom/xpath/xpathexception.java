package com.sap.engine.lib.xml.dom.xpath;

public class XPathException extends RuntimeException {

  public XPathException(short code, String message) {
    super(message);
    this.code = code;
  }

  public short code;
  // XPathExceptionCode
  public static final short INVALID_EXPRESSION_ERR = 1;
  public static final short TYPE_ERR = 2;

}

