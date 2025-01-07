/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.logging;

/**
 * A class holding constants for error and warning messages used by the DS
 * logging.
 * //engine/j2ee.if/dev/src/container_api/_tc~je~container_api/java/com/
 * sap/engine/services/deploy/DeployResourceBundle.properties
 * 
 * @author Assia Djambazova
 */
public interface DSLogConstants {

	public static final String DEFAULT_LOG_CONSTANT = "deploy_0000";
	//
	// DS Log Error Constants
	//
	public static final String TIME_OUT = "deploy_6007";
	//
	// DS Log Warning Constants
	//
	public static final String PARENT_LOADER_DOESNT_EXISTS = "deploy_6123";
	public static final String INCOMPATIBLE_CONTAINER_INFO = "deploy_6125";
	public static final String INFO_NAME_DIFFERENT = "deploy_6126";
	public static final String INFO_SERVICE_NAME_DIFFERENT = "deploy_6127";
	public static final String INFO_MODULE_NAME_DIFFERENT = "deploy_6128";
	public static final String INFO_J2EEMUDULE_NAME_DIFFERENT = "deploy_6129";
	public static final String INFO_PRIORITY_DIFFERENT = "deploy_6130";
	public static final String INFO_CLASS_LOAD_PRIORITY_DIFFERENT = "deploy_6131";
	public static final String INFO_J2EE_CONTAINER_DIFFERENT = "deploy_6132";
	public static final String INFO_SUPPORT_SINGLE_FILE_UPDATE_DIFFERENT = "deploy_6133";
	public static final String INFO_SUPPORT_LAZY_START_DIFFERENT = "deploy_6134";
	public static final String INFO_SUPPORT_PARALLELISM_DIFFERENT = "deploy_6135";
	public static final String INFO_NEED_START_INITIALLY_DIFFERENT = "deploy_6136";
	public static final String INFO_FILE_EXTENSIONS_DIFFERENT = "deploy_6137";
	public static final String INFO_FILE_NAMES_DIFFERENT = "deploy_6138";
	public static final String INFO_RESOURCE_TYPES_DIFFERENT = "deploy_6139";
	public static final String INFO_SOFTWARE_TYPES_DIFFERENT = "deploy_6140";
	public static final String OBSOLETE_CONTAINER = "deploy_6141";
	public static final String CONDITIONAL_FILE_NAMES_DIFFERENT = "deploy_6142";
	public static final String SOFTWARE_SUB_TYPES_DIFFERENT = "deploy_6143";
	//
	// DS Log Debug Constants
	//
	public static final String START_SERVICE_AND_WAIT = "deploy_5206";
	public static final String ADDED_CONTAINER_FROM_XML_INFO = "deploy_5207";
	public static final String REMOVED_CONTAINER_FROM_XML_INFO = "deploy_5208";
}
