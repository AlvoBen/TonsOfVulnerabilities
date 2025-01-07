package com.sap.liteadmin.application;

public class ListenerSettings {

  private String className;
  private String description;
  private String displayName;
  private String elementName;
  
  public ListenerSettings() {
  }
  
  public void setClasstName(String className) {
    this.className = className;
    
  }

  public void setDescription(String description) {
    this.description = description;    
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;    
  }

  public String getClassName() {
    return className;
  }

  public String getDescription() {
    return description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getElementName() {
    return elementName;
  }



}
