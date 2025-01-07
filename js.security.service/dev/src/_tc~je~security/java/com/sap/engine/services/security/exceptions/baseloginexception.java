package com.sap.engine.services.security.exceptions;

import javax.security.auth.login.LoginException;

import com.sap.tc.logging.Severity;
import com.sap.engine.lib.security.LoginExceptionDetails;

public class BaseLoginException extends LoginException implements LoginExceptionDetails {

  public static final long serialVersionUID = 5485264041767498558L;

  private byte cause = -1;
  private int severity = Severity.INFO;

  public BaseLoginException(String message) {
    super(message);
  }

  public BaseLoginException(String message, Throwable causedBy) {
    super(message);
    this.initCause(causedBy);
  }

  public BaseLoginException(String message, byte cause) {
    super(message);
    this.cause = cause;
  }

  public BaseLoginException(String message, Throwable causedBy, byte cause) {
    super(message);
    this.initCause(causedBy);
    this.cause = cause;
  }

  public BaseLoginException(String message, byte cause, int severity) {
    super(message);
    this.cause = cause;
    this.severity = severity;
  }
  
  public BaseLoginException(String message, Throwable causedBy, byte cause, int severity) {
    super(message);
    this.initCause(causedBy);
    this.cause = cause;
    this.severity = severity;
  }

  public byte getExceptionCause() {
    return cause;
  }
  
  public int getLogSeverity() {
    return severity;
  }

}
