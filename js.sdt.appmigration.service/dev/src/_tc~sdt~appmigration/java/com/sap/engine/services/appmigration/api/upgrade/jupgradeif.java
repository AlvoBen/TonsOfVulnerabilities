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

import java.rmi.RemoteException;
import java.util.List;

import com.sap.engine.services.appmigration.api.util.VersionIF;

/**
 *
 */
/**
 * The main interface that should be implemented from the
 * upgrade procedure in order to provide information  
 * necessary for the migration applications
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface JUpgradeIF extends java.rmi.Remote
{
    /**
     * Set the componentVersions
     * 
     * @param compVersions
     */
    public void setSourceComponentVersions(String name, String vendor, VersionIF version) throws RemoteException;
    

    /**
     * Set the source product version
     * 
     * @param version
     */
    public void setSourceProductVersion(VersionIF version) throws RemoteException;
    

    /**
     * Set the target product version
     * 
     * @param version
     */
    public void setTargetProductVersion(VersionIF version) throws RemoteException;
    
    
    /**
     * Called by the upgrade procedure to invoke the migration
     * service to read BC_COMPVERS table and to store the version 
     * information in config manager. This method can be used
     * by JSPM to keep the source version before updating it
     * with the target version
     *      
     */
    public void readComponentVersions() throws RemoteException;

    /**
     * Sets application parameters. These parameters are 
     * from user input during the upgrade procedure
     * 
     * @param appName the name of the application
     * Example: Setting parameter with name - <code>user</code>, 
     * value(as file) - <code>myFile</code> for application 
     * <code>myApp</code> with vendor <code>sap.com</code>:<br>
     * <code>
     * setApplicationParam("myApp", "sap.com", user, myFile)
     * </code>
     * 
     * @param paramName the name of the parameter to be stored
     * @param param the value of the parameter
     */
    public void setApplicationParam(String appName, String vendor, String paramName, Object param) throws RemoteException;

    /**
     * Sets application parameters. These parameters are 
     * from user input during the upgrade procedure
     * 
     * @param appName the name of the application
     * Example: Setting parameter with name - <code>user</code>, 
     * value - <code>myuser</code> for application 
     * <code>myApp</code> with vendor <code>sap.com</code>:<br>
     * <code>
     * setApplicationParam("myApp", "sap.com", user, value)
     * </code>
     * 
     * @param paramName the name of the parameter to be stored
     * @param param the value of the parameter
     */
    public void setApplicationParamEntry(String appName, String vendor, String paramName, Object param) throws RemoteException;

    /**
     * 
     * @param configHandler
     * @throws RemoteException
     */
    public void openCfg(String confName) 
        throws RemoteException;

     /**
      * 
      * @throws RemoteException
      */
    public void deleteAMCEntries()
       throws RemoteException;

    /**
     * Used to clean up the resources(final submit, close configurations)
     * when finished
     * @throws RemoteException
     */ 
    public void cleanUp() throws RemoteException;
    
    /**
     * Gets the version factory, use this method to create 
     * VersionIF objects
     * @return the version factory
     */
    public RemoteVersionFactoryIF getVersionFactory() throws RemoteException;
    
    /**
     * Used to get the information about the failed modules
     *
     * @return List containing MigrationModuleInfoIF objects 
     * which migration didn't pass successfully
     * 
     * @throws RemoteException if something is wrong
     */
    public List getFailedModulesInfo() throws RemoteException;
    
    /**
     * Used to get the information about the successfully passed 
     * migration modules
     *
     * @return List containing MigrationModuleInfoIF objects 
     * which migration has passed successfully
     * 
     * @throws RemoteException if something is wrong
     */
    public List getSuccessfulModulesInfo() throws RemoteException;

	/**
	 * Used to get the information about the not passed 
	 * migration modules
	 *
	 * @return List containing MigrationModuleInfoIF objects 
	 * which migration has never been started
	 * 
	 * @throws RemoteException if something is wrong
	 */
	public List getNotPassedModulesInfo() throws RemoteException;
	    
    /**
     * Used to get the information about all 
     * migration modules
     *
     * @return List containing MigrationModuleInfoIF objects 
     * that contains information about all 
     * 
     * @throws RemoteException if something is wrong
     */
    public List getAllModulesInfo() throws RemoteException;
    
    
    /**
     * Return the number of the migration modules deployed
     * @return the number of the migration modules deployed
     * @throws RemoteException
     */
    public int getMigrationModulesLenght() throws RemoteException;
    
    
    /**
     * Checks if there are migration modules deployed
     *  
     * @return true if there are at least one migration module
     * deployed, false otherwise
     * @throws RemoteException
     */
    public boolean hasMigrationModules() throws RemoteException;
    
    /**
     * Sets the products of the source system
     *  
     * @param sourceProducts
     */
	public void setSourceProducts(String[] sourceProducts) throws RemoteException;
	
	/**
	 * Sets the products of the target system 
	 * 
	 * @param targetProducts
	 */
	public void setTargetProducts(String[] targetProducts) throws RemoteException;
	
	/**
	 * Sets the usages of the source system
	 * 
	 * @param sourceUsages
	 */
	public void setSourceUsages(String[] sourceUsages) throws RemoteException;
	
	/**
	 * Sets the usages for the target system
	 * 
	 * @param targetUsages
	 */
	public void setTargetUsages(String[] targetUsages) throws RemoteException;

}
