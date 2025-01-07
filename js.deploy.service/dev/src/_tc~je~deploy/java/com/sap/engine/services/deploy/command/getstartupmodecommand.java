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
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * The GetStartUpModeCommand is used to get start-up mode of a deployed
 * application.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Georgi Danov
 * @version
 */
public class GetStartUpModeCommand extends DSCommand {

	public static final String CMD_NAME = "GET_STARTUP";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public GetStartUpModeCommand(DSChangeLog deploy) {
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

			if (params.length != 1) {
				pw.println(getHelpMessage());
				return;
			}

			String appName = params[0];
			DeploymentInfo dinfo = deploy.getApplicationInfo(appName);

			if (dinfo == null) {
				pw
						.println("Application "
								+ appName
								+ " doesn't exist. Please specify fully qualified name.");
				return;
			}

			pw.println("\nCurrent start-up mode is "
					+ AdditionalAppInfo.getStartUpString(dinfo.getStartUp())
					+ "\n");
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
		return "Displays the start-up mode of an application.\n" + "Usage: "
				+ getName() + " <applicationName>\n" + "Parameters:\n"
				+ "   <applicationName> - The name of the application.\n";
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
