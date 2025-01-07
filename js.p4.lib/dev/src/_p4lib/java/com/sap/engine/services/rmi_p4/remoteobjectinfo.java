package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.all.ConnectionProfile;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.10
 */
public abstract class RemoteObjectInfo implements java.io.Serializable {

  static final long serialVersionUID = 5094603158314034311L;
  public static final String SUPPORT_OPTIMIZATION = "SupportOptimization";
  public ConnectionProfile[] connectionProfiles;
  public int client_id = -1;
  public String server_classLoaderName = null;

  /**
   * the server id on witch is installed the remote object implementation
   */
  public int server_id;

  /**
   * the server id on witch is installed the client part of the remote object
   */
  public int local_id;

  public HashMap<String, String> operationNames = new HashMap<String, String>();

  /**
   * Identify owner P4ObjectBroker
   */
  public int ownerId;

  /**
   * The object key to witch is mapped implementation
   * reference of the object on the server process VM
   */
  public byte[] key;

  /**
   * protocol string identification
   */
  public byte[] protocol_id;
  public boolean isRedirectable = false;
  public String redirIdent = "";

  protected String factoryName = null;
  protected Serializable objIdentity = null;

  public String[] stubs = null;
  public boolean connected = false;

  /**
   * resources with classes from the engine
   */
  public String[] urls = null;

  /**
   * urls with all available dispatchers
   */
  public String[] hosts = null;


  public String getServer_classLoaderName() {
    return server_classLoaderName;
  }

  public void setServer_classLoaderName(String server_classLoaderName) {
    this.server_classLoaderName = server_classLoaderName;
  }

  public String[] getHosts() {
    return hosts;
  }

  public String[] getUrls() {
    return urls;
  }

  public void setHosts(String[] strings) {
    hosts = strings;
  }

  public void setUrls(String[] strings) {
    urls = strings;
  }

  public int getOwnerId() {
    return ownerId;
  }

  public int getServer_id() {
    return server_id;
  }

  public String[] getStubs() {
    return stubs;
  }

  public void setOwnerId(int i) {
    ownerId = i;
  }

  public void setServer_id(int i) {
    server_id = i;
  }

  public void setStubs(String[] strings) {
    stubs = strings;
  }

  public String getFactoryName() {
    return factoryName;
  }

  public void setFactoryName(String factoryName) {
    this.factoryName = factoryName;
  }

  public Serializable getObjIdentity() {
    return objIdentity;
  }

  public void setObjIdentity(Serializable objIdentity) {
    this.objIdentity = objIdentity;
  }

  public boolean supportOptimization(){
    if(operationNames == null){
      operationNames = new HashMap<String, String>();
    }
    if(operationNames.get(SUPPORT_OPTIMIZATION) != null && this.operationNames.get(SUPPORT_OPTIMIZATION).equalsIgnoreCase("true")){
      return true;
    }
    return false;
  }

  public void setOptimization(boolean support){
    this.operationNames.put(SUPPORT_OPTIMIZATION, String.valueOf(support));
  }

  public void setOpNameMap(HashMap<String, String> operationNames){
    this.operationNames = operationNames;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    boolean isOpt = false;
    String ret = this.operationNames.get(SUPPORT_OPTIMIZATION);
    if(ret != null && ret.equalsIgnoreCase("true")){
      isOpt = true;
    }
    result.append(" ------ P4 Remote Object Info : " + super.toString());
    result.append("\r\n   |-           broker id : " + ownerId);
    result.append("\r\n   |-           server id : " + server_id);
    result.append("\r\n   |-           client Id : " + client_id);
    result.append("\r\n   |-            local Id : " + local_id);
    result.append("\r\n   |-     is redirectable : " + isRedirectable);
    result.append("\r\n   |-         redir Ident : " + redirIdent);
    result.append("\r\n   |-        factory Name : " + factoryName);
    result.append("\r\n   |-               objId : " + objIdentity);
    result.append("\r\n   |-  server loader name : " + server_classLoaderName);
    result.append("\r\n   |-           connected : " + connected);
    result.append("\r\n   |- supportOptimization : " + isOpt);
    if(protocol_id != null){
      result.append("\r\n   |-            protocol : " + Message.toString(protocol_id, 0, protocol_id.length));
    }
    result.append("\r\n        Remote Interfaces : ");
    if (stubs != null) {
      for (int i = 0; i < stubs.length; i++) {
        result.append("\r\n   Interface : [" + i + "] = " + stubs[i]);
      }
    }
    result.append("\r\n =========================================");
    result.append("\r\n       Connection profiles : \r\n");
    if (connectionProfiles != null) {
      for (int i = 0; i < connectionProfiles.length; i++) {
        result.append("\r\n   connection profile : [" + i + "] = " + connectionProfiles[i]);
      }
      result.append("\r\n =========================================\r\n");
    }
    if (hosts != null) {
      result.append("\r\n       Hosts : \r\n");
      for (int i = 0; i < hosts.length; i++) {
        result.append("\r\n host[" + i + "]= " + hosts[i]);
      }
      result.append("\r\n =========================================\r\n");
    }
    if (urls != null) {
      result.append("\r\n       URLs : ");
      for (int i = 0; i < urls.length; i++) {
        result.append("\r\n URL[" + i + "]= " + urls[i]);
      }
      result.append("\r\n =========================================\r\n");
    }

    return result.toString();
  }

}

