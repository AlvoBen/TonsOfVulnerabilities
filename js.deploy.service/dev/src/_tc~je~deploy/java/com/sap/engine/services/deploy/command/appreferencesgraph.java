package com.sap.engine.services.deploy.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;

/**
 * The ListApplications command is used to list all currently deployed
 * applications on the server. If the container name is not specified - the
 * applications will be searched through all containers on specified servers. If
 * the names of the servers are not specified - through all current cluster
 * elements. Otherwise it is executed on specific server and for specific
 * container.
 * <p/>
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Luchesar Cekov
 */
public class AppReferencesGraph implements Command {
	public static final String ALL = "-all";

	public static final String LEVEL_STOPPER = "-level";

	public static final String DUPLICATES = "-duplicate";

	public static final String DIRECTED = "-directed";
	public static final String DIRECTED_FROM = "from";
	public static final String DIRECTED_TO = "to";

	Properties status_colour = new Properties();

	/**
	 * This method executes the command when called from the shell.
	 * 
	 * @param env
	 *            - the environment of the corresponding process, which executes
	 *            the command.
	 * @param is
	 *            - an input stream for this command.
	 * @param os
	 *            - an output stream for the results of this command.
	 * @param params
	 *            - parameters of the command.
	 */
	public void exec(Environment env, InputStream is, OutputStream os,
			String[] params) {
		java.io.PrintWriter pw = new java.io.PrintWriter(os, true);

		try {
			Parameters parameters = null;

			if ((params.length > 0 && (params[0].equals("-?") || params[0]
					.toLowerCase().startsWith("-h")))) {
				pw.println(getHelpMessage());
				return;
			} else if (params.length > 0) {
				parameters = calculateParameters(params);
			} else {
				pw.println(getHelpMessage());
				return;
			}

			if (parameters.hasErrors) {
				pw.println(getHelpMessage());
				return;
			}

			if (checkApplicationStatus(pw, parameters.applicationNames)) {
				return;
			}

			ConsoleGraphRenderer renderer = new ConsoleGraphRenderer(pw,
					!parameters.duplicate, parameters.level,
					parameters.direction.equals(DIRECTED_TO));
			renderer.startRoot();
			try {
				for (String appName : parameters.applicationNames) {
					if (parameters.direction.equals(DIRECTED_TO)) {
						Applications.getReferenceGraph().traverseForward(
								new Component(appName,
										Component.Type.APPLICATION),
								renderer);
					} else {
						Applications.getReferenceGraph().traverseBackward(
								new Component(appName,
										Component.Type.APPLICATION),
								renderer);
					}
				}
			} finally {
				renderer.endRoot();
			}

		} catch (Exception e) {
			e.printStackTrace(pw);
			pw.println("Exception occurred : " + e.getMessage());
		}
	}

	private Parameters calculateParameters(String[] params) {
		Parameters parameters = new Parameters();
		ArrayList<String> result = new ArrayList<String>(params.length);

		for (int i = 0; i < params.length; i++) {
			if (ALL.equals(params[i])) {
				parameters.all = true;
			} else if (DUPLICATES.equals(params[i])) {
				parameters.duplicate = true;
			} else if (params[i].startsWith(LEVEL_STOPPER)) {
				try {
					parameters.levelStopper = true;
					if (params.length >= i + 2 && params[i + 1].equals("=")
							&& Integer.parseInt(params[i + 2]) > 0) {
						parameters.level = Integer.parseInt(params[i + 2]);
						i += 2;
					} else {
						parameters.hasErrors = true;
					}
				} catch (NumberFormatException e) {
					parameters.hasErrors = true;
				} catch (IndexOutOfBoundsException e) {
					parameters.hasErrors = true;
				}
			} else if (params[i].startsWith(DIRECTED)) {
				try {
					parameters.directed = true;
					if (params.length >= i + 2
							&& params[i + 1].equals("=")
							&& (params[i + 2].equalsIgnoreCase(DIRECTED_FROM) || params[i + 2]
									.equalsIgnoreCase(DIRECTED_TO))) {
						parameters.direction = params[i + 2].toLowerCase();
						i += 2;
					} else {
						parameters.hasErrors = true;
					}
				} catch (IndexOutOfBoundsException e) {
					parameters.hasErrors = true;
				}
			} else {
				result.add(params[i]);
			}
		}

		if (parameters.all) {
			if (params.length == 1
					|| (params.length == 2 && parameters.duplicate)
					|| (params.length == 4 && parameters.levelStopper)
					|| (params.length == 5 && parameters.levelStopper && parameters.duplicate)) {
				parameters.applicationNames = Applications.list();
			} else {
				parameters.hasErrors = true;
			}
		} else if ((params.length == 1 && (parameters.duplicate || parameters.levelStopper))
				|| (params.length == 2 && parameters.duplicate && parameters.levelStopper)
				|| (params.length == 3 && parameters.levelStopper)
				|| (params.length == 4 && parameters.duplicate && parameters.levelStopper)) {
			parameters.hasErrors = true;
		} else {
			parameters.applicationNames = new String[result.size()];
			result.toArray(parameters.applicationNames);
		}

		return parameters;
	}

	/**
	 * @param pw
	 * @param appsnames
	 * @return
	 */
	private boolean checkApplicationStatus(java.io.PrintWriter pw,
			String[] appsnames) {
		for (int i = 0; i < appsnames.length; i++) {
			if (Applications.get(appsnames[i]) == null) {
				pw.println("Application \"" + appsnames[i]
						+ "\" is not deployed!");
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the command.
	 */
	public String getHelpMessage() {
		return "Prints the reference graph for all the applications. The graph contains application references to other applications, services and libraries along with resource references.\n"
				+ "Usage: APP_REFS_GRAPH [applicationName_1 [applicationName_2] .... [applicationName_N]] [-all] [-nodup] [-level=<depth>] [-directed=<depth>]\n"
				+ "Parameters:\n"
				+ "   [applicationName_1...N]  - builds graph only for passed applications.\n"
				+ "   [-all] - builds graphs for all applications.\n"
				+ "   [-duplicate] - builds reference graph with duplicating branches and can be used for all applications or only for the passed ones.\n"
				+ "   [-level=<depth>] - builds graph with limitation of the printed depth of references. <depth> is the deepest level to which to print and has integer value greater than 0.\n"
				+ "   [-directed=<depth>] - defines the direction that will be used to print the graph. Allowed values are <to> (will build the graph for all application beneath the current) and <from> (will build the graph for all applications above the current).\n";
	}

	/**
	 * Gets the name of the command.
	 * 
	 * @return The name of the command.
	 */
	public String getName() {
		return "APP_REFS_GRAPH";
	}

	/**
	 * Returns the name of the group the command belongs to.
	 * 
	 * @return The name of the group of commands, to which this command belongs.
	 */
	public String getGroup() {
		return "DEPLOY";
	}

	/**
	 * Gives the name of the supported shell providers.
	 * 
	 * @return The Shell providers' names who supports this command.
	 */
	public String[] getSupportedShellProviderNames() {
		String[] names = { "InQMyShell" };
		return names;
	}

	private static class Parameters {
		public String[] applicationNames;

		public int level = Integer.MAX_VALUE;
		public String direction = DIRECTED_TO;

		public boolean levelStopper;

		public boolean all;

		public boolean hasErrors;

		public boolean duplicate;

		public boolean directed;

		@Override
        public String toString() {
			return "";
		}
	}
}
