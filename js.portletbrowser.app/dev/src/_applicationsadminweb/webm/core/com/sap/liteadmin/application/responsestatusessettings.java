package com.sap.liteadmin.application;

public class ResponseStatusesSettings {

  private String elementName;
  private String description;
  private int statusCode;
  private String reasonPhrase;


  public ResponseStatusesSettings() {
  }


  public void setElementName(String elementName) {
    this.elementName = elementName;
    
  }

  public void setDescritption(String description) {
    this.description = description;
    
  }


  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;    
  }


  public void setReasonPhrase(String reasonPhrase) {
    this.reasonPhrase = reasonPhrase;    
  }


  public String getDescription() {
    return description;
  }


  public String getElementName() {
    return elementName;
  }


  public String getReasonPhrase() {
    return reasonPhrase;
  }


  public int getStatusCode() {
    return statusCode;
  }

}
