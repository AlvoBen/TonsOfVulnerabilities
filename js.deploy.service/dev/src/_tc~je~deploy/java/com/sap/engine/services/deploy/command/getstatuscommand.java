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
import java.rmi.RemoteException;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;


/**
 * The GetStatus command is used to get the current status of a deployed
 * application.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Monika Kovachka
 * @version
 */
public class GetStatusCommand extends DSCommand {

	public static final String CMD_NAME = "GET_STATUS";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public GetStatusCommand(DSChangeLog deploy) {
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
		String applicationName = null;
		boolean isApplication = false;
		String serverName = null;
		boolean isServer = false;
		ClassLoader threadLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());
		try {
			if ((params.length == 0) || params[0].equals("-?")
					|| params[0].toLowerCase().startsWith("-h")) {
				pw.println(getHelpMessage());
				return;
			} else {
				if (params.length > 1) {
					String cmd = null;

					for (int i = 0; i < params.length; i++) {
						cmd = params[i];

						if (cmd.equals("-a")) {
							isApplication = true;
						} else if (isApplication) {
							applicationName = cmd;
							isApplication = false;
						} else if (cmd.equals("-s")) {
							isServer = true;
						} else if (isServer) {
							serverName = cmd;
							isServer = false;
						} else {
							pw.println("Error in parameters.\n");
							return;
						}
					}
				} else {
					pw.println("Error in parameters.\n");
					return;
				}
			}

			if (applicationName == null) {
				pw.println("Error in parameters. Missing required data.\n");
				return;
			}

			if (deploy == null) {
				pw.println("Deploy Service was not started!");
				return;
			}
			try {
				String status = DeployService.UNKNOWN_APP_STATUS;
				if (applicationName != null) {
					pw.println("");
					if (serverName == null) {
						final DeploymentInfo dInfo =
							deploy.getApplicationInfo(applicationName);
						if(dInfo != null) {	
							pw.println("Application " + applicationName +
								" is currently in status : "  + dInfo.getStatus());
							pw.println("Status description :" +
								dInfo.getStatusDescription().getDescription());
						} else {
							pw.println("Application " + applicationName +
								" is currently in status : UNKNOWN");
						}
					} else {
						status = deploy.getApplicationStatus(applicationName,
								serverName);
						pw.println("Application " + applicationName
								+ " is currently in status : " + status);
					}
				}
			} catch (RemoteException re) {
				handleProblem(re.toString());
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
		} finally {
			Thread.currentThread().setContextClassLoader(threadLoader);
		}
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the command.
	 */
	public String getHelpMessage() {
		return "Displays the current status of an application.\n"
				+ "Usage: "
				+ getName()
				+ " <-a applicationName> [-s serverName]\n"
				+ "Parameters:\n"
				+ "   <-a applicationName> - The name of the application to check.\n"
				+ "   [-s serverName]      - The name of the server, on which application status is\n"
				+ "                          checked. If not specified, it is the server which is\n"
				+ "                          processing the request.\n";
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
