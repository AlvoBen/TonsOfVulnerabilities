package com.sap.pj.jmx;

/**
 * @author gregor
 * @version 1.0
 */
public class ImplementationException extends RuntimeException {

  private static final String note =
          "This is an internal error of the SAP JMX implementation.\n";
  //	+ "Please notify <mailto:gregor.karl.frey@sap.com>!";
  /**
   * Constructor for ImplementationException.
   */
  public ImplementationException() {
    super();
  }

  /**
   * Constructor for ImplementationException.
   * @param message
   */
  public ImplementationException(String message) {
    super(note + "\n" + message);
  }

}
