package com.sap.engine.session.runtime;

/**
 * This exception is thrown when there is a protection data specified
 * for some session and this protection data is violated
 * @author Nikolai Neichev
 */
public abstract class ProtectionException extends IllegalStateException {

  public ProtectionException(String s) {
    super(s);
  }

}
