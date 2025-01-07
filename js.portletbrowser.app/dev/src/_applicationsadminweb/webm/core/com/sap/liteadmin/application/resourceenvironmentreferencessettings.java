package com.sap.liteadmin.application;

public class ResourceEnvironmentReferencesSettings {

  private String jndiName;
  private String name;
  private String type;
  private String elementName;
  private String description;

  public ResourceEnvironmentReferencesSettings() {
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
