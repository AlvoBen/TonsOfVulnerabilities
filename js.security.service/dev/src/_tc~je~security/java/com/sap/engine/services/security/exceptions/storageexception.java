package com.sap.engine.services.security.exceptions;

public class StorageException extends SecurityException {

  private static final long serialVersionUID = 1L;

  public StorageException(String message) {
    super(message);
  }

  public StorageException(String message, Exception ex) {
    super(message, ex);
  }

}