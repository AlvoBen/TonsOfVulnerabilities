package com.sap.engine.services.deploy.server;

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.server.event.impl.DeployEventSystem;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.remote.RemoteCaller;
import com.sap.engine.services.deploy.server.utils.concurrent.LockManager;

/**
 * Only one instance of this object exists and it is shared between all
 * components of deploy service. It is initialized during the activation of 
 * deploy service.
 * 
 * @see DeployServiceContext
 * 
 * @author Emil Dinchev
 */
public final class DeployServiceContextImpl implements DeployServiceContext {
	private final LocalDeployment localDeploy;
	private final TransactionCommunicator txComm;
	private final LockManager<Component> lockManager;
	private final ReferenceResolver resolver;
	private final DeployEventSystem eventSystem;
	private final RemoteCaller remote;
	private final TransactionManager txManager;
	private final ContainerManagement cManagement;
	private final ClusterMonitorHelper cmHelper;
	
	// Internal state field - the server is in a process of shutdown.
	private boolean inShutdown;
	
	public DeployServiceContextImpl(final LocalDeployment localDeploy,
		final TransactionCommunicator txComm, final ReferenceResolver resolver,
		final DeployEventSystem eventSystem, final RemoteCaller remote, 
		final LockManager<Component> lockManager, 
		final TransactionManager txManager, final ClusterMonitorHelper cmHelper, 
		final ContainerManagement cManagement) {
		this.localDeploy = localDeploy;
		this.txComm = txComm;
		this.resolver = resolver;
		this.eventSystem = eventSystem;
		this.remote = remote;
		this.lockManager = lockManager;
		this.txManager = txManager;
		this.cmHelper = cmHelper;
		this.cManagement = cManagement;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getLocalDeployment()
	 */
	public LocalDeployment getLocalDeployment() {
		return localDeploy;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getTxManager()
	 */
	public TransactionManager getTxManager() {
		return txManager;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getTxCommunicator()
	 */
	public TransactionCommunicator getTxCommunicator() {
		return txComm;
	}
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#markForShutdown()
	 */
	public void markForShutdown() {
		inShutdown = true;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#isMarkedForShutdown()
	 */
	public boolean isMarkedForShutdown() {
		return inShutdown;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getReferenceResolver()
	 */
	public ReferenceResolver getReferenceResolver() {
		return resolver;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getLockManager()
	 */
	public LockManager<Component> getLockManager() {
		return lockManager;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getEventSystem()
	 */
	public DeployEventSystem getEventSystem() {
		return eventSystem;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getContainerManagement()
	 */
	public ContainerManagement getContainerManagement() {
		return cManagement;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getRemoteCaller()
	 */
	public RemoteCaller getRemoteCaller() {
		return remote;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DeployServiceContext
	 * 		#getClusterMonitorHelper()
	 */
	public ClusterMonitorHelper getClusterMonitorHelper() {
	    return cmHelper;
    }
}