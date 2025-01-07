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
import java.util.StringTokenizer;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * The ChangeReference command is for making and removing a reference.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Anton Georgiev
 * @version
 */
public class ChangeReference extends DSCommand {

	public static final String CMD_NAME = "CHANGE_REF";

	ReferenceObject[] references = null;

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public ChangeReference(DSChangeLog deploy) {
		this.deploy = deploy;
	}

	/**
	 * A method that executes the command.
	 * 
	 * @param environment
	 *            An implementation of Environment.
	 * @param input
	 *            The InputStream , used by the command.
	 * @param output
	 *            The OutputStream , used by the command.
	 * @param params
	 *            Parameters of the command.
	 * 
	 */
	public void exec(Environment environment, InputStream input,
			OutputStream output, String[] params) {
		pw = new PrintWriter(output, true);
		references = new ReferenceObject[0];

		// gets deprecation message
		pw.println(getDeprecatedMessage());

		if ((params.length == 0) || params[0].equals("-?")
				|| params[0].toLowerCase().startsWith("-h")) {
			pw.println(getHelpMessage());
			return;
		}
		ClassLoader threadLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());
		try {
			if (params.length >= 3) {
				try {
					if (params[0].equals("-m")) {
						// MAKE
						if (!parseRestArgs(params, 2)) {
							pw.println(getHelpMessage());
							return;
						}
						try {
							deploy.makeReferences(params[1], references,
									VIA_TELNET);
						} catch (WarningException wex) {
							pw.println("References from application "
									+ params[1] + " made with WARNINGS.");
							handleWarnings(wex);
						}
						for (int r = 0; r < references.length; r++) {
							pw.println("The reference between application "
									+ params[1] + " and " + references[r]
									+ " was made!");
						}
					} else if (params[0].equals("-r")) {
						// REMOVE
						if (!parseRestArgs(params, 2)) {
							pw.println(getHelpMessage());
							return;
						}
						try {
							deploy.removeReferences(params[1], references,
									VIA_TELNET);
						} catch (WarningException wex) {
							pw.println("References from application "
									+ params[1] + " removed with WARNINGS.");
							handleWarnings(wex);
						}
						for (int r = 0; r < references.length; r++) {
							pw.println("The reference between application "
									+ params[1] + " and " + references[r]
									+ " removed!");
						}
					} else {
						// ELSE
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
			} else {
				pw.println(getHelpMessage());
				return;
			}
		} finally {
			Thread.currentThread().setContextClassLoader(threadLoader);
		}
	}

	/**
	 * Gets the name of the command.
	 * 
	 * @return The name of the command.
	 */
	public String getName() {
		return CMD_NAME;
	}

	/**
	 * Gives a short help message about the command.
	 * 
	 * @return A help message for this command.
	 */
	public String getHelpMessage() {
		return "Creates or removes references between an application and its resources.\n"
				+ "Usage: "
				+ getName()
				+ " <mode> <applicationName> [referenceType] [-pr providerName] <[resourceType:]<resourceName>>*\n"
				+ "Parameters:\n"
				+ "   <mode>:\n"
				+ "      -m                            - Creates references from an application to\n"
				+ "                                      a resource(s).\n"
				+ "      -r                            - Removes references between an application\n"
				+ "                                      and a resource(s).\n"
				+ "   <applicationName>                - The name of the application to which to create to\n"
				+ "                                      or remove from references .\n"
				+ "   [referenceType]                  - Reference type for resources to be created\n"
				+ "                                      references to. The default type is weak.\n"
				+ "                                      The reference type is ignored when \n"
				+ "                                      removing references.\n"
				+ "      -weak                         - Creates weak references to resources.\n"
				+ "      -hard                         - Creates hard references to resources.\n"
				+ "   [-pr providerName]               - Provider name for application resources.\n"
				+ "                                      Default values are:\n"
				+ "                                       - for applications: \"sap.com\";\n"
				+ "                                       - for libraries, interfaces and services:\n"
				+ "                                         \"engine.sap.com\".\n"
				+ "   <[resourceType:]<resourceName>>* - A semicolon-separated list of resources.\n"
				+ "      [resourceType]                - Resource type to which to create to or remove \n"
				+ "                                      from references. The default type is library.\n"
				+ "                                      Possible types are:\n"
				+ "         application                - Referenced resource is an application.\n"
				+ "         library                    - Referenced resource is a library.\n"
				+ "         interface                  - Referenced resource is an interface.\n"
				+ "         service                    - Referenced resource is a service.\n"
				+ "      <resourceName>                - The name of the resource.\n";
	}

	private ReferenceObject[] parseRefs(StringTokenizer st,
			String referenceType, String referenceProviderName) {
		String ref = null;
		ReferenceObject[] result = new ReferenceObject[st.countTokens()];
		ReferenceObject refObject = null;
		int i = 0;
		while (st.hasMoreTokens()) {
			ref = st.nextToken();
			refObject = new ReferenceObject();
			if (ref.startsWith("application:")) {
				refObject.setReferenceTarget(ref.substring("application:"
						.length()));
				refObject
						.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_APPLICATION);
			} else if (ref.startsWith("library:")) {
				refObject
						.setReferenceTarget(ref.substring("library:".length()));
				refObject
						.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_LIBRARY);
			} else if (ref.startsWith("service:")) {
				refObject
						.setReferenceTarget(ref.substring("service:".length()));
				refObject
						.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_SERVICE);
			} else if (ref.startsWith("interface:")) {
				refObject.setReferenceTarget(ref.substring("interface:"
						.length()));
				refObject
						.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_INTERFACE);
			} else {
				refObject.setReferenceTarget(ref);
				refObject
						.setReferenceTargetType(ReferenceObject.REF_TARGET_TYPE_LIBRARY);
			}
			refObject.setReferenceType(referenceType);
			if (referenceProviderName != null) {
				refObject.setReferenceProviderName(referenceProviderName);
			}
			result[i++] = refObject;
		}
		return result;
	}

	private boolean parseRestArgs(String[] params, int index) {
		boolean result = true;
		String providerName = null;
		boolean weak = true;
		for (int i = index; i < params.length - 1; i++) {
			if (params[i].equalsIgnoreCase("-hard")) {
				weak = false;
			} else if (params[i].equalsIgnoreCase("-weak")) {
				weak = true;
			} else if (params[i].equalsIgnoreCase("-pr")) {
				i++;
				if (i == params.length - 1) {
					return false;
				} else {
					providerName = params[i];
				}
			}
		}
		StringTokenizer st = new StringTokenizer(params[params.length - 1], ";");
		references = parseRefs(st, weak ? ReferenceObject.REF_TYPE_WEAK
				: ReferenceObject.REF_TYPE_HARD, providerName);
		if (references.length == 0) {
			pw.println("No references specified.");
			return false;
		}
		return result;
	}

	private String getDeprecatedMessage() {
		return "WARNING: The command "
				+ getName()
				+ " is deprecated. "
				+ "It will continue to work, but each application, which data is changed using "
				+ "this command will have to be restarted. To start or restart the application"
				+ ", please use " + StopApplicationCommand.CMD_NAME + " and "
				+ StartApplicationCommand.CMD_NAME
				+ " commands from DEPLOY group.\n";
	}

}
