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
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * The ListElements command is used to list all currently deployed components on
 * the server that belong to a specific application. If the container name is
 * not specified - the components will be searched through all containers on
 * specified servers. If the names of the servers are not specified - through
 * all current cluster elements. Otherwise it is executed on specific server and
 * for specific container and selected application.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Monika Kovachka
 * @version
 */
public class ListElementsCommand extends DSCommand {

	public static final String CMD_NAME = "LIST_EL";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public ListElementsCommand(DSChangeLog deploy) {
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
		String[] servers = null;
		String applicationName = null;
		String containerName = null;
		boolean isServer = false;
		boolean isContainer = false;
		boolean isApplication = false;

		ClassLoader threadLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());
		try {
			if (params.length == 0
					|| params.length > 0
					&& (params[0].equals("-?") || params[0].toLowerCase()
							.startsWith("-h"))) {
				pw.println(getHelpMessage());
				containerName = null;
				return;
			} else {
				if (params.length > 1) {
					int j = 0;
					String cmd = null;
					servers = new String[params.length];

					for (int i = 0; i < params.length; i++) {
						cmd = params[i];

						if (cmd.equals("-s")) {
							isServer = true;
							isContainer = false;
							isApplication = false;
						} else if (cmd.equals("-c")) {
							isServer = false;
							isContainer = true;
							isApplication = false;
						} else if (cmd.equals("-a")) {
							isServer = false;
							isContainer = false;
							isApplication = true;
						} else if (isServer) {
							servers[j++] = cmd;
						} else if (isContainer) {
							containerName = cmd;
							isContainer = false;
						} else if (isApplication) {
							applicationName = cmd;
							isApplication = false;
						} else {
							pw.println("Error in parameters.\n");
							return;
						}
					}

					if (j == 0) {
						servers = null;
					} else {
						String[] temp = new String[j];
						System.arraycopy(servers, 0, temp, 0, j);
						servers = temp;
						temp = null;
					}
				} else {
					pw.println("Error in parameters.\n");
					return;
				}
			}

			try {
				String[] res = deploy.listElements(containerName,
						applicationName, servers);
				pw.println("");

				if (res != null && res.length != 0) {
					if (applicationName != null) {
						pw.println("Elements for application "
								+ applicationName + " : \n");
					} else {
						pw.println("Elements for all applications : \n");
					}

					for (int i = 0; i < res.length; i++) {
						pw.println("\t" + res[i]);
					}
				} else {
					if (applicationName != null) {
						pw.println("There are no elements for application "
								+ applicationName
								+ " or the application don't exist!");
					} else {
						pw.println("No applications are deployed.");
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
		return "Lists all components of a deployed application on containers in the cluster.\n"
				+ "Usage: "
				+ getName()
				+ " [-s serverName*] [-c containerName] <-a applicationName>\n"
				+ "Parameters:\n"
				+ "   [-s serverName*]     - A space-separated list of server names for which\n"
				+ "                          components of a deployed application are listed. If\n"
				+ "                          not specified, application components deployed on\n"
				+ "                          all servers in the cluster are listed.\n"
				+ "   [-c containerName]   - The name of the container for which components of a\n"
				+ "                          deployed application are listed. If not specified,\n"
				+ "                          application components deployed on all registered\n"
				+ "                          containers are listed.\n"
				+ "   <-a applicationName> - The name of the deployed application.\n";
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
