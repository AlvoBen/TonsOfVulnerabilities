package com.sap.engine.services.deploy.server;

import java.rmi.RemoteException;
import java.util.Enumeration;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.utils.ManagementListenerUtils;

/**
 * @author Monika Kovachka
 * @version 6.25
 */
public interface TransactionCommunicator {

	String[] listApplications();

	String[] listApplications(String containerName);

	/**
	 * Returns containers.
	 * 
	 * @return Enumeration of container names.
	 */
	Enumeration getContainers();

	/**
	 * Returns the container with the specified name.
	 * 
	 * @param contName
	 *            the name of the container.
	 * @return implementation of ContainerInterface for this container.
	 */
	ContainerInterface getContainer(String contName);

	/**
	 * Binds class loader to application.
	 * 
	 * @param appName
	 *            application name.
	 * @param deployment
	 *            deployment info.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	ClassLoader bindLoader(DeploymentInfo dInfo) throws DeploymentException;

	/**
	 * Removes application class loader.
	 * 
	 * @param appName
	 *            application name.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	void removeApplicationLoader(String appName) throws DeploymentException;


	DeploymentInfo getApplicationInfo(String appName);
	
	/**
	 * Refresh the deployment info cache, reading the deployment info for the 
	 * corresponding application from DB.
	 * @param appName the application name.
	 * @param appsAppConfig the application configuration.
	 * @param handler used configuration handler.
	 * @return the deployment info object, as read from DB. Cannot be null, 
	 * rather an exception will be thrown.
	 * @throws ServerDeploymentException
	 */
	DeploymentInfo refreshDeploymentInfoFromDB(String appName, 
		Configuration appsAppConfig, ConfigurationHandler handler)
		throws ServerDeploymentException;

	/**
	 * Adds application info for an application.
	 * @param appName application name.
	 * @param info deployment info.
	 */
	void addApplicationInfo(String appName, DeploymentInfo info);

	/**
	 * Removes application info for an application.
	 * 
	 * @param appName
	 *            application name.
	 */
	void removeApplicationInfo(String appName);

	/**
	 * Returns the work directory for the specified container and application,
	 * which is canonical and exists on the file system.
	 * 
	 * @param containerName
	 *            container name.
	 * @param applicationName
	 *            application name.
	 * @return container working directory for this application.
	 */
	String getContainerWorkDir(String containerName, String applicationName);

	/**
	 * Sets local application status.
	 * 
	 * @param appName
	 *            application name.
	 * @param status
	 *            byte representing application status.
	 */
	void setLocalApplicationStatus(String appName, Status status,
			StatusDescriptionsEnum statusDescId, Object[] oDescParams);

	/**
	 * Returns application status on current server.
	 * 
	 * @param appName
	 *            application name.
	 * @return byte representing application status.
	 * @throws DeploymentException
	 *             if the application is not deployed.
	 */
	byte getStatus(String appName) throws DeploymentException;

	/**
	 * Returns additional references for an application.
	 * 
	 * @param applicationName
	 *            application name.
	 * @return array of ReferenceObjects for the application.
	 */
	ReferenceObject[] getAdditionalReferences(String applicationName);

	/**
	 * Registers references for an application.
	 * 
	 * @param appName
	 *            application name.
	 * @param refs
	 *            array of application references.
	 */
	void registerReferences(String appName, ReferenceObject[] refs);

	/**
	 * Unregister references of still not deployed application.
	 * 
	 * @param moduleId
	 *            module ID of the application.
	 */
	void unregisterReferences(String moduleId);

	/**
	 * Returns deploy property with the specified key.
	 * 
	 * @param key
	 *            property key.
	 * @return property value.
	 */
	String getDeployProperty(String key);

	/**
	 * Returns the current status of an application on the given server node. 
	 * @param applicationName the name of the application.
	 * @param serverName the name of the server node.
	 * @return the current application status as string.
	 * @throws RemoteException
	 */
	String getApplicationStatus(String applicationName, String serverName)
			throws RemoteException;

	/**
	 * Internally removes references from application to other applications.
	 * 
	 * @param fromApplicationName
	 *            application name.
	 * @param refs
	 *            String array with references.
	 */
	void removeReferencesInternally(String fromApplicationName, String[] refs);

	void registerApplicationManagedObject(String appName);

	void unregisterApplicationManagedObject(String appName);

	ManagementListenerUtils getManagementListenerUtils();
}
