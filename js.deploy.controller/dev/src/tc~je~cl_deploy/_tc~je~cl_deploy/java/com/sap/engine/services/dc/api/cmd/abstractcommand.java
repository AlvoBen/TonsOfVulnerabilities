/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Sep 7, 2005
 */
package com.sap.engine.services.dc.api.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.cmd.util.CmdLogger;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.tc.logging.Severity;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Sep 7, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class AbstractCommand implements Command {
	protected static final String CLTOOL_PROPS_LOCATION = "com/sap/engine/services/dc/api/cmd/util/cltool.properties";

	private Client client;
	private CmdLogger logger;
	private DALog _daLog;
	private String[] argList;
	private StringBuffer description;
	private String host = null, user = null, password = null;
	private int port = -1;
	private String name;

	public final int init(String aName, String[] aArgList) {
		this.name = aName;
		this.argList = aArgList;
		this.logger = new CmdLogger(System.out, System.err);
		this._daLog = DALog.getInstance(this.logger, true);
		this.daLog().logInfo("ASJ.dpl_api.001257", "Log file is [{0}]",
				new Object[] { this.daLog().getCatPath() });
		this.daLog().logInfo("ASJ.dpl_api.001258", "Trace file is [{0}]",
				new Object[] { this.daLog().getLocPath() });
		this.daLog().logInfo("ASJ.dpl_api.001259", "Init command:[{0}]",
				new Object[] { getName() });
		this.logger.msgToTrace(CmdLogger.PATTERN_LOG_FILE_BEGIN + Command.EOL
				+ this.daLog().getCatPath() + Command.EOL
				+ CmdLogger.PATTERN_LOG_FILE_END);

		this.logger.msgToTrace(CmdLogger.PATTERN_TRACE_FILE_BEGIN + Command.EOL
				+ this.daLog().getLocPath() + Command.EOL
				+ CmdLogger.PATTERN_TRACE_FILE_END);

		return Command.CODE_SUCCESS;
	}

	public int execute() {
		String[] arguments = getArguments();
		String key, value;
		for (int i = 0; i < arguments.length; i += 2) {
			if (isArgSpecial(i)) {
				key = arguments[i];
				if (key == null || (key = key.trim()).length() == 0) {
					addDescription("Wrong option", true);
					return Command.CODE_CRITICAL_ERROR;
				}
			} else {
				addDescription("Unknown option '" + arguments[i] + "'.", true);
				return Command.CODE_CRITICAL_ERROR;
			}

			if ("-h".equals(key) || "--help".equals(key)) {
				usage();
				return Command.CODE_SUCCESS;
			}

			if (!hasNextArg(i + 1)) {
				addDescription("There is no value for the option '" + key
						+ "'.", true);
				return Command.CODE_CRITICAL_ERROR;
			}
			value = arguments[i + 1];
			if (value == null || (value = value.trim()).length() == 0) {
				addDescription("Wrong value for option '" + key + "'.", true);
				return Command.CODE_CRITICAL_ERROR;
			}
			if (isArgSpecial(i + 1)) {
				addDescription("There should be value for the option '" + key
						+ "' instead of another option ('" + value + " ').",
						true);
				return Command.CODE_CRITICAL_ERROR;
			}

			if ("--host".equals(key)) {
				this.host = value;
			} else if ("--port".equals(key)) {
				try {
					this.port = Integer.parseInt(value, 10);
				} catch (NumberFormatException nfe) {
					addDescription("Invalid port value:"
							+ nfe.getLocalizedMessage(), true);
					return Command.CODE_CRITICAL_ERROR;
				}
			} else if ("--user".equals(key)) {
				this.user = value;
			} else if ("--password".equals(key)) {
				this.password = value;
			} else {
				int ret = processOption(key, value);
				if (!isSuccess(ret)) {
					return ret;
				}
			}
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		// check host
		if (this.host == null || this.host.trim().length() == 0) {
			System.out.print("Enter host:");
			this.host = readStringFromConsole(in);
			if (this.host == null || this.host.length() == 0) {
				addDescription("Host name cannot be empty.", true);
				return Command.CODE_SYNTAX_INCORRECT;
			}
		}
		// check port
		if (this.port == -1) {
			System.out.print("Enter port:");
			String tmp = readStringFromConsole(in);
			if (tmp == null || tmp.length() == 0) {
				addDescription("Port is not valid.", true);
				return Command.CODE_SYNTAX_INCORRECT;
			}
			try {
				this.port = Integer.parseInt(tmp, 10);
			} catch (NumberFormatException nfe) {
				addDescription("Server Port should be valid integer value.",
						true);
				return Command.CODE_SYNTAX_INCORRECT;
			}
		}
		// check user
		if (this.user == null || this.user.trim().length() == 0) {
			System.out.print("Enter user:");
			this.user = readStringFromConsole(in);
			if (this.user == null || this.user.length() == 0) {
				addDescription("User name cannot be empty.", true);
				return Command.CODE_SYNTAX_INCORRECT;
			}
		}

		// check password
		if (this.password == null || this.password.trim().length() == 0) {
			System.out.print("Enter user password:");
			this.password = readStringFromConsole(in);
			if (this.password == null || this.password.length() == 0) {
				addDescription("User password name cannot be empty.", true);
				return Command.CODE_SYNTAX_INCORRECT;
			}
		}

		try {
			this.client = ClientFactory.getInstance().createClient(
					this.daLog(), this.host, this.port, this.user,
					this.password);
		} catch (ConnectionException e) {
			this.daLog().logThrowable(e);
			addDescription("Cannot connect[ ConnectionException ].Reason: "
					+ e.getLocalizedMessage(), true);
			return Command.CODE_PREREQUISITE_VIOLATED;
		} catch (AuthenticationException e) {
			this.daLog().logThrowable(e);
			addDescription("Cannot connect[ AuthenticationException ].Reason: "
					+ e.getLocalizedMessage() + Command.EOL/*
															 * +CmdLogger.
															 * getStackTrace(e)
															 */, true);
			return Command.CODE_PREREQUISITE_VIOLATED;
		}
		return executeCommand();
	}

	protected abstract int processOption(String key, String value);

	protected abstract int executeCommand();

	public void setSeverity(int severity) {
		this.logger.setSeverity(severity);
	}

	public int getSeverity() {
		return this.logger.getSeverity();
	}

	protected String[] getArguments() {
		return this.argList;
	}

	public String getDescription() {
		return this.description != null ? this.description.toString() : "";
	}

	protected Client getClient() {
		return this.client;
	}

	public final String getName() {
		return this.name;
	}

	public static final String getCanonicalFilePath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	public final void usage() {
		Properties props = CommandFactory.getInstance().getProperties();
		StringBuffer buffer = new StringBuffer(getName()).append(" ").append(
				props.getProperty("cmd.usage." + getName() + ".line",
						"Not available\r\n")).append(
				props.getProperty("cmd.usage.common", "Not available")).append(
				Command.EOL).append(
				props.getProperty("cmd.usage." + getName() + ".desc",
						"Not available"));
		daLog().logInfo("ASJ.dpl_api.001261", "{0}",
				new Object[] { buffer.toString() });
	}

	protected void addDescription(String desc, boolean isError) {
		if (this.description == null) {
			this.description = new StringBuffer();
		}
		this.description.append(desc).append(Command.EOL);
		if (isError) {
			this.daLog().logError("ASJ.dpl_api.001260", "{0}",
					new Object[] { desc });

			this.logger.msgToTrace(CmdLogger.PATTERN_ERROR_BEGIN + Command.EOL
					+ desc + Command.EOL + CmdLogger.PATTERN_ERROR_END);
		} else {
			this.logger.msgToTrace(CmdLogger.PATTERN_WARNING_BEGIN
					+ Command.EOL + desc + Command.EOL
					+ CmdLogger.PATTERN_WARNING_END);
		}
	}

	protected DALog daLog() {
		return this._daLog;
	}

	/*
	 * protected void logInfo(String line){
	 * this.daLog.logInfo1("ASJ.dpl_api.001262", "{0}", new Object[]{line} ); }
	 * 
	 * protected void logWarning(String line){
	 * this.daLog.logWarning1("ASJ.dpl_api.001261", "{0}", new Object[]{line});
	 * }
	 * 
	 * protected void logException( Exception ex ){ this.daLog.logThrowable( ex
	 * ); }
	 */

	private boolean hasNextArg(int index) {
		return (this.argList.length > index);
	}

	private boolean isArgSpecial(int index) {
		return this.argList[index].startsWith("-");
	}

	public void destroy() {
		this.daLog().logInfo("ASJ.dpl_api.001263", "Destroy command:[{0}]",
				new Object[] { getName() });
		if (this.client != null) {
			try {
				this.client.close();
			} catch (ConnectionException e) {
				// $JL-EXC$
			}
		}
	}

	protected CmdLogger getCmdLogger() {
		return this.logger;
	}

	private final String readStringFromConsole(BufferedReader in) {
		try {
			return in.readLine();
		} catch (IOException e) {
			this.daLog().logThrowable("ASJ.dpl_api.001264",
					"Exception while read from console", e);
			return null;
		}
	}

	protected boolean isSuccess(int code) {
		return Command.CODE_SUCCESS == code
				|| Command.CODE_SUCCESS_WITH_WARNINGS == code;
	}
}
