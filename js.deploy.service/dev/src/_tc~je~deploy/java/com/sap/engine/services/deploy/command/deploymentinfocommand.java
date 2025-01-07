/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
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

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ApplicationStatusResolver;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DeploymentInfoCommand extends DSCommand {
	private static final Location location = 
		Location.getLocation(DeploymentInfoCommand.class);
	
	private static final String SAVE = "-save";
	private static final String DEL = "-del";
	private static final String PCODB = "-pcodb";

	public static final String CMD_NAME = "DEPLOY_INFO";

	public DeploymentInfoCommand(DSChangeLog deploy) {
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

		if (params.length != 1 || params[0].equalsIgnoreCase("-h")
				|| params[0].equalsIgnoreCase("-help")
				|| params[0].equals("-?")) {
			pw.println(getHelpMessage());
			return;
		} else if (params[0].equalsIgnoreCase(SAVE)) {
			modifySerialized(true);
		} else if (params[0].equalsIgnoreCase(DEL)) {
			modifySerialized(false);
		} else if (params[0].equalsIgnoreCase(PCODB)) {
			ContainerWrapper.printCName2OpDurBlob();
			println("The blob with the duration of the container operations was traced.");
		} else {
			final String appName = params[0];
			final DeploymentInfo dInfo = deploy.getApplicationInfo(appName);
			if (dInfo == null) {
				pw.println("There is no application with name " + appName
						+ " deployed on the current server\n" + "node.");
			} else {
				pw.println(dInfo.toString());
			}
		}
	}

	private void modifySerialized(boolean isSaveOrDelete) {
		final String apps[] = deploy.listApplications();
		ConfigurationHandler cfgHandler = null;
		if (apps != null) {
			DeploymentInfo dInfo = null;
			DIWriter diWriter = null;
			try {
				cfgHandler = PropManager.getInstance()
						.getConfigurationHandlerFactory()
						.getConfigurationHandler();
				Configuration appsCfg = null;
				for (int i = 0; i < apps.length; i++) {
					dInfo = deploy.getApplicationInfo(apps[i]);
					diWriter = EditorFactory.getInstance().getDIWriter(
							dInfo.getVersion());
					appsCfg = cfgHandler.openConfiguration(
							DeployConstants.ROOT_CFG_APPS + "/"
									+ dInfo.getApplicationName(),
							ConfigurationHandler.WRITE_ACCESS);
					if (isSaveOrDelete) {
						diWriter.modifySerialized(appsCfg, dInfo);
					} else {
						diWriter.modifySerialized(appsCfg, null);
					}
					cfgHandler.commit();
					appsCfg.close();
				}
			} catch (Exception e) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.004251", "ERROR (safe): ",
						e);
				println("ERROR (safe): ", e.getMessage());
				try {
					if(cfgHandler != null) {
						cfgHandler.rollback();
					}
				} catch (ConfigurationException e1) {
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.004252",
							"ERROR (rollback): ", e);
					println("ERROR (rollback): ", e
							.getMessage());
				}
			} finally {
				try {
					if(cfgHandler != null) {
						cfgHandler.closeAllConfigurations();
					}
				} catch (ConfigurationException e) {
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.004253",
							"ERROR (closeAllConfigurations): ", e);
					println("ERROR (closeAllConfigurations): ",
							e.getMessage());
				}
			}
		}
		println("The serialized deployment infos were [{0}] successfully.",
				(isSaveOrDelete ? "saved" : "deleted"));
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the remove application command.
	 */
	public String getHelpMessage() {
		return "Displays deployment information from the current server node.\n"
			+ "Usage : " + getName() + " <application name>\n";
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
