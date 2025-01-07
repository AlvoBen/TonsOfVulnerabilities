package com.sap.liteadmin.application;

public class ResourceReferencesSettings {

  private String jndiName;
  private String name;
  private String type;
  private String authType;
  private String sharingScope;
  private boolean transactional;
  private String elementName;
  private String description;

  public ResourceReferencesSettings() {
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

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public void setSharingScope(String sharingScope) {
    this.sharingScope = sharingScope;
  }

  public void setTransactional(boolean transactional) {
    this.transactional = transactional;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public void setDescritption(String description) {
    this.description = description;
  }

  public String getAuthType() {
    return authType;
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

  public String getSharingScope() {
    return sharingScope;
  }

  public boolean isTransactional() {
    return transactional;
  }

  public String getType() {
    return type;
  }

}
