package com.sap.liteadmin.application;

public class MIMEMappingsSettings {

  private String extension;
  private String mimeType;
  private String elementName;
  private String description;

  public MIMEMappingsSettings() {

  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public void setMIMEType(String mimeType) {
    this.mimeType = mimeType;
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

  public String getExtension() {
    return extension;
  }

  public String getMimeType() {
    return mimeType;
  }

}
