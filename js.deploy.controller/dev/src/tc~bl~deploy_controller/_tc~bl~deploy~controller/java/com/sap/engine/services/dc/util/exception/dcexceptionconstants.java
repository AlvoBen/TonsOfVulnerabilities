/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.util.exception;

/**
 * Mappes the exception constants to property key in the resource bundle.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class DCExceptionConstants {

	// ////////////////////
	// DEPENDENCIES //
	// ////////////////////
	//
	// Unresolved dependencies found for the following deployment items: {0}.
	public final static String DEPENDENCIES_UNRESOLVED = "dc_000";
	// Cyclic dependencies found for the following items: {0}.
	public final static String DEPENDENCIES_CYCLIC = "dc_001";

	// ///////////
	// XML //
	// ///////////
	//
	// An error occurred while parsing the {0}.
//	public final static String XML_WHILE_PARSING_THE = "dc_100";
	// An error occurred while initializing the xml parser.
//	public final static String XML_PARSER_INIT_ERROR = "dc_101";

	// ///////////
	// CMD //
	// ///////////
	//
	// Cannot register {0} commands for {1}.
	public final static String CMD_CANNOT_REGISTER = "dc_200";
	// The argument {0} cannot be mapped to any parameter of command {1}.
//	public final static String CMD_WRONG_ARGUMENT = "dc_201";
	// There is no {0} with value {1}. All possible values are {2}.
	public final static String CMD_WRONG_ARG_VALUE = "dc_202";
	// The parameter {0}, which is obligatory for command {1}, is missing.
	public final static String CMD_MISSING_ARGUMENT = "dc_203";
	// Cannot build parameter property, because key={0}, spplitter={1} and
	// value={2}.
	public final static String CMD_CANNOT_BUILD_PARAM_PROPERTY = "dc_204";
	// Cannot use parameter {0} and {1} together in command {2}.
	public final static String CMD_CANNOT_USER_TWO_PARAMS_TOGETHER = "dc_205";
	// Cannot use parameter {0} without use paarmeter {1} in command {2}.
	public final static String CMD_MUST_USE_TWO_PARAMS_TOGETHER = "dc_206";
	// No argument specified.
	public final static String CMD_NO_ARGUMENT_SPECIFIED = "dc_207";
	// No file(dir) or list argument specified.
	public final static String CMD_NO_FILE_OR_LIST_ARG_SPEC = "dc_208";
	// Not specified {0} or {1} and {2} arguments.
	public final static String CMD_NOT_SPECIFIED_OR_AND_ARGS = "dc_209";

	// /////////////
	// GRAPH //
	// /////////////
	//
	// dc_300=Cycle found.
	public final static String GRAPH_CYCLE_FOUND = "dc_300";

	// ///////////////
	// GENERAL //
	// ///////////////
	//
	// The specified file {0} does not exist.
	public final static String FILE_NOT_FONUD = "dc_400";
	// The specified file {0} is not valid.
	public final static String FILE_NOT_VALID = "dc_401";
	// An error occurred while creating a {0}.
	public final static String ERROR_CREATING = "dc_402";
	// An error occurred while getting the {0}.
	public final static String ERROR_GETTING = "dc_403";
	// Cannot {0} configuration handler.
	public final static String CANNOT_CFG_HANDLER = "dc_404";
	// Cannot {0} configuration with path {1}.
	public final static String CANNOT_CFG = "dc_405";
	// The argument {0} is null.
	public final static String NULL_ARG = "dc_406";
	// The specified file {0} is not valid. Additional information: '{1}'.
	public final static String FILE_NOT_VALID_ADD_INFO = "dc_407";
	// Cannot create {0} using {1}.
	public final static String CANNOT_CREATE_X_USING_Y = "dc_408";
	// Cannot create {0}.
	public final static String CANNOT_CREATE_X = "dc_409";
	// Cannot close {0}.
	public final static String CANNOT_CLOSE_X = "dc_410";
	// The specified file {0} is could not be read.
	public final static String FILE_NOT_READABLE = "dc_411";
	// The specified file {0} is not recognized as a file.
	public final static String FILE_IS_NOT_FILE = "dc_412";
	// Cannot {0} database connection.
	public final static String CANNOT_DB_CONN = "dc_413";
	/**
	 * Deploy controller is not ready to serve yet. Probably due to repository
	 * initialization or un/deploy operation after offline phase is performing
	 * at the moment.
	 */
	public final static String DC_NOT_AVAILABLE_YET = "dc.ex.414";

	// ///////////
	// SDU //
	// ///////////
	//
	// An error occurred while validating the archive '{0}'.
	public final static String SDU_INVALID = "dc_500";
	// The sca could not be extracted. {0} while building member location.
	public final static String SDU_EXTRACT_ERROR_MEMEBER_LOCATION = "dc_501";
	// The sca could not be extracted. An error occurred extracting archive {0}.
	public final static String SDU_EXTRACT_ERROR = "dc_502";
	// The sca could not be extracted. The archive is is not correct.
	public final static String SDU_EXTRACT_ERROR_ARCHIVE = "dc_503";
	// An error occurred while mapping the SDU location to a deployment batch
	// item.
	public final static String SDU_MAPPING_ERROR = "dc_504";
	// The sdu repo location is {0}. The sdu storage location is {1}.
	// They are equals, which is wrong.
	public final static String SDU_LOCATION_ERROR = "dc_505";

	// /////////////
	// CVERS //
	// /////////////
	//
	// An error occurred while updating the {0} with information about the
	// component '{1}'.
	public final static String CVERS_ERROR_UPDATE = "dc_600";
	// An error occurred while removing from the {0} the information about the
	// component '{1}'.
	public final static String CVERS_ERROR_REMOVE = "dc_601";
	// An error occurred while initializing the component element from the
	// string '{0}'.
	public final static String CVERS_ERROR_INIT = "dc_602";

	// ////////////////
	// JSTARTUP //
	// ////////////////
	//  
	// An error occurred while loading the property file '{0}'.
	public final static String JSTARTUP_LOAD_PROPS = "dc_700";
	// JStartup Framework error occurred while restarting the cluster by using
	// the message server host '{0}' and port '{1}'.
	public final static String JSTARTUP_RESTART_CLUSTER_ERROR = "dc_701";
	// JStartup Framework error occurred while creating JStartupInitialFactory.
	public final static String JSTARTUP_INIT_FACTORY_ERROR = "dc_702";
	// IO error occurred while creating JStartupClusterController.
	public final static String JSTARTUP_CLUSTER_CTRL_ERROR = "dc_703";
	// Error occurred while restarting the instance.
	public final static String JSTARTUP_RESTART_INSTANCE_ERROR = "dc_704";

	// //////////////////
	// SERVER&SPI //
	// //////////////////
	//  
	// An error occurred while restarting the Application Server Java.
	public final static String SERVER_RESTART_SERVICE_ERROR = "dc_800";
	// An error occurred while loading the property file ''{0}''.
	public final static String SERVICE_LOAD_PROPS_ERROR = "dc_801";
	// The name/value pair '{0}' is in the correct format. The right format is
	// 'name/value'.
	public final static String UNSUPPRT_COMP_SERVICE_WRONG_PROPS_ERROR = "dc_802";
	// The Deploy Controller is not able to restart the instance for ''{0}''
	// milliseconds. The reason is that a timeout occurred while waiting for the
	// restart. Please, contact the SAP AS Java cluster administrator in order
	// to find out the reason.
	public final static String SERVER_NOT_ABLE_TO_RESTART_INST = "dc_803";
	// The Deploy Controller is not able to restart the cluster for ''{0}''
	// milliseconds. The reason is that a timeout occurred while waiting for the
	// restart. Please, contact the SAP AS Java cluster administrator in order
	// to find out the reason.
	public final static String SERVER_NOT_ABLE_TO_RESTART = "dc_804";

	// ////////////
	// REPO //
	// ////////////
	//
	// Cannot persist {0} information in location {1}.
	public final static String REPO_CANNOT_PERSIST = "dc_900";
	// Cannot load to {0} information from location {1}.
	public final static String REPO_CANNOT_LOAD_TO = "dc_901";
	// Cannot load location {0}.
	public final static String REPO_CANNOT_LOAD = "dc_902";
	// Cannot load all SDUs from configuration {0}.
	public final static String REPO_CANNOT_LOAD_ALL = "dc_903";
	// Cannot delete {0}.
	public final static String REPO_CANNOT_DELETE = "dc_904";
	// Cannot load archive for location ''{0}'' as it is not stored in
	// repository.
	public final static String REPO_CANNOT_LOAD_ARCHIVE_AS_NOT_STORED = "dc_905";
	// An error occur while reading 'description' from configuration: {0}
	public final static String REPO_ERROR_OCCUR_WHILE_READ_DD_DESCR = "dc_906";
	// An error occur while reading '{0}' from configuration: {1}
	public final static String REPO_ERROR_OCCUR_WHILE_READ_DBI_DESCR = "dc_907";
	// The deployments container has already been initialized.
	public final static String REPO_ERROR_DPL_CONTAINER_ALREADY_INIT = "dc_908";
	// [ERROR CODE DPL.DC.3072] An error occurred while persisting sca.
	public final static String REPO_ERROR_WHILE_PERSISTING_SCA = "dc_909";
	// [ERROR CODE DPL.DC.3471] An error occurred while cloning repository.
	public final static String REPO_ERROR_WHILE_CLONING_REPOSITORY = "dc_910";

	// /////////////////////
	// OFFLINE_PHASE //
	// /////////////////////
	//
	// An error occurred while loading the deployment data in order to perform
	// the offline deployment.
	public final static String OFFLINE_PHASE_LOAD_DEPL_DATA_ERROR = "dc_1000";
	// An error occurred by the time of offline phase deployment while
	// persisting the deployment batch item '{0}'.
	public final static String OFFLINE_PHASE_PERSIST_DEPL_ITEM_ERROR = "dc_1001";
	// An error occurred while loading the undeployment data in order to perform
	// the offline undeployment.
	public final static String OFFLINE_PHASE_LOAD_UNDEPL_DATA_ERROR = "dc_1002";
	// An error occurred by the time of offline phase undeployment while
	// persisting the undeployment batch item '{0}'.
	public final static String OFFLINE_PHASE_PERSIST_UNDEPL_ITEM_ERROR = "dc_1003";
	// The DIR_EXECUTABLE value is empty or null.
	public final static String WRONG_DIR_EXECUTABLE = "dc_1004";

	// //////////////
	// DEPLOY //
	// //////////////
	//  
	// An error occurred in the online deployment phase by the time of post
	// processing the deployment data.
	public final static String DEPLOY_OFFLINE_POST_PROCESS_STORAGE_ERROR = "dc_1100";
	// Rolling deploy is not allowed.
	public static final String OFFLINE_ROLLING_DEPLOY_ERROR = "dc_1101";
	// An error occurred during the deployment data persistence.
	public final static String DEPLOYMENT_DATA_PERSISTENCE_ERROR = "dc_1102";
	// Error during deserialization of SyncRequest object from configuration:
	// {0}
	public final static String SYNC_REQUEST_DESERIALIZATION_ERROR = "dc_1103";
	// Error during serialization of SyncRequest object to configuration: {0}
	public final static String SYNC_REQUEST_SERIALIZATION_ERROR = "dc_1104";
	// Error during deserialization of LockData object from configuration: {0}
	public final static String LOCK_DATA_DESERIALIZATION_ERROR = "dc_1105";
	// Error during serialization of LockData:
	public final static String LOCK_DATA_SERIALIZATION_ERROR = "dc_1106";

	// //////////
	// GenericDelivery //
	// //////////
	//
	// An error occurred during online deployment while processing references of
	// component {0}.
	public final static String PROCESS_REFERENCES = "dc_1200";
	// An error occurred while getting Deploy Service during online deployment.
	public static final String GET_DS = "dc_1201";
	// The component {0} cannot be deployed because it has a version status NOT
	// RESOLVED.
	public static final String NOT_RESOLVED = "dc_1202";
	// An error occurred during deployment of {0}. The DC name cannot be empty.
	public static final String DC_EMPTY = "dc_1203";
	// An error occurred during deployment of {0}. The vendor name cannot be
	// empty.
	public static final String VENDOR_EMPTY = "dc_1204";
	// An error occurred during deployment of {0}. Cannot unregister old
	// references.
	public static final String UNREGISTER_REFS = "dc_1205";
	// An error occurred during deployment of {0}. Cannot register new
	// references.
	public static final String REGISTER_REFS = "dc_1206";
	// An error occurred during deployment of {0}. Cannot deploy it.
	public static final String DEPLOY = "dc_1207";
	// An error occurred during deployment of {0}. Cannot update it.
	public static final String UPDATE = "dc_1208";
	// An error occurred during deployment of {0}. Cannot start it.
	public static final String START_APP = "dc_1209";
	// An error occurred during deployment of {0}. Cannot get application
	// status.
	public static final String GET_APP_STATUS = "dc_1210";
	// An error occurred during undeployment of {0}.
	public static final String REMOVE = "dc_1211";
	// Cannot deliver the component, because its software type {0} is not
	// recognized by Generic Delivery module.
	public static final String SW_TYPE_UNRECOGNIZED = "dc_1212";
	// The specified deployment item could not be null.
	public static final String NULL_ITEM = "dc_1213";
	// Rolling deploy is not allowed.
	public static final String ROLLING_DEPLOY_ERROR = "dc_1214";
	// Rolling undeploy is not allowed.
	public final static String ROLLING_UNEPLOY_ERROR = "dc_1215";
	// Rolling update of {0} failed due to: {1}
	public final static String ROLLING_UPDATE_ERROR = "dc_1216";
	// An error occurred while getting Deploy Service Rolling Patcher during
	// online deployment.
	public static final String GET_DSRP = "dc_1217";
	// An error occurred while getting Core Rolling Patcher during online
	// deployment.
	public static final String GET_CORERP = "dc_1218";
	// {0} is not supported.
	public static final String WRONG_DELIVERY_TYPE = "dc_1219";
	// Rolling update of component: {0} FAILED.
	public static final String ROLLING_UPDATE_FAILED = "dc_1220";
	// Rolling sync failed: SC cannot be synced.
	public static final String ROLLING_SC_SYNC = "dc_1221";
	// Rolling sync of component: {0} FAILED.
	public static final String ROLLING_SYNC_FAILED = "dc_1222";
	// Rolling sync of component: {0} FAILED. Cluster descriptor is missed.
	public static final String ROLLING_MISSED_CLUSTER_DESC = "dc_1223";
	// Rolling sync of component: {0} FAILED. Rolling info is missed.
	public static final String ROLLING_MISSED_ROLLING_INFO = "dc_1224";

	// ////////////////
	// UNDEPLOY //
	// ////////////////
	//
	// An error occurred while checking whether the component with name '{0}'
	// and vendor '{1}' is allowed to be undeployed.
	public final static String UNDEPL_SUPPORT_CHECK_ERROR = "dc_1300";
	// The system requested server service of type
	// 'UnsupportedUndeployComponentsService', but received '{0}'.
	public final static String UNDEPL_SUPPORT_CHECK_SERVICE_ERROR = "dc_1301";
	// An error occurred in the online undeployment phase by the time of post
	// processing the undeployment data.
	public final static String UNEPLOY_OFFLINE_POST_PROCESS_STORAGE_ERROR = "dc_1302";

	// //////////////////
	// SESSION_ID //
	// //////////////////
	//  
	// Cannot generate session ID. Please check the caused by exceptions.
	public final static String SESSION_ID_CANNOT_GENERATE = "dc_1400";
	// Cannot generate session ID for {0} ms, because there are too much request
	// for session ID generation. Please try later.
	public final static String SESSION_ID_GENERATION_TIMED_OUT = "dc_1401";
	// Session ID configuration or entry is missing.
	public final static String MISSING_SESSION_ID_CFG_OR_ENTRY = "dc_1402";

	// ///////////////////////
	// DATA_STORAGE_GC //
	// ///////////////////////
	//  
	// Cannot perform data storage garbage collection for configuration {0}.
	public final static String GC_CANNOT_PERFORM = "dc_1500";
	// Cannot parse the Date stored for configuration {0} creation.
	public final static String GC_CANNOT_PARSE_CREATION_DATE = "dc_1501";
	// Cannot get Date from DataBase.
	public final static String GC_CANNOT_DATE_FROM_DB = "dc_1502";
	// Cannot garbage collect data because of problem with getting active
	// session id.
	public final static String GC_CANNOT_GC_DATA_BECAUSE_SESSION_ID = "dc_1503";
	// Cannot list files in upload directory [{0}]
	public final static String GC_CANNOT_GC_FILES_BECAUSE_IOEX = "dc_1504";

	// //////////////////////
	// SERVER VERSION //
	// //////////////////////
	//   
	// Cannot read all SDUs from the repository of the deploy controller.
	public final static String SER_VER_CANNOT_READ_ALL_SDUS = "dc_1600";
	// ######################
	// # SERVER CONFIGURER #
	// ######################
	// #
	// Cannot get service interface ''{0}'' from the component registry.
	public final static String CANNOT_GET_SRVC_INTRFC_FROM_REG = "dc_1700";

	public final static String UNDEPLOY_VALIDATION_ERROR = "dc_1800";
	
	// ///////////////////////////////////////////////////
	// More Exception messages - to be sorted in groups //
	// ///////////////////////////////////////////////////

	
}
