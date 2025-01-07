package com.sap.engine.lib.schema.exception;

import com.sap.engine.lib.schema.exception.SchemaException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
public class SchemaComponentException extends SchemaException {

  public SchemaComponentException() {

  }

  public SchemaComponentException(String s) {
    super(s);
  }

  public SchemaComponentException(Throwable th) {
    super(th);
  }

  public SchemaComponentException(String s, Throwable th) {
    super(s, th);
  }

  public SchemaComponentException(Throwable th, String s) {
    super(th, s);
  }

}

