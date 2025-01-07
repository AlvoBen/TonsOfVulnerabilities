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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatus;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.container.op.util.StatusFlagsEnum;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.remote.MessageResponse;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.cfg.MigrationConfigUtils;

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
 * @author Monika Kovachka
 * @version
 */
public class ListApplicationsCommand extends DSCommand {

	public static final String CMD_NAME = "LIST_APP";

	private final static String SERVER_ID = "SERVER ID";
	private final static String STATUS = "STATUS";
	private final static String NAME = "NAME";
	private final static String MODE = "MODE";
	private final static String STATUS_DESCRIPTION = "STATUS DESCRIPTION";

	private final ClusterMonitorHelper cmHelper;
	/**
	 * Constructor of the class.
	 * 
	 * @param deploy
	 */
	public ListApplicationsCommand(final DSChangeLog deploy, 
		final ClusterMonitorHelper cmHelper) {
		this.deploy = deploy;
		this.cmHelper = cmHelper;
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
	 *            - an output stream for the results of this command.
	 * @param params
	 *            - parameters of the command.
	 */
	public void exec(Environment env, InputStream is, OutputStream os,
		String[] params) {
		isRemote = env.isRemote();
		pw = new PrintWriter(os, true);
		String[] servers = null;
		String containerName = null;
		boolean isServer = false;
		boolean isContainer = false;
		boolean isOnlyJ2ee = true;
		// show migration status - activated by default
		boolean bShowMigStat = true;
		// show the status description - by default not activated
		boolean showStatusDescription = false;
		int buffer = 3;
		int MAX_APP_LEN = 46;

		ClassLoader threadLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());

		try {
			if (params.length > 0
					&& (params[0].equals("-?") || params[0].toLowerCase()
							.startsWith("-h"))) {
				pw.println(getHelpMessage());
				return;
			}

			if (params.length > 0) {
				int j = 0;
				String cmd = null;
				servers = new String[params.length];

				for (int i = 0; i < params.length; i++) {
					cmd = params[i];

					if (cmd.equals("-s")) {
						isServer = true;
						isContainer = false;
					} else if (cmd.equals("-c")) {
						isServer = false;
						isContainer = true;
					} else if (cmd.equals("-all")) {
						isServer = false;
						isContainer = false;
						isOnlyJ2ee = false;
					} else if (cmd.equals("-mig")) {
						bShowMigStat = false;
					} else if (isServer) {
						servers[j++] = cmd;
					} else if (isContainer) {
						containerName = cmd;
						isContainer = false;
					} else if (cmd.equals("-statDesc")) {
						showStatusDescription = true;
					} else {
						pw.println("Error in parameters.\n");
						return;
					}
				}

				if (j == 0) {
					servers = null;
				} else {
					String[] temp = new String[j];
					System.arraycopy(servers, 0, temp, 0, j);
					servers = temp;
					temp = null;
				}
			}

			try {
				// get the cluster IDs for the servers one time only for the
				// whole command to reduce cluster communication
				int[] clusterIDs = cmHelper.getServerIDs(servers);

				MessageResponse stat[] = 
					deploy.listApplicationAndStatusesInCluster(containerName,
								clusterIDs, isOnlyJ2ee, showStatusDescription);
				Hashtable<String, Object> all = evaluate(stat);
				String[] res = getKeysAsStringArray(all);
				pw.println("");

				if (res != null && res.length != 0) {
					pw.println("Applications : \n");
					int appNameLen = -1;
					int appModeLen = 5;
					int maxStatusLen = DeployService.UPGRADING_APP_STATUS
							.length();
					int maxServerLen = 7;
					int maxStatusDescLen = STATUS_DESCRIPTION.length();
					for (int i = 0; i < res.length; i++) {
						if (appNameLen < res[i].length()
								&& res[i].length() < MAX_APP_LEN) {
							appNameLen = res[i].length();
						}
						int startUpLen = getStartUp(res[i]).getName().length();
						if (appModeLen < startUpLen) {
							appModeLen = startUpLen;
						}
						for (int k = 0; k < clusterIDs.length; k++) {
							// detect maximal status length
							String status = null;
							if (showStatusDescription) {
								status = (String) ((Object[]) ((Hashtable) all
										.get(res[i])).get(new Integer(
										clusterIDs[k])))[0];
							} else {
								status = (String) ((Hashtable) all.get(res[i]))
										.get(new Integer(clusterIDs[k]));
							}
							if (status == null) {
								status = "NOT DEPLOYED";
							}
							if (maxStatusLen < status.length()) {
								maxStatusLen = status.length();
							}
							// detect also maximal server ID length first time
							if (i == 0) {
								String sServerId = String
										.valueOf(clusterIDs[i]);
								if (maxServerLen < sServerId.length()) {
									maxServerLen = sServerId.length();
								}
							}
						}
					}
					if (appNameLen == 0) {
						appNameLen = 10;
					}

					pw.print(" " + NAME);
					printChar(' ', appNameLen - NAME.length() + buffer - 2, pw);

					pw.print(STATUS);
					printChar(' ', maxStatusLen - STATUS.length() + buffer, pw);

					pw.print(MODE);
					printChar(' ', appModeLen - MODE.length() + buffer - 2, pw);

					pw.print(SERVER_ID);
					printChar(' ', maxServerLen - SERVER_ID.length() + buffer,
							pw);

					if (showStatusDescription) {
						pw.print(STATUS_DESCRIPTION);

						pw.println("");
						printChar('-', appNameLen + buffer - 2 + maxStatusLen
								+ buffer + appModeLen + buffer - 2
								+ maxServerLen + buffer + maxStatusDescLen + 2,
								pw);
					} else {
						pw.println("");
						printChar('-', appNameLen + buffer - 2 + maxStatusLen
								+ buffer + appModeLen + buffer - 2
								+ maxServerLen + 2, pw);
					}

					pw.println("");
					String status = null;
					// migration status information entries
					CMigrationStatus cMigStat = null;
					ConfigurationHandler cfgHandler = null;
					// initialize only if needed
					if (bShowMigStat) {
						cfgHandler = ConfigUtils.getConfigurationHandler(
								PropManager.getInstance()
										.getConfigurationHandlerFactory(),
								"list applications");
					}
					for (int i = 0; i < res.length; i++) {
						for (int k = 0; k < clusterIDs.length; k++) {
							pw.print(" " + res[i]);
							int br;

							if (res[i].length() >= MAX_APP_LEN) {
								pw.println("");
								pw.print(" ");
								br = appNameLen + buffer - 2;
							} else {
								br = appNameLen - res[i].length() + buffer - 2;
							}
							printChar(' ', br, pw);

							try {
								boolean bShowDeployStatus = false;
								if (bShowMigStat) {
									cMigStat = MigrationConfigUtils
											.readAppMigrationStatus(res[i],
													cfgHandler);
									if (cMigStat != null
											&& cMigStat.getStatus() != CMigrationStatus.PASSED) {
										status = "MIGRATION_"
												+ MigrationConfigUtils
														.getHumanReadableMigrationStatus(cMigStat);
									} else {
										bShowDeployStatus = true;
									}
								} else {
									bShowDeployStatus = true;
								}
								// no migration status to display - show
								// deployment status
								if (bShowDeployStatus) {
									if (showStatusDescription) {
										status = (String) ((Object[]) ((Hashtable) all
												.get(res[i])).get(new Integer(
												clusterIDs[k])))[0];
									} else {
										status = (String) ((Hashtable) all
												.get(res[i])).get(new Integer(
												clusterIDs[k]));
									}
									if (status == null) {
										status = "NOT DEPLOYED";
									}
								}
							} catch (Exception ex) {
								handleProblem(
										ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
										new String[] { getName() }, ex);
								return;
							} catch (ThreadDeath td) {
								handleProblem(
										ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
										new String[] { getName() }, td);
								throw td;
							} catch (OutOfMemoryError oome) {
								handleProblem(
										ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
										new String[] { getName() }, oome);
								throw oome;
							} catch (Error er) {
								handleProblem(
										ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
										new String[] { getName() }, er);
							}
							colour(status);
							pw.print(status);
							if (status
									.equalsIgnoreCase(DeployService.STOPPED_APP_STATUS)) {
								pw.print("!");
								br = maxStatusLen - status.length() + buffer
										- 1;
							} else {
								br = maxStatusLen - status.length() + buffer;
							}
							colour(DEFAULT);
							if (br < 0) {
								pw.println("");
								br = 1 + appNameLen + buffer - 2 + maxStatusLen
										+ 3;
							}
							printChar(' ', br, pw);

							StartUp startUp = getStartUp(res[i]);
							pw.print(startUp.getName());
							boolean isLazyStartUpSupported = false;
							if (StartUp.LAZY.equals(startUp)) {
								if (isSupportedLazyStartUp(res[i])) {
									isLazyStartUpSupported = true;
								} else {
									pw.print("*");
								}
							}

							if (startUp.getName().length() >= appModeLen) {
								br = buffer - 2;
							} else {
								br = appModeLen - startUp.getName().length();
							}
							if (isLazyStartUpSupported) {
								br++;
							}
							printChar(' ', br, pw);

							pw.print(clusterIDs[k]);

							// print the status description
							if (showStatusDescription) {
								String sClusterId = String
										.valueOf(clusterIDs[k]);
								if (sClusterId.length() >= maxServerLen) {
									br = buffer;
								} else {
									br = maxServerLen - sClusterId.length()
											+ buffer;
								}
								printChar(' ', br, pw);
								StatusDescription statDesc = (StatusDescription) ((Object[]) ((Hashtable) all
										.get(res[i])).get(new Integer(
										clusterIDs[k])))[1];
								if (statDesc != null) {
									StatusFlagsEnum sFlagStr = statDesc
											.getStatusFlag();
									if (sFlagStr != null
											&& sFlagStr.getDescription()
													.length() > 0) {
										pw.print(sFlagStr.getDescription()
												+ ". ");
									}
									pw.print(statDesc.getDescription());
									String excInfo = statDesc
											.getExceptionInfoDesc();
									if (excInfo != null) {
										pw.print(" Exception info: " + excInfo);
									}
								}
							}
							pw.println();
						}
					}
				} else {
					pw.println("No Applications are currently deployed.");
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
	 * @param appName
	 * @return
	 */
	private boolean isSupportedLazyStartUp(String appName) {
		DeploymentInfo dplInfo = deploy.getApplicationInfo(appName);
		return dplInfo.isSupportingLazyStart();
	}

	private StartUp getStartUp(String appName) {
		DeploymentInfo dplInfo = deploy.getApplicationInfo(appName);
		return dplInfo.getStartUpO();
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the command.
	 */
	public String getHelpMessage() {
		return "Lists all applications deployed on containers in the cluster.\n"
				+ "Usage: "
				+ getName()
				+ " [-s serverNames*] [-c containerName]\n"
				+ "Parameters:\n"
				+ "   [-s serverNames*]  - Server names for which deployed applications are listed.\n"
				+ "                        Server names are separated with spaces. If not\n"
				+ "                        specified, the applications deployed on all servers in the\n"
				+ "                        cluster are listed.\n"
				+ "   [-c containerName] - The name of the container for which the deployed\n"
				+ "                        applications are listed. If not specified, the applications\n"
				+ "                        deployed on all registered containers are listed.\n"
				+ "   [-mig]             - Deactivates the migration status option of the command.\n"
				+ "                        The option is enabled by default and shows the migration\n"
				+ "                        state of an application if not PASSED.\n"
				+ "   [-statDesc]        - Activates display of deployment status description.\n"
				+ "                        The option is disabled by default and shows detailed description\n"
				+ "                        for the current status of an application (STARTED, STOPPED etc.).\n"
				+ "\n"
				+ "NOTE: \'lazy*\' start-up MODE means that an application is marked as lazy, but\n"
				+ "is deployed on container(s) that do not support lazy start, so it will be\n"
				+ "treated as normal non-lazy application.\r\n" + "\r\n" + "";

	}

	/**
	 * Gets the name of the command.
	 * 
	 * @return The name of the command.
	 */
	public String getName() {
		return CMD_NAME;
	}

	@SuppressWarnings("unchecked")
	private Hashtable<String, Object> evaluate(final MessageResponse stat[]) {
		final Hashtable<String, Object> all = new Hashtable<String, Object>();
		if (stat != null) {
			Map<String, Object> server = null;
			// may be only status or an object array holding status and
			// status description depending on whether the description is
			// required
			Object oStatObj = null;
			for (int i = 0; i < stat.length; i++) {
				server = (Map<String, Object>)stat[i].getResponse();
				if (server != null) {
					for(final String appName : server.keySet()) {
						if (appName != null) {
							oStatObj = server.get(appName);
							if (oStatObj != null) {
								Hashtable<Integer, Object> current = 
									(Hashtable<Integer, Object>) all.get(appName);
								if (current == null) {
									current = new Hashtable();
								}
								current.put(
										new Integer(stat[i].getClusterID()),
										oStatObj);
								all.put(appName, current);
							}
						}
					}
				}
			}
		}
		return all;
	}

	private String[] getKeysAsStringArray(Hashtable hash) {
		if (hash == null) {
			return null;
		}
		String[] sRetVal = (String[]) hash.keySet().toArray(new String[] {});
		Arrays.sort(sRetVal);
		return sRetVal;
	}

	private void printChar(char symbol, int count, PrintWriter pw) {
		while (count > 0) {
			pw.print(symbol);
			count--;
		}
	}
}
