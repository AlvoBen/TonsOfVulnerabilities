package com.sap.engine.services.rmi_p4.exception;

/**
 * Author: Asen Petrov
 * Date: 2006-8-24
 * Time: 15:21:10
 */
public class NoSuchOperationException extends com.sap.engine.services.rmi_p4.P4RuntimeException {
 static final long serialVersionUID = 1714244481744142616L;

  public NoSuchOperationException() {
    super();
  }

  public NoSuchOperationException(String str) {
    super(str);
  }

  public NoSuchOperationException(String str, Throwable t) {
    super(str, t);
  }

  public NoSuchOperationException(String str, Throwable thr, Object[] args) {
    super(str, thr, args);
  }
}
