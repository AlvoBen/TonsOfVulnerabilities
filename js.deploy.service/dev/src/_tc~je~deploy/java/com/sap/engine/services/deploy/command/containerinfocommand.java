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
import java.util.Arrays;
import java.util.List;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.engine.services.deploy.server.ExceptionConstants;
/**
 * Implementation of CONTAINER_INFO command.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Rumiana Angelova
 * @version
 */
public class ContainerInfoCommand extends DSCommand {

	public static final String CMD_NAME = "CONTAINER_INFO";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public ContainerInfoCommand(DSChangeLog deploy) {
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
		ClassLoader threadLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());
		String containerName = null;
		List<String> servers = new ArrayList<String>();

		boolean isServers = false;
		boolean isContainer = false;
		Boolean all = false;

		try {
			if (params.length == 0 || params[0].equalsIgnoreCase("-h")
					|| params[0].equalsIgnoreCase("-help")
					|| params[0].equals("-?")) {
				pw.println(getHelpMessage());
				return;
			}
			for (int i = 0; i < params.length; i++) {
				if (params[i].equals("-a")) {
					isServers = false;
					isContainer = false;
					all = true;
				} else if (params[i].equals("-c")) {
					isContainer = true;
					isServers = false;
				} else if (params[i].equals("-s")) {
					isServers = true;
					isContainer = false;
					all = true;
				} else if (params[i].equals("-ad")) {
					isServers = true;
					isContainer = false;
					all = null;
				} else if (isContainer) {
					isContainer = false;
					containerName = params[i];
				} else if (isServers) {
					servers.add(params[i]);

				} else {
					pw.println("Error in parameters");
					return;
				}
			}

			String[] serverNames = null;
			if (servers.size() > 0) {
				serverNames = new String[servers.size()];
				servers.toArray(serverNames);
			}
			try {
				if (all == null) {
					listContainers(true, serverNames);
				} else if (all) {
					listContainers(false, serverNames);
				} else {
					printContainer(containerName, serverNames);
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

	private void listContainers(boolean withDetails, String[] serverNames)
			throws RemoteException {
		String[] containers = deploy.listContainers(serverNames);
		if (containers != null) {
			Arrays.sort(containers);
			pw.println("\n  All available containers:\n");
			for (int i = 0; i < containers.length; i++) {
				if (withDetails) {
					printContainer(containers[i], serverNames);
				} else {
					pw.println("\t" + containers[i]);
				}
			}
			pw.println();
		} else {
			pw.println("\nThere are no available containers.");
		}
	}

	private void printContainer(String containerName, String[] serverNames)
			throws RemoteException {
		if (containerName != null) {
			ContainerInfo info = deploy.getContainerInfo(containerName,
					serverNames);
			if (info == null) {
				pw.println("Container " + containerName + " is not available!");
				return;
			}
			if (info != null) {
				ContainerWrapper wrapper = (ContainerWrapper) deploy
						.getContainer(containerName);
				pw.print("\n" + info.toString());
				if (wrapper != null) {
					pw.print("Container Class: "
							+ wrapper.getRealContainerName() + "\n");
					boolean hasContainerInfoXML = wrapper
							.hasContainerInfoWrapper();
					String provideXML = hasContainerInfoXML ? "YES" : "NO";
					pw.println("This container provides containers-info.xml: "
							+ provideXML + "\n");
				} else {
					pw
							.println("This container does not have container wrapper!");
				}

			}
		}
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the remove application command.
	 */
	public String getHelpMessage() {
		return "Displays container information.\n"
				+ "Usage: "
				+ getName()
				+ " <options> [-s serverName*]\n"
				+ "Parameters: \n"
				+ "   <options>:\n"
				+ "      -a                 - Lists the names of all containers on servers in the\n"
				+ "                           cluster.\n"
				+ "      -ad                - Lists the names of all containers on servers in the\n"
				+ "                           cluster with details.\n"
				+ "      -c <containerName> - The name of the container for which to display detailed\n"
				+ "                           information about.\n"
				+ "   [-s server-name*]     - A space-separated list of server names to be\n"
				+ "                           requested for container information.If not specified,\n"
				+ "                           thr containers on all servers in the cluster are\n"
				+ "                           displayed.\n";
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
