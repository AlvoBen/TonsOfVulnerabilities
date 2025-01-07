package com.sap.engine.services.deploy.server;

/**
 * @author Rumiana Angelova
 * @version 6.25
 */
public class DeployConstants {

	// module types
	public static final byte APP_TYPE = 0;
	public static final byte MODULE_TYPE = 1;
	public static final byte LIB_TYPE = 2;
	public static final byte REF_TYPE = 3;
	public static final byte INTERFACE_TYPE = 4;
	public static final byte SERVICE_TYPE = 5;
	// transaction phases
	public static final byte BEGIN_PHASE = 0;
	public static final byte PREPARE_PHASE = 1;
	public static final byte COMMIT_PHASE = 2;
	public static final byte ROLLBACK_PHASE = 3;
	public static final byte ROLLBACK_PREPARE_PHASE = 4;
	public static final byte UNKNOWN_PHASE = 5;
	public static final String begin = "begin";
	public static final String prepare = "prepare";
	public static final String commit = "commit";
	public static final String rollback = "rollback";
	public static final String rollbackPrepare = "rollbackPrepare";
	public static final String force = "force";
	// commands
	public static final String respond = "respond";
	public static final String deploy = "deploy";
	public static final String deployLib = "deployLib";
	public static final String removeLib = "removeLib";
	public static final String deployService = "deployService";
	public static final String removeService = "removeService";
	public static final String deployInterface = "deployInterface";
	public static final String removeInterface = "removeInterface";
	public static final String makeRefs = "makeRefs";
	public static final String removeRefs = "removeRefs";
	public static final String update = "update";
	public static final String listContainers = "listContainers";
	public static final String listApp = "listApp";
	public static final String listEl = "listEl";
	public static final String getStatus = "getStatus";
	public static final String getContainerInfo = "getContainerInfo";
	public static final String removeApp = "removeApp";
	public static final String removeComp = "removeComp";
	public static final String getClientJar = "getClientJar";
	public static final String updateJar = "updateJar";
	public static final String stopApp = "stopApp";
	public static final String startApp = "startApp";
	public static final String startInitiallyApp = "startInitiallyApp";
	public static final String runtimeChanges = "runtimeChanges";
	public static final String appInfoChange = "appInfoChange";
	public static final String singleFileUpdate = "singleFileUpdate";
	public static final String initialStartApplications = "initialStartApplications";
	public static final String getStatusDescription = "getStatusDescription";
	public static final String oncePerInstance = "oncePerInstance";
	public static final String updateWithSync = "updateWithSync";

	// command parameters
	public static final String admincommand = "admin_command";
	public static final String phase = "phase";
	public static final String library = "library:";
	public static final String LIB_JARS = "library_jars";
	public static final String LIB_REFS = "library_references";
	public static final String REFS = "references";
	public static final String LIBS = "libraries";
	public static final String REFS_INFO = "reference";
	public static final String LIBS_INFO = "library";
	public static final String TRANSACTION_TYPE = "transactionType";
	public static final String DOWNLOAD = "download";
	public static final String READ_DI_FROM_DB = "readDIFromDB";

	// components
	public static final String INTERFACE_LOG = "log";
	public static final String INTERFACE_CROSS = "cross";
	public static final String INTERFACE_SHELL = "shell";
	public static final String INTERFACE_SECURITY = "security";
	public static final String SERVICE_BASICADMIN = "basicadmin";
	public static final String REMOTE_INSTANCE_ADMIN = "/admin/InstanceAdmin";

	//
	// Configurations
	//
	public static final String ROOT_CFG_APPS = "apps";
	public static final String ROOT_CFG_DEPLOY = "deploy";
	public static final String ROOT_CFG_DEPLOY_SERVICE = "deploy_service";

	// for zero admin
	public static final String CC_SYS_INST = "cluster_config/system/instances";
	public static final String CURR_INST = "current_instance";
	public static final String CFG_APPS = "cfg/apps";

	public static final String CUSTOM_GLOBAL_CONFIG = "cluster_config/system/custom_global/cfg/apps";
	public static final String GLOBAL_CONFIG = "cluster_config/globals/clusternode_config/workernode/apps";
	public static final String CURRENT_INSTANCE_CONFIG = CC_SYS_INST + "/"
			+ CURR_INST + "/" + CFG_APPS;
	// SAPPreferencesFactory.PREFS_ROOT needs to be deleted and we define it
	// locally
	public static final String PREFS_ROOT = "PREFS_ROOT";

	//
	// File names
	//
	public static final String FN_META_INF = "META-INF";
	public static final String FN_SAP_APP_GLOBAL_PROPS = "sap.application.global.properties";
	public static final String FN_SAP_MANIFEST_MF = "SAP_MANIFEST.MF";

	//
	// Providers
	//
	public static final String DEFAULT_PROVIDER_4_APPS_SAP_COM = "sap.com";
	public static final String DEFAULT_PROVIDER_4_CORE_COMPS_SAP_COM = "sap.com";
	public static final String DEFAULT_PROVIDER_4_CORE_COMPS_ENGINE_SAP_COM = "engine.sap.com";

	public static final char DELIMITER_4_PROVIDER_AND_NAME = '/';

	public static final String LIBRARY_LOADER_SUFFIX = "-library-loader";

	// resource type string constants
	public static final String RESOURCE_TYPE_CONTAINER = "container";
	public static final String RESOURCE_TYPE_LIBRARY = "library";
	public static final String RESOURCE_TYPE_SERVICE = "service";
	public static final String RESOURCE_TYPE_INTERFACE = "interface";
	public static final String RESOURCE_TYPE_APPLICATION = "application";
	public static final String RESOURCE_TYPE_COMPONENT = "application component";

	// references' type names
	public static final String REFERENCE_TYPE_NAME_HARD = "hard";
	public static final String REFERENCE_TYPE_NAME_WEAK = "weak";

	public static final String WEB = "web:";

	// deploy thread name
	public static final String DEPLOY_THREAD_NAME = "Deploy Thread";
	public static final String DEPLOY_DI_THREAD = "Deploy Read DeploymentInfo Thread";
	public static final String DEPLOY_APPBIN_THREAD = "Deploy Read AppBinaries Thread";
	public static final String DEPLOY_PARALLEL_START_THREAD_NAME = "Deploy Parallel Start Thread";
	public static final String DEPLOY_PARALLEL_STOP_THREAD_NAME = "Deploy Parallel Stop Thread";
	public static final String DEPLOY_START_APP_THREAD_NAME = "Deploy Start Application Thread";
	public static final String DEPLOY_STOP_APP_THREAD_NAME = "Deploy Stop Application Thread";
	public static final String DEPLOY_RESTART_APP_THREAD_NAME = "Deploy Restart Application Thread";

	// push monitor defined in monitor-configuration.xml
	public static final String FAILED_2_START = "FailedToStart";

}
