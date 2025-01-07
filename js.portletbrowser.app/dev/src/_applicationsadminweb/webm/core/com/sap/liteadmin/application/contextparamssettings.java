package com.sap.liteadmin.application;

public class ContextParamsSettings {

  private String value;
  private String name;
  private String description;
  private String elementName;

  public ContextParamsSettings() {
  }

  public void setName(String name) {
    this.name = name;    
  }

  public void setValue(String value) {
    this.value = value;    
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void setDescription(String description) {
    this.description = description;    
  }

  public void setElelemntName(String elementName) {
    this.elementName = elementName;    
  }

  public String getDescription() {
    return description;
  }

  public String getElementName() {
    return elementName;
  }

}
