package com.sap.liteadmin.application;

public class TaglibsSettings {

  private String location;
  private String uri;
  private String elementName;
  private String description;

  public TaglibsSettings() {
  }

  public void setLocation(String location) {
    this.location = location;    
  }

  public void setURI(String uri) {
    this.uri = uri;    
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;    
  }

  public void setDescritption(String description) {
    this.description = description;    
  }

  public String getUri() {
    return uri;
  }

  public String getDescription() {
    return description;
  }

  public String getElementName() {
    return elementName;
  }

  public String getLocation() {
    return location;
  }

}
