package com.sap.liteadmin.application;

public class ServiceReferencesSettings {

  private String description;
  private String elementName;
  private String type;
  private String name;
  private String jndiName;

  public ServiceReferencesSettings() {

  }

  public void setJNDIName(String jndiName) {
    this.jndiName = jndiName;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getJndiName() {
    return jndiName;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

}
