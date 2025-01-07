package com.sap.liteadmin.application;

public class JSPPropertiesGroupSettings {

  private String pageEncoding;
  private String[] patterns;
  private boolean scriptingInvalid;
  private String[] includePreludes;
  private boolean elIgnored;
  private boolean xml;
  private String elementName;
  private String displayName;
  private String description;
  private String[] includeCodas;

  public JSPPropertiesGroupSettings() {
  }

  public void setPageEncoding(String pageEncoding) {
    this.pageEncoding = pageEncoding;    
  }

  public void setURLPatterns(String[] patterns) {
    this.patterns = patterns;    
  }

  public void setScriptingInvalid(boolean scriptingInvalid) {
    this.scriptingInvalid = scriptingInvalid;    
  }

  public void setIncludePreludes(String[] includePreludes) {
   this.includePreludes = includePreludes;    
  }

  public void isELIgnored(boolean elIgnored) {
    this.elIgnored = elIgnored;    
  }

  public void setXML(boolean xml) {
    this.xml = xml;      
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;    
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    
  }

  public void setDescritption(String description) {
    this.description = description;    
  }

  public String getDescription() {
    return description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getElementName() {
    return elementName;
  }

  public boolean isElIgnored() {
    return elIgnored;
  }

  public String getIncludePreludes() {
    return arrayToString(includePreludes);
  }

  public String getPageEncoding() {
    return pageEncoding;
  }

  public String getPatterns() {
    return arrayToString(patterns);
  }

  public boolean isScriptingInvalid() {
    return scriptingInvalid;
  }

  public boolean isXml() {
    return xml;
  }
  
  public void setIncludeCodas(String[] includeCodas) {
    this.includeCodas = includeCodas;    
  }
  
  public String getIncludeCodas() {
    return arrayToString(includeCodas);
  }
  
  private String arrayToString(String array[]) {
    if (array == null) {
      return "";
    }
    
    String result = "";
    for (int i = 0; i < array.length; i++) {
      result = result + array[i];
    }
    return result;
  }
}
