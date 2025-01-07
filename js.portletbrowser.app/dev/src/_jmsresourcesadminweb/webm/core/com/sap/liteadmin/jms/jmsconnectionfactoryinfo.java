package com.sap.liteadmin.jms;

public class JMSConnectionFactoryInfo {
   
  private String name;
  private String lookupName;
  
  private String caption;
  private String clientID;
  private String connectionType;
  private String description;
  private String elementName;
  private String lookupNameSettings;
  private boolean selected = false;
  
  public JMSConnectionFactoryInfo(String name, String lookupName) {
    this.name = name;
    this.lookupName = lookupName;
    this.selected = false;
  }

  public String getLookupName() {
    return lookupName;
  }

  public String getName() {
    return name;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getClientID() {
    return clientID;
  }

  public void setClientID(String clientID) {
    this.clientID = clientID;
  }

  public String getConnectionType() {
    return connectionType;
  }

  public void setConnectionType(String connectionType) {
    this.connectionType = connectionType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getElementName() {
    return elementName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public String getLookupNameSettings() {
    return lookupNameSettings;
  }

  public void setLookupNameSettings(String lookupNameSettings) {
    this.lookupNameSettings = lookupNameSettings;
  }
  
  public boolean isSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
