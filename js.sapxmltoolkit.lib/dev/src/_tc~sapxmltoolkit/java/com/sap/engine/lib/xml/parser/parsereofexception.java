package com.sap.engine.lib.xml.parser;

/**
 * Class description - This exception is thrown when EOF is detected, it is rather internal
 *
 * @author Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version 1.00
 */
public class ParserEOFException extends ParserException {

  public ParserEOFException(String s) {
    super(s, 0, 0);
  }

  public String toString() {
    return msg;
  }

  public String getMessage() {
    return msg;
  }

}

