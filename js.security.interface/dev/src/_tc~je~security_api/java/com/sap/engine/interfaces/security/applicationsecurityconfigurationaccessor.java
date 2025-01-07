/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security;

import java.util.Set;

/**
 * @author ekaterina.zheleva@sap.com
 */
public interface ApplicationSecurityConfigurationAccessor {
  public static final byte SWITCH_MODE = 1;
  public static final byte UPDATE_MODE = 2;
	
  public void setMode(byte type);
  
  public void setApplicationName(String applicationName);
  
  public String getApplicationName();

  /**
   * Retrieve the PolicyConfigurations of the modules with a specified application type, registered within this application configuration.
   * 
   * @param type the type 
   * @return
   * @throws SecurityException
   */
  public Set getModulesNames(byte type) throws SecurityException;
  
  /**
   * Retrieve the names of the j2ee roles currently deployed for a specified module within this application configuration.
   *
   * @param moduleName
   * @return
   * @throws SecurityException
   */
  public Set getModuleRolesNames(String moduleName) throws SecurityException; //String set
  
  /**
   * Retrieve the names of the UME roles currently mapped to a j2ee role deployed for a specified module within this application configuration.
   *
   * @param moduleName
   * @param j2eeRoleName
   * @return
   * @throws SecurityException
   */
  public Set getModuleMappedServerRoles(String moduleName, String j2eeRoleName) throws SecurityException; //String set
}
