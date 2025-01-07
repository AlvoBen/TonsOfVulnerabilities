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
package com.sap.engine.services.dc.api.util.exception;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Enter description here</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Mar 11, 2005</DD>
 * </DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 */

public final class APIExceptionConstants {
	/**
	 * {0}
	 */
	public static final String UNFORMATED = "da_000";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_DEPLOYMENT_EXCEPTION = "da_200";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_VALIDATION_EXCEPTION = "da_205";
	/**
	 * {0}.\nReason: {1}
	 */
	// public static final String DC_LOCK_EXCEPTION = "da_210";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_CM_EXCEPTION = "da_215";
	/**
	 * Deploy item #{0}. The specified file '{1}' does not exist.
	 */
	public static final String TRANSPORT_NOT_EXISTS = "da_220";
	/**
	 * Deploy item #{0}. The specified file '{1}' cannot not be read.
	 */
	public static final String TRANSPORT_CANNOT_READ = "da_225";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String TRANSPORT_IO_EXCEPTION = "da_230";
	/**
	 * Deploy API could not retrieve Un/deploy Result after {0} ms.
	 */
	public static final String OFFLINE_ENGINE_TIMEOUTED = "da_235";
	/**
	 * Deploy Result not found.Reason: {0}.
	 */
	public static final String DC_DEPLOYRESULTNOTFOUND_EXCEPTION = "da_240";
	/**
	 * Deploy process passed but there is no result.
	 */
	public static final String DC_NODEPLOYRESULT_EXCEPTION = "da_245";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_REPOSITORYEXPLORING_EXCEPTION = "da_250";
	/**
	 * Remote Repository Explorer not created.
	 */
	public static final String REPOSITORYEXPLORERNOTCREATED = "da_255";
	// params exceptions
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_REMOTEPARAMSFACTORY_EXCEPTION = "da_260";
	/**
	 * Parameter '{0}' already exists
	 */
	public static final String DC_PARAMALREADYEXISTS_EXCEPTION = "da_265";
	/**
	 * {0}.Parameter '{1}'.\nReason={2}
	 */
	public static final String DC_PARAMS_EXCEPTION_WITH_INFO = "da_270";
	/**
	 * {0}.Parameter '{1}'.\nReason: {1}
	 */
	public static final String DC_REMOTEPARAMSFACTORY_EXCEPTION_WITH_INFO = "da_275";
	/**
	 * Parameter already exists.\nReason: {0}
	 */
	public static final String DC_PARAMALREADYEXISTS_EXCEPTION_WITH_INFO = "da_280";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_PARAMS_EXCEPTION = "da_285";
	/**
	 * Parameter '{0}' not found.\nReason: {1}
	 */
	public static final String DC_PARAMNOTFOUND_EXCEPTION_WITH_INFO = "da_290";
	/**
	 * Cannot create Remote Params Factory.
	 */
	public static final String PARAMS_CANNOT_CREATE_REMOTE_FACTORY = "da_295";
	/**
	 * Parameter not found.\nReason: {0}
	 */
	public static final String DC_PARAMNOTFOUND_EXCEPTION = "da_300";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_CONNECTION_EXCEPTION = "da_305";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_CM_SELFCHECKER_EXCEPTION = "da_310";
	/**
	 * Self Check Result not found.
	 */
	public static final String DC_SELFCHECKRESULT_NOTFOUND = "da_315";
	/**
	 * Authentication Exception.Reason: {0}
	 */
	public static final String AUTH_EXCEPTION = "da_320";

	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_CRC_VALIDATION_EXCEPTION = "da_325";

	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_ALL_ITEMS_FILTERED_VALIDATION_EXCEPTION = "da_326";

	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_ALL_ITEMS_ALREADY_DEPLOYED_VALIDATION_EXCEPTION = "da_327";

	/**
	 * Undeploy process passed but there is no result.
	 */
	public static final String DC_NOUNDEPLOYRESULT_EXCEPTION = "da_330";
	/**
	 * Undeploy result not founde exception.\nReason:{0}
	 */
	public static final String DC_UNDEPLOYRESULTNOTFOUND_EXCEPTION = "da_335";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_UNDEPLOYMENT_EXCEPTION = "da_340";
	/**
	 * {0}.\nReason: {1}
	 */
	public static final String DC_REMOTE_EXCEPTION = "da_345";
	/**
	 * Unsupported Batch Filter type :{0}
	 */
	public static final String DA_UNSUPPORTEDBATCHFILTER_EXCEPTION = "da_350";
	/**
	 * The SAP J2EE ENGINE service ''{0}'' was looked up, but its value is null.
	 */
	public static final String DA_CANNOT_LOOKUP_SERVICE = "da_355";
	/**
	 * {0}.The SAP J2EE ENGINE service ''{1}'' is not available because of
	 * deployment or down engine( service ).\nReason: {2}
	 */
	public static final String DA_SERVICE_IS_NOT_RUNNING = "da_360";
	/**
	 * {0}.Cannot get initial context.\nReason: {1}
	 */
	public static final String DA_CANNOT_GET_CONTEXT = "da_365";
	/**
	 * Cannot get the Archive Manager.\nReason: {0}
	 */
	public static final String DA_CANNOT_GET_ARCHIVE_MNG = "da_370";
	/**
	 * An error occurred while uploading the archive for the component with name
	 * {0} and vendor {1} to the location {2}.\nReason: {3}
	 */
	public static final String DA_CANNOT_GET_ARCHIVE_PATH = "da_375";
	/**
	 * An error occurred while getting the sources archive path for the
	 * component with name {0} and vendor {1} to the location {2}.\nReason: {3}
	 */
	public static final String DA_CANNOT_GET_SRC_ARCHIVE_PATH = "da_377";
	/**
	 * Cannot get the Deploy Controller Component Manager.\nReason: {1}
	 */
	public static final String DA_CANNOT_DC_CM = "da_380";
	/**
	 * Cannot generate session id.\nReason: {1}
	 */
	public static final String DA_CANNOT_GEN_SESSION_ID = "da_385";
	/**
	 * An error occurred while creating remote file via the file transfer
	 * service. The local file is {0}, the remote file is {1}
	 */
	public static final String DA_REMOTE_FILE_GEN_ERR = "da_390";
	/**
	 * An error occurred while downloading the archive for the component with
	 * name {0} and vendor {1} from the Engine location {2} to the local
	 * location {3}.\nReason: {4}
	 */
	public static final String DA_DOWNLOAD_ERR = "da_395";
	/**
	 * An error occurred while triggernig archive garbage collection on the
	 * Engine side for the file '{0}'.\nReason: {1}
	 */
	public static final String DA_CANNOT_GC = "da_400";
	/**
	 * Cannot get the Life Cycle Manager.\nReason: {0}
	 */
	public static final String DA_CANNOT_GET_LCM = "da_405";
	/**
	 * Cannot add Life Cycle Manager listener.\nReason: {0}
	 */
	public final static String DA_CANNOT_ADD_LCM_LISTENER = "da_407";
	/**
	 * Cannot remove Life Cycle Manager listener.\nReason: {0}
	 */
	public final static String DA_CANNOT_REMOVE_LCM_LISTENER = "da_409";
	/**
	 * Cannot start the component with name {0} and vendor {1}.\nReason: {2}
	 */
	public static final String DA_CANNOT_START_COMP = "da_410";
	/**
	 * Cannot stop the component with name {0} and vendor {1}.\nReason: {2}
	 */
	public static final String DA_CANNOT_STOP_COMP = "da_415";
	/**
	 * The component with name {0} and vendor {1} does not exist.\nReason: {2}
	 */
	public static final String DA_COMP_NOT_EXIST = "da_420";
	/**
	 * Cannot get the LCM status for the component with name {0} and vendor
	 * {1}.\nReason: {2}
	 */
	public static final String DA_CANNOT_GET_LCM_STATUS = "da_425";
	/**
	 * Exception on getting sda statuses.\nReason: {0}
	 */
	public static final String DA_GET_LCM_STATUSES_EXCEPTION = "da_427";
	/**
	 * Cannot lock the Deploy Controller with action {0}. It is already
	 * locked.\nReason: {1}
	 */
	public static final String DA_ALREADY_LOCKED = "da_430";
	/**
	 * Cannot lock the Deploy Controller with action {0}.\nReason: {1}
	 */
	public static final String DA_CANNOT_LOCK = "da_435";
	/**
	 * Cannot unlock the Deploy Controller with action {0}. No such lock has
	 * been found.\nReason: {1}
	 */
	public static final String DA_NO_SUCH_LOCK = "da_440";
	/**
	 * Cannot unlock the Deploy Controller with action {0}. \nReason: {1}
	 */
	public static final String DA_CANNOT_UNLOCK = "da_445";
	/**
	 * da_450=Cannot get the Lock Manager.\nReason: {0}
	 */
	public static final String DA_CANNOT_GET_LOCK_MNG = "da_450";
	/**
	 * Cannot perform action {0}.Deploy Controller is locked.\nReason: {1}
	 */
	public static final String DA_DEPLOY_CONTROLLER_IS_LOCKED = "da_455";
	/**
	 * Deploy Controller is binded but is not fully operational yet due to
	 * initialization or un/deployment performing at the moment ( usually online
	 * un/deployment after the offline phase).
	 */
	public static final String DC_NOT_OPERATIONAL_YET = "da_460";

	/**
	 * Exception durring rolling update
	 */
	public static final String ROLLING_EXCEPTION = "da_470";

	/**
	 * A problem occurred while waiting for the server to start. The process {0}
	 * with PID {1} exited with exit code {2} , {3}
	 */
	public static final String CRITICAL_SHUTDOWN_EXCEPTION = "da_480";

}
