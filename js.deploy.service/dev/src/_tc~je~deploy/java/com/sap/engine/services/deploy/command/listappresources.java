package com.sap.engine.services.deploy.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;

/**
 * The ListApplications command is used to list all currently deployed
 * applications on the server. If the container name is not specified - the
 * applications will be searched through all containers on specified servers. If
 * the names of the servers are not specified - through all current cluster
 * elements. Otherwise it is executed on specific server and for specific
 * container.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Georgi Danov
 * @version
 */
public class ListAppResources extends DSCommand {

	public static final String CMD_NAME = "LIST_APP_RES";

	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public ListAppResources(DSChangeLog deploy) {
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
		pw = new java.io.PrintWriter(os, true);

		try {
			String[] appsnames = null;

			if ((params.length > 0 && (params[0].equals("-?") || params[0]
					.toLowerCase().startsWith("-h")))
					|| (params.length > 1)) {
				pw.println(getHelpMessage());
				return;
			} else if (params.length == 1) {

				DeploymentInfo dinfo = Applications.get(params[0]);

				if (dinfo == null) {
					pw.println("There is no application with such name");
					return;
				} else {
					appsnames = params;
				}
			} else {
				appsnames = Applications.list();
			}

			for (int i = 0; i < appsnames.length; i++) {

				DeploymentInfo di = Applications.get(appsnames[i]);

				Set<Resource> providedResources = di.getAllProvidedResources();
				if (providedResources != null && providedResources.size() > 0) {
					pw.println(appsnames[i] + " provides :");
					for (Resource res : providedResources) {
						pw.println("  " + res.getName() + " of type "
								+ res.getType());
					}
				}

				Set<ResourceReference> resref = di.getResourceReferences();
				if (resref != null) {
					if (resref.size() > 0) {
						pw.println(appsnames[i] + " references :");
					}
					Iterator<ResourceReference> it = resref.iterator();
					while (it.hasNext()) {
						ResourceReference ref = it.next();
						pw.println("  " + ref.getReferenceType() + " "
								+ ref.getResRefName() + " of type "
								+ ref.getResRefType());
					}
				}
			}
		} catch (Exception e) {
			pw.println("Exception occurred : " + e.getMessage());

		}
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the command.
	 */
	public String getHelpMessage() {
		return "Lists all resources exposed and referenced by applications.\n"
				+ "Usage: LIST_APP_RES [applicationName]\n"
				+ "Parameters:\n"
				+ "   [applicationName]  - list only resources exposed and referenced by the specified application.\n";
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
