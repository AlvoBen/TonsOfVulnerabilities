package com.sap.security.core.server.jaas;

import com.sap.engine.lib.security.LoginExceptionDetails;

import javax.security.auth.login.LoginException;

/**
 *  Login exception containing the cause for the failed logon.
 *
 * @see com.sap.engine.lib.security.LoginExceptionDetails
 *
 * @author Svetlana Stancheva
 */
public class DetailedLoginException extends LoginException implements LoginExceptionDetails {

  private byte cause = -1;

  /**
   *
   * @param message  The exception message.
   * @param cause  The exception cause.
   */
  public DetailedLoginException(String message, byte cause) {
    super(message);
    this.cause = cause;
  }

  /**
   *  Gets the cause of the login exception.
   *
   * @return  an identifier of the exception cause.
   */
  public byte getExceptionCause() {
    return cause;
  }

}
