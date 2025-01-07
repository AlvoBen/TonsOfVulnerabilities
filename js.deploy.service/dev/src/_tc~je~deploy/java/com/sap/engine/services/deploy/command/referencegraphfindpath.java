package com.sap.engine.services.deploy.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;

public class ReferenceGraphFindPath implements Command {

	public static final String LEVEL_STOPPER = "-level";
	public static final String DUPLICATES = "-duplicate";
	public static final String APP_START = "-app_start";
	public static final String APP_END = "-app_end";
	public static final String DIRECTED = "-directed";
	public static final String DIRECTED_FROM = "from";
	public static final String DIRECTED_TO = "to";

	Properties status_colour = new Properties();

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

			if (checkApplicationStatus(pw, new String[] { parameters.start_app,
					parameters.end_app })) {
				return;
			}

			ConsoleGraphRenderer renderer = new ConsoleGraphRenderer(pw,
					!parameters.duplicate, parameters.level,
					parameters.direction.equals(DIRECTED_TO));
			renderer.startRoot();
			try {
				if (parameters.direction.equals(DIRECTED_TO)) {
					Applications.getReferenceGraph().traverseFindPath(
						new Component(parameters.start_app,
							Component.Type.APPLICATION),
						new Component(parameters.end_app,
							Component.Type.APPLICATION), renderer);
				} else {
					Applications.getReferenceGraph().traverseFindReversePath(
						new Component(parameters.start_app,
							Component.Type.APPLICATION),
						new Component(parameters.end_app,
							Component.Type.APPLICATION), renderer);
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

		for (int i = 0; i < params.length; i++) {
			if (DUPLICATES.equals(params[i])) {
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
			} else if (params[i].startsWith(APP_START)) {
				try {
					if (params.length >= i + 2 && params[i + 1].equals("=")) {
						parameters.start_app = params[i + 2].toString();
						i += 2;
					} else {
						parameters.hasErrors = true;
					}
				} catch (IndexOutOfBoundsException e) {
					parameters.hasErrors = true;
				}
			} else if (params[i].startsWith(APP_END)) {
				try {
					if (params.length >= i + 2 && params[i + 1].equals("=")) {
						parameters.end_app = params[i + 2].toString();
						i += 2;
					} else {
						parameters.hasErrors = true;
					}
				} catch (IndexOutOfBoundsException e) {
					parameters.hasErrors = true;
				}
			} else {
				parameters.hasErrors = true;
			}
		}

		if (parameters.start_app == null || parameters.end_app == null) {
			parameters.hasErrors = true;
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

	public String getHelpMessage() {
		return "Prints the path in reference graph for defined applications starting from <from> abnd ending to <to> application .\n"
				+ "Usage: REFS_GRAPH_FIND_PATH [-nodup] [-level=<depth>] -from=<from_app> -to=<to_app> [-directed=<depth>]\n"
				+ "Parameters:\n"
				+ "   [-duplicate] - builds reference graph with duplicating branches and can be used for all applications or only for the passed ones.\n"
				+ "   [-level=<depth>] - builds graph with limitation of the printed depth of references. <depth> is the deepest level to which to print and has integer value greater than 0.\n"
				+ "   [-directed=<from|to>] - defines the direction that will be used to print the graph. Allowed values are <to> (will build the graph for all application beneath the current) and <from> (will build the graph for all applications above the current).\n"
				+ "   [-app_start=<from_app>] - defines the start application.\n"
				+ "   [-app_end=<to_app>] - defines the end application.\n";
	}

	/**
	 * Gets the name of the command.
	 * 
	 * @return The name of the command.
	 */
	public String getName() {
		return "REFS_GRAPH_FIND_PATH";
	}

	/**
	 * Returns the name of the group the command belongs to.
	 * 
	 * @return The name of the group of commands, to which this command belongs.
	 */
	public String getGroup() {
		return "DEPLOY";
	}

	public String[] getSupportedShellProviderNames() {
		String[] names = { "InQMyShell" };
		return names;
	}

	private static class Parameters {
		public int level = Integer.MAX_VALUE;
		public String direction = DIRECTED_TO;

		public boolean levelStopper;

		public boolean hasErrors;

		public boolean duplicate;

		public boolean directed;

		public String start_app = null;

		public String end_app = null;

		@Override
        public String toString() {
			return "";
		}
	}
}
