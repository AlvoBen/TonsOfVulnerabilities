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
package com.sap.engine.services.appmigration.api.upgrade;

import java.io.Serializable;
import java.rmi.RemoteException;

import com.sap.engine.services.appmigration.api.util.VersionIF;

/**
 * This class is used to create a version object from a string
 * and it should be used only by the upgrade procedure remotely.
 * Its methods throws RemoteException
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public interface RemoteVersionFactoryIF extends Serializable
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
      throws RemoteException;


    /** Construct a version object from a string
     * 
     * @param versionString the version that should be constructed
     * @return version object 
     */  
    public VersionIF getVersion(String name, String vendor, String versionString)
        throws RemoteException;
        
    /** 
     * Construct a version object that must represent
     * the source engine version from a string
     * 
     * @param versionString the version that should be constructed
     * @return version object 
     */  
    public VersionIF getSourceEngineVersion(String versionString)
        throws RemoteException;        

    /** 
     * Construct a version object that must represent
     * the target engine version from a string
     * 
     * @param versionString the version that should be constructed
     * @return version object 
     */  
    public VersionIF getTargetEngineVersion(String versionString)
        throws RemoteException;   
}
