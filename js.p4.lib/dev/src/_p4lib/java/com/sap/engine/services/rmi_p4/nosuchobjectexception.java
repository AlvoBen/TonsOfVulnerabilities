package com.sap.engine.services.rmi_p4;

public class NoSuchObjectException extends java.rmi.NoSuchObjectException  {

  static final long serialVersionUID = -7766644245394369896L;

  public NoSuchObjectException() {
    super("");
  }

  public NoSuchObjectException(String s) {
    super(s);
  }

  public NoSuchObjectException(String s, Throwable ex) {
    super(s + "Caused by : " + ex.toString());
  }
}
