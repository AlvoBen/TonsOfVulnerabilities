package com.sap.liteadmin.application;

public class MessageDestinationReferencesSetting {

  private String jndiName;
  private String name;
  private String link;
  private String type;
  private String usage;
  private String elementName;
  private String description;

  public MessageDestinationReferencesSetting() {
  }

  public void setJNDIName(String jndiName) {
    this.jndiName = jndiName;    
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLink(String link) {
    this.link = link;    
  }

  public void setType(String type) {
    this.type = type;
    
  }

  public void setUsage(String usage) {
    this.usage = usage;
    
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

  public String getLink() {
    return link;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getUsage() {
    return usage;
  }

}
