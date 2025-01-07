package com.sap.liteadmin.application;

public class EJBLocalReferencesSettings {

  private String elementName;
  private String description;
  private String name;
  private String link;
  private String local;
  private String jndiName;
  private String localHome;
  private String type;

  public EJBLocalReferencesSettings() {
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
    
  }

  public void setDescritption(String description) {
    this.description = description;    
  }

  public void setEJBName(String name) {
    this.name = name;    
  }

  public void setEJBLink(String link) {
    this.link = link;
    
  }

  public void setLocal(String local) {
    this.local = local;
    
  }

  public void setJNDIName(String jndiName) {
    this.jndiName = jndiName;    
  }

  public void setLocalHome(String localHome) {
    this.localHome = localHome;    
  }

  public void setType(String type) {
    this.type = type;    
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getLocal() {
    return local;
  }

  public String getLocalHome() {
    return localHome;
  }

  public String getType() {
    return type;
  }

  
}
