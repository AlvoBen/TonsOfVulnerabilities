package com.sap.engine.lib.schema.exception;

import com.sap.engine.lib.xml.util.NestedRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-11-12
 * Time: 11:49:10
 * To change this template use Options | File Templates.
 */
public class SchemaValidationRuntimeException extends NestedRuntimeException {

  public SchemaValidationRuntimeException(Throwable cause) {
    super(cause);
  }

  public SchemaValidationRuntimeException(String message) {
    super(message);
  }

  public SchemaValidationRuntimeException(Throwable cause, String message) {
    super(cause, message);
  }

  public SchemaValidationRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
