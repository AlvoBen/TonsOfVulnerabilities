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
import java.util.Properties;
import java.util.Vector;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.ear.exceptions.BaseIOException;
import com.sap.engine.services.deploy.ear.exceptions.BaseIllegalArgumentException;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * The UpdateFilesCommand is used to update single file(s) in the structure of
 * an application already deployed on the server. The application will be
 * updated on all servers in the cluster.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author
 * @version
 */
public class UpdateFilesCommand extends DSCommand {

	public static final String CMD_NAME = "UPDATE_FILES";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public UpdateFilesCommand(DSChangeLog deploy) {
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

		if ((params.length == 0) || params[0].equals("-?")
				|| params[0].toLowerCase().startsWith("-h")) {
			pw.println(getHelpMessage());
			return;
		}
		ClassLoader threadLoader = Thread.currentThread()
				.getContextClassLoader();
		Vector updateInfoes = new Vector();
		FileUpdateInfo updateInfo = null;
		String applicationName = null;
		for (int i = 0; i < params.length; i++) {
			if (params[i].equalsIgnoreCase("-unit")) {
				if (updateInfo != null) {
					updateInfoes.add(updateInfo);
				}
				updateInfo = new FileUpdateInfo();
			} else if (params[i].equalsIgnoreCase("-c")) {
				i++;
				if (i < params.length) {
					if (updateInfo != null) {
						updateInfo.setContainerName(params[i]);
					} else {
						pw.println(getHelpMessage());
						return;
					}
				} else {
					pw.println(getHelpMessage());
					return;
				}
			} else if (params[i].equalsIgnoreCase("-f")) {
				i++;
				if (i < params.length) {
					if (updateInfo != null) {
						try {
							updateInfo.setFileName(params[i]);
						} catch (BaseIllegalArgumentException biae) {
							handleProblem(
									ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
									new String[] { getName() }, biae);
							return;
						} catch (BaseIOException bioe) {
							handleProblem(
									ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
									new String[] { getName() }, bioe);
							return;
						}
					} else {
						pw.println(getHelpMessage());
						return;
					}
				} else {
					pw.println(getHelpMessage());
					return;
				}
			} else if (params[i].equalsIgnoreCase("-a")) {
				i++;
				if (i < params.length) {
					applicationName = params[i];
				} else {
					pw.println(getHelpMessage());
					return;
				}
			} else if (params[i].equalsIgnoreCase("-e")) {
				i++;
				if (i < params.length) {
					if (updateInfo != null) {
						try {
							updateInfo.setFileEntryName(params[i]);
						} catch (BaseIllegalArgumentException biae) {
							handleProblem(
									ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
									new String[] { getName() }, biae);
							return;
						}
					} else {
						pw.println(getHelpMessage());
						return;
					}
				} else {
					pw.println(getHelpMessage());
					return;
				}
			} else if (params[i].equalsIgnoreCase("-m")) {
				i++;
				if (i < params.length) {
					if (updateInfo != null) {
						try {
							updateInfo.setArchiveEntryName(params[i]);
						} catch (BaseIllegalArgumentException biae) {
							handleProblem(
									ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
									new String[] { getName() }, biae);
							return;
						}
					} else {
						pw.println(getHelpMessage());
						return;
					}
				} else {
					pw.println(getHelpMessage());
					return;
				}
			}
		}
		if (updateInfo != null) {
			updateInfoes.add(updateInfo);
		}
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());
		try {
			FileUpdateInfo[] infoes = new FileUpdateInfo[updateInfoes.size()];
			updateInfoes.toArray(infoes);
			try {
				if (applicationName != null) {
					deploy.singleFileUpdate(infoes, applicationName,
							new Properties(), VIA_TELNET);
				} else {
					pw.println("Application name is not specified!");
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
		return "Updates a file(s) in the structure of a deployed application. Information about each updated file is described in a separate unit.\n"
				+ "Usage: "
				+ getName()
				+ " <-a applicationName> <-unit <-f filePath> <-e fileEntryName> <-m moduleArchiveEntryName> <-c containerName>>*\n"
				+ "Parameters :\n"
				+ "   <-a applicationName>        - The name of the application whose files to\n"
				+ "                                 update.\n"
				+ "   <-unit>*                    - Marks the start and end of the information for the unit\n"
				+ "   <-f filePath>               - Path to a file which will be used to replace an\n"
				+ "                                 existing file in application structure.\n"
				+ "   <-e fileEntryName>          - Entry name of a file which will be updated in the structure\n"
				+ "                                 of a module archive which is a part of\n"
				+ "                                 application ear.\n"
				+ "   <-m moduleArchiveEntryName> - Entry name of a module archive in the structure\n"
				+ "                                 of application ear.\n"
				+ "   <-c containerName>          - The name of the container on which a module\n"
				+ "                                 archive is deployed.\n";
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
