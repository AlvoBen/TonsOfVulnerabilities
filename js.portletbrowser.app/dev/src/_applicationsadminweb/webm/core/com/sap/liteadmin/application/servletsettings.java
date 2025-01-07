package com.sap.liteadmin.application;

public class ServletSettings {

  private String elementName;
  /**
   * the class of the servlet or the jsp file
   */  
  private String source;
  private int loadOnStartup;
  //todo 
  private Object initParameters;
  private String displayName;
  private String description;
  
  public ServletSettings() {

  }

  public String getElementName() {
    return elementName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public Object getInitParameters() {
    return initParameters;
  }

  public void setInitParameters(Object initParameters) {
    this.initParameters = initParameters;
  }

  public int getLoadOnStartup() {
    return loadOnStartup;
  }

  public void setLoadOnStartup(int loadOnStartup) {
    this.loadOnStartup = loadOnStartup;
  }
  
  /**
   * the class of the servlet or the jsp file
   */ 
  public String getSource() {
    return source;
  }

  public void setSource(String servletClass_JspFile) {
    this.source = servletClass_JspFile;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    
  }

  public void setDescription(String description) {
    this.description = description;
    
  }

  public String getDescription() {
    return description;
  }

  public String getDisplayName() {
    return displayName;
  } 

}
