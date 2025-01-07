package com.sap.liteadmin.application;

public class ServletMappingsSettings {
  
  private String servletName = null;
  private String urlPattern = null;
  private String description = null;
  
  public ServletMappingsSettings() {
    
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getServletName() {
    return servletName;
  }

  public void setServletName(String servletName) {
    this.servletName = servletName;
  }

  public String getUrlPattern() {
    return urlPattern;
  }

  public void setUrlPattern(String urlPattern) {
    this.urlPattern = urlPattern;
  }
}
