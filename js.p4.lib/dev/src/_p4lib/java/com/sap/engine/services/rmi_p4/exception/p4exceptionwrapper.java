package com.sap.engine.services.rmi_p4.exception;

import com.sap.engine.services.rmi_p4.P4ObjectBroker;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class P4ExceptionWrapper implements Serializable {

  static final long serialVersionUID = 2632583003756004183L;

  public Throwable th = null;
  private String msg = null;
  private int type = -1;

  public P4ExceptionWrapper(String msg, int type, Throwable ex){
    this.th = ex;
    this.msg = msg;
    this.type = type;
  }



  public Object replaceObject(Object o){
    return null;

  }

  public Object readResolve() throws ObjectStreamException{
    Exception e = P4ObjectBroker.init().getException(this.type,  this.msg,  this.th);
    return e;
  }
}
