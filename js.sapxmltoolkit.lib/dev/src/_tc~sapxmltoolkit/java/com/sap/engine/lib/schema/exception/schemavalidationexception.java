package com.sap.engine.lib.schema.exception;

import com.sap.engine.lib.schema.exception.SchemaException;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version Feb 26, 2002, 3:29:58 PM
 */
public final class SchemaValidationException extends SchemaException {

  public SchemaValidationException() {

  }

  public SchemaValidationException(Throwable cause) {
    super(cause);
  }

  public SchemaValidationException(String message) {
    super(message);
  }

  public SchemaValidationException(Throwable cause, String message) {
    super(cause, message);
  }

  public SchemaValidationException(String message, Throwable cause) {
    super(message, cause);
  }

}

