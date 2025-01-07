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

package com.sap.engine.services.deploy.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * This command is used to manipulate (get/set) the custom java version of a
 * deployed application.
 * 
 * Copyright (c) 2006, SAP-AG
 * 
 * @author Todor Stoitsev
 * @version
 */
public class JavaVersionCommand extends DSCommand {

	public static final String CMD_NAME = "JAVA_VERSION";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public JavaVersionCommand(DSChangeLog deploy) {
		this.deploy = deploy;
	}

	/**
	 * This method executes the command when called from the shell.
	 * 
	 * @param env
	 *            - the environment of the corresponding process, which executes
	 *            the command.
	 * @param is
	 *            - an input stream for this command.
	 * @param os
	 *            - an output stream for the resusts of this command.
	 * @param params
	 *            - parameters of the command.
	 */
	public void exec(Environment env, InputStream is, OutputStream os,
			String[] params) {
		pw = new PrintWriter(os, true);
		try {
			// general help indication
			if ((params.length != 1 && params.length != 3)
					|| params[0].equalsIgnoreCase("-h")
					|| params[0].equalsIgnoreCase("-help")
					|| params[0].equals("-?")) {
				pw.println(getHelpMessage());
				return;
			}

			boolean isSet = false;
			// exact parameter check:
			// expect setter command
			if (params.length == 3) {
				if (!params[0].equalsIgnoreCase("-set")) {
					pw.println(getHelpMessage());
					return;
				}
				// a dummy info is used just to validate the version
				AdditionalAppInfo dummy = new AdditionalAppInfo();
				int niValidationResult = dummy.setJavaVersion(params[1], true);
				// create appropriate message
				switch (niValidationResult) {
				case IOpConstants.UNSUPPORTED:
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < IOpConstants.SUPPORTED_JAVA_VERSIONS.length; i++) {
						sb.append(IOpConstants.SUPPORTED_JAVA_VERSIONS[i]);
						if (i != IOpConstants.SUPPORTED_JAVA_VERSIONS.length - 1) {
							sb.append(", ");
						}
					}
					pw.println("\nUnsupported java version " + params[1]
							+ "! Please use one of the following: "
							+ sb.toString() + "!\n");
					return;
				case IOpConstants.SUB_VERSION:
					pw.println("\nA sub version " + params[1]
							+ " cannot be set. A java version "
							+ dummy.getJavaVersion() + " will be set.\n");
					// modify value to match validated one for further
					// processing
					params[1] = dummy.getJavaVersion();
					break;
				}
				// valid set command is given
				isSet = true;
			} else if (params[0].equals("-default")) {
				pw.println("\nThe current default java version is "
						+ IOpConstants.DEFAULT_JAVA_VERSION + ".\n");
				return;
			} else if (params[0].startsWith("-")) {
				pw.println(getHelpMessage());
				return;
			}

			String appName = isSet ? params[2] : params[0];

			DeploymentInfo dInfo = deploy.getApplicationInfo(appName);

			if (dInfo == null) {
				pw
						.println("\nApplication "
								+ appName
								+ " doesn't exist. Please specify a fully qualified name.\n");
				return;
			}

			if (!isSet) {
				String sJavaVersion = dInfo.getJavaVersion();
				if (sJavaVersion != null && !dInfo.isCustomJavaVersion()) {
					sJavaVersion = sJavaVersion + " (default)";
				}
				pw.println("\nThe current java version is "
						+ (sJavaVersion == null ? "not available"
								: sJavaVersion) + ".\n");
			} else {
				if (params[1] != null
						&& params[1].equals(dInfo.getJavaVersion())
						&& dInfo.isCustomJavaVersion()) {
					pw
							.println("\nJava version \'"
									+ params[1]
									+ "\' is the same as the current version. Set operation aborted!\n");
					return;
				}
				// setter command
				deploy.setJavaVersion(appName, params[1], VIA_TELNET);
			}
		} catch (Exception ex) {
			handleProblem(ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
					new String[] { getName() }, ex);
			return;
		} catch (ThreadDeath td) {
			handleProblem(ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
					new String[] { getName() }, td);
			throw td;
		} catch (OutOfMemoryError oome) {
			handleProblem(ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
					new String[] { getName() }, oome);
			throw oome;
		} catch (Error er) {
			handleProblem(ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
					new String[] { getName() }, er);
		}
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of this command.
	 */
	public String getHelpMessage() {
		return "\nManipulates the Java version of an application.\n"
				+ "\nUsage: "
				+ getName()
				+ " <-default> | <[<-set java_version_number>] <applicationName>>\n"
				+ "\nParameters:\n"
				+ "  <-set javaVersionNumber> - This option is used for setting the Java version of an application.\n"
				+ "                             If not used, the command will be interpreted as a 'get' command\n"
				+ "                             and the current Java version of the application will be displayed.\n"
				+ "  <applicationName>        - The name of the application.\n"
				+ "  <-default>               - This option displays the current default Java version within the AS Java.\n";

	}

	/**
	 * Gets the name of the command.
	 * 
	 * @return The name of the command.
	 */
	public String getName() {
		return CMD_NAME;
	}
}
