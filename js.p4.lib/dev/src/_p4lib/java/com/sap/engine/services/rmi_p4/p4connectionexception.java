package com.sap.engine.services.rmi_p4;

public class P4ConnectionException extends P4RuntimeException {

  static final long serialVersionUID = -2473004941499376700L;

  public static String ConnectionLost = "p4_0003";
  public static String Ilegal_client_ID = "p4_0005";
  public static String Stub_couldnot_Establish_connection = "p4_0021";

  public P4ConnectionException(){
    super();
  }

  public P4ConnectionException(String msg){
    super(msg);
  }

  public P4ConnectionException(String msg,Throwable th){
    super(msg,th);
  }

  public P4ConnectionException(String msg,Throwable th, Object[] args){
    super(msg, th, args);
  }

}
