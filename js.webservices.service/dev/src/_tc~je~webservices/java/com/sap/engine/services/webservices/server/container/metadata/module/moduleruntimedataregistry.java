/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
 
package com.sap.engine.services.webservices.server.container.metadata.module;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Title: ModuleRuntimeDataRegistry 
 * Description: ModuleRuntimeDataRegistry is a registry for module runtime data
 * 
 * @author Dimitrina Stoyanova
 * @version
 */
public class ModuleRuntimeDataRegistry {

  private Hashtable<String, ModuleRuntimeData> moduleRuntimeDatas;
  
  public ModuleRuntimeDataRegistry() {
    this.moduleRuntimeDatas = new Hashtable<String, ModuleRuntimeData>();   
  }
   
  /**
   * @return - a hashtable of module runtime data 
   */
  public Hashtable getModuleRuntimeDatas() {
    if(moduleRuntimeDatas == null) {
      moduleRuntimeDatas = new Hashtable();
    }
    return moduleRuntimeDatas;
  }
  
  public boolean containsModuleRuntimeDataID(String id) {
    return getModuleRuntimeDatas().containsKey(id);    
  }
  
  public boolean containsModuleRuntimeData(ModuleRuntimeData moduleRuntimeData) {
    return getModuleRuntimeDatas().contains(moduleRuntimeData);    
  }
  
  public void putModuleRuntimeData(String id, ModuleRuntimeData moduleRuntimeData) {
    getModuleRuntimeDatas().put(id, moduleRuntimeData);    
  }
  
  public ModuleRuntimeData getModuleRuntimeData(String id) {
    return (ModuleRuntimeData)getModuleRuntimeDatas().get(id);    
  }
  
  public ModuleRuntimeData removeModuleRuntimeData(String id) {
    return (ModuleRuntimeData)getModuleRuntimeDatas().remove(id);            
  }
  
  public String toString() {
    String resultStr = ""; 
    String nl = System.getProperty("line.separator");    
  
    Hashtable moduleRuntimeDatas = getModuleRuntimeDatas();
  
    if(moduleRuntimeDatas.size() == 0) {
      return "EMPTY";
    }
  
    Enumeration enum1 = moduleRuntimeDatas.keys();    
    while(enum1.hasMoreElements()) {
      String moduleName = (String)enum1.nextElement();
      ModuleRuntimeData moduleRuntimeData = (ModuleRuntimeData)moduleRuntimeDatas.get(moduleName);
      resultStr += moduleRuntimeData.toString() + nl;      
    }            
  
    return resultStr;
  }   

}
