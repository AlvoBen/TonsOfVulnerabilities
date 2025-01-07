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

import com.sap.engine.services.appmigration.api.exception.MigrationException;


/**
 * This is the base interface that all application migration modules 
 * should implement
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface ApplicationMigrationIF
{
    /**
     * This constant indicates that the migration module has
     * finished its execution successfully
     */
    public static final int MIGRATION_OK = 0;
    
    /**
     * This contstant indicates that the migration module has
     * finished its execution with some warnings
     */
    public static final int MIGRATION_WARNINGS = 1;
   
    /**
     * This method is used for preparing the migration module with some
     * initial steps. It is the first method that is called on the 
     * migration module
     * 
     * @param migrationContext the migrationContext that can be
     * used by the migration module to get different information
     * from the Migration container
     * 
     * @throws MigrationException if some exception occured 
     */
    public void init(MigrationContextIF migrationContext)
        throws MigrationException;
        
    /** 
     * This method is invoked from the migration container. 
     * It should be used to initsialises the migration of the 
     * application data.
     * 
     * @param migrationContext this is the context that is 
     * needed to the application to get information about the
     * product and components versions, status, etc. 
     * @return status MIGRATION_OK, if the migration module
     * has finished successfully, MIGRATION_WARNINGS, if the
     * migration module has finished its execution with some 
     * warnings, if any errors occurs the method should end up
     * with throwing MigrationException
     * @throws MigrationException if some exception occured during
     * the migration
     * @see com.sap.engine.services.appmigration.api.applications.MigrationContextIF
     */
    public int migrate(MigrationContextIF migrationContext)
        throws MigrationException;

     /**
      * Gets the component to which CSN messages will be
      * created when the execution of the migration module fails
      * Be sure to implement this method correctly, so the 
      * messages that will be created when problems with the
      * migration occur are correctly assigned!
      * 
      * @return the CSN component to which CSN messages will be 
      * created when problems occur.
      */
    public String getCSNComponent();

   
     /**
      * Gets the migration module name. It will be used for logging
      * purposes.
      * 
      * @return migration module name
      */
    public String getMigrationModuleName();    
    
      /**
       * Gets the migration module version as String
       * Used for logging purposes.
       * @return the migration module version
       */
    public String getMigrationModuleVersion();
    
    /**
     * Used to perform some final steps like freeing resources
     * Called after the migrate method is finished
     * 
     * @throws MigrationException if some exception occured 
     */
    public void terminate();    
   
}
