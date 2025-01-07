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
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * The ListRefsCommand is used to list all the references of an application.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author
 * @version
 */
public class ListRefsCommand extends DSCommand {

	public static final String CMD_NAME = "LIST_REFS";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public ListRefsCommand(DSChangeLog deploy) {
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
		try {
			if (params.length > 0) {
				if (params[0].equalsIgnoreCase("-help")
						|| params[0].equalsIgnoreCase("-h")
						|| params[0].equals("-?")) {
					pw.println(getHelpMessage());
					return;
				} else {
					try {
						String appName = params[0];
						if (params.length == 1) {
							DeploymentInfo dInfoOneParam = deploy
									.getApplicationInfo(appName);
							if (dInfoOneParam == null) {
								pw.println("\n  The  application with name "
										+ appName + " is not  deployed.");
								return;
							}

							final Set<ReferenceObject> refsSet = deploy
									.getApplicationReferences(appName);
							if (refsSet == null || refsSet.size() == 0) {
								pw.println("\n  The application " + appName
										+ " has no references.\n");
							} else {
								final Iterator<ReferenceObject> refsIter = refsSet
										.iterator();
								ReferenceObject ref = null;
								pw.println("\n  The application " + appName
										+ " has following references.\n");
								while (refsIter.hasNext()) {
									ref = refsIter.next();
									pw.println("\t" + ref.print(""));
								}
							}
						} else if (params.length == 2
								&& params[1].equalsIgnoreCase("-resource")) {
							DeploymentInfo dInfo = deploy
									.getApplicationInfo(appName);
							if (dInfo == null) {
								pw.println("\n  The application " + appName
										+ " is not deployed.");
								return;
							}
							Set resources = dInfo.getResourceReferences();
							if (resources == null || resources.size() == 0) {
								pw.println("\n  The application " + appName
										+ " has no references to resources.");
								return;
							}
							ResourceReference resRef = null;
							final Iterator rrIter = resources.iterator();
							pw
									.println("\n  The application "
											+ appName
											+ " has following references to resources.\n");
							while (rrIter.hasNext()) {
								resRef = (ResourceReference) rrIter.next();
								pw.println("\t" + resRef.getReferenceType()
										+ " reference to resource "
										+ resRef.getResRefName() + " of type "
										+ resRef.getResRefType() + ".");
							}
						} else {
							pw.println(getHelpMessage());
							return;
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
				}
			} else {
				pw.println(getHelpMessage());
				return;
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
		return "Lists all runtime references of an application.\n"
				+ "Usage: "
				+ getName()
				+ " <applicationName> [-resource]\n"
				+ "Parameters: \n"
				+ "   <applicationName> - The name of the application to check.\n"
				+ "   [-resource]       - With this option only references to resources are listed.\n"
				+ "                       Otherwise references to other components are listed.\n";
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
