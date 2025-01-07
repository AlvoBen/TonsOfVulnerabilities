package com.sap.engine.services.deploy.logging;

import static com.sap.engine.services.deploy.logging.DSLog.logError;
import static com.sap.engine.services.deploy.logging.DSLog.logInfo;

import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.ear.jar.StandaloneModuleReader;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceImpl;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.remote.MessageResponse;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * Tracks all manual write operation done using Telnet or MBeans.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class DSChangeLog {
	private static final Location location = 
		Location.getLocation(DSChangeLog.class);
	private static Category categoryChLc = Category.getCategory(
		Category.SYS_CHANGES, "Lifecycle");
	private static final String forcedUnregisterTransactionWithoutLock = 
		"forcedUnregisterTransactionWithoutLock";
	private static final String javaVersionChange = "javaVersionChange";
	private static final String stopApplicationAndWait = 
		DeployConstants.stopApp	+ "AndWait";
	private static final String startApplicationAndWait = 
		DeployConstants.startApp + "AndWait";
	private static final String startApplicationLocalAndWait = 
		DeployConstants.startApp + "LocalAndWait";
	private static final String stopApplicationOnInstanceAndWaitAuth = 
		DeployConstants.stopApp + "OnInstanceAndWaitAuth";
	private static final String startApplicationOnInstanceAndWaitAuth = 
		DeployConstants.startApp + "OnInstanceAndWaitAuth";



	private final DeployServiceImpl deploy;

	public DSChangeLog(final DeployServiceImpl deploy) {
		this.deploy = deploy;
	}

	private void processLcResult(final boolean isOk, final String operation,
		final String appName, final String via) {
		if (isOk) {
			logInfoChLcOK(operation, appName, via);
		} else {
			logErrorChLcNotOK(operation, appName, via);
		}
	}

	private void logInfoChLcOK(String operation, String appName, String via) {
		logInfoChLc(
			"ASJ.dpl_ds.000573",
			"Operation [{0}] over application [{1}], started via [{2}] request, finished with success.",
			new Object[] { operation, appName, via });
	}

	private void logErrorChLcNotOK(String operation, String appName, String via) {
		logErrorChLc(
			"ASJ.dpl_ds.000574",
			"Operation [{0}] over application [{1}], started via [{2}] request, finished with error.",
			new Object[] { operation, appName, via });
	}

	// *************** WRITE *************** //

	public void forcedUnregisterTransactionWithoutLock(String appName,
		String via) throws ServerDeploymentException {
		boolean isOk = false;
		try {
			deploy.forcedUnregisterTransactionWithoutLock(appName);
			isOk = true;
		} finally {
			processLcResult(isOk, forcedUnregisterTransactionWithoutLock,
				appName, via);
		}
	}

	public void singleFileUpdate(FileUpdateInfo[] files, String appName,
		Properties props, String via) throws java.rmi.RemoteException {
		boolean isOk = false;
		try {
			deploy.singleFileUpdate(files, appName, props);
			isOk = true;
		} finally {
			processLcResult(isOk, DeployConstants.singleFileUpdate, appName,
				via);
		}
	}

	public void stopApplicationAndWait(String appName, String via)
		throws RemoteException {
		boolean isOk = false;
		try {
			deploy.stopApplicationAndWait(appName);
			isOk = true;
		} finally {
			processLcResult(isOk, stopApplicationAndWait, appName, via);
		}
	}

	public void startApplicationAndWait(String appName, String via)
		throws RemoteException {
		boolean isOk = false;
		try {
			deploy.startApplicationAndWait(appName);
			isOk = true;
		} finally {
			processLcResult(isOk, startApplicationAndWait, appName, via);
		}
	}

	public void startApplication(String appName, String via)
		throws RemoteException {
		boolean isOk = false;
		try {
			deploy.startApplication(appName);
			isOk = true;
		} finally {
			processLcResult(isOk, DeployConstants.startApp, appName, via);
		}
	}

	public void stopApplication(String appName, String via)
		throws RemoteException {
		boolean isOk = false;
		try {
			deploy.stopApplication(appName);
			isOk = true;
		} finally {
			processLcResult(isOk, DeployConstants.stopApp, appName, via);
		}
	}

	public void setAdditionalAppInfo(String appName, AdditionalAppInfo info,
		String via) throws RemoteException {
		boolean isOk = false;
		try {
			deploy.setAdditionalAppInfo(appName, info);
			isOk = true;
		} finally {
			processLcResult(isOk, DeployConstants.appInfoChange, appName, via);
		}
	}

	public void startApplicationLocalAndWait(String appName, String via)
		throws DeploymentException {
		boolean isOk = false;
		try {
			deploy.startApplicationLocalAndWait(appName, null);
			isOk = true;
		} finally {
			processLcResult(isOk, startApplicationLocalAndWait, appName, via);
		}
	}

	public void startApplicationOnInstanceAndWaitAuth(String appName,
		String via, Set<Integer> instances) throws RemoteException {
		boolean isOk = false;
		try {
			deploy.startApplicationOnInstanceAndWaitAuth(appName, 
				deploy.getDeployServiceContext().getClusterMonitorHelper()
					.findServers(instances));
			isOk = true;
		} finally {
			processLcResult(isOk, startApplicationOnInstanceAndWaitAuth,
				appName, via);
		}
	}

	public void stopApplicationOnInstanceAndWaitAuth(final String appName,
		final String via, final Set<Integer> instances)
		throws RemoteException {
		boolean isOk = false;
		try {
			deploy.stopApplicationOnInstanceAndWaitAuth(appName, 
				deploy.getDeployServiceContext().getClusterMonitorHelper()
					.findServers(instances));
			isOk = true;
		} finally {
			processLcResult(isOk, stopApplicationOnInstanceAndWaitAuth, appName, via);
		}
	}

	public void makeReferences(String fromApplication,
			ReferenceObject[] references, String via) throws RemoteException {
		boolean isOk = false;
		try {
			deploy.makeReferences(fromApplication, references);
			isOk = true;
		} finally {
			processLcResult(isOk, DeployConstants.makeRefs, fromApplication,
				via);
		}
	}

	public void removeReferences(String fromApplication,
		ReferenceObject[] references, String via) throws RemoteException {
		boolean isOk = false;
		try {
			deploy.removeReferences(fromApplication, references);
			isOk = true;
		} finally {
			processLcResult(isOk, DeployConstants.removeRefs, fromApplication,
				via);
		}
	}

	public void setJavaVersion(String appName, String sVersion, String via)
		throws RemoteException {
		boolean isOk = false;
		try {
			deploy.setJavaVersion(appName, sVersion);
			isOk = true;
		} finally {
			processLcResult(isOk, javaVersionChange, appName, via);
		}
	}

	// *************** READ *************** //

	public String[] listJ2EEApplications(String containerName,
		String[] serverNames) throws java.rmi.RemoteException {
		// read - no need for change log
		return deploy.listJ2EEApplications(containerName, serverNames);
	}

	public String[] listContainers(String[] serverNames)
		throws java.rmi.RemoteException {
		// read - no need for change log
		return deploy.listContainers(serverNames);
	}

	public ContainerInfo getContainerInfo(String containerName) {
		// read - no need for change log
		return deploy.getContainerInfo(containerName);
	}

	public ContainerInfo getContainerInfo(String containerName,
		String[] serverNames) throws RemoteException {
		// read - no need for change log
		return deploy.getContainerInfo(containerName, serverNames);
	}

	public ContainerInterface getContainer(String contName) {
		// read - no need for change log
		return deploy.getContainer(contName);
	}

	public DeploymentInfo getApplicationInfo(String appName) {
		// read - no need for change log
		return deploy.getApplicationInfo(appName);
	}

	public String[] listApplications() {
		// read - no need for change log
		return deploy.listApplications();
	}

	public String getApplicationStatus(String applicationName)
		throws RemoteException {
		// read - no need for change log
		return deploy.getApplicationStatus(applicationName);
	}

	public String getApplicationStatus(String applicationName, String serverName)
		throws RemoteException {
		// read - no need for change log
		return deploy.getApplicationStatus(applicationName, serverName);
	}

	public MessageResponse[] listApplicationAndStatusesInCluster(
		String containerName, int[] clusterIDs, boolean onlyJ2ee,
		boolean withStatusDescription) throws RemoteException {
		// read - no need for change log
		return deploy.listApplicationAndStatusesInCluster(containerName,
			clusterIDs, onlyJ2ee, withStatusDescription);
	}

	public String[] listElements(String containerName, String applicationName,
		String[] serverNames) throws java.rmi.RemoteException {
		// read - no need for change log
		return deploy.listElements(containerName, applicationName, serverNames);
	}

	public Set<ReferenceObject> getApplicationReferences(String appName)
		throws RemoteException {
		// read - no need for change log
		return deploy.getApplicationReferences(appName);
	}

	// ************** help ************** //

	// Logs messages with severity Severity.INFO in case of deploy service issue
	private static void logInfoChLc(String messageID, String message,
		Object... args) {
		logInfo(categoryChLc, location,  null, messageID, message, args);
	}

	// Logs messages with severity Severity.ERROR in case of deploy service
	// issue
	private static void logErrorChLc(String messageID, String message,
		Object... args) {
		logError(categoryChLc, location,  null, messageID, message, args);
	}
}