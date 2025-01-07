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
package com.sap.engine.services.deploy.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.tc.logging.Location;

/**
 * Common class for all deploy service Telnet commands
 * <p>
 * NOTE: All write operations must be tracked using <code>DSChangeLog</code>
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public abstract class DSCommand implements Command {
	
	private static final Location location = 
		Location.getLocation(DSCommand.class);

	protected final static String COLOUR_PROPERTIES = "status_colour.properties";
	protected final static Properties status_colour = new Properties();

	protected final static String INSTANCE = "-I";

	protected final static String HELP = "-h";

	protected final static String ALL = "-all";

	static {
		final InputStream is = ListApplicationsCommand.class
				.getResourceAsStream("status_colour.properties");
		try {
			status_colour.load(is);
		} catch (IOException ioe) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006350",
					"Error loading status color properties", ioe);
		}
	}
	protected final static String DEFAULT = "DEFAULT";
	protected final static String OTHER = "OTHER";
	protected final static String VIA_TELNET = "Telnet";

	protected DSChangeLog deploy = null;
	protected PrintWriter pw = null;
	protected boolean isRemote = true;

	/**
	 * Returns the name of the group the command belongs to.
	 * 
	 * @return The name of the group of commands, to which this command belongs.
	 */
	public String getGroup() {
		return "DEPLOY";
	}

	/**
	 * Gives the name of the supported shell providers.
	 * 
	 * @return The Shell providers' names who supports this command.
	 */
	public String[] getSupportedShellProviderNames() {
		String[] names = { "InQMyShell" };
		return names;
	}

	/**
	 * Usual problem handling.
	 * 
	 * @param s
	 *            the message for this exception to be set.
	 * @param args
	 *            the parameters.
	 * @param t
	 *            the nested throwable.
	 */
	protected void handleProblem(String s, Object[] args, Throwable t) {
		final ServerDeploymentException de = new ServerDeploymentException(s,
				args, t);
		de.setMessageID("ASJ.dpl_ds.006404");
		DSLog
				.logErrorThrowable(
						location, 
						"ASJ.dpl_ds.006404",
						"There is an exception while executing the current command",
						de);
		handleProblem(((t.getMessage() != null && !t.getMessage().trim()
				.equals("")) ? t.getMessage() : de.getLocalizedMessage()));
	}

	/**
	 * Usual problem handling.
	 * 
	 * @param s
	 *            the message,
	 */
	protected void handleProblem(String s) {
		if (pw == null) {
			return;
		}
		pw.println(s);
		pw.println(ExceptionConstants.MORE_INFO);
	}

	/**
	 * Prints the message in the telnet and traces it.
	 * @param message
	 */
	protected void println(String message, Object... args) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, message, args);
		}
		pw.println(message);
	}

	/**
	 * Prints the message in the telnet and traces it.
	 * @param message
	 * @param type
	 */
	protected void println(String message, String type, Object... args) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, message, args);
		}
		try {
			colour(type);
			pw.println(message);
		} finally {
			colour(DEFAULT);
		}
	}

	/**
	 * Usual warning handling.
	 * 
	 * @param wex
	 *            all warnings.
	 */
	protected void handleWarnings(WarningException wex) {
		if (pw == null) {
			return;
		}
		if (wex == null) {
			return;
		}
		String[] arr = wex.getWarnings();
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] != null) {
					if (arr[i].length() > 1) {
						arr[i] = arr[i].substring(1);
					}
					pw.println("WARNING " + i + " - " + arr[i]);
				}
			}
		}
	}

	protected void colour(String type) {
		if (isRemote) {
			if (status_colour.get(type) != null) {
				pw.print(status_colour.get(type));
			} else {
				pw.print(status_colour.get(OTHER));
			}
		}
	}

}
