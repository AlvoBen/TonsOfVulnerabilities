package com.sap.liteadmin.application;

public class LocaleEncodingMappingsSettings {

  private String elementName;
  private String description;
  private String locale;
  private String encoding;

  public LocaleEncodingMappingsSettings() {
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
    
  }

  public void setDescritption(String description) {
    this.description = description;
    
  }

  public void setLocale(String locale) {
    this.locale = locale;
    
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;    
  }

  public String getDescription() {
    return description;
  }

  public String getElementName() {
    return elementName;
  }

  public String getEncoding() {
    return encoding;
  }

  public String getLocale() {
    return locale;
  }

}
