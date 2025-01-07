package com.sap.engine.services.deploy.server;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.services.deploy.DeployService;

/**
 * @author Monika Kovachka, Rumiana Angelova
 * @version 6.25
 */
public interface ClusterChangeListener {

	/**
	 * Notifies that current cluster element is ready.
	 */
	void clusterElementReady();

	/**
	 * Notifies that a cluster element joined to the cluster.
	 * 
	 * @param element
	 *            the newly joined element.
	 */
	void elementJoin(ClusterElement element);

	/**
	 * Notifies that a cluster element disjoined from the cluster.
	 * 
	 * @param element
	 *            the disjoined element.
	 */
	void elementLoss(ClusterElement element);

	/**
	 * Marks current server for shutdown.
	 */
	void markForShutdown();

	/**
	 * Returns shell commands of the service.
	 * 
	 * @return array of shell commands.
	 */
	Command[] getCommands();

	/**
	 * Returns the instance of Deploy service.
	 * 
	 * @return the instance of Deploy service.
	 */
	DeployService getDeployService();

	void registerAllManagedObjects();

	void unregisterAllManagedObjects();
}
