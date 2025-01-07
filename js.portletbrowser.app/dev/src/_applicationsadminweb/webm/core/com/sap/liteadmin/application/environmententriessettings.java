package com.sap.liteadmin.application;

public class EnvironmentEntriesSettings {

  private String jndiName;
  private String type;
  private String name;
  private String value;
  private String elementName;
  private String description;

  public EnvironmentEntriesSettings() {
  }

  public void setJNDIName(String jndiName) {
    this.jndiName = jndiName;    
  }

  public void setType(String type) {
    this.type = type;    
  }

  public void setName(String name) {
    this.name = name;    
  }

  public void setValue(String value) {
    this.value = value;    
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

  public String getJndiName() {
    return jndiName;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

}
