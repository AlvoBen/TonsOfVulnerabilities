package com.sap.engine.services.deploy.server;

import java.rmi.RemoteException;
import java.util.Map;

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.remote.MessageResponse;

public interface LocalDeployment {
	/**
	 * Starts local transaction specified by a Hashtable of commands and cluster
	 * ID of the server initiated the transaction.
	 * 
	 * @param commandTable Hashtable with commands.
	 * @param initiatorId cluster ID.
	 * @return response with the result of execution of the transaction.
	 * @throws DeploymentException if a problem occurs during the process.
	 * @throws ComponentNotDeployedException thrown if trying to remove a non 
	 * existing application.
	 */
	MessageResponse beginLocalTransaction(
		Map<String, Object> commandTable, int initiatorId)
		throws DeploymentException, ComponentNotDeployedException;

	/**
	 * Returns the transaction registered for an application.
	 * 
	 * @param appName
	 *            application name.
	 * @param transactionType
	 *            the transaction type.
	 * @return transaction.
	 */
	DTransaction getTransaction(String applicationName, String transactionType);

	/**
	 * Lists deployed applications.
	 * @param containerName the container name on which we want to list the
	 * deployed applications. If this parameter is <tt>null</tt> we want to 
	 * list all deployed applications independently from the containers.
	 * @return String array of application names.
	 */
	String[] listApplications(String containerName);

	/**
	 * Lists deployed J2EE applications.
	 * @param containerName the container name on which we want to list the
	 * deployed J2EE applications. If this parameter is <tt>null</tt> we want 
	 * to list all deployed J2EE applications independently from the 
	 * containers.
	 * @return String array of J2EE application names.
	 */
	String[] listJ2EEApplications(String containerName);

	/**
	 * Lists elements of an application for a container.
	 * @param containerName container name.
	 * @param applicationName application name.
	 * @return List of application elements for a container. Not null.
	 */
	String[] listElements(String containerName, String applicationName);

	/**
	 * Lists all registered containers on the current server node.
	 * @return String array of container names.
	 */
	String[] listContainers();

	String getApplicationStatus(String applicationName) throws RemoteException;

	StatusDescription getApplicationStatusDescription(String applicationName)
			throws RemoteException;

	/**
	 * Obtains information about specified container from current server.
	 * 
	 * @param containerName
	 *            container name.
	 * @return ContainerInfo object, which contains information about the
	 *         specified container or null if such container is not registered.
	 */
	ContainerInfo getContainerInfo(String containerName);

	/**
	 * Returns application info for an application.
	 * 
	 * @param appName
	 *            application name.
	 * @return DeploymentInfo object for that application.
	 */
	DeploymentInfo getApplicationInfo(String appName);

	/**
	 * Lists all deployed applications.
	 * 
	 * @return String array of application names.
	 */
	String[] listApplications();

	/**
	 * Stops application locally.
	 * @param appName application name.
	 * @param cause the reason to be stopped. If this is null, we suppose that
 	 * the reason for stop is user request.
	 * @return if the application is referred hard from application with always
	 * start up mode or its mode is always, otherwise false.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	void stopApplicationLocalAndWait(String appName, Component cause)
		throws DeploymentException;

	/**
	 * Starts an application locally.
	 * 
	 * @param appName the name of the application to be started.
	 * @param cause the reason to be started. If this is null, all predecessors
	 * will be started, because we suppose that the reason for start is user 
	 * request.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	void startApplicationLocalAndWait(String appName, Component cause)
		throws DeploymentException;
}