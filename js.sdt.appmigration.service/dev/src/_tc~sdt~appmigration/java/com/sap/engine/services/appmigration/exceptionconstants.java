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
package com.sap.engine.services.appmigration;
/**
 * This interface contains exception constants
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface ExceptionConstants
{
    // Cannot create subconfiguration for the migration container in config manager
    public static final String CANNOT_CREATE_SUBCONFIGURATION = "migration_0001"; 
    
    // Cannot create working directory for the migration container
    public static final String CANNOT_CREATE_WORKING_DIRECTORY = "migration_0002";
    
    // The file mapping for the archive is empty
    public static final String FILE_MAPPING_FOR_ARCHIVE_IS_EMPTY = "migration_0003";

    // It is not possible to update the database through the Config manager
    public static final String UNABLE_TO_UPDATE_DATABASE = "migration_0004";

    // It is not possible to find migration module for the archive
    public static final String UNABLE_TO_FIND_MIGRATION_MODULE = "migration_0005";
    
    // It is not possible to read the file from the archive
    public static final String UNABLE_TO_READ_FILE_FROM_ARCHIVE = "migration_0006";
    
    // There is an error while closing the archive
    public static final String ERROR_CLOSING_ARCHIVE = "migration_0007";
    
    // The migration main class name, defined in manifest is null or empty
    public static final String EMPTY_MIGRATION_MAIN_CLASS_NAME = "migration_0008";

    // The migration main class name, defined in manifest was not found in the jar
    public static final String MISSING_MIGRATION_MAIN_CLASS_NAME = "migration_0009";

    // IOException when copying file
    public static final String COPYING_FILE_EXCEPTION = "migration_0010";
    
    // Application has been successfully deployed
    public static final String APPLICATION_HAS_BEEN_SUCCESSFULLY_DEPLOYED = "migration_0011";
    
    // Cannot set the migrator
    public static final String CANNOT_SET_MIGRATOR = "migration_0012";
    
    // Cannot get the migration module class name from the config
    public static final String ERROR_GETTING_MIGRATION_MODULE_FROM_CONFIG = "migration_0013";
    
    // Error loading migration class from application classloader
    public static final String ERROR_LOADING_MIGRATION_CLASS_FROM_LOADER = "migration_0014";
    
    // Error invoking some of the migration module methods
    public static final String ERROR_INVOKING_MIGRATION_METHOD = "migration_0015";
    
    // Error accessing migration module class
    public static final String ERROR_ACCESSING_MIGRATION_CLASS = "migration_0016";
    
    // Error while loading migration module class
    public static final String ERROR_INSTANTIATING_MIGRATION_CLASS = "migration_0017";
    
    // The main module class does not implement ApplicationMigrationIF
    public static final String MIGRATION_CLASS_IMPLEMENTATION_ERROR = "migration_0018";
    
    // Error during migration
    public static final String ERROR_DURING_MIGRATION = "migration_0019";
    
    // Error storing information in Config
    public static final String ERROR_STORING_INFO_IN_CONFIG = "migration_0020";
    
    // Error getting configuration entry
    public static final String CANNOT_GET_CONFIG_ENTRY = "migration_0021";

    // Error setting configuration entry
    public static final String CANNOT_SET_CONFIG_ENTRY = "migration_0022";
    
    // The value for config entry is not correct
    public static final String INCORRECT_CONFIG_ENTRY_VALUE = "migration_0023";
    
    // Exception when trying to compare two component versions
    public static final String CANNOT_COMPARE_COMP_VERSIONS = "migration_0024";
    
    // Exception when trying to compare two versions and one of them is not instance of version object
    public static final String VERSION_CAST_EXCEPTION = "migration_0025";
    
    // The version number is null
    public static final String VERSION_NUMBER_NULL = "migration_0026";

    // The version number format is not correct
    public static final String VERSION_FORMAT_ERROR = "migration_0027";
    
    // Cannot get configuration handler
    public static final String CANNOT_GET_HANDLER_ON_PRINCIPLE = "migration_0028";
    
    // Configuration handler is null
    public static final String CONFIGURATION_NOT_AVAILABLE = "migration_0029";
    
    // Cannot get the root names from configuration
    public static final String CANNOT_GET_ALL_ROOT_NAMES = "migration_0030";
    
    // The configuration is locked
    public static final String CONFIGURATION_LOCKED_EXCEPTION = "migration_0031";
    
    // The configuration handler cannot be commited 
    public static final String CANNOT_COMMIT_CONFIGURATION = "migration_0032";
    
    // Cannot close configuration
    public static final String CANNOT_CLOSE_CONFIGURATION = "migration_0033";
    
    // Cannot get subconfiguration
    public static final String CANNOT_GET_SUBCONFIGURATION = "migration_0034";
    
    // Cannot open configuration
    public static final String CANNOT_OPEN_CONFIGURATION = "migration_0035";
    
    // Cannot create version
    public static final String CANNOT_CREATE_VERSION = "migration_0036";
    
    // Cannot create component element
    public static final String CANNOT_CREATE_COMPONENT = "migration_0037";    

    // Cannot set migration module initial values
    public static final String CANNOT_SET_INITIAL_VALUES = "migration_0038";
    
    // Cannot rollback transaction
    public static final String CANNOT_ROLLBACK = "migration_0039";
    
    //Cannot check subconfiguration existance 
    public static final String CANNOT_CHECK_FOR_SUBCONFIGURATION = "migration_0040";

    // Cannot delete configuration
    public static final String CANNOT_DELETE_CONFIGURATION = "migration_0041";
    
    // Cannot set the application to undeployed
    public static final String CANNOT_SET_APPLICATION_TO_UNDEPLOYED = "migration_0042";
}
