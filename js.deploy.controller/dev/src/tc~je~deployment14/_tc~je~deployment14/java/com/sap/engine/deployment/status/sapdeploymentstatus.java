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
package com.sap.engine.deployment.status;

import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.spi.status.DeploymentStatus;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.exceptions.ExceptionConstants;
import com.sap.engine.deployment.exceptions.SAPIllegalArgumentsException;
import com.sap.engine.deployment.Logger;

/**
 * The DeploymentStatus interface provides information about the progress status
 * of a deployment action.
 * 
 * @author Mariela Todorova
 */
public class SAPDeploymentStatus implements DeploymentStatus {
	private static final Location location = Location
			.getLocation(SAPDeploymentStatus.class);
	private StateType state = null;
	private CommandType command = null;
	private ActionType action = null;
	private String message = "";

	public SAPDeploymentStatus(CommandType cmd)
			throws SAPIllegalArgumentsException {
		setCommand(cmd);
		Logger.trace(location, Severity.PATH,
				"Initiating new deployment status for " + cmd.toString()
						+ " command");
		this.action = ActionType.EXECUTE;
		this.state = StateType.RUNNING;
	}

	private void setCommand(CommandType cmd)
			throws SAPIllegalArgumentsException {
		if (cmd == null) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.PARAMETER_NULL,
					new String[] { "command" });
		}

		this.command = cmd;
	}

	/**
	 * Retrieve the StateType value.
	 * 
	 * @return the StateType object
	 */
	public StateType getState() {
		Logger.trace(location, Severity.DEBUG, "Current operation state is "
				+ state);
		return this.state;
	}

	/**
	 * Retrieve the deployment CommandType of this event.
	 * 
	 * @return the CommandType Object
	 */
	public CommandType getCommand() {
		Logger.trace(location, Severity.DEBUG, "Command type " + command);
		return this.command;
	}

	/**
	 * Retrieve the deployment ActionType for this event.
	 * 
	 * @return the ActionType Object
	 */
	public ActionType getAction() {
		Logger.trace(location, Severity.DEBUG, "Action type " + action);
		return this.action;
	}

	/**
	 * Retrieve any additional information about the status of this event.
	 * 
	 * @return message text
	 */
	public String getMessage() {
		Logger.trace(location, Severity.DEBUG, "Message: " + message);
		return this.message;
	}

	/**
	 * A convience method to report if the operation is in the completed state.
	 * 
	 * @return true if this command has completed successfully
	 */
	public boolean isCompleted() {
		Logger.trace(location, Severity.DEBUG, StateType.COMPLETED
				.equals(state) ? "Operation completed" : "Operation " + state);
		return StateType.COMPLETED.equals(state);
	}

	/**
	 * A convience method to report if the operation is in the failed state.
	 * 
	 * @return true if this command has failed
	 */
	public boolean isFailed() {
		Logger.trace(location, Severity.DEBUG,
				StateType.FAILED.equals(state) ? "Operation failed"
						: "Operation " + state);
		return StateType.FAILED.equals(state);
	}

	/**
	 * A convience method to report if the operation is in the running state.
	 * 
	 * @return true if this command is still running
	 */
	public boolean isRunning() {
		Logger.trace(location, Severity.DEBUG,
				StateType.RUNNING.equals(state) ? "Operation running"
						: "Operation " + state);
		return StateType.RUNNING.equals(state);
	}

	public void setStateType(StateType type) {
		Logger.trace(location, Severity.DEBUG, "Setting state " + type);

		if (type == null) {
			return;
		}

		this.state = type;
	}

	public void setActionType(ActionType type) {
		Logger.trace(location, Severity.DEBUG, "Setting action " + type);
		if (type == null) {
			return;
		}

		this.action = type;
	}

	public void setMessage(String msg) {
		Logger.trace(location, Severity.DEBUG, "Setting message " + msg);
		this.message = msg;
	}

}
