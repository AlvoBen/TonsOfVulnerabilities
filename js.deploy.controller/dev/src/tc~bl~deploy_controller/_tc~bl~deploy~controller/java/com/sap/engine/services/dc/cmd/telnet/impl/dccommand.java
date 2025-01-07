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
package com.sap.engine.services.dc.cmd.telnet.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cmd.telnet.impl.util.Argument;
import com.sap.engine.services.dc.cmd.telnet.impl.util.TelnetConstants;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCChangeLog;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * Common class for all deploy controller Telnet commands
 * <p>
 * NOTE: All write operations must be tracked using <code>DCChangeLog</code>
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public abstract class DCCommand implements Command {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final static String DC_CMD_GROUP = "DEPLOY";
	private final static String[] SUPPORTED_SHELL_PROVIDERS = { "InQMyShell" };
	protected final static String VIA_TELNET = "Telnet";

	protected final static String PROGRESS_SDA = " (sda)";
	protected final static String PROGRESS_SCA = " (SCA)";
	protected final static String PROGRESS_NO_SDU = " (noSDU)";

	private final static Set serverOnlineSet;
	private final static Set serverOfflineSet;
	static {
		final ServerService serverService = ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());
		if (serverService == null
				|| !(serverService instanceof SoftwareTypeService)) {
			serverOnlineSet = new HashSet();
			serverOnlineSet.add(new String("N/A"));
			serverOfflineSet = new HashSet();
			serverOfflineSet.add(new String("N/A"));
		} else {
			final SoftwareTypeService stService = (SoftwareTypeService) serverService;
			serverOnlineSet = stService.getOnlineSoftwareTypes();
			serverOfflineSet = stService.getOfflineSoftwareTypes();
		}
	}

	protected DCChangeLog dcChangeLog = new DCChangeLog();

	protected static String getServerOfflineSet() {
		return getSetToString(serverOfflineSet);
	}

	protected static String getServerOnlineSet() {
		return getSetToString(serverOnlineSet);
	}

	protected static String getComponentVersionHandlingRules() {
		final Iterator name_VerRule = ComponentVersionHandlingRule
				.getNameAndComponentVersionHandlingRule().keySet().iterator();

		Set userSet = new HashSet();
		while (name_VerRule.hasNext()) {
			userSet
					.add(TelnetConstants
							.getTelnetConstantByDcConstant((String) name_VerRule
									.next()));

		}
		return getSetToString(userSet);
	}

	protected static String getErrorStrategies() {
		final Iterator name_ErrStrategy = ErrorStrategy
				.getNameAndErrorStrategy().keySet().iterator();

		Set userSet = new HashSet();
		while (name_ErrStrategy.hasNext()) {
			userSet.add(TelnetConstants
					.getTelnetConstantByDcConstant((String) name_ErrStrategy
							.next()));

		}
		return getSetToString(userSet);
	}

	protected static String getDeployWorkflowStrategies() {
		final Iterator name_DeployWorkflowStrategy = DeployWorkflowStrategy
				.getNameAndDeployWorkflowStrategy().keySet().iterator();

		Set userSet = new HashSet();
		while (name_DeployWorkflowStrategy.hasNext()) {
			userSet
					.add(TelnetConstants
							.getTelnetConstantByDcConstant((String) name_DeployWorkflowStrategy
									.next()));

		}
		return getSetToString(userSet);
	}

	protected static String getUndeployWorkflowStrategies() {
		final Iterator name_UndeployWorkflowStrategy = UndeployWorkflowStrategy
				.getNameAndUndeployWorkflowStrategy().keySet().iterator();

		Set userSet = new HashSet();
		while (name_UndeployWorkflowStrategy.hasNext()) {
			userSet
					.add(TelnetConstants
							.getTelnetConstantByDcConstant((String) name_UndeployWorkflowStrategy
									.next()));

		}
		return getSetToString(userSet);
	}

	protected static String getLifeCycleDeployStrategies() {
		final Iterator name_LifeCycleDeployStrategy = LifeCycleDeployStrategy
				.getNameAndLifeCycleDeployStrategy().keySet().iterator();

		Set userSet = new HashSet();
		while (name_LifeCycleDeployStrategy.hasNext()) {
			userSet
					.add(TelnetConstants
							.getTelnetConstantByDcConstant((String) name_LifeCycleDeployStrategy
									.next()));

		}
		return getSetToString(userSet);
	}

	protected static String getSpaces(String str) {
		final String space = " ";
		StringBuffer result = new StringBuffer();
		if (str != null) {
			final int index = str.length();
			result.append(space);
			for (int i = 0; i < index; i++) {
				result.append(space);
			}
		}
		return result.toString();
	}

	protected static String getValue(String prop, String key) {
		if (prop.startsWith(key)) {
			return prop.substring(key.length());
		} else {
			return null;
		}
	}

	private static String getSetToString(Set set) {
		final StringBuffer result = new StringBuffer();
		if (set != null && set.size() != 0) {
			final Iterator namesIter = set.iterator();
			String name;
			boolean isStarted = false;
			while (namesIter.hasNext()) {
				if (isStarted) {
					result.append("|");
				} else {
					isStarted = !isStarted;
				}
				name = namesIter.next().toString();
				result.append(name);
			}
		}
		return result.toString();
	}

	// ****************************** COMMAND
	// ***********************************
	protected CM cm = null;
	protected PrintWriter pw = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.interfaces.shell.Command#getGroup()
	 */
	public String getGroup() {
		return DC_CMD_GROUP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.interfaces.shell.Command#getSupportedShellProviderNames()
	 */
	public String[] getSupportedShellProviderNames() {
		return SUPPORTED_SHELL_PROVIDERS;
	}

	protected String getDefaultHelpMessage() {
		return Constants.EOL + "Use -h option to see the usage of the command.";
	}

	// ****************************** LOG ************************************
	protected void init(OutputStream output) {
		pw = new PrintWriter(output, true);
	}

	protected void print(String message) {
		pw.print(message);
		pw.flush();
	}

	protected void printAndTraceDebug(String message) {
		printAndTraceDebug(message, null);
	}

	protected void printAndTraceDebug(String message,
			Object[] args) {
		pw.print(MessageFormat.format(message, args));
		pw.flush();

		if (location.beDebug()) {
			traceDebug(location, message, args);
		}
	}

	protected void println(String message) {
		pw.println(message);
		pw.flush();
	}

	protected void printlnAndTraceDebug(String message) {
		printlnAndTraceDebug(message, null);
	}

	protected void printlnAndTraceDebug(String message,	Object[] args) {
		pw.println(MessageFormat.format(message, args));
		pw.flush();

		if (location.beDebug()) {
			traceDebug(location, message, args);
		}
	}

	protected void println(Throwable th) {
		pw.println(th.getClass().getName() + ": " + th.getLocalizedMessage());
		pw.flush();
		DCLog.logErrorThrowable(location, th);
	}

	protected void println(WrongParameterException wpe) {
		pw.println(wpe.getLocalizedMessage());
		pw.flush();

		if (location.beDebug()) {
			traceDebug(location, "{0}", new Object[] { wpe
					.getStackTraceString() });
		}
	}

	// ****************************** INIT ************************************
	protected ErrorStrategy initErrorStrategy(Argument[] arguments,
			String action) throws WrongParameterException {
		for (int i = 0; i < arguments.length; i++) {
			if (action.equalsIgnoreCase(arguments[i].getKey())) {
				final String actionHandling = TelnetConstants
						.getDcConstantByTelnetConstant(arguments[i].getValue());
				ErrorStrategy actionErrStrategy = null;
				if (actionHandling != null) {
					actionErrStrategy = ErrorStrategy
							.getErrorStrategyByName(actionHandling);
					if (actionErrStrategy == null) {
						handleWrongArgumentValue(action, actionHandling,
								getErrorStrategies());
					}
				}
				return actionErrStrategy;
			}
		}
		return null;
	}

	protected DeployWorkflowStrategy initDeployWorkflowStrategy(
			final Argument[] arguments, final String action)
			throws WrongParameterException {
		for (int i = 0; i < arguments.length; i++) {
			if (action.equalsIgnoreCase(arguments[i].getKey())) {
				final String actionHandling = TelnetConstants
						.getDcConstantByTelnetConstant(arguments[i].getValue());
				DeployWorkflowStrategy deployWorkflowStrategy = null;
				if (actionHandling != null) {
					deployWorkflowStrategy = DeployWorkflowStrategy
							.getDeployStrategyByName(actionHandling);
					if (deployWorkflowStrategy == null) {
						handleWrongArgumentValue(action, actionHandling,
								getDeployWorkflowStrategies());
					}
				}
				return deployWorkflowStrategy;
			}
		}
		return null;
	}

	protected UndeployWorkflowStrategy initUndeployWorkflowStrategy(
			final Argument[] arguments, final String action)
			throws WrongParameterException {
		for (int i = 0; i < arguments.length; i++) {
			if (action.equalsIgnoreCase(arguments[i].getKey())) {
				final String actionHandling = TelnetConstants
						.getDcConstantByTelnetConstant(arguments[i].getValue());
				UndeployWorkflowStrategy undeployWorkflowStrategy = null;
				if (actionHandling != null) {
					undeployWorkflowStrategy = UndeployWorkflowStrategy
							.getUndeployWorkflowStrategyByName(actionHandling);
					if (undeployWorkflowStrategy == null) {
						handleWrongArgumentValue(action, actionHandling,
								getUndeployWorkflowStrategies());
					}
				}
				return undeployWorkflowStrategy;
			}
		}
		return null;
	}

	protected LifeCycleDeployStrategy initLifeCycleDeployStrategy(
			final Argument[] arguments, final String action)
			throws WrongParameterException {
		for (int i = 0; i < arguments.length; i++) {
			if (action.equalsIgnoreCase(arguments[i].getKey())) {
				final String actionHandling = TelnetConstants
						.getDcConstantByTelnetConstant(arguments[i].getValue());
				LifeCycleDeployStrategy lifeCycleDeployStrategy = null;
				if (actionHandling != null) {
					lifeCycleDeployStrategy = LifeCycleDeployStrategy
							.getLifeCycleDeployStrategyByName(actionHandling);
					if (lifeCycleDeployStrategy == null) {
						handleWrongArgumentValue(action, actionHandling,
								getLifeCycleDeployStrategies());
					}
				}
				return lifeCycleDeployStrategy;
			}
		}
		return null;
	}

	// ****************************** HANDLE
	// ************************************
	protected WrongParameterException handleWrongArgumentValue(String type,
			String value, String values) throws WrongParameterException {
		throw new WrongParameterException(
				DCExceptionConstants.CMD_WRONG_ARG_VALUE, new String[] { type,
						value, values });
	}

	protected WrongParameterException handleMissingArgument(String argument,
			String command) throws WrongParameterException {
		throw new WrongParameterException(
				DCExceptionConstants.CMD_MISSING_ARGUMENT, new String[] {
						argument, command });
	}

	protected void logCommand(String cmd, String[] params) {
		StringBuffer userCommandLine = new StringBuffer(cmd);
		userCommandLine.append(" ");
		for (int i = 0; i < params.length; i++) {
			userCommandLine.append("{");
			userCommandLine.append(params[i]);
			userCommandLine.append("}");
		}

		if (location.beDebug()) {
			traceDebug(location, "Telnet command is invoked: [{0}]",
				new Object[] { userCommandLine.toString() });
		}
	}

}
