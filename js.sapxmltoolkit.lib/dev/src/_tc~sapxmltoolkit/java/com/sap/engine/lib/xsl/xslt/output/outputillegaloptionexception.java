package com.sap.engine.lib.xsl.xslt.output;

/**
 * Thrown by <tt>DocHandlerToOuptut</tt> when <tt>setOption</tt> is invoked with an
 * illegal argument.
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public class OutputIllegalOptionException extends OutputException {

  public OutputIllegalOptionException() {
    super();
  }

  public OutputIllegalOptionException(String s) {
    super(s);
  }

  public OutputIllegalOptionException(Exception e) {
    super(e);
  }

}

