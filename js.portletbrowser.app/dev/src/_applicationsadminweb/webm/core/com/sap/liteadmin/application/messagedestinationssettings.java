package com.sap.liteadmin.application;

public class MessageDestinationsSettings {

  private String jndiName;
  private String name;
  private String elementName;
  private String description;

  public MessageDestinationsSettings() {
  }

  public void setJNDIName(String jndiName) {
    this.jndiName = jndiName;    
  }

  public void setName(String name) {
    this.name = name;    
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
  

}
