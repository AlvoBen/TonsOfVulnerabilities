package com.sap.engine.lib.xml.parser;

import java.io.*;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;

import com.sap.engine.lib.log.LogWriter;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
public class NestedSAXParseException extends SAXParseException {
  
  private Throwable cause;
  
  public NestedSAXParseException(String message, Locator locator) {
    super(message, locator);
  }

  public NestedSAXParseException(String message, Locator locator, Exception e) {
    super(message, locator, e);
    this.cause = e;
  }

  public NestedSAXParseException(java.lang.String message, java.lang.String publicId, java.lang.String systemId, int lineNumber, int columnNumber) {
    super(message, publicId, systemId, lineNumber, columnNumber);
  }

   public NestedSAXParseException(java.lang.String message, java.lang.String publicId, java.lang.String systemId, int lineNumber, int columnNumber, java.lang.Exception e) {
    super(message, publicId, systemId, lineNumber, columnNumber, e);
    this.cause = e;
  }

  public NestedSAXParseException(String message, Exception e) {
    this(message, null, e);
  }

  public NestedSAXParseException(Exception e) {
    this(null, null, e);
  }

  public Throwable getCause() {
    return cause;
  }

  public String toString() {
    return(LogWriter.createExceptionRepresentation(this));
  }
}

