package com.sap.engine.lib.xsl.xslt.output;

import java.io.IOException;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public class OutputException extends IOException {

  public OutputException() {
    super("");
  }

  public OutputException(String s) {
    super(s);
  }

  public OutputException(Exception e) {
    super(e.toString());
  }

}

