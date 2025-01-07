package com.sap.engine.lib.schema.exception;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      May 2001
 */
public class SchemaException 
//extends com.sap.engine.lib.xml.util.NestedRuntimeException {
extends com.sap.engine.lib.xml.util.NestedException {

  public SchemaException() {

  }

  public SchemaException(Throwable cause) {
    super(cause);
  }

  public SchemaException(String message) {
    super(message);
  }

  public SchemaException(Throwable cause, String message) {
    super(cause, message);
  }

  public SchemaException(String message, Throwable cause) {
    super(message, cause);
  }

}

