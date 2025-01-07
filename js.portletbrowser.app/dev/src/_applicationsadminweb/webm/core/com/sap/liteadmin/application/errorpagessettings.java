package com.sap.liteadmin.application;

public class ErrorPagesSettings {

  
  private String errorCode;
  private String location;
  private String type;
  private String elementName;
  private String description;

  public ErrorPagesSettings() {
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;   
  }

  public void setLocation(String location) {
    this.location = location;    
  }

  public void setType(String type) {
    this.type = type;    
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;    
  }

  public void setDescritption(String description) {
    this.description = description;    
  }

  public String getDescription() {
    return description;
  }

  public String getElementName() {
    return elementName;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getLocation() {
    return location;
  }

  public String getType() {
    return type;
  }

}
