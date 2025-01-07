package com.sap.liteadmin.application;

import java.util.logging.Logger;

public class ApplicationInfo {
  //general infos
  private String name;
  private Integer state;
  private ModulesInfo[] modules;
  
  private boolean isSelected;
  
  //properties return from setting attribute
  private String vendor;
  private String softwareType;
  private String[] remoteSupport; 
  private Long archiveSize;
  private String applicationFailover;
  private String caption;
  private String elementName;
  
  private static final Logger logger = Logger.getLogger(ApplicationInfo.class.getName());

  public ApplicationInfo() {
    name = null;
    state = null;    
    modules = null;
    isSelected = false;
  }

  public ApplicationInfo(String name, Integer state, ModulesInfo[] modules) {
    this.name = name;
    this.state = state;
    this.modules = modules;
    this.isSelected = false;
  }

  public void setName(String name) {
    this.name = name;
  } 
  
  public String getName() {
    return name;
  }

  public void setState(Integer state) {
    this.state = state;
  }
  
  public Integer getState() {
    return state;
  }

  public String getStateString() {
    if (state.intValue() == 0) {
      return "Startting";
    } else if (state.intValue() == 1) {
      return "Started";
    } else if (state.intValue() == 3) {
      return "Stopped";
    } else {
      logger.info("Unknown application status " + state);
      return "unknown:" + state;       
    }
  }

  public void setModules(ModulesInfo[] modules) {
    this.modules = modules;
  }
  
  public ModulesInfo[] getModules() {
    return modules;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  
  // getters and setter for the attributes from settings
  public String getApplicationFailover() {
    return applicationFailover;
  }

  public void setApplicationFailover(String applicationFailover) {
    this.applicationFailover = applicationFailover;
  }

  public Long getArchiveSize() {
    return archiveSize;
  }

  public void setArchiveSize(Long archiveSize) {
    this.archiveSize = archiveSize;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getElementName() {
    return elementName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public String[] getRemoteSupport() {
    return remoteSupport;
  }

  public String getRemoteSupportString() {
    if (remoteSupport == null) {
      return "null";
    }
    
    String result = "";
    for (int i = 0; i < remoteSupport.length; i++) {
      result = result + remoteSupport[i] + "; ";
    }
    return result;
  }
  
  public void setRemoteSupport(String[] remoteSupport) {
    this.remoteSupport = remoteSupport;
  }

  public String getSoftwareType() {
    return softwareType;
  }

  public void setSoftwareType(String softwareType) {
    this.softwareType = softwareType;
  }

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }
}
