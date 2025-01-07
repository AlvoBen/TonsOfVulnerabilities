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

package com.sap.engine.services.deploy;

import java.io.Serializable;

import com.sap.engine.services.deploy.server.utils.DSConstants;

/* This class belongs to the public API of the DeployService project. */
/**
 * DeployEvent represents an action carried out by Deploy Service. It stores
 * information about the type and the phase of the action, the component
 * involved in the action and the server that initiates it. Some additional
 * message regarding the action might be provided.
 * 
 * @author Rumiana Angelova
 */
public final class DeployEvent implements Serializable {

	static final long serialVersionUID = -2169771414910936940L;

	// *** ACTION PHASES ***
	//
	/**
	 * Indicates the beginning of a global action concerning all servers in
	 * cluster.
	 */
	public static final byte ACTION_START = 0;
	/**
	 * Indicates the finishing of a global action concerning all servers in
	 * cluster.
	 */
	public static final byte ACTION_FINISH = 1;
	/**
	 * Indicates the beginning of a local action concerning one server.
	 */
	public static final byte LOCAL_ACTION_START = 2;
	/**
	 * Indicates the finishing of a local action concerning one server.
	 */
	public static final byte LOCAL_ACTION_FINISH = 3;
	//
	// *** ACTION PHASES ***

	// *** ACTION TYPES ***
	//

	/**
	 * WARNING: ACTION_TYPE_MIN must be equals to the lowest action type
	 * constant.
	 */
	public static final byte ACTION_TYPE_MIN = 0;
	/**
	 * Action type for deploying an application.
	 */
	public static final byte DEPLOY_APP = 0;
	/**
	 * Action type for deploying a library.
	 */
	public static final byte DEPLOY_LIB = 1;
	/**
	 * Action type for removing a library.
	 */
	public static final byte REMOVE_LIB = 2;
	/**
	 * Action type for deploying a language library.
	 */
	public static final byte DEPLOY_LANG_LIB = 3;
	/**
	 * Action type for creating references from an application to server
	 * components.
	 */
	public static final byte MAKE_REFS = 4;
	/**
	 * Action type for removing references from an application to server
	 * components.
	 */
	public static final byte REMOVE_REFS = 5;
	/**
	 * Action type for updating an application.
	 */
	public static final byte UPDATE_APP = 6;
	/**
	 * Action type for removing an application.
	 */
	public static final byte REMOVE_APP = 7;
	/**
	 * Action type for stopping an application.
	 */
	public static final byte STOP_APP = 8;
	/**
	 * Action type for starting an application.
	 */
	public static final byte START_APP = 9;
	/**
	 * Action type for making runtime changes to an application.
	 */
	public static final byte RUNTIME_CHANGES = 10;
	/**
	 * Action type for updating single files of an application.
	 * 
	 */
	public static final byte SINGLE_FILE_UPDATE = 11;
	/**
	 * Action type for changing additional info for an application.
	 */
	public static final byte ADD_APP_INFO_CHANGE = 12;
	/**
	 * Action type for notification of the initial starting of all applications,
	 * which have to be started after the startup of a server node.
	 */
	public static final byte INITIAL_START_APPLICATIONS = 13;
	/**
	 * Action type for notification of registered container interface.
	 */
	public static final byte REGISTER_CONTAINER_INTERFACE = 14;
	/**
	 * Action type for notification of registered container interface.
	 */
	public static final byte UNREGISTER_CONTAINER_INTERFACE = 15;
	/**
	 * Action type for starting initially an application.
	 */
	public static final byte START_INITIALLY = 16;

	/**
	 * WARNING: ACTION_TYPE_MAX must be equals to the highest action type
	 * constant.
	 */
	public static final byte ACTION_TYPE_MAX = 16;
	//
	// *** ACTION TYPES ***

	// *** APPLICATION STATUS ***
	//
	/**
	 * Unknown application status.
	 */
	public static final byte UNKNOWN_STATUS = -1;
	/**
	 * Started application status.
	 */
	public static final byte STARTED_STATUS = 0;
	/**
	 * Stopped application status.
	 */
	public static final byte STOPPED_STATUS = 1;
	/**
	 * Removed application status.
	 */
	public static final byte REMOVED_STATUS = 2;
	/**
	 * Implicit stopped application status.
	 */
	public static final byte IMPLICIT_STOPPED_STATUS = 3;
	/**
	 * Starting application status.
	 */
	public static final byte STARTING_STATUS = 4;
	/**
	 * Stopping application status.
	 */
	public static final byte STOPPING_STATUS = 5;
	/**
	 * Upgrading application status.
	 */
	public static final byte UPGRADING_STATUS = 6;
	//
	// *** APPLICATION STATUS ***

	// *** FIELDS ***
	//
	private byte action;
	private byte actionType;
	private String fromServer;
	private String componentName = null;
	private String message = null;
	private String errors[] = null;
	private String warnings[] = null;
	private byte componentStatus = UNKNOWN_STATUS;
	private String whoCausedGroupOperation = null;

	//
	// *** FIELDS ***

	/**
	 * Constructor of the class specifying component name, action phase, action
	 * type, server and message. Creates a DeployEvent representing an action -
	 * its phase and type, the component involved in the action and the server
	 * that initiates it; also some short message regarding the process, i.e. if
	 * errors and/or warnings occurred during action execution.
	 * 
	 * @param compName
	 *            the name of the component involved in the action.
	 * @param action
	 *            the phase of the action.
	 * @param actionType
	 *            the type of the action.
	 * @param fromServer
	 *            the name of the server that initiates the action.
	 */
	public DeployEvent(String compName, byte action, byte actionType,
			String fromServer) {
		this.componentName = compName;
		this.action = action;
		this.actionType = actionType;
		this.fromServer = fromServer;
	}

	/**
	 * Returns the name of the component involved in the action. Its type might
	 * be application, library, etc.
	 * 
	 * Note: This name is null, if the event represents beginning or end of
	 * operation over multiple components (group operation). If the operation is
	 * over one component, then this method returns its name.
	 * 
	 * @return the name of the component.
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * Returns the action phase, which might be one of the following:
	 * <code>ACTION_START, ACTION_FINISH, LOCAL_ACTION_START, LOCAL_ACTION_FINISH </code>
	 * .
	 * 
	 * @return the phase of the action represented by this event.
	 */
	public byte getAction() {
		return action;
	}

	/**
	 * Sets the action phase, which might be one of the following:
	 * <code>ACTION_START, ACTION_FINISH, LOCAL_ACTION_START, LOCAL_ACTION_FINISH </code>
	 * .
	 * 
	 * @param action
	 *            action phase
	 */
	public void setAction(byte action) {
		this.action = action;
	}

	/**
	 * Returns the action type, which might be one of the following::
	 * <code>DEPLOY_APP,
	 * DEPLOY_LIB, REMOVE_LIB, DEPLOY_LANG_LIB, MAKE_REFS, REMOVE_REFS,
	 * UPDATE_APP, REMOVE_APP, STOP_APP, START_APP,
	 * RUNTIME_CHANGES, SINGLE_FILE_UPDATE, ADD_APP_INFO_CHANGE </code>.
	 * 
	 * @return the type of the action represented by this event.
	 */
	public byte getActionType() {
		return actionType;
	}

	/**
	 * Returns the name of the server that initiates the action represented by
	 * this event.
	 * 
	 * @return the name of the server that initiates the action.
	 */
	public String getServer() {
		return fromServer;
	}

	/**
	 * Returns a short message that describes the action represented by this
	 * event.
	 * 
	 * @return the message describing the action.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets short message describing the action represented by this event.
	 * 
	 * @param message
	 *            to be set
	 */
	public void setMessage(String msg) {
		this.message = msg;
	}

	/**
	 * Returns errors occurred during execution of current action.
	 * 
	 * @return errors occurred during execution of current action.
	 */
	public String[] getErrors() {
		return errors;
	}

	/**
	 * Sets errors, which occurred during execution of current action.
	 * 
	 * @param array
	 *            of error messages
	 */
	public void setErrors(String[] err) {
		this.errors = err;
	}

	/**
	 * Returns warnings occurred during execution of current action.
	 * 
	 * @return warnings occurred during execution of current action.
	 */
	public String[] getWarnings() {
		return warnings;
	}

	/**
	 * Sets warnings, which occurred during execution of current action.
	 * 
	 * @param array
	 *            of warnings
	 */
	public void setWarnings(String[] wrn) {
		this.warnings = wrn;
	}

	/**
	 * Returns String representation of this event.
	 * 
	 * @return a string representing this event with its main properties.
	 */
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("Deploy Event:" + DSConstants.EOL);

		res.append("\tPhase          : " + this.getActionAsString()
				+ DSConstants.EOL);
		res.append("\tOperation      : " + this.getActionTypeAsString()
				+ DSConstants.EOL);
		res
				.append("\tComponent Name : " + this.componentName
						+ DSConstants.EOL);
		res.append("\tFrom Server    : " + this.fromServer + DSConstants.EOL);
		res.append("\tWho			 : " + this.whoCausedGroupOperation
				+ DSConstants.EOL);
		res.append("\tMessage        : " + this.message + DSConstants.EOL);
		res.append("\tStatus         : " + getStatus(this.componentStatus)
				+ DSConstants.EOL);
		res.append("\tErrors         : "
				+ concatStrings(this.errors, "         "));
		res.append("\tWarnings       : "
				+ concatStrings(this.warnings, "         "));

		return res.toString();
	}

	private String concatStrings(String source[], String ballast) {
		final StringBuilder result = new StringBuilder("");
		if (source != null && source.length > 0) {
			for (int i = 0; i < source.length; i++) {
				if (i != 0) {
					result.append(ballast);
				}
				result.append("\n");
			}
		} else {
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Sets the status of the component involved in the action.
	 * 
	 * @param status
	 *            the component status, which might be one of the following:
	 * 
	 *            <code>UNKNOWN_STATUS, STARTED_STATUS, STOPPED_STATUS, REMOVED_STATUS,
	 * IMPLICIT_STOPPED_STATUS, STARTING_STATUS, STOPPING_STATUS, UPGRADING_STATUS</code>
	 *            .
	 */
	public void setStatus(byte status) {
		this.componentStatus = status;
	}

	/**
	 * Returns the status of the component involved in the action.
	 * 
	 * @return component status.
	 */
	public byte getStatus() {
		return this.componentStatus;
	}

	/**
	 * Sets the initiator of a group operation, described with the event. The
	 * operation is group, when the component name is null, which means that the
	 * event notifies about more components (more than one).
	 * 
	 * @param who
	 *            - the initiator, which can be <code>deploy</code> or container
	 *            name.
	 */
	public void setWhoCausedGroupOperation(String who) {
		this.whoCausedGroupOperation = who;
	}

	/**
	 * Returns the initiator of a group operation, described with the event. The
	 * operation is group, when the component name is null, which means that the
	 * event notifies about more components (more than one).
	 * 
	 * @return who - the initiator, which can be <code>deploy</code> or
	 *         container name.
	 */
	public String whoCausedGroupOperation() {
		return this.whoCausedGroupOperation;
	}

	/**
	 * Returns a String representation of the component status.
	 * 
	 * @param status
	 *            component status as byte.
	 * 
	 * @return a string representing the component status.
	 */
	public String getStatus(byte status) {
		switch (status) {
		case DeployEvent.STARTED_STATUS:
			return DeployService.STARTED_APP_STATUS;
		case DeployEvent.STOPPED_STATUS:
			return DeployService.STOPPED_APP_STATUS;
		case DeployEvent.STARTING_STATUS:
			return DeployService.STARTING_APP_STATUS;
		case DeployEvent.STOPPING_STATUS:
			return DeployService.STOPPING_APP_STATUS;
		case DeployEvent.IMPLICIT_STOPPED_STATUS:
			return DeployService.IMPLICIT_STOPPED_APP_STATUS;
		case DeployEvent.UPGRADING_STATUS:
			return DeployService.UPGRADING_APP_STATUS;
		case DeployEvent.UNKNOWN_STATUS:
			return DeployService.UNKNOWN_APP_STATUS;
		default:
			return DeployService.UNKNOWN_APP_STATUS;
		}
	}

	private String getActionAsString() {
		switch (action) {
		case ACTION_START:
			return "global start";
		case ACTION_FINISH:
			return "global finish";
		case LOCAL_ACTION_START:
			return "local start";
		case LOCAL_ACTION_FINISH:
			return "local finish";
		default:
			return "unknown";
		}
	}

	private String getActionTypeAsString() {
		switch (actionType) {
		case DEPLOY_APP:
			return "deploy an application";
		case DEPLOY_LIB:
			return "deploy a library";
		case REMOVE_LIB:
			return "remove a library";
		case DEPLOY_LANG_LIB:
			return "deploy a language library";
		case MAKE_REFS:
			return "make reference(s)";
		case REMOVE_REFS:
			return "remove reference(s)";
		case UPDATE_APP:
			return "update an application";
		case REMOVE_APP:
			return "remove an application";
		case STOP_APP:
			return "stop an application";
		case START_APP:
			return "start an application";
		case RUNTIME_CHANGES:
			return "runtime changes over an application";
		case SINGLE_FILE_UPDATE:
			return "single file update over an application";
		case ADD_APP_INFO_CHANGE:
			return "change of additional information of application";
		case INITIAL_START_APPLICATIONS:
			return "initial start of applications during server startup";
		case START_INITIALLY:
			return "start initially";
		default:
			return "unknown";
		}
	}

}
