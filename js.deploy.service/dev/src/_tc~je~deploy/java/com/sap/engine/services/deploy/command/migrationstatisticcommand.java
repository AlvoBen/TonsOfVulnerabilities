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

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatistic;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.cfg.MigrationConfigUtils;
import com.sap.engine.services.deploy.server.ExceptionConstants;

/**
 * A command for reading and displaying the complete current migration
 * statistic.
 * 
 * @author Todor Stoitsev
 * @version 7.0
 */
public class MigrationStatisticCommand extends DSCommand {

	public static final String CMD_NAME = "MIG_STAT";

	public MigrationStatisticCommand(DSChangeLog deploy) {
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
			if (params.length != 0) {
				// any parameter will print help message because
				// command does not expect parameters
				pw.println(getHelpMessage());
				return;
			}

			ConfigurationHandler cfgHandler = ConfigUtils
					.getConfigurationHandler(PropManager.getInstance()
							.getConfigurationHandlerFactory(),
							"read migration statistic");
			final CMigrationStatistic cMigStatistic = MigrationConfigUtils
					.readCMigrationStatistic(cfgHandler);
			if (cMigStatistic == null) {
				pw
						.println("There is no migration statistic, which means that the applications were not migrated.");
			} else {
				pw.println(cMigStatistic.print(""));
			}
		} catch (Exception ex) {
			handleProblem(ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
					new String[] { getName() }, ex);
			return;
		}
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the migration statistic command.
	 */
	public String getHelpMessage() {
		return "Displays the current migration statistic for all applications. No parameters expected.";
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
