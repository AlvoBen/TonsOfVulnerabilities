package com.sap.engine.services.webservices.espbase.server.runtime.exceptions;

public class NoSuchWebServiceException extends RuntimeException{
  public NoSuchWebServiceException(){
    
  }
  
  public NoSuchWebServiceException(String msg){
    super(msg);
  }
}
