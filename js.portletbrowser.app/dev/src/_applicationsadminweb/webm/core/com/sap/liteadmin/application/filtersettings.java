package com.sap.liteadmin.application;

public class FilterSettings {

  private String className = null;
  private String description;
  private String displayName;
  private String elementName;
  
  public FilterSettings() {
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    
  }

  public void setDescription(String description) {
    this.description = description;    
  }

  public String getDescription() {
    return description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;    
  }
  
  public String getElementName() {
    return elementName;
  }
  
}
