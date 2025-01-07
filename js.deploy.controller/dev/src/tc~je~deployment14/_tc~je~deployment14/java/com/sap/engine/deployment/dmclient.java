/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.deployment;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.enterprise.deploy.spi.status.DeploymentStatus;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.deployment.exceptions.SAPDeploymentManagerCreationException;

/**
 * @author Mariela Todorova
 */
public class DMClient {
	private static final Location location = Location
			.getLocation(DMClient.class);
	private DeploymentManager dm = null;
	private final int factor;

	private ProgressListener listener = new ProgressListener() {
		public void handleProgressEvent(ProgressEvent event) {
			DeploymentStatus ds = event.getDeploymentStatus();
			Logger.log(DMClient.location, Severity.INFO,
					"Received progress event for operation " + ds.getCommand());
			Logger.log(DMClient.location, Severity.INFO,
					"Progress event state " + ds.getState());
			if (!event.getDeploymentStatus().isRunning()) {
				notifyForOperationTermination();
			}
		}
	};

	private synchronized void notifyForOperationTermination() {
		notifyAll();
	}

	private synchronized void waitWhileRunning(ProgressObject progress) {
		long timeout = factor * 1000;
		long previousNotifiedTime = System.currentTimeMillis();
		while (progress.getDeploymentStatus().isRunning() && timeout > 0) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
			timeout -= System.currentTimeMillis() - previousNotifiedTime;
			previousNotifiedTime = System.currentTimeMillis();
		}
	}

	public DMClient() {
		factor = getFactor();
	}

	private int getFactor() {
		String fact = PropertiesHolder
				.getProperty(PropertiesHolder.TIME_TO_WAIT_FACTOR);
		int fctr = 10;

		if (fact != null) {
			fctr = Integer.parseInt(fact);
		}

		Logger.log(location, Severity.DEBUG, "Set time-to-wait factor " + fctr);
		return fctr;
	}

	private DeploymentManager obtainDeploymentManager(String uri, String user,
			String pass) throws DeploymentManagerCreationException {
		DeploymentFactory factory = new SAPDeploymentFactory();
		Logger.log(location, Severity.INFO, "Obtaining Deployment Manager");
		return dm = factory.getDeploymentManager(uri, user, pass);
	}

	private ProgressObject deploy(String fileName, String plan) {
		Target[] targets = dm.getTargets();
		ProgressObject obj = dm.distribute(targets, new File(fileName),
				plan != null ? new File(plan) : null);
		obj.addProgressListener(listener);
		Logger.log(location, Severity.INFO, "Deploying " + fileName
				+ " with plan " + plan);
		System.out.println("\nDeploying " + fileName);// $JL-SYS_OUT_ERR$

		waitWhileRunning(obj);

		return obj;
	}

	private boolean start(TargetModuleID[] modules) {
		ProgressObject obj = dm.start(modules);
		obj.addProgressListener(listener);
		Logger.log(location, Severity.INFO, "Starting");
		System.out.println("\nStarting");// $JL-SYS_OUT_ERR$

		for (int i = 0; i < modules.length; i++) {
			Logger.log(location, Severity.INFO, modules[i].toString());
			System.out.println("\t" + modules[i]);// $JL-SYS_OUT_ERR$
		}

		waitWhileRunning(obj);

		return obj.getDeploymentStatus().isCompleted();
	}

	private void release() {
		if (dm != null) {
			dm.release();
		}
	}

	private static String getHelpMessage() {
		return "\n"
				+ "Deploys an archive file to the specified target using provided credentials. Deployed modules get started afterwards.\n"
				+ "\n"
				+ "Usage: deploy <user>:<password>@<host>:<port> <archive> [-no_start]\n"
				+ "Parameters:\n"
				+ "  <user>      - User with administrators' rights.\n"
				+ "  <password>  - Password for this user.\n"
				+ "  <host>      - Target AS Java host.\n"
				+ "  <port>      - Target P4 port.\n"
				+ "  <archive>   - Path to archive.\n"
				+ "  [-no_start] - Deployed modules are not started.\n"
				+ "\n"
				+ "Examples:\n"
				+ "deploy <user>:<password>@localhost:50004 /EARs/MyEar.ear\n"
				+ "deploy <user>:<password>@their_host:50804 /EJBs/OurEJB.jar -no_start\n";
	}

	public static void main(String args[]) {
		PropertiesHolder.init();
		Logger.initLogging();

		int systemExitCode = 0;

		final int INCORRECT_ARGUMENTS_PASSED = 1;
		final int DEPLOYMENT_STATUS_FAILED = 2;
		final int DEPLOYMENT_STATUS_RUNNING = 3;
		final int NO_MODULES_AFTER_DEPLOYMENT = 4;
		final int MODULES_NOT_STARTED = 5;
		final int DEPLOYMENT_MANAGER_ERROR = 6;
		final int UNKNOWN_ERROR = 7;

		if (args[0].equals("-?") || args[0].toLowerCase().startsWith("-h")) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			return;
		}

		if (args == null || args.length < 2 || args.length > 3) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR, "Incorrect arguments passed");
			System.exit(INCORRECT_ARGUMENTS_PASSED);
		}

		String temp = args[0];
		int index = temp.indexOf('@');

		if (index <= 0) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR, "Incorrect arguments passed");
			System.exit(INCORRECT_ARGUMENTS_PASSED);
		}

		String uri = temp.substring(index + 1) + ":" + "AS_Java_Deployer";
		temp = temp.substring(0, index);
		index = temp.indexOf(':');

		if (index <= 0) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR, "Incorrect arguments passed");
			System.exit(INCORRECT_ARGUMENTS_PASSED);
		}

		String user = temp.substring(0, index);
		String pass = temp.substring(index + 1);
		temp = args[1];

		if (temp.equals("")) {
			System.out.println("No archive specified");// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR, "No archive specified");
			System.exit(INCORRECT_ARGUMENTS_PASSED);
		}

		DMClient client = new DMClient();
		ProgressObject result = null;
		DeploymentStatus status = null;
		TargetModuleID[] modules = null;

		try {
			client.obtainDeploymentManager(uri, user, pass);
			result = client.deploy(args[1], null);
			status = result.getDeploymentStatus();

			if (status != null) {
				if (status.isCompleted()) {
					System.out.println("\tcompleted successfully");// $JL-
					// SYS_OUT_ERR$
				} else if (status.isFailed()) {
					System.out.println("\tResult: Failed");
					System.out.println("\tDescription: " + status.getMessage());
					systemExitCode = DEPLOYMENT_STATUS_FAILED;
					// -
					// SYS_OUT_ERR$
					return;
				} else if (status.isRunning()) {

					System.out
							.println("\nFor details refer to deployment logs and traces");// $JL
					// -
					// SYS_OUT_ERR$
					System.out
							.println("\nThe value of the  time.to.wait.factor  in file ../cfg/deployment14.properties was not big enough to see the result. ");// $JL
					// -
					// SYS_OUT_ERR$
					System.out
							.println("\nNote that the deployment HAS NOT been canceled and you can check via telnet (with the command  list_app), whether the archive has been deployed successfully. ");// $JL
					// -
					// SYS_OUT_ERR$
					System.out
							.println("\nHint: for further deployment of large archives, please increase the value of  the time.to.wait.factor, so you can see the result of the deployment on the screen.");// $JL
					// -
					// SYS_OUT_ERR$
					systemExitCode = DEPLOYMENT_STATUS_RUNNING;
					return;
				}
			}

			modules = result.getResultTargetModuleIDs();

			if (modules == null || modules.length == 0) {
				System.out.println("\nNo modules returned by DEPLOY operation");// $JL
				// -
				// SYS_OUT_ERR$
				System.out
						.println("\nFor details refer to deployment logs and traces");// $JL
				// -
				// SYS_OUT_ERR$
				System.out
						.println("\nHint: DEPLOY operation supports only Java EE compatible archives:");// $JL
				// -
				// SYS_OUT_ERR$
				System.out
						.println("application (EAR), web (WAR), ejb (JAR), application client (JAR), connector (RAR) modules.");// $JL
				// -
				// SYS_OUT_ERR$
				System.out
						.println("\nYou would not receive feedback for other module types even if they were successfully deployed.");// $JL
				// -
				// SYS_OUT_ERR$
				Logger.log(location, Severity.WARNING,
						"No modules returned by DEPLOY operation");
				systemExitCode = NO_MODULES_AFTER_DEPLOYMENT;
				return;
			}

			System.out.println("\nDeployed modules");// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.INFO, "Deployed modules");

			for (int i = 0; i < modules.length; i++) {
				System.out.println("\t" + modules[i]);// $JL-SYS_OUT_ERR$
				Logger.log(location, Severity.INFO, "\t" + modules[i]);
			}

			if (args.length == 3 && args[2].toLowerCase().equals("-no_start")) {
				System.out.println("\nModules will not be started");// $JL-
				// SYS_OUT_ERR$
				Logger.log(location, Severity.INFO,
						"Modules will not be started");
				return;
			}

			if (client.start(modules)) {
				System.out.println("\nModules started successfully");// $JL-
				// SYS_OUT_ERR$
				Logger.log(location, Severity.INFO,
						"Modules started successfully");
			} else {
				System.out.println("\nModules could not be started");// $JL-
				// SYS_OUT_ERR$
				System.out
						.println("\nFor details refer to deployment logs and traces");// $JL
				// -
				// SYS_OUT_ERR$
				Logger.log(location, Severity.WARNING,
						"Modules could not be started");
				systemExitCode = MODULES_NOT_STARTED;
			}
		} catch (SAPDeploymentManagerCreationException dmce) {// $JL-EXC$
			String message = Logger.parseNestedMessages(dmce
					.getNestedLocalizedMessage());
			Logger.logThrowable(location, Severity.ERROR,
					"Error occurred while obtaining Deployment Manager", dmce);
			System.out
					.println("Error occurred while obtaining Deployment Manager: "
							+ message);// $JL-SYS_OUT_ERR$
			System.out
					.println("\nFor details refer to deployment logs and traces.");// $JL
			systemExitCode = DEPLOYMENT_MANAGER_ERROR;
			// -
			// SYS_OUT_ERR$
		} catch (Exception e) {// $JL-EXC$
			StringWriter strWr = new StringWriter();
			e.printStackTrace(new PrintWriter(strWr));
			Logger.log(location, Severity.ERROR,
					"Error occurred while performing DEPLOY: "
							+ strWr.toString());
			System.out.println("Error occurred while performing DEPLOY"
					+ (e.getMessage() == null ? "." : ": " + e.getMessage()));// $JL
			// -
			// SYS_OUT_ERR$
			System.out
					.println("\nFor details refer to deployment logs and traces.");// $JL
			systemExitCode = UNKNOWN_ERROR;
			// -
			// SYS_OUT_ERR$
		} finally {
			client.release();
			if (systemExitCode != 0) {
				System.exit(systemExitCode);
			}
		}
	}

}
