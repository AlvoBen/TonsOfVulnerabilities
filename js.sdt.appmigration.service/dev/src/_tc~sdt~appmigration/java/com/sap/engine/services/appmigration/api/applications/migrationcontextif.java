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
package com.sap.engine.services.appmigration.api.applications;

import javax.sql.DataSource;

import com.sap.engine.services.appmigration.api.exception.ConfigException;
import com.sap.engine.services.appmigration.api.util.ComponentVersionsIF;
import com.sap.engine.services.appmigration.api.util.StatusIF;
import com.sap.engine.services.appmigration.api.util.VersionFactoryIF;
import com.sap.engine.services.appmigration.api.util.VersionIF;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * This is the context that is passed to the application
 * migration module. It is used to pass to the migration modules
 * information that they need about the product source and
 * target versions, component versions, etc.
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface MigrationContextIF
{

    /**
     * This method is used to get the source version 
     * of the engine
     * 
     * @return the engine source version
     */
    public VersionIF getProductSourceVersion() throws ConfigException;

    /**
     * This method is used to get the target version 
     * of the engine
     * 
     * @return the engine target version
     */    
    public VersionIF getProductTargetVersion() throws ConfigException;

    /**
     * This method is used to get the source component version
     * 
     * @return the source component version
     */
    public ComponentVersionsIF getSourceComponentVersions();
    
    /**
     * This method is used to get the target component version
     * 
     * @return the target component version
     */
    public ComponentVersionsIF getTargetComponentVersions();

    /**
     * This method is used to get a status object for a transaction
     * of the migration module. When the migration module executes
     * several transactions, this status object can be used to set
     * and get the status of every transaction.
     * 
     * @return status object for the transaction with the name specified
     * as parameter
     */
    public StatusIF getStatusObj();
    
    /**
     * Check if the migration module has been started at 
     * least once. It does not take into account the result
     * from this execution (successful, not successful, ended
     * with some exception).
     * 
     * @return true if the migration module has been started
     * at least once.
     * @throws ConfigException when problems with the database
     * occur
     */
    public boolean isRestart() throws ConfigException;

    /**
     * This method should be used only by the migration module of 
     * the applications that have asked for user 
     * input during the upgrade procedure. 
     * 
     * @param name the name of the parameter(stored as file) which value is asked
     * @return the value of the parameter
     * @throws ConfigException when problems with the database
     * occur
     */
    public Object getApplicationParam(String name) throws ConfigException;

    /**
     * This method should be used only by the migration module of 
     * the applications that have asked for user 
     * input during the upgrade procedure. 
     * 
     * @param name the name of the parameter which value is asked
     * @return the value of the parameter
     * @throws ConfigException when problems with the database
     * occur
     */    
    public Object getApplicationParamEntry(String name) throws ConfigException;
    
      
    /**
     * Gets the version factory, use this method to create 
     * VersionIF objects
     * @return the version factory
     */
    public VersionFactoryIF getVersionFactory();
    
    /**
     * Used by the migration modules to get the category for logging.
     * All migration modules should log into this category
     * @return the category for logging
     */
    public Category getCategory();
    
    /**
     * Used by the migration modules to get the location for logging.
     * All migration modules should tract into this category
     * @return the location for logging
     */
    public Location getLocation();
    
    /**
     * Used by the migration modules to receive the application classloader
     * @return the application classloader
     */
    public ClassLoader getApplicationLoader();
    
    
    
    /**
     * This method provides information about the products on the source
     * system
     * @return String array containing the products' names
     */    
    public String[] getSourceProducts();

    /**
     * This method provides information about the products of the target
     * system
     * @return String array containing the products' names
     */   	
	 public String[] getTargetProducts();
	  
	/**
     * This method provides information about the usages on the source
     * system
     * @return String array containing the usages
     */   
	 public String[] getSourceUsages();
	  
	/**
     * This method provides information about the usages on the target
     * system
     * @return String array containing the usages
     */
	 public String[] getTargetUsages();

    /** 
     * This method provides the system datasource to the migration
     * modules. It should be used to access to the database
     * 
     * @return the system datasource
     */
	 public DataSource getSystemDataSource();
}
