package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.util.List;

import javax.xml.namespace.QName;

public class TestData {
  private File folder;
  private String reqPath;
  private String host = "usai1q2o.wdf.sap.corp";
  private String port = "50020";
  private String user = "FINTESTER";
  private String password = "SECATT9!";
  private String client = "026";
  private List<File> payloads;
  private QName infQName;
  
  public TestData(String requestPath) {
    setReqPath(requestPath);
  }
  
  public File getFolder() {
    return folder;
  }
  public void setFolder(File folder) {
    this.folder = folder;
  }
  public String getHost() {
    return host;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getPort() {
    return port;
  }
  public void setPort(String port) {
    this.port = port;
  }
  public String getReqPath() {
    return reqPath;
  }
  private void setReqPath(String requestPath) {
    if (requestPath == null || requestPath.equals("")) {
      throw new IllegalArgumentException("Illegal value for request path: " + requestPath);
    }
    
    this.reqPath = requestPath;
  }
  public String getUser() {
    return user;
  }
  public void setUser(String user) {
    this.user = user;
  }
  
  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public List<File> getPayloads() {
    return payloads;
  }

  public void setPayloads(List<File> payloads) {
    this.payloads = payloads;
  }

  public boolean equals(Object o) {
    if (!(o instanceof TestData)) {
      return false;
    }
    
    TestData obj = (TestData) o;
    
    if (!obj.getReqPath().equals(this.reqPath)) {
      return false;
    }
    
    return true;
  }
  
  public int hashCode() {
    return this.reqPath.hashCode();
  } 
  
  public String toString() {
    return "Request Path=" + reqPath + "; Folder=" + folder + "; Host=" + host + "; Port=" + port + "; User=" + user + "; Password=" + password + "; Client=" + client + "; Payloads=" + payloads;
  }

  public QName getInfQName() {
    return infQName;
  }

  public void setInfQName(QName pInfQName) {
    infQName = pInfQName;
  }
  
}
