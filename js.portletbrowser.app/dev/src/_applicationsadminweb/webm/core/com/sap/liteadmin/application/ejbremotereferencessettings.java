package com.sap.liteadmin.application;

public class EJBRemoteReferencesSettings {

  private String elementName;
  private String description;
  private String name;
  private String link;
  private String remote;
  private String jndiName;
  private String remoteHome;
  private String type;

  public EJBRemoteReferencesSettings() {
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

  public void setRemote(String remote) {
    this.remote = remote;
    
  }

  public void setJNDIName(String jndiName) {
    this.jndiName = jndiName;    
  }

  public void setRemoteHome(String remoteHome) {
    this.remoteHome = remoteHome;    
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

  public String getRemote() {
    return remote;
  }

  public String getRemoteHome() {
    return remoteHome;
  }

  public String getType() {
    return type;
  }

}
