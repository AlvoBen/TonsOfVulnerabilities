/*
 * Created on 2005-5-3 by radoslav-i
 */
package com.sap.engine.services.dc.util.logging;

/**
 * @author radoslav-i
 */
public abstract class DCLogConstants {

	// ////////////////////
	// DEPENDENCIES //
	// ////////////////////
	//

	// ///////////
	// XML //
	// ///////////
	//

	// ///////////
	// CMD //
	// ///////////
	//
	// The telnet commands of DeployController will not be registered, because
	// the 'shell' is NULL.
	public final static String CMD_TELNET_CMDS_WILL_NOT_BE_REGISTERED = "dc_200";

	// ///////////////
	// GENERAL //
	// ///////////////
	//

	// A runtime error/exception will leave deploy controller {0}
	public static final String RUNTIME_ERROR_WILL_LEAVE_DC = "dc_300";

	// File already exists: [{0}]
	public static final String FILE_ALREADY_EXISTS = "dc_301";

	// Cannot remove file: [{0}]
	public static final String CANNOT_REMOVE_FILE = "dc_302";

	// File does not exist: [{0}]
	public static final String FILE_DOES_NOT_EXIST = "dc_303";

	// File removed successfully: [{0}]
	public static final String FILE_REMOVED_SUCCESSFULY = "dc_304";
	// ///////////
	// SDU //
	// ///////////
	//

	// /////////////
	// CVERS //
	// /////////////
	//

	// ////////////////
	// JSTARTUP //
	// ////////////////
	//  
	// Getting the JStartup Cluster Controller ...
	public final static String JSTARTUP_GETTING_CLUSTER_CONTROLLER = "dc_700";
	// Invoke the JStartup Cluster Controller instant restart operation ...
	public final static String JSTARTUP_INVOKE_INSTANT_RESTART = "dc_701";
	/**
	 * Instant restart operation has been triggered
	 */
	public final static String JSTARTUP_INSTANT_RESTART_TRIGGERED = "dc_702";

	// //////////////////
	// SERVER&SPI //
	// //////////////////
	//
	// The Deploy Controller is not able to restart the cluster for ''{0}''
	// milliseconds. The reason is that a timeout occurred while waiting for the
	// restart. Please, contact the SAP AS Java cluster administrator in order
	// to find out the reason.
	public final static String SERVER_NOT_ABLE_TO_RESTART = "dc_800";
	/**
	 * After restart cluster is still running. Waiting ''{0}'' milliseconds for
	 * the restart process ...
	 */
	public final static String SERVER_AFTER_PERFORMED_RESTART_CLUSTER_STILL_RUNNING = "dc_801";
	// Executing the command: {0}
	/**
	 * @deprecated
	 */
	public final static String SERVER_EXECUTING_CMD = "dc_803";
	// The working directory is: {0}
	public final static String SERVER_WORKING_DIR_IS = "dc_804";
	// set server mode result: {0}
	public final static String SERVER_SET_SERVER_MODE_RESULT = "dc_805";
	// The JStartup Cluster Manager will be initialized for the ms host ''{0}''
	// and port ''{1}''.
	public final static String SERVER_JSTARTUP_MANAGER_WILL_BE_INITIALLIZED_ON = "dc_806";
	// Set server in ''{0}'' , performerId ''{1}''
	public final static String SERVER_SET_SERVER_MODE = "dc_807";
	// The Deploy Controller is not able to restart the instance for ''{0}''
	// milliseconds. The reason is that a timeout occurred while waiting for the
	// restart. Please, contact the SAP AS Java cluster administrator in order
	// to find out the reason.
	public final static String SERVER_NOT_ABLE_TO_RESTART_INST = "dc_808";
	// After restart instance is still running. Waiting ''{0}'' milliseconds for
	// the restart process ...
	public final static String SERVER_AFTER_PERFORMED_RESTART_INSTANCE_STILL_RUNNING = "dc_809";
	// Store the server mode [{0} mode , action {1}]
	public final static String SERVER_STORE_SERVER_MODE = "dc_810";
	// Restore the server mode [{0} mode , action {1}]
	public final static String SERVER_RESTORE_SERVER_MODE = "dc_811";

	// ////////////
	// REPO //
	// ////////////
	//
	// No information about the SDU corresponding to the repository location
	// ''{0}''. The impact is that the just added SDU will not be registered
	// into the runtime Repository Container.
	public final static String NO_INFO_ABOUT_SDU_FOR_LOCATION__IMPACT_ADD = "dc_900";
	// No information about the SDU corresponding to the repository location
	// ''{0}''. The impact is that the just modified SDU will not be registered
	// into the runtime Repository Container.
	public final static String NO_INFO_ABOUT_SDU_FOR_LOCATION__IMPACT_MODIFIED = "dc_901";
	// Starting to persist the sdu {0}
	public final static String REPO_STARTING_TO_PERSIST_SDU = "dc_902";
	// Persisting the sdu data into the DB
	public final static String REPO_PERSISTING_SDU_DATA_IN_DB = "dc_903";
	// The sdu ''{0}'' was persisted into the repository
	public final static String REPO_SDU_WAS_PERSISTED = "dc_904";
	// The ''{0}'' will be corrected. It is ''{1}'' and contains charactes which
	// are not allowed from the system. The corrected {2} is ''{3}''.
	public final static String REPO_WILL_BE_CORRECTED_IT_IS_ = "dc_905";
	// [ERROR CODE DPL.DC.3294] Fatal error occurred while loading the
	// repository deployments data. The system will not be able to serve
	// requests
	public final static String ERROR_LOADING_REPOSITORY_DATA = "dc_906";

	// /////////////////////
	// OFFLINE_PHASE //
	// /////////////////////
	//
	// Offline Phase Undeploy - the item ''{0}'' is persisted into the storage.
	public final static String OFFLINE_PHASE_UNDEPLOY_ITEM_IS_PERSISTED = "dc_1000";
	// Restarting the SAP Application Server Java ...
	public final static String OFFLINE_RESTARTING_ENGINE = "dc_1001";
	// Setting the SAP Application Server Java in {0}.
	public final static String OFFLINE_SET_ENGINE_IN_MODE = "dc_1002";
	// Offline Phase Undeploy - start undeploying the component ''{0}''.
	public final static String OFFLINE_START_UNDEPLOY_COMPONENT = "dc_1003";
	// Offline Phase Undeploy processing the item ''{0}'.
	public final static String OFFLINE_PROCESSING_ITEM = "dc_1004";
	// Offline Phase Undeploy - the undeployment of the component ''{0}''
	// finished with {1}
	public final static String OFFLINE_UNDEPLOY_FINISHED = "dc_1005";
	// Starting the Offline Phase of the Deploy Controller.
	public final static String OFFLINE_STARTING_OFFLINE_PHASE_OF_DC = "dc_1006";
	// The Deploy Controller has been run on cluster instance which is not the
	// one where the (un)deployment has been triggered.
	public final static String OFFLINE_DC_RUN_ON_CLUSTER_WHICH_ = "dc_1007";
	// Starting offline phase deployment ...
	public final static String OFFLINE_STARTING_OFFLINE_PHASE_DEPLOYMENT = "dc_1008";
	// The offline phase deployment ended.
	public final static String OFFLINE_PHASE_DEPLOYMENT_ENDED = "dc_1009";

	// String offline phase undeployment ...
	public final static String OFFLINE_STRING_OFFLINE_PHASE_UNDEPLOYMENT = "dc_1010";
	// The offline phase undeployment ended
	public final static String OFFLINE_PHASE_UNDEPL_ENDED = "dc_1011";
	// The Deploy Controller will not perform any deploy or undeploy operations.
	public final static String OFFLINE_DC_WILL_NOT_PERFORMED_ANY_OPERS = "dc_1012";
	// The jar {0} will be added to the loaded resources.
	public final static String LOAD_ADD_JAR = "dc_1013";
	// The additional jars were loaded.
	public final static String ADD_JAR_LOADED = "dc_1014";
	// The current classloader {0} is not an instance of {1}. The specified jars
	// will not be loaded.
	public final static String WRONG_CL = "dc_1015";
	// The classloader {0} could not be loaded. The specified jars will not be
	// loaded.
	public final static String NO_OFFLINE_FILE_CL = "dc_1016";
	// The cluster instance performer id could not be got.Reason: {0}
	public final static String OFFLINE_DC_CANNOT_GET_PERFORMER_ID = "dc_1017";

	// //////////////
	// DEPLOY //
	// //////////////
	//  
	// The following dependencies could not be resolved: {0}({1}).
	public final static String DEPLOY_FOLLOWING_DEPENDENCIES_NOT_RESOLVED = "dc_1100";
	// The specified software type ''{0}'' is not mapped to a dependency
	// visibility resolver.
	public final static String DEPLOY_SOFTWARE_TYPE_NOT_MAPPED_TO_RESOLVER = "dc_1101";
	// Unknown deployment batch item status ''{0}'' detected for the item
	// ''{1}'!
	public final static String DEPLOY_UKNOWN_DEPLOYMENT_BATCH_ITEM_STATUS = "dc_1102";
	// There is an archive path [{0}] which is null.
	public final static String DEPLOY_ARCHIVE_PATH_IS_NULL = "dc_1103";
	// Entered illegal state while checking the deploy status of the deployment
	// batch item!
	public final static String DEPLOY_ENTERED_ILLEGAL_STATE = "dc_1104";
	// An error occurred while deploying the composite deployment item ''{0}'.
	public final static String DEPLOY_ERROR_WHILE_DEPLOY_COMPOSITE_ITEM = "dc_1105";
	// Unknown deployment item status ''{0}'' detected for the item ''{1}'!
	public final static String DEPLOY_UKNOWN_DEPLOYMENT_ITEM_STATUS = "dc_1106";
	// Entered illegal state while setting the deploy status for the composite
	// deployment item ''{0}'!"
	public final static String DEPLOY_ILLEGAL_STATE_WHILE_SETTING_STATUS_FOR_COMPOSITE_ITEM = "dc_1107";
	// Received ServerService for determining online and offline software types
	// that is no SoftwareTypeService.
	public final static String DEPLOY_RECEIVED_SERVER_SERVICE_IS_NOT_SFTWR_TYPE = "dc_1108";
	// Deployment Item graph contains cycles ({0}).
	public final static String DEPLOY_ITEM_GRAPH_CONTAINS_CYCLES = "dc_1109";

	// Composite items graph contains cycles ({0}).
	public final static String DEPLOY_COMPOSITE_ITEM_GRAPH_CONTAINS_CYCLES = "dc_1110";
	// Offline Phase Deployer - the item ''{0}'' is persisted into the storage.
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_ITEM_IS_PERSISTED = "dc_1111";
	// Setting the SAP Application Server Java in {0}
	public final static String DEPLOY_SETTING_ENGINE_IN = "dc_1112";
	// Offline Phase Deployer - Starting JDDI deployment for the component
	// ''{0}'.
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_STARTTING_JDDI_DEPLOY = "dc_1113";
	// Offline Phase Deployer - Starting AS Java offline deployment for the
	// component ''{0}'.
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_STARTING_ENGINE_OFFLINE_DEPL_FOR_COMP = "dc_1115";
	// Offline Phase Deployer - The deployment of the component ''{0}'' finished
	// with {1}
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_DEPL_FINISHED_OF_COMPONENT_WITH = "dc_1116";
	// Offline Phase Deployer - the jdd component ''{0}'' was successfully
	// deployed.
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_JDD_COMPONENT_SUCCESSFULLY_DEPLOYED = "dc_1117";
	// Offline Phase Deployer - the dbsc component ''{0}'' was successfully
	// deployed.
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_DBSC_COMPONENT_SUCCESSFULLY_DEPLOYED = "dc_1118";
	// Offline Phase Deployer - Starting DBSC deployment for the component
	// ''{0}'.
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_STARTING_DBSC_DEPLOYMENT_FOR = "dc_1119";

	// Offline Phase Deployer - the component ''{0}'' is persisted into the
	// storage.
	public final static String DEPLOY_OFFLINE_PHASE_DEPLOYER_COMPONENT_IS_PERSISTED = "dc_1120";
	// Restarting the SAP Application Server Java ...
	public final static String DEPLOY_RESTARTING_ENGINE = "dc_1121";
	// Due to version check deployment status of component ''{0}'' is set to
	// ''{1}'.
	public final static String DEPLOY_DUE_TO_VERSION_CHECK_STATUS_IS_SET_TO = "dc_1122";
	// Starting the sequential delivery ...
	public final static String DEPLOY_STARTING_SEQUENTIAL_DELIVERY = "dc_1123";
	// Delivery finished.
	public final static String DEPLOY_DELIVERY_FINSHED = "dc_1124";
	// Starting to notify the deploy observers
	public final static String DEPLOY_STARTING_TO_NOTIFY_OBSERVERS = "dc_1125";
	/**
	 * Observers has been notified.Component:{0}.
	 */
	public final static String DEPLOY_OBSERVERS_NOTIFIED = "dc_1126";
	// Start loading archives ...
	public final static String DEPLOY_START_LOADING_ARCHIVES = "dc_1127";
	// {0} [{1}] was loaded from location [{2}].
	public final static String DEPLOY_ARCHIVE_IS_LOADED = "dc_1128";
	// ''{0}'' archive(s) loaded.
	public final static String DEPLOY_ARCHIVES_ARE_LOADED = "dc_1129";

	// Persisting the component ''{0}'' into the temporary deployment data
	public final static String DEPLOY_PERSISTING_COMPONENT_INTO_TEMP_DEPLOY_DATA = "dc_1130";
	// The component ''{0}'' is stored into the repository by the {1}
	public final static String DEPLOY_COMPONENT_IS_STORED_INTO_REPO_BY = "dc_1131";
	// Apply the prerequisites checks ...
	public final static String DEPLOY_APPLY_PREREQUISITES_CHECKS = "dc_1132";
	// The prerequisites checks ended.
	public final static String DEPLOY_PREREQUISITES_CHECKS_ENDED = "dc_1133";
	// Registering the offline deployed component {0}
	public final static String DEPLOY_REGISTERING_OFFLINE_DEPLOYED_COMPONENT = "dc_1134";
	// The component ''{0}'' has been registered
	public final static String DEPLOY_COMPONENT_HAS_BEEN_REGISTERED = "dc_1135";
	// The development component is an offline one, therefore only its status is
	// changed to {0}
	public final static String DEPLOY_DEVELOPMENT_COMPONENT_IS_OFFLINE_ = "dc_1136";
	// The software component is an offline one, therefore only its status is
	// changed to {0}
	public final static String DEPLOY_SOFTWARE_COMPONENT__IS_OFFLINE_ = "dc_1137";
	// The Deploy Controller is in ''{0}'' state. The DB lock is not going to be
	// unlocked.
	public final static String DEPLOY_DC_IS_IN_STATE_DB_WILL_NOT_BE_UNLOCKED = "dc_1138";
	// Unlocking the db ...
	public final static String DEPLOY_UNLOCKING_DB = "dc_1139";

	// Start the offline deployment post processing logic ...
	public final static String DEPLOY_START_OFFLINE_DEPLOYMENT_POST_ = "dc_1142";
	// Persisting the temporary deployment data ...
	public final static String DEPLOY_PERSISTING_TEMP_DEPL_DATA = "dc_1143";
	// Locking the db ...
	public final static String DEPLOY_LOCKING_DB = "dc_1144";
	// The system removes the deployment item ''{0}'' from the composite
	// deployment item ''{1}'' because the SDA ('{2}') to which it refers is
	// already added with another deployment item to the deployment batch!
	public final static String DEPLOY_SYSTEM_REMOVES_DEPL_ITEM_ = "dc_1145";
	// There are no batch filters specified.
	public final static String DEPLOY_THERE_ARE_NO_BATCH_FILTERS_SPEC = "dc_1146";
	// Filter: {0}
	public final static String DEPLOY_FILTER = "dc_1147";
	// The item ''{0}'' is filtered. Therefore it is not going to be deployed.
	public final static String DEPLOY_ITEM_IS_FILTERED = "dc_1148";

	// Apply the deployment batch filters ...
	public final static String DEPLOY_APPLY_DEPLOYMENT_BATCH_FILTERS = "dc_1150";
	// The batch filters are applied.
	public final static String DEPLOY_BATCH_FILTERS_ARE_APPLIED = "dc_1151";
	// The Deploy Controller is in ''{0}'' state. The deployment batch files are
	// not going to be deleted.
	public final static String DEPLOY_DC_IS_IN_STATE_BATCH_FILES_WILL_NOT_BE_DEL = "dc_1152";
	// Starting to delete the directory ''{0}'.
	public final static String DEPLOY_STARTING_TO_DELETE_DIR = "dc_1153";
	// The directory ''{0}'' was successfully deleted.
	public final static String DEPLOY_DIR_WAS_SUCCESSFULLY_DELETED = "dc_1154";
	// Starting validation: {0}
	public final static String DEPLOY_STARTING_VALIDATION = "dc_1155";
	// Validation end: {0}
	public final static String DEPLOY_VALIDATION_END = "dc_1156";
	// Sorting the deployment items ...
	public final static String DEPLOY_SORTING_DEPLOYMENT_ITEMS = "dc_1157";
	// Resolving the dependencies ...
	public final static String DEPLOY_RESOLVING_DEPENDENCIES = "dc_1158";
	// +++++++ Deploying [{0}] +++++++
	public final static String DEPLOY_STARTING_TO_DEPLOY = "dc_1159";

	// {0}
	public final static String DEPLOY_A_VALUE = "dc_1160";
	/**
	 * +++++++ Deployment for item ''{0}'' finished with ''{1}'' for ''{2}'' ms
	 * +++++++
	 */
	public final static String DEPLOY_DEPLOYMENT_FOR_ITEM_FINISHED_WITH = "dc_1161";
	/**
	 * Deployment settings are:{0}{1}{0}Version Handling Rule:
	 * [{2}].{0}Deployment Strategy: [{3}].{0}Life Cycle Deployment Strategy:
	 * [{4}].{0}{5}
	 */
	public final static String DEPLOY_VERSION_HANDLING_RULE_ = "dc_1162";
	// The archives specified for deployment are:
	public final static String DEPLOY_ARCHIVES_SPEC_FOR_DEPL_ARE = "dc_1163";
	//
	// "dc_1164";
	// Performing the deployment ...
	public final static String DEPLOY_PERFORMING_DEPLOYMENT = "dc_1165";
	// Building the deploy result ...
	public final static String DEPLOY_BUILDING_DEPLOY_RESULT = "dc_1166";
	// The deployment finished with result: {0}
	public final static String DEPLOY_DEPLOYMENT_FINISHED_WITH_RESULT = "dc_1167";
	// Evaluating lock data ...
	public final static String DEPLOY_EVALUATING_LOCK_DATA = "dc_1168";
	// ==================================================
	public final static String DEPLOY_EQUALS_SEPARATOR = "dc_1169";

	// ++++++++++++++ Starting deployment ++++++++++++++
	public final static String DEPLOY_STARTING_DEPLOYMENT = "dc_1170";
	// ++++++++++++++ Deployment finished ++++++++++++++
	public final static String DEPLOY_DEPLOYMENT_FINISHED = "dc_1171";
	// Starting the deployment checks ...
	public final static String DEPLOY_STARTING_DEPLOYMENT_CHECKS = "dc_1172";
	// Due to CRC check deployment status is set to ''{0}'.
	public final static String DEPLOY_DUE_TO_CRC_CHECK_ = "dc_1173";
	// Starting the bulk delivery ...
	public final static String DEPLOY_STARTING_BULK_DELIVERY = "dc_1174";
	// Starting the deliver without start ...
	public final static String DEPLOY_STARTING_ONLY_DELIVERY = "dc_1175";
	// Deployment of the component {0} finished. The item status is ''{1}'.
	public final static String DEPLOY_DEPLOYMENT_OF_COMP_FINISHED = "dc_1176";
	// The Admitted Safe Deploy Processor processes the component {0}
	public final static String DEPLOY_ADMITTED_SAFE_DEPLOY_PROCESSOR = "dc_1177";
	// Deploying the component {0}
	public final static String DEPLOY_DEPLOYING_COMPONENT = "dc_1178";
	/**
	 * Observer ''{0}'' has been notified. Component:{1}.
	 */
	public final static String DEPLOY_OBSERVER_IS_NOTIFIED = "dc_1179";

	/**
	 * Starting to notify listeners about action {0}. Component:{1}.
	 */
	public final static String DEPLOY_STARTING_TO_NOTIFY_LISTENERS_ABOUT = "dc_1180";
	/**
	 * Listeners are notified about action {0}. Component:{1}. Delay:{2} ms.
	 */
	public final static String DEPLOY_LISTENERS_ARE_NOTIFIED_ABOUT_ACTION = "dc_1181";
	// The listener {0} was unregistered as during the notification about the
	// event \'{1}\' the following error occurred: {2}
	public final static String DEPLOY_LISTENER_WAS_UNREGISTERED_AS_DURING_ = "dc_1182";
	// The parameter ''{0}'' will be trimmed. The spaces surrounding it are not
	// allowed.
	public final static String DEPLOY_PARAMETER_WILL_BE_TRIMMED = "dc_1183";
	// The component property {0} ''{1}'' will be corrected to ''{2}'' as it
	// contains charactes which are not allowed from the system.The parameter
	// ''{0}'' will be trimmed. The spaces surrounding it are not allowed.
	public final static String DEPLOY_COMPONENT_PROPERTY_WILL_BE_CORRECTED = "dc_1184";
	// The following batch items are going to be removed from the deployment
	// batch, because the SDUs to which they refer have already been added with
	// another batch items to the deployment batch. It does not make sense same
	// SDUs to be deployed more than one time per deployment batch:
	public final static String DEPLOY_FOLLOWING_BATCH_ITEMS_ARE_GOING_TO_BE_REMOVED_ = "dc_1185";
	// Trying to start default offline deployment for the component ''{0}''. All
	// the offline admitted components have to be processed by the others
	// processors
	public final static String DEPLOY_TRYING_TO_START_DEFAULT_OFFLINE_DEPL_ = "dc_1186";
	// A lock problem occurred while checking whether the system has to delete
	// all the deployment batch files. {0}
	public final static String DEPLOY_LOCK_PROBLEM_OCCURED_WHILE_ = "dc_1187";
	// A deployment problem occurred while checking whether the system has to
	// delete all the deployment batch files. {0}
	public final static String DEPLOY_DEPLOYMENT_PROBLEM_OCCURED_WHILE_ = "dc_1188";
	// Cluster will be restarted again due to "offline" -> "online" components
	// dependencies
	public final static String ENGINE_MULTIPLE_RESTART = "dc_1189";

	/**
	 * rolling patch
	 */
	// Current instance cannot be binded to the cluster: {0}.
	public final static String ROLLING_BIND_ERROR = "dc_1190";

	// Received an event from DS that the applications are started.
	public final static String APP_STARTED_EVENT_RECEIVED = "dc_1191";
	// There are post onlines left for deployment. Deploy callback will be
	// registered.
	public final static String POST_ONLINES_LEFT_FOR_DEPLOYMENT = "dc_1192";
	// The registration of the post online deploy callback failed. The post
	// online items will not be deployed
	public final static String POST_ONLINE_DPL_CALLBACK_REG_FAILED = "dc_1193";
	// The locks will not be deleted, because there are post onlines left for
	// deployment.
	public final static String LOCKS_NOT_DELETED = "dc_1194";
	// Starting deployment of post online items
	public final static String POST_ONLINES_DEPLOYMENT_STARTED = "dc_1195";
	// Post online deployment finished
	public final static String POST_ONLINES_DEPLOYMENT_FINISHED = "dc_1196";

	// //////////
	// GenericDelivery //
	// //////////
	//
	// Invoking the Deploy Service's deployLibrary operation ...
	public final static String GD_INVOKE_DEPLOY_LIB = "dc_1200";
	/**
	 * Deploy Service's deployLibrary operation has finished.Time: {0} ms.
	 */
	public final static String GD_DEPLOY_LIB_OPERATION_FINISHED = "dc_1201";
	// Invoking the Deploy Service's removeLibrary operation ...
	public final static String GD_INVOKE_REMOVE_LIB = "dc_1202";
	/**
	 * Deploy Service's removeLibrary operation has finished.Time: {0} ms.
	 */
	public final static String GD_REMOVE_LIB_OPERATION_FINISHED = "dc_1203";
	// Invoking the Deploy Service's deploy operation ...
	public final static String GD_INVOKE_DEPLOY_OPER = "dc_1204";
	// Deploy Service's deploy operation finished.
	public final static String GD_DEPLOY_OPERATION_FINISHED = "dc_1205";
	// Invoking the Deploy Service's update operation ...
	public final static String GD_INVOKE_UPDATE_OPER = "dc_1206";
	// Deploy Service's update operation finished.
	public final static String GD_UPDATE_OPERATION_FINISHED = "dc_1207";
	// Invoking the Deploy Service's remove operation ...
	public final static String GD_INVOKE_REMOVE_OPER = "dc_1208";
	// Deploy Service's remove operation finished.
	public final static String GD_REMOVE_OPERATION_FINISHED = "dc_1209";

	// Invoking the Deploy Service's removeReferences operation ...
	public final static String GD_INVOKE_REMOVE_REFER = "dc_1210";
	// Deploy Service's removeReferences operation finished.
	public final static String GD_REMOVE_REFER_OPERATION_FINISHED = "dc_1211";
	// Invoking the Deploy Service's makeReferences operation ...
	public final static String GD_INVOKE_MAKE_REFER = "dc_1212";
	// Deploy Service's makeReferences operation finished.
	public final static String GD_MAKE_REFER_OPERATION_FINISHED = "dc_1213";

	// Deploy Service's deploy operation has finished with warnings. \r\n {0}
	public final static String GD_DEPLOY_OPERATION_FINISHED_WITH_WARNINGS = "dc_1214";
	// Deploy Service's update operation has finished with warnings. \r\n {0}
	public final static String GD_UPDATE_OPERATION_FINISHED_WITH_WARNINGS = "dc_1215";

	// ////////////////
	// UNDEPLOY //
	// ////////////////
	//
	// Undeployment Item graph contains cycles ({0})!
	public final static String UNDEPLOY_ITEM_GRAPH_CONTAINS_CYCLES = "dc_1300";
	// Received ServerService for determining online and offline software types
	// that is no SoftwareTypeService!
	public final static String UNDEPLOY_SERVER_SERVICE_IS_NOT_ON_OFF_SOFTWARE_TYPE = "dc_1301";
	// '==================================================='
	public final static String UNDEPLOY_EQUALS_SEPARATOR = "dc_1302";
	// '++++++++++++++ Starting undeployment ++++++++++++++'
	public final static String UNDEPLOY_STARTING_UNDEPLOYMENT = "dc_1303";
	// Start undeployment checks ...
	public final static String UNDEPLOY_START_UNDEPLOYMENT_CHECKS = "dc_1304";
	// Sorting the components for undeployment ...
	public final static String UNDEPLOY_SORTING_COMPONENTS_FOR_UNDEPLOY = "dc_1305";
	// Performing the undeployment ...
	public final static String UNDEPLOY_PERFORMING_UNDEPLOY = "dc_1306";
	// The undeployment finished with result ''{0}'.
	public final static String UNDEPLOY_FINISHED_WITH_RESULT = "dc_1307";
	// Start sorting the undeploy items again, as there are newly undeploy items
	// added to the batch.
	public final static String UNDEPLOY_START_SORTING_ITEMS_AGAIN = "dc_1308";
	// The undeploy items are sorted successfully.
	public final static String UNDEPLOY_ITEMS_ARE_SORTED_SUCCESSFULLY = "dc_1309";

	// The undeploy item ''{0}'' is added to the specified by the client ones.
	// It depends on at least one of the specified.
	public final static String UNDEPLOY_ITEM_IS_ADDED_BY_CLIENT = "dc_1310";
	// The sdu ''{0}'' was removed from the repository by the {0}
	public final static String UNDEPLOY_SDU_WAS_REMOVED_FROM_REPO = "dc_1311";
	// Starting to notify the undeploy observers ...
	public final static String UNDEPLOY_STARTING_TO_NOTIFY_OBSERVERS = "dc_1312";
	// The observers are notified
	public final static String UNDEPLOY_OBSERVERS_ARE_NOTIFIED = "dc_1313";
	// Starting the Generic Delivery to remove the item ...
	public final static String UNDEPLOY_STARTING_GD_TO_REMOVE_ITEM = "dc_1314";
	// Generic Delivery finished.
	public final static String UNDEPLOY_GD_FINISHED = "dc_1315";
	// Observer ''{0}'' was notified.
	public final static String UNDEPLOY_OBSERVER_WAS_NOTIFIED = "dc_1316";
	// The dDeploy Controller is in ''{0}'' state. The DB lock is not going to
	// be unlocked.
	public final static String UNDEPLOY_DC_IS_IN_STATE = "dc_1317";
	// Unlocking the db ...
	public final static String UNDEPLOY_UNLOCKING_DB = "dc_1318";
	// Undeployment settings are: {0}{1}{0}Undeployment Strategy: [{2}]
	public final static String UNDEPLOY_STRATEGY = "dc_1319";

	// Setting the SAP Application Server Java in {0}
	public final static String UNDEPLOY_SETTING_ENGINE_IN = "dc_1320";
	// Restarting the SAP Application Server Java ...
	public final static String UNDEPLOY_RESTARTING_ENGINE = "dc_1321";
	// Start the offline undeployment post processing logic ...
	public final static String UNDEPLOY_START_OFFLINE_POSTPROCESS = "dc_1322";
	// Persisting the temporary undeployment data ...
	public final static String UNDEPLOY_PERSISTING_TEMP_DATA = "dc_1323";
	// Locking the db ...
	public final static String UNDEPLOY_LOCKING_DB = "dc_1324";
	// The undeploy item ''{0}'' is an offline one, therefore only its status is
	// changed to ''{0}'.
	public final static String UNDEPLOY_ITEM_IS_OFFLINE_SO_CHANGE_STATUS = "dc_1325";
	// '+++++++ Starting undeploying the component ''{0}'' +++++++'
	public final static String UNDEPLOY_STARTING_UNDEPLOY_COMPONENT = "dc_1326";
	// '+++++++ Undeployment ended for the component ''{0}'' with ''{1}''
	// +++++++'
	public final static String UNDEPLOY_ENDED_FOR_COMPONENT_WITH = "dc_1327";
	// The components specified for undeployment are:
	public final static String UNDEPLOY_COMPONENTS_SPECIFIED_FOR_UNDEPLOY_ARE = "dc_1328";
	// SDU: {0}
	public final static String UNDEPLOY_A_VALUE = "dc_1329";

	// The following batch items are going to be removed from the undeployment
	// batch, because the SDUs to which they refer have already been added with
	// another batch items to the undeployment batch. It does not make sense
	// same SDUs to be undeployed more than one time per one undeployment batch:
	public final static String UNDEPLOY_FOLLOWING_BATCH_ITEMS_WILL_BE_REMOVED_FROM_BATCH_ = "dc_1330";
	// The undeploy item ''{0}'' could not be undeployed as it is not deployed.
	public final static String UNDEPLOY_ITEM_COULD_NOT_BE_UNDEPLOYED_AS_IT_NOT_DEPLOYED = "dc_1331";
	// The undeploy item property {0} ''{1}'' will be corrected to ''{2}'' as it
	// contains charactes which are not allowed from the system.
	public final static String UNDEPLOY_ITEM_PROPERTY_WILL_BE_CORRECTED_TO = "dc_1332";
	// Persisting the component ''{0}'' into the temporary undeployment data
	public final static String UNDEPLOY_PERSISTING_COMP_INTO_TEMP_DATA = "dc_1333";
	// Undeployment of the component {0} finished. The component status is
	// ''{1}'.
	public final static String UNDEPLOY_UNDPL_OF_COMP_FINISHED = "dc_1334";
	// The Admitted Undeploy Processor processes the component {0}
	public final static String UNDEPLOY_ADMITTED_SAFE_UNDEPLOY_PROCESSOR = "dc_1335";

	// //////////////////
	// SESSION_ID //
	// //////////////////
	//  
	// The session id read from DB was {0}. The local session ids are {1} and
	// updated the DB with {2}.
	public final static String SESSION_ID_FROM_DB_LOCAL_4_DB = "dc_1400";
	// Will return session id {0}.
	public final static String SESSION_ID_WILL_RETURN = "dc_1401";
	// The session id [{0}] is associated with transaction id [{1}], which can
	// be used to filter trace and log messages.
	public final static String SESSION_ID_ASSOCIATED_WITH_TRANSACTION_ID = "dc_1402";
	// The sessionId parameter is null.
	public final static String SESSION_ID_IS_NULL = "dc_1403";

	// ///////////////////////
	// DATA_STORAGE_GC //
	// ///////////////////////
	//  
	// The data storage {0} is out of data and was deleted.
	public final static String GC_DATA_STORAGE_IS_OUT_OF_DATA_AND_DELETED = "dc_1500";
	// The data storage garbage collection was started for {0}.
	public final static String GC_DATA_STORAGE_WAS_STARTED = "dc_1501";

	// ///////////////////////
	// LOG //
	// ///////////////////////
	//  
	/**
	 * Log was reinitialized {0}.
	 */
	public final static String LOG_WAS_REINITED = "dc_1600";
	// The log was not reinited as the file ''{0}'' does not exist.
	public final static String OFF_LOG_WAS_NOT_REINITED = "dc_1601";

	// ///////////////////////
	// MANAGE //
	// ///////////////////////
	//
	// The {0} was binded in the registry by the message processor.
	public final static String MANAGE_CM_WAS_BINDED_IN_REGISTRY = "dc_1700";

	// ///////////////////////
	// LCM //
	// ///////////////////////
	//
	// Starting the component with name ''{0}'' and vendor ''{1}'.
	public final static String LCM_STRARTING_COMPONENT = "dc_1800";
	// Performing Java EE start operation for the development component {0}
	public final static String LCM_PERFORMING_START_ON_COMPONENT = "dc_1801";
	// The Java EE start of the development component ''{0}'' finished
	// successfully.
	public final static String LCM_START_ON_COMPONENT_FINISHED_SUCCESS = "dc_1802";
	// The system does not support 'start' operation for component {0}
	public final static String LCM_SYSTEM_DOES_NOT_SUPPORT_START_OPER = "dc_1803";
	// The system does not support 'stop' operation for component {0}
	public final static String LCM_SYSTEM_DOES_NOT_SUPPORT_STOP_OPER = "dc_1804";
	// The system does not support 'getLCMStatus' operation for component {0}
	public final static String LCM_SYSTEM_DOES_NOT_SUPPORT_CMSTATUS_OPER = "dc_1805";
	// Warning exception has been returned while the ''{0}'' was starting.
	// Warnings:{1}{2}
	public final static String LCM_WARNING_EXC_IS_RETURNED_WHILE_STARTING = "dc_1806";
	// Exception has been returned while the ''{0}'' was starting.
	// Warning/Exception :{1}{2}
	public final static String LCM_EXCEPTION_IS_RETURNED_WHILE_STARTING = "dc_1807";
	// Performing Java EE stop operation for the development component {0}
	public final static String LCM_PERFORMING_STOP_ON_COMPONENT = "dc_1808";
	// Performing Java EE get status operation for the development component {0}
	public final static String LCM_PERFORMING_GET_STATUS_ON_COMPONENT = "dc_1809";
	// The Java EE status {0}
	public final static String LCM_J2EE_STATUS = "dc_1810";
	// The property {0} ''{1}'' will be corrected to ''{2}'' as it contains
	// charactes which are not allowed from the system.
	public final static String LCM_PROPERTY_WILL_BE_CORRECTED_TO_ = "dc_1811";
	// Exception has been returned while the ''{0}'' was stopping.
	// Warning/Exception :{1}{2}
	public final static String LCM_EXCEPTION_IS_RETURNED_WHILE_STOPPING = "dc_1812";
	// The ''{0}'' cannot be started, because is 'disabled' in the zero admin
	// template
	public final static String LCM_DISABLED_EXCEPTION_IS_RETURNED_WHILE_STARTING = "dc_1813";

	// ///////////////////////
	// FRAME //
	// ///////////////////////
	//
	// {0} = {1}
	public final static String FRAME_KEY_VALUE = "dc_1900";
	// '=========== Starting Deploy Controller ( TimerId: {0} )==========='
	public final static String FRAME_STARTING_DEPLOY_CONTROLLER = "dc_1901";
	// '=========== Stopping the Deploy Controller ==========='
	public final static String FRAME_STOPPING_DEPLOY_CONTROLLER = "dc_1902";
	// Deploy Controller is started. Total elapsed: {0}
	public final static String FRAME_DEPLOY_CONTROLLER_STARTED = "dc_1903";
	// Deploy Controller is stopped.
	public final static String FRAME_DEPLOY_CONTROLLER_STOPPED = "dc_1904";
	// Starting the finalization of the process ...
	public final static String FRAME_STARTING_FINALIZATION_OF_PROCESS = "dc_1905";
	// Removing the locks
	public final static String FRAME_REMOVING_LOCKS = "dc_1906";
	// Restarting the SAP Application Server Java in {0}
	public final static String FRAME_RESTARTING_ENGINE_IN_MODE = "dc_1907";
	/**
	 * Application Server Java is in ''{0}'', but there is no lock applied.The
	 * read lock is ''{1}''.
	 * */
	public final static String FRAME_ENGINE_IS_IN_MODE_BUT_NO_LOCK_APPLIED = "dc_1908";
	/**
	 * Removing the locks {0}
	 */
	public final static String FRAME_REMOVING_LOCKS_FOR = "dc_1909";

	// Registering the Deploy Controller
	public final static String FRAME_REGISTERING_DEPLOY_CONTROLLER = "dc_1910";
	// Notifying all the other Deploy Controller services to register themselves
	public final static String FRAME_NOTIFY_SERVICES_TO_REGISTER = "dc_1912";
	// The current instance is not the one on which the process has been
	// triggered. No process related actions will be performed.
	public final static String FRAME_PROCESS_ON_CURRENT_INST_MUST_NOT_BE_TRIGGERED = "dc_1913";
	/**
	 * Locking enqueue for {0}
	 */
	public final static String FRAME_LOCKING_ENQUE_FOR = "dc_1914";
	// The {0} was binded in the registry.
	public final static String FRAME_BINDED_IN_REGISTRY = "dc_1915";
	// The Deploy Controller has received an event that the SAP AS Java Service
	// Container is started while it is running in {0}
	public final static String FRAME_DC_RECEIVED_EVENT_CONTAINER_RUN = "dc_1916";
	/**
	 * Current Deploy Controller service is not able to manage the process
	 */
	public final static String FRAME_DC_NOT_ABLE_TO_MANAGE_PROCESS = "dc_1917";
	// Deploying the online deployment components (if there are) ...
	public final static String FRAME_DEPLOYING_ONLINE_IF_ANY = "dc_1918";
	// Online deployment finished.
	public final static String FRAME_ONLINE_DEPLOYMENT_FINSHED = "dc_1919";

	/**
	 * Deploy Controller has received an event that AS Java Core is started
	 * while it is running in {0}
	 */
	public final static String FRAME_DC_RECEIVED_EVENT_ENGINE_CORE_STARTED = "dc_1920";
	// Registering the offline deployed data ...
	public final static String FRAME_REGISTERING_OFF_DEPLOYED_DATA = "dc_1921";
	// The offline deployed data is registered into the Deploy Controller
	// repository.
	public final static String FRAME_OFF_DEPLOYED_DATA_IS_REGISTERED = "dc_1922";
	// Registering the offline undeployed data ...
	public final static String FRAME_REGISTERING_OFF_UNDEPLOYED_DATA = "dc_1923";
	// The offline deployed data is unregistered from the Deploy Controller
	// repository.
	public final static String FRAME_OFF_DEPLOYED_DATA_IS_UNREGISTERED = "dc_1924";
	// The Deploy Controller is stopping while the SAP Application Server Java
	// is in {0}
	public final static String FRAME_DC_STOPPING_WHILE_ENGINE_IN_MODE = "dc_1925";
	// The Deploy Controller is started while the SAP Application Server Java is
	// in {0}
	public final static String FRAME_DC_STARTED_WHILE_ENGINE_IN_MODE = "dc_1926";
	// The Deploy Controller will be started by default as neither ''{0}'' nor
	// ''{1}'' action has been specified.
	public final static String FRAME_DC_WILL_BE_STARED_AS_ACTION_NOT_SPECIFIED = "dc_1927";
	// Undeploying the online deployment components (if there are) ...
	public final static String FRAME_UNDEPLOYING_ONLINE_IF_ANY = "dc_1928";
	// Online undeployment finished.
	public final static String FRAME_ONLINE_UNDEPLOYMENT_FINSHED = "dc_1929";

	// Could not aquire administrator user prior to online deploy
	public final static String FRAME_CANNOT_ACQUIRE_ADMINISTRATOR = "dc_1930";
	// Could not check user for administrator role
	public final static String FRAME_CANNOT_CHECK_FOR_ADMINISTRATOR = "dc_1931";
	// The {0} value of {1} service property {2} is wrong. The correct values
	// are {3}.
	public final static String FRAME_WRONG_SERVICE_PARAM_VALUE = "dc_1932";
	// Sync Action is already locked.
	public final static String FRAME_SYNC_ACTION_ALREADY_LOCK = "dc_1933";
	// DC Manager state is set to {0}.
	public final static String DCSTATE_SET = "dc_1934";

	// ///////////////////////
	// COMPVERS //
	// ///////////////////////
	//  
	// Undating the {0} with the component ''{1}'' ...
	public final static String COMPVERS_UPDATING_WITH_COMPONENT = "dc_2000";
	// The sdu ''{0}'' was removed from the {1}
	public final static String COMPVERS_SDU_WAS_REMOVED_FROM = "dc_2001";
	// The sdu ''{0}'' was stored into the {1}
	public final static String COMPVERS_SDU_WAS_STORED_INTO = "dc_2002";

	// ///////////////////////
	// TELNET //
	// ///////////////////////
	//
	// {0}Deploy settings:
	public final static String TELNET_DEPLOY_SETTINGS = "dc_2100";
	// {0}Undeploy settings:
	public final static String TELNET_UNDEPLOY_SETTINGS = "dc_2101";
	// {0}{1}{2}
	public final static String TELNET_SPACE_ONE_TWO_THREE = "dc_2102";
	// {0}{1}
	public final static String TELNET_ONE_TWO = "dc_2103";
	// {0} {1}
	public final static String TELNET_ONE_SPACE_TWO = "dc_2104";
	// {0}{1}{2}
	public final static String TELNET_ONE_TWO_THREE = "dc_2105";
	// There are no registered substitution variables.
	public final static String TELNET_THERE_ARE_NO_SUB_VARIABLES = "dc_2106";
	// {0}If there is an offline deployment, Telnet connection to host may be
	// lost, but the result can be seen using 'get_result' command{1}
	public final static String TELNET_IF_OFFLINE_DEPLOY_TELNET_LOST = "dc_2107";
	// The telnet commands of DeployController will not be registered, because
	// the ''{0}'' is NULL.
	public final static String TELNET_COMMANDS_OF_DC_WILL_NOT_BE_REGISTERED_ = "dc_2108";
	// There are no telnet commands for DeployController, which to registered.
	public final static String TELNET_THERE_ARE_NO_TELNET_COMMANDS_FOR_DC = "dc_2109";

	// {0}If there is an offline undeployment, Telnet connection to host may be
	// lost, but the result can be seen using 'get_result' command{1}
	public final static String TELNET_IF_OFFLINE_UNDEPLOY_TELNET_LOST = "dc_2110";
	// Processing deployment operation, please wait...
	public final static String TELNET_PROCESSING_DEPLOYMENT_WAIT = "dc_2111";
	// Processing undeployment operation, please wait...
	public final static String TELNET_PROCESSING_UNDEPLOYMENT_WAIT = "dc_2112";
	// Processing remove operation of ''{0}'', please wait...
	public final static String TELNET_PROCESSING_REMOVE_WAIT = "dc_2113";
	// telnet command is invoked {0}.
	public final static String TELNET_COMMAND_IS_INVOKED = "dc_2114";
	// {0}.
	public final static String TELNET_ONE = "dc_2115";

	// //////////////
	// PARAM //
	// //////////////
	//
	// Parameter Manager replaces the parameter's ''{0}'' value ''{1}'' with the
	// one ''{2}', because the parameter is part of the SDM system parameters.
	public final static String PARAM_MANAGER_REPLACES_BECAUSE_IS_PART_OF_SDM_SYSTEM_PARAMS = "dc_2200";
	// Parameter Manager replaces the parameter's ''{0}'' value ''{1}'' with the
	// one ''{2}', because the parameter is part of the system profile
	// parameters
	public final static String PARAM_MANAGER_REPLACES_BECAUSE_IS_PART_OF_SYSTEM_PROFILE_PARAMS = "dc_2201";

	// /////////////
	// LOCK //
	// /////////////
	//
	// Locking the enqueue for operation [{0}] ...
	public final static String LOCK_LOCKING_THE_ENQUEUE = "dc_2300";
	// Unlocking the {0}.
	public final static String LOCK_UNLOCKING_THE = "dc_2301";
	// {0} operation can be executed with the following lock data {1}.
	public final static String LOCK_OPERATION_AND_DATA = "dc_2302";
	// Locking the instance for offline operation with file [{0}] ...
	public final static String LOCK_LOCKING_FOR_OFFLINE = "dc_2303";

	// /////////////////
	// PARALLEL //
	// /////////////////
	//
	// The batch will be executed with [{0}] parallelism strategy.
	public final static String P_SESSION_ID_AND_PARALLELISM_STRATEGY = "dc_2400";
	// Batch {0}
	public final static String P_CHECK_RESULT = "dc_2401";

	// ///////////////////
	// EVENTS //
	// //////////////////
	//
	/**
	 * Global event received from other cluster note with unknown event message
	 * type code ''{0}''.
	 */
	public final static String EVENT_GLOBAL_UNKNOWN_TYPE = "dc_2500";
	/**
	 * Exception during parsing Distributed Message. Cause=''{0}''.
	 */
	public final static String EVENT_GLOBAL_MESSAGE_PARSING_ERROR = "dc_2505";

	/**
	 * The message ''{0}'' will not be distributed because of huge size''{1}''.
	 */
	public final static String EVENT_GLOBAL_BIG_MESSAGE_ERROR = "dc.event.big.message.error";

	// ///////////////////////
	// FILE_STORAGE_GC //
	// ///////////////////////
	//  
	// The file storage {0} is out of data and was deleted.
	public final static String GC_FILE_STORAGE_IS_OUT_OF_DATA_AND_DELETED = "dc_2600";
	// The file storage garbage collection was started for {0}.
	public final static String GC_FILE_STORAGE_WAS_STARTED = "dc_2601";

	// ///////////////////////
	// UTIL //
	// ///////////////////////
	//    
	// Problem occurrs while closing stream: {0}
	public final static String UTIL_PROBLEM_CLOSE_STREAM = "dc_2700";

	// ///////////////////////
	// VALIDATE //
	// ///////////////////////
	//    
	// Starting batch validation: [{0}]
	public final static String VALIDATE_START = "dc_2800";
	public final static String VALIDATE_END = "dc_2801";

	// ///////////////////////
	// LOG SUMMARY //
	// ///////////////////////
	// {0}
	public final static String SUMMARY_DEPLOY_RESULT_INFO = "dc_3000";
	// Deploying [{0}] ...{1}
	public final static String TELNET_DEPLOYMENT_STARTED = "dc_3001";
	// Deployment of [{0}] finished.{1}
	public final static String TELNET_DEPLOYMENT_FINISHED = "dc_3002";
	// ===== SUMMARY - DEPLOY RESULT =====
	public final static String SUMMARY_DEPLOY_RESULT = "dc_3003";
	// *** --- *** --- *** --- ***
	public final static String SUMMARY_SEPARATOR = "dc_3004";
	// SCA(s) ADMITTED FOR DEPLOYMENT [{0}]:
	public final static String SUMMARY_SCA_ADMITTED = "dc_3005";
	// SDA(s) ADMITTED FOR DEPLOYMENT [{0}]:
	public final static String SUMMARY_SDA_ADMITTED = "dc_3006";
	// ===== SUMMARY - END DEPLOY RESULT =====
	public final static String SUMMARY_END_DEPLOY_RESULT = "dc_3007";
	// # LIST OF {0} ITEMS #
	public final static String SUMMARY_LIST = "dc_3008";
	// * SDA:
	public final static String SUMMARY_SDA_ALONE_ITEM = "dc_3009";
	// * {0} items belong to SCA: {1}
	public final static String SUMMARY_SDA_OF_SCA = "dc_3010";
	// - SDA:
	public final static String SUMMARY_SDA_ITEM = "dc_3011";
	// - [{0}] : [{1}]
	public final static String SUMMARY_COUNT = "dc_3012";
	// {0}===== PROGRESS START ====={0}{0}
	public final static String TELNET_PROGRESS_START = "dc_3013";
	// {0}===== PROGRESS END ====={0}
	public final static String TELNET_PROGRESS_END = "dc_3014";
	// {0}===== DEPLOY RESULT ====={0}
	public final static String DEPLOY_RESULT = "dc_3015";
	// {0}===== END DEPLOY RESULT ====={0}{0}
	public final static String END_DEPLOY_RESULT = "dc_3016";

	// =================================================={0}
	public final static String DEPLOY_EQUALS_SEPARATOR_PARAM = "dc_3017";
	// Deployment status of [{0}] was changed from [{1}] to [{2}]
	public final static String DEPLOYMENT_STATUS_CHANGED = "dc_3018";

	// Undeploying [{0}] ...{1}
	public final static String TELNET_UNDEPLOYMENT_STARTED = "dc_3019";
	// Undeployment of [{0}] finished.{1}
	public final static String TELNET_UNDEPLOYMENT_FINISHED = "dc_3020";

	// Undeployment status of [{0}] was changed from [{1}] to [{2}]
	public final static String UNDEPLOYMENT_STATUS_CHANGED = "dc_3021";

	// ++++++++++++++ Undeployment finished ++++++++++++++
	public final static String UNDEPLOY_UNDEPLOYMENT_FINISHED = "dc_3022";
	// ==================================================={0}
	public final static String UNDEPLOY_EQUALS_SEPARATOR_PARAM = "dc_3023";
	// {0}===== UNDEPLOY RESULT ====={0}
	public final static String UNDEPLOY_RESULT = "dc_3024";
	// {0}===== END UNDEPLOY RESULT ====={0}{0}
	public final static String END_UNDEPLOY_RESULT = "dc_3025";

	// ===== SUMMARY - UNDEPLOY RESULT =====
	public final static String SUMMARY_UNDEPLOY_RESULT = "dc_3026";
	// ===== SUMMARY - END UNDEPLOY RESULT =====
	public final static String SUMMARY_END_UNDEPLOY_RESULT = "dc_3027";
	// SDA(s) ADMITTED FOR UNDEPLOYMENT [{0}]:
	public final static String SUMMARY_SDA_ADMITTED_UNDEPLOY = "dc_3028";
	// SDA(s) ADMITTED FOR UNDEPLOYMENT [{0}]:
	public final static String SUMMARY_SCA_ADMITTED_UNDEPLOY = "dc_3029";
	// * SCA:
	public final static String SUMMARY_SCA_ALONE_ITEM = "dc_3030";

}