package com.sap.engine.services.deploy.server;

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.server.event.impl.DeployEventSystem;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.remote.RemoteCaller;
import com.sap.engine.services.deploy.server.utils.concurrent.LockManager;

/**
 * <p>This is the object registry where all important parts of the deploy 
 * service are registered during the activation of the service.</p> 
 * <p>All transactions need access to deploy service context in order to use 
 * some of the registered object. There will be only one instance of this 
 * interface.</p>
 * 
 * @author Emil Dinchev
 */
public interface DeployServiceContext {

	/**
	 * @return the local deployment used for local deploy operations.
	 */
	LocalDeployment getLocalDeployment();

	/**
	 * @return the transaction manager.
	 */
	TransactionManager getTxManager();

	/**
	 * @return the transaction communicator.
	 */
	TransactionCommunicator getTxCommunicator();

	/**
	 * Marks the cluster for shutdown.
	 */
	void markForShutdown();

	/**
	 * Check whether the cluster is marked for shutdown. 
	 * @return
	 */
	boolean isMarkedForShutdown();

	/**
	 * @return the reference resolver.
	 */
	ReferenceResolver getReferenceResolver();
	
	/**
	 * @return the lock manager.
	 */
	LockManager<Component> getLockManager();
	
	/**
	 * @return the event system.
	 */
	DeployEventSystem getEventSystem();
	
	/**
	 * @return the remote caller.
	 */
	RemoteCaller getRemoteCaller();
	
	/**
	 * @return the container management.
	 */
	ContainerManagement getContainerManagement();

	/**
	 * @return the cluster monitor helper.
	 */
	ClusterMonitorHelper getClusterMonitorHelper();
}