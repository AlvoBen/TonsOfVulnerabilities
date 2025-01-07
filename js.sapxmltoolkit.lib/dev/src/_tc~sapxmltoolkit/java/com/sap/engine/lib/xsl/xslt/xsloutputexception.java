package com.sap.engine.lib.xsl.xslt;

public final class XSLOutputException extends XSLException {

  public static final byte IO_ERROR = 0;
  public static final byte PROCESSING_ALREADY_STARTED_ERROR = 1;
  public static final byte NO_OUTPUTSTREAM_SPECIFIED_ERROR = 2;
  public static final byte INVALID_XML_ERROR = 3;
//  private static final String ERROR_MESSAGES[] = {"Error writing output.", "Cannot set output stream if output processing is already started.", "No output stream specified.", "Invalid XML format."};
  byte errorcode;
  String msg;

  public XSLOutputException(byte code, String msg) {
    errorcode = code;
    this.msg = msg;
  }

  public XSLOutputException(byte code, Exception e) {
    super(e.toString(), e);
    errorcode = code;
    this.msg = e.toString();
  }

  public XSLOutputException(byte code) {
    errorcode = code;
    this.msg = "";
  }

  public XSLOutputException(String str) {
    super(str);
    errorcode = 0;
    this.msg = str;
  }

  public XSLOutputException(String str, Exception e) {
    super(str, e);
  }

}

