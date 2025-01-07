/*
 * Created on 10.01.2005
 *
 */
package com.sap.security.core.server.jaas.spnego;

import com.sap.security.core.server.jaas.DetailedLoginException;

public class SPNegoProtocolException extends DetailedLoginException {
  private byte cause = -1;

  /**
   * 
   * @param message
   *          The exception message.
   * @param cause
   *          The exception cause.
   */
  public SPNegoProtocolException(String message, byte cause) {
    super(message, cause);
    this.cause = cause;
  }

  /**
   * Gets the cause of the login exception.
   * 
   * @return an identifier of the exception cause.
   */
  public byte getExceptionCause() {
    return cause;
  }
}
