package com.sap.engine.lib.xsl.xslt.output;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public class OutputIOException extends OutputException {

  public OutputIOException() {

  }

  public OutputIOException(String s) {
    super(s);
  }

  public OutputIOException(Exception e) {
    super(e);
  }

}

