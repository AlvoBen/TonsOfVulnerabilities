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

package com.sap.engine.services.webservices.server.container.configuration;

import java.util.Enumeration;
import java.util.Hashtable;

import com.sap.engine.services.webservices.espbase.configuration.InterfaceDefinition;

/**
 * Title: InterfaceDefinitionRegistry
 * Description: InterfaceDefinitionRegistry is a registry for interface definitions
 * 
 * @author Dimitrina Stoyanova
 * @version
 */

public class InterfaceDefinitionRegistry {
  
  private Hashtable<String, InterfaceDefinition> interfaceDefinitions;
  
  public InterfaceDefinitionRegistry() { 
    this.interfaceDefinitions = new Hashtable<String, InterfaceDefinition>(); 
  }
 
  /**
   * @return - a hashtable of interface definitions 
   */
  public Hashtable<String, InterfaceDefinition> getInterfaceDefinitions() {
    if(interfaceDefinitions == null) {
      interfaceDefinitions = new Hashtable<String, InterfaceDefinition>();
    }
    
    return interfaceDefinitions;
  }
  
  public boolean containsInterfaceDefinitionID(String id) {
    return getInterfaceDefinitions().containsKey(id);
  }
  
  public boolean containsInterfaceDefinition(InterfaceDefinition interfaceDefinition) {
    return getInterfaceDefinitions().contains(interfaceDefinition);
  }
  
  public void putInterfaceDefinition(String id, InterfaceDefinition interfaceDefinition) throws Exception {
    if(containsInterfaceDefinitionID(id)) {
      //throw new RegistryException(PatternKeys.WS_DUBLICATE_ELEMENT, new Object[]{id});
    }
    getInterfaceDefinitions().put(id, interfaceDefinition);
  }
  
  public InterfaceDefinition getInterfaceDefinition(String id) {
    return (InterfaceDefinition)getInterfaceDefinitions().get(id);
  }
    
  public InterfaceDefinition removeInterfaceDefinition(String id) {
    return (InterfaceDefinition)getInterfaceDefinitions().remove(id);
  }
  
  public String[] listInterfaceDefinitionIds() {
    Hashtable<String, InterfaceDefinition> interfaceDefinitions = getInterfaceDefinitions(); 
    if(interfaceDefinitions.size() == 0) {
      return new String[0]; 	
    }
    
    Enumeration<String> enumer = interfaceDefinitions.keys(); 
    String[] interfaceDefinitionIds =  new String[interfaceDefinitions.size()];
    int i = 0;
    while(enumer.hasMoreElements()) {
      interfaceDefinitionIds[i++] = (String)enumer.nextElement();
    }   

    return interfaceDefinitionIds;   
  }
  
  public InterfaceDefinition[] listInterfaceDefinitions() {
    Hashtable<String, InterfaceDefinition> interfaceDefinitions = getInterfaceDefinitions(); 
    if(interfaceDefinitions.size() == 0) {
      return new InterfaceDefinition[0];
    }

    Enumeration<InterfaceDefinition> enumer = interfaceDefinitions.elements();
    InterfaceDefinition[] interfaceDefinitionsArr = new InterfaceDefinition[interfaceDefinitions.size()];
    int i = 0;
    while(enumer.hasMoreElements()) {
      interfaceDefinitionsArr[i++] = (InterfaceDefinition)enumer.nextElement();
    }   

    return interfaceDefinitionsArr; 
  }
  
  public String toString() {
    String resultStr = ""; 
    String nl = System.getProperty("line.separator");    
  
    Hashtable<String, InterfaceDefinition> interfaceDefinitions = getInterfaceDefinitions();
    if(interfaceDefinitions.size() == 0) {
      return "EMPTY";
    }
  
    Enumeration<String> enumer = interfaceDefinitions.keys();
    int i = 0; 
    while(enumer.hasMoreElements()) {
      resultStr += "Interface Definition[" + i++ + "]: " + enumer.nextElement() + nl;   
    }            
  
    return resultStr;
  }
  
}
