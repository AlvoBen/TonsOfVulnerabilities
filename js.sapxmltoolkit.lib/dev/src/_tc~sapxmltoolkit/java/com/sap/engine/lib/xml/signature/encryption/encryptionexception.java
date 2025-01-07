package com.sap.engine.lib.xml.signature.encryption;

import com.sap.engine.lib.xml.signature.SignatureException;

public class EncryptionException extends SignatureException {

  public EncryptionException() {

  }

  public EncryptionException(Throwable cause) {
    super(cause);
  }

  public EncryptionException(String message) {
    super(message);
  }

  public EncryptionException(Throwable cause, String message) {
    super(cause, message);
  }

  public EncryptionException(String message, Throwable cause) {
    super(message, cause);
  }

}

