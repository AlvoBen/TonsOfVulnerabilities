package com.sap.engine.services.rmi_p4;

public class InitializingException extends RuntimeException {

  static final long serialVersionUID = -2447303403706150058L;

  public InitializingException() {
    super();
  }

  public InitializingException(String str) {
    super(str);
  }
  
  public InitializingException(String str, Throwable t) {
    super(str, t);
  }

  public InitializingException(Throwable thr){
    super(thr);
  }

  public InitializingException(String str, Throwable t, Object[] args) {
    super(str, t);
  }
}

