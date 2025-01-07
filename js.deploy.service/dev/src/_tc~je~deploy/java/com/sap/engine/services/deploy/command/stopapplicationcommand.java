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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * The StopApplicationCommand is used for stopping an application deployed on
 * the servers in cluster. If server name is specified the application is
 * stopped on the specified cluster element, otherwise it is stopped in the
 * whole cluster.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author
 * @version
 */
public class StopApplicationCommand extends DSCommand {

	public static final String CMD_NAME = "STOP_APP";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public StopApplicationCommand(DSChangeLog deploy) {
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
		List<String> gropuId = new ArrayList<String>();
		boolean isAllApplications = false, isInstances = false;

		ClassLoader threadLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());
		try {
			if ((params.length == 0) || params[0].equals("-?")
					|| params[0].equalsIgnoreCase("-h")) {
				pw.println(getHelpMessage());
				return;
			} else {
				for (int i = 0; i < params.length; i++) {
					if (params[i].equalsIgnoreCase(INSTANCE)) {
						isInstances = true;
					} else if (params[i].equalsIgnoreCase(ALL)) {
						if (applicationName != null) {
							pw.println(getHelpMessage());
							return;
						}
						isAllApplications = true;
					} else if (!isInstances) {
						if (applicationName == null && !isAllApplications) {
							applicationName = params[i];
						} else {
							pw.println(getHelpMessage());
							return;
						}
					} else if (isInstances) {
						gropuId.add(params[i]);
					}
				}
			}
			

			if (deploy == null) {
				pw.println("Deploy Service was not started!");
				return;
			}
			if (applicationName == null && !isAllApplications) {
				pw.println("Application not specified!");
				return;
			}
			
			Set<Integer> groupIds = new HashSet<Integer>();
			for (String id: gropuId){
				try {
					groupIds.add(Integer.parseInt(id));
				} catch (NumberFormatException nfe){
					pw.println("Group id [" + id + "] must be a number.");
					pw.println(getHelpMessage());
					return;
				}
			}
			if (isInstances && gropuId.size()==0){
				//only -I is passed ands the application will
				//be stopped on the current instance
				groupIds.add(PropManager.getInstance().getClusterMonitor()
						.getCurrentParticipant().getGroupId());
			}
			
			String apps[] = null;
			if (isAllApplications) {
				try {
					apps = deploy.listJ2EEApplications(null, (String[])null);
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
			} else {
				if (!Applications.isDeployedApplication(applicationName)) {
					pw.println("Application " + applicationName
							+ " is not deployed.\n");
					pw.println();
					pw.println(getHelpMessage());
					return;
				}
				apps = new String[] { applicationName };
			}
			if (apps != null) {
				for (int j = 0; j < apps.length; j++) {
					applicationName = apps[j];
					try {
						if (applicationName != null) {
							deploy.stopApplicationOnInstanceAndWaitAuth(
									applicationName, VIA_TELNET, groupIds);

							pw.println("");
						}
					} catch (WarningException wex) {
						pw.println("Application " + applicationName
								+ " stopped with WARNINGS. \n");
						handleWarnings(wex);
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
				}
			}
		} finally {
			Thread.currentThread().setContextClassLoader(threadLoader);
		}
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the stop application command.
	 */
	public String getHelpMessage() {
		return "Stops an application on servers in the cluster.\n"
		+ "Usage: "
		+ getName()
		+ " [<applicationName> | <-all>] | [<-I> | <-I groupIds*>]\n"
		+ "Parameters:\n"
		+ "   <applicationName>    "
		+ "- The name of the application to stop.\n"
		+ "   <" + ALL + ">               "
		+ "- Stops all applications synchronously in the cluster.\n"
		+ "   <" + INSTANCE + ">                 "
		+ "- Will be executed on the current server instance.\n"
		+ "   <-I groupIds*>       - A space-separated list of instance ids on which\n"
		+ "                          the application will be stopped.\n";
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
