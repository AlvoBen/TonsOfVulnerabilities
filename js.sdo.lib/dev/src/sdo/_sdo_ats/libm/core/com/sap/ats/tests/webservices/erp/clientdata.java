package com.sap.ats.tests.webservices.erp;

import java.io.File;

public class ClientData {
  private File folder;
  private String url;
  private String serviceClass;
  private String portClass;
  private String serviceQName;
  private String infFile;
  private File iniFolder;
  
  public ClientData(String _url) {
    setUrl(_url);
  }
  
  public File getFolder() {
    return folder;
  }
  public void setFolder(File folder) {
    this.folder = folder;
  }
  public String getServiceClass() {
    return serviceClass;
  }
  public void setServiceClass(String serviceClass) {
    this.serviceClass = serviceClass;
  }
  public String getUrl() {
    return url;
  }
  private void setUrl(String _url) {
    if (_url == null || _url.equals("")) {
      throw new IllegalArgumentException("Illegal value for URL: " + _url);
    }
    
    this.url = _url;
  }
    
  public String getPortClass() {
    return portClass;
  }

  public void setPortClass(String portClass) {
    this.portClass = portClass;
  }
  
  public String getServiceQName() {
    return serviceQName;
  }

  public void setServiceQName(String serviceQName) {
    this.serviceQName = serviceQName;
  }

  public String getInfFile() {
    return infFile;
  }

  public void setInfFile(String infFile) {
    this.infFile = infFile;
  }

  public File getIniFolder() {
    return iniFolder;
  }

  public void setIniFolder(File iniFolder) {
    this.iniFolder = iniFolder;
  }
  
  public boolean equals(Object o) {
    if (!(o instanceof ClientData)) {
      return false;
    }
    
    ClientData obj = (ClientData) o;
    
    if (!obj.getUrl().equals(this.url)) {
      return false;
    }
    
    return true;
  }
  
  public int hashCode() {
    return this.url.hashCode();
  }
  
  public String toString() {
    return "URL=" + url + "; Service Class=" + serviceClass + "; Port Class: " + portClass + "; Service QName=" + serviceQName + "; Folder=" + folder + "; Inf File=" + infFile + "; Initial Folder=" + iniFolder;
  }
  
}
