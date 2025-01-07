/**
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.appmigration.api.util;

import java.io.Serializable;

import com.sap.engine.services.appmigration.api.exception.VersionException;


/**
 * This class is used to create a version object from a string
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface VersionFactoryIF extends Serializable
{
  /**
   * Construct a version object 
   * 
   * @param name the name of the component
   * @param vendor the vendor (e.g. "sap.com")
   * @param release the release number (6.40, 7.00 etc.)
   * @param spLevel the support package level (if null it is treated like "0")
   * @param patchLevel the patch level (if null it is treated like "0")
   * @param counter the counter or the timestamp
   * @return version object
   */  
  public VersionIF getVersion(String name, String vendor, 
        String release, String spLevel, String patchLevel, String counter)
    throws VersionException;


  /** Construct a version object from a string
   * the versionString should be in format 
   * <code><release>.<SPLevel>.<Patch></code><br>
   * Example: Release 6.40, SP11, Patch 0
   * getVersion("SAP-JEE", "sap.com", "6.40.11.0");
   * 
   * @param versionString the version that should be constructed
   * @return version object 
   */  
  public VersionIF getVersion(String name, String vendor, String versionString)
      throws VersionException;
     
}
