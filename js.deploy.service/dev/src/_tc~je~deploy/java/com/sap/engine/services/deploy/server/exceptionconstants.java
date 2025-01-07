/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server;

import com.sap.engine.services.deploy.container.DeployExceptionConstContainer;

/**
 * An interface holding constants for exception messages.
 * //engine/j2ee.if/dev/src
 * /container_api/_tc~je~container_api/java/com/sap/engine
 * /services/deploy/DeployResourceBundle.properties
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Rumiana Angelova
 * @version
 */
public interface ExceptionConstants extends DeployExceptionConstContainer {

	public final static String UNEXPECTED_EXCEPTION_IN_OPERATION = "deploy_1006";
	public static final String MORE_INFO = "deploy_1007";
	
	public static final String D_INFO_NULL = "deploy_5000";
	public static final String NO_INFO_ABOUT_CONTAINER_RECEIVED = "deploy_5001";

	public static final String CANNOT_CREATE_APPLOADER = "deploy_5004";
	public static final String NOT_DEPLOYED = "deploy_5005";
	public static final String NOT_AVAILABLE_CONTAINER = "deploy_5006";

	public static final String CANNOT_GET_HANDLER_AT_BEGINNING = "deploy_5009";

	public static final String CANNOT_OPEN_CONFIGURATION = "deploy_5011";
	public static final String NOT_SPECIFIED_APP_NAME = "deploy_5012";
	public static final String CANNOT_CREATE_APP_CONFIG = "deploy_5013";
	public static final String NOT_RECOGNIZED_COMPONENT = "deploy_5014";

	public static final String CANNOT_DEPLOY_LIB = "deploy_5020";
	public static final String APP_CONTAINER_RESTRICTION = "deploy_5021";
	public static final String UNKNOWN_SERVER_COMPONENT_TYPE = "deploy_5022";
	public static final String ERROR_DURING_PROCESS_SERVER_COMPONENT = "deploy_5023";
	public static final String MISSING_PARAMETERS = "deploy_5024";
	public static final String CANNOT_SEND_MESSAGE = "deploy_5025";
	public static final String CANNOT_COMMIT_HANDLER = "deploy_5026";
	public static final String CANNOT_GET_HANDLER = "deploy_5027";
	public static final String THREAD_INTERRUPTED = "deploy_5028";
	public static final String UNEXPECTED_EXCEPTION = "deploy_5029";
	public static final String COMPLEX_ERROR = "deploy_5030";
	public static final String ERROR_IN_SERIALIZATION = "deploy_5031";
	public static final String ERROR_IN_DESERIZALIZATION = "deploy_5032";
	public static final String NOT_AVAILABLE_INFO_ABOUT_CONTS = "deploy_5033";

	public static final String CANNOT_RESOLVE_REF_WITH_CAUSE = "deploy_5035";
	public static final String NO_AVAILABLE_CONTS_FOR_APP = "deploy_5036";
	public static final String CANNOT_DOWNLOAD_FILES = "deploy_5037";

	public static final String NO_INFO_ABOUT_CONTS_RECEIVED = "deploy_5040";
	public static final String ERROR_IN_READING_DESCR = "deploy_5041";
	public static final String ERROR_IN_READING_APP_MODULES = "deploy_5042";
	public static final String ERROR_IN_PROCESSING_JAR = "deploy_5043";
	public static final String FILE_WITHOUT_EXT = "deploy_5044";

	public static final String ERROR_DURING_GET_INTERNAL_LIBS = "deploy_5046";
	public static final String CANNOT_DEPLOY_APP = "deploy_5047";
	public static final String CANNOT_STORE_D_INFO_IN_DB = "deploy_5048";

	public static final String NOT_SPECIFIED_CONT_NAME = "deploy_5052";
	public static final String ERROR_IN_PARAMETERS = "deploy_5053";
	public static final String NOT_SUPPORTED_OPERATION = "deploy_5054";

	public static final String CANNOT_GET_HANDLER_ON_PRINCIPLE = "deploy_5056";
	public static final String NOT_AVAILABLE_CONFIG_MANAGER_ON_PRINCIPLE = "deploy_5057";
	public static final String CANNOT_CREATE_CONFIGURATION = "deploy_5058";
	public static final String CANNOT_GET_ALL_ROOT_NAMES = "deploy_5059";
	public static final String CANNOT_COMMIT_HANDLER_ON_PRINCIPLE = "deploy_5060";

	public static final String CANNOT_OPEN_CONFIGURATION_ON_PRINCIPLE = "deploy_5062";
	public static final String ERROR_IN_GETTING_CLIENT_JAR = "deploy_5063";

	public static final String CANNOT_REMOVE_APPLOADER = "deploy_5066";
	public static final String CANNOT_FIND_SERVER = "deploy_5067";
	public static final String IN_SHUTDOWN = "deploy_5068";

	public static final String ALREADY_STARTED_OPERATION = "deploy_5070";
	public static final String CANNOT_LOCK_BECAUSE_OF_TECHNICAL_PROBLEMS = "deploy_5071";

	public static final String CORRUPT_CONFIGURATION = "deploy_5075";

	public static final String CANNOT_READ_ANSWER = "deploy_5080";

	public static final String UNEXPECTED_EXCEPTION_OCCURRED = "deploy_5082";

	public static final String CANNOT_RESOLVE_STOP_REF = "deploy_5087";
	public static final String CYCLE_REF = "deploy_5088";
	public static final String EXEPTION_DURING_CONTAINER_GENERATION = "deploy_5089";

	public static final String CANNOT_STORE_REFERENCES_IN_DB = "deploy_5094";
	public static final String NO_CONT_FOR_APPINFO_CHANGED = "deploy_5095";

	public static final String CALLBACK_THROWS_EXCEPTION = "deploy_5099";
	public static final String SERVER_COMPONENT_NOT_DEPLOYED = "deploy_5100";

	public static final String ERROR_DURING_UPLOAD_SPECIAL_FILES = "deploy_5102";
	public static final String GENERAL_IO_EXCEPTION = "deploy_5103";
	public static final String MIGRATOR_FAILURE = "deploy_5104";
	public static final String CANNOT_DELETE_CFG = "deploy_5105";
	public static final String WILL_NOT_START_APP = "deploy_5106";
	public static final String EXCEPTION_DURING_START_INITIALLY = "deploy_5107";

	public static final String APP_NOT_MIGRATED = "deploy_5109";
	public static final String OPERATION_DEPRECATED = "deploy_5110";
	public static final String APPLICATION_START_DISABLED_IN_FILTERS = "deploy_5111";
	public static final String OPERATION_ALWAYS_SUCCEEDS = "deploy_5112";
	public static final String CANNOT_RESOLVE_REF_2_INTERFACE_PROVIDED_BY_SERVICE = "deploy_5113";
	public static final String STANDARD_JEE_LIBRARY_NAME_NOT_SPECIFIED = "deploy_5114";
	public static final String PARENT_CLASS_LOADER_DOES_NOT_EXIST = "deploy_5115";
	public static final String CANNOT_START_SERVICE = "deploy_5116";
	public static final String ILLEGAL_CONTAINER_PASSED = "deploy_5117";
	public static final String CANNOT_INIT_CONTAINERS = "deploy_5118";
	public static final String INVALID_COMPONENT_STATUS = "deploy_5119";
	public static final String CANNOT_INIT_CONTAINER_XML_ERROR = "deploy_5120";
	public static final String CANNOT_INIT_CONTAINER_IO_ERROR = "deploy_5121";
	public static final String CANNOT_INIT_CONTAINERS_XML_GENERAL_ERROR ="deploy_5122";
	public static final String APPLICATION_START_DISABLED_IN_XML = "deploy_5123";
	public static final String CANNOT_START_APPLICATION = "deploy_5124";
	public static final String CANNOT_UNREGISTER_CONTAINER = "deploy_5125";
	public static final String SERVER_NOT_STARTED = "deploy_5126";
	public static final String CANNOT_HAVE_SAME_CONTAINER_BY_DIFFERENT_PROVIDERS = "deploy_5127";
	public static final String SERVICE_FAILED_TO_START = "deploy_5128";
	public static final String ERRORS_WHILE_PARSING_CONTAINERS_INFO_XML = "deploy_5129";
	public static final String ERRORS_WHILE_READING_CONTAINERS_INFO_XML = "deploy_5130";
	public static final String CYCLIC_REFERENCE_ON_ADD = "deploy_5131";
	public static final String CYCLIC_REFERENCE_ON_REMOVE = "deploy_5132";
	public static final String CANNOT_COPY_FILE = "deploy_5133";
	public static final String NO_FILTERS_FOUND = "deploy_5134";
	public static final String MISSING_RESOURCES = "deploy_5135";
	public static final String START_INITIALLY_NOT_FINISHED = "deploy_5136";
	public static final String PARSING_DELOYED_RESOURCES = "deploy_5137";
	public static final String CHANGE_SERVER_MODE_AND_ACTION = "deploy_5138";
	public static final String CANNOT_GET_OBJECT_FROM_MESSAGE = "deploy_5139";
	public static final String START_APP_ON_RESOURCE_ACTIVATION = "deploy_5140";
	public static final String RESTART_APP = "deploy_5141";
	public static final String REGISTER_TRANSACTION = "deploy_5142";
	public static final String UNREGISTER_TRANSACTION = "deploy_5143";

	//
	// DEPLOYMETN INFO
	//
	public static final String DI_ACTOR_MISSING = "deploy_5200";
	public static final String DI_ACTOR_DUPLICATED = "deploy_5201";
	public static final String DI_WRONG_APP_NAME = "deploy_5202";
	public static final String DI_MISSING_CFG = "deploy_5203";
	public static final String DI_MODULE_READ_FROM_IS = "deploy_5204";
	public static final String DI_NO_DEPL_COMP = "deploy_5205";

	//
	// CONFIGURATION
	//
	public static final String CFG_CANNOT_READ = "deploy_5300";

	//
	// VALIDATION
	//
	public static final String VAL_APP = "deploy_5400";
	public static final String VAL_APP_NOT_POSSIBLE = "deploy_5401";
	public static final String VAL_APP_RESULT = "deploy_5402";

	//
	// RUNTIME GENERATION
	//
	public static final String FILE_NOT_PRESENT = "deploy_5501";
	
}
