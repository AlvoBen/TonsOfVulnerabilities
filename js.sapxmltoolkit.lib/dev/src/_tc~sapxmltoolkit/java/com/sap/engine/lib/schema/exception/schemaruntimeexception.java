package com.sap.engine.lib.schema.exception;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-1-27
 * Time: 17:11:50
 * To change this template use Options | File Templates.
 */
public class SchemaRuntimeException extends RuntimeException {

  public SchemaRuntimeException(Throwable throwable) {
    super(throwable);
  }

  public SchemaRuntimeException(String errorMessage) {
    super(errorMessage);
  }
}
