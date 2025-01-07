package com.sap.engine.services.rmi_p4;

import java.rmi.RemoteException;

public class MarshalException extends RemoteException {

  static final long serialVersionUID = 6015365802690637782L;

  public MarshalException() {
    super();
  }

  public MarshalException(String s) {
    super(s);
  }

  public MarshalException(String s, Throwable ex) {
    super(s, ex);
  }

  public MarshalException(String s, Throwable ex, Object[] args){
    super(s, ex);
  }
}
