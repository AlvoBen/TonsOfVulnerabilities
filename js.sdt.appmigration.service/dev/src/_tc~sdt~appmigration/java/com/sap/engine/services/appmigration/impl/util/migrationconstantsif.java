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
package com.sap.engine.services.appmigration.impl.util;

import com.sap.tc.logging.Category;

/**
 * Description
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface MigrationConstantsIF
{
    public static final Category CATEGORY = 
        Category.getCategory(Category.SYS_SERVER, "/Migration");
    
    public static final Category MIGRATION_MODULE_CATEGORY = 
        Category.getCategory(Category.SYS_SERVER, "/AppMigration");
    public static final String MIGRATION_CONFIG_NAME = "migration_service"; 
    
    public static final String MIGRATION_BCCOMPDATA = "bccompdata";
    
    public static final String MIGRATION_MODULES = "modulesdata";
    
    public static final String MIGRATION_COMPVERS_COPY_NAME = 
    	MIGRATION_CONFIG_NAME + "/" + MIGRATION_BCCOMPDATA;

    public static final String MIGRATION_MODULES_CONFIG = 
    	MIGRATION_CONFIG_NAME + "/" + MIGRATION_MODULES;
   
    public static final String PRODUCTS = "products";
    
    public static final String SOURCE_PRODUCTS = PRODUCTS + "/" + "source";
    
    public static final String TARGET_PRODUCTS = PRODUCTS + "/" + "target";
    
    public static final String USAGES = "usages";
    
    public static final String SOURCE_USAGES = USAGES + "/" + "source";
    
    public static final String TARGET_USAGES = USAGES + "/" + "target"; 
    
    public static final String MIGRATION_CONTAINER_NAME = "MigrationContainer";
    
    public static final String ENGINE_VERSION_SOURCE = "ENGINEVERSION_SOURCE"; 
    
    public static final String ENGINE_VERSION_TARGET = "ENGINEVERSION_TARGET";
    
    public static final String APPLICATION_CONFIGURATION_STRING = "apps/sap.com";
    
    public static final String VENDOR_SAP_COM = "sap.com";
    
    public static final String RELEASE = "RELEASE";
    public static final String SP_LEVEL = "SP_LEVEL";
    public static final String PATCH = "PATCH";
    public static final String VENDOR_ENTRY = "VENDOR";
    //public static final String LOCATION = "LOCATION";
    public static final String COUNTER = "COUNTER";
   
    public static final String STATUS_ENTRY = "STATUS";
    public static final String STATUS_ENTRY_OK = "OK"; 
    public static final String STATUS_ENTRY_NOT_PASSED = "NOTPASSED"; 
    public static final String STATUS_ENTRY_FAILED = "FAILED";
    public static final String RESTART = "Started";
    public static final String STATUS_EXC_TEXT = "EXCEPTION_TEXT";
    public static final String MM_CSN_COMPONENT = "CSN_COMPONENT";

	public static final String UNDEPLOYED_FLAG = "UNDEPLOYED";
	public static final String YES_OPTION = "Yes";
	public static final String NO_OPTION = "No";
    
    /** version type UNKNOWN */                                             
    public static final int TYPE_FIRST   = 0;
    
    public static final int TYPE_UNKNOWN = TYPE_FIRST - 1;

    /** version type DECIMAL. F.e.: 10.1 or 1.2.3.4.5 */                                             
    public static final int TYPE_DECIMAL = TYPE_FIRST + 0;
    /** version type SAP ABAP system: F.e.: 46C or 310 */
    public static final int TYPE_SAP     = TYPE_FIRST + 1;
    /** version of a jdk: 1.3.1_07                     */
    public static final int TYPE_JAVA    = TYPE_FIRST + 2;
    
    public static final int TYPE_LAST    = TYPE_FIRST + 3;

    public static String ATTR_TYPE = "type";
 
    public static final String UNKNOWN_STR = "UNKNOWN";

    public static final String SAP_STR     = "SAP";
    public static final String DECIMAL_STR = "DECIMAL";
    public static final String JAVA_STR    = "JAVA";
}
