/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.timestat;

/**
 * This interface provides constants for various known time statistic node names
 * and table entiries
 * 
 * @author Todor Stoitsev
 * 
 */
public interface ITimeStatConstants {

	/**
	 * General table entries describing the statistics content.
	 */
	public static final String TIME_STATISTICS_HEADER = "Time statistics for ";
	public static final String APP_NAME = "Application Name ";

	/**
	 * General entries
	 */
	public static final String DURATION = "duration";
	public static final String RATIO = "ratio";
	public static final String MS = "ms";
	public static final String OBJ = "obj";
	public static final String B = "b";
	public static final String CPU_DURATION = "CPU time";
	public static final String ALLOCATED_MEMORY = "Allocated Memory";
	public static final String FREED_MEMORY = "Freed Memory";
	public static final String HOLD_MEMORY = "Hold Memory";

	/**
	 * Constants for known node names
	 */
	public static final String FULL_OPERATION_TIME = "Full operation time";
	public static final String DEPLOY_SERVICE = "Deploy Service";
	public static final String CLUSTER_COMMUNICATION_DURATION = "Cluster Communication Duration";
	public static final String OTHER = "Other";
	public static final String JLIN_EE = "JLinEE";
	public static final String JLIN_EE_PREPROCESS_DURATION = "preprocess";
	public static final String JLIN_EE_VALIDATION_DURATION = "validation duration";
	public static final String PROT_DOM = "ProtectionDomain";
	public static final String PROT_DOM_GET_FACTORY = "getFactory";
	public static final String PROT_DOM_REMOVE_STORED_PROT_DOMS = "removeStoredProtectionDomains";
	public static final String APP_MNG_OBJ = "ApplicationManagedObject";
	public static final String APP_MNG_OBJ_REG = "register";
	public static final String APP_MNG_OBJ_UNREG = "unregister";
	public static final String IOs = "OIs";
	public static final String IOs_Delete = "delete";
	public static final String CFG_MNG = "ConfigurationManager";
	public static final String EAR_READ = "EARReader";
	public static final String CLEAR_FS = "ClearFS";
	public static final String CFG_MNG_OPEN_HANDLER = "getHandler";
	public static final String CFG_MNG_COMMIT_HANDLER = "commitHandler";
	public static final String EAR_READ_GET_DESCRIPTOR = "getDescriptor";
	public static final String EAR_READ_CLEAR = "clear";
	public static final String CLEAR_FS_CLEAN_UP_DEPLOYMENT_FILE = "cleanUpDeploymentFile";
	public static final String CFG_MNG_ROLLBACK_HANDLER = "rollbackHandler";
	public static final String CFG_MNG_DELETE_CFG = "deleteConfiguration";
	public static final String ALL_CONTAINERS_DURATION = "All containers duration";
	public static final String CREATE_CLASSLOADER = "Create classloader";
	public static final String UNREGISTER_CLASSLOADER = "Unregister classloader";	
}
